/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax.Managers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cinemax.Models.Proiezione;
import cinemax.Models.Film;
import cinemax.Helpers.*;
import cinemax.ViewModels.CriteriRicercaProiezione;

/**
 * Gestore delle operazioni relative alle proiezioni cinematografiche.
 * Fornisce funzionalità per la gestione del ciclo di vita delle proiezioni,
 * inclusa la ricerca, l'aggiunta, la modifica, l'eliminazione e la
 * visualizzazione delle proiezioni.
 * 
 * 
 */
public class ProiezioneManager {

    /** Lista di tutte le proiezioni gestite dal sistema */
    public List<Proiezione> proiezioni;

    /**
     * Costruttore che inizializza il manager caricando le proiezioni dal file CSV.
     * Legge il file "data/proiezioni.csv" e converte ogni riga in un oggetto Proiezione.
     */
    public ProiezioneManager() {
        // Inizializza il lettore CSV per il caricamento dei dati
        CSVReader csvReader = new CSVReader();

        try {
            // Legge il file CSV e mappa ogni riga a un oggetto Proiezione
            this.proiezioni = csvReader.readFile("data/proiezioni.csv", values -> {
                // CSV mapping delle colonne:
                // 0: data_ora_proiezione
                // 1: titolo_film
                // 2: genere
                // 3: regista
                // 4: anno
                // 5: durata_minuti
                // 6: eta_minima
                // 7: prezzo_biglietto

                // Parse della data e ora della proiezione dal formato specificato
                Date dataOra = CSVReader.parseDate(values.get(0), "yyyy-MM-dd HH:mm:ss");

                // Creazione dell'oggetto Film a partire dai dati del CSV
                Film film = new Film(values.get(1), // titolo
                        values.get(2), // genere
                        values.get(3), // regista
                        CSVReader.parseInt(values.get(4)), // anno
                        CSVReader.parseInt(values.get(5)), // durata
                        CSVReader.parseInt(values.get(6)) // eta_minima
                );

                // Parse del prezzo del biglietto
                Double prezzo = CSVReader.parseDouble(values.get(7));

                // Creazione e restituzione dell'oggetto Proiezione
                Proiezione proiezione = new Proiezione(film, dataOra, prezzo);

                return proiezione;
            });

        } catch (IOException e) {
            // Gestione dell'errore in caso di problemi nella lettura del file
            System.err.println("Error reading CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Salva tutte le proiezioni correnti nel file CSV.
     * Il metodo sovrascrive il file esistente con i dati aggiornati.
     */
    private void salvaCSV() {
        // Formattatore per la data/ora nel formato richiesto dal CSV
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/proiezioni.csv"))) {
            // Scrittura dell'intestazione del CSV
            writer.write(
                    "data_ora_proiezione,titolo_film,genere,regista,anno,durata_minuti,eta_minima,prezzo_biglietto");
            writer.newLine();
            
            // Iterazione su tutte le proiezioni per scriverle nel file
            for (Proiezione p : proiezioni) {
                // Salta elementi nulli o incompleti
                if (p == null || p.getFilm() == null || p.getDataOra() == null)
                    continue;
                    
                Film f = p.getFilm();
                // Scrittura dei dati formattati come riga CSV
                writer.write(String.format("\"%s\",\"%s\",%s,\"%s\",%d,%d,%d,%.2f", fmt.format(p.getDataOra()),
                        f.getTitolo(), f.getGenere(), f.getRegista(), f.getAnno(), f.getDurata(), f.getEta_minima(),
                        p.getCostoBiglietto()));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio del CSV: " + e.getMessage());
        }
    }
    
    /**
     * Cerca le proiezioni in base ai criteri specificati.
     * 
     * @param criteriRicercaProiezione Oggetto contenente i criteri di ricerca
     * @return Lista di proiezioni che soddisfano tutti i criteri specificati
     */
    public List<Proiezione> cercaProiezione(CriteriRicercaProiezione criteriRicercaProiezione) {
        try {
            // Applica filtri stream per cercare le proiezioni
            return proiezioni.stream().filter(proiezione -> {
                // Filtro 1: Per titolo (se titolo non è null)
                var titolo = criteriRicercaProiezione.getTitolo();
                if (titolo != null && !titolo.trim().isEmpty()) {
                    // Verifica se il titolo del film contiene il testo cercato (case-insensitive)
                    boolean abbinaTitolo = proiezione.getFilm() != null && proiezione.getFilm().getTitolo() != null
                            && proiezione.getFilm().getTitolo().trim().toLowerCase()
                                    .contains(titolo.trim().toLowerCase());
                    if (!abbinaTitolo)
                        return false;
                }

                // Filtro 2: Per tipologia/genere (se tipologia non è null)
                var tipologia = criteriRicercaProiezione.getTipologia();
                if (tipologia != null && !tipologia.trim().isEmpty()) {
                    // Verifica corrispondenza esatta del genere (case-insensitive)
                    boolean abbinaTipologia = proiezione.getFilm() != null && proiezione.getFilm().getGenere() != null
                            && proiezione.getFilm().getGenere().trim().equalsIgnoreCase(tipologia.trim());
                    if (!abbinaTipologia)
                        return false;
                }

                // Filtro 3: Per periodo data (se date disponibili)
                var dataInizio = criteriRicercaProiezione.getDataInizio();
                var dataFine = criteriRicercaProiezione.getDataFine();

                if (dataInizio != null || dataFine != null) {
                    Date dataProiezione = proiezione.getDataOra();
                    if (dataProiezione == null)
                        return false;

                    // Verifica che la proiezione sia dopo la data di inizio
                    if (dataInizio != null && dataProiezione.before(dataInizio)) {
                        return false;
                    }

                    // Verifica che la proiezione sia prima della data di fine
                    if (dataFine != null && dataProiezione.after(dataFine)) {
                        return false;
                    }
                }

                // Filtro 4: Per prezzo del biglietto (se diverso da null)
                var costoBiglietto = criteriRicercaProiezione.getCostoBiglietto();
                var costoMin = criteriRicercaProiezione.getCostoMin();
                var costoMax = criteriRicercaProiezione.getCostoMax();

                // Filtro per prezzo esatto (con tolleranza per arrotondamenti)
                if (costoBiglietto != null) {
                    Double costoProiezione = proiezione.getCostoBiglietto();
                    if (costoProiezione == null)
                        return false;

                    // Tolleranza di 0.01 per confronto double
                    if (Math.abs(costoProiezione - costoBiglietto) > 0.01) {
                        return false;
                    }
                }

                // Filtro per prezzo minimo
                if (costoMin != null) {
                    Double costoProiezione = proiezione.getCostoBiglietto();
                    if (costoProiezione == null || costoProiezione < costoMin) {
                        return false;
                    }
                }

                // Filtro per prezzo massimo
                if (costoMax != null) {
                    Double costoProiezione = proiezione.getCostoBiglietto();
                    if (costoProiezione == null || costoProiezione > costoMax) {
                        return false;
                    }
                }

                return true;
            }).collect(Collectors.toList());

        } catch (Exception ex) {
            System.err.println("Errore nella ricerca di proiezioni: " + ex.getMessage());
            return List.of(); // Restituisce lista vuota in caso di errore
        }
    }

    /**
     * Raggruppa le proiezioni per titolo del film
     * 
     * @param proiezioni Lista di proiezioni da raggruppare
     * @return Mappa con chiave = titolo film, valore = lista di proiezioni
     */
    public Map<String, List<Proiezione>> raggruppaPerTitolo(List<Proiezione> proiezioni) {
        // Filtra le proiezioni valide e le raggruppa per titolo del film
        return proiezioni.stream().filter(p -> p.getFilm() != null && p.getFilm().getTitolo() != null)
                .collect(Collectors.groupingBy(p -> p.getFilm().getTitolo()));
    }

    /**
     * Visualizza le proiezioni raggruppate per titolo
     * 
     * @param raggruppate Mappa delle proiezioni raggruppate per titolo
     */
    public void visualizzaRaggruppato(Map<String, List<Proiezione>> raggruppate) {
        // Mostra i film raggruppati con numerazione
        System.out.println("\n=== FILM DISPONIBILI ===");
        List<String> titoliFilm = new ArrayList<>(raggruppate.keySet());

        // Stampa ogni film con il numero di proiezioni disponibili
        for (int i = 0; i < titoliFilm.size(); i++) {
            String titolo = titoliFilm.get(i);
            List<Proiezione> proiezioniFilm = raggruppate.get(titolo);
            System.out.printf("%d. %s (%d proiezioni)\n", i + 1, titolo, proiezioniFilm.size());
        }
    }

    /**
     * Visualizza i dettagli completi di una singola proiezione.
     * 
     * @param proiezione La proiezione da visualizzare
     */
    public void visualizzaProiezione(Proiezione proiezione) {
        // Validazione della proiezione
        if (proiezione == null || proiezione.getFilm() == null) {
            System.out.println("Proiezione non valida.");
            return;
        }

        // Formattatore per data/ora leggibile
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Film film = proiezione.getFilm();

        // Stampa di tutti i dettagli della proiezione
        System.out.println("\n=== DETTAGLIO PROIEZIONE ===");
        System.out.println("Titolo: " + film.getTitolo());
        System.out.println("Genere: " + film.getGenere());
        System.out.println("Regista: " + film.getRegista());
        System.out.println("Anno: " + film.getAnno());
        System.out.println("Durata: " + film.getDurata() + " min");
        System.out.println("Eta' minima:  " + film.getEta_minima() + " anni");
        System.out.println("Data/Ora: " + dateTimeFormat.format(proiezione.getDataOra()));
        System.out.printf("Costo biglietto: %.2f euro%n", proiezione.getCostoBiglietto());
        System.out.println("Posti liberi: " + proiezione.getNumeroPostiDisponibili());
    }

    /**
     * Restituisce la lista delle proiezioni ordinate per data e ora.
     * 
     * @return Lista di proiezioni ordinate cronologicamente
     */
    public List<Proiezione> getProiezioniOrdinate() {
        if (proiezioni == null) {
            return List.of(); // Restituisce lista vuota se non ci sono proiezioni
        }

        // Filtra elementi nulli e ordina per data/ora crescente
        return proiezioni.stream().filter(p -> p != null && p.getDataOra() != null)
                .sorted(Comparator.comparing(Proiezione::getDataOra)).toList();
    }

    /**
     * Aggiunge una nuova proiezione al sistema.
     * 
     * @param nuovaProiezione La proiezione da aggiungere
     * @return true se l'aggiunta è riuscita, false altrimenti
     */
    public boolean aggiungiProiezione(Proiezione nuovaProiezione) {
        // Validazione dei dati della nuova proiezione
        if (nuovaProiezione == null || nuovaProiezione.getFilm() == null || nuovaProiezione.getDataOra() == null
                || nuovaProiezione.getCostoBiglietto() == null || nuovaProiezione.getCostoBiglietto() < 0
                || nuovaProiezione.getFilm().getDurata() <= 0) {
            return false;
        }

        // Verifica che non ci siano sovrapposizioni con altre proiezioni
        if (haSovrapposizione(nuovaProiezione, null)) {
            return false;
        }

        // Inizializza la lista se è null
        if (proiezioni == null) {
            proiezioni = new ArrayList<>();
        }

        // Aggiunge la proiezione e salva su file
        proiezioni.add(nuovaProiezione);
        salvaCSV();
        return true;
    }

    /**
     * Modifica i dati di una proiezione esistente.
     * 
     * @param proiezioneDaModificare La proiezione da modificare
     * @param nuovoFilm Il nuovo film da associare
     * @param nuovaDataOra La nuova data/ora della proiezione
     * @param nuovoCostoBiglietto Il nuovo costo del biglietto
     * @return true se la modifica è riuscita, false altrimenti
     */
    public boolean modificaProiezione(Proiezione proiezioneDaModificare, Film nuovoFilm, Date nuovaDataOra,
            Double nuovoCostoBiglietto) {
        // Validazione dei nuovi dati
        if (proiezioneDaModificare == null || nuovoFilm == null || nuovaDataOra == null || nuovoCostoBiglietto == null
                || nuovoCostoBiglietto < 0 || nuovoFilm.getDurata() <= 0) {
            return false;
        }

        // Non permette modifica se ci sono prenotazioni attive
        if (proiezioneDaModificare.hasPrenotazioni()) {
            return false;
        }

        // Crea una proiezione candidata con i nuovi dati ma mantenendo i posti prenotati
        Proiezione candidata = new Proiezione(nuovoFilm, nuovaDataOra, nuovoCostoBiglietto,
                proiezioneDaModificare.getPostiPrenotati());

        // Verifica sovrapposizioni escludendo la proiezione corrente
        if (haSovrapposizione(candidata, proiezioneDaModificare)) {
            return false;
        }

        // Applica le modifiche e salva
        proiezioneDaModificare.setFilm(nuovoFilm);
        proiezioneDaModificare.setDataOra(nuovaDataOra);
        proiezioneDaModificare.setCostoBiglietto(nuovoCostoBiglietto);
        salvaCSV();
        return true;
    }

    /**
     * Elimina una proiezione dal sistema.
     * 
     * @param proiezioneDaEliminare La proiezione da eliminare
     * @return true se l'eliminazione è riuscita, false altrimenti
     */
    public boolean eliminaProiezione(Proiezione proiezioneDaEliminare) {
        if (proiezioneDaEliminare == null || proiezioni == null) {
            return false;
        }

        if (proiezioneDaEliminare.hasPrenotazioni()) {
            return false;
        }

        boolean rimossa = proiezioni.remove(proiezioneDaEliminare);
        if (rimossa) {
            salvaCSV();
        }
        return rimossa;
    }

    /**
     * Verifica se una nuova proiezione si sovrappone con proiezioni esistenti.
     * 
     * @param nuovaProiezione La proiezione da verificare
     * @param daEscludere Proiezione da escludere dal controllo (es. quella in modifica)
     * @return true se c'è sovrapposizione, false altrimenti
     */
    private boolean haSovrapposizione(Proiezione nuovaProiezione, Proiezione daEscludere) {
        // Validazione dei parametri
        if (nuovaProiezione == null || nuovaProiezione.getDataOra() == null || nuovaProiezione.getFilm() == null
                || nuovaProiezione.getFilm().getDurata() <= 0 || proiezioni == null) {
            return false;
        }

        // Calcolo dell'intervallo orario della nuova proiezione
        long nuovoInizio = nuovaProiezione.getDataOra().getTime();
        long nuovoFine = nuovoInizio + (long) nuovaProiezione.getFilm().getDurata() * 60_000L;

        // Controllo sovrapposizioni con tutte le proiezioni esistenti
        for (Proiezione esistente : proiezioni) {
            // Salta elementi nulli, la proiezione da escludere e proiezioni non valide
            if (esistente == null || esistente == daEscludere || esistente.getDataOra() == null
                    || esistente.getFilm() == null || esistente.getFilm().getDurata() <= 0) {
                continue;
            }

            // Calcolo dell'intervallo orario della proiezione esistente
            long esistenteInizio = esistente.getDataOra().getTime();
            long esistenteFine = esistenteInizio + (long) esistente.getFilm().getDurata() * 60_000L;

            // Verifica se gli intervalli si sovrappongono
            if (nuovoInizio < esistenteFine && nuovoFine > esistenteInizio) {
                return true; // Sovrapposizione trovata
            }
        }

        return false; // Nessuna sovrapposizione
    }
}