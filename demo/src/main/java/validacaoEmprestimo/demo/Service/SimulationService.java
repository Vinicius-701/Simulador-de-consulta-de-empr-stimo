package validacaoEmprestimo.demo.Service;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import validacaoEmprestimo.demo.dominio.Pedido;
import validacaoEmprestimo.demo.dominio.ResultadoEmprestimo;
import validacaoEmprestimo.demo.dominio.SimulationResponse;
import validacaoEmprestimo.demo.integracao.Gateway;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class SimulationService {

    private static final Logger log = LoggerFactory.getLogger(SimulationService.class);
    private static final long TIMEOUT_SECONDS = 3;

    private final List<Gateway> gateways;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public SimulationService(List<Gateway> gateways) {
        this.gateways = gateways;
    }

    public SimulationResponse simulate(Pedido app) {
        List<CompletableFuture<ResultadoEmprestimo>> futures = gateways.stream()
                .map(g -> CompletableFuture
                        .supplyAsync(() -> g.analisarFicha(app), executor)
                        .orTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .exceptionally(ex -> {
                            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                            log.warn("Falha ao consultar {}: {} - {}", g.bankName(),
                                    cause.getClass().getSimpleName(), cause.getMessage());
                            return ResultadoEmprestimo.indisponivel(g.bankName(), "BANCO_INDISPONIVEL");
                        })).toList();

        List<ResultadoEmprestimo> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        return SimulationResponse.from(results);
    }

    @PreDestroy
    void shutdown() {
        executor.shutdown();
    }
}