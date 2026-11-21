package mx.edu.utez.veterinariaintegradora.exception;

/**
 * Se lanza cuando una operación viola una regla de negocio del DFR
 * (traslapes, especie no permitida, lote caducado, pagos parciales, etc.).
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
