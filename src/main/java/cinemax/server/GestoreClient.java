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
import cinemax.common.Utente;
import cinemax.server.service.PrenotazioneService;
import cinemax.server.service.ProiezioneService;
import cinemax.server.service.UtenteService;

/**
 * Gestisce la sessione di un singolo client connesso al server.
 *
 * Ogni istanza viene eseguita in un thread dedicato: il server crea
 * un GestoreClient per ogni connessione accettata, cosi' da servire
 * piu' utenti in parallelo (requisito di concorrenza delle specifiche).
 *
 * Ogni gestore apre una propria connessione JDBC verso il database:
 * in questo modo le sessioni dei diversi client sono isolate tra loro
 * e le transazioni di un client non interferiscono con quelle degli altri.
 *
 * La logica di business e' delegata a tre service dedicati:
 * UtenteService (login e registrazione), ProiezioneService (gestione
 * del palinsesto) e PrenotazioneService (gestione delle prenotazioni).
 */
public class GestoreClient implements Runnable {

    /** Socket di comunicazione con il client servito da questo thread. */
    private Socket socket;

    /** URL JDBC del database. */
    private String urlJdbc;

    /** Username per l'accesso al database. */
    private String dbUser;

    /** Password per l'accesso al database. */
    private String dbPassword;

    /** Connessione JDBC dedicata alla sessione di questo client. */
    private Connection connessione;

    /** Utente attualmente autenticato in questa sessione (null se nessuno). */
    private Utente utenteAutenticato;

    /** Service per le operazioni sugli utenti (login, registrazione). */
    private UtenteService utenteService;

    /** Service per le operazioni sulle proiezioni. */
    private ProiezioneService proiezioneService;

    /** Service per le operazioni sulle prenotazioni. */
    private PrenotazioneService prenotazioneService;

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
     * Ciclo di vita della sessione del client.
     *
     * Apre la connessione al database, istanzia i service e poi entra
     * nel ciclo principale: legge una richiesta dal client, la elabora
     * e invia la risposta, finche' il client non si disconnette.
     */
    @Override
    public void run() {
        try {
            // Connessione JDBC dedicata alla sessione di questo client
            connessione = DriverManager.getConnection(urlJdbc, dbUser, dbPassword);

            // Istanziazione dei service che implementano la logica di business
            utenteService = new UtenteService(connessione);
            proiezioneService = new ProiezioneService(connessione);
            prenotazioneService = new PrenotazioneService(connessione);

            // NOTA: l'ObjectOutputStream va creato PRIMA dell'ObjectInputStream,
            // altrimenti client e server si bloccano a vicenda in attesa
            // dell'header di serializzazione (deadlock).
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                Object ricevuto = in.readObject();

                // Controllo difensivo: accettiamo solo oggetti Richiesta
                if (!(ricevuto instanceof Richiesta)) {
                    out.writeObject(new Risposta(false, "Richiesta non valida", null));
                    out.flush();
                    continue;
                }

                Richiesta richiesta = (Richiesta) ricevuto;
                Risposta risposta = elabora(richiesta);

                out.writeObject(risposta);
                out.flush();
                // reset() svuota la cache della serializzazione: senza questa
                // chiamata l'ObjectOutputStream riutilizzerebbe i riferimenti
                // a oggetti gia' inviati, restituendo al client dati non aggiornati.
                out.reset();
            }
        } catch (java.io.EOFException e) {
            // Il client ha chiuso la connessione: terminazione normale della sessione
            System.out.println("Client disconnesso: " + socket.getInetAddress());
        } catch (Exception e) {
            System.err.println("Errore nella sessione client: " + e.getMessage());
        } finally {
            chiudiRisorse();
        }
    }

    /**
     * Smista la richiesta al service appropriato in base al tipo di operazione.
     *
     * Le operazioni riservate a un ruolo specifico (cliente, bigliettaio,
     * proiezionista) vengono autorizzate verificando l'utente autenticato
     * nella sessione corrente: il controllo di autorizzazione avviene quindi
     * lato server, e non puo' essere aggirato da un client malevolo.
     *
     * @param richiesta richiesta ricevuta dal client
     * @return risposta da inviare al client
     */
    private Risposta elabora(Richiesta richiesta) {
        switch (richiesta.getOperazione()) {

            // --- Operazioni che non richiedono autenticazione ---

            case LOGIN:
                Risposta rispostaLogin = utenteService.login(richiesta);
                if (rispostaLogin.isSuccesso()) {
                    // Memorizziamo l'utente autenticato nella sessione:
                    // servira' per autorizzare le operazioni successive
                    utenteAutenticato = (Utente) rispostaLogin.getDati();
                }
                return rispostaLogin;

            case LOGOUT:
                utenteAutenticato = null;
                return new Risposta(true, "Logout effettuato", null);

            case REGISTRA_CLIENTE:
                return utenteService.registraCliente(richiesta);

            case CERCA_PROIEZIONE:
                return proiezioneService.cercaProiezione(richiesta);

            case VISUALIZZA_PROIEZIONE:
                return proiezioneService.visualizzaProiezione(richiesta);

            // --- Operazioni riservate ai clienti autenticati ---

            case CREA_PRENOTAZIONE:
                if (!haRuolo(Utente.RUOLO_CLIENTE)) {
                    return new Risposta(false, "Operazione riservata ai clienti autenticati", null);
                }
                return prenotazioneService.creaPrenotazione(richiesta);

            case VISUALIZZA_PRENOTAZIONI:
                if (!haRuolo(Utente.RUOLO_CLIENTE)) {
                    return new Risposta(false, "Operazione riservata ai clienti autenticati", null);
                }
                return prenotazioneService.visualizzaPrenotazioni(richiesta);

            case MODIFICA_PRENOTAZIONE:
                if (!haRuolo(Utente.RUOLO_CLIENTE)) {
                    return new Risposta(false, "Operazione riservata ai clienti autenticati", null);
                }
                return prenotazioneService.modificaPrenotazione(richiesta);

            case ELIMINA_PRENOTAZIONE:
                if (!haRuolo(Utente.RUOLO_CLIENTE)) {
                    return new Risposta(false, "Operazione riservata ai clienti autenticati", null);
                }
                return prenotazioneService.eliminaPrenotazione(richiesta);

            // --- Operazioni riservate ai bigliettai ---

            case CERCA_PRENOTAZIONE:
                if (!haRuolo(Utente.RUOLO_BIGLIETTAIO)) {
                    return new Risposta(false, "Operazione riservata ai bigliettai", null);
                }
                return prenotazioneService.cercaPrenotazione(richiesta);

            case PRENOTAZIONI_OGGI:
                if (!haRuolo(Utente.RUOLO_BIGLIETTAIO)) {
                    return new Risposta(false, "Operazione riservata ai bigliettai", null);
                }
                return prenotazioneService.prenotazioniOggi();

            // --- Operazioni riservate ai proiezionisti ---

            case AGGIUNGI_PROIEZIONE:
                if (!haRuolo(Utente.RUOLO_PROIEZIONISTA)) {
                    return new Risposta(false, "Operazione riservata ai proiezionisti", null);
                }
                return proiezioneService.aggiungiProiezione(richiesta);

            case MODIFICA_PROIEZIONE:
                if (!haRuolo(Utente.RUOLO_PROIEZIONISTA)) {
                    return new Risposta(false, "Operazione riservata ai proiezionisti", null);
                }
                return proiezioneService.modificaProiezione(richiesta);

            case ELIMINA_PROIEZIONE:
                if (!haRuolo(Utente.RUOLO_PROIEZIONISTA)) {
                    return new Risposta(false, "Operazione riservata ai proiezionisti", null);
                }
                return proiezioneService.eliminaProiezione(richiesta);

            case VISUALIZZA_PALINSESTO:
                if (!haRuolo(Utente.RUOLO_PROIEZIONISTA)) {
                    return new Risposta(false, "Operazione riservata ai proiezionisti", null);
                }
                return proiezioneService.visualizzaPalinsesto(richiesta);

            // --- Operazione non riconosciuta ---

            default:
                return new Risposta(false,
                        "Operazione non supportata: " + richiesta.getOperazione(), null);
        }
    }

    /**
     * Verifica che l'utente autenticato nella sessione corrente
     * abbia il ruolo indicato.
     *
     * @param ruolo ruolo richiesto per l'operazione
     * @return true se un utente e' autenticato e ha il ruolo indicato
     */
    private boolean haRuolo(String ruolo) {
        return utenteAutenticato != null && ruolo.equals(utenteAutenticato.getRuolo());
    }

    /**
     * Chiude la connessione al database e il socket del client,
     * ignorando eventuali errori di chiusura (chiusura best-effort).
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