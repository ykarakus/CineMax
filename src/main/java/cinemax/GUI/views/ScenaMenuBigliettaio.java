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

import java.util.List;

/**
 * Schermata del menu principale per un utente con ruolo 'bigliettaio'.
 * Come da specifica, permette di:
 * <ul>
 * <li>Visualizzare le prenotazioni della data odierna (tab "Oggi")</li>
 * <li>Cercare prenotazioni per vari criteri (tab "Cerca")</li>
 * <li>Effettuare il logout</li>
 * </ul>
 * Le due funzionalita' sono organizzate in tab separati all'interno
 * della stessa schermata per una navigazione rapida.
 */
public class ScenaMenuBigliettaio {

    private static final java.time.format.DateTimeFormatter FMT = java.time.format.DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm");

    /** Utente bigliettaio autenticato. */
    private final Utente utente;

    /**
     * Costruttore.
     *
     * @param utente utente bigliettaio autenticato
     */
    public ScenaMenuBigliettaio(Utente utente) {
        this.utente = utente;
    }

    /**
     * Costruisce e restituisce la scena del menu bigliettaio.
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
                "Bigliettaio: " + utente.getNome() + " " + utente.getCognome());
        lblBenvenuto.setFont(Font.font("System", FontWeight.BOLD, 20));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        intestazione.getChildren().addAll(lblBenvenuto, spacer, btnLogout);

        Separator sep = new Separator();

        // --- Tab pane ---
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // ===== TAB 1: Prenotazioni di oggi =====
        Tab tabOggi = new Tab("Prenotazioni di oggi");

        VBox contenutoOggi = new VBox(10);
        contenutoOggi.setPadding(new Insets(12));

        TableView<Prenotazione> tabellaOggi = costruisciTabella();
        Button btnRicaricaOggi = new Button("Aggiorna");
        Label lblStatusOggi = new Label();

        contenutoOggi.getChildren().addAll(tabellaOggi, btnRicaricaOggi, lblStatusOggi);
        VBox.setVgrow(tabellaOggi, Priority.ALWAYS);
        tabOggi.setContent(contenutoOggi);

        // ===== TAB 2: Ricerca prenotazioni =====
        Tab tabRicerca = new Tab("Cerca prenotazione");
        tabRicerca.setContent(new ScenaRicercaPrenotazioni(utente).costruisciPannello());

        tabPane.getTabs().addAll(tabOggi, tabRicerca);

        root.getChildren().addAll(intestazione, sep, tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // --- Logica caricamento prenotazioni di oggi ---

        Runnable caricaOggi = () -> {
            lblStatusOggi.setText("Caricamento prenotazioni di oggi...");
            Richiesta r = new Richiesta(TipoOperazione.PRENOTAZIONI_OGGI);

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
                    tabellaOggi.setItems(FXCollections.observableArrayList(prenotazioni));
                    lblStatusOggi.setStyle("-fx-text-fill: green;");
                    lblStatusOggi.setText(prenotazioni.size() + " prenotazioni per oggi.");
                } else {
                    lblStatusOggi.setStyle("-fx-text-fill: red;");
                    lblStatusOggi.setText(risposta.getMessaggio());
                }
            });

            task.setOnFailed(ev -> {
                lblStatusOggi.setStyle("-fx-text-fill: red;");
                lblStatusOggi.setText("Errore di rete: " + task.getException().getMessage());
            });

            new Thread(task).start();
        };

        btnRicaricaOggi.setOnAction(e -> caricaOggi.run());
        btnLogout.setOnAction(e -> eseguiLogout());

        // Carica le prenotazioni di oggi all'apertura
        javafx.application.Platform.runLater(caricaOggi::run);

        return new Scene(root, 860, 620);
    }

    /**
     * Crea e configura la TableView per la visualizzazione delle prenotazioni.
     *
     * @return tabella configurata con tutte le colonne previste
     */
    private TableView<Prenotazione> costruisciTabella() {
        TableView<Prenotazione> tabella = new TableView<>();
        tabella.setPlaceholder(new Label("Nessuna prenotazione."));

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
        colFilm.setPrefWidth(185);

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
        return tabella;
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
