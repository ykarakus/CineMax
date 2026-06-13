/**
 * Autori:
 * - Karakus Yasemin, matricola 762746, sede VA
 * - Choudhry Maha Ilyas, matricola 747119, sede VA
 */

package cinemax.ViewModels;
import java.util.Date;

public class CriteriRicercaProiezione {
	
	private String titolo;
	private String tipologia;
	private Date  dataInizio;
	private Date  dataFine;
	private Double costoBiglietto;
	private Double costoMax;
	private Double costoMin;
	
	public String getTitolo() {
		return titolo;
	}
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}
	public String getTipologia() {
		return tipologia;
	}
	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}
	public Date getDataInizio() {
		return dataInizio;
	}
	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}
	public Date getDataFine() {
		return dataFine;
	}
	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
	}
	public void setCostoBiglietto(Double costoBiglietto) {
		this.costoBiglietto = costoBiglietto;
	}
	public Double getCostoBiglietto() {
		return costoBiglietto;
	}
	
	public void setCostoMax(Double costoMax) {
		this.costoMax = costoMax;
	}
	public Double getCostoMax() {
		return this.costoMax;
	}
	public void setCostoMin(Double costoMin) {
		this.costoMin = costoMin;
	}
	public Double getCostoMin() {
		return this.costoMin;
	}


}