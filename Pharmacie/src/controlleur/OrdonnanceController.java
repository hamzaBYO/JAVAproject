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
    private final OrdonnanceDAO  ordonnanceDAO;
    private final LigneOrdDAO    ligneOrdDAO;
    private final MedicamentDAO  medicamentDAO;
    private final ClientDao      clientDAO;

    private Ordonnance currentOrdonnance;

    public OrdonnanceController(OrdonnanceView view) {
        this.view          = view;
        this.ordonnanceDAO = new OrdonnanceDAO();
        this.ligneOrdDAO   = new LigneOrdDAO();
        this.medicamentDAO = new MedicamentDAO();
        this.clientDAO     = new ClientDao();
        bindEvents();
    }

    public OrdonnanceController(OrdonnanceView view, String ordonnanceId) {
        this(view);
        loadOrdonnance(ordonnanceId);
    }

    private void bindEvents() {
        view.getBtnClose()      .addActionListener(e -> view.dispose());
        view.getBtnCreateOrd()  .addActionListener(e -> createOrdonnance());
        view.getBtnAddLigne()   .addActionListener(e -> addLigne());
        view.getBtnRemoveLigne().addActionListener(e -> removeLigne());
    }

    // ── Création ──────────────────────────────────────────────────────────────

    private void createOrdonnance() {
        String ordId    = view.getNewOrdId().trim();
        String clientId = view.getNewClientId().trim();
        String dateStr  = view.getNewDate().trim();

        if (ordId.isEmpty())    { view.showError("L'ID de l'ordonnance est obligatoire.");              return; }
        if (clientId.isEmpty()) { view.showError("L'ID du client est obligatoire.");                    return; }
        if (dateStr.isEmpty())  { view.showError("La date est obligatoire (format : yyyy-MM-dd).");     return; }

        if (ordonnanceDAO.findById(ordId) != null) {
            view.showError("Une ordonnance avec l'ID « " + ordId + " » existe déjà."); return;
        }

        Client client = clientDAO.findById(clientId);
        if (client == null) { view.showError("Aucun client trouvé avec l'ID : " + clientId); return; }

        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception ex) {
            view.showError("Format de date invalide. Utilisez : yyyy-MM-dd (ex: 2024-01-15)."); return;
        }

        currentOrdonnance = new Ordonnance(ordId, date, client);
        if (!ordonnanceDAO.create(currentOrdonnance)) {
            view.showError("Erreur lors de la création de l'ordonnance."); currentOrdonnance = null; return;
        }

        String clientInfo = client.getNom() + " " + client.getPrenom() + " (Tél : " + client.getTelephone() + ")";
        view.updateHeader(ordId, dateStr, clientInfo);
        view.clearTable();
        view.setTotal(0.0);
        view.showSuccess("Ordonnance « " + ordId + " » créée avec succès !\nVous pouvez maintenant ajouter des médicaments.");
    }

    // ── Ajout ligne ───────────────────────────────────────────────────────────

    private void addLigne() {
        if (currentOrdonnance == null) { view.showError("Créez d'abord une ordonnance avant d'ajouter des médicaments."); return; }

        String medId  = view.getMedId().trim();
        String qteStr = view.getQuantiteStr().trim();

        if (medId.isEmpty()) { view.showError("L'ID du médicament est obligatoire."); return; }

        int quantite;
        try { quantite = Integer.parseInt(qteStr); }
        catch (NumberFormatException ex) { view.showError("La quantité doit être un nombre entier."); return; }
        if (quantite <= 0) { view.showError("La quantité doit être supérieure à 0."); return; }

        Medicament med = medicamentDAO.findById(medId);
        if (med == null) { view.showError("Aucun médicament trouvé avec l'ID : " + medId); return; }

        if (med.getQuantiteStock() < quantite) {
            view.showError("Stock insuffisant pour « " + med.getNom() + " ».\nStock disponible : "
                + med.getQuantiteStock() + " | Quantité demandée : " + quantite); return;
        }

        for (LigneOrd l : ligneOrdDAO.findByOrdonnance(currentOrdonnance.getIdOrdonnance())) {
            if (l.getIdMedicament().getIdMedicament().equals(medId)) {
                view.showError("Le médicament « " + med.getNom() + " » est déjà dans cette ordonnance.\nRetirez la ligne existante pour la modifier."); return;
            }
        }

        if (!ligneOrdDAO.create(new LigneOrd(currentOrdonnance, med, quantite))) {
            view.showError("Erreur lors de l'ajout de la ligne médicament."); return;
        }

        int newStock = med.getQuantiteStock() - quantite;
        if (!medicamentDAO.updateStock(med.getIdMedicament(), newStock))
            view.showError("Ligne ajoutée mais impossible de mettre à jour le stock. Vérifiez manuellement.");

        view.addLigne(med.getIdMedicament(), med.getNom(), quantite, med.getPrix());
        recalculateTotal();
        view.showSuccess("Médicament « " + med.getNom() + " » ajouté.\nStock restant : " + newStock);
    }

    // ── Retrait ligne ─────────────────────────────────────────────────────────

    private void removeLigne() {
        if (currentOrdonnance == null) { view.showError("Aucune ordonnance active."); return; }

        int row = view.getTableDetails().getSelectedRow();
        if (row < 0) { view.showError("Sélectionnez une ligne dans la table pour la retirer."); return; }

        String medId = (String) view.getModel().getValueAt(row, 0);
        int    qte   = (int)    view.getModel().getValueAt(row, 2);

        if (!ligneOrdDAO.delete(currentOrdonnance.getIdOrdonnance(), medId)) {
            view.showError("Erreur lors de la suppression de la ligne."); return;
        }

        Medicament med = medicamentDAO.findById(medId);
        if (med != null) medicamentDAO.updateStock(medId, med.getQuantiteStock() + qte);

        view.getModel().removeRow(row);
        recalculateTotal();
    }

    // ── Chargement ────────────────────────────────────────────────────────────

    public void loadOrdonnance(String ordonnanceId) {
        Ordonnance ord = ordonnanceDAO.findById(ordonnanceId);
        if (ord == null) { view.showError("Ordonnance introuvable : " + ordonnanceId); return; }

        currentOrdonnance = ord;
        String clientInfo = ord.getIdClient().getNom() + " " + ord.getIdClient().getPrenom()
                          + " (Tél : " + ord.getIdClient().getTelephone() + ")";
        view.updateHeader(ord.getIdOrdonnance(), ord.getDate().toString(), clientInfo);

        view.clearTable();
        for (LigneOrd l : ligneOrdDAO.findByOrdonnance(ordonnanceId))
            view.addLigne(l.getIdMedicament().getIdMedicament(), l.getIdMedicament().getNom(),
                          l.getQuantite(), l.getIdMedicament().getPrix());
        recalculateTotal();
    }

    // ── Total ─────────────────────────────────────────────────────────────────

    private void recalculateTotal() {
        double total = 0.0;
        for (int i = 0; i < view.getModel().getRowCount(); i++) {
            try {
                int    qte  = (Integer) view.getModel().getValueAt(i, 2);
                double prix = Double.parseDouble(((String) view.getModel().getValueAt(i, 3)).replace(",", "."));
                total += qte * prix;
            } catch (Exception ignored) {}
        }
        view.setTotal(total);
    }
}