package eitex.sas.address;

import eitex.sas.common.ExceptionLogger;
import eitex.sas.data.ServerConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import eitex.sas.common.NotFoundException;

/**
 * A model class with static fields to perform database actions of Address
 * class. Contains static methods that perform the database manipulations.
 *
 * @author Amanu
 */
public class AddressModel {

    /**
     * Gets fields of an address from database and constructs an Address object.
     * This method accesses the database and gets an address using id of the
     * address. if there is no address that corresponds the id in the database,
     * NotFoundException is thrown
     *
     * @param id
     * @return
     */
    static Address getAddress(int id) throws NotFoundException {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall("EXECUTE getAddress ?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String officeBdg = rs.getString("officeBdg");
                String officeNumber = rs.getString("officeNumber");
                String officePhoneNumber = rs.getString("officePhoneNumber");
                String mobilePhoneNumber = rs.getString("mobilePhoneNumber");
                String email = rs.getString("email");
                return new Address(id, officeBdg, officeNumber, mobilePhoneNumber, officePhoneNumber, email);
            } else {
                throw new NotFoundException("Address was not found in the database.");
            }
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
            throw new NotFoundException("Address was not found in the database.");
        }
    }

    /**
     * Takes an Unit object and creates an entry for it in the database.
     *
     * @param address
     * @param registeredBy
     * @return
     */
    static int saveToDataBase(Address address, String registeredBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall("EXECUTE saveAddress ?,?,?,?,?,?");

            ps.setString(1, address.getOfficeBuilding());
            ps.setString(2, address.getOfficeNumber());
            ps.setString(3, address.getMobilePhoneNumber());
            ps.setString(4, address.getOfficePhoneNumber());
            ps.setString(5, address.getEmail());
            ps.setString(6, registeredBy);

            int eu = ps.executeUpdate();
            if (eu > 0) {
                Statement s = con.createStatement();
                ResultSet rs = s.executeQuery("SELECT IDENT_CURRENT('Address') as lastId");
                if (rs.next()) {
                    return rs.getInt("lastId");
                }
            }
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return 0;
    }

    /**
     * Updates a database entry. It updates all fields weather changed or not.
     *
     * @param address
     * @param updatedBy
     * @return
     */
    static int updateDataBase(Address address, String updatedBy) {
        try (Connection con = ServerConnection.getConnection()) {
            String sql = "updateAddress ?,?,?,?,?,?,?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, address.getId());
            ps.setString(2, address.getOfficeBuilding());
            ps.setInt(3, Integer.parseInt(address.getOfficeNumber()));
            ps.setString(4, address.getMobilePhoneNumber());
            ps.setString(5, address.getOfficePhoneNumber());
            ps.setString(6, address.getEmail());
            ps.setString(7, updatedBy);

            int eu = ps.executeUpdate();
            return eu;
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return 0;
    }

}
