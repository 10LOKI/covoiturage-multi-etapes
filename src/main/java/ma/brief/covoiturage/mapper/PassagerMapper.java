package ma.brief.covoiturage.mapper;

import ma.brief.covoiturage.domain.entity.Passager;
import ma.brief.covoiturage.dto.PassagerRequest;
import ma.brief.covoiturage.dto.PassagerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PassagerMapper {

    Passager toEntity(PassagerRequest request);

    PassagerResponse toResponse(Passager passager);

    void update(@MappingTarget Passager passager, PassagerRequest request);
}
