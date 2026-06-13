/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax.Helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import cinemax.Models.Proiezione;
import cinemax.ViewModels.CriteriRicercaProiezione;

public final class RicercaProiezioneConsoleHelper {

    private RicercaProiezioneConsoleHelper() {
    }

    /**
     * Leggi i criteri di ricerca per proiezione
     * @param scanner
     * @return CriteriRicercaProiezione - l'oggetto contenente tutti i criteri di ricerca
     */
    public static CriteriRicercaProiezione acquisisciCriteriRicerca(Scanner scanner) {
        CriteriRicercaProiezione criteri = new CriteriRicercaProiezione();
        SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
        formatoData.setLenient(false); //controllo rigoroso per formato della data 

        stampaHeader("=== RICERCA PROIEZIONI ===");
        System.out.print("Titolo film (anche parziale, invio per saltare): ");
        String titolo = scanner.nextLine().trim();
        if (!titolo.isEmpty()) {
        	criteri.setTitolo(titolo);
        }

        System.out.print("Tipologia/genere film (invio per saltare): ");
        String tipologia = scanner.nextLine().trim();
        if (!tipologia.isEmpty()) {
        	criteri.setTipologia(tipologia);
        }

        System.out.println("Filtro date:");
        System.out.println("0. Nessun filtro");
        System.out.println("1. Dopo una data");
        System.out.println("2. Tra due date");
        System.out.print("Scelta: ");
        String sceltaDate = scanner.nextLine().trim();

        if ("1".equals(sceltaDate)) {
            Date dataInizio = leggiData(scanner, "Inserisci data inizio (dd/MM/yyyy): ", formatoData);
            criteri.setDataInizio(dataInizio);
        } else if ("2".equals(sceltaDate)) {
            Date dataInizio = leggiData(scanner, "Inserisci data inizio (dd/MM/yyyy): ", formatoData);
            Date dataFine = leggiData(scanner, "Inserisci data fine (dd/MM/yyyy): ", formatoData);
            if (dataInizio != null && dataFine != null && dataInizio.after(dataFine)) {
                Date temp = dataInizio;
                dataInizio = dataFine;
                dataFine = temp;
            }
            criteri.setDataInizio(dataInizio);
            criteri.setDataFine(dataFine);
        }

        System.out.println("Filtro costo biglietto:");
        System.out.println("0. Nessun filtro");
        System.out.println("1. Minore o uguale a un valore");
        System.out.println("2. Tra due valori");
        System.out.println("3. Uguale a un valore");
        System.out.print("Scelta: ");
        String sceltaCosto = scanner.nextLine().trim();

        if ("1".equals(sceltaCosto)) {
            Double max = leggiDouble(scanner, "Inserisci costo massimo: ");
            criteri.setCostoMax(max);
        } else if ("2".equals(sceltaCosto)) {
            Double min = leggiDouble(scanner, "Inserisci costo minimo: ");
            Double max = leggiDouble(scanner, "Inserisci costo massimo: ");
            if (min != null && max != null && min > max) {
                Double temp = min;
                min = max;
                max = temp;
            }
            criteri.setCostoMin(min);
            criteri.setCostoMax(max);
        } else if ("3".equals(sceltaCosto)) {
            Double exact = leggiDouble(scanner, "Inserisci costo esatto: ");
            criteri.setCostoBiglietto(exact);
        }

        return criteri;
    }
    
    /**
     * Stampa a console i risultati della ricerca di proiezione
     * @param Lista di proiezioni
     * @return 
     */
    public static void mostraRisultatiRicerca(List<Proiezione> risultati) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String resultsTitle = "=== RISULTATI RICERCA ===";
        System.out.println("\n" + ColoreConsole.headerRisultati(resultsTitle));


        Map<String, List<Integer>> raggruppatePerTitolo = new LinkedHashMap<>();
        for (int i = 0; i < risultati.size(); i++) {
            Proiezione p = risultati.get(i);
            String titolo = p.getFilm() != null && p.getFilm().getTitolo() != null
                    ? p.getFilm().getTitolo()
                    : "N/D";
            raggruppatePerTitolo.computeIfAbsent(titolo, k -> new ArrayList<>()).add(i);
        }

        for (Map.Entry<String, List<Integer>> gruppo : raggruppatePerTitolo.entrySet()) {
            String titolo = gruppo.getKey();
            String intestazioneFilm = "Film: " + titolo;
            System.out.println(ColoreConsole.titoloFilm(intestazioneFilm));

            for (Integer indice : gruppo.getValue()) {
                Proiezione p = risultati.get(indice);
                String genere = p.getFilm() != null ? p.getFilm().getGenere() : "N/D";
                String dataOra = p.getDataOra() != null ? dateTimeFormat.format(p.getDataOra()) : "N/D";
                String costo = p.getCostoBiglietto() != null ? String.format("%.2f", p.getCostoBiglietto()) : "N/D";
                System.out.printf("%d. %s | %s | %s euro%n", indice + 1, genere, dataOra, costo);
            }

            System.out.println();
        }
    }

    /**
     * Fa selezionare al cliente la proiezione dalla lista dei risultati
     * @param Lista di proiezioni restituite applicando i filtri
     * @param prompt per utente
     * @return la proiezione selezionata
     */
    public static Proiezione selezionaProiezione(Scanner scanner, List<Proiezione> risultati, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int scelta = Integer.parseInt(input);
                if (scelta == 0) {
                    return null;
                }
                if (scelta < 1 || scelta > risultati.size()) {
                    System.out.println(formattaErrore("Indice non valido."));
                    continue;
                }
                return risultati.get(scelta - 1);
            } catch (NumberFormatException e) {
                System.out.println(formattaErrore("Inserisci un numero valido."));
            }
        }
    }

    /**
     * Richiede al cliente di inserire una data in un certo formato e ritorna la stringa convertita in Data
     * @param Scanner
     * @param Prompt per utente
     * @param Formato accettabile della data
     * @return Data
     */
    private static Date leggiData(Scanner scanner, String prompt, SimpleDateFormat format) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                return format.parse(input);
            } catch (ParseException e) {
                System.out.println(formattaErrore("Data non valida, riprova."));
            }
        }
    }

    /**
     * Richiede al cliente di inserire un numero di tipo double e restituisce la stringa convertita in double
     * @param Scanner
     * @param Prompt per utente
     * @return Double
     */
    private static Double leggiDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().replace(',', '.');
            if (input.isEmpty()) {
                return null;
            }
            try {
                double value = Double.parseDouble(input);
                if (value < 0) {
                    System.out.println(formattaErrore("Inserisci un valore positivo."));
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println(formattaErrore("Valore non valido, riprova."));
            }
        }
    }

    
    /**
     * Stampa l'header con colore specifico 
     * @param String
     * @return 
     */
    private static void stampaHeader(String header) {
            System.out.println("\n" + ColoreConsole.header(header));
    }

    /**
     * Stampa l'errore con colore specifico
     * @param String
     * @return 
     */
    private static String formattaErrore(String message) {
        return ColoreConsole.errore(message);
    }
}