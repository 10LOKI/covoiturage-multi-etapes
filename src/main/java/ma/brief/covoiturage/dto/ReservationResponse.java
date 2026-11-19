package ma.brief.covoiturage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import ma.brief.covoiturage.domain.enums.StatutReservation;

public record ReservationResponse(
        Long id,
        Long trajetId,
        Long passagerId,
        String villeMontee,
        String villeDescente,
        Integer nombrePlaces,
        BigDecimal montantTotal,
        LocalDateTime dateReservation,
        StatutReservation statut) {
}
