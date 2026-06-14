/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax.Handlers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import cinemax.Managers.PrenotazioneManager;
import cinemax.Managers.UtenteManager;
import cinemax.Models.Prenotazione;

/**
 * Gestisce il menu e le operazioni disponibili per l'utente con ruolo Bigliettaio.
 *
 * Permette di visualizzare le prenotazioni della data odierna
 * e di cercare prenotazioni tramite codice, nome e cognome del cliente,
 * titolo del film o intervallo di date.
 */
public class BigliettaioMenuHandler {

	private Scanner scanner;
	private UtenteManager utenteManager;
	private PrenotazioneManager prenotazioneManager;

	/**
	 * Costruttore della classe BigliettaioMenuHandler.
	 *
	 * @param scanner scanner utilizzato per leggere l'input dell'utente
	 * @param utenteManager manager per la gestione degli utenti
	 * @param prenotazioneManager manager per la gestione delle prenotazioni
	 */
	public BigliettaioMenuHandler(Scanner scanner, UtenteManager utenteManager,
	                              PrenotazioneManager prenotazioneManager) {
		this.scanner = scanner;
		this.utenteManager = utenteManager;
		this.prenotazioneManager = prenotazioneManager;
	}

	/**
	 * Mostra il menu principale del bigliettaio e gestisce le scelte
	 */
	public void mostraMenu() {
		while (true) {
			System.out.println("\n=== MENU BIGLIETTAIO ===");
			System.out.println("1. Visualizza prenotazioni di oggi");
			System.out.println("2. Cerca una prenotazione");
			System.out.println("0. Logout");
			System.out.print("Scelta: ");

			String scelta = scanner.nextLine();

			switch (scelta) {
				case "1":
					visualizzaPrenotazioniOggi();
					break;
				case "2":
					cercaPrenotazione();
					break;
				case "0":
					return;
				default:
					System.out.println("Scelta non valida.");
			}
		}
	}

	// -------------------------------------------------------------------------
	// VISUALIZZA PRENOTAZIONI DI OGGI
	// -------------------------------------------------------------------------

	/**
	 * Mostra tutte le prenotazioni per le proiezioni della data odierna
	 */
	private void visualizzaPrenotazioniOggi() {

		// Prendiamo tutte le prenotazioni del sistema
		ArrayList<Prenotazione> tutte = prenotazioneManager.getTutteLePrenotazioni();

		// SimpleDateFormat ci serve per confrontare solo la data (senza l'ora)
		// "dd/MM/yyyy" significa che confrontiamo solo giorno/mese/anno
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String oggi = sdf.format(new Date());

		// Lista temporanea dove raccogliamo le prenotazioni di oggi
		ArrayList<Prenotazione> prenotazioniOggi = new ArrayList<>();

		for (int i = 0; i < tutte.size(); i++) {
			Prenotazione p = tutte.get(i);
			String dataProiezione = sdf.format(p.getProiezione().getDataOra());

			// Controlliamo se la data della proiezione è oggi
			if (dataProiezione.equals(oggi)) {
				prenotazioniOggi.add(p);
			}
		}

		if (prenotazioniOggi.isEmpty()) {
			System.out.println("\nNessuna prenotazione per oggi.");
			return;
		}

		System.out.println("\n=== PRENOTAZIONI DI OGGI ===");
		stampaListaPrenotazioni(prenotazioniOggi);
	}

	// -------------------------------------------------------------------------
	// CERCA PRENOTAZIONE
	// -------------------------------------------------------------------------

	/**
	 * Mostra un menu per scegliere il criterio di ricerca
	 * e poi mostra i risultati
	 */
	private void cercaPrenotazione() {

		System.out.println("\n=== CERCA PRENOTAZIONE ===");
		System.out.println("1. Per codice prenotazione");
		System.out.println("2. Per nome e cognome del cliente");
		System.out.println("3. Per titolo del film");
		System.out.println("4. Per intervallo di date");
		System.out.println("0. Torna indietro");
		System.out.print("Scelta: ");

		String scelta = scanner.nextLine();

		switch (scelta) {
			case "1":
				cercaPerCodice();
				break;
			case "2":
				cercaPerNomeCognome();
				break;
			case "3":
				cercaPerTitolo();
				break;
			case "4":
				cercaPerDate();
				break;
			case "0":
				return;
			default:
				System.out.println("Scelta non valida.");
		}
	}

	/**
	 * Cerca una prenotazione per codice univoco
	 */
	private void cercaPerCodice() {

		System.out.print("Inserisci il codice prenotazione: ");
		String codice = scanner.nextLine().trim();

		ArrayList<Prenotazione> tutte = prenotazioneManager.getTutteLePrenotazioni();
		ArrayList<Prenotazione> risultati = new ArrayList<>();

		for (int i = 0; i < tutte.size(); i++) {
			if (tutte.get(i).getCodice().equals(codice)) {
				risultati.add(tutte.get(i));
			}
		}

		mostraRisultati(risultati);
	}

	/**
	 * Cerca prenotazioni per nome e cognome del cliente.
	 */
	private void cercaPerNomeCognome() {

		System.out.print("Inserisci nome: ");
		String nome = scanner.nextLine().trim().toLowerCase();
		System.out.print("Inserisci cognome: ");
		String cognome = scanner.nextLine().trim().toLowerCase();

		ArrayList<Prenotazione> tutte = prenotazioneManager.getTutteLePrenotazioni();
		ArrayList<Prenotazione> risultati = new ArrayList<>();

		for (int i = 0; i < tutte.size(); i++) {
			Prenotazione p = tutte.get(i);
			String nomeUtente = p.getUtente().getNome().toLowerCase();
			String cognomeUtente = p.getUtente().getCognome().toLowerCase();

			// Controlliamo che nome e cognome corrispondano
			if (nomeUtente.contains(nome) && cognomeUtente.contains(cognome)) {
				risultati.add(p);
			}
		}

		mostraRisultati(risultati);
	}

	/**
	 * Cerca prenotazioni per titolo del film anche parziale
	 */
	private void cercaPerTitolo() {

		System.out.print("Inserisci il titolo del film (anche parziale): ");
		String titolo = scanner.nextLine().trim().toLowerCase();

		ArrayList<Prenotazione> tutte = prenotazioneManager.getTutteLePrenotazioni();
		ArrayList<Prenotazione> risultati = new ArrayList<>();

		for (int i = 0; i < tutte.size(); i++) {
			Prenotazione p = tutte.get(i);
			String titoloFilm = p.getProiezione().getFilm().getTitolo().toLowerCase();

			// contains() controlla se il titolo contiene la stringa cercata
			if (titoloFilm.contains(titolo)) {
				risultati.add(p);
			}
		}

		mostraRisultati(risultati);
	}

	/**
	 * Cerca prenotazioni per intervallo di date
	 * L'utente inserisce una data di inizio e una di fine
	 */
	private void cercaPerDate() {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// Leggiamo la data di inizio
		Date dataInizio = null;
		while (dataInizio == null) {
			System.out.print("Data inizio (dd/MM/yyyy, invio per nessun limite): ");
			String input = scanner.nextLine().trim();
			if (input.isEmpty()) break;
			try {
				dataInizio = sdf.parse(input);
			} catch (Exception e) {
				System.out.println("Formato data non valido, riprova.");
			}
		}

		// Leggiamo la data di fine
		Date dataFine = null;
		while (dataFine == null) {
			System.out.print("Data fine (dd/MM/yyyy, invio per nessun limite): ");
			String input = scanner.nextLine().trim();
			if (input.isEmpty()) break;
			try {
				dataFine = sdf.parse(input);
			} catch (Exception e) {
				System.out.println("Formato data non valido, riprova.");
			}
		}

		ArrayList<Prenotazione> tutte = prenotazioneManager.getTutteLePrenotazioni();
		ArrayList<Prenotazione> risultati = new ArrayList<>();

		for (int i = 0; i < tutte.size(); i++) {
			Prenotazione p = tutte.get(i);
			Date dataProiezione = p.getProiezione().getDataOra();

			// Controlliamo che la data sia nell'intervallo
			if (dataInizio != null && dataProiezione.before(dataInizio)) continue;
			if (dataFine != null && dataProiezione.after(dataFine)) continue;

			risultati.add(p);
		}

		mostraRisultati(risultati);
	}

	// -------------------------------------------------------------------------
	// METODI DI SUPPORTO
	// -------------------------------------------------------------------------

	/**
	 * Mostra i risultati della ricerca
	 * Se ci sono risultati li stampa
	 * Altrimenti avvisa che non ce ne sono
	 *
	 * @param risultati lista di prenotazioni trovate
	 */
	private void mostraRisultati(ArrayList<Prenotazione> risultati) {

		if (risultati.isEmpty()) {
			System.out.println("\nNessuna prenotazione trovata.");
			return;
		}

		System.out.println("\n=== RISULTATI ===");
		stampaListaPrenotazioni(risultati);
	}

	/**
	 * Stampa una lista di prenotazioni a schermo
	 * Per ogni prenotazione mostra: codice, cliente, film, data, biglietti, totale
	 *
	 * @param lista lista di prenotazioni da stampare
	 */
	private void stampaListaPrenotazioni(ArrayList<Prenotazione> lista) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		for (int i = 0; i < lista.size(); i++) {
			Prenotazione p = lista.get(i);

			System.out.println("\n--- Prenotazione " + (i + 1) + " ---");
			System.out.println("Codice:    " + p.getCodice());
			System.out.println("Cliente:   " + p.getUtente().getNome() + " " + p.getUtente().getCognome());
			System.out.println("Film:      " + p.getProiezione().getFilm().getTitolo());
			System.out.println("Data:      " + sdf.format(p.getProiezione().getDataOra()));
			System.out.println("Biglietti: " + p.getNumeroBiglietti());
			System.out.printf("Totale:    %.2f euro%n", p.getCostoTotale());
		}
	}
}