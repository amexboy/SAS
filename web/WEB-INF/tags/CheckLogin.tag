<%@tag description="Checks if a user is loged in or not" pageEncoding="UTF-8"%>
<%@tag import="eitex.sas.module.Module"%>
<%@tag import="eitex.sas.role.Role"%>
<%@tag import="java.util.ArrayList"%>
<%@tag import="eitex.sas.user.User"%>
<%@tag import="java.net.URLEncoder"%>
<%
    if (session.getAttribute("userName") == null) {
        //there is no user that is currently logged in, hence the user should be ridirected to the login page
        if (session.getAttribute("userName") == null) {
            String queryString = request.getQueryString() == null ? "" : request.getQueryString();
            String url = URLEncoder.encode(request.getRequestURI() + queryString, "UTF-8");
            url = url == null ? "" : url;
            //there is no user that is currently logged in, hence the user should be ridirected to the login page
            response.sendRedirect("/SAS/login.jsp?msg=You+have+to+login+first&url=" + url);
            return;
        }
    } 
//else {
//        //to check if the logged on user has permission to access the requested page....
//        User user = new User(session.getAttribute("userName").toString());
//        ArrayList<Role> roles = user.getRoles();
//        boolean found = false;
//        for (Role r : roles) {//for each Role r in roles
//            ArrayList<Module> modules = r.getModules();
//            String module = request.getRequestURI();
//            for (Module m : modules) {
//                if (m.getModuleURL().equals(module)) {
//                    found = true;
//                    break;
//                }
//            }
//            if(found){
//                break;
//            }
//        }
//        if(!found){
//            response.sendRedirect("/SAS/index.jsp");
//            return;
//        }

//    }

%>
