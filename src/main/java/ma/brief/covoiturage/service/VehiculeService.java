package ma.brief.covoiturage.service;

import java.util.List;
import ma.brief.covoiturage.dto.VehiculeRequest;
import ma.brief.covoiturage.dto.VehiculeResponse;

public interface VehiculeService {

    VehiculeResponse create(VehiculeRequest request);

    VehiculeResponse getById(Long id);

    List<VehiculeResponse> findByConducteur(Long conducteurId);

    VehiculeResponse update(Long id, VehiculeRequest request);

    void delete(Long id);
}
