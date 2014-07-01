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
 * 采购资金（生产）支付信息
 * 
 * @author WCS-RAY
 * 
 */
@Entity
@Table(name = "PROC_PURCHASE_FUND_PROD_PAY")
public class ProcPurchaseFundProdPay extends BaseEntity {

	private static final long serialVersionUID = -3084426891708527702L;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROC_INST_ID")
	private ProcPurchaseFundProd procPurchaseFundProd;
	/** 支付金额 */
	@Column(name = "PAY_FUNDS_TOTAL", precision = 12, scale = 4)
	private Double payFundsTotal;
	/* 支付方式 */
	@Column(name = "PAY_WAY", length = 255)
	private String payWay;
	/* 支付时间 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PAY_DATETIME")
	private Date payDatetime;

	public ProcPurchaseFundProd getProcPurchaseFundProd() {
		return procPurchaseFundProd;
	}

	public void setProcPurchaseFundProd(
			ProcPurchaseFundProd procPurchaseFundProd) {
		this.procPurchaseFundProd = procPurchaseFundProd;
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
	@Transient
	public String getDisplayText() {
		// TODO Auto-generated method stub
		return null;
	}

}
