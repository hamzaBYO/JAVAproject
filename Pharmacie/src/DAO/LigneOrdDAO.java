package DAO;

import modele.LigneOrd;
import modele.Medicament;
import modele.Ordonnance;
import outils.SingletonConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LigneOrdDAO {

    private Connection connection = SingletonConnection.getInstance();
    private OrdonnanceDAO ordonnanceDAO = new OrdonnanceDAO();
    private MedicamentDAO medicamentDAO = new MedicamentDAO();

    public boolean create(LigneOrd l) {
        String sql = "INSERT INTO ligne_ord (id_Ordonnance, id_Medicament, quantite) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, l.getIdOrdonnance().getIdOrdonnance());
            ps.setString(2, l.getIdMedicament().getIdMedicament());
            ps.setInt(3, l.getQuantite());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(LigneOrd l) {
        String sql = "UPDATE ligne_ord SET quantite=? WHERE id_Ordonnance=? AND id_Medicament=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, l.getQuantite());
            ps.setString(2, l.getIdOrdonnance().getIdOrdonnance());
            ps.setString(3, l.getIdMedicament().getIdMedicament());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String idOrdonnance, String idMedicament) {
        String sql = "DELETE FROM ligne_ord WHERE id_Ordonnance=? AND id_Medicament=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, idOrdonnance);
            ps.setString(2, idMedicament);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<LigneOrd> findByOrdonnance(String idOrdonnance) {
        List<LigneOrd> list = new ArrayList<>();
        String sql = "SELECT * FROM ligne_ord WHERE id_Ordonnance=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, idOrdonnance);
            ResultSet rs = ps.executeQuery();
            Ordonnance ordonnance = ordonnanceDAO.findById(idOrdonnance);
            while (rs.next()) {
                Medicament medicament = medicamentDAO.findById(rs.getString("id_Medicament"));
                list.add(new LigneOrd(ordonnance, medicament, rs.getInt("quantite")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<LigneOrd> findAll() {
        List<LigneOrd> list = new ArrayList<>();
        String sql = "SELECT * FROM ligne_ord";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Ordonnance ordonnance = ordonnanceDAO.findById(rs.getString("id_Ordonnance"));
                Medicament medicament = medicamentDAO.findById(rs.getString("id_Medicament"));
                list.add(new LigneOrd(ordonnance, medicament, rs.getInt("quantite")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}