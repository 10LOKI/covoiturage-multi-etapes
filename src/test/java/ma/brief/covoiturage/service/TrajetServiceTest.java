package ma.brief.covoiturage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import ma.brief.covoiturage.dto.PointArretRequest;
import ma.brief.covoiturage.dto.TrajetRequest;
import ma.brief.covoiturage.dto.TrajetResponse;
import ma.brief.covoiturage.mapper.ReservationMapper;
import ma.brief.covoiturage.mapper.TrajetMapper;
import ma.brief.covoiturage.repository.ConducteurRepository;
import ma.brief.covoiturage.repository.ReservationRepository;
import ma.brief.covoiturage.repository.TrajetRepository;
import ma.brief.covoiturage.repository.VehiculeRepository;
import ma.brief.covoiturage.service.impl.TrajetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TrajetServiceTest {

    @Mock private TrajetRepository trajetRepository;
    @Mock private ConducteurRepository conducteurRepository;
    @Mock private VehiculeRepository vehiculeRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private TrajetMapper trajetMapper;
    @Mock private ReservationMapper reservationMapper;

    @InjectMocks private TrajetServiceImpl service;

    private Conducteur conducteur;
    private Vehicule vehicule;

    @BeforeEach
    void setUp() {
        conducteur = Conducteur.builder().id(1L).nom("Alami").prenom("Hamid")
                .email("h@test.ma").telephone("0600000000").numeroPermis("P1").build();
        vehicule = Vehicule.builder().id(10L).marque("Dacia").modele("Logan")
                .immatriculation("AA-1").capacite(4).conducteur(conducteur).build();
    }

    private LocalDateTime future(int hours) {
        return LocalDateTime.now().plusHours(hours);
    }

    private TrajetRequest request(int places) {
        return new TrajetRequest(1L, 10L, places, List.of(
                new PointArretRequest("Rabat", future(5), BigDecimal.ZERO),
                new PointArretRequest("Casablanca", future(6), new BigDecimal("40.00")),
                new PointArretRequest("Marrakech", future(9), new BigDecimal("120.00"))));
    }

    @Test
    void publish_refuseLeVehiculeDUnAutreConducteur() {
        Conducteur autre = Conducteur.builder().id(2L).build();
        vehicule.setConducteur(autre);
        when(conducteurRepository.findById(1L)).thenReturn(Optional.of(conducteur));
        when(vehiculeRepository.findById(10L)).thenReturn(Optional.of(vehicule));

        assertThatThrownBy(() -> service.publish(request(3)))
                .isInstanceOf(AccesRefuseException.class)
                .hasMessageContaining("propres vehicules");
    }

    @Test
    void publish_refusePlusDePlacesQueLaCapacite() {
        when(conducteurRepository.findById(1L)).thenReturn(Optional.of(conducteur));
        when(vehiculeRepository.findById(10L)).thenReturn(Optional.of(vehicule));

        assertThatThrownBy(() -> service.publish(request(5)))
                .isInstanceOf(DonneesInvalidesException.class)
                .hasMessageContaining("4 places au maximum");
    }

    @Test
    void publish_refuseLesPrixDecroissants() {
        TrajetRequest invalid = new TrajetRequest(1L, 10L, 2, List.of(
                new PointArretRequest("Rabat", future(5), BigDecimal.ZERO),
                new PointArretRequest("Casablanca", future(6), new BigDecimal("50.00")),
                new PointArretRequest("Marrakech", future(9), new BigDecimal("40.00"))));
        when(conducteurRepository.findById(1L)).thenReturn(Optional.of(conducteur));
        when(vehiculeRepository.findById(10L)).thenReturn(Optional.of(vehicule));

        assertThatThrownBy(() -> service.publish(invalid))
                .isInstanceOf(DonneesInvalidesException.class)
                .hasMessageContaining("croissants");
    }

    @Test
    void publish_creeUnTrajetPUBLIEAvecPointsOrdonnes() {
        when(conducteurRepository.findById(1L)).thenReturn(Optional.of(conducteur));
        when(vehiculeRepository.findById(10L)).thenReturn(Optional.of(vehicule));
        when(trajetRepository.save(any(Trajet.class))).thenAnswer(inv -> inv.getArgument(0));
        when(trajetMapper.toResponse(any(Trajet.class))).thenReturn(
                new TrajetResponse(null, 1L, null, 10L, null, "Rabat", "Marrakech",
                        null, 3, null, StatutTrajet.PUBLIE, List.of()));

        TrajetResponse response = service.publish(request(3));

        assertThat(response.statut()).isEqualTo(StatutTrajet.PUBLIE);
        assertThat(response.placesRestantes()).isEqualTo(3);
        verify(trajetRepository).save(any(Trajet.class));
    }

    @Test
    void terminer_refuseAvantLHeureDeDepart() {
        Trajet trajet = Trajet.builder().id(5L).conducteur(conducteur).vehicule(vehicule)
                .statut(StatutTrajet.PUBLIE).placesProposees(2)
                .dateDepart(future(10)).points(new ArrayList<>()).build();
        when(trajetRepository.findByIdWithDetails(5L)).thenReturn(Optional.of(trajet));

        assertThatThrownBy(() -> service.terminer(5L, 1L))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("apres son heure de depart");
    }

    @Test
    void annuleLeTrajetEtSesReservations() {
        Trajet trajet = Trajet.builder().id(6L).conducteur(conducteur).vehicule(vehicule)
                .statut(StatutTrajet.COMPLET).placesProposees(2)
                .dateDepart(future(10)).points(new ArrayList<>()).build();
        Reservation r1 = Reservation.builder().id(1L).statut(StatutReservation.CONFIRMEE).trajet(trajet).build();
        Reservation r2 = Reservation.builder().id(2L).statut(StatutReservation.CONFIRMEE).trajet(trajet).build();
        trajet.setReservations(new ArrayList<>(List.of(r1, r2)));
        when(trajetRepository.findByIdWithDetails(6L)).thenReturn(Optional.of(trajet));
        when(trajetRepository.save(any(Trajet.class))).thenAnswer(inv -> inv.getArgument(0));
        when(reservationRepository.sumPlacesConfirmees(6L, StatutReservation.CONFIRMEE)).thenReturn(0);
        when(trajetMapper.toResponse(any(Trajet.class))).thenReturn(
                new TrajetResponse(6L, 1L, null, 10L, null, "Rabat", "Marrakech",
                        null, 2, null, StatutTrajet.ANNULE, List.of()));

        service.annuler(6L, 1L);

        assertThat(trajet.getStatut()).isEqualTo(StatutTrajet.ANNULE);
        assertThat(trajet.getReservations())
                .allMatch(r -> r.getStatut() == StatutReservation.ANNULEE);
    }

    @Test
    void search_utiliseDesBornesParDefautQuandLesFiltresSontNuls() {
        when(trajetRepository.search(eq("Rabat"), eq("Marrakech"), any(LocalDateTime.class),
                any(LocalDateTime.class), any(BigDecimal.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        service.search("Rabat", "Marrakech", null, null, PageRequest.of(0, 10));

        ArgumentCaptor<LocalDateTime> debut = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> fin = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<BigDecimal> prix = ArgumentCaptor.forClass(BigDecimal.class);
        verify(trajetRepository).search(eq("Rabat"), eq("Marrakech"),
                debut.capture(), fin.capture(), prix.capture(), any(Pageable.class));
        assertThat(debut.getValue()).isEqualTo(LocalDateTime.of(1900, 1, 1, 0, 0));
        assertThat(fin.getValue()).isEqualTo(LocalDateTime.of(2100, 12, 31, 23, 59, 59));
        assertThat(prix.getValue()).isEqualByComparingTo("999999999.99");
    }

    @Test
    void search_appliqueLesFiltresDeDateEtDePrix() {
        java.time.LocalDate date = java.time.LocalDate.of(2026, 11, 25);
        BigDecimal prixMax = new BigDecimal("150.00");
        when(trajetRepository.search(eq("Rabat"), eq("Marrakech"),
                eq(date.atStartOfDay()), eq(date.atTime(java.time.LocalTime.MAX)),
                eq(prixMax), any(Pageable.class))).thenReturn(Page.empty());

        service.search("Rabat", "Marrakech", date, prixMax, PageRequest.of(0, 10));

        verify(trajetRepository).search(eq("Rabat"), eq("Marrakech"),
                eq(date.atStartOfDay()), eq(date.atTime(java.time.LocalTime.MAX)),
                eq(prixMax), any(Pageable.class));
    }
}
