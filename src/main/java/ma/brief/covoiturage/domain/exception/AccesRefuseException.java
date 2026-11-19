package ma.brief.covoiturage.domain.exception;

public class AccesRefuseException extends RuntimeException {
    public AccesRefuseException(String message) {
        super(message);
    }
}
