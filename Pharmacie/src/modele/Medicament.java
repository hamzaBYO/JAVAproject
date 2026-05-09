package modele;

import java.util.Objects;

public class Medicament {
 
	private String idMedicament;
    private String nom;
    private double prix;
    private int quantiteStock;
    private String type;
    public Medicament(String idMedicament, String nom, double prix, int quantiteStock, String type) {
		super();
		this.idMedicament = idMedicament;
		this.nom = nom;
		this.prix = prix;
		this.quantiteStock = quantiteStock;
		this.type = type;
	}
	public String getIdMedicament() {
		return idMedicament;
	}
	public void setIdMedicament(String idMedicament) {
		this.idMedicament = idMedicament;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public double getPrix() {
		return prix;
	}
	public void setPrix(double prix) {
		this.prix = prix;
	}
	public int getQuantiteStock() {
		return quantiteStock;
	}
	public void setQuantiteStock(int quantiteStock) {
		this.quantiteStock = quantiteStock;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public int hashCode() {
		return Objects.hash(idMedicament, nom, prix, quantiteStock, type);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Medicament other = (Medicament) obj;
		return Objects.equals(idMedicament, other.idMedicament) && Objects.equals(nom, other.nom)
				&& Double.doubleToLongBits(prix) == Double.doubleToLongBits(other.prix)
				&& quantiteStock == other.quantiteStock && Objects.equals(type, other.type);
	}
	@Override
	public String toString() {
		return "Medicament [idMedicament=" + idMedicament + ", nom=" + nom + ", prix=" + prix + ", quantiteStock="
				+ quantiteStock + ", type=" + type + "]";
	}

   
}
