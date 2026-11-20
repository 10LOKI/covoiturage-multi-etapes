# COV-3 — Voir le détail d'un trajet

## Critères d'acceptation
- Retourne les points d'arrêt ordonnés.
- Retourne les places restantes calculées.
- Trajet introuvable → 404 avec un message clair.

## Code livré
- `TrajetServiceImpl.getById()` : une ligne, lecture via `findWithDetails()` puis mapping.
- `toResponseAvecPlaces()` : ajoute les places restantes au `TrajetResponse`.
- `findWithDetails()` utilise `findByIdWithDetails()` (JOIN FETCH) pour éviter les N+1.
- Interface `TrajetMapper` conservée.

## Vérification
- `.\mvnw.cmd test` → **16 tests, 0 échec**.
- Vérification HTTP : `GET /api/trajets/1` renvoie `points` triés et `placesRestantes`.
- Scénario Postman : « Trajet de Hamid (PUBLIE, 3 places) ».

## Branche
- `feat/trip-details`
