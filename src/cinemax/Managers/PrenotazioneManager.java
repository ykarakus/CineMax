package cinemax.Managers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cinemax.Helpers.CSVReader;
import cinemax.Models.Prenotazione;
import cinemax.Models.Proiezione;
import cinemax.Models.Utente;

/**
 * Gestisce le prenotazioni del sistema CineMax.
 *
 * Si occupa di:
 * - leggere e scrivere il file CSV delle prenotazioni
 * - creare nuove prenotazioni
 * - visualizzare le prenotazioni dell'utente loggato
 * - modificare una prenotazione esistente
 * - eliminare una prenotazione esistente
 */
public class PrenotazioneManager {

	/** Percorso del file CSV delle prenotazioni */
	private static final String FILE_PRENOTAZIONI = "data/prenotazioni.csv";

	/** Formato data usato nel CSV */
	private static final String FORMATO_DATA = "yyyy-MM-dd HH:mm:ss";

	private UtenteManager utenteManager;
	private ProiezioneManager proiezioneManager;

	/**
	 * Lista delle prenotazioni in memoria.
	 * Le modifiche vengono tenute qui temporaneamente
	 * e salvate su file solo quando si chiama salvaPrenotazioni().
	 */
	private ArrayList<Prenotazione> prenotazioni;

	/**
	 * Costruttore: inizializza la lista e carica le prenotazioni dal file CSV.
	 *
	 * @param utenteManager manager degli utenti
	 * @param proiezioneManager manager delle proiezioni
	 */
	public PrenotazioneManager(UtenteManager utenteManager, ProiezioneManager proiezioneManager) {
		this.utenteManager = utenteManager;
		this.proiezioneManager = proiezioneManager;
		this.prenotazioni = new ArrayList<>();
		caricaPrenotazioni();
	}

	// -------------------------------------------------------------------------
	// LETTURA / SCRITTURA FILE
	// -------------------------------------------------------------------------

	/**
	 * Legge il file CSV delle prenotazioni e riempie la lista.
	 * Il CSV usa ";" come separatore e ha questa intestazione:
	 * codice;username;titolo_film;data_ora_proiezione;numero_biglietti
	 *
	 * Se il file non esiste ancora non è un errore —
	 * verrà creato automaticamente alla prima prenotazione.
	 */
	private void caricaPrenotazioni() {

		CSVReader lettoreCSV = new CSVReader(";", true);

		try {
			// readFile() legge il file riga per riga e per ogni riga
			// chiama la funzione lambda che converte i valori in un oggetto Prenotazione
			List<Prenotazione> lette = lettoreCSV.readFile(FILE_PRENOTAZIONI, valori -> {

				// Controlliamo che la riga abbia almeno 5 colonne
				if (valori.size() < 5) return null;

				String codice             = valori.get(0);
				String username           = valori.get(1);
				String titoloFilm         = valori.get(2);
				String dataOraStr         = valori.get(3);
				String numeroBigliettiStr = valori.get(4);

				// Cerchiamo l'utente corrispondente allo username salvato nel file
				Utente utente = trovaUtentePerUsername(username);
				if (utente == null) return null;

				// Cerchiamo la proiezione corrispondente a titolo e data
				Proiezione proiezione = proiezioneManager.trovaProiezione(titoloFilm,
						CSVReader.parseDate(dataOraStr, FORMATO_DATA));
				if (proiezione == null) return null;

				// Integer.parseInt() converte la stringa "3" nel numero intero 3
				// Il try-catch gestisce il caso in cui il valore non sia un numero valido
				int numeroBiglietti;
				try {
					numeroBiglietti = Integer.parseInt(numeroBigliettiStr.trim());
				} catch (NumberFormatException e) {
					return null;
				}

				// Creiamo la prenotazione e impostiamo il codice salvato nel file
				Prenotazione prenotazione = new Prenotazione(utente, proiezione, numeroBiglietti);
				prenotazione.setCodice(codice);
				return prenotazione;
			});

			// Aggiungiamo alla lista solo le prenotazioni valide (saltiamo le righe null)
			for (int i = 0; i < lette.size(); i++) {
				if (lette.get(i) != null) {
					prenotazioni.add(lette.get(i));
				}
			}

			System.out.println("Prenotazioni caricate: " + prenotazioni.size());

		} catch (IOException e) {
			// Se il file non esiste ancora non è un errore
			System.out.println("Nessun file prenotazioni trovato, verrà creato alla prima prenotazione.");
		}
	}

	/**
	 * Salva tutte le prenotazioni in memoria nel file CSV.
	 * Sovrascrive il file esistente con i dati aggiornati.
	 *
	 * Questo metodo è public perché il salvataggio avviene
	 * solo quando necessario. es. al logout, non ad ogni operazione.
	 */
	public void salvaPrenotazioni() {

		try {
			BufferedWriter scrittore = new BufferedWriter(new FileWriter(FILE_PRENOTAZIONI));

			// Scriviamo l'intestazione del file
			scrittore.write("codice;username;titolo_film;data_ora_proiezione;numero_biglietti");
			scrittore.newLine();

			SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_DATA);

			// Scriviamo una prenotazione per riga
			for (int i = 0; i < prenotazioni.size(); i++) {
				Prenotazione p = prenotazioni.get(i);

				String riga = p.getCodice() + ";" +
						p.getUtente().getUsername() + ";" +
						p.getProiezione().getFilm().getTitolo() + ";" +
						sdf.format(p.getProiezione().getDataOra()) + ";" +
						p.getNumeroBiglietti();

				scrittore.write(riga);
				scrittore.newLine();
			}

			scrittore.close();

		} catch (IOException e) {
			System.err.println("Errore durante il salvataggio delle prenotazioni: " + e.getMessage());
		}
	}

	// -------------------------------------------------------------------------
	// METODI DI SUPPORTO
	// -------------------------------------------------------------------------

	/**
	 * Cerca un utente nella lista per username.
	 *
	 * @param username username da cercare
	 * @return utente trovato oppure null se non esiste
	 */
	private Utente trovaUtentePerUsername(String username) {
		for (int i = 0; i < utenteManager.getUtenti().size(); i++) {
			if (utenteManager.getUtenti().get(i).getUsername().equals(username)) {
				return utenteManager.getUtenti().get(i);
			}
		}
		return null;
	}


	// -------------------------------------------------------------------------
	// OPERAZIONI SULLE PRENOTAZIONI
	// -------------------------------------------------------------------------

	/**
	 * Crea una nuova prenotazione per l'utente loggato.
	 * Controlla che:
	 * - ci sia un utente loggato
	 * - ci siano abbastanza posti disponibili
	 * - la data della proiezione sia futura
	 *
	 * Le modifiche vengono tenute in memoria: chiamare salvaPrenotazioni()
	 * per salvare su file.
	 *
	 * @param proiezione proiezione da prenotare
	 * @param numeroBiglietti numero di biglietti richiesti
	 * @return true se la prenotazione è andata a buon fine, false altrimenti
	 */
	public boolean creaPrenotazione(Proiezione proiezione, int numeroBiglietti) {

		// Prendiamo l'utente attualmente loggato
		Utente utente = utenteManager.getUtenteCorrente();
		if (utente == null) {
			System.out.println("Devi essere loggato per prenotare.");
			return false;
		}

		// Controlliamo che ci siano abbastanza posti disponibili
		if (proiezione.getNumeroPostiDisponibili() < numeroBiglietti) {
			System.out.println("Posti non sufficienti. Disponibili: " + proiezione.getNumeroPostiDisponibili());
			return false;
		}

		// new Date() restituisce la data e ora attuali.
		// before() controlla se la data della proiezione è prima di adesso.
		if (proiezione.getDataOra().before(new Date())) {
			System.out.println("Non puoi prenotare una proiezione già passata.");
			return false;
		}

		// Creiamo la prenotazione — il codice univoco viene generato
		// automaticamente nel costruttore di Prenotazione
		Prenotazione prenotazione = new Prenotazione(utente, proiezione, numeroBiglietti);

		// Aggiorniamo il numero di posti occupati nella proiezione
		proiezione.setPostiPrenotati(proiezione.getPostiPrenotati() + numeroBiglietti);

		// Aggiungiamo la prenotazione alla lista in memoria
		// (non salviamo su file qui — si chiama salvaPrenotazioni() separatamente)
		prenotazioni.add(prenotazione);

		System.out.println("Prenotazione creata! Codice: " + prenotazione.getCodice());
		return true;
	}

	/**
	 * Restituisce la lista delle prenotazioni dell'utente attualmente loggato.
	 *
	 * @return lista di prenotazioni dell'utente corrente
	 */
	public ArrayList<Prenotazione> visualizzaPrenotazioni() {

		Utente utente = utenteManager.getUtenteCorrente();
		ArrayList<Prenotazione> miePrenotazioni = new ArrayList<>();

		for (int i = 0; i < prenotazioni.size(); i++) {
			if (prenotazioni.get(i).getUtente().getUsername().equals(utente.getUsername())) {
				miePrenotazioni.add(prenotazioni.get(i));
			}
		}

		return miePrenotazioni;
	}

	/**
	 * Modifica una prenotazione cambiando la proiezione.
	 * La modifica è consentita solo se sia la vecchia che la nuova data sono future.
	 *
	 * Le modifiche vengono tenute in memoria: chiamare salvaPrenotazioni()
	 * per salvare su file.
	 *
	 * @param codice codice della prenotazione da modificare
	 * @param nuovaProiezione nuova proiezione scelta
	 * @return true se la modifica è andata a buon fine, false altrimenti
	 */
	public boolean modificaPrenotazione(String codice, Proiezione nuovaProiezione) {

		Prenotazione prenotazione = trovaPerCodice(codice);
		if (prenotazione == null) {
			System.out.println("Prenotazione non trovata.");
			return false;
		}

		// Controlliamo che la vecchia proiezione sia ancora futura
		if (prenotazione.getProiezione().getDataOra().before(new Date())) {
			System.out.println("Non puoi modificare una prenotazione per una proiezione già passata.");
			return false;
		}

		// Controlliamo che la nuova proiezione sia futura
		if (nuovaProiezione.getDataOra().before(new Date())) {
			System.out.println("Non puoi spostare la prenotazione su una proiezione già passata.");
			return false;
		}

		// Controlliamo che ci siano posti nella nuova proiezione
		if (nuovaProiezione.getNumeroPostiDisponibili() < prenotazione.getNumeroBiglietti()) {
			System.out.println("Posti non sufficienti. Disponibili: " + nuovaProiezione.getNumeroPostiDisponibili());
			return false;
		}

		// Liberiamo i posti nella vecchia proiezione
		prenotazione.getProiezione().setPostiPrenotati(
				prenotazione.getProiezione().getPostiPrenotati() - prenotazione.getNumeroBiglietti());

		// Occupiamo i posti nella nuova proiezione
		nuovaProiezione.setPostiPrenotati(
				nuovaProiezione.getPostiPrenotati() + prenotazione.getNumeroBiglietti());

		// Aggiorniamo la proiezione nella prenotazione
		prenotazione.setProiezione(nuovaProiezione);

		// (non salviamo su file qui — si chiama salvaPrenotazioni() separatamente)
		System.out.println("Prenotazione modificata con successo!");
		return true;
	}

	/**
	 * Elimina una prenotazione esistente.
	 * L'eliminazione è consentita solo se la data della proiezione è futura.
	 *
	 * Le modifiche vengono tenute in memoria: chiamare salvaPrenotazioni()
	 * per salvare su file.
	 *
	 * @param codice codice della prenotazione da eliminare
	 * @return true se l'eliminazione è andata a buon fine, false altrimenti
	 */
	public boolean eliminaPrenotazione(String codice) {

		Prenotazione prenotazione = trovaPerCodice(codice);
		if (prenotazione == null) {
			System.out.println("Prenotazione non trovata.");
			return false;
		}

		// Controlliamo che la data sia futura
		if (prenotazione.getProiezione().getDataOra().before(new Date())) {
			System.out.println("Non puoi cancellare una prenotazione per una proiezione già passata.");
			return false;
		}

		// Liberiamo i posti nella proiezione
		prenotazione.getProiezione().setPostiPrenotati(
				prenotazione.getProiezione().getPostiPrenotati() - prenotazione.getNumeroBiglietti());

		// Rimuoviamo la prenotazione dalla lista
		prenotazioni.remove(prenotazione);

		// (non salviamo su file qui — si chiama salvaPrenotazioni() separatamente)
		System.out.println("Prenotazione eliminata con successo!");
		return true;
	}

	/**
	 * Cerca una prenotazione nella lista per codice.
	 *
	 * @param codice codice da cercare
	 * @return prenotazione trovata, oppure null se non esiste
	 */
	private Prenotazione trovaPerCodice(String codice) {
		for (int i = 0; i < prenotazioni.size(); i++) {
			if (prenotazioni.get(i).getCodice().equals(codice)) {
				return prenotazioni.get(i);
			}
		}
		return null;
	}

	/**
	 * Restituisce tutte le prenotazioni nel sistema.
	 * Usato dai bigliettai per cercare prenotazioni.
	 *
	 * @return lista di tutte le prenotazioni
	 */
	public ArrayList<Prenotazione> getTutteLePrenotazioni() {
		return prenotazioni;
	}
}