# COV-6 — Empêcher les doublons confirmés

## Critères d'acceptation
- Un passager ne peut avoir qu'une réservation `CONFIRMEE` par trajet.
- Une deuxième demande est refusée avec le code 409 et un message clair.

## Code livré
- `verifierPasDeDoublon()` : un `boolean` explicite, un `if`, une exception métier.
- Appel dans `reserver()` juste après la validation des points.
- Interface `ReservationRepository` conservée.

## Vérification
- `.\mvnw.cmd test` → **16 tests, 0 échec**.
- Test concerné : `reserver_refuseUneSecondeReservationConfirmee`.
- Vérification HTTP : deux réservations confirmées du même passager sur le même trajet
  → 409 `REGLE_METIER_VIOLEE`.

## Branche
- `feat/prevent-duplicate-booking`
