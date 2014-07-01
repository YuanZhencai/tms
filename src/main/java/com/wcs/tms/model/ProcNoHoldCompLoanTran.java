package com.wcs.tms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 非控股公司借款转款流程实体</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
@Entity
@Table(name = "PROC_NO_HOLD_COMP_LOAN_TRAN")
public class ProcNoHoldCompLoanTran extends BasePayEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 流程实例ID*/
	@Column(name = "PROC_INST_ID")
	private String procInstId;
	/**收款人户名*/
	@Column(name = "PAYEE_NAME", length = 255)
	private String payeeName;
	/**流程状态*/
	@Column(name = "PROCESS_STATUS", length = 1)
	private String processStatus;

	/** 保存PE的备注*/
	@Transient
	private String peMemo;

	public ProcNoHoldCompLoanTran() {
		this.company = new Company();
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getPayeeName() {
		return payeeName;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public String getPeMemo() {
		return peMemo;
	}

	public void setPeMemo(String peMemo) {
		this.peMemo = peMemo;
	}

	@Override
	public String getDisplayText() {
		return null;
	}

}
