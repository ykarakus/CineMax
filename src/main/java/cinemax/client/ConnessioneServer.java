/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import cinemax.common.Richiesta;
import cinemax.common.Risposta;

/**
 * Gestisce la connessione TCP tra il client e il server CineMax.
 * Incapsula gli stream di serializzazione Java e offre un unico metodo
 * sincronizzato per inviare una {@link Richiesta} e ricevere la
 * {@link Risposta} corrispondente.
 * La sincronizzazione garantisce la correttezza in presenza di piu'
 * thread JavaFX {@code Task} che accedano alla connessione in parallelo.
 */
public class ConnessioneServer {

    /** Socket di comunicazione con il server. */
    private final Socket socket;

    /** Stream di output per l'invio delle richieste serializzate. */
    private final ObjectOutputStream out;

    /** Stream di input per la ricezione delle risposte serializzate. */
    private final ObjectInputStream in;

    /**
     * Apre la connessione TCP al server CineMax.
     * L'ObjectOutputStream viene creato prima dell'ObjectInputStream
     * per evitare il deadlock durante il handshake della serializzazione
     * (entrambi i lati devono inviare l'header prima di poter leggere).
     *
     * @param host  indirizzo IP o hostname del server
     * @param porta porta TCP del server
     * @throws IOException se la connessione non puo' essere stabilita
     */
    public ConnessioneServer(String host, int porta) throws IOException {
        socket = new Socket(host, porta);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Invia una richiesta al server e restituisce la risposta.
     * Il metodo e' {@code synchronized} per evitare interleaving tra
     * chiamate concorrenti provenienti da thread diversi.
     *
     * @param richiesta richiesta da inviare al server
     * @return risposta ricevuta dal server
     * @throws IOException            se si verifica un errore di I/O di rete
     * @throws ClassNotFoundException se la risposta non puo' essere deserializzata
     */
    public synchronized Risposta invia(Richiesta richiesta) throws IOException, ClassNotFoundException {
        out.writeObject(richiesta);
        out.flush();
        // reset() forza la ritrasmissione degli oggetti anche se gia' serializzati
        // in precedenza, evitando che il server riceva dati obsoleti dalla cache.
        out.reset();
        Object ricevuto = in.readObject();
        if (ricevuto instanceof Risposta r) {
            return r;
        }
        throw new IOException("Risposta non valida ricevuta dal server.");
    }

    /**
     * Chiude il socket di connessione, ignorando eventuali errori.
     * Viene invocato alla chiusura dell'applicazione client.
     */
    public void chiudi() {
        try {
            socket.close();
        } catch (IOException e) {
            // chiusura best-effort: non propagare l'errore
        }
    }
}
