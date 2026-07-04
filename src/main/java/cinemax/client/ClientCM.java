/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.client;

import cinemax.GUI.views.ScenaConnessione;
import cinemax.common.Utente;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale del modulo clientCM.
 * Estende {@link javafx.application.Application} per avviare la GUI JavaFX.
 * Mantiene il riferimento alla finestra principale (Stage), alla connessione
 * con il server e all'utente attualmente autenticato, rendendoli accessibili
 * come dati statici a tutte le schermate dell'applicazione.
 * La prima schermata mostrata all'avvio e' {@link ScenaConnessione},
 * che chiede all'utente di specificare l'host e la porta del server.
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
     * poi mostra la schermata di connessione al server.
     *
     * @param stage stage principale fornito dal framework JavaFX
     */
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("CineMax");
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        mostraScena(new ScenaConnessione().costruisci());
        stage.show();
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
