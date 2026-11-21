package mx.edu.utez.veterinariaintegradora.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return respuesta(HttpStatus.BAD_REQUEST, "Los datos enviados no son válidos.", errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBodyIlegible(HttpMessageNotReadableException ex) {
        return respuesta(HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición no es válido (revisa formato JSON, fechas y valores de catálogo).", Map.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return respuesta(HttpStatus.BAD_REQUEST,
                "El parámetro '" + ex.getName() + "' tiene un valor inválido.", Map.of());
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNoEncontrado(RecursoNoEncontradoException ex) {
        return respuesta(HttpStatus.NOT_FOUND, ex.getMessage(), Map.of());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> handleReglaNegocio(ReglaNegocioException ex) {
        return respuesta(HttpStatus.CONFLICT, ex.getMessage(), Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleIntegridad(DataIntegrityViolationException ex) {
        return respuesta(HttpStatus.CONFLICT,
                "La operación viola una restricción de la base de datos (registro duplicado o relacionado).", Map.of());
    }

    @ExceptionHandler(NoAutenticadoException.class)
    public ResponseEntity<ErrorResponse> handleNoAutenticado(NoAutenticadoException ex) {
        return respuesta(HttpStatus.UNAUTHORIZED, ex.getMessage(), Map.of());
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ErrorResponse> handleAccesoDenegado(AccesoDenegadoException ex) {
        return respuesta(HttpStatus.FORBIDDEN, ex.getMessage(), Map.of());
    }

    private ResponseEntity<ErrorResponse> respuesta(HttpStatus estado, String mensaje, Map<String, String> errores) {
        return ResponseEntity.status(estado).body(ErrorResponse.of(estado.value(), mensaje, errores));
    }
}
