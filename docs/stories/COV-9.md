# COV-9 — Erreurs normalisées

## Critères d'acceptation
- Toutes les erreurs sont renvoyées sous forme de `ProblemDetail`.
- Codes : 400 (validation / données invalides), 403 (accès refusé),
  404 (introuvable), 409 (règle métier), 500 (erreur interne).
- Chaque réponse contient timestamp, code, type, message et chemin.
- Les erreurs de validation listent les champs en faute.

## Code livré
- `GlobalExceptionHandler` : un handler par type d'exception, une méthode `build()` commune.
- `handleValidation()` : boucle `for` simple sur les `FieldError`.
- Import `Instant` explicite pour le timestamp.
- Interfaces et exceptions métier inchangées.

## Vérification
- `.\mvnw.cmd test` → **16 tests, 0 échec**.
- Vérification HTTP : les scénarios 4, 6, 7, 8, 9 et 10 du brief renvoient
  respectivement 409, 400, 400, 403, 409 et 409 avec un `detail` explicite.
- Collection Postman : scripts de test sur les codes 400/403/404/409.

## Branche
- `feat/COV-9`
