# COV-10 — Tests unitaires

## Critères d'acceptation
- JUnit 5, Mockito et AssertJ.
- 16 tests verts, couvrant les règles métier et les cas d'erreur.
- Aucun test ne dépend de l'ordre d'exécution.

## Code livré
- `TrajetServiceTest` : 8 tests (publication, capacité, prix croissants, statut,
  annulation atomique, recherche).
- `ReservationServiceTest` : 7 tests (prix, doublon, ordre des points, places,
  annulation, délai 24 h, propriétaire).
- `CovoiturageApplicationTests` : 1 test de chargement du contexte Spring.
- Les classes de test restent simples : `@BeforeEach` pour les données communes,
  un test par règle, assertions AssertJ lisibles.

## Vérification
- Commande : `.\\mvnw.cmd test`
- Résultat : **Tests run: 16, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS**.
- Prérequis local : PostgreSQL démarré (`.\\scripts\\start-db.ps1`), sinon le test
  de chargement du contexte échoue faute de dialecte JDBC.

## Branche
- `feat/COV-10`
