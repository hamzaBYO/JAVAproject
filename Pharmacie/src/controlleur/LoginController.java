package controlleur;

import DAO.UtilisateurDAO;
import modele.Utilisateur;
import vue.Admindashboardview;
import vue.LoginView;
import vue.PharmacienDashboardView;
import vue.RegisterView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * LoginController - Gère la logique de connexion
 * Redirige vers AdminDashboard ou PharmacienDashboard selon le type
 */
public class LoginController {

    private final LoginView    view;
    private final UtilisateurDAO dao;

    public LoginController(LoginView view) {
        this.view = view;
        this.dao  = new UtilisateurDAO();
        bindEvents();
    }

    // ─── Liaison des événements ────────────────────────────────────────────────

    private void bindEvents() {

        // Bouton "Se connecter"
        view.getBtnLogin().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Bouton "S'inscrire" → ouvrir RegisterView
        view.getBtnGoRegister().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegister();
            }
        });
    }

    // ─── Logique Login ────────────────────────────────────────────────────────

    private void handleLogin() {
        String email    = view.getEmail().trim();
        String password = view.getPassword().trim();

        // ── Validation des champs ──
        if (email.isEmpty()) {
            view.showError("Veuillez saisir votre adresse email.");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            view.showError("Format d'email invalide.");
            return;
        }
        if (password.isEmpty()) {
            view.showError("Veuillez saisir votre mot de passe.");
            return;
        }

        // ── Authentification via DAO ──
        Utilisateur user = dao.authenticate(email, password);

        if (user == null) {
            view.showError("Email ou mot de passe incorrect.\nVeuillez réessayer.");
            return;
        }

        // ── Redirection selon le rôle ──
        view.dispose();

        String type = user.getType().toUpperCase().trim();

        switch (type) {
            case "ADMIN":
                openAdminDashboard(user);
                break;

            case "PHARMACIEN":
                openPharmacienDashboard(user);
                break;

            default:
                view.showError("Rôle utilisateur inconnu : " + user.getType());
                break;
        }
    }

    // ─── Navigation ───────────────────────────────────────────────────────────

    private void openAdminDashboard(Utilisateur user) {
        Admindashboardview adminView = new Admindashboardview();
        new AdminDashboardController(adminView, user);
        adminView.setVisible(true);
    }

    private void openPharmacienDashboard(Utilisateur user) {
        PharmacienDashboardView pharmView = new PharmacienDashboardView();
        new Pharmaciendashboardcontroller(pharmView, user);
        pharmView.setVisible(true);
    }

    private void openRegister() {
        RegisterView regView = new RegisterView();
        new RegisterController(regView);
        regView.setVisible(true);
    }

    // ─── Point d'entrée ───────────────────────────────────────────────────────

    public static void main(String[] args) {
    	LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }
}