/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax.Helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * Classe di utilità per leggere file CSV.
 *
 * Permette di leggere un file CSV e convertire ogni riga
 * in un oggetto Java tramite una funzione di conversione (mapper).
 */
public class CSVReader {

    /** Separatore dei campi nel file CSV (es. "," oppure ";") */
    private String separatore;

    /** Indica se il file ha una riga di intestazione da saltare */
    private boolean haIntestazione;

    /**
     * Costruttore con separatore virgola e intestazione presente.
     * Usato quando non si specifica nulla.
     */
    public CSVReader() {
        this(",", true);
    }

    /**
     * Costruttore con separatore e intestazione personalizzati.
     *
     * @param separatore    separatore dei campi (es. ",", ";", "|")
     * @param haIntestazione true se il file ha una riga di intestazione da saltare
     */
    public CSVReader(String separatore, boolean haIntestazione) {
        this.separatore = separatore;
        this.haIntestazione = haIntestazione;
    }

    /**
     * Legge un file CSV e converte ogni riga in un oggetto Java.
     *
     * @param percorsoFile percorso del file CSV da leggere
     * @param mapper       funzione che converte una lista di valori in un oggetto
     * @return lista di oggetti convertiti
     * @throws IOException se il file non esiste o non è leggibile
     */
    public <T> List<T> readFile(String percorsoFile, Function<List<String>, T> mapper) throws IOException {

        List<T> risultati = new ArrayList<>();

        try (BufferedReader lettore = new BufferedReader(new FileReader(percorsoFile))) {
            String riga;
            boolean primaRiga = true;

            while ((riga = lettore.readLine()) != null) {

                // Saltiamo le righe vuote
                if (riga.trim().isEmpty()) {
                    continue;
                }

                // Saltiamo la riga di intestazione se presente
                if (haIntestazione && primaRiga) {
                    primaRiga = false;
                    continue;
                }
                primaRiga = false;

                // Dividiamo la riga nei suoi valori
                List<String> valori = analizzaRiga(riga);

                // Convertiamo i valori in un oggetto tramite il mapper
                T oggetto = mapper.apply(valori);
                risultati.add(oggetto);
            }
        }

        return risultati;
    }

    /**
     * Divide una riga CSV nei suoi valori, gestendo anche i valori tra virgolette.
     *
     * @param riga riga CSV da analizzare
     * @return lista dei valori trovati
     */
    private List<String> analizzaRiga(String riga) {

        List<String> valori = new ArrayList<>();
        boolean traVirgolette = false;
        StringBuilder valoreCorrente = new StringBuilder();

        for (int i = 0; i < riga.length(); i++) {
            char c = riga.charAt(i);

            if (c == '"') {
                // Gestiamo le virgolette doppie dentro un valore tra virgolette
                if (traVirgolette && i + 1 < riga.length() && riga.charAt(i + 1) == '"') {
                    valoreCorrente.append('"');
                    i++; // Saltiamo la seconda virgoletta
                } else {
                    traVirgolette = !traVirgolette;
                }
            } else if (c == separatore.charAt(0) && !traVirgolette) {
                // Fine del valore corrente
                valori.add(valoreCorrente.toString().trim());
                valoreCorrente = new StringBuilder();
            } else {
                valoreCorrente.append(c);
            }
        }

        // Aggiungiamo l'ultimo valore
        valori.add(valoreCorrente.toString().trim());

        return valori;
    }

    /**
     * Converte una stringa in intero.
     * Restituisce null se la stringa è vuota o non è un numero valido.
     *
     * @param valore stringa da convertire
     * @return valore intero, oppure null in caso di errore
     */
    public static Integer parseInt(String valore) {
        if (valore == null || valore.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(valore.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Converte una stringa in numero decimale.
     * Restituisce null se la stringa è vuota o non è un numero valido.
     *
     * @param valore stringa da convertire
     * @return valore decimale, oppure null in caso di errore
     */
    public static Double parseDouble(String valore) {
        if (valore == null || valore.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(valore.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Converte una stringa in data usando il formato specificato.
     * Restituisce null se la stringa è vuota o il formato non corrisponde.
     *
     * @param valore  stringa da convertire
     * @param formato formato della data (es. "yyyy-MM-dd", "dd/MM/yyyy")
     * @return oggetto Date, oppure null in caso di errore
     */
    public static Date parseDate(String valore, String formato) {
        if (valore == null || valore.trim().isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(formato);
            return sdf.parse(valore.trim());
        } catch (ParseException e) {
            return null;
        }
    }
}