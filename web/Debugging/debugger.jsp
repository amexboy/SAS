<%
    if (request.getParameter("ajax") != null) {
        int lastId = Integer.parseInt(request.getParameter("ajax"));
        out.write(serveAJAXGetLast(lastId));
    } else {
%>
<%@page import="java.util.Date" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.sql.ResultSet" %>
<%@page import="eitex.sas.common.ExceptionLogger" %>
<%@page import="java.sql.SQLException" %>
<%@page import="java.sql.PreparedStatement" %>
<%@page import="eitex.sas.data.ServerConnection" %>
<%@page import="java.sql.Connection" %>

<%@page contentType="text/html" pageEncoding="UTF-8" %>

<%@taglib prefix="amanu" uri="/WEB-INF/tlds/sas.tld" %>
<amanu:pagetop title="Debugger" userName=""></amanu:pagetop>
<div id="exceptionContainer">

</div>
<input type="button" id="refresh">
<script src="../_script/debug.js"></script>
<script>
    (function() {
        var refreshButton = document.getElementById("refresh");
        refreshButton.addEventListener("click", refresh, true);
        var interval = setInterval(refresh,1000);
        function refresh() {
            Debugger.getLog();
            <%--Debugger.printLog();--%>
        }
    }());
</script>
<%}%><%!
    public String serveAJAXGetLast(int lastId) {
        try {
            Connection con = ServerConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ExceptionLog WHERE id >= ? ORDER BY id;");
            ps.setInt(1,lastId);
            
            ResultSet rs = ps.executeQuery();
            StringBuilder sb = new StringBuilder("{\"time\":\"" + new Date().toString() + "\",\"exceptions\":[\"\"");
            while (rs.next()) {
                sb.append(", {\"id\":"+rs.getInt("id")+",\"exceptionType\":\"" + rs.getString("exceptionType") + "\","
                        + "\"exceptionMessage\":\"" + rs.getString("exceptionMessage").replace("\"", "'") + "\","
                        + "\"exceptionStackTrace\":\"" + rs.getString("exceptionStackTrace").replace("\n", "").replace("\"", "'") + "\","
                        + "\"seen\":" + rs.getString("seen") + "}");
            }
            sb.append("]}");
            return sb.toString().trim();
        } catch (SQLException ex1) {
            ExceptionLogger.log(ex1);
        }
        return "";
    }
%>
<amanu:pagebottom></amanu:pagebottom>