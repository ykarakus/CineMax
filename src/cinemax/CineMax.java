/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax;


import java.util.Scanner;
import cinemax.Handlers.AuthHandler;
import cinemax.Handlers.BigliettaioMenuHandler;
import cinemax.Handlers.ClienteMenuHandler;
import cinemax.Handlers.GuestMenuHandler;
import cinemax.Handlers.ProiezionistaMenuHandler;
import cinemax.Managers.*;
/**
 * Classe principale dell'applicazione CineMax.
 *
 * Contiene il metodo main e gestisce il menu principale
 * da cui l'utente può effettuare login, registrazione,
 * accesso come guest o chiusura del programma.
 */
public class CineMax {

	private Scanner scanner = new Scanner(System.in);

	// Managers (each manages its own data)
	private UtenteManager utenteManager = new UtenteManager();
	private ProiezioneManager proiezioneManager = new ProiezioneManager();
	private PrenotazioneManager prenotazioneManager = new PrenotazioneManager(utenteManager, proiezioneManager);

	// Menu handlers (receive managers they need)
	private ClienteMenuHandler clienteMenuHandler;
	private BigliettaioMenuHandler bigliettaioMenuHandler;
	private ProiezionistaMenuHandler proiezionistaMenuHandler;
	private GuestMenuHandler guestMenuHandler;

	/**
	 * Costruttore della classe CineMax.
	 *
	 * Inizializza gli handler dei menu e collega i manager necessari
	 * alla gestione di utenti, proiezioni e prenotazioni.
	 */
	public CineMax() {
		this.clienteMenuHandler = new ClienteMenuHandler(scanner, utenteManager, prenotazioneManager,
				proiezioneManager);
		this.proiezionistaMenuHandler = new ProiezionistaMenuHandler(scanner, utenteManager, prenotazioneManager,
				proiezioneManager);
		this.bigliettaioMenuHandler = new BigliettaioMenuHandler(scanner, utenteManager, prenotazioneManager);
		this.guestMenuHandler = new GuestMenuHandler(scanner, proiezioneManager);
	}

	/**
	 * Metodo principale di avvio dell'applicazione.
	 *
	 * @param args argomenti passati da linea di comando
	 */
	public static void main(String[] args) {

		new CineMax().start();

	}

	private void start() {
		while (true) {
			mainMenu();
		}
	}

	private void mainMenu() {

		System.out.println("\nBenvenuto in CinemaXA!\n");

		AuthHandler authHandler = new AuthHandler(scanner, utenteManager, clienteMenuHandler, proiezionistaMenuHandler,
				bigliettaioMenuHandler);

		System.out.println("\nMENU PRINCIPALE\nInserisci il numero in base a come vuoi procedere:");
		System.out.println("1. Login.");
		System.out.println("2. Registrati.");
		System.out.println("3. Guest (login non richiesto).");
		System.out.println("0. Chiudi.");
		System.out.print("Scelta: ");

		String scelta = scanner.nextLine();

		switch (scelta) {
		case "1":
			authHandler.gestisciLogin();
			break;
		case "2":
			authHandler.gestisciRegistrazione();
			break;
		case "3":
			guestMenuHandler.mostraMenu();
			break;
		case "0":
			System.out.println("Arrivederci!");
			System.exit(0);
			break;
		default:
			System.out.println("Scelta invalida!");
		}
	}
}