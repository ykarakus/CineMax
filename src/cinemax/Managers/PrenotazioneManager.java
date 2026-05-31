package cinemax.Managers;

import java.util.ArrayList;
import java.util.List;

import cinemax.Models.Prenotazione;
import cinemax.Managers.ProiezioneManager;
import cinemax.Managers.UtenteManager;

public class PrenotazioneManager {

	
	private UtenteManager utenteManager;
	private ProiezioneManager proiezioneManager;
	private ArrayList<Prenotazione> prenotazioni;
	
	
	public PrenotazioneManager(UtenteManager utenteManager, ProiezioneManager proiezioneManager) {
		this.proiezioneManager = proiezioneManager;
		this.utenteManager = utenteManager;
		this.prenotazioni = new ArrayList<>();

	}
	
}
