package com.wcs.tms.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 银行授信集团申请确认单</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Entity
@Table(name = "PROC_BANK_CREDIT_BLOC_CONFIRM")
public class ProcBankCreditBlocConfirm extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BLOC_CREDIT_ID")
	private ProcBankCreditBloc procBankCreditBloc;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BANK_ID")
	private Bank bank;
	@Temporal(TemporalType.DATE)
	@Column(name = "CREDIT_START", length = 10)
	private Date creditStart;
	@Temporal(TemporalType.DATE)
	@Column(name = "CREDIT_END", length = 10)
	private Date creditEnd;
	@Column(name = "CREDIT_LIMIT", precision = 12, scale = 4)
	private Double creditLimit;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "procBankCreditBlocConfirm")
	private List<ProcBankCreditBlocCompanyConfirm> procBankCreditBlocCompanyConfirms = new ArrayList<ProcBankCreditBlocCompanyConfirm>(0);
	/** creditLineCode*/
	// --12-12-12新增需求
	@Column(name = "CREDIT_LINE_CODE", length = 101)
	private String creditLineCode;
	/** 审批备注 */
	@Transient
	private String peMemo;

	// Constructors

	/** default constructor */
	public ProcBankCreditBlocConfirm() {
	}

	public ProcBankCreditBloc getProcBankCreditBloc() {
		return this.procBankCreditBloc;
	}

	public void setProcBankCreditBloc(ProcBankCreditBloc procBankCreditBloc) {
		this.procBankCreditBloc = procBankCreditBloc;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Bank getBank() {
		return this.bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public Date getCreditStart() {
		return this.creditStart;
	}

	public void setCreditStart(Date creditStart) {
		this.creditStart = creditStart;
	}

	public Date getCreditEnd() {
		return this.creditEnd;
	}

	public void setCreditEnd(Date creditEnd) {
		this.creditEnd = creditEnd;
	}

	public Double getCreditLimit() {
		return this.creditLimit;
	}

	public void setCreditLimit(Double creditLimit) {
		this.creditLimit = creditLimit;
	}

	public List<ProcBankCreditBlocCompanyConfirm> getProcBankCreditBlocCompanyConfirms() {
		return procBankCreditBlocCompanyConfirms;
	}

	public void setProcBankCreditBlocCompanyConfirms(List<ProcBankCreditBlocCompanyConfirm> procBankCreditBlocCompanyConfirms) {
		this.procBankCreditBlocCompanyConfirms = procBankCreditBlocCompanyConfirms;
	}

	public String getPeMemo() {
		return peMemo;
	}

	public void setPeMemo(String peMemo) {
		this.peMemo = peMemo;
	}

	@Transient
	@Override
	public String getDisplayText() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCreditLineCode() {
		return creditLineCode;
	}

	public void setCreditLineCode(String creditLineCode) {
		this.creditLineCode = creditLineCode;
	}

}