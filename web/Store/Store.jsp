<%@page import="eitex.sas.store.Store"%>
<%@page import="java.util.ArrayList"%>
<%@page import="eitex.sas.coc.CoC"%>
<%@page import="eitex.sas.address.Address"%>
<%@page import="eitex.sas.user.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>  
<%@page import="eitex.pms.common.Page"%>
<%@page import="java.sql.ResultSet"%>
<%@include  file="../loginCheck.jsp" %>
<%    String LoggedInUserName = null;
    try {
        LoggedInUserName = session.getAttribute("userName").toString();
    } catch (NullPointerException e) {
    }
    Page p = new Page(LoggedInUserName, "Users");
    out.write(p.getTop());
%>

<%
    boolean rMode = false, eMode = false; // To deterimin in which mode is the user accessing....
    String storeCode = "", storeName = "", storeDisc = "";
    String officeBdg = "", officeNumber = "", officePhoneNumber = "", mobilePhoneNumber = "", email = "";

    boolean storeCodeError = false, storeNameError = false, storeDiscError = false;
    boolean officeBdgError = false, officeNumberError = false, officePhoneNumberError = false, mobilePhoneNumberError = false, emailError = false;

    boolean rSuccess = false, eSuccess = false;

    if (request.getParameter("register") != null) {
        rMode = true;

        storeCode = request.getParameter("storeCode");
        storeName = request.getParameter("storeName");
        storeDisc = request.getParameter("storeDisc");

        officeBdg = request.getParameter("officeBdg");
        officeNumber = request.getParameter("officeNumber");
        officePhoneNumber = request.getParameter("officePhoneNumber");
        
        //create a new object  of the needed class
        Address address = new Address(0, officeBdg, officeNumber, mobilePhoneNumber, officePhoneNumber, email);
        Store store = new Store(storeCode, storeName, storeDisc, address);

        //validate the object obj.validate
        Store.Validation valRes = store.validate();

        if (valRes.NO_ERROR) {//if validation goes successfull then save it........
            //save the object object.save
            rSuccess = store.save(LoggedInUserName);
        } else {
            //set error variables
            storeCodeError = valRes.STORE_CODE_ERROR;
            storeNameError = valRes.STORE_NAME_ERROR;
            storeDiscError = valRes.STORE_DISC_ERROR;
            if (valRes.ADDRESS_ERROR) {
                Address.Validation addVal = address.validate();
                officeBdgError = addVal.OFFICE_BUILDING_ERROR;
                officeNumberError = addVal.OFFICE_NUMBER_ERROR;
                officePhoneNumberError = addVal.OFFICE_PHONE_ERROR;
                mobilePhoneNumberError = addVal.MOBILE_NUMBER_ERROR;
                emailError = addVal.EMAIL_ERROR;
            }
        }
        if (rSuccess) {
            out.write("<div class='success'>Successfully registered the store</div>");
        } else {
            out.write("<div class='error'>Something went wrong, pleas try again</div>");
        }
    } else if (request.getParameter("edit") != null) {
        eMode = true;
        //Editing Code......
        if (request.getParameter("storeCode") != null) {
            // this means the user is applying edit ......

            //get the key parameter
            try {
                storeCode = request.getParameter("userName");
                Store store = new Store(storeCode);

                storeName = request.getParameter("firstName");
                storeDisc = request.getParameter("lastName");

                officeBdg = request.getParameter("officeBdg");
                officeNumber = request.getParameter("officeNumber");
                officePhoneNumber = request.getParameter("officePhoneNumber");

                store.setStoreName(storeName);
                store.setStoreDisc(storeDisc);
                Address address = store.getAddress();

                address.setOfficeBdg(officeBdg);
                address.setOfficeNumber(officeNumber);
                address.setOfficePhoneNumber(officePhoneNumber);
                address.setMobilePhoneNumber(mobilePhoneNumber);
                address.setEmail(email);
                store.setAddress(address);

                Store.Validation valRes = store.validate();
                if (valRes.NO_ERROR) {
                    //save the object object.save
                    eSuccess = store.save(LoggedInUserName);
                } else {
                    //set error variables
                    storeCodeError = valRes.STORE_CODE_ERROR;
                    storeNameError = valRes.STORE_CODE_ERROR;
                    storeDiscError = valRes.STORE_CODE_ERROR;
                    if (valRes.ADDRESS_ERROR) {
                        Address.Validation addVal = address.validate();
                        officeBdgError = addVal.OFFICE_BUILDING_ERROR;
                        officeNumberError = addVal.OFFICE_NUMBER_ERROR;
                        officePhoneNumberError = addVal.OFFICE_PHONE_ERROR;
                        mobilePhoneNumberError = addVal.MOBILE_NUMBER_ERROR;
                        emailError = addVal.EMAIL_ERROR;
                    }
                }
            } catch (NullPointerException ex) {
                //set error variable for the pk
            }

            if (eSuccess) {
                out.write("<div class='success'>Successfully updated the Store</div>");
            } else {
                out.write("<div class='error'>Something went wrong,pleas try again!<br/>"
                        + "Check if the Code exist!</div>");
            }
        } else {
//editing is starting
            try {
                storeCode = request.getParameter("edit");
                Store store = new Store(storeCode);

                storeName = store.getStoreName();
                storeDisc = store.getStoreDisc();
                Address address = store.getAddress();

                officeBdg = address.getOfficeBdg();
                officeNumber = address.getOfficeNumber();
                officePhoneNumber = address.getOfficePhoneNumber();
                mobilePhoneNumber = address.getMobilePhoneNumber();
                email = address.getEmail();
            } catch (NullPointerException ex) {
                //empty the fields
                eMode = false;//to get out of edit mode
                out.write("<div class='error'>Invalid item or it is not in the database anymore!</div>");
            }
        }
    } else if (request.getParameter("delete") != null) {//user is deleting a User
        try {
            storeCode = request.getParameter("delete");
            Store store = new Store(storeCode);

            if (store.delete(LoggedInUserName)) {
                out.write("<script type='text/javascript'>alert('Item Is Deleted....')</script>");
            } else {
                out.write("<script type='text/javascript'>alert('Item Is not Deleted, It was not found....')</script>");
            }
        } catch (NullPointerException ex) {
            //reset the fields
            out.write("<script type='text/javascript'>alert('Item Is not Deleted, Item not found....')</script>");
        }
    }

    if (eSuccess || rSuccess) {
        //to get out of edit or register mode
        eMode = false;
        rMode = false;

        storeCode = storeName = storeDisc = "";
        officeBdg = officeNumber = officePhoneNumber = mobilePhoneNumber = email = "";

        //reset the fields
    }

%> 
<!--//tabed.js, uses this div to put the tab buttons--> 
<div id='tabs_link_container'></div>
<div id="coc_module" class="tabbed"> 
    <div class="tab" title="+ View Users">
        <table class="table striped">
            <tr>
                <td class="width_15">Store Code</td>
                <td>Store Name</td>
                <td>Description</td>
                <td>Address</td>
                <td></td>
            </tr>
            <%                //get all call getAll
                ArrayList<Store> allStores = Store.getAllStores();
                boolean x = false;
                for (Store s : allStores) {
                    x = true;
                    out.write("<tr>");
                    out.write("<td>" + s.getStoreCode() + "</td>");
                    out.write("<td>" + s.getStoreName() + " ");
                    out.write(" " + s.getStoreDisc() + "</td>");
                    out.write("<td>(" + s.getAddress().getOfficeBdg() + "-" + s.getAddress().getOfficeNumber() + ") - "
                            + s.getAddress().getMobilePhoneNumber() + " - " + s.getAddress().getMobilePhoneNumber()
                            + "</td>");
                    out.write("<td><a class='button' "
                            + "href='Store.jsp?edit=" + s.getStoreCode() + "'> Edit </a> ");
                    out.write("<a class='button error'  onclick=\"return confirm('Are you sure?')\" "
                            + "href='Store.jsp?delete=" + s.getStoreCode() + "'> Delete </a></td> ");
                    out.write("</tr>");
                }
                if (!x) {
                    out.write("<tr><td colspan='4'>There is no data to show....</td></tr>");
                }
            %>
        </table>
    </div>

    <!--replace module by the module name-->
    <div class="tab" title="<%= eMode ? "= Edit Store" : "+ New Store"%>">
        <form method="post" action="Store.jsp">
            <table id="maint" class="module_container">
                <tr>
                    <td class="button-container">
                        <%-- 
                            here the buttons name and value is changed if in edit mode...
                        --%>

                        <input type="submit" name="<%= eMode ? "edit" : "register"%>" value="<%= eMode ? "+ Save Change" : "+ Add User"%>"/>
                        <a class="button" href="Store.jsp">Cancel</a>
                        <!--<input type="reset" value="Cancel"/>-->
                    </td>
                </tr>
                <tr>
                    <td class="centered">
                        <!--fields of the form that corresponding to the module-->

                        <table>
                            <tr>
                                <td>
                                    <table>
                                        <tr>
                                            <th>
                                                Personal Information
                                            </th>
                                        </tr>
                                        <tr>
                                            <td>
                                                <%if (eMode) {%>
                                                Store Code <b class="code"><%=storeCode%></b>
                                                <input type="text" name="storeCode"
                                                       value ="<%=storeCode%>" 
                                                       hidden="true">
                                                <%} else {%>
                                                Store Code: <input type="text" name="storeCode"
                                                                  value ="<%=storeCode%>" 
                                                                  class="<%=storeCodeError ? "error" : ""%>">

                                                <%}%>
                                            </td>
                                        </tr> 
                                        <tr>
                                            <td>
                                                Store Name: <input type="text" name="storeName"
                                                                   value="<%= storeName%>" 
                                                                   class="<%= storeNameError ? "error" : ""%>">
                                            </td>
                                        </tr> 
                                        <tr >
                                            <td colspan="2">
                                                Store Descripton <textarea name="storeDisc" maxlength="50"><%=storeDisc%></textarea>
                                            </td>
                                        </tr> 
                                    </table>
                                </td>
                                <td>
                                    <table>
                                        <tr>
                                            <th>
                                                Address Information
                                            </th>
                                        <tr>
                                        <tr>
                                            <td>
                                                Office Building: <input type="text" name="officeBdg" 
                                                                        value="<%= officeBdg%>" 
                                                                        class="<%= officeBdgError ? "error" : ""%>">
                                            </td>
                                        </tr> 
                                        <tr>
                                            <td>
                                                Office Number <input type="text" name="officeNumber" 
                                                                     value="<%= officeNumber%>" 
                                                                     class="<%= officeNumberError ? "error" : ""%>">
                                            </td>
                                        </tr> 
                                        <tr>
                                            <td>
                                                Office Phone Number: <input type="text" name="officePhoneNumber" 
                                                                            value="<%= officePhoneNumber%>" 
                                                                            class="<%= officePhoneNumberError ? "error" : ""%>">
                                            </td>
                                        </tr> 

                                    </table>
                                </td>
                            </tr> 
                        </table>
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


