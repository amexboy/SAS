package eitex.sas.user;

import eitex.sas.common.ExceptionLogger;
import eitex.sas.common.NotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
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
@WebServlet(name = "UserActions", urlPatterns = {"/User/actions.jsp", "/User/UserActions"})/*urlPatterns = {"/CoC/actions.jsp",*/

public class UserActions extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkLogin(response, request)) {
            return;
        }
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            String action = request.getParameter("action");
            String loggedInUser = null;
            String model = request.getParameter("models");

            try {
                loggedInUser = session.getAttribute("userName").toString();
            } catch (NullPointerException e) {
            }

            if (action == null) {
                getAllAction(out, false);
            } else {
                switch (action) {
                    case "edit":
                        editAction(out, model, loggedInUser);
                        break;
                    case "delete":
                        deleteAction(out, model, loggedInUser);
                        break;
                    case "create":
                        createAction(out, model, loggedInUser);
                        break;
                    case "recover":
                        recoverAction(out, model, loggedInUser);
                        break;
                    case "getDeleted":
                        getAllAction(out, true);
                        break;
                    default:
                        getAllAction(out, false);
                        break;
                }
            }

        }
    }

    public boolean checkLogin(HttpServletResponse response, HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("userName") == null) {
            String queryString = request.getQueryString() == null ? "" : request.getQueryString();
            String url = URLEncoder.encode(request.getRequestURI() + "?" + queryString, "UTF-8");
            url = url == null ? "" : url;
            //there is no user that is currently logged in, hence the user should be ridirected to the login page
            PrintWriter out = response.getWriter();
            out.println("[{\"error\":true, \"message\":\"You must login first. "
                    + "<a href='/SAS/login.jsp?msg=You+have+to+login+first&url=/SAS/'>Click here to login</a>\"}]");//<a href='"/SAS/login.jsp?msg=You+have+to+login+first&url=" + url+"'
//            response.sendRedirect("/SAS/login.jsp?msg=You+have+to+login+first&url=" + url);
            return false;
        }
//        else { 
//
//            try {
//                //to check if the logged on user has permission to access the requested page....
//                User user = new User(session.getAttribute("userName").toString());
//                ArrayList<Role> roles = user.getRoles();
//                boolean found = false;
//                for (Role r : roles) {//for each Role r in roles
//                    ArrayList<Module> modules = r.getModules();
//                    String module = request.getRequestURI();
//                    for (Module m : modules) {
//                        if (m.getModuleURL().equals(module)) {
//                            found = true;
//                            break;
//                        }
//                    }
//                    if (found) {
//                        break;
//                    }
//                }
//                if (!found) {
//                    response.sendRedirect("/SAS/index.jsp");
//                    return false;
//                }
//            } catch (NotFoundException | UserFieldException ex) {
//                ExceptionLogger.log(ex);
//            }
//        }
        return true;
    }

    public boolean getAllAction(PrintWriter out, boolean deleted) {
        JsonArrayBuilder values = Json.createArrayBuilder();
        ArrayList<User> allUsers;
        allUsers = deleted ? User.getAllDeletedUsers() : User.getAllUsers();
        for (User u : allUsers) {
            values.add(Json.createObjectBuilder()
                    .add("userName", u.getUserName())
                    .add("firstName", u.getFirstName())
                    .add("lastName", u.getLastName())
                    .add("fullName", u.getFirstName() + " " + u.getLastName())
                    .add("password", "")
                    .add("coc", Json.createObjectBuilder()
                            .add("cocCode", u.getCoc() != null ? u.getCoc().getCoCCode() : null)
                            .add("cocName", u.getCoc() != null ? u.getCoc().getCoCName() : "Un assigned")
                    )
                    .add("officeBuilding", u.getAddress().getOfficeBuilding())
                    .add("officeNumber", u.getAddress().getOfficeNumber())
                    .add("officePhoneNumber", u.getAddress().getOfficePhoneNumber())
                    .add("mobilePhoneNumber", u.getAddress().getMobilePhoneNumber())
                    .add("email", u.getAddress().getEmail())
            );
        }
        StringWriter stWriter = new StringWriter();
        JsonWriter jOut = Json.createWriter(stWriter);
        jOut.writeArray(values.build());;
        jOut.close();
        out.print(stWriter);
        return true;
    }

    private void editAction(PrintWriter out, String model, String loggedInUser) {
        JsonArrayBuilder values = Json.createArrayBuilder();

        ArrayList<User> usersFromModel = User.createUserFormJSON(model);
        for (User u : usersFromModel) {
            try {
                u.setIsNew(false);
                u.setModified(true);
                if (u.validate()) {
                    u.save(loggedInUser);
                    values.add(Json.createObjectBuilder()
                            .add("userName", u.getUserName())
                            .add("firstName", u.getFirstName())
                            .add("lastName", u.getLastName())
                            .add("fullName", u.getFirstName() + " " + u.getLastName())
                            .add("password", "")
                            .add("coc", Json.createObjectBuilder()
                                    .add("cocCode", u.getCoc() != null ? u.getCoc().getCoCCode() : null)
                                    .add("cocName", u.getCoc() != null ? u.getCoc().getCoCName() : "Un assigned")
                            )
                            .add("officeBuilding", u.getAddress().getOfficeBuilding())
                            .add("officeNumber", u.getAddress().getOfficeNumber())
                            .add("officePhoneNumber", u.getAddress().getOfficePhoneNumber())
                            .add("mobilePhoneNumber", u.getAddress().getMobilePhoneNumber())
                            .add("email", u.getAddress().getEmail())
                    );
                }
            } catch (UserFieldException ex) {
                ExceptionLogger.log(ex);
                values.add(Json.createObjectBuilder()
                        .add("error", true)
                        .add("message", "There was an error validating the user!!")
                        .add("userName", u.getUserName())
                        .add("firstName", u.getFirstName())
                        .add("lastName", u.getLastName())
                        .add("fullName", u.getFirstName() + " " + u.getLastName())
                        .add("password", "")
                        .add("coc", Json.createObjectBuilder()
                                .add("cocCode", u.getCoc() != null ? u.getCoc().getCoCCode() : null)
                                .add("cocName", u.getCoc() != null ? u.getCoc().getCoCName() : "Un assigned")
                        )
                        .add("officeBuilding", u.getAddress().getOfficeBuilding())
                        .add("officeNumber", u.getAddress().getOfficeNumber())
                        .add("officePhoneNumber", u.getAddress().getOfficePhoneNumber())
                        .add("mobilePhoneNumber", u.getAddress().getMobilePhoneNumber())
                        .add("email", u.getAddress().getEmail())
                );
            }
        }

        StringWriter stWriter = new StringWriter();
        JsonWriter jOut = Json.createWriter(stWriter);
        jOut.writeArray(values.build());;
        jOut.close();
        out.print(stWriter);
    }

    private void deleteAction(PrintWriter out, String model, String loggedInUser) {
        JsonArrayBuilder values = Json.createArrayBuilder();
        ArrayList<User> userFromModel = User.createUserFormJSON(model);
        for (User u : userFromModel) {
            u.setIsNew(false);
            if (u.delete(loggedInUser)) {
                values.add(Json.createObjectBuilder()
                        .add("error", false)
                        .add("message", "The user was deleted!!")
                );
            } else {
                values.add(Json.createObjectBuilder()
                        .add("error", true)
                        .add("message", "There was an error user was not deleted!!")
                        .add("userName", u.getUserName())
                        .add("firstName", u.getFirstName())
                        .add("lastName", u.getLastName())
                        .add("fullName", u.getFirstName() + " " + u.getLastName())
                        .add("password", "")
                        .add("coc", Json.createObjectBuilder()
                                .add("cocCode", u.getCoc() != null ? u.getCoc().getCoCCode() : null)
                                .add("cocName", u.getCoc() != null ? u.getCoc().getCoCName() : "Un assigned")
                        )
                        .add("officeBuilding", u.getAddress().getOfficeBuilding())
                        .add("officeNumber", u.getAddress().getOfficeNumber())
                        .add("officePhoneNumber", u.getAddress().getOfficePhoneNumber())
                        .add("mobilePhoneNumber", u.getAddress().getMobilePhoneNumber())
                        .add("email", u.getAddress().getEmail())
                );
            }
        }
        StringWriter stWriter = new StringWriter();
        JsonWriter jOut = Json.createWriter(stWriter);
        jOut.writeArray(values.build());;
        jOut.close();
        out.print(stWriter);
    }

    private void recoverAction(PrintWriter out, String model, String loggedInUser) {
        JsonArrayBuilder values = Json.createArrayBuilder();
        try {
            User u = new User(model);
            u.setIsNew(false);
            if (u.recover(loggedInUser)) {
                values.add(Json.createObjectBuilder()
                        .add("error", false)
                        .add("message", "The user was un-deleted!!")
                );
            } else {
                values.add(Json.createObjectBuilder()
                        .add("error", true)
                        .add("message", "There was an error, the user was not recovered!!")
                );
            }
        } catch (NotFoundException ex) {
            ExceptionLogger.log(ex);
            values.add(Json.createObjectBuilder()
                    .add("error", true)
                    .add("message", "The user was not found!!")
            );
        }
        StringWriter stWriter = new StringWriter();
        JsonWriter jOut = Json.createWriter(stWriter);
        jOut.writeArray(values.build());;
        jOut.close();
        out.print(stWriter);
    }

    private void createAction(PrintWriter out, String model, String loggedInUser) {
        JsonArrayBuilder values = Json.createArrayBuilder();
        ArrayList<User> userFromModel = User.createUserFormJSON(model);
        for (User u : userFromModel) {
            try {
                if (u.validate()) {
                    u.save(loggedInUser);
                    values.add(Json.createObjectBuilder()
                        .add("userName", u.getUserName())
                        .add("firstName", u.getFirstName())
                        .add("lastName", u.getLastName())
                        .add("fullName", u.getFirstName() + " " + u.getLastName())
                        .add("password", "")
                        .add("coc", Json.createObjectBuilder()
                                .add("cocCode", u.getCoc() != null ? u.getCoc().getCoCCode() : null)
                                .add("cocName", u.getCoc() != null ? u.getCoc().getCoCName() : "Un assigned")
                        )
                        .add("officeBuilding", u.getAddress().getOfficeBuilding())
                        .add("officeNumber", u.getAddress().getOfficeNumber())
                        .add("officePhoneNumber", u.getAddress().getOfficePhoneNumber())
                        .add("mobilePhoneNumber", u.getAddress().getMobilePhoneNumber())
                        .add("email", u.getAddress().getEmail())
                    );
                }
            } catch (UserFieldException ex) {
                ExceptionLogger.log(ex);
                values.add(Json.createObjectBuilder()
                        .add("error", true)
                        .add("message", "There was an error validating the user!!")
                        .add("userName", u.getUserName())
                        .add("firstName", u.getFirstName())
                        .add("lastName", u.getLastName())
                        .add("fullName", u.getFirstName() + " " + u.getLastName())
                        .add("password", "")
                        .add("coc", Json.createObjectBuilder()
                                .add("cocCode", u.getCoc() != null ? u.getCoc().getCoCCode() : null)
                                .add("cocName", u.getCoc() != null ? u.getCoc().getCoCName() : "Un assigned")
                        )
                        .add("officeBuilding", u.getAddress().getOfficeBuilding())
                        .add("officeNumber", u.getAddress().getOfficeNumber())
                        .add("officePhoneNumber", u.getAddress().getOfficePhoneNumber())
                        .add("mobilePhoneNumber", u.getAddress().getMobilePhoneNumber())
                        .add("email", u.getAddress().getEmail())
                );
            }
        }
        StringWriter stWriter = new StringWriter();
        JsonWriter jOut = Json.createWriter(stWriter);
        jOut.writeArray(values.build());;
        jOut.close();
        out.print(stWriter);
    }

    // <editor-fold defaultstate="collapsed" desc="Important methods">
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
