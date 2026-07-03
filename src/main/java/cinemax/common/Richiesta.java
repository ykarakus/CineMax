/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.common;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Rappresenta una richiesta inviata dal client al server.
 * Ogni richiesta e composta dal tipo di operazione da eseguire e
 * da una mappa di parametri il cui contenuto dipende dall'operazione
 * (ad esempio, per il LOGIN i parametri sono "username" e "password").
 */
public class Richiesta implements Serializable {

    /** Identificatore di versione per la serializzazione. */
    private static final long serialVersionUID = 1L;

    /** Tipo di operazione richiesta al server. */
    private TipoOperazione operazione;

    /** Parametri della richiesta, indicizzati per nome. */
    private HashMap<String, Object> parametri;

    /**
     * Costruisce una richiesta per l'operazione indicata,
     * inizialmente senza parametri.
     *
     * @param operazione tipo di operazione da eseguire
     */
    public Richiesta(TipoOperazione operazione) {
        this.operazione = operazione;
        this.parametri = new HashMap<String, Object>();
    }

    /** @return tipo di operazione richiesta */
    public TipoOperazione getOperazione() {
        return operazione;
    }

    /**
     * Aggiunge un parametro alla richiesta.
     *
     * @param nome   nome del parametro
     * @param valore valore del parametro (deve essere serializzabile)
     */
    public void setParametro(String nome, Object valore) {
        parametri.put(nome, valore);
    }

    /**
     * Restituisce il valore di un parametro.
     *
     * @param nome nome del parametro
     * @return valore del parametro, oppure null se assente
     */
    public Object getParametro(String nome) {
        return parametri.get(nome);
    }
}
