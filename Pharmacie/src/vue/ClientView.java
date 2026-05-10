package vue;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ClientView extends JFrame {

    public static final Color COLOR_PRIMARY   = new Color(33, 97, 140);
    public static final Color COLOR_SECONDARY = new Color(39, 174, 96);
    public static final Color COLOR_ACCENT    = new Color(52, 152, 219);
    public static final Color COLOR_LIGHT     = new Color(236, 240, 241);
    public static final Color COLOR_WHITE     = Color.WHITE;
    public static final Color COLOR_TEXT      = new Color(30, 39, 46);
    public static final Color COLOR_HEADER    = new Color(28, 78, 115);

    // ── Form fields ───────────────────────────────────────────────────────────
    private JTextField txtId, txtNom, txtPrenom, txtTel, txtCredit, txtSearch;

    // ── Buttons ───────────────────────────────────────────────────────────────
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnClose;

    // ── Table ─────────────────────────────────────────────────────────────────
    private JTable table;
    private DefaultTableModel model;

    // Placeholder strings — must match what clean() checks against
    private static final String PH_ID     = "CLT001";
    private static final String PH_NOM    = "Nom";
    private static final String PH_PRENOM = "Prénom";
    private static final String PH_TEL    = "0X XXX XXX";
    private static final String PH_CREDIT = "0.000";

    public ClientView() {
        setTitle("Gestion des Clients");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildForm(),   BorderLayout.WEST);
        root.add(buildTable(),  BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_HEADER);
        p.setBorder(new EmptyBorder(12, 20, 12, 20));
        p.setPreferredSize(new Dimension(0, 50));

        JLabel lbl = new JLabel("Gestion des Clients");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lbl.setForeground(COLOR_WHITE);

        btnClose = actionBtn("✕ Fermer", new Color(180, 60, 60));
        btnClose.setPreferredSize(new Dimension(110, 32));

        p.add(lbl,      BorderLayout.WEST);
        p.add(btnClose, BorderLayout.EAST);
        return p;
    }

    // ── Form ──────────────────────────────────────────────────────────────────

    private JPanel buildForm() {
        JPanel f = form();
        txtId     = row(f, "ID Client",   PH_ID);
        txtNom    = row(f, "Nom",         PH_NOM);
        txtPrenom = row(f, "Prénom",      PH_PRENOM);
        txtTel    = row(f, "Téléphone",   PH_TEL);
        txtCredit = row(f, "Crédit (DT)", PH_CREDIT);
        f.add(Box.createVerticalStrut(14));
        btnAdd    = actionBtn("Ajouter",   COLOR_SECONDARY);         f.add(btnAdd);    f.add(vs(8));
        btnUpdate = actionBtn("Modifier",  COLOR_ACCENT);            f.add(btnUpdate); f.add(vs(8));
        btnDelete = actionBtn("Supprimer", new Color(231, 76, 60));  f.add(btnDelete); f.add(vs(8));
        btnClear  = actionBtn("Vider",     Color.GRAY);              f.add(btnClear);
        return f;
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    private JPanel buildTable() {
        model = new DefaultTableModel(
            new String[]{"ID", "Nom", "Prénom", "Téléphone", "Crédit"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table     = styledTable(model);
        btnSearch = actionBtn("Rechercher", COLOR_PRIMARY);
        btnSearch.setPreferredSize(new Dimension(130, 36));
        txtSearch = searchField();

        JPanel bar = new JPanel(new BorderLayout(8, 0));
        bar.setBackground(COLOR_LIGHT);
        bar.add(txtSearch, BorderLayout.CENTER);
        bar.add(btnSearch, BorderLayout.EAST);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(200, 220, 240), 1, true));

        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(COLOR_LIGHT);
        p.setBorder(new EmptyBorder(20, 10, 20, 20));
        p.add(bar,    BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ── Style helpers ─────────────────────────────────────────────────────────

    private JPanel form() {
        JPanel f = new JPanel();
        f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
        f.setBackground(COLOR_WHITE);
        f.setPreferredSize(new Dimension(240, 0));
        f.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 220, 240), 1, true),
            new EmptyBorder(18, 16, 18, 16)
        ));
        return f;
    }

    private JTextField row(JPanel form, String label, String ph) {
        form.add(lbl(label));
        form.add(vs(4));
        JTextField f = new JTextField(ph);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setForeground(Color.GRAY);
        f.setMaximumSize(new Dimension(Short.MAX_VALUE, 34));
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 220, 240), 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(ph)) { f.setText(""); f.setForeground(COLOR_TEXT); }
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(ph); f.setForeground(Color.GRAY); }
            }
        });
        form.add(f);
        form.add(vs(9));
        return f;
    }

    private JButton actionBtn(String t, Color bg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(COLOR_WHITE); b.setBackground(bg);
        b.setOpaque(true); b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        b.setAlignmentX(LEFT_ALIGNMENT);
        return b;
    }

    private JTextField searchField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(new CompoundBorder(
            new LineBorder(COLOR_ACCENT, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    private JTable styledTable(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(30);
        t.setGridColor(new Color(220, 230, 240));
        t.setSelectionBackground(COLOR_ACCENT);
        t.setSelectionForeground(COLOR_WHITE);
        t.setShowVerticalLines(false);
        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 12));
        h.setBackground(COLOR_PRIMARY);
        h.setForeground(COLOR_WHITE);
        h.setReorderingAllowed(false);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable tbl, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, v, sel, foc, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? COLOR_WHITE : new Color(235, 245, 255));
                    setForeground(COLOR_TEXT);
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
        return t;
    }

    private static JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(COLOR_PRIMARY);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private static Component vs(int h) { return Box.createVerticalStrut(h); }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** Returns the field value, or empty string if it shows only the grey hint text. */
    private String clean(JTextField f, String ph) {
        String v = f.getText().trim();
        return v.equals(ph) ? "" : v;
    }

    public String getClientId()     { return clean(txtId,     PH_ID); }
    public String getClientNom()    { return clean(txtNom,    PH_NOM); }
    public String getClientPrenom() { return clean(txtPrenom, PH_PRENOM); }
    public String getClientTel()    { return clean(txtTel,    PH_TEL); }
    public String getClientCredit() { return clean(txtCredit, PH_CREDIT); }
    public String getSearchText()   { return txtSearch.getText().trim(); }

    public JTable            getTable() { return table; }
    public DefaultTableModel getModel() { return model; }
    public JButton getBtnAdd()    { return btnAdd; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
    public JButton getBtnClear()  { return btnClear; }
    public JButton getBtnSearch() { return btnSearch; }
    public JButton getBtnClose()  { return btnClose; }

    /** Fills the form with real data (not placeholders). */
    public void fillForm(String id, String nom, String prenom, String tel, String credit) {
        txtId    .setText(id);     txtId    .setForeground(COLOR_TEXT);
        txtNom   .setText(nom);    txtNom   .setForeground(COLOR_TEXT);
        txtPrenom.setText(prenom); txtPrenom.setForeground(COLOR_TEXT);
        txtTel   .setText(tel);    txtTel   .setForeground(COLOR_TEXT);
        txtCredit.setText(credit); txtCredit.setForeground(COLOR_TEXT);
    }

    /**
     * Resets all fields to their grey placeholder state.
     * Sets text to placeholder AND colour to gray so clean() correctly returns "".
     */
    public void clearForm() {
        txtId    .setText(PH_ID);     txtId    .setForeground(Color.GRAY);
        txtNom   .setText(PH_NOM);    txtNom   .setForeground(Color.GRAY);
        txtPrenom.setText(PH_PRENOM); txtPrenom.setForeground(Color.GRAY);
        txtTel   .setText(PH_TEL);    txtTel   .setForeground(Color.GRAY);
        txtCredit.setText(PH_CREDIT); txtCredit.setForeground(Color.GRAY);
    }

    public void showError(String m)   { JOptionPane.showMessageDialog(this, m, "Erreur",  JOptionPane.ERROR_MESSAGE); }
    public void showSuccess(String m) { JOptionPane.showMessageDialog(this, m, "Succès",  JOptionPane.INFORMATION_MESSAGE); }
    public int  confirm(String m)     { return JOptionPane.showConfirmDialog(this, m, "Confirmation", JOptionPane.YES_NO_OPTION); }
}