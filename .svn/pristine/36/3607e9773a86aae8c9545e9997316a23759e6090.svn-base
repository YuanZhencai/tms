package com.wcs.tms.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 * <p>Description: 银行授信集团申请</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Entity
@Table(name = "PROC_BANK_CREDIT_BLOC")
public class ProcBankCreditBloc extends BaseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // Fields

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private Company company;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BANK_ID")
    private Bank bank;
    /** 申请日期*/
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "APPLY_DATE")
    private Date applyDate;
    @Column(name = "PROC_INST_ID")
    private String procInstId;
    @Column(name = "CREDIT_LINE_CU", length = 100)
    private String creditLineCu;
    @Column(name = "LAST_CREDIT_LINE", precision = 12, scale = 4)
    private Double lastCreditLine;
    @Column(name = "LAST_CREDIT_TOTAL", precision = 12, scale = 4)
    private Double lastCreditTotal;
    @Column(name = "CREDIT_LINE", precision = 12, scale = 4)
    private Double creditLine;
    @Column(name = "GUARANTEE_CD", precision = 12, scale = 4)
    private Double guaranteeCd;
    @Column(name = "GUARANTEE_MG", precision = 12, scale = 4)
    private Double guaranteeMg;
    @Column(name = "GUARANTEE_QM", precision = 12, scale = 4)
    private Double guaranteeQm;
    @Column(name = "GUARANTEE_GR", precision = 12, scale = 4)
    private Double guaranteeGr;
    @Column(name = "GUARANTEE_OT", precision = 12, scale = 4)
    private Double guaranteeOt;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "procBankCreditBloc")
    private List<ProcBankCreditBlocConfirm> procBankCreditBlocConfirms = new ArrayList<ProcBankCreditBlocConfirm>(0);
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "procBankCreditBloc")
    private List<ProcBankCreditBlocCompany> procBankCreditBlocCompanies = new ArrayList<ProcBankCreditBlocCompany>(0);

    
    /** 审批备注 */
    @Transient
    private String peMemo;
    // Constructors

    /** default constructor */
    public ProcBankCreditBloc() {
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
        return this.procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getCreditLineCu() {
        return this.creditLineCu;
    }

    public void setCreditLineCu(String creditLineCu) {
        this.creditLineCu = creditLineCu;
    }

    public Double getLastCreditLine() {
        return this.lastCreditLine;
    }

    public void setLastCreditLine(Double lastCreditLine) {
        this.lastCreditLine = lastCreditLine;
    }

    public Double getLastCreditTotal() {
        return this.lastCreditTotal;
    }

    public void setLastCreditTotal(Double lastCreditTotal) {
        this.lastCreditTotal = lastCreditTotal;
    }

    public Double getCreditLine() {
        return this.creditLine;
    }

    public void setCreditLine(Double creditLine) {
        this.creditLine = creditLine;
    }

    public Double getGuaranteeCd() {
        return this.guaranteeCd;
    }

    public void setGuaranteeCd(Double guaranteeCd) {
        this.guaranteeCd = guaranteeCd;
    }

    public Double getGuaranteeMg() {
        return this.guaranteeMg;
    }

    public void setGuaranteeMg(Double guaranteeMg) {
        this.guaranteeMg = guaranteeMg;
    }

    public Double getGuaranteeQm() {
        return this.guaranteeQm;
    }

    public void setGuaranteeQm(Double guaranteeQm) {
        this.guaranteeQm = guaranteeQm;
    }

    public Double getGuaranteeGr() {
        return this.guaranteeGr;
    }

    public void setGuaranteeGr(Double guaranteeGr) {
        this.guaranteeGr = guaranteeGr;
    }

    public Double getGuaranteeOt() {
        return this.guaranteeOt;
    }

    public void setGuaranteeOt(Double guaranteeOt) {
        this.guaranteeOt = guaranteeOt;
    }

    public List<ProcBankCreditBlocConfirm> getProcBankCreditBlocConfirms() {
        return procBankCreditBlocConfirms;
    }

    public void setProcBankCreditBlocConfirms(List<ProcBankCreditBlocConfirm> procBankCreditBlocConfirms) {
        this.procBankCreditBlocConfirms = procBankCreditBlocConfirms;
    }

    public List<ProcBankCreditBlocCompany> getProcBankCreditBlocCompanies() {
        return procBankCreditBlocCompanies;
    }

    public void setProcBankCreditBlocCompanies(List<ProcBankCreditBlocCompany> procBankCreditBlocCompanies) {
        this.procBankCreditBlocCompanies = procBankCreditBlocCompanies;
    }


    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
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

}