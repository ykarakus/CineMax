/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax.Models;

/**
 * Classe che rappresenta una prenotazione.
 *
 * Ogni prenotazione contiene:
 * - un codice univoco;
 * - l'utente che ha effettuato la prenotazione;
 * - la proiezione prenotata;
 * - il numero di biglietti prenotati.
 */
public class Prenotazione {

    /** Codice univoco della prenotazione. */
    private String codice;

    /** Utente che ha effettuato la prenotazione. */
    private Utente utente;

    /** Proiezione prenotata dall'utente. */
    private Proiezione proiezione;

    /** Numero di biglietti prenotati. */
    private int numeroBiglietti;

    /**
     * Costruttore della classe Prenotazione.
     *
     * Crea una nuova prenotazione e genera automaticamente
     * un codice univoco.
     *
     * @param utente utente che effettua la prenotazione
     * @param proiezione proiezione scelta dall'utente
     * @param numeroBiglietti numero di biglietti prenotati
     */
    public Prenotazione(Utente utente, Proiezione proiezione, int numeroBiglietti) {
        this.codice = generaCodice();
        this.utente = utente;
        this.proiezione = proiezione;
        this.numeroBiglietti = numeroBiglietti;
    }

    /**
     * Restituisce l'utente che ha effettuato la prenotazione.
     *
     * @return utente della prenotazione
     */
    public Utente getUtente() {
        return utente;
    }

    /**
     * Imposta l'utente associato alla prenotazione.
     *
     * @param utente nuovo utente della prenotazione
     */
    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    /**
     * Restituisce la proiezione associata alla prenotazione.
     *
     * @return proiezione prenotata
     */
    public Proiezione getProiezione() {
        return proiezione;
    }

    /**
     * Imposta la proiezione associata alla prenotazione.
     *
     * @param proiezione nuova proiezione prenotata
     */
    public void setProiezione(Proiezione proiezione) {
        this.proiezione = proiezione;
    }

    /**
     * Restituisce il codice univoco della prenotazione.
     *
     * @return codice della prenotazione
     */
    public String getCodice() {
        return codice;
    }

    /**
     * Imposta il codice della prenotazione.
     *
     * Questo metodo viene usato quando una prenotazione viene caricata dal file CSV
     * e bisogna mantenere il codice già salvato.
     *
     * @param codice nuovo codice della prenotazione
     */
    public void setCodice(String codice) {
        this.codice = codice;
    }

    /**
     * Restituisce il numero di biglietti prenotati.
     *
     * @return numero di biglietti
     */
    public int getNumeroBiglietti() {
        return numeroBiglietti;
    }

    /**
     * Imposta il numero di biglietti prenotati.
     *
     * @param numeroBiglietti nuovo numero di biglietti
     */
    public void setNumeroBiglietti(int numeroBiglietti) {
        this.numeroBiglietti = numeroBiglietti;
    }

    /**
     * Calcola il costo totale della prenotazione.
     *
     * Il costo totale è dato dal numero di biglietti moltiplicato
     * per il costo unitario del biglietto della proiezione.
     *
     * @return costo totale della prenotazione
     */
    public double getCostoTotale() {
        return numeroBiglietti * proiezione.getCostoBiglietto();
    }

    /**
     * Genera automaticamente un codice univoco per la prenotazione.
     *
     * Il codice è composto dal prefisso "PR" seguito dal timestamp corrente.
     *
     * @return codice univoco della prenotazione
     */
    private String generaCodice() {
        return "PR" + System.currentTimeMillis();
    }
}