package cinemax.Managers;

import java.util.List;

import cinemax.Models.Utente;

public class UtenteManager {

	public List<Utente> utenti;
	private Utente utente_corrente; // utente autenticato

	public UtenteManager() {

		// TODO: lettura di utenti dalla lista csv utilizzando csvreader in helpers
	}

	public boolean login(String username, String password) {

		// TODO: controllare se l'utente esiste nella lista
		// e se le credenziali sono corrette

		return true;
	}

	public boolean registrazione(String nome, String cognome, String username, String password, String datanascita,
			String domicilio) {

		// TODO: validazione dei valori
		// e inserire l'utente nella lista

		return true;
	}

	public void logout() {

		// non esiste un utente autenticato
		utente_corrente = null;
	}

	public Utente getUtenteCorrente() {

		return utente_corrente = null;
	}

}
