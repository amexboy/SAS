package eitex.sas.data;

import eitex.sas.common.ExceptionLogger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnection {

    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=eitex;instanceName=SQLEXPRESS;userName=sa;password=12345");
        } catch (ClassNotFoundException | SQLException ex) {
            ExceptionLogger.log(ex);
        }
        return con;
    }

    public static void main(String[] args) {
        getConnection();
    }
}
