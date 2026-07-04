/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.server.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cinemax.common.Prenotazione;
import cinemax.common.Proiezione;
import cinemax.common.Richiesta;
import cinemax.common.Risposta;

/**
 * Servizio per le operazioni sulle prenotazioni: creazione, visualizzazione,
 * modifica, eliminazione e ricerca (per clienti e bigliettai).
 *
 * Le operazioni che verificano la disponibilita' dei posti (creazione e
 * modifica) sono protette da una transazione con SELECT FOR UPDATE sulla
 * proiezione interessata: in questo modo gli accessi concorrenti di piu'
 * client vengono serializzati dal database e non e' possibile superare
 * la capienza della sala (overbooking).
 */
public class PrenotazioneService {

    private final Connection conn;

    /**
     * Costruttore.
     *
     * @param conn connessione JDBC dedicata alla sessione corrente
     */
    public PrenotazioneService(Connection conn) {
        this.conn = conn;
    }

    /**
     * Crea una nuova prenotazione per un cliente.
     * Usa una transazione con SELECT FOR UPDATE sulla proiezione
     * per serializzare gli accessi concorrenti ed evitare overbooking.
     *
     * @param richiesta parametri: username, idProiezione (int), numPosti (int)
     * @return risposta con il codice univoco della prenotazione in caso di successo
     */
    public Risposta creaPrenotazione(Richiesta richiesta) {
        String username = (String) richiesta.getParametro("username");
        Object idObj    = richiesta.getParametro("idProiezione");
        Object postiObj = richiesta.getParametro("numPosti");

        if (username == null || idObj == null || postiObj == null)
            return new Risposta(false, "Parametri mancanti", null);

        int idProiezione = (int) idObj;
        int numPosti     = (int) postiObj;

        try {
            conn.setAutoCommit(false);

            // Blocca la riga della proiezione per serializzare l'accesso concorrente:
            // un secondo client che tenta di prenotare la stessa proiezione rimane
            // in attesa finche' questa transazione non termina.
            PreparedStatement lock = conn.prepareStatement(
                    "SELECT id FROM proiezione WHERE id = ? FOR UPDATE");
            lock.setInt(1, idProiezione);
            ResultSet rsLock = lock.executeQuery();
            if (!rsLock.next()) {
                conn.rollback(); conn.setAutoCommit(true);
                rsLock.close(); lock.close();
                return new Risposta(false, "Proiezione non trovata", null);
            }
            rsLock.close(); lock.close();

            // Verifica posti disponibili (capienza - posti gia' prenotati)
            PreparedStatement postiSt = conn.prepareStatement(
                    "SELECT (" + Proiezione.CAPIENZA_SALA + " - COALESCE(SUM(num_posti), 0)) AS liberi "
                            + "FROM prenotazione WHERE proiezione_id = ?");
            postiSt.setInt(1, idProiezione);
            ResultSet rsPosti = postiSt.executeQuery();
            rsPosti.next();
            int liberi = rsPosti.getInt("liberi");
            rsPosti.close(); postiSt.close();

            if (numPosti > liberi) {
                conn.rollback(); conn.setAutoCommit(true);
                return new Risposta(false,
                        "Posti insufficienti: disponibili " + liberi + ", richiesti " + numPosti, null);
            }

            // Genera codice univoco (8 caratteri uppercase)
            String codice = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

            PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO prenotazione (codice, username, proiezione_id, num_posti) "
                            + "VALUES (?, ?, ?, ?)");
            ins.setString(1, codice);
            ins.setString(2, username);
            ins.setInt(3, idProiezione);
            ins.setInt(4, numPosti);
            ins.executeUpdate();
            ins.close();

            conn.commit(); conn.setAutoCommit(true);
            return new Risposta(true, "Prenotazione creata", codice);
        } catch (Exception e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (Exception ex) {}
            return new Risposta(false, "Errore durante la prenotazione: " + e.getMessage(), null);
        }
    }

    /**
     * Restituisce le prenotazioni future del cliente specificato.
     *
     * @param richiesta parametro: username
     * @return lista di {@link Prenotazione} future ordinate per data
     */
    public Risposta visualizzaPrenotazioni(Richiesta richiesta) {
        String username = (String) richiesta.getParametro("username");
        if (username == null)
            return new Risposta(false, "Username mancante", null);

        String sql = DbHelper.SQL_PRENOTAZIONE
                + "WHERE pren.username = ? AND p.data_ora > NOW() ORDER BY p.data_ora";
        try {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            List<Prenotazione> lista = new ArrayList<>();
            while (rs.next())
                lista.add(DbHelper.prenotazioneFromRs(rs));
            rs.close(); st.close();
            return new Risposta(true, lista.size() + " prenotazioni trovate", lista);
        } catch (Exception e) {
            return new Risposta(false, "Errore caricamento prenotazioni: " + e.getMessage(), null);
        }
    }

    /**
     * Modifica la data di una prenotazione cercando una proiezione dello
     * stesso film nella nuova data.
     *
     * Come da specifica, la modifica e' consentita solo se sia la data
     * attuale della proiezione che la nuova data sono successive a oggi.
     *
     * Come per la creazione, l'operazione avviene in una transazione con
     * SELECT FOR UPDATE sulla proiezione di destinazione: senza il lock,
     * due modifiche concorrenti sulla stessa proiezione potrebbero
     * entrambe superare il controllo dei posti e causare overbooking.
     * Il conteggio dei posti esclude la prenotazione che si sta
     * modificando, per gestire correttamente il caso in cui la nuova
     * proiezione coincida con quella attuale.
     *
     * @param richiesta parametri: codice (String), nuovaData (LocalDate)
     * @return risposta di conferma o messaggio di errore
     */
    public Risposta modificaPrenotazione(Richiesta richiesta) {
        String codice       = (String) richiesta.getParametro("codice");
        LocalDate nuovaData = (LocalDate) richiesta.getParametro("nuovaData");

        if (codice == null || nuovaData == null)
            return new Risposta(false, "Parametri mancanti", null);
        if (!nuovaData.isAfter(LocalDate.now()))
            return new Risposta(false, "La nuova data deve essere successiva a oggi", null);

        try {
            conn.setAutoCommit(false);

            // Recupera i dati della prenotazione da modificare
            PreparedStatement chk = conn.prepareStatement(
                    "SELECT p.data_ora, p.film_id, pren.num_posti "
                            + "FROM prenotazione pren JOIN proiezione p ON pren.proiezione_id = p.id "
                            + "WHERE pren.codice = ?");
            chk.setString(1, codice);
            ResultSet rsChk = chk.executeQuery();
            if (!rsChk.next()) {
                rsChk.close(); chk.close();
                conn.rollback(); conn.setAutoCommit(true);
                return new Risposta(false, "Prenotazione non trovata", null);
            }
            LocalDateTime dataAttuale = rsChk.getTimestamp("data_ora").toLocalDateTime();
            int filmId   = rsChk.getInt("film_id");
            int numPosti = rsChk.getInt("num_posti");
            rsChk.close(); chk.close();

            // Vincolo di specifica: anche la data attuale deve essere futura
            if (!dataAttuale.toLocalDate().isAfter(LocalDate.now())) {
                conn.rollback(); conn.setAutoCommit(true);
                return new Risposta(false, "La data corrente della proiezione e' gia' passata", null);
            }

            // Trova una proiezione dello stesso film nella nuova data
            // e la blocca (FOR UPDATE) per serializzare il controllo posti
            PreparedStatement trova = conn.prepareStatement(
                    "SELECT id FROM proiezione WHERE film_id = ? AND data_ora::date = ? FOR UPDATE");
            trova.setInt(1, filmId);
            trova.setDate(2, java.sql.Date.valueOf(nuovaData));
            ResultSet rsTrova = trova.executeQuery();
            if (!rsTrova.next()) {
                rsTrova.close(); trova.close();
                conn.rollback(); conn.setAutoCommit(true);
                return new Risposta(false,
                        "Nessuna proiezione dello stesso film trovata per la data " + nuovaData, null);
            }
            int nuovaProiezioneId = rsTrova.getInt("id");
            rsTrova.close(); trova.close();

            // Verifica posti liberi nella nuova proiezione, escludendo la
            // prenotazione che si sta modificando (i suoi posti verranno
            // spostati, quindi non vanno contati come occupati)
            PreparedStatement postiSt = conn.prepareStatement(
                    "SELECT (" + Proiezione.CAPIENZA_SALA + " - COALESCE(SUM(num_posti), 0)) AS liberi "
                            + "FROM prenotazione WHERE proiezione_id = ? AND codice != ?");
            postiSt.setInt(1, nuovaProiezioneId);
            postiSt.setString(2, codice);
            ResultSet rsPosti = postiSt.executeQuery();
            rsPosti.next();
            int liberi = rsPosti.getInt("liberi");
            rsPosti.close(); postiSt.close();

            if (numPosti > liberi) {
                conn.rollback(); conn.setAutoCommit(true);
                return new Risposta(false,
                        "Posti insufficienti nella nuova proiezione: disponibili " + liberi, null);
            }

            PreparedStatement upd = conn.prepareStatement(
                    "UPDATE prenotazione SET proiezione_id = ? WHERE codice = ?");
            upd.setInt(1, nuovaProiezioneId);
            upd.setString(2, codice);
            upd.executeUpdate();
            upd.close();

            conn.commit(); conn.setAutoCommit(true);
            return new Risposta(true, "Prenotazione modificata con successo", null);
        } catch (Exception e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (Exception ex) {}
            return new Risposta(false, "Errore modifica prenotazione: " + e.getMessage(), null);
        }
    }

    /**
     * Elimina una prenotazione. Consentita solo se la proiezione e' futura.
     *
     * @param richiesta parametro: codice (String)
     * @return risposta di conferma o messaggio di errore
     */
    public Risposta eliminaPrenotazione(Richiesta richiesta) {
        String codice = (String) richiesta.getParametro("codice");
        if (codice == null)
            return new Risposta(false, "Codice mancante", null);

        try {
            PreparedStatement chk = conn.prepareStatement(
                    "SELECT p.data_ora FROM prenotazione pren "
                            + "JOIN proiezione p ON pren.proiezione_id = p.id WHERE pren.codice = ?");
            chk.setString(1, codice);
            ResultSet rs = chk.executeQuery();
            if (!rs.next()) {
                rs.close(); chk.close();
                return new Risposta(false, "Prenotazione non trovata", null);
            }
            LocalDateTime dataOra = rs.getTimestamp("data_ora").toLocalDateTime();
            rs.close(); chk.close();

            if (!dataOra.toLocalDate().isAfter(LocalDate.now()))
                return new Risposta(false,
                        "Non e' possibile cancellare una prenotazione per una proiezione passata", null);

            PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM prenotazione WHERE codice = ?");
            del.setString(1, codice);
            del.executeUpdate();
            del.close();
            return new Risposta(true, "Prenotazione cancellata", null);
        } catch (Exception e) {
            return new Risposta(false, "Errore cancellazione: " + e.getMessage(), null);
        }
    }

    /**
     * Ricerca prenotazioni con filtri opzionali (uso bigliettai).
     *
     * @param richiesta parametri opzionali: codice, nome, cognome,
     *                  titolo, dataFrom, dataTo
     * @return lista di {@link Prenotazione} ordinate per data
     */
    public Risposta cercaPrenotazione(Richiesta richiesta) {
        String codice   = (String) richiesta.getParametro("codice");
        String nome     = (String) richiesta.getParametro("nome");
        String cognome  = (String) richiesta.getParametro("cognome");
        String titolo   = (String) richiesta.getParametro("titolo");
        LocalDate daData = (LocalDate) richiesta.getParametro("dataFrom");
        LocalDate aData  = (LocalDate) richiesta.getParametro("dataTo");

        StringBuilder sql = new StringBuilder(DbHelper.SQL_PRENOTAZIONE).append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (codice != null && !codice.isEmpty()) {
            sql.append("AND pren.codice = ? ");
            params.add(codice);
        }
        if (nome != null && !nome.isEmpty()) {
            sql.append("AND u.nome ILIKE ? ");
            params.add("%" + nome + "%");
        }
        if (cognome != null && !cognome.isEmpty()) {
            sql.append("AND u.cognome ILIKE ? ");
            params.add("%" + cognome + "%");
        }
        if (titolo != null && !titolo.isEmpty()) {
            sql.append("AND f.titolo ILIKE ? ");
            params.add("%" + titolo + "%");
        }
        if (daData != null) {
            sql.append("AND p.data_ora >= ? ");
            params.add(Timestamp.valueOf(daData.atStartOfDay()));
        }
        if (aData != null) {
            sql.append("AND p.data_ora < ? ");
            params.add(Timestamp.valueOf(aData.plusDays(1).atStartOfDay()));
        }
        sql.append("ORDER BY p.data_ora");

        try {
            PreparedStatement st = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++)
                st.setObject(i + 1, params.get(i));
            ResultSet rs = st.executeQuery();
            List<Prenotazione> lista = new ArrayList<>();
            while (rs.next())
                lista.add(DbHelper.prenotazioneFromRs(rs));
            rs.close(); st.close();
            return new Risposta(true, lista.size() + " prenotazioni trovate", lista);
        } catch (Exception e) {
            return new Risposta(false, "Errore ricerca prenotazioni: " + e.getMessage(), null);
        }
    }

    /**
     * Restituisce tutte le prenotazioni relative alla data odierna.
     *
     * @return lista di {@link Prenotazione} di oggi ordinate per orario
     */
    public Risposta prenotazioniOggi() {
        String sql = DbHelper.SQL_PRENOTAZIONE
                + "WHERE p.data_ora::date = CURRENT_DATE ORDER BY p.data_ora";
        try {
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            List<Prenotazione> lista = new ArrayList<>();
            while (rs.next())
                lista.add(DbHelper.prenotazioneFromRs(rs));
            rs.close(); st.close();
            return new Risposta(true, lista.size() + " prenotazioni oggi", lista);
        } catch (Exception e) {
            return new Risposta(false, "Errore: " + e.getMessage(), null);
        }
    }
}