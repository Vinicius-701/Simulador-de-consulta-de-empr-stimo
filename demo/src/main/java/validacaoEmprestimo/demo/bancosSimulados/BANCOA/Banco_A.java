package validacaoEmprestimo.demo.bancosSimulados.BANCOA;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.*;

import java.util.concurrent.ThreadLocalRandom;


@RestController
@RequestMapping("/banco_a")
public class Banco_A {

        public record entrada(String cpf, BigDecimal salario, BigDecimal valor, int prazo) {}
        public record saida(String status, BigDecimal maxAmount, String motivo) {}


        @PostMapping("/avaliacao")
        public saida avaliar(@RequestBody entrada requisicao) throws InterruptedException{
            if (ThreadLocalRandom.current().nextInt(100) < 30) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Falha simulada");
            }

            BigDecimal margem = requisicao.salario().multiply(new BigDecimal("0.35"));
            BigDecimal parcela = requisicao.valor().divide(BigDecimal.valueOf(requisicao.prazo()), 2, RoundingMode.HALF_UP);

            if (parcela.compareTo(margem) <= 0) {
                return new saida("APPROVED", requisicao.valor(), null);
            }
            return new saida("DENIED", null, "MARGEM_INSUFICIENTE");
        }
}

