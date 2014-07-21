package com.wcs.tms.model;
//公司外债额度详情
public class DebtQuota {
	private Double shortDebt; //短期外债
	private Double midLongDebt;//中长期外债
	private Double extensionDebt;//展期外债 
	private Double termMidLongDebt;//到期中长期
	private Double paymentAmount;//请款金额
	private Double unAccountAmount;//未到帐请款
	private String currency="1";//币种 统计先不区分  默认为美金
	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Double getShortDebt() {
		return shortDebt;
	}
	public void setShortDebt(Double shortDebt) {
		this.shortDebt = shortDebt;
	}
	public Double getMidLongDebt() {
		return midLongDebt;
	}
	public void setMidLongDebt(Double midLongDebt) {
		this.midLongDebt = midLongDebt;
	}
	public Double getExtensionDebt() {
		return extensionDebt;
	}
	public void setExtensionDebt(Double extensionDebt) {
		this.extensionDebt = extensionDebt;
	}
	public Double getTermMidLongDebt() {
		return termMidLongDebt;
	}
	public void setTermMidLongDebt(Double termMidLongDebt) {
		this.termMidLongDebt = termMidLongDebt;
	}
	public Double getPaymentAmount() {
		return paymentAmount;
	}
	public void setPaymentAmount(Double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}
	public Double getUnAccountAmount() {
		return unAccountAmount;
	}
	public void setUnAccountAmount(Double unAccountAmount) {
		this.unAccountAmount = unAccountAmount;
	}
	
	
}
