/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.common;

import java.io.Serializable;

/**
 * Rappresenta un film del catalogo del cinema.
 * La classe implementa Serializable perche i suoi oggetti vengono
 * trasmessi sul socket tra client e server tramite ObjectStream.
 */
public class Film implements Serializable {

    /** Identificatore di versione per la serializzazione. */
    private static final long serialVersionUID = 1L;

    /** Identificativo univoco del film (chiave primaria sul database). */
    private int id;

    /** Titolo del film. */
    private String titolo;

    /** Genere del film (es. Drama, Comedy, Action). */
    private String genere;

    /** Regista del film. */
    private String regista;

    /** Anno di uscita del film. */
    private int anno;

    /** Durata del film espressa in minuti. */
    private int durataMinuti;

    /** Eta' minima del pubblico per assistere alla proiezione. */
    private int etaMinima;

    /**
     * Costruttore completo.
     *
     * @param id            identificativo univoco del film
     * @param titolo        titolo del film
     * @param genere        genere del film
     * @param regista       regista del film
     * @param anno          anno di uscita
     * @param durataMinuti  durata in minuti
     * @param etaMinima     eta' minima del pubblico
     */
    public Film(int id, String titolo, String genere, String regista,
                int anno, int durataMinuti, int etaMinima) {
        this.id = id;
        this.titolo = titolo;
        this.genere = genere;
        this.regista = regista;
        this.anno = anno;
        this.durataMinuti = durataMinuti;
        this.etaMinima = etaMinima;
    }

    /** @return identificativo univoco del film */
    public int getId() {
        return id;
    }

    /** @return titolo del film */
    public String getTitolo() {
        return titolo;
    }

    /** @return genere del film */
    public String getGenere() {
        return genere;
    }

    /** @return regista del film */
    public String getRegista() {
        return regista;
    }

    /** @return anno di uscita del film */
    public int getAnno() {
        return anno;
    }

    /** @return durata del film in minuti */
    public int getDurataMinuti() {
        return durataMinuti;
    }

    /** @return eta' minima del pubblico */
    public int getEtaMinima() {
        return etaMinima;
    }

    /**
     * Restituisce una rappresentazione testuale del film.
     *
     * @return stringa con titolo, anno e regista
     */
    @Override
    public String toString() {
        return titolo + " (" + anno + ") - " + regista;
    }
}
