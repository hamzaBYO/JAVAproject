package DAO;

import modele.Medicament;
import outils.SingletonConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentDAO implements Idao<Medicament> {

    private Connection connection = SingletonConnection.getInstance();

    @Override
    public boolean create(Medicament m) {
        String sql = "INSERT INTO medicament (id_Medicament, nom, prix, quantite_stock, type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, m.getIdMedicament());
            ps.setString(2, m.getNom());
            ps.setDouble(3, m.getPrix());
            ps.setInt(4, m.getQuantiteStock());
            ps.setString(5, m.getType());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Medicament m) {
        String sql = "UPDATE medicament SET nom=?, prix=?, quantite_stock=?, type=? WHERE id_Medicament=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, m.getNom());
            ps.setDouble(2, m.getPrix());
            ps.setInt(3, m.getQuantiteStock());
            ps.setString(4, m.getType());
            ps.setString(5, m.getIdMedicament());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM medicament WHERE id_Medicament=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Medicament findById(String id) {
        String sql = "SELECT * FROM medicament WHERE id_Medicament=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Medicament(
                    rs.getString("id_Medicament"),
                    rs.getString("nom"),
                    rs.getDouble("prix"),
                    rs.getInt("quantite_stock"),
                    rs.getString("type")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Medicament> findAll() {
        List<Medicament> list = new ArrayList<>();
        String sql = "SELECT * FROM medicament";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Medicament(
                    rs.getString("id_Medicament"),
                    rs.getString("nom"),
                    rs.getDouble("prix"),
                    rs.getInt("quantite_stock"),
                    rs.getString("type")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStock(String idMedicament, int newQuantite) {
        String sql = "UPDATE medicament SET quantite_stock=? WHERE id_Medicament=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newQuantite);
            ps.setString(2, idMedicament);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}