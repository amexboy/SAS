<%@page import="java.util.ArrayList"%>
<%@page import="eitex.sas.unit.Unit"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="eitex.pms.common.Page"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%> 
<%@include  file="../loginCheck.jsp" %> 
<%    String loggedInUser = null;
    try {
        loggedInUser = session.getAttribute("userName").toString();
    } catch (NullPointerException ex) {
    }
    Page p = new Page(loggedInUser,"Unit");
    out.write(p.getTop());
%>

<%
    boolean rMode = false, eMode = false; // To deterimin in which mode is the user accessing....
    String unitCode = "", unitName = "", unitDisc = "";

    boolean unitCodeError = false, unitNameError = false;//, unitDiscError = false;
    boolean rSuccess = false, eSuccess = false;

    if (request.getParameter("register") != null) {
        rMode = true;
        //Registration code....
        unitCode = request.getParameter("unitCode");//getting a request parameter sent from user
        unitName = request.getParameter("unitName");
        unitDisc = request.getParameter("unitDisc");

        Unit unit = new Unit(unitCode, unitName, unitDisc);//create a new Unit object
        Unit.Validation unitVal = unit.validate();//Validation is an inner class of Unit

        if (unitVal.NO_ERROR) {//if validation goes successfull then save it........
            rSuccess = unit.save(loggedInUser);
        } else {
            unitCodeError = unitVal.CODE_ERROR;
            unitNameError = unitVal.NAME_ERROR;
        }

        if (rSuccess) {
            out.write("<div class='success'>Successfully registered the unit</div>");
        } else {
            out.write("<div class='error'>Something went wrong,pleas try again!<br/>"
                    + "Check if the Code exist!</div>");
        }
    } else if (request.getParameter("edit") != null) {
        eMode = true;
        //Editing Code......
        if (request.getParameter("unitCode") != null) {
            // this means the user is applying edit ......
            unitCode = request.getParameter("unitCode");
            try {
                Unit unit = new Unit(unitCode);
                unitName = request.getParameter("unitName");
                unitDisc = request.getParameter("unitDisc");
                unit.setUnitName(unitName);
                unit.setUnitDisc(unitDisc);

                Unit.Validation unitVal = unit.validate();
                //Validaiton result....
                if (unitVal.NO_ERROR) {
                    eSuccess = unit.save(loggedInUser);
                } else {
                    unitNameError = unitVal.NAME_ERROR;
                }

            } catch (NullPointerException ex) {
                unitCodeError = true;
            }

            if (eSuccess) {
                out.write("<div class='success'>Successfully updated the unit</div>");
            } else {
                out.write("<div class='error'>Something went wrong,pleas try again!<br/>"
                        + "Check if the Code exist!/div>");
            }
        } else {
            // editing is started
            unitCode = request.getParameter("edit");
            try {
                Unit unit = new Unit(unitCode);
                unitName = unit.getUnitName();
                unitDisc = unit.getUnitDisc();

            } catch (NullPointerException ex) {
                unitCode = unitName = unitDisc = "";
                eMode = false;
                out.write("<div class='error'>Unknown item or it is not in the database anymore!</div>");
            }
        }
    } else if (request.getParameter("delete") != null) {//user is deleting a Unit
        try {
            Unit unit = new Unit(request.getParameter("delete"));
            if (unit.delete(loggedInUser)) {
                out.write("<script type='text/javascript'>alert('Unit Is Deleted....')</script>");
            } else {
                out.write("<script type='text/javascript'>alert('Unit Is not Deleted, item not found....')</script>");
            }
        } catch (NullPointerException ex) {
            unitCode = unitName = unitDisc = "";
            out.write("<script type='text/javascript'>alert('Unit Is not Deleted, item not found....')</script>");
        }
    }

    if (eSuccess || rSuccess) {
        eMode = false;
        rMode = false;

        unitCode = unitName = unitDisc = "";
    }

%> 

<div id='tabs_link_container'></div>
<div id="unit_module" class="tabbed"> 
    <div class="tab" title="+ View Unit">
        <table class="table striped">
            <tr>
                <td class="width_15">Unit Code</td>
                <td>Unit Name</td>
                <td>Unit Description</td>
                <td></td>
            </tr>
            <%                ArrayList<Unit> units = Unit.getAllUnits();
                boolean x = false;

                for(Unit u: units) {
                    x = true;
                    out.write("<tr>");
                    out.write("<td>" + u.getUnitCode() + "</td>");
                    out.write("<td>" + u.getUnitName() + "</td>");
                    out.write("<td>" + u.getUnitDisc() + "</td>");
                    out.write("<td><a class='button' "
                            + "href='Unit.jsp?edit=" + u.getUnitCode() + "'> Edit </a> ");
                    out.write("<a class='button error'  onclick=\"return confirm('Are you sure?')\" "
                            + "href='Unit.jsp?delete=" + u.getUnitCode() + "'> Delete </a></td>");
                    out.write("</tr>");
                }
                //TODO: Need some way to copy the result set... can't close other wise!
                if (!x) {
                    out.write("<tr><td colspan='4'>There is no data to show....</td></tr>");
                }
            %>
        </table>
    </div>
    <div class="tab" title="<%= eMode ? "= Edit Unit" : "+ New Unit"%>">
        <form method="post" action="Unit.jsp">
            <table id="maint" class="module_container">
                <tr>
                    <td class="button-container">
                        <%-- 
                            here the buttons name and value is changed if in edit mode...
                        --%>
                        <input type="submit" name="<%= eMode ? "edit" : "register"%>" value="<%= eMode ? "+ Save Change" : "+ Add Unit"%>"/>
                        <a class="button" href="Unit.jsp">Cancel</a>
                        <!--<input type="reset" value="Cancel"/>-->
                    </td>
                </tr>
                <tr>
                    <td class="centered">
                        Unit Code: 
                        <%if (eMode) {%>
                        <b class="code"><%=unitCode%></b>
                        <input type="text" name="unitCode"
                               value ="<%=unitCode%>" 
                               hidden="true">
                        <%} else {%>
                        <input type="text" name="unitCode"
                               value ="<%=unitCode%>" 
                               class="<%= unitCodeError ? "error" : ""%>">

                        <%}%>
                        Unit Name: <input type="text" name="unitName"
                                         value ="<%=unitName%>" 
                                         class="<%= unitNameError ? "error" : ""%>">
                        <br/>
                        Unit Description: <textarea name="unitDisc" maxlength="50"><%=unitDisc%></textarea>
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

        if (eMode||rMode) {
            out.write("prepareAllTabs(1);");
        } else {
            out.write("prepareAllTabs(0);");
        }
    %>

</script>

<%
    out.write(p.getBottom());
%>


