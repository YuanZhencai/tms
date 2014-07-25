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
 * 
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description: 注册资本金Model
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Entity
@Table(name = "PROC_REGI_CAPITAL")
public class ProcRegiCapital extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 公司 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;
	/** 流程实例Id */
	@Column(name = "PROC_INST_ID")
	private String procInstId;
	/** 目前公司余额 */
	@Column(name = "REST", precision = 12, scale = 4)
	private Double rest;
	/** 目前公司余额币别 */
	@Column(name = "REST_CU", length = 101)
	private String restCu;
	/** 申请日期 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "APPLY_DATE")
	private Date applyDate;
	/** 申请类型 */
	@Column(name = "APPLY_TYPE")
	private String applyType;
	/** 付款方 */
	@Column(name = "PAYER")
	private Long payer;
	/** 已签订合同金额 */
	@Column(name = "SIGN_CONTRACT", precision = 12, scale = 4)
	private Double signContract;
	/** 已签订合同金额币别 */
	@Column(name = "SIGN_CONT_CU", length = 101)
	private String signConsCu;
	/** 其中已付款金额 */
	@Column(name = "PAID_FUNDS", precision = 12, scale = 4)
	private Double paidFunds;
	/** 已付款币别 */
	@Column(name = "PAID_FUNDS_CU", length = 101)
	private String paidFundsCu;
	/** 可用资金额度 */
	@Column(name = "CAN_USE", precision = 12, scale = 4)
	private Double canUse;
	/** 可用资金币别 */
	@Column(name = "CAN_USE_CU", length = 101)
	private String canUseCu;
	/** 保证金额度 */
	@Column(name = "INSURE", precision = 12, scale = 4)
	private Double insure;
	/** 保证金额度币别 */
	@Column(name = "INSURE_CU", length = 101)
	private String insureCu;
	/** 本次申请资金金额 */
	@Column(name = "THIS_FONDS", precision = 12, scale = 4)
	private Double thisFonds;
	/** 本次申请资金币别 */
	@Column(name = "THIS_FONDS_CU", length = 101)
	private String thisFondsCu;
	/** 申请资金用途 */
	@Column(name = "USE", length = 2000)
	private String use;
	/** 备注 */
	@Column(name = "REMARK", length = 2000)
	private String remark;
	/** 汇款路线及其账号 */
	@Column(name = "PATH_AND_COUNT", length = 6000)
	private String pathAndCount;
	/** 汇款路线 **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REMITTANCE_LINE_ACCOUNT_ID")
	private RemittanceLineAccount remittanceLineAccount;

	/** 实际审批金额 */
	@Column(name = "ACTUAL_AUDIT", precision = 12, scale = 4)
	private Double actualAudit;
	/** 实际审批金额币别 */
	@Column(name = "ACTUAL_AUDIT_CU", length = 101)
	private String actualAuditCu;

	/** 保存PE的备注 */
	@Transient
	private String peMemo;
	@Transient
	private Double notPayFounds;

	/**
	 * 流程申请时,原股东主数据表信息
	 */
	/** 已到位资金 */
	@Column(name = "FONDS_IN_PLACE", precision = 12, scale = 4)
	private Double fondsInPlace;
	/** 股东名称 */
	@Column(name = "SHAREHOLDER_NAME", length = 255)
	private String shareHolderName;
	/** 资金类别 */
	@Column(name = "FONDS_CURRENCY", length = 101)
	private String fondsCurrency;
	/** 股权比例 */
	@Column(name = "EQUITY_PERC", precision = 12, scale = 4)
	private Double equityPerc;
	/** 注册资本金额 */
	@Column(name = "FONDS_TOTAL", precision = 12, scale = 4)
	private Double fondsTotal;
	@Transient
	private Double fondsNotTotal;
	/** 是否股权关联 */
	@Column(name = "IS_EQUITY_RELATED", length = 1)
	private String isEquityRelated;
	/** 关联比例 */
	@Column(name = "RELATED_PERC", precision = 12, scale = 4)
	private Double relatedPerc;
	/** 流程状态 */
	@Column(name = "PROCESS_STATUS")
	private String processStatus;

	/** 是否到账 */
	@Column(name = "IS_RECEIVED_FUNDS", length = 1)
	private String isReceivedFunds;

	/** 到账金额 */
	@Column(name = "RECEIVED_FUNDS", precision = 12, scale = 4)
	private Double receivedFunds;
	/** 到账金额币别 */
	@Column(name = "RECEIVED_FUNDS_CU", length = 101)
	private String receivedFundsCu;

	/** 到账日期 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RECEIVED_FUNDS_DATE")
	private Date receivedFundsDate;

	/** 到账登记人员 */
	@Column(name = "REGISTER_BY", length = 50)
	private String registerBy;

	/** 到账登记日期 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REGISTER_DATETIME")
	private Date registerDateTime;

	/** default constructor */
	public ProcRegiCapital() {
		this.remittanceLineAccount = new RemittanceLineAccount();
		this.company = new Company();

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

	public Double getRest() {
		return this.rest;
	}

	public void setRest(Double rest) {
		this.rest = rest;
	}

	public String getRestCu() {
		return this.restCu;
	}

	public void setRestCu(String restCu) {
		this.restCu = restCu;
	}

	public Double getCanUse() {
		return this.canUse;
	}

	public void setCanUse(Double canUse) {
		this.canUse = canUse;
	}

	public String getCanUseCu() {
		return this.canUseCu;
	}

	public void setCanUseCu(String canUseCu) {
		this.canUseCu = canUseCu;
	}

	public Double getInsure() {
		return this.insure;
	}

	public void setInsure(Double insure) {
		this.insure = insure;
	}

	public String getInsureCu() {
		return this.insureCu;
	}

	public void setInsureCu(String insureCu) {
		this.insureCu = insureCu;
	}

	public Double getThisFonds() {
		return this.thisFonds;
	}

	public void setThisFonds(Double thisFonds) {
		this.thisFonds = thisFonds;
	}

	public String getThisFondsCu() {
		return this.thisFondsCu;
	}

	public void setThisFondsCu(String thisFondsCu) {
		this.thisFondsCu = thisFondsCu;
	}

	public String getUse() {
		return this.use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public String getPathAndCount() {
		return this.pathAndCount;
	}

	public void setPathAndCount(String pathAndCount) {
		this.pathAndCount = pathAndCount;
	}

	public Double getActualAudit() {
		return this.actualAudit;
	}

	public void setActualAudit(Double actualAudit) {
		this.actualAudit = actualAudit;
	}

	public String getActualAuditCu() {
		return this.actualAuditCu;
	}

	public void setActualAuditCu(String actualAuditCu) {
		this.actualAuditCu = actualAuditCu;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	@Override
	@Transient
	public String getDisplayText() {
		return null;
	}

	public String getPeMemo() {
		return peMemo;
	}

	public void setPeMemo(String peMemo) {
		this.peMemo = peMemo;
	}

	public String getApplyType() {
		return applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	public Double getSignContract() {
		return signContract;
	}

	public void setSignContract(Double signContract) {
		this.signContract = signContract;
	}

	public Double getPaidFunds() {
		return paidFunds;
	}

	public void setPaidFunds(Double paidFunds) {
		this.paidFunds = paidFunds;
	}

	public String getSignConsCu() {
		return signConsCu;
	}

	public void setSignConsCu(String signConsCu) {
		this.signConsCu = signConsCu;
	}

	public String getPaidFundsCu() {
		return paidFundsCu;
	}

	public void setPaidFundsCu(String paidFundsCu) {
		this.paidFundsCu = paidFundsCu;
	}

	public Long getPayer() {
		return payer;
	}

	public void setPayer(Long payer) {
		this.payer = payer;
	}

	public Double getNotPayFounds() {
		return notPayFounds;
	}

	public void setNotPayFounds(Double notPayFounds) {
		this.notPayFounds = notPayFounds;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public RemittanceLineAccount getRemittanceLineAccount() {
		return remittanceLineAccount;
	}

	public void setRemittanceLineAccount(
			RemittanceLineAccount remittanceLineAccount) {
		this.remittanceLineAccount = remittanceLineAccount;
	}

	public Double getFondsInPlace() {
		return fondsInPlace;
	}

	public void setFondsInPlace(Double fondsInPlace) {
		this.fondsInPlace = fondsInPlace;
	}

	public String getShareHolderName() {
		return shareHolderName;
	}

	public void setShareHolderName(String shareHolderName) {
		this.shareHolderName = shareHolderName;
	}

	public String getFondsCurrency() {
		return fondsCurrency;
	}

	public void setFondsCurrency(String fondsCurrency) {
		this.fondsCurrency = fondsCurrency;
	}

	public Double getEquityPerc() {
		return equityPerc;
	}

	public void setEquityPerc(Double equityPerc) {
		this.equityPerc = equityPerc;
	}

	public Double getFondsTotal() {
		return fondsTotal;
	}

	public void setFondsTotal(Double fondsTotal) {
		this.fondsTotal = fondsTotal;
	}

	public Double getFondsNotTotal() {
		return fondsNotTotal;
	}

	public void setFondsNotTotal(Double fondsNotTotal) {
		this.fondsNotTotal = fondsNotTotal;
	}

	public String getIsEquityRelated() {
		return isEquityRelated;
	}

	public void setIsEquityRelated(String isEquityRelated) {
		this.isEquityRelated = isEquityRelated;
	}

	public Double getRelatedPerc() {
		return relatedPerc;
	}

	public void setRelatedPerc(Double relatedPerc) {
		this.relatedPerc = relatedPerc;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
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

	public Date getReceivedFundsDate() {
		return receivedFundsDate;
	}

	public void setReceivedFundsDate(Date receivedFundsDate) {
		this.receivedFundsDate = receivedFundsDate;
	}

	public String getRegisterBy() {
		return registerBy;
	}

	public void setRegisterBy(String registerBy) {
		this.registerBy = registerBy;
	}

	public Date getRegisterDateTime() {
		return registerDateTime;
	}

	public void setRegisterDateTime(Date registerDateTime) {
		this.registerDateTime = registerDateTime;
	}

}