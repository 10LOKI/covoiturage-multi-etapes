# Vérification des user stories — Plateforme de covoiturage

Chaque story a sa branche `feat/COV-x` et son dossier de vérification dans `docs/stories/`.
Les branches sont cumulatives : `feat/COV-2` part de `feat/COV-1`, etc.
À fusionner dans l'ordre de 1 à 11.

| Story | Branche | Fichier de vérification | Résultat |
|---|---|---|---|
| COV-1 Publier un trajet | `feat/COV-1` | `docs/stories/COV-1.md` | 16 tests OK |
| COV-2 Rechercher | `feat/COV-2` | `docs/stories/COV-2.md` | 16 tests OK |
| COV-3 Détail d'un trajet | `feat/COV-3` | `docs/stories/COV-3.md` | 16 tests OK |
| COV-4 Fin de cycle du trajet | `feat/COV-4` | `docs/stories/COV-4.md` | 16 tests OK |
| COV-5 Réserver un segment | `feat/COV-5` | `docs/stories/COV-5.md` | 16 tests OK |
| COV-6 Pas de doublon confirmé | `feat/COV-6` | `docs/stories/COV-6.md` | 16 tests OK |
| COV-7 Places restantes | `feat/COV-7` | `docs/stories/COV-7.md` | 16 tests OK |
| COV-8 Annuler une réservation | `feat/COV-8` | `docs/stories/COV-8.md` | 16 tests OK |
| COV-9 Erreurs normalisées | `feat/COV-9` | `docs/stories/COV-9.md` | 16 tests OK |
| COV-10 Tests unitaires | `feat/COV-10` | `docs/stories/COV-10.md` | 16 tests OK |
| COV-11 Documentation et démo | `feat/COV-11` | `docs/stories/COV-11.md` | livrables présents |

## Règles de style appliquées

- Code simple et lisible : petites méthodes privées, une seule responsabilité par méthode,
  conditions explicites plutôt que des lambdas complexes.
- Architecture conservée : `Controller → Service (interface + impl) → Repository → Entity`.
- DTO `records`, MapStruct, Bean Validation, `ProblemDetail` et logs SLF4J inchangés.
- Aucun comportement métier modifié : les 16 tests et les 11 scénarios Postman restent verts.
