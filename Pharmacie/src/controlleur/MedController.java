package controlleur;

import DAO.MedicamentDAO;
import modele.Medicament;
import vue.MedView;

import javax.swing.JOptionPane;

public class MedController {

    private final MedView       view;
    private final MedicamentDAO dao = new MedicamentDAO();

    public MedController(MedView view) {
        this.view = view;

        view.getBtnSearch().addActionListener(e -> search());
        view.getBtnClose() .addActionListener(e -> view.dispose());

        if (!view.isReadOnly()) {
            view.getBtnAdd()   .addActionListener(e -> add());
            view.getBtnUpdate().addActionListener(e -> update());
            view.getBtnDelete().addActionListener(e -> delete());
            view.getBtnClear() .addActionListener(e -> { view.clearForm(); loadAll(); });

            view.getTable().getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) fillFromTable();
            });
        }

        loadAll();
    }
    private void loadAll() {
        view.getModel().setRowCount(0);
        dao.findAll().forEach(m -> view.getModel().addRow(new Object[]{
            m.getIdMedicament(),
            m.getNom(),
            String.format("%.3f", m.getPrix()),
            m.getQuantiteStock(),
            m.getType()
        }));
    }

 
    private void add() {
        String id    = view.getMedId();
        String nom   = view.getMedNom();
        String prixS = view.getMedPrix();
        String stkS  = view.getMedStock();

        if (id.isEmpty() || nom.isEmpty() || prixS.isEmpty() || stkS.isEmpty()) {
            view.showError("Tous les champs sont obligatoires."); return;
        }
        double prix; int stock;
        try { prix  = Double.parseDouble(prixS); } catch (NumberFormatException e) { view.showError("Prix invalide.");  return; }
        try { stock = Integer.parseInt(stkS);    } catch (NumberFormatException e) { view.showError("Stock invalide."); return; }
        if (prix < 0 || stock < 0) { view.showError("Prix et stock doivent être positifs."); return; }

        if (dao.create(new Medicament(id, nom, prix, stock, view.getMedType()))) {
            view.showSuccess("Médicament ajouté !"); loadAll();
        } else {
            view.showError("Erreur lors de l'ajout. L'ID est peut-être déjà utilisé.");
        }
    }

    private void update() {
        String id = view.getMedId();
        if (id.isEmpty()) { view.showError("Sélectionnez un médicament."); return; }
        double prix; int stock;
        try { prix  = Double.parseDouble(view.getMedPrix());  } catch (NumberFormatException e) { view.showError("Prix invalide.");  return; }
        try { stock = Integer.parseInt(view.getMedStock());   } catch (NumberFormatException e) { view.showError("Stock invalide."); return; }

        if (dao.update(new Medicament(id, view.getMedNom(), prix, stock, view.getMedType()))) {
            view.showSuccess("Médicament mis à jour !"); loadAll();
        } else {
            view.showError("Erreur lors de la mise à jour.");
        }
    }

    private void delete() {
        String id = view.getMedId();
        if (id.isEmpty()) { view.showError("Sélectionnez un médicament."); return; }
        if (view.confirm("Supprimer le médicament « " + id + " » ?") != JOptionPane.YES_OPTION) return;

        if (dao.delete(id)) {
            view.showSuccess("Médicament supprimé !"); loadAll();
        } else {
            view.showError("Erreur : ce médicament est peut-être lié à des ordonnances.");
        }
    }

    private void search() {
        String kw = view.getSearch().toLowerCase();
        if (kw.isEmpty()) { loadAll(); return; }

        view.getModel().setRowCount(0);
        dao.findAll().stream()
            .filter(m -> m.getNom().toLowerCase().contains(kw)
                      || m.getIdMedicament().toLowerCase().contains(kw)
                      || m.getType().toLowerCase().contains(kw))
            .forEach(m -> view.getModel().addRow(new Object[]{
                m.getIdMedicament(), m.getNom(),
                String.format("%.3f", m.getPrix()), m.getQuantiteStock(), m.getType()
            }));

        if (view.getModel().getRowCount() == 0)
            view.showError("Aucun médicament trouvé pour : « " + kw + " »");
    }

    private void fillFromTable() {
        int row = view.getTable().getSelectedRow();
        if (row < 0) return;
        view.fillForm(
            (String) view.getModel().getValueAt(row, 0),
            (String) view.getModel().getValueAt(row, 1),
            (String) view.getModel().getValueAt(row, 2),
            String.valueOf(view.getModel().getValueAt(row, 3)),
            (String) view.getModel().getValueAt(row, 4)
        );
    }
}