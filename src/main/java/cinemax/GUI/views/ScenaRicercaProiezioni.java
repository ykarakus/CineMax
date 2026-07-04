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
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Schermata di ricerca delle proiezioni disponibili.
 * Accessibile sia agli utenti guest (con titolo film pre-compilato)
 * che ai clienti autenticati. Supporta la ricerca per:
 * <ul>
 * <li>Titolo del film (anche parziale)</li>
 * <li>Genere del film</li>
 * <li>Intervallo di date</li>
 * <li>Fascia di prezzo del biglietto</li>
 * <li>Qualsiasi combinazione dei criteri precedenti</li>
 * </ul>
 * Se viene passato un titolo pre-compilato (accesso guest), la ricerca
 * viene eseguita automaticamente all'apertura della schermata,
 * mostrando le proiezioni nei tre mesi successivi alla data odierna.
 */
public class ScenaRicercaProiezioni {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Utente corrente (null se guest). */
    private final Utente utente;

    /** Titolo pre-compilato proveniente dall'accesso guest (puo' essere vuoto). */
    private final String titoloPre;

    /**
     * Costruttore.
     *
     * @param utente    utente autenticato, oppure null se guest
     * @param titoloPre titolo del film pre-compilato; puo' essere null o vuoto
     */
    public ScenaRicercaProiezioni(Utente utente, String titoloPre) {
        this.utente = utente;
        this.titoloPre = titoloPre != null ? titoloPre : "";
    }

    /**
     * Costruisce e restituisce la scena di ricerca delle proiezioni.
     *
     * @return scena JavaFX pronta per essere impostata sullo Stage
     */
    public Scene costruisci() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(20));

        Label lblTitolo = new Label("Ricerca Proiezioni");
        lblTitolo.setFont(Font.font("System", FontWeight.BOLD, 22));

        // --- Form criteri di ricerca ---
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(12));
        form.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5;");

        int r = 0;
        form.add(new Label("Titolo film:"), 0, r);
        TextField campoTitolo = new TextField(titoloPre);
        campoTitolo.setPrefWidth(200);
        form.add(campoTitolo, 1, r++);

        form.add(new Label("Genere:"), 0, r);
        TextField campoGenere = new TextField();
        form.add(campoGenere, 1, r++);

        form.add(new Label("Data da:"), 0, r);
        DatePicker pickerDa = new DatePicker();
        form.add(pickerDa, 1, r++);

        form.add(new Label("Data a:"), 0, r);
        DatePicker pickerA = new DatePicker();
        form.add(pickerA, 1, r++);

        form.add(new Label("Prezzo min (€):"), 0, r);
        TextField campoPrezzoMin = new TextField();
        campoPrezzoMin.setPrefWidth(100);
        form.add(campoPrezzoMin, 1, r++);

        form.add(new Label("Prezzo max (€):"), 0, r);
        TextField campoPrezzoMax = new TextField();
        campoPrezzoMax.setPrefWidth(100);
        form.add(campoPrezzoMax, 1, r++);

        Button btnCerca = new Button("Cerca");
        btnCerca.setDefaultButton(true);
        Button btnIndietro = new Button("Indietro");

        HBox boxBtnCerca = new HBox(10, btnIndietro, btnCerca);
        boxBtnCerca.setAlignment(Pos.CENTER_LEFT);
        form.add(boxBtnCerca, 0, r++, 2, 1);

        // --- Tabella risultati ---
        TableView<Proiezione> tabella = new TableView<>();
        tabella.setPlaceholder(new Label("Inserire i criteri di ricerca e premere 'Cerca'."));

        TableColumn<Proiezione, String> colFilm = new TableColumn<>("Film");
        colFilm.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFilm().getTitolo()));
        colFilm.setPrefWidth(200);

        TableColumn<Proiezione, String> colGenere = new TableColumn<>("Genere");
        colGenere.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFilm().getGenere()));
        colGenere.setPrefWidth(100);

        TableColumn<Proiezione, String> colData = new TableColumn<>("Data/Ora");
        colData.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getDataOra().format(FMT)));
        colData.setPrefWidth(130);

        TableColumn<Proiezione, String> colPrezzo = new TableColumn<>("Biglietto");
        colPrezzo.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("%.2f \u20ac", d.getValue().getPrezzoBiglietto())));
        colPrezzo.setPrefWidth(90);

        TableColumn<Proiezione, String> colPosti = new TableColumn<>("Posti liberi");
        colPosti.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getPostiLiberi() + " / " + Proiezione.CAPIENZA_SALA));
        colPosti.setPrefWidth(100);

        tabella.getColumns().addAll(colFilm, colGenere, colData, colPrezzo, colPosti);

        Button btnDettagli = new Button("Visualizza dettagli");
        btnDettagli.setDisable(true);

        Label lblStatus = new Label();

        tabella.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> btnDettagli.setDisable(sel == null));

        root.getChildren().addAll(lblTitolo, form, lblStatus, tabella, btnDettagli);
        VBox.setVgrow(tabella, Priority.ALWAYS);

        // --- Logica di ricerca ---

        Runnable eseguiRicerca = () -> {
            String titoloVal = campoTitolo.getText().trim();
            String genereVal = campoGenere.getText().trim();
            LocalDate daVal = pickerDa.getValue();
            LocalDate aVal = pickerA.getValue();

            Double prezzoMin = null, prezzoMax = null;
            try {
                if (!campoPrezzoMin.getText().trim().isEmpty())
                    prezzoMin = Double.parseDouble(campoPrezzoMin.getText().trim().replace(',', '.'));
            } catch (NumberFormatException ex) {
                lblStatus.setStyle("-fx-text-fill: red;");
                lblStatus.setText("Valore non valido nel campo 'Prezzo min'.");
                return;
            }
            try {
                if (!campoPrezzoMax.getText().trim().isEmpty())
                    prezzoMax = Double.parseDouble(campoPrezzoMax.getText().trim().replace(',', '.'));
            } catch (NumberFormatException ex) {
                lblStatus.setStyle("-fx-text-fill: red;");
                lblStatus.setText("Valore non valido nel campo 'Prezzo max'.");
                return;
            }

            lblStatus.setStyle("");
            lblStatus.setText("Ricerca in corso...");
            btnCerca.setDisable(true);
            tabella.setItems(FXCollections.emptyObservableList());

            final Double pMin = prezzoMin;
            final Double pMax = prezzoMax;

            Richiesta richiesta = new Richiesta(TipoOperazione.CERCA_PROIEZIONE);
            if (!titoloVal.isEmpty())
                richiesta.setParametro("titolo", titoloVal);
            if (!genereVal.isEmpty())
                richiesta.setParametro("genere", genereVal);
            if (daVal != null)
                richiesta.setParametro("dataFrom", daVal);
            if (aVal != null)
                richiesta.setParametro("dataTo", aVal);
            if (pMin != null)
                richiesta.setParametro("prezzoMin", pMin);
            if (pMax != null)
                richiesta.setParametro("prezzoMax", pMax);

            // Per l'accesso guest: limita automaticamente ai tre mesi successivi
            if (utente == null && daVal == null) {
                richiesta.setParametro("dataFrom", LocalDate.now());
                richiesta.setParametro("dataTo", LocalDate.now().plusMonths(3));
            }

            Task<Risposta> task = new Task<>() {
                @Override
                protected Risposta call() throws Exception {
                    return ClientCM.getConnessione().invia(richiesta);
                }
            };

            task.setOnSucceeded(ev -> {
                btnCerca.setDisable(false);
                Risposta risposta = task.getValue();
                if (risposta.isSuccesso() && risposta.getDati() instanceof List<?> lista) {
                    @SuppressWarnings("unchecked")
                    List<Proiezione> proiezioni = (List<Proiezione>) lista;
                    tabella.setItems(FXCollections.observableArrayList(proiezioni));
                    lblStatus.setStyle("-fx-text-fill: green;");
                    lblStatus.setText("Trovate " + proiezioni.size() + " proiezioni.");
                } else {
                    lblStatus.setStyle("-fx-text-fill: red;");
                    lblStatus.setText(risposta.getMessaggio());
                }
            });

            task.setOnFailed(ev -> {
                btnCerca.setDisable(false);
                lblStatus.setStyle("-fx-text-fill: red;");
                lblStatus.setText("Errore di rete: " + task.getException().getMessage());
            });

            new Thread(task).start();
        };

        btnCerca.setOnAction(e -> eseguiRicerca.run());

        btnDettagli.setOnAction(e -> {
            Proiezione sel = tabella.getSelectionModel().getSelectedItem();
            if (sel != null) {
                ClientCM.mostraScena(new ScenaDettaglioProiezione(sel, utente).costruisci());
            }
        });

        btnIndietro.setOnAction(e -> {
            if (utente == null) {
                ClientCM.mostraScena(new ScenaIniziale().costruisci());
            } else {
                switch (utente.getRuolo()) {
                    case Utente.RUOLO_CLIENTE ->
                        ClientCM.mostraScena(new ScenaMenuCliente(utente).costruisci());
                    default ->
                        ClientCM.mostraScena(new ScenaIniziale().costruisci());
                }
            }
        });

        // Ricerca automatica al lancio (accesso guest con titolo fornito, oppure click
        // Cerca)
        if (!titoloPre.isEmpty()) {
            javafx.application.Platform.runLater(eseguiRicerca::run);
        }

        return new Scene(root, 820, 640);
    }
}
