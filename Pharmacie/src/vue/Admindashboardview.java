package vue;
import javax.swing.*;
import java.awt.*;

public class Admindashboardview extends JFrame {

    private JButton btnMeds, btnUsers, btnClients, btnLogout;

    public Admindashboardview() {
        setTitle("Pharmacie — Administration");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 380); setLocationRelativeTo(null); setResizable(false);

        // ── Header ────────────────────────────────────────────────────────────
        JLabel header = new JLabel("Espace Administrateur", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setOpaque(true); header.setBackground(new Color(28, 78, 115));
        header.setForeground(Color.WHITE); header.setPreferredSize(new Dimension(0, 70));

        // ── 2×2 button grid ───────────────────────────────────────────────────
        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        grid.setBackground(new Color(236, 240, 241));
        grid.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        btnMeds    = bigBtn("Gérer Médicaments",  new Color(39, 174, 96));
        btnUsers   = bigBtn("Gérer Utilisateurs",  new Color(33, 97, 140));
        btnClients = bigBtn("Gérer Clients",        new Color(52, 152, 219));
        btnLogout  = bigBtn("Déconnexion",          new Color(192, 57, 43));
        grid.add(btnMeds); grid.add(btnUsers);
        grid.add(btnClients); grid.add(btnLogout);

        JPanel root = new JPanel(new BorderLayout());
        root.add(header, BorderLayout.NORTH);
        root.add(grid,   BorderLayout.CENTER);
        setContentPane(root);
    }

    private JButton bigBtn(String text, Color color) {
        JButton b = new JButton(text );
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setBackground(color); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public JButton getBtnMeds()    { return btnMeds; }
    public JButton getBtnUsers()   { return btnUsers; }
    public JButton getBtnClients() { return btnClients; }
    public JButton getBtnLogout()  { return btnLogout; }

    public int confirm(String m) {
        return JOptionPane.showConfirmDialog(this, m, "Confirmation", JOptionPane.YES_NO_OPTION);
    }
}