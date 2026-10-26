package validacaoEmprestimo.demo.integracao;

import validacaoEmprestimo.demo.dominio.Pedido;
import validacaoEmprestimo.demo.dominio.ResultadoEmprestimo;

public interface Gateway {
    String bankName();
    ResultadoEmprestimo analisarFicha(Pedido application);
}