package vue;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class LoginView extends JFrame {

    public static final Color COLOR_PRIMARY = new Color(33, 97, 140);
    public static final Color COLOR_ACCENT  = new Color(52, 152, 219);
    public static final Color COLOR_WHITE   = Color.WHITE;
    public static final Color COLOR_TEXT    = new Color(30, 39, 46);
    public static final Color COLOR_LIGHT   = new Color(236, 240, 241);

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnGoRegister;

    private Runnable onLogin, onGoRegister;

    public LoginView() {
        setTitle("Pharmacie — Connexion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 340);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_LIGHT);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildForm(),   BorderLayout.CENTER);
        setContentPane(root);

        // ── Events wired in the view ──────────────────────────────────────────
        btnLogin.addActionListener     (e -> { if (onLogin      != null) onLogin.run(); });
        btnGoRegister.addActionListener(e -> { if (onGoRegister != null) onGoRegister.run(); });
    }

    public void setOnLogin     (Runnable r) { onLogin      = r; }
    public void setOnGoRegister(Runnable r) { onGoRegister = r; }

    private JPanel buildHeader() {
        JPanel p = new JPanel();
        p.setBackground(COLOR_PRIMARY);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel lbl = new JLabel("Connexion");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(COLOR_WHITE);
        p.add(lbl);
        return p;
    }

    private JPanel buildForm() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(COLOR_WHITE);
        p.setBorder(new EmptyBorder(24, 32, 24, 32));

        txtEmail    = field(p, "Email",        "email@mail.com");
        txtPassword = passField(p, "Mot de passe");

        p.add(vs(16));
        btnLogin = actionBtn("Se connecter", COLOR_PRIMARY);
        p.add(btnLogin);
        p.add(vs(10));
        btnGoRegister = actionBtn("S'inscrire", COLOR_ACCENT);
        p.add(btnGoRegister);

        return p;
    }

    private JTextField field(JPanel form, String label, String ph) {
        form.add(lbl(label)); form.add(vs(4));
        JTextField f = new JTextField(ph);
        styleField(f, ph);
        form.add(f); form.add(vs(12));
        return f;
    }

    private JPasswordField passField(JPanel form, String label) {
        form.add(lbl(label)); form.add(vs(4));
        JPasswordField f = new JPasswordField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(new LineBorder(new Color(200,220,240),1,true), new EmptyBorder(6,10,6,10)));
        form.add(f); form.add(vs(12));
        return f;
    }

    private void styleField(JTextField f, String ph) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setForeground(Color.GRAY);
        f.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(new LineBorder(new Color(200,220,240),1,true), new EmptyBorder(6,10,6,10)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if (f.getText().equals(ph)) { f.setText(""); f.setForeground(COLOR_TEXT); } }
            public void focusLost (FocusEvent e)  { if (f.getText().isEmpty())  { f.setText(ph); f.setForeground(Color.GRAY); } }
        });
    }

    private JButton actionBtn(String t, Color bg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(COLOR_WHITE); b.setBackground(bg);
        b.setOpaque(true); b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Short.MAX_VALUE, 38));
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

    public String getEmail()    { String v = txtEmail.getText().trim(); return v.equals("email@mail.com") ? "" : v; }
    public String getPassword() { return new String(txtPassword.getPassword()).trim(); }

    public void showError(String m)   { JOptionPane.showMessageDialog(this, m, "Erreur",  JOptionPane.ERROR_MESSAGE); }
    public void showSuccess(String m) { JOptionPane.showMessageDialog(this, m, "Succès",  JOptionPane.INFORMATION_MESSAGE); }
}