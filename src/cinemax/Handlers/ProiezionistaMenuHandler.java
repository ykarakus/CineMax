/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax.Handlers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import cinemax.Helpers.ColoreConsole;
import cinemax.Managers.PrenotazioneManager;
import cinemax.Managers.ProiezioneManager;
import cinemax.Managers.UtenteManager;
import cinemax.Models.Film;
import cinemax.Models.Proiezione;

/**
 * Gestisce il menu e le operazioni disponibili per l'utente con ruolo Proiezionista.
 * Permette di aggiungere, modificare ed eliminare proiezioni, con validazioni
 * sulle regole di business (es. nessuna sovrapposizione, presenza di prenotazioni).
 */
public class ProiezionistaMenuHandler {
    
    private Scanner scanner;
    private UtenteManager utenteManager;
    private PrenotazioneManager prenotazioneManager;
    private ProiezioneManager proiezioneManager;

    /**
     * Costruttore della classe ProiezionistaMenuHandler.
     * 
     * @param scanner               Lo scanner per leggere l'input dell'utente
     * @param utenteManager         Il manager per la gestione degli utenti
     * @param prenotazioneManager   Il manager per la gestione delle prenotazioni
     * @param proiezioneManager     Il manager per la gestione delle proiezioni
     */
    public ProiezionistaMenuHandler(Scanner scanner, UtenteManager utenteManager,
            PrenotazioneManager prenotazioneManager, ProiezioneManager proiezioneManager) {
        this.scanner = scanner;
        this.utenteManager = utenteManager;
        this.prenotazioneManager = prenotazioneManager;
        this.proiezioneManager = proiezioneManager;
    }

    /**
     * Mostra il menu principale del proiezionista e gestisce le scelte dell'utente.
     * Il menu rimane attivo fino a quando l'utente non sceglie di effettuare il logout.
     */
    public void mostraMenu() {
        while (true) {
            System.out.println("\n" + ColoreConsole.header("=== MENU PROIEZIONISTA ==="));
            System.out.println("1. Aggiungi proiezione");
            System.out.println("2. Modifica proiezione");
            System.out.println("3. Elimina proiezione");
            System.out.println("4. Logout");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine().trim();

            switch (scelta) {
            case "1":
                aggiungiProiezione();   // Flusso per creare una nuova proiezione
                break;
            case "2":
                modificaProiezione();   // Flusso per modificare una proiezione esistente
                break;
            case "3":
                eliminaProiezione();    // Flusso per eliminare una proiezione
                break;
            case "4":
                return;                 // Logout: torna al menu principale
            default:
                System.out.println(ColoreConsole.errore("Scelta non valida."));
            }
        }
    }

    /**
     * Gestisce l'aggiunta di una nuova proiezione.
     * Acquisisce i dati del film, data/ora e costo, poi tenta l'aggiunta.
     * Applica la regola di non sovrapposizione con proiezioni esistenti.
     */
    private void aggiungiProiezione() {
        System.out.println("\n" + ColoreConsole.header("=== AGGIUNGI PROIEZIONE ==="));
        
        // Acquisizione dati: se null viene creato un nuovo film, altrimenti si parte da uno esistente
        Film film = acquisisciDatiFilm(null);
        Date dataOra = leggiDataOra("Data e ora proiezione (dd/MM/yyyy HH:mm): ", null);
        double costoBiglietto = leggiDoublePositivo("Costo biglietto: ", null);

        // Crea la nuova proiezione e tenta di aggiungerla
        Proiezione nuovaProiezione = new Proiezione(film, dataOra, costoBiglietto);
        boolean aggiunta = proiezioneManager.aggiungiProiezione(nuovaProiezione);

        if (aggiunta) {
            System.out.println(ColoreConsole.successo("Proiezione aggiunta con successo."));
        } else {
            System.out.println(ColoreConsole.errore("Impossibile aggiungere la proiezione."));
            System.out.println(ColoreConsole.avvertimento(
                    "Regola applicata: la nuova proiezione non deve sovrapporsi a una proiezione esistente."));
        }
    }

    /**
     * Gestisce la modifica di una proiezione esistente.
     * Verifica che la proiezione non abbia prenotazioni prima di permettere la modifica.
     * Applica la regola di non sovrapposizione con altre proiezioni dopo la modifica.
     */
    private void modificaProiezione() {
        System.out.println("\n" + ColoreConsole.header("=== MODIFICA PROIEZIONE ==="));
        
        // Selezione della proiezione da modificare (0 annulla)
        Proiezione selezionata = selezionaProiezioneDaLista("Seleziona proiezione da modificare (0 per annullare): ");
        if (selezionata == null) {
            return;  // Utente ha annullato
        }

        // Regola di business: non si può modificare una proiezione con prenotazioni
        if (selezionata.hasPrenotazioni()) {
            System.out.println(ColoreConsole.errore("Modifica non consentita: ci sono prenotazioni sulla proiezione."));
            return;
        }

        // Acquisizione nuovi valori (con prepopolamento dei valori correnti)
        Film nuovoFilm = acquisisciDatiFilm(selezionata.getFilm());
        Date nuovaDataOra = leggiDataOra("Nuova data e ora (dd/MM/yyyy HH:mm): ", selezionata.getDataOra());
        double nuovoCosto = leggiDoublePositivo("Nuovo costo biglietto: ", selezionata.getCostoBiglietto());

        // Tentativo di modifica (verifica sovrapposizioni internamente)
        boolean modificata = proiezioneManager.modificaProiezione(selezionata, nuovoFilm, nuovaDataOra, nuovoCosto);
        if (modificata) {
            System.out.println(ColoreConsole.successo("Proiezione modificata con successo."));
        } else {
            System.out.println(ColoreConsole.errore("Modifica non riuscita."));
            System.out.println(ColoreConsole.avvertimento(
                    "Regole applicate: nessuna prenotazione presente e nessuna sovrapposizione con altre proiezioni."));
        }
    }

    /**
     * Gestisce l'eliminazione di una proiezione.
     * Verifica che la proiezione non abbia prenotazioni prima di eliminarla.
     */
    private void eliminaProiezione() {
        System.out.println("\n" + ColoreConsole.header("=== ELIMINA PROIEZIONE ==="));
        
        // Selezione della proiezione da eliminare
        Proiezione selezionata = selezionaProiezioneDaLista("Seleziona proiezione da eliminare (0 per annullare): ");
        if (selezionata == null) {
            return;  // Utente ha annullato
        }

        // Tentativo di eliminazione (verifica prenotazioni internamente)
        boolean eliminata = proiezioneManager.eliminaProiezione(selezionata);
        if (eliminata) {
            System.out.println(ColoreConsole.successo("Proiezione eliminata con successo."));
        } else {
            System.out.println(ColoreConsole.errore("Eliminazione non consentita o non riuscita."));
            System.out.println(ColoreConsole
                    .avvertimento("Regola applicata: non si puo eliminare una proiezione con prenotazioni."));
        }
    }

    /**
     * Permette all'utente di selezionare una proiezione da una lista.
     * Supporta la ricerca per titolo film (filtro parziale, case-insensitive).
     * 
     * @param prompt Il messaggio da mostrare per la selezione
     * @return La proiezione selezionata, oppure null se l'utente annulla (0)
     */
    private Proiezione selezionaProiezioneDaLista(String prompt) {
        // Ottiene tutte le proiezioni ordinate per data/ora
        List<Proiezione> tutte = proiezioneManager.getProiezioniOrdinate();
        if (tutte.isEmpty()) {
            System.out.println(ColoreConsole.avvertimento("Nessuna proiezione disponibile."));
            return null;
        }

        // Fase di filtro per titolo (opzionale)
        List<Proiezione> proiezioni;
        while (true) {
            System.out.print("Cerca per titolo film (invio per mostrare tutte): ");
            String filtro = scanner.nextLine().trim();
            if (filtro.isEmpty()) {
                proiezioni = tutte;  // Nessun filtro, mostra tutte
            } else {
                // Filtro case-insensitive sul titolo del film
                final String filtroLower = filtro.toLowerCase();
                proiezioni = tutte.stream()
                        .filter(p -> p.getFilm() != null && p.getFilm().getTitolo() != null
                                && p.getFilm().getTitolo().toLowerCase().contains(filtroLower))
                        .toList();
            }

            if (proiezioni.isEmpty()) {
                System.out.println(ColoreConsole.avvertimento("Nessuna proiezione trovata. Riprova."));
                continue;  // Ritorna alla richiesta del filtro
            }
            break;  // Trovate proiezioni, esce dal ciclo
        }

        // Visualizzazione delle proiezioni trovate in formato tabellare
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        System.out.println("\n" + ColoreConsole.header("=== PROIEZIONI TROVATE ==="));
        for (int i = 0; i < proiezioni.size(); i++) {
            Proiezione p = proiezioni.get(i);
            String titolo = p.getFilm() != null ? p.getFilm().getTitolo() : "N/D";
            String dataOra = p.getDataOra() != null ? format.format(p.getDataOra()) : "N/D";
            int durata = p.getFilm() != null ? p.getFilm().getDurata() : 0;
            double costo = p.getCostoBiglietto() != null ? p.getCostoBiglietto() : 0.0;
            System.out.printf("%d. %s | %s | %d min | %.2f euro | Prenotazioni: %d%n", 
                    i + 1, titolo, dataOra, durata, costo, p.getPostiPrenotati());
        }

        // Selezione numerica dell'elemento (1..N, 0 per annullare)
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int indice = Integer.parseInt(input);
                if (indice == 0) {
                    return null;  // Annullamento
                }
                if (indice < 1 || indice > proiezioni.size()) {
                    System.out.println(ColoreConsole.errore("Indice non valido."));
                    continue;
                }
                return proiezioni.get(indice - 1);
            } catch (NumberFormatException e) {
                System.out.println(ColoreConsole.errore("Inserisci un numero valido."));
            }
        }
    }

    /**
     * Acquisisce i dati di un film dall'utente.
     * Se viene fornito un film corrente, i valori vengono prepopolati e l'utente
     * può mantenerli premendo invio.
     * 
     * @param filmCorrente Il film esistente da modificare (null per nuovo film)
     * @return Un nuovo oggetto Film con i dati acquisiti
     */
    private Film acquisisciDatiFilm(Film filmCorrente) {
        // Acquisizione di tutti i campi obbligatori del film
        String titolo = leggiStringaObbligatoria("Titolo film", filmCorrente != null ? filmCorrente.getTitolo() : null);
        String genere = leggiStringaObbligatoria("Genere", filmCorrente != null ? filmCorrente.getGenere() : null);
        String regista = leggiStringaObbligatoria("Regista", filmCorrente != null ? filmCorrente.getRegista() : null);
        int anno = leggiInteroPositivo("Anno", filmCorrente != null ? filmCorrente.getAnno() : null);
        int durata = leggiInteroPositivo("Durata in minuti", filmCorrente != null ? filmCorrente.getDurata() : null);
        int etaMinima = leggiInteroPositivo("Eta minima", filmCorrente != null ? filmCorrente.getEta_minima() : null);
        
        return new Film(titolo, genere, regista, anno, durata, etaMinima);
    }

    /**
     * Legge una stringa obbligatoria dall'utente.
     * Se viene fornito un valore corrente e l'utente preme invio, viene mantenuto.
     * 
     * @param label         L'etichetta da mostrare all'utente
     * @param valoreCorrente Il valore attuale (può essere null)
     * @return La stringa inserita o mantenuta
     */
    private String leggiStringaObbligatoria(String label, String valoreCorrente) {
        while (true) {
            // Mostra il prompt con o senza valore corrente prepopolato
            if (valoreCorrente == null) {
                System.out.print(label + ": ");
            } else {
                System.out.print(label + " [" + valoreCorrente + "] (invio per mantenere): ");
            }

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                // Se l'input è vuoto e c'è un valore corrente, lo mantiene
                if (valoreCorrente != null && !valoreCorrente.isBlank()) {
                    return valoreCorrente;
                }
                System.out.println(ColoreConsole.errore("Valore obbligatorio."));
                continue;
            }
            return input;
        }
    }

    /**
     * Legge un intero positivo dall'utente.
     * Se viene fornito un valore corrente e l'utente preme invio, viene mantenuto.
     * 
     * @param label         L'etichetta da mostrare all'utente
     * @param valoreCorrente Il valore attuale (può essere null)
     * @return L'intero inserito o mantenuto
     */
    private int leggiInteroPositivo(String label, Integer valoreCorrente) {
        while (true) {
            if (valoreCorrente == null) {
                System.out.print(label + ": ");
            } else {
                System.out.print(label + " [" + valoreCorrente + "] (invio per mantenere): ");
            }

            String input = scanner.nextLine().trim();
            if (input.isEmpty() && valoreCorrente != null) {
                return valoreCorrente;  // Mantiene il valore corrente
            }

            try {
                int value = Integer.parseInt(input);
                if (value <= 0) {
                    System.out.println(ColoreConsole.errore("Inserisci un valore maggiore di zero."));
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println(ColoreConsole.errore("Valore non valido."));
            }
        }
    }

    /**
     * Legge una data/ora dall'utente nel formato "dd/MM/yyyy HH:mm".
     * Se viene fornito un valore corrente e l'utente preme invio, viene mantenuto.
     * 
     * @param prompt         Il messaggio da mostrare all'utente
     * @param valoreCorrente Il valore attuale (può essere null)
     * @return La data/ora inserita o mantenuta
     */
    private Date leggiDataOra(String prompt, Date valoreCorrente) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        format.setLenient(false);  // Rende il parsing rigoroso (es. 31/02 viene rifiutato)

        while (true) {
            if (valoreCorrente == null) {
                System.out.print(prompt);
            } else {
                System.out.print(prompt + " [" + format.format(valoreCorrente) + "] (invio per mantenere): ");
            }

            String input = scanner.nextLine().trim();
            if (input.isEmpty() && valoreCorrente != null) {
                return valoreCorrente;  // Mantiene il valore corrente
            }

            try {
                return format.parse(input);
            } catch (ParseException e) {
                System.out.println(ColoreConsole.errore("Data/ora non valida. Usa il formato dd/MM/yyyy HH:mm"));
            }
        }
    }

    /**
     * Legge un double positivo dall'utente (può essere anche zero).
     * Supporta sia il punto che la virgola come separatore decimale.
     * Se viene fornito un valore corrente e l'utente preme invio, viene mantenuto.
     * 
     * @param label         L'etichetta da mostrare all'utente
     * @param valoreCorrente Il valore attuale (può essere null)
     * @return Il double inserito o mantenuto
     */
    private double leggiDoublePositivo(String label, Double valoreCorrente) {
        while (true) {
            if (valoreCorrente == null) {
                System.out.print(label);
            } else {
                System.out.print(label + " [" + String.format("%.2f", valoreCorrente) + "] (invio per mantenere): ");
            }

            String input = scanner.nextLine().trim().replace(',', '.');  // Sostituisce virgola con punto
            if (input.isEmpty() && valoreCorrente != null) {
                return valoreCorrente;  // Mantiene il valore corrente
            }

            try {
                double value = Double.parseDouble(input);
                if (value < 0) {
                    System.out.println(ColoreConsole.errore("Inserisci un valore positivo o zero."));
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println(ColoreConsole.errore("Valore non valido."));
            }
        }
    }
}