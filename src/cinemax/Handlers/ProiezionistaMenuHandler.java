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
                    aggiungiProiezione();
                    break;
                case "2":
                    modificaProiezione();
                    break;
                case "3":
                    eliminaProiezione();
                    break;
                case "4":
                    return;
                default:
                    System.out.println(ColoreConsole.errore("Scelta non valida."));
            }
        }
    }

    private void aggiungiProiezione() {
        System.out.println("\n" + ColoreConsole.header("=== AGGIUNGI PROIEZIONE ==="));
        Film film = acquisisciDatiFilm(null);
        Date dataOra = leggiDataOra("Data e ora proiezione (dd/MM/yyyy HH:mm): ", null);
        double costoBiglietto = leggiDoublePositivo("Costo biglietto: ", null);

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

    private void modificaProiezione() {
        System.out.println("\n" + ColoreConsole.header("=== MODIFICA PROIEZIONE ==="));
        Proiezione selezionata = selezionaProiezioneDaLista("Seleziona proiezione da modificare (0 per annullare): ");
        if (selezionata == null) {
            return;
        }

        if (selezionata.hasPrenotazioni()) {
            System.out.println(ColoreConsole.errore("Modifica non consentita: ci sono prenotazioni sulla proiezione."));
            return;
        }

        Film nuovoFilm = acquisisciDatiFilm(selezionata.getFilm());
        Date nuovaDataOra = leggiDataOra("Nuova data e ora (dd/MM/yyyy HH:mm): ", selezionata.getDataOra());
        double nuovoCosto = leggiDoublePositivo("Nuovo costo biglietto: ", selezionata.getCostoBiglietto());

        boolean modificata = proiezioneManager.modificaProiezione(selezionata, nuovoFilm, nuovaDataOra, nuovoCosto);
        if (modificata) {
            System.out.println(ColoreConsole.successo("Proiezione modificata con successo."));
        } else {
            System.out.println(ColoreConsole.errore("Modifica non riuscita."));
            System.out.println(ColoreConsole.avvertimento(
                    "Regole applicate: nessuna prenotazione presente e nessuna sovrapposizione con altre proiezioni."));
        }
    }

    private void eliminaProiezione() {
        System.out.println("\n" + ColoreConsole.header("=== ELIMINA PROIEZIONE ==="));
        Proiezione selezionata = selezionaProiezioneDaLista("Seleziona proiezione da eliminare (0 per annullare): ");
        if (selezionata == null) {
            return;
        }

        boolean eliminata = proiezioneManager.eliminaProiezione(selezionata);
        if (eliminata) {
            System.out.println(ColoreConsole.successo("Proiezione eliminata con successo."));
        } else {
            System.out.println(ColoreConsole.errore("Eliminazione non consentita o non riuscita."));
            System.out.println(
            		ColoreConsole.avvertimento("Regola applicata: non si puo eliminare una proiezione con prenotazioni."));
        }
    }

    private Proiezione selezionaProiezioneDaLista(String prompt) {
        List<Proiezione> proiezioni = proiezioneManager.getProiezioniOrdinate();
        if (proiezioni.isEmpty()) {
            System.out.println(ColoreConsole.avvertimento("Nessuna proiezione disponibile."));
            return null;
        }

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        System.out.println("\n" + ColoreConsole.header("=== PROIEZIONI DISPONIBILI ==="));
        for (int i = 0; i < proiezioni.size(); i++) {
            Proiezione p = proiezioni.get(i);
            String titolo = p.getFilm() != null ? p.getFilm().getTitolo() : "N/D";
            String dataOra = p.getDataOra() != null ? format.format(p.getDataOra()) : "N/D";
            int durata = p.getFilm() != null ? p.getFilm().getDurata() : 0;
            double costo = p.getCostoBiglietto() != null ? p.getCostoBiglietto() : 0.0;
            System.out.printf("%d. %s | %s | %d min | %.2f euro | Prenotazioni: %d%n",
                    i + 1,
                    titolo,
                    dataOra,
                    durata,
                    costo,
                    p.getPostiPrenotati());
        }

        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int indice = Integer.parseInt(input);
                if (indice == 0) {
                    return null;
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

    private Film acquisisciDatiFilm(Film filmCorrente) {
        String titolo = leggiStringaObbligatoria("Titolo film", filmCorrente != null ? filmCorrente.getTitolo() : null);
        String genere = leggiStringaObbligatoria("Genere", filmCorrente != null ? filmCorrente.getGenere() : null);
        String regista = leggiStringaObbligatoria("Regista", filmCorrente != null ? filmCorrente.getRegista() : null);
        int anno = leggiInteroPositivo("Anno", filmCorrente != null ? filmCorrente.getAnno() : null);
        int durata = leggiInteroPositivo("Durata in minuti", filmCorrente != null ? filmCorrente.getDurata() : null);
        int etaMinima = leggiInteroPositivo("Eta minima", filmCorrente != null ? filmCorrente.getEta_minima() : null);
        return new Film(titolo, genere, regista, anno, durata, etaMinima);
    }

    private String leggiStringaObbligatoria(String label, String valoreCorrente) {
        while (true) {
            if (valoreCorrente == null) {
                System.out.print(label + ": ");
            } else {
                System.out.print(label + " [" + valoreCorrente + "] (invio per mantenere): ");
            }

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                if (valoreCorrente != null && !valoreCorrente.isBlank()) {
                    return valoreCorrente;
                }
                System.out.println(ColoreConsole.errore("Valore obbligatorio."));
                continue;
            }
            return input;
        }
    }

    private int leggiInteroPositivo(String label, Integer valoreCorrente) {
        while (true) {
            if (valoreCorrente == null) {
                System.out.print(label + ": ");
            } else {
                System.out.print(label + " [" + valoreCorrente + "] (invio per mantenere): ");
            }

            String input = scanner.nextLine().trim();
            if (input.isEmpty() && valoreCorrente != null) {
                return valoreCorrente;
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

    private Date leggiDataOra(String prompt, Date valoreCorrente) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        format.setLenient(false);

        while (true) {
            if (valoreCorrente == null) {
                System.out.print(prompt);
            } else {
                System.out.print(prompt + " [" + format.format(valoreCorrente) + "] (invio per mantenere): ");
            }

            String input = scanner.nextLine().trim();
            if (input.isEmpty() && valoreCorrente != null) {
                return valoreCorrente;
            }

            try {
                return format.parse(input);
            } catch (ParseException e) {
                System.out.println(ColoreConsole.errore("Data/ora non valida. Usa il formato dd/MM/yyyy HH:mm"));
            }
        }
    }

    private double leggiDoublePositivo(String label, Double valoreCorrente) {
        while (true) {
            if (valoreCorrente == null) {
                System.out.print(label);
            } else {
                System.out.print(label + " [" + String.format("%.2f", valoreCorrente) + "] (invio per mantenere): ");
            }

            String input = scanner.nextLine().trim().replace(',', '.');
            if (input.isEmpty() && valoreCorrente != null) {
                return valoreCorrente;
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