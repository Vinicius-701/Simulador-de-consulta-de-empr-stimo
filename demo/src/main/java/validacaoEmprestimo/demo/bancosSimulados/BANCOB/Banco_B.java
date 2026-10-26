package validacaoEmprestimo.demo.bancosSimulados.BANCOB;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/banco_b")
public class Banco_B {

    public record entrada(String documento, BigDecimal salario, BigDecimal valor, int prazo) {}
    public record saida(boolean aprovado, BigDecimal limite, String motivo) {}

    @PostMapping("/avaliacao")
    public saida analisar(@RequestBody entrada req) throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextInt(300, 1000));

        BigDecimal limite = req.salario().multiply(new BigDecimal("0.30"))
                .multiply(BigDecimal.valueOf(req.prazo()));

        if (req.valor().compareTo(limite) <= 0) {
            return new saida(true, req.valor(), null);
        }
        return new saida(false, limite, "LIMITE_EXCEDIDO");
    }
}


