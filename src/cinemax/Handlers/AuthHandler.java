package cinemax.Handlers;

import java.util.Scanner;

import cinemax.Models.Ruolo;

public class AuthHandler {

	private Scanner input;
	private ClienteMenuHandler clienteMenuHandler;
	private BigliettaioMenuHandler bigliettaioMenuHandler;
	private ProiezionistaMenuHandler proiezionistaMenuHandler;

	public AuthHandler(Scanner scanner, ClienteMenuHandler clienteMenuHandler,
			ProiezionistaMenuHandler proiezionistaMenuHandler, BigliettaioMenuHandler bigliettaioMenuHandler) {
		this.input = scanner;
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

		//TODO: utenteManager.login(username, password));
		visualizzaMenu();

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
    	
    	
    	//TODO: utenteManager.registrazione(nome, cognome, username, password, datanascita, domicilio))
		visualizzaMenu();
	}
	
	private void visualizzaMenu() {		
		
		//TODO: Get role for current user from utentemanger 
		Ruolo ruolo = Ruolo.CLIENTE;

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