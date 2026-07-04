/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.GUI.views;

import cinemax.client.ClientCM;
import cinemax.client.ConnessioneServer;
import cinemax.server.ServerCM;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Prima schermata mostrata all'avvio dell'applicazione clientCM.
 * Permette di specificare l'host e la porta del server CineMax e
 * di avviare la connessione TCP. La connessione avviene in un thread
 * in background per non bloccare il thread JavaFX. In caso di errore
 * viene mostrato un messaggio descrittivo senza chiudere l'applicazione,
 * consentendo all'utente di correggere i dati e riprovare.
 */
public class ScenaConnessione {

    /**
     * Costruisce e restituisce la scena di connessione al server.
     *
     * @return scena JavaFX pronta per essere impostata sullo Stage
     */
    public Scene costruisci() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));

        Label lblTitolo = new Label("CineMax");
        lblTitolo.setFont(Font.font("System", FontWeight.BOLD, 40));

        Label lblSottotitolo = new Label("Connessione al server");
        lblSottotitolo.setFont(Font.font("System", 16));

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        form.setAlignment(Pos.CENTER);

        form.add(new Label("Host:"), 0, 0);
        TextField campoHost = new TextField("localhost");
        campoHost.setPrefWidth(220);
        form.add(campoHost, 1, 0);

        form.add(new Label("Porta:"), 0, 1);
        TextField campoPorta = new TextField(String.valueOf(ServerCM.PORTA));
        campoPorta.setPrefWidth(220);
        form.add(campoPorta, 1, 1);

        Button btnConnetti = new Button("Connetti");
        btnConnetti.setPrefWidth(160);
        btnConnetti.setDefaultButton(true);
        btnConnetti.setStyle("-fx-font-size: 13;");

        Label lblErrore = new Label();
        lblErrore.setStyle("-fx-text-fill: red;");
        lblErrore.setWrapText(true);

        root.getChildren().addAll(lblTitolo, lblSottotitolo, form, btnConnetti, lblErrore);

        // --- Azione connessione ---
        btnConnetti.setOnAction(e -> {
            String host = campoHost.getText().trim();
            String portaStr = campoPorta.getText().trim();

            if (host.isEmpty()) {
                lblErrore.setText("Inserire l'indirizzo host del server.");
                return;
            }

            int porta;
            try {
                porta = Integer.parseInt(portaStr);
                if (porta < 1 || porta > 65535)
                    throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                lblErrore.setText("Porta non valida: inserire un numero tra 1 e 65535.");
                return;
            }

            lblErrore.setText("");
            btnConnetti.setDisable(true);
            btnConnetti.setText("Connessione in corso...");

            final int portaFinale = porta;
            Task<ConnessioneServer> task = new Task<>() {
                @Override
                protected ConnessioneServer call() throws Exception {
                    return new ConnessioneServer(host, portaFinale);
                }
            };

            task.setOnSucceeded(ev -> {
                ClientCM.setConnessione(task.getValue());
                ClientCM.mostraScena(new ScenaIniziale().costruisci());
            });

            task.setOnFailed(ev -> {
                lblErrore.setText("Impossibile connettersi: " + task.getException().getMessage());
                btnConnetti.setDisable(false);
                btnConnetti.setText("Connetti");
            });

            new Thread(task).start();
        });

        return new Scene(root, 700, 450);
    }
}
