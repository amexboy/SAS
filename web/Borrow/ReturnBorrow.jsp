<%@page import="eitex.sas.borrow.Borrow"%>
<%@page import="eitex.sas.store.Store"%>
<%@page import="eitex.sas.category.Category"%>
<%@page import="eitex.sas.unit.Unit"%>
<%@page import="java.util.ArrayList"%>
<%@page import="eitex.sas.item.Item"%>
<%@page import="eitex.pms.common.Page"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%> 
<%@include  file="../loginCheck.jsp" %> 
<%    String loggedInUser = null;
    try {
        loggedInUser = session.getAttribute("userName").toString();
    } catch (NullPointerException e) {
    }
    Page p = new Page(loggedInUser, "Return an Item");
    out.write(p.getTop());

%>
<%   
    
    if (request.getParameter("return") != null) {
        int borrowCode = Integer.parseInt(request.getParameter("return"));
        if (new Borrow(borrowCode).returnBorrow()) {
            out.write("<div class='success'>Successfully registered the borrow</div>");
        } else {
            out.write("<div class='error'>Something went wrong,pleas try again!</div>");
        }
    } 

%> 


<div>
    <table class="table striped">
        <tr>
            <td>Item</td>
            <td>Quantity</td>
            <td>Borrowed By</td>
            <td>Given By</td>
            <td></td>
        </tr>
        <%                ArrayList<Borrow> allBorrows = Borrow.getAllBorrows();

            boolean x = false;
            for (Borrow c : allBorrows) {
                if(c.isReturned()){
                    continue;
                }
                x = true;
                out.write("<tr class="+ (c.isReturned()?"success":"")+">");
                out.write("<td>" + c.getBorrower().getFirstName() + " " + c.getBorrower().getLastName()+ "</td>");
                out.write("<td>" + c.getItem().getItemName()+ "</td>");
                out.write("<td>" + c.getQuantity() + " ");
                out.write(c.getItem().getUnit().getUnitName() + "</td>");
                out.write("<td>" + c.getGivenBy().getFirstName() + " " + c.getGivenBy().getLastName()+ "</td>"); 
                out.write("<td><a class='button' onclick=\"return confirm('Are you sure this Item is returned?')\"" 
                        + "href='ReturnBorrow.jsp?return=" + c.getBorrowCode()+ "'> Return </a></td> ");
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
