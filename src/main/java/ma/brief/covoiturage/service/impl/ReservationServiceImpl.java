package ma.brief.covoiturage.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import ma.brief.covoiturage.domain.entity.Passager;
import ma.brief.covoiturage.domain.entity.PointArret;
import ma.brief.covoiturage.domain.entity.Reservation;
import ma.brief.covoiturage.domain.entity.Trajet;
import ma.brief.covoiturage.domain.enums.StatutReservation;
import ma.brief.covoiturage.domain.enums.StatutTrajet;
import ma.brief.covoiturage.domain.exception.AccesRefuseException;
import ma.brief.covoiturage.domain.exception.DonneesInvalidesException;
import ma.brief.covoiturage.domain.exception.RegleMetierException;
import ma.brief.covoiturage.domain.exception.RessourceIntrouvableException;
import ma.brief.covoiturage.dto.ReservationRequest;
import ma.brief.covoiturage.dto.ReservationResponse;
import ma.brief.covoiturage.mapper.ReservationMapper;
import ma.brief.covoiturage.repository.PassagerRepository;
import ma.brief.covoiturage.repository.ReservationRepository;
import ma.brief.covoiturage.repository.TrajetRepository;
import ma.brief.covoiturage.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationServiceImpl implements ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final ReservationRepository reservationRepository;
    private final TrajetRepository trajetRepository;
    private final PassagerRepository passagerRepository;
    private final ReservationMapper mapper;

    @Override
    @Transactional
    public ReservationResponse reserver(ReservationRequest request) {
        Passager passager = passagerRepository.findById(request.passagerId())
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Passager introuvable : " + request.passagerId()));
        Trajet trajet = trajetRepository.findByIdWithDetails(request.trajetId())
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Trajet introuvable : " + request.trajetId()));

        verifierTrajetPublie(trajet);
        PointArret montage = trouverPoint(trajet, request.villeMontee());
        PointArret descente = trouverPoint(trajet, request.villeDescente());
        verifierOrdrePoints(montage, descente);
        reservationRepository.findByPassagerIdAndTrajetIdAndStatut(
                        passager.getId(), trajet.getId(), StatutReservation.CONFIRMEE)
                .ifPresent(r -> {
                    throw new RegleMetierException(
                            "Le passager a deja une reservation confirmee sur ce trajet");
                });

        Integer occupees = reservationRepository.sumPlacesConfirmees(
                trajet.getId(), StatutReservation.CONFIRMEE);
        int restantes = trajet.getPlacesProposees() - (occupees == null ? 0 : occupees);
        if (request.nombrePlaces() > restantes || request.nombrePlaces() > trajet.getPlacesProposees()) {
            throw new RegleMetierException("Places restantes insuffisantes : " + restantes);
        }

        BigDecimal montant = calculerMontant(montage, descente, request.nombrePlaces());

        Reservation reservation = Reservation.builder()
                .passager(passager)
                .trajet(trajet)
                .pointMontage(montage)
                .pointDescente(descente)
                .nombrePlaces(request.nombrePlaces())
                .montantTotal(montant)
                .dateReservation(LocalDateTime.now())
                .statut(StatutReservation.CONFIRMEE)
                .build();
        Reservation saved = reservationRepository.save(reservation);

        mettreAJourStatut(trajet, restantes - request.nombrePlaces());
        trajetRepository.save(trajet);

        log.info("Reservation creee : id={}, trajet={}, passager={}, places={}, montant={}",
                saved.getId(), trajet.getId(), passager.getId(), saved.getNombrePlaces(), montant);
        return mapper.toResponse(saved);
    }

    @Override
    public List<ReservationResponse> findByPassager(Long passagerId) {
        return reservationRepository.findByPassagerId(passagerId).stream()
                .map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public void annuler(Long reservationId, Long passagerId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Reservation introuvable : " + reservationId));
        if (!Objects.equals(reservation.getPassager().getId(), passagerId)) {
            log.warn("Acces refuse à la reservation {} pour le passager {}", reservationId, passagerId);
            throw new AccesRefuseException("Cette reservation n'appartient pas a ce passager");
        }
        if (reservation.getStatut() != StatutReservation.CONFIRMEE) {
            throw new RegleMetierException("La reservation est deja annulee");
        }
        Trajet trajet = reservation.getTrajet();
        if (!LocalDateTime.now().isBefore(trajet.getDateDepart().minusHours(24))) {
            throw new RegleMetierException("Le delai de 24 heures avant le depart est depasse");
        }
        reservation.setStatut(StatutReservation.ANNULEE);
        reservationRepository.save(reservation);

        if (trajet.getStatut() == StatutTrajet.COMPLET) {
            trajet.setStatut(StatutTrajet.PUBLIE);
            trajetRepository.save(trajet);
        }
        log.info("Reservation annulee : id={}, trajet={}", reservationId, trajet.getId());
    }

    private void verifierTrajetPublie(Trajet trajet) {
        if (trajet.getStatut() != StatutTrajet.PUBLIE) {
            log.warn("Reservation refusee : trajet {} en statut {}", trajet.getId(), trajet.getStatut());
            throw new RegleMetierException("On ne reserve que sur un trajet PUBLIE");
        }
    }

    private void verifierOrdrePoints(PointArret montage, PointArret descente) {
        if (montage.getOrdre() >= descente.getOrdre()) {
            throw new DonneesInvalidesException("La montee doit etre avant la descente");
        }
    }

    private BigDecimal calculerMontant(PointArret montage, PointArret descente, int places) {
        BigDecimal prixPlace = descente.getPrixDepuisDepart()
                .subtract(montage.getPrixDepuisDepart())
                .setScale(2, RoundingMode.HALF_UP);
        return prixPlace.multiply(BigDecimal.valueOf(places)).setScale(2, RoundingMode.HALF_UP);
    }

    private void mettreAJourStatut(Trajet trajet, int restantes) {
        trajet.setStatut(restantes == 0 ? StatutTrajet.COMPLET : StatutTrajet.PUBLIE);
    }

    private PointArret trouverPoint(Trajet trajet, String ville) {
        String cible = ville == null ? "" : ville.trim();
        return trajet.getPoints().stream()
                .filter(p -> p.getVille().equalsIgnoreCase(cible))
                .findFirst()
                .orElseThrow(() -> new DonneesInvalidesException(
                        "La ville " + ville + " ne fait pas partie du trajet"));
    }
}
