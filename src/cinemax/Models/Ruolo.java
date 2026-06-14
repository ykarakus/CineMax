/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax.Models;

/**
 * Enum che rappresenta i possibili ruoli degli utenti nel sistema CineMax.
 *
 * Ogni ruolo determina quali funzionalità sono disponibili per l'utente
 * dopo il login o durante l'utilizzo dell'applicazione.
 */
public enum Ruolo {

    /**
     * Ruolo assegnato agli utenti registrati come clienti.
     *
     * Un cliente può cercare proiezioni, creare prenotazioni,
     * visualizzare le proprie prenotazioni, modificarle e cancellarle.
     */
    CLIENTE,

    /**
     * Ruolo assegnato agli utenti proiezionisti.
     *
     * Un proiezionista può aggiungere, modificare ed eliminare proiezioni,
     * rispettando le regole di gestione del palinsesto.
     */
    PROIEZIONISTA,

    /**
     * Ruolo assegnato agli utenti bigliettai.
     *
     * Un bigliettaio può visualizzare le prenotazioni della giornata
     * e cercare prenotazioni tramite diversi criteri.
     */
    BIGLIETTAIO,

    /**
     * Ruolo usato per rappresentare un utente non autenticato.
     *
     * Un guest può cercare proiezioni e visualizzarne i dettagli,
     * ma non può effettuare prenotazioni.
     */
    GUEST
}