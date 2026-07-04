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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Schermata del menu principale per un utente con ruolo 'cliente'.
 * Come da specifica, permette di:
 * <ul>
 * <li>Cercare proiezioni disponibili</li>
 * <li>Visualizzare le proprie prenotazioni attive (future)</li>
 * <li>Modificare la data di una prenotazione (cambio proiezione)</li>
 * <li>Cancellare una prenotazione futura</li>
 * <li>Effettuare il logout</li>
 * </ul>
 */
public class ScenaMenuCliente {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Utente cliente autenticato. */
    private final Utente utente;

    /**
     * Costruttore.
     *
     * @param utente utente cliente autenticato
     */
    public ScenaMenuCliente(Utente utente) {
        this.utente = utente;
    }

    /**
     * Costruisce e restituisce la scena del menu cliente.
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
                "Benvenuto, " + utente.getNome() + " " + utente.getCognome());
        lblBenvenuto.setFont(Font.font("System", FontWeight.BOLD, 20));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        intestazione.getChildren().addAll(lblBenvenuto, spacer, btnLogout);

        Separator sep = new Separator();

        // --- Azioni rapide ---
        Button btnCercaProiezioni = new Button("Cerca Proiezioni");
        btnCercaProiezioni.setStyle("-fx-background-color: #1565C0; -fx-text-fill: white;");
        Button btnRicarica = new Button("Aggiorna prenotazioni");

        HBox boxAzioni = new HBox(10, btnCercaProiezioni, btnRicarica);
        boxAzioni.setAlignment(Pos.CENTER_LEFT);

        // --- Tabella prenotazioni attive ---
        Label lblPrenotazioni = new Label("Le mie prenotazioni attive");
        lblPrenotazioni.setFont(Font.font("System", FontWeight.BOLD, 15));

        TableView<Prenotazione> tabella = new TableView<>();
        tabella.setPlaceholder(new Label("Nessuna prenotazione attiva."));

        TableColumn<Prenotazione, String> colCodice = new TableColumn<>("Codice");
        colCodice.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCodice()));
        colCodice.setPrefWidth(110);

        TableColumn<Prenotazione, String> colFilm = new TableColumn<>("Film");
        colFilm.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getProiezione().getFilm().getTitolo()));
        colFilm.setPrefWidth(190);

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

        tabella.getColumns().addAll(colCodice, colFilm, colData, colPosti, colTotale);

        // --- Pulsanti gestione prenotazione ---
        Button btnModifica = new Button("Modifica data");
        Button btnCancella = new Button("Cancella");
        btnCancella.setStyle("-fx-text-fill: red;");
        btnModifica.setDisable(true);
        btnCancella.setDisable(true);

        tabella.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            btnModifica.setDisable(sel == null);
            btnCancella.setDisable(sel == null);
        });

        HBox boxGestione = new HBox(10, btnModifica, btnCancella);
        boxGestione.setAlignment(Pos.CENTER_LEFT);

        Label lblStatus = new Label();

        root.getChildren().addAll(intestazione, sep, boxAzioni,
                lblPrenotazioni, tabella, boxGestione, lblStatus);
        VBox.setVgrow(tabella, Priority.ALWAYS);

        // --- Azioni ---

        Runnable caricaPrenotazioni = () -> {
            lblStatus.setStyle("");
            lblStatus.setText("Caricamento prenotazioni...");
            Richiesta r = new Richiesta(TipoOperazione.VISUALIZZA_PRENOTAZIONI);
            r.setParametro("username", utente.getUsername());

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
                    List<Prenotazione> prenotazioni = (List<Prenotazione>) lista;
                    tabella.setItems(FXCollections.observableArrayList(prenotazioni));
                    lblStatus.setStyle("-fx-text-fill: green;");
                    lblStatus.setText(prenotazioni.size() + " prenotazioni caricate.");
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

        btnLogout.setOnAction(e -> eseguiLogout());
        btnCercaProiezioni.setOnAction(e -> ClientCM.mostraScena(new ScenaRicercaProiezioni(utente, "").costruisci()));
        btnRicarica.setOnAction(e -> caricaPrenotazioni.run());

        btnModifica.setOnAction(e -> {
            Prenotazione sel = tabella.getSelectionModel().getSelectedItem();
            if (sel != null) {
                mostraDialogModifica(sel, caricaPrenotazioni, lblStatus);
            }
        });

        btnCancella.setOnAction(e -> {
            Prenotazione sel = tabella.getSelectionModel().getSelectedItem();
            if (sel != null) {
                mostraDialogCancella(sel, caricaPrenotazioni, lblStatus);
            }
        });

        // Carica le prenotazioni automaticamente all'apertura
        javafx.application.Platform.runLater(caricaPrenotazioni::run);

        return new Scene(root, 820, 600);
    }

    /**
     * Mostra un dialogo per selezionare la nuova data di una prenotazione.
     * Come da specifica, la modifica e' consentita solo se sia la data attuale
     * che quella nuova sono successive alla data odierna (il server verifica).
     *
     * @param prenotazione   prenotazione da modificare
     * @param dopoOperazione azione da eseguire dopo la modifica riuscita
     * @param lblStatus      label per i messaggi di stato
     */
    private void mostraDialogModifica(Prenotazione prenotazione,
            Runnable dopoOperazione, Label lblStatus) {
        Dialog<LocalDate> dialog = new Dialog<>();
        dialog.setTitle("Modifica prenotazione");
        dialog.setHeaderText("Film: " + prenotazione.getProiezione().getFilm().getTitolo()
                + "\nCodice: " + prenotazione.getCodice());

        ButtonType btnOk = new ButtonType("Conferma", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnOk, ButtonType.CANCEL);

        DatePicker picker = new DatePicker(
                prenotazione.getProiezione().getDataOra().toLocalDate());

        VBox content = new VBox(8, new Label("Nuova data della proiezione:"), picker);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(bt -> bt == btnOk ? picker.getValue() : null);

        dialog.showAndWait().ifPresent(nuovaData -> {
            if (nuovaData == null)
                return;

            Richiesta r = new Richiesta(TipoOperazione.MODIFICA_PRENOTAZIONE);
            r.setParametro("codice", prenotazione.getCodice());
            r.setParametro("nuovaData", nuovaData);

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
                    lblStatus.setText("Prenotazione modificata con successo.");
                    dopoOperazione.run();
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
     * Mostra un dialogo di conferma per la cancellazione di una prenotazione.
     * Come da specifica, la cancellazione e' consentita solo se la data della
     * proiezione e' successiva alla data odierna (il server verifica).
     *
     * @param prenotazione   prenotazione da cancellare
     * @param dopoOperazione azione da eseguire dopo la cancellazione riuscita
     * @param lblStatus      label per i messaggi di stato
     */
    private void mostraDialogCancella(Prenotazione prenotazione,
            Runnable dopoOperazione, Label lblStatus) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancellazione prenotazione");
        alert.setHeaderText("Cancellare la prenotazione " + prenotazione.getCodice() + "?");
        alert.setContentText(
                "Film: " + prenotazione.getProiezione().getFilm().getTitolo()
                        + "\nData: " + prenotazione.getProiezione().getDataOra().toLocalDate()
                        + "\nPosti: " + prenotazione.getNumPosti()
                        + "\nTotale: " + String.format("%.2f \u20ac", prenotazione.getCostoTotale()));

        alert.showAndWait().filter(bt -> bt == ButtonType.OK).ifPresent(bt -> {
            Richiesta r = new Richiesta(TipoOperazione.ELIMINA_PRENOTAZIONE);
            r.setParametro("codice", prenotazione.getCodice());

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
                    lblStatus.setText("Prenotazione cancellata.");
                    dopoOperazione.run();
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
