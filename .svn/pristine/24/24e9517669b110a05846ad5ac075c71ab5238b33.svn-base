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
* <p>Title: 流程明细——采购资金(贸易)</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
@Entity
@Table(name = "PROC_PURCHASE_FUND_TRADE_DETAIL")
public class ProcPurchaseFundTradeDetail extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROC_INST_ID")
	private ProcPurchaseFundTrade procPurchaseFundTrade;
	/** 支付金额*/
	@Column(name = "PAY_FUNDS_TOTAL", precision = 12, scale = 4)
	private Double payFundsTotal;
	/* 品种1 */
	@Column(name = "VARIETY_ONE", length = 101)
	protected String varietyOne;
	/* 品种1审批头寸 */
	@Column(name = "VARIETY_ONE_NUM", precision = 12, scale = 4)
	private Double varietyOneNum;
	/* 品种1支付金额 */
	@Column(name = "VARIETY_ONE_AMOUNT", precision = 12, scale = 4)
	private Double varietyOneAmount;
	/* 品种2 */
	@Column(name = "VARIETY_TWO", length = 101)
	protected String varietyTwo;
	/* 品种2审批头寸 */
	@Column(name = "VARIETY_TWO_NUM", precision = 12, scale = 4)
	private Double varietyTwoNum;
	/* 品种2支付金额 */
	@Column(name = "VARIETY_TWO_AMOUNT", precision = 12, scale = 4)
	private Double varietyTwoAmount;
	/* 品种3 */
	@Column(name = "VARIETY_THR", length = 101)
	protected String varietyThr;
	/* 品种3审批头寸 */
	@Column(name = "VARIETY_THR_NUM", precision = 12, scale = 4)
	private Double varietyThrNum;
	/* 品种3支付金额 */
	@Column(name = "VARIETY_THR_AMOUNT", precision = 12, scale = 4)
	private Double varietyThrAmount;
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

	public ProcPurchaseFundTradeDetail() {
		procPurchaseFundTrade = new ProcPurchaseFundTrade();
	}

	public String getPeMemo() {
		return peMemo;
	}

	public void setPeMemo(String peMemo) {
		this.peMemo = peMemo;
	}

	public ProcPurchaseFundTrade getProcPurchaseFundTrade() {
		return procPurchaseFundTrade;
	}

	public void setProcPurchaseFundTrade(ProcPurchaseFundTrade procPurchaseFundTrade) {
		this.procPurchaseFundTrade = procPurchaseFundTrade;
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

	public Double getVarietyOneNum() {
		return varietyOneNum;
	}

	public void setVarietyOneNum(Double varietyOneNum) {
		this.varietyOneNum = varietyOneNum;
	}

	public Double getVarietyOneAmount() {
		return varietyOneAmount;
	}

	public void setVarietyOneAmount(Double varietyOneAmount) {
		this.varietyOneAmount = varietyOneAmount;
	}

	public Double getVarietyTwoNum() {
		return varietyTwoNum;
	}

	public void setVarietyTwoNum(Double varietyTwoNum) {
		this.varietyTwoNum = varietyTwoNum;
	}

	public Double getVarietyTwoAmount() {
		return varietyTwoAmount;
	}

	public void setVarietyTwoAmount(Double varietyTwoAmount) {
		this.varietyTwoAmount = varietyTwoAmount;
	}

	public Double getVarietyThrNum() {
		return varietyThrNum;
	}

	public void setVarietyThrNum(Double varietyThrNum) {
		this.varietyThrNum = varietyThrNum;
	}

	public Double getVarietyThrAmount() {
		return varietyThrAmount;
	}

	public void setVarietyThrAmount(Double varietyThrAmount) {
		this.varietyThrAmount = varietyThrAmount;
	}

	public String getVarietyOne() {
		return varietyOne;
	}

	public void setVarietyOne(String varietyOne) {
		this.varietyOne = varietyOne;
	}

	public String getVarietyTwo() {
		return varietyTwo;
	}

	public void setVarietyTwo(String varietyTwo) {
		this.varietyTwo = varietyTwo;
	}

	public String getVarietyThr() {
		return varietyThr;
	}

	public void setVarietyThr(String varietyThr) {
		this.varietyThr = varietyThr;
	}

	public Double getPayFundsTotal() {
		return payFundsTotal;
	}

	public void setPayFundsTotal(Double payFundsTotal) {
		this.payFundsTotal = payFundsTotal;
	}

	@Override
	public String getDisplayText() {
		// TODO Auto-generated method stub
		return null;
	}

}