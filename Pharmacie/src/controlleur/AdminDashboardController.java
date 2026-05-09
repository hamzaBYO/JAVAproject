package controlleur;

import DAO.MedicamentDAO;
import DAO.UtilisateurDAO;
import modele.Medicament;
import modele.Utilisateur;
import vue.Admindashboardview;
import vue.LoginView;

import javax.swing.*;

public class AdminDashboardController {

    private final Admindashboardview view;
    private final UtilisateurDAO     utilisateurDAO = new UtilisateurDAO();
    private final MedicamentDAO      medicamentDAO  = new MedicamentDAO();
    private final Utilisateur        currentUser;

    public AdminDashboardController(Admindashboardview view, Utilisateur currentUser) {
        this.view        = view;
        this.currentUser = currentUser;
        bindEvents();
        loadAllUsers();
        loadAllMeds();
        view.showUsers();
    }

    // ── Events ────────────────────────────────────────────────────────────────

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

    // ── Users ─────────────────────────────────────────────────────────────────

    private void loadAllUsers() {
        view.getModelUsers().setRowCount(0);
        utilisateurDAO.findAll().forEach(u ->
            view.getModelUsers().addRow(new Object[]{ u.getId(), u.getCin(), u.getNom(), u.getPrenom(), u.getEmail(), u.getType() }));
    }

    private void addUser() {
        String id = view.getUserId().trim(), nom = view.getUserNom().trim();
        String email = view.getUserEmail().trim(), pwd = view.getUserPwd().trim();

        if (id.isEmpty() || nom.isEmpty() || email.isEmpty() || pwd.isEmpty()) {
            view.showError("Remplissez tous les champs obligatoires."); return;
        }
        if (!email.contains("@")) { view.showError("Email invalide."); return; }

        if (utilisateurDAO.create(new Utilisateur(id, view.getUserCin().trim(), nom,
                view.getUserPrenom().trim(), email, pwd, view.getUserType()))) {
            view.showSuccess("Utilisateur « " + nom + " » ajouté !"); loadAllUsers();
        } else { view.showError("Erreur lors de l'ajout."); }
    }

    private void updateUser() {
        String id = view.getUserId().trim();
        if (id.isEmpty()) { view.showError("Sélectionnez un utilisateur."); return; }

        if (utilisateurDAO.update(new Utilisateur(id, view.getUserCin().trim(), view.getUserNom().trim(),
                view.getUserPrenom().trim(), view.getUserEmail().trim(), view.getUserPwd().trim(), view.getUserType()))) {
            view.showSuccess("Utilisateur mis à jour !"); loadAllUsers();
        } else { view.showError("Erreur lors de la mise à jour."); }
    }

    private void deleteUser() {
        String id = view.getUserId().trim();
        if (id.isEmpty()) { view.showError("Sélectionnez un utilisateur."); return; }
        if (currentUser != null && id.equals(currentUser.getId())) {
            view.showError("Vous ne pouvez pas supprimer votre propre compte !"); return;
        }
        if (view.confirm("Supprimer l'utilisateur « " + id + " » ?") != JOptionPane.YES_OPTION) return;

        if (utilisateurDAO.delete(id)) { view.showSuccess("Utilisateur supprimé !"); loadAllUsers(); }
        else { view.showError("Erreur lors de la suppression."); }
    }

    private void searchUser() {
        String kw = view.getSearchUser().toLowerCase().trim();
        if (kw.isEmpty()) { loadAllUsers(); return; }

        view.getModelUsers().setRowCount(0);
        utilisateurDAO.findAll().stream()
            .filter(u -> u.getNom().toLowerCase().contains(kw)
                      || u.getPrenom().toLowerCase().contains(kw)
                      || u.getEmail().toLowerCase().contains(kw)
                      || u.getId().toLowerCase().contains(kw))
            .forEach(u -> view.getModelUsers().addRow(
                new Object[]{ u.getId(), u.getCin(), u.getNom(), u.getPrenom(), u.getEmail(), u.getType() }));

        if (view.getModelUsers().getRowCount() == 0)
            view.showError("Aucun utilisateur trouvé pour : « " + kw + " »");
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

    // ── Medicines ─────────────────────────────────────────────────────────────

    private void loadAllMeds() {
        view.getModelMeds().setRowCount(0);
        medicamentDAO.findAll().forEach(m ->
            view.getModelMeds().addRow(new Object[]{ m.getIdMedicament(), m.getNom(), String.format("%.3f", m.getPrix()), m.getQuantiteStock(), m.getType() }));
    }

    private void addMed() {
        String id = view.getMedId().trim(), nom = view.getMedNom().trim();
        String prixS = view.getMedPrix().trim(), stkS = view.getMedStock().trim();

        if (id.isEmpty() || nom.isEmpty() || prixS.isEmpty() || stkS.isEmpty()) {
            view.showError("Remplissez tous les champs du médicament."); return;
        }

        double prix; int stock;
        try { prix  = Double.parseDouble(prixS); } catch (NumberFormatException ex) { view.showError("Prix invalide."); return; }
        try { stock = Integer.parseInt(stkS);     } catch (NumberFormatException ex) { view.showError("Stock invalide."); return; }
        if (prix < 0 || stock < 0) { view.showError("Le prix et le stock doivent être positifs."); return; }

        if (medicamentDAO.create(new Medicament(id, nom, prix, stock, view.getMedType()))) {
            view.showSuccess("Médicament « " + nom + " » ajouté !"); loadAllMeds();
        } else { view.showError("Erreur lors de l'ajout."); }
    }

    private void updateMed() {
        String id = view.getMedId().trim();
        if (id.isEmpty()) { view.showError("Sélectionnez un médicament."); return; }

        double prix; int stock;
        try { prix  = Double.parseDouble(view.getMedPrix().trim()); } catch (NumberFormatException ex) { view.showError("Prix invalide."); return; }
        try { stock = Integer.parseInt(view.getMedStock().trim());   } catch (NumberFormatException ex) { view.showError("Stock invalide."); return; }

        if (medicamentDAO.update(new Medicament(id, view.getMedNom().trim(), prix, stock, view.getMedType()))) {
            view.showSuccess("Médicament mis à jour !"); loadAllMeds();
        } else { view.showError("Erreur lors de la mise à jour."); }
    }

    private void deleteMed() {
        String id = view.getMedId().trim();
        if (id.isEmpty()) { view.showError("Sélectionnez un médicament."); return; }
        if (view.confirm("Supprimer le médicament « " + id + " » ?") != JOptionPane.YES_OPTION) return;

        if (medicamentDAO.delete(id)) { view.showSuccess("Médicament supprimé !"); loadAllMeds(); }
        else { view.showError("Erreur lors de la suppression. Il est peut-être lié à des ordonnances."); }
    }

    private void searchMed() {
        String kw = view.getSearchMed().toLowerCase().trim();
        if (kw.isEmpty()) { loadAllMeds(); return; }

        view.getModelMeds().setRowCount(0);
        medicamentDAO.findAll().stream()
            .filter(m -> m.getNom().toLowerCase().contains(kw)
                      || m.getIdMedicament().toLowerCase().contains(kw)
                      || m.getType().toLowerCase().contains(kw))
            .forEach(m -> view.getModelMeds().addRow(
                new Object[]{ m.getIdMedicament(), m.getNom(), String.format("%.3f", m.getPrix()), m.getQuantiteStock(), m.getType() }));

        if (view.getModelMeds().getRowCount() == 0)
            view.showError("Aucun médicament trouvé pour : « " + kw + " »");
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

    // ── Logout ────────────────────────────────────────────────────────────────

    private void logout() {
        if (view.confirm("Voulez-vous vraiment vous déconnecter ?") != JOptionPane.YES_OPTION) return;
        view.dispose();
        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }
}