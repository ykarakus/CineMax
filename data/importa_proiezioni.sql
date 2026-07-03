-- CineMax - import proiezioni
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

\copy staging FROM 'proiezioni_fix.csv' WITH (FORMAT csv, HEADER true)

INSERT INTO film (titolo, genere, regista, anno, durata_minuti, eta_minima)
SELECT DISTINCT titolo_film, genere, regista, anno, durata_minuti, eta_minima
FROM staging;

INSERT INTO proiezione (film_id, data_ora, prezzo_biglietto)
SELECT f.id, s.data_ora_proiezione, s.prezzo_biglietto
FROM staging s
JOIN film f ON f.titolo = s.titolo_film
           AND f.anno = s.anno
           AND f.regista = s.regista;

DROP TABLE staging;

SELECT 'film' AS tabella, COUNT(*) FROM film
UNION ALL
SELECT 'proiezione', COUNT(*) FROM proiezione
UNION ALL
SELECT 'utenti', COUNT(*) FROM utenti;
