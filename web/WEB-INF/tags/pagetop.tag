<%-- 
    Document   : pagetop
    Created on : Jul 20, 2014, 11:21:21 AM
    Author     : Amanu
--%>

<%@tag description="The top part of the page" pageEncoding="UTF-8"%>
<%@tag import="eitex.sas.user.User" %>
<%@tag import="java.io.File" %>
<%@tag import="eitex.sas.role.Role" %>
<%@tag import="eitex.sas.module.Module" %>
<%@tag import="java.util.ArrayList" %>

<%@attribute name="userName"%>
<%@attribute name="title"%>
<!DOCTYPE html>
<html>
    <head>
        <title>${title}</title>
        <link href='/SAS/_style/main.css' rel='stylesheet' type='text/css'/>
        <link href="/SAS/_style/kendo.common.min.css" rel="stylesheet">
        <link href="/SAS/_style/kendo.default.min.css" rel="stylesheet">
        <link href="/SAS/_style/chosen.min.css" rel="stylesheet">
        <link href="/SAS/_style/examples-offline.css" rel="stylesheet">
        <script src="/SAS/_script/jquery.js"></script>
        <script src="/SAS/_script/chosen.jquery.js"></script>
        <script src="/SAS/_script/tabs.js"></script>
        <script src="/SAS/_script/kendo.web.min.js"></script>
    </head>
    <body >
        <div id='main-header' >
            <div id='nav'><ul>
                    <li>
                        <a href='/SAS/login.jsp'></a>
                    </li>
                    <%
                        ArrayList<File> files = new ArrayList<File>();
                        Module.getModuleURLs(application.getRealPath("/"), files);
                        for (File f : files) {
                            if (f.getPath().endsWith(".jsp")) {
                                String path = "/SAS/" + f.getPath().substring(application.getRealPath("/").length()).replace("\\", "/");
                                String[] a = path.split("/");
                                String Name = a[a.length-1].substring(0,a[a.length-1].lastIndexOf("."));
                                if(Name.contains("Elements"))
                                    continue;
                                out.write("<li><a href=\"");
                                out.write(path);
                                out.write("\">"+Name+"</a></li>");
                            }
                        }
                    %>
                    <!--                    <li>
                                            <a href='/SAS/CoC/CoC.jsp'>CoC</a>
                                        </li>
                                        <li>
                                            <a href='/SAS/CoC/AssignHeads.jsp'>Assign Heads</a>
                                        </li>
                                        <li>
                                            <a href='/SAS/User/User.jsp'>User</a>
                                        </li>
                                        <li>
                                            <a href='/SAS/Role/Role.jsp'>User</a>
                                        </li>-->
                </ul>
            </div>
            <%
                User user = null;
                if (userName != null && !userName.isEmpty()) {
                    user = new User(userName);
                    out.write("<b class='login'>" + user.getFirstName() + " " + user.getLastName() + " <a class='button' href='/SAS/logout.jsp'>Logout</a></b>");
                } else {
                    out.write("<b class='login'><a class='button' href='/SAS/login.jsp'>Login</a></b>");
                }
            %>
        </div>
        <div id='content-warper'>
            <div id='content'>
                <h1 id='page-title'>${title}</h1>