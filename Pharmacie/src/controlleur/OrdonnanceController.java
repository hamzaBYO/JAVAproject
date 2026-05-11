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
import java.util.List;

public class OrdonnanceController {

    private final OrdonnanceView view;
    private final OrdonnanceDAO  ordonnanceDAO = new OrdonnanceDAO();
    private final LigneOrdDAO    ligneOrdDAO   = new LigneOrdDAO();
    private final MedicamentDAO  medicamentDAO = new MedicamentDAO();
    private final ClientDao      clientDAO     = new ClientDao();

    private Ordonnance currentOrdonnance;

    public OrdonnanceController(OrdonnanceView view) {
        this(view, null);
    }

    public OrdonnanceController(OrdonnanceView view, Ordonnance existing) {
        this.view = view;
        this.currentOrdonnance = existing;

        view.getBtnClose()      .addActionListener(e -> view.dispose());
        view.getBtnCreateOrd()  .addActionListener(e -> createOrdonnance());
        view.getBtnAddLigne()   .addActionListener(e -> addMedicine());
        view.getBtnRemoveLigne().addActionListener(e -> removeMedicine());

        if (existing != null) {
            loadLignes(existing);
        }
    }


    private void loadLignes(Ordonnance ord) {
        view.clearTable();
        List<LigneOrd> lignes = ligneOrdDAO.findByOrdonnance(ord.getIdOrdonnance());
        for (LigneOrd ligne : lignes) {
            Medicament med = ligne.getIdMedicament();
            view.addLigne(
                med.getIdMedicament(),
                med.getNom(),
                ligne.getQuantite(),
                med.getPrix()
            );
        }
        recalculateTotal();
    }


    private void createOrdonnance() {
        String id       = view.getNewOrdId();
        String clientId = view.getNewClientId();
        String dateStr  = view.getNewDate();

        if (id.isEmpty() || clientId.isEmpty() || dateStr.isEmpty()) {
            view.showError("Veuillez remplir tous les champs : ID ordonnance, client et date."); return;
        }

        Client client = clientDAO.findById(clientId);
        if (client == null) {
            view.showError("Impossible de trouver le client avec l'ID : " + clientId); return;
        }

        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception ex) {
            view.showError("La date doit être au format yyyy-MM-dd  (ex : 2024-01-15)."); return;
        }

        currentOrdonnance = new Ordonnance(id, date, client);
        if (!ordonnanceDAO.create(currentOrdonnance)) {
            view.showError("Erreur lors de la création. L'ID ordonnance est peut-être déjà utilisé.");
            currentOrdonnance = null; return;
        }

        String clientLabel = client.getNom() + " " + client.getPrenom()
                           + " (Tél : " + client.getTelephone() + ")";
        view.updateHeader(id, dateStr, clientLabel);
        view.clearTable();
        view.setTotal(0.0);
        view.showSuccess("Ordonnance « " + id + " » créée ! Vous pouvez maintenant ajouter des médicaments.");
    }

    private void addMedicine() {
        if (currentOrdonnance == null) {
            view.showError("Commencez par créer une ordonnance."); return;
        }

        String medId = view.getMedId();
        if (medId.isEmpty()) { view.showError("Veuillez saisir l'ID du médicament."); return; }

        int quantity;
        try {
            quantity = Integer.parseInt(view.getQuantiteStr());
        } catch (NumberFormatException ex) {
            view.showError("La quantité doit être un nombre entier valide."); return;
        }
        if (quantity <= 0) { view.showError("La quantité doit être au moins 1."); return; }

        Medicament medicine = medicamentDAO.findById(medId);
        if (medicine == null) {
            view.showError("Aucun médicament trouvé avec l'ID : " + medId); return;
        }

        if (medicine.getQuantiteStock() < quantity) {
            view.showError("Stock insuffisant. Stock disponible : " + medicine.getQuantiteStock()); return;
        }

        if (!ligneOrdDAO.create(new LigneOrd(currentOrdonnance, medicine, quantity))) {
            view.showError("Erreur lors de l'ajout du médicament. Réessayez."); return;
        }

        view.addLigne(medicine.getIdMedicament(), medicine.getNom(), quantity, medicine.getPrix());
        recalculateTotal();
        view.showSuccess("« " + medicine.getNom() + " » ajouté avec succès.");
    }

    private void removeMedicine() {
        if (currentOrdonnance == null) { view.showError("Aucune ordonnance en cours."); return; }

        int selectedRow = view.getTableDetails().getSelectedRow();
        if (selectedRow < 0) { view.showError("Cliquez sur une ligne pour la sélectionner."); return; }

        String medId = (String) view.getModel().getValueAt(selectedRow, 0);
        if (!ligneOrdDAO.delete(currentOrdonnance.getIdOrdonnance(), medId)) {
            view.showError("Impossible de supprimer cette ligne. Réessayez."); return;
        }

        view.getModel().removeRow(selectedRow);
        recalculateTotal();
    }


    private void recalculateTotal() {
        double total = 0.0;
        for (int i = 0; i < view.getModel().getRowCount(); i++) {
            try {
                int    qty      = (Integer) view.getModel().getValueAt(i, 2);
                String priceStr = ((String) view.getModel().getValueAt(i, 3))
                                    .replace(',', '.');
                double price    = Double.parseDouble(priceStr);
                total += qty * price;
            } catch (Exception ignored) { }
        }
        view.setTotal(total);
    }
}