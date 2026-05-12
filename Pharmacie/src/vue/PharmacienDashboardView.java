package vue;
import javax.swing.*;
import java.awt.*;

public class PharmacienDashboardView extends JFrame {

    private JButton btnClients, btnMeds, btnOrd, btnLogout;

    public PharmacienDashboardView() {
        setTitle("Pharmacie — Espace Pharmacien");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 380); setLocationRelativeTo(null); setResizable(false);

        // ── Header ────────────────────────────────────────────────────────────
        JLabel header = new JLabel("Espace Pharmacien", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setOpaque(true); header.setBackground(new Color(22, 90, 76));
        header.setForeground(Color.WHITE); header.setPreferredSize(new Dimension(0, 70));

        // ── 2×2 button grid ───────────────────────────────────────────────────
        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        grid.setBackground(new Color(232, 248, 244));
        grid.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        btnClients = bigBtn("Consulter Clients",     new Color(33, 150, 120));
        btnMeds    = bigBtn("Consulter Médicaments", new Color(52, 152, 219));
        btnOrd     = bigBtn("Gérer Ordonnances",     new Color(142, 68, 173));
        btnLogout  = bigBtn("Déconnexion",            new Color(192, 57, 43));
        grid.add(btnClients); grid.add(btnMeds);
        grid.add(btnOrd);     grid.add(btnLogout);

        JPanel root = new JPanel(new BorderLayout());
        root.add(header, BorderLayout.NORTH);
        root.add(grid,   BorderLayout.CENTER);
        setContentPane(root);
    }

    private JButton bigBtn(String text, Color color) {
        JButton b = new JButton( text );
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setBackground(color); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public JButton getBtnClients() { return btnClients; }
    public JButton getBtnMeds()    { return btnMeds; }
    public JButton getBtnOrd()     { return btnOrd; }
    public JButton getBtnLogout()  { return btnLogout; }

    public int confirm(String m) {
        return JOptionPane.showConfirmDialog(this, m, "Confirmation", JOptionPane.YES_NO_OPTION);
    }

    /**
     * Affiche une boîte de dialogue avec un ComboBox listant tous les clients.
     * Retourne l'index sélectionné, ou -1 si annulé.
     */
    public int chooseClient(String[] clientLabels) {
        JComboBox<String> combo = new JComboBox<>(clientLabels);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setPreferredSize(new Dimension(380, 32));

        JLabel lbl = new JLabel("Sélectionnez un client :");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));
        panel.add(lbl,   BorderLayout.NORTH);
        panel.add(combo, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
            this, panel, "Choisir un client",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        return result == JOptionPane.OK_OPTION ? combo.getSelectedIndex() : -1;
    }

    /**
     * Affiche une boîte de dialogue avec un ComboBox listant les ordonnances d'un client.
     * Retourne l'index sélectionné, ou -1 si annulé.
     */
    public int chooseOrdonnance(String[] ordLabels) {
        JComboBox<String> combo = new JComboBox<>(ordLabels);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setPreferredSize(new Dimension(380, 32));

        JLabel lbl = new JLabel("Sélectionnez une ordonnance :");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));
        panel.add(lbl,   BorderLayout.NORTH);
        panel.add(combo, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
            this, panel, "Choisir une ordonnance",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        return result == JOptionPane.OK_OPTION ? combo.getSelectedIndex() : -1;
    }
}