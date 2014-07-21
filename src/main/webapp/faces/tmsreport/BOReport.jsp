<%@page import="com.wcs.base.service.LoginService"%>
<%@page import="com.crystaldecisions.celib.properties.URLEncoder"%>
<%@ page language="java" contentType="text/html;charset=utf-8"%>

<%@ page import="com.crystaldecisions.sdk.framework.CrystalEnterprise"%>
<%@ page import="com.crystaldecisions.sdk.framework.ISessionMgr"%>
<%@ page import="com.crystaldecisions.sdk.framework.IEnterpriseSession"%>
<%@ page import="com.crystaldecisions.sdk.occa.security.ILogonTokenMgr"%>
<%@ page import="com.crystaldecisions.sdk.exception.SDKException" %>
<%@ page import="com.crystaldecisions.sdk.exception.SDKException.Serialization" %>

<html>
  <head>
    <title>BO report</title>
  </head>
  <body>
<%  
String docID = request.getParameter("docID");

String CMS = "10.11.1.127";
IEnterpriseSession enterpriseSession = (IEnterpriseSession)session.getAttribute("enterpriseSession");

if (enterpriseSession == null) {
	String userName = "TMS_BI_SSO_user";
	String password = "123";
	String port = ":6400";
	String auth = "secEnterprise";
	
	try {
		ISessionMgr sessionMgr = CrystalEnterprise.getSessionMgr();
		enterpriseSession = sessionMgr.logon(userName, password, CMS+port, auth);
		
		session.setAttribute("enterpriseSession", enterpriseSession);
	} catch (SDKException e) {
		Serialization ser = new Serialization(e);
		String errorMessage = ser.getMessage();
		out.println(errorMessage);
		return;
	}
	out.println("new session");
} else {
	out.println("old session");
}


String sapCode = LoginService.getCurrentUserSapCode();
String token = enterpriseSession.getLogonTokenMgr().createWCAToken("",1,1); 


String reportUrl = "http://"+CMS+":8080/BOE/OpenDocument/opendoc/openDocument.jsp?sIDType=CUID&iDocID="+docID+"&lsMsapcode="+sapCode+"&token="+token;


response.sendRedirect(reportUrl);
%>
  </body>
</html>

