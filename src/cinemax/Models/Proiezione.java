/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax.Models;

import java.util.Date;

/**
 * Classe che rappresenta una proiezione cinematografica.
 *
 * Ogni proiezione contiene:
 * - il film proiettato;
 * - la data e l'ora della proiezione;
 * - il costo del biglietto;
 * - il numero di posti già prenotati.
 *
 * La sala è monosala e ha una capienza massima di 200 posti.
 */
public class Proiezione {

    /** Capienza massima della sala. */
    private static final int CAPIENZA = 200;

    /** Film associato alla proiezione. */
    private Film film;

    /** Data e ora della proiezione. */
    private Date dataOra;

    /** Costo del biglietto della proiezione. */
    private Double costoBiglietto;

    /** Numero di posti già prenotati per questa proiezione. */
    private int postiPrenotati = 0;

    /**
     * Costruttore vuoto della classe Proiezione.
     *
     * Permette di creare un oggetto Proiezione senza inizializzare subito
     * i suoi attributi.
     */
    public Proiezione() {
    }

    /**
     * Costruttore della classe Proiezione.
     *
     * Crea una proiezione con film, data/ora e costo del biglietto.
     * Il numero di posti prenotati viene inizializzato a zero.
     *
     * @param film film associato alla proiezione
     * @param dataOra data e ora della proiezione
     * @param costoBiglietto costo del biglietto
     */
    public Proiezione(Film film, Date dataOra, Double costoBiglietto) {
        this.film = film;
        this.dataOra = dataOra;
        this.costoBiglietto = costoBiglietto;
    }

    /**
     * Costruttore completo della classe Proiezione.
     *
     * Crea una proiezione specificando anche il numero di posti già prenotati.
     *
     * @param film film associato alla proiezione
     * @param dataOra data e ora della proiezione
     * @param costoBiglietto costo del biglietto
     * @param postiPrenotati numero di posti già prenotati
     */
    public Proiezione(Film film, Date dataOra, Double costoBiglietto, int postiPrenotati) {
        this.film = film;
        this.dataOra = dataOra;
        this.costoBiglietto = costoBiglietto;
        this.postiPrenotati = postiPrenotati;
    }

    /**
     * Restituisce il film associato alla proiezione.
     *
     * @return film della proiezione
     */
    public Film getFilm() {
        return film;
    }

    /**
     * Imposta il film associato alla proiezione.
     *
     * @param film nuovo film della proiezione
     */
    public void setFilm(Film film) {
        this.film = film;
    }

    /**
     * Restituisce la data e l'ora della proiezione.
     *
     * @return data e ora della proiezione
     */
    public Date getDataOra() {
        return dataOra;
    }

    /**
     * Imposta la data e l'ora della proiezione.
     *
     * @param dataOra nuova data e ora della proiezione
     */
    public void setDataOra(Date dataOra) {
        this.dataOra = dataOra;
    }

    /**
     * Restituisce il costo del biglietto.
     *
     * @return costo del biglietto
     */
    public Double getCostoBiglietto() {
        return costoBiglietto;
    }

    /**
     * Imposta il costo del biglietto.
     *
     * @param costoBiglietto nuovo costo del biglietto
     */
    public void setCostoBiglietto(Double costoBiglietto) {
        this.costoBiglietto = costoBiglietto;
    }

    /**
     * Restituisce il numero di posti già prenotati.
     *
     * @return numero di posti prenotati
     */
    public int getPostiPrenotati() {
        return postiPrenotati;
    }

    /**
     * Imposta il numero di posti già prenotati.
     *
     * @param postiPrenotati nuovo numero di posti prenotati
     */
    public void setPostiPrenotati(int postiPrenotati) {
        this.postiPrenotati = postiPrenotati;
    }

    /**
     * Controlla se per la proiezione esistono già prenotazioni.
     *
     * @return true se ci sono posti prenotati, false altrimenti
     */
    public boolean hasPrenotazioni() {
        return postiPrenotati > 0;
    }

    /**
     * Calcola il numero di posti ancora disponibili per la proiezione.
     *
     * @return numero di posti disponibili
     */
    public int getNumeroPostiDisponibili() {
        return CAPIENZA - postiPrenotati;
    }
}