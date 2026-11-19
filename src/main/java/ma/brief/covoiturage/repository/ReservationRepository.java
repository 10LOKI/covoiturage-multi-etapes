package ma.brief.covoiturage.repository;

import java.util.List;
import java.util.Optional;
import ma.brief.covoiturage.domain.entity.Reservation;
import ma.brief.covoiturage.domain.enums.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByPassagerId(Long passagerId);

    @Query("select distinct r from Reservation r left join fetch r.passager left join fetch r.pointMontage left join fetch r.pointDescente where r.trajet.id = :trajetId")
    List<Reservation> findByTrajetIdWithDetails(@Param("trajetId") Long trajetId);

    Optional<Reservation> findByPassagerIdAndTrajetIdAndStatut(
            Long passagerId, Long trajetId, StatutReservation statut);

    @Query("select coalesce(sum(r.nombrePlaces), 0) from Reservation r where r.trajet.id = :trajetId and r.statut = :statut")
    Integer sumPlacesConfirmees(@Param("trajetId") Long trajetId,
                                @Param("statut") StatutReservation statut);
}
