package eitex.sas.module;

import eitex.sas.data.ServerConnection;
import eitex.sas.role.Role;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Amanu
 */
class ModuleModel {

    static boolean saveToDataBase(Module module,String registeredBy) throws SQLException {
        Connection con = ServerConnection.getConnection();
        CallableStatement cs = con.prepareCall("EXECUTE saveModule ?, ?, ?");
        cs.setString(1, module.getModuleName());
        cs.setString(2, module.getModuleURL());
        cs.setString(3, registeredBy);
        int eu = cs.executeUpdate();
        con.close();
        return (eu > 0);
    } 
    
    static boolean updateDataBase(Module module,String updatedBy) throws SQLException {
        Connection con = ServerConnection.getConnection();
        CallableStatement ps = con.prepareCall("EXECUTE updateModule ?, ?, ?, ?");

        ps.setInt(1, module.getModuleCode());
        ps.setString(2, module.getModuleName());
        ps.setString(3, module.getModuleURL());
        ps.setString(4, updatedBy);

        int eu = ps.executeUpdate();
        con.close();
        return eu > 0;
    }

    static boolean delete(Module module, String deletedBy) throws SQLException {
        Connection con = ServerConnection.getConnection();
        CallableStatement ps = con.prepareCall("EXECUTE deleteModule ?, ?");

        ps.setInt(1, module.getModuleCode());
        ps.setString(2, deletedBy);

        int eu = ps.executeUpdate();
        con.close();
        return eu > 0;
    }
    static ArrayList<Module> getAllModule() throws SQLException {
        Connection con = ServerConnection.getConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("EXECUTE getAllModule");
        ArrayList<Module> modules = new ArrayList<>();
        while (rs.next()) {
            int moduleCode = rs.getInt("moduleCode");
            String moduleName = rs.getString("moduleName");
            String moduleURL = rs.getString("moduleURL");

            modules.add(new Module(moduleCode, moduleName, moduleURL));
        }
        return modules;
    }

    static Module getModule(int moduleCode) throws SQLException {
        Connection con = ServerConnection.getConnection();

        
        CallableStatement ps = con.prepareCall("EXECUTE getModule ? ");
        ps.setInt(1, moduleCode);
        
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            String moduleName = rs.getString("moduleName");
            String moduleURL = rs.getString("moduleURL");
            rs.close();
            con.close();
            return new Module(moduleCode, moduleName, moduleURL);
        } else {
            con.close();
            return null;
        }

    }

}
