package eitex.sas.item;

import eitex.sas.category.Category;
import eitex.sas.common.ExceptionLogger;
import eitex.sas.data.ServerConnection;
import eitex.sas.item.*;
import eitex.sas.item.Item;
import eitex.sas.store.Store;
import eitex.sas.unit.Unit;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author mekdi
 */
public class ItemModel {

    static Item getItem(String itemCode) throws ItemFieldException {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement cs = con.prepareCall("getItem ?");
            cs.setString(1, itemCode);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                String itemName = rs.getString("itemName");
                String unitCode = rs.getString("unitCode");
                String categoryCode = rs.getString("categoryCode");
                String itemDisc = rs.getString("itemDisc");

                return new Item(itemCode, new Unit(unitCode), new Category(categoryCode), itemName, itemDisc);
            }
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return null;
    }

    static boolean saveToDataBase(Item item, String registeredBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement cs = con.prepareCall("EXECUTE saveItem ?, ?, ?, ?, ?, ?");
            cs.setString(1, item.getItemCode());
            cs.setString(2, item.getItemName());
            cs.setString(3, item.getUnit().getUnitCode());
            cs.setString(4, item.getCategory().getCategoryCode());
            cs.setString(5, item.getItemDisc());
            cs.setString(6, registeredBy);

            int eu = cs.executeUpdate();
            con.close();
            return (eu > 0);
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }

    static boolean updateDataBase(Item item, String updatedBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement cs = con.prepareCall("EXECUTE updateItem  ?, ?, ?, ?, ?, ?");
            cs.setString(1, item.getItemCode());
            cs.setString(2, item.getItemName());
            cs.setString(3, item.getUnit().getUnitCode());
            cs.setString(4, item.getCategory().getCategoryCode());
            cs.setString(5, item.getItemDisc());
            cs.setString(6, updatedBy);

            int eu = cs.executeUpdate();
            con.close();
            return eu > 0;
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }

    static boolean delete(Item item, String deletedBy) {
        try (Connection con = ServerConnection.getConnection()) {
            CallableStatement ps = con.prepareCall("EXECUTE deleteItem ?, ?");

            ps.setString(1, item.getItemCode());
            ps.setString(2, deletedBy);

            int eu = ps.executeUpdate();
            con.close();
            return eu > 0;
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }

    static ArrayList<Item> getAllItems() throws ItemFieldException {
        ArrayList<Item> items = new ArrayList<>();
        try (Connection con = ServerConnection.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("EXECUTE getAllItem");

            while (rs.next()) {
                String itemCode = rs.getString("itemCode");
                String itemName = rs.getString("itemName");
                String unitCode = rs.getString("unitCode");
                String categoryCode = rs.getString("categoryCode");
                String itemDisc = rs.getString("itemDisc");

                items.add(new Item(itemCode, new Unit(unitCode), new Category(categoryCode), itemName, itemDisc));
            }
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }

        return items;
    }

    static int getTotalInAllStores(Item item) {
        try (Connection con = ServerConnection.getConnection()) {
            PreparedStatement st = con.prepareStatement("SELECT SUM(quantity) as total FROM ItemLeft WHERE itemCode = ? ");
            st.setString(1, item.getItemCode());
            ResultSet rs = st.executeQuery();

            int result = 0;
            if (rs.next()) {
                result = rs.getInt("total");
            }
            return result;
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return 0;
    }

    static HashMap<Store, Integer> getAllQuntities(Item item) {
        HashMap<Store, Integer> allQuantities = new HashMap<>();
        try (Connection con = ServerConnection.getConnection()) {
            ArrayList<Store> allStores = Store.getAllStores();
            String itemCode = item.getItemCode();

            for (Store s : allStores) {
                String storeCode = s.getStoreCode();

                PreparedStatement st = con.prepareStatement("SELECT quantity FROM ItemLeft WHERE storeCode = ? AND itemCode = ? ");
                st.setString(1, storeCode);
                st.setString(2, itemCode);
                ResultSet rs = st.executeQuery();

                int result = 0;
                if (rs.next()) {
                    result = rs.getInt("quantity");
                }

                allQuantities.put(s, result);
            }
        } catch (SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return allQuantities;
    }

}
