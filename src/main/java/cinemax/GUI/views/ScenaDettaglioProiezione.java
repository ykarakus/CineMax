/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.GUI.views;

import cinemax.client.ClientCM;
import cinemax.common.Proiezione;
import cinemax.common.Utente;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;

/**
 * Schermata di visualizzazione dei dettagli di una proiezione selezionata.
 * Mostra, come richiesto dalla specifica:
 * <ul>
 * <li>Le caratteristiche del film (titolo, genere, regista, anno, durata,
 * eta' minima del pubblico)</li>
 * <li>La data e l'ora della proiezione</li>
 * <li>Il costo del biglietto</li>
 * <li>Il numero di posti liberi (calcolato dal server come differenza
 * tra la capienza della sala e la somma delle prenotazioni)</li>
 * </ul>
 * Se l'utente e' un cliente autenticato e ci sono posti disponibili,
 * viene mostrato il pulsante per creare una prenotazione.
 */
public class ScenaDettaglioProiezione {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Proiezione di cui visualizzare i dettagli. */
    private final Proiezione proiezione;

    /** Utente corrente (null se guest). */
    private final Utente utente;

    /**
     * Costruttore.
     *
     * @param proiezione proiezione selezionata dai risultati di ricerca
     * @param utente     utente corrente (null se guest)
     */
    public ScenaDettaglioProiezione(Proiezione proiezione, Utente utente) {
        this.proiezione = proiezione;
        this.utente = utente;
    }

    /**
     * Costruisce e restituisce la scena con i dettagli della proiezione.
     *
     * @return scena JavaFX pronta per essere impostata sullo Stage
     */
    public Scene costruisci() {
        VBox root = new VBox(18);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);

        Label lblTitoloFilm = new Label(proiezione.getFilm().getTitolo());
        lblTitoloFilm.setFont(Font.font("System", FontWeight.BOLD, 28));

        Separator sep1 = new Separator();

        // --- Sezione informazioni sul film ---
        Label lblSezioneFilm = new Label("Informazioni sul film");
        lblSezioneFilm.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblSezioneFilm.setStyle("-fx-text-fill: #555555;");

        GridPane infoFilm = new GridPane();
        infoFilm.setHgap(20);
        infoFilm.setVgap(9);
        infoFilm.setAlignment(Pos.CENTER);

        int r = 0;
        aggiungiRiga(infoFilm, "Genere:", proiezione.getFilm().getGenere(), r++);
        aggiungiRiga(infoFilm, "Regista:", proiezione.getFilm().getRegista(), r++);
        aggiungiRiga(infoFilm, "Anno:", String.valueOf(proiezione.getFilm().getAnno()), r++);
        aggiungiRiga(infoFilm, "Durata:", proiezione.getFilm().getDurataMinuti() + " minuti", r++);
        aggiungiRiga(infoFilm, "Eta' minima:", proiezione.getFilm().getEtaMinima() + " anni", r++);

        Separator sep2 = new Separator();

        // --- Sezione informazioni sulla proiezione ---
        Label lblSezioneProiezione = new Label("Dettagli proiezione");
        lblSezioneProiezione.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblSezioneProiezione.setStyle("-fx-text-fill: #555555;");

        GridPane infoProiezione = new GridPane();
        infoProiezione.setHgap(20);
        infoProiezione.setVgap(9);
        infoProiezione.setAlignment(Pos.CENTER);

        r = 0;
        aggiungiRiga(infoProiezione, "Data e ora:", proiezione.getDataOra().format(FMT), r++);
        aggiungiRiga(infoProiezione, "Prezzo biglietto:",
                String.format("%.2f \u20ac", proiezione.getPrezzoBiglietto()), r++);

        // Numero posti con evidenziazione colorata
        int postiLiberi = proiezione.getPostiLiberi();
        Label lblPostiVal = new Label(postiLiberi + " / " + Proiezione.CAPIENZA_SALA);
        if (postiLiberi == 0) {
            lblPostiVal.setText("ESAURITO");
            lblPostiVal.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else if (postiLiberi <= 20) {
            lblPostiVal.setStyle("-fx-text-fill: #e65100; -fx-font-weight: bold;");
        } else {
            lblPostiVal.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
        }

        Label lblPostiKey = new Label("Posti liberi:");
        lblPostiKey.setFont(Font.font("System", FontWeight.BOLD, 13));
        infoProiezione.add(lblPostiKey, 0, r);
        infoProiezione.add(lblPostiVal, 1, r++);

        // --- Pulsanti ---
        HBox boxBtn = new HBox(15);
        boxBtn.setAlignment(Pos.CENTER);

        Button btnIndietro = new Button("Indietro");
        btnIndietro.setOnAction(e -> ClientCM.mostraScena(new ScenaRicercaProiezioni(utente, "").costruisci()));
        boxBtn.getChildren().add(btnIndietro);

        // Il pulsante "Prenota" e' visibile solo ai clienti autenticati con posti
        // disponibili
        if (utente != null && Utente.RUOLO_CLIENTE.equals(utente.getRuolo()) && postiLiberi > 0) {
            Button btnPrenota = new Button("Prenota");
            btnPrenota.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;"
                    + " -fx-font-weight: bold;");
            btnPrenota
                    .setOnAction(e -> ClientCM.mostraScena(new ScenaCreaPrenotazione(proiezione, utente).costruisci()));
            boxBtn.getChildren().add(btnPrenota);
        }

        root.getChildren().addAll(
                lblTitoloFilm, sep1,
                lblSezioneFilm, infoFilm, sep2,
                lblSezioneProiezione, infoProiezione,
                boxBtn);

        return new Scene(root, 700, 520);
    }

    /**
     * Aggiunge una coppia etichetta-valore al GridPane fornito.
     *
     * @param grid   griglia di destinazione
     * @param chiave testo dell'etichetta (in grassetto)
     * @param valore testo del valore
     * @param riga   indice di riga nella griglia
     */
    private void aggiungiRiga(GridPane grid, String chiave, String valore, int riga) {
        Label lblKey = new Label(chiave);
        lblKey.setFont(Font.font("System", FontWeight.BOLD, 13));
        Label lblVal = new Label(valore);
        lblVal.setFont(Font.font("System", 13));
        grid.add(lblKey, 0, riga);
        grid.add(lblVal, 1, riga);
    }
}
