package ma.brief.covoiturage.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VehiculeRequest(
        @NotBlank(message = "La marque est obligatoire") String marque,
        @NotBlank(message = "Le modele est obligatoire") String modele,
        @NotBlank(message = "L'immatriculation est obligatoire") String immatriculation,
        String couleur,
        @NotNull(message = "La capacite est obligatoire") @Min(1) @Max(8) Integer capacite,
        @NotNull(message = "Le conducteur est obligatoire") Long conducteurId) {
}
