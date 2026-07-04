/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.client;

/**
 * Classe di avvio del modulo clientCM.
 *
 * Questa classe NON estende javafx.application.Application: serve ad
 * aggirare il controllo che la JVM esegue quando la classe main estende
 * Application e le librerie JavaFX si trovano sul classpath (e non sul
 * module path). Senza questo passaggio intermedio, l'avvio fallirebbe
 * con l'errore "non sono presenti i componenti runtime di JavaFX".
 *
 * L'unico compito di questa classe e' delegare l'avvio alla vera
 * applicazione JavaFX ({@link ClientCM}).
 */
public class AvvioClient {

    /**
     * Punto di ingresso del modulo client.
     * Delega l'avvio all'applicazione JavaFX.
     *
     * @param args argomenti da riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
        ClientCM.main(args);
    }
}