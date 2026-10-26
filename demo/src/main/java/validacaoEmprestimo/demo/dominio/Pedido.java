package validacaoEmprestimo.demo.dominio;

import java.math.BigDecimal;


public record Pedido(
        String cpf,
        BigDecimal salario,
        String cnpjEmpregador,
        BigDecimal valorSolicitado,
        int prazoMeses
) {}
