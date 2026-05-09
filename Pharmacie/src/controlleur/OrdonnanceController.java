package controlleur;

import DAO.ClientDao;
import DAO.LigneOrdDAO;
import DAO.MedicamentDAO;
import DAO.OrdonnanceDAO;
import modele.Client;
import modele.LigneOrd;
import modele.Medicament;
import modele.Ordonnance;
import vue.OrdonnanceView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrdonnanceController {

    private final OrdonnanceView view;
    private final OrdonnanceDAO  ordonnanceDAO = new OrdonnanceDAO();
    private final LigneOrdDAO    ligneOrdDAO   = new LigneOrdDAO();
    private final MedicamentDAO  medicamentDAO = new MedicamentDAO();
    private final ClientDao      clientDAO     = new ClientDao();

    private Ordonnance currentOrdonnance;

    public OrdonnanceController(OrdonnanceView view) {
        this.view = view;
        view.getBtnClose()      .addActionListener(e -> view.dispose());
        view.getBtnCreateOrd()  .addActionListener(e -> createOrdonnance());
        view.getBtnAddLigne()   .addActionListener(e -> addMedicine());
        view.getBtnRemoveLigne().addActionListener(e -> removeMedicine());
    }

    private void fail(String message) {
        view.showError(message);
    }

    // ── Create the prescription ───────────────────────────────────────────────

    private void createOrdonnance() {
        String id       = view.getNewOrdId().trim();
        String clientId = view.getNewClientId().trim();
        String dateStr  = view.getNewDate().trim();

        if (id.isEmpty() || clientId.isEmpty() || dateStr.isEmpty()) {
            fail("Veuillez remplir tous les champs : ID, client et date."); return;
        }

        Client client = clientDAO.findById(clientId);
        if (client == null) {
            fail("Impossible de trouver le client avec l'ID : " + clientId); return;
        }

        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception ex) {
            fail("La date doit être au format yyyy-MM-dd, par exemple : 2024-01-15."); return;
        }

        currentOrdonnance = new Ordonnance(id, date, client);
        if (!ordonnanceDAO.create(currentOrdonnance)) {
            fail("Quelque chose s'est mal passé lors de la création. Réessayez.");
            currentOrdonnance = null; return;
        }

        String clientLabel = client.getNom() + " " + client.getPrenom() + " (Tél : " + client.getTelephone() + ")";
        view.updateHeader(id, dateStr, clientLabel);
        view.clearTable();
        view.setTotal(0.0);
        view.showSuccess("Ordonnance « " + id + " » créée ! Ajoutez vos médicaments.");
    }

    // ── Add a medicine ────────────────────────────────────────────────────────

    private void addMedicine() {
        if (currentOrdonnance == null) {
            fail("Commencez par créer une ordonnance."); return;
        }

        String medId = view.getMedId().trim();
        if (medId.isEmpty()) { fail("Veuillez saisir l'ID du médicament."); return; }

        int quantity;
        try {
            quantity = Integer.parseInt(view.getQuantiteStr().trim());
        } catch (NumberFormatException ex) {
            fail("La quantité doit être un nombre entier valide."); return;
        }
        if (quantity <= 0) { fail("La quantité doit être au moins 1."); return; }

        Medicament medicine = medicamentDAO.findById(medId);
        if (medicine == null) {
            fail("Aucun médicament trouvé avec l'ID : " + medId); return;
        }

        if (!ligneOrdDAO.create(new LigneOrd(currentOrdonnance, medicine, quantity))) {
            fail("Erreur lors de l'ajout du médicament. Réessayez."); return;
        }

        view.addLigne(medicine.getIdMedicament(), medicine.getNom(), quantity, medicine.getPrix());
        recalculateTotal();
        view.showSuccess("« " + medicine.getNom() + " » ajouté avec succès.");
    }

    // ── Remove a medicine ─────────────────────────────────────────────────────

    private void removeMedicine() {
        if (currentOrdonnance == null) { fail("Aucune ordonnance en cours."); return; }

        int selectedRow = view.getTableDetails().getSelectedRow();
        if (selectedRow < 0) { fail("Cliquez sur une ligne pour la sélectionner."); return; }

        String medId = (String) view.getModel().getValueAt(selectedRow, 0);

        if (!ligneOrdDAO.delete(currentOrdonnance.getIdOrdonnance(), medId)) {
            fail("Impossible de supprimer cette ligne. Réessayez."); return;
        }

        view.getModel().removeRow(selectedRow);
        recalculateTotal();
    }

    // ── Recalculate total ─────────────────────────────────────────────────────

    private void recalculateTotal() {
        double total = 0.0;
        for (int i = 0; i < view.getModel().getRowCount(); i++) {
            try {
                int    qty   = (Integer) view.getModel().getValueAt(i, 2);
                double price = Double.parseDouble(((String) view.getModel().getValueAt(i, 3)).replace(",", "."));
                total += qty * price;
            } catch (Exception ignored) {}
        }
        view.setTotal(total);
    }
}