package validacaoEmprestimo.demo.API;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import validacaoEmprestimo.demo.Service.SimulationService;
import validacaoEmprestimo.demo.dominio.Pedido;
import validacaoEmprestimo.demo.dominio.SimulationResponse;



@RestController
@RequestMapping("/simulations")
public class BancoController {

    private final SimulationService service;

    public BancoController(SimulationService service) {
        this.service = service;
    }

    @PostMapping
    public SimulationResponse create(@Valid @RequestBody ValidacaoDoJson r) {
        var app = new Pedido(
                r.cpf().replaceAll("\\D", ""), r.salario(),
                r.cnpjEmpregador().replaceAll("\\D", ""),
                r.valorSolicitado(), r.prazoMeses());
        return service.simulate(app);
    }
}