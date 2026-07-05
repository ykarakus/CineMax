-- CineMax - Vincolo di capienza a livello di database
-- La somma dei posti prenotati per una proiezione non puo'
-- superare la capienza della sala (200 posti).
-- Vincolo inter-riga non esprimibile con CHECK: implementato con trigger.

CREATE OR REPLACE FUNCTION controlla_capienza() RETURNS trigger AS $$
DECLARE
    capienza CONSTANT INTEGER := 200;
    occupati INTEGER;
BEGIN
    SELECT COALESCE(SUM(num_posti), 0) INTO occupati
    FROM prenotazione
    WHERE proiezione_id = NEW.proiezione_id
      AND codice <> NEW.codice;

    IF occupati + NEW.num_posti > capienza THEN
        RAISE EXCEPTION 'Capienza superata per la proiezione %: % posti occupati, % richiesti (max %)',
            NEW.proiezione_id, occupati, NEW.num_posti, capienza;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_capienza ON prenotazione;

CREATE TRIGGER trg_capienza
BEFORE INSERT OR UPDATE ON prenotazione
FOR EACH ROW EXECUTE FUNCTION controlla_capienza();

SELECT tgname FROM pg_trigger WHERE tgname = 'trg_capienza';
