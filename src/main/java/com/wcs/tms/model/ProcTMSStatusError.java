package com.wcs.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.wcs.base.model.IdEntity;

/**
 * TMS状态回传，异常数据记录表
 * @since 2014-07-21
 * @author liushengbin
 * 
 */
@Entity
@Table(name = "PROC_TMS_STATUS_ERROR")
public class ProcTMSStatusError extends IdEntity {

	private static final long serialVersionUID = 3033324859024541080L;
	// 支付编号
	@Column(name = "BPM_NO", length = 255)
	private String bpmNo;
	// 所在文件名
	@Column(name = "FILE_NAME", length = 255)
	private String fileName;
	// 文件中所在异常的行号
	@Column(name = "ERROR_LINE", length = 255)
	private String errorLine;
	// 异常行的内容
	@Column(name = "ERROR_LINE_CONTENT", length = 255)
	private String errorLineContent;
	//解析时的异常
	@Column(name = "ERROR_INFO", length = 4000)
	private String errorInfo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATETIME")
	private Date createdDatetime;

	public String getBpmNo() {
		return bpmNo;
	}

	public void setBpmNo(String bpmNo) {
		this.bpmNo = bpmNo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getErrorLine() {
		return errorLine;
	}

	public void setErrorLine(String errorLine) {
		this.errorLine = errorLine;
	}

	public String getErrorLineContent() {
		return errorLineContent;
	}

	public void setErrorLineContent(String errorLineContent) {
		this.errorLineContent = errorLineContent;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public Date getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

}
