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
        view.getBtnRegister().addActionListener(e -> handleRegister());
        view.getBtnGoLogin() .addActionListener(e -> goToLogin());
    }

    // ── Register ──────────────────────────────────────────────────────────────

    private void handleRegister() {
        String id       = view.getId().trim();
        String cin      = view.getCin().trim();
        String nom      = view.getNom().trim();
        String prenom   = view.getPrenom().trim();
        String email    = view.getEmail().trim();
        String password = view.getPassword().trim();
        String type     = view.getTypee();

        if (id.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
            view.showError("L'ID, le nom et le prénom sont obligatoires."); return;
        }
        if (!cin.matches("\\d{8}")) {
            view.showError("Le CIN doit contenir exactement 8 chiffres."); return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            view.showError("Email invalide."); return;
        }
        if (password.length() < 4) {
            view.showError("Le mot de passe doit contenir au moins 4 caractères."); return;
        }
        if (type == null || type.isEmpty()) {
            view.showError("Veuillez sélectionner un type d'utilisateur."); return;
        }

        if (dao.create(new Utilisateur(id, cin, nom, prenom, email, password, type))) {
            view.showSuccess("Compte créé ! Vous pouvez maintenant vous connecter.");
            goToLogin();
        } else {
            view.showError("Erreur lors de la création. L'ID, l'email ou le CIN est peut-être déjà utilisé.");
        }
    }

    // ── Go to login ───────────────────────────────────────────────────────────

    private void goToLogin() {
        view.dispose();
        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }
}