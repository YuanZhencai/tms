package com.wcs.common.controller.helper;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@WebServlet("/NotificationServlet")
public class NotificationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public NotificationServlet() {

	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			response.setContentType("text/xml; charset=UTF-8");
			response.setHeader("Cache-Control", "no-cache");
			String news = "";
			news += " <a href='http://www.chinahtml.com' target='_blank'>你有新邮件请注意查收</a> <br />";
			news += " <a href='http://www.chinahtml.com' target='_blank'>你有新消息请注意查收</a> ";

			String str = "<DIV style='BORDER-RIGHT: #455690 1px solid; BORDER-TOP: #a6b4cf 1px solid; Z-INDEX: 99999; LEFT: 0px; BORDER-LEFT: #a6b4cf 1px solid; WIDTH: "
					+ "221px; BORDER-BOTTOM: #455690 1px solid; POSITION: absolute; TOP: 0px; HEIGHT: "
					+ "155px; BACKGROUND-COLOR: #c9d3f3'>";
			str += "<TABLE style='BORDER-TOP: #ffffff 1px solid; BORDER-LEFT: #ffffff 1px solid' cellSpacing=0 cellPadding=0 width='100%' bgColor=#cfdef4 border=0>";
			str += "<TR><TD style='FONT-SIZE: 12px;COLOR: #0f2c8c' width='30' height='24'></TD>";
			str += "<TD style='PADDING-LEFT: 4px; FONT-WEIGHT: normal; FONT-SIZE: 12px; COLOR: #1f336b; PADDING-TOP: 4px' vAlign=center width='100%'>我的消息</TD>";
			str += "<TD style='PADDING-RIGHT: 2px; PADDING-TOP: 2px' vAlign=center align=right width=19>";
			str += "<SPAN title='关闭' style='FONT-WEIGHT: bold; FONT-SIZE: 12px; cursor:pointer; COLOR: red; MARGIN-RIGHT: 4px' id='btSysClose' onclick='javascript:bottomBar.hide();'>×</SPAN>";
			str += "</TD></TR>";
			str += "<TR><TD style='PADDING-RIGHT: 1px;PADDING-BOTTOM: 1px' colSpan='3' height='125'>";
			str += "<DIV style='BORDER-RIGHT: #b9c9ef 1px solid; PADDING-RIGHT: 8px; BORDER-TOP: #728eb8 1px solid; PADDING-LEFT: 8px; FONT-SIZE: 12px; PADDING-BOTTOM: 8px; BORDER-LEFT: #728eb8 1px solid; WIDTH: 100%; COLOR: #1f336b; PADDING-TOP: 8px; BORDER-BOTTOM: #b9c9ef 1px solid; HEIGHT: 125px;'>";
			str += "<DIV style='WORD-BREAK: break-all' align=left>" + news
					+ "</DIV>";
			str += "</DIV>";
			str += "</TD></TR></TABLE></DIV>";

			response.getWriter().print(str);
		} catch (Exception e) {
			response.getWriter().print("");
		}
	}
}
