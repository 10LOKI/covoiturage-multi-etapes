package ma.brief.covoiturage.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record TrajetRequest(
        @NotNull(message = "Le conducteur est obligatoire") Long conducteurId,
        @NotNull(message = "Le vehicule est obligatoire") Long vehiculeId,
        @NotNull(message = "Le nombre de places est obligatoire") @Positive Integer placesProposees,
        @NotEmpty(message = "Le parcours doit contenir au moins deux points")
        @Valid List<PointArretRequest> points) {
}
