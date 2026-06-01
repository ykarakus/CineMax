package cinemax;

import java.util.Scanner;

import cinemax.Handlers.AuthHandler;
import cinemax.Handlers.BigliettaioMenuHandler;
import cinemax.Handlers.ClienteMenuHandler;
import cinemax.Handlers.GuestMenuHandler;
import cinemax.Handlers.ProiezionistaMenuHandler;
import cinemax.Managers.PrenotazioneManager;
import cinemax.Managers.ProiezioneManager;
import cinemax.Managers.UtenteManager;

/**
 * Classe principale dell'applicazione CineMax.
 * Contiene il metodo main e gestisce il menu principale.
 */
public class CineMax {

	private Scanner scanner = new Scanner(System.in);

	// Managers
	private UtenteManager utenteManager = new UtenteManager();
	private ProiezioneManager proiezioneManager = new ProiezioneManager();
	private PrenotazioneManager prenotazioneManager = new PrenotazioneManager(utenteManager, proiezioneManager);

	// Menu handlers
	private ClienteMenuHandler clienteMenuHandler;
	private BigliettaioMenuHandler bigliettaioMenuHandler;
	private ProiezionistaMenuHandler proiezionistaMenuHandler;
	private GuestMenuHandler guestMenuHandler;

	/**
	 * Costruttore: inizializza tutti i menu handler.
	 */
	public CineMax() {
		this.clienteMenuHandler = new ClienteMenuHandler(scanner, utenteManager, prenotazioneManager, proiezioneManager);
		this.proiezionistaMenuHandler = new ProiezionistaMenuHandler(scanner, utenteManager, prenotazioneManager, proiezioneManager);
		this.bigliettaioMenuHandler = new BigliettaioMenuHandler(scanner, utenteManager, prenotazioneManager);
		this.guestMenuHandler = new GuestMenuHandler(scanner, proiezioneManager);
	}

	/**
	 * Metodo main: punto di ingresso dell'applicazione.
	 */
	public static void main(String[] args) {
		new CineMax().avvia();
	}

	/**
	 * Avvia il loop principale dell'applicazione.
	 */
	private void avvia() {
		while (true) {
			menuPrincipale();
		}
	}

	/**
	 * Mostra il menu principale e gestisce la scelta dell'utente.
	 */
	private void menuPrincipale() {

		System.out.println("\n=== BENVENUTO IN CINEMAX ===");
		System.out.println("1. Login");
		System.out.println("2. Registrati");
		System.out.println("3. Continua come ospite");
		System.out.println("0. Esci");
		System.out.print("Scelta: ");

		String scelta = scanner.nextLine();

		// Creiamo AuthHandler qui dentro così è sempre fresco
		AuthHandler authHandler = new AuthHandler(scanner, utenteManager, clienteMenuHandler,
				proiezionistaMenuHandler, bigliettaioMenuHandler);

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
				System.out.println("Scelta non valida, riprova.");
		}
	}
}