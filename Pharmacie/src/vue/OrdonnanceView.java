package vue;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class OrdonnanceView extends JFrame {

    // ── Colours ───────────────────────────────────────────────────────────────
    private static final Color COLOR_PRIMARY   = new Color(33, 97, 140);
    private static final Color COLOR_SECONDARY = new Color(39, 174, 96);
    private static final Color COLOR_ACCENT    = new Color(52, 152, 219);
    private static final Color COLOR_LIGHT     = new Color(236, 245, 255);
    private static final Color COLOR_WHITE     = Color.WHITE;
    private static final Color COLOR_TEXT      = new Color(30, 39, 46);
    private static final Color COLOR_RED       = new Color(231, 76, 60);

    // Sentinel placeholder strings (never valid real data)
    private static final String PH_ORD_ID    = "__ORD_ID__";
    private static final String PH_CLIENT_ID = "__CLT_ID__";
    private static final String PH_DATE      = "__DATE__";
    private static final String PH_MED_ID    = "__MED_ID__";
    private static final String PH_QTY       = "__QTY__";

    // ── Header ────────────────────────────────────────────────────────────────
    private JLabel lblOrdId;

    // ── New Ordonnance block ──────────────────────────────────────────────────
    private JTextField txtOrdId, txtClientId, txtDate;
    private JButton    btnCreateOrd;

    // ── Add Medicine block ────────────────────────────────────────────────────
    private JTextField txtMedId, txtQuantite;
    private JButton    btnAddLigne, btnRemoveLigne;

    // ── Table + Footer ────────────────────────────────────────────────────────
    private JTable             tableDetails;
    private DefaultTableModel  model;
    private JLabel             lblTotal;
    private JButton            btnClose;

    public OrdonnanceView(String ordonnanceId, String dateOrd, String clientInfo) {
        setTitle("Ordonnance N° " + ordonnanceId);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(950, 620);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_LIGHT);
        root.add(buildHeader(ordonnanceId, dateOrd), BorderLayout.NORTH);
        root.add(buildCenter(),                      BorderLayout.CENTER);
        root.add(buildFooter(),                      BorderLayout.SOUTH);
        setContentPane(root);
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader(String ordId, String dateOrd) {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(new Color(28, 78, 115));
        h.setBorder(new EmptyBorder(10, 24, 10, 24));
        h.setPreferredSize(new Dimension(0, 50));

        JLabel logo = lbl("PharmaCie — Ordonnance Médicale", Font.BOLD, 15, COLOR_WHITE);
        lblOrdId    = lbl("N° " + ordId + "   |   Date : " + dateOrd, Font.PLAIN, 12, new Color(190, 225, 255));

        h.add(logo,    BorderLayout.WEST);
        h.add(lblOrdId, BorderLayout.EAST);
        return h;
    }

    // ── Centre ────────────────────────────────────────────────────────────────

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setBackground(COLOR_LIGHT);
        p.setBorder(new EmptyBorder(14, 18, 8, 18));
        p.add(buildLeftPanel(),  BorderLayout.WEST);
        p.add(buildTablePanel(), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildLeftPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(COLOR_LIGHT);
        p.setPreferredSize(new Dimension(245, 0));
        p.add(buildNewOrdBlock());
        p.add(vs(12));
        p.add(buildAddMedBlock());
        p.add(Box.createVerticalGlue());
        return p;
    }

    private JPanel buildNewOrdBlock() {
        JPanel f = block("Nouvelle Ordonnance");
        txtOrdId    = row(f, "ID Ordonnance",     PH_ORD_ID,    "ORD001");
        txtClientId = row(f, "ID Client",         PH_CLIENT_ID, "CLT001");
        txtDate     = row(f, "Date (yyyy-MM-dd)", PH_DATE,      "2025-01-01");
        f.add(vs(4));
        btnCreateOrd = actionBtn("Créer Ordonnance", COLOR_PRIMARY);
        f.add(btnCreateOrd);
        return f;
    }

    private JPanel buildAddMedBlock() {
        JPanel f = block("Ajouter Médicament");
        txtMedId    = row(f, "ID Médicament", PH_MED_ID, "MED001");
        txtQuantite = row(f, "Quantité",      PH_QTY,    "1");
        f.add(vs(4));
        btnAddLigne    = actionBtn("Ajouter ligne",              COLOR_SECONDARY); f.add(btnAddLigne);    f.add(vs(8));
        btnRemoveLigne = actionBtn("Retirer ligne sélectionnée", COLOR_RED);       f.add(btnRemoveLigne);
        return f;
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    private JPanel buildTablePanel() {
        String[] cols = {"ID Méd.", "Médicament", "Qté", "Prix Unit. (DT)", "Sous-total (DT)"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableDetails = styledTable(model);

        JScrollPane scroll = new JScrollPane(tableDetails);
        scroll.setBorder(new LineBorder(new Color(190, 215, 250), 1, true));

        JLabel title = lbl(" Médicaments prescrits", Font.BOLD, 13, COLOR_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 4, 0));

        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(COLOR_LIGHT);
        p.add(title,  BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ── Footer ────────────────────────────────────────────────────────────────

    private JPanel buildFooter() {
        JPanel f = new JPanel(new BorderLayout());
        f.setBackground(COLOR_WHITE);
        f.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(200, 220, 240)),
            new EmptyBorder(14, 24, 14, 24)
        ));

        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new BoxLayout(totalPanel, BoxLayout.Y_AXIS));
        totalPanel.setBackground(COLOR_WHITE);
        totalPanel.add(lbl("MONTANT TOTAL", Font.BOLD, 10, Color.GRAY));
        lblTotal = new JLabel("0.000 DT");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotal.setForeground(COLOR_PRIMARY);
        totalPanel.add(lblTotal);

        btnClose = actionBtn("Fermer", COLOR_PRIMARY);
        btnClose.setPreferredSize(new Dimension(115, 40));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.setBackground(COLOR_WHITE);
        btnPanel.add(btnClose);

        f.add(totalPanel, BorderLayout.WEST);
        f.add(btnPanel,   BorderLayout.EAST);
        return f;
    }

    // ── Style helpers ─────────────────────────────────────────────────────────

    private JPanel block(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(COLOR_WHITE);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        p.setBorder(new CompoundBorder(
            new LineBorder(new Color(190, 215, 250), 1, true),
            new EmptyBorder(12, 14, 16, 14)
        ));
        JLabel lbl = lbl(title, Font.BOLD, 12, COLOR_PRIMARY);
        p.add(lbl);
        p.add(vs(6));
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(210, 230, 255));
        sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        p.add(sep);
        p.add(vs(10));
        return p;
    }

    /**
     * Creates a labelled text field.
     *
     * @param sentinel  Internal sentinel value used by clean() to detect "empty" state.
     * @param hint      Greyed hint text shown to the user before they type.
     */
    private JTextField row(JPanel form, String label, String sentinel, String hint) {
        form.add(lbl(label, Font.BOLD, 10, new Color(80, 110, 140)));
        form.add(vs(3));
        JTextField f = new JTextField(hint);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setForeground(Color.GRAY);
        f.setMaximumSize(new Dimension(Short.MAX_VALUE, 32));
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 220, 245), 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
        // Use sentinel tag on the field so prefill can be read back correctly
        f.setName(sentinel);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getForeground().equals(Color.GRAY)) { f.setText(""); f.setForeground(COLOR_TEXT); }
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(hint); f.setForeground(Color.GRAY); }
            }
        });
        form.add(f);
        form.add(vs(10));
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

    private JTable styledTable(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(30);
        t.setGridColor(new Color(210, 230, 255));
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
                    setBackground(row % 2 == 0 ? COLOR_WHITE : COLOR_LIGHT);
                    setForeground(COLOR_TEXT);
                }
                setHorizontalAlignment(col >= 2 ? RIGHT : LEFT);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
        return t;
    }

    private static JLabel lbl(String t, int style, int size, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(c);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private static Component vs(int h) { return Box.createVerticalStrut(h); }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Pre-fills the "Nouvelle Ordonnance" form with real values.
     * These values are NOT treated as placeholders — they will be read back correctly.
     */
    public void prefillNewOrd(String ordId, String clientId, String date) {
        txtOrdId   .setText(ordId);    txtOrdId   .setForeground(COLOR_TEXT);
        txtClientId.setText(clientId); txtClientId.setForeground(COLOR_TEXT);
        txtDate    .setText(date);     txtDate    .setForeground(COLOR_TEXT);
    }

    /** Returns the field value; empty string if it contains only the grey hint text. */
    private String clean(JTextField f) {
        if (f.getForeground().equals(Color.GRAY)) return "";
        return f.getText().trim();
    }

    public String getNewOrdId()    { return clean(txtOrdId); }
    public String getNewClientId() { return clean(txtClientId); }
    public String getNewDate()     { return clean(txtDate); }
    public String getMedId()       { return clean(txtMedId); }
    public String getQuantiteStr() { return clean(txtQuantite); }

    public void addLigne(String medId, String medNom, int qty, double prix) {
        model.addRow(new Object[]{
            medId, medNom, qty,
            String.format("%.3f", prix),
            String.format("%.3f", qty * prix)
        });
    }

    public void setTotal(double total)  { lblTotal.setText(String.format("%.3f DT", total)); }
    public void clearTable()            { model.setRowCount(0); }

    public void updateHeader(String id, String date, String client) {
        lblOrdId.setText("N° " + id + "   |   Date : " + date);
        setTitle("Ordonnance N° " + id);
    }

    public DefaultTableModel getModel()          { return model; }
    public JTable            getTableDetails()   { return tableDetails; }
    public JButton           getBtnCreateOrd()   { return btnCreateOrd; }
    public JButton           getBtnAddLigne()    { return btnAddLigne; }
    public JButton           getBtnRemoveLigne() { return btnRemoveLigne; }
    public JButton           getBtnClose()       { return btnClose; }
    public JLabel            getLblTotal()       { return lblTotal; }

    public void showError(String m)   { JOptionPane.showMessageDialog(this, m, "Erreur",  JOptionPane.ERROR_MESSAGE); }
    public void showSuccess(String m) { JOptionPane.showMessageDialog(this, m, "Succès",  JOptionPane.INFORMATION_MESSAGE); }
}