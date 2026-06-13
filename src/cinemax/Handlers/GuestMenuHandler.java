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
		CriteriRicercaProiezione criteri = RicercaProiezioneConsoleHelper.acquisisciCriteriRicerca(scanner);
		List<Proiezione> risultati = proiezioneManager.cercaProiezione(criteri);

		if (risultati.isEmpty()) {
			System.out.println("Nessuna proiezione trovata con i criteri selezionati.");
			return;
		}

		RicercaProiezioneConsoleHelper.mostraRisultatiRicerca(risultati);
		selezionaEVisualizzaProiezione(risultati);
	}

	private void selezionaEVisualizzaProiezione(List<Proiezione> risultati) {
		Proiezione selezionata = RicercaProiezioneConsoleHelper.selezionaProiezione(
				scanner,
				risultati,
				"\nSeleziona una proiezione per dettagli (0 per tornare): ");
		if (selezionata != null) {
			proiezioneManager.visualizzaProiezione(selezionata);
		}
	}

}
