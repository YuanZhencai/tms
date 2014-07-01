package com.wcs.tms.view.process.common.entity;

import java.io.Serializable;

public class CompanyBankBalanceVo implements Serializable{

	private static final long serialVersionUID = 1L;
	// 公司名称
	private String companyName;
	// 中国工商银行
	private String ICBCBalance;
	// 中国农业银行
	private String ABCBalance;
	// 中国招商银行
	private String CCBBalance;
	// 中国银行
	private String BOCBalance;
	// 交通银行
	private String BCMBalance;
	// 合计余额(元)
	private String amountSum;
	
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getICBCBalance() {
		return ICBCBalance;
	}
	public void setICBCBalance(String iCBCBalance) {
		ICBCBalance = iCBCBalance;
	}
	public String getABCBalance() {
		return ABCBalance;
	}
	public void setABCBalance(String aBCBalance) {
		ABCBalance = aBCBalance;
	}
	public String getCCBBalance() {
		return CCBBalance;
	}
	public void setCCBBalance(String cCBBalance) {
		CCBBalance = cCBBalance;
	}
	public String getBOCBalance() {
		return BOCBalance;
	}
	public void setBOCBalance(String bOCBalance) {
		BOCBalance = bOCBalance;
	}
	public String getBCMBalance() {
		return BCMBalance;
	}
	public void setBCMBalance(String bCMBalance) {
		BCMBalance = bCMBalance;
	}
	public String getAmountSum() {
		return amountSum;
	}
	public void setAmountSum(String amountSum) {
		this.amountSum = amountSum;
	}
	
}
