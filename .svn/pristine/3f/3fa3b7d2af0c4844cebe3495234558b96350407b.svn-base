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


/**
 * 
 * <p>Project: tms</p>
 * <p>Description:流程明细_银行授信额度调剂申请提供方 实体</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Entity
@Table(name = "PROC_RPT_ADJUST_PROV")
public class ProcRptAdjustProv extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROC_ADJUST_ID")
	private ProcBankCreditAdjust procBankCreditAdjust;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private Company company;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BANK_ID")
    private Bank bank;
	
	@Column(name = "CREDIT_REDUCE", precision = 12, scale = 4)
    private Double creditReduce;
	
	@Column(name = "CREDIT_TOTAL", precision = 12, scale = 4)
    private Double creditTotal;
	
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
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procRptAdjustProv")
    private List<ProcRptAdjustProvO> procRptAdjustProvOs = new ArrayList<ProcRptAdjustProvO>(0);

    // Constructors

    /** default constructor */
    public ProcRptAdjustProv() {
    	company = new Company();
    	bank = new Bank();
    	procBankCreditAdjust = new ProcBankCreditAdjust();
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
    public ProcBankCreditAdjust getProcBankCreditAdjust() {
        return this.procBankCreditAdjust;
    }

    public void setProcBankCreditAdjust(ProcBankCreditAdjust procBankCreditAdjust) {
        this.procBankCreditAdjust = procBankCreditAdjust;
    }

    public Bank getBank() {
        return this.bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public Double getCreditReduce() {
        return this.creditReduce;
    }

    public void setCreditReduce(Double creditReduce) {
        this.creditReduce = creditReduce;
    }

    public Double getCreditTotal() {
        return this.creditTotal;
    }

    public void setCreditTotal(Double creditTotal) {
        this.creditTotal = creditTotal;
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

    public List<ProcRptAdjustProvO> getProcRptAdjustProvOs() {
        return this.procRptAdjustProvOs;
    }

    public void setProcRptAdjustProvOs(List<ProcRptAdjustProvO> procRptAdjustProvOs) {
        this.procRptAdjustProvOs = procRptAdjustProvOs;
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