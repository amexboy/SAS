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
    Page p = new Page(loggedInUser, "Item");
    out.write(p.getTop());

%>
<%    boolean rMode = false, eMode = false; // To deterimin in which mode is the user accessing....
    String itemCode = "", itemName = "", unitCode = "", categoryCode = "", itemDisc = "";

    boolean itemCodeError = false, itemNameError = false;//, itemDiscError = false;
    boolean rSuccess = false, eSuccess = false;

    if (request.getParameter("register") != null) {
        rMode = true;
        //Registration code....
        itemCode = request.getParameter("itemCode");//getting a request parameter sent from user
        itemName = request.getParameter("itemName");
        unitCode = request.getParameter("unitCode");
        categoryCode = request.getParameter("categoryCode");
        itemDisc = request.getParameter("itemDisc");

        Item item = new Item(itemCode, new Unit(unitCode), new Category(categoryCode), itemName, itemDisc);//create a new Item object
        Item.Validation itemVal = item.validate();//Validation is an inner class of Item

        if (itemVal.NO_ERROR) {//if validation goes successfull then save it........
            rSuccess = item.save(loggedInUser);
        } else {
            itemCodeError = itemVal.CODE_ERROR;
            itemNameError = itemVal.NAME_ERROR;
        }

        if (rSuccess) {
            out.write("<div class='success'>Successfully registered the item</div>");
        } else {
            out.write("<div class='error'>Something went wrong,pleas try again!<br/>"
                    + "Check if the Code exist!</div>");
        }
    } else if (request.getParameter("edit") != null) {
        eMode = true;
        //Editing Code......
        if (request.getParameter("itemCode") != null) {
            // this means the user is applying edit ......
            itemCode = request.getParameter("itemCode");
            try {
                Item item = new Item(itemCode);
                itemName = request.getParameter("itemName");
                unitCode = request.getParameter("unitCode");
                categoryCode = request.getParameter("categoryCode");
                itemDisc = request.getParameter("itemDisc");
                item.setItemName(itemName);
                item.setItemDisc(itemDisc);
                item.setUnit(new Unit(unitCode));
                item.setCategory(new Category(categoryCode));

                Item.Validation itemVal = item.validate();
                //Validaiton result....
                if (itemVal.NO_ERROR) {
                    eSuccess = item.save(loggedInUser);
                } else {
                    itemNameError = itemVal.NAME_ERROR;
                }

            } catch (NullPointerException ex) {
                itemCodeError = true;
            }

            if (eSuccess) {
                out.write("<div class='success'>Successfully updated the item</div>");
            } else {
                out.write("<div class='error'>Something went wrong,pleas try again!<br/>"
                        + "Check if the Code exist!/div>");
            }
        } else {
            // editing is started
            itemCode = request.getParameter("edit");
            try {
                Item item = new Item(itemCode);
                itemName = item.getItemName();
                unitCode = item.getUnit().getUnitCode();
                categoryCode = item.getCategory().getCategoryCode();
                itemDisc = item.getItemDisc();

            } catch (NullPointerException ex) {
                itemCode = itemName = itemDisc = "";
                eMode = false;
                out.write("<div class='error'>Unknown item or it is not in the database anymore!</div>");
            }
        }
    } else if (request.getParameter("delete") != null) {//user is deleting a Item
        try {
            Item item = new Item(request.getParameter("delete"));
            if (item.delete(loggedInUser)) {
                out.write("<script type='text/javascript'>alert('Item Is Deleted....')</script>");
            } else {
                out.write("<script type='text/javascript'>alert('Item Is not Deleted, item not found....')</script>");
            }
        } catch (NullPointerException ex) {
            itemCode = itemName = itemDisc = "";
            out.write("<script type='text/javascript'>alert('Item Is not Deleted, item not found....')</script>");
        }
    }

    if (eSuccess || rSuccess) {
        eMode = false;
        rMode = false;

        itemCode = itemName = itemDisc = "";
    }

%> 

<div id='tabs_link_container'></div>
<div id="item_module" class="tabbed"> 
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
                    out.write("<tr class='" + (totalInAllStores < 10 ? "error" : "") + "'>");
                    out.write("<tr>");
                    out.write("<td>" + c.getItemCode() + "</td>");
                    out.write("<td>" + c.getItemName() + "</td>");
                    out.write("<td>" + c.getCategory().getCategoryName() + "</td>");
                    out.write("<td>" + totalInAllStores + " " + c.getUnit().getUnitName() + "</td>");
                    out.write("<td><a class='button' "
                            + "href='Item.jsp?edit=" + c.getItemCode() + "'> Edit </a> ");
                    out.write("<a class='button error'  onclick=\"return confirm('Are you sure?')\" "
                            + "href='Item.jsp?delete=" + c.getItemCode() + "'> Delete </a>"
                            + "<a class='button' "
                            + "href='ItemDetail.jsp?item=" + c.getItemCode() + "'> Details </a> </td>");

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
    <div class="tab" title="<%= eMode ? "= Edit item" : "+ New item"%>">
        <form method="post" action="Item.jsp">
            <table id="maint" class="module_container">
                <tr>
                    <td class="button-container">
                        <%-- 
                            here the buttons name and value is changed if in edit mode...
                        --%>
                        <input type="submit" name="<%= eMode ? "edit" : "register"%>" value="<%= eMode ? "+ Save Change" : "+ Add Item"%>"/>
                        <a class="button" href="Item.jsp">Cancel</a>
                        <!--<input type="reset" value="Cancel"/>-->
                    </td>
                </tr>
                <tr>
                    <td class="centered">
                        Item Code: 
                        <%if (eMode) {%>
                        <b class="code"><%=itemCode%></b>
                        <input type="text" name="itemCode"
                               value ="<%=itemCode%>" 
                               hidden="true">
                        <%} else {%>
                        <input type="text" name="itemCode"
                               value ="<%=itemCode%>" 
                               class="<%= itemCodeError ? "error" : ""%>">

                        <%}%>
                        Item Name: <input type="text" name="itemName"
                                          value ="<%=itemName%>" 
                                          class="<%= itemNameError ? "error" : ""%>">
                        <br/>
                        Unit:
                        <select name="unitCode" >
                            <%                //get all call getAll
                                ArrayList<Unit> allUnits = Unit.getAllUnits();
                                boolean x2 = false;

                                //some code that looks like the ff
                                for (Unit u : allUnits) {
                                    x2 = true;
                                    out.write("<option value='" + u.getUnitCode() + "'>");
                                    out.write(u.getUnitName() + "</option>");
                                }
                                if (!x2) {
                                    out.write("<option value='null'>No Units's Registered</option>");
                                }
                            %>
                        </select>
                        Category:
                        <select name="categoryCode" >
                            <%                //get all call getAll
                                ArrayList<Category> allCategories = Category.getAllCategories();
                                boolean x3 = false;

                                //some code that looks like the ff
                                for (Category c : allCategories) {
                                    x3 = true;
                                    out.write("<option value='" + c.getCategoryCode() + "'>");
                                    out.write(c.getCategoryName() + "</option>");
                                }
                                if (!x3) {
                                    out.write("<option value='null'>No Categories's Registered</option>");
                                }
                            %>
                        </select>
                        <br/>
                        Item Description: <textarea name="itemDisc" maxlength="50"><%=itemDisc%></textarea>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script src="/SAS/_script/tabs.js"></script>
<script type="text/javascript">
    stripTable();
    <%
        if (eMode || rMode) {
            out.write("prepareAllTabs(1);");
        } else {
            out.write("prepareAllTabs(0);");
        }
    %>

</script>

<%
    out.write(p.getBottom());
%>
