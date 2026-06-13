/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.Handlers;

import java.util.List;
import java.util.Scanner;

import cinemax.Managers.ProiezioneManager;
import cinemax.Models.Proiezione;
import cinemax.ViewModels.CriteriRicercaProiezione;
import cinemax.Helpers.RicercaProiezioneConsoleHelper;

/**
 * Gestisce il menu e le interazioni per gli utenti non autenticati (guest).
 * Fornisce funzionalità di ricerca e visualizzazione delle proiezioni.
 */
public class GuestMenuHandler {
    
    private Scanner scanner;
    private ProiezioneManager proiezioneManager;

    /**
     * Costruttore della classe GuestMenuHandler.
     * 
     * @param scanner            Lo scanner per leggere l'input dell'utente
     * @param proiezioneManager  Il manager per la gestione delle proiezioni
     */
    public GuestMenuHandler(Scanner scanner, ProiezioneManager proiezioneManager) {
        this.scanner = scanner;
        this.proiezioneManager = proiezioneManager;
    }
    
    /**
     * Mostra il menu principale per gli utenti guest e gestisce le scelte.
     * Il menu rimane attivo fino a quando l'utente non sceglie di tornare indietro.
     */
    public void mostraMenu() {
        
        while (true) {
            System.out.println("\n=== MENU GUEST ===");
            System.out.println("1. Cerca proiezioni");
            System.out.println("0. Torna al menu principale");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine();
            switch (scelta) {
                case "1":
                    gestisciRicercaProiezioni(); // Avvia il flusso di ricerca proiezioni
                    break;
                case "0":
                    return; // Torna al menu principale
                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }
    
    /**
     * Gestisce l'intero flusso di ricerca delle proiezioni:
     * - Acquisizione dei criteri di ricerca dall'utente
     * - Esecuzione della ricerca tramite ProiezioneManager
     * - Visualizzazione dei risultati
     * - Gestione della selezione di una proiezione per i dettagli
     */
    private void gestisciRicercaProiezioni() {
        // Acquisisce i criteri di ricerca (es. data, titolo film, sala) tramite helper
        CriteriRicercaProiezione criteri = RicercaProiezioneConsoleHelper.acquisisciCriteriRicerca(scanner);
        
        // Esegue la ricerca delle proiezioni che corrispondono ai criteri
        List<Proiezione> risultati = proiezioneManager.cercaProiezione(criteri);

        // Se la lista è vuota, nessuna proiezione soddisfa i criteri
        if (risultati.isEmpty()) {
            System.out.println("Nessuna proiezione trovata con i criteri selezionati.");
            return;
        }

        // Mostra l'elenco delle proiezioni trovate in formato tabellare
        RicercaProiezioneConsoleHelper.mostraRisultatiRicerca(risultati);
        
        // Permette all'utente di selezionare una proiezione e visualizzarne i dettagli
        selezionaEVisualizzaProiezione(risultati);
    }

    /**
     * Permette all'utente di selezionare una proiezione dall'elenco dei risultati
     * e visualizzarne i dettagli completi.
     * 
     * @param risultati La lista delle proiezioni tra cui scegliere (non nulla)
     */
    private void selezionaEVisualizzaProiezione(List<Proiezione> risultati) {
        // Mostra un prompt numerato e chiede all'utente di selezionare una proiezione
        // Se l'utente inserisce 0, il metodo restituisce null e si torna indietro
        Proiezione selezionata = RicercaProiezioneConsoleHelper.selezionaProiezione(
                scanner,
                risultati,
                "\nSeleziona una proiezione per dettagli (0 per tornare): ");
        
        // Se è stata selezionata una proiezione valida, ne mostra i dettagli
        if (selezionata != null) {
            proiezioneManager.visualizzaProiezione(selezionata);
        }
    }
}