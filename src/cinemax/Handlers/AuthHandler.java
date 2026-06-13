/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax.Handlers;

import java.util.Scanner;

import cinemax.Helpers.ColoreConsole;
import cinemax.Managers.UtenteManager;
import cinemax.Models.Ruolo;

/**
 * Gestisce l'autenticazione degli utenti (login e registrazione) e il routing
 * verso i menu specifici in base al ruolo dell'utente autenticato.
 */
public class AuthHandler {

    private Scanner input;
    private ClienteMenuHandler clienteMenuHandler;
    private BigliettaioMenuHandler bigliettaioMenuHandler;
    private UtenteManager utenteManager;
    private ProiezionistaMenuHandler proiezionistaMenuHandler;

    /**
     * Costruttore della classe AuthHandler.
     * 
     * @param scanner                     Lo scanner per leggere l'input dell'utente
     * @param utenteManager               Il manager per la gestione degli utenti
     * @param clienteMenuHandler          Il gestore del menu per il ruolo Cliente
     * @param proiezionistaMenuHandler    Il gestore del menu per il ruolo Proiezionista
     * @param bigliettaioMenuHandler      Il gestore del menu per il ruolo Bigliettaio
     */
    public AuthHandler(Scanner scanner, UtenteManager utenteManager, ClienteMenuHandler clienteMenuHandler,
            ProiezionistaMenuHandler proiezionistaMenuHandler, BigliettaioMenuHandler bigliettaioMenuHandler) {
        this.input = scanner;
        this.utenteManager = utenteManager;
        this.clienteMenuHandler = clienteMenuHandler;
        this.proiezionistaMenuHandler = proiezionistaMenuHandler;
        this.bigliettaioMenuHandler = bigliettaioMenuHandler;
    }

    /**
     * Gestisce il flusso di login per un utente esistente.
     * Richiede username e password, verifica le credenziali e,
     * in caso di successo, mostra il menu corrispondente al ruolo.
     * Al termine del menu, effettua automaticamente il logout.
     */
    public void gestisciLogin() {

        System.out.println("Login.\n");
        System.out.println("Inserisci username: ");
        String username = input.nextLine();
        System.out.println("Inserisci password: ");
        String password = input.nextLine();

        // Tentativo di login: se le credenziali sono valide, l'utente viene impostato come corrente
        if (utenteManager.login(username, password)) {
            System.out.println(ColoreConsole.benvenuto("Benvenuto/a " + username + "!"));
            visualizzaMenu();                // Mostra il menu specifico per il ruolo dell'utente
            utenteManager.logout();          // Clean logout when menu returns
        } else {
            System.out.println("Credenziali non valide!");
        }

    }

    /**
     * Gestisce il flusso di registrazione per un nuovo utente (ruolo CLIENTE di default).
     * Richiede tutti i dati anagrafici necessari (nome, cognome, username, password,
     * data di nascita, domicilio) e crea un nuovo account.
     * Al termine della registrazione, l'utente viene automaticamente loggato e
     * viene mostrato il menu del cliente.
     */
    public void gestisciRegistrazione() {

        System.out.println("Registrazione.\n");
        System.out.println("Inserisci nome: ");
        String nome = input.nextLine();
        System.out.println("Inserisci cognome: ");
        String cognome = input.nextLine();
        System.out.println("Inserisci username: ");
        String username = input.nextLine();
        System.out.println("Inserisci password: ");
        String password = input.nextLine();
        System.out.println("Inserisci datanascita (in formato dd/mm/aaaa): ");
        String datanascita = input.nextLine();
        System.out.println("Inserisci domicilio: ");
        String domicilio = input.nextLine();

        // Tentativo di registrazione: se riuscita, l'utente viene automaticamente loggato
        if (utenteManager.registrazione(nome, cognome, username, password, datanascita, domicilio)) {
            System.out.println(ColoreConsole.benvenuto("Benvenuto/a " + username + "!"));
            visualizzaMenu();                // Mostra il menu del cliente (ruolo di default)
            utenteManager.logout();          // Clean logout when menu returns
        } else {
            System.out.println("Registrazione non andata a buon fine!");
        }
    }

    /**
     * Visualizza il menu appropriato in base al ruolo dell'utente attualmente loggato.
     * Recupera il ruolo dall'utente corrente e invoca il metodo mostraMenu()
     * dell'handler corrispondente.
     * 
     * Ruoli supportati:
     * - CLIENTE: menu per prenotazioni e ricerca proiezioni
     * - BIGLIETTAIO: menu per gestione vendita biglietti
     * - PROIEZIONISTA: menu per gestione proiezioni (CRUD)
     */
    private void visualizzaMenu() {

        // Recupera il ruolo dell'utente attualmente autenticato
        Ruolo ruolo = utenteManager.getUtenteCorrente().getRuolo();

        // Routing verso l'handler specifico in base al ruolo
        switch (ruolo) {
            case CLIENTE:
                clienteMenuHandler.mostraMenu();      // Menu per operazioni cliente
                break;
            case BIGLIETTAIO:
                bigliettaioMenuHandler.mostraMenu();  // Menu per operazioni bigliettaio
                break;
            case PROIEZIONISTA:
                proiezionistaMenuHandler.mostraMenu(); // Menu per operazioni proiezionista
                break;
            default:
                break;  // Caso non previsto (non dovrebbe accadere)

        }
    }

}