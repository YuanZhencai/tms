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

import com.wcs.base.model.BaseEntity;

/** 
* <p>Project: tms</p> 
* <p>Title: 支付明细_采购资金(贸易或生产)_主数据Model</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
@Entity
@Table(name = "PURCHASE_FUND_DETAIL")
public class PurchaseFundDetail extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAIN_DATE_ID")
	private PurchaseFund purchaseFund;
	/* 流程实例ID */
	@Column(name = "PROC_INST_ID", length = 255)
	private String procInstId;
	/* 支付方式 */
	@Column(name = "PAY_WAY", length = 255)
	private String payWay;
	/* 付款日期 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PAY_TIME")
	private Date payTime;
	/* 审批头寸数量 */
	@Column(name = "APP_NUM_PAY", precision = 12, scale = 4)
	private Double appNumPay;
	/* 审批金额 */
	@Column(name = "APP_AMOUNT_PAY", precision = 12, scale = 4)
	private Double appAmountPay;

	public PurchaseFundDetail() {
		purchaseFund = new PurchaseFund();
	}

	public PurchaseFund getPurchaseFund() {
		return purchaseFund;
	}

	public void setPurchaseFund(PurchaseFund purchaseFund) {
		this.purchaseFund = purchaseFund;
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getPayWay() {
		return payWay;
	}

	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Double getAppNumPay() {
		return appNumPay;
	}

	public void setAppNumPay(Double appNumPay) {
		this.appNumPay = appNumPay;
	}

	public Double getAppAmountPay() {
		return appAmountPay;
	}

	public void setAppAmountPay(Double appAmountPay) {
		this.appAmountPay = appAmountPay;
	}

	@Override
	public String getDisplayText() {
		return null;
	}
}