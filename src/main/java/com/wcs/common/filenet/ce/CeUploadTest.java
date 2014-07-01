package com.wcs.common.filenet.ce;

import java.io.File;
import java.io.IOException;

import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.wcs.common.filenet.env.SysCfg;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: CE文档上传</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
public class CeUploadTest {
	
	private CeUploadTest() {
		
	}
	
    public static void main(String args[]) throws IOException {
        String ceUsername = SysCfg.getStrConfig("CE.filenetCeAdminUserName");
        String password = SysCfg.getStrConfig("CE.filenetCeAdminUserPasswd");
        String ceUrl = SysCfg.getStrConfig("CE.CEURL").replace("\\", "");
        String objectStore = SysCfg.getStrConfig("CE.ObjectStore");
        CEConnection ce = new CEConnection();
        ce.establishConnection(ceUsername, password, "FileNetP8", ceUrl);
        ObjectStore os = ce.fetchOS(objectStore);
        File file = new File("D:/IMG_20120118_122118.jpg");
        byte[] by = CEUtil.readDocContentFromFile(file);
        Document doc = CEUtil.createDocWithContent(by, "cdtest1", "text/plain", os, "Document");
        doc.save(RefreshMode.REFRESH);
        ReferentialContainmentRelationship rcr = CEUtil.fileObject(os, doc, "/test/cetest");
        rcr.save(RefreshMode.REFRESH);
    }
}
