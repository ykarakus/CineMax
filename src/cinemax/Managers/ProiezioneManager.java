package cinemax.Managers;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import cinemax.Models.Proiezione;
import cinemax.Models.Film;
import cinemax.Helpers.*;

public class ProiezioneManager {


	public List<Proiezione> proiezioni;
	
	
	public ProiezioneManager() {
		
		CSVReader csvReader = new CSVReader();
        
        
		try {
            // Read and parse the CSV file into Proiezione objects
           this.proiezioni = csvReader.readFile("src/data/proiezioni.csv", values -> {
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
                Film film = new Film(
                    values.get(1),  // titolo
                    values.get(2),  // genere
                    values.get(3),  // regista
                    CSVReader.parseInt(values.get(4)),   // anno
                    CSVReader.parseInt(values.get(5)),   // durata
                    CSVReader.parseInt(values.get(6))    // eta_minima
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
}
