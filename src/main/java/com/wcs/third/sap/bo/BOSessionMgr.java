package com.wcs.third.sap.bo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;

import com.crystaldecisions.sdk.framework.CrystalEnterprise;
import com.crystaldecisions.sdk.framework.IEnterpriseSession;
import com.crystaldecisions.sdk.framework.ISessionMgr;

/**
 * BO报表session管理
 * @author liushengbin
 *
 */
public class BOSessionMgr {	

	public static String getToken() {
		String tokenEn = "";
		try {
			ISessionMgr sessionMgr = CrystalEnterprise.getSessionMgr();			
			IEnterpriseSession enterpriseSession = sessionMgr.logon("qastest2",
					"123", "10.11.1.127:6400", "secEnterprise");
			// url=http://<servername>:<port>/BOE/OpenDocument/opendoc/openDocument.jsp?<parameter1>&<parameter2>&...&<parameterN>
			String token = enterpriseSession.getLogonTokenMgr()
					.createLogonToken("", 120, 100);
			enterpriseSession.logoff();
			tokenEn = URLEncoder.encode(token, "UTF-8");		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tokenEn;

	}
	


	public static void openUrl(String url) throws IOException {
		// 打开到此 URL 的连接并返回一个用于从该连接读入的 InputStream。
		Reader reader = new InputStreamReader(new BufferedInputStream(new URL(
				url).openStream()));
		int c;
		while ((c = reader.read()) != -1) {
			System.out.print((char) c);
		}
		reader.close();
	}

	public static void main(String[] args) {
		System.out.println(getToken());
	}
}
