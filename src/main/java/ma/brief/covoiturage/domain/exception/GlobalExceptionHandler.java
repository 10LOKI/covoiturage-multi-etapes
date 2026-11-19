package ma.brief.covoiturage.domain.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(RessourceIntrouvableException e, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "RESSOURCE_INTROUVABLE", e.getMessage(), req);
    }

    @ExceptionHandler(AccesRefuseException.class)
    public ResponseEntity<ProblemDetail> handleForbidden(AccesRefuseException e, HttpServletRequest req) {
        log.warn("Acces refuse : {}", e.getMessage());
        return build(HttpStatus.FORBIDDEN, "ACCES_REFUSE", e.getMessage(), req);
    }

    @ExceptionHandler(RegleMetierException.class)
    public ResponseEntity<ProblemDetail> handleBusiness(RegleMetierException e, HttpServletRequest req) {
        log.warn("Regle metier refusee : {}", e.getMessage());
        return build(HttpStatus.CONFLICT, "REGLE_METIER_VIOLEE", e.getMessage(), req);
    }

    @ExceptionHandler(DonneesInvalidesException.class)
    public ResponseEntity<ProblemDetail> handleInvalid(DonneesInvalidesException e, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "DONNEES_INVALIDES", e.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException e, HttpServletRequest req) {
        Map<String, String> fields = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(fe -> fields.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
        ProblemDetail pd = build(HttpStatus.BAD_REQUEST, "VALIDATION_ECHOUEE",
                "Donnees de requete invalides", req).getBody();
        pd.setProperty("fields", fields);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception e, HttpServletRequest req) {
        log.error("Erreur interne", e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "ERREUR_INTERNE",
                "Une erreur interne est survenue", req);
    }

    private ResponseEntity<ProblemDetail> build(HttpStatus status, String code, String message,
                                                 HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, message);
        pd.setType(URI.create("urn:error:" + code.toLowerCase().replace('_', '-')));
        pd.setTitle(code);
        pd.setProperty("timestamp", java.time.Instant.now().toString());
        pd.setProperty("status", status.value());
        pd.setProperty("code", code);
        pd.setProperty("path", req.getRequestURI());
        return ResponseEntity.status(status).body(pd);
    }
}
