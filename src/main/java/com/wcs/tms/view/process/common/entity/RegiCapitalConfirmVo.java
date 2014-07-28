package com.wcs.tms.view.process.common.entity;

import java.util.Date;

import com.wcs.tms.model.ProcRegiCapital;

public class RegiCapitalConfirmVo {
	
	// 注册资本金流程实例
	private ProcRegiCapital regiCapital;

	// 到账金额
	private Double alreadyAccount;
	// 币别
	private String currency;
	// 到账日期
	private Date inAccountDate = new Date();
	// 登记人员
	private String registrant;
	// 登记时间
	private Date registerDate = new Date();
	
	public Double getAlreadyAccount() {
		return alreadyAccount;
	}
	public void setAlreadyAccount(Double alreadyAccount) {
		this.alreadyAccount = alreadyAccount;
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
	public ProcRegiCapital getRegiCapital() {
		return regiCapital;
	}
	public void setRegiCapital(ProcRegiCapital regiCapital) {
		this.regiCapital = regiCapital;
	}
	
}
