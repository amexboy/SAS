package eitex.sas.role;

import eitex.sas.role.*;
import eitex.sas.common.ExceptionLogger;
import eitex.sas.data.ServerConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import eitex.sas.common.NotFoundException;

/**
 * A model class with static fields to perform database actions of Role class.
 * Contains static methods that perform the database manipulations.
 *
 * @author Amanu
 */
public class RoleModel {

    /**
     * Takes an Role object and creates an entry for it in the database.
     *
     * @param role
     * @param registeredBy
     * @return
     */
    static boolean saveToDataBase(Role role, String registeredBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement cs = con.prepareCall("EXECUTE saveRole ?, ?, ?, ?");
            cs.setString(1, role.getRoleCode());
            cs.setString(2, role.getRoleName());
            cs.setString(3, role.getRoleDisc());
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
     * @param role
     * @param updatedBy
     * @return
     */
    static boolean updateDataBase(Role role, String updatedBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall("EXECUTE updateRole ?, ?, ?, ?");

            ps.setString(1, role.getRoleCode());
            ps.setString(2, role.getRoleName());
            ps.setString(3, role.getRoleDisc());
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
     * role.
     *
     * @param role
     * @param deletedBy
     * @return
     */
    static boolean delete(Role role, String deletedBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall(
                    "UPDATE Role "
                    + "SET deleted = 1, updatedBy = ? "
                    + "WHERE roleCode = ? ");

            ps.setString(1, deletedBy);
            ps.setString(2, role.getRoleCode());

            int eu = ps.executeUpdate();
            con.close();
            return eu > 0;
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }
/**
     * Recovers the database entry that is represented by the first parameter
     * role after it is deleted.
     *
     * @param role
     * @param deletedBy
     * @return
     */
    static boolean recover(Role role, String deletedBy) {
         try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall(
                    "UPDATE Role "
                    + "SET deleted = 0, updatedBy = ? "
                    + "WHERE roleCode = ? ");

            ps.setString(1, deletedBy);
            ps.setString(2, role.getRoleCode());

            int eu = ps.executeUpdate();
            con.close();
            return eu > 0;
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }
    /**
     * Gets fields of a role from database and constructs an Role object. This
     * method accesses the database and gets a role using roleCode of the role.
     * if there is no role that corresponds the roleCode in the database,
     * NotFoundException is thrown
     *
     * @param roleCode
     * @return
     */
    static Role getRole(String roleCode) throws NotFoundException {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement cs = con.prepareCall(
                    " SELECT * "
                    + "FROM Role "
                    + "WHERE roleCode = ? ");
            cs.setString(1, roleCode);

            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                String roleName = rs.getString("roleName");
                String roleDisc = rs.getString("roleDisc");

                return new Role(roleCode, roleName, roleDisc);
            } else {
                throw new NotFoundException();
            }

        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
            throw new NotFoundException("The unit you requested was not found in the database.");
        }
    }

    /**
     * Gets and returns ArrayList object that is composed of all the roles that
     * are registered in the database.
     *
     * @return
     */
    static java.util.ArrayList<Role> getAllRoles(boolean nonDeleted) {
        ArrayList<Role> roles = new ArrayList<>();
        try (Connection con = ServerConnection.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT * FROM Role WHERE "
                    + " deleted " + (nonDeleted ? "<> 1" : " = 1"));

            while (rs.next()) {
                String roleCode = rs.getString("roleCode");
                String roleName = rs.getString("roleName");
                String roleDisc = rs.getString("roleDisc");

                roles.add(new Role(roleCode, roleName, roleDisc));
            }

        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return roles;
    }

}
