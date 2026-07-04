/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.GUI.views;

import cinemax.client.ClientCM;
import cinemax.common.Utente;
import javafx.scene.control.Button;

/**
 * Classe di utilita' per la navigazione tra le schermate.
 *
 * Centralizza la logica del ritorno alla schermata principale, che
 * dipende dal tipo di utente: per un utente autenticato la "home" e'
 * il menu del proprio ruolo (cliente, proiezionista o bigliettaio),
 * mentre per un guest e il menu iniziale dell'applicazione.
 *
 * Le schermate piu' profonde (ricerca, dettaglio, creazione
 * prenotazione, aggiunta proiezione) usano questa classe per offrire
 * un pulsante "Home" che riporta al menu con un solo click,
 * migliorando la navigabilita' della GUI.
 */
public final class NavigazioneHelper {

    /** Costruttore privato: la classe offre solo metodi statici. */
    private NavigazioneHelper() {
    }

    /**
     * Riporta l'utente alla propria schermata principale.
     *
     * Se l'utente non e autenticato (guest) viene mostrato il menu
     * iniziale; altrimenti viene mostrato il menu corrispondente al
     * ruolo dell'utente.
     *
     * @param utente utente corrente, oppure null se guest
     */
    public static void tornaAlMenu(Utente utente) {
        if (utente == null) {
            // Guest: torna al menu iniziale dell'applicazione
            ClientCM.mostraScena(new ScenaIniziale().costruisci());
        } else if (Utente.RUOLO_CLIENTE.equals(utente.getRuolo())) {
            ClientCM.mostraScena(new ScenaMenuCliente(utente).costruisci());
        } else if (Utente.RUOLO_PROIEZIONISTA.equals(utente.getRuolo())) {
            ClientCM.mostraScena(new ScenaMenuProiezionista(utente).costruisci());
        } else if (Utente.RUOLO_BIGLIETTAIO.equals(utente.getRuolo())) {
            ClientCM.mostraScena(new ScenaMenuBigliettaio(utente).costruisci());
        } else {
            // Ruolo non riconosciuto: per sicurezza si torna al menu iniziale
            ClientCM.mostraScena(new ScenaIniziale().costruisci());
        }
    }

    /**
     * Crea un pulsante "Home" gia' configurato per tornare alla
     * schermata principale dell'utente indicato.
     *
     * @param utente utente corrente, oppure null se guest
     * @return pulsante pronto da aggiungere a un contenitore JavaFX
     */
    public static Button creaBottoneHome(Utente utente) {
        Button btnHome = new Button("\u2302 Home");
        btnHome.setOnAction(e -> tornaAlMenu(utente));
        return btnHome;
    }
}