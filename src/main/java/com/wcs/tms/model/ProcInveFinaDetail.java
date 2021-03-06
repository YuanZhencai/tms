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
* <p>Title: 流程明细——投资融资保证金</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
@Entity
@Table(name = "PROC_INVE_FINA_DETAIL")
public class ProcInveFinaDetail extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROC_INST_ID")
	private ProcInveFinaBail procInveFinaBail;

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

	public ProcInveFinaDetail() {
		procInveFinaBail = new ProcInveFinaBail();
	}

	public String getPeMemo() {
		return peMemo;
	}

	public void setPeMemo(String peMemo) {
		this.peMemo = peMemo;
	}

	public ProcInveFinaBail getProcInveFinaBail() {
		return procInveFinaBail;
	}

	public void setProcInveFinaBail(ProcInveFinaBail procInveFinaBail) {
		this.procInveFinaBail = procInveFinaBail;
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

	@Override
	public String getDisplayText() {
		// TODO Auto-generated method stub
		return null;
	}

}