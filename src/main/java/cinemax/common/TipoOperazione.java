/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.common;

/**
 * Elenco delle operazioni previste dal protocollo di comunicazione
 * tra clientCM e serverCM. Ogni richiesta inviata dal client contiene
 * uno di questi valori, in base al quale il server stabilisce quale
 * servizio erogare.
 */
public enum TipoOperazione {

    /** Autenticazione di un utente registrato. */
    LOGIN,

    /** Registrazione di un nuovo cliente. */
    REGISTRA_CLIENTE,

    /** Ricerca di proiezioni per titolo, genere, date e/o prezzo. */
    CERCA_PROIEZIONE,

    /** Visualizzazione dei dettagli di una proiezione (con posti liberi). */
    VISUALIZZA_PROIEZIONE,

    /** Creazione di una nuova prenotazione (solo clienti). */
    CREA_PRENOTAZIONE,

    /** Visualizzazione delle prenotazioni del cliente autenticato. */
    VISUALIZZA_PRENOTAZIONI,

    /** Modifica della data di una prenotazione esistente (solo clienti). */
    MODIFICA_PRENOTAZIONE,

    /** Cancellazione di una prenotazione esistente (solo clienti). */
    ELIMINA_PRENOTAZIONE,

    /** Ricerca di prenotazioni (solo bigliettai). */
    CERCA_PRENOTAZIONE,

    /** Prenotazioni relative alla data odierna (solo bigliettai). */
    PRENOTAZIONI_OGGI,

    /** Aggiunta di una nuova proiezione (solo proiezionisti). */
    AGGIUNGI_PROIEZIONE,

    /** Modifica di una proiezione esistente (solo proiezionisti). */
    MODIFICA_PROIEZIONE,

    /** Eliminazione di una proiezione esistente (solo proiezionisti). */
    ELIMINA_PROIEZIONE,

    /** Elenco delle proiezioni pianificate o storiche (solo proiezionisti). */
    VISUALIZZA_PALINSESTO,

    /** Chiusura della sessione utente. */
    LOGOUT
}
