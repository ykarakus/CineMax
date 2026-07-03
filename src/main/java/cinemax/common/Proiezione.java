/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.common;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Rappresenta una proiezione di un film in una determinata data e ora,
 * con il relativo prezzo del biglietto.
 * Il prezzo appartiene alla proiezione e non al film, perche lo stesso
 * film puo' essere proiettato con prezzi diversi in date diverse.
 */
public class Proiezione implements Serializable {

    /** Identificatore di versione per la serializzazione. */
    private static final long serialVersionUID = 1L;

    /** Capienza massima della sala (cinema monosala da 200 posti). */
    public static final int CAPIENZA_SALA = 200;

    /** Identificativo univoco della proiezione (chiave primaria sul database). */
    private int id;

    /** Film proiettato. */
    private Film film;

    /** Data e ora di inizio della proiezione. */
    private LocalDateTime dataOra;

    /** Prezzo del biglietto in euro. */
    private double prezzoBiglietto;

    /**
     * Numero di posti liberi per la proiezione.
     * Viene calcolato dal server come differenza tra la capienza della
     * sala e la somma dei posti gia' prenotati.
     */
    private int postiLiberi;

    /**
     * Costruttore completo.
     *
     * @param id              identificativo univoco della proiezione
     * @param film            film proiettato
     * @param dataOra         data e ora di inizio
     * @param prezzoBiglietto prezzo del biglietto in euro
     * @param postiLiberi     numero di posti ancora disponibili
     */
    public Proiezione(int id, Film film, LocalDateTime dataOra,
                      double prezzoBiglietto, int postiLiberi) {
        this.id = id;
        this.film = film;
        this.dataOra = dataOra;
        this.prezzoBiglietto = prezzoBiglietto;
        this.postiLiberi = postiLiberi;
    }

    /** @return identificativo univoco della proiezione */
    public int getId() {
        return id;
    }

    /** @return film proiettato */
    public Film getFilm() {
        return film;
    }

    /** @return data e ora di inizio della proiezione */
    public LocalDateTime getDataOra() {
        return dataOra;
    }

    /** @return prezzo del biglietto in euro */
    public double getPrezzoBiglietto() {
        return prezzoBiglietto;
    }

    /** @return numero di posti liberi */
    public int getPostiLiberi() {
        return postiLiberi;
    }

    /**
     * Restituisce una rappresentazione testuale della proiezione.
     *
     * @return stringa con titolo del film, data/ora e prezzo
     */
    @Override
    public String toString() {
        return film.getTitolo() + " - " + dataOra + " - " + prezzoBiglietto + " euro";
    }
}
