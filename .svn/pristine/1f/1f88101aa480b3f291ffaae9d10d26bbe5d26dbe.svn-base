package com.wcs.tms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/** 
* <p>Project: tms</p> 
* <p>Title: 投资、融资保证金及归还银行贷款借款/转款Model</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
@Entity
@Table(name = "PROC_ELSE_SPE_LOAN")
public class ProcElseSpeLoan extends BasePayEntity {

	private static final long serialVersionUID = 1L;

	/* 流程实例ID */
	@Column(name = "PROC_INST_ID", length = 255)
	private String procInstId;

	/* 收款人户名 */
	@Column(name = "RECEIVER_NAME", length = 255)
	private String receiverName;

	/* 流程状态 */
	@Column(name = "STATUS", length = 1)
	private String status;
	/** 保存PE的备注*/
	@Transient
	private String peMemo;

	public ProcElseSpeLoan() {
		company = new Company();
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPeMemo() {
		return peMemo;
	}

	public void setPeMemo(String peMemo) {
		this.peMemo = peMemo;
	}

	@Override
	public String getDisplayText() {
		// TODO Auto-generated method stub
		return null;
	}
}