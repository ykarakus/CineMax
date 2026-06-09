package cinemax.Managers;

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

public class ProiezioneManager {

	public List<Proiezione> proiezioni;

	public ProiezioneManager() {

		CSVReader csvReader = new CSVReader();

		try {
			// Leggi e fa il pasing di CSV file into oggetti di tipo Proiezione
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

				// Parse  data ora
				Date dataOra = CSVReader.parseDate(values.get(0), "yyyy-MM-dd HH:mm:ss");

				// Creazione dell'oggetto Film a partire dai dati del CSV
				Film film = new Film(values.get(1), // titolo
						values.get(2), // genere
						values.get(3), // regista
						CSVReader.parseInt(values.get(4)), // anno
						CSVReader.parseInt(values.get(5)), // durata
						CSVReader.parseInt(values.get(6)) // eta_minima
				);

				// prezzo
				Double prezzo = CSVReader.parseDouble(values.get(7));

				Proiezione proiezione = new Proiezione(film, dataOra, prezzo);

				return proiezione;
			});

		} catch (IOException e) {
			System.err.println("Error reading CSV file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public List<Proiezione> cercaProiezione(CriteriRicercaProiezione criteriRicercaProiezione) {
		try {
			return proiezioni.stream().filter(proiezione -> {
				// Filtro 1: Per titolo (se titolo non è null)
				var titolo = criteriRicercaProiezione.getTitolo();
				if (titolo != null && !titolo.trim().isEmpty()) {
					boolean abbinaTitolo = proiezione.getFilm() != null && proiezione.getFilm().getTitolo() != null
							&& proiezione.getFilm().getTitolo().trim().toLowerCase()
									.contains(titolo.trim().toLowerCase());
					if (!abbinaTitolo)
						return false;
				}

				// Filtro 2: Per tipologia/genere (se tipologia non è null)
				var tipologia = criteriRicercaProiezione.getTipologia();
				if (tipologia != null && !tipologia.trim().isEmpty()) {
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

					if (dataInizio != null && dataProiezione.before(dataInizio)) {
						return false;
					}

					if (dataFine != null && dataProiezione.after(dataFine)) {
						return false;
					}
				}

				// Filtro 4: Per prezzo del biglietto (se diverso da null)
				var costoBiglietto = criteriRicercaProiezione.getCostoBiglietto();
				if (costoBiglietto != null) {
					Double costoProiezione = proiezione.getCostoBiglietto();
					if (costoProiezione == null)
						return false;

					if (Math.abs(costoProiezione - costoBiglietto) > 0.01) {
						return false;
					}
				}

				return true;
			}).collect(Collectors.toList());

		} catch (Exception ex) {
			System.err.println("Errore nella ricerca di proiezioni: " + ex.getMessage());
			return List.of();
		}

	}

	/**
	 * Raggruppa le proiezioni per titolo del film
	 * 
	 * @param proiezioni Lista di proiezioni da raggruppare
	 * @return Mappa con chiave = titolo film, valore = lista di proiezioni
	 */
	public Map<String, List<Proiezione>> raggruppaPerTitolo(List<Proiezione> proiezioni) {
		return proiezioni.stream().filter(p -> p.getFilm() != null && p.getFilm().getTitolo() != null)
				.collect(Collectors.groupingBy(p -> p.getFilm().getTitolo()));
	}

	/**
	 * Visualizza le proiezioni raggruppate per titolo
	 * 
	 * @param proiezioni Lista di proiezioni da visualizzare
	 */
	public void visualizzaRaggruppato(Map<String, List<Proiezione>> raggruppate) {

		// Mostra i film raggruppati con numerazione
		System.out.println("\n=== FILM DISPONIBILI ===");
		List<String> titoliFilm = new ArrayList<>(raggruppate.keySet());

		for (int i = 0; i < titoliFilm.size(); i++) {
			String titolo = titoliFilm.get(i);
			List<Proiezione> proiezioniFilm = raggruppate.get(titolo);
			System.out.printf("%d. %s (%d proiezioni)\n", i + 1, titolo, proiezioniFilm.size());
		}
	}

	public void visualizzaProiezione(Proiezione proiezione) {

			if (proiezione == null || proiezione.getFilm() == null) {
				System.out.println("Proiezione non valida.");
				return;
			}

			SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			Film film = proiezione.getFilm();

			System.out.println("\n=== DETTAGLIO PROIEZIONE ===");
			System.out.println("Titolo: " + film.getTitolo());
			System.out.println("Genere: " + film.getGenere());
			System.out.println("Regista: " + film.getRegista());
			System.out.println("Anno: " + film.getAnno());
			System.out.println("Durata: " + film.getDurata() + " min");
			System.out.println("Data/Ora: " + dateTimeFormat.format(proiezione.getDataOra()));
			System.out.printf("Costo biglietto: %.2f euro%n", proiezione.getCostoBiglietto());
			System.out.println("Posti liberi: " + proiezione.getNumeroPostiDisponibili());
				
	}
	
	   
	   
	public List<Proiezione> getProiezioniOrdinate() {
	        if (proiezioni == null) {
	            return List.of();
	        }

	        return proiezioni.stream()
	                .filter(p -> p != null && p.getDataOra() != null)
	                .sorted(Comparator.comparing(Proiezione::getDataOra))
	                .toList();
	    }

	    public boolean aggiungiProiezione(Proiezione nuovaProiezione) {
	        if (nuovaProiezione == null || nuovaProiezione.getFilm() == null || nuovaProiezione.getDataOra() == null
	                || nuovaProiezione.getCostoBiglietto() == null || nuovaProiezione.getCostoBiglietto() < 0
	                || nuovaProiezione.getFilm().getDurata() <= 0) {
	            return false;
	        }

	        if (haSovrapposizione(nuovaProiezione, null)) {
	            return false;
	        }

	        if (proiezioni == null) {
	            proiezioni = new ArrayList<>();
	        }

	        proiezioni.add(nuovaProiezione);
	        return true;
	    }

	    public boolean modificaProiezione(Proiezione proiezioneDaModificare, Film nuovoFilm, Date nuovaDataOra,
	            Double nuovoCostoBiglietto) {
	        if (proiezioneDaModificare == null || nuovoFilm == null || nuovaDataOra == null || nuovoCostoBiglietto == null
	                || nuovoCostoBiglietto < 0 || nuovoFilm.getDurata() <= 0) {
	            return false;
	        }

	        if (proiezioneDaModificare.hasPrenotazioni()) {
	            return false;
	        }

	        Proiezione candidata = new Proiezione(nuovoFilm, nuovaDataOra, nuovoCostoBiglietto,
	                proiezioneDaModificare.getPostiPrenotati());

	        if (haSovrapposizione(candidata, proiezioneDaModificare)) {
	            return false;
	        }

	        proiezioneDaModificare.setFilm(nuovoFilm);
	        proiezioneDaModificare.setDataOra(nuovaDataOra);
	        proiezioneDaModificare.setCostoBiglietto(nuovoCostoBiglietto);
	        return true;
	    }

	    public boolean eliminaProiezione(Proiezione proiezioneDaEliminare) {
	        if (proiezioneDaEliminare == null || proiezioni == null) {
	            return false;
	        }

	        if (proiezioneDaEliminare.hasPrenotazioni()) {
	            return false;
	        }

	        return proiezioni.remove(proiezioneDaEliminare);
	    }

	    private boolean haSovrapposizione(Proiezione nuovaProiezione, Proiezione daEscludere) {
	        if (nuovaProiezione == null || nuovaProiezione.getDataOra() == null || nuovaProiezione.getFilm() == null
	                || nuovaProiezione.getFilm().getDurata() <= 0 || proiezioni == null) {
	            return false;
	        }

	        long nuovoInizio = nuovaProiezione.getDataOra().getTime();
	        long nuovoFine = nuovoInizio + (long) nuovaProiezione.getFilm().getDurata() * 60_000L;

	        for (Proiezione esistente : proiezioni) {
	            if (esistente == null || esistente == daEscludere || esistente.getDataOra() == null || esistente.getFilm() == null
	                    || esistente.getFilm().getDurata() <= 0) {
	                continue;
	            }

	            long esistenteInizio = esistente.getDataOra().getTime();
	            long esistenteFine = esistenteInizio + (long) esistente.getFilm().getDurata() * 60_000L;

	            if (nuovoInizio < esistenteFine && nuovoFine > esistenteInizio) {
	                return true;
	            }
	        }

	        return false;
	    }
	    
	    /**
		 * Cerca una proiezione nella lista per titolo del film e data/ora.
		 *
		 * @param titoloFilm titolo del film
		 * @param dataOra    data e ora della proiezione
		 * @return proiezione trovata, oppure null se non esiste
		 */
		public Proiezione trovaProiezione(String titoloFilm, Date dataOra) {
			for (int i = 0; i < this.proiezioni.size(); i++) {
				Proiezione p = this.proiezioni.get(i);
				if (p.getFilm().getTitolo().equals(titoloFilm) && p.getDataOra().equals(dataOra)) {
					return p;
				}
			}
			return null;
		}
}
