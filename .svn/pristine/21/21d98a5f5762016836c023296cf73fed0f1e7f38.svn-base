

<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<%
    String contextPath = request.getContextPath();
    String logoutUrl = request.getServletContext().getInitParameter("cas.logout.host");
    
    String url = contextPath + "/login.xhtml";
    session.invalidate();
    if (logoutUrl != null && logoutUrl.trim().length() > 0) {
        //带http开头的地址，是cas，直接跳转到cas登出地址
        if (logoutUrl.startsWith("http:") || logoutUrl.startsWith("https://")) {
            response.sendRedirect(logoutUrl);
            return;
        }
    }
    response.sendRedirect(url);
%>