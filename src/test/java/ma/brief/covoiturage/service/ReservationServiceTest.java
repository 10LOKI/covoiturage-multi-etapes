package ma.brief.covoiturage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import ma.brief.covoiturage.domain.entity.Passager;
import ma.brief.covoiturage.domain.entity.PointArret;
import ma.brief.covoiturage.domain.entity.Reservation;
import ma.brief.covoiturage.domain.entity.Trajet;
import ma.brief.covoiturage.domain.enums.StatutReservation;
import ma.brief.covoiturage.domain.enums.StatutTrajet;
import ma.brief.covoiturage.domain.exception.AccesRefuseException;
import ma.brief.covoiturage.domain.exception.DonneesInvalidesException;
import ma.brief.covoiturage.domain.exception.RegleMetierException;
import ma.brief.covoiturage.dto.ReservationRequest;
import ma.brief.covoiturage.dto.ReservationResponse;
import ma.brief.covoiturage.mapper.ReservationMapper;
import ma.brief.covoiturage.repository.PassagerRepository;
import ma.brief.covoiturage.repository.ReservationRepository;
import ma.brief.covoiturage.repository.TrajetRepository;
import ma.brief.covoiturage.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private TrajetRepository trajetRepository;
    @Mock private PassagerRepository passagerRepository;
    @Mock private ReservationMapper mapper;

    @InjectMocks private ReservationServiceImpl service;

    private Passager passager;
    private Trajet trajet;
    private PointArret rabat;
    private PointArret casablanca;
    private PointArret marrakech;

    @BeforeEach
    void setUp() {
        passager = Passager.builder().id(2L).nom("Bennani").prenom("Sara")
                .email("sara@test.ma").telephone("0611111111").build();
        rabat = point(1L, "Rabat", 0, "0.00");
        casablanca = point(2L, "Casablanca", 1, "40.00");
        marrakech = point(3L, "Marrakech", 2, "120.00");
        trajet = Trajet.builder().id(5L).villeDepart("Rabat").villeArrivee("Marrakech")
                .dateDepart(LocalDateTime.now().plusHours(48))
                .placesProposees(2).statut(StatutTrajet.PUBLIE)
                .points(List.of(rabat, casablanca, marrakech)).build();
    }

    private PointArret point(Long id, String ville, int ordre, String prix) {
        return PointArret.builder().id(id).ville(ville).ordre(ordre)
                .prixDepuisDepart(new BigDecimal(prix))
                .heurePassage(LocalDateTime.now().plusHours(10 + ordre)).build();
    }

    private ReservationRequest request(String montee, String descente, int places) {
        return new ReservationRequest(5L, 2L, montee, descente, places);
    }

    private void stubCommun() {
        when(passagerRepository.findById(2L)).thenReturn(Optional.of(passager));
        when(trajetRepository.findByIdWithDetails(5L)).thenReturn(Optional.of(trajet));
    }

    private ReservationResponse response() {
        return new ReservationResponse(9L, 5L, 2L, "Casablanca", "Marrakech",
                2, new BigDecimal("160.00"), LocalDateTime.now(), StatutReservation.CONFIRMEE);
    }

    @Test
    void reserver_calculeLePrixEntreMonteeEtDescenteEtCompleteLeTrajet() {
        stubCommun();
        when(reservationRepository.findByPassagerIdAndTrajetIdAndStatut(
                2L, 5L, StatutReservation.CONFIRMEE)).thenReturn(Optional.empty());
        when(reservationRepository.sumPlacesConfirmees(5L, StatutReservation.CONFIRMEE)).thenReturn(0);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Reservation.class))).thenReturn(response());

        ReservationResponse result = service.reserver(request("Casablanca", "Marrakech", 2));

        assertThat(result.montantTotal()).isEqualByComparingTo("160.00");
        assertThat(result.statut()).isEqualTo(StatutReservation.CONFIRMEE);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).save(captor.capture());
        Reservation saved = captor.getValue();
        assertThat(saved.getPointMontage().getVille()).isEqualTo("Casablanca");
        assertThat(saved.getPointDescente().getVille()).isEqualTo("Marrakech");
        assertThat(saved.getMontantTotal()).isEqualByComparingTo("160.00");
        assertThat(saved.getNombrePlaces()).isEqualTo(2);
        assertThat(trajet.getStatut()).isEqualTo(StatutTrajet.COMPLET);
        verify(trajetRepository).save(trajet);
    }

    @Test
    void reserver_refuseUneSecondeReservationConfirmee() {
        stubCommun();
        Reservation deja = Reservation.builder().id(7L).statut(StatutReservation.CONFIRMEE).build();
        when(reservationRepository.findByPassagerIdAndTrajetIdAndStatut(
                2L, 5L, StatutReservation.CONFIRMEE)).thenReturn(Optional.of(deja));

        assertThatThrownBy(() -> service.reserver(request("Rabat", "Casablanca", 1)))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("deja une reservation confirmee");
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void reserver_refuseUneDescenteAvantLaMontee() {
        stubCommun();

        assertThatThrownBy(() -> service.reserver(request("Marrakech", "Casablanca", 1)))
                .isInstanceOf(DonneesInvalidesException.class)
                .hasMessageContaining("montee doit etre avant la descente");
    }

    @Test
    void reserver_refuseQuandIlManqueDesPlaces() {
        stubCommun();
        when(reservationRepository.findByPassagerIdAndTrajetIdAndStatut(
                2L, 5L, StatutReservation.CONFIRMEE)).thenReturn(Optional.empty());
        when(reservationRepository.sumPlacesConfirmees(5L, StatutReservation.CONFIRMEE)).thenReturn(1);

        assertThatThrownBy(() -> service.reserver(request("Rabat", "Marrakech", 2)))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("Places restantes insuffisantes : 1");
    }

    @Test
    void annuler_accepteApres24hEtReouvreLeTrajetComplet() {
        Reservation reservation = Reservation.builder().id(9L).passager(passager).trajet(trajet)
                .statut(StatutReservation.CONFIRMEE).nombrePlaces(2).build();
        trajet.setStatut(StatutTrajet.COMPLET);
        when(reservationRepository.findById(9L)).thenReturn(Optional.of(reservation));

        service.annuler(9L, 2L);

        assertThat(reservation.getStatut()).isEqualTo(StatutReservation.ANNULEE);
        assertThat(trajet.getStatut()).isEqualTo(StatutTrajet.PUBLIE);
        verify(trajetRepository).save(trajet);
    }

    @Test
    void annuler_refuseMoinsDe24hAvantLeDepart() {
        trajet.setDateDepart(LocalDateTime.now().plusHours(10));
        Reservation reservation = Reservation.builder().id(9L).passager(passager).trajet(trajet)
                .statut(StatutReservation.CONFIRMEE).build();
        when(reservationRepository.findById(9L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> service.annuler(9L, 2L))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("delai de 24 heures");
        assertThat(reservation.getStatut()).isEqualTo(StatutReservation.CONFIRMEE);
    }

    @Test
    void annuler_refuseUneReservationDUnAutrePassager() {
        Reservation reservation = Reservation.builder().id(9L)
                .passager(Passager.builder().id(99L).build())
                .trajet(trajet).statut(StatutReservation.CONFIRMEE).build();
        when(reservationRepository.findById(9L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> service.annuler(9L, 2L))
                .isInstanceOf(AccesRefuseException.class)
                .hasMessageContaining("n'appartient pas");
    }
}
