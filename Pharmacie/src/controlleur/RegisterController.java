package controlleur;

import DAO.UtilisateurDAO;
import modele.Utilisateur;
import vue.LoginView;
import vue.RegisterView;

public class RegisterController {

    private final RegisterView   view;
    private final UtilisateurDAO dao = new UtilisateurDAO();

    public RegisterController(RegisterView view) {
        this.view = view;
        view.setOnRegister(this::handleRegister);
        view.setOnGoLogin (this::goToLogin);
    }

    private void handleRegister() {
        String id = view.getId(), cin = view.getCin(), nom = view.getNom();
        String prenom = view.getPrenom(), email = view.getEmail();
        String password = view.getPassword(), type = view.getTypee();

        if (id.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
            view.showError("L'ID, le nom et le prénom sont obligatoires."); return;
        }
        if (!cin.matches("\\d{8}"))                          { view.showError("CIN invalide (8 chiffres)."); return; }
        if (!email.contains("@") || !email.contains("."))    { view.showError("Email invalide."); return; }
        if (password.length() < 4)                           { view.showError("Mot de passe : 4 caractères minimum."); return; }
        if (type == null || type.isEmpty())                  { view.showError("Sélectionnez un type."); return; }

        if (dao.create(new Utilisateur(id, cin, nom, prenom, email, password, type))) {
            view.showSuccess("Compte créé ! Vous pouvez vous connecter."); goToLogin();
        } else {
            view.showError("Erreur : l'ID, l'email ou le CIN est déjà utilisé.");
        }
    }

    private void goToLogin() {
        view.dispose();
        LoginView v = new LoginView();
        new LoginController(v);
        v.setVisible(true);
    }
}