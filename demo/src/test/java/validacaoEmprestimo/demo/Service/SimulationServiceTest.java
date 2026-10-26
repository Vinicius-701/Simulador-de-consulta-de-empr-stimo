package validacaoEmprestimo.demo.Service;

import org.junit.jupiter.api.Test;
import validacaoEmprestimo.demo.dominio.*;
import validacaoEmprestimo.demo.integracao.Gateway;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class SimulationServiceTest {

    private static final Pedido PEDIDO =
            new Pedido("52998224725", new BigDecimal("4500"), "11222333000181", new BigDecimal("10000"), 36);

    private record FakeGateway(String nome, Supplier<ResultadoEmprestimo> acao) implements Gateway {
        public String bankName() { return nome; }
        public ResultadoEmprestimo analisarFicha(Pedido p) { return acao.get(); }
    }

    private Gateway aprovando(String nome, String valor) {
        return new FakeGateway(nome, () -> ResultadoEmprestimo.aprovado(nome, new BigDecimal(valor)));
    }

    private Gateway quebrando(String nome) {
        return new FakeGateway(nome, () -> { throw new RuntimeException("falha simulada"); });
    }

    @Test
    void bancoComFalhaViraIndisponivelEOutroContinuaNormal() {
        var service = new SimulationService(List.of(quebrando("Banco A"), aprovando("Banco B", "10000")));

        SimulationResponse resp = service.simulate(PEDIDO);

        assertEquals(2, resp.results().size());
        assertEquals(1, resp.unavailable());
        assertEquals(1, resp.approved());
        assertEquals(StatusBanco.INDISPONIVEL, resp.results().get(0).status());
        assertEquals(StatusBanco.APROVADO, resp.results().get(1).status());
    }

    @Test
    void melhorOfertaEhADeMaiorValorAprovado() {
        var service = new SimulationService(List.of(aprovando("Banco A", "8000"), aprovando("Banco B", "10000")));

        SimulationResponse resp = service.simulate(PEDIDO);

        assertEquals("Banco B", resp.bestOffer().banco());
    }

    @Test
    void semAprovacoesNaoHaMelhorOferta() {
        var rejeitando = new FakeGateway("Banco A", () -> ResultadoEmprestimo.rejeitado("Banco A", "MARGEM_INSUFICIENTE"));
        var service = new SimulationService(List.of(rejeitando, quebrando("Banco B")));

        SimulationResponse resp = service.simulate(PEDIDO);

        assertNull(resp.bestOffer());
        assertEquals(1, resp.rejected());
        assertEquals(1, resp.unavailable());
    }
}