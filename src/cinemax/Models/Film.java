/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax.Models;

/**
 * Classe che rappresenta un film.
 *
 * Ogni film contiene:
 * - titolo;
 * - genere;
 * - regista;
 * - anno di uscita;
 * - durata in minuti;
 * - età minima consigliata per la visione.
 */
public class Film {

    private String titolo;
    private String genere;
    private String regista;
    private int anno;
    private int durata;
    private int eta_minima;

    /**
     * Costruttore vuoto della classe Film.
     *
     * Permette di creare un oggetto Film senza inizializzare subito i suoi attributi.
     */
    public Film() {
    }

    /**
     * Costruttore completo della classe Film.
     *
     * @param titolo titolo del film
     * @param genere genere del film
     * @param regista regista del film
     * @param anno anno di uscita del film
     * @param durata durata del film in minuti
     * @param eta_minima età minima consigliata per la visione del film
     */
    public Film(String titolo, String genere, String regista, int anno, int durata, int eta_minima) {
        this.titolo = titolo;
        this.genere = genere;
        this.regista = regista;
        this.anno = anno;
        this.durata = durata;
        this.eta_minima = eta_minima;
    }

    /**
     * Restituisce il titolo del film.
     *
     * @return titolo del film
     */
    public String getTitolo() {
        return titolo;
    }

    /**
     * Imposta il titolo del film.
     *
     * @param titolo nuovo titolo del film
     */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    /**
     * Restituisce il genere del film.
     *
     * @return genere del film
     */
    public String getGenere() {
        return genere;
    }

    /**
     * Imposta il genere del film.
     *
     * @param genere nuovo genere del film
     */
    public void setGenere(String genere) {
        this.genere = genere;
    }

    /**
     * Restituisce il nome del regista del film.
     *
     * @return regista del film
     */
    public String getRegista() {
        return regista;
    }

    /**
     * Imposta il nome del regista del film.
     *
     * @param regista nuovo regista del film
     */
    public void setRegista(String regista) {
        this.regista = regista;
    }

    /**
     * Restituisce l'anno di uscita del film.
     *
     * @return anno di uscita del film
     */
    public int getAnno() {
        return anno;
    }

    /**
     * Imposta l'anno di uscita del film.
     *
     * @param anno nuovo anno di uscita del film
     */
    public void setAnno(int anno) {
        this.anno = anno;
    }

    /**
     * Restituisce la durata del film in minuti.
     *
     * @return durata del film in minuti
     */
    public int getDurata() {
        return durata;
    }

    /**
     * Imposta la durata del film in minuti.
     *
     * @param durata nuova durata del film in minuti
     */
    public void setDurata(int durata) {
        this.durata = durata;
    }

    /**
     * Restituisce l'età minima consigliata per la visione del film.
     *
     * @return età minima consigliata
     */
    public int getEta_minima() {
        return eta_minima;
    }

    /**
     * Imposta l'età minima consigliata per la visione del film.
     *
     * @param eta_minima nuova età minima consigliata
     */
    public void setEta_minima(int eta_minima) {
        this.eta_minima = eta_minima;
    }
}