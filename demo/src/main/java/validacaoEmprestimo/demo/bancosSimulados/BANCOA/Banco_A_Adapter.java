package validacaoEmprestimo.demo.bancosSimulados.BANCOA;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import validacaoEmprestimo.demo.dominio.Pedido;
import validacaoEmprestimo.demo.dominio.ResultadoEmprestimo;
import validacaoEmprestimo.demo.integracao.Gateway;

import java.math.BigDecimal;

@Component
public class Banco_A_Adapter implements Gateway {


        private static final String NAME = "Banco A";

        record BancoARequest(String cpf, BigDecimal salario, BigDecimal valor, int prazo) {}
        record BancoAResponse(String status, BigDecimal maxAmount, String motivo) {}

    private final RestClient client;

    public Banco_A_Adapter(@Value("${banks.banco-a.url}") String baseUrl) {
        this.client = RestClient.builder().baseUrl(baseUrl).build();
    }

        @Override
        public String bankName() { return NAME; }

        @Override
        public ResultadoEmprestimo analisarFicha(Pedido app) {
            var request = new BancoARequest(app.cpf(), app.salario(), app.valorSolicitado(), app.prazoMeses());

            BancoAResponse response = client.post()
                    .uri("/avaliacao")
                    .body(request)
                    .retrieve()
                    .body(BancoAResponse.class);

            return toResult(response);
        }

        private ResultadoEmprestimo toResult(BancoAResponse res) {
            if (res != null && "APPROVED".equals(res.status())) {
                return ResultadoEmprestimo.aprovado(NAME, res.maxAmount());
            }
            return ResultadoEmprestimo.rejeitado(NAME, res != null ? res.motivo() : "RESPOSTA_VAZIA");
        }
    }

