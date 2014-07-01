package com.wcs.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.wcs.base.model.IdEntity;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Entity
@Table(name = "PROCESS_FILE")
public class ProcessFile extends IdEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** CE生成文档ID*/
    @Column(name = "CE_DOC_ID")
    private String ceDocId;

    /** 流程实例Id*/
    @Column(name = "PROC_INST_ID")
    private String procInstId;

    /** 文件上传人*/
    @Column(name = "CREATER")
    private String creater;

    /** 文件名称*/
    @Column(name = "FILE_NAME")
    private String fileName;

    /** 文件路径*/
    @Column(name = "FILE_PATH")
    private String filePath;

    /** 文件类型*/
    @Column(name = "FILE_TYPE")
    private String fileType;
    
    /** 文件版本*/
    @Column(name = "FILE_VERSON")
    private Integer fileVerson;
    
    /** 上传日期*/
    @Column(name = "UPLOAD_DATE")
    private Date uploadDate;
    

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Integer getFileVerson() {
        return fileVerson;
    }

    public void setFileVerson(Integer fileVerson) {
        this.fileVerson = fileVerson;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

  
    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getCeDocId() {
        return ceDocId;
    }

    public void setCeDocId(String ceDocId) {
        this.ceDocId = ceDocId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((ceDocId == null) ? 0 : ceDocId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
        	return true;
        }
        if (!super.equals(obj)) {
        	return false;
        }
        if (getClass() != obj.getClass()) {
        	return false;
        }
        ProcessFile other = (ProcessFile) obj;
        if (ceDocId == null) {
            if (other.ceDocId != null) {
            	return false;
            }
        } else if (!ceDocId.equals(other.ceDocId)) {
        	return false;
        }
        return true;
    }
    
  

}
