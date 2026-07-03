/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.common;

import java.io.Serializable;

/**
 * Rappresenta la risposta inviata dal server al client dopo
 * l'elaborazione di una richiesta.
 * Contiene l'esito dell'operazione, un messaggio descrittivo
 * (utile soprattutto in caso di errore) e gli eventuali dati
 * prodotti dall'operazione (ad esempio la lista delle proiezioni
 * trovate da una ricerca).
 */
public class Risposta implements Serializable {

    /** Identificatore di versione per la serializzazione. */
    private static final long serialVersionUID = 1L;

    /** Esito dell'operazione: true se completata con successo. */
    private boolean successo;

    /** Messaggio descrittivo dell'esito. */
    private String messaggio;

    /** Dati restituiti dall'operazione (puo' essere null). */
    private Object dati;

    /**
     * Costruttore completo.
     *
     * @param successo  esito dell'operazione
     * @param messaggio messaggio descrittivo
     * @param dati      dati restituiti (puo' essere null)
     */
    public Risposta(boolean successo, String messaggio, Object dati) {
        this.successo = successo;
        this.messaggio = messaggio;
        this.dati = dati;
    }

    /** @return true se l'operazione e' andata a buon fine */
    public boolean isSuccesso() {
        return successo;
    }

    /** @return messaggio descrittivo dell'esito */
    public String getMessaggio() {
        return messaggio;
    }

    /** @return dati restituiti dall'operazione, oppure null */
    public Object getDati() {
        return dati;
    }
}
