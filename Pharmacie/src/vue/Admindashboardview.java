package vue;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class Admindashboardview extends JFrame {

    // ── Couleurs ──────────────────────────────────────────────────────────────
    public static final Color COLOR_SIDEBAR   = new Color(28, 78, 115);
    public static final Color COLOR_PRIMARY   = new Color(33, 97, 140);
    public static final Color COLOR_SECONDARY = new Color(39, 174, 96);
    public static final Color COLOR_ACCENT    = new Color(52, 152, 219);
    public static final Color COLOR_LIGHT     = new Color(236, 240, 241);
    public static final Color COLOR_WHITE     = Color.WHITE;
    public static final Color COLOR_TEXT      = new Color(30, 39, 46);

    // ── Champs Utilisateurs ───────────────────────────────────────────────────
    private JTextField txtUserId, txtUserCin, txtUserNom, txtUserPrenom, txtUserEmail, txtUserPwd;
    private JComboBox<String> cmbUserType;
    private JButton btnAddUser, btnUpdateUser, btnDeleteUser, btnSearchUser, btnClearUser;
    private JTable tableUsers; private DefaultTableModel modelUsers;
    private JTextField txtSearchUser;

    // ── Champs Médicaments ────────────────────────────────────────────────────
    private JTextField txtMedId, txtMedNom, txtMedPrix, txtMedStock;
    private JComboBox<String> cmbMedType;
    private JButton btnAddMed, btnUpdateMed, btnDeleteMed, btnSearchMed, btnClearMed;
    private JTable tableMeds; private DefaultTableModel modelMeds;
    private JTextField txtSearchMed;

    // ── Navigation ────────────────────────────────────────────────────────────
    private JButton btnNavUsers, btnNavMeds, btnLogout;
    private JPanel contentPanel; private CardLayout cardLayout;

    public Admindashboardview() {
        setTitle("Pharmacie - Administration");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1050, 680); setLocationRelativeTo(null); setResizable(true);
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(COLOR_SIDEBAR); p.setPreferredSize(new Dimension(210, 0));
        p.setBorder(new EmptyBorder(20, 12, 20, 12));

        JLabel lblApp  = lbl("PharmaCie", Font.BOLD, 18, COLOR_WHITE);     lblApp.setAlignmentX(CENTER_ALIGNMENT);
        JLabel lblRole = lbl("ADMIN",     Font.PLAIN, 11, new Color(173,216,230)); lblRole.setAlignmentX(CENTER_ALIGNMENT);
        JSeparator sep = new JSeparator(); sep.setForeground(new Color(60,110,150)); sep.setMaximumSize(new Dimension(Short.MAX_VALUE,1));

        btnNavUsers = sidebarBtn("Utilisateurs");
        btnNavMeds  = sidebarBtn("Médicaments");
        btnLogout   = sidebarBtn("Déconnexion"); btnLogout.setForeground(new Color(255,120,120));

        p.add(Box.createVerticalStrut(10)); p.add(lblApp); p.add(Box.createVerticalStrut(4));
        p.add(lblRole); p.add(Box.createVerticalStrut(20)); p.add(sep);
        p.add(Box.createVerticalStrut(24)); p.add(btnNavUsers);
        p.add(Box.createVerticalStrut(10)); p.add(btnNavMeds);
        p.add(Box.createVerticalGlue());    p.add(btnLogout);
        return p;
    }

    private JButton sidebarBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13)); b.setForeground(COLOR_WHITE);
        b.setBackground(COLOR_SIDEBAR); b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));
        b.setAlignmentX(LEFT_ALIGNMENT); b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(0, 14, 0, 0));
        return b;
    }

    // ── Content CardLayout ────────────────────────────────────────────────────

    private JPanel buildContent() {
        cardLayout = new CardLayout(); contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(COLOR_LIGHT);
        contentPanel.add(buildSection("Gestion des Utilisateurs", buildUserForm(), buildUserTable()), "USERS");
        contentPanel.add(buildSection("Gestion des Médicaments",  buildMedForm(),  buildMedTable()),  "MEDS");
        return contentPanel;
    }

    private JPanel buildSection(String title, JPanel form, JPanel table) {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(COLOR_LIGHT); p.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARY); header.setBorder(new EmptyBorder(12, 20, 12, 20));
        header.setPreferredSize(new Dimension(0, 50));
        JLabel lbl = new JLabel(title); lbl.setFont(new Font("Segoe UI", Font.BOLD, 17)); lbl.setForeground(COLOR_WHITE);
        header.add(lbl, BorderLayout.WEST);
        p.add(header, BorderLayout.NORTH);
        p.add(form,   BorderLayout.WEST);
        p.add(table,  BorderLayout.CENTER);
        return p;
    }

    // ── Formulaire Utilisateurs ───────────────────────────────────────────────

    private JPanel buildUserForm() {
        JPanel f = form();
        txtUserId     = row(f, "ID",           "USR001");
        txtUserCin    = row(f, "CIN",          "12345678");
        txtUserNom    = row(f, "Nom",          "Nom");
        txtUserPrenom = row(f, "Prénom",       "Prénom");
        txtUserEmail  = row(f, "Email",        "email@mail.com");
        txtUserPwd    = row(f, "Mot de passe", "••••••");
        cmbUserType   = combo(f, "Type", new String[]{"PHARMACIEN","ADMIN"});
        f.add(Box.createVerticalStrut(14));
        btnAddUser    = actionBtn("Ajouter",    COLOR_SECONDARY); f.add(btnAddUser);    f.add(vs(8));
        btnUpdateUser = actionBtn("Modifier",   COLOR_ACCENT);    f.add(btnUpdateUser); f.add(vs(8));
        btnDeleteUser = actionBtn("Supprimer",  new Color(231,76,60)); f.add(btnDeleteUser); f.add(vs(8));
        btnClearUser  = actionBtn("Vider",      Color.GRAY);      f.add(btnClearUser);
        return f;
    }

    // ── Formulaire Médicaments ────────────────────────────────────────────────

    private JPanel buildMedForm() {
        JPanel f = form();
        txtMedId    = row(f, "ID Médicament", "MED001");
        txtMedNom   = row(f, "Nom",           "Nom médicament");
        txtMedPrix  = row(f, "Prix (DT)",     "0.00");
        txtMedStock = row(f, "Stock",         "0");
        cmbMedType  = combo(f, "Type", new String[]{"Comprimé","Sirop","Injection","Pommade","Capsule","Autre"});
        f.add(Box.createVerticalStrut(14));
        btnAddMed    = actionBtn("Ajouter",   COLOR_SECONDARY); f.add(btnAddMed);    f.add(vs(8));
        btnUpdateMed = actionBtn("Modifier",  COLOR_ACCENT);    f.add(btnUpdateMed); f.add(vs(8));
        btnDeleteMed = actionBtn("Supprimer", new Color(231,76,60)); f.add(btnDeleteMed); f.add(vs(8));
        btnClearMed  = actionBtn("Vider",     Color.GRAY);      f.add(btnClearMed);
        return f;
    }

    // ── Tables ────────────────────────────────────────────────────────────────

    private JPanel buildUserTable() {
        String[] cols = {"ID","CIN","Nom","Prénom","Email","Type"};
        modelUsers = model(cols); tableUsers = styledTable(modelUsers);
        btnSearchUser = actionBtn("Rechercher", COLOR_PRIMARY); btnSearchUser.setPreferredSize(new Dimension(130,36));
        txtSearchUser = searchField();
        return tablePanel(txtSearchUser, btnSearchUser, tableUsers);
    }

    private JPanel buildMedTable() {
        String[] cols = {"ID","Nom","Prix (DT)","Stock","Type"};
        modelMeds = model(cols); tableMeds = styledTable(modelMeds);
        btnSearchMed = actionBtn("Rechercher", COLOR_PRIMARY); btnSearchMed.setPreferredSize(new Dimension(130,36));
        txtSearchMed = searchField();
        return tablePanel(txtSearchMed, btnSearchMed, tableMeds);
    }

    private JPanel tablePanel(JTextField search, JButton searchBtn, JTable table) {
        JPanel bar = new JPanel(new BorderLayout(8,0)); bar.setBackground(COLOR_LIGHT);
        bar.add(search, BorderLayout.CENTER); bar.add(searchBtn, BorderLayout.EAST);
        JPanel p = new JPanel(new BorderLayout(0,10)); p.setBackground(COLOR_LIGHT);
        JScrollPane scroll = new JScrollPane(table); scroll.setBorder(new LineBorder(new Color(200,220,240),1,true));
        p.add(bar, BorderLayout.NORTH); p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ── Helpers de style ──────────────────────────────────────────────────────

    private JPanel form() {
        JPanel f = new JPanel(); f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
        f.setBackground(COLOR_WHITE); f.setPreferredSize(new Dimension(250,0));
        f.setBorder(new CompoundBorder(new LineBorder(new Color(200,220,240),1,true), new EmptyBorder(18,16,18,16)));
        return f;
    }

    private JTextField row(JPanel form, String label, String ph) {
        form.add(lbl(label, Font.BOLD, 11, COLOR_PRIMARY));
        form.add(vs(4));
        JTextField f = new JTextField(ph);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12)); f.setForeground(Color.GRAY);
        f.setMaximumSize(new Dimension(Short.MAX_VALUE, 34)); f.setAlignmentX(LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(new LineBorder(new Color(200,220,240),1,true), new EmptyBorder(4,8,4,8)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if(f.getText().equals(ph)){f.setText("");f.setForeground(COLOR_TEXT);} }
            public void focusLost (FocusEvent e) { if(f.getText().isEmpty()){f.setText(ph);f.setForeground(Color.GRAY);} }
        });
        form.add(f); form.add(vs(9));
        return f;
    }

    private JComboBox<String> combo(JPanel form, String label, String[] opts) {
        form.add(lbl(label, Font.BOLD, 11, COLOR_PRIMARY)); form.add(vs(4));
        JComboBox<String> c = new JComboBox<>(opts);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        c.setMaximumSize(new Dimension(Short.MAX_VALUE, 34)); c.setAlignmentX(LEFT_ALIGNMENT);
        form.add(c); form.add(vs(9));
        return c;
    }

    private JButton actionBtn(String t, Color bg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12)); b.setForeground(COLOR_WHITE);
        b.setBackground(bg); b.setOpaque(true); b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Short.MAX_VALUE, 36)); b.setAlignmentX(LEFT_ALIGNMENT);
        return b;
    }

    private JTextField searchField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(new CompoundBorder(new LineBorder(COLOR_ACCENT,1,true), new EmptyBorder(6,10,6,10)));
        return f;
    }

    private DefaultTableModel model(String[] cols) {
        return new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c){return false;} };
    }

    private JTable styledTable(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13)); t.setRowHeight(30);
        t.setGridColor(new Color(220,230,240)); t.setSelectionBackground(COLOR_ACCENT);
        t.setSelectionForeground(COLOR_WHITE); t.setShowVerticalLines(false);
        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 12)); h.setBackground(COLOR_PRIMARY);
        h.setForeground(COLOR_WHITE); h.setReorderingAllowed(false);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl,v,sel,foc,row,col);
                if(!sel){ setBackground(row%2==0?COLOR_WHITE:new Color(235,245,255)); setForeground(COLOR_TEXT); }
                setBorder(new EmptyBorder(0,8,0,8)); return this;
            }
        });
        return t;
    }

    private static JLabel lbl(String t, int style, int size, Color c) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI",style,size));
        l.setForeground(c); l.setAlignmentX(LEFT_ALIGNMENT); return l;
    }
    private static Component vs(int h) { return Box.createVerticalStrut(h); }

    // ── Navigation ────────────────────────────────────────────────────────────

    public void showUsers() { cardLayout.show(contentPanel,"USERS"); }
    public void showMeds()  { cardLayout.show(contentPanel,"MEDS"); }

    // ── Getters Utilisateurs ──────────────────────────────────────────────────

    private String clean(JTextField f, String ph) { String v=f.getText().trim(); return v.equals(ph)?"":v; }

    public String getUserId()     { return clean(txtUserId,    "USR001"); }
    public String getUserCin()    { return clean(txtUserCin,   "12345678"); }
    public String getUserNom()    { return clean(txtUserNom,   "Nom"); }
    public String getUserPrenom() { return clean(txtUserPrenom,"Prénom"); }
    public String getUserEmail()  { return clean(txtUserEmail, "email@mail.com"); }
    public String getUserPwd()    { return clean(txtUserPwd,   "••••••"); }
    public String getUserType()   { return (String)cmbUserType.getSelectedItem(); }
    public String getSearchUser() { return txtSearchUser.getText().trim(); }
    public JTable getTableUsers() { return tableUsers; }
    public DefaultTableModel getModelUsers() { return modelUsers; }
    public JButton getBtnAddUser()    { return btnAddUser; }
    public JButton getBtnUpdateUser() { return btnUpdateUser; }
    public JButton getBtnDeleteUser() { return btnDeleteUser; }
    public JButton getBtnSearchUser() { return btnSearchUser; }
    public JButton getBtnClearUser()  { return btnClearUser; }

    // ── Getters Médicaments ───────────────────────────────────────────────────

    public String getMedId()     { return clean(txtMedId,    "MED001"); }
    public String getMedNom()    { return clean(txtMedNom,   "Nom médicament"); }
    public String getMedPrix()   { return clean(txtMedPrix,  "0.00"); }
    public String getMedStock()  { return clean(txtMedStock, "0"); }
    public String getMedType()   { return (String)cmbMedType.getSelectedItem(); }
    public String getSearchMed() { return txtSearchMed.getText().trim(); }
    public JTable getTableMeds() { return tableMeds; }
    public DefaultTableModel getModelMeds() { return modelMeds; }
    public JButton getBtnAddMed()    { return btnAddMed; }
    public JButton getBtnUpdateMed() { return btnUpdateMed; }
    public JButton getBtnDeleteMed() { return btnDeleteMed; }
    public JButton getBtnSearchMed() { return btnSearchMed; }
    public JButton getBtnClearMed()  { return btnClearMed; }

    // ── Getters Navigation ────────────────────────────────────────────────────

    public JButton getBtnNavUsers() { return btnNavUsers; }
    public JButton getBtnNavMeds()  { return btnNavMeds; }
    public JButton getBtnLogout()   { return btnLogout; }

    // ── Fill forms ────────────────────────────────────────────────────────────

    public void fillUserForm(String id, String cin, String nom, String prenom, String email, String type) {
        txtUserId.setText(id);         txtUserId.setForeground(COLOR_TEXT);
        txtUserCin.setText(cin);       txtUserCin.setForeground(COLOR_TEXT);
        txtUserNom.setText(nom);       txtUserNom.setForeground(COLOR_TEXT);
        txtUserPrenom.setText(prenom); txtUserPrenom.setForeground(COLOR_TEXT);
        txtUserEmail.setText(email);   txtUserEmail.setForeground(COLOR_TEXT);
        cmbUserType.setSelectedItem(type);
    }

    public void fillMedForm(String id, String nom, String prix, String stock, String type) {
        txtMedId.setText(id);       txtMedId.setForeground(COLOR_TEXT);
        txtMedNom.setText(nom);     txtMedNom.setForeground(COLOR_TEXT);
        txtMedPrix.setText(prix);   txtMedPrix.setForeground(COLOR_TEXT);
        txtMedStock.setText(stock); txtMedStock.setForeground(COLOR_TEXT);
        cmbMedType.setSelectedItem(type);
    }

    public void showError(String m)   { JOptionPane.showMessageDialog(this,m,"Erreur",  JOptionPane.ERROR_MESSAGE); }
    public void showSuccess(String m) { JOptionPane.showMessageDialog(this,m,"Succès",  JOptionPane.INFORMATION_MESSAGE); }
    public int  confirm(String m)     { return JOptionPane.showConfirmDialog(this,m,"Confirmation",JOptionPane.YES_NO_OPTION); }
}