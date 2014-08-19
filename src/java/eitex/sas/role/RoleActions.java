package eitex.sas.role;

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
@WebServlet(name = "RoleActions", urlPatterns = {"/Role/actions.jsp", "/Role/RoleActions"})/*urlPatterns = {"/Role/actions.jsp",*/

public class RoleActions extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkLogin(response, request)) {
            return;
        }
        response.setContentType("text/json");
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
                    case "recover":
                        recoverAction(out, model, loggedInUser);
                        break;
                    case "create":
                        createAction(out, model, loggedInUser);
                        break;
                    case "approvingRole":
                        approvingRoleAction(out, model, loggedInUser);
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
            String url = URLEncoder.encode(request.getRequestURI() + queryString, "UTF-8");
            url = url == null ? "" : url;
            PrintWriter out = response.getWriter();
            out.println("[{\"error\":true, \"message\":\"You must login first. "
                    + "<a href='/SAS/login.jsp?msg=You+have+to+login+first&url=" + url + "'>Click here to login</a>\"}]");//<a href='"/SAS/login.jsp?msg=You+have+to+login+first&url=" + url+"'
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
        ArrayList<Role> allCategories;
        allCategories = deleted ? Role.getAllDeletedRoles() : Role.getAllRoles();
        for (Role c : allCategories) {
            values.add(Json.createObjectBuilder()
                    .add("roleCode", c.getRoleCode())
                    .add("roleName", c.getRoleName())
                    .add("roleDisc", c.getRoleDisc().replace("\"", "'"))
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

        ArrayList<Role> roleFromModel = Role.createRoleFormJSON(model);
        for (Role c : roleFromModel) {
            try {
                Role role = new Role(c.getRoleCode());
                role.setRoleName(c.getRoleName());
                role.setRoleDisc(c.getRoleDisc());
                role.setModified(true);
                if (c.validate()) {
                    c.save(loggedInUser);
                    values.add(Json.createObjectBuilder()
                            .add("roleCode", role.getRoleCode())
                            .add("roleName", role.getRoleName())
                            .add("roleDisc", role.getRoleDisc().replace("\"", "'"))
                    );
                }
            } catch (RoleFieldException ex) {
                ExceptionLogger.log(ex);
                values.add(Json.createObjectBuilder()
                        .add("error", true)
                        .add("message", "There was an error validating the role!!")
                );
            } catch (NotFoundException ex) {
                ExceptionLogger.log(ex);
                values.add(Json.createObjectBuilder()
                        .add("error", true)
                        .add("message", "There was an error role was not found!!")
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
        ArrayList<Role> roleFromModel = Role.createRoleFormJSON(model);
        for (Role c : roleFromModel) {
            try {
                Role role = new Role(c.getRoleCode());
                role.setNew(false);
                if (role.delete(loggedInUser)) {
                    values.add(Json.createObjectBuilder()
                            .add("error", false)
                            .add("message", "The role was deleted!!")
                    );
                } else {
                    values.add(Json.createObjectBuilder()
                            .add("error", true)
                            .add("message", "There was an error role was not deleted!!")
                    );
                }
            } catch (NotFoundException ex) {
                ExceptionLogger.log(ex);
                values.add(Json.createObjectBuilder()
                        .add("error", true)
                        .add("message", "The role was not found!!")
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
            Role role = new Role(model);
            role.setNew(false);
            if (role.recover(loggedInUser)) {
                values.add(Json.createObjectBuilder()
                        .add("error", false)
                        .add("message", "The role was un-deleted!!")
                );
            } else {
                values.add(Json.createObjectBuilder()
                        .add("error", true)
                        .add("message", "There was an error role was not recovered!!")
                );
            }
        } catch (NotFoundException ex) {
            ExceptionLogger.log(ex);
            values.add(Json.createObjectBuilder()
                    .add("error", true)
                    .add("message", "The role was not found!!")
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
        ArrayList<Role> roleFromModel = Role.createRoleFormJSON(model);
        for (Role c : roleFromModel) {
            try {
                if (c.validate()) {
                    c.save(loggedInUser);
                    values.add(Json.createObjectBuilder()
                            .add("roleCode", c.getRoleCode())
                            .add("roleName", c.getRoleName())
                            .add("roleDisc", c.getRoleDisc().replace("\"", "'"))
                    );
                }
            } catch (RoleFieldException ex) {
                ExceptionLogger.log(ex);
                values.add(Json.createObjectBuilder()
                        .add("error", true)
                        .add("message", "There was an error validating the role!!")
                );
            }
        }
        StringWriter stWriter = new StringWriter();
        JsonWriter jOut = Json.createWriter(stWriter);
        jOut.writeArray(values.build());;
        jOut.close();
        out.print(stWriter);
    }

    private void approvingRoleAction(PrintWriter out, String model, String loggedInUser) {
//        JsonArrayBuilder values = Json.createArrayBuilder();
//
//        class ApprovingRoleModel {
//
//            public String roleCode;
//            public String headUserName;
//            public String viceUserName;
//            public String json;
//            public JsonParser p;
//
//            public ApprovingRoleModel ObjectFromJSON(ApprovingRoleModel model) {
//                switch (p.next()) {
//                    case END_OBJECT:
//                        return model;
//                    case KEY_NAME:
//                        String keyName = p.getString();
//                        Event next = p.next();
//                        try {
//                            if (next == Event.VALUE_STRING) {
//                                model.getClass().getField(keyName).set(model, p.getString());
//                            } else {
//                                model.getClass().getField(keyName).set(model, null);
//                            }
////                            System.out.println("keyName" + keyName);
////                            System.out.println("keyValue" + model.getClass().getField(keyName).get(model));
//                        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
//                            Logger.getLogger(RoleActions.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//
//                }
//                return ObjectFromJSON(model);
//            }
//
//            public ApprovingRoleModel(String json) {
//                this.json = json;
//                p = Json.createParser(new StringReader(json));
//                ObjectFromJSON(this);
//            }
//        }
//        try {
//            ApprovingRoleModel as = new ApprovingRoleModel(model);
//            Role c = new Role(as.roleCode);
//            if (c.assignHeads(new User(as.headUserName), new User(as.viceUserName))) {
//                values.add(Json.createObjectBuilder()
//                        .add("error", false)
//                        .add("message", "Heads are assigned successfully!")
//                );
//            }
//        } catch (NotFoundException ex) {
//            ExceptionLogger.log(ex);
//            values.add(Json.createObjectBuilder()
//                    .add("error", true)
//                    .add("message", "There role was not found in the database!!")
//            );
//        }
//        StringWriter stWriter = new StringWriter();
//        JsonWriter jOut = Json.createWriter(stWriter);
//        jOut.writeArray(values.build());;
//        jOut.close();
//        out.print(stWriter);
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
