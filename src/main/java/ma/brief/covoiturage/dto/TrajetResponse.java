package ma.brief.covoiturage.dto;

import java.time.LocalDateTime;
import java.util.List;
import ma.brief.covoiturage.domain.enums.StatutTrajet;

public record TrajetResponse(
        Long id,
        Long conducteurId,
        String conducteurNom,
        Long vehiculeId,
        String vehiculeImmatriculation,
        String villeDepart,
        String villeArrivee,
        LocalDateTime dateDepart,
        Integer placesProposees,
        Integer placesRestantes,
        StatutTrajet statut,
        List<PointArretResponse> points) {

    public TrajetResponse withPlacesRestantes(Integer restantes) {
        return new TrajetResponse(id, conducteurId, conducteurNom, vehiculeId,
                vehiculeImmatriculation, villeDepart, villeArrivee, dateDepart,
                placesProposees, restantes, statut, points);
    }
}
