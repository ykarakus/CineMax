/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

/**
 * Classe principale del modulo serverCM.
 * All'avvio richiede all'operatore le credenziali di accesso al
 * database dbCM e l'host su cui esso e in esecuzione, come previsto
 * dalle specifiche di progetto. Verificata la connessione al database,
 * il server si mette in ascolto di richieste di connessione da parte
 * dei client clientCM; ogni client connesso viene servito da un thread
 * dedicato, in modo da supportare l'interazione in parallelo di piu'
 * utenti.
 */
public class ServerCM {

    /** Porta TCP su cui il server accetta le connessioni dei client. */
    public static final int PORTA = 4444;

    /** Nome del database di supporto alla piattaforma. */
    private static final String NOME_DB = "dbcm";

    /**
     * Punto di ingresso del server.
     * Richiede host e credenziali del database, verifica la connessione
     * e avvia il ciclo di accettazione dei client.
     *
     * @param args non utilizzati
     */
    public static void main(String[] args) {
        Scanner tastiera = new Scanner(System.in);

        System.out.println("=== CineMax - serverCM ===");

        // Richiesta dell'host del database (requisito delle specifiche)
        System.out.print("Host del database [localhost]: ");
        String host = tastiera.nextLine().trim();
        if (host.isEmpty()) {
            host = "localhost";
        }

        // Richiesta delle credenziali di accesso al database
        System.out.print("Username del database: ");
        String dbUser = tastiera.nextLine().trim();

        System.out.print("Password del database: ");
        String dbPassword = tastiera.nextLine();

        // Costruzione dell'URL JDBC per PostgreSQL
        String urlJdbc = "jdbc:postgresql://" + host + ":5432/" + NOME_DB;

        // Verifica preliminare della connessione al database:
        // se le credenziali sono errate il server termina subito,
        // segnalando il problema all'operatore.
        try {
            Connection connessioneDiProva = DriverManager.getConnection(urlJdbc, dbUser, dbPassword);
            connessioneDiProva.close();
            System.out.println("Connessione al database verificata.");
        } catch (Exception e) {
            System.err.println("Impossibile connettersi al database: " + e.getMessage());
            System.err.println("Verificare host e credenziali e riavviare il server.");
            return;
        }

        // Avvio del server socket: il server rimane in attesa di
        // richieste di connessione da parte dei client (requisito
        // delle specifiche). Per ogni client accettato viene creato
        // un thread dedicato che ne gestisce l'intera sessione.
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("Server in ascolto sulla porta " + PORTA + "...");

            while (true) {
                Socket socketClient = serverSocket.accept();
                System.out.println("Nuovo client connesso: "
                        + socketClient.getInetAddress());

                GestoreClient gestore = new GestoreClient(socketClient, urlJdbc, dbUser, dbPassword);
                Thread threadClient = new Thread(gestore);
                threadClient.start();
            }
        } catch (Exception e) {
            System.err.println("Errore del server: " + e.getMessage());
        }
    }
}
