# Plateforme de covoiturage multi-étapes — API REST

Backend REST (Spring Boot 3.5.7 / Java 17 / Maven / PostgreSQL).

Dépôt GitHub : <https://github.com/10LOKI/covoiturage-multi-etapes>

## 1. Prérequis installés sur cette machine

| Outil | Version | Emplacement |
|---|---|---|
| JDK Temurin | 17.0.21 | `C:\Users\Hp\Tools\jdk-17.0.21+9` |
| Maven | 3.10.1 (Maven Wrapper `mvnw.cmd`) | `~/.m2/wrapper/dists` |
| PostgreSQL | 17.6 (portable) | `C:\Users\Hp\Tools\pgsql` |
| Données | `C:\Users\Hp\Tools\pgdata` | base `covoiturage`, user `postgres`, mot de passe `postgres` |

> Spring Boot et Maven n'ont pas besoin d'installation séparée : le `pom.xml` et le wrapper `mvnw.cmd` suffisent.

## 2. Démarrer la base de données

### Option A — PostgreSQL portable (utilisé ici, Docker indisponible)

```powershell
.\scripts\start-db.ps1
```

Pour l'arrêter : `.\scripts\stop-db.ps1`

### Option B — Docker Compose (quand la virtualisation sera activée)

```powershell
docker compose up -d
```

> Sur cette machine, Docker Desktop échoue avec `HCS_E_HYPERV_NOT_INSTALLED` :
> la virtualisation n'est pas activée. Il faut activer **Plateforme de machines
> virtuelles** / **Hyper-V** dans les fonctionnalités Windows (et dans le BIOS si
> nécessaire), puis redémarrer. Le fichier `docker-compose.yml` est déjà prêt.

## 3. Démarrer l'API

```powershell
$env:JAVA_HOME="C:\Users\Hp\Tools\jdk-17.0.21+9"
.\mvnw.cmd spring-boot:run
```

Vérification : <http://localhost:8080/actuator/health> doit répondre `{"status":"UP"}`.

## 4. Tests

```powershell
$env:JAVA_HOME="C:\Users\Hp\Tools\jdk-17.0.21+9"
.\mvnw.cmd test
```

Résultat actuel : **16 tests, 0 échec** (`CovoiturageApplicationTests`,
`TrajetServiceTest`, `ReservationServiceTest`).

## 5. Endpoints

| Méthode | Route | Description |
|---|---|---|
| GET | `/actuator/health` | Santé de l'API |
| POST | `/api/trajets` | Publier un trajet (201) |
| GET | `/api/trajets/{id}` | Détail d'un trajet |
| GET | `/api/trajets/recherche?villeDepart=…&villeArrivee=…&date=…&prixMax=…` | Recherche paginée |
| GET | `/api/trajets/{id}/reservations?conducteurId=…` | Réservations d'un trajet |
| POST | `/api/trajets/{id}/terminer?conducteurId=…` | Terminer un trajet |
| POST | `/api/trajets/{id}/annuler?conducteurId=…` | Annuler trajet + réservations (204) |
| POST | `/api/reservations` | Réserver un segment (201) |
| GET | `/api/reservations?passagerId=…` | Réservations d'un passager |
| DELETE | `/api/reservations/{id}?passagerId=…` | Annuler une réservation (204) |

## 6. Collection Postman et diagramme UML

- Collection générale : [`postman_collection.json`](postman_collection.json) — 16 requêtes avec scripts de test
  (codes 200/201/204/400/403/404/409, calcul du prix, annulation atomique, délai 24 h).
- Collection exacte du brief : [`postman_scenarios_11.json`](postman_scenarios_11.json) — les **11 scénarios**
  du PDF (Salma, Karim, Imane, Hamid), à exécuter dans l'ordre sur le trajet de Hamid.
  Importer dans Postman via **Import → File**, puis garder la variable `baseUrl=http://localhost:8080`.
- Diagramme de classes : [`docs/class-diagram.png`](docs/class-diagram.png)
  (source PlantUML : [`docs/class-diagram.puml`](docs/class-diagram.puml)).
- Backlog JIRA à importer : [`docs/jira-backlog.md`](docs/jira-backlog.md).

## 7. Scénarios vérifiés sur l'API en cours d'exécution

| Scénario | Résultat |
|---|---|
| Publication d'un trajet à 5 points, prix croissants | 201, `PUBLIE`, 3 places restantes |
| Recherche Rabat → Marrakech avec `prixMax` | 200, projection en une requête SQL |
| Réservation Casablanca → Marrakech | 201, montant `80.00` = 120 − 40 |
| Deuxième réservation confirmée du même passager | 409 |
| Montée après descente | 400 |
| Réservations d'un trajet par un autre conducteur | 403 |
| Trajet inexistant | 404 |
| Annulation de sa réservation (> 24 h) | 204 |
| Annulation de la réservation d'autrui | 403 |
| Terminer avant l'heure de départ | 409 |
| Annuler le trajet | 204, réservations passées en `ANNULEE` |
| Annuler à moins de 24 h du départ | 409 |
| Validation Bean Validation (ville vide, 0 place) | 400 |

## 8. Structure prévue

```
src/main/java/ma/brief/covoiturage/
├── config/            # configuration globale
├── domain/
│   ├── entity/        # Conducteur, Passager, Vehicule, Trajet, PointArret, Reservation
│   ├── enums/         # StatutTrajet, StatutReservation
│   └── exception/     # exceptions métier + RestControllerAdvice
├── dto/               # records (request/response)
├── mapper/            # MapStruct
├── repository/        # Spring Data JPA
├── service/           # interfaces + implémentations (@Transactional)
└── controller/        # endpoints REST
```

## 9. Règles métier clés (du brief)

- Un trajet a au moins 2 points d'arrêt, ordonnés, villes uniques, prix croissants.
- Prix d'un segment = prix(descente) − prix(montée), en `BigDecimal` 2 décimales.
- Places restantes = places proposées − places des réservations `CONFIRMEE` (calculé, jamais stocké).
- Une seule réservation `CONFIRMEE` par passager et par trajet.
- Annulation réservation jusqu'à 24 h avant le départ.
- Annulation d'un trajet = trajet + réservations annulés dans la même transaction.
- Codes HTTP : 400 validation, 403 ressource d'autrui, 404 introuvable, 409 règle métier, 500 interne.

## 10. Avant la soutenance

- Slides simples : [`docs/slides.md`](docs/slides.md).
- Jeu de données : 5 conducteurs, 6 passagers (dont Karim), 5 véhicules, 5 trajets,
  13 points d'arrêt, 5 réservations (≥ 5 dans chaque table).
- Ouvrir : IDE, `docs/class-diagram.png`, `docs/jira-backlog.md`, le dépôt GitHub
  et la collection `postman_scenarios_11.json`.
- Lancer la base (`.\scripts\start-db.ps1`) puis l'API avant d'importer Postman.
