<%@page import="eitex.sas.borrow.Borrow"%>
<%@page import="eitex.pms.common.Page"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%> 
<%@include  file="../loginCheck.jsp" %> 
<%    String loggedInUser = null;
    try {
        loggedInUser = session.getAttribute("userName").toString();
    } catch (NullPointerException e) {
    }
    Page p = new Page(loggedInUser, "Borrow an Item");
    out.write(p.getTop());

%>
<div class="tab">
    <table class="table striped">
        <tr>
            <td>Borrow Code</td>
            <td>Item</td>
            <td>Quantity</td>
            <td>Borrowed By</td>
            <td>Given By</td>
            <td>Returned</td>
        </tr>
        <%                ArrayList<Borrow> allBorrows = Borrow.getAllBorrows();

            boolean x = false;
            for (Borrow c : allBorrows) {
                x = true;
                out.write("<tr class="+ (c.isReturned()?"success":"")+">");
                out.write("<td>" + c.getBorrowCode()+ "</td>");
                out.write("<td>" + c.getItem().getItemName()+ "</td>");
                out.write("<td>" + c.getQuantity() + " ");
                out.write(c.getItem().getUnit().getUnitName() + "</td>");
                out.write("<td>" + c.getBorrower().getFirstName() + " " + c.getBorrower().getLastName()+ "</td>");
                out.write("<td>" + c.getGivenBy().getFirstName() + " " + c.getGivenBy().getLastName()+ "</td>");
                out.write("<td>" + c.isReturned() +  "</td>");
                out.write("</tr>");
            }

            if (!x) {
                out.write("<tr><td colspan='4'>There is no data to show....</td></tr>");
            } else {
                out.write("");//TODO: paging is done here.....
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
