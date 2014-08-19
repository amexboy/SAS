<%

    String defaultPath = "login.jsp";
    if (session.getAttribute("userName") != null) {
        session.setAttribute("userName", null);
    }
    response.sendRedirect(defaultPath);

%>