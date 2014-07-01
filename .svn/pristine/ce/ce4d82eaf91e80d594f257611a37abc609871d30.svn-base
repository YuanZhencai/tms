package com.wcs.common.filenet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.wcs.common.filenet.ce.CEUtil;
import com.wcs.common.filenet.env.Env;
import com.wcs.common.filenet.env.EnvInit1;
import com.wcs.common.filenet.util.ConfigLocationsUtil;
import com.wcs.tms.conf.xml.ProcessParseXmlUtil;
import com.wcs.tms.conf.xml.ProcessXmlUtil;

/**
 * <p>Project: tms</p>
 * <p>Description: 导入本地流程模板文件至FileNetCE环境</p>
 * <p>Copyright © 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
public class ImportPepFile {
    /** 流程定义文件类型*/
    private static final String DOCUMENT_CLASS = "WorkflowDefinition";
    //上传到FileNet CE中的目录
    public static final String PE_TEMPLATE_PATH = "/TMS/Process"; 
    private static Log logger = LogFactory.getLog(ImportPepFile.class);
    //本地流程定义文件的根目录（只搜索.pep文件）
    private static final String LOCAL_PATH = "D:/wilmar-svn/tms/doc/02_Engineering/05_Deployment/FileNet流程文件";
    
 
 
    
    /**
     * 
     * <p>Description: 上传文件</p>
     * @param workClassName
     * @param file
     * @return
     * @throws Exception
     */
    public static void createDocument(File file,String fileName, ObjectStore os) throws Exception {     
        try {
            if (file.isFile() && file.getName().lastIndexOf(".pep") != -1) {
                // 文件内容
                byte[] content = readDocContentFromFile(file);
                // 文件类型                
                Document doc = CEUtil.createDocWithContent(content, fileName, "application/x-filenet-workflowdefinition", os, DOCUMENT_CLASS);
                doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
                doc.save(RefreshMode.REFRESH);
                doc.refresh();
                
                // 将文档内容转移到对应文件夹下
                ReferentialContainmentRelationship rcr = CEUtil.fileObject(os, doc, PE_TEMPLATE_PATH);
                rcr.save(RefreshMode.NO_REFRESH);
            }
            
        } catch (Exception e) {
        	logger.error("createDocument方法 上传文件出现异常", e);
            throw e;
        }
    }
    
    
    
    /*
     * Reads the content from a file and stores it
     * in a byte array. The byte array will later be
     * used to create ContentTransfer object.
     */
    public static byte[] readDocContentFromFile(File f)
    {
        FileInputStream is;
        byte[] b = null;
        int fileLength = (int)f.length();
        if(fileLength != 0)
        {
            try
            {
                is = new FileInputStream(f);
                b = new byte[fileLength];
                is.read(b);
                is.close();
            }
            catch (FileNotFoundException e)
            {
            	logger.error("readDocContentFromFile方法出现异常", e);
            }
            catch (IOException e)
            {
            	logger.error("readDocContentFromFile方法出现异常", e);
            }
        }
        return b;
    }
    
    
   public static void main(String[] args)throws Exception {
        
        EnvInit1 init = new EnvInit1();
        init.initEnv();
        
        ProcessParseXmlUtil parseXmlUtil = new ProcessParseXmlUtil();
        parseXmlUtil.initXml();
        
        String[] pepFiles = ConfigLocationsUtil.getConfigLocationArray(LOCAL_PATH, ".pep",LOCAL_PATH);
        for (int i = 0; i < pepFiles.length; i++) {
            
            String filePath = pepFiles[i];
            File file = new File(filePath);
            String fileName = file.getName();
            String xmlFileName = fileName.substring(0,fileName.indexOf('.'));
            
            String fileName_zh = ProcessXmlUtil.getProcessAttribute("className",xmlFileName, "cePath");
            fileName_zh = StringUtils.replace(fileName_zh, "/TMS/Process/", "");
            logger.info("fileName_zh:"+fileName_zh);
            createDocument(file, fileName_zh,Env.fechObjectStore("TIHUAT1OBJ"));
        }
    }    
}


