package vue;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class RegisterView extends JFrame {

    public static final Color COLOR_PRIMARY = new Color(33, 97, 140);
    public static final Color COLOR_ACCENT  = new Color(52, 152, 219);
    public static final Color COLOR_WHITE   = Color.WHITE;
    public static final Color COLOR_TEXT    = new Color(30, 39, 46);
    public static final Color COLOR_LIGHT   = new Color(236, 240, 241);

    // Fields declared here — no casting needed anywhere
    private JTextField     txtId, txtCin, txtNom, txtPrenom, txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbType;
    private JButton btnRegister, btnGoLogin;

    private Runnable onRegister, onGoLogin;

    public RegisterView() {
        setTitle("Pharmacie — Inscription");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(560, 680);
        setMinimumSize(new Dimension(480, 600));
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_LIGHT);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildForm(),   BorderLayout.CENTER);
        setContentPane(root);

        btnRegister.addActionListener(e -> { if (onRegister != null) onRegister.run(); });
        btnGoLogin .addActionListener(e -> { if (onGoLogin  != null) onGoLogin.run(); });
    }

    public void setOnRegister(Runnable r) { onRegister = r; }
    public void setOnGoLogin (Runnable r) { onGoLogin  = r; }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setBackground(COLOR_PRIMARY);
        p.setBorder(new EmptyBorder(22, 32, 22, 32));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Créer un compte");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(COLOR_WHITE);
        JLabel sub = new JLabel("Système de gestion de pharmacie");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(180, 210, 240));
        text.add(title);
        text.add(Box.createVerticalStrut(3));
        text.add(sub);

        p.add(text, BorderLayout.CENTER);
        return p;
    }

    // ── Form ──────────────────────────────────────────────────────────────────

    private JPanel buildForm() {
        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(COLOR_WHITE);
        inner.setBorder(new EmptyBorder(28, 40, 28, 40));

        // ── Row 1: ID + CIN side by side ──────────────────────────────────────
        txtId  = styledField("USR001");
        txtCin = styledField("12345678");
        inner.add(twoCol("Identifiant", txtId, "CIN", txtCin));
        inner.add(vs(14));

        // ── Row 2: Nom + Prénom side by side ──────────────────────────────────
        txtNom    = styledField("Nom");
        txtPrenom = styledField("Prénom");
        inner.add(twoCol("Nom", txtNom, "Prénom", txtPrenom));
        inner.add(vs(14));

        // ── Email ─────────────────────────────────────────────────────────────
        txtEmail = styledField("email@mail.com");
        inner.add(labeled("Email", txtEmail));
        inner.add(vs(14));

        // ── Password ──────────────────────────────────────────────────────────
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));
        txtPassword.setAlignmentX(LEFT_ALIGNMENT);
        txtPassword.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 220, 240), 1, true),
            new EmptyBorder(6, 10, 6, 10)));
        inner.add(labeled("Mot de passe", txtPassword));
        inner.add(vs(14));

        // ── Type ──────────────────────────────────────────────────────────────
        cmbType = new JComboBox<>(new String[]{"PHARMACIEN", "ADMIN"});
        cmbType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbType.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));
        cmbType.setAlignmentX(LEFT_ALIGNMENT);
        inner.add(labeled("Type d'utilisateur", cmbType));
        inner.add(vs(22));

        // ── Divider ───────────────────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(220, 230, 240));
        sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        inner.add(sep);
        inner.add(vs(20));

        // ── Buttons ───────────────────────────────────────────────────────────
        btnRegister = actionBtn("S'inscrire",                    COLOR_PRIMARY);
        btnGoLogin  = actionBtn("Se connecter",   COLOR_ACCENT);
        inner.add(btnRegister);
        inner.add(vs(10));
        inner.add(btnGoLogin);

        // Scroll so it stays usable on small screens
        JScrollPane scroll = new JScrollPane(inner,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COLOR_WHITE);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(COLOR_LIGHT);
        outer.add(scroll, BorderLayout.CENTER);
        return outer;
    }

    // ── Layout builders ───────────────────────────────────────────────────────

    /** Wraps a label + component vertically. */
    private JPanel labeled(String labelText, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(COLOR_WHITE);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Short.MAX_VALUE, 70));
        p.add(lbl(labelText));
        p.add(vs(5));
        p.add(field);
        return p;
    }

    /** Places two labeled fields side by side in a horizontal panel. */
    private JPanel twoCol(String lbl1, JTextField f1, String lbl2, JTextField f2) {
        JPanel col1 = new JPanel();
        col1.setLayout(new BoxLayout(col1, BoxLayout.Y_AXIS));
        col1.setBackground(COLOR_WHITE);
        col1.add(lbl(lbl1)); col1.add(vs(5)); col1.add(f1);

        JPanel col2 = new JPanel();
        col2.setLayout(new BoxLayout(col2, BoxLayout.Y_AXIS));
        col2.setBackground(COLOR_WHITE);
        col2.add(lbl(lbl2)); col2.add(vs(5)); col2.add(f2);

        // Use GridLayout so both columns share width equally
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setBackground(COLOR_WHITE);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 70));
        row.add(col1);
        row.add(col2);
        return row;
    }

    // ── Style helpers ─────────────────────────────────────────────────────────

    private JTextField styledField(String ph) {
        JTextField f = new JTextField(ph);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(Color.GRAY);
        f.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 220, 240), 1, true),
            new EmptyBorder(6, 10, 6, 10)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(ph)) { f.setText(""); f.setForeground(COLOR_TEXT); }
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(ph); f.setForeground(Color.GRAY); }
            }
        });
        return f;
    }

    private JButton actionBtn(String t, Color bg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(COLOR_WHITE); b.setBackground(bg);
        b.setOpaque(true); b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Short.MAX_VALUE, 44));
        b.setAlignmentX(LEFT_ALIGNMENT);
        return b;
    }

    private static JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(33, 97, 140));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private static Component vs(int h) { return Box.createVerticalStrut(h); }

    // ── Getters ───────────────────────────────────────────────────────────────

    private String clean(JTextField f, String ph) {
        String v = f.getText().trim();
        return v.equals(ph) ? "" : v;
    }

    public String getId()       { return clean(txtId,     "USR001"); }
    public String getCin()      { return clean(txtCin,    "12345678"); }
    public String getNom()      { return clean(txtNom,    "Nom"); }
    public String getPrenom()   { return clean(txtPrenom, "Prénom"); }
    public String getEmail()    { return clean(txtEmail,  "email@mail.com"); }
    public String getPassword() { return new String(txtPassword.getPassword()).trim(); }
    public String getTypee()    { return (String) cmbType.getSelectedItem(); }

    public void showError(String m)   { JOptionPane.showMessageDialog(this, m, "Erreur",  JOptionPane.ERROR_MESSAGE); }
    public void showSuccess(String m) { JOptionPane.showMessageDialog(this, m, "Succès",  JOptionPane.INFORMATION_MESSAGE); }
}