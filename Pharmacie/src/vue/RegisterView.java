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

    private JTextField    txtId, txtCin, txtNom, txtPrenom, txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbType;
    private JButton btnRegister, btnGoLogin;

    private Runnable onRegister, onGoLogin;

    public RegisterView() {
        setTitle("Pharmacie — Inscription");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(236, 240, 241));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildForm(),   BorderLayout.CENTER);
        setContentPane(root);

        // ── Events wired in the view ──────────────────────────────────────────
        btnRegister.addActionListener(e -> { if (onRegister != null) onRegister.run(); });
        btnGoLogin .addActionListener(e -> { if (onGoLogin  != null) onGoLogin.run(); });
    }

    public void setOnRegister(Runnable r) { onRegister = r; }
    public void setOnGoLogin (Runnable r) { onGoLogin  = r; }

    private JPanel buildHeader() {
        JPanel p = new JPanel();
        p.setBackground(COLOR_PRIMARY);
        p.setBorder(new EmptyBorder(18, 20, 18, 20));
        JLabel lbl = new JLabel("Créer un compte");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(COLOR_WHITE);
        p.add(lbl);
        return p;
    }

    private JPanel buildForm() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(COLOR_WHITE);
        p.setBorder(new EmptyBorder(20, 32, 20, 32));

        txtId       = field(p, "Identifiant",    "USR001");
        txtCin      = field(p, "CIN",            "12345678");
        txtNom      = field(p, "Nom",            "Nom");
        txtPrenom   = field(p, "Prénom",         "Prénom");
        txtEmail    = field(p, "Email",          "email@mail.com");
        txtPassword = passField(p, "Mot de passe");

        p.add(lbl("Type")); p.add(vs(4));
        cmbType = new JComboBox<>(new String[]{"PHARMACIEN", "ADMIN"});
        cmbType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbType.setMaximumSize(new Dimension(Short.MAX_VALUE, 34));
        cmbType.setAlignmentX(LEFT_ALIGNMENT);
        p.add(cmbType); p.add(vs(16));

        btnRegister = actionBtn("S'inscrire",         COLOR_PRIMARY);
        btnGoLogin  = actionBtn("Déjà inscrit ? Se connecter", COLOR_ACCENT);
        p.add(btnRegister); p.add(vs(8)); p.add(btnGoLogin);
        return p;
    }

    private JTextField field(JPanel form, String label, String ph) {
        form.add(lbl(label)); form.add(vs(4));
        JTextField f = new JTextField(ph);
        styleField(f, ph);
        form.add(f); form.add(vs(10));
        return f;
    }

    private JPasswordField passField(JPanel form, String label) {
        form.add(lbl(label)); form.add(vs(4));
        JPasswordField f = new JPasswordField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setMaximumSize(new Dimension(Short.MAX_VALUE, 34));
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(new LineBorder(new Color(200,220,240),1,true), new EmptyBorder(4,8,4,8)));
        form.add(f); form.add(vs(10));
        return f;
    }

    private void styleField(JTextField f, String ph) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setForeground(Color.GRAY);
        f.setMaximumSize(new Dimension(Short.MAX_VALUE, 34));
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(new LineBorder(new Color(200,220,240),1,true), new EmptyBorder(4,8,4,8)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if (f.getText().equals(ph)) { f.setText(""); f.setForeground(COLOR_TEXT); } }
            public void focusLost (FocusEvent e)  { if (f.getText().isEmpty())  { f.setText(ph); f.setForeground(Color.GRAY); } }
        });
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

    private static JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(new Color(33, 97, 140));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }
    private static Component vs(int h) { return Box.createVerticalStrut(h); }

    private String clean(JTextField f, String ph) { String v = f.getText().trim(); return v.equals(ph) ? "" : v; }

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