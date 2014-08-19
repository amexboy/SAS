<%@page contentType="text/html" pageEncoding="UTF-8"%> 
<%@taglib prefix="amanu" uri="/WEB-INF/tlds/sas.tld" %>
<%    String loggedInUserName = "";
    try {
        loggedInUserName = session.getAttribute("userName").toString();
    } catch (NullPointerException e) {
    }
%>
<amanu:pagetop title="Home" userName="<%=loggedInUserName%>"></amanu:pagetop>

<div> 
    <h1>Welcome to the EiTex Property Management System </h1>
    <br/>
    <p>Designed by: <br/>
        <b>Amanuel Nega</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0400145&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0918221184<br/>
        <b>Mekdes Sahle</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0401056&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0912785673<br/>
        <b>Getaneh Mekonnen</b>&nbsp;2394/03&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0912213370<br/>
    </p>
</div>

<amanu:pagebottom></amanu:pagebottom>
