-- ============================================================
-- CineMax - Laboratorio Interdisciplinare B - a.a. 2025/2026
-- Script di creazione del database dbCM
-- Eseguire su database: dbcm
-- ============================================================

-- Estensione per la cifratura delle password (BCrypt)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ============================================================
-- TABELLA: film
-- Contiene i dati anagrafici dei film, separati dalle proiezioni
-- per garantire la terza forma normale (gli attributi del film
-- dipendono dal film, non dalla singola proiezione)
-- ============================================================
CREATE TABLE film (
    id              SERIAL PRIMARY KEY,
    titolo          VARCHAR(200) NOT NULL,
    genere          VARCHAR(50)  NOT NULL,
    regista         VARCHAR(100) NOT NULL,
    anno            INTEGER      NOT NULL CHECK (anno >= 1888),
    durata_minuti   INTEGER      NOT NULL CHECK (durata_minuti > 0),
    eta_minima      INTEGER      NOT NULL CHECK (eta_minima >= 0),
    UNIQUE (titolo, anno, regista)
);

-- ============================================================
-- TABELLA: proiezione
-- Il prezzo appartiene alla proiezione (varia tra proiezioni
-- dello stesso film, come confermato dai dati forniti)
-- ============================================================
CREATE TABLE proiezione (
    id               SERIAL PRIMARY KEY,
    film_id          INTEGER NOT NULL REFERENCES film(id) ON DELETE RESTRICT,
    data_ora         TIMESTAMP NOT NULL UNIQUE,
    prezzo_biglietto NUMERIC(5,2) NOT NULL CHECK (prezzo_biglietto >= 0)
);

-- Indice per le ricerche per data (cercaProiezione per intervallo di date)
CREATE INDEX idx_proiezione_data ON proiezione(data_ora);

-- ============================================================
-- TABELLA: utenti
-- Password cifrata con BCrypt (pgcrypto).
-- Data di nascita facoltativa come da specifiche.
-- ============================================================
CREATE TABLE utenti (
    username         VARCHAR(50)  PRIMARY KEY,
    nome             VARCHAR(100) NOT NULL,
    cognome          VARCHAR(100) NOT NULL,
    password_cifrata VARCHAR(100) NOT NULL,
    data_nascita     DATE,
    domicilio        VARCHAR(100) NOT NULL,
    ruolo            VARCHAR(20)  NOT NULL
                     CHECK (ruolo IN ('cliente', 'proiezionista', 'bigliettaio'))
);

-- ============================================================
-- TABELLA: prenotazione
-- Il codice univoco viene generato dall'applicazione al momento
-- della creazione (requisito creaPrenotazione).
-- ============================================================
CREATE TABLE prenotazione (
    codice        VARCHAR(20) PRIMARY KEY,
    username      VARCHAR(50) NOT NULL REFERENCES utenti(username) ON DELETE CASCADE,
    proiezione_id INTEGER     NOT NULL REFERENCES proiezione(id) ON DELETE RESTRICT,
    num_posti     INTEGER     NOT NULL CHECK (num_posti > 0)
);

-- Indice per il conteggio dei posti occupati per proiezione
CREATE INDEX idx_prenotazione_proiezione ON prenotazione(proiezione_id);

-- ============================================================
-- UTENTI PREDEFINITI: 2 proiezionisti e 5 bigliettai
-- (requisito delle specifiche, slide 6)
-- Password di default = username seguito da "123"
-- ============================================================
INSERT INTO utenti (username, nome, cognome, password_cifrata, data_nascita, domicilio, ruolo) VALUES
('proiez1', 'Mario',    'Rossi',    crypt('proiez1123', gen_salt('bf')), '1980-03-15', 'Varese',  'proiezionista'),
('proiez2', 'Laura',    'Bianchi',  crypt('proiez2123', gen_salt('bf')), '1975-07-22', 'Como',    'proiezionista'),
('bigl1',   'Giuseppe', 'Verdi',    crypt('bigl1123',   gen_salt('bf')), '1990-01-10', 'Varese',  'bigliettaio'),
('bigl2',   'Anna',     'Neri',     crypt('bigl2123',   gen_salt('bf')), '1988-11-05', 'Milano',  'bigliettaio'),
('bigl3',   'Paolo',    'Gialli',   crypt('bigl3123',   gen_salt('bf')), '1995-06-30', 'Varese',  'bigliettaio'),
('bigl4',   'Sara',     'Blu',      crypt('bigl4123',   gen_salt('bf')), '1992-09-18', 'Como',    'bigliettaio'),
('bigl5',   'Luca',     'Viola',    crypt('bigl5123',   gen_salt('bf')), NULL,         'Varese',  'bigliettaio');

-- ============================================================
-- IMPORT DEL FILE proiezioni.csv
-- Si usa una tabella di staging temporanea, poi si separano i
-- dati in film e proiezione (normalizzazione).
--
-- NOTA: il comando \copy va eseguito da psql (client-side).
-- Sostituire il percorso con quello reale del file sul proprio computer.
-- ============================================================
CREATE TEMP TABLE staging (
    data_ora_proiezione TIMESTAMP,
    titolo_film         VARCHAR(200),
    genere              VARCHAR(50),
    regista             VARCHAR(100),
    anno                INTEGER,
    durata_minuti       INTEGER,
    eta_minima          INTEGER,
    prezzo_biglietto    NUMERIC(5,2)
);

\copy staging FROM 'proiezioni.csv' WITH (FORMAT csv, HEADER true)

-- Popolamento tabella film (un record per film distinto)
INSERT INTO film (titolo, genere, regista, anno, durata_minuti, eta_minima)
SELECT DISTINCT titolo_film, genere, regista, anno, durata_minuti, eta_minima
FROM staging;

-- Popolamento tabella proiezione (con riferimento al film)
INSERT INTO proiezione (film_id, data_ora, prezzo_biglietto)
SELECT f.id, s.data_ora_proiezione, s.prezzo_biglietto
FROM staging s
JOIN film f ON f.titolo = s.titolo_film
           AND f.anno = s.anno
           AND f.regista = s.regista;

DROP TABLE staging;

-- ============================================================
-- VERIFICA FINALE
-- Attesi: 725 film, 8878 proiezioni, 7 utenti
-- ============================================================
SELECT 'film' AS tabella, COUNT(*) FROM film
UNION ALL
SELECT 'proiezione', COUNT(*) FROM proiezione
UNION ALL
SELECT 'utenti', COUNT(*) FROM utenti;
