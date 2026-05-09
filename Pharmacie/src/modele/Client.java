package modele;

import java.util.Objects;

public class Client {
    
	private String idClient;
    private String nom;
    private String prenom;
    private String telephone;
    private String credit;
    
    public Client(String idClient, String nom, String prenom, String telephone, String credit) {
		this.idClient = idClient;
		this.nom = nom;
		this.prenom = prenom;
		this.telephone = telephone;
		this.credit = credit;
	}
	@Override
	public int hashCode() {
		return Objects.hash(credit, idClient, nom, prenom, telephone);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Client other = (Client) obj;
		return Objects.equals(credit, other.credit) && Objects.equals(idClient, other.idClient)
				&& Objects.equals(nom, other.nom) && Objects.equals(prenom, other.prenom)
				&& Objects.equals(telephone, other.telephone);
	}
	@Override
	public String toString() {
		return "Client [idClient=" + idClient + ", nom=" + nom + ", prenom=" + prenom + ", telephone=" + telephone
				+ ", credit=" + credit + "]";
	}
	public String getIdClient() {
		return idClient;
	}
	public void setIdClient(String idClient) {
		this.idClient = idClient;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}

    
}