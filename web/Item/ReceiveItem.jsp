<%@page import="eitex.sas.reception.Reception"%>
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
    Page p = new Page(loggedInUser, "Item Reception");
    out.write(p.getTop());

%>
<%    boolean rMode = false, eMode = false; // To deterimin in which mode is the user accessing....
    String receiptCode = "", itemCode = "", itemName = "", unitName = "", storeCode = "", acceptedByName = "";
    int quantity = 0;

    boolean receiptCodeError = false, quantityError = false;
    boolean rSuccess = false, eSuccess = false;

    if (request.getParameter("register") != null) {
        rMode = true;
        //Registration code....
        //getting a request parameter sent from user
        receiptCode = request.getParameter("receiptCode");
        itemCode = request.getParameter("itemCode");
        storeCode = request.getParameter("storeCode");
        
        Item item = new Item(itemCode);
        itemName = item.getItemName();
        unitName = item.getUnit().getUnitName();
        User acceptedUser = new User(loggedInUser);
        acceptedByName = acceptedUser.getFirstName() + " " + acceptedUser.getLastName();
        
        out.write(receiptCode);
        out.write(itemCode);
        out.write(storeCode);
        try {
            quantity = Integer.parseInt(request.getParameter("quantity"));
        }catch(NumberFormatException ex){
            quantityError = true;
        }
        out.write(""+quantity);

        //create a new Reception object
        Reception reception = new Reception(receiptCode, item, new Store(storeCode), acceptedUser, quantity);
        //Validation is an inner class of Item
        Reception.Validation valRes = reception.validate();
        if (valRes.NO_ERROR) {//if validation goes successfull then save it........
            rSuccess = reception.save(loggedInUser);
        } else {
            quantityError = valRes.QUANTITY_ERROR;
            receiptCodeError = valRes.CODE_ERROR;
        }

        if (rSuccess) {
            out.write("<div class='success'>Successfully registered the item</div>");
        } else {
            out.write("<div class='error'>Something went wrong,pleas try again!</div>");
        }
    } else if (request.getParameter("receive") != null) {
        eMode = true;
        //Editing Code......
        itemCode = request.getParameter("receive");
        Item item = new Item(itemCode);
        itemName = item.getItemName();
        unitName = item.getUnit().getUnitName();
        User acceptedUser = new User(loggedInUser);
        acceptedByName = acceptedUser.getFirstName() + " " + acceptedUser.getLastName();
    }

    if (eSuccess || rSuccess) {
        eMode = false;
        rMode = false;

        itemCode = itemName = acceptedByName = "";
    }

%> 

<%if (!eMode && !rMode) {%>
<div class="tab" title="+ View Item">
    <table class="table striped">
        <tr>
            <td class="width_15">Item Code</td>
            <td>Item Name</td>
            <td>Unit</td>
            <td>Category</td>
            <td>Item Description</td>
            <td></td>
        </tr>
        <%                ArrayList<Item> allItems = Item.getAllItems();

            boolean x = false;
            for (Item c : allItems) {
                x = true;
                out.write("<tr>");
                out.write("<td>" + c.getItemCode() + "</td>");
                out.write("<td>" + c.getItemName() + "</td>");
                out.write("<td>" + c.getUnit().getUnitName() + "</td>");
                out.write("<td>" + c.getCategory().getCategoryName() + "</td>");
                out.write("<td>" + c.getItemDisc() + "</td>");
                out.write("<td><a class='button' "
                        + "href='ReceiveItem.jsp?receive=" + c.getItemCode() + "'> Receive </a> </td>");
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
    <form method="post" action="ReceiveItem.jsp">
        <table id="maint" class="module_container">
            <tr>
                <td class="button-container">
                    <%-- 
                        here the buttons name and value is changed if in edit mode...
                    --%>
                    <input type="submit" name="register" value="Save"/>
                    <a class="button" href="ReceiveItem.jsp">Cancel</a>
                </td>
            </tr>
            <tr>
                <td class="centered">
                    Receipt :<input type="text" name="receiptCode" value="<%=receiptCode%>">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <b>Accepted By: <%= acceptedByName%></b>
                    <br/>
                    Item Code :
                    <b class="code"><%=itemCode%></b>
                    <input type="text" name="itemCode"
                           value ="<%=itemCode%>" 
                           hidden="true">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    Item Name: <b class="code"> <%= itemName%></b>
                    <br/>
                    Quantity: <input type="number" name="quantity" min="1"
                                     class="<%=quantityError?"error":""%>"/> <%= unitName%> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    Store:
                    <select name="storeCode" >
                        <%                //get all call getAll
                            ArrayList<Store> allStores = Store.getAllStores();
                            boolean x3 = false;

                            //some code that looks like the ff
                            for (Store s : allStores) {
                                x3 = true;
                                out.write("<option value='" + s.getStoreCode() + "'>");
                                out.write(s.getStoreName() + "</option>");
                            }
                            if (!x3) {
                                out.write("<option value='null'>No Stores's Registered</option>");
                            }
                        %>
                    </select>
                </td>
            </tr>
        </table>
    </form>
</div>
<%}%>
<script src="/SAS/_script/tabs.js"></script>
<script type="text/javascript">
    stripTable();
</script>

<%
    out.write(p.getBottom());
%>
