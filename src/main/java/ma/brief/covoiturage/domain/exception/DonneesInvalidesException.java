package ma.brief.covoiturage.domain.exception;

public class DonneesInvalidesException extends RuntimeException {
    public DonneesInvalidesException(String message) {
        super(message);
    }
}
