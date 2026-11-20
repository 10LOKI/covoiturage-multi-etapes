# COV-1 — Publier un trajet multi-étapes

## Critères d'acceptation
- Au moins deux points d'arrêt, villes uniques.
- Heures et prix depuis le départ strictement croissants, prix du départ = 0.
- Départ dans le futur.
- Places proposées ≤ capacité du véhicule.
- Le conducteur utilise uniquement son propre véhicule.
- Trajet créé avec le statut `PUBLIE`.

## Code livré
- `TrajetServiceImpl.publish()` : lecture du conducteur, du véhicule, contrôles, création.
- `findConducteur()`, `findVehicule()`, `verifierVehicule()` : recherche et contrôle du véhicule.
- `construireTrajet()` : création du trajet et de ses points ordonnés.
- `verifierParcours()` découpé en petites méthodes : `verifierPointObligatoire()`,
  `verifierVilleUnique()`, `verifierPrix()`, `verifierHeure()`, `verifierDepartFutur()`.
- Les interfaces `TrajetService`, `ConducteurRepository`, `VehiculeRepository`,
  `TrajetRepository` et `TrajetMapper` restent inchangées.

## Vérification
- `.\mvnw.cmd test` → **16 tests, 0 échec**.
- Tests concernés : `publish_refuseLeVehiculeDUnAutreConducteur`,
  `publish_refusePlusDePlacesQueLaCapacite`, `publish_refuseLesPrixDecroissants`,
  `publish_creeUnTrajetPUBLIEAvecPointsOrdonnes`.
- Scénario Postman : « Publier un trajet ».

## Branche
- `feat/COV-1`
