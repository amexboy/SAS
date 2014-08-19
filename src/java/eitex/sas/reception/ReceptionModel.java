package eitex.sas.reception;

import eitex.sas.category.Category;
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

/**
 *
 * @author mekdi
 */
public class ReceptionModel {

    static Reception getReception(String receiptCode) throws SQLException {
        Connection con = ServerConnection.getConnection();

        CallableStatement cs = con.prepareCall("getReception ?");
        cs.setString(1, receiptCode);
        ResultSet rs = cs.executeQuery();

        if (rs.next()) {
            String itemCode = rs.getString("itemcode");
            String storeCode = rs.getString("storeCode");
            String userName = rs.getString("userName");
            int quantity = rs.getInt("quantity");

            return new Reception(receiptCode, new Item(itemCode), new Store(storeCode), new User(userName), quantity);
        }
        return null;
    }

    static boolean saveToDataBase(Reception reception, String registeredBy) throws SQLException {
        Connection con = ServerConnection.getConnection();
        CallableStatement cs = con.prepareCall("EXECUTE receiveItem ?, ?, ?, ?, ?");
        cs.setString(1, reception.getReceiptCode());
        cs.setString(2, reception.getItem().getItemCode());
        cs.setString(3, reception.getStore().getStoreCode());
        cs.setString(4, reception.getAcceptedBy().getUserName());
        cs.setInt(5, reception.getQuantity());
        
        int eu = cs.executeUpdate();
        con.close();
        return (eu > 0);
    }

    static ArrayList<Reception> getAllReceptions() throws SQLException {
        Connection con = ServerConnection.getConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("EXECUTE getAllReception");

        ArrayList<Reception> receptions = new ArrayList<>();

        while (rs.next()) {
            String recieptCode = rs.getString("recieptCode");
            String itemCode = rs.getString("itemCode");
            String storeCode = rs.getString("storeCode");
            String userName = rs.getString("userName");
            int quantity = rs.getInt("quantity");

            receptions.add(new Reception(recieptCode, new Item(itemCode), new Store(storeCode), new User(userName), quantity));
        }

        return receptions;
    }

}
