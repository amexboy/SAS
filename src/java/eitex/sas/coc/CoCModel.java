package eitex.sas.coc;

import eitex.sas.common.ExceptionLogger;
import eitex.sas.common.NotFoundException;
import eitex.sas.data.ServerConnection;
import eitex.sas.user.User;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A model class with static fields to perform database actions of CoC class.
 * Contains static methods that perform the database manipulations.
 *
 * @author Amanu
 */
public class CoCModel {

    /**
     * Takes an CoC object and creates an entry for it in the database.
     *
     * @param coc
     * @param registeredBy
     * @return
     */
    static boolean saveToDataBase(CoC coc, String registeredBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement cs = con.prepareCall("EXECUTE saveCoC ?, ?, ?, ?");
            cs.setString(1, coc.getCoCCode());
            cs.setString(2, coc.getCoCName());
            cs.setString(3, coc.getCoCDisc());
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
     * @param coc
     * @param updatedBy
     * @return
     */
    static boolean updateDataBase(CoC coc, String updatedBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall("EXECUTE updateCoC ?, ?, ?, ?");

            ps.setString(1, coc.getCoCCode());
            ps.setString(2, coc.getCoCName());
            ps.setString(3, coc.getCoCDisc());
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
     * coc.
     *
     * @param coc
     * @param deletedBy
     * @return
     */
    static boolean delete(CoC coc, String deletedBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall(
                    "UPDATE CoC "
                    + "SET deleted = 1, updatedBy = ? "
                    + "WHERE cocCode = ? ");

            ps.setString(1, deletedBy);
            ps.setString(2, coc.getCoCCode());

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
     * coc after it is deleted.
     *
     * @param coc
     * @param deletedBy
     * @return
     */
    static boolean recover(CoC coc, String deletedBy) {
         try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall(
                    "UPDATE CoC "
                    + "SET deleted = 0, updatedBy = ? "
                    + "WHERE cocCode = ? ");

            ps.setString(1, deletedBy);
            ps.setString(2, coc.getCoCCode());

            int eu = ps.executeUpdate();
            con.close();
            return eu > 0;
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }

    /**
     * Gets fields of a coc from database and constructs an CoC object. This
     * method accesses the database and gets a coc using cocCode of the coc. if
     * there is no coc that corresponds the cocCode in the database,
     * NotFoundException is thrown
     *
     * @param cocCode
     * @return
     */
    static CoC getCoC(String cocCode) throws NotFoundException {
        try (Connection con = ServerConnection.getConnection()) {
            PreparedStatement cs = con.prepareStatement(
                    "SELECT * "
                    + "FROM CoC "
                    + "WHERE cocCode = ? ");
            cs.setString(1, cocCode);
//            cs.set
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                String cocName = rs.getString("cocName");
                String cocDisc = rs.getString("cocDisc");

                PreparedStatement cs2 = con.prepareStatement(
                        "SELECT u1.userName headUserName, u1.FirstName headFirstName, u1.LastName headLastName, u1.[Password] headPassword, "
                        + "u2.userName viceUserName, u2.FirstName viceFirstName, u2.LastName viceLastName, u2.[Password] vicePassword "
                        + "FROM CoC_Heads c INNER JOIN [User] u1 ON c.headUserName = u1.userName INNER JOIN [User] u2 ON c.viceUserName = u2.userName "
                        + "WHERE c.cocCode = ? ");
                cs2.setString(1, cocCode);

                ResultSet rs2 = cs2.executeQuery();
                User head = null;
                User vice = null;
                if (rs2.next()) {
                    if (rs2.getString("headUserName") != null && rs2.getString("viceUserName") != null) {

                        String headUserName = rs2.getString("headUserName");
                        String headFirstName = rs2.getString("headFirstName");
                        String headLastName = rs2.getString("headLastName");
                        String headPassword = rs2.getString("headpassword");

                        String viceUserName = rs2.getString("viceUserName");
                        String viceFirstName = rs2.getString("viceFirstName");
                        String viceLastName = rs2.getString("viceLastName");
                        String vicePassword = rs2.getString("vicePassword");

                        head = new User(headUserName, headPassword, headFirstName, headLastName, null, null);
                        vice = new User(viceUserName, vicePassword, viceFirstName, viceLastName, null, null);

                    }
                }
                if (head == null || vice == null) {
                    return new CoC(cocCode, cocName, cocDisc);
                } else {
                    return new CoC(cocCode, cocName, cocDisc, head, vice);
                }
            } else {
                throw new NotFoundException("CoC");
            }

        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
            throw new NotFoundException("CoC");
        }
    }

    /**
     * Gets and returns ArrayList object that is composed of all the cocs that
     * are registered in the database.
     *
     * @return
     */
    static java.util.ArrayList<CoC> getAllCoCs(boolean nonDeleted) {
        ArrayList<CoC> cocs = new ArrayList<>();
        try (Connection con = ServerConnection.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT * FROM CoC WHERE "
                    + " deleted " + (nonDeleted ? "<> 1" : "= 1"));

            while (rs.next()) {
                String cocCode = rs.getString("cocCode");
                String cocName = rs.getString("cocName");
                String cocDisc = rs.getString("cocDisc");
                PreparedStatement cs2 = con.prepareStatement(
                        "SELECT u1.userName headUserName, u1.FirstName headFirstName, u1.LastName headLastName, u1.[Password] headPassword, "
                        + "u2.userName viceUserName, u2.FirstName viceFirstName, u2.LastName viceLastName, u2.[Password] vicePassword "
                        + "FROM CoC_Heads c INNER JOIN [User] u1 ON c.headUserName = u1.userName INNER JOIN [User] u2 ON c.viceUserName = u2.userName "
                        + "WHERE c.cocCode = ? ");
                cs2.setString(1, cocCode);

                ResultSet rs2 = cs2.executeQuery();
                User head = null;
                User vice = null;
                if (rs2.next()) {
                    if (rs2.getString("headUserName") != null && rs2.getString("viceUserName") != null) {
                        String headUserName = rs2.getString("headUserName");
                        String headFirstName = rs2.getString("headFirstName");
                        String headLastName = rs2.getString("headLastName");
                        String headPassword = rs2.getString("headpassword");

                        String viceUserName = rs2.getString("viceUserName");
                        String viceFirstName = rs2.getString("viceFirstName");
                        String viceLastName = rs2.getString("viceLastName");
                        String vicePassword = rs2.getString("vicePassword");

                        head = new User(headUserName, headPassword, headFirstName, headLastName, null, null);
                        vice = new User(viceUserName, vicePassword, viceFirstName, viceLastName, null, null);

                    }
                }
                if (head == null || vice == null) {
                    cocs.add(new CoC(cocCode, cocName, cocDisc));
                } else {
                    cocs.add(new CoC(cocCode, cocName, cocDisc, head, vice));
                }
            }

        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return cocs;
    }

    static boolean assignHeads(CoC coc, User head, User vice) {
        try (Connection con = ServerConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE CoC_Heads SET headUserName = ?, viceUserName = ? "
                    + "WHERE cocCode = ?;");
            ps.setString(1, head.getUserName());
            ps.setString(2, vice.getUserName());
            ps.setString(3, coc.getCoCCode());

            if (ps.executeUpdate() > 0) {
                return true;
            } else {
                PreparedStatement ps2 = con.prepareStatement(""
                        + "IF NOT EXISTS (SELECT * FROM CoC_Heads WHERE cocCode = ?)  "
                        + "INSERT INTO CoC_Heads VALUES(?,?,?)");
                ps2.setString(1, coc.getCoCCode());
                ps2.setString(2, coc.getCoCCode());
                ps2.setString(3, head.getUserName());
                ps2.setString(4, vice.getUserName());

                return ps2.executeUpdate() > 0;
            }
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }
}
