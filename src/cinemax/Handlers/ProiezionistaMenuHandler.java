package cinemax.Handlers;

import java.util.Scanner;

import cinemax.Managers.PrenotazioneManager;
import cinemax.Managers.ProiezioneManager;
import cinemax.Managers.UtenteManager;

public class ProiezionistaMenuHandler {
	
	private Scanner scanner;
	private UtenteManager utenteManager;
	private PrenotazioneManager prenotazioneManager;
	private ProiezioneManager proiezioneManager;

	public ProiezionistaMenuHandler(Scanner scanner, UtenteManager utenteManager, PrenotazioneManager prenotazioneManager,
			ProiezioneManager proiezioneManager) {
		this.scanner = scanner;
		this.utenteManager = utenteManager;
		this.prenotazioneManager = prenotazioneManager;
		this.proiezioneManager = proiezioneManager;
	}

	public void mostraMenu() {
	}


}
