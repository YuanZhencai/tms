package com.wcs.tms.model;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;
import com.wcs.tms.service.process.bankcreditadjust.entity.Provider;


/**
 * 
 * <p>Project: tms</p>
 * <p>Description:流程_银行授信额度调剂申请审批 实体</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Entity
@Table(name = "PROC_BANK_CREDIT_ADJUST")
public class ProcBankCreditAdjust extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private Company company;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BANK_ID")
    private Bank bank;
	
	@Column(name = "PROC_INST_ID", length = 255)
	private String procInstId;
	
	@Column(name = "CREDIT_ORI", precision = 12, scale = 4)
    private Double creditOri;
	
	@Column(name = "CREDIT_ADD", precision = 12, scale = 4)
    private Double creditAdd;
	
	@Column(name = "CREDIT_CU", length = 101)
    private String creditCu;
	
	@Column(name = "USE",length = 600)
    private String use;
	
	@Column(name = "LIQU_CRED", precision = 12, scale = 4)
    private Double liquCred;
	
	@Column(name = "BANK_ACPT", precision = 12, scale = 4)
    private Double bankAcpt;
	
	@Column(name = "IMPORT_CREDIT", precision = 12, scale = 4)
    private Double importCredit;
	
	@Column(name = "IMPORT_FINANCE", precision = 12, scale = 4)
    private Double importFinance;
	
	@Column(name = "OUTPORT_FINANCE", precision = 12, scale = 4)
    private Double outportFinance;
	
	@Column(name = "DOLLAR_FLOW", precision = 12, scale = 4)
    private Double dollarFlow;
	
	@Column(name = "DOMESTIC_CRED", precision = 12, scale = 4)
    private Double domesticCred;
	
	@Column(name = "BUSS_TICKET", precision = 12, scale = 4)
    private Double bussTicket;
	
	/** 远期交易-额度 add on 2013-6-28 by yan*/
    @Column(name = "FORW_TRADE", precision = 12, scale = 4)
    private Double forwTrade;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procBankCreditAdjust")
    private List<ProcRptAdjustO> procRptAdjustOs = new ArrayList<ProcRptAdjustO>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procBankCreditAdjust")
    private List<ProcRptAdjustProv> procRptAdjustProvs = new ArrayList<ProcRptAdjustProv>(0);
	
	
	/** 保存PE的备注*/
    @Transient
    private String peMemo;
    
    //授信提供方列表
    @Transient
    private List<Provider> providers = new ArrayList<Provider>();

    // Constructors

    /** default constructor */
    public ProcBankCreditAdjust() {
    	company = new Company();
    	bank = new Bank();
    }

    public String getPeMemo() {
		return peMemo;
	}

	public void setPeMemo(String peMemo) {
		this.peMemo = peMemo;
	}
	
	public List<Provider> getProviders() {
		return providers;
	}

	public void setProviders(List<Provider> providers) {
		this.providers = providers;
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
    
    public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public Double getCreditOri() {
        return this.creditOri;
    }

    public void setCreditOri(Double creditOri) {
        this.creditOri = creditOri;
    }

    public Double getCreditAdd() {
        return this.creditAdd;
    }

    public void setCreditAdd(Double creditAdd) {
        this.creditAdd = creditAdd;
    }

    public String getCreditCu() {
        return this.creditCu;
    }

    public void setCreditCu(String creditCu) {
        this.creditCu = creditCu;
    }

    public String getUse() {
        return this.use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public Double getLiquCred() {
        return this.liquCred;
    }

    public void setLiquCred(Double liquCred) {
        this.liquCred = liquCred;
    }

    public Double getBankAcpt() {
        return this.bankAcpt;
    }

    public void setBankAcpt(Double bankAcpt) {
        this.bankAcpt = bankAcpt;
    }

    public Double getImportCredit() {
        return this.importCredit;
    }

    public void setImportCredit(Double importCredit) {
        this.importCredit = importCredit;
    }

    public Double getImportFinance() {
        return this.importFinance;
    }

    public void setImportFinance(Double importFinance) {
        this.importFinance = importFinance;
    }

    public Double getOutportFinance() {
        return this.outportFinance;
    }

    public void setOutportFinance(Double outportFinance) {
        this.outportFinance = outportFinance;
    }

    public Double getDollarFlow() {
        return this.dollarFlow;
    }

    public void setDollarFlow(Double dollarFlow) {
        this.dollarFlow = dollarFlow;
    }

    public Double getDomesticCred() {
        return this.domesticCred;
    }

    public void setDomesticCred(Double domesticCred) {
        this.domesticCred = domesticCred;
    }

    public Double getBussTicket() {
        return this.bussTicket;
    }

    public void setBussTicket(Double bussTicket) {
        this.bussTicket = bussTicket;
    }

    public List<ProcRptAdjustO> getProcRptAdjustOs() {
        return this.procRptAdjustOs;
    }

    public void setProcRptAdjustOs(List<ProcRptAdjustO> procRptAdjustOs) {
        this.procRptAdjustOs = procRptAdjustOs;
    }

    public List<ProcRptAdjustProv> getProcRptAdjustProvs() {
        return this.procRptAdjustProvs;
    }

    public void setProcRptAdjustProvs(List<ProcRptAdjustProv> procRptAdjustProvs) {
        this.procRptAdjustProvs = procRptAdjustProvs;
    }
    
    
    @Override
	@Transient
	public String getDisplayText() {
		return null;
	}

	public Double getForwTrade() {
		return forwTrade;
	}

	public void setForwTrade(Double forwTrade) {
		this.forwTrade = forwTrade;
	}

}