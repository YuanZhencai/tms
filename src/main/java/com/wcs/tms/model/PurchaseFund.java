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

import com.wcs.base.model.BaseEntity;

/** 
* <p>Project: tms</p> 
* <p>Title: 采购资金(贸易或生产)_主数据Model</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
@Entity
@Table(name = "PURCHASE_FUND")
public class PurchaseFund extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;

	/* 流程实例ID */
	@Column(name = "PROC_INST_ID", length = 255)
	private String procInstId;
	/* 帐号描述 */
	@Column(name = "ACCOUNT_DESC", length = 255)
	private String accountDesc;
	/* 类型 */
	@Column(name = "TAPE", length = 1)
	private String tape;
	/* 付款日期 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PAY_TIME")
	private Date payTime;
	/* 流程申请时间 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "APP_TIME")
	private Date appTime;
	/* 借款转款标识 */
	@Column(name = "LOAN_IDEN", length = 1)
	private String loanIden;
	/* 收款人户名 */
	@Column(name = "RECEIVER_NAME", length = 255)
	private String receiverName;
	/* 帐号 */
	@Column(name = "ACCOUNTS", length = 255)
	private String accounts;
	/* 下拨资金用途描述 */
	@Column(name = "FUND_PURP_DESC", length = 2000)
	private String fundPurpDesc;
	/* 品种 */
	@Column(name = "VARIETY", length = 101)
	private String variety;
	/* 是否为关联方 */
	@Column(name = "VARIETY_RELATED", length = 1)
	private String varietyRelated;
	/* 品种剩余头寸 */
	@Column(name = "VARIETY_REMAIN", precision = 12, scale = 4)
	private Double varietyRemain;
	/* 品种申请数量 */
	@Column(name = "VARIETY_NUM", precision = 12, scale = 4)
	private Double varietyNum;
	/* 品种申请金额 */
	@Column(name = "VARIETY_AMOUNT", precision = 12, scale = 4)
	private Double varietyAmount;
	/* 品种备注 */
	@Column(name = "VARIETY_REMARK", length = 2000)
	private String varietyRemark;

	public PurchaseFund() {
		company = new Company();
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

	public String getAccountDesc() {
		return accountDesc;
	}

	public void setAccountDesc(String accountDesc) {
		this.accountDesc = accountDesc;
	}

	public String getTape() {
		return tape;
	}

	public void setTape(String tape) {
		this.tape = tape;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public String getLoanIden() {
		return loanIden;
	}

	public void setLoanIden(String loanIden) {
		this.loanIden = loanIden;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getAccounts() {
		return accounts;
	}

	public void setAccounts(String accounts) {
		this.accounts = accounts;
	}

	public String getFundPurpDesc() {
		return fundPurpDesc;
	}

	public void setFundPurpDesc(String fundPurpDesc) {
		this.fundPurpDesc = fundPurpDesc;
	}

	public String getVariety() {
		return variety;
	}

	public void setVariety(String variety) {
		this.variety = variety;
	}

	public Double getVarietyRemain() {
		return varietyRemain;
	}

	public void setVarietyRemain(Double varietyRemain) {
		this.varietyRemain = varietyRemain;
	}

	public Double getVarietyNum() {
		return varietyNum;
	}

	public void setVarietyNum(Double varietyNum) {
		this.varietyNum = varietyNum;
	}

	public Double getVarietyAmount() {
		return varietyAmount;
	}

	public void setVarietyAmount(Double varietyAmount) {
		this.varietyAmount = varietyAmount;
	}

	public String getVarietyRemark() {
		return varietyRemark;
	}

	public void setVarietyRemark(String varietyRemark) {
		this.varietyRemark = varietyRemark;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((variety == null) ? 0 : variety.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PurchaseFund other = (PurchaseFund) obj;
		if (variety == null) {
			if (other.variety != null) {
				return false;
			}
		} else if (!variety.equals(other.variety)) {
			return false;
		}
		return true;
	}

	@Override
	public String getDisplayText() {
		return null;
	}

	public Date getAppTime() {
		return appTime;
	}

	public void setAppTime(Date appTime) {
		this.appTime = appTime;
	}

	public String getVarietyRelated() {
		return varietyRelated;
	}

	public void setVarietyRelated(String varietyRelated) {
		this.varietyRelated = varietyRelated;
	}
}