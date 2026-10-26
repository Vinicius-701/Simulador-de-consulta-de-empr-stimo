package validacaoEmprestimo.demo.API;

import org.hibernate.validator.constraints.br.CPF;
import org.hibernate.validator.constraints.br.CNPJ;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ValidacaoDoJson(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank @Email(message = "Email inválido")
        String email,

        @NotNull @Positive(message = "Salário inválido")
        BigDecimal salario,

        @NotNull @Positive(message = "Valor solicitado inválido")
        BigDecimal valorSolicitado,

        @NotBlank @CPF(message = "CPF inválido")
        String cpf,

        @NotBlank @CNPJ(message = "CNPJ inválido")
        String cnpjEmpregador,

        @NotNull @Min(value = 6, message = "Mínimo de 6 meses") @Max(value = 96, message = "Máximo de 96 meses")
        Integer prazoMeses
) {}