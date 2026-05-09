package vue;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterView extends JFrame {

    private JTextField     txtId       = field("ID utilisateur (ex: USR001)");
    private JTextField     txtCin      = field("CIN (8 chiffres)");
    private JTextField     txtNom      = field("Nom");
    private JTextField     txtPrenom   = field("Prénom");
    private JTextField     txtEmail    = field("Email");
    private JPasswordField txtPassword = pwd();
    private JComboBox<String> cmbType  = combo();
    private JButton        btnRegister = btn("S'inscrire");
    private JButton        btnGoLogin;

    private static final Color BLUE  = new Color(33, 97, 140);
    private static final Color LIGHT = new Color(214, 234, 248);
    private static final Color GREEN = new Color(39, 174, 96);

    public RegisterView() {
        setTitle("Pharmacie - Inscription");
        setSize(440, 640);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ── Card ──────────────────────────────────────────────────────────────
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(24, 38, 24, 38));

        // Header
        card.add(centered(lbl("Créer un compte", Font.BOLD, 20, BLUE)));
        card.add(vs(14));
        JSeparator sep = new JSeparator();
        sep.setForeground(LIGHT);
        sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        card.add(sep);
        card.add(vs(12));

        // Champs via boucle
        for (Object[] row : new Object[][]{
            {"ID",           txtId},
            {"CIN",          txtCin},
            {"Nom",          txtNom},
            {"Prénom",       txtPrenom},
            {"Email",        txtEmail},
            {"Mot de passe", txtPassword},
            {"Type",         cmbType}
        }) {
            card.add(lbl((String) row[0], Font.BOLD, 11, Color.DARK_GRAY));
            card.add(vs(3));
            card.add((JComponent) row[1]);
            card.add(vs(8));
        }

        card.add(vs(8));
        card.add(btnRegister);
        card.add(vs(10));

        // Lien login
        btnGoLogin = new JButton("Se connecter");
        btnGoLogin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGoLogin.setForeground(BLUE);
        btnGoLogin.setBorderPainted(false); btnGoLogin.setContentAreaFilled(false);
        btnGoLogin.setFocusPainted(false);
        btnGoLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel q = new JLabel("Déjà un compte ? ");
        q.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        q.setForeground(Color.GRAY);

        JPanel loginRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        loginRow.setBackground(Color.WHITE);
        loginRow.setAlignmentX(CENTER_ALIGNMENT);
        loginRow.add(q); loginRow.add(btnGoLogin);
        card.add(loginRow);

        // ── Scroll autour de la carte ─────────────────────────────────────────
        JScrollPane scroll = new JScrollPane(card,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        // ── Background bleu en BorderLayout ───────────────────────────────────
        JPanel bg = new JPanel(new BorderLayout());
        bg.setBackground(BLUE);
        bg.setBorder(new EmptyBorder(30, 30, 30, 30));
        bg.add(scroll, BorderLayout.CENTER);

        setContentPane(bg);
    }

    // ── Factories ─────────────────────────────────────────────────────────────

    private static JTextField field(String ph) {
        JTextField f = new JTextField(ph);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13)); f.setForeground(Color.GRAY);
        f.setMaximumSize(new Dimension(Short.MAX_VALUE, 36)); f.setAlignmentX(LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(new LineBorder(new Color(214,234,248),1,true), new EmptyBorder(5,10,5,10)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if (f.getText().equals(ph)) { f.setText(""); f.setForeground(Color.DARK_GRAY); } }
            public void focusLost (FocusEvent e) { if (f.getText().isEmpty())   { f.setText(ph); f.setForeground(Color.GRAY); } }
        });
        return f;
    }

    private static JPasswordField pwd() {
        JPasswordField f = new JPasswordField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setMaximumSize(new Dimension(Short.MAX_VALUE, 36)); f.setAlignmentX(LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(new LineBorder(new Color(214,234,248),1,true), new EmptyBorder(5,10,5,10)));
        return f;
    }

    private static JComboBox<String> combo() {
        JComboBox<String> c = new JComboBox<>(new String[]{"PHARMACIEN", "ADMIN"});
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13)); c.setBackground(Color.WHITE);
        c.setMaximumSize(new Dimension(Short.MAX_VALUE, 36)); c.setAlignmentX(LEFT_ALIGNMENT);
        return c;
    }

    private static JButton btn(String t) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13)); b.setForeground(Color.WHITE);
        b.setBackground(new Color(39,174,96)); b.setOpaque(true);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Short.MAX_VALUE, 40)); b.setAlignmentX(LEFT_ALIGNMENT);
        return b;
    }

    private static JLabel lbl(String t, int style, int size, Color c) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(c); l.setAlignmentX(LEFT_ALIGNMENT); return l;
    }

    private static JLabel icon(String e, int size) {
        return new JLabel(e) {{ setFont(new Font("Segoe UI Emoji", Font.PLAIN, size)); }};
    }

    private static JPanel centered(JComponent c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setBackground(Color.WHITE); p.setAlignmentX(CENTER_ALIGNMENT); p.add(c); return p;
    }

    private static Component vs(int h) { return Box.createVerticalStrut(h); }

    // ── Getters ───────────────────────────────────────────────────────────────

    private String clean(JTextField f, String ph) { String v = f.getText().trim(); return v.equals(ph) ? "" : v; }

    public String getId()       { return clean(txtId,     "ID utilisateur (ex: USR001)"); }
    public String getCin()      { return clean(txtCin,    "CIN (8 chiffres)"); }
    public String getNom()      { return clean(txtNom,    "Nom"); }
    public String getPrenom()   { return clean(txtPrenom, "Prénom"); }
    public String getEmail()    { return clean(txtEmail,  "Email"); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public String getTypee()    { return (String) cmbType.getSelectedItem(); }

    public JButton getBtnRegister() { return btnRegister; }
    public JButton getBtnGoLogin()  { return btnGoLogin; }
    public void showError(String m)   { JOptionPane.showMessageDialog(this, m, "Erreur",  JOptionPane.ERROR_MESSAGE); }
    public void showSuccess(String m) { JOptionPane.showMessageDialog(this, m, "Succès",  JOptionPane.INFORMATION_MESSAGE); }
}