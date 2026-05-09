package controlleur;

import DAO.ClientDao;
import DAO.OrdonnanceDAO;
import modele.Client;
import modele.Ordonnance;
import modele.Utilisateur;
import vue.ClientView;
import vue.LoginView;
import vue.OrdonnanceView;
import vue.PharmacienDashboardView;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Pharmaciendashboardcontroller {

    private final PharmacienDashboardView view;
    private final ClientDao               clientDAO     = new ClientDao();
    private final OrdonnanceDAO           ordonnanceDAO = new OrdonnanceDAO();
    private final Utilisateur             currentUser;

    public Pharmaciendashboardcontroller(PharmacienDashboardView view, Utilisateur currentUser) {
        this.view        = view;
        this.currentUser = currentUser;

        view.setOnAdd               (this::addClient);
        view.setOnUpdate            (this::updateClient);
        view.setOnDelete            (this::deleteClient);
        view.setOnSearch            (this::searchClient);
        view.setOnClear             (() -> { view.clearForm(); loadAll(); });
        view.setOnRowSelected       (this::fillFormFromTable);
        view.setOnRowDoubleClicked  (this::openExistingPrescription);
        view.setOnVoirOrdonnances   (this::openExistingPrescription);
        view.setOnNouvelleOrdonnance(this::openNewPrescription);
        view.setOnManageClients     (this::openClientManager);
        view.setOnLogout            (this::logout);

        loadAll();
    }

    // ── Load ──────────────────────────────────────────────────────────────────

    private void loadAll() {
        view.getModel().setRowCount(0);
        clientDAO.findAll().forEach(c ->
            view.getModel().addRow(new Object[]{ c.getIdClient(), c.getNom(), c.getPrenom(), c.getTelephone(), c.getCredit() }));
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    private void addClient() {
        String id = view.getClientId(), nom = view.getClientNom(), prenom = view.getClientPrenom();
        String tel = view.getClientTel(), credit = view.getClientCredit();

        if (id.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
            view.showError("L'ID, le nom et le prénom sont obligatoires."); return;
        }
        if (!tel.matches("\\d{8}")) { view.showError("Téléphone invalide (8 chiffres)."); return; }
        if (!credit.isEmpty()) {
            try { Double.parseDouble(credit); } catch (NumberFormatException ex) { view.showError("Crédit invalide."); return; }
        }

        if (clientDAO.create(new Client(id, nom, prenom, tel, credit.isEmpty() ? "0.000" : credit))) {
            view.showSuccess("Client « " + nom + " " + prenom + " » ajouté !"); loadAll();
        } else { view.showError("Erreur lors de l'ajout."); }
    }

    private void updateClient() {
        String id = view.getClientId(), nom = view.getClientNom(), prenom = view.getClientPrenom();
        String tel = view.getClientTel(), credit = view.getClientCredit();

        if (id.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
            view.showError("L'ID, le nom et le prénom sont obligatoires."); return;
        }
        if (!tel.isEmpty() && !tel.matches("\\d{8}")) { view.showError("Téléphone invalide."); return; }
        if (!credit.isEmpty()) {
            try { Double.parseDouble(credit); } catch (NumberFormatException ex) { view.showError("Crédit invalide."); return; }
        }

        if (clientDAO.update(new Client(id, nom, prenom, tel, credit.isEmpty() ? "0.000" : credit))) {
            view.showSuccess("Client mis à jour !"); loadAll();
        } else { view.showError("Erreur lors de la mise à jour."); }
    }

    private void deleteClient() {
        String id = view.getClientId();
        if (id.isEmpty()) { view.showError("Sélectionnez un client."); return; }
        if (view.confirm("Supprimer le client « " + id + " » ?") != JOptionPane.YES_OPTION) return;

        if (clientDAO.delete(id)) { view.showSuccess("Client supprimé !"); loadAll(); }
        else { view.showError("Erreur : client peut-être lié à des ordonnances."); }
    }

    private void searchClient() {
        String kw = view.getSearchText().toLowerCase();
        if (kw.isEmpty()) { loadAll(); return; }

        view.getModel().setRowCount(0);
        clientDAO.findAll().stream()
            .filter(c -> c.getIdClient().toLowerCase().contains(kw)
                      || c.getNom().toLowerCase().contains(kw)
                      || c.getPrenom().toLowerCase().contains(kw)
                      || c.getTelephone().contains(kw))
            .forEach(c -> view.getModel().addRow(
                new Object[]{ c.getIdClient(), c.getNom(), c.getPrenom(), c.getTelephone(), c.getCredit() }));

        if (view.getModel().getRowCount() == 0)
            view.showError("Aucun client trouvé pour : « " + kw + " »");
    }

    private void fillFormFromTable() {
        int row = view.getTable().getSelectedRow();
        if (row < 0) return;
        view.fillClientForm(
            (String) view.getModel().getValueAt(row, 0),
            (String) view.getModel().getValueAt(row, 1),
            (String) view.getModel().getValueAt(row, 2),
            (String) view.getModel().getValueAt(row, 3),
            (String) view.getModel().getValueAt(row, 4)
        );
    }

    // ── Prescriptions ─────────────────────────────────────────────────────────

    private int getSelectedRow() {
        int row = view.getTable().getSelectedRow();
        if (row < 0) view.showError("Sélectionnez d'abord un client.");
        return row;
    }

    private void openExistingPrescription() {
        int row = getSelectedRow(); if (row < 0) return;

        String clientId = (String) view.getModel().getValueAt(row, 0);
        String nom      = (String) view.getModel().getValueAt(row, 1);
        String prenom   = (String) view.getModel().getValueAt(row, 2);
        String tel      = (String) view.getModel().getValueAt(row, 3);

        List<Ordonnance> prescriptions = ordonnanceDAO.findByClient(clientId);
        if (prescriptions.isEmpty()) { view.showError("Aucune ordonnance pour « " + nom + " " + prenom + " »."); return; }

        String[] options = prescriptions.stream()
            .map(o -> "N° " + o.getIdOrdonnance() + "  —  " + o.getDate())
            .toArray(String[]::new);

        String chosen = (String) JOptionPane.showInputDialog(view,
            "Ordonnances de " + nom + " " + prenom + " :", "Choisir une ordonnance",
            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (chosen == null) return;

        Ordonnance selected = prescriptions.stream()
            .filter(o -> ("N° " + o.getIdOrdonnance() + "  —  " + o.getDate()).equals(chosen))
            .findFirst().orElse(null);
        if (selected == null) return;

        OrdonnanceView ordView = new OrdonnanceView(selected.getIdOrdonnance(),
            selected.getDate().toString(), nom + " " + prenom + " (Tél : " + tel + ")");
        new OrdonnanceController(ordView);
        ordView.setVisible(true);
    }

    private void openNewPrescription() {
        int row = getSelectedRow(); if (row < 0) return;

        String clientId = (String) view.getModel().getValueAt(row, 0);
        String nom      = (String) view.getModel().getValueAt(row, 1);
        String prenom   = (String) view.getModel().getValueAt(row, 2);
        String tel      = (String) view.getModel().getValueAt(row, 3);
        String dateStr  = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String autoId   = "ORD-" + clientId + "-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "-" + (System.currentTimeMillis() % 1000);

        OrdonnanceView ordView = new OrdonnanceView(autoId, dateStr, nom + " " + prenom + " (Tél : " + tel + ")");
        ordView.prefillNewOrd(autoId, clientId, dateStr);
        new OrdonnanceController(ordView);
        ordView.setVisible(true);
    }

    // ── Client manager popup ──────────────────────────────────────────────────

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