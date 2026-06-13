/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax.Helpers;

/**
 * Classe utility per la formattazione del testo nella console utilizzando
 * codici ANSI per i colori. Fornisce metodi statici per visualizzare messaggi
 * colorati in base al loro tipo (successo, errore, avvertimento, ecc.).
 * 
 * 
 */
public final class ColoreConsole {

	// Costanti per i codici ANSI di controllo colore
	private static final String RESET = "\u001B[0m"; // Resetta tutti gli attributi (colore, sfondo, ecc.)
	private static final String RED = "\u001B[31m"; // Colore rosso per errori
	private static final String GREEN = "\u001B[32m"; // Colore verde per successi e benvenuti
	private static final String YELLOW = "\u001B[33m"; // Colore giallo per avvertimenti
	private static final String BLUE = "\u001B[34m"; // Colore blu per header dei risultati
	private static final String PURPLE = "\u001B[35m"; // Colore viola per titoli dei film
	private static final String CYAN = "\u001B[36m"; // Colore ciano per header generici

	/**
	 * Costruttore privato per impedire l'istanziazione della classe. Questa � una
	 * utility class che fornisce solo metodi statici.
	 */
	private ColoreConsole() {
		// Costruttore vuoto e privato per rispettare il pattern Utility Class
	}

	/**
	 * Metodo di utilit� privato che avvolge il testo con i codici ANSI per
	 * applicare un colore e successivamente resettare la formattazione.
	 * 
	 * @param color Il codice ANSI del colore da applicare
	 * @param text  Il testo da colorare
	 * @return Il testo colorato con reset automatico alla fine
	 */
	private static String colora(String color, String text) {
		// Applica il colore prima del testo e resetta dopo
		return color + text + RESET;
	}

	/**
	 * Restituisce il testo formattato con colore verde, tipicamente usato per
	 * indicare operazioni completate con successo.
	 * 
	 * @param text Il messaggio di successo
	 * @return Il testo colorato in verde
	 */
	public static String successo(String text) {
		return colora(GREEN, text);
	}

	/**
	 * Restituisce il testo formattato con colore giallo, tipicamente usato per
	 * avvisare l'utente di situazioni che richiedono attenzione.
	 * 
	 * @param text Il messaggio di avvertimento
	 * @return Il testo colorato in giallo
	 */
	public static String avvertimento(String text) {
		return colora(YELLOW, text);
	}

	/**
	 * Restituisce il testo formattato con colore rosso, tipicamente usato per
	 * segnalare errori o operazioni fallite.
	 * 
	 * @param text Il messaggio di errore
	 * @return Il testo colorato in rosso
	 */
	public static String errore(String text) {
		return colora(RED, text);
	}

	/**
	 * Restituisce il testo formattato con colore verde, usato specificamente per i
	 * messaggi di benvenuto dopo login o registrazione.
	 * 
	 * @param text Il messaggio di benvenuto
	 * @return Il testo colorato in verde
	 */
	public static String benvenuto(String text) {
		return colora(GREEN, text);
	}

	/**
	 * Restituisce il testo formattato con colore ciano, usato per evidenziare
	 * header o titoli di sezione nei menu.
	 * 
	 * @param text Il testo dell'header
	 * @return Il testo colorato in ciano
	 */
	public static String header(String text) {
		return colora(CYAN, text);
	}

	/**
	 * Restituisce il testo formattato con colore blu, usato per evidenziare header
	 * specifici nella visualizzazione dei risultati di ricerca.
	 * 
	 * @param text Il testo dell'header risultati
	 * @return Il testo colorato in blu
	 */
	public static String headerRisultati(String text) {
		return colora(BLUE, text);
	}

	/**
	 * Restituisce il testo formattato con colore viola, usato per evidenziare i
	 * titoli dei film nelle liste e nei dettagli.
	 * 
	 * @param text Il titolo del film
	 * @return Il testo colorato in viola
	 */
	public static String titoloFilm(String text) {
		return colora(PURPLE, text);
	}
}