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
 * 
 * <p>Project: tms</p>
 * <p>Description:公司银行账户实体</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liaowei@wcs-global.com">廖伟</a>
 */
@Entity
@Table(name = "COMPANY_ACCOUNT")
public class CompanyAccount extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/** 公司信息 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;

	/** 选择银行 */
	@Column(name = "BANK", length = 255)
	private String bank;

	/** 账号 */
	@Column(name = "ACCOUNT", length = 255)
	private String account;

	/** 账号描述 */
	@Column(name = "ACCOUNT_DESC", length = 255)
	private String accountDesc;

	/** BSB代码 */
	@Column(name = "BSB_CODE", length = 255)
	private String bsbCode;

	/** 联行号 */
	@Column(name = "UNION_BANK_NO", length = 255)
	private String unionBankNo;

	/** 账户标识 */
	@Column(name = "COUNTERPARTY_CODE", length = 255)
	private String counterpartyCode;

	public CompanyAccount() {
		this.company = new Company();
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAccountDesc() {
		return accountDesc;
	}

	public void setAccountDesc(String accountDesc) {
		this.accountDesc = accountDesc;
	}

	public String getBsbCode() {
		return bsbCode;
	}

	public void setBsbCode(String bsbCode) {
		this.bsbCode = bsbCode;
	}

	public String getUnionBankNo() {
		return unionBankNo;
	}

	public void setUnionBankNo(String unionBankNo) {
		this.unionBankNo = unionBankNo;
	}

	public String getCounterpartyCode() {
		return counterpartyCode;
	}

	public void setCounterpartyCode(String counterpartyCode) {
		this.counterpartyCode = counterpartyCode;
	}

	@Override
	@Transient
	public String getDisplayText() {
		// TODO Auto-generated method stub
		return null;
	}

}
