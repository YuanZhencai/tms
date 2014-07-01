package com.wcs.tms.view.process.regicapital;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.wcs.base.service.EntityService;
import com.wcs.common.filenet.ce.CEConnection;
import com.wcs.common.filenet.ce.CEUtil;
import com.wcs.common.filenet.env.SysCfg;
import com.wcs.tms.model.ProcessFile;

@ManagedBean
@ViewScoped
public class CeDemoBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(CeDemoBean.class);
    
    @Inject
    private EntityService entityService;

    public void handleFileUpload(FileUploadEvent event) {

        String ceUsername = SysCfg.getStrConfig("CE.filenetCeAdminUserName");
        String password = SysCfg.getStrConfig("CE.filenetCeAdminUserPasswd");
        String ceUrl = SysCfg.getStrConfig("CE.CEURL").replace("\\", "");
        String objectStore = SysCfg.getStrConfig("CE.ObjectStore");
        CEConnection ce = new CEConnection();
        ce.establishConnection(ceUsername, password, "FileNetP8", ceUrl);

        ObjectStore os = ce.fetchOS(objectStore);
        UploadedFile file = event.getFile();
        Document doc = CEUtil.createDocWithContent(file.getContents(), file.getFileName(), "text/plain", os, "Document");
        doc.save(RefreshMode.REFRESH);
        ReferentialContainmentRelationship rcr = CEUtil.fileObject(os, doc, "/test/cetest");
        rcr.save(RefreshMode.REFRESH);
        // 保存记录到应用数据库
        ProcessFile processFile = new ProcessFile();
        processFile.setCeDocId(doc.get_Id().toString());

        processFile.setFileName(doc.get_Name());
        processFile.setFileType(doc.get_MimeType());
        
        this.entityService.create(processFile);
        log.info(event.getFile().getFileName());
    }
}
