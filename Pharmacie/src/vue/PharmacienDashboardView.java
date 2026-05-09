package vue;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PharmacienDashboardView extends JFrame {

    public static final Color COLOR_SIDEBAR   = new Color(22, 90, 76);
    public static final Color COLOR_PRIMARY   = new Color(33, 150, 120);
    public static final Color COLOR_SECONDARY = new Color(39, 174, 96);
    public static final Color COLOR_ACCENT    = new Color(26, 188, 156);
    public static final Color COLOR_LIGHT     = new Color(232, 248, 244);
    public static final Color COLOR_WHITE     = Color.WHITE;
    public static final Color COLOR_TEXT      = new Color(30, 39, 46);
    public static final Color COLOR_BLUE      = new Color(33, 97, 140);
    public static final Color COLOR_PURPLE    = new Color(142, 68, 173);

    private JTextField txtClientId, txtClientNom, txtClientPrenom, txtClientTel, txtClientCredit, txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch;
    private JButton btnVoirOrdonnances, btnNouvelleOrdonnance, btnManageClients, btnLogout;
    private JTable table; private DefaultTableModel model;

    // ── Callbacks ─────────────────────────────────────────────────────────────
    private Runnable onAdd, onUpdate, onDelete, onSearch, onClear;
    private Runnable onVoirOrdonnances, onNouvelleOrdonnance, onManageClients, onLogout;
    private Runnable onRowSelected, onRowDoubleClicked;

    public PharmacienDashboardView() {
        setTitle("Pharmacie — Espace Pharmacien");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 660); setLocationRelativeTo(null); setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(),      BorderLayout.WEST);
        root.add(buildClientPanel(),  BorderLayout.CENTER);
        setContentPane(root);

        // ── All events wired here in the view ─────────────────────────────────
        btnAdd.addActionListener              (e -> { if (onAdd                != null) onAdd.run(); });
        btnUpdate.addActionListener           (e -> { if (onUpdate             != null) onUpdate.run(); });
        btnDelete.addActionListener           (e -> { if (onDelete             != null) onDelete.run(); });
        btnSearch.addActionListener           (e -> { if (onSearch             != null) onSearch.run(); });
        btnClear.addActionListener            (e -> { if (onClear              != null) onClear.run(); });
        btnVoirOrdonnances.addActionListener  (e -> { if (onVoirOrdonnances    != null) onVoirOrdonnances.run(); });
        btnNouvelleOrdonnance.addActionListener(e ->{ if (onNouvelleOrdonnance != null) onNouvelleOrdonnance.run(); });
        btnManageClients.addActionListener    (e -> { if (onManageClients      != null) onManageClients.run(); });
        btnLogout.addActionListener           (e -> { if (onLogout             != null) onLogout.run(); });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && onRowSelected != null) onRowSelected.run();
        });
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && onRowDoubleClicked != null) onRowDoubleClicked.run();
            }
        });
    }

    // ── Callback setters ──────────────────────────────────────────────────────

    public void setOnAdd               (Runnable r) { onAdd                = r; }
    public void setOnUpdate            (Runnable r) { onUpdate             = r; }
    public void setOnDelete            (Runnable r) { onDelete             = r; }
    public void setOnSearch            (Runnable r) { onSearch             = r; }
    public void setOnClear             (Runnable r) { onClear              = r; }
    public void setOnVoirOrdonnances   (Runnable r) { onVoirOrdonnances    = r; }
    public void setOnNouvelleOrdonnance(Runnable r) { onNouvelleOrdonnance = r; }
    public void setOnManageClients     (Runnable r) { onManageClients      = r; }
    public void setOnLogout            (Runnable r) { onLogout             = r; }
    public void setOnRowSelected       (Runnable r) { onRowSelected        = r; }
    public void setOnRowDoubleClicked  (Runnable r) { onRowDoubleClicked   = r; }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(COLOR_SIDEBAR); p.setPreferredSize(new Dimension(210, 0));
        p.setBorder(new EmptyBorder(20, 12, 20, 12));

        JLabel lblApp  = lbl("Pharmacie",  Font.BOLD,  18, COLOR_WHITE);            lblApp.setAlignmentX(CENTER_ALIGNMENT);
        JLabel lblRole = lbl("PHARMACIEN", Font.PLAIN, 11, new Color(180,230,210)); lblRole.setAlignmentX(CENTER_ALIGNMENT);
        JSeparator sep = new JSeparator(); sep.setForeground(new Color(40,130,100)); sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));

        btnManageClients = sidebarBtn("Gérer les Clients");
        btnLogout        = sidebarBtn("Déconnexion"); btnLogout.setForeground(new Color(255,120,120));

        p.add(vs(10)); p.add(lblApp); p.add(vs(4)); p.add(lblRole); p.add(vs(20)); p.add(sep);
        p.add(vs(24)); p.add(btnManageClients);
        p.add(Box.createVerticalGlue()); p.add(btnLogout);
        return p;
    }

    private JButton sidebarBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13)); b.setForeground(COLOR_WHITE);
        b.setBackground(COLOR_SIDEBAR); b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Short.MAX_VALUE, 42)); b.setAlignmentX(LEFT_ALIGNMENT);
        b.setHorizontalAlignment(SwingConstants.LEFT); b.setBorder(new EmptyBorder(0,14,0,0));
        return b;
    }

    // ── Client panel ──────────────────────────────────────────────────────────

    private JPanel buildClientPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(COLOR_LIGHT); p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_SIDEBAR); header.setBorder(new EmptyBorder(12, 20, 12, 20));
        header.setPreferredSize(new Dimension(0, 50));
        JLabel lbl = new JLabel("Gestion des Clients");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 17)); lbl.setForeground(COLOR_WHITE);
        header.add(lbl, BorderLayout.WEST);

        p.add(header,            BorderLayout.NORTH);
        p.add(buildClientForm(), BorderLayout.WEST);
        p.add(buildClientTable(),BorderLayout.CENTER);
        return p;
    }

    private JPanel buildClientForm() {
        JPanel f = form();
        txtClientId     = row(f, "ID Client",   "CLT001");
        txtClientNom    = row(f, "Nom",         "Nom");
        txtClientPrenom = row(f, "Prénom",      "Prénom");
        txtClientTel    = row(f, "Téléphone",   "0X XXX XXX");
        txtClientCredit = row(f, "Crédit (DT)", "0.000");
        f.add(vs(14));
        btnAdd    = actionBtn("Ajouter",   COLOR_SECONDARY);         f.add(btnAdd);    f.add(vs(8));
        btnUpdate = actionBtn("Modifier",  COLOR_ACCENT);            f.add(btnUpdate); f.add(vs(8));
        btnDelete = actionBtn("Supprimer", new Color(231, 76, 60));  f.add(btnDelete); f.add(vs(8));
        btnClear  = actionBtn("Vider",     Color.GRAY);              f.add(btnClear);  f.add(vs(14));

        JSeparator sep = new JSeparator(); sep.setForeground(new Color(180,220,200));
        sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 1)); f.add(sep); f.add(vs(12));

        btnVoirOrdonnances    = actionBtn("Voir Ordonnances",    COLOR_BLUE);   f.add(btnVoirOrdonnances);    f.add(vs(8));
        btnNouvelleOrdonnance = actionBtn("Nouvelle Ordonnance", COLOR_PURPLE); f.add(btnNouvelleOrdonnance);
        return f;
    }

    private JPanel buildClientTable() {
        model = new DefaultTableModel(new String[]{"ID","Nom","Prénom","Téléphone","Crédit"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table     = styledTable(model);
        btnSearch = actionBtn("Rechercher", COLOR_PRIMARY); btnSearch.setPreferredSize(new Dimension(130, 36));
        txtSearch = searchField();

        JPanel bar = new JPanel(new BorderLayout(8, 0)); bar.setBackground(COLOR_LIGHT);
        bar.add(txtSearch, BorderLayout.CENTER); bar.add(btnSearch, BorderLayout.EAST);

        JPanel p = new JPanel(new BorderLayout(0, 10)); p.setBackground(COLOR_LIGHT);
        JScrollPane scroll = new JScrollPane(table); scroll.setBorder(new LineBorder(new Color(180,220,200),1,true));
        p.add(bar, BorderLayout.NORTH); p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ── Style helpers ─────────────────────────────────────────────────────────

    private JPanel form() {
        JPanel f = new JPanel(); f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
        f.setBackground(COLOR_WHITE); f.setPreferredSize(new Dimension(250, 0));
        f.setBorder(new CompoundBorder(new LineBorder(new Color(180,220,200),1,true), new EmptyBorder(18,16,18,16)));
        return f;
    }

    private JTextField row(JPanel form, String label, String ph) {
        form.add(lbl(label, Font.BOLD, 11, COLOR_PRIMARY)); form.add(vs(4));
        JTextField f = new JTextField(ph);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12)); f.setForeground(Color.GRAY);
        f.setMaximumSize(new Dimension(Short.MAX_VALUE, 34)); f.setAlignmentX(LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(new LineBorder(new Color(180,220,200),1,true), new EmptyBorder(4,8,4,8)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if (f.getText().equals(ph)) { f.setText(""); f.setForeground(COLOR_TEXT); } }
            public void focusLost (FocusEvent e)  { if (f.getText().isEmpty())  { f.setText(ph); f.setForeground(Color.GRAY); } }
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
        f.setBorder(new CompoundBorder(new LineBorder(COLOR_ACCENT,1,true), new EmptyBorder(6,10,6,10)));
        return f;
    }

    private JTable styledTable(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13)); t.setRowHeight(30);
        t.setGridColor(new Color(200,235,220)); t.setSelectionBackground(COLOR_ACCENT);
        t.setSelectionForeground(COLOR_WHITE); t.setShowVerticalLines(false);
        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 12)); h.setBackground(COLOR_SIDEBAR);
        h.setForeground(COLOR_WHITE); h.setReorderingAllowed(false);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl,v,sel,foc,row,col);
                if (!sel) { setBackground(row%2==0 ? COLOR_WHITE : new Color(230,250,240)); setForeground(COLOR_TEXT); }
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

    // ── Getters ───────────────────────────────────────────────────────────────

    private String clean(JTextField f, String ph) { String v = f.getText().trim(); return v.equals(ph) ? "" : v; }

    public String getClientId()     { return clean(txtClientId,     "CLT001"); }
    public String getClientNom()    { return clean(txtClientNom,    "Nom"); }
    public String getClientPrenom() { return clean(txtClientPrenom, "Prénom"); }
    public String getClientTel()    { return clean(txtClientTel,    "0X XXX XXX"); }
    public String getClientCredit() { return clean(txtClientCredit, "0.000"); }
    public String getSearchText()   { return txtSearch.getText().trim(); }

    public JTable            getTable() { return table; }
    public DefaultTableModel getModel() { return model; }

    public void fillClientForm(String id, String nom, String prenom, String tel, String credit) {
        txtClientId.setText(id);         txtClientId.setForeground(COLOR_TEXT);
        txtClientNom.setText(nom);       txtClientNom.setForeground(COLOR_TEXT);
        txtClientPrenom.setText(prenom); txtClientPrenom.setForeground(COLOR_TEXT);
        txtClientTel.setText(tel);       txtClientTel.setForeground(COLOR_TEXT);
        txtClientCredit.setText(credit); txtClientCredit.setForeground(COLOR_TEXT);
    }

    public void clearForm() {
        txtClientId.setText("CLT001");       txtClientId.setForeground(Color.GRAY);
        txtClientNom.setText("Nom");         txtClientNom.setForeground(Color.GRAY);
        txtClientPrenom.setText("Prénom");   txtClientPrenom.setForeground(Color.GRAY);
        txtClientTel.setText("0X XXX XXX"); txtClientTel.setForeground(Color.GRAY);
        txtClientCredit.setText("0.000");    txtClientCredit.setForeground(Color.GRAY);
    }

    public void showError(String m)   { JOptionPane.showMessageDialog(this, m, "Erreur",  JOptionPane.ERROR_MESSAGE); }
    public void showSuccess(String m) { JOptionPane.showMessageDialog(this, m, "Succès",  JOptionPane.INFORMATION_MESSAGE); }
    public int  confirm(String m)     { return JOptionPane.showConfirmDialog(this, m, "Confirmation", JOptionPane.YES_NO_OPTION); }
}