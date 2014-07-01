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
 * Description: 银行账户申请流程实体
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */

@Entity
@Table(name = "PROC_BANK_ACCOUNT")
public class ProcBankAccount extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/** 公司信息 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;
	/** 销户流程实例ID */
	@Column(name = "PROC_INST_ID_CLOSED", length = 255)
	private String procInstIdClosed;
	/** 申请人确认银行code */
	@Column(name = "BANK_CODE", length = 255)
	private String bankCode;
	/** 所属银行名 */
	@Column(name = "BELONG_BANK_NAME", length = 300)
	private String belongBankName;
	/** 账户标识 */
	@Column(name = "ACCOUNT_IDENT", length = 255)
	private String accountIdent;
	/** 流程实例ID */
	@Column(name = "PROC_INST_ID", length = 255)
	private String procInstId;
	/** 账户类型 */
	@Column(name = "ACCOUNT_TYPE", length = 50)
	private String accountType;
	/** 操作类型 */
	@Column(name = "OPERATE_TYPE", length = 50)
	private String operateType;
	/** 持卡人姓名 */
	@Column(name = "CARD_HOLDER_NAME", length = 100)
	private String cardHolderName;
	/** 持卡人职位 */
	@Column(name = "CARD_HOLDER_POSITION", length = 255)
	private String cardHolderPosition;
	/** 用途 */
	@Column(name = "USE_DESC", length = 1000)
	private String useDesc;
	/** 已有卡数量 */
	@Column(name = "OWNED_CARD_NUMBER")
	private Integer ownedCardNumber;
	/** 使用期限 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "USE_DEADLINE")
	private Date useDeadline;
	/** 使用时间 */
	@Column(name = "USE_TIME", length = 50)
	private String useTime;
	/** 城市 */
	@Column(name = "CITY", length = 50)
	private String city;
	/** 中行机构号 */
	@Column(name = "BOC_ORGAN_NUMBER", length = 255)
	private String bocOrganNumber;
	/** 账户描述 */
	@Column(name = "ACCOUNT_DESC", length = 255)
	private String accountDesc;
	/** 开户原因 */
	@Column(name = "OPEN_ACCOUNT_REASON", length = 1000)
	private String openAccountReason;
	/** SAP银行存款活期科目号 */
	@Column(name = "SAP_BANK_SAVE_CURR_SUBJ_NUM", length = 255)
	private String sapBankSaveCurrSubjNum;
	/** SAP业务对应科目 */
	@Column(name = "SAP_BUS_SUBJ", length = 255)
	private String sapBusSubj;
	/** TMS银行账户代码 */
	@Column(name = "TMS_BANK_ACCOUNT_CODE", length = 255)
	private String tmsBankAccountCode;
	/** 银行账号 */
	@Column(name = "BANK_ACCOUNT", length = 255)
	private String bankAccount;
	/** 币别 */
	@Column(name = "CURRENCY", length = 300)
	private String currency;
	/** 账户性质 */
	@Column(name = "ACCOUNT_NATURE", length = 300)
	private String accountNature;
	/** 是否需要对账 */
	@Column(name = "ACCOUNT_CHECK_FLAG", length = 1)
	private String accountCheckFlag;

	/** 保存PE的备注 */
	@Transient
	private String peMemo;

	public ProcBankAccount() {
		this.company = new Company();
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	public String getCardHolderPosition() {
		return cardHolderPosition;
	}

	public void setCardHolderPosition(String cardHolderPosition) {
		this.cardHolderPosition = cardHolderPosition;
	}

	public String getUseDesc() {
		return useDesc;
	}

	public void setUseDesc(String useDesc) {
		this.useDesc = useDesc;
	}

	public Integer getOwnedCardNumber() {
		return ownedCardNumber;
	}

	public void setOwnedCardNumber(Integer ownedCardNumber) {
		this.ownedCardNumber = ownedCardNumber;
	}

	public Date getUseDeadline() {
		return useDeadline;
	}

	public void setUseDeadline(Date useDeadline) {
		this.useDeadline = useDeadline;
	}

	public String getUseTime() {
		return useTime;
	}

	public void setUseTime(String useTime) {
		this.useTime = useTime;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getBocOrganNumber() {
		return bocOrganNumber;
	}

	public void setBocOrganNumber(String bocOrganNumber) {
		this.bocOrganNumber = bocOrganNumber;
	}

	public String getAccountDesc() {
		return accountDesc;
	}

	public void setAccountDesc(String accountDesc) {
		this.accountDesc = accountDesc;
	}

	public String getOpenAccountReason() {
		return openAccountReason;
	}

	public void setOpenAccountReason(String openAccountReason) {
		this.openAccountReason = openAccountReason;
	}

	public String getSapBankSaveCurrSubjNum() {
		return sapBankSaveCurrSubjNum;
	}

	public void setSapBankSaveCurrSubjNum(String sapBankSaveCurrSubjNum) {
		this.sapBankSaveCurrSubjNum = sapBankSaveCurrSubjNum;
	}

	public String getSapBusSubj() {
		return sapBusSubj;
	}

	public void setSapBusSubj(String sapBusSubj) {
		this.sapBusSubj = sapBusSubj;
	}

	public String getTmsBankAccountCode() {
		return tmsBankAccountCode;
	}

	public void setTmsBankAccountCode(String tmsBankAccountCode) {
		this.tmsBankAccountCode = tmsBankAccountCode;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getAccountNature() {
		return accountNature;
	}

	public void setAccountNature(String accountNature) {
		this.accountNature = accountNature;
	}

	public String getAccountCheckFlag() {
		return accountCheckFlag;
	}

	public void setAccountCheckFlag(String accountCheckFlag) {
		this.accountCheckFlag = accountCheckFlag;
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
		return null;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getAccountIdent() {
		return accountIdent;
	}

	public void setAccountIdent(String accountIdent) {
		this.accountIdent = accountIdent;
	}

	public String getBelongBankName() {
		return belongBankName;
	}

	public void setBelongBankName(String belongBankName) {
		this.belongBankName = belongBankName;
	}

	public String getProcInstIdClosed() {
		return procInstIdClosed;
	}

	public void setProcInstIdClosed(String procInstIdClosed) {
		this.procInstIdClosed = procInstIdClosed;
	}

}
