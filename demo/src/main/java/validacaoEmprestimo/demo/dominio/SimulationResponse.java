package validacaoEmprestimo.demo.dominio;



import java.util.Comparator;
import java.util.List;

public record SimulationResponse(
        List<ResultadoEmprestimo> results,
        long approved,
        long rejected,
        long unavailable,
        ResultadoEmprestimo bestOffer
) {
    public static SimulationResponse from(List<ResultadoEmprestimo> results) {
        ResultadoEmprestimo best = results.stream()
                .filter(r -> r.status() == StatusBanco.APROVADO)
                .max(Comparator.comparing(ResultadoEmprestimo::valorAprovado))
                .orElse(null);

        return new SimulationResponse(
                results,
                count(results, StatusBanco.APROVADO),
                count(results, StatusBanco.REJEITADO),
                count(results, StatusBanco.INDISPONIVEL),
                best);
    }

    private static long count(List<ResultadoEmprestimo> results, StatusBanco status) {
        return results.stream().filter(r -> r.status() == status).count();
    }
}