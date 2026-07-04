/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.GUI.views;

import cinemax.client.ClientCM;
import cinemax.common.Prenotazione;
import cinemax.common.Richiesta;
import cinemax.common.Risposta;
import cinemax.common.TipoOperazione;
import cinemax.common.Utente;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Componente di ricerca delle prenotazioni per i bigliettai.
 * Come da specifica, supporta la ricerca per:
 * <ul>
 * <li>Codice della prenotazione</li>
 * <li>Nome e cognome del cliente</li>
 * <li>Titolo del film (anche parziale)</li>
 * <li>Intervallo di date della proiezione</li>
 * </ul>
 * Puo' essere usata sia come pannello incorporato in un'altra schermata
 * (tramite {@link #costruisciPannello()}) sia come scena autonoma
 * (tramite {@link #costruisci()}).
 */
public class ScenaRicercaPrenotazioni {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Utente bigliettaio autenticato. */
    private final Utente utente;

    /**
     * Costruttore.
     *
     * @param utente utente bigliettaio autenticato
     */
    public ScenaRicercaPrenotazioni(Utente utente) {
        this.utente = utente;
    }

    /**
     * Costruisce il pannello di ricerca prenotazioni come nodo JavaFX.
     * Il pannello puo' essere inserito come contenuto di un {@link Tab}
     * o di qualsiasi altro contenitore.
     *
     * @return pannello JavaFX con form di ricerca e tabella risultati
     */
    public Parent costruisciPannello() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // --- Form criteri di ricerca ---
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 12;");

        int r = 0;
        form.add(new Label("Codice prenotazione:"), 0, r);
        TextField campoCodice = new TextField();
        campoCodice.setPrefWidth(160);
        form.add(campoCodice, 1, r++);

        form.add(new Label("Nome cliente:"), 0, r);
        TextField campoNome = new TextField();
        form.add(campoNome, 1, r++);

        form.add(new Label("Cognome cliente:"), 0, r);
        TextField campoCognome = new TextField();
        form.add(campoCognome, 1, r++);

        form.add(new Label("Titolo film:"), 0, r);
        TextField campoTitolo = new TextField();
        form.add(campoTitolo, 1, r++);

        form.add(new Label("Data proiezione da:"), 0, r);
        DatePicker pickerDa = new DatePicker();
        form.add(pickerDa, 1, r++);

        form.add(new Label("Data proiezione a:"), 0, r);
        DatePicker pickerA = new DatePicker();
        form.add(pickerA, 1, r++);

        Button btnCerca = new Button("Cerca");
        btnCerca.setDefaultButton(true);
        form.add(btnCerca, 0, r++, 2, 1);

        // --- Tabella risultati ---
        TableView<Prenotazione> tabella = new TableView<>();
        tabella.setPlaceholder(new Label("Inserire i criteri di ricerca e premere 'Cerca'."));

        TableColumn<Prenotazione, String> colCodice = new TableColumn<>("Codice");
        colCodice.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCodice()));
        colCodice.setPrefWidth(110);

        TableColumn<Prenotazione, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getNomeCliente() + " " + d.getValue().getCognomeCliente()));
        colCliente.setPrefWidth(155);

        TableColumn<Prenotazione, String> colFilm = new TableColumn<>("Film");
        colFilm.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getProiezione().getFilm().getTitolo()));
        colFilm.setPrefWidth(175);

        TableColumn<Prenotazione, String> colData = new TableColumn<>("Data/Ora");
        colData.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getProiezione().getDataOra().format(FMT)));
        colData.setPrefWidth(130);

        TableColumn<Prenotazione, String> colPosti = new TableColumn<>("Posti");
        colPosti.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(d.getValue().getNumPosti())));
        colPosti.setPrefWidth(60);

        TableColumn<Prenotazione, String> colTotale = new TableColumn<>("Totale");
        colTotale.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("%.2f \u20ac", d.getValue().getCostoTotale())));
        colTotale.setPrefWidth(80);

        tabella.getColumns().addAll(colCodice, colCliente, colFilm, colData, colPosti, colTotale);

        Label lblStatus = new Label();

        root.getChildren().addAll(form, lblStatus, tabella);
        VBox.setVgrow(tabella, Priority.ALWAYS);

        // --- Azione ricerca ---
        btnCerca.setOnAction(e -> {
            String codice = campoCodice.getText().trim();
            String nome = campoNome.getText().trim();
            String cognome = campoCognome.getText().trim();
            String titolo = campoTitolo.getText().trim();
            LocalDate da = pickerDa.getValue();
            LocalDate a = pickerA.getValue();

            lblStatus.setStyle("");
            lblStatus.setText("Ricerca in corso...");
            btnCerca.setDisable(true);
            tabella.setItems(FXCollections.emptyObservableList());

            Richiesta richiesta = new Richiesta(TipoOperazione.CERCA_PRENOTAZIONE);
            if (!codice.isEmpty())
                richiesta.setParametro("codice", codice);
            if (!nome.isEmpty())
                richiesta.setParametro("nome", nome);
            if (!cognome.isEmpty())
                richiesta.setParametro("cognome", cognome);
            if (!titolo.isEmpty())
                richiesta.setParametro("titolo", titolo);
            if (da != null)
                richiesta.setParametro("dataFrom", da);
            if (a != null)
                richiesta.setParametro("dataTo", a);

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
                    List<Prenotazione> prenotazioni = (List<Prenotazione>) lista;
                    tabella.setItems(FXCollections.observableArrayList(prenotazioni));
                    lblStatus.setStyle("-fx-text-fill: green;");
                    lblStatus.setText("Trovate " + prenotazioni.size() + " prenotazioni.");
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
        });

        return root;
    }

    /**
     * Costruisce e restituisce la scena di ricerca prenotazioni come schermata
     * autonoma (per eventuale navigazione diretta senza TabPane).
     *
     * @return scena JavaFX pronta per essere impostata sullo Stage
     */
    public Scene costruisci() {
        VBox wrapper = new VBox(15);
        wrapper.setPadding(new Insets(20));

        Label lblTitolo = new Label("Ricerca Prenotazioni");
        lblTitolo.setFont(Font.font("System", FontWeight.BOLD, 22));

        Button btnIndietro = new Button("Indietro");
        btnIndietro.setOnAction(e -> ClientCM.mostraScena(new ScenaMenuBigliettaio(utente).costruisci()));

        HBox boxTop = new HBox(10, btnIndietro);
        boxTop.setAlignment(Pos.CENTER_LEFT);

        Parent pannello = costruisciPannello();
        VBox.setVgrow(pannello, Priority.ALWAYS);

        wrapper.getChildren().addAll(lblTitolo, boxTop, pannello);
        return new Scene(wrapper, 860, 650);
    }
}
