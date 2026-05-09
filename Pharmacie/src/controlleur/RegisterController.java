package controlleur;

import DAO.UtilisateurDAO;
import modele.Utilisateur;
import vue.LoginView;
import vue.RegisterView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * RegisterController - Gère la logique d'inscription d'un utilisateur
 * Valide les champs, crée l'utilisateur en base, redirige vers Login
 */
public class RegisterController {

    private final RegisterView   view;
    private final UtilisateurDAO dao;

    public RegisterController(RegisterView view) {
        this.view = view;
        this.dao  = new UtilisateurDAO();
        bindEvents();
    }

    // ─── Liaison des événements ────────────────────────────────────────────────

    private void bindEvents() {

        // Bouton "S'inscrire"
        view.getBtnRegister().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });

        // Lien "Se connecter"
        view.getBtnGoLogin().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToLogin();
            }
        });
    }

    // ─── Logique Inscription ──────────────────────────────────────────────────

    private void handleRegister() {
        // ── Récupération des valeurs ──
        String id       = view.getId().trim();
        String cin      = view.getCin().trim();
        String nom      = view.getNom().trim();
        String prenom   = view.getPrenom().trim();
        String email    = view.getEmail().trim();
        String password = view.getPassword().trim();
        String type     = view.getTypee();

        // ── Validations ──
        if (id.isEmpty()) {
            view.showError("L'identifiant est obligatoire.");
            return;
        }
        if (cin.isEmpty() || cin.length() != 8 || !cin.matches("\\d{8}")) {
            view.showError("Le CIN doit contenir exactement 8 chiffres.");
            return;
        }
        if (nom.isEmpty()) {
            view.showError("Le nom est obligatoire.");
            return;
        }
        if (prenom.isEmpty()) {
            view.showError("Le prénom est obligatoire.");
            return;
        }
        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            view.showError("Veuillez saisir un email valide.");
            return;
        }
        if (password.isEmpty() || password.length() < 4) {
            view.showError("Le mot de passe doit contenir au moins 4 caractères.");
            return;
        }
        if (type == null || type.isEmpty()) {
            view.showError("Veuillez sélectionner un type d'utilisateur.");
            return;
        }

        // ── Vérifier si l'ID existe déjà ──
        if (dao.findById(id) != null) {
            view.showError("Un utilisateur avec l'identifiant « " + id + " » existe déjà.");
            return;
        }

        // ── Créer l'utilisateur ──
        Utilisateur newUser = new Utilisateur(id, cin, nom, prenom, email, password, type);
        boolean success = dao.create(newUser);

        if (success) {
            view.showSuccess("Compte créé avec succès !\nVous pouvez maintenant vous connecter.");
            goToLogin();
        } else {
            view.showError("Erreur lors de la création du compte.\nVérifiez que l'email ou le CIN ne sont pas déjà utilisés.");
        }
    }

    // ─── Navigation ───────────────────────────────────────────────────────────

    private void goToLogin() {
        view.dispose();
        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }
}