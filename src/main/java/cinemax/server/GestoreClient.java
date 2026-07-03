/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import cinemax.common.Richiesta;
import cinemax.common.Risposta;
import cinemax.common.TipoOperazione;
import cinemax.common.Utente;

/**
 * Gestisce la sessione di un singolo client connesso al server.
 * Ogni istanza viene eseguita in un thread dedicato: il server crea
 * un GestoreClient per ogni connessione accettata, cosi' da servire
 * piu' utenti in parallelo.
 * Ogni gestore apre una propria connessione JDBC verso il database:
 * in questo modo le sessioni dei diversi client sono isolate tra loro
 * e non condividono statement o transazioni.
 */
public class GestoreClient implements Runnable {

    /** Socket di comunicazione con il client. */
    private Socket socket;

    /** URL JDBC del database. */
    private String urlJdbc;

    /** Username per l'accesso al database. */
    private String dbUser;

    /** Password per l'accesso al database. */
    private String dbPassword;

    /** Connessione JDBC dedicata a questo client. */
    private Connection connessione;

    /** Utente attualmente autenticato in questa sessione (null se nessuno). */
    private Utente utenteAutenticato;

    /**
     * Costruttore.
     *
     * @param socket     socket del client da servire
     * @param urlJdbc    URL JDBC del database
     * @param dbUser     username del database
     * @param dbPassword password del database
     */
    public GestoreClient(Socket socket, String urlJdbc, String dbUser, String dbPassword) {
        this.socket = socket;
        this.urlJdbc = urlJdbc;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.utenteAutenticato = null;
    }

    /**
     * Ciclo di vita della sessione del client: apre la connessione al
     * database e gli stream di comunicazione, poi legge le richieste
     * una alla volta e invia le relative risposte, fino alla
     * disconnessione del client.
     */
    @Override
    public void run() {
        try {
            // Connessione JDBC dedicata alla sessione di questo client
            connessione = DriverManager.getConnection(urlJdbc, dbUser, dbPassword);

            // NOTA: l'ObjectOutputStream va creato PRIMA dell'ObjectInputStream,
            // altrimenti client e server si bloccano a vicenda in attesa
            // dell'header di serializzazione.
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            boolean sessioneAttiva = true;
            while (sessioneAttiva) {
                Object oggettoRicevuto = in.readObject();

                if (!(oggettoRicevuto instanceof Richiesta)) {
                    out.writeObject(new Risposta(false, "Richiesta non valida", null));
                    out.flush();
                    continue;
                }

                Richiesta richiesta = (Richiesta) oggettoRicevuto;
                Risposta risposta = elabora(richiesta);

                out.writeObject(risposta);
                out.flush();
                // reset() evita che l'ObjectOutputStream riutilizzi riferimenti
                // a oggetti gia' inviati, restituendo dati non aggiornati.
                out.reset();
            }
        } catch (java.io.EOFException e) {
            // Il client ha chiuso la connessione: terminazione normale
            System.out.println("Client disconnesso: " + socket.getInetAddress());
        } catch (Exception e) {
            System.err.println("Errore nella sessione client: " + e.getMessage());
        } finally {
            chiudiRisorse();
        }
    }

    /**
     * Smista la richiesta al metodo che implementa l'operazione.
     *
     * @param richiesta richiesta ricevuta dal client
     * @return risposta da inviare al client
     */
    private Risposta elabora(Richiesta richiesta) {
        TipoOperazione operazione = richiesta.getOperazione();

        switch (operazione) {
            case LOGIN:
                return login(richiesta);
            case LOGOUT:
                return logout();
            // (REGISTRA_CLIENTE, CERCA_PROIEZIONE, CREA_PRENOTAZIONE, ...)
            default:
                return new Risposta(false, "Operazione non ancora supportata: " + operazione, null);
        }
    }

    /**
     * Autentica un utente registrato.
     * La verifica della password avviene interamente sul database
     * tramite la funzione crypt() dell'estensione pgcrypto: la password
     * ricevuta dal client viene confrontata con l'hash BCrypt memorizzato,
     * senza mai salvare o registrare la password in chiaro.
     *
     * @param richiesta richiesta contenente i parametri "username" e "password"
     * @return risposta con l'oggetto Utente in caso di successo
     */
    private Risposta login(Richiesta richiesta) {
        String username = (String) richiesta.getParametro("username");
        String password = (String) richiesta.getParametro("password");

        if (username == null || password == null) {
            return new Risposta(false, "Username e password sono obbligatori", null);
        }

        String sql = "SELECT username, nome, cognome, data_nascita, domicilio, ruolo "
                   + "FROM utenti "
                   + "WHERE username = ? AND password_cifrata = crypt(?, password_cifrata)";

        try {
            PreparedStatement statement = connessione.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet risultato = statement.executeQuery();

            if (risultato.next()) {
                // Costruzione dell'oggetto Utente dai dati del database.
                // La data di nascita e' facoltativa: se assente resta null.
                LocalDate dataNascita = null;
                java.sql.Date dataSql = risultato.getDate("data_nascita");
                if (dataSql != null) {
                    dataNascita = dataSql.toLocalDate();
                }

                utenteAutenticato = new Utente(
                        risultato.getString("username"),
                        risultato.getString("nome"),
                        risultato.getString("cognome"),
                        dataNascita,
                        risultato.getString("domicilio"),
                        risultato.getString("ruolo"));

                risultato.close();
                statement.close();
                return new Risposta(true, "Login effettuato", utenteAutenticato);
            } else {
                risultato.close();
                statement.close();
                return new Risposta(false, "Username o password errati", null);
            }
        } catch (Exception e) {
            return new Risposta(false, "Errore durante il login: " + e.getMessage(), null);
        }
    }

    /**
     * Chiude la sessione dell'utente autenticato.
     *
     * @return risposta di conferma
     */
    private Risposta logout() {
        utenteAutenticato = null;
        return new Risposta(true, "Logout effettuato", null);
    }

    /**
     * Chiude la connessione al database e il socket del client,
     * ignorando eventuali errori di chiusura.
     */
    private void chiudiRisorse() {
        try {
            if (connessione != null) {
                connessione.close();
            }
        } catch (Exception e) {
            // chiusura best-effort
        }
        try {
            socket.close();
        } catch (Exception e) {
            // chiusura best-effort
        }
    }
}
