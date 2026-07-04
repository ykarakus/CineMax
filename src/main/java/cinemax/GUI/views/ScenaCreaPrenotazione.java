/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.GUI.views;

import cinemax.client.ClientCM;
import cinemax.common.Proiezione;
import cinemax.common.Richiesta;
import cinemax.common.Risposta;
import cinemax.common.TipoOperazione;
import cinemax.common.Utente;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;

/**
 * Schermata per la creazione di una nuova prenotazione per una proiezione.
 * Il cliente specifica il numero di posti da prenotare; il server verifica
 * che i posti richiesti siano effettivamente disponibili e, in caso di
 * successo, genera un codice univoco per la prenotazione.
 * Il totale viene aggiornato dinamicamente al variare del numero di posti.
 */
public class ScenaCreaPrenotazione {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Proiezione per cui si effettua la prenotazione. */
    private final Proiezione proiezione;

    /** Utente cliente che effettua la prenotazione. */
    private final Utente utente;

    /**
     * Costruttore.
     *
     * @param proiezione proiezione selezionata
     * @param utente     utente cliente autenticato
     */
    public ScenaCreaPrenotazione(Proiezione proiezione, Utente utente) {
        this.proiezione = proiezione;
        this.utente = utente;
    }

    /**
     * Costruisce e restituisce la scena di creazione prenotazione.
     *
     * @return scena JavaFX pronta per essere impostata sullo Stage
     */
    public Scene costruisci() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);

        Label lblTitolo = new Label("Nuova prenotazione");
        lblTitolo.setFont(Font.font("System", FontWeight.BOLD, 22));

        // --- Riepilogo proiezione ---
        GridPane riepilogo = new GridPane();
        riepilogo.setHgap(20);
        riepilogo.setVgap(10);
        riepilogo.setAlignment(Pos.CENTER);
        riepilogo.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 6; -fx-padding: 18;"
                + " -fx-background-color: #f9f9f9; -fx-background-radius: 6;");

        int r = 0;
        aggiungiRiga(riepilogo, "Film:", proiezione.getFilm().getTitolo(), r++);
        aggiungiRiga(riepilogo, "Data/Ora:", proiezione.getDataOra().format(FMT), r++);
        aggiungiRiga(riepilogo, "Prezzo biglietto:",
                String.format("%.2f \u20ac", proiezione.getPrezzoBiglietto()), r++);
        aggiungiRiga(riepilogo, "Posti disponibili:",
                String.valueOf(proiezione.getPostiLiberi()), r++);

        Separator sep = new Separator();

        // --- Selezione numero posti ---
        HBox rigaPosti = new HBox(12);
        rigaPosti.setAlignment(Pos.CENTER);

        Label lblPosti = new Label("Numero di posti:");
        lblPosti.setFont(Font.font("System", FontWeight.BOLD, 13));

        Spinner<Integer> spinnerPosti = new Spinner<>(1, proiezione.getPostiLiberi(), 1);
        spinnerPosti.setEditable(true);
        spinnerPosti.setPrefWidth(85);

        rigaPosti.getChildren().addAll(lblPosti, spinnerPosti);

        // Totale aggiornato dinamicamente
        Label lblTotale = new Label(
                "Totale: " + String.format("%.2f \u20ac", proiezione.getPrezzoBiglietto()));
        lblTotale.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblTotale.setStyle("-fx-text-fill: #1565C0;");

        spinnerPosti.valueProperty()
                .addListener((obs, old, val) -> lblTotale.setText("Totale: " + String.format("%.2f \u20ac",
                        val * proiezione.getPrezzoBiglietto())));

        // --- Pulsanti ---
        Button btnConferma = new Button("Conferma prenotazione");
        btnConferma.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;"
                + " -fx-font-weight: bold;");
        btnConferma.setDefaultButton(true);

        Button btnIndietro = new Button("Indietro");

        HBox boxBtn = new HBox(15, btnIndietro, btnConferma);
        boxBtn.setAlignment(Pos.CENTER);

        Label lblMessaggio = new Label();
        lblMessaggio.setWrapText(true);
        lblMessaggio.setMaxWidth(500);

        root.getChildren().addAll(lblTitolo, riepilogo, sep, rigaPosti, lblTotale, boxBtn, lblMessaggio);

        // --- Azioni ---

        btnConferma.setOnAction(e -> {
            int numPosti = spinnerPosti.getValue();
            lblMessaggio.setText("");
            btnConferma.setDisable(true);

            Richiesta richiesta = new Richiesta(TipoOperazione.CREA_PRENOTAZIONE);
            richiesta.setParametro("username", utente.getUsername());
            richiesta.setParametro("idProiezione", proiezione.getId());
            richiesta.setParametro("numPosti", numPosti);

            Task<Risposta> task = new Task<>() {
                @Override
                protected Risposta call() throws Exception {
                    return ClientCM.getConnessione().invia(richiesta);
                }
            };

            task.setOnSucceeded(ev -> {
                Risposta risposta = task.getValue();
                if (risposta.isSuccesso()) {
                    lblMessaggio.setStyle("-fx-text-fill: green;");
                    String codice = risposta.getDati() != null
                            ? risposta.getDati().toString()
                            : "N/D";
                    lblMessaggio.setText(
                            "Prenotazione effettuata con successo!\nCodice: " + codice);
                    // Pulsante disabilitato per evitare doppia prenotazione
                } else {
                    lblMessaggio.setStyle("-fx-text-fill: red;");
                    lblMessaggio.setText(risposta.getMessaggio());
                    btnConferma.setDisable(false);
                }
            });

            task.setOnFailed(ev -> {
                lblMessaggio.setStyle("-fx-text-fill: red;");
                lblMessaggio.setText("Errore di rete: " + task.getException().getMessage());
                btnConferma.setDisable(false);
            });

            new Thread(task).start();
        });

        btnIndietro
                .setOnAction(e -> ClientCM.mostraScena(new ScenaDettaglioProiezione(proiezione, utente).costruisci()));

        return new Scene(root, 700, 500);
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
        Label key = new Label(chiave);
        key.setFont(Font.font("System", FontWeight.BOLD, 13));
        grid.add(key, 0, riga);
        grid.add(new Label(valore), 1, riga);
    }
}
