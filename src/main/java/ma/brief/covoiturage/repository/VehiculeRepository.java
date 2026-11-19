package ma.brief.covoiturage.repository;

import java.util.List;
import java.util.Optional;
import ma.brief.covoiturage.domain.entity.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    Optional<Vehicule> findByImmatriculation(String immatriculation);

    List<Vehicule> findByConducteurId(Long conducteurId);

    @Query("select count(t) > 0 from Trajet t where t.vehicule.id = :id")
    boolean existsTrajets(@Param("id") Long id);
}
