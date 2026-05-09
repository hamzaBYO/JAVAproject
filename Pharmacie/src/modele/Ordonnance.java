package modele;

import java.util.Date;

public class Ordonnance {
    public Ordonnance(String idOrdonnance, Date date, Client idClient) {
		super();
		this.idOrdonnance = idOrdonnance;
		this.date = date;
		this.idClient = idClient;
	}
	private String idOrdonnance;
    private Date date;
    private Client idClient;
	public String getIdOrdonnance() {
		return idOrdonnance;
	}
	public void setIdOrdonnance(String idOrdonnance) {
		this.idOrdonnance = idOrdonnance;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Client getIdClient() {
		return idClient;
	}
	public void setIdClient(Client idClient) {
		this.idClient = idClient;
	}
}
    
