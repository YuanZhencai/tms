package com.wcs.tms.view.process.common.entity;

import java.util.Date;

import com.wcs.tms.model.DebtContract;
import com.wcs.tms.model.ProcDebtBorrow;

public class RegiDebtConfirmVo {

	// 外债申请流程实例
	private ProcDebtBorrow debtBorrow;
	// 外债合同
	private DebtContract debtContract;
	
	// 合同编号
	private String contractNo;
	// 借款期限
	private Date borrowStartDate;
	private Date borrowEndDate;

	// 合同金额
	private Double contractAccount;
	// 币别
	private String currency;

	// 借款利率
	private Double interestRate;
	
	// 报备人员
	private String filler;
	// 报备时间
	private Date fillDate = new Date();

	// 登记人员
	private String registrant;
	// 登记时间
	private Date registerDate = new Date();
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public Date getBorrowStartDate() {
		return borrowStartDate;
	}
	public void setBorrowStartDate(Date borrowStartDate) {
		this.borrowStartDate = borrowStartDate;
	}
	public Date getBorrowEndDate() {
		return borrowEndDate;
	}
	public void setBorrowEndDate(Date borrowEndDate) {
		this.borrowEndDate = borrowEndDate;
	}
	public Double getContractAccount() {
		return contractAccount;
	}
	public void setContractAccount(Double contractAccount) {
		this.contractAccount = contractAccount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Double getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}
	public String getFiller() {
		return filler;
	}
	public void setFiller(String filler) {
		this.filler = filler;
	}
	public Date getFillDate() {
		return fillDate;
	}
	public void setFillDate(Date fillDate) {
		this.fillDate = fillDate;
	}
	public String getRegistrant() {
		return registrant;
	}
	public void setRegistrant(String registrant) {
		this.registrant = registrant;
	}
	public Date getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}
	public ProcDebtBorrow getDebtBorrow() {
		return debtBorrow;
	}
	public void setDebtBorrow(ProcDebtBorrow debtBorrow) {
		this.debtBorrow = debtBorrow;
	}
	public DebtContract getDebtContract() {
		return debtContract;
	}
	public void setDebtContract(DebtContract debtContract) {
		this.debtContract = debtContract;
	}
	
}
