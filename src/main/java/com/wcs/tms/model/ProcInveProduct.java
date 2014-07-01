package com.wcs.tms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:投资理财流程Model</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Entity
@Table(name = "PROC_INVE_PRODUCT")
public class ProcInveProduct extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="BANK_ID")
	private Bank bank;

	@Column(name = "PROC_INST_ID", length = 255)
	private String procInstId;

	@Column(name = "BANK_LEVEL", length = 600)
	private String bankLevel;

	@Column(name = "BANK_CREDIT", precision = 12, scale = 4)
	private Double bankCredit;

	@Column(name = "BANK_CREDIT_CU", length = 101)
	private String bankCreditCu;

	@Column(name = "PRESENT", length = 600)
	private String present;

	@Column(name = "PRODUCT_TYPE", length = 101)
	private String productType;

	@Column(name = "PRODUCT_NAME", length = 600)
	private String productName;

	@Column(name = "PRODUCT_FORM", length = 600)
	private String productForm;

	@Column(name = "LIMIT", precision = 4, scale = 0)
	private Double limit;

	@Column(name = "LIMIT_UNIT", length = 101)
	private String limitUnit;

	@Column(name = "AMOUNT", precision = 12, scale = 4)
	private Double amount;

	@Column(name = "AMOUNT_CU", length = 101)
	private String amountCu;

	@Column(name = "REASON", length = 600)
	private String reason;

	/** 保存PE的备注*/
    @Transient
    private String peMemo;
	
	public ProcInveProduct(){
		this.company = new Company();
		this.bank = new Bank();
	}
	
	
	public String getPeMemo() {
		return peMemo;
	}

	public void setPeMemo(String peMemo) {
		this.peMemo = peMemo;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}


	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getBankLevel() {
		return bankLevel;
	}

	public void setBankLevel(String bankLevel) {
		this.bankLevel = bankLevel;
	}

	public Double getBankCredit() {
		return bankCredit;
	}

	public void setBankCredit(Double bankCredit) {
		this.bankCredit = bankCredit;
	}

	public String getBankCreditCu() {
		return bankCreditCu;
	}

	public void setBankCreditCu(String bankCreditCu) {
		this.bankCreditCu = bankCreditCu;
	}

	public String getPresent() {
		return present;
	}

	public void setPresent(String present) {
		this.present = present;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductForm() {
		return productForm;
	}

	public void setProductForm(String productForm) {
		this.productForm = productForm;
	}

	public Double getLimit() {
		return limit;
	}

	public void setLimit(Double limit) {
		this.limit = limit;
	}

	public String getLimitUnit() {
		return limitUnit;
	}

	public void setLimitUnit(String limitUnit) {
		this.limitUnit = limitUnit;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getAmountCu() {
		return amountCu;
	}

	public void setAmountCu(String amountCu) {
		this.amountCu = amountCu;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	@Transient
	public String getDisplayText() {
		return null;
	}
}
