# Backlog JIRA — Plateforme de covoiturage multi-étapes

> À importer dans un projet JIRA (type *Scrum*) : `EPIC` puis `Story`, avec les criticalités
> ci-dessous. Chaque story correspond à un scénario vérifiable par la collection Postman.

## EPIC-1 — Publication et recherche de trajets (8 pts)

| Clé | Story | Critères d'acceptation | Pts |
|---|---|---|---|
| COV-1 | Publier un trajet multi-étapes | 2 points minimum, villes uniques, heures et prix croissants, prix du départ = 0, départ futur, places ≤ capacité du véhicule, statut `PUBLIE` | 3 |
| COV-2 | Rechercher un trajet par ville, date et prix | Une seule requête SQL, filtres optionnels, pagination, `prixSegment = prix(descente) − prix(montee)`, places restantes calculées | 3 |
| COV-3 | Voir le détail d'un trajet | Retourne les points ordonnés et les places restantes calculées | 1 |
| COV-4 | Terminer / annuler un trajet | `TERMINE` seulement après l'heure de départ ; `ANNULE` annule aussi ses réservations dans la même transaction | 2 |

## EPIC-2 — Réservation et règles métier (8 pts)

| Clé | Story | Critères d'acceptation | Pts |
|---|---|---|---|
| COV-5 | Réserver un segment | Montée avant descente, villes du trajet, `BigDecimal` 2 décimales, statut `CONFIRMEE`, trajet passé en `COMPLET` si plus de places | 3 |
| COV-6 | Empêcher les doublons confirmés | Une seule réservation `CONFIRMEE` par passager et par trajet → 409 | 1 |
| COV-7 | Vérifier les places restantes | Refus si places demandées > places restantes → 409 | 2 |
| COV-8 | Annuler une réservation | Propriétaire uniquement (403), délai ≥ 24 h avant départ (409), réouverture du trajet `COMPLET` | 2 |

## EPIC-3 — Qualité, sécurité et livrables (8 pts)

| Clé | Story | Critères d'acceptation | Pts |
|---|---|---|---|
| COV-9 | Erreurs normalisées | `ProblemDetail` avec codes 400 / 403 / 404 / 409 / 500 | 2 |
| COV-10 | Tests unitaires | JUnit 5 + Mockito + AssertJ, 16 tests verts | 3 |
| COV-11 | Documentation et démo | README, collection Postman, diagramme UML, ≥ 5 lignes par table, lien GitHub | 3 |

## Scénarios de recette (Postman)

Les 16 requêtes de `postman_collection.json` couvrent les scénarios COV-1 à COV-11,
y compris les cas d'erreur 400/403/404/409 et l'annulation atomique d'un trajet complet.
