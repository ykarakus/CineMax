/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.common;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Rappresenta un utente registrato alla piattaforma CineMax.
 * L'utente puo' avere uno dei tre ruoli previsti: cliente,
 * proiezionista o bigliettaio.
 * La password non e presente in questa classe: viene trasmessa
 * solo all'interno delle richieste di login e registrazione e non
 * viaggia mai dal server verso il client.
 */
public class Utente implements Serializable {

    /** Identificatore di versione per la serializzazione. */
    private static final long serialVersionUID = 1L;

    /** Ruolo cliente: puo' effettuare e gestire prenotazioni. */
    public static final String RUOLO_CLIENTE = "cliente";

    /** Ruolo proiezionista: gestisce il palinsesto delle proiezioni. */
    public static final String RUOLO_PROIEZIONISTA = "proiezionista";

    /** Ruolo bigliettaio: consulta e cerca le prenotazioni. */
    public static final String RUOLO_BIGLIETTAIO = "bigliettaio";

    /** Username univoco dell'utente (chiave primaria sul database). */
    private String username;

    /** Nome dell'utente. */
    private String nome;

    /** Cognome dell'utente. */
    private String cognome;

    /** Data di nascita dell'utente (facoltativa, puo' essere null). */
    private LocalDate dataNascita;

    /** Luogo del domicilio dell'utente. */
    private String domicilio;

    /** Ruolo dell'utente: cliente, proiezionista o bigliettaio. */
    private String ruolo;

    /**
     * Costruttore completo.
     *
     * @param username    username univoco
     * @param nome        nome dell'utente
     * @param cognome     cognome dell'utente
     * @param dataNascita data di nascita (puo' essere null)
     * @param domicilio   luogo del domicilio
     * @param ruolo       ruolo dell'utente
     */
    public Utente(String username, String nome, String cognome,
                  LocalDate dataNascita, String domicilio, String ruolo) {
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.domicilio = domicilio;
        this.ruolo = ruolo;
    }

    /** @return username univoco dell'utente */
    public String getUsername() {
        return username;
    }

    /** @return nome dell'utente */
    public String getNome() {
        return nome;
    }

    /** @return cognome dell'utente */
    public String getCognome() {
        return cognome;
    }

    /** @return data di nascita, oppure null se non fornita */
    public LocalDate getDataNascita() {
        return dataNascita;
    }

    /** @return luogo del domicilio */
    public String getDomicilio() {
        return domicilio;
    }

    /** @return ruolo dell'utente */
    public String getRuolo() {
        return ruolo;
    }

    /**
     * Restituisce una rappresentazione testuale dell'utente.
     *
     * @return stringa con nome, cognome e ruolo
     */
    @Override
    public String toString() {
        return nome + " " + cognome + " (" + ruolo + ")";
    }
}
