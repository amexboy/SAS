package eitex.sas.borrow;

import eitex.sas.reception.*;
import eitex.sas.category.Category;
import eitex.sas.common.ExceptionLogger;
import eitex.sas.data.ServerConnection;
import eitex.sas.item.Item;
import eitex.sas.store.Store;
import eitex.sas.unit.Unit;
import eitex.sas.user.User;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.NotFoundException;

/**
 *
 * @author mekdi
 */
public class BorrowModel {

    static Borrow getBorrow(int borrowCode) throws BorrowFieldException {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement cs = con.prepareCall("getBorrow ?");
            cs.setInt(1, borrowCode);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                String itemCode = rs.getString("itemcode");
                String storeCode = rs.getString("storeCode");
                String borrowerName = rs.getString("borrowerName");
                String givenBy = rs.getString("givenBy");
                int quantity = rs.getInt("quantity");
                boolean returned = rs.getString("returned") == null;

                return new Borrow(borrowCode, new Item(itemCode), new Store(storeCode), new User(borrowerName), new User(givenBy), quantity, returned);
            }

        } catch (SQLException ex) {
            ExceptionLogger.log(ex);;
        }
        return null;
    }

    static boolean saveToDataBase(Borrow borrow) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement cs = con.prepareCall("EXECUTE BorrowItem ?, ?, ?, ?, ?");
            cs.setString(1, borrow.getItem().getItemCode());
            cs.setString(2, borrow.getStore().getStoreCode());
            cs.setString(3, borrow.getBorrower().getUserName());
            cs.setString(4, borrow.getGivenBy().getUserName());
            cs.setInt(5, borrow.getQuantity());

            int eu = cs.executeUpdate();
            con.close();
            return (eu > 0);
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }

    static ArrayList<Borrow> getAllBorrows() throws BorrowFieldException {
        ArrayList<Borrow> receptions = new ArrayList<>();;
        try (Connection con = ServerConnection.getConnection()) {

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("EXECUTE getAllBorrows");

            while (rs.next()) {
                int borrowCode = rs.getInt("borrowCode");
                String itemCode = rs.getString("itemCode");
                String storeCode = rs.getString("storeCode");
                String borrowerName = rs.getString("borowerName");
                String givenBy = rs.getString("givenBy");
                int quantity = rs.getInt("quantity");
                boolean returned = rs.getString("returned") != null;

                receptions.add(new Borrow(borrowCode, new Item(itemCode), new Store(storeCode), new User(borrowerName), new User(givenBy), quantity, returned));
            }

        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return receptions;
    }

    static boolean returnBorrow(Borrow borrow) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement cs = con.prepareCall("EXECUTE returnItem ? ");
            cs.setInt(1, borrow.getBorrowCode());

            int eu = cs.executeUpdate();
            con.close();
            return (eu > 0);
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }

}
