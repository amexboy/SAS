<%@page import="java.util.Enumeration"%>
<%@page import="java.util.ArrayList"%>
<%@page import="eitex.sas.role.Role"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="eitex.sas.user.User"%>
<%@page import="eitex.pms.common.Page"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%> 
<%@include  file="../loginCheck.jsp" %> 
<%    String loggedInUser = null;
    try {
        loggedInUser = session.getAttribute("userName").toString();
    } catch (NullPointerException e) {
    }
    Page p = new Page(loggedInUser, "Manage Roles");
    out.write(p.getTop());
%>
<%    String userName = "", fullName = "", officeAddress = "", mobilePhoneNumber = "", officePhoneNumber = "", email = "";
    ArrayList<Role> userRoles = null;
    if (request.getParameter("user") != null) {
        userName = request.getParameter("user");
        try {
            User u = new User(userName);
            fullName = u.getFirstName() + " " + u.getLastName();
            officeAddress = u.getAddress().getOfficeBdg() + " - " + u.getAddress().getOfficeNumber();
            mobilePhoneNumber = u.getAddress().getMobilePhoneNumber();
            officePhoneNumber = u.getAddress().getOfficePhoneNumber();
            email = u.getAddress().getEmail();
            userRoles = u.getRoles();

        } catch (NullPointerException e) {
//            response.sendRedirect("../User/User.jsp");
        }
    } else if (request.getParameter("userName") != null) {
        userName = request.getParameter("userName");
        User u = new User(userName);

        fullName = u.getFirstName() + " " + u.getLastName();
        officeAddress = u.getAddress().getOfficeBdg() + " - " + u.getAddress().getOfficeNumber();
        mobilePhoneNumber = u.getAddress().getMobilePhoneNumber();
        officePhoneNumber = u.getAddress().getOfficePhoneNumber();
        email = u.getAddress().getEmail();
        userRoles = u.getRoles();

        ArrayList<Role> newRoles = new ArrayList<Role>();
        Enumeration<String> e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String par = e.nextElement();
            if (!par.equals("userName")) {
                newRoles.add(new Role(par));
            }
        }

        if (u.assignRoles(newRoles)) {
            userRoles = newRoles;
        }
    }
%>
<% if (!userName.isEmpty()) {%>

<div id='manage_roles_module' >

    <div class="user_info">
        <a href="/SAS/User/UserDetail"><h1><%=fullName%></h1></a>
        <span><%=officeAddress%></span>
        * <span><%=mobilePhoneNumber%></span>
        * <span><%=officePhoneNumber%></span> 
        * <span><%=email%></span>
    </div>

    <div id="roles_list" class="roles">
        <form method="POST" action="ManageRoles.jsp">
            <input type="submit" value="+ Save Changes">
            <a class="button" href="ManageRoles.jsp">Go Back</a>
            <br/><br/>
            <input type="hidden" name="userName" value="<%=userName%>">
            <table class="table striped" >
                <tr>
                    <td><input type="checkbox"   /></td>
                    <td>Role Name</td>
                    <td>Role Disc</td>

                </tr>
                <%
                    //get all the roles in the database
                    ArrayList<Role> allRoles = Role.getAllRole();
                    //get the codes of the roles the user have
                    ArrayList<String> roleCodeList = new ArrayList<String>();
                    if (userRoles != null) {
                        for (Role r : userRoles) {
                            roleCodeList.add(r.getRoleCode());
                        }
                    }
                    boolean x = false;
                    for (Role r : allRoles) {
                        x = true;
                        String roleCode = r.getRoleCode();
                        String roleName = r.getRoleName();
                        String roleDisc = r.getRoleDisc();

                        out.write("<tr class='role_item'>");
                        out.write("<td><input type='checkbox' name='" + roleCode + "' "
                                + "value='" + roleCode + "'");
                        if (roleCodeList.contains(roleCode)) {
                            out.write(" checked");
                        }
                        out.write(">");
                        out.write("</td> ");
                        out.write("<td>" + roleName + "</td> ");
                        out.write("<td>" + roleDisc + "</td> ");
                        out.write("</tr>");

                    }

                    if (!x) {
                        out.write("<tr><td colspan='3'>There are no roles defined in the database. Please define a role if you have permission or contact the administrator</td></tr>");
                    }
                %>
            </table>
        </form>
    </div>

</div>

<%} else {%>
<div >
    <div class="user_info">
        <h2>Choose user to manage roles.</h2>
    </div>
    <table class="table striped">
        <tr>
            <td class="width_15">User Name</td>
            <td>Full Name</td>
            <td>Department</td>
            <td></td>
        </tr>
        <%
            ArrayList<User> allUsers = User.getAllUsers();
            boolean x = false;
            for (User u : allUsers) {
                x = true;
                out.write("<tr>");
                out.write("<td>" + u.getUserName() + "</td>");
                out.write("<td>" + u.getFirstName() + " ");
                out.write(" " + u.getLastName() + "</td>");
                out.write("<td>" + u.getCoc().getCocName() + "</td>");
                out.write("<td>(" + u.getAddress().getOfficeBdg() + "-" + u.getAddress().getOfficeNumber() + ") - "
                        + u.getAddress().getMobilePhoneNumber() + " - " + u.getAddress().getMobilePhoneNumber()
                        + "</td>");
                out.write("<td><a class='button' "
                        + "href='ManageRoles.jsp?user=" + u.getUserName() + "'> Manage Roles </a> ");
                out.write("</tr>");
            }
            if (!x) {
                out.write("<tr><td colspan='4'>There is no data to show....</td></tr>");
            }

        %>
    </table>
</div>


<%}%>
<script src="/SAS/_script/tabs.js"></script>
<script type="text/javascript">
    stripTable();
</script>
<%
    out.write(p.getBottom());
%>
