package eitex.sas.store;

import eitex.sas.address.Address;
import eitex.sas.coc.*;
import eitex.sas.data.ServerConnection;
import eitex.sas.role.Role;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Amanu
 */
class StoreModel {

    static boolean saveToDataBase(Store s, String registeredBy) throws SQLException {
        Connection con = ServerConnection.getConnection();
        CallableStatement ps = con.prepareCall("EXECUTE saveStore ?,?,?,?,?");
        ps.setString(1, s.getStoreCode());
        ps.setString(2, s.getStoreName());
        ps.setString(3, s.getStoreDisc());
        ps.setInt(4, s.getAddress().getId());
        ps.setString(5, registeredBy);

        int eu = ps.executeUpdate();

        return eu > 0;
    }

    static boolean updateDataBase(Store s, String updatedBy) throws SQLException {
        Connection con = ServerConnection.getConnection();
        CallableStatement ps = con.prepareCall("EXECUTE updateStore ?,?,?,?");
        ps.setString(1, s.getStoreCode());
        ps.setString(2, s.getStoreName());
        ps.setString(3, s.getStoreDisc());
        ps.setString(4, updatedBy);

        int eu = ps.executeUpdate();
        con.close();
        return eu > 0;
    }

    static boolean delete(Store s, String deletedBy) throws SQLException {
        Connection con = ServerConnection.getConnection();
        CallableStatement ps = con.prepareCall("EXECUTE deleteStore ?,?");
        ps.setString(1, s.getStoreCode());
        ps.setString(2, deletedBy);

        int eu = ps.executeUpdate();
        con.close();
        return eu > 0;
    }

    static ArrayList<Store> getAllStores() throws SQLException {
        Connection con = ServerConnection.getConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("EXECUTE getAllStores");
        ArrayList<Store> stores = new ArrayList<>();
        
        while(rs.next()){
            String storeCode = rs.getString("storeCode");
            String storeName = rs.getString("storeName");
            String storeDisc = rs.getString("storeDisc");
            int id = rs.getInt("id");
            Address address = new Address(id);
            
            stores.add(new Store(storeCode, storeName, storeDisc, address));
        }
        return stores;
    }

    static Store getStore(String storeCode) throws SQLException {
        Connection con = ServerConnection.getConnection();
        CallableStatement cs = con.prepareCall("EXECUTE getStore ?");
        cs.setString(1, storeCode);

        ResultSet rs = cs.executeQuery();
        if (rs.next()) {
            String storeName = rs.getString("storeName");
            String storeDisc = rs.getString("storeDisc");
            int id = rs.getInt("id");
            Address address = new Address(id);
            return new Store(storeCode, storeName, storeDisc, address);
        }
        return null;
    }

}
