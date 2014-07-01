/**
 * 
 */
package com.wcs.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;

/**
 * <p>Project: tms</p>
 * <p>Description:流程明细_支付工程款股利及归还股东借款</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liaowei@wcs-global.com">廖伟</a>
 */
@Entity
@Table(name = "PROC_PAY_PROJECT_DETAILS")
public class ProcPayProjectDetails extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROC_INST_ID")
	private ProcPayProject procPayProject;

	/* 支付金额 */
	@Column(name = "PAY_FUNDS_TOTAL", precision = 12, scale = 4)
	private Double payFundsTotal;
	/* 支付方式 */
	@Column(name = "PAY_WAY", length = 255)
	private String payWay;
	/* 支付时间 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PAY_DATETIME")
	private Date payDatetime;
	/** 保存PE的备注*/
	@Transient
	private String peMemo;

	public ProcPayProjectDetails() {
		this.procPayProject = new ProcPayProject();
	}

	public ProcPayProject getProcPayProject() {
		return procPayProject;
	}

	public void setProcPayProject(ProcPayProject procPayProject) {
		this.procPayProject = procPayProject;
	}

	public Double getPayFundsTotal() {
		return payFundsTotal;
	}

	public void setPayFundsTotal(Double payFundsTotal) {
		this.payFundsTotal = payFundsTotal;
	}

	public String getPayWay() {
		return payWay;
	}

	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}

	public Date getPayDatetime() {
		return payDatetime;
	}

	public void setPayDatetime(Date payDatetime) {
		this.payDatetime = payDatetime;
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
