package eitex.sas.user;

import eitex.sas.address.Address;
import eitex.sas.coc.*;
import eitex.sas.common.ExceptionLogger;
import eitex.sas.common.NotFoundException;
import eitex.sas.data.ServerConnection;
import eitex.sas.role.Role;
import eitex.sas.role.RoleFieldException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * A model class that performs database actions of the User class.
 *
 * @author Amanu
 */
class UserModel {

    static boolean saveToDataBase(User us, String registeredBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall("EXECUTE saveUser ?,?,?,?,?,?,?");
            ps.setString(1, us.getUserName());
            ps.setString(2, us.getFirstName());
            ps.setString(3, us.getLastName());
            if (us.getCoc() != null) {
                ps.setString(4, us.getCoc().getCoCCode());
            } else {
                ps.setString(4, "NULL");
            }
            ps.setString(5, us.getPassword());
            ps.setInt(6, us.getAddress().getId());
            ps.setString(7, registeredBy);

            int eu = ps.executeUpdate();

            return eu > 0;
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }

    static boolean updateDataBase(User us, String updatedBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall("EXECUTE updateUser ?,?,?,?,?" + (us.getPassword().isEmpty() ? "" : ",?"));
            ps.setString(1, us.getUserName());
            ps.setString(2, us.getFirstName());
            ps.setString(3, us.getLastName());
            ps.setString(4, us.getCoc().getCoCCode());
            ps.setString(5, updatedBy);
            if (!us.getPassword().isEmpty()) {
                ps.setString(6, us.getPassword());
            }
            int eu = ps.executeUpdate();
            con.close();
            return eu > 0;
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }

    static boolean delete(User us, String deletedBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall("EXECUTE deleteUser ?,?");
            ps.setString(1, us.getUserName());
            ps.setString(2, deletedBy);

            int eu = ps.executeUpdate();
            con.close();
            return eu > 0;
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }

    static ArrayList<User> getAllUsers(boolean nonDeleted) {
        ArrayList<User> users = new ArrayList<>();
        try (Connection con = ServerConnection.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT u.firstName firstName,u.userName userName, u.lastName lastName, u.cocCode cocCode, u.password password, ua.id id "
                    + "FROM [User] u INNER JOIN User_Address ua ON u.userName = ua.userName "
                    + "WHERE deleted " + (nonDeleted ? "<> 1" : "= 1"));

            while (rs.next()) {
                String userName = rs.getString("userName");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String cocCode = rs.getString("cocCode");
                String password = rs.getString("password");
                int id = rs.getInt("id");
                Address address = new Address(id);
                CoC coc = new CoC(cocCode);

                users.add(new User(userName, password, firstName, lastName, coc, address));
            }
            return users;
        } catch (SQLException | NotFoundException ex) {
            ExceptionLogger.log(ex);
        }
        return users;
    }

    static User getUser(String userName) throws NotFoundException {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement cs = con.prepareCall(
                    "SELECT u.firstName firstName, u.lastName lastName, u.cocCode cocCode, u.password password, ua.id id "
                    + "FROM [User] u INNER JOIN User_Address ua ON u.userName = ua.userName "
                    + "WHERE u.userName = ? ");
            cs.setString(1, userName);

            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String cocCode = rs.getString("cocCode");
                String password = rs.getString("password");
                int id = rs.getInt("id");
                Address address = new Address(id);
                CoC coc = new CoC(cocCode);
                return new User(userName, password, firstName, lastName, coc, address);
            } else {
                throw new NotFoundException("User");
            }
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
            throw new NotFoundException("User");
        }
    }

    static boolean checkLogin(String userName, String password) {
        try (Connection con = ServerConnection.getConnection()) {
            PreparedStatement cs = con.prepareStatement("SELECT * FROM  [User] WHERE userName = ? AND password = ?");
            cs.setString(1, userName);
            cs.setString(2, password);

            ResultSet rs = cs.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }

    static ArrayList<Role> getUserRoles(User user) throws RoleFieldException {
        ArrayList<Role> roles = new ArrayList<>();
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement cs = con.prepareCall("EXECUTE getUserRoles ?");
            cs.setString(1, user.getUserName());

            ResultSet rs = cs.executeQuery();
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

    static boolean assignRoles(User us, ArrayList<Role> roles) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement cs2 = con.prepareCall("EXECUTE removeAllRoles ?");
            cs2.setString(1, us.getUserName());

            cs2.executeUpdate();
            for (Role r : roles) {
                CallableStatement cs = con.prepareCall("EXECUTE addRole ?, ? ");
                cs.setString(1, us.getUserName());
                cs.setString(2, r.getRoleCode());

                cs.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }

    static boolean recover(User user, String recoveredBy) {
         try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall(
                    "UPDATE [User] "
                    + "SET deleted = 0, updatedBy = ? "
                    + "WHERE userName = ? ");

            ps.setString(1, recoveredBy);
            ps.setString(2, user.getUserName());

            int eu = ps.executeUpdate();
            return eu > 0;
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }
}
