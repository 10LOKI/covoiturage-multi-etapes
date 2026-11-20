# COV-11 — Documentation et démo

## Critères d'acceptation
- README expliquant la logique métier du brief.
- Collection Postman couvrant les scénarios.
- Diagramme de classes UML (image).
- Au moins 5 enregistrements par table.
- Lien vers le dépôt GitHub.
- Backlog JIRA et slides de soutenance.

## Vérification effectuée
- `README.md` : uniquement la logique métier (acteurs, règles, statuts, scénarios).
- `postman_collection.json` : **16 requêtes**, JSON valide.
- `postman_scenarios_11.json` : les **11 scénarios du brief**, JSON valide.
- `docs/class-diagram.png` : 160 Ko, diagramme UML généré depuis `class-diagram.puml`.
- `docs/jira-backlog.md` : 11 stories réparties en 3 epics.
- `docs/slides.md` : 7 slides de démonstration.
- Base de démonstration : 5 conducteurs, 6 passagers, 5 véhicules, 5 trajets,
  13 points d'arrêt, 5 réservations (≥ 5 dans chaque table).
- Dépôt : <https://github.com/10LOKI/covoiturage-multi-etapes>.

## Branche
- `feat/publish-multi-step-trip1`
