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

/**
 * Schermata principale (menu iniziale) dell'applicazione CineMax.
 * Come da specifica, al lancio del client viene mostrato un menu iniziale
 * da cui e' possibile:
 * <ul>
 * <li>Autenticarsi come utente registrato (cliente, proiezionista o
 * bigliettaio)</li>
 * <li>Registrarsi come nuovo cliente</li>
 * <li>Proseguire come utente <em>guest</em> indicando il titolo (anche
 * parziale)
 * di un film, accedendo alle funzionalita' che non richiedono
 * autenticazione</li>
 * </ul>
 */
public class ScenaIniziale {

    /**
     * Costruisce e restituisce la scena del menu iniziale.
     *
     * @return scena JavaFX pronta per essere impostata sullo Stage
     */
    public Scene costruisci() {
        VBox root = new VBox(28);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Label lblTitolo = new Label("Benvenuto in CineMax");
        lblTitolo.setFont(Font.font("System", FontWeight.BOLD, 30));

        // ===== Sezione login =====
        VBox boxLogin = new VBox(10);
        boxLogin.setAlignment(Pos.CENTER);
        boxLogin.setPadding(new Insets(15));
        boxLogin.setStyle("-fx-border-color: #aaaaaa; -fx-border-radius: 6;"
                + " -fx-background-color: #f9f9f9; -fx-background-radius: 6;");
        boxLogin.setMaxWidth(420);

        Label lblTitoloLogin = new Label("Accesso utente registrato");
        lblTitoloLogin.setFont(Font.font("System", FontWeight.BOLD, 14));

        GridPane formLogin = new GridPane();
        formLogin.setHgap(10);
        formLogin.setVgap(8);
        formLogin.setAlignment(Pos.CENTER);

        formLogin.add(new Label("Username:"), 0, 0);
        TextField campoUsername = new TextField();
        campoUsername.setPrefWidth(190);
        formLogin.add(campoUsername, 1, 0);

        formLogin.add(new Label("Password:"), 0, 1);
        PasswordField campoPassword = new PasswordField();
        campoPassword.setPrefWidth(190);
        formLogin.add(campoPassword, 1, 1);

        Button btnAccedi = new Button("Accedi");
        btnAccedi.setPrefWidth(130);

        Label lblErroreLogin = new Label();
        lblErroreLogin.setStyle("-fx-text-fill: red;");
        lblErroreLogin.setWrapText(true);

        boxLogin.getChildren().addAll(lblTitoloLogin, formLogin, btnAccedi, lblErroreLogin);

        // ===== Sezione guest =====
        VBox boxGuest = new VBox(10);
        boxGuest.setAlignment(Pos.CENTER);
        boxGuest.setPadding(new Insets(15));
        boxGuest.setStyle("-fx-border-color: #aaaaaa; -fx-border-radius: 6;"
                + " -fx-background-color: #f9f9f9; -fx-background-radius: 6;");
        boxGuest.setMaxWidth(420);

        Label lblTitoloGuest = new Label("Accesso come Guest");
        lblTitoloGuest.setFont(Font.font("System", FontWeight.BOLD, 14));

        HBox rigaFilm = new HBox(10);
        rigaFilm.setAlignment(Pos.CENTER);
        TextField campoFilm = new TextField();
        campoFilm.setPromptText("Titolo del film (anche parziale)");
        campoFilm.setPrefWidth(210);
        Button btnGuest = new Button("Esplora");
        rigaFilm.getChildren().addAll(campoFilm, btnGuest);

        Separator sep = new Separator();

        Button btnRegistrati = new Button("Registrati come nuovo cliente");
        btnRegistrati.setStyle("-fx-background-color: transparent; -fx-underline: true;"
                + " -fx-text-fill: #1565C0; -fx-cursor: hand;");

        boxGuest.getChildren().addAll(lblTitoloGuest, rigaFilm, sep, btnRegistrati);

        // ===== Bottone configurazione server (in basso a destra) =====
        Button btnServer = new Button("⚙ Configurazione server");
        btnServer.setStyle("-fx-background-color: transparent; -fx-text-fill: #777777;"
                + " -fx-font-size: 11; -fx-cursor: hand;");

        BorderPane outerLayout = new BorderPane();
        outerLayout.setCenter(root);
        BorderPane.setAlignment(btnServer, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(btnServer, new Insets(0, 10, 8, 0));
        outerLayout.setBottom(btnServer);

        root.getChildren().addAll(lblTitolo, boxLogin, boxGuest);

        // ===== Azioni =====

        btnAccedi.setOnAction(e -> eseguiLogin(
                campoUsername.getText().trim(),
                campoPassword.getText(),
                lblErroreLogin, btnAccedi));

        // Premere Invio nel campo password equivale a cliccare "Accedi"
        campoPassword.setOnAction(e -> btnAccedi.fire());

        btnGuest.setOnAction(e -> {
            ClientCM.setUtenteCorrente(null);
            ClientCM.mostraScena(
                    new ScenaRicercaProiezioni(null, campoFilm.getText().trim()).costruisci());
        });

        btnRegistrati.setOnAction(e -> ClientCM.mostraScena(new ScenaRegistrazione().costruisci()));

        btnServer.setOnAction(e -> ClientCM.mostraScena(new ScenaConnessione().costruisci()));

        return new Scene(outerLayout, 700, 530);
    }

    /**
     * Esegue il login inviando le credenziali al server in un thread background.
     * In caso di successo naviga al menu appropriato in base al ruolo dell'utente.
     *
     * @param username  username inserito dall'utente
     * @param password  password inserita dall'utente
     * @param lblErrore label in cui mostrare eventuali errori
     * @param btnAccedi pulsante da disabilitare durante l'operazione
     */
    private void eseguiLogin(String username, String password,
                             Label lblErrore, Button btnAccedi) {
        if (username.isEmpty() || password.isEmpty()) {
            lblErrore.setText("Username e password sono obbligatori.");
            return;
        }

        lblErrore.setText("");
        btnAccedi.setDisable(true);

        Richiesta richiesta = new Richiesta(TipoOperazione.LOGIN);
        richiesta.setParametro("username", username);
        richiesta.setParametro("password", password);

        Task<Risposta> task = new Task<>() {
            @Override
            protected Risposta call() throws Exception {
                return ClientCM.getConnessione().invia(richiesta);
            }
        };

        task.setOnSucceeded(ev -> {
            Risposta risposta = task.getValue();
            if (risposta.isSuccesso()) {
                Utente utente = (Utente) risposta.getDati();
                ClientCM.setUtenteCorrente(utente);
                // Navigazione al menu appropriato in base al ruolo
                switch (utente.getRuolo()) {
                    case Utente.RUOLO_CLIENTE ->
                            ClientCM.mostraScena(new ScenaMenuCliente(utente).costruisci());
                    case Utente.RUOLO_PROIEZIONISTA ->
                            ClientCM.mostraScena(new ScenaMenuProiezionista(utente).costruisci());
                    case Utente.RUOLO_BIGLIETTAIO ->
                            ClientCM.mostraScena(new ScenaMenuBigliettaio(utente).costruisci());
                    default -> {
                        lblErrore.setText("Ruolo non riconosciuto: " + utente.getRuolo());
                        btnAccedi.setDisable(false);
                    }
                }
            } else {
                lblErrore.setText(risposta.getMessaggio());
                btnAccedi.setDisable(false);
            }
        });

        task.setOnFailed(ev -> {
            lblErrore.setText("Errore di rete: " + task.getException().getMessage());
            btnAccedi.setDisable(false);
        });

        new Thread(task).start();
    }
}
