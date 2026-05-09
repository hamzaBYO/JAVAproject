package controlleur;

import DAO.MedicamentDAO;
import DAO.UtilisateurDAO;
import modele.Medicament;
import modele.Utilisateur;
import vue.Admindashboardview;
import vue.ClientView;
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

        view.setOnNavUsers      (view::showUsers);
        view.setOnNavMeds       (view::showMeds);
        view.setOnNavClients    (this::openClientManager);
        view.setOnLogout        (this::logout);
        view.setOnAddUser       (this::addUser);
        view.setOnUpdateUser    (this::updateUser);
        view.setOnDeleteUser    (this::deleteUser);
        view.setOnSearchUser    (this::searchUser);
        view.setOnClearUser     (this::loadAllUsers);
        view.setOnUserRowSelected(this::fillUserFormFromTable);
        view.setOnAddMed        (this::addMed);
        view.setOnUpdateMed     (this::updateMed);
        view.setOnDeleteMed     (this::deleteMed);
        view.setOnSearchMed     (this::searchMed);
        view.setOnClearMed      (this::loadAllMeds);
        view.setOnMedRowSelected(this::fillMedFormFromTable);

        loadAllUsers();
        loadAllMeds();
        view.showUsers();
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    private void loadAllUsers() {
        view.getModelUsers().setRowCount(0);
        utilisateurDAO.findAll().forEach(u ->
            view.getModelUsers().addRow(new Object[]{ u.getId(), u.getCin(), u.getNom(), u.getPrenom(), u.getEmail(), u.getType() }));
    }

    private void addUser() {
        String id = view.getUserId(), nom = view.getUserNom();
        String email = view.getUserEmail(), pwd = view.getUserPwd();

        if (id.isEmpty() || nom.isEmpty() || email.isEmpty() || pwd.isEmpty()) {
            view.showError("Remplissez tous les champs obligatoires."); return;
        }
        if (!email.contains("@")) { view.showError("Email invalide."); return; }

        if (utilisateurDAO.create(new Utilisateur(id, view.getUserCin(), nom, view.getUserPrenom(), email, pwd, view.getUserType()))) {
            view.showSuccess("Utilisateur « " + nom + " » ajouté !"); loadAllUsers();
        } else { view.showError("Erreur lors de l'ajout."); }
    }

    private void updateUser() {
        String id = view.getUserId();
        if (id.isEmpty()) { view.showError("Sélectionnez un utilisateur."); return; }

        if (utilisateurDAO.update(new Utilisateur(id, view.getUserCin(), view.getUserNom(), view.getUserPrenom(), view.getUserEmail(), view.getUserPwd(), view.getUserType()))) {
            view.showSuccess("Utilisateur mis à jour !"); loadAllUsers();
        } else { view.showError("Erreur lors de la mise à jour."); }
    }

    private void deleteUser() {
        String id = view.getUserId();
        if (id.isEmpty()) { view.showError("Sélectionnez un utilisateur."); return; }
        if (currentUser != null && id.equals(currentUser.getId())) {
            view.showError("Vous ne pouvez pas supprimer votre propre compte !"); return;
        }
        if (view.confirm("Supprimer l'utilisateur « " + id + " » ?") != JOptionPane.YES_OPTION) return;

        if (utilisateurDAO.delete(id)) { view.showSuccess("Utilisateur supprimé !"); loadAllUsers(); }
        else { view.showError("Erreur lors de la suppression."); }
    }

    private void searchUser() {
        String kw = view.getSearchUser().toLowerCase();
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
        String id = view.getMedId(), nom = view.getMedNom();
        String prixS = view.getMedPrix(), stkS = view.getMedStock();

        if (id.isEmpty() || nom.isEmpty() || prixS.isEmpty() || stkS.isEmpty()) {
            view.showError("Remplissez tous les champs du médicament."); return;
        }

        double prix; int stock;
        try { prix  = Double.parseDouble(prixS); } catch (NumberFormatException ex) { view.showError("Prix invalide."); return; }
        try { stock = Integer.parseInt(stkS);     } catch (NumberFormatException ex) { view.showError("Stock invalide."); return; }
        if (prix < 0 || stock < 0) { view.showError("Prix et stock doivent être positifs."); return; }

        if (medicamentDAO.create(new Medicament(id, nom, prix, stock, view.getMedType()))) {
            view.showSuccess("Médicament « " + nom + " » ajouté !"); loadAllMeds();
        } else { view.showError("Erreur lors de l'ajout."); }
    }

    private void updateMed() {
        String id = view.getMedId();
        if (id.isEmpty()) { view.showError("Sélectionnez un médicament."); return; }

        double prix; int stock;
        try { prix  = Double.parseDouble(view.getMedPrix()); } catch (NumberFormatException ex) { view.showError("Prix invalide."); return; }
        try { stock = Integer.parseInt(view.getMedStock());   } catch (NumberFormatException ex) { view.showError("Stock invalide."); return; }

        if (medicamentDAO.update(new Medicament(id, view.getMedNom(), prix, stock, view.getMedType()))) {
            view.showSuccess("Médicament mis à jour !"); loadAllMeds();
        } else { view.showError("Erreur lors de la mise à jour."); }
    }

    private void deleteMed() {
        String id = view.getMedId();
        if (id.isEmpty()) { view.showError("Sélectionnez un médicament."); return; }
        if (view.confirm("Supprimer le médicament « " + id + " » ?") != JOptionPane.YES_OPTION) return;

        if (medicamentDAO.delete(id)) { view.showSuccess("Médicament supprimé !"); loadAllMeds(); }
        else { view.showError("Erreur : médicament peut-être lié à des ordonnances."); }
    }

    private void searchMed() {
        String kw = view.getSearchMed().toLowerCase();
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

    // ── Clients ───────────────────────────────────────────────────────────────

    private void openClientManager() {
        ClientView clientView = new ClientView();
        new ClientController(clientView);
        clientView.setVisible(true);
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    private void logout() {
        if (view.confirm("Voulez-vous vraiment vous déconnecter ?") != JOptionPane.YES_OPTION) return;
        view.dispose();
        LoginView v = new LoginView();
        new LoginController(v);
        v.setVisible(true);
    }
}