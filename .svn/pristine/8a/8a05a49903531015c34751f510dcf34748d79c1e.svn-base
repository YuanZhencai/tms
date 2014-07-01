<%@page import="org.apache.derby.tools.sysinfo"%>
<%@ page language="java" import="java.util.*" 
   contentType="text/html;charset=utf-8"%>
<%@ page import="com.crystaldecisions.sdk.exception.SDKException" %>
<%@ page import="com.crystaldecisions.sdk.framework.IEnterpriseSession" %>
<%@ page import="com.crystaldecisions.sdk.occa.infostore.IInfoStore" %>
<%@ page import="com.crystaldecisions.sdk.occa.infostore.IInfoObject" %>
<%@ page import="com.crystaldecisions.sdk.occa.infostore.IInfoObjects" %>
<%@ page import="com.crystaldecisions.sdk.framework.ISessionMgr"%>
<%@ page import="com.crystaldecisions.sdk.framework.CrystalEnterprise"%>
<%@ page import="com.crystaldecisions.sdk.plugin.CeKind" %>
<%@page import="java.net.URLEncoder"%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>My JSP</title>
    
<%
			try{
   			 String doc_id="AYPl5yUPkzVChZG3SRnYxdk";
			ISessionMgr sessionMgr = CrystalEnterprise.getSessionMgr();
			  //enterpriseSession = sessionMgr.logon(username, password, cmsName, authType);
			  IEnterpriseSession enterpriseSession = sessionMgr.logon("qastest2","123", "10.11.1.127:6400", "secEnterprise");
			  //url=http://<servername>:<port>/BOE/OpenDocument/opendoc/openDocument.jsp?<parameter1>&<parameter2>&...&<parameterN>
            	String token = enterpriseSession.getLogonTokenMgr().createLogonToken("",120,100);
            	enterpriseSession.logoff();
            	String tokenEn = URLEncoder.encode(token, "UTF-8");
			  String URL= 
			"http://10.11.1.127:8080/BOE/OpenDocument/opendoc/openDocument.jsp?sIDType=CUID&iDocID="+doc_id+"&token="+tokenEn;
	        response.sendRedirect(URL);
			}catch(Exception e){
				e.printStackTrace();
			}
 %>

  </head>
  
</html>
