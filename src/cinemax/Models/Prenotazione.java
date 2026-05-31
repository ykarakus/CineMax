package cinemax.Models;

/**
 * Classe che rappresenta una prenotazione.
 *
 * Ogni prenotazione contiene:
 * - codice univoco
 * - utente che ha effettuato la prenotazione
 * - proiezione prenotata
 * - numero di biglietti
 */
public class Prenotazione {

    // Codice univoco della prenotazione
    private String codice;

    // Utente che ha effettuato la prenotazione
    private Utente utente;

    // Proiezione prenotata dall'utente
    private Proiezione proiezione;

    // Numero di biglietti prenotati
    private int numeroBiglietti;

    /**
     * Costruttore della prenotazione.
     *
     * @param utente utente che prenota
     * @param proiezione proiezione scelta
     * @param numeroBiglietti numero di biglietti prenotati
     */
    public Prenotazione(Utente utente, Proiezione proiezione, int numeroBiglietti) {
        this.codice = generaCodice();
        this.utente = utente;
        this.proiezione = proiezione;
        this.numeroBiglietti = numeroBiglietti;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public Proiezione getProiezione() {
        return proiezione;
    }

    public void setProiezione(Proiezione proiezione) {
        this.proiezione = proiezione;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public int getNumeroBiglietti() {
        return numeroBiglietti;
    }

    public void setNumeroBiglietti(int numeroBiglietti) {
        this.numeroBiglietti = numeroBiglietti;
    }

    /**
     * Calcola il costo totale della prenotazione.
     * @return costo totale della prenotazione
     */
    public double getCostoTotale() {
        return numeroBiglietti * proiezione.getCosto_biglietto();
    }

    /**
     * Genera automaticamente un codice univoco per la prenotazione.
     * @return codice prenotazione
     */
    private String generaCodice() {
        return "PR" + System.currentTimeMillis();
    }
}