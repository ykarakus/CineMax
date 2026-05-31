package cinemax.Handlers;

import java.util.Scanner;

import cinemax.Managers.PrenotazioneManager;
import cinemax.Managers.UtenteManager;

public class BigliettaioMenuHandler {
	
	
	private Scanner scanner;
	private UtenteManager utenteManager;
	private PrenotazioneManager prenotazioneManager;

	public BigliettaioMenuHandler(Scanner scanner, UtenteManager utenteManager, PrenotazioneManager prenotazioneManager) {
		this.scanner = scanner;
		this.utenteManager = utenteManager;
		this.prenotazioneManager = prenotazioneManager;
	}
	
	public void mostraMenu() {
	}

}
