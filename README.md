# Plateforme de covoiturage multi-étapes

API REST d'une plateforme où des conducteurs publient des trajets composés de plusieurs
points d'arrêt, et où des passagers réservent une place sur une partie ou la totalité
du trajet.

## 1. Acteurs

- **Conducteur** : possède des véhicules, publie des trajets, consulte les réservations
  de ses trajets, termine ou annule ses trajets. Il ne réserve pas de place.
- **Passager** : recherche des trajets, réserve des places, consulte et annule ses
  propres réservations. Il ne publie pas de trajet.

## 2. Données métier

- **Conducteur** : identifiant, nom, prénom, email, téléphone, numéro de permis.
- **Passager** : identifiant, nom, prénom, email, téléphone.
- **Véhicule** : marque, modèle, immatriculation, couleur, capacité, conducteur propriétaire.
- **Trajet** : conducteur, véhicule, villes de départ et d'arrivée, date de départ,
  places proposées, statut, points d'arrêt.
- **Point d'arrêt** : ville, ordre (0 = départ), heure de passage, prix depuis le départ.
- **Réservation** : passager, trajet, point de montée, point de descente, nombre de
  places, montant total, date, statut.

## 3. Publication d'un trajet

- Un trajet contient au moins deux points d'arrêt : le premier est le départ, le dernier
  l'arrivée.
- Le départ est dans le futur et les heures de passage sont strictement croissantes.
- Une ville n'apparaît qu'une seule fois sur le parcours.
- Le prix depuis le départ vaut `0` au premier point, puis il est strictement croissant.
- Les places proposées sont comprises entre 1 et la capacité du véhicule.
- Le conducteur utilise uniquement l'un de ses propres véhicules.

## 4. Prix des segments et places restantes

Un segment est déduit de deux points d'arrêt : il n'est jamais stocké.

- **Prix d'une place** = prix du point de descente − prix du point de montée.
- **Montant d'une réservation** = prix d'une place × nombre de places.
- **Places restantes** = places proposées − somme des places des réservations
  `CONFIRMEE`.
- Tous les montants sont des `BigDecimal` à 2 décimales.

Exemple : Rabat (0 DH) → Casablanca (40 DH) → Settat (70 DH) → Marrakech (120 DH).

| Segment | Prix d'une place |
|---|---|
| Rabat → Marrakech | 120 DH |
| Casablanca → Settat | 30 DH |
| Settat → Marrakech | 50 DH |

Le sens du trajet est respecté : `Settat → Casablanca` et `Rabat → Agadir` sont refusés.

## 5. Réservation

- On ne réserve que sur un trajet `PUBLIE`.
- Les points de montée et de descente appartiennent au trajet, et la montée précède
  la descente.
- Le nombre de places réservées ne dépasse jamais les places proposées.
- Un passager n'a qu'une seule réservation `CONFIRMEE` par trajet.
- Une place réservée est occupée sur tout le trajet, quels que soient ses points de
  montée et de descente (simplification du brief).

## 6. Cycle de vie d'un trajet

- `PUBLIE` : ouvert à la réservation.
- `COMPLET` : plus aucune place disponible ; obtenu automatiquement quand la dernière
  place est réservée.
- `PUBLIE` : repris automatiquement quand une annulation libère une place.
- `TERMINE` : le trajet est terminé par son conducteur après l'heure de départ.
- `ANNULE` : le trajet est annulé par son conducteur ; toutes ses réservations
  `CONFIRMEE` passent à `ANNULEE` dans la même transaction.

`TERMINE` et `ANNULE` sont des statuts finaux.

## 7. Annulation par le passager

- Le passager annule sa propre réservation jusqu'à 24 heures avant le départ.
- Passé ce délai, la demande est refusée.
- Une place libérée sur un trajet `COMPLET` le fait repasser en `PUBLIE`.

## 8. Recherche

- Recherche par ville de départ, ville d'arrivée, date et prix maximal.
- Les villes recherchées peuvent être n'importe quels points du parcours, à condition
  que la ville de départ soit avant la ville d'arrivée.
- Le prix maximal s'applique au prix du segment recherché, pas au trajet complet.
- Résultats paginés et triés par heure de départ ; seuls les trajets `PUBLIE` avec
  des places restantes sont renvoyés.

## 9. Scénarios de validation du brief

1. Salma réserve 1 place Settat → Marrakech : 50 DH, 2 places restantes.
2. Karim recherche Settat → Casablanca : le trajet de Hamid n'apparaît pas.
3. Karim réserve 2 places Casablanca → Marrakech : 160 DH, trajet `COMPLET`.
4. Imane réserve sur le trajet complet : demande refusée.
5. Karim annule sa réservation 3 jours avant le départ : trajet de nouveau `PUBLIE`.
6. Imane monte à Marrakech et descend à Settat : ordre invalide, refus.
7. Hamid propose 5 places dans un véhicule de 4 places : refus.
8. Hamid publie avec le véhicule d'un autre conducteur : refus.
9. Hamid termine son trajet avant l'heure de départ : refus.
10. Salma annule à 2 heures du départ : délai de 24 heures dépassé, refus.
11. Hamid annule son trajet : le trajet et ses réservations passent en `ANNULE` /
    `ANNULEE` en une seule opération.
