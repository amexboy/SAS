<%
     boolean edit=false, deleted=false;
     String element = "";
    if(request.getParameter("element")!=null){
        element = request.getParameter("element"); 
        edit = (request.getParameter("edit")!=null)?true:false;
        deleted = (request.getParameter("deleted")!=null)?true:false;
    }else{
        out.println("<script>"
                + "$(\".tab\").before(\"<div class='error ajax'>There is an error loading the table.</div>\");"
                + "</script>");
        return;
    }
%>
<script>
    var wnd, detailsTemplate, helpWindow;
    var crudServiceBaseUrl = "/SAS/User/UserActions";
    var element = "<%=element%>";
    var edit = <%=edit%>;
    var deleted = <%=deleted%>;
    var editable = edit ? "popup" : false;
    var colWidth = edit ? 260 : 160;
    colWidth = deleted && !edit ? colWidth + 50 : colWidth;
    colWidth = colWidth + "px";
    var commands = edit ? [{text: "View Details", click: showDetails}, "edit"]
            : [{text: "View Details", click: showDetails}];
    if (edit && !deleted) {
        commands.push("destroy");
    } else if (deleted) {
        commands.push({text: "Recover", click: recover});
    }
    var toolbars = edit ? ["create", {text: "Import From Excel"}] : [];
    $(document).ready(function() {
        var datasource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: crudServiceBaseUrl + (deleted ? "?action=getDeleted" : ""),
                    dataType: "json"
                },
                update: {
                    url: crudServiceBaseUrl + "?action=edit",
                    dataType: "json"
                },
                destroy: {
                    url: crudServiceBaseUrl + "?action=delete",
                    dataType: "json"
                },
                create: {
                    url: crudServiceBaseUrl + "?action=create",
                    dataType: "json"
                },
                parameterMap: function(options, operation) {
                    if (operation !== "read" && options.models) {
                        return {models: kendo.stringify(options.models)};
                    }
                }
            },
            schema: {
                model: {
                    id: "userName",
                    fields: {
                        userName: {nullable: false, validation: {required: true}},
                        firstName: {nullable: false, validation: {required: true}},
                        lastName: {nullable: false, validation: {required: true}},
                        password: {nullable: false, validation: {required: true}},
                        coc: {defaultValue: {cocCode: "", cocName: ""}, nullable: false, validation: {required: true}},
                        officeBuilding: {nullable: false, validation: {required: true}},
                        officeNumber: {nullable: false, validation: {required: true}},
                        mobilePhoneNumber: {nullable: false, validation: {required: true}},
                        officePhoneNumber: {nullable: false, validation: {required: true}},
                        email: {nullable: false, validation: {required: true}}
                    }
                }
            },
            pageSize: 4,
            batch: true
        });
        $(element).kendoGrid({
            dataSource: datasource,
            pageable: {
                input: false,
                numeric: true
            },
            height: 430,
            navigatable: true,
            reorderable: true,
            scrollable: true,
            sortable: true,
            groupable: true,
            filterable: true,
            columnMenu: true,
            toolbar: toolbars,
            columns: [
                {field: "userName", title: "User Name", width: "100px", groupable: false, editor: userNameEditor, template: "#= userName #"},
                {field: "firstName", title: "Name", editor: fullNameEditor, template: "#= firstName # #=lastName #"},
                {field: "coc.cocName", title: "CoC", editor: cocCodeEditor, template: "#= coc.cocName #"},
                {field: "address", title: "Address", width: 200, editor: addressEditor, filterable: false, groupable: false,
                    template: "<span class='icon mobile'></span> #= officeBuilding #-#= officeNumber # <div></div>" +
                            "<span class='icon mobile'></span> #= mobilePhoneNumber # / " +
                            "<span class='icon mobile'></span> #= officePhoneNumber # " +
                            "<span class='icon mobile'></span> #= email #"},
                {command: commands, width: colWidth, title: "&nbsp;"}
            ],
            editable: editable
        });
        detailsTemplate = kendo.template($("#template").html());
        wnd = $("#details").kendoWindow({
            title: "User Details",
            modal: true,
            visible: false,
            resizable: false,
            width: 400
        }).data("kendoWindow");
        helpWindow = $("#helpWindow").kendoWindow({
            title: "Import From Excel From",
            modal: true,
            visible: false,
            resizable: false,
            height: 400,
            width: 600
        }).data("kendoWindow");

//        $(document).ajaxError(function(e) {
//            console.log(arguments)
//            $(element).before("<div class='error ajax'>Your last operation did not compleate successfully.</div>");
//            $(".error.ajax").css({position: "relative"}).append("<span style='position:absolute;right:5px;top:2px'>x</span>").click(function() {
//                $(this).remove();
//            });
//        });
        $(document).ajaxComplete(function(e, data) {
            var obj = JSON.parse(data.responseText);
            console.log(data);
            if (obj[0].error) {
                $(element).before("<div class='error ajax'>" + obj[0].message + "</div>");
                $(".error.ajax").css({position: "relative"}).append("<span style='position:absolute;right:5px;top:2px'>x</span>").click(function() {
                    $(this).remove();
                });
            } else if (data.status !== 200) {
                $(element).before("<div class='error ajax'>Your last operation did not compleate successfully.</div>");
                $(".error.ajax").css({position: "relative"}).append("<span style='position:absolute;right:5px;top:2px'>x</span>").click(function() {
                    $(this).remove();
                });
            }
        });
        $(document).ajaxStart(function(e) {
            $(".error.ajax").remove();
        });

        $(".k-grid-ImportFromExcel").click(function() {
            importFromExcel();
        });
    });
    function userNameEditor(container, options) {
        if (options.model.id === "") {
            $('<input class="k-input k-textbox" '
                    + 'name="' + options.field + '" '
                    + 'data-bind="value:' + options.field + '"'
                    + ' type="text" required '
                    + ' placeholder="User Name" '
                    + ' />')
                    .appendTo(container);
            $('<input class="k-input k-textbox" '
                    + 'name="password" '
                    + 'data-bind="value:password"'

                    + ' type="password" required '
                    + ' placeholder="Password" '
                    + ' />')
                    .appendTo(container);
        } else {
            $('<b>' + options.model.userName + '</b>')
                    .appendTo(container);
        }
    }
    function cocCodeEditor(container, options) {
        $('<input required data-text-field="cocName" data-value-field="cocCode" data-bind="value:coc"/>')
                .appendTo(container)
                .kendoDropDownList({
                    autoBind: false,
                    dataTextField: "cocName",
                    dataValueField: "cocCode",
                    dataSource: {
                        transport: {
                            read: {
                                dataType: "json",
                                url: "/SAS/CoC/actions.jsp?get=get"
                            }
                        }
                    }
                });
    }
    function fullNameEditor(container, options) {
        $('<input class="k-input k-textbox" '
                + 'name="firstName" '
                + 'data-bind="value:firstName"'
                + ' type="text" required '
                + ' placeholder="First Name" '
                + ' />')
                .appendTo($("<lable>First Name</lable>").appendTo(container));
        $('<input class="k-input k-textbox" '
                + 'name="lastName" '
                + 'data-bind="value:lastName"'
                + ' type="text" required '
                + ' placeholder="Last Name" '
                + '  />')
                .appendTo($("<lable>Last Name</lable>").appendTo(container));
    }
    function addressEditor(container, options) {
        $('<input class="k-input k-textbox" '
                + 'name="officeBuilding" '
                + 'data-bind="officeBuilding"'
                + ' type="text" required '
                + ' placeholder="Office Building" '
                + ' />')
                .appendTo($("<lable>Office Building</lable>").appendTo($("<div></div>").appendTo(container)));
        $('<input class="k-input k-textbox" '
                + 'name="officeNumber" '
                + 'data-bind="officeNumber"'
                + ' type="text" required '
                + ' placeholder="Office Number" '
                + ' />')
                .appendTo($("<lable>Office Number</lable>").appendTo($("<div></div>").appendTo(container)));
        $('<input class="k-input k-textbox" '
                + 'name="mobilePhoneNumber" '
                + 'data-bind="mobilePhoneNumber"'
                + ' type="text" required '
                + ' placeholder="Mobile Phone Number" '
                + ' />')
                .appendTo($("<lable>Mobile Phone Number</lable>").appendTo($("<div></div>").appendTo(container)));
        $('<input class="k-input k-textbox" '
                + 'name="officePhoneNumber" '
                + 'data-bind="officePhoneNumber"'
                + ' type="text" required '
                + ' placeholder="Office Phone Number" '
                + ' />')
                .appendTo($("<lable>Office Phone Number</lable>").appendTo($("<div></div>").appendTo(container)));
        $('<input class="k-input k-textbox" '
                + 'name="email" '
                + 'data-bind="email"'
                + ' type="text" required '
                + ' placeholder="Email" '
                + ' />')
                .appendTo($("<lable>Email<div></div></lable>").appendTo($("<div></div>").appendTo(container)));
    }
    function showDetails(e) {
        e.preventDefault();
        var dataItem = this.dataItem($(e.currentTarget).closest("tr"));
        wnd.content(detailsTemplate(dataItem));
        wnd.center().open();
    }
    function importFromExcel() {
        helpWindow.center().open();
    }
    function recover(e) {
        e.preventDefault();
        var dataItem = this.dataItem($(e.currentTarget).closest("tr"));
        var row = $(e.currentTarget).closest("tr");
        console.log(dataItem);
        $.ajax({url: crudServiceBaseUrl + "?action=recover&models=" + dataItem.userName, type: "POST",
            success: function(data) {
                var obj = JSON.parse(data);
                
                if (!obj[0].error) {
                    row.remove();
                }
            }
        });
    }
</script>
<div id="helpWindow">
    <h1>Caution</h1>
    <p>Before you proceed, please check if your excel table matches the following form</p>
    <table></table>
</div>
<script type="text/x-kendo-template" id="template">
    <div id="details-container" 
    #= userName # : <h1>#= firstName # #= lastName # </h1>
    <h2>#= coc.cocName # (#=coc.cocCode #)</h2>
    <hr/>
    <br/>
    <h3>
    <span class='icon office'></span> #= officeBuilding #-#= officeNumber # 
    <hr/>
    <span class='icon mobile'></span> #= mobilePhoneNumber #  / 
    <span class='icon phone'></span> #= officePhoneNumber # 
    <hr/>
    <span class='icon email'></span> #= email #
    </h3>
    </div>
</script>
<div id="details"></div>
