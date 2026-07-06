/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

-- ============================================================
-- CineMax Lab B - Migrazione dati di prova dal Lab A
-- Inserisce i 2 clienti di test e le 3 prenotazioni di esempio.
-- Da eseguire su dbcm DOPO crea_database.sql
--
-- Password (cifrate con BCrypt, stessi valori del Lab A):
--   test -> "test"
--   mch  -> "1234"
-- ============================================================

-- Clienti di prova
INSERT INTO utenti (username, nome, cognome, password_cifrata, data_nascita, domicilio, ruolo) VALUES
('test', 'test', 'test',     crypt('test', gen_salt('bf')), NULL, 'test',   'cliente'),
('mch',  'maha', 'choudhrz', crypt('1234', gen_salt('bf')), NULL, 'Varese', 'cliente');

-- Prenotazioni di prova: la proiezione viene individuata tramite
-- titolo del film e data/ora, come nel CSV del Lab A
INSERT INTO prenotazione (codice, username, proiezione_id, num_posti)
SELECT 'PR1781277989607', 'test', p.id, 4
FROM proiezione p JOIN film f ON f.id = p.film_id
WHERE f.titolo = 'The Long Goodbye' AND p.data_ora = '2027-11-15 14:00:00';

INSERT INTO prenotazione (codice, username, proiezione_id, num_posti)
SELECT 'PR1781278580132', 'test', p.id, 7
FROM proiezione p JOIN film f ON f.id = p.film_id
WHERE f.titolo = 'The Best Years of Our Lives' AND p.data_ora = '2027-10-30 10:30:00';

INSERT INTO prenotazione (codice, username, proiezione_id, num_posti)
SELECT 'PR1782719264817', 'mch', p.id, 10
FROM proiezione p JOIN film f ON f.id = p.film_id
WHERE f.titolo = 'Vizontele' AND p.data_ora = '2026-07-08 10:30:00';

-- Verifica: attesi 9 utenti e 3 prenotazioni (piu' quelle create nei test)
SELECT 'utenti' AS tabella, COUNT(*) FROM utenti
UNION ALL
SELECT 'prenotazione', COUNT(*) FROM prenotazione;
