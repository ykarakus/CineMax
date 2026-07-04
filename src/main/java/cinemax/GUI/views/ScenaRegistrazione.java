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
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;

/**
 * Schermata di registrazione di un nuovo cliente alla piattaforma CineMax.
 * Raccoglie tutti i dati richiesti dalla specifica (nome, cognome, username,
 * password, data di nascita opzionale, domicilio) e invia la richiesta
 * {@link TipoOperazione#REGISTRA_CLIENTE} al server. L'operazione di
 * registrazione non richiede autenticazione previa.
 */
public class ScenaRegistrazione {

    /**
     * Costruisce e restituisce la scena di registrazione.
     *
     * @return scena JavaFX pronta per essere impostata sullo Stage
     */
    public Scene costruisci() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));

        Label lblTitolo = new Label("Registrazione nuovo cliente");
        lblTitolo.setFont(Font.font("System", FontWeight.BOLD, 22));

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        form.setAlignment(Pos.CENTER);
        form.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 6; -fx-padding: 18;");

        int riga = 0;

        form.add(new Label("Nome: *"), 0, riga);
        TextField campoNome = new TextField();
        campoNome.setPrefWidth(230);
        form.add(campoNome, 1, riga++);

        form.add(new Label("Cognome: *"), 0, riga);
        TextField campoCognome = new TextField();
        form.add(campoCognome, 1, riga++);

        form.add(new Label("Username: *"), 0, riga);
        TextField campoUsername = new TextField();
        form.add(campoUsername, 1, riga++);

        form.add(new Label("Password: *"), 0, riga);
        PasswordField campoPassword = new PasswordField();
        form.add(campoPassword, 1, riga++);

        form.add(new Label("Conferma password: *"), 0, riga);
        PasswordField campoConferma = new PasswordField();
        form.add(campoConferma, 1, riga++);

        form.add(new Label("Data di nascita:"), 0, riga);
        DatePicker dataNascitaPicker = new DatePicker();
        dataNascitaPicker.setPromptText("facoltativa");
        form.add(dataNascitaPicker, 1, riga++);

        form.add(new Label("Domicilio: *"), 0, riga);
        TextField campoDomicilio = new TextField();
        form.add(campoDomicilio, 1, riga++);

        Label lblObbligatori = new Label("* campi obbligatori");
        lblObbligatori.setStyle("-fx-font-size: 11; -fx-text-fill: grey;");

        Button btnRegistra = new Button("Registrati");
        btnRegistra.setPrefWidth(150);
        btnRegistra.setDefaultButton(true);
        Button btnIndietro = new Button("Indietro");
        btnIndietro.setPrefWidth(100);

        HBox boxBtn = new HBox(15, btnIndietro, btnRegistra);
        boxBtn.setAlignment(Pos.CENTER);

        Label lblMessaggio = new Label();
        lblMessaggio.setWrapText(true);
        lblMessaggio.setMaxWidth(450);

        root.getChildren().addAll(lblTitolo, form, lblObbligatori, boxBtn, lblMessaggio);

        // --- Azioni ---

        btnRegistra.setOnAction(e -> {
            String nome = campoNome.getText().trim();
            String cognome = campoCognome.getText().trim();
            String username = campoUsername.getText().trim();
            String password = campoPassword.getText();
            String conferma = campoConferma.getText();
            String domicilio = campoDomicilio.getText().trim();
            LocalDate dataNascita = dataNascitaPicker.getValue();

            if (nome.isEmpty() || cognome.isEmpty() || username.isEmpty()
                    || password.isEmpty() || domicilio.isEmpty()) {
                lblMessaggio.setStyle("-fx-text-fill: red;");
                lblMessaggio.setText("Compilare tutti i campi obbligatori.");
                return;
            }

            if (!password.equals(conferma)) {
                lblMessaggio.setStyle("-fx-text-fill: red;");
                lblMessaggio.setText("Le due password non corrispondono.");
                return;
            }

            lblMessaggio.setText("");
            btnRegistra.setDisable(true);

            Richiesta richiesta = new Richiesta(TipoOperazione.REGISTRA_CLIENTE);
            richiesta.setParametro("nome", nome);
            richiesta.setParametro("cognome", cognome);
            richiesta.setParametro("username", username);
            richiesta.setParametro("password", password);
            richiesta.setParametro("dataNascita", dataNascita); // puo' essere null
            richiesta.setParametro("domicilio", domicilio);

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
                    lblMessaggio.setText("Registrazione avvenuta con successo! "
                            + "Torna al menu per accedere.");
                    // Disabilita il pulsante per evitare doppia registrazione
                    btnRegistra.setDisable(true);
                } else {
                    lblMessaggio.setStyle("-fx-text-fill: red;");
                    lblMessaggio.setText(risposta.getMessaggio());
                    btnRegistra.setDisable(false);
                }
            });

            task.setOnFailed(ev -> {
                lblMessaggio.setStyle("-fx-text-fill: red;");
                lblMessaggio.setText("Errore di rete: " + task.getException().getMessage());
                btnRegistra.setDisable(false);
            });

            new Thread(task).start();
        });

        btnIndietro.setOnAction(e -> ClientCM.mostraScena(new ScenaIniziale().costruisci()));

        return new Scene(root, 700, 580);
    }
}
