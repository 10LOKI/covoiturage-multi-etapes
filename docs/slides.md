# Slides de présentation — Plateforme de covoiturage multi-étapes

> 5 minutes de démonstration : 1 slide ≈ 30 s. Ouvrir ce fichier dans un éditeur Markdown ou le convertir en PDF.

---

## 1. Titre

**Plateforme de covoiturage multi-étapes — API REST**

Spring Boot 3.5.7 · Java 17 · Maven · PostgreSQL · JPA/Hibernate

Dépôt : <https://github.com/10LOKI/covoiturage-multi-etapes>

---

## 2. Contexte et objectifs

- Conducteurs publient des trajets avec **plusieurs points d'arrêt**.
- Passagers réservent **une partie ou la totalité** du trajet.
- Prix d'un segment = `prix(descente) − prix(montee)`.
- Gestion des annulations, des places restantes et des statuts.

---

## 3. Stack et architecture

- **Backend REST uniquement**, Spring Boot, Java 17, Maven Wrapper.
- Couches : `Controller → Service (interface + impl) → Repository → Entity`.
- DTO `records` + **MapStruct**, **Bean Validation**, **SLF4J**.
- Exceptions centralisées en **ProblemDetail** (400/403/404/409/500).
- Tests **JUnit 5 / Mockito / AssertJ** : 16 tests verts.

---

## 4. Modèle de données

- `Conducteur` 1—* `Vehicule`, 1—* `Trajet`
- `Trajet` 1—* `PointArret` (composition, triés par `ordre`)
- `Trajet` 1—* `Reservation`, `Passager` 1—* `Reservation`
- `Reservation` référence 2 `PointArret` : montée et descente
- Enums : `StatutTrajet`, `StatutReservation`
- ≥ 5 enregistrements par table dans `data.sql`

---

## 5. Règles métier clés

- 2 points minimum, heures et prix strictement croissants, départ futur.
- `BigDecimal` 2 décimales ; places restantes calculées, jamais stockées.
- PUBLIE → COMPLET à la dernière place, COMPLET → PUBLIE lors d'une annulation.
- Une seule réservation `CONFIRMEE` par passager et par trajet.
- Annulation passager jusqu'à **24 h** avant le départ.
- Annulation d'un trajet : trajet + réservations dans la **même transaction**.

---

## 6. Démo API

1. `POST /api/trajets` — publier (Rabat → Settat → Marrakech).
2. `GET /api/trajets/recherche?villeDepart=Settat&villeArrivee=Marrakech` — recherche paginée.
3. `POST /api/reservations` — réserver un segment, voir le montant calculé.
4. `GET /api/trajets/{id}` — places restantes et statut.
5. `DELETE /api/reservations/{id}?passagerId=…` — annuler.
6. `POST /api/trajets/{id}/annuler?conducteurId=…` — annulation atomique (204).

Collection : `postman_scenarios_11.json` (11 scénarios du brief).

---

## 7. Résultats et livrables

- 11 scénarios de validation vérifiés en HTTP.
- Recherche en **une seule requête JPQL**, détail sans N+1 (`JOIN FETCH`).
- Diagramme UML, backlog JIRA, README, collection Postman, lien GitHub.
- PostgreSQL portable : Docker indisponible ici (virtualisation désactivée), `docker-compose.yml` fourni.
