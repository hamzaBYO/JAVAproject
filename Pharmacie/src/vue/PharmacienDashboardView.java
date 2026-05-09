package vue;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class PharmacienDashboardView extends JFrame {

    // ── Couleurs ──────────────────────────────────────────────────────────────
    public static final Color COLOR_SIDEBAR   = new Color(22, 90, 76);
    public static final Color COLOR_PRIMARY   = new Color(33, 150, 120);
    public static final Color COLOR_SECONDARY = new Color(39, 174, 96);
    public static final Color COLOR_ACCENT    = new Color(26, 188, 156);
    public static final Color COLOR_LIGHT     = new Color(232, 248, 244);
    public static final Color COLOR_WHITE     = Color.WHITE;
    public static final Color COLOR_TEXT      = new Color(30, 39, 46);
    public static final Color COLOR_BLUE      = new Color(33, 97, 140);
    public static final Color COLOR_PURPLE    = new Color(142, 68, 173);

    // ── Champs Clients ────────────────────────────────────────────────────────
    private JTextField txtClientId, txtClientNom, txtClientPrenom, txtClientTel, txtClientCredit;
    private JButton btnAddClient, btnUpdateClient, btnDeleteClient, btnClearClient;
    private JButton btnSearchClient, btnVoirOrdonnances, btnNouvelleOrdonnance;
    private JTable tableClients; private DefaultTableModel modelClients;
    private JTextField txtSearchClient;

    // ── Navigation ────────────────────────────────────────────────────────────
    private JButton btnLogout;

    public PharmacienDashboardView() {
        setTitle("Pharmacie — Espace Pharmacien");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 660); setLocationRelativeTo(null); setResizable(true);
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(),      BorderLayout.WEST);
        root.add(buildClientPanel(),  BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(COLOR_SIDEBAR); p.setPreferredSize(new Dimension(210, 0));
        p.setBorder(new EmptyBorder(20, 12, 20, 12));

        JLabel lblApp  = lbl("Pharmacie",   Font.BOLD,  18, COLOR_WHITE);           lblApp.setAlignmentX(CENTER_ALIGNMENT);
        JLabel lblRole = lbl("PHARMACIEN",  Font.PLAIN, 11, new Color(180,230,210)); lblRole.setAlignmentX(CENTER_ALIGNMENT);
        JSeparator sep = new JSeparator(); sep.setForeground(new Color(40,130,100)); sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));

        btnLogout = sidebarBtn("Déconnexion"); btnLogout.setForeground(new Color(255, 120, 120));

        p.add(Box.createVerticalStrut(10)); p.add(lblApp); p.add(Box.createVerticalStrut(4));
        p.add(lblRole); p.add(Box.createVerticalStrut(20)); p.add(sep);
        p.add(Box.createVerticalStrut(24));
        p.add(Box.createVerticalGlue()); p.add(btnLogout);
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

    // ── Panel Clients ─────────────────────────────────────────────────────────

    private JPanel buildClientPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(COLOR_LIGHT); p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_SIDEBAR); header.setBorder(new EmptyBorder(12, 20, 12, 20));
        header.setPreferredSize(new Dimension(0, 50));
        JLabel lbl = new JLabel("Gestion des Clients");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 17)); lbl.setForeground(COLOR_WHITE);
        header.add(lbl, BorderLayout.WEST);

        p.add(header,             BorderLayout.NORTH);
        p.add(buildClientForm(),  BorderLayout.WEST);
        p.add(buildClientTable(), BorderLayout.CENTER);
        return p;
    }

    // ── Formulaire Clients ────────────────────────────────────────────────────

    private JPanel buildClientForm() {
        JPanel f = form();
        txtClientId     = row(f, "ID Client",   "CLT001");
        txtClientNom    = row(f, "Nom",         "Nom");
        txtClientPrenom = row(f, "Prénom",      "Prénom");
        txtClientTel    = row(f, "Téléphone",   "0X XXX XXX");
        txtClientCredit = row(f, "Crédit (DT)", "0.000");
        f.add(Box.createVerticalStrut(14));
        btnAddClient    = actionBtn("Ajouter",    COLOR_SECONDARY); f.add(btnAddClient);    f.add(vs(8));
        btnUpdateClient = actionBtn("Modifier",   COLOR_ACCENT);    f.add(btnUpdateClient); f.add(vs(8));
        btnDeleteClient = actionBtn("Supprimer",  new Color(231, 76, 60)); f.add(btnDeleteClient); f.add(vs(8));
        btnClearClient  = actionBtn("Vider",      Color.GRAY);      f.add(btnClearClient);  f.add(vs(14));

        JSeparator sep = new JSeparator(); sep.setForeground(new Color(180, 220, 200));
        sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 1)); f.add(sep); f.add(vs(12));

        btnVoirOrdonnances   = actionBtn("Voir Ordonnances",   COLOR_BLUE);   f.add(btnVoirOrdonnances);   f.add(vs(8));
        btnNouvelleOrdonnance = actionBtn("Nouvelle Ordonnance", COLOR_PURPLE); f.add(btnNouvelleOrdonnance);
        return f;
    }

    // ── Table Clients ─────────────────────────────────────────────────────────

    private JPanel buildClientTable() {
        String[] cols = {"ID", "Nom", "Prénom", "Téléphone", "Crédit"};
        modelClients = model(cols); tableClients = styledTable(modelClients);
        btnSearchClient = actionBtn("Rechercher", COLOR_PRIMARY); btnSearchClient.setPreferredSize(new Dimension(130, 36));
        txtSearchClient = searchField();
        return tablePanel(txtSearchClient, btnSearchClient, tableClients);
    }

    private JPanel tablePanel(JTextField search, JButton searchBtn, JTable table) {
        JPanel bar = new JPanel(new BorderLayout(8, 0)); bar.setBackground(COLOR_LIGHT);
        bar.add(search, BorderLayout.CENTER); bar.add(searchBtn, BorderLayout.EAST);
        JPanel p = new JPanel(new BorderLayout(0, 10)); p.setBackground(COLOR_LIGHT);
        JScrollPane scroll = new JScrollPane(table); scroll.setBorder(new LineBorder(new Color(180, 220, 200), 1, true));
        p.add(bar, BorderLayout.NORTH); p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ── Helpers de style ──────────────────────────────────────────────────────

    private JPanel form() {
        JPanel f = new JPanel(); f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
        f.setBackground(COLOR_WHITE); f.setPreferredSize(new Dimension(250, 0));
        f.setBorder(new CompoundBorder(new LineBorder(new Color(180, 220, 200), 1, true), new EmptyBorder(18, 16, 18, 16)));
        return f;
    }

    private JTextField row(JPanel form, String label, String ph) {
        form.add(lbl(label, Font.BOLD, 11, COLOR_PRIMARY)); form.add(vs(4));
        JTextField f = new JTextField(ph);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12)); f.setForeground(Color.GRAY);
        f.setMaximumSize(new Dimension(Short.MAX_VALUE, 34)); f.setAlignmentX(LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(new LineBorder(new Color(180, 220, 200), 1, true), new EmptyBorder(4, 8, 4, 8)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if (f.getText().equals(ph)) { f.setText(""); f.setForeground(COLOR_TEXT); } }
            public void focusLost(FocusEvent e)   { if (f.getText().isEmpty())  { f.setText(ph); f.setForeground(Color.GRAY); } }
        });
        form.add(f); form.add(vs(9));
        return f;
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
        f.setBorder(new CompoundBorder(new LineBorder(COLOR_ACCENT, 1, true), new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private DefaultTableModel model(String[] cols) {
        return new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
    }

    private JTable styledTable(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13)); t.setRowHeight(30);
        t.setGridColor(new Color(200, 235, 220)); t.setSelectionBackground(COLOR_ACCENT);
        t.setSelectionForeground(COLOR_WHITE); t.setShowVerticalLines(false);
        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 12)); h.setBackground(COLOR_SIDEBAR);
        h.setForeground(COLOR_WHITE); h.setReorderingAllowed(false);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, v, sel, foc, row, col);
                if (!sel) { setBackground(row % 2 == 0 ? COLOR_WHITE : new Color(230, 250, 240)); setForeground(COLOR_TEXT); }
                setBorder(new EmptyBorder(0, 8, 0, 8)); return this;
            }
        });
        return t;
    }

    private static JLabel lbl(String t, int style, int size, Color c) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(c); l.setAlignmentX(LEFT_ALIGNMENT); return l;
    }
    private static Component vs(int h) { return Box.createVerticalStrut(h); }

    // ── Getters ───────────────────────────────────────────────────────────────

    private String clean(JTextField f, String ph) { String v = f.getText().trim(); return v.equals(ph) ? "" : v; }

    public String getClientId()     { return clean(txtClientId,     "CLT001"); }
    public String getClientNom()    { return clean(txtClientNom,    "Nom"); }
    public String getClientPrenom() { return clean(txtClientPrenom, "Prénom"); }
    public String getClientTel()    { return clean(txtClientTel,    "0X XXX XXX"); }
    public String getClientCredit() { return clean(txtClientCredit, "0.000"); }
    public String getSearchClient() { return txtSearchClient.getText().trim(); }

    public JTable            getTableClients()         { return tableClients; }
    public DefaultTableModel getModelClients()         { return modelClients; }
    public JButton getBtnAddClient()                   { return btnAddClient; }
    public JButton getBtnUpdateClient()                { return btnUpdateClient; }
    public JButton getBtnDeleteClient()                { return btnDeleteClient; }
    public JButton getBtnClearClient()                 { return btnClearClient; }
    public JButton getBtnSearchClient()                { return btnSearchClient; }
    public JButton getBtnVoirOrdonnances()             { return btnVoirOrdonnances; }
    public JButton getBtnNouvelleOrdonnance()          { return btnNouvelleOrdonnance; }
    public JButton getBtnLogout()                      { return btnLogout; }

    public void fillClientForm(String id, String nom, String prenom, String tel, String credit) {
        txtClientId.setText(id);         txtClientId.setForeground(COLOR_TEXT);
        txtClientNom.setText(nom);       txtClientNom.setForeground(COLOR_TEXT);
        txtClientPrenom.setText(prenom); txtClientPrenom.setForeground(COLOR_TEXT);
        txtClientTel.setText(tel);       txtClientTel.setForeground(COLOR_TEXT);
        txtClientCredit.setText(credit); txtClientCredit.setForeground(COLOR_TEXT);
    }

    public void showError(String m)   { JOptionPane.showMessageDialog(this, m, "Erreur",        JOptionPane.ERROR_MESSAGE); }
    public void showSuccess(String m) { JOptionPane.showMessageDialog(this, m, "Succès",        JOptionPane.INFORMATION_MESSAGE); }
    public int  confirm(String m)     { return JOptionPane.showConfirmDialog(this, m, "Confirmation", JOptionPane.YES_NO_OPTION); }
}