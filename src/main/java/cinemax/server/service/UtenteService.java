/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */
package cinemax.server.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDate;

import cinemax.common.Richiesta;
import cinemax.common.Risposta;
import cinemax.common.Utente;

/**
 * Servizio per le operazioni sugli utenti: login e registrazione.
 * La password non viene mai memorizzata in chiaro: la verifica avviene
 * tramite la funzione {@code crypt()} di pgcrypto (BCrypt), e l'inserimento
 * usa {@code gen_salt('bf')} per generare un hash sicuro.
 */
public class UtenteService {

    private final Connection conn;

    /**
     * Costruttore.
     *
     * @param conn connessione JDBC dedicata alla sessione corrente
     */
    public UtenteService(Connection conn) {
        this.conn = conn;
    }

    /**
     * Autentica un utente verificando username e password contro il database.
     *
     * @param richiesta richiesta con parametri "username" e "password"
     * @return risposta con l'oggetto {@link Utente} in caso di successo
     */
    public Risposta login(Richiesta richiesta) {
        String username = (String) richiesta.getParametro("username");
        String password = (String) richiesta.getParametro("password");

        if (username == null || password == null)
            return new Risposta(false, "Username e password sono obbligatori", null);

        String sql = "SELECT username, nome, cognome, data_nascita, domicilio, ruolo "
                + "FROM utenti "
                + "WHERE username = ? AND password_cifrata = crypt(?, password_cifrata)";
        try {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, username);
            st.setString(2, password);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                LocalDate dataNascita = null;
                java.sql.Date d = rs.getDate("data_nascita");
                if (d != null)
                    dataNascita = d.toLocalDate();
                Utente utente = new Utente(
                        rs.getString("username"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        dataNascita,
                        rs.getString("domicilio"),
                        rs.getString("ruolo"));
                rs.close(); st.close();
                return new Risposta(true, "Login effettuato", utente);
            }
            rs.close(); st.close();
            return new Risposta(false, "Username o password errati", null);
        } catch (Exception e) {
            return new Risposta(false, "Errore durante il login: " + e.getMessage(), null);
        }
    }

    /**
     * Registra un nuovo cliente inserendo i suoi dati nel database.
     * La password viene cifrata con BCrypt tramite {@code crypt()}.
     *
     * @param richiesta richiesta con parametri: nome, cognome, username,
     *                  password, dataNascita (opzionale), domicilio
     * @return risposta di conferma o di errore
     */
    public Risposta registraCliente(Richiesta richiesta) {
        String nome      = (String) richiesta.getParametro("nome");
        String cognome   = (String) richiesta.getParametro("cognome");
        String username  = (String) richiesta.getParametro("username");
        String password  = (String) richiesta.getParametro("password");
        LocalDate dataNascita = (LocalDate) richiesta.getParametro("dataNascita");
        String domicilio = (String) richiesta.getParametro("domicilio");

        if (nome == null || cognome == null || username == null
                || password == null || domicilio == null)
            return new Risposta(false, "Parametri obbligatori mancanti", null);

        String sql = "INSERT INTO utenti "
                + "(username, nome, cognome, password_cifrata, data_nascita, domicilio, ruolo) "
                + "VALUES (?, ?, ?, crypt(?, gen_salt('bf')), ?, ?, 'cliente')";
        try {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, username);
            st.setString(2, nome);
            st.setString(3, cognome);
            st.setString(4, password);
            if (dataNascita != null)
                st.setDate(5, java.sql.Date.valueOf(dataNascita));
            else
                st.setNull(5, Types.DATE);
            st.setString(6, domicilio);
            st.executeUpdate();
            st.close();
            return new Risposta(true, "Registrazione avvenuta con successo", null);
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("unique") || msg.contains("duplicate")
                    || msg.contains("already exists") || msg.contains("pkey"))
                return new Risposta(false,
                        "Username '" + username + "' già in uso. Scegliere un altro username.", null);
            return new Risposta(false, "Errore durante la registrazione: " + e.getMessage(), null);
        }
    }
}
