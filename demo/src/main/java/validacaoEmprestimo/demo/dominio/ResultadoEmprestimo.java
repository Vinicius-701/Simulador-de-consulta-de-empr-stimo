package validacaoEmprestimo.demo.dominio;

import java.math.BigDecimal;

public record ResultadoEmprestimo(
        String banco,
        StatusBanco status,
        BigDecimal valorAprovado,
        String razão
) {

    public static ResultadoEmprestimo aprovado(String banco, BigDecimal valor){
        return new ResultadoEmprestimo(banco, StatusBanco.APROVADO, valor, null);
    }

    public static ResultadoEmprestimo rejeitado(String banco, String razao){
        return new ResultadoEmprestimo(banco, StatusBanco.REJEITADO, null, razao);
    }

    public static ResultadoEmprestimo indisponivel(String banco, String razao){
        return new ResultadoEmprestimo(banco, StatusBanco.INDISPONIVEL, null, razao);
    }


}