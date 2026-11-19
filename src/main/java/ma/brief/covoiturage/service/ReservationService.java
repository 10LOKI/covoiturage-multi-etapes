package ma.brief.covoiturage.service;

import java.util.List;
import ma.brief.covoiturage.dto.ReservationRequest;
import ma.brief.covoiturage.dto.ReservationResponse;

public interface ReservationService {

    ReservationResponse reserver(ReservationRequest request);

    List<ReservationResponse> findByPassager(Long passagerId);

    void annuler(Long reservationId, Long passagerId);
}
