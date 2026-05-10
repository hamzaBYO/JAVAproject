package vue;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;

public class UserView extends JFrame {

    private JTextField txtId, txtCin, txtNom, txtPrenom, txtEmail, txtPwd, txtSearch;
    private JComboBox<String> cmbType;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnClose;
    private JTable table; private DefaultTableModel model;

    public UserView() {
        setTitle("Gestion des Utilisateurs"); setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(980, 580); setLocationRelativeTo(null);

        // ── Form ──────────────────────────────────────────────────────────────
        JPanel form = new JPanel(); form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE); form.setPreferredSize(new Dimension(230, 0));
        form.setBorder(new CompoundBorder(new LineBorder(new Color(200,220,240),1), new EmptyBorder(16,14,16,14)));

        txtId     = field(form, "ID",           "USR001");
        txtCin    = field(form, "CIN",          "12345678");
        txtNom    = field(form, "Nom",          "Nom");
        txtPrenom = field(form, "Prénom",       "Prénom");
        txtEmail  = field(form, "Email",        "email@mail.com");
        txtPwd    = field(form, "Mot de passe", "");
        form.add(label("Type")); form.add(Box.createVerticalStrut(4));
        cmbType = new JComboBox<>(new String[]{"PHARMACIEN","ADMIN"});
        cmbType.setMaximumSize(new Dimension(Short.MAX_VALUE, 32)); form.add(cmbType);
        form.add(Box.createVerticalStrut(14));
        btnAdd    = btn("Ajouter",   new Color(39,174,96));  form.add(btnAdd);    form.add(Box.createVerticalStrut(8));
        btnUpdate = btn("Modifier",  new Color(52,152,219)); form.add(btnUpdate); form.add(Box.createVerticalStrut(8));
        btnDelete = btn("Supprimer", new Color(231,76,60));  form.add(btnDelete); form.add(Box.createVerticalStrut(8));
        btnClear  = btn("Vider",     Color.GRAY);            form.add(btnClear);

        // ── Table ─────────────────────────────────────────────────────────────
        model = new DefaultTableModel(new String[]{"ID","CIN","Nom","Prénom","Email","Type"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13)); table.setRowHeight(28);
        table.getTableHeader().setBackground(new Color(28,78,115)); table.getTableHeader().setForeground(Color.WHITE);
        txtSearch = new JTextField(); txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSearch = btn("Rechercher", new Color(33,97,140)); btnSearch.setPreferredSize(new Dimension(120,34));
        btnClose  = btn("✕ Fermer",   new Color(180,60,60));  btnClose.setPreferredSize(new Dimension(100,34));

        JPanel bar = new JPanel(new BorderLayout(8,0)); bar.add(txtSearch, BorderLayout.CENTER); bar.add(btnSearch, BorderLayout.EAST);
        JPanel right = new JPanel(new BorderLayout(0,10)); right.setBorder(new EmptyBorder(16,10,16,16));
        right.add(bar, BorderLayout.NORTH); right.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel header = new JPanel(new BorderLayout()); header.setBackground(new Color(28,78,115)); header.setBorder(new EmptyBorder(12,20,12,20));
        JLabel title = new JLabel("Gestion des Utilisateurs");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16)); title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST); header.add(btnClose, BorderLayout.EAST);

        JPanel root = new JPanel(new BorderLayout());
        root.add(header, BorderLayout.NORTH); root.add(form, BorderLayout.WEST); root.add(right, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JTextField field(JPanel p, String lbl, String ph) {
        p.add(label(lbl)); p.add(Box.createVerticalStrut(4));
        JTextField f = new JTextField(); f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setMaximumSize(new Dimension(Short.MAX_VALUE, 32)); f.setBorder(new CompoundBorder(new LineBorder(new Color(200,220,240),1), new EmptyBorder(3,7,3,7)));
        p.add(f); p.add(Box.createVerticalStrut(8)); return f;
    }
    private JLabel label(String t) { JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 11)); l.setForeground(new Color(33,97,140)); return l; }
    private JButton btn(String t, Color c) {
        JButton b = new JButton(t); b.setFont(new Font("Segoe UI", Font.BOLD, 12)); b.setBackground(c); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Short.MAX_VALUE, 34)); b.setAlignmentX(LEFT_ALIGNMENT); return b;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getUserId()     { return txtId.getText().trim(); }
    public String getUserCin()    { return txtCin.getText().trim(); }
    public String getUserNom()    { return txtNom.getText().trim(); }
    public String getUserPrenom() { return txtPrenom.getText().trim(); }
    public String getUserEmail()  { return txtEmail.getText().trim(); }
    public String getUserPwd()    { return txtPwd.getText().trim(); }
    public String getUserType()   { return (String) cmbType.getSelectedItem(); }
    public String getSearch()     { return txtSearch.getText().trim(); }
    public JTable getTable()              { return table; }
    public DefaultTableModel getModel()   { return model; }
    public JButton getBtnAdd()    { return btnAdd; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
    public JButton getBtnClear()  { return btnClear; }
    public JButton getBtnSearch() { return btnSearch; }
    public JButton getBtnClose()  { return btnClose; }
    public void fillForm(String id, String cin, String nom, String prenom, String email, String type) {
        txtId.setText(id); txtCin.setText(cin); txtNom.setText(nom); txtPrenom.setText(prenom); txtEmail.setText(email); cmbType.setSelectedItem(type);
    }
    public void clearForm() { txtId.setText(""); txtCin.setText(""); txtNom.setText(""); txtPrenom.setText(""); txtEmail.setText(""); txtPwd.setText(""); }
    public void showError(String m)   { JOptionPane.showMessageDialog(this, m, "Erreur",  JOptionPane.ERROR_MESSAGE); }
    public void showSuccess(String m) { JOptionPane.showMessageDialog(this, m, "Succès",  JOptionPane.INFORMATION_MESSAGE); }
    public int  confirm(String m)     { return JOptionPane.showConfirmDialog(this, m, "Confirmation", JOptionPane.YES_NO_OPTION); }
}