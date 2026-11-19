package ma.brief.covoiturage.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import ma.brief.covoiturage.dto.ReservationResponse;
import ma.brief.covoiturage.dto.TrajetRequest;
import ma.brief.covoiturage.dto.TrajetResponse;
import ma.brief.covoiturage.dto.TrajetSearchItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrajetService {

    TrajetResponse publish(TrajetRequest request);

    TrajetResponse getById(Long id);

    Page<TrajetSearchItem> search(String villeDepart, String villeArrivee, LocalDate date,
                                  BigDecimal prixMax, Pageable pageable);

    List<ReservationResponse> reservationsDuTrajet(Long trajetId, Long conducteurId);

    TrajetResponse terminer(Long trajetId, Long conducteurId);

    TrajetResponse annuler(Long trajetId, Long conducteurId);
}
