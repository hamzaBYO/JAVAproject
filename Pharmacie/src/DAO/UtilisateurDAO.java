package DAO;

import modele.Utilisateur;
import outils.SingletonConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO implements Idao<Utilisateur> {

    private Connection connection = SingletonConnection.getInstance();

    @Override
    public boolean create(Utilisateur u) {
        String sql = "INSERT INTO utilisateur (id, cin, nom, prenom, email, password, type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, u.getId());
            ps.setString(2, u.getCin());
            ps.setString(3, u.getNom());
            ps.setString(4, u.getPrenom());
            ps.setString(5, u.getEmail());
            ps.setString(6, u.getPassword());
            ps.setString(7, u.getType());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Utilisateur u) {
        String sql = "UPDATE utilisateur SET cin=?, nom=?, prenom=?, email=?, password=?, type=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, u.getCin());
            ps.setString(2, u.getNom());
            ps.setString(3, u.getPrenom());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getPassword());
            ps.setString(6, u.getType());
            ps.setString(7, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM utilisateur WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Utilisateur findById(String id) {
        String sql = "SELECT * FROM utilisateur WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Utilisateur(
                    rs.getString("id"),
                    rs.getString("cin"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("type")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Utilisateur> findAll() {
        List<Utilisateur> list = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Utilisateur(
                    rs.getString("id"),
                    rs.getString("cin"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("type")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Extra: login authentication
    public Utilisateur authenticate(String email, String password) {
        String sql = "SELECT * FROM utilisateur WHERE email=? AND password=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Utilisateur(
                    rs.getString("id"),
                    rs.getString("cin"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("type")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}