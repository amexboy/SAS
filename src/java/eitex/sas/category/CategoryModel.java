package eitex.sas.category;

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
 * A model class with static fields to perform database actions of Category class.
 * Contains static methods that perform the database manipulations.
 *
 * @author Amanu
 */
public class CategoryModel {

    /**
     * Takes an Category object and creates an entry for it in the database.
     *
     * @param category
     * @param registeredBy
     * @return
     */
    static boolean saveToDataBase(Category category, String registeredBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement cs = con.prepareCall("EXECUTE saveCategory ?, ?, ?, ?");
            cs.setString(1, category.getCategoryCode());
            cs.setString(2, category.getCategoryName());
            cs.setString(3, category.getCategoryDisc());
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
     * @param category
     * @param updatedBy
     * @return
     */
    static boolean updateDataBase(Category category, String updatedBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall("EXECUTE updateCategory ?, ?, ?, ?");

            ps.setString(1, category.getCategoryCode());
            ps.setString(2, category.getCategoryName());
            ps.setString(3, category.getCategoryDisc());
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
     * category.
     *
     * @param category
     * @param deletedBy
     * @return
     */
    static boolean delete(Category category, String deletedBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall(
                    "UPDATE Category "
                    + "SET deleted = 1, updatedBy = ? "
                    + "WHERE categoryCode = ? ");

            ps.setString(1, deletedBy);
            ps.setString(2, category.getCategoryCode());

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
     * category after it is deleted.
     *
     * @param category
     * @param deletedBy
     * @return
     */
    static boolean recover(Category category, String deletedBy) {
         try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall(
                    "UPDATE Category "
                    + "SET deleted = 0, updatedBy = ? "
                    + "WHERE categoryCode = ? ");

            ps.setString(1, deletedBy);
            ps.setString(2, category.getCategoryCode());

            int eu = ps.executeUpdate();
            con.close();
            return eu > 0;
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }
    /**
     * Gets fields of a category from database and constructs an Category object. This
     * method accesses the database and gets a category using categoryCode of the category.
     * if there is no category that corresponds the categoryCode in the database,
     * NotFoundException is thrown
     *
     * @param categoryCode
     * @return
     */
    static Category getCategory(String categoryCode) throws NotFoundException {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement cs = con.prepareCall(
                    " SELECT * "
                    + "FROM Category "
                    + "WHERE categoryCode = ? ");
            cs.setString(1, categoryCode);

            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                String categoryName = rs.getString("categoryName");
                String categoryDisc = rs.getString("categoryDisc");

                return new Category(categoryCode, categoryName, categoryDisc);
            } else {
                throw new NotFoundException();
            }

        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
            throw new NotFoundException("The unit you requested was not found in the database.");
        }
    }

    /**
     * Gets and returns ArrayList object that is composed of all the categorys that
     * are registered in the database.
     *
     * @return
     */
    static java.util.ArrayList<Category> getAllCategorys(boolean nonDeleted) {
        ArrayList<Category> categorys = new ArrayList<>();
        try (Connection con = ServerConnection.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT * FROM Category WHERE "
                    + " deleted " + (nonDeleted ? "<> 1" : " = 1"));

            while (rs.next()) {
                String categoryCode = rs.getString("categoryCode");
                String categoryName = rs.getString("categoryName");
                String categoryDisc = rs.getString("categoryDisc");

                categorys.add(new Category(categoryCode, categoryName, categoryDisc));
            }

        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return categorys;
    }

}
