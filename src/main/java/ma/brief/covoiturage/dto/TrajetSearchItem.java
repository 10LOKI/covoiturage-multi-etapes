package ma.brief.covoiturage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ma.brief.covoiturage.domain.enums.StatutTrajet;

/** Projection retournee par la recherche en une seule requete JPQL. */
@Getter
@AllArgsConstructor
public class TrajetSearchItem {
    private final Long id;
    private final String villeDepart;
    private final String villeArrivee;
    private final LocalDateTime dateDepart;
    private final BigDecimal prixSegment;
    private final Integer placesRestantes;
    private final StatutTrajet statut;

    /**
     * Hibernate retourne un {@link Long} pour la soustraction
     * {@code placesProposees - coalesce(sum(...), 0)} de la requete de recherche.
     */
    public TrajetSearchItem(Long id, String villeDepart, String villeArrivee,
                            LocalDateTime dateDepart, BigDecimal prixSegment,
                            Long placesRestantes, StatutTrajet statut) {
        this(id, villeDepart, villeArrivee, dateDepart, prixSegment,
                placesRestantes == null ? 0 : placesRestantes.intValue(), statut);
    }
}
