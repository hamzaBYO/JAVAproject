package DAO;

import modele.Client;
import modele.Ordonnance;
import outils.SingletonConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceDAO implements Idao<Ordonnance> {

    private Connection connection = SingletonConnection.getInstance();
    private ClientDao clientDAO = new ClientDao();

    @Override
    public boolean create(Ordonnance o) {
        String sql = "INSERT INTO ordonnance (id_Ordonnance, date, id_Client) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, o.getIdOrdonnance());
            ps.setDate(2, new java.sql.Date(o.getDate().getTime()));
            ps.setString(3, o.getIdClient().getIdClient());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Ordonnance o) {
        String sql = "UPDATE ordonnance SET date=?, id_Client=? WHERE id_Ordonnance=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(o.getDate().getTime()));
            ps.setString(2, o.getIdClient().getIdClient());
            ps.setString(3, o.getIdOrdonnance());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM ordonnance WHERE id_Ordonnance=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Ordonnance findById(String id) {
        String sql = "SELECT * FROM ordonnance WHERE id_Ordonnance=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Client client = clientDAO.findById(rs.getString("id_Client"));
                return new Ordonnance(
                    rs.getString("id_Ordonnance"),
                    rs.getDate("date"),
                    client
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Ordonnance> findAll() {
        List<Ordonnance> list = new ArrayList<>();
        String sql = "SELECT * FROM ordonnance";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Client client = clientDAO.findById(rs.getString("id_Client"));
                list.add(new Ordonnance(
                    rs.getString("id_Ordonnance"),
                    rs.getDate("date"),
                    client
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Extra: get all ordonnances for a specific client
    public List<Ordonnance> findByClient(String idClient) {
        List<Ordonnance> list = new ArrayList<>();
        String sql = "SELECT * FROM ordonnance WHERE id_Client=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, idClient);
            ResultSet rs = ps.executeQuery();
            Client client = clientDAO.findById(idClient);
            while (rs.next()) {
                list.add(new Ordonnance(
                    rs.getString("id_Ordonnance"),
                    rs.getDate("date"),
                    client
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}