package validacaoEmprestimo.demo.bancosSimulados.BANCOB;



import org.hibernate.validator.cfg.defs.pl.PESELDef;
import validacaoEmprestimo.demo.dominio.ResultadoEmprestimo;
import validacaoEmprestimo.demo.dominio.Pedido;
import validacaoEmprestimo.demo.integracao.Gateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.math.BigDecimal;

@Component
public class Banco_B_Adapter implements Gateway {

    private static final String NAME = "Banco B";

    record BancoBRequest(String document, BigDecimal income, BigDecimal requestedValue, int installments) {}
    record BancoBResponse(boolean approved, BigDecimal limit, String reason) {}

    private final RestClient client;


    public Banco_B_Adapter(@Value("${banks.banco-a.url}") String baseUrl) {
        this.client = RestClient.builder().baseUrl(baseUrl).build();
    }
    @Override
    public String bankName() { return NAME; }

    @Override
    public ResultadoEmprestimo analisarFicha(Pedido app) {
        var request = new BancoBRequest(app.cpf(), app.salario(), app.valorSolicitado(), app.prazoMeses());

        BancoBResponse response = client.post()
                .uri("/analise")
                .body(request)
                .retrieve()
                .body(BancoBResponse.class);

        return toResult(response);
    }

    private ResultadoEmprestimo toResult(BancoBResponse res) {
        if (res != null && res.approved()) {
            return ResultadoEmprestimo.aprovado(NAME, res.limit());
        }
        return ResultadoEmprestimo.rejeitado(NAME, res != null ? res.reason() : "RESPOSTA_VAZIA");
    }
}
