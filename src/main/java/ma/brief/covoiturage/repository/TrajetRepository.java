package ma.brief.covoiturage.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import ma.brief.covoiturage.domain.entity.Trajet;
import ma.brief.covoiturage.dto.TrajetSearchItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrajetRepository extends JpaRepository<Trajet, Long> {

    @Query("""
            select distinct t from Trajet t
            left join fetch t.points
            left join fetch t.conducteur
            left join fetch t.vehicule
            where t.id = :id
            """)
    Optional<Trajet> findByIdWithDetails(@Param("id") Long id);

    List<Trajet> findByConducteurId(Long conducteurId);

    @Query("""
            select new ma.brief.covoiturage.dto.TrajetSearchItem(
                t.id,
                p1.ville,
                p2.ville,
                t.dateDepart,
                p2.prixDepuisDepart - p1.prixDepuisDepart,
                t.placesProposees - coalesce((
                    select sum(r.nombrePlaces) from Reservation r
                    where r.trajet = t
                      and r.statut = ma.brief.covoiturage.domain.enums.StatutReservation.CONFIRMEE
                ), 0),
                t.statut
            )
            from Trajet t
            join t.points p1
            join t.points p2
            where p1.ville = :villeDepart
              and p2.ville = :villeArrivee
              and p1.ordre < p2.ordre
              and t.statut = ma.brief.covoiturage.domain.enums.StatutTrajet.PUBLIE
              and t.dateDepart >= :dateDebut
              and t.dateDepart < :dateFin
              and (p2.prixDepuisDepart - p1.prixDepuisDepart) <= :prixMax
              and (t.placesProposees - coalesce((
                    select sum(r.nombrePlaces) from Reservation r
                    where r.trajet = t
                      and r.statut = ma.brief.covoiturage.domain.enums.StatutReservation.CONFIRMEE
              ), 0)) > 0
            """)
    Page<TrajetSearchItem> search(
            @Param("villeDepart") String villeDepart,
            @Param("villeArrivee") String villeArrivee,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            @Param("prixMax") java.math.BigDecimal prixMax,
            Pageable pageable);
}
