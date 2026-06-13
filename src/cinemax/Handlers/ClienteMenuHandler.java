/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax.Handlers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cinemax.Helpers.ColoreConsole;
import cinemax.Helpers.RicercaProiezioneConsoleHelper;
import cinemax.Managers.PrenotazioneManager;
import cinemax.Managers.ProiezioneManager;
import cinemax.Managers.UtenteManager;
import cinemax.Models.Prenotazione;
import cinemax.Models.Proiezione;
import cinemax.ViewModels.CriteriRicercaProiezione;

/**
 * Gestisce il menu per gli utenti clienti loggati
 *
 * Un cliente loggato può:
 * - cercare proiezioni e prenotare biglietti
 * - visualizzare le proprie prenotazioni
 * - modificare una prenotazione esistente
 * - cancellare una prenotazione esistente
 * - fare logout
 */
public class ClienteMenuHandler {

    private Scanner scanner;
    private UtenteManager utenteManager;
    private PrenotazioneManager prenotazioneManager;
    private ProiezioneManager proiezioneManager;

    /**
     * Costruttore del ClienteMenuHandler.
     *
     * @param scanner scanner per leggere l'input dell'utente
     * @param utenteManager manager che gestisce gli utenti
     * @param prenotazioneManager manager che gestisce le prenotazioni
     * @param proiezioneManager manager che gestisce le proiezioni
     */
    public ClienteMenuHandler(Scanner scanner, UtenteManager utenteManager,
                              PrenotazioneManager prenotazioneManager, ProiezioneManager proiezioneManager) {
        this.scanner = scanner;
        this.utenteManager = utenteManager;
        this.prenotazioneManager = prenotazioneManager;
        this.proiezioneManager = proiezioneManager;
    }

    /**
     * Mostra il menu principale del cliente e gestisce le scelte
     * Il ciclo while(true) continua finché il cliente non sceglie il logout
     */
    public void mostraMenu() {
        while (true) {
            System.out.println("\n" + ColoreConsole.header("=== MENU CLIENTE ==="));
            System.out.println("1. Cerca proiezioni e prenota");
            System.out.println("2. Visualizza le mie prenotazioni");
            System.out.println("3. Modifica una prenotazione");
            System.out.println("4. Cancella una prenotazione");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine();

            switch (scelta) {
                case "1":
                    cercaEPrenota();
                    break;
                case "2":
                    visualizzaPrenotazioni();
                    break;
                case "3":
                    modificaPrenotazione();
                    break;
                case "4":
                    cancellaPrenotazione();
                    break;
                case "0":
                    // Prima di fare logout salviamo le prenotazioni su file
                    // salvaPrenotazioni() è public proprio per essere chiamato qui
                    prenotazioneManager.salvaPrenotazioni();
                    return;
                default:
                    System.out.println(ColoreConsole.errore("Scelta non valida."));
            }
        }
    }

    // ------------------------------------------------------------------------
    // CERCA PROIEZIONI E PRENOTA
    // -----------------------------------------------------------------------

    /**
     * Permette al cliente di cercare proiezioni e prenotarne una
     *
     * Il flusso è:
     * 1. acquisisciCriteriRicerca() raccoglie i criteri dall'utente
     * 2. cercaProiezione() cerca le proiezioni corrispondenti
     * 3. mostraRisultatiRicerca() mostra i risultati a schermo
     * 4. selezionaProiezione() fa scegliere una proiezione all'utente
     * 5. creaPrenotazione() crea la prenotazione
     */
    private void cercaEPrenota() {

        // raccogliamo i criteri di ricerca
        // acquisisciCriteriRicerca() chiede titolo, genere, date e prezzo
        CriteriRicercaProiezione criteri = RicercaProiezioneConsoleHelper.acquisisciCriteriRicerca(scanner);

        // cerchiamo le proiezioni corrispondenti ai criteri inseriti
        List<Proiezione> risultati = proiezioneManager.cercaProiezione(criteri);

        if (risultati.isEmpty()) {
            System.out.println("Nessuna proiezione trovata con i criteri selezionati.");
            return;
        }

        // mostriamo i risultati a schermo con titolo, data e prezzo
        RicercaProiezioneConsoleHelper.mostraRisultatiRicerca(risultati);

        // l'utente seleziona la proiezione che vuole prenotare
        // selezionaProiezione() restituisce null se l'utente sceglie 0. torna indietro
        Proiezione selezionata = RicercaProiezioneConsoleHelper.selezionaProiezione(
                scanner,
                risultati,
                "\nSeleziona una proiezione per prenotare (0 per tornare): ");

        if (selezionata == null) return;

        // Mostriamo i dettagli completi della proiezione selezionata
        proiezioneManager.visualizzaProiezione(selezionata);

        // chiediamo quanti biglietti vuole prenotare
        System.out.print("\nQuanti biglietti vuoi prenotare? ");

        // Integer.parseInt() converte la stringa in numero intero
        // Il try-catch gestisce il caso in cui l'utente scriva qualcosa che non è un numero
        int numeroBiglietti;
        try {
            numeroBiglietti = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println(ColoreConsole.errore("Numero non valido."));
            return;
        }

        if (numeroBiglietti <= 0) {
            System.out.println(ColoreConsole.errore("Il numero di biglietti deve essere maggiore di zero."));
            return;
        }

        // creiamo la prenotazione tramite il PrenotazioneManager
        // creaPrenotazione() controlla automaticamente se ci sono posti disponibili
        // e se la data è futura
        boolean riuscita = prenotazioneManager.creaPrenotazione(selezionata, numeroBiglietti);
        if (riuscita) {
            System.out.println(ColoreConsole.successo("Prenotazione effettuata con successo!"));
        }
    }

    // ---------------------------------------------------------------
    // VISUALIZZA PRENOTAZIONI
    // -------------------------------------------------------------------

    /**
     * Mostra tutte le prenotazioni dell'utente attualmente loggato
     * Chiama visualizzaPrenotazioni() del PrenotazioneManager che
     * filtra automaticamente le prenotazioni dell'utente corrente
     */
    private void visualizzaPrenotazioni() {

        // visualizzaPrenotazioni() restituisce solo le prenotazioni dell'utente loggato
        ArrayList<Prenotazione> miePrenotazioni = prenotazioneManager.visualizzaPrenotazioni();

        if (miePrenotazioni.isEmpty()) {
            System.out.println("\nNon hai ancora nessuna prenotazione.");
            return;
        }

        System.out.println("\n" + ColoreConsole.header("=== LE MIE PRENOTAZIONI ==="));
        stampaListaPrenotazioni(miePrenotazioni);
    }

    // ------------------------------------------------------
    // MODIFICA PRENOTAZIONE
    // ------------------------------------------------------

    /**
     * Permette al cliente di modificare una prenotazione esistente
     * cambiando la proiezione
     *
     * la modifica è consentita solo se sia la vecchia
     * che la nuova data di proiezione sono future
     * Questo controllo viene fatto da modificaPrenotazione() nel PrenotazioneManager
     */
    private void modificaPrenotazione() {

        ArrayList<Prenotazione> miePrenotazioni = prenotazioneManager.visualizzaPrenotazioni();

        if (miePrenotazioni.isEmpty()) {
            System.out.println("\nNon hai prenotazioni da modificare.");
            return;
        }

        System.out.println("\n" + ColoreConsole.header("=== MODIFICA PRENOTAZIONE ==="));
        stampaListaPrenotazioni(miePrenotazioni);

        // Chiediamo quale prenotazione vuole modificare
        System.out.print("\nInserisci il numero della prenotazione da modificare (0 per tornare): ");
        int numero;
        try {
            numero = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println(ColoreConsole.errore("Numero non valido."));
            return;
        }

        if (numero == 0) return;

        // numero 1 perché la lista parte da 0 ma mostriamo da 1
        if (numero < 1 || numero > miePrenotazioni.size()) {
            System.out.println(ColoreConsole.errore("Numero non valido."));
            return;
        }

        Prenotazione prenotazioneDaModificare = miePrenotazioni.get(numero - 1);

        // Cerchiamo la nuova proiezione usando i criteri di ricerca
        System.out.println("\nCerca la nuova proiezione:");
        CriteriRicercaProiezione criteri = RicercaProiezioneConsoleHelper.acquisisciCriteriRicerca(scanner);
        List<Proiezione> risultati = proiezioneManager.cercaProiezione(criteri);

        if (risultati.isEmpty()) {
            System.out.println("Nessuna proiezione trovata.");
            return;
        }

        RicercaProiezioneConsoleHelper.mostraRisultatiRicerca(risultati);

        Proiezione nuovaProiezione = RicercaProiezioneConsoleHelper.selezionaProiezione(
                scanner,
                risultati,
                "\nSeleziona la nuova proiezione (0 per annullare): ");

        if (nuovaProiezione == null) return;

        // modificaPrenotazione() controlla automaticamente che entrambe le date siano future
        boolean riuscita = prenotazioneManager.modificaPrenotazione(
                prenotazioneDaModificare.getCodice(), nuovaProiezione);

        if (riuscita) {
            System.out.println(ColoreConsole.successo("Prenotazione modificata con successo!"));
        }
    }

    // ----------------------------------------------
    // CANCELLA PRENOTAZIONE
    // ----------------------------------------------
    /**
     * Permette al cliente di cancellare una prenotazione esistente
     *
     * la cancellazione è consentita solo se la data della proiezione è futura
     * Questo controllo viene fatto da eliminaPrenotazione() nel PrenotazioneManager
     */
    private void cancellaPrenotazione() {

        ArrayList<Prenotazione> miePrenotazioni = prenotazioneManager.visualizzaPrenotazioni();

        if (miePrenotazioni.isEmpty()) {
            System.out.println("\nNon hai prenotazioni da cancellare.");
            return;
        }

        System.out.println("\n" + ColoreConsole.header("=== CANCELLA PRENOTAZIONE ==="));
        stampaListaPrenotazioni(miePrenotazioni);

        System.out.print("\nInserisci il numero della prenotazione da cancellare (0 per tornare): ");
        int numero;
        try {
            numero = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println(ColoreConsole.errore("Numero non valido."));
            return;
        }

        if (numero == 0) return;

        if (numero < 1 || numero > miePrenotazioni.size()) {
            System.out.println(ColoreConsole.errore("Numero non valido."));
            return;
        }

        // Chiediamo conferma prima di cancellare per evitare cancellazioni accidentali
        System.out.print("Sei sicuro di voler cancellare la prenotazione? (s/n): ");
        String conferma = scanner.nextLine().trim().toLowerCase();
        if (!conferma.equals("s")) {
            System.out.println("Cancellazione annullata.");
            return;
        }

        // eliminaPrenotazione() controlla automaticamente che la data sia futura
        boolean riuscita = prenotazioneManager.eliminaPrenotazione(
                miePrenotazioni.get(numero - 1).getCodice());

        if (riuscita) {
            System.out.println(ColoreConsole.successo("Prenotazione cancellata con successo!"));
        }
    }

    // ----------------------------------------------------------
    // METODI DI SUPPORTO
    // ----------------------------------------------------------

    /**
     * Stampa una lista di prenotazioni a schermo
     * Per ogni prenotazione mostra: numero, codice, film, data, biglietti e totale
     *
     * SimpleDateFormat formatta la data in modo leggibile:
     * "dd/MM/yyyy HH:mm" significa giorno/mese/anno ore:minuti
     * Es. "2027-12-30 10:30:00" diventa "30/12/2027 10:30"
     *
     * @param lista lista di prenotazioni da stampare
     */
    private void stampaListaPrenotazioni(ArrayList<Prenotazione> lista) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (int i = 0; i < lista.size(); i++) {
            Prenotazione p = lista.get(i);

            System.out.println("\n--- Prenotazione " + (i + 1) + " ---");
            System.out.println("Codice:    " + p.getCodice());
            System.out.println("Film:      " + p.getProiezione().getFilm().getTitolo());
            System.out.println("Data:      " + sdf.format(p.getProiezione().getDataOra()));
            System.out.println("Biglietti: " + p.getNumeroBiglietti());

            // printf con "%.2f" stampa il numero con 2 decimali
            // Es. 8.5 diventa "8.50"
            System.out.printf("Totale:    %.2f euro%n", p.getCostoTotale());
        }
    }
}