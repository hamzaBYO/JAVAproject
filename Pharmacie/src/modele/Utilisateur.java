package modele;

import java.util.Objects;

public class Utilisateur {
    
	private String id;
    private String cin;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String type;
    public Utilisateur(String id, String cin, String nom, String prenom, String email, String password, String type) {
		this.id = id;
		this.cin = cin;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.password = password;
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCin() {
		return cin;
	}
	public void setCin(String cin) {
		this.cin = cin;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public int hashCode() {
		return Objects.hash(cin, email, id, nom, password, prenom, type);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Utilisateur other = (Utilisateur) obj;
		return Objects.equals(cin, other.cin) && Objects.equals(email, other.email) && Objects.equals(id, other.id)
				&& Objects.equals(nom, other.nom) && Objects.equals(password, other.password)
				&& Objects.equals(prenom, other.prenom) && Objects.equals(type, other.type);
	}
	@Override
	public String toString() {
		return "Utilisateur [id=" + id + ", cin=" + cin + ", nom=" + nom + ", prenom=" + prenom + ", email=" + email
				+ ", password=" + password + ", type=" + type + "]";
	}
    
}