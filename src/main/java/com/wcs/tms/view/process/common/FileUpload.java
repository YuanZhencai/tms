package com.wcs.tms.view.process.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.filenet.api.core.Document;
import com.google.common.collect.Lists;
import com.wcs.base.model.BaseEntity;
import com.wcs.base.service.EntityService;
import com.wcs.base.view.ViewBaseBean;
import com.wcs.common.filenet.ce.CEManager;
import com.wcs.common.filenet.ce.CEUtil;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.model.ProcessFile;
import com.wcs.tms.service.process.common.FileUploadService;
import com.wcs.tms.util.MessageUtils;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 文件上传</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
public class FileUpload<T extends BaseEntity> extends ViewBaseBean<T> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(FileUpload.class);

    /** 流程实例名称*/
    private String workClassName;

    @Inject
    protected EntityService entityService;
    @Inject
    protected CEManager ceManager;
    @Inject
    protected PEManager peManager;
    @Inject
    private FileUploadService fileUploadService;
    /** 上传以后的文档Id集合*/
    private List<String> docmentIdList = Lists.newArrayList();
    /** 文件List，为了性能加上此集合。多文件上传会循环调用handleFileUpload*/
    private List<ProcessFile> processFileList = Lists.newArrayList();
    /** 下载文件*/
    private ProcessFile processFile;
    protected boolean showDetailDoc;
    protected String delAllWorkNum;

    /**
     * 
     * <p>Description: 多文件上传</p>
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {
    	System.out.println("FileUpload.handleFileUpload()");
        UploadedFile file = event.getFile();
        try {
            if (file == null) {
                MessageUtils.addErrorMessage("msg", "请选择上传文件");
                return;
            }else{
            	// 文件名
                String fileName = file.getFileName();
                if (fileName != null && !"".equals(fileName)) {
                    String str[] = fileName.split(File.separator.replace("\\", "\\\\"));
                    fileName = str[str.length - 1];
                }
                for(ProcessFile pfile : processFileList){
                	if(pfile.getFileName().equals(fileName)){
                		MessageUtils.addErrorMessage("msg", "不可上传重复的文件");
                        return;
                	}
                }
            }
            
            
            Document doc = ceManager.createDocument(workClassName, file, peManager.getOS());
            docmentIdList.add(doc.get_Id().toString());
            addFile(doc);
            showDetailDoc = true;
            MessageUtils.addSuccessMessage("msg", file.getFileName() + "上传成功");
        } catch (Exception e) {
            log.error("handleFileUpload方法 多文件上传 出现异常：",e);
            MessageUtils.addErrorMessage("msg", "上传附件失败");
        }
    }

    /**
     * 
     * <p>Description:保存流程附件到数据库 </p>
     */
    public void saveProcessFile(String processId) {
        if (!processFileList.isEmpty()) {
            for (ProcessFile pfile : processFileList) {
                if (pfile.getId() == null) {
                    pfile.setProcInstId(processId);
                    this.entityService.create(pfile);
                } else {
                    pfile.setProcInstId(processId);
                    this.entityService.update(pfile);
                }
            }
        }
        processFileList.clear();
        docmentIdList.clear();
    }

    /**
     * 
     * <p>Description: 上传成功之后显示附件</p>
     */
    public void displayDocument(CloseEvent event) {
        if (docmentIdList.isEmpty()) { return; }
        try {
            showDetailDoc = true;
        } catch (Exception e) {
            log.error("displayDocument方法 上传成功之后显示附件 出现异常：",e);
        }

    }

    /**
     * 
     * <p>Description: 显示文件列表 通过查询应用系统</p>
     */
    public void displayDetailAttach(String workObjNumId) {
        if (workObjNumId == null || "".equals(workObjNumId)) {
            MessageUtils.addErrorMessage("msg", "工作流程实例编号为空");
        }
        processFileList.addAll(fileUploadService.findProceeFileList(workObjNumId));
    }

    /**
     * 
     * <p>Description: 清楚附件</p>
     */
    public void clearAttachmentt() {
        try {
            List<ProcessFile> fileList = fileUploadService.findProceeFileList(delAllWorkNum);
            if (fileList.isEmpty() && processFileList.isEmpty()) {
            	MessageUtils.addWarnMessage("msg", MessageUtils.getMessage("not_have_accessory_need_clear"));
                return;
            }
            if (!fileList.isEmpty()) {
                // 清楚CE管理的文档
                for (ProcessFile pf : fileList) {
                    Document document = CEUtil.fetchDocById(peManager.getOS(), pf.getCeDocId());
                    if (document != null) {
                        document.delete();
                    }
                }
            }
            // 清楚Pe的附件参数
            peManager.vwDeleteAttach(delAllWorkNum);
            processFileList.clear();
            // 清楚数据库记录
            fileUploadService.deleteProcessFile(delAllWorkNum);
            MessageUtils.addSuccessMessage("msg",MessageUtils.getMessage("delete_accessory_success"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("clearAttachmentt方法 清楚附件 出现异常：",e);
            MessageUtils.addErrorMessage("msg", "删除附件失败");
        }
    }

    /**
     * 
     * <p>Description: 删除附件</p>
     */
    public void deleteDocument() {
        if (processFile != null) {
            try {
                processFileList.remove(processFile);
                docmentIdList.remove(processFile.getCeDocId());
                if (processFile.getProcInstId() != null) {
                    fileUploadService.deleteProcessFile(processFile.getProcInstId(), processFile.getCeDocId());
                }
                Document document = CEUtil.fetchDocById(peManager.getOS(), processFile.getCeDocId());
                if (document != null) {
                    document.delete();
                }
                MessageUtils.addSuccessMessage("msg", "删除文件成功");
            } catch (Exception e) {
                log.error("deleteDocument方法 删除附件 出现异常：",e);
                MessageUtils.addErrorMessage("msg", "删除文件失败");
            }

        }
    }

    /**
     * 
     * <p>Description: </p>
     */
    public void downloadFile() {
        String id = processFile.getCeDocId();
        Document document = CEUtil.fetchDocById(peManager.getOS(), id);
        // 获取文档输入流
        InputStream is = document.accessContentStream(0);
        try {
            FileDownloadUtil.fileDonwload(processFile.getFileName(), is);
        } catch (FileNotFoundException e) {
            MessageUtils.addErrorMessage("msg", "附件文档不存在");
            log.error("downloadFile方法 下载文件 出现异常：",e);
        } catch (IOException e) {
            MessageUtils.addErrorMessage("msg", "附件文档下载失败");
            log.error("downloadFile方法 下载文件 出现异常：",e);
        }
    }

    /**
     * 
     * <p>Description:添加流程附件到集合 </p>
     * @param doc
     * @param workClassName
     */
    private void addFile(Document doc) {
        // 新建流程记录
        ProcessFile processFile = new ProcessFile();
        processFile.setCeDocId(doc.get_Id().toString());
        processFile.setFileName(doc.get_Name());
        processFile.setFileType(doc.get_MimeType());
        processFile.setCreater(doc.get_Creator());
        processFile.setFileVerson(doc.get_MajorVersionNumber());
        processFile.setUploadDate(doc.get_DateCreated());
        processFileList.add(processFile);
    }

    public List<String> getDocmentIdList() {
        return docmentIdList;
    }

    public void setDocmentIdList(List<String> docmentIdList) {
        this.docmentIdList = docmentIdList;
    }

    public String getWorkClassName() {
        return workClassName;
    }

    public void setWorkClassName(String workClassName) {
        this.workClassName = workClassName;
    }

    public List<ProcessFile> getProcessFileList() {
        return processFileList;
    }

    public void setProcessFileList(List<ProcessFile> processFileList) {
        this.processFileList = processFileList;
    }

    public boolean isShowDetailDoc() {
        return showDetailDoc;
    }

    public void setShowDetailDoc(boolean showDetailDoc) {
        this.showDetailDoc = showDetailDoc;
    }

    public ProcessFile getProcessFile() {
        return processFile;
    }

    public void setProcessFile(ProcessFile processFile) {
        this.processFile = processFile;
    }

    public String getDelAllWorkNum() {
        return delAllWorkNum;
    }

    public void setDelAllWorkNum(String delAllWorkNum) {
        this.delAllWorkNum = delAllWorkNum;
    }

}
