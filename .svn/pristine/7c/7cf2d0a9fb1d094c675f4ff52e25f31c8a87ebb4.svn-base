package com.wcs.tms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * The persistent class for the BANK database table.
 * 
 */
/**
 * 
 * <p>Project: tms</p>
 * <p>Description:银行实体</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Entity
@Table(name = "BANK")
public class Bank extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "BAN_ID")
    private Long topBankId;

    @Column(name = "BANK_CODE", length = 100)
    private String bankCode;

    @Column(name = "BANK_NAME", length = 255)
    private String bankName;

    @Column(name = "BANK_EN", length = 255)
    private String bankEn;

    @Column(name = "BSB_CODE", length = 100)
    private String bsbCode;

    @Column(name = "COUNTERPARTY_CODE", length = 100)
    private String counterpartyCode;

    @Column(name = "STATUS", length = 1)
    private String status = "Y";

    @Column(name = "TOP_LEVEL_FLAG", length = 1)
    private String topLevelFlag = "N";
    
    @Column(name = "ACCOUNT_BANK_FLAG", length = 2)
    private String accountBankFlag = "C";

    @Column(name = "UNION_BANK_NO", length = 100)
    private String unionBankNo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bank")
    private List<Credit> credits = new ArrayList<Credit>(0);
    @OneToMany(fetch = FetchType.LAZY, mappedBy="bank")
	private List<Guarantee> guarantees = new ArrayList<Guarantee>(0);
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bank")
    private List<ProcBankCredit> procBankCredits = new ArrayList<ProcBankCredit>(0);
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bank")
    private List<ProcBankCreditConfirm> procBankCreditConfirms = new ArrayList<ProcBankCreditConfirm>(0);

    public Bank() {

    }

    public String getBankCode() {
        return this.bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankEn() {
        return this.bankEn;
    }

    public void setBankEn(String bankEn) {
        this.bankEn = bankEn;
    }

    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBsbCode() {
        return this.bsbCode;
    }

    public void setBsbCode(String bsbCode) {
        this.bsbCode = bsbCode;
    }

    public String getCounterpartyCode() {
        return this.counterpartyCode;
    }

    public void setCounterpartyCode(String counterpartyCode) {
        this.counterpartyCode = counterpartyCode;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTopLevelFlag() {
        return this.topLevelFlag;
    }

    public void setTopLevelFlag(String topLevelFlag) {
        this.topLevelFlag = topLevelFlag;
    }

    public String getUnionBankNo() {
        return this.unionBankNo;
    }

    public void setUnionBankNo(String unionBankNo) {
        this.unionBankNo = unionBankNo;
    }

    public Long getTopBankId() {
        return topBankId;
    }

    public void setTopBankId(Long topBankId) {
        this.topBankId = topBankId;
    }

    public List<Credit> getCredits() {
        return this.credits;
    }

    public void setCredits(List<Credit> credits) {
        this.credits = credits;
    }
    public List<Guarantee> getGuarantees() {
		return guarantees;
	}

	public void setGuarantees(List<Guarantee> guarantees) {
		this.guarantees = guarantees;
	}

	public List<ProcBankCredit> getProcBankCredits() {
        return procBankCredits;
    }

    public void setProcBankCredits(List<ProcBankCredit> procBankCredits) {
        this.procBankCredits = procBankCredits;
    }

    public List<ProcBankCreditConfirm> getProcBankCreditConfirms() {
        return procBankCreditConfirms;
    }

    public void setProcBankCreditConfirms(List<ProcBankCreditConfirm> procBankCreditConfirms) {
        this.procBankCreditConfirms = procBankCreditConfirms;
    }

    public String getAccountBankFlag() {
    	return accountBankFlag;
    }
    
    public void setAccountBankFlag(String accountBankFlag) {
    	this.accountBankFlag = accountBankFlag;
    }
    	
    @Override
    @Transient
    public String getDisplayText() {
        // TODO Auto-generated method stub
        return null;
    }


}