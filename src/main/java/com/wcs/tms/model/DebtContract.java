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

@Entity
@Table(name = "DEBT_CONTRACT")
public class DebtContract extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6033835301347253740L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEBT_BORROW_PROC_INST_ID", referencedColumnName = "PROC_INST_ID")
	private ProcDebtBorrow procDebtBorrow;

	@Column(name = "IS_CONFIRMED")
	private String isConfirmed;

	@Column(name = "DEBT_CONTRACT_NO")
	private String debtContractNo;

	@Column(name = "DEBT_CONTRACT_FUNDS", precision = 12, scale = 4)
	private Double debtContractFunds;

	@Column(name = "DEBT_CONTRACT_FUNDS_CU")
	private String debtContractFundsCu;

	@Column(name = "CONTRACT_RATE", precision = 12, scale = 4)
	private Double contractRate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CONTRACT_START_DATE")
	private Date contractStartDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CONTRACT_END_DATE")
	private Date contractEndDate;

	@Column(name = "CONTRACT_SUMMITED_BY")
	private String contractSummitedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CONTRACT_SUMMITED_DATE")
	private Date contractSummitedDate;

	@Column(name = "CONTRACT_REGISTED_BY")
	private String contractRegistedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CONTRACT_REGISTED_TIME")
	private Date contractRegistedTime;

	@Column(name = "DEBT_DEADLINE_TYPE")
	private String debtDeadlineType;

	@Column(name = "IS_EXPIRED")
	private String isExpired;

	/*** 资金提供股东 *******/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FUNDS_PROVIDER")
	private ShareHolder shareHolder;

	@Column(name = "APPROVAL_FUNDS", precision = 12, scale = 4)
	private Double approvalFunds;

	@Column(name = "APPROVAL_FUNDS_CU", length = 101)
	private String approvalFundsCu;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "APPROVAL_START_DATE")
	private Date approvalStartDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "APPROVAL_END_DATE")
	private Date approvalEndDate;

	@Column(name = "APPROVAL_RATE", precision = 12, scale = 4)
	private Double approvalRate;

	@Column(name = "APPLIED_FUNDS", precision = 12, scale = 4)
	private Double appliedFunds;

	@Column(name = "APPLIED_FUNDS_CU")
	private String appliedFundsCu;

	@Column(name = "RECEIVED_FUNDS", precision = 12, scale = 4)
	private Double receivedFunds;

	@Column(name = "RECEIVED_FUNDS_CU")
	private String receivedFundsCu;

	@Column(name = "DEFUNCT_IND")
	private String defunctInd;
	//是否为展期
	@Column(name = "IS_EXTEND",length=1)
	private String isExtend;
	//是否已被展期
	@Column(name = "IS_BY_EXTEND",length=1)
	private String isByExtend;

	/** 未请款金额 */
	@Transient
	private Double unAppliedFunds;

	public Double getUnAppliedFunds() {
		try {
			this.unAppliedFunds = this.debtContractFunds - this.appliedFunds;
		} catch (Exception e) {
			this.unAppliedFunds = 0D;
		}
		return unAppliedFunds;
	}

	public void setUnAppliedFunds(Double unAppliedFunds) {
		this.unAppliedFunds = unAppliedFunds;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public ProcDebtBorrow getProcDebtBorrow() {
		return procDebtBorrow;
	}

	public void setProcDebtBorrow(ProcDebtBorrow procDebtBorrow) {
		this.procDebtBorrow = procDebtBorrow;
	}

	public String getIsConfirmed() {
		return isConfirmed;
	}

	public void setIsConfirmed(String isConfirmed) {
		this.isConfirmed = isConfirmed;
	}

	public String getDebtContractNo() {
		return debtContractNo;
	}

	public void setDebtContractNo(String debtContractNo) {
		this.debtContractNo = debtContractNo;
	}

	public Double getDebtContractFunds() {
		return debtContractFunds;
	}

	public void setDebtContractFunds(Double debtContractFunds) {
		this.debtContractFunds = debtContractFunds;
	}

	public String getDebtContractFundsCu() {
		return debtContractFundsCu;
	}

	public void setDebtContractFundsCu(String debtContractFundsCu) {
		this.debtContractFundsCu = debtContractFundsCu;
	}

	public Double getContractRate() {
		return contractRate;
	}

	public void setContractRate(Double contractRate) {
		this.contractRate = contractRate;
	}

	public Date getContractStartDate() {
		return contractStartDate;
	}

	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
	}

	public Date getContractEndDate() {
		return contractEndDate;
	}

	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate = contractEndDate;
	}

	public String getContractSummitedBy() {
		return contractSummitedBy;
	}

	public void setContractSummitedBy(String contractSummitedBy) {
		this.contractSummitedBy = contractSummitedBy;
	}

	public Date getContractSummitedDate() {
		return contractSummitedDate;
	}

	public void setContractSummitedDate(Date contractSummitedDate) {
		this.contractSummitedDate = contractSummitedDate;
	}

	public String getContractRegistedBy() {
		return contractRegistedBy;
	}

	public void setContractRegistedBy(String contractRegistedBy) {
		this.contractRegistedBy = contractRegistedBy;
	}

	public Date getContractRegistedTime() {
		return contractRegistedTime;
	}

	public void setContractRegistedTime(Date contractRegistedTime) {
		this.contractRegistedTime = contractRegistedTime;
	}

	public String getDebtDeadlineType() {
		return debtDeadlineType;
	}

	public void setDebtDeadlineType(String debtDeadlineType) {
		this.debtDeadlineType = debtDeadlineType;
	}

	public String getIsExpired() {
		return isExpired;
	}

	public void setIsExpired(String isExpired) {
		this.isExpired = isExpired;
	}

	public ShareHolder getShareHolder() {
		return shareHolder;
	}

	public void setShareHolder(ShareHolder shareHolder) {
		this.shareHolder = shareHolder;
	}

	public Double getApprovalFunds() {
		return approvalFunds;
	}

	public void setApprovalFunds(Double approvalFunds) {
		this.approvalFunds = approvalFunds;
	}

	public Date getApprovalStartDate() {
		return approvalStartDate;
	}

	public void setApprovalStartDate(Date approvalStartDate) {
		this.approvalStartDate = approvalStartDate;
	}

	public Date getApprovalEndDate() {
		return approvalEndDate;
	}

	public void setApprovalEndDate(Date approvalEndDate) {
		this.approvalEndDate = approvalEndDate;
	}

	public Double getApprovalRate() {
		return approvalRate;
	}

	public void setApprovalRate(Double approvalRate) {
		this.approvalRate = approvalRate;
	}

	public Double getAppliedFunds() {
		return appliedFunds;
	}

	public void setAppliedFunds(Double appliedFunds) {
		this.appliedFunds = appliedFunds;
	}

	public String getAppliedFundsCu() {
		return appliedFundsCu;
	}

	public void setAppliedFundsCu(String appliedFundsCu) {
		this.appliedFundsCu = appliedFundsCu;
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

	public String getDefunctInd() {
		return defunctInd;
	}

	public void setDefunctInd(String defunctInd) {
		this.defunctInd = defunctInd;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String getDisplayText() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getApprovalFundsCu() {
		return approvalFundsCu;
	}

	public void setApprovalFundsCu(String approvalFundsCu) {
		this.approvalFundsCu = approvalFundsCu;
	}

	public String getIsExtend() {
		return isExtend;
	}

	public void setIsExtend(String isExtend) {
		this.isExtend = isExtend;
	}

	public String getIsByExtend() {
		return isByExtend;
	}

	public void setIsByExtend(String isByExtend) {
		this.isByExtend = isByExtend;
	}

}
