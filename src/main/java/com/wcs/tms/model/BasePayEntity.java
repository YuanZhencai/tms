package com.wcs.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.wcs.base.model.BaseEntity;

/**
 * <p>Project: tms</p>
 * <p>Description:付款的基础entity，付款相关流程的Entity继承此类 </p>
 * <p>Copyright © 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
@MappedSuperclass
public class BasePayEntity extends BaseEntity {

	private static final long serialVersionUID = 8416322178638192350L;

	/* 公司信息 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	protected Company company;

	/* 帐号描述 */
	@Column(name = "ACCOUNT_DESC", length = 255)
	protected String accountDesc;

	/* 账号 */
	@Column(name = "ACCOUNT_NUMBER", length = 255)
	protected String accountNumber;

	/* 金额 */
	@Column(name = "AMOUNT", precision = 12, scale = 4)
	protected Double amount;

	/* 付款日期 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PAYMENT_DATE")
	protected Date paymentDate;

	/* 借款转款标识(借款类型) */
	@Column(name = "LOAN_IDEN", length = 1)
	protected String loanIden;

	/* 下拨资金用途描述 */
	@Column(name = "USE_DESC", length = 2000)
	protected String useDesc;

	/* 表单类型 */
	@Column(name = "FORM_TYPE", length = 1)
	protected String formType;

	/* 币别 */
	@Column(name = "AMOUNT_CU", length = 101)
	protected String amountCu;

	/* 品种1 */
	@Column(name = "VARIETY_ONE", length = 101)
	protected String varietyOne;

	/* 品种2 */
	@Column(name = "VARIETY_TWO", length = 101)
	protected String varietyTwo;

	/* 品种3 */
	@Column(name = "VARIETY_THR", length = 101)
	protected String varietyThr;
	
	/* 发起人手动终止流程标识  */
	@Column(name = "TERMINATE_FLAG", length = 1)
	private String terminateFlag;
	
	/* 剩余需要 支付金额*/
	@Column(name = "LAST_PAIED_AMOUNT", precision = 12, scale = 4)
	private Double lastPaiedAmount;
	

	@Override
	public String getDisplayText() {
		return null;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getAccountDesc() {
		return accountDesc;
	}

	public void setAccountDesc(String accountDesc) {
		this.accountDesc = accountDesc;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getLoanIden() {
		return loanIden;
	}

	public void setLoanIden(String loanIden) {
		this.loanIden = loanIden;
	}

	public String getUseDesc() {
		return useDesc;
	}

	public void setUseDesc(String useDesc) {
		this.useDesc = useDesc;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public String getAmountCu() {
		return amountCu;
	}

	public void setAmountCu(String amountCu) {
		this.amountCu = amountCu;
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

	public String getTerminateFlag() {
		return terminateFlag;
	}

	public void setTerminateFlag(String terminateFlag) {
		this.terminateFlag = terminateFlag;
	}

	public Double getLastPaiedAmount() {
		return lastPaiedAmount;
	}

	public void setLastPaiedAmount(Double lastPaiedAmount) {
		this.lastPaiedAmount = lastPaiedAmount;
	}

}
