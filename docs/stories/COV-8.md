# COV-8 — Annuler une réservation

## Critères d'acceptation
- Seul le passager propriétaire peut annuler (403 sinon).
- Délai minimal : 24 heures avant le départ (409 sinon).
- Réservation déjà annulée → 409.
- Si le trajet était `COMPLET`, il repasse en `PUBLIE`.

## Code livré
- `annuler()` : lecture, propriétaire, statut, délai, annulation, réouverture.
- `findReservation()`, `verifierProprietaire()`, `verifierConfirmee()`,
  `verifierDelai24h()`, `reouvrirTrajetSiComplet()` : une règle par méthode.
- `@Transactional` conservé : réservation et trajet cohérents en cas d'erreur.

## Vérification
- `.\mvnw.cmd test` → **16 tests, 0 échec**.
- Tests concernés : `annuler_accepteApres24hEtReouvreLeTrajetComplet`,
  `annuler_refuseMoinsDe24hAvantLeDepart`,
  `annuler_refuseUneReservationDUnAutrePassager`.
- Scénarios Postman 5 et 10 du brief.

## Branche
- `feat/cancel-booking`
