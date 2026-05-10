package controlleur;

import DAO.UtilisateurDAO;
import modele.Utilisateur;
import vue.UserView;

import javax.swing.JOptionPane;

public class UserController {

    private final UserView       view;
    private final UtilisateurDAO dao = new UtilisateurDAO();

    public UserController(UserView view) {
        this.view = view;
        view.getBtnAdd()   .addActionListener(e -> add());
        view.getBtnUpdate().addActionListener(e -> update());
        view.getBtnDelete().addActionListener(e -> delete());
        view.getBtnSearch().addActionListener(e -> search());
        view.getBtnClear() .addActionListener(e -> { view.clearForm(); loadAll(); });
        view.getBtnClose() .addActionListener(e -> view.dispose());

        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFromTable();
        });
        loadAll();
    }

    private void loadAll() {
        view.getModel().setRowCount(0);
        dao.findAll().forEach(u -> view.getModel().addRow(
            new Object[]{ u.getId(), u.getCin(), u.getNom(), u.getPrenom(), u.getEmail(), u.getType() }));
    }

    private void add() {
        String id = view.getUserId(), nom = view.getUserNom(), email = view.getUserEmail(), pwd = view.getUserPwd();
        if (id.isEmpty() || nom.isEmpty() || email.isEmpty() || pwd.isEmpty()) { view.showError("Remplissez tous les champs."); return; }
        if (!email.contains("@")) { view.showError("Email invalide."); return; }
        if (!view.getUserCin().matches("\\d{8}")) { view.showError("CIN invalide (8 chiffres)."); return; }
        if (dao.create(new Utilisateur(id, view.getUserCin(), nom, view.getUserPrenom(), email, pwd, view.getUserType())))
            { view.showSuccess("Utilisateur ajouté !"); loadAll(); }
        else view.showError("Erreur : ID, email ou CIN déjà utilisé.");
    }

    private void update() {
        String id = view.getUserId();
        if (id.isEmpty()) { view.showError("Sélectionnez un utilisateur."); return; }
        if (dao.update(new Utilisateur(id, view.getUserCin(), view.getUserNom(), view.getUserPrenom(), view.getUserEmail(), view.getUserPwd(), view.getUserType())))
            { view.showSuccess("Utilisateur mis à jour !"); loadAll(); }
        else view.showError("Erreur lors de la mise à jour.");
    }

    private void delete() {
        String id = view.getUserId();
        if (id.isEmpty()) { view.showError("Sélectionnez un utilisateur."); return; }
        if (view.confirm("Supprimer l'utilisateur « " + id + " » ?") != JOptionPane.YES_OPTION) return;
        if (dao.delete(id)) { view.showSuccess("Utilisateur supprimé !"); loadAll(); }
        else view.showError("Erreur lors de la suppression.");
    }

    private void search() {
        String kw = view.getSearch().toLowerCase();
        if (kw.isEmpty()) { loadAll(); return; }
        view.getModel().setRowCount(0);
        dao.findAll().stream()
            .filter(u -> u.getNom().toLowerCase().contains(kw) || u.getEmail().toLowerCase().contains(kw) || u.getId().toLowerCase().contains(kw))
            .forEach(u -> view.getModel().addRow(
                new Object[]{ u.getId(), u.getCin(), u.getNom(), u.getPrenom(), u.getEmail(), u.getType() }));
        if (view.getModel().getRowCount() == 0) view.showError("Aucun utilisateur trouvé.");
    }

    private void fillFromTable() {
        int row = view.getTable().getSelectedRow();
        if (row < 0) return;
        view.fillForm(
            (String) view.getModel().getValueAt(row, 0),
            (String) view.getModel().getValueAt(row, 1),
            (String) view.getModel().getValueAt(row, 2),
            (String) view.getModel().getValueAt(row, 3),
            (String) view.getModel().getValueAt(row, 4),
            (String) view.getModel().getValueAt(row, 5)
        );
    }
}