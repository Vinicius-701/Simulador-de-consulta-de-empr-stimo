# Simulador de Empréstimo Consignado

MVP que recebe os dados de um cliente, consulta dois bancos simulados em paralelo e
apresenta o resultado individual de cada um.

## Tcnologias utilizadas:

##### linguagem: Java 21 
##### framework: SpringBoot 4.1.2
```bash
cd demo
./mvnw spring-boot:run      # Windows: mvnw.cmd spring-boot:run
```

Acesse http://localhost:8080. Testes: .\mvnw.cmd test`.

Dados de exemplo (fictícios): CPF `529.982.247-25`, CNPJ `11.222.333/0001-81`,
salário 4500, valor 10000, prazo 36.

## Arquitetura

```
Controller → SimulationService → Gateway (interface)
                                   ├── Banco_A_Adapter → /banco_a (mock)
                                   └── Banco_B_Adapter → /banco_b (mock)
```

- **API**: recebe o JSON, valida e converte para o modelo de domínio.
- **Service**: orquestra as consultas em paralelo e consolida o resultado.
- **Gateway + Adapters**: cada banco responde em um formato diferente; o adapter
  traduz para o modelo único `ResultadoEmprestimo`. Novo banco = nova classe que
  implementa `Gateway`, sem alterar o restante.
- **Bancos simulados**: endpoints no próprio projeto, com latência aleatória e 30% de
  falha no Banco A. As URLs ficam em `application.properties`.

## Decisões técnicas

- Spring Boot, `RestClient` para as chamadas HTTP e `CompletableFuture` para paralelismo.
- Validação de CPF/CNPJ delegada ao Hibernate Validator, em vez de reimplementar.
- `BigDecimal` para valores monetários.
- Três status por banco: `APROVADO`, `REJEITADO` e `INDISPONIVEL`. Banco fora do ar
  não é o mesmo que reprovação.

## Propostas

### Implementadas

**1. Resultado parcial com timeout e consultas em paralelo**
- **Problema:** o cliente espera a resposta, e a empresa depende de bancos que não controla.
  Se um banco cai ou demora, uma implementação sequencial trava ou falha a simulação inteira.
- **Proposta:** consulta simultânea a todos os bancos, timeout de 3s por banco e falhas
  convertidas em `INDISPONIVEL`. A tela mostra os bancos que responderam e avisa
  quais ficaram indisponíveis.
- **Por quê:** o cliente sempre recebe uma resposta útil, e o tempo total passa a ser o do
  banco mais lento (limitado a 3s), e não a soma de todos.

**2. Proteção de dados pessoais nos logs**
- **Problema:** CPF é dado sensível e logs costumam vazar dados.
- **Proposta:** os logs registram apenas o nome do banco e o tipo da falha, nunca dados do cliente.
- **Por quê:** reduz o risco de exposição de dados pessoais (LGPD).

**3. Validação de entrada com mensagens por campo**
- **Problema:** dados inválidos geram consultas inúteis aos bancos e erros confusos.
- **Proposta:** validação de CPF/CNPJ (dígito verificador), e-mail, faixa de prazo e valores
  positivos antes de qualquer chamada externa, com erros indicados por campo na tela.
- **Por quê:** o cliente corrige o erro na hora e a empresa não gasta chamadas com dados inválidos.

### Planejadas (em ordem de prioridade)

**1. Retry com backoff e circuit breaker (Resilience4j)**
- **Problema:** hoje um banco instável continua sendo chamado a cada simulação e sempre
  espera o timeout inteiro.
- **Proposta:** circuit breaker por banco, que passa a responder `INDISPONIVEL`
  imediatamente quando o banco está falhando, e retry com backoff para falhas transitórias.
- **Por quê:** simulações mais rápidas durante incidentes e menos carga sobre o banco com problema.

**2. Processamento assíncrono com consulta de status**
- **Problema:** com bancos reais, a resposta pode levar muito mais que 3s.
- **Proposta:** `POST` devolve um id imediatamente e o front consulta o andamento,
  exibindo cada banco assim que responde.
- **Por quê:** o cliente não fica olhando uma tela travada.

**3. Persistência do histórico de simulações**
- **Problema:** nada é guardado, então não há auditoria nem como reexibir um resultado.
- **Proposta:** salvar simulações e resultados (com CPF mascarado ou criptografado).
- **Por quê:** rastreabilidade e base para métricas por banco (taxa de falha, tempo de resposta).

**4. Mascaramento do CPF na resposta, rate limiting e HTTPS**
- **Por quê:** reduz exposição de dados e abuso do serviço.

## Limitações

- Bancos simulados no mesmo processo; não há autenticação nem contratos reais.
- Sem persistência: nenhum dado é salvo.
- Sem retry nem circuit breaker; só timeout.
- Testes cobrem o service e a validação, mas não o timeout nem os adapters (HTTP).
- A simulação aplica regras de margem simplificadas, sem juros ou CET.