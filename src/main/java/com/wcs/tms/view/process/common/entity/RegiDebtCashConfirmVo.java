package com.wcs.tms.view.process.common.entity;

import java.util.Date;

import com.wcs.tms.model.ProcDebtPayment;

public class RegiDebtCashConfirmVo {
	
	//外债请款流程实例
	private ProcDebtPayment debtPayment;
	
	// 请款金额
	private Double requestAccount;
	// 币别
	private String currency;
	// 到账日期
	private Date inAccountDate = new Date();
	// 登记人员
	private String registrant;
	// 登记时间
	private Date registerDate = new Date();
	public Double getRequestAccount() {
		return requestAccount;
	}
	public void setRequestAccount(Double requestAccount) {
		this.requestAccount = requestAccount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Date getInAccountDate() {
		return inAccountDate;
	}
	public void setInAccountDate(Date inAccountDate) {
		this.inAccountDate = inAccountDate;
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
	public ProcDebtPayment getDebtPayment() {
		return debtPayment;
	}
	public void setDebtPayment(ProcDebtPayment debtPayment) {
		this.debtPayment = debtPayment;
	}
	
	
}
