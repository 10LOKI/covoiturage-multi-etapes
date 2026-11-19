package ma.brief.covoiturage.mapper;

import ma.brief.covoiturage.domain.entity.Conducteur;
import ma.brief.covoiturage.dto.ConducteurRequest;
import ma.brief.covoiturage.dto.ConducteurResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ConducteurMapper {

    Conducteur toEntity(ConducteurRequest request);

    ConducteurResponse toResponse(Conducteur conducteur);

    void update(@MappingTarget Conducteur conducteur, ConducteurRequest request);
}
