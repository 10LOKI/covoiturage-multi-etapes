package ma.brief.covoiturage.mapper;

import java.util.List;
import ma.brief.covoiturage.domain.entity.Trajet;
import ma.brief.covoiturage.dto.PointArretResponse;
import ma.brief.covoiturage.dto.TrajetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PointArretMapper.class)
public interface TrajetMapper {

    @Mapping(target = "conducteurId", source = "conducteur.id")
    @Mapping(target = "conducteurNom", expression = "java(conducteurNom(trajet))")
    @Mapping(target = "vehiculeId", source = "vehicule.id")
    @Mapping(target = "vehiculeImmatriculation", source = "vehicule.immatriculation")
    @Mapping(target = "placesRestantes", ignore = true)
    TrajetResponse toResponse(Trajet trajet);

    List<TrajetResponse> toResponse(List<Trajet> trajets);

    default String conducteurNom(Trajet trajet) {
        if (trajet.getConducteur() == null) {
            return null;
        }
        return trajet.getConducteur().getPrenom() + " " + trajet.getConducteur().getNom();
    }
}
