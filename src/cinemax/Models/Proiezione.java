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
    private Date  data_ora;
    private Double costo_biglietto;
    private int posti_prenotati = 0;

    public Proiezione() {
    }

    public Proiezione(Film film, Date data_ora, Double costo_biglietto) {
        this.film = film;
        this.data_ora = data_ora;
        this.costo_biglietto = costo_biglietto;
    }

    public Proiezione(Film film, Date data_ora, Double costo_biglietto, int posti_prenotati) {
        this.film = film;
        this.data_ora = data_ora;
        this.costo_biglietto = costo_biglietto;
        this.posti_prenotati = posti_prenotati;
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
    public Date getData_ora() {

        return data_ora;
    }
    public void setData_ora(Date data_ora) {

        this.data_ora = data_ora;
    }

    /**
     * Restituisce il costo del biglietto.
     *
     * @return costo del biglietto
     */
    public Double getCosto_biglietto() {

        return costo_biglietto;
    }
    public void setCosto_biglietto(Double costo_biglietto) {

        this.costo_biglietto = costo_biglietto;
    }

    public int getPosti_prenotati() {

        return posti_prenotati;
    }

    public void setPosti_prenotati(int posti_prenotati) {

        this.posti_prenotati = posti_prenotati;
    }


    /**
     * Controlla se ci sono già prenotazioni.
     *
     * @return true se ci sono posti prenotati
     */
    public boolean hasPrenotazioni() {

        return posti_prenotati > 0;
    }

    /**
     * Calcola il numero di posti disponibili.
     *
     * @return numero di posti ancora disponibili
     */
    public int getNumeroPostiDisponibili() {
        return CAPIENZA - posti_prenotati;
    }
}

