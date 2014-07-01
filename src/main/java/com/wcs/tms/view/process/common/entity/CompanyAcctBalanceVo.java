package com.wcs.tms.view.process.common.entity;

import java.io.Serializable;
import java.util.Date;

import com.wcs.common.controller.helper.IdModel;

public class CompanyAcctBalanceVo extends IdModel implements Serializable{

	private static final long serialVersionUID = 1L;

	// 公司名称
	private String companyName;
	// 银行账号
	private String account;
	// 账号余额
	private String availableAmount;
	// 银行名称
	private String bankName;
	// 同步时间
	private Date updateDate;


	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(String availableAmount) {
		this.availableAmount = availableAmount;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
}
