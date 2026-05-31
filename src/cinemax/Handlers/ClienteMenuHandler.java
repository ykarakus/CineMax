package cinemax.Handlers;

import java.util.Scanner;

import cinemax.Managers.*;

public class ClienteMenuHandler {
	
	
	private Scanner scanner;
    private UtenteManager utenteManager;
    private PrenotazioneManager prenotazioneManager;
    private ProiezioneManager proiezioneManager;

    public ClienteMenuHandler(Scanner scanner, UtenteManager utenteManager, PrenotazioneManager prenotazioneManager,
            ProiezioneManager proiezioneManager) {
        this.scanner = scanner;
        this.utenteManager = utenteManager;
        this.prenotazioneManager = prenotazioneManager;
        this.proiezioneManager = proiezioneManager;
    }
    
	public void mostraMenu() {
	}

}
