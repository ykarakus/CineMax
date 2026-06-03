package cinemax.Managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cinemax.Models.Proiezione;
import cinemax.Models.Film;
import cinemax.Helpers.*;
import cinemax.ViewModels.ProiezioneSearch;

public class ProiezioneManager {

	public List<Proiezione> proiezioni;

	public ProiezioneManager() {

		CSVReader csvReader = new CSVReader();

		try {
			// Read and parse the CSV file into Proiezione objects
			this.proiezioni = csvReader.readFile("data/proiezioni.csv", values -> {
				// CSV column mapping:
				// 0: data_ora_proiezione
				// 1: titolo_film
				// 2: genere
				// 3: regista
				// 4: anno
				// 5: durata_minuti
				// 6: eta_minima
				// 7: prezzo_biglietto

				// Parse the date/time
				Date dataOra = CSVReader.parseDate(values.get(0), "yyyy-MM-dd HH:mm:ss");

				// Create Film object from CSV data
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

	public List<Proiezione> cercaProiezione(ProiezioneSearch proSearchCriteria) {
		try {
			return proiezioni.stream().filter(proiezione -> {
				// Filter 1: By title (if titolo is not null)
				var titolo = proSearchCriteria.getTitolo();
				if (titolo != null && !titolo.trim().isEmpty()) {
					boolean matchesTitle = proiezione.getFilm() != null && proiezione.getFilm().getTitolo() != null
							&& proiezione.getFilm().getTitolo().trim().toLowerCase()
									.contains(titolo.trim().toLowerCase());
					if (!matchesTitle)
						return false;
				}

				// Filter 2: By tipologia/genre (if tipologia is not null)
				var tipologia = proSearchCriteria.getTipologia();
				if (tipologia != null && !tipologia.trim().isEmpty()) {
					boolean matchesTipologia = proiezione.getFilm() != null && proiezione.getFilm().getGenere() != null
							&& proiezione.getFilm().getGenere().trim().equalsIgnoreCase(tipologia.trim());
					if (!matchesTipologia)
						return false;
				}

				// Filter 3: By date range (if dates are provided)
				var dataInizio = proSearchCriteria.getData_inizio();
				var dataFine = proSearchCriteria.getData_fine();

				if (dataInizio != null || dataFine != null) {
					Date dataProiezione = proiezione.getData_ora();
					if (dataProiezione == null)
						return false;

					if (dataInizio != null && dataProiezione.before(dataInizio)) {
						return false;
					}

					if (dataFine != null && dataProiezione.after(dataFine)) {
						return false;
					}
				}

				// Filter 4: By ticket price (if costoBiglietto is not null)
				var costoBiglietto = proSearchCriteria.getCosto_biglietto();
				if (costoBiglietto != null) {
					Double costoProiezione = proiezione.getCosto_biglietto();
					if (costoProiezione == null)
						return false;

					if (Math.abs(costoProiezione - costoBiglietto) > 0.01) {
						return false;
					}
				}

				return true;
			}).collect(Collectors.toList());

		} catch (Exception ex) {
			System.err.println("Error searching for projections: " + ex.getMessage());
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

	public void vsualizzaProiezione() {
			
	}
}
