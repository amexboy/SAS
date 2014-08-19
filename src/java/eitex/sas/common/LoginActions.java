package eitex.sas.common;

import eitex.sas.user.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Amanu
 */
@WebServlet(name = "Login", urlPatterns = {"/LoginActions"})
public class LoginActions extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/json");
        HttpSession session = request.getSession();
        try (PrintWriter out = response.getWriter()) {
            String userName = "", password = "";
            JsonObjectBuilder values = Json.createObjectBuilder();
            if (session.getAttribute("userName") != null) {
                values.add("error", false);
            }
            if (request.getParameter("login") != null) {
                userName = request.getParameter("userName");
                password = request.getParameter("password");
                //check if the user name and password is a valid combination
                if (User.checkLogin(userName, password)) {
                    session.setAttribute("userName", userName);
                    values.add("error", false);
                } else {
                    values.add("error", false)
                            .add("message", "The username password combination is not recognized, please try again!");
                }
            }
            StringWriter stWriter = new StringWriter();
            JsonWriter jOut = Json.createWriter(stWriter);
            jOut.writeObject(values.build());;
            jOut.close();
            out.print(stWriter);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
