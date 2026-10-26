package validacaoEmprestimo.demo.bancosSimulados.BANCOB;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import validacaoEmprestimo.demo.dominio.Pedido;
import validacaoEmprestimo.demo.dominio.ResultadoEmprestimo;
import validacaoEmprestimo.demo.integracao.Gateway;

import java.math.BigDecimal;

@Component
public class Banco_B_Adapter implements Gateway {

    private static final String NAME = "Banco B";

    record BancoBRequest(String documento, BigDecimal salario, BigDecimal valor, int prazo) {}
    record BancoBResponse(boolean aprovado, BigDecimal limite, String motivo) {}

    private final RestClient client;

    public Banco_B_Adapter(@Value("${banks.banco-b.url}") String baseUrl) {
        this.client = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public String bankName() { return NAME; }

    @Override
    public ResultadoEmprestimo analisarFicha(Pedido app) {
        var request = new BancoBRequest(app.cpf(), app.salario(), app.valorSolicitado(), app.prazoMeses());

        BancoBResponse response = client.post()
                .uri("/avaliacao")
                .body(request)
                .retrieve()
                .body(BancoBResponse.class);

        return toResult(response);
    }

    private ResultadoEmprestimo toResult(BancoBResponse res) {
        if (res != null && res.aprovado()) {
            return ResultadoEmprestimo.aprovado(NAME, res.limite());
        }
        return ResultadoEmprestimo.rejeitado(NAME, res != null ? res.motivo() : "RESPOSTA_VAZIA");
    }
}