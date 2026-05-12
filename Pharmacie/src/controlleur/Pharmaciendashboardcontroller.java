package controlleur;

import DAO.ClientDao;
import DAO.OrdonnanceDAO;
import modele.Client;
import modele.Ordonnance;
import modele.Utilisateur;
import vue.*;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Pharmaciendashboardcontroller {

    private final PharmacienDashboardView view;
    private final ClientDao      clientDAO     = new ClientDao();
    private final OrdonnanceDAO  ordonnanceDAO = new OrdonnanceDAO();

    public Pharmaciendashboardcontroller(PharmacienDashboardView view, Utilisateur currentUser) {
        this.view = view;
        view.getBtnClients().addActionListener(e -> openClients());
        view.getBtnMeds()   .addActionListener(e -> openMeds());
        view.getBtnOrd()    .addActionListener(e -> openOrdonnances());
        view.getBtnLogout() .addActionListener(e -> logout());
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private void openClients() {
        ClientView v = new ClientView();
        new ClientController(v);
        v.setVisible(true);
    }

    private void openMeds() {
        MedView v = new MedView(true);
        new MedController(v);
        v.setVisible(true);
    }

    // ── Ordonnance flow: ask client ID → choose new or existing ───────────────

    private void openOrdonnances() {
        List<Client> clients = clientDAO.findAll();
        if (clients.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Aucun client enregistré.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Labels construits ici (logique métier) puis passés à la View (affichage)
        String[] labels = clients.stream()
            .map(c -> c.getIdClient() + " — " + c.getNom() + " " + c.getPrenom()
                    + "  |  Tél : " + c.getTelephone())
            .toArray(String[]::new);

        int idx = view.chooseClient(labels);
        if (idx < 0) return;

        Client client      = clients.get(idx);
        String clientLabel = client.getNom() + " " + client.getPrenom()
                           + " (Tél : " + client.getTelephone() + ")";

        String[] choices = {"Nouvelle ordonnance", "Voir ordonnances existantes"};
        int opt = JOptionPane.showOptionDialog(
            view, "Client : " + clientLabel, "Que souhaitez-vous faire ?",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, choices, choices[0]);

        if      (opt == 0) openNewOrd(client, clientLabel);
        else if (opt == 1) openExistingOrd(client, clientLabel);
    }

    private void openNewOrd(Client client, String clientLabel) {
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String autoId  = "ORD-" + client.getIdClient() + "-"
                       + new SimpleDateFormat("yyyyMMdd").format(new Date())
                       + "-" + (System.currentTimeMillis() % 1000);

        OrdonnanceView v = new OrdonnanceView(autoId, dateStr, clientLabel);
        v.prefillNewOrd(autoId, client.getIdClient(), dateStr);
        new OrdonnanceController(v);   // null existing → nouvelle ordonnance
        v.setVisible(true);
    }

    private void openExistingOrd(Client client, String clientLabel) {
        List<Ordonnance> list = ordonnanceDAO.findByClient(client.getIdClient());
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(
                view, "Aucune ordonnance pour ce client.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] labels = list.stream()
            .map(o -> "N° " + o.getIdOrdonnance() + "  —  " + o.getDate())
            .toArray(String[]::new);

        int idx = view.chooseOrdonnance(labels);
        if (idx < 0) return;

        Ordonnance sel = list.get(idx);
        OrdonnanceView v = new OrdonnanceView(
            sel.getIdOrdonnance(), sel.getDate().toString(), clientLabel);
        new OrdonnanceController(v, sel);
        v.setVisible(true);
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    private void logout() {
        if (view.confirm("Voulez-vous vraiment vous déconnecter ?") != JOptionPane.YES_OPTION) return;
        view.dispose();
        LoginView v = new LoginView();
        new LoginController(v);
        v.setVisible(true);
    }
}