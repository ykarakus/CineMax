/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.GUI.views;

import cinemax.client.ClientCM;
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

import java.time.LocalDateTime;

/**
 * Schermata per l'aggiunta di una nuova proiezione da parte del proiezionista.
 * Come da specifica, permette di specificare:
 * <ul>
 * <li>Informazioni sul film: titolo, genere, regista, anno, durata, eta'
 * minima</li>
 * <li>Dati della proiezione: data, ora, prezzo del biglietto</li>
 * </ul>
 * Il server verifica che la nuova proiezione non si sovrapponga temporalmente
 * con una proiezione esistente (tenendo conto della durata del film).
 */
public class ScenaAggiungiProiezione {

    /** Utente proiezionista autenticato. */
    private final Utente utente;

    /**
     * Costruttore.
     *
     * @param utente utente proiezionista autenticato
     */
    public ScenaAggiungiProiezione(Utente utente) {
        this.utente = utente;
    }

    /**
     * Costruisce e restituisce la scena di aggiunta proiezione.
     * Il contenuto e' racchiuso in un {@link ScrollPane} per gestire
     * schermi di altezza ridotta.
     *
     * @return scena JavaFX pronta per essere impostata sullo Stage
     */
    public Scene costruisci() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.TOP_CENTER);

        Label lblTitolo = new Label("Aggiungi nuova proiezione");
        lblTitolo.setFont(Font.font("System", FontWeight.BOLD, 22));

        // ===== Sezione informazioni film =====
        Label lblFilm = new Label("Informazioni sul film");
        lblFilm.setFont(Font.font("System", FontWeight.BOLD, 15));
        lblFilm.setStyle("-fx-text-fill: #555555;");

        GridPane formFilm = new GridPane();
        formFilm.setHgap(12);
        formFilm.setVgap(10);
        formFilm.setAlignment(Pos.CENTER_LEFT);
        formFilm.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 6; -fx-padding: 14;");

        int r = 0;
        formFilm.add(new Label("Titolo: *"), 0, r);
        TextField campoTitolo = new TextField();
        campoTitolo.setPrefWidth(260);
        formFilm.add(campoTitolo, 1, r++);

        formFilm.add(new Label("Genere: *"), 0, r);
        TextField campoGenere = new TextField();
        formFilm.add(campoGenere, 1, r++);

        formFilm.add(new Label("Regista: *"), 0, r);
        TextField campoRegista = new TextField();
        formFilm.add(campoRegista, 1, r++);

        formFilm.add(new Label("Anno: *"), 0, r);
        TextField campoAnno = new TextField();
        campoAnno.setPrefWidth(100);
        formFilm.add(campoAnno, 1, r++);

        formFilm.add(new Label("Durata (minuti): *"), 0, r);
        TextField campoDurata = new TextField();
        campoDurata.setPrefWidth(100);
        formFilm.add(campoDurata, 1, r++);

        formFilm.add(new Label("Eta' minima:"), 0, r);
        TextField campoEta = new TextField("0");
        campoEta.setPrefWidth(80);
        formFilm.add(campoEta, 1, r++);

        // ===== Sezione dati proiezione =====
        Label lblProiezione = new Label("Dati della proiezione");
        lblProiezione.setFont(Font.font("System", FontWeight.BOLD, 15));
        lblProiezione.setStyle("-fx-text-fill: #555555;");

        GridPane formProiezione = new GridPane();
        formProiezione.setHgap(12);
        formProiezione.setVgap(10);
        formProiezione.setAlignment(Pos.CENTER_LEFT);
        formProiezione.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 6; -fx-padding: 14;");

        r = 0;
        formProiezione.add(new Label("Data: *"), 0, r);
        DatePicker dataPicker = new DatePicker();
        formProiezione.add(dataPicker, 1, r++);

        formProiezione.add(new Label("Ora: *"), 0, r);
        Spinner<Integer> spinOra = new Spinner<>(0, 23, 20);
        spinOra.setPrefWidth(72);
        Spinner<Integer> spinMin = new Spinner<>(0, 59, 0, 5);
        spinMin.setPrefWidth(72);
        HBox boxOra = new HBox(5, spinOra, new Label(":"), spinMin);
        boxOra.setAlignment(Pos.CENTER_LEFT);
        formProiezione.add(boxOra, 1, r++);

        formProiezione.add(new Label("Prezzo biglietto (\u20ac): *"), 0, r);
        TextField campoPrezzo = new TextField();
        campoPrezzo.setPrefWidth(100);
        formProiezione.add(campoPrezzo, 1, r++);

        Label lblObbligatori = new Label("* campi obbligatori");
        lblObbligatori.setStyle("-fx-font-size: 11; -fx-text-fill: grey;");

        // --- Pulsanti ---
        Button btnAggiungi = new Button("Aggiungi proiezione");
        btnAggiungi.setStyle("-fx-background-color: #1565C0; -fx-text-fill: white;"
                + " -fx-font-weight: bold;");
        btnAggiungi.setDefaultButton(true);
        Button btnIndietro = new Button("Indietro");

        HBox boxBtn = new HBox(15, btnIndietro, btnAggiungi);
        boxBtn.setAlignment(Pos.CENTER);

        Label lblMessaggio = new Label();
        lblMessaggio.setWrapText(true);
        lblMessaggio.setMaxWidth(500);

        root.getChildren().addAll(
                lblTitolo,
                lblFilm, formFilm,
                lblProiezione, formProiezione,
                lblObbligatori, boxBtn, lblMessaggio);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);

        // --- Azioni ---

        btnAggiungi.setOnAction(e -> {
            String titolo = campoTitolo.getText().trim();
            String genere = campoGenere.getText().trim();
            String regista = campoRegista.getText().trim();
            String prezzoStr = campoPrezzo.getText().trim().replace(',', '.');
            java.time.LocalDate data = dataPicker.getValue();

            if (titolo.isEmpty() || genere.isEmpty() || regista.isEmpty()
                    || campoAnno.getText().trim().isEmpty()
                    || campoDurata.getText().trim().isEmpty()
                    || prezzoStr.isEmpty() || data == null) {
                lblMessaggio.setStyle("-fx-text-fill: red;");
                lblMessaggio.setText("Compilare tutti i campi obbligatori.");
                return;
            }

            int anno, durata, eta;
            double prezzo;
            try {
                anno = Integer.parseInt(campoAnno.getText().trim());
                durata = Integer.parseInt(campoDurata.getText().trim());
                eta = Integer.parseInt(campoEta.getText().trim());
                prezzo = Double.parseDouble(prezzoStr);
                if (prezzo <= 0 || durata <= 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                lblMessaggio.setStyle("-fx-text-fill: red;");
                lblMessaggio.setText("Valori numerici non validi (anno, durata e prezzo devono essere positivi).");
                return;
            }

            LocalDateTime dataOra = data.atTime(spinOra.getValue(), spinMin.getValue());
            lblMessaggio.setText("");
            btnAggiungi.setDisable(true);

            Richiesta richiesta = new Richiesta(TipoOperazione.AGGIUNGI_PROIEZIONE);
            richiesta.setParametro("titolo", titolo);
            richiesta.setParametro("genere", genere);
            richiesta.setParametro("regista", regista);
            richiesta.setParametro("anno", anno);
            richiesta.setParametro("durataMinuti", durata);
            richiesta.setParametro("etaMinima", eta);
            richiesta.setParametro("dataOra", dataOra);
            richiesta.setParametro("prezzoBiglietto", prezzo);

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
                    lblMessaggio.setText("Proiezione aggiunta con successo!");
                    // Pulsante disabilitato per evitare duplicati
                } else {
                    lblMessaggio.setStyle("-fx-text-fill: red;");
                    lblMessaggio.setText(risposta.getMessaggio());
                    btnAggiungi.setDisable(false);
                }
            });

            task.setOnFailed(ev -> {
                lblMessaggio.setStyle("-fx-text-fill: red;");
                lblMessaggio.setText("Errore di rete: " + task.getException().getMessage());
                btnAggiungi.setDisable(false);
            });

            new Thread(task).start();
        });

        btnIndietro.setOnAction(e -> ClientCM.mostraScena(new ScenaMenuProiezionista(utente).costruisci()));

        return new Scene(scroll, 760, 650);
    }
}
