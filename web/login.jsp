<%@page import="java.net.URLDecoder"%>
<%@page import="eitex.sas.user.User"%>
<%

    String defaultPath = "index.jsp";
    if (request.getParameter("url") != null ) {
        defaultPath = request.getParameter("url");
    }
    if (session.getAttribute("userName") != null) {
        response.sendRedirect(defaultPath);
    }
%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="amanu" uri="/WEB-INF/tlds/sas.tld" %>
<amanu:pagetop title="Login" userName=""></amanu:pagetop>
    <div id="login_module">
        <form method="post" class="login-form">
            <div class='messageBox'></div>

            Username <input type="text" name="userName" id="userName"
                            placeholder="User Name" 
                            required="required">
            <br/>
            Password <input type="password" name="password" id="password"
                            placeholder="Password" 
                            required="required">
            <br/>
            <input type="submit" name="login" value="Login"/>
            <a class="button cancel" href="#">Cancel</a>
        </form>

    </div>
    <script src="_script/jquery.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            var toUrl = "<%=URLDecoder.decode(defaultPath,"UTF-8")==null?"index.jsp":URLDecoder.decode(defaultPath,"UTF-8")%>";
            var loginForm = $(this), $userName = $("#userName", $(this))
                    , $password = $("#password", $(this))
                    , $messageBox = $(".messageBox", loginForm);
            $(".login-form").submit(function() {

                $.ajax({url: "/SAS/LoginActions", type: "POST",
                    data: {login: "", userName: $userName.val(), password: $password.val()},
                    success: function(data) {
                        console.log(data);
                        var error = data;//JSON.parse(data);
                        $messageBox.removeAttr("style")
                        if (error.error) {
                            $messageBox.removeClass("success");
                            $messageBox.addClass("error");
                            $messageBox.text(error.errorMessage);
                        } else {
                            $messageBox.removeClass("error");
                            $messageBox.addClass("success");
                            $messageBox.css({
                                backgroundImage: "url('/SAS/_images/loading.gif')",
                                backgroundRepeat: "no-repeat",
                            });
                            $messageBox.text("Success, Redirecting...");
                            setTimeout(1000, function() {
                                location = toUrl;
                            }());
                        }
                    }
                });
                return false;
            });
            $(document).ajaxStart(function() {
                $messageBox.removeAttr("style");
                $messageBox.css({
                    backgroundImage: "url('/SAS/_images/loading.gif')",
                    backgroundRepeat: "no-repeat",
                    padding: 10
                });
                $messageBox.text("Logging in, please wait!");
            });
            $(document).ajaxError(function() {
                $messageBox.removeAttr("style");
                $messageBox.addClass("error");
                $messageBox.text("Error connecting to the server, please try again!")
            });
            $(".button.cancel", $(".login-form")).click(function() {
                $messageBox.removeAttr("style");
                $messageBox.html("");
                $messageBox.removeAttr("class");
                $password.val("");
                $userName.val("");
                return false;
            });
        });
</script>

<amanu:pagebottom></amanu:pagebottom>