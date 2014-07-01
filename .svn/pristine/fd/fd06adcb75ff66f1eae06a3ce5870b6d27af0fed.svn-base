package com.wcs.common.filenet.ce;

import java.io.File;
import java.io.Serializable;
import javax.ejb.Singleton;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.UploadedFile;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.wcs.base.util.DateUtil;
import com.wcs.tms.exception.ServiceException;

/**
 * 
 * <p>Project: myfilenet</p>
 * <p>Description: Ce文档操作方法</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Singleton
public class CEManager implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private static final String ROOT_PATH = File.separator + "TMS" + File.separator + "Attachment";
    /** 文件夹类*/
    private static final String FLODER_CLASS = "Folder";
    /** 文档类*/
    private static final String DOCUMENT_CLASS = "Document";

    private Log log = LogFactory.getLog(CEManager.class);
    /**
     * 
     * <p>Description: 上传文件</p>
     * @param workClassName
     * @param file
     * @return
     * @throws Exception
     */
    public Document createDocument(String workClassName, UploadedFile file, ObjectStore os) throws Exception {
        Validate.notNull(workClassName, "流程实例编号为空");
        Validate.notNull(file, "文件对象为空");
        try {
            String monthFolder = DateUtil.getNowStringDate().substring(0, 7);
            String filePath = checkFloder(workClassName, monthFolder, os);
            // 文件名
            String fileName = file.getFileName();
            if (fileName != null && !"".equals(fileName)) {
                String str[] = fileName.split(File.separator.replace("\\", "\\\\"));
                fileName = str[str.length - 1];
            }
            // 文件内容
            byte[] content = file.getContents();
            // 文件类型
            String fileType = this.getMimeType(fileName);
            Document doc = CEUtil.createDocWithContent(content, fileName, fileType, os, DOCUMENT_CLASS);
            doc.save(RefreshMode.REFRESH);
            // 将文档内容转移到对应文件夹下
            ReferentialContainmentRelationship rcr = CEUtil.fileObject(os, doc, filePath);
            rcr.save(RefreshMode.REFRESH);
            return doc;
        } catch (Exception e) {
        	log.error("createDocument方法 上传文件出现异常", e);
            throw e;
        }
    }

    /**
     * 
     * <p>Description: 检查文件是否存在，不存在则创建</p>
     * @param workClassName
     * @param monthFolder
     * @param os
     * @return
     */
    private String checkFloder(String workClassName, String monthFolder, ObjectStore os) {
        // 月度文件夹不存在就创建
        boolean mflag = CEUtil.existFolder(os, ROOT_PATH, monthFolder);
        if (!mflag) {
            CEUtil.createFolder(os, ROOT_PATH, monthFolder, FLODER_CLASS);
        }
        // 流程文件夹是否存在
        boolean wflag = CEUtil.existFolder(os, ROOT_PATH.concat(File.separator).concat(monthFolder), workClassName);
        if (!wflag) {
            String parentPath = ROOT_PATH.concat(File.separator).concat(monthFolder);
            CEUtil.createFolder(os, parentPath, workClassName, FLODER_CLASS);
        }
        return ROOT_PATH.concat(File.separator).concat(monthFolder).concat(File.separator).concat(workClassName);
    }

    /**
     * 
     * <p>Description: 根据文件名得到文档类型</p>
     * @param name
     * @return
     */
    private String getMimeType(String name) {
        if (name.lastIndexOf('.') == -1) { 
        	throw new ServiceException("name error" + name); 
        }
        String fileType = name.substring(name.lastIndexOf('.') + 1, name.length());
        if (fileType != null) {
            fileType = fileType.toLowerCase();
        }
        return MimeUtil.getMimeType(fileType);
    }
}
