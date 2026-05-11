package controlleur;

import modele.Utilisateur;
import vue.*;

import javax.swing.JOptionPane;

public class AdminDashboardController {

    private final Admindashboardview view;

    public AdminDashboardController(Admindashboardview view, Utilisateur currentUser) {
        this.view = view;
        view.getBtnMeds()   .addActionListener(e -> openMeds());
        view.getBtnUsers()  .addActionListener(e -> openUsers());
        view.getBtnClients().addActionListener(e -> openClients());
        view.getBtnLogout() .addActionListener(e -> logout());
    }

    private void openMeds() {
        MedView v = new MedView(false);   
        new MedController(v);
        v.setVisible(true);
    }

    private void openUsers() {
        UserView v = new UserView();
        new UserController(v);
        v.setVisible(true);
    }

    private void openClients() {
        ClientView v = new ClientView();
        new ClientController(v);
        v.setVisible(true);
    }

    private void logout() {
        if (view.confirm("Voulez-vous vraiment vous déconnecter ?") != JOptionPane.YES_OPTION) return;
        view.dispose();
        LoginView v = new LoginView();
        new LoginController(v);
        v.setVisible(true);
    }
}