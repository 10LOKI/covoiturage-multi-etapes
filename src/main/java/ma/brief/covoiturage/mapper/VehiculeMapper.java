package ma.brief.covoiturage.mapper;

import ma.brief.covoiturage.domain.entity.Vehicule;
import ma.brief.covoiturage.dto.VehiculeRequest;
import ma.brief.covoiturage.dto.VehiculeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VehiculeMapper {

    @Mapping(target = "conducteur", ignore = true)
    Vehicule toEntity(VehiculeRequest request);

    @Mapping(target = "conducteurId", source = "conducteur.id")
    VehiculeResponse toResponse(Vehicule vehicule);

    @Mapping(target = "conducteur", ignore = true)
    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget Vehicule vehicule, VehiculeRequest request);
}
