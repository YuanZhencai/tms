package com.wcs.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;

/**
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description: 流程_外债请款申请
 * </p>
 * <p>
 * Copyright (c) 2014 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * </a>
 */
@Entity
@Table(name = "PROC_DEBT_PAYMENT")
public class ProcDebtPayment extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7368853102849558388L;

	@Override
	public String getDisplayText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Column(name = "PROC_INST_ID", length = 255)
	private String procInstId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;

	@Column(name = "COMPANY_NAME", length = 255)
	private String companyName;

	@Column(name = "COMPANY_EN", length = 255)
	private String companyEn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEBT_ACCOUNT_ID")
	private RemittanceLineAccount remittanceLineAccount;

	@Column(name = "IS_HAS_DEBT_CONTRACT", length = 1)
	private String isHasDebtContract;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROC_DEBT_BORROW_ID")
	private DebtContract debtContract;

	@Column(name = "APPLY_FUNDS", precision = 12, scale = 4)
	private Double applyFunds;

	@Column(name = "APPLY_FUNDS_CU", length = 101)
	private String applyFundsCu;

	@Column(name = "USE", length = 2000)
	private String use;

	@Column(name = "DEBT_PROVIDER", length = 255)
	private String debtProvider;

	@Column(name = "DEBT_REQUESTER", length = 255)
	private String debtRequester;

	@Column(name = "DEBT_REQUESTER_ADDR", length = 255)
	private String debtRequesterAddr;

	@Column(name = "AFCE_FLAG", length = 1)
	private String afceFlag;

	@Column(name = "AFCE_SIGN", precision = 12, scale = 4)
	private Double afceSign;

	@Column(name = "AFCE_SIGN_CU", length = 101)
	private String afceSignCu;

	@Column(name = "AFCE_EXC_RATE", precision = 12, scale = 4)
	private Double afceExcRate;

	@Column(name = "AFCE_PAID", precision = 12, scale = 4)
	private Double afcePaid;

	@Column(name = "AFCE_PAID_CU", length = 101)
	private String afcePaidCu;

	@Column(name = "DEFUNCT_IND", length = 1)
	private String defunctInd;

	/** AFCE签批情况(折算值) ****/
	@Transient
	private Double afceSignExc;
	/** 保存PE的备注 */
	@Transient
	private String peMemo;

	@Column(name = "PROCESS_STATUS", length = 1)
	private String processStatus;

	@Column(name = "CURRENT_NODE", length = 255)
	private String currentNode;
	// 是否到账
	@Column(name = "IS_RECEIVED_FUNDS", length = 1)
	private String isReceivedFunds;
	// 已到账金额
	@Column(name = "RECEIVED_FUNDS", precision = 12, scale = 4)
	private Double receivedFunds;
	// 已到账金额币别
	@Column(name = "RECEIVED_FUNDS_CU", length = 101)
	private String receivedFundsCu;
	// 到账时间
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RECEIVED_FUNDS_TIME")
	private Date receivedFundsTime;
	// 登记人员
	@Column(name = "REGISTED_BY", length = 50)
	private String registedBy;
	// 登记时间
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REGISTED_TIME")
	private Date registedTime;

	public String getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(String currentNode) {
		this.currentNode = currentNode;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public ProcDebtPayment() {
		this.remittanceLineAccount = new RemittanceLineAccount();
		this.company = new Company();
		debtContract = new DebtContract();
	}

	public String getPeMemo() {
		return peMemo;
	}

	public void setPeMemo(String peMemo) {
		this.peMemo = peMemo;
	}

	public Double getAfceSignExc() {
		return afceSignExc;
	}

	public void setAfceSignExc(Double afceSignExc) {
		if (this.getAfceFlag().equals("Y")) {
			if (this.getAfceSign() != null && this.getAfceExcRate() != null) {
				this.afceSignExc = getAfceSign() * getAfceExcRate();
			} else {
				this.afceSignExc = afceSignExc;
			}
		} else {
			this.afceSignExc = afceSignExc;
		}
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyEn() {
		return companyEn;
	}

	public void setCompanyEn(String companyEn) {
		this.companyEn = companyEn;
	}

	public RemittanceLineAccount getRemittanceLineAccount() {
		return remittanceLineAccount;
	}

	public void setRemittanceLineAccount(
			RemittanceLineAccount remittanceLineAccount) {
		this.remittanceLineAccount = remittanceLineAccount;
	}

	public String getIsHasDebtContract() {
		return isHasDebtContract;
	}

	public void setIsHasDebtContract(String isHasDebtContract) {
		this.isHasDebtContract = isHasDebtContract;
	}

	public DebtContract getDebtContract() {
		return debtContract;
	}

	public void setDebtContract(DebtContract debtContract) {
		this.debtContract = debtContract;
	}

	public Double getApplyFunds() {
		return applyFunds;
	}

	public void setApplyFunds(Double applyFunds) {
		this.applyFunds = applyFunds;
	}

	public String getApplyFundsCu() {
		return applyFundsCu;
	}

	public void setApplyFundsCu(String applyFundsCu) {
		this.applyFundsCu = applyFundsCu;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public String getDebtProvider() {
		return debtProvider;
	}

	public void setDebtProvider(String debtProvider) {
		this.debtProvider = debtProvider;
	}

	public String getDebtRequester() {
		return debtRequester;
	}

	public void setDebtRequester(String debtRequester) {
		this.debtRequester = debtRequester;
	}

	public String getDebtRequesterAddr() {
		return debtRequesterAddr;
	}

	public void setDebtRequesterAddr(String debtRequesterAddr) {
		this.debtRequesterAddr = debtRequesterAddr;
	}

	public String getAfceFlag() {
		return afceFlag;
	}

	public void setAfceFlag(String afceFlag) {
		this.afceFlag = afceFlag;
	}

	public Double getAfceSign() {
		return afceSign;
	}

	public void setAfceSign(Double afceSign) {
		this.afceSign = afceSign;
	}

	public String getAfceSignCu() {
		return afceSignCu;
	}

	public void setAfceSignCu(String afceSignCu) {
		this.afceSignCu = afceSignCu;
	}

	public Double getAfceExcRate() {
		return afceExcRate;
	}

	public void setAfceExcRate(Double afceExcRate) {
		this.afceExcRate = afceExcRate;
	}

	public Double getAfcePaid() {
		return afcePaid;
	}

	public void setAfcePaid(Double afcePaid) {
		this.afcePaid = afcePaid;
	}

	public String getAfcePaidCu() {
		return afcePaidCu;
	}

	public void setAfcePaidCu(String afcePaidCu) {
		this.afcePaidCu = afcePaidCu;
	}

	public String getDefunctInd() {
		return defunctInd;
	}

	public void setDefunctInd(String defunctInd) {
		this.defunctInd = defunctInd;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getIsReceivedFunds() {
		return isReceivedFunds;
	}

	public void setIsReceivedFunds(String isReceivedFunds) {
		this.isReceivedFunds = isReceivedFunds;
	}

	public Double getReceivedFunds() {
		return receivedFunds;
	}

	public void setReceivedFunds(Double receivedFunds) {
		this.receivedFunds = receivedFunds;
	}

	public String getReceivedFundsCu() {
		return receivedFundsCu;
	}

	public void setReceivedFundsCu(String receivedFundsCu) {
		this.receivedFundsCu = receivedFundsCu;
	}

	public Date getReceivedFundsTime() {
		return receivedFundsTime;
	}

	public void setReceivedFundsTime(Date receivedFundsTime) {
		this.receivedFundsTime = receivedFundsTime;
	}

	public String getRegistedBy() {
		return registedBy;
	}

	public void setRegistedBy(String registedBy) {
		this.registedBy = registedBy;
	}

	public Date getRegistedTime() {
		return registedTime;
	}

	public void setRegistedTime(Date registedTime) {
		this.registedTime = registedTime;
	}

}
