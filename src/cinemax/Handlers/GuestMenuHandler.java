package cinemax.Handlers;

import java.util.Scanner;

import cinemax.Managers.ProiezioneManager;

public class GuestMenuHandler {
	
	
	private Scanner scanner;
	private ProiezioneManager proiezioneManager;

	public GuestMenuHandler(Scanner scanner, ProiezioneManager proiezioneManager) {
		this.scanner = scanner;
		this.proiezioneManager = proiezioneManager;
	}
	
	public void mostraMenu() {
		
		while (true) {
			System.out.println("\n=== MENU GUEST ===");
			System.out.println("1. Cerca proiezioni");
			System.out.println("0. Torna al menu principale");
			System.out.print("Scelta: ");

			String scelta = scanner.nextLine();
			switch (scelta) {
				case "1":
					gestisciRicercaProiezioni();
					break;
				case "0":
					return;
				default:
					System.out.println("Scelta non valida.");
			}
		}
	}
	
	private void gestisciRicercaProiezioni() {
	
	}

}
