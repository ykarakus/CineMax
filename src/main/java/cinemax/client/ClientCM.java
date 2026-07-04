/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.client;

import cinemax.GUI.views.ScenaConnessione;
import cinemax.GUI.views.ScenaIniziale;
import cinemax.common.Utente;
import cinemax.server.ServerCM;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Classe principale del modulo clientCM.
 * Estende {@link javafx.application.Application} per avviare la GUI JavaFX.
 * Mantiene il riferimento alla finestra principale (Stage), alla connessione
 * con il server e all'utente attualmente autenticato, rendendoli accessibili
 * come dati statici a tutte le schermate dell'applicazione.
 * La prima schermata mostrata all'avvio e' una schermata di caricamento
 * mentre si tenta la connessione automatica al server (localhost:porta
 * predefinita).
 * Se la connessione riesce si passa direttamente a
 * {@link cinemax.GUI.views.ScenaIniziale};
 * in caso di errore viene mostrata {@link ScenaConnessione} per la
 * configurazione manuale.
 */
public class ClientCM extends Application {

    /** Finestra principale dell'applicazione JavaFX. */
    private static Stage primaryStage;

    /** Connessione TCP attiva con il server CineMax (null se non connesso). */
    private static ConnessioneServer connessione;

    /** Utente attualmente autenticato (null se guest o non loggato). */
    private static Utente utenteCorrente;

    /**
     * Punto di avvio dell'applicazione JavaFX.
     * Configura il titolo e le dimensioni minime della finestra,
     * poi tenta automaticamente la connessione al server con i parametri
     * predefiniti (localhost:porta). Se la connessione riesce si passa
     * direttamente alla schermata iniziale; in caso di errore viene mostrata
     * la schermata di connessione manuale.
     *
     * @param stage stage principale fornito dal framework JavaFX
     */
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("CineMax");
        stage.setMinWidth(700);
        stage.setMinHeight(700);

        // Schermata di caricamento mentre si tenta la connessione automatica
        VBox splash = new VBox(20);
        splash.setAlignment(Pos.CENTER);
        Label lblTitolo = new Label("CineMax");
        lblTitolo.setFont(Font.font("System", FontWeight.BOLD, 40));
        Label lblAttesa = new Label("Connessione al server in corso...");
        lblAttesa.setFont(Font.font("System", 14));
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(50, 50);
        splash.getChildren().addAll(lblTitolo, spinner, lblAttesa);
        mostraScena(new Scene(splash, 700, 450));
        stage.show();

        // Tentativo di connessione automatica in background
        Task<ConnessioneServer> task = new Task<>() {
            @Override
            protected ConnessioneServer call() throws Exception {
                return new ConnessioneServer("localhost", ServerCM.PORTA);
            }
        };

        task.setOnSucceeded(ev -> {
            ClientCM.setConnessione(task.getValue());
            mostraScena(new ScenaIniziale().costruisci());
        });

        task.setOnFailed(ev -> {
            // Connessione automatica fallita: mostra la schermata manuale
            mostraScena(new ScenaConnessione().costruisci());
        });

        new Thread(task).start();
    }

    /**
     * Sostituisce la scena correntemente visualizzata con quella indicata.
     * Deve essere invocato dal thread JavaFX (Application Thread).
     *
     * @param scena nuova scena da visualizzare
     */
    public static void mostraScena(Scene scena) {
        primaryStage.setScene(scena);
    }

    /**
     * Restituisce la connessione attiva con il server.
     *
     * @return connessione al server, oppure null se non ancora connesso
     */
    public static ConnessioneServer getConnessione() {
        return connessione;
    }

    /**
     * Imposta la connessione con il server.
     * Viene chiamato da {@link ScenaConnessione} dopo una connessione riuscita.
     *
     * @param conn nuova connessione al server
     */
    public static void setConnessione(ConnessioneServer conn) {
        connessione = conn;
    }

    /**
     * Restituisce l'utente attualmente autenticato.
     *
     * @return utente autenticato, oppure null se nessun utente e' loggato
     */
    public static Utente getUtenteCorrente() {
        return utenteCorrente;
    }

    /**
     * Imposta l'utente corrente dopo un login riuscito.
     * Passando null si effettua il logout lato client.
     *
     * @param utente utente autenticato, oppure null per il logout
     */
    public static void setUtenteCorrente(Utente utente) {
        utenteCorrente = utente;
    }

    /**
     * Invocato dal framework JavaFX alla chiusura dell'applicazione.
     * Chiude la connessione con il server, se attiva, liberando le risorse.
     */
    @Override
    public void stop() {
        if (connessione != null) {
            connessione.chiudi();
        }
    }

    /**
     * Punto di ingresso del modulo client.
     *
     * @param args argomenti da riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
