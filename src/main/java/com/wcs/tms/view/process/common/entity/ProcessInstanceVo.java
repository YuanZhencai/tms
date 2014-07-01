package com.wcs.tms.view.process.common.entity;

import java.io.Serializable;
import java.util.Date;

import com.wcs.common.controller.helper.IdModel;

/**
 * 
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description: 流程已接收任务VO
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
public class ProcessInstanceVo extends IdModel implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	/** 流程实例编号 PE */
	private String procInstId;
	/** 系统流程定义编号 */
	private String processCode;
	/** 流程名称 */
	private String processName;
	/** 流程版本 */
	private Integer processVersion;
	/** 流程描述 */
	private String describle;
	/** 任务名称 */
	private String nodeName;
	/** 任务处理人 */
	private String nodeExer;
	/** 提交申请时间 */
	private Date submitDate;
	/** 接收任务时间(锁定时间) */
	private Date acceptDate;
	/** 任务完成时间(节点完成时间) */
	private Date stepDate;
	/** 流程结束时间 */
	private Date processEndDate;
	/** 是否为终止流程 */
	private Boolean terminalFlag = false;
	/** xml表单路径 */
	private String xmlPath;

	/** 可否用确认单详细页面 **/
	private Boolean userComfirmView = false;

	// 8.27 huhan add for 已处理任务
	/** 当前流程实例所处任务 **/
	private String thisStepName;

	/** 当前处理人 **/
	private String thisStepExer;

	/** 流程状态 **/
	private String thisStatus;

	// 9.10 huhan add for all 申请公司 & TMS流程实例编号
	/** 申请公司 **/
	private String companyName;

	/** TMS业务流程实例编号 **/
	private String pidTms;

	// 2014-05-05 liushengbin add 流程申请人
	private String originator;
	// 2014-05-16 liushengbin add 是否是故障期间的数据，是为true，否为false
	private String isPatch;

	public String getIsPatch() {
		if(isPatch == null){
			isPatch = "false";
		}
		return isPatch;
	}

	public void setIsPatch(String isPatch) {
		this.isPatch = isPatch;
	}

	public String getOriginator() {
		return originator;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
	}

	public String getThisStatus() {
		return thisStatus;
	}

	public void setThisStatus(String thisStatus) {
		this.thisStatus = thisStatus;
	}

	public String getThisStepName() {
		return thisStepName;
	}

	public void setThisStepName(String thisStepName) {
		this.thisStepName = thisStepName;
	}

	public String getThisStepExer() {
		return thisStepExer;
	}

	public void setThisStepExer(String thisStepExer) {
		this.thisStepExer = thisStepExer;
	}

	public Boolean getTerminalFlag() {
		return terminalFlag;
	}

	public void setTerminalFlag(Boolean terminalFlag) {
		this.terminalFlag = terminalFlag;
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getProcessCode() {
		return processCode;
	}

	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Integer getProcessVersion() {
		return processVersion;
	}

	public void setProcessVersion(Integer processVersion) {
		this.processVersion = processVersion;
	}

	public String getDescrible() {
		return describle;
	}

	public void setDescrible(String describle) {
		this.describle = describle;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getNodeExer() {
		return nodeExer;
	}

	public void setNodeExer(String nodeExer) {
		this.nodeExer = nodeExer;
	}

	public Date getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}

	public Date getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(Date acceptDate) {
		this.acceptDate = acceptDate;
	}

	public Date getProcessEndDate() {
		return processEndDate;
	}

	public void setProcessEndDate(Date processEndDate) {
		this.processEndDate = processEndDate;
	}

	public Date getStepDate() {
		return stepDate;
	}

	public void setStepDate(Date stepDate) {
		this.stepDate = stepDate;
	}

	public String getXmlPath() {
		return xmlPath;
	}

	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}

	public Boolean getUserComfirmView() {
		return userComfirmView;
	}

	public void setUserComfirmView(Boolean userComfirmView) {
		this.userComfirmView = userComfirmView;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getPidTms() {
		return pidTms;
	}

	public void setPidTms(String pidTms) {
		this.pidTms = pidTms;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((procInstId == null) ? 0 : procInstId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ProcessInstanceVo other = (ProcessInstanceVo) obj;
		if (procInstId == null) {
			if (other.procInstId != null) {
				return false;
			}
		} else if (!procInstId.equals(other.procInstId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ProcessInstanceVo [procInstId=" + procInstId + ", processCode="
				+ processCode + ", processName=" + processName
				+ ", processVersion=" + processVersion + ", describle="
				+ describle + ", nodeName=" + nodeName + ", nodeExer="
				+ nodeExer + ", submitDate=" + submitDate + ", acceptDate="
				+ acceptDate + ", stepDate=" + stepDate + ", processEndDate="
				+ processEndDate + ", terminalFlag=" + terminalFlag
				+ ", xmlPath=" + xmlPath + ", userComfirmView="
				+ userComfirmView + ", thisStepName=" + thisStepName
				+ ", thisStepExer=" + thisStepExer + ", thisStatus="
				+ thisStatus + ", companyName=" + companyName + ", pidTms="
				+ pidTms + ", originator=" + originator + ", isPatch="
				+ isPatch + "]";
	}
	
	

}
