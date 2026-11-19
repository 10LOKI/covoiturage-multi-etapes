package ma.brief.covoiturage.mapper;

import ma.brief.covoiturage.domain.entity.Reservation;
import ma.brief.covoiturage.dto.ReservationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "trajetId", source = "trajet.id")
    @Mapping(target = "passagerId", source = "passager.id")
    @Mapping(target = "villeMontee", source = "pointMontage.ville")
    @Mapping(target = "villeDescente", source = "pointDescente.ville")
    ReservationResponse toResponse(Reservation reservation);
}
