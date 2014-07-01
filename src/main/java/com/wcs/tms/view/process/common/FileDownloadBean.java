package com.wcs.tms.view.process.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.filenet.api.core.Document;
import com.google.common.collect.Lists;
import com.wcs.base.util.JSFUtils;
import com.wcs.common.filenet.ce.CEManager;
import com.wcs.common.filenet.ce.CEUtil;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.model.ProcessFile;
import com.wcs.tms.service.process.bankcredit.BankCreditService;
import com.wcs.tms.service.process.common.FileUploadService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.entity.FileVo;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 文件下载</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@ManagedBean
@ViewScoped
public class FileDownloadBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(FileDownloadBean.class);
    
    /** 是否查看详细*/
    private boolean showDetail;
    /** 流程实例编号*/
    private String workObjNum;
    /** 当前需要下载的文件对象*/
    private FileVo fileVo;
    private ProcessFile processFile;
    /** 文件详细List*/
    private List<FileVo> fileList = Lists.newArrayList();
    private List<ProcessFile> processFileList = Lists.newArrayList();
    @Inject
    protected CEManager ceManager;
    @Inject
    protected PEManager peManager;
    @Inject
    private FileUploadService fileUploadService;

    @PostConstruct
    public void init() {
        Object audit = JSFUtils.getRequest().getAttribute("op");
        Object workclassNumber = JSFUtils.getRequest().getAttribute("procInstId");
        // 是否是审批和查看
        if (audit != null && workclassNumber != null) {
            String wcnum = String.valueOf(workclassNumber.toString());
            this.workObjNum = wcnum;
            displayDetailAttach();
        }
    }

    /**
     * 
     * <p>Description: 显示申请人所上传的附件列表，通过查询PE的节点附件</p>
     */
    public void displayFile() {
        try {
            fileList.clear();
            if (workObjNum == null || "".equals(workObjNum)) {
            	MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("process_number_isNull"));
            }
            // 通过流程实例查询流程附件文档ID集合
            List<String> docIdList = this.peManager.vwFindDocIdByAttach(workObjNum);
            // 通过ID查询CE的文档
            List<Document> documentList = findStepAttachList(workObjNum, docIdList);
            if (!documentList.isEmpty()) {
                docToFileVo(documentList);
            }
        } catch (Exception e) {
            log.error("displayFile方法 显示申请人所上传的附件列表，通过查询PE的节点附件 出现异常：",e);
        }
    }

    /**
     * 
     * <p>Description: 显示文件列表 通过查询应用系统</p>
     */
    public void displayDetailAttach() {
        if (workObjNum == null || "".equals(workObjNum)) {
        	MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("process_number_isNull"));
        }
        processFileList.addAll(fileUploadService.findProceeFileList(workObjNum));
    }

    /**
     * 
     * <p>Description: 清楚附件</p>
     */
    public void clearAttachmentt() {
        try {
            List<ProcessFile> fileList = fileUploadService.findProceeFileList(workObjNum);
            if (fileList.isEmpty()) {
            	MessageUtils.addWarnMessage("msg", MessageUtils.getMessage("del_attachment_suc"));
                return;
            }
            // 清楚CE管理的文档
            for (ProcessFile pf : fileList) {
                Document document = CEUtil.fetchDocById(peManager.getOS(), pf.getCeDocId());
                if (document != null) {
                    document.delete();
                }
            }
            // 清楚Pe的附件参数
            peManager.vwDeleteAttach(workObjNum);
            // 清楚数据库记录
            fileUploadService.deleteProcessFile(workObjNum);
        	MessageUtils.addWarnMessage("msg", MessageUtils.getMessage("del_attachment_suc"));           
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("clearAttachmentt方法 清楚附件 出现异常：",e);
            MessageUtils.addSuccessMessage("msg", "删除附件失败");
        }
    }

    public void deleteDocument() {
        if (processFile != null) {
            this.processFileList.remove(processFile);
            Document document = CEUtil.fetchDocById(peManager.getOS(), processFile.getCeDocId());
            if (document != null) {
                document.delete();
            }
        }
    }

    /**
     * 
     * <p>Description: 文件附件下载</p>
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
            log.error("downloadFile方法 文件附件下载 出现异常：",e);
        } catch (IOException e) {
            MessageUtils.addErrorMessage("msg", "附件文档下载失败");
            log.error("downloadFile方法 文件附件下载 出现异常：",e);
        }
    }

    /**
     * 
     * <p>Description: 得到当前节点下的附件文档</p>
     * @param workObjNum 工作流程实例号
     * @return
     * @throws Exception
     */
    public List<Document> findStepAttachList(String workObjNum, List<String> docIdList) {
    	List<Document> documentList = Lists.newArrayList();
        try {
            if (docIdList.isEmpty()) { return documentList; }
            for (String id : docIdList) {
                Document doc = CEUtil.fetchDocById(this.peManager.getOS(), id);
                documentList.add(doc);
            }
        } catch (Exception e) {
        	log.error("findStepAttachList方法 得到当前节点下的附件文档 出现异常：",e);
        }
        return documentList;
    }

    /**
     * 
     * <p>Description: 将文件转成Vo对象</p>
     * @param documentList
     */
    private void docToFileVo(List<Document> documentList) {
        for (Document doc : documentList) {
            FileVo file = new FileVo();
            file.setFileId(doc.get_Id().toString());
            file.setFileName(doc.get_Name());
            file.setUploadDate(doc.get_DateCreated());
            file.setUserName(doc.get_Creator());
            fileList.add(file);
        }
    }

    public List<FileVo> getFileList() {
        return fileList;
    }

    public void setFileList(List<FileVo> fileList) {
        this.fileList = fileList;
    }

    public String getWorkObjNum() {
        return workObjNum;
    }

    public void setWorkObjNum(String workObjNum) {
        this.workObjNum = workObjNum;
    }

    public boolean isShowDetail() {
        return showDetail;
    }

    public void setShowDetail(boolean showDetail) {
        this.showDetail = showDetail;
    }

    public FileVo getFileVo() {
        return fileVo;
    }

    public void setFileVo(FileVo fileVo) {
        this.fileVo = fileVo;
    }

    public List<ProcessFile> getProcessFileList() {
        return processFileList;
    }

    public void setProcessFileList(List<ProcessFile> processFileList) {
        this.processFileList = processFileList;
    }

    public ProcessFile getProcessFile() {
        return processFile;
    }

    public void setProcessFile(ProcessFile processFile) {
        this.processFile = processFile;
    }

}
