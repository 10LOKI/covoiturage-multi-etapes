package ma.brief.covoiturage.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import ma.brief.covoiturage.domain.entity.Conducteur;
import ma.brief.covoiturage.domain.entity.PointArret;
import ma.brief.covoiturage.domain.entity.Reservation;
import ma.brief.covoiturage.domain.entity.Trajet;
import ma.brief.covoiturage.domain.entity.Vehicule;
import ma.brief.covoiturage.domain.enums.StatutReservation;
import ma.brief.covoiturage.domain.enums.StatutTrajet;
import ma.brief.covoiturage.domain.exception.AccesRefuseException;
import ma.brief.covoiturage.domain.exception.DonneesInvalidesException;
import ma.brief.covoiturage.domain.exception.RegleMetierException;
import ma.brief.covoiturage.domain.exception.RessourceIntrouvableException;
import ma.brief.covoiturage.dto.PointArretRequest;
import ma.brief.covoiturage.dto.ReservationResponse;
import ma.brief.covoiturage.dto.TrajetRequest;
import ma.brief.covoiturage.dto.TrajetResponse;
import ma.brief.covoiturage.dto.TrajetSearchItem;
import ma.brief.covoiturage.mapper.ReservationMapper;
import ma.brief.covoiturage.mapper.TrajetMapper;
import ma.brief.covoiturage.repository.ConducteurRepository;
import ma.brief.covoiturage.repository.ReservationRepository;
import ma.brief.covoiturage.repository.TrajetRepository;
import ma.brief.covoiturage.repository.VehiculeRepository;
import ma.brief.covoiturage.service.TrajetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrajetServiceImpl implements TrajetService {

    private static final Logger log = LoggerFactory.getLogger(TrajetServiceImpl.class);

    private final TrajetRepository trajetRepository;
    private final ConducteurRepository conducteurRepository;
    private final VehiculeRepository vehiculeRepository;
    private final ReservationRepository reservationRepository;
    private final TrajetMapper trajetMapper;
    private final ReservationMapper reservationMapper;

    @Override
    @Transactional
    public TrajetResponse publish(TrajetRequest request) {
        Conducteur conducteur = conducteurRepository.findById(request.conducteurId())
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Conducteur introuvable : " + request.conducteurId()));
        Vehicule vehicule = vehiculeRepository.findById(request.vehiculeId())
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Vehicule introuvable : " + request.vehiculeId()));
        if (!vehicule.getConducteur().getId().equals(conducteur.getId())) {
            log.warn("Tentative de publication avec le vehicule d'un autre conducteur");
            throw new AccesRefuseException("Le conducteur ne peut utiliser que ses propres vehicules");
        }
        if (request.placesProposees() > vehicule.getCapacite()) {
            throw new DonneesInvalidesException("Le vehicule ne peut proposer que "
                    + vehicule.getCapacite() + " places au maximum");
        }

        List<PointArretRequest> pointsRequest = request.points();
        if (pointsRequest == null || pointsRequest.size() < 2) {
            throw new DonneesInvalidesException("Un trajet doit contenir au moins deux points d'arret");
        }
        verifierParcours(pointsRequest);

        Trajet trajet = Trajet.builder()
                .conducteur(conducteur)
                .vehicule(vehicule)
                .villeDepart(norm(pointsRequest.get(0).ville()))
                .villeArrivee(norm(pointsRequest.get(pointsRequest.size() - 1).ville()))
                .dateDepart(pointsRequest.get(0).heurePassage())
                .placesProposees(request.placesProposees())
                .statut(StatutTrajet.PUBLIE)
                .build();

        for (int i = 0; i < pointsRequest.size(); i++) {
            PointArretRequest p = pointsRequest.get(i);
            trajet.addPoint(PointArret.builder()
                    .ville(norm(p.ville()))
                    .ordre(i)
                    .heurePassage(p.heurePassage())
                    .prixDepuisDepart(p.prixDepuisDepart().setScale(2, RoundingMode.HALF_UP))
                    .build());
        }

        Trajet saved = trajetRepository.save(trajet);
        log.info("Trajet publie : id={}, conducteur={}, {}, {} -> {}", saved.getId(),
                conducteur.getId(), saved.getVilleDepart(), saved.getVilleArrivee(), saved.getDateDepart());
        return trajetMapper.toResponse(saved).withPlacesRestantes(saved.getPlacesProposees());
    }

    @Override
    public TrajetResponse getById(Long id) {
        Trajet trajet = findWithDetails(id);
        return trajetMapper.toResponse(trajet).withPlacesRestantes(placesRestantes(trajet));
    }

    @Override
    public Page<TrajetSearchItem> search(String villeDepart, String villeArrivee, LocalDate date,
                                         BigDecimal prixMax, Pageable pageable) {
        // La requete JPQL utilise des comparaisons directes : des bornes non nulles evitent
        // a PostgreSQL de deviner le type d'un parametre null et gardent une seule requete.
        LocalDateTime dateDebut = date == null
                ? LocalDateTime.of(1900, 1, 1, 0, 0)
                : date.atStartOfDay();
        LocalDateTime dateFin = date == null
                ? LocalDateTime.of(2100, 12, 31, 23, 59, 59)
                : date.atTime(LocalTime.MAX);
        BigDecimal prixMaxEffectif = prixMax == null ? new BigDecimal("999999999.99") : prixMax;
        return trajetRepository.search(norm(villeDepart), norm(villeArrivee),
                dateDebut, dateFin, prixMaxEffectif, pageable);
    }

    @Override
    public List<ReservationResponse> reservationsDuTrajet(Long trajetId, Long conducteurId) {
        Trajet trajet = findWithDetails(trajetId);
        verifierProprietaire(trajet, conducteurId);
        return reservationRepository.findByTrajetIdWithDetails(trajetId).stream()
                .map(reservationMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public TrajetResponse terminer(Long trajetId, Long conducteurId) {
        Trajet trajet = findWithDetails(trajetId);
        verifierProprietaire(trajet, conducteurId);
        if (trajet.getStatut() != StatutTrajet.PUBLIE && trajet.getStatut() != StatutTrajet.COMPLET) {
            throw new RegleMetierException("Seuls les trajets PUBLIE ou COMPLET peuvent etre termines");
        }
        if (!LocalDateTime.now().isAfter(trajet.getDateDepart())) {
            throw new RegleMetierException("Le trajet ne peut etre termine qu'apres son heure de depart");
        }
        trajet.setStatut(StatutTrajet.TERMINE);
        log.info("Trajet termine : id={}", trajetId);
        return trajetMapper.toResponse(trajetRepository.save(trajet))
                .withPlacesRestantes(placesRestantes(trajet));
    }

    @Override
    @Transactional
    public TrajetResponse annuler(Long trajetId, Long conducteurId) {
        Trajet trajet = findWithDetails(trajetId);
        verifierProprietaire(trajet, conducteurId);
        if (trajet.getStatut() == StatutTrajet.TERMINE || trajet.getStatut() == StatutTrajet.ANNULE) {
            throw new RegleMetierException("Le trajet est deja dans un statut final");
        }
        trajet.setStatut(StatutTrajet.ANNULE);
        trajet.getReservations().stream()
                .filter(r -> r.getStatut() == StatutReservation.CONFIRMEE)
                .forEach(r -> r.setStatut(StatutReservation.ANNULEE));
        log.info("Trajet annule avec ses reservations : id={}", trajetId);
        return trajetMapper.toResponse(trajetRepository.save(trajet))
                .withPlacesRestantes(placesRestantes(trajet));
    }

    private void verifierParcours(List<PointArretRequest> points) {
        Set<String> villes = new HashSet<>();
        BigDecimal prixPrecedent = BigDecimal.ZERO.setScale(2);
        LocalDateTime heurePrecedente = null;

        for (int i = 0; i < points.size(); i++) {
            PointArretRequest p = points.get(i);
            if (p.ville() == null || p.ville().isBlank()) {
                throw new DonneesInvalidesException("La ville du point " + i + " est obligatoire");
            }
            if (p.heurePassage() == null || p.prixDepuisDepart() == null) {
                throw new DonneesInvalidesException("Heure et prix sont obligatoires pour chaque point");
            }
            if (!villes.add(norm(p.ville()))) {
                throw new DonneesInvalidesException("Une ville ne peut apparaitre qu'une seule fois : " + p.ville());
            }
            BigDecimal prix = p.prixDepuisDepart().setScale(2, RoundingMode.HALF_UP);
            if (prix.compareTo(BigDecimal.ZERO) < 0) {
                throw new DonneesInvalidesException("Le prix ne peut pas etre negatif");
            }
            if (i == 0 && prix.compareTo(BigDecimal.ZERO) != 0) {
                throw new DonneesInvalidesException("Le prix du point de depart doit valoir 0");
            }
            if (i > 0 && prix.compareTo(prixPrecedent) <= 0) {
                throw new DonneesInvalidesException("Les prix depuis le depart doivent etre strictement croissants");
            }
            if (heurePrecedente != null && !p.heurePassage().isAfter(heurePrecedente)) {
                throw new DonneesInvalidesException("Les heures de passage doivent etre strictement croissantes");
            }
            prixPrecedent = prix;
            heurePrecedente = p.heurePassage();
        }
        if (!points.get(0).heurePassage().isAfter(LocalDateTime.now())) {
            throw new DonneesInvalidesException("Le depart doit etre dans le futur");
        }
    }

    private void verifierProprietaire(Trajet trajet, Long conducteurId) {
        if (!trajet.getConducteur().getId().equals(conducteurId)) {
            log.warn("Acces refuse au trajet {} pour le conducteur {}", trajet.getId(), conducteurId);
            throw new AccesRefuseException("Ce trajet n'appartient pas a ce conducteur");
        }
    }

    private Trajet findWithDetails(Long id) {
        return trajetRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Trajet introuvable : " + id));
    }

    private Integer placesRestantes(Trajet trajet) {
        Integer occupees = reservationRepository.sumPlacesConfirmees(
                trajet.getId(), StatutReservation.CONFIRMEE);
        return trajet.getPlacesProposees() - (occupees == null ? 0 : occupees);
    }

    private String norm(String value) {
        return value == null ? null : value.trim();
    }
}
