<%@page import="java.io.File"%>
<%@page import="java.util.ArrayList"%>
<%@page import="eitex.sas.module.Module"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="eitex.pms.common.Page"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%> 
<%@include  file="../loginCheck.jsp" %> 

<%    String loggedInUser = null;
    try {
        loggedInUser = session.getAttribute("userName").toString();
    } catch (NullPointerException e) {
    }
    Page p = new Page(loggedInUser, "Module");
    out.write(p.getTop());
%>
<%
    boolean rMode = false, eMode = false; // To deterimin in which mode is the user accessing....
    String moduleName = "", moduleURL = "";
    int moduleCode = 0;

    boolean moduleCodeError = false, moduleNameError = false, moduleURLError = false;
    boolean rSuccess = false, eSuccess = false;

    if (request.getParameter("register") != null) {
        rMode = true;
        //Registration code....
        moduleName = request.getParameter("moduleName");
        moduleURL = request.getParameter("moduleURL");

        Module module = new Module(0, moduleName, moduleURL);//create a new Module object
        Module.Validation moduleVal = module.validate();//Validation is an inner class of Module

        if (moduleVal.NO_ERROR) {//if validation goes successfull then save it........
            rSuccess = module.save(loggedInUser);
        } else {
            moduleNameError = moduleVal.NAME_ERROR;
            moduleURLError = moduleVal.URL_ERROR;
        }

        if (rSuccess) {
            out.write("<div class='success'>Successfully registered the module</div>");
        } else {
            out.write("<div class='error'>Something went wrong,pleas try again!<br/>"
                    + "Check if the Module Name exist!</div>");
        }
    } else if (request.getParameter("edit") != null) {
        eMode = true;
        //Editing Code......
        if (request.getParameter("moduleCode") != null) {
            // this means the user is applying edit ......
            moduleCode = Integer.parseInt(request.getParameter("moduleCode"));
            try {
                Module module = new Module(moduleCode);
                moduleName = request.getParameter("moduleName");
                moduleURL = request.getParameter("moduleURL");
                module.setModuleName(moduleName);
                module.setModuleURL(moduleURL);

                Module.Validation moduleVal = module.validate();
                //Validaiton result....
                if (moduleVal.NO_ERROR) {
                    eSuccess = module.save(loggedInUser);
                } else {
                    moduleNameError = moduleVal.NAME_ERROR;
                }

            } catch (NullPointerException ex) {
                moduleCodeError = true;
            }

            if (eSuccess) {
                out.write("<div class='success'>Successfully updated the module</div>");
            } else {
                out.write("<div class='error'>Something went wrong,pleas try again!<br/>"
                        + "Check if the Code exist!/div>");
            }
        } else {
            // editing is started
            moduleCode = Integer.parseInt(request.getParameter("edit"));
            try {
                Module module = new Module(moduleCode);
                moduleName = module.getModuleName();
                moduleURL = module.getModuleURL();

            } catch (NullPointerException ex) {
                moduleCode = 0;
                moduleName = moduleURL = "";
                eMode = false;
                out.write("<div class='error'>Unknown item or it is not in the database anymore!</div>");
            }
        }
    } else if (request.getParameter("delete") != null) {//user is deleting a Module
        try {
            moduleCode = Integer.parseInt(request.getParameter("delete"));
            Module module = new Module(moduleCode);
            if (module.delete(loggedInUser)) {
                out.write("<script type='text/javascript'>alert('Module Is Deleted....')</script>");
            } else {
                out.write("<script type='text/javascript'>alert('Module Is not Deleted, item not found....')</script>");
            }
        } catch (NullPointerException ex) {
            moduleCode = 0;
            moduleName = moduleURL = "";
            out.write("<script type='text/javascript'>alert('Module Is not Deleted, item not found....')</script>");
        }
    }

    if (eSuccess || rSuccess) {
        eMode = false;
        rMode = false;

        moduleCode = 0;
        moduleName = moduleURL = "";
    }

%> 
<!--//tabed.js, uses this dive to put the tab buttons--> 
<div id='tabs_link_container'></div>
<div id="module_module" class="tabbed"> 
    <div class="tab" title="+ View Modules">
        <table class="table striped">
            <tr>
                <td class="width_15">Module Code</td>
                <td>Module Name</td>
                <td>Module Description</td>
                <td></td>
            </tr>
            <%           
    ArrayList<Module> modules = Module.getAllModule();
                boolean x = false;

                for(Module m: modules) {
                    x = true;
                    out.write("<tr>");
                    out.write("<td>" + m.getModuleName() + "</td>");
                    out.write("<td>" + m.getModuleURL() + "</td>");
                    out.write("<td><a class='button' "
                            + "href='Module.jsp?edit=" + m.getModuleCode() + "'> Edit </a> ");
                    out.write("<a class='button error'  onclick=\"return confirm('Are you sure?')\" "
                            + "href='Module.jsp?delete=" + m.getModuleCode() + "'> Delete </a></td>");
                    out.write("</tr>");
                }
                //TODO: Need some way to copy the result set... can't close other wise!
                if (!x) {
                    out.write("<tr><td colspan='4'>There is no data to show....</td></tr>");
                }
            %>
        </table>
    </div>
    <div class="tab" title="<%= eMode ? "= Edit Module" : "+ New Module"%>">
        <form method="post" action="Module.jsp">
            <table id="maint" class="module_container">
                <tr>
                    <td class="button-container">
                        <%-- 
                            here the buttons name and value is changed if in edit mode...
                        --%>
                        <input type="submit" name="<%= eMode ? "edit" : "register"%>" value="<%= eMode ? "+ Save Change" : "+ Add Module"%>"/>
                        <a class="button" href="Module.jsp">Cancel</a>
                        <!--<input type="reset" value="Cancel"/>-->
                    </td>
                </tr>
                <tr>
                <input type="hidden" name="moduleCode"
                       value ="<%=moduleCode%>" 
                       class="<%= moduleCodeError ? "error" : ""%>">
                <td class="centered">
                    Module Name: <input type="text" name="moduleName"
                                        value ="<%=moduleName%>" 
                                        class="<%= moduleNameError ? "error" : ""%>">
                    <br/>
                    <select name="moduleURL"  >
                            <%
                                ArrayList<File> files = new ArrayList<File>();
                                Module.getModuleURLs(application.getRealPath("/"), files);
                                for (File f : files) {
                                    if (f.getPath().endsWith(".jsp")) {
                                        String path = "/SAS/" + f.getPath().substring(application.getRealPath("/").length()).replace("\\", "/");
                                        if (path.equals(moduleURL)) {
                                            out.write("<option selected='true'>");
                                        } else {
                                            out.write("<option>");
                                        }
                                        out.write(path);
                                        out.write("</option>");
                                    }
                                }
                            %>
                </select>
            </td>
            </tr>

        </table>
    </form>
</div>
</div>
<script src="/SAS/_script/tabs.js"></script>
<script type="text/javascript">
    stripTable();
    <%
        if (eMode || rMode) {
            out.write("prepareAllTabs(1);");
        } else {
            out.write("prepareAllTabs(0);");
        }
    %>

</script>

<%
    out.write(p.getBottom());
%>
