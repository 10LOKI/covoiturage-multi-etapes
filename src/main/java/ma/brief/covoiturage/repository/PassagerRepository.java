package ma.brief.covoiturage.repository;

import java.util.List;
import java.util.Optional;
import ma.brief.covoiturage.domain.entity.Passager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassagerRepository extends JpaRepository<Passager, Long> {

    Optional<Passager> findByEmail(String email);

    List<Passager> findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(String nom, String email);
}
