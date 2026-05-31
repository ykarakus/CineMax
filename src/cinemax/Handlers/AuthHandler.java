package cinemax.Handlers;

import java.util.Scanner;

import cinemax.Helpers.ConsoleColor;
import cinemax.Managers.UtenteManager;
import cinemax.Models.Ruolo;

public class AuthHandler {

	private Scanner input;
	private ClienteMenuHandler clienteMenuHandler;
	private BigliettaioMenuHandler bigliettaioMenuHandler;
	private UtenteManager utenteManager;
	private ProiezionistaMenuHandler proiezionistaMenuHandler;

	public AuthHandler(Scanner scanner, UtenteManager utenteManager, ClienteMenuHandler clienteMenuHandler,
			ProiezionistaMenuHandler proiezionistaMenuHandler, BigliettaioMenuHandler bigliettaioMenuHandler) {
		this.input = scanner;
		this.utenteManager = utenteManager;
		this.clienteMenuHandler = clienteMenuHandler;
		this.proiezionistaMenuHandler = proiezionistaMenuHandler;
		this.bigliettaioMenuHandler = bigliettaioMenuHandler;
	}

	public void gestisciLogin() {

		System.out.println("Login.\n");
		System.out.println("Inserisci username: ");
		String username = input.nextLine();
		System.out.println("Inserisci password: ");
		String password = input.nextLine();

		if (utenteManager.login(username, password)) {
			System.out.println(ConsoleColor.welcome("Benvenuto/a " + username + "!"));
			visualizzaMenu();
			utenteManager.logout(); // Clean logout when menu returns
		} else {
			System.out.println("Credenziali non valide!");
		}

	}

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

		if (utenteManager.registrazione(nome, cognome, username, password, datanascita, domicilio)) {
			System.out.println(ConsoleColor.welcome("Benvenuto/a " + username + "!"));
			visualizzaMenu();
			utenteManager.logout(); // Clean logout when menu returns
		} else {
			System.out.println("Registrazione non andata a buon fine!");
		}
	}

	private void visualizzaMenu() {

		Ruolo ruolo = utenteManager.getUtenteCorrente().getRuolo();

		switch (ruolo) {
			case CLIENTE:
				clienteMenuHandler.mostraMenu();
				break;
			case BIGLIETTAIO:
				bigliettaioMenuHandler.mostraMenu();
				break;
			case PROIEZIONISTA:
				proiezionistaMenuHandler.mostraMenu();
				break;
			default:
				break;

		}
	}

}