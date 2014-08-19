<%@page import="eitex.sas.reception.Reception"%>
<%@page import="eitex.pms.common.Page"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%> 
<%@include  file="../loginCheck.jsp" %> 
<%    String loggedInUser = null;
    try {
        loggedInUser = session.getAttribute("userName").toString();
    } catch (NullPointerException e) {
    }
    Page p = new Page(loggedInUser, "Item Reception");
    out.write(p.getTop());

%>
<div class="tab" title="+ View Item">
    <table class="table striped">
        <tr>
            <td class="width_15">Reception Code</td>
            <td>Item</td>
            <td>Quantity</td>
            <td>Accepted By</td>
        </tr>
        <% ArrayList<Reception> allReceptions = Reception.getAllReceptions();
            boolean x = false;
            for (Reception c : allReceptions) {
                x = true;
                out.write("<tr>");
                out.write("<td>" + c.getReceiptCode() + "</td>");
                out.write("<td>" + c.getItem().getItemName() + "</td>");
                out.write("<td>" + c.getQuantity() + " ");
                out.write(c.getItem().getUnit().getUnitName() + "</td>");
                out.write("<td>" + c.getAcceptedBy().getFirstName() + " " + c.getAcceptedBy().getLastName() + "</td>");
                out.write("</tr>");
            }

            if (!x) {
                out.write("<tr><td colspan='4'>There is no data to show....</td></tr>");
            } else {
                out.write("");
            }
        %>
    </table>
</div>
<script src="/SAS/_script/tabs.js"></script>
<script type="text/javascript">
    stripTable();
</script>
<%
    out.write(p.getBottom());
%>
