package DAO;

import modele.Client;
import outils.SingletonConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDao implements Idao<Client> {

    private Connection connection = SingletonConnection.getInstance();

    @Override
    public boolean create(Client c) {
        String sql = "INSERT INTO client (id_Client, nom, prenom, telephone, credit) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getIdClient());
            ps.setString(2, c.getNom());
            ps.setString(3, c.getPrenom());
            ps.setString(4, c.getTelephone());
            ps.setString(5, c.getCredit());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Client c) {
        String sql = "UPDATE client SET nom=?, prenom=?, telephone=?, credit=? WHERE id_Client=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getNom());
            ps.setString(2, c.getPrenom());
            ps.setString(3, c.getTelephone());
            ps.setString(4, c.getCredit());
            ps.setString(5, c.getIdClient());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM client WHERE id_Client=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Client findById(String id) {
        String sql = "SELECT * FROM client WHERE id_Client=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Client(
                    rs.getString("id_Client"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("telephone"),
                    rs.getString("credit")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Client> findAll() {
        List<Client> list = new ArrayList<>();
        String sql = "SELECT * FROM client";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Client(
                    rs.getString("id_Client"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("telephone"),
                    rs.getString("credit")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}