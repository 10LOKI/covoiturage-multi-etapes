-- Jeu de donnees de demonstration (au moins 5 lignes par table)
INSERT INTO conducteurs (id, nom, prenom, email, telephone, numero_permis) VALUES
 (1, 'Alami', 'Hamid', 'hamid.alami@example.com', '0611223344', 'PERM-001'),
 (2, 'Benali', 'Karim', 'karim.benali@example.com', '0622334455', 'PERM-002'),
 (3, 'Idrissi', 'Youssef', 'youssef.idrissi@example.com', '0633445566', 'PERM-003'),
 (4, 'Najih', 'Nadia', 'nadia.najih@example.com', '0644556677', 'PERM-004'),
 (5, 'Bennani', 'Omar', 'omar.bennani@example.com', '0655667788', 'PERM-005')
ON CONFLICT (id) DO NOTHING;

INSERT INTO passagers (id, nom, prenom, email, telephone) VALUES
 (1, 'Cherif', 'Salma', 'salma.cherif@example.com', '0661122334'),
 (2, 'Bennani', 'Imane', 'imane.bennani@example.com', '0662233445'),
 (3, 'El Fassi', 'Zineb', 'zineb.elfassi@example.com', '0663344556'),
 (4, 'Tazi', 'Lina', 'lina.tazi@example.com', '0664455667'),
 (5, 'Amrani', 'Sofia', 'sofia.amrani@example.com', '0665566778')
ON CONFLICT (id) DO NOTHING;

INSERT INTO vehicules (id, marque, modele, immatriculation, couleur, capacite, conducteur_id) VALUES
 (1, 'Dacia', 'Logan', '1234-AA-01', 'Blanc', 4, 1),
 (2, 'Renault', 'Clio', '1234-BB-02', 'Rouge', 4, 2),
 (3, 'Peugeot', '308', '1234-CC-03', 'Gris', 6, 3),
 (4, 'Fiat', 'Punto', '1234-DD-04', 'Bleu', 3, 4),
 (5, 'Toyota', 'Yaris', '1234-EE-05', 'Noir', 5, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO trajets (id, conducteur_id, vehicule_id, ville_depart, ville_arrivee, date_depart, places_proposees, statut) VALUES
 (1, 1, 1, 'Rabat', 'Marrakech', '2026-11-25 17:00:00', 3, 'PUBLIE'),
 (2, 2, 2, 'Casablanca', 'Fes', '2026-11-26 08:00:00', 2, 'PUBLIE'),
 (3, 3, 3, 'Tanger', 'Rabat', '2026-11-27 09:30:00', 4, 'PUBLIE'),
 (4, 4, 4, 'Agadir', 'Essaouira', '2026-11-28 10:00:00', 2, 'PUBLIE'),
 (5, 5, 5, 'Meknes', 'Tetouan', '2026-11-29 07:15:00', 3, 'PUBLIE')
ON CONFLICT (id) DO NOTHING;

INSERT INTO points_arret (id, trajet_id, ville, ordre, heure_passage, prix_depuis_depart) VALUES
 (1, 1, 'Rabat', 0, '2026-11-25 17:00:00', 0.00),
 (2, 1, 'Casablanca', 1, '2026-11-25 18:15:00', 40.00),
 (3, 1, 'Settat', 2, '2026-11-25 19:15:00', 70.00),
 (4, 1, 'Marrakech', 3, '2026-11-25 21:30:00', 120.00),
 (5, 2, 'Casablanca', 0, '2026-11-26 08:00:00', 0.00),
 (6, 2, 'Rabat', 1, '2026-11-26 11:00:00', 60.00),
 (7, 2, 'Fes', 2, '2026-11-26 15:00:00', 150.00),
 (8, 3, 'Tanger', 0, '2026-11-27 09:30:00', 0.00),
 (9, 3, 'Rabat', 1, '2026-11-27 14:00:00', 100.00),
 (10, 4, 'Agadir', 0, '2026-11-28 10:00:00', 0.00),
 (11, 4, 'Essaouira', 1, '2026-11-28 14:00:00', 80.00),
 (12, 5, 'Meknes', 0, '2026-11-29 07:15:00', 0.00),
 (13, 5, 'Tetouan', 1, '2026-11-29 13:00:00', 110.00)
ON CONFLICT (id) DO NOTHING;

INSERT INTO reservations (id, passager_id, trajet_id, point_montage_id, point_descente_id, nombre_places, montant_total, date_reservation, statut) VALUES
 (1, 3, 2, 5, 7, 1, 150.00, '2026-11-20 09:00:00', 'CONFIRMEE'),
 (2, 4, 3, 8, 9, 2, 200.00, '2026-11-20 10:00:00', 'CONFIRMEE'),
 (3, 5, 4, 10, 11, 1, 80.00, '2026-11-20 11:00:00', 'CONFIRMEE'),
 (4, 1, 5, 12, 13, 1, 110.00, '2026-11-20 12:00:00', 'CONFIRMEE'),
 (5, 2, 5, 12, 13, 2, 220.00, '2026-11-20 13:00:00', 'ANNULEE')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('conducteurs', 'id'), (SELECT MAX(id) FROM conducteurs));
SELECT setval(pg_get_serial_sequence('passagers', 'id'), (SELECT MAX(id) FROM passagers));
SELECT setval(pg_get_serial_sequence('vehicules', 'id'), (SELECT MAX(id) FROM vehicules));
SELECT setval(pg_get_serial_sequence('trajets', 'id'), (SELECT MAX(id) FROM trajets));
SELECT setval(pg_get_serial_sequence('points_arret', 'id'), (SELECT MAX(id) FROM points_arret));
SELECT setval(pg_get_serial_sequence('reservations', 'id'), (SELECT MAX(id) FROM reservations));
