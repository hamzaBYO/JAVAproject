package modele;


public class LigneOrd {
	private Ordonnance idOrdonnance;
    private Medicament idMedicament;
    private int quantite;
    public LigneOrd(Ordonnance idOrdonnance, Medicament idMedicament, int quantite) {
		super();
		this.idOrdonnance = idOrdonnance;
		this.idMedicament = idMedicament;
		this.quantite = quantite;
	}
	public Ordonnance getIdOrdonnance() {
		return idOrdonnance;
	}
	public void setIdOrdonnance(Ordonnance idOrdonnance) {
		this.idOrdonnance = idOrdonnance;
	}
	public Medicament getIdMedicament() {
		return idMedicament;
	}
	public void setIdMedicament(Medicament idMedicament) {
		this.idMedicament = idMedicament;
	}
	public int getQuantite() {
		return quantite;
	}
	public void setQuantite(int quantite) {
		this.quantite = quantite;
	}
    
}