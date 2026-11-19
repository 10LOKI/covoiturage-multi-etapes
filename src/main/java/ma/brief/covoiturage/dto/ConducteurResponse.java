package ma.brief.covoiturage.dto;

public record ConducteurResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String telephone,
        String numeroPermis) {
}
