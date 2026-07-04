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

import cinemax.common.Proiezione;
import cinemax.common.Richiesta;
import cinemax.common.Risposta;

/**
 * Servizio per le operazioni sulle proiezioni:
 * ricerca, visualizzazione, aggiunta, modifica, eliminazione e palinsesto.
 */
public class ProiezioneService {

    private final Connection conn;

    /**
     * Costruttore.
     *
     * @param conn connessione JDBC dedicata alla sessione corrente
     */
    public ProiezioneService(Connection conn) {
        this.conn = conn;
    }

    /**
     * Ricerca proiezioni applicando filtri opzionali.
     * Tutti i parametri sono opzionali; se nessuno è specificato vengono
     * restituite tutte le proiezioni ordinate per data.
     *
     * @param richiesta parametri opzionali: titolo, genere, dataFrom,
     *                  dataTo, prezzoMin, prezzoMax
     * @return lista di {@link Proiezione} corrispondenti ai criteri
     */
    public Risposta cercaProiezione(Richiesta richiesta) {
        String titolo    = (String) richiesta.getParametro("titolo");
        String genere    = (String) richiesta.getParametro("genere");
        LocalDate daData = (LocalDate) richiesta.getParametro("dataFrom");
        LocalDate aData  = (LocalDate) richiesta.getParametro("dataTo");
        Double prezzoMin = (Double) richiesta.getParametro("prezzoMin");
        Double prezzoMax = (Double) richiesta.getParametro("prezzoMax");

        StringBuilder sql = new StringBuilder(DbHelper.SQL_PROIEZIONE).append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (titolo != null && !titolo.isEmpty()) {
            sql.append("AND f.titolo ILIKE ? ");
            params.add("%" + titolo + "%");
        }
        if (genere != null && !genere.isEmpty()) {
            sql.append("AND f.genere ILIKE ? ");
            params.add("%" + genere + "%");
        }
        if (daData != null) {
            sql.append("AND p.data_ora >= ? ");
            params.add(Timestamp.valueOf(daData.atStartOfDay()));
        }
        if (aData != null) {
            sql.append("AND p.data_ora < ? ");
            params.add(Timestamp.valueOf(aData.plusDays(1).atStartOfDay()));
        }
        if (prezzoMin != null) {
            sql.append("AND p.prezzo_biglietto >= ? ");
            params.add(prezzoMin);
        }
        if (prezzoMax != null) {
            sql.append("AND p.prezzo_biglietto <= ? ");
            params.add(prezzoMax);
        }
        sql.append("ORDER BY p.data_ora");

        try {
            PreparedStatement st = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++)
                st.setObject(i + 1, params.get(i));
            ResultSet rs = st.executeQuery();
            List<Proiezione> lista = new ArrayList<>();
            while (rs.next())
                lista.add(DbHelper.proiezioneFromRs(rs));
            rs.close(); st.close();
            return new Risposta(true, lista.size() + " proiezioni trovate", lista);
        } catch (Exception e) {
            return new Risposta(false, "Errore ricerca proiezioni: " + e.getMessage(), null);
        }
    }

    /**
     * Restituisce i dettagli di una singola proiezione con il conteggio
     * aggiornato dei posti liberi.
     *
     * @param richiesta parametro: idProiezione (int)
     * @return la {@link Proiezione} richiesta, oppure errore se non trovata
     */
    public Risposta visualizzaProiezione(Richiesta richiesta) {
        Object idObj = richiesta.getParametro("idProiezione");
        if (idObj == null)
            return new Risposta(false, "ID proiezione mancante", null);
        int id = (int) idObj;
        String sql = DbHelper.SQL_PROIEZIONE + "WHERE p.id = ?";
        try {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Proiezione p = DbHelper.proiezioneFromRs(rs);
                rs.close(); st.close();
                return new Risposta(true, "Proiezione trovata", p);
            }
            rs.close(); st.close();
            return new Risposta(false, "Proiezione non trovata", null);
        } catch (Exception e) {
            return new Risposta(false, "Errore: " + e.getMessage(), null);
        }
    }

    /**
     * Aggiunge una nuova proiezione al palinsesto.
     * Se il film non esiste nel database viene creato automaticamente.
     * Verifica che la nuova proiezione non si sovrapponga temporalmente
     * ad altre (tenendo conto della durata del film).
     *
     * @param richiesta parametri: titolo, genere, regista, anno, durataMinuti,
     *                  etaMinima, dataOra, prezzoBiglietto
     * @return risposta di conferma o messaggio di errore
     */
    public Risposta aggiungiProiezione(Richiesta richiesta) {
        String titolo       = (String) richiesta.getParametro("titolo");
        String genere       = (String) richiesta.getParametro("genere");
        String regista      = (String) richiesta.getParametro("regista");
        Object annoObj      = richiesta.getParametro("anno");
        Object durataObj    = richiesta.getParametro("durataMinuti");
        Object etaObj       = richiesta.getParametro("etaMinima");
        LocalDateTime dataOra = (LocalDateTime) richiesta.getParametro("dataOra");
        Object prezzoObj    = richiesta.getParametro("prezzoBiglietto");

        if (titolo == null || genere == null || regista == null
                || annoObj == null || durataObj == null || etaObj == null
                || dataOra == null || prezzoObj == null)
            return new Risposta(false, "Parametri obbligatori mancanti", null);

        int anno   = (int) annoObj;
        int durata = (int) durataObj;
        int eta    = (int) etaObj;
        double prezzo = (double) prezzoObj;

        try {
            conn.setAutoCommit(false);

            // Trova o crea il film (chiave naturale: titolo + anno + regista)
            PreparedStatement filmSt = conn.prepareStatement(
                    "SELECT id, durata_minuti FROM film WHERE titolo = ? AND anno = ? AND regista = ?");
            filmSt.setString(1, titolo);
            filmSt.setInt(2, anno);
            filmSt.setString(3, regista);
            ResultSet rsFilm = filmSt.executeQuery();
            int filmId;
            if (rsFilm.next()) {
                filmId = rsFilm.getInt("id");
                durata = rsFilm.getInt("durata_minuti");
                rsFilm.close(); filmSt.close();
            } else {
                rsFilm.close(); filmSt.close();
                PreparedStatement insFilm = conn.prepareStatement(
                        "INSERT INTO film (titolo, genere, regista, anno, durata_minuti, eta_minima) "
                                + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id");
                insFilm.setString(1, titolo);
                insFilm.setString(2, genere);
                insFilm.setString(3, regista);
                insFilm.setInt(4, anno);
                insFilm.setInt(5, durata);
                insFilm.setInt(6, eta);
                ResultSet rsId = insFilm.executeQuery();
                rsId.next();
                filmId = rsId.getInt(1);
                rsId.close(); insFilm.close();
            }

            // Verifica sovrapposizione temporale con proiezioni esistenti
            PreparedStatement ovlp = conn.prepareStatement(
                    "SELECT COUNT(*) FROM proiezione p JOIN film f ON p.film_id = f.id "
                            + "WHERE p.data_ora < ? + (? * interval '1 minute') "
                            + "AND p.data_ora + (f.durata_minuti * interval '1 minute') > ?");
            ovlp.setTimestamp(1, Timestamp.valueOf(dataOra));
            ovlp.setInt(2, durata);
            ovlp.setTimestamp(3, Timestamp.valueOf(dataOra));
            ResultSet rsOvlp = ovlp.executeQuery();
            rsOvlp.next();
            int overlap = rsOvlp.getInt(1);
            rsOvlp.close(); ovlp.close();

            if (overlap > 0) {
                conn.rollback(); conn.setAutoCommit(true);
                return new Risposta(false,
                        "La proiezione si sovrappone a un'altra già in programma", null);
            }

            PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO proiezione (film_id, data_ora, prezzo_biglietto) VALUES (?, ?, ?)");
            ins.setInt(1, filmId);
            ins.setTimestamp(2, Timestamp.valueOf(dataOra));
            ins.setDouble(3, prezzo);
            ins.executeUpdate();
            ins.close();

            conn.commit(); conn.setAutoCommit(true);
            return new Risposta(true, "Proiezione aggiunta con successo", null);
        } catch (Exception e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (Exception ex) {}
            return new Risposta(false, "Errore aggiunta proiezione: " + e.getMessage(), null);
        }
    }

    /**
     * Modifica la data/ora di una proiezione esistente.
     * La modifica è consentita solo se non esistono prenotazioni per quella
     * proiezione e la nuova data/ora non si sovrappone ad altre.
     *
     * @param richiesta parametri: idProiezione (int), nuovaDataOra (LocalDateTime)
     * @return risposta di conferma o messaggio di errore
     */
    public Risposta modificaProiezione(Richiesta richiesta) {
        Object idObj = richiesta.getParametro("idProiezione");
        LocalDateTime nuovaDataOra = (LocalDateTime) richiesta.getParametro("nuovaDataOra");
        if (idObj == null || nuovaDataOra == null)
            return new Risposta(false, "Parametri mancanti", null);
        int id = (int) idObj;

        try {
            // Verifica assenza di prenotazioni
            PreparedStatement chk = conn.prepareStatement(
                    "SELECT COUNT(*) FROM prenotazione WHERE proiezione_id = ?");
            chk.setInt(1, id);
            ResultSet rs = chk.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                rs.close(); chk.close();
                return new Risposta(false,
                        "Impossibile modificare: esistono prenotazioni per questa proiezione", null);
            }
            rs.close(); chk.close();

            // Recupera durata del film per il controllo sovrapposizione
            PreparedStatement durSt = conn.prepareStatement(
                    "SELECT f.durata_minuti FROM proiezione p JOIN film f ON p.film_id = f.id WHERE p.id = ?");
            durSt.setInt(1, id);
            ResultSet rsDur = durSt.executeQuery();
            if (!rsDur.next()) {
                rsDur.close(); durSt.close();
                return new Risposta(false, "Proiezione non trovata", null);
            }
            int durata = rsDur.getInt("durata_minuti");
            rsDur.close(); durSt.close();

            // Verifica sovrapposizione (esclude la proiezione stessa)
            PreparedStatement ovlp = conn.prepareStatement(
                    "SELECT COUNT(*) FROM proiezione p JOIN film f ON p.film_id = f.id "
                            + "WHERE p.id != ? "
                            + "AND p.data_ora < ? + (? * interval '1 minute') "
                            + "AND p.data_ora + (f.durata_minuti * interval '1 minute') > ?");
            ovlp.setInt(1, id);
            ovlp.setTimestamp(2, Timestamp.valueOf(nuovaDataOra));
            ovlp.setInt(3, durata);
            ovlp.setTimestamp(4, Timestamp.valueOf(nuovaDataOra));
            ResultSet rsOvlp = ovlp.executeQuery();
            rsOvlp.next();
            if (rsOvlp.getInt(1) > 0) {
                rsOvlp.close(); ovlp.close();
                return new Risposta(false,
                        "La nuova data/ora si sovrappone a un'altra proiezione", null);
            }
            rsOvlp.close(); ovlp.close();

            PreparedStatement upd = conn.prepareStatement(
                    "UPDATE proiezione SET data_ora = ? WHERE id = ?");
            upd.setTimestamp(1, Timestamp.valueOf(nuovaDataOra));
            upd.setInt(2, id);
            upd.executeUpdate();
            upd.close();
            return new Risposta(true, "Proiezione modificata", null);
        } catch (Exception e) {
            return new Risposta(false, "Errore modifica proiezione: " + e.getMessage(), null);
        }
    }

    /**
     * Elimina una proiezione dal palinsesto.
     * L'eliminazione è consentita solo se non esistono prenotazioni.
     *
     * @param richiesta parametro: idProiezione (int)
     * @return risposta di conferma o messaggio di errore
     */
    public Risposta eliminaProiezione(Richiesta richiesta) {
        Object idObj = richiesta.getParametro("idProiezione");
        if (idObj == null)
            return new Risposta(false, "ID proiezione mancante", null);
        int id = (int) idObj;

        try {
            PreparedStatement chk = conn.prepareStatement(
                    "SELECT COUNT(*) FROM prenotazione WHERE proiezione_id = ?");
            chk.setInt(1, id);
            ResultSet rs = chk.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                rs.close(); chk.close();
                return new Risposta(false,
                        "Impossibile eliminare: esistono prenotazioni per questa proiezione", null);
            }
            rs.close(); chk.close();

            PreparedStatement del = conn.prepareStatement("DELETE FROM proiezione WHERE id = ?");
            del.setInt(1, id);
            del.executeUpdate();
            del.close();
            return new Risposta(true, "Proiezione eliminata", null);
        } catch (Exception e) {
            return new Risposta(false, "Errore eliminazione proiezione: " + e.getMessage(), null);
        }
    }

    /**
     * Restituisce il palinsesto (proiezioni future o storiche).
     *
     * @param richiesta parametro: tipo ("future" | "storiche")
     * @return lista di {@link Proiezione} ordinate per data
     */
    public Risposta visualizzaPalinsesto(Richiesta richiesta) {
        String tipo = (String) richiesta.getParametro("tipo");
        boolean future = !"storiche".equals(tipo);
        String condizione = future ? "p.data_ora > NOW() " : "p.data_ora <= NOW() ";
        String ordine     = future ? "ASC" : "DESC";
        String sql = DbHelper.SQL_PROIEZIONE + "WHERE " + condizione + "ORDER BY p.data_ora " + ordine;

        try {
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            List<Proiezione> lista = new ArrayList<>();
            while (rs.next())
                lista.add(DbHelper.proiezioneFromRs(rs));
            rs.close(); st.close();
            return new Risposta(true, lista.size() + " proiezioni", lista);
        } catch (Exception e) {
            return new Risposta(false, "Errore palinsesto: " + e.getMessage(), null);
        }
    }
}