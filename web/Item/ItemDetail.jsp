<%@page import="java.util.HashMap"%>
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
    Page p = new Page(loggedInUser, "Item Details");
    out.write(p.getTop());

%>
<%    boolean itemChoosed = false;
    String itemCode = "", itemName = "", itemDisc = "", unitName = "", categoryName = "";
    int totalInStore = 0;
    if (request.getParameter("item") != null) {
        itemChoosed = true;
        itemCode = request.getParameter("item");

        Item item = new Item(itemCode);
        itemName = item.getItemName();
        itemDisc = item.getItemDisc();
        unitName = item.getUnit().getUnitName();
        categoryName = item.getCategory().getCategoryName();
        totalInStore = item.getTotalInAllStores();
    }
%>
<%if (!itemChoosed) {%>
<div class="tab" title="+ View Item">
    <table class="table striped">
        <tr>
            <td class="width_15">Item Code</td>
            <td>Item Name</td>
            <td>Category</td>
            <td>Total</td>
            <td></td>
        </tr>
        <%                ArrayList<Item> allItems = Item.getAllItems();

            boolean x = false;
            for (Item c : allItems) {
                x = true;
                int totalInAllStores = c.getTotalInAllStores();
                out.write("<tr class='"+ (totalInAllStores<10?"error":"")+"'>");
                out.write("<td>" + c.getItemCode() + "</td>");
                out.write("<td>" + c.getItemName() + "</td>");
                out.write("<td>" + c.getCategory().getCategoryName() + "</td>");
                out.write("<td>" + totalInAllStores + " " + c.getUnit().getUnitName() + "</td>");
                out.write("<td><a class='button' "
                        + "href='ItemDetail.jsp?item=" + c.getItemCode() + "'> Details </a> ");
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
<%} else {%>
<div>
    <div class="user_info">
        <h1><%=itemName%></h1>
        <span><%=itemCode%></span>
        * <span><%=categoryName%></span>
        * <span><%=totalInStore + " " + unitName + " left in all stores"%></span>
        <div class="discription"><%= itemDisc%></div> 
    </div>
    <table class="table striped">
        <tr>
            <td>Store Name</td>
            <td>Quantity</td>
        </tr>
        <%

            HashMap<Store, Integer> allQuantities = new Item(itemCode).getAllQuntities();

            boolean xs = false;
            for (Store c : allQuantities.keySet()) {
                xs = true;
                out.write("<tr class='"+(allQuantities.get(c)<10?"error":"")+"'>");
                out.write("<td>" + c.getStoreName() + "</td>");
                out.write("<td>" + allQuantities.get(c) + "</td>");
                out.write("</tr>");
            }

            if (!xs) {
                out.write("<tr><td colspan='4'>There is no data to show....</td></tr>");
            } else {
                out.write("");//TODO: paging is done here.....
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
