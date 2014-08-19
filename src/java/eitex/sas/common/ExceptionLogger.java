package eitex.sas.common;

import eitex.sas.data.ServerConnection;
import eitex.sas.user.User;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.stream.JsonParser;

/**
 * This is how exception details are printed. This will be changed to support a
 * more convenient way latter.
 *
 * @author Amanu
 */
public class ExceptionLogger {

    public static void log(Exception ex) {
        Logger.getLogger(ExceptionLogger.class.getName()).log(Level.SEVERE, null, ex);
        String exceptionName = ex.getClass().getName();
        String exceptionMessage = ex.getMessage();
        StringBuilder exceptionStackTrace = new StringBuilder("");
        StackTraceElement[] stackTrace = ex.getStackTrace();

        Class c = ex.getClass();
        Method[] methods = c.getMethods();

        String name = "";
        String val = "";
        for (Method m : methods) {
            if (m.getName().startsWith("has")) {
                name = m.getName();
                try {
                    val = String.valueOf(m.invoke(ex));
                    System.out.println(val);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex1) {
                    Logger.getLogger(ExceptionLogger.class.getName()).log(Level.SEVERE, null, ex1);
                }
                exceptionStackTrace.append(name).append(":").append(val).append("<br/>");
            }
        }

        for (StackTraceElement stackTraceElement : stackTrace) {
            exceptionStackTrace.append(stackTraceElement).append("<br/>");
        }

        try (Connection con = ServerConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("INSERT INTO ExceptionLog VALUES(?,?,?,0);");
            ps.setString(1, exceptionName);
            ps.setString(2, exceptionMessage.replace("\"", "'"));
            ps.setString(3, exceptionStackTrace.toString());

            ps.executeUpdate();
        } catch (SQLException ex1) {
            logToConsole(ex1);
        }
    }

    public static void logToConsole(Exception ex) {

        String exceptionName = ex.getClass().getName();
        String exceptionMessage = ex.getMessage();
        StringBuilder exceptionStackTrace = new StringBuilder("");
        StackTraceElement[] stackTrace = ex.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            exceptionStackTrace.append(stackTraceElement).append("<br/>\n");
        }

        System.out.println(exceptionName);
        System.out.println(exceptionMessage);
        System.out.println(exceptionStackTrace.toString());
    }

    public static void main(String[] args) {
        final String model = "[{\"cocCode\":\"GED\",\"cocName\":\"Garment Engineering\",\"cocDisc\":\"Garmentm Engineering 1\"}]";
        JsonParser p = Json.createParser(new StringReader(model));
        String cocCode = "";
        while (p.hasNext()) {
            JsonParser.Event next = p.next();
            if (next == JsonParser.Event.KEY_NAME) {
                p.next();
                cocCode = p.getString();
                break;
            }
        }

        System.out.println(cocCode);
    }

}
