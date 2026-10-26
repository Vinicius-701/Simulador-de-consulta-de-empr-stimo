package validacaoEmprestimo.demo.API;



import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ValidacaoDoJsonTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private ValidacaoDoJson com(String cpf, String cnpj, int prazo) {
        return new ValidacaoDoJson("Maria Silva", "maria@email.com", new BigDecimal("4500"),
                new BigDecimal("10000"), cpf, cnpj, prazo);
    }

    @Test
    void dadosValidosNaoGeramErros() {
        assertTrue(validator.validate(com("529.982.247-25", "11.222.333/0001-81", 36)).isEmpty());
    }

    @Test
    void cpfComDigitoVerificadorErradoEhRejeitado() {
        var erros = validator.validate(com("529.982.247-26", "11.222.333/0001-81", 36));
        assertEquals(1, erros.size());
        assertEquals("cpf", erros.iterator().next().getPropertyPath().toString());
    }

    @Test
    void cnpjInvalidoEhRejeitado() {
        var erros = validator.validate(com("529.982.247-25", "11.222.333/0001-82", 36));
        assertEquals("cnpjEmpregador", erros.iterator().next().getPropertyPath().toString());
    }

    @Test
    void prazoForaDoLimiteEhRejeitado() {
        assertFalse(validator.validate(com("529.982.247-25", "11.222.333/0001-81", 3)).isEmpty());
        assertFalse(validator.validate(com("529.982.247-25", "11.222.333/0001-81", 120)).isEmpty());
    }
}