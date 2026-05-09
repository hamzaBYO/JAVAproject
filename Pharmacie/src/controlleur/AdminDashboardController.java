package controlleur;

import DAO.MedicamentDAO;
import DAO.UtilisateurDAO;
import modele.Medicament;
import modele.Utilisateur;
import vue.Admindashboardview;
import vue.LoginView;

import javax.swing.*;
import java.util.List;

public class AdminDashboardController {

    private final Admindashboardview view;
    private final UtilisateurDAO     utilisateurDAO;
    private final MedicamentDAO      medicamentDAO;
    private final Utilisateur        currentUser;

    public AdminDashboardController(Admindashboardview view, Utilisateur currentUser) {
        this.view           = view;
        this.currentUser    = currentUser;
        this.utilisateurDAO = new UtilisateurDAO();
        this.medicamentDAO  = new MedicamentDAO();
        bindEvents();
        loadAllUsers();
        loadAllMeds();
        view.showUsers();
    }

    // ── Événements ────────────────────────────────────────────────────────────

    private void bindEvents() {
        view.getBtnNavUsers().addActionListener(e -> view.showUsers());
        view.getBtnNavMeds() .addActionListener(e -> view.showMeds());
        view.getBtnLogout()  .addActionListener(e -> logout());

        view.getBtnAddUser()   .addActionListener(e -> addUser());
        view.getBtnUpdateUser().addActionListener(e -> updateUser());
        view.getBtnDeleteUser().addActionListener(e -> deleteUser());
        view.getBtnSearchUser().addActionListener(e -> searchUser());
        view.getBtnClearUser() .addActionListener(e -> loadAllUsers());

        view.getTableUsers().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillUserFormFromTable();
        });

        view.getBtnAddMed()   .addActionListener(e -> addMed());
        view.getBtnUpdateMed().addActionListener(e -> updateMed());
        view.getBtnDeleteMed().addActionListener(e -> deleteMed());
        view.getBtnSearchMed().addActionListener(e -> searchMed());
        view.getBtnClearMed() .addActionListener(e -> loadAllMeds());

        view.getTableMeds().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillMedFormFromTable();
        });
    }

    // ── Utilisateurs ──────────────────────────────────────────────────────────

    private void loadAllUsers() {
        view.getModelUsers().setRowCount(0);
        for (Utilisateur u : utilisateurDAO.findAll())
            view.getModelUsers().addRow(new Object[]{ u.getId(), u.getCin(), u.getNom(), u.getPrenom(), u.getEmail(), u.getType() });
    }

    private void addUser() {
        String id = view.getUserId().trim(), cin = view.getUserCin().trim(), nom = view.getUserNom().trim();
        String prenom = view.getUserPrenom().trim(), email = view.getUserEmail().trim();
        String pwd = view.getUserPwd().trim(), type = view.getUserType();

        if (id.isEmpty() || nom.isEmpty() || email.isEmpty() || pwd.isEmpty()) { view.showError("Veuillez remplir tous les champs obligatoires (ID, Nom, Email, Mot de passe)."); return; }
        if (!email.contains("@")) { view.showError("Email invalide."); return; }
        if (utilisateurDAO.findById(id) != null) { view.showError("Un utilisateur avec l'ID « " + id + " » existe déjà."); return; }

        if (utilisateurDAO.create(new Utilisateur(id, cin, nom, prenom, email, pwd, type))) {
            view.showSuccess("Utilisateur « " + nom + " » ajouté avec succès !"); loadAllUsers();
        } else { view.showError("Erreur lors de l'ajout. Vérifiez les données."); }
    }

    private void updateUser() {
        String id = view.getUserId().trim();
        if (id.isEmpty()) { view.showError("Sélectionnez un utilisateur dans la table ou saisissez son ID."); return; }
        if (utilisateurDAO.findById(id) == null) { view.showError("Aucun utilisateur trouvé avec l'ID : " + id); return; }

        Utilisateur u = new Utilisateur(id, view.getUserCin().trim(), view.getUserNom().trim(),
            view.getUserPrenom().trim(), view.getUserEmail().trim(), view.getUserPwd().trim(), view.getUserType());

        if (utilisateurDAO.update(u)) { view.showSuccess("Utilisateur mis à jour avec succès !"); loadAllUsers(); }
        else { view.showError("Erreur lors de la mise à jour."); }
    }

    private void deleteUser() {
        String id = view.getUserId().trim();
        if (id.isEmpty()) { view.showError("Sélectionnez ou saisissez l'ID de l'utilisateur à supprimer."); return; }
        if (currentUser != null && id.equals(currentUser.getId())) { view.showError("Vous ne pouvez pas supprimer votre propre compte !"); return; }
        if (view.confirm("Êtes-vous sûr de vouloir supprimer l'utilisateur « " + id + " » ?") != JOptionPane.YES_OPTION) return;

        if (utilisateurDAO.delete(id)) { view.showSuccess("Utilisateur supprimé avec succès !"); loadAllUsers(); }
        else { view.showError("Erreur lors de la suppression."); }
    }

    private void searchUser() {
        String kw = view.getSearchUser().toLowerCase().trim();
        if (kw.isEmpty()) { loadAllUsers(); return; }

        view.getModelUsers().setRowCount(0);
        for (Utilisateur u : utilisateurDAO.findAll()) {
            if (u.getNom().toLowerCase().contains(kw) || u.getPrenom().toLowerCase().contains(kw)
             || u.getEmail().toLowerCase().contains(kw) || u.getId().toLowerCase().contains(kw))
                view.getModelUsers().addRow(new Object[]{ u.getId(), u.getCin(), u.getNom(), u.getPrenom(), u.getEmail(), u.getType() });
        }
        if (view.getModelUsers().getRowCount() == 0) view.showError("Aucun utilisateur trouvé pour : « " + kw + " »");
    }

    private void fillUserFormFromTable() {
        int row = view.getTableUsers().getSelectedRow();
        if (row < 0) return;
        view.fillUserForm(
            (String) view.getModelUsers().getValueAt(row, 0),
            (String) view.getModelUsers().getValueAt(row, 1),
            (String) view.getModelUsers().getValueAt(row, 2),
            (String) view.getModelUsers().getValueAt(row, 3),
            (String) view.getModelUsers().getValueAt(row, 4),
            (String) view.getModelUsers().getValueAt(row, 5)
        );
    }

    // ── Médicaments ───────────────────────────────────────────────────────────

    private void loadAllMeds() {
        view.getModelMeds().setRowCount(0);
        for (Medicament m : medicamentDAO.findAll())
            view.getModelMeds().addRow(new Object[]{ m.getIdMedicament(), m.getNom(), String.format("%.3f", m.getPrix()), m.getQuantiteStock(), m.getType() });
    }

    private void addMed() {
        String id = view.getMedId().trim(), nom = view.getMedNom().trim();
        String prixS = view.getMedPrix().trim(), stkS = view.getMedStock().trim(), type = view.getMedType();

        if (id.isEmpty() || nom.isEmpty() || prixS.isEmpty() || stkS.isEmpty()) { view.showError("Veuillez remplir tous les champs du médicament."); return; }

        double prix; int stock;
        try { prix  = Double.parseDouble(prixS); } catch (NumberFormatException ex) { view.showError("Prix invalide."); return; }
        try { stock = Integer.parseInt(stkS);     } catch (NumberFormatException ex) { view.showError("Stock invalide (entier attendu)."); return; }
        if (prix < 0 || stock < 0) { view.showError("Le prix et le stock doivent être positifs."); return; }
        if (medicamentDAO.findById(id) != null) { view.showError("Un médicament avec l'ID « " + id + " » existe déjà."); return; }

        if (medicamentDAO.create(new Medicament(id, nom, prix, stock, type))) {
            view.showSuccess("Médicament « " + nom + " » ajouté avec succès !"); loadAllMeds();
        } else { view.showError("Erreur lors de l'ajout du médicament."); }
    }

    private void updateMed() {
        String id = view.getMedId().trim();
        if (id.isEmpty()) { view.showError("Sélectionnez un médicament dans la table ou saisissez son ID."); return; }
        if (medicamentDAO.findById(id) == null) { view.showError("Médicament introuvable avec l'ID : " + id); return; }

        double prix; int stock;
        try { prix  = Double.parseDouble(view.getMedPrix().trim()); } catch (NumberFormatException ex) { view.showError("Prix invalide."); return; }
        try { stock = Integer.parseInt(view.getMedStock().trim());   } catch (NumberFormatException ex) { view.showError("Stock invalide."); return; }

        if (medicamentDAO.update(new Medicament(id, view.getMedNom().trim(), prix, stock, view.getMedType()))) {
            view.showSuccess("Médicament mis à jour avec succès !"); loadAllMeds();
        } else { view.showError("Erreur lors de la mise à jour."); }
    }

    private void deleteMed() {
        String id = view.getMedId().trim();
        if (id.isEmpty()) { view.showError("Sélectionnez ou saisissez l'ID du médicament à supprimer."); return; }
        if (view.confirm("Êtes-vous sûr de vouloir supprimer le médicament « " + id + " » ?") != JOptionPane.YES_OPTION) return;

        if (medicamentDAO.delete(id)) { view.showSuccess("Médicament supprimé avec succès !"); loadAllMeds(); }
        else { view.showError("Erreur lors de la suppression. Il est peut-être lié à des ordonnances."); }
    }

    private void searchMed() {
        String kw = view.getSearchMed().toLowerCase().trim();
        if (kw.isEmpty()) { loadAllMeds(); return; }

        view.getModelMeds().setRowCount(0);
        for (Medicament m : medicamentDAO.findAll()) {
            if (m.getNom().toLowerCase().contains(kw) || m.getIdMedicament().toLowerCase().contains(kw)
             || m.getType().toLowerCase().contains(kw))
                view.getModelMeds().addRow(new Object[]{ m.getIdMedicament(), m.getNom(), String.format("%.3f", m.getPrix()), m.getQuantiteStock(), m.getType() });
        }
        if (view.getModelMeds().getRowCount() == 0) view.showError("Aucun médicament trouvé pour : « " + kw + " »");
    }

    private void fillMedFormFromTable() {
        int row = view.getTableMeds().getSelectedRow();
        if (row < 0) return;
        view.fillMedForm(
            (String) view.getModelMeds().getValueAt(row, 0),
            (String) view.getModelMeds().getValueAt(row, 1),
            (String) view.getModelMeds().getValueAt(row, 2),
            String.valueOf(view.getModelMeds().getValueAt(row, 3)),
            (String) view.getModelMeds().getValueAt(row, 4)
        );
    }

    // ── Déconnexion ───────────────────────────────────────────────────────────

    private void logout() {
        if (view.confirm("Voulez-vous vraiment vous déconnecter ?") != JOptionPane.YES_OPTION) return;
        view.dispose();
        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }
}