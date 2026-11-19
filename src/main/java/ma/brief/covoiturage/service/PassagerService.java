package ma.brief.covoiturage.service;

import java.util.List;
import ma.brief.covoiturage.dto.PassagerRequest;
import ma.brief.covoiturage.dto.PassagerResponse;
import ma.brief.covoiturage.dto.ReservationResponse;

public interface PassagerService {

    PassagerResponse create(PassagerRequest request);

    PassagerResponse getById(Long id);

    List<PassagerResponse> findAll();

    List<PassagerResponse> search(String email);

    PassagerResponse update(Long id, PassagerRequest request);

    List<ReservationResponse> historique(Long passagerId);
}
