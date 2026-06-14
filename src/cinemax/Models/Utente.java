/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax.Models;

import java.util.Date;

/**
 * Classe che rappresenta un utente del sistema CineMax.
 *
 * Ogni utente possiede:
 * - nome;
 * - cognome;
 * - username;
 * - password;
 * - data di nascita;
 * - domicilio;
 * - ruolo.
 *
 * I ruoli possibili sono definiti dall'enum Ruolo.
 */
public class Utente {

    /** Nome dell'utente. */
    private String nome;

    /** Cognome dell'utente. */
    private String cognome;

    /** Username usato per il login. */
    private String username;

    /** Password dell'utente, salvata in forma cifrata nel file CSV. */
    private String password;

    /** Data di nascita dell'utente. */
    private Date data_nascita;

    /** Domicilio dell'utente. */
    private String domicilio;

    /** Ruolo dell'utente nel sistema. */
    private Ruolo ruolo;

    /**
     * Costruttore vuoto della classe Utente.
     *
     * Permette di creare un oggetto Utente senza inizializzare subito
     * i suoi attributi.
     */
    public Utente() {
    }

    /**
     * Costruttore completo della classe Utente.
     *
     * @param nome nome dell'utente
     * @param cognome cognome dell'utente
     * @param username username dell'utente
     * @param password password dell'utente
     * @param data_nascita data di nascita dell'utente
     * @param domicilio domicilio dell'utente
     * @param ruolo ruolo dell'utente
     */
    public Utente(String nome, String cognome, String username, String password,
                  Date data_nascita, String domicilio, Ruolo ruolo) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.data_nascita = data_nascita;
        this.domicilio = domicilio;
        this.ruolo = ruolo;
    }

    /**
     * Restituisce il nome dell'utente.
     *
     * @return nome dell'utente
     */
    public String getNome() {
        return nome;
    }

    /**
     * Imposta il nome dell'utente.
     *
     * @param nome nuovo nome dell'utente
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce il cognome dell'utente.
     *
     * @return cognome dell'utente
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * Imposta il cognome dell'utente.
     *
     * @param cognome nuovo cognome dell'utente
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * Restituisce lo username dell'utente.
     *
     * @return username dell'utente
     */
    public String getUsername() {
        return username;
    }

    /**
     * Imposta lo username dell'utente.
     *
     * @param username nuovo username dell'utente
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Restituisce la password dell'utente.
     *
     * @return password dell'utente
     */
    public String getPassword() {
        return password;
    }

    /**
     * Imposta la password dell'utente.
     *
     * @param password nuova password dell'utente
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Restituisce la data di nascita dell'utente.
     *
     * @return data di nascita dell'utente
     */
    public Date getData_nascita() {
        return data_nascita;
    }

    /**
     * Imposta la data di nascita dell'utente.
     *
     * @param data_nascita nuova data di nascita dell'utente
     */
    public void setData_nascita(Date data_nascita) {
        this.data_nascita = data_nascita;
    }

    /**
     * Restituisce il domicilio dell'utente.
     *
     * @return domicilio dell'utente
     */
    public String getDomicilio() {
        return domicilio;
    }

    /**
     * Imposta il domicilio dell'utente.
     *
     * @param domicilio nuovo domicilio dell'utente
     */
    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    /**
     * Restituisce il ruolo dell'utente.
     *
     * @return ruolo dell'utente
     */
    public Ruolo getRuolo() {
        return ruolo;
    }

    /**
     * Imposta il ruolo dell'utente.
     *
     * @param ruolo nuovo ruolo dell'utente
     */
    public void setRuolo(Ruolo ruolo) {
        this.ruolo = ruolo;
    }
}