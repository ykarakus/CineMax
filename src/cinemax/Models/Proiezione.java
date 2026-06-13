/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax.Models;

import java.util.Date;

/**
 * Classe che rappresenta una proiezione.
 *
 * Ogni proiezione contiene:
 * - il film proiettato
 * - la data e l'ora della proiezione
 * - il costo del biglietto
 * - il numero di posti già prenotati
 */
public class Proiezione {

    // Capienza massima della sala
    private static final int CAPIENZA = 200;

    private Film film;
    private Date  dataOra;
    private Double costoBiglietto;
    private int postiPrenotati = 0;

    public Proiezione() {
    }

    public Proiezione(Film film, Date dataOra, Double costoBiglietto) {
        this.film = film;
        this.dataOra = dataOra;
        this.costoBiglietto = costoBiglietto;
    }

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
    public void setFilm(Film film) {

        this.film = film;
    }
    public Date getDataOra() {

        return dataOra;
    }
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
    public void setCostoBiglietto(Double costoBiglietto) {

        this.costoBiglietto = costoBiglietto;
    }

    public int getPostiPrenotati() {

        return postiPrenotati;
    }

    public void setPostiPrenotati(int postiPrenotati) {

        this.postiPrenotati = postiPrenotati;
    }


    /**
     * Controlla se ci sono già prenotazioni.
     *
     * @return true se ci sono posti prenotati
     */
    public boolean hasPrenotazioni() {

        return postiPrenotati > 0;
    }

    /**
     * Calcola il numero di posti disponibili.
     *
     * @return numero di posti ancora disponibili
     */
    public int getNumeroPostiDisponibili() {
        return CAPIENZA - postiPrenotati;
    }
}

