package com.wcs.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:流程_支付工程款股利及归还股东借款</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liaowei@wcs-global.com">廖伟</a>
 */
@Entity
@Table(name = "PROC_PAY_PROJECT")
public class ProcPayProject extends BasePayEntity {

	private static final long serialVersionUID = 1L;

	/** 流程实例ID*/
	@Column(name = "PROC_INST_ID", length = 255)
	private String procInstId;

	/** 收款人户名*/
	@Column(name = "RECEIVER_NAME", length = 60)
	private String receiverName;

	/**到期日*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EXPIRE_TIME")
	private Date expireTime;

	/** 还贷金额*/
	@Column(name = "REPAY_AMOUNT", precision = 12, scale = 4)
	private Double repayAmount;

	/** 流程状态*/
	@Column(name = "STATUS", length = 1)
	private String status;

	/** 保存PE的备注*/
	@Transient
	private String peMemo;

	public ProcPayProject() {
		this.company = new Company();
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
	@Transient
	public String getDisplayText() {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public Double getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(Double repayAmount) {
		this.repayAmount = repayAmount;
	}

}
