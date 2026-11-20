# COV-5 — Réserver un segment

## Critères d'acceptation
- Montee avant descente, les deux villes appartiennent au trajet.
- Montant = (prix descente − prix montée) × places, `BigDecimal` 2 décimales.
- Réservation créée avec le statut `CONFIRMEE`.
- Trajet passé en `COMPLET` quand la dernière place est réservée.

## Code livré
- `ReservationServiceImpl.reserver()` : enchaînement lisible — trajet, points, doublon,
  places, montant, sauvegarde, statut.
- `verifierTrajetPublie()`, `verifierOrdrePoints()`, `calculerMontant()`,
  `mettreAJourStatut()` : une seule règle par méthode.
- Interfaces `ReservationService`, `ReservationRepository`, `TrajetRepository` et
  `ReservationMapper` conservées.

## Vérification
- `.\mvnw.cmd test` → **16 tests, 0 échec**.
- Test concerné : `reserver_calculeLePrixEntreMonteeEtDescenteEtCompleteLeTrajet`
  (160,00 DH pour 2 places Casablanca → Marrakech).
- Scénarios Postman 1 et 3 du brief (50 DH et 160 DH).

## Branche
- `feat/book-segment`
