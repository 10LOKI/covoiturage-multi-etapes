package ma.brief.covoiturage.mapper;

import ma.brief.covoiturage.domain.entity.PointArret;
import ma.brief.covoiturage.dto.PointArretResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PointArretMapper {

    PointArretResponse toResponse(PointArret point);
}
