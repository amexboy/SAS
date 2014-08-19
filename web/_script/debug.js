/**
 * Created by Amanu on 7/17/2014.
 */

var Debugger = (function() {
    var lastId = 0;
    var $log = $("#exceptionContainer");


    return{
        /**
         * List of methods
         * Get exceptions
         * first get the top 10*lastId, then increment lastId
         */
        getLog: function() {
            $.post("/SAS/Debugging/debugger.jsp", {ajax: lastId},
            function(data) {
                var trace = JSON.parse(data);
                for (var i = 1; i <= trace.exceptions.length; i++) {
                    console.log(data)
                    var c = trace.exceptions[i];
                    $log.prepend(
                            '<div id="exception'+c.id+'" class="title">'+c.exceptionType + ":" + c.exceptionMessage+'</div>');
                    $log.prepend(
                            '<div id="exceptionTrace'+c.id+'" class="title">'+c.exceptionStackTrace+'</div>');
                    $("#exception"+c.id).click(function(){
                        $("#exceptionTrace"+c.id).slideToggle(1000);
                    });
                    lastId++;
                }
            }
            );
        }
    }
}());