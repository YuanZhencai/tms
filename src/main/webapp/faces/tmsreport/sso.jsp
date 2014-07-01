
<%@page import="com.wcs.third.sap.bo.BOSessionMgr"%>
<%@ page language="java" import="java.util.*"
	contentType="text/html;charset=gb2312"%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>

<title>My JSP</title>

<%
	try {
		String doc_id = "AV_ONpAfZI1HmDhgS8xktlM";

		String URL = "http://10.11.1.127:8080/BOE/OpenDocument/opendoc/openDocument.jsp?sIDType=CUID&iDocID="
				+ doc_id + "&token="+BOSessionMgr.getToken(); 
		response.sendRedirect(URL);
	} catch (Exception e) {
		e.printStackTrace();
	}
%>

</head>

</html>
