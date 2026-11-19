package ma.brief.covoiturage.repository;

import java.util.List;
import java.util.Optional;
import ma.brief.covoiturage.domain.entity.Conducteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConducteurRepository extends JpaRepository<Conducteur, Long> {

    Optional<Conducteur> findByEmail(String email);

    Optional<Conducteur> findByNumeroPermis(String numeroPermis);

    List<Conducteur> findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(String nom, String email);
}
