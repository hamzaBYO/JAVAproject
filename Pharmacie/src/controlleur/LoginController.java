package controlleur;

import DAO.UtilisateurDAO;
import modele.Utilisateur;
import vue.Admindashboardview;
import vue.LoginView;
import vue.PharmacienDashboardView;
import vue.RegisterView;

public class LoginController {

    private final LoginView      view;
    private final UtilisateurDAO dao = new UtilisateurDAO();

    public LoginController(LoginView view) {
        this.view = view;
        view.setOnLogin     (this::handleLogin);
        view.setOnGoRegister(this::openRegister);
    }

    private void handleLogin() {
        String email    = view.getEmail();
        String password = view.getPassword();

        if (email.isEmpty() || password.isEmpty()) {
            view.showError("Veuillez remplir tous les champs."); return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            view.showError("Adresse email invalide."); return;
        }

        Utilisateur user = dao.authenticate(email, password);
        if (user == null) { view.showError("Email ou mot de passe incorrect."); return; }

        view.dispose();
        switch (user.getType().toUpperCase().trim()) {
            case "ADMIN": {
                Admindashboardview v = new Admindashboardview();
                new AdminDashboardController(v, user);
                v.setVisible(true);
                break;
            }
            case "PHARMACIEN": {
                PharmacienDashboardView v = new PharmacienDashboardView();
                new Pharmaciendashboardcontroller(v, user);
                v.setVisible(true);
                break;
            }
            default:
                view.showError("Rôle inconnu : " + user.getType());
        }
    }

    private void openRegister() {
        RegisterView v = new RegisterView();
        new RegisterController(v);
        v.setVisible(true);
    }

    public static void main(String[] args) {
        LoginView v = new LoginView();
        new LoginController(v);
        v.setVisible(true);
    }
}