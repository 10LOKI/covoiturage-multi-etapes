# COV-4 — Terminer / annuler un trajet

## Critères d'acceptation
- `TERMINE` uniquement après l'heure de départ, et seulement depuis `PUBLIE` ou `COMPLET`.
- `ANNULE` depuis `PUBLIE` ou `COMPLET`.
- L'annulation met aussi toutes les réservations `CONFIRMEE` en `ANNULEE`
  dans la même transaction.
- Réservé au conducteur propriétaire (403 sinon).

## Code livré
- `TrajetServiceImpl.terminer()` : propriétaire → statut → heure → sauvegarde.
- `TrajetServiceImpl.annuler()` : propriétaire → statut final → annulation des réservations.
- `verifierStatutModifiable()`, `verifierDepartDepasse()`, `verifierStatutNonFinal()`,
  `annulerReservationsConfirmees()` : une seule responsabilité chacune.
- `@Transactional` conservé sur les deux méthodes : succès ou échec en bloc.

## Vérification
- `.\mvnw.cmd test` → **16 tests, 0 échec**.
- Tests concernés : `terminer_refuseAvantLHeureDeDepart`, `annuleLeTrajetEtSesReservations`.
- Scénarios Postman : « Hamid termine avant 17:00 » (409) et
  « Hamid annule le trajet avec 2 réservations » (204).

## Branche
- `feat/trip-lifecycle`
