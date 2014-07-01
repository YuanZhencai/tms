package com.wcs.common.filenet.ce;

import com.filenet.api.core.Document;
import com.filenet.api.core.ObjectStore;
import com.wcs.common.filenet.env.SysCfg;

/**
 * 
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description: CE文档下载测试
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
public class CeDownloadTest {

	private CeDownloadTest() {

	}

	public static void main(String args[]) {
		String ceUsername = SysCfg.getStrConfig("CE.filenetCeAdminUserName");
		String password = SysCfg.getStrConfig("CE.filenetCeAdminUserPasswd");
		String ceUrl = SysCfg.getStrConfig("CE.CEURL").replace("\\", "");
		String objectStore = SysCfg.getStrConfig("CE.ObjectStore");
		CEConnection ce = new CEConnection();
		ce.establishConnection(ceUsername, password, "FileNetP8", ceUrl);
		ObjectStore os = ce.fetchOS(objectStore);
		Document doc = CEUtil.fetchDocById(os,
				"{9F608615-585E-4EB9-959F-A5CD22A0345A}");
		String path = "D:/";
		CEUtil.writeDocContentToFile(doc, path);
	}
}
