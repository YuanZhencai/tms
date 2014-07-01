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

@Entity
@Table(name = "PROC_PURCHASE_FUND_TRADE_PAY")
public class ProcPurchaseFundTradePay extends BaseEntity {

	private static final long serialVersionUID = -7584600118591565853L;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROC_INST_ID")
	private ProcPurchaseFundTrade procPurchaseFundTrade;
	/** 支付金额*/
	@Column(name = "PAY_FUNDS_TOTAL", precision = 12, scale = 4)
	private Double payFundsTotal;
	/* 支付方式 */
	@Column(name = "PAY_WAY", length = 255)
	private String payWay;
	/* 支付时间 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PAY_DATETIME")
	private Date payDatetime;
	
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


	public ProcPurchaseFundTrade getProcPurchaseFundTrade() {
		return procPurchaseFundTrade;
	}



	public void setProcPurchaseFundTrade(ProcPurchaseFundTrade procPurchaseFundTrade) {
		this.procPurchaseFundTrade = procPurchaseFundTrade;
	}



	@Override
	@Transient
	public String getDisplayText() {
		// TODO Auto-generated method stub
		return null;
	}

}
