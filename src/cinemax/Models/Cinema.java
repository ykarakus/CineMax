package cinemax.Models;

import java.util.ArrayList;

/**
 * Classe che rappresenta il cinema CineMax.
 *
 * Il cinema è monosala e possiede:
 * - un nome
 * - una capienza massima di 200 posti
 * - una lista di proiezioni
 *
 * La classe permette di:
 * - aggiungere proiezioni
 * - rimuovere proiezioni
 * - controllare i posti disponibili
 */

public class Cinema {

    // Nome del cinema
    private String nome;

    // Capienza massima della sala
    private final int capienza;

    // Lista di tutte le proiezioni del cinema
    private ArrayList<Proiezione> proiezioni;

    /**
     * Costruttore della classe Cinema.
     *
     * @param nome nome del cinema
     */
    public Cinema(String nome) {

        this.nome = nome;

        // 200 posti
        this.capienza = 200;

        // Creazione lista vuota di proiezioni (da aggiungere lista data dal docente)
        this.proiezioni = new ArrayList<>();
    }

    /**
     * Restituisce il nome del cinema.
     *
     * @return nome del cinema
     */
    public String getNome() {
        return nome;
    }

    /**
     * Restituisce la capienza massima della sala.
     *
     * @return numero massimo di posti
     */
    public int getCapienza() {
        return capienza;
    }

    /**
     * Restituisce la lista delle proiezioni.
     *
     * @return lista proiezioni
     */
    public ArrayList<Proiezione> getProiezioni() {
        return proiezioni;
    }

    /**
     * Aggiunge una nuova proiezione alla lista.
     *
     * @param proiezione proiezione da aggiungere
     */
    public void aggiungiProiezione(Proiezione proiezione) {
        proiezioni.add(proiezione);
    }

    /**
     * Rimuove una proiezione dalla lista.
     *
     * @param proiezione proiezione da rimuovere
     * @return true se rimossa correttamente, false altrimenti
     */
    public boolean rimuoviProiezione(Proiezione proiezione) {
        return proiezioni.remove(proiezione);
    }

    /**
     * Controlla se una proiezione esiste nella lista.
     *
     * @param proiezione proiezione da controllare
     * @return true se presente
     */
    public boolean contieneProiezione(Proiezione proiezione) {
        return proiezioni.contains(proiezione);
    }

    /**
     * Calcola il numero di posti disponibili per una determinata proiezione.
     *
     * @param proiezione proiezione selezionata
     * @return numero posti disponibili
     */
    public int getPostiDisponibili(Proiezione proiezione) {

        return capienza - proiezione.getPosti_prenotati();
    }

    /**
     * Controlla se ci sono abbastanza posti liberi per effettuare una prenotazione.
     *
     * @param proiezione      proiezione scelta
     * @param numeroBiglietti numero di biglietti richiesti
     * @return true se i posti sono sufficienti
     */
    public boolean haPostiDisponibili(Proiezione proiezione, int numeroBiglietti) {

        return getPostiDisponibili(proiezione) >= numeroBiglietti;
    }
}