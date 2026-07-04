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

import cinemax.common.Richiesta;
import cinemax.common.Risposta;
import cinemax.common.TipoOperazione;
import cinemax.common.Utente;
import cinemax.server.service.UtenteService;

/**
 * Gestisce la sessione di un singolo client connesso al server.
 * Ogni istanza viene eseguita in un thread dedicato: il server crea
 * un GestoreClient per ogni connessione accettata, cosi' da servire
 * piu' utenti in parallelo.
 * Ogni gestore apre una propria connessione JDBC verso il database e
 * delega la logica di business ai service dedicati:
 * {@link UtenteService}, {@link ProiezioneService},
 * {@link PrenotazioneService}.
 */
public class GestoreClient implements Runnable {

    private final Socket socket;
    private final String urlJdbc;
    private final String dbUser;
    private final String dbPassword;

    private Connection connessione;
    private Utente utenteAutenticato;

    private UtenteService utenteService;

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
    }

    /**
     * Ciclo di vita della sessione del client: apre la connessione al
     * database, istanzia i service e legge le richieste in loop fino
     * alla disconnessione del client.
     */
    @Override
    public void run() {
        try {
            connessione = DriverManager.getConnection(urlJdbc, dbUser, dbPassword);

            utenteService = new UtenteService(connessione);

            // ObjectOutputStream PRIMA di ObjectInputStream per evitare deadlock
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                Object received = in.readObject();
                if (!(received instanceof Richiesta)) {
                    out.writeObject(new Risposta(false, "Richiesta non valida", null));
                    out.flush();
                    continue;
                }
                Risposta risposta = elabora((Richiesta) received);
                out.writeObject(risposta);
                out.flush();
                out.reset();
            }
        } catch (java.io.EOFException e) {
            System.out.println("Client disconnesso: " + socket.getInetAddress());
        } catch (Exception e) {
            System.err.println("Errore nella sessione client: " + e.getMessage());
        } finally {
            chiudiRisorse();
        }
    }

    /**
     * Smista la richiesta al service appropriato.
     *
     * @param richiesta richiesta ricevuta dal client
     * @return risposta da inviare al client
     */
    private Risposta elabora(Richiesta richiesta) {
        TipoOperazione op = richiesta.getOperazione();
        return switch (op) {
            case LOGIN -> {
                Risposta r = utenteService.login(richiesta);
                if (r.isSuccesso())
                    utenteAutenticato = (Utente) r.getDati();
                yield r;
            }
            case LOGOUT -> {
                utenteAutenticato = null;
                yield new Risposta(true, "Logout effettuato", null);
            }
            case REGISTRA_CLIENTE -> utenteService.registraCliente(richiesta);
            default -> new Risposta(false, "Operazione non supportata: " + op, null);
        };
    }

    /**
     * Chiude la connessione al database e il socket, ignorando errori di chiusura.
     */
    private void chiudiRisorse() {
        try {
            if (connessione != null)
                connessione.close();
        } catch (Exception e) {
        }
        try {
            socket.close();
        } catch (Exception e) {
        }
    }
}
