package cinemax.ViewModels;
import java.util.Date;

public class ProiezioneSearch {
	
	private String titolo;
	private String tipologia;
	private Date  data_inizio;
	private Date  data_fine;
	private Double costo_biglietto;
	
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
	public Date getData_inizio() {
		return data_inizio;
	}
	public void setData_inizio(Date data_inizio) {
		this.data_inizio = data_inizio;
	}
	public Date getData_fine() {
		return data_fine;
	}
	public void setData_fine(Date data_fine) {
		this.data_fine = data_fine;
	}
	public Double getCosto_biglietto() {
		return costo_biglietto;
	}
	public void setCosto_biglietto(Double costo_biglietto) {
		this.costo_biglietto = costo_biglietto;
	}


}