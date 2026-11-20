# COV-7 — Vérifier les places restantes

## Critères d'acceptation
- Places restantes = places proposées − somme des places `CONFIRMEE`.
- Toute demande supérieure aux places restantes est refusée (409).
- La valeur est calculée, jamais stockée en base.

## Code livré
- `placesRestantes()` : calcul centralisé dans `ReservationServiceImpl`.
- `verifierPlacesDisponibles()` : une seule comparaison, un seul message d'erreur.
- Même calcul utilisé dans `TrajetServiceImpl` pour l'affichage du détail.
- Interfaces repositories conservées.

## Vérification
- `.\mvnw.cmd test` → **16 tests, 0 échec**.
- Test concerné : `reserver_refuseQuandIlManqueDesPlaces` (1 place restante, 2 demandées → 409).
- Scénarios Postman 1, 3 et 4 du brief (2 places restantes, trajet `COMPLET`, refus).

## Branche
- `feat/remaining-seats`
