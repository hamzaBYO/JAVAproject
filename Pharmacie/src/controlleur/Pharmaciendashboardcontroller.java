package controlleur;

import DAO.ClientDao;
import DAO.LigneOrdDAO;
import DAO.MedicamentDAO;
import DAO.OrdonnanceDAO;
import modele.Client;
import modele.LigneOrd;
import modele.Medicament;
import modele.Ordonnance;
import modele.Utilisateur;
import vue.LoginView;
import vue.OrdonnanceView;
import vue.PharmacienDashboardView;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Pharmaciendashboardcontroller {

    private final PharmacienDashboardView view;
    private final ClientDao               clientDAO     = new ClientDao();
    private final OrdonnanceDAO           ordonnanceDAO = new OrdonnanceDAO();
    private final LigneOrdDAO             ligneOrdDAO   = new LigneOrdDAO();
    private final MedicamentDAO           medicamentDAO = new MedicamentDAO();
    private final Utilisateur             currentUser;

    public Pharmaciendashboardcontroller(PharmacienDashboardView view, Utilisateur currentUser) {
        this.view        = view;
        this.currentUser = currentUser;
        bindEvents();
        loadAllClients();
    }

    // ── Events ────────────────────────────────────────────────────────────────

    private void bindEvents() {
        view.getBtnAddClient()         .addActionListener(e -> addClient());
        view.getBtnUpdateClient()      .addActionListener(e -> updateClient());
        view.getBtnDeleteClient()      .addActionListener(e -> deleteClient());
        view.getBtnSearchClient()      .addActionListener(e -> searchClient());
        view.getBtnClearClient()       .addActionListener(e -> loadAllClients());
        view.getBtnVoirOrdonnances()   .addActionListener(e -> openExistingPrescription());
        view.getBtnNouvelleOrdonnance().addActionListener(e -> openNewPrescription());
        view.getBtnLogout()            .addActionListener(e -> logout());

        view.getTableClients().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromTable();
        });
        view.getTableClients().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) openExistingPrescription(); }
        });
    }

    // ── Clients ───────────────────────────────────────────────────────────────

    private void loadAllClients() {
        view.getModelClients().setRowCount(0);
        clientDAO.findAll().forEach(c ->
            view.getModelClients().addRow(new Object[]{ c.getIdClient(), c.getNom(), c.getPrenom(), c.getTelephone(), c.getCredit() }));
    }

    private void addClient() {
        String id     = view.getClientId().trim();
        String nom    = view.getClientNom().trim();
        String prenom = view.getClientPrenom().trim();
        String tel    = view.getClientTel().trim();
        String credit = view.getClientCredit().trim();

        if (id.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
            view.showError("L'ID, le nom et le prénom sont obligatoires."); return;
        }
        if (!tel.matches("\\d{8}")) { view.showError("Le téléphone doit contenir exactement 8 chiffres."); return; }
        if (!credit.isEmpty()) {
            try { Double.parseDouble(credit); } catch (NumberFormatException ex) { view.showError("Crédit invalide."); return; }
        }

        if (clientDAO.create(new Client(id, nom, prenom, tel, credit.isEmpty() ? "0.000" : credit))) {
            view.showSuccess("Client « " + nom + " " + prenom + " » ajouté !"); loadAllClients();
        } else { view.showError("Erreur lors de l'ajout du client."); }
    }

    private void updateClient() {
        String id     = view.getClientId().trim();
        String nom    = view.getClientNom().trim();
        String prenom = view.getClientPrenom().trim();
        String tel    = view.getClientTel().trim();
        String credit = view.getClientCredit().trim();

        if (id.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
            view.showError("L'ID, le nom et le prénom sont obligatoires."); return;
        }
        if (!tel.isEmpty() && !tel.matches("\\d{8}")) { view.showError("Le téléphone doit contenir exactement 8 chiffres."); return; }
        if (!credit.isEmpty()) {
            try { Double.parseDouble(credit); } catch (NumberFormatException ex) { view.showError("Crédit invalide."); return; }
        }

        if (clientDAO.update(new Client(id, nom, prenom, tel, credit.isEmpty() ? "0.000" : credit))) {
            view.showSuccess("Client mis à jour !"); loadAllClients();
        } else { view.showError("Erreur lors de la mise à jour."); }
    }

    private void deleteClient() {
        String id = view.getClientId().trim();
        if (id.isEmpty()) { view.showError("Sélectionnez un client."); return; }

        List<Ordonnance> prescriptions = ordonnanceDAO.findByClient(id);
        String msg = prescriptions.isEmpty()
            ? "Supprimer le client « " + id + " » ?"
            : "Ce client a " + prescriptions.size() + " ordonnance(s). Tout sera supprimé. Continuer ?";
        if (view.confirm(msg) != JOptionPane.YES_OPTION) return;

        // Clean up prescriptions and restore stock
        for (Ordonnance ord : prescriptions) {
            for (LigneOrd line : ligneOrdDAO.findByOrdonnance(ord.getIdOrdonnance())) {
                Medicament med = line.getIdMedicament();
                medicamentDAO.updateStock(med.getIdMedicament(), med.getQuantiteStock() + line.getQuantite());
                ligneOrdDAO.delete(ord.getIdOrdonnance(), med.getIdMedicament());
            }
            ordonnanceDAO.delete(ord.getIdOrdonnance());
        }

        if (clientDAO.delete(id)) { view.showSuccess("Client supprimé !"); loadAllClients(); }
        else { view.showError("Erreur lors de la suppression."); }
    }

    private void searchClient() {
        String kw = view.getSearchClient().toLowerCase().trim();
        if (kw.isEmpty()) { loadAllClients(); return; }

        view.getModelClients().setRowCount(0);
        clientDAO.findAll().stream()
            .filter(c -> c.getIdClient().toLowerCase().contains(kw)
                      || c.getNom().toLowerCase().contains(kw)
                      || c.getPrenom().toLowerCase().contains(kw)
                      || c.getTelephone().toLowerCase().contains(kw))
            .forEach(c -> view.getModelClients().addRow(
                new Object[]{ c.getIdClient(), c.getNom(), c.getPrenom(), c.getTelephone(), c.getCredit() }));

        if (view.getModelClients().getRowCount() == 0)
            view.showError("Aucun client trouvé pour : « " + kw + " »");
    }

    private void fillFormFromTable() {
        int row = view.getTableClients().getSelectedRow();
        if (row < 0) return;
        view.fillClientForm(
            (String) view.getModelClients().getValueAt(row, 0),
            (String) view.getModelClients().getValueAt(row, 1),
            (String) view.getModelClients().getValueAt(row, 2),
            (String) view.getModelClients().getValueAt(row, 3),
            (String) view.getModelClients().getValueAt(row, 4)
        );
    }

    // ── Prescriptions ─────────────────────────────────────────────────────────

    private int getSelectedRow() {
        int row = view.getTableClients().getSelectedRow();
        if (row < 0) view.showError("Sélectionnez d'abord un client.");
        return row;
    }

    private void openExistingPrescription() {
        int row = getSelectedRow();
        if (row < 0) return;

        String clientId = (String) view.getModelClients().getValueAt(row, 0);
        String nom      = (String) view.getModelClients().getValueAt(row, 1);
        String prenom   = (String) view.getModelClients().getValueAt(row, 2);
        String tel      = (String) view.getModelClients().getValueAt(row, 3);

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
        int row = getSelectedRow();
        if (row < 0) return;

        String clientId = (String) view.getModelClients().getValueAt(row, 0);
        String nom      = (String) view.getModelClients().getValueAt(row, 1);
        String prenom   = (String) view.getModelClients().getValueAt(row, 2);
        String tel      = (String) view.getModelClients().getValueAt(row, 3);
        String dateStr  = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String autoId   = "ORD-" + clientId + "-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "-" + (System.currentTimeMillis() % 1000);

        OrdonnanceView ordView = new OrdonnanceView(autoId, dateStr, nom + " " + prenom + " (Tél : " + tel + ")");
        ordView.prefillNewOrd(autoId, clientId, dateStr);
        new OrdonnanceController(ordView);
        ordView.setVisible(true);
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