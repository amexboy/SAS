<%@page import="java.util.Enumeration"%>
<%@page import="eitex.sas.module.Module"%>
<%@page import="java.util.ArrayList"%>
<%@page import="eitex.sas.role.Role"%>
<%@page import="eitex.pms.common.Page"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%> 
<%@include  file="../loginCheck.jsp" %> 
<%    String loggedInUser = null;
    try {
        loggedInUser = session.getAttribute("userName").toString();
    } catch (NullPointerException e) {
    }
    Page p = new Page(loggedInUser, "Assign Modules to Roles");
    out.write(p.getTop());
%>
<%
    String roleCode = "",roleName = "", roleDisc = "";

    ArrayList<Module> roleModules = null;
    if (request.getParameter("role") != null) {
        roleCode = request.getParameter("role");

        Role r = new Role(roleCode);
        roleName = r.getRoleName();
        roleDisc = r.getRoleDisc();
        roleModules = r.getModules();

    } else if (request.getParameter("roleCode") != null) {
        roleCode = request.getParameter("roleCode");

        Role r = new Role(roleCode);
        roleName = r.getRoleName();
        roleDisc = r.getRoleDisc();
        
        roleModules = r.getModules();

        ArrayList<Module> newModules = new ArrayList<Module>();
        Enumeration<String> e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String par = e.nextElement();
            if (!par.equals("roleCode")) {
                newModules.add(new Module(Integer.parseInt(par)));
            }
        }

        if (r.assignModules(newModules)) {
            roleModules = newModules;
        }
    }
%>

<% if (!roleName.isEmpty()) {%>
<div id="assign_roles_module">
    <div class="user_info">
        <h1><%=roleName%></h1>
        <span><%=roleDisc%></span>
    </div>

    <div id="roles_list" class="roles">
        <form method="POST" action="AssignModules.jsp">
            <input type="submit" value="+ Save Changes">
            <a class="button" href="AssignModules.jsp">Go Back</a>
            <br/><br/>
            <input type="hidden" name="roleCode" value="<%=roleCode%>">
            <table class="table striped" >
                <tr>
                    <td></td>
                    <td>Module Name</td>
                    <td>Module Description</td>

                </tr>
                <%
                    //get all the roles in the database
                    ArrayList<Module> allModules = Module.getAllModule();
                    //get the codes of the roles the user have
                    ArrayList<Integer> moduleCodeList = new ArrayList<Integer>();
                    if (roleModules != null) {
                        for (Module m : roleModules) {
//                            out.println(m.getModuleCode());
                            moduleCodeList.add(m.getModuleCode());
                        }
                    }
                    boolean x = false;
                    for (Module m : allModules) {
                        x = true;
                        int moduleCode = m.getModuleCode();
                        String moduleName = m.getModuleName();
                        String moduleURL = m.getModuleURL();

                        out.write("<tr class='role_item'>");
                        out.write("<td><input type='checkbox' name='" + moduleCode + "' "
                                + "value='" + moduleCode + "'");
                        if (moduleCodeList.contains(moduleCode)) {
                            out.write(" checked");
                        }
                        out.write(">");
                        out.write("</td> ");
                        out.write("<td>" + moduleName + "</td> ");
                        out.write("<td>" + moduleURL + "</td> ");
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
<div class="tab" title="+ View Roles">
    <table class="table striped">
        <tr>
            <td class="width_15">Role Code</td>
            <td>Role Name</td>
            <td>Role Description</td>
            <td></td>
        </tr>
        <%           ArrayList<Role> allRoles = Role.getAllRole();

            boolean x = false;
            for (Role r : allRoles) {
                x = true;
                out.write("<tr>");
                out.write("<td>" + r.getRoleCode() + "</td>");
                out.write("<td>" + r.getRoleName() + "</td>");
                out.write("<td>" + r.getRoleDisc() + "</td>");
                out.write("<td><a class='button' href='AssignModules.jsp?role=" + r.getRoleCode() + "'> Assign Modules </a></td>");
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
