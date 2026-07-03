/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.common;

import java.io.Serializable;

/**
 * Rappresenta una prenotazione di posti effettuata da un cliente
 * per una specifica proiezione.
 * Ogni prenotazione e identificata da un codice univoco generato
 * dal server al momento della creazione.
 */
public class Prenotazione implements Serializable {

    /** Identificatore di versione per la serializzazione. */
    private static final long serialVersionUID = 1L;

    /** Codice univoco della prenotazione. */
    private String codice;

    /** Username del cliente che ha effettuato la prenotazione. */
    private String username;

    /** Nome del cliente (utile al bigliettaio in fase di visualizzazione). */
    private String nomeCliente;

    /** Cognome del cliente (utile al bigliettaio in fase di visualizzazione). */
    private String cognomeCliente;

    /** Proiezione a cui si riferisce la prenotazione. */
    private Proiezione proiezione;

    /** Numero di posti prenotati. */
    private int numPosti;

    /**
     * Costruttore completo.
     *
     * @param codice         codice univoco della prenotazione
     * @param username       username del cliente
     * @param nomeCliente    nome del cliente
     * @param cognomeCliente cognome del cliente
     * @param proiezione     proiezione prenotata
     * @param numPosti       numero di posti prenotati
     */
    public Prenotazione(String codice, String username, String nomeCliente,
                        String cognomeCliente, Proiezione proiezione, int numPosti) {
        this.codice = codice;
        this.username = username;
        this.nomeCliente = nomeCliente;
        this.cognomeCliente = cognomeCliente;
        this.proiezione = proiezione;
        this.numPosti = numPosti;
    }

    /** @return codice univoco della prenotazione */
    public String getCodice() {
        return codice;
    }

    /** @return username del cliente */
    public String getUsername() {
        return username;
    }

    /** @return nome del cliente */
    public String getNomeCliente() {
        return nomeCliente;
    }

    /** @return cognome del cliente */
    public String getCognomeCliente() {
        return cognomeCliente;
    }

    /** @return proiezione prenotata */
    public Proiezione getProiezione() {
        return proiezione;
    }

    /** @return numero di posti prenotati */
    public int getNumPosti() {
        return numPosti;
    }

    /**
     * Calcola il costo totale della prenotazione.
     *
     * @return prezzo del biglietto moltiplicato per il numero di posti
     */
    public double getCostoTotale() {
        return proiezione.getPrezzoBiglietto() * numPosti;
    }

    /**
     * Restituisce una rappresentazione testuale della prenotazione.
     *
     * @return stringa con codice, cliente e numero di posti
     */
    @Override
    public String toString() {
        return codice + " - " + nomeCliente + " " + cognomeCliente
                + " - " + numPosti + " posti";
    }
}
