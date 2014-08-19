<%
     boolean edit=false, deleted=false;
     String element = "";
    if(request.getParameter("element")!=null){
        element = request.getParameter("element"); 
        edit = (request.getParameter("edit")!=null)?true:false;
        deleted = (request.getParameter("delete")!=null)?true:false;
    }else{
        out.println("<script>"
                + "$(\".tab\").before(\"<div class='error ajax'>There is an error loading the table.</div>\");"
                + "</script>");
        return;
    }
%>
<script>
    var wnd, detailsTemplate, helpWindow;
    var crudServiceBaseUrl = "/SAS/Category/CategoryActions";
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
    var toolbars = edit && !deleted ? ["create", {text: "Import From Excel"}] : [];
    
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
                    id: "categoryCode",
                    fields: {
                        categoryCode: {editable: true, nullable: false, validation: {required: true}},
                        categoryName: {nullable: false, validation: {required: true}},
                        categoryDisc: {type: "string"}
                    }
                }
            },
            pageSize: 20,
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
            filterable: true,
            columnMenu: true,
            toolbar: toolbars,
            columns: [
                {field: "categoryCode", title: "Category Code", width: "100px", editor: categoryCodeEditor, template: "#= categoryCode #"},
                {field: "categoryName", title: "Category Name", editor: categoryNameEditor},
                {field: "categoryDisc", hidden: true, editor: categoryDiscEditor, title: "Category Description"},
                {command: commands, width: colWidth, title: "&nbsp;"}
            ],
            editable: editable
        });
        detailsTemplate = kendo.template($("#template").html());
        
        wnd = $("#details").kendoWindow({
            title: "Category Details",
            modal: true,
            visible: false,
            resizable: false,
            width: 500
        }).data("kendoWindow");

        helpWindow = $("#helpWindow").kendoWindow({
            title: "Import From Excel From",
            modal: true,
            visible: false,
            resizable: false,
            height: 400,
            width: 600
        }).data("kendoWindow");

        $(document).ajaxError(function(e) {
//            $(element).before("<div class='error ajax'>Your last operation did not compleate successfully.</div>");
//            $(".error.ajax").css({position: "relative"}).append("<span style='position:absolute;right:5px;top:2px'>x</span>").click(function() {
//                $(this).remove();
//            });
//            console.log(arguments);
        });
        $(document).ajaxSuccess(function(e, data) {
            var obj = JSON.parse(data.responseText);
            if (obj[0].error) {
                $(element).before("<div class='error ajax'>" + obj[0].message + "</div>");
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
    function categoryCodeEditor(container, options) {
        if (options.model.id === "") {
            $('<input class="k-input k-textbox" '
                    + 'name="' + options.field + '" '
                    + 'data-bind="value:' + options.field + '"'
                    + ' type="text" required />')
                    .appendTo(container);
        } else {
            $('<b>' + options.model.categoryCode + '</b>')
                    .appendTo(container);
        }
    }
    function categoryNameEditor(container, options) {
        $('<input class="k-input k-textbox" '
                + 'name="categoryName" '
                + 'data-bind="value:categoryName"'
                + ' type="text" required />')
                .appendTo(container);

    }
    function categoryDiscEditor(container, options) {
        $('<textarea class="k-content" data-bind="value:categoryDisc" name="categoryDisc" >' + options.model.categoryDisc + '</textarea>')
        $("<label>Description</lable>").appendTo(container).kendoEditor({
            tools: [
                "bold",
                "italic",
                "underline",
                "justifyLeft",
                "justifyCenter",
                "justifyRight",
                "insertUnorderedList",
                "insertOrderedList",
                "createLink",
                "unlink"
            ]
        });
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

        $.ajax({url: crudServiceBaseUrl + "?action=recover&models=" + dataItem.categoryCode, type: "POST",
            success: function(data) {
                var obj = JSON.parse(data);
                console.log(obj);
                if (obj[0].error) {
                    $(e.currentTarget).after("<div class='error'>" + obj[0].message + "</div>");
                    $(".error").css({position: "relative"}).append("<span style='position:absolute;right:5px;top:2px'>x</span>").click(function() {
                        $(this).remove();
                    });
                } else {
                    row.remove();
                }
            }, error: function(e) {
                $(e.currentTarget).after("<div class='error'>There was some error performing your request. Please try again later!</div>");
                $(".error").css({position: "relative"}).append("<span style='position:absolute;right:5px;top:2px'>x</span>").click(function() {
                    $(this).remove();
                });
            }
        });
    }
    function assignHeads(e) {
        e.defaultPrevented = true;
        e.preventDefault();
        var data = this.dataItem($(e.currentTarget).closest("tr"));
        assignHeadsWindow.content(assignHeadsTemplate(data));
        assignHeadsWindow.center().open();
        var usersDataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: "/SAS/User/actions.jsp",
                    dataType: "json"
                }
            },
            schema: {
                model: {
                    id: "userName",
                    fields: {
                        userName: {editable: false},
                        firstName: {type: "string"},
                        lastName: {type: "string"}
                    }
                }
            }

        });
        var headsCombo = $('<input name="headComboBox" type="hidden" value="' + data.categoryHead + '" style="width:300px"/>').appendTo($("#headComboBox")).kendoComboBox({
            dataSource: usersDataSource,
            dataTextField: "fullName",
            dataValueField: "userName",
            width: 300,
            template: '#= firstName # #= lastName # (#= category.categoryName #)',
            suggest: true,
            filter: "startswith"
        }).data("kendoComboBox");
        var viceCombo = $('<input name="viceComboBox" type="hidden" value="' + data.categoryVice + '" style="width:300px"/>').appendTo($("#viceComboBox")).kendoComboBox({
            dataSource: usersDataSource,
            dataTextField: "fullName",
            dataValueField: "userName",
            suggest: true,
            filter: "startswith"
        }).data("kendoComboBox");

        $('#assignHeadsSave').click(function(e) {
            var object = {
                categoryCode: data.categoryCode,
                headUserName: $("input[name=headComboBox]").val(),
                viceUserName: $("input[name=viceComboBox]").val()
            }
            var model = JSON.stringify(object);
            $.ajax({url: crudServiceBaseUrl + "?action=heads&models=" + model, type: "POST",
                success: function(data) {
                    var object = JSON.parse(data);
                    if (object[0].error) {
                        $("assignHeadsSave").before('<div class="error">' + object.message + '</div>');
                        $(".error").css({position: "relative"}).append("<span style='position:absolute;right:5px;top:2px'>x</span>").click(function() {
                            $(this).remove();
                        });
                    } else {
                        assignHeadsWindow.close();
                    }
                }, error: function() {
                    $("assignHeadsSave").before('<div class="error">There was an error sending your request!</div>');
                    $(".error").css({position: "relative"}).append("<span style='position:absolute;right:5px;top:2px'>x</span>").click(function() {
                        $(this).remove();
                    });
                }
            });
        });

        $('#assignHeadsCancel').click(function() {
            assignHeadsWindow.close();
        });
    }
</script> 
<div id="helpWindow">
    <h1>Caution</h1>
    <p>Before you proceed, please check if your excel table matches the following form</p>
    <table></table>
</div>
<script type="text/x-kendo-template" id="assignHeadsTemplate">
    <div class="assignHeadsWindow">
    <h1>#= categoryCode # : #= categoryName # </h1>
    <br/>
    <div id="headComboBox">Choose Head
    <div></div>
    </div>
    <div id="viceComboBox">Choose Vice
    <div></div>
    </div>

    <a class="k-button k-grid-update " id="assignHeadsSave"><span class="k-icon k-update"></span>Save Changes</a>
    <a class="k-button k-grid-update " id="assignHeadsCancel"><span class="k-icon k-cancel"></span>Cancel</a>

    </div>
</script>
<script type="text/x-kendo-template" id="template">
    <div id="details-container">
    <h1>#= categoryCode # : #= categoryName #</h1>
    <hr/>
    <br/>
    <br/>
    Description
    <div id="detail-disc">#= categoryDisc #</div>
    </div>
</script>
<div id="details"></div>
<div id="assignHeads"></div>
