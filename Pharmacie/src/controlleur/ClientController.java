package controlleur;

import DAO.ClientDao;
import modele.Client;
import vue.ClientView;

import javax.swing.*;

public class ClientController {

    private final ClientView view;
    private final ClientDao  clientDAO = new ClientDao();

    public ClientController(ClientView view) {
        this.view = view;
        view.getBtnAdd()   .addActionListener(e -> addClient());
        view.getBtnUpdate().addActionListener(e -> updateClient());
        view.getBtnDelete().addActionListener(e -> deleteClient());
        view.getBtnSearch().addActionListener(e -> searchClient());
        view.getBtnClear() .addActionListener(e -> { view.clearForm(); loadAll(); });
        view.getBtnClose() .addActionListener(e -> view.dispose());

        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromTable();
        });

        loadAll();
    }

    private void loadAll() {
        view.getModel().setRowCount(0);
        clientDAO.findAll().forEach(c ->
            view.getModel().addRow(new Object[]{ c.getIdClient(), c.getNom(), c.getPrenom(), c.getTelephone(), c.getCredit() }));
    }
    private void addClient() {
        String id = view.getClientId(), nom = view.getClientNom(), prenom = view.getClientPrenom();
        String tel = view.getClientTel(), credit = view.getClientCredit();

        if (id.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
            view.showError("L'ID, le nom et le prénom sont obligatoires."); return;
        }
        if (!tel.matches("\\d{8}")) { view.showError("Le téléphone doit contenir exactement 8 chiffres."); return; }
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
        if (view.confirm("Supprimer le client « " + id + " » ?\n⚠ Toutes ses ordonnances et leurs lignes seront également supprimées.")
                != JOptionPane.YES_OPTION) return;

        if (clientDAO.delete(id)) { view.showSuccess("Client et toutes ses ordonnances supprimés !"); loadAll(); }
        else { view.showError("Erreur lors de la suppression."); }
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
        view.fillForm(
            (String) view.getModel().getValueAt(row, 0),
            (String) view.getModel().getValueAt(row, 1),
            (String) view.getModel().getValueAt(row, 2),
            (String) view.getModel().getValueAt(row, 3),
            (String) view.getModel().getValueAt(row, 4)
        );
    }
}