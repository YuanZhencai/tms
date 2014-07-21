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
 * Description: 流程_外债股东借款申请
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Entity
@Table(name = "PROC_DEBT_BORROW")
public class ProcDebtBorrow extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;

	/*** 资金提供股东 *******/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SH_ID")
	private ShareHolder shareHolder;

	@Column(name = "PROC_INST_ID")
	private String procInstId;

	@Column(name = "COMPANY_NAME", length = 255)
	private String companyName;

	@Column(name = "COMPANY_EN", length = 255)
	private String companyEn;

	@Column(name = "INVEST_BALANCE", precision = 12, scale = 4)
	private Double investBalance;

	@Column(name = "INVEST_BALANCE_CU", length = 101)
	private String investBalanceCu;

	@Column(name = "BEBT_IN_PLACE", precision = 12, scale = 4)
	private Double bebtInPlace;

	@Column(name = "BEBT_IN_PLACE_CU", length = 101)
	private String bebtInPlaceCu;

	@Column(name = "SH_BORROW", precision = 12, scale = 4)
	private Double shBorrow;

	@Column(name = "SH_BORROW_CU", length = 101)
	private String shBorrowCu;

	@Column(name = "SH_BORROW_LI", precision = 4, scale = 0)
	private Double shBorrowLi;

	@Column(name = "SH_BORROW_RA", precision = 12, scale = 4)
	private Double shBorrowRa;

	@Column(name = "FORN_BEBT", precision = 12, scale = 4)
	private Double fornBebt;

	@Column(name = "FORN_BEBT_CU", length = 101)
	private String fornBebtCu;

	@Column(name = "FORN_BEBT_LI", precision = 4, scale = 0)
	private Double fornBebtLi;

	@Column(name = "FORN_BEBT_RA", precision = 12, scale = 4)
	private Double fornBebtRa;

	/****** 可用外债额度 改为 可用投注差 ***************/
	@Column(name = "AVAILB_BEBT", precision = 12, scale = 4)
	private Double availbBebt;

	@Column(name = "AVAILB_BEBT_CU", length = 101)
	private String availbBebtCu;

	@Column(name = "THIS_SH_BORROW_SE", length = 1)
	private String thisShBorrowSe;

	@Column(name = "THIS_SH_BORROW", precision = 12, scale = 4)
	private Double thisShBorrow;

	@Column(name = "THIS_SH_BORROW_CU", length = 101)
	private String thisShBorrowCu;

	@Column(name = "THIS_SH_BORROW_LI", precision = 4, scale = 0)
	private Double thisShBorrowLi;

	/****** 本次股东借款起始年月 ***************/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "THIS_SH_BORROW_LIS")
	private Date thisShBorrowLis;

	/****** 本次股东借款结束年月 ***************/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "THIS_SH_BORROW_LIE")
	private Date thisShBorrowLie;

	@Column(name = "THIS_SH_BORROW_RA", length = 100)
	private String thisShBorrowRa;

	/** 外债提供方 (去除掉) start ****/
	@Column(name = "BEBT_PROVIDER", precision = 12, scale = 4)
	private Double bebtProvider;

	@Column(name = "BEBT_PROVIDER_CU", length = 101)
	private String bebtProviderCu;

	@Column(name = "BEBT_PROVIDER_LI", precision = 4, scale = 0)
	private Double bebtProviderLi;

	@Column(name = "BEBT_PROVIDER_RA", precision = 12, scale = 4)
	private Double bebtProviderRa;
	/** 外债提供方 (去除掉) end ****/

	@Column(name = "USE", length = 2000)
	private String use;

	@Column(name = "AFCE_FLAG", length = 1)
	private String afceFlag = "N";

	@Column(name = "AFCE_SIGN", precision = 12, scale = 4)
	private Double afceSign;

	@Column(name = "AFCE_SIGN_CU", length = 101)
	private String afceSignCu;

	/** AFCE折算汇率 ****/
	@Column(name = "AFCE_EXC_RATE", precision = 12, scale = 4)
	private Double afceExcRate;

	/** AFCE签批情况(折算值) ****/
	@Transient
	private Double afceSignExc;

	/** AFCE已付款金额 ****/
	@Column(name = "AFCE_PAID", precision = 12, scale = 4)
	private Double afcePaid;

	/** AFCE已付款金额币别 ****/
	@Column(name = "AFCE_PAID_CU", length = 101)
	private String afcePaidCu;

	@Column(name = "CORP_AUDIT", precision = 12, scale = 4)
	private Double corpAudit;

	@Column(name = "CORP_AUDIT_CU", length = 101)
	private String corpAuditCu;

	@Column(name = "CORP_AUDIT_LI", precision = 4, scale = 0)
	private Double corpAuditLi;

	/****** 集团项目审批期限起始年月 ***************/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CORP_AUDIT_LIS")
	private Date corpAuditLis;

	/****** 集团项目审批期限结束年月 ***************/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CORP_AUDIT_LIE")
	private Date corpAuditLie;

	@Column(name = "CORP_AUDIT_RA", length = 100)
	private String corpAuditRa;

	/** 备注(小股东是否按比例提供资金) ****/
	@Column(name = "MEMO", length = 2000)
	private String Memo;

	/** 还款方式及来源 ****/
	@Column(name = "PAYBACK_MEMO", length = 2000)
	private String payBackMemo;

	/** 资金提供方类型 ****/
	@Column(name = "PROVIDER_TYPE", length = 1)
	private String providerType;

	/** 资金提供公司KEY ****/
	@Column(name = "PROVIDER_KEY", length = 101)
	private String providerKey;

	/** 折算汇率 ****/
	@Column(name = "EXCHANGE_RATE", precision = 12, scale = 4)
	private Double exchangeRate;

	/** 保存PE的备注 */
	@Transient
	private String peMemo;
	// Constructors

	/** 贷款时间 */
	@Transient
	private Date borrowDate;

	/** 还款时间 */
	@Transient
	private Date payBackDate;

	/** 资金提供方名称 ****/
	@Transient
	private String providerName;

	/** 2014-07-04 add by liushengbin 新加字段 **/

	/*** 外债期限类型 **/
	private String bebtDeadlineType;
	/*** 申请类型 **/
	private String applyType;
	/*** 展期原外债合同主数据ID 关联 ‘外债合同_主数据(DEBT_CONTRACT)’表 **/
	private Long debtContractId;
	/** 当前节点 **/
	private String currentNode;
	/** 流程状态 **/
	private String processStatus;

	/** default constructor */
	public ProcDebtBorrow() {
		this.company = new Company();
		this.shareHolder = new ShareHolder();
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public ShareHolder getShareHolder() {
		return shareHolder;
	}

	public void setShareHolder(ShareHolder shareHolder) {
		this.shareHolder = shareHolder;
	}

	public String getPayBackMemo() {
		return payBackMemo;
	}

	public void setPayBackMemo(String payBackMemo) {
		this.payBackMemo = payBackMemo;
	}

	public String getProviderType() {
		return providerType;
	}

	public void setProviderType(String providerType) {
		this.providerType = providerType;
	}

	public String getProviderKey() {
		return providerKey;
	}

	public void setProviderKey(String providerKey) {
		this.providerKey = providerKey;
	}

	public Double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(Double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public Date getBorrowDate() {
		return borrowDate;
	}

	public void setBorrowDate(Date borrowDate) {
		this.borrowDate = borrowDate;
	}

	public Date getPayBackDate() {
		return payBackDate;
	}

	public void setPayBackDate(Date payBackDate) {
		this.payBackDate = payBackDate;
	}

	public String getPeMemo() {
		return peMemo;
	}

	public void setPeMemo(String peMemo) {
		this.peMemo = peMemo;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getProcInstId() {
		return this.procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyEn() {
		return this.companyEn;
	}

	public void setCompanyEn(String companyEn) {
		this.companyEn = companyEn;
	}

	public Double getInvestBalance() {
		return this.investBalance;
	}

	public void setInvestBalance(Double investBalance) {
		this.investBalance = investBalance;
	}

	public String getInvestBalanceCu() {
		return this.investBalanceCu;
	}

	public void setInvestBalanceCu(String investBalanceCu) {
		this.investBalanceCu = investBalanceCu;
	}

	public Double getBebtInPlace() {
		return this.bebtInPlace;
	}

	public void setBebtInPlace(Double bebtInPlace) {
		this.bebtInPlace = bebtInPlace;
	}

	public String getBebtInPlaceCu() {
		return this.bebtInPlaceCu;
	}

	public void setBebtInPlaceCu(String bebtInPlaceCu) {
		this.bebtInPlaceCu = bebtInPlaceCu;
	}

	public Double getShBorrow() {
		return this.shBorrow;
	}

	public void setShBorrow(Double shBorrow) {
		this.shBorrow = shBorrow;
	}

	public String getShBorrowCu() {
		return this.shBorrowCu;
	}

	public void setShBorrowCu(String shBorrowCu) {
		this.shBorrowCu = shBorrowCu;
	}

	public Double getShBorrowLi() {
		return this.shBorrowLi;
	}

	public void setShBorrowLi(Double shBorrowLi) {
		this.shBorrowLi = shBorrowLi;
	}

	public Double getShBorrowRa() {
		return this.shBorrowRa;
	}

	public void setShBorrowRa(Double shBorrowRa) {
		this.shBorrowRa = shBorrowRa;
	}

	public Double getFornBebt() {
		return this.fornBebt;
	}

	public void setFornBebt(Double fornBebt) {
		this.fornBebt = fornBebt;
	}

	public String getFornBebtCu() {
		return this.fornBebtCu;
	}

	public void setFornBebtCu(String fornBebtCu) {
		this.fornBebtCu = fornBebtCu;
	}

	public Double getFornBebtLi() {
		return this.fornBebtLi;
	}

	public void setFornBebtLi(Double fornBebtLi) {
		this.fornBebtLi = fornBebtLi;
	}

	public Double getFornBebtRa() {
		return this.fornBebtRa;
	}

	public void setFornBebtRa(Double fornBebtRa) {
		this.fornBebtRa = fornBebtRa;
	}

	public Double getAvailbBebt() {
		return this.availbBebt;
	}

	public void setAvailbBebt(Double availbBebt) {
		this.availbBebt = availbBebt;
	}

	public String getAvailbBebtCu() {
		return this.availbBebtCu;
	}

	public void setAvailbBebtCu(String availbBebtCu) {
		this.availbBebtCu = availbBebtCu;
	}

	public String getThisShBorrowSe() {
		return thisShBorrowSe;
	}

	public void setThisShBorrowSe(String thisShBorrowSe) {
		this.thisShBorrowSe = thisShBorrowSe;
	}

	public Double getThisShBorrow() {
		return this.thisShBorrow;
	}

	public void setThisShBorrow(Double thisShBorrow) {
		this.thisShBorrow = thisShBorrow;
	}

	public String getThisShBorrowCu() {
		return this.thisShBorrowCu;
	}

	public void setThisShBorrowCu(String thisShBorrowCu) {
		this.thisShBorrowCu = thisShBorrowCu;
	}

	public Double getThisShBorrowLi() {
		return this.thisShBorrowLi;
	}

	public void setThisShBorrowLi(Double thisShBorrowLi) {
		this.thisShBorrowLi = thisShBorrowLi;
	}

	public String getThisShBorrowRa() {
		return this.thisShBorrowRa;
	}

	public void setThisShBorrowRa(String thisShBorrowRa) {
		this.thisShBorrowRa = thisShBorrowRa;
	}

	public Double getBebtProvider() {
		return this.bebtProvider;
	}

	public void setBebtProvider(Double bebtProvider) {
		this.bebtProvider = bebtProvider;
	}

	public String getBebtProviderCu() {
		return this.bebtProviderCu;
	}

	public void setBebtProviderCu(String bebtProviderCu) {
		this.bebtProviderCu = bebtProviderCu;
	}

	public Double getBebtProviderLi() {
		return this.bebtProviderLi;
	}

	public void setBebtProviderLi(Double bebtProviderLi) {
		this.bebtProviderLi = bebtProviderLi;
	}

	public Double getBebtProviderRa() {
		return this.bebtProviderRa;
	}

	public void setBebtProviderRa(Double bebtProviderRa) {
		this.bebtProviderRa = bebtProviderRa;
	}

	public String getUse() {
		return this.use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public String getAfceFlag() {
		return afceFlag;
	}

	public void setAfceFlag(String afceFlag) {
		this.afceFlag = afceFlag;
	}

	public Double getAfceSign() {
		return this.afceSign;
	}

	public void setAfceSign(Double afceSign) {
		this.afceSign = afceSign;
	}

	public String getAfceSignCu() {
		return this.afceSignCu;
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

	public Double getCorpAudit() {
		return this.corpAudit;
	}

	public void setCorpAudit(Double corpAudit) {
		this.corpAudit = corpAudit;
	}

	public String getCorpAuditCu() {
		return this.corpAuditCu;
	}

	public void setCorpAuditCu(String corpAuditCu) {
		this.corpAuditCu = corpAuditCu;
	}

	public Double getCorpAuditLi() {
		return this.corpAuditLi;
	}

	public void setCorpAuditLi(Double corpAuditLi) {
		this.corpAuditLi = corpAuditLi;
	}

	public String getCorpAuditRa() {
		return this.corpAuditRa;
	}

	public void setCorpAuditRa(String corpAuditRa) {
		this.corpAuditRa = corpAuditRa;
	}

	public Date getThisShBorrowLis() {
		return thisShBorrowLis;
	}

	public void setThisShBorrowLis(Date thisShBorrowLis) {
		this.thisShBorrowLis = thisShBorrowLis;
	}

	public Date getThisShBorrowLie() {
		return thisShBorrowLie;
	}

	public void setThisShBorrowLie(Date thisShBorrowLie) {
		this.thisShBorrowLie = thisShBorrowLie;
	}

	public Date getCorpAuditLis() {
		return corpAuditLis;
	}

	public void setCorpAuditLis(Date corpAuditLis) {
		this.corpAuditLis = corpAuditLis;
	}

	public Date getCorpAuditLie() {
		return corpAuditLie;
	}

	public void setCorpAuditLie(Date corpAuditLie) {
		this.corpAuditLie = corpAuditLie;
	}

	public Double getAfceSignExc() {
		return afceSignExc;
	}

	public void setAfceSignExc(Double afceSignExc) {
		this.afceSignExc = afceSignExc;
	}

	public String getMemo() {
		return Memo;
	}

	public void setMemo(String memo) {
		Memo = memo;
	}

	@Override
	@Transient
	public String getDisplayText() {
		return null;
	}

	public String getBebtDeadlineType() {
		return bebtDeadlineType;
	}

	public void setBebtDeadlineType(String bebtDeadlineType) {
		this.bebtDeadlineType = bebtDeadlineType;
	}

	public String getApplyType() {
		return applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	public Long getDebtContractId() {
		return debtContractId;
	}

	public void setDebtContractId(Long debtContractId) {
		this.debtContractId = debtContractId;
	}

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

}