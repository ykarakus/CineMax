/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.server.service;

import java.sql.ResultSet;

import cinemax.common.Film;
import cinemax.common.Prenotazione;
import cinemax.common.Proiezione;

/**
 * Classe di utilita' con le costanti SQL e i metodi di conversione da
 * {@link ResultSet} agli oggetti del dominio, condivisi da tutti i
 * service del server.
 *
 * Il numero di posti liberi di una proiezione viene calcolato
 * direttamente nella query come differenza tra la capienza della sala
 * ({@link Proiezione#CAPIENZA_SALA}) e la somma dei posti gia'
 * prenotati per quella proiezione, come richiesto dalle specifiche.
 */
public final class DbHelper {

    /** Costruttore privato: la classe offre solo membri statici. */
    private DbHelper() {
    }

    /**
     * Frammento SELECT riusabile per le proiezioni.
     * Espone gli alias: pid, fid, titolo, genere, regista, anno,
     * durata_minuti, eta_minima, data_ora, prezzo_biglietto, posti_liberi.
     * La capienza della sala e' presa dalla costante di dominio
     * per evitare valori duplicati nel codice.
     */
    public static final String SQL_PROIEZIONE =
            "SELECT p.id AS pid, f.id AS fid, f.titolo, f.genere, f.regista, "
                    + "f.anno, f.durata_minuti, f.eta_minima, p.data_ora, p.prezzo_biglietto, "
                    + "(" + Proiezione.CAPIENZA_SALA + " - COALESCE((SELECT SUM(pr.num_posti) "
                    + "  FROM prenotazione pr WHERE pr.proiezione_id = p.id), 0)) AS posti_liberi "
                    + "FROM proiezione p JOIN film f ON p.film_id = f.id ";

    /**
     * Frammento SELECT riusabile per le prenotazioni.
     * Espone, oltre alle colonne di {@link #SQL_PROIEZIONE}:
     * codice, username, nome_cliente, cognome_cliente, num_posti.
     */
    public static final String SQL_PRENOTAZIONE =
            "SELECT pren.codice, pren.username, u.nome AS nome_cliente, u.cognome AS cognome_cliente, "
                    + "pren.num_posti, p.id AS pid, f.id AS fid, f.titolo, f.genere, f.regista, "
                    + "f.anno, f.durata_minuti, f.eta_minima, p.data_ora, p.prezzo_biglietto, "
                    + "(" + Proiezione.CAPIENZA_SALA + " - COALESCE((SELECT SUM(pr2.num_posti) "
                    + "  FROM prenotazione pr2 WHERE pr2.proiezione_id = p.id), 0)) AS posti_liberi "
                    + "FROM prenotazione pren "
                    + "JOIN proiezione p ON pren.proiezione_id = p.id "
                    + "JOIN film f ON p.film_id = f.id "
                    + "JOIN utenti u ON pren.username = u.username ";

    /**
     * Costruisce un oggetto {@link Proiezione} dalla riga corrente del ResultSet.
     *
     * @param rs ResultSet posizionato su una riga valida
     * @return proiezione costruita dai dati della riga
     * @throws Exception se si verifica un errore di accesso al ResultSet
     */
    public static Proiezione proiezioneFromRs(ResultSet rs) throws Exception {
        Film film = new Film(
                rs.getInt("fid"),
                rs.getString("titolo"),
                rs.getString("genere"),
                rs.getString("regista"),
                rs.getInt("anno"),
                rs.getInt("durata_minuti"),
                rs.getInt("eta_minima"));
        return new Proiezione(
                rs.getInt("pid"),
                film,
                rs.getTimestamp("data_ora").toLocalDateTime(),
                rs.getDouble("prezzo_biglietto"),
                rs.getInt("posti_liberi"));
    }

    /**
     * Costruisce un oggetto {@link Prenotazione} dalla riga corrente del ResultSet.
     *
     * @param rs ResultSet posizionato su una riga valida
     * @return prenotazione costruita dai dati della riga
     * @throws Exception se si verifica un errore di accesso al ResultSet
     */
    public static Prenotazione prenotazioneFromRs(ResultSet rs) throws Exception {
        return new Prenotazione(
                rs.getString("codice"),
                rs.getString("username"),
                rs.getString("nome_cliente"),
                rs.getString("cognome_cliente"),
                proiezioneFromRs(rs),
                rs.getInt("num_posti"));
    }
}