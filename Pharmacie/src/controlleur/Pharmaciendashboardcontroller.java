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
    private final ClientDao               clientDAO;
    private final OrdonnanceDAO           ordonnanceDAO;
    private final LigneOrdDAO             ligneOrdDAO;
    private final MedicamentDAO           medicamentDAO;
    private final Utilisateur             currentUser;

    public Pharmaciendashboardcontroller(PharmacienDashboardView view, Utilisateur currentUser) {
        this.view          = view;
        this.currentUser   = currentUser;
        this.clientDAO     = new ClientDao();
        this.ordonnanceDAO = new OrdonnanceDAO();
        this.ligneOrdDAO   = new LigneOrdDAO();
        this.medicamentDAO = new MedicamentDAO();
        bindEvents();
        loadAllClients();
    }

    // ── Événements ────────────────────────────────────────────────────────────

    private void bindEvents() {
        view.getBtnAddClient()       .addActionListener(e -> addClient());
        view.getBtnUpdateClient()    .addActionListener(e -> updateClient());
        view.getBtnDeleteClient()    .addActionListener(e -> deleteClient());
        view.getBtnSearchClient()    .addActionListener(e -> searchClient());
        view.getBtnClearClient()     .addActionListener(e -> loadAllClients());
        view.getBtnVoirOrdonnances() .addActionListener(e -> openOrdonnancesExistantes());
        view.getBtnNouvelleOrdonnance().addActionListener(e -> openNouvelleOrdonnance());
        view.getBtnLogout()          .addActionListener(e -> logout());

        view.getTableClients().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromTable();
        });
        view.getTableClients().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) openOrdonnancesExistantes(); }
        });
    }

    // ── CRUD Clients ──────────────────────────────────────────────────────────

    private void loadAllClients() {
        view.getModelClients().setRowCount(0);
        for (Client c : clientDAO.findAll())
            view.getModelClients().addRow(new Object[]{ c.getIdClient(), c.getNom(), c.getPrenom(), c.getTelephone(), c.getCredit() });
    }

    private void addClient() {
        String id = view.getClientId().trim(), nom = view.getClientNom().trim();
        String prenom = view.getClientPrenom().trim(), tel = view.getClientTel().trim(), credit = view.getClientCredit().trim();

        if (id.isEmpty())     { view.showError("L'ID client est obligatoire."); return; }
        if (nom.isEmpty())    { view.showError("Le nom est obligatoire."); return; }
        if (prenom.isEmpty()) { view.showError("Le prénom est obligatoire."); return; }
        if (!tel.matches("\\d{8}")) { view.showError("Le téléphone doit contenir exactement 8 chiffres."); return; }
        if (!credit.isEmpty()) { try { Double.parseDouble(credit); } catch (NumberFormatException ex) { view.showError("Crédit invalide (ex: 0.000)."); return; } }
        if (clientDAO.findById(id) != null) { view.showError("Un client avec l'ID « " + id + " » existe déjà."); return; }

        if (clientDAO.create(new Client(id, nom, prenom, tel, credit.isEmpty() ? "0.000" : credit))) {
            view.showSuccess("Client « " + nom + " " + prenom + " » ajouté avec succès !"); loadAllClients();
        } else { view.showError("Erreur lors de l'ajout du client."); }
    }

    private void updateClient() {
        String id = view.getClientId().trim();
        if (id.isEmpty()) { view.showError("Sélectionnez un client ou saisissez son ID."); return; }
        if (clientDAO.findById(id) == null) { view.showError("Aucun client trouvé avec l'ID : " + id); return; }

        String nom = view.getClientNom().trim(), prenom = view.getClientPrenom().trim();
        String tel = view.getClientTel().trim(), credit = view.getClientCredit().trim();

        if (nom.isEmpty() || prenom.isEmpty()) { view.showError("Le nom et le prénom sont obligatoires."); return; }
        if (!tel.isEmpty() && !tel.matches("\\d{8}")) { view.showError("Le téléphone doit contenir exactement 8 chiffres."); return; }
        if (!credit.isEmpty()) { try { Double.parseDouble(credit); } catch (NumberFormatException ex) { view.showError("Crédit invalide."); return; } }

        if (clientDAO.update(new Client(id, nom, prenom, tel, credit.isEmpty() ? "0.000" : credit))) {
            view.showSuccess("Client mis à jour avec succès !"); loadAllClients();
        } else { view.showError("Erreur lors de la mise à jour du client."); }
    }

    private void deleteClient() {
        String id = view.getClientId().trim();
        if (id.isEmpty()) { view.showError("Sélectionnez ou saisissez l'ID du client à supprimer."); return; }
        if (clientDAO.findById(id) == null) { view.showError("Aucun client trouvé avec l'ID : " + id); return; }

        List<Ordonnance> ordonnances = ordonnanceDAO.findByClient(id);
        String msg = ordonnances.isEmpty()
            ? "Êtes-vous sûr de vouloir supprimer le client « " + id + " » ?"
            : "Ce client possède " + ordonnances.size() + " ordonnance(s).\nToutes ses ordonnances et leurs lignes seront supprimées.\nVoulez-vous vraiment continuer ?";
        if (view.confirm(msg) != JOptionPane.YES_OPTION) return;

        for (Ordonnance ord : ordonnances) {
            for (LigneOrd l : ligneOrdDAO.findByOrdonnance(ord.getIdOrdonnance())) {
                Medicament med = l.getIdMedicament();
                medicamentDAO.updateStock(med.getIdMedicament(), med.getQuantiteStock() + l.getQuantite());
                ligneOrdDAO.delete(ord.getIdOrdonnance(), med.getIdMedicament());
            }
            ordonnanceDAO.delete(ord.getIdOrdonnance());
        }

        if (clientDAO.delete(id)) { view.showSuccess("Client et toutes ses données supprimés avec succès !"); loadAllClients(); }
        else { view.showError("Erreur lors de la suppression du client."); }
    }

    private void searchClient() {
        String kw = view.getSearchClient().toLowerCase().trim();
        if (kw.isEmpty()) { loadAllClients(); return; }

        view.getModelClients().setRowCount(0);
        for (Client c : clientDAO.findAll()) {
            if (c.getIdClient().toLowerCase().contains(kw) || c.getNom().toLowerCase().contains(kw)
             || c.getPrenom().toLowerCase().contains(kw)   || c.getTelephone().toLowerCase().contains(kw))
                view.getModelClients().addRow(new Object[]{ c.getIdClient(), c.getNom(), c.getPrenom(), c.getTelephone(), c.getCredit() });
        }
        if (view.getModelClients().getRowCount() == 0) view.showError("Aucun client trouvé pour : « " + kw + " »");
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

    // ── Ordonnances ───────────────────────────────────────────────────────────

    private void openOrdonnancesExistantes() {
        int row = view.getTableClients().getSelectedRow();
        if (row < 0) { view.showError("Sélectionnez d'abord un client."); return; }

        String clientId = (String) view.getModelClients().getValueAt(row, 0);
        String nom      = (String) view.getModelClients().getValueAt(row, 1);
        String prenom   = (String) view.getModelClients().getValueAt(row, 2);
        String tel      = (String) view.getModelClients().getValueAt(row, 3);

        List<Ordonnance> ordonnances = ordonnanceDAO.findByClient(clientId);
        if (ordonnances.isEmpty()) { view.showError("Aucune ordonnance pour « " + nom + " " + prenom + " »."); return; }

        String[] options = new String[ordonnances.size()];
        for (int i = 0; i < ordonnances.size(); i++)
            options[i] = "N° " + ordonnances.get(i).getIdOrdonnance() + "  —  " + ordonnances.get(i).getDate();

        String chosen = (String) JOptionPane.showInputDialog(view,
            "Ordonnances de " + nom + " " + prenom + " :", "Choisir une ordonnance",
            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (chosen == null) return;

        Ordonnance selected = null;
        for (int i = 0; i < options.length; i++)
            if (options[i].equals(chosen)) { selected = ordonnances.get(i); break; }
        if (selected == null) return;

        OrdonnanceView ordView = new OrdonnanceView(selected.getIdOrdonnance(), selected.getDate().toString(), nom + " " + prenom + " (Tél : " + tel + ")");
        new OrdonnanceController(ordView, selected.getIdOrdonnance());
        ordView.setVisible(true);
    }

    private void openNouvelleOrdonnance() {
        int row = view.getTableClients().getSelectedRow();
        if (row < 0) { view.showError("Sélectionnez d'abord un client dans la table."); return; }

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

    // ── Déconnexion ───────────────────────────────────────────────────────────

    private void logout() {
        if (view.confirm("Voulez-vous vraiment vous déconnecter ?") != JOptionPane.YES_OPTION) return;
        view.dispose();
        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }
}