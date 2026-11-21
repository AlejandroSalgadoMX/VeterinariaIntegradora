package mx.edu.utez.veterinariaintegradora.exception;

public class NoAutenticadoException extends RuntimeException {

    public NoAutenticadoException(String mensaje) {
        super(mensaje);
    }
}
