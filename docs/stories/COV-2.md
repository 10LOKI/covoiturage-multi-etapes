# COV-2 — Rechercher un trajet par ville, date et prix

## Critères d'acceptation
- Filtres optionnels : ville de départ, ville d'arrivée, date, prix maximal.
- Une seule requête JPQL dans `TrajetRepository.search()`.
- Pagination et tri par heure de départ.
- `prixSegment = prix(descente) − prix(montee)`.
- Places restantes calculées, jamais stockées.

## Code livré
- `TrajetServiceImpl.search()` : appel simple au repository avec les filtres normalisés.
- `debutRecherche()`, `finRecherche()`, `prixMaxRecherche()` : valeurs par défaut
  quand un filtre est absent.
- Interface `TrajetRepository` conservée : la logique SQL reste dans une seule requête.

## Vérification
- `.\mvnw.cmd test` → **16 tests, 0 échec**.
- Tests concernés : `search_utiliseDesBornesParDefautQuandLesFiltresSontNuls`,
  `search_appliqueLesFiltresDeDateEtDePrix`.
- Scénario Postman : « Rechercher Settat → Casablanca » (trajet de Hamid absent).

## Branche
- `feat/search-trips`
