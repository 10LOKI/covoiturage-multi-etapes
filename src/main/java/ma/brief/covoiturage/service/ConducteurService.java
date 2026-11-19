package ma.brief.covoiturage.service;

import java.util.List;
import ma.brief.covoiturage.dto.ConducteurRequest;
import ma.brief.covoiturage.dto.ConducteurResponse;

public interface ConducteurService {

    ConducteurResponse create(ConducteurRequest request);

    ConducteurResponse getById(Long id);

    List<ConducteurResponse> findAll();

    List<ConducteurResponse> search(String nom, String email);

    ConducteurResponse update(Long id, ConducteurRequest request);
}
