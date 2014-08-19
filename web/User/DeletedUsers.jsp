<%@page contentType="text/html" pageEncoding="UTF-8"%>  
<%@page import="eitex.sas.address.AddressFieldException"%>
<%@page import="eitex.sas.user.UserFieldException"%>
<%@page import="java.util.ArrayList"%>
<%@page import="eitex.sas.coc.CoC"%>
<%@page import="eitex.sas.address.Address"%>
<%@page import="eitex.sas.user.User"%>
<%@taglib prefix="amanu" uri="/WEB-INF/tlds/sas.tld" %>
<amanu:CheckLogin></amanu:CheckLogin>
<%    String loggedInUserName = null;
    try {
        loggedInUserName = session.getAttribute("userName").toString();
    } catch (NullPointerException e) {
    }
%>
<amanu:pagetop title="User" userName="<%=loggedInUserName%>"></amanu:pagetop>
    <div id='tabs_link_container'></div>
    <div id="user_module" class="tabbed"> 
        <div class="tab">
            <div id="tablePlace"></div>
        </div>
    </div>
    <script>
        $.post("/SAS/User/UserElements.jsp",
            {element:"#tablePlace",edit:" ",deleted:" "},
            function(data) {
                    $("#tablePlace").append(data);
            }
        );
    </script>
<amanu:pagebottom></amanu:pagebottom>
<%--<amanu:UserTable element="#tablePlace" edit="true" deleted="false"></amanu:UserTable>--%>

