package cinemax.Models;

import java.util.Date;

/**
 * Classe che rappresenta un utente del sistema CineMax.
 *
 * Ogni utente possiede:
 * - nome
 * - cognome
 * - username
 * - password
 * - data di nascita
 * - domicilio
 * - ruolo
 *
 * I ruoli possibili sono:
 * CLIENTE, PROIEZIONISTA e BIGLIETTAIO.
 */

public class Utente {
    private String nome;
    private String cognome;
    // Username usato per il login
    private String username;
    private String password;
    private Date data_nascita;
    private String domicilio;
    private Ruolo ruolo;


    public Utente() {
    }

    /**
     * Costruttore completo della classe Utente.
     *
     * @param nome nome utente
     * @param cognome cognome utente
     * @param username username utente
     * @param password password utente
     * @param data_nascita data di nascita
     * @param domicilio domicilio utente
     * @param ruolo ruolo utente
     */
    public Utente(String nome, String cognome, String username,String password, Date data_nascita, String domicilio, Ruolo ruolo) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.data_nascita = data_nascita;
        this.domicilio = domicilio;
        this.ruolo = ruolo;
    }


    public String getNome() {

        return nome;
    }
    public void setNome(String nome) {

        this.nome = nome;
    }
    public String getCognome() {

        return cognome;
    }
    public void setCognome(String cognome) {

        this.cognome = cognome;
    }

    /**
     * Restituisce lo username dell'utente.
     * @return username
     */
    public String getUsername() {

        return username;
    }

    /**
     * Imposta lo username dell'utente.
     * @param username nuovo username
     */
    public void setUsername(String username) {

        this.username = username;
    }

    public String getPassword() {

        return password;
    }
    public void setPassword(String password) {

        this.password = password;
    }
    public Date getData_nascita() {

        return data_nascita;
    }
    public void setData_nascita(Date data_nascita) {

        this.data_nascita = data_nascita;
    }
    public String getDomicilio() {
        return domicilio;
    }
    public void setDomicilio(String domicilio) {

        this.domicilio = domicilio;
    }
    public Ruolo getRuolo() {

        return ruolo;
    }
    public void setRuolo(Ruolo ruolo) {

        this.ruolo = ruolo;
    }

}

