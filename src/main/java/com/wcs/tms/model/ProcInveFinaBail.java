package com.wcs.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name = "PROC_INVE_FINA_BAIL")
public class ProcInveFinaBail extends BasePayEntity {

	private static final long serialVersionUID = 1L;

	/* 流程实例ID */
	@Column(name = "PROC_INST_ID", length = 255)
	private String procInstId;
	/* 收款人户名 */
	@Column(name = "RECEIVER_NAME", length = 255)
	private String receiverName;
	/* 是否为转口 */
	@Column(name = "TRANSIT", length = 1)
	private String transit;
	/* 币别金额 */
	@Column(name = "CUAMOUNT", precision = 12, scale = 4)
	private Double cuAmount;
	/* 业务审批流水号 */
	@Column(name = "SERI_NUMB", length = 101)
	private String seriNumb;
	/* 流程状态 */
	@Column(name = "STATUS", length = 1)
	private String status;
	// 含外汇保证金
	@Column(name = "FOREIGN_IS_CONTAINED", length = 1)
	private String foreignIsContained = "Y";
	// 归还历史贷款
	@Column(name = "HISTORY_LOAN_IS_RETURNED", length = 1)
	private String historyLoanIsReturned = "Y";
	// 保证金存入行-总行
	@Column(name = "CASH_PARENT_BANK", length = 150)
	private String cashParentBank;
	// 保证金存入行-城市
	@Column(name = "CASH_CITY", length = 150)
	private String cashCity;
	// 保证金存入行-分支行
	@Column(name = "CASH_BRANCH_BANK", length = 150)
	private String cashBranchBank;
	// 还贷-总行
	@Column(name = "REPAY_PARENT_BANK", length = 150)
	private String repayParentBank;
	// 还贷-城市
	@Column(name = "REPAY_CITY", length = 150)
	private String repayCity;
	// 还贷-分支行
	@Column(name = "REPAY_BRANCH_BANK", length = 150)
	private String repayBranchBank;
	/* 还贷-贷款到期日*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LOAN_DATE")
	private Date loanDate;

	/** 保存PE的备注*/
	@Transient
	private String peMemo;
	
	public ProcInveFinaBail() {
		company = new Company();
	}

	public String getForeignIsContained() {
		return foreignIsContained;
	}

	public void setForeignIsContained(String foreignIsContained) {
		this.foreignIsContained = foreignIsContained;
	}

	public String getHistoryLoanIsReturned() {
		return historyLoanIsReturned;
	}

	public void setHistoryLoanIsReturned(String historyLoanIsReturned) {
		this.historyLoanIsReturned = historyLoanIsReturned;
	}

	public String getCashParentBank() {
		return cashParentBank;
	}

	public void setCashParentBank(String cashParentBank) {
		this.cashParentBank = cashParentBank;
	}

	public String getCashCity() {
		return cashCity;
	}

	public void setCashCity(String cashCity) {
		this.cashCity = cashCity;
	}

	public String getCashBranchBank() {
		return cashBranchBank;
	}

	public void setCashBranchBank(String cashBranchBank) {
		this.cashBranchBank = cashBranchBank;
	}

	public String getRepayParentBank() {
		return repayParentBank;
	}

	public void setRepayParentBank(String repayParentBank) {
		this.repayParentBank = repayParentBank;
	}

	public String getRepayCity() {
		return repayCity;
	}

	public void setRepayCity(String repayCity) {
		this.repayCity = repayCity;
	}

	public String getRepayBranchBank() {
		return repayBranchBank;
	}

	public void setRepayBranchBank(String repayBranchBank) {
		this.repayBranchBank = repayBranchBank;
	}

	public Date getLoanDate() {
		return loanDate;
	}

	public void setLoanDate(Date loanDate) {
		this.loanDate = loanDate;
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

	public String getSeriNumb() {
		return seriNumb;
	}

	public void setSeriNumb(String seriNumb) {
		this.seriNumb = seriNumb;
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

	public String getTransit() {
		return transit;
	}

	public void setTransit(String transit) {
		this.transit = transit;
	}

	public Double getCuAmount() {
		return cuAmount;
	}

	public void setCuAmount(Double cuAmount) {
		this.cuAmount = cuAmount;
	}

	@Override
	public String getDisplayText() {
		return null;
	}
}