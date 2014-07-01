package com.wcs.tms.service.process.common.patch.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * The persistent class for the PATCH_SUBED_PROCESS database table.
 * 
 */
@Entity
@Table(name = "PATCH_SUBED_PROCESS")
public class PatchSubedProcess extends com.wcs.base.model.IdEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACCEPT_DATE")
	private Date acceptDate;

	@Column(name = "COMPANY_ID")
	private Long companyId;

	@Column(name = "COMPANY_NAME")
	private String companyName;

	@Column(name = "describle")
	private String describle;

	@Column(name = "PROCESS_DEFINE_ID")
	private String processDefineId;

	@Column(name = "FN_PID")
	private String fnPid;

	@Column(name = "IS_TERMINAL_FLAG")
	private Boolean isTerminalFlag;

	@Column(name = "IS_COMFIRM_VIEW")
	private Boolean isComfirmView;

	@Column(name = "NODE_EXER")
	private String nodeExer;

	@Column(name = "NODE_NAME")
	private String nodeName;

	@Column(name = "originator")
	private String originator;

	@Column(name = "ORIGINATOR_NAME")
	private String originatorName;

	@Column(name = "PROCESS_CODE")
	private String processCode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PROCESS_END_DATE")
	private Date processEndDate;

	@Column(name = "PROCESS_NAME")
	private String processName;

	@Column(name = "PROCESS_STATUS")
	private String processStatus;

	@Column(name = "PROCESS_VERSION")
	private String processVersion;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "STEP_DATE")
	private Date stepDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SUBMIT_DATE")
	private Date submitDate;

	@Column(name = "TMS_PID")
	private String tmsPid;

	public PatchSubedProcess() {
	}

	public Date getAcceptDate() {
		return this.acceptDate;
	}

	public void setAcceptDate(Date acceptDate) {
		this.acceptDate = acceptDate;
	}

	public Long getCompanyId() {
		return this.companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDescrible() {
		return this.describle;
	}

	public void setDescrible(String describle) {
		this.describle = describle;
	}

	public String getFnPid() {
		return this.fnPid;
	}

	public void setFnPid(String fnPid) {
		this.fnPid = fnPid;
	}

	public Boolean getIsTerminalFlag() {
		return isTerminalFlag;
	}

	public void setIsTerminalFlag(Boolean isTerminalFlag) {
		this.isTerminalFlag = isTerminalFlag;
	}

	public String getProcessDefineId() {
		return processDefineId;
	}

	public void setProcessDefineId(String processDefineId) {
		this.processDefineId = processDefineId;
	}

	public String getNodeExer() {
		return this.nodeExer;
	}

	public void setNodeExer(String nodeExer) {
		this.nodeExer = nodeExer;
	}

	public String getNodeName() {
		return this.nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getOriginator() {
		return this.originator;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
	}

	public String getOriginatorName() {
		return this.originatorName;
	}

	public void setOriginatorName(String originatorName) {
		this.originatorName = originatorName;
	}

	public String getProcessCode() {
		return this.processCode;
	}

	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public Date getProcessEndDate() {
		return this.processEndDate;
	}

	public void setProcessEndDate(Date processEndDate) {
		this.processEndDate = processEndDate;
	}

	public String getProcessName() {
		return this.processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getProcessStatus() {
		return this.processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public String getProcessVersion() {
		return this.processVersion;
	}

	public void setProcessVersion(String processVersion) {
		this.processVersion = processVersion;
	}

	public Date getStepDate() {
		return this.stepDate;
	}

	public void setStepDate(Date stepDate) {
		this.stepDate = stepDate;
	}

	public Date getSubmitDate() {
		return this.submitDate;
	}

	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}

	public String getTmsPid() {
		return this.tmsPid;
	}

	public void setTmsPid(String tmsPid) {
		this.tmsPid = tmsPid;
	}

	public Boolean getIsComfirmView() {
		if (isComfirmView == null) {
			isComfirmView = Boolean.FALSE;
		}
		return isComfirmView;
	}

	public void setIsComfirmView(Boolean isComfirmView) {
		this.isComfirmView = isComfirmView;
	}

}