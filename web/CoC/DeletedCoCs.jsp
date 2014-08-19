<%@page contentType="text/html" pageEncoding="UTF-8"%> 
<%@taglib uri="/WEB-INF/tlds/sas.tld" prefix="amanu" %>
<amanu:CheckLogin></amanu:CheckLogin>
<%    String loggedInUser = null;
    try {
        loggedInUser = session.getAttribute("userName").toString();
    } catch (NullPointerException e) {
    }
%>
<amanu:pagetop title="CoC" userName="<%=loggedInUser%>"></amanu:pagetop>
    <div id='tabs_link_container'></div>
    <div id="coc_module" class="tabbed"> 
        <div class="tab">
            <div id="tablePlace"></div>
        </div>
    </div>
    <script>
        $(document).ready(function() {
            $.post("/SAS/CoC/CoCElements.jsp",
                {element: "#tablePlace", edit: " ", deleted: " "},
                function(data) {
                    $("#tablePlace").append(data);
                }
            );
        });
    </script>
<amanu:pagebottom></amanu:pagebottom>
