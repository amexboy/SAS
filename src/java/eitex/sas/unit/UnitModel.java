package eitex.sas.unit;

import eitex.sas.common.ExceptionLogger;
import eitex.sas.data.ServerConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.ws.rs.NotFoundException;

/**
 * A model class with static fields to perform database actions of Unit class.
 * Contains static methods that perform the database manipulations.
 *
 * @author Amanu
 */
public class UnitModel {

    /**
     * Takes an Unit object and creates an entry for it in the database.
     *
     * @param unit
     * @param registeredBy
     * @return
     */
    static boolean saveToDataBase(Unit unit, String registeredBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement cs = con.prepareCall("EXECUTE saveUnit ?, ?, ?, ?");
            cs.setString(1, unit.getUnitCode());
            cs.setString(2, unit.getUnitName());
            cs.setString(3, unit.getUnitDisc());
            cs.setString(4, registeredBy);

            int eu = cs.executeUpdate();
            return (eu > 0);
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }

    /**
     * Updates a database entry. It updates all fields weather changed or not.
     *
     * @param unit
     * @param updatedBy
     * @return
     */
    static boolean updateDataBase(Unit unit, String updatedBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall("EXECUTE updateUnit ?, ?, ?, ?");

            ps.setString(1, unit.getUnitCode());
            ps.setString(2, unit.getUnitName());
            ps.setString(3, unit.getUnitDisc());
            ps.setString(4, updatedBy);

            int eu = ps.executeUpdate();
            return eu > 0;
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }

    /**
     * Deletes the database entry that is represented by the first parameter
     * unit.
     *
     * @param unit
     * @param deletedBy
     * @return
     */
    static boolean delete(Unit unit, String deletedBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall("EXECUTE deleteUnit ?, ?");

            ps.setString(1, unit.getUnitCode());
            ps.setString(2, deletedBy);

            int eu = ps.executeUpdate();
            con.close();
            return eu > 0;
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }

    /**
     * Gets fields of a unit from database and constructs an Unit object. This
     * method accesses the database and gets a unit using unitCode of the unit.
     * if there is no unit that corresponds the unitCode in the database,
     * NotFoundException is thrown
     *
     * @param unitCode
     * @return
     */
    static Unit getUnit(String unitCode) throws NotFoundException {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement cs = con.prepareCall("getUnit ? ");
            cs.setString(1, unitCode);

            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                String unitName = rs.getString("unitName");
                String unitDisc = rs.getString("unitDisc");

                return new Unit(unitCode, unitName, unitDisc);
            } else {
                throw new NotFoundException();
            }

        } catch (SQLException | UnitFieldException ex) {
            ExceptionLogger.log(ex);
            throw new NotFoundException();
        }
    }

    /**
     * Gets and returns ArrayList object that is composed of all the units that
     * are registered in the database.
     *
     * @return
     */
    static java.util.ArrayList<Unit> getAllUnits() {
        ArrayList<Unit> units = new ArrayList<>();
        try (Connection con = ServerConnection.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("EXECUTE getAllUnit");

            while (rs.next()) {
                String unitCode = rs.getString("unitCode");
                String unitName = rs.getString("unitName");
                String unitDisc = rs.getString("unitDisc");

                units.add(new Unit(unitCode, unitName, unitDisc));
            }

        } catch (SQLException | UnitFieldException ex) {
            ExceptionLogger.log(ex);
        }
        return units;
    }

}
