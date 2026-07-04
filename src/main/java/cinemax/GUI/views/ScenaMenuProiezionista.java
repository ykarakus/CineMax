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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Schermata del menu principale per un utente con ruolo 'proiezionista'.
 * Come da specifica, permette di:
 * <ul>
 * <li>Visualizzare le proiezioni pianificate (future) e storiche (passate)</li>
 * <li>Aggiungere una nuova proiezione (se non si sovrappone ad altre)</li>
 * <li>Modificare la data/ora di una proiezione futura (se non ha
 * prenotazioni)</li>
 * <li>Eliminare una proiezione futura (se non ha prenotazioni)</li>
 * <li>Effettuare il logout</li>
 * </ul>
 */
public class ScenaMenuProiezionista {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Utente proiezionista autenticato. */
    private final Utente utente;

    /**
     * Costruttore.
     *
     * @param utente utente proiezionista autenticato
     */
    public ScenaMenuProiezionista(Utente utente) {
        this.utente = utente;
    }

    /**
     * Costruisce e restituisce la scena del menu proiezionista.
     *
     * @return scena JavaFX pronta per essere impostata sullo Stage
     */
    public Scene costruisci() {
        VBox root = new VBox(14);
        root.setPadding(new Insets(20));

        // --- Intestazione ---
        HBox intestazione = new HBox();
        intestazione.setAlignment(Pos.CENTER_LEFT);
        Label lblBenvenuto = new Label(
                "Proiezionista: " + utente.getNome() + " " + utente.getCognome());
        lblBenvenuto.setFont(Font.font("System", FontWeight.BOLD, 20));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        intestazione.getChildren().addAll(lblBenvenuto, spacer, btnLogout);

        Separator sep = new Separator();

        // --- Toggle proiezioni future / storiche ---
        ToggleGroup gruppo = new ToggleGroup();
        RadioButton rbFuture = new RadioButton("Proiezioni pianificate (future)");
        RadioButton rbStoriche = new RadioButton("Proiezioni storiche (passate)");
        rbFuture.setToggleGroup(gruppo);
        rbStoriche.setToggleGroup(gruppo);
        rbFuture.setSelected(true);

        HBox boxToggle = new HBox(20, rbFuture, rbStoriche);
        boxToggle.setAlignment(Pos.CENTER_LEFT);

        // --- Pulsanti azioni ---
        Button btnAggiungi = new Button("+ Aggiungi proiezione");
        btnAggiungi.setStyle("-fx-background-color: #1565C0; -fx-text-fill: white;");
        Button btnRicarica = new Button("Aggiorna");

        HBox boxAzioni = new HBox(10, btnAggiungi, btnRicarica);
        boxAzioni.setAlignment(Pos.CENTER_LEFT);

        // --- Tabella ---
        TableView<Proiezione> tabella = new TableView<>();
        tabella.setPlaceholder(new Label("Nessuna proiezione."));

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

        // --- Pulsanti gestione riga ---
        Button btnModifica = new Button("Modifica data/ora");
        Button btnElimina = new Button("Elimina");
        btnElimina.setStyle("-fx-text-fill: red;");
        btnModifica.setDisable(true);
        btnElimina.setDisable(true);

        tabella.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            boolean abilitato = sel != null && rbFuture.isSelected();
            btnModifica.setDisable(!abilitato);
            btnElimina.setDisable(!abilitato);
        });

        HBox boxGestione = new HBox(10, btnModifica, btnElimina);
        boxGestione.setAlignment(Pos.CENTER_LEFT);

        Label lblStatus = new Label();

        root.getChildren().addAll(intestazione, sep, boxToggle, boxAzioni,
                tabella, boxGestione, lblStatus);
        VBox.setVgrow(tabella, Priority.ALWAYS);

        // --- Logica caricamento palinsesto ---

        Runnable caricaPalinsesto = () -> {
            lblStatus.setStyle("");
            lblStatus.setText("Caricamento...");
            String tipo = rbFuture.isSelected() ? "future" : "storiche";

            Richiesta r = new Richiesta(TipoOperazione.VISUALIZZA_PALINSESTO);
            r.setParametro("tipo", tipo);

            Task<Risposta> task = new Task<>() {
                @Override
                protected Risposta call() throws Exception {
                    return ClientCM.getConnessione().invia(r);
                }
            };

            task.setOnSucceeded(ev -> {
                Risposta risposta = task.getValue();
                if (risposta.isSuccesso() && risposta.getDati() instanceof List<?> lista) {
                    @SuppressWarnings("unchecked")
                    List<Proiezione> proiezioni = (List<Proiezione>) lista;
                    tabella.setItems(FXCollections.observableArrayList(proiezioni));
                    lblStatus.setStyle("-fx-text-fill: green;");
                    lblStatus.setText(proiezioni.size() + " proiezioni caricate.");
                } else {
                    lblStatus.setStyle("-fx-text-fill: red;");
                    lblStatus.setText(risposta.getMessaggio());
                }
            });

            task.setOnFailed(ev -> {
                lblStatus.setStyle("-fx-text-fill: red;");
                lblStatus.setText("Errore di rete: " + task.getException().getMessage());
            });

            new Thread(task).start();
        };

        // Quando si cambia radio, ricarica e resetta selezione
        rbFuture.setOnAction(e -> {
            caricaPalinsesto.run();
            btnModifica.setDisable(true);
            btnElimina.setDisable(true);
        });
        rbStoriche.setOnAction(e -> {
            caricaPalinsesto.run();
            btnModifica.setDisable(true);
            btnElimina.setDisable(true);
        });

        btnRicarica.setOnAction(e -> caricaPalinsesto.run());
        btnAggiungi.setOnAction(e -> ClientCM.mostraScena(new ScenaAggiungiProiezione(utente).costruisci()));

        btnModifica.setOnAction(e -> {
            Proiezione sel = tabella.getSelectionModel().getSelectedItem();
            if (sel != null)
                mostraDialogModifica(sel, caricaPalinsesto, lblStatus);
        });

        btnElimina.setOnAction(e -> {
            Proiezione sel = tabella.getSelectionModel().getSelectedItem();
            if (sel != null)
                mostraDialogElimina(sel, caricaPalinsesto, lblStatus);
        });

        btnLogout.setOnAction(e -> eseguiLogout());

        javafx.application.Platform.runLater(caricaPalinsesto::run);

        return new Scene(root, 860, 620);
    }

    /**
     * Mostra un dialogo per modificare la data/ora di una proiezione.
     * Il server verifica che non ci siano prenotazioni per quella proiezione.
     *
     * @param proiezione proiezione da modificare
     * @param dopoOperaz azione dopo la modifica riuscita
     * @param lblStatus  label per i messaggi di stato
     */
    private void mostraDialogModifica(Proiezione proiezione,
            Runnable dopoOperaz, Label lblStatus) {
        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle("Modifica proiezione");
        dialog.setHeaderText("Film: " + proiezione.getFilm().getTitolo());

        ButtonType btnOk = new ButtonType("Conferma", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnOk, ButtonType.CANCEL);

        DatePicker picker = new DatePicker(proiezione.getDataOra().toLocalDate());
        Spinner<Integer> spinOra = new Spinner<>(0, 23, proiezione.getDataOra().getHour());
        Spinner<Integer> spinMin = new Spinner<>(0, 59, proiezione.getDataOra().getMinute(), 5);
        spinOra.setPrefWidth(72);
        spinMin.setPrefWidth(72);

        HBox boxOra = new HBox(5, spinOra, new Label(":"), spinMin);
        boxOra.setAlignment(Pos.CENTER_LEFT);

        GridPane content = new GridPane();
        content.setHgap(10);
        content.setVgap(8);
        content.setPadding(new Insets(10));
        content.add(new Label("Nuova data:"), 0, 0);
        content.add(picker, 1, 0);
        content.add(new Label("Ora:"), 0, 1);
        content.add(boxOra, 1, 1);

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(bt -> {
            if (bt == btnOk && picker.getValue() != null)
                return picker.getValue().atTime(spinOra.getValue(), spinMin.getValue());
            return null;
        });

        dialog.showAndWait().ifPresent(nuovaDataOra -> {
            Richiesta r = new Richiesta(TipoOperazione.MODIFICA_PROIEZIONE);
            r.setParametro("idProiezione", proiezione.getId());
            r.setParametro("nuovaDataOra", nuovaDataOra);

            Task<Risposta> task = new Task<>() {
                @Override
                protected Risposta call() throws Exception {
                    return ClientCM.getConnessione().invia(r);
                }
            };

            task.setOnSucceeded(ev -> {
                Risposta risposta = task.getValue();
                if (risposta.isSuccesso()) {
                    lblStatus.setStyle("-fx-text-fill: green;");
                    lblStatus.setText("Proiezione modificata.");
                    dopoOperaz.run();
                } else {
                    lblStatus.setStyle("-fx-text-fill: red;");
                    lblStatus.setText(risposta.getMessaggio());
                }
            });

            task.setOnFailed(ev -> {
                lblStatus.setStyle("-fx-text-fill: red;");
                lblStatus.setText("Errore di rete: " + task.getException().getMessage());
            });

            new Thread(task).start();
        });
    }

    /**
     * Mostra un dialogo di conferma per eliminare una proiezione.
     * Il server verifica che non ci siano prenotazioni per quella proiezione.
     *
     * @param proiezione proiezione da eliminare
     * @param dopoOperaz azione dopo l'eliminazione riuscita
     * @param lblStatus  label per i messaggi di stato
     */
    private void mostraDialogElimina(Proiezione proiezione,
            Runnable dopoOperaz, Label lblStatus) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Elimina proiezione");
        alert.setHeaderText("Eliminare la proiezione?");
        alert.setContentText(
                "Film: " + proiezione.getFilm().getTitolo()
                        + "\nData: " + proiezione.getDataOra().toLocalDate()
                        + "\n\nL'operazione e' possibile solo se non ci sono prenotazioni.");

        alert.showAndWait().filter(bt -> bt == ButtonType.OK).ifPresent(bt -> {
            Richiesta r = new Richiesta(TipoOperazione.ELIMINA_PROIEZIONE);
            r.setParametro("idProiezione", proiezione.getId());

            Task<Risposta> task = new Task<>() {
                @Override
                protected Risposta call() throws Exception {
                    return ClientCM.getConnessione().invia(r);
                }
            };

            task.setOnSucceeded(ev -> {
                Risposta risposta = task.getValue();
                if (risposta.isSuccesso()) {
                    lblStatus.setStyle("-fx-text-fill: green;");
                    lblStatus.setText("Proiezione eliminata.");
                    dopoOperaz.run();
                } else {
                    lblStatus.setStyle("-fx-text-fill: red;");
                    lblStatus.setText(risposta.getMessaggio());
                }
            });

            task.setOnFailed(ev -> {
                lblStatus.setStyle("-fx-text-fill: red;");
                lblStatus.setText("Errore di rete: " + task.getException().getMessage());
            });

            new Thread(task).start();
        });
    }

    /**
     * Invia la richiesta di logout al server e torna al menu iniziale.
     */
    private void eseguiLogout() {
        Task<Risposta> task = new Task<>() {
            @Override
            protected Risposta call() throws Exception {
                return ClientCM.getConnessione().invia(new Richiesta(TipoOperazione.LOGOUT));
            }
        };
        task.setOnSucceeded(ev -> {
            ClientCM.setUtenteCorrente(null);
            ClientCM.mostraScena(new ScenaIniziale().costruisci());
        });
        task.setOnFailed(ev -> {
            ClientCM.setUtenteCorrente(null);
            ClientCM.mostraScena(new ScenaIniziale().costruisci());
        });
        new Thread(task).start();
    }
}
