package validacaoEmprestimo.demo.API;

import org.springframework.boot.micrometer.metrics.autoconfigure.system.SystemMetricsAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.*;



@RestControllerAdvice
public class TratamentoDeErros {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErroValidacao>> tratarErro400(MethodArgumentNotValidException exception){
        List<FieldError> erros = exception.getFieldErrors();

        List<ErroValidacao> listaFormatada = erros.stream().map(erro -> new ErroValidacao(erro.getField(), erro.getDefaultMessage())).toList();

        return ResponseEntity.badRequest().body(listaFormatada);
    }

}
