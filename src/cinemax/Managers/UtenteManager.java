/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax.Managers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cinemax.Helpers.CSVReader;
import cinemax.Models.Ruolo;
import cinemax.Models.Utente;

/**
 * Gestisce gli utenti del sistema CineMax.
 *
 * Si occupa di:
 * - leggere e scrivere il file CSV degli utenti
 * - effettuare il login verificando username e password (cifrata con SHA-256)
 * - registrare nuovi clienti
 * - tenere traccia dell'utente attualmente loggato
 */
public class UtenteManager {

	/** Percorso del file CSV degli utenti */
	private static final String FILE_UTENTI = "data/utenti.csv";

	/** Formato data usato nel CSV */
	private static final String FORMATO_DATA = "yyyy-MM-dd";

	/** Lista di tutti gli utenti caricati dal file */
	public ArrayList<Utente> utenti;

	/** Utente attualmente loggato (null se nessuno è loggato) */
	private Utente utenteCorrente = null;

	/**
	 * Costruttore: crea la lista vuota e carica gli utenti dal file CSV.
	 */
	public UtenteManager() {
		utenti = new ArrayList<>();
		caricaUtenti();
	}

	// -------------------------------------------------------------------------
	// LETTURA / SCRITTURA FILE
	// -------------------------------------------------------------------------

	/**
	 * Legge il file CSV degli utenti e popola la lista utenti.
	 * Il CSV usa ";" come separatore e ha questa intestazione:
	 * nome;cognome;username;password;data_nascita;domicilio;ruolo
	 *
	 * Le password nel file sono già salvate come hash SHA-256.
	 */
	private void caricaUtenti() {

		// Usiamo ";" come separatore perché il file utenti.csv usa il punto e virgola
		CSVReader lettoreCSV = new CSVReader(";", true);

		try {
			// Leggiamo tutte le righe del file come lista di valori
			// Il CSVReader restituisce una lista di oggetti Utente
			List<Utente> letti = lettoreCSV.readFile(FILE_UTENTI, valori -> {

				// Controlliamo che la riga abbia almeno 7 colonne
				if (valori.size() < 7) return null;

				String nome           = valori.get(0);
				String cognome        = valori.get(1);
				String username       = valori.get(2);
				String password       = valori.get(3); // già salvata come hash nel file
				String dataNascitaStr = valori.get(4);
				String domicilio      = valori.get(5);
				String ruoloStr       = valori.get(6);

				// Convertiamo la stringa della data in oggetto Date (campo facoltativo)
				Date dataNascita = null;
				if (dataNascitaStr != null && !dataNascitaStr.trim().isEmpty()) {
					dataNascita = CSVReader.parseDate(dataNascitaStr, FORMATO_DATA);
				}

				// Convertiamo la stringa del ruolo nell'enum Ruolo
				// Es. "CLIENTE" diventa Ruolo.CLIENTE
				Ruolo ruolo;
				try {
					ruolo = Ruolo.valueOf(ruoloStr.trim().toUpperCase());
				} catch (IllegalArgumentException e) {
					System.err.println("Ruolo non riconosciuto per l'utente " + username + ": " + ruoloStr);
					return null;
				}

				return new Utente(nome, cognome, username, password, dataNascita, domicilio, ruolo);
			});

			// Aggiungiamo alla lista solo gli utenti validi (saltiamo le righe null)
			for (int i = 0; i < letti.size(); i++) {
				if (letti.get(i) != null) {
					utenti.add(letti.get(i));
				}
			}

			System.out.println("Utenti caricati correttamente: " + utenti.size());

		} catch (IOException e) {
			System.err.println("Errore durante la lettura del file utenti: " + e.getMessage());
		}
	}

	/**
	 * Salva tutti gli utenti presenti in memoria nel file CSV.
	 * Sovrascrive il file esistente con i dati aggiornati.
	 */
	private void salvaUtenti() {

		try {
			// BufferedWriter è la classe Java per scrivere testo su file
			// FileWriter apre il file, BufferedWriter lo scrive in modo efficiente
			BufferedWriter scrittore = new BufferedWriter(new FileWriter(FILE_UTENTI));

			// Scriviamo prima l'intestazione del file
			scrittore.write("nome;cognome;username;password;data_nascita;domicilio;ruolo");
			scrittore.newLine();

			SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_DATA);

			// Scriviamo un utente per riga
			for (int i = 0; i < utenti.size(); i++) {
				Utente u = utenti.get(i);

				// La data di nascita è facoltativa, potrebbe essere null
				String dataNascita = "";
				if (u.getData_nascita() != null) {
					dataNascita = sdf.format(u.getData_nascita());
				}

				// Costruiamo la riga separando i campi con ";"
				String riga = u.getNome() + ";" +
						u.getCognome() + ";" +
						u.getUsername() + ";" +
						u.getPassword() + ";" +
						dataNascita + ";" +
						u.getDomicilio() + ";" +
						u.getRuolo().name();

				scrittore.write(riga);
				scrittore.newLine();
			}

			scrittore.close();

		} catch (IOException e) {
			System.err.println("Errore durante il salvataggio del file utenti: " + e.getMessage());
		}
	}

	// -------------------------------------------------------------------------
	// CIFRATURA PASSWORD
	// -------------------------------------------------------------------------

	/**
	 * Cifra una password in chiaro usando l'algoritmo SHA-256.
	 *
	 * SHA-256 è un algoritmo che trasforma una stringa in un codice di lunghezza fissa che non può essere decifrato.
	 * Es. "1234" diventa "03ac674216f3e15c761ee1a5e255f067..."
	 *
	 * @param password password in chiaro da cifrare
	 * @return hash SHA-256 della password
	 */
	public static String cifraPassword(String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(password.getBytes());

			// Convertiamo ogni byte in due caratteri esadecimali
			StringBuilder hex = new StringBuilder();
			for (int i = 0; i < hashBytes.length; i++) {
				hex.append(String.format("%02x", hashBytes[i]));
			}
			return hex.toString();

		} catch (NoSuchAlgorithmException e) {
			System.err.println("Errore durante la cifratura della password: " + e.getMessage());
			return password;
		}
	}

	// -------------------------------------------------------------------------
	// LOGIN / LOGOUT / REGISTRAZIONE
	// -------------------------------------------------------------------------

	/**
	 * Effettua il login verificando username e password.
	 * La password inserita viene cifrata e confrontata con quella salvata nel file.
	 *
	 * @param username username dell'utente
	 * @param password password in chiaro inserita dall'utente
	 * @return true se le credenziali sono corrette, false altrimenti
	 */
	public boolean login(String username, String password) {

		// Cifriamo la password inserita per poterla confrontare con quella salvata
		String passwordHash = cifraPassword(password);

		// Scorriamo tutti gli utenti cercando username e password corrispondenti
		for (int i = 0; i < utenti.size(); i++) {
			Utente u = utenti.get(i);
			if (u.getUsername().equals(username) && u.getPassword().equals(passwordHash)) {
				utenteCorrente = u;
				return true;
			}
		}

		// Nessun utente trovato con quelle credenziali
		return false;
	}

	/**
	 * Registra un nuovo utente con ruolo CLIENTE.
	 * Controlla che lo username non sia già in uso prima di procedere.
	 * La password viene cifrata con SHA-256 prima di essere salvata.
	 *
	 * @param nome        nome del nuovo utente
	 * @param cognome     cognome del nuovo utente
	 * @param username    username scelto (deve essere univoco)
	 * @param password    password in chiaro (verrà cifrata automaticamente)
	 * @param dataNascita data di nascita in formato dd/MM/yyyy (può essere vuota)
	 * @param domicilio   luogo di domicilio
	 * @return true se la registrazione è avvenuta con successo, false se lo username è già in uso
	 */
	public boolean registrazione(String nome, String cognome, String username,
	                             String password, String dataNascita, String domicilio) {

		// Verifichiamo che lo username non sia già usato da un altro utente
		for (int i = 0; i < utenti.size(); i++) {
			if (utenti.get(i).getUsername().equalsIgnoreCase(username)) {
				System.out.println("Username già in uso. Scegline un altro.");
				return false;
			}
		}

		// Convertiamo la data di nascita da stringa a Date (campo facoltativo)
		Date data = null;
		if (dataNascita != null && !dataNascita.trim().isEmpty()) {
			try {
				data = new SimpleDateFormat("dd/MM/yyyy").parse(dataNascita.trim());
			} catch (ParseException e) {
				System.out.println("Formato data non valido, il campo verrà lasciato vuoto.");
			}
		}

		// Cifriamo la password prima di salvarla
		String passwordHash = cifraPassword(password);

		// Creiamo il nuovo utente con ruolo CLIENTE
		Utente nuovoUtente = new Utente(nome, cognome, username, passwordHash, data, domicilio, Ruolo.CLIENTE);

		// Aggiungiamo l'utente alla lista in memoria
		utenti.add(nuovoUtente);

		// Salviamo la lista aggiornata sul file CSV
		salvaUtenti();

		// Impostiamo il nuovo utente come utente corrente (login automatico)
		utenteCorrente = nuovoUtente;
		return true;
	}

	/**
	 * Effettua il logout rimuovendo l'utente corrente dalla sessione.
	 */
	public void logout() {
		utenteCorrente = null;
	}

	// -------------------------------------------------------------------------
	// METODI DI ACCESSO (GETTER)
	// -------------------------------------------------------------------------

	/**
	 * Restituisce l'utente attualmente loggato.
	 * @return utente corrente, oppure null se nessuno è loggato
	 */
	public Utente getUtenteCorrente() {

		return utenteCorrente;
	}

	/**
	 * Verifica se un utente è attualmente loggato.
	 * @return true se c'è un utente loggato, false altrimenti
	 */
	public boolean isLoggedIn() {
		return utenteCorrente != null;
	}

	/**
	 * Restituisce la lista completa degli utenti registrati.
	 * @return lista di tutti gli utenti
	 */
	public ArrayList<Utente> getUtenti() {
		return utenti;
	}
}