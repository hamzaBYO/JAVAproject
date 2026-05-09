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
        view.getBtnLogin()      .addActionListener(e -> handleLogin());
        view.getBtnGoRegister() .addActionListener(e -> openRegister());
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    private void handleLogin() {
        String email    = view.getEmail().trim();
        String password = view.getPassword().trim();

        if (email.isEmpty() || password.isEmpty()) {
            view.showError("Veuillez remplir tous les champs."); return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            view.showError("Adresse email invalide."); return;
        }

        Utilisateur user = dao.authenticate(email, password);
        if (user == null) {
            view.showError("Email ou mot de passe incorrect."); return;
        }

        view.dispose();
        redirectUser(user);
    }

    // ── Redirection selon le rôle ─────────────────────────────────────────────

    private void redirectUser(Utilisateur user) {
        switch (user.getType().toUpperCase().trim()) {
            case "ADMIN": {
                Admindashboardview adminView = new Admindashboardview();
                new AdminDashboardController(adminView, user);
                adminView.setVisible(true);
                break;
            }
            case "PHARMACIEN": {
                PharmacienDashboardView pharmView = new PharmacienDashboardView();
                new Pharmaciendashboardcontroller(pharmView, user);
                pharmView.setVisible(true);
                break;
            }
            default:
                view.showError("Rôle inconnu : " + user.getType());
        }
    }

    // ── Register ──────────────────────────────────────────────────────────────

    private void openRegister() {
        RegisterView regView = new RegisterView();
        new RegisterController(regView);
        regView.setVisible(true);
    }

    // ── Entry point ───────────────────────────────────────────────────────────

    public static void main(String[] args) {
        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }
}