package ma.brief.covoiturage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservationRequest(
        @NotNull(message = "Le trajet est obligatoire") Long trajetId,
        @NotNull(message = "Le passager est obligatoire") Long passagerId,
        @NotBlank(message = "La ville de montee est obligatoire") String villeMontee,
        @NotBlank(message = "La ville de descente est obligatoire") String villeDescente,
        @NotNull(message = "Le nombre de places est obligatoire") @Positive Integer nombrePlaces) {
}
