package cinemax.Models;

/**
 * Classe che rappresenta un film.
 *
 * Ogni film contiene:
 * - titolo
 * - genere
 * - regista
 * - anno di uscita
 * - durata
 * - età minima consigliata
 */

public class Film {

    private String titolo;
    private String genere;
    private String regista;
    private int anno;
    private int durata;
    private int eta_minima;

    /**
     * Costruttore vuoto.
     */
    public Film() {
    }

    /**
     * Costruttore completo della classe Film.
     *
     * @param titolo titolo del film
     * @param genere genere del film
     * @param regista regista del film
     * @param anno anno di uscita
     * @param durata durata in minuti
     * @param eta_minima età minima richiesta
     */
    public Film(String titolo, String genere, String regista, int anno, int durata, int eta_minima) {
        this.titolo = titolo;
        this.genere = genere;
        this.regista = regista;
        this.anno = anno;
        this.durata = durata;
        this.eta_minima = eta_minima;
    }

    public String getTitolo() {

        return titolo;
    }

    public void setTitolo(String titolo) {

        this.titolo = titolo;
    }

    public String getGenere() {

        return genere;
    }

    public void setGenere(String genere) {

        this.genere = genere;
    }

    public String getRegista() {

        return regista;
    }

    public void setRegista(String regista) {

        this.regista = regista;
    }

    public int getAnno() {

        return anno;
    }

    public void setAnno(int anno) {

        this.anno = anno;
    }

    public int getDurata() {

        return durata;
    }

    public void setDurata(int durata) {

        this.durata = durata;
    }

    public int getEta_minima() {

        return eta_minima;
    }

    public void setEta_minima(int eta_minima) {

        this.eta_minima = eta_minima;
    }
}