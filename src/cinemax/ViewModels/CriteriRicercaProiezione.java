/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax.ViewModels;

import java.util.Date;

/**
 * Classe che rappresenta i criteri utilizzati per la ricerca delle proiezioni.
 *
 * I criteri possono riguardare:
 * - titolo del film;
 * - tipologia o genere;
 * - data di inizio;
 * - data di fine;
 * - costo esatto del biglietto;
 * - costo minimo;
 * - costo massimo.
 */
public class CriteriRicercaProiezione {

	private String titolo;
	private String tipologia;
	private Date dataInizio;
	private Date dataFine;
	private Double costoBiglietto;
	private Double costoMax;
	private Double costoMin;

	/**
	 * Restituisce il titolo del film usato come criterio di ricerca.
	 *
	 * @return titolo del film, anche parziale
	 */
	public String getTitolo() {
		return titolo;
	}

	/**
	 * Imposta il titolo del film usato come criterio di ricerca.
	 *
	 * @param titolo titolo del film, anche parziale
	 */
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	/**
	 * Restituisce la tipologia o il genere del film usato come criterio di ricerca.
	 *
	 * @return tipologia o genere del film
	 */
	public String getTipologia() {
		return tipologia;
	}

	/**
	 * Imposta la tipologia o il genere del film usato come criterio di ricerca.
	 *
	 * @param tipologia tipologia o genere del film
	 */
	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}

	/**
	 * Restituisce la data iniziale dell'intervallo di ricerca.
	 *
	 * @return data iniziale del filtro, oppure null se non impostata
	 */
	public Date getDataInizio() {
		return dataInizio;
	}

	/**
	 * Imposta la data iniziale dell'intervallo di ricerca.
	 *
	 * @param dataInizio data iniziale del filtro
	 */
	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}

	/**
	 * Restituisce la data finale dell'intervallo di ricerca.
	 *
	 * @return data finale del filtro, oppure null se non impostata
	 */
	public Date getDataFine() {
		return dataFine;
	}

	/**
	 * Imposta la data finale dell'intervallo di ricerca.
	 *
	 * @param dataFine data finale del filtro
	 */
	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
	}

	/**
	 * Imposta il costo esatto del biglietto usato come criterio di ricerca.
	 *
	 * @param costoBiglietto costo esatto del biglietto
	 */
	public void setCostoBiglietto(Double costoBiglietto) {
		this.costoBiglietto = costoBiglietto;
	}

	/**
	 * Restituisce il costo esatto del biglietto usato come criterio di ricerca.
	 *
	 * @return costo esatto del biglietto, oppure null se non impostato
	 */
	public Double getCostoBiglietto() {
		return costoBiglietto;
	}

	/**
	 * Imposta il costo massimo del biglietto usato come criterio di ricerca.
	 *
	 * @param costoMax costo massimo del biglietto
	 */
	public void setCostoMax(Double costoMax) {
		this.costoMax = costoMax;
	}

	/**
	 * Restituisce il costo massimo del biglietto usato come criterio di ricerca.
	 *
	 * @return costo massimo del biglietto, oppure null se non impostato
	 */
	public Double getCostoMax() {
		return this.costoMax;
	}

	/**
	 * Imposta il costo minimo del biglietto usato come criterio di ricerca.
	 *
	 * @param costoMin costo minimo del biglietto
	 */
	public void setCostoMin(Double costoMin) {
		this.costoMin = costoMin;
	}

	/**
	 * Restituisce il costo minimo del biglietto usato come criterio di ricerca.
	 *
	 * @return costo minimo del biglietto, oppure null se non impostato
	 */
	public Double getCostoMin() {
		return this.costoMin;
	}
}