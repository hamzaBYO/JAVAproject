package vue;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginView extends JFrame {

    private JTextField     txtEmail    = field("exemple@email.com");
    private JPasswordField txtPassword = new JPasswordField();
    private JButton        btnLogin    = btn("Se connecter", new Color(33,97,140), Color.WHITE);
    private JButton        btnGoRegister;

    private static final Color BLUE  = new Color(33, 97, 140);
    private static final Color LIGHT = new Color(214, 234, 248);

    public LoginView() {
        setTitle("Pharmacie - Connexion");
        setSize(420, 480);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(40, 46, 40, 46));

        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPassword.setHorizontalAlignment(JTextField.CENTER);
        txtPassword.setBorder(new CompoundBorder(new LineBorder(LIGHT, 1, true), new EmptyBorder(7, 10, 7, 10)));

        card.add(centered(label("Connexion", Font.BOLD, 22, BLUE)));
        card.add(Box.createVerticalStrut(24));

        card.add(centered(label("Email", Font.BOLD, 11, Color.DARK_GRAY)));        card.add(Box.createVerticalStrut(4));
        card.add(centeredField(txtEmail));                                           card.add(Box.createVerticalStrut(14));
        card.add(centered(label("Mot de passe", Font.BOLD, 11, Color.DARK_GRAY))); card.add(Box.createVerticalStrut(4));
        card.add(centeredField(txtPassword));                                        card.add(Box.createVerticalStrut(24));
        card.add(btnLogin);                                                     card.add(Box.createVerticalStrut(16));

        btnGoRegister = new JButton("S'inscrire");
        btnGoRegister.setForeground(BLUE);
        btnGoRegister.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGoRegister.setBorderPainted(false); btnGoRegister.setContentAreaFilled(false); btnGoRegister.setFocusPainted(false);
        btnGoRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel regRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        regRow.setBackground(Color.WHITE); regRow.setAlignmentX(CENTER_ALIGNMENT);
        JLabel q = new JLabel("Pas de compte ?");
        q.setFont(new Font("Segoe UI", Font.PLAIN, 12)); q.setForeground(Color.GRAY);
        regRow.add(q); regRow.add(btnGoRegister);
        card.add(regRow);

        JPanel bg = new JPanel(new GridBagLayout());
        bg.setBackground(BLUE);
        bg.add(card);
        setContentPane(bg);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static JTextField field(String ph) {
        JTextField f = new JTextField(ph);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setForeground(Color.GRAY);
        f.setHorizontalAlignment(JTextField.CENTER);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(new LineBorder(new Color(214,234,248),1,true), new EmptyBorder(7,10,7,10)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if (f.getText().equals(ph)) { f.setText(""); f.setForeground(Color.DARK_GRAY); } }
            public void focusLost (FocusEvent e)  { if (f.getText().isEmpty())  { f.setText(ph); f.setForeground(Color.GRAY); } }
        });
        return f;
    }

    private static JButton btn(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13)); b.setForeground(fg); b.setBackground(bg);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42)); b.setAlignmentX(LEFT_ALIGNMENT);
        return b;
    }

    private static JLabel label(String t, int style, int size, Color c) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(c); l.setAlignmentX(LEFT_ALIGNMENT); return l;
    }

    private static JPanel centered(JComponent c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setBackground(Color.WHITE); p.setAlignmentX(CENTER_ALIGNMENT); p.add(c); return p;
    }

    private static JPanel centeredField(JComponent c) {
        c.setPreferredSize(new Dimension(280, 40));
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setBackground(Color.WHITE); p.setAlignmentX(CENTER_ALIGNMENT); p.add(c); return p;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String  getEmail()         { String v = txtEmail.getText().trim(); return v.equals("exemple@email.com") ? "" : v; }
    public String  getPassword()      { return new String(txtPassword.getPassword()); }
    public JButton getBtnLogin()      { return btnLogin; }
    public JButton getBtnGoRegister() { return btnGoRegister; }
    public void showError(String m)   { JOptionPane.showMessageDialog(this, m, "Erreur",  JOptionPane.ERROR_MESSAGE); }
    public void showSuccess(String m) { JOptionPane.showMessageDialog(this, m, "Succès",  JOptionPane.INFORMATION_MESSAGE); }
}