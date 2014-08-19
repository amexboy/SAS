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
    Page p = new Page(loggedInUser, "Item Borrow");
    out.write(p.getTop());

%>
<%    boolean rMode = false, eMode = false; // To deterimin in which mode is the user accessing....
    String borrowCode = "", itemCode = "", itemName = "", unitName = "", storeCode = "", borrowerName = "", borrowerFullName = "", givenByName = "";
    int quantity = 0;

    boolean quantityError = false;
    boolean rSuccess = false, eSuccess = false;

    if (request.getParameter("register") != null) {
        rMode = true;
        //Registration code....
        //getting a request parameter sent from user
        itemCode = request.getParameter("itemCode");
        storeCode = request.getParameter("storeCode");
        borrowerName = request.getParameter("borrowerName");

        User givenBy = new User(loggedInUser);
        User borrower = new User(borrowerName);
        givenByName = givenBy.getFirstName() + " " + givenBy.getLastName();
        borrowerFullName = borrower.getFirstName() + " " + borrower.getLastName();
        try {
            quantity = Integer.parseInt(request.getParameter("quantity"));
        } catch (NumberFormatException ex) {
            quantity = 0;
            quantityError = true;
        }

        //create a new Borrow object
        Borrow borrow = new Borrow(0, new Item(itemCode), new Store(storeCode), borrower, givenBy, quantity, false);
        //Validation is an inner class of Item
        Borrow.Validation valRes = borrow.validate();
        if (valRes.NO_ERROR) {//if validation goes successfull then save it........
            rSuccess = borrow.save();
        } else {
            quantityError = valRes.QUANTITY_ERROR;
        }

        if (rSuccess) {
            out.write("<div class='success'>Successfully registered the borrow</div>");
        } else {
            out.write("<div class='error'>Something went wrong,pleas try again!</div>");
        }
    } else if (request.getParameter("borrow") != null) {
        eMode = true;
        //Editing Code......
        borrowerName = request.getParameter("borrow");
        User borrower = new User(borrowerName);
        borrowerFullName = borrower.getFirstName() + " " + borrower.getLastName();
        User givenBy = new User(loggedInUser);
        givenByName = givenBy.getFirstName() + " " + givenBy.getLastName();
    }

    if (eSuccess || rSuccess) {
        eMode = false;
        rMode = false;

        itemCode = itemName = givenByName = borrowerFullName = "";
    }

%> 

<%if (!eMode && !rMode) {%>
<div>
    <table class="table striped">
        <tr>
            <td class="width_15">Item Code</td>
            <td>Item Name</td>
            <td>Unit</td>
            <td>Category</td>
            <td>Item Description</td>
            <td></td>
        </tr>
        <%
            ArrayList<User> allUsers = User.getAllUsers();
            boolean x = false;
            for (User u : allUsers) {
                x = true;
                out.write("<tr>");
                out.write("<td>" + u.getUserName() + "</td>");
                out.write("<td>" + u.getFirstName() + " ");
                out.write(" " + u.getLastName() + "</td>");
                out.write("<td>" + u.getCoc().getCocName() + "</td>");
                out.write("<td>(" + u.getAddress().getOfficeBdg() + "-" + u.getAddress().getOfficeNumber() + ") - "
                        + u.getAddress().getMobilePhoneNumber() + " - " + u.getAddress().getMobilePhoneNumber()
                        + "</td>");
                out.write("<td><a class='button' "
                        + "href='BorrowItem.jsp?borrow=" + u.getUserName() + "'> Borrow </a></td> ");
                out.write("</tr>");
            }
            if (!x) {
                out.write("<tr><td colspan='4'>There is no data to show....</td></tr>");
            }
        %>
    </table>
</div>
<%} else {%>
<div>
    <form method="post" action="BorrowItem.jsp">
        <table id="maint" class="module_container">
            <tr>
                <td class="button-container">
                    <input type="submit" name="register" value="Save"/>
                    <a class="button" href="BorrowItem.jsp">Cancel</a>
                </td>
            </tr>
            <tr>
                <td class="centered">
                    <b>Given By: <%= givenByName%></b>
                    <br/>
                    <b>Borrower Name By: <%= borrowerFullName%></b>
                    <input type="text" name="borrowerName"
                           value ="<%=borrowerName%>" 
                           hidden="true">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    Item:
                    <select name="itemCode" >
                        <%                //get all call getAll
                            ArrayList<Item> allItems = Item.getAllItems();
                            boolean x3 = false;

                            //some code that looks like the ff
                            for (Item s : allItems) {
                                x3 = true;
                                out.write("<option value='" + s.getItemCode() + "'>");
                                out.write(s.getItemName() + "</option>");
                            }
                            if (!x3) {
                                out.write("<option value='null'>No Stores's Registered</option>");
                            }
                        %>
                    </select>                    
                    <br/>
                    Quantity: <input type="number" name="quantity" min="1"
                                     value="<%= quantity%>"
                                     class="<%=quantityError ? "error" : ""%>"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    Store:
                    <select name="storeCode" >
                        <%                //get all call getAll
                            ArrayList<Store> allStores = Store.getAllStores();
                            boolean x4 = false;

                            //some code that looks like the ff
                            for (Store s : allStores) {
                                x4 = true;
                                out.write("<option value='" + s.getStoreCode() + "'>");
                                out.write(s.getStoreName() + "</option>");
                            }
                            if (!x4) {
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
