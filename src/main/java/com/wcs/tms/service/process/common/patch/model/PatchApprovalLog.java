package com.wcs.tms.service.process.common.patch.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * The persistent class for the PATCH_APPROVAL_LOG database table.
 * 
 */
@Entity
@Table(name = "PATCH_APPROVAL_LOG")
public class PatchApprovalLog extends com.wcs.base.model.IdEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "FN_PID")
	private String fnPid;

	@Column(name = "NODE_EXER")
	private String nodeExer;

	@Column(name = "NODE_EXER_NAME")
	private String nodeExerName;

	@Column(name = "NODE_MEMO")
	private String nodeMemo;

	@Column(name = "NODE_NAME")
	private String nodeName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "OPERTOR_TIME")
	private Date opertorTime;

	@Column(name = "PROCESS_CODE")
	private String processCode;

	@Column(name = "PROCESS_NAME")
	private String processName;

	@Column(name = "TMS_PID")
	private String tmsPid;

	public PatchApprovalLog() {
	}

	public String getFnPid() {
		return this.fnPid;
	}

	public void setFnPid(String fnPid) {
		this.fnPid = fnPid;
	}

	public String getNodeExer() {
		return this.nodeExer;
	}

	public void setNodeExer(String nodeExer) {
		this.nodeExer = nodeExer;
	}

	public String getNodeExerName() {
		return nodeExerName;
	}

	public void setNodeExerName(String nodeExerName) {
		this.nodeExerName = nodeExerName;
	}

	public String getNodeMemo() {
		return this.nodeMemo;
	}

	public void setNodeMemo(String nodeMemo) {
		this.nodeMemo = nodeMemo;
	}

	public String getNodeName() {
		return this.nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public Date getOpertorTime() {
		return this.opertorTime;
	}

	public void setOpertorTime(Date opertorTime) {
		this.opertorTime = opertorTime;
	}

	public String getProcessCode() {
		return this.processCode;
	}

	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public String getProcessName() {
		return this.processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getTmsPid() {
		return this.tmsPid;
	}

	public void setTmsPid(String tmsPid) {
		this.tmsPid = tmsPid;
	}

}