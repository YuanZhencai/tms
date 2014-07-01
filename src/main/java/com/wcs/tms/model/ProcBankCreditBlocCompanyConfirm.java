package com.wcs.tms.model;

import java.util.ArrayList;
import java.util.List;

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
 * <p>Description: 银行授信集团申请成员公司确认</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Entity
@Table(name = "PROC_BANK_CREDIT_BLOC_COMPANY_CONFIRM")
public class ProcBankCreditBlocCompanyConfirm extends BaseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // Fields
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private Company company;
    /** 银行授信申请集团确认单*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BLOC_CREDIT_CONFIRM_ID")
    private ProcBankCreditBlocConfirm procBankCreditBlocConfirm;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BANK_ID")
    private Bank bank;
    @Column(name = "PROC_INST_ID")
    private String procInstId;
    @Column(name = "STATUS", length = 1)
    private String status;
    @Column(name = "CREDIT_LINE", precision = 12, scale = 4)
    private Double creditLine;
    @Column(name = "NOTARIZE_CREDIT_LINE", precision = 12, scale = 4)
    private Double notarizeCreditLine;
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
    @Column(name = "LIQU_CRED", precision = 12, scale = 4)
    private Double liquCred;
    
    /** 流动资金贷款-利率(挂钩)*/
    @Column(name = "LIQU_CRED_RA", length = 101)
    private String liquCredRa;
    /**上浮/下浮*/
    @Column(name = "FLOAT_FLAG", length = 50)
    private String floatFlag;
    @Column(name = "FLOAT1",  precision = 12, scale = 4,  columnDefinition="default null")
    private Double float1;
    @Column(name = "FLOAT2",  precision = 12, scale = 4,  columnDefinition="default null")
    private Double float2;
    
    /** 流动资金贷款-利率(点数)*/
    @Column(name = "LIQU_CRED_PONIT", precision = 12, scale = 4, columnDefinition="default null")
    private Double liquCredPonit;
    
    @Column(name = "LIQU_CRED_AP", precision = 12, scale = 4)
    private Double liquCredAp;
    @Column(name = "BANK_ACPT", precision = 12, scale = 4)
    private Double bankAcpt;
    @Column(name = "BANK_ACPT_GP", precision = 12, scale = 4)
    private Double bankAcptGp;
    @Column(name = "BANK_ACPT_FE", precision = 12, scale = 4)
    private Double bankAcptFe;
    @Column(name = "BANK_ACPT_EF", precision = 12, scale = 4)
    private Double bankAcptEf;
    @Column(name = "IMPORT_CREDIT", precision = 12, scale = 4)
    private Double importCredit;
    @Column(name = "IMPORT_CREDIT_GP", precision = 12, scale = 4)
    private Double importCreditGp;
    @Column(name = "IMPORT_CREDIT_FE", precision = 12, scale = 4)
    private Double importCreditFe;
    @Column(name = "IMPORT_FINANCE", precision = 12, scale = 4)
    private Double importFinance;
    @Column(name = "IMPORT_FINANCE_LINK", length = 101)
    private String importFinanceLink;
    @Column(name = "IMPORT_FINANCE_PONIT", precision = 12, scale = 4)
    private Double importFinancePonit;
    @Column(name = "EXPORT_FINANCE", precision = 12, scale = 4)
    private Double exportFinance;
    @Column(name = "EXPORT_FINANCE_LINK", length = 101)
    private String exportFinanceLink;
    @Column(name = "EXPORT_FINANCE_PONIT", precision = 12, scale = 4)
    private Double exportFinancePonit;
    @Column(name = "DOLLAR_FLOW_FINANCE", precision = 12, scale = 4)
    private Double dollarFlowFinance;
    @Column(name = "DOLLAR_FLOW_LINK", length = 101)
    private String dollarFlowLink;
    @Column(name = "DOLLAR_FLOW_POINT", precision = 12, scale = 4)
    private Double dollarFlowPoint;
    @Column(name = "DOMESTIC_CRED", precision = 12, scale = 4)
    private Double domesticCred;
    @Column(name = "DOMESTIC_CRED_GP", precision = 12, scale = 4)
    private Double domesticCredGp;
    @Column(name = "DOMESTIC_CRED_FE", precision = 12, scale = 4)
    private Double domesticCredFe;
    @Column(name = "DOMESTIC_CRED_DF", precision = 12, scale = 4)
    private Double domesticCredDf;
    @Column(name = "BUSS_TICKET", precision = 12, scale = 4)
    private Double bussTicket;
    @Column(name = "BUSS_TICKET_GP", precision = 12, scale = 4)
    private Double bussTicketGp;
    @Column(name = "BUSS_TICKET_FE", precision = 12, scale = 4)
    private Double bussTicketFe;
    @Column(name = "BUSS_TICKET_DC", precision = 12, scale = 4)
    private Double bussTicketDc;
    @Column(name = "FORW_TRADE", precision = 12, scale = 4)
    private Double forwTrade;
    @Column(name = "FORW_TRADE_CR", precision = 12, scale = 4)
    private Double forwTradeCr;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "procBankCreditBlocCompanyConfirm")
    private List<ProcRptBankCreditBlocConfirm> procRptBankCreditBlocConfirms = new ArrayList<ProcRptBankCreditBlocConfirm>(0);

    /** 合作理由简述*/
    @Column(name = "COOPERATION_REASON", length=600)
    private String cooperationReason;
    // Constructors

    /** default constructor */
    public ProcBankCreditBlocCompanyConfirm() {
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
    public String getCooperationReason() {
		return cooperationReason;
	}

	public void setCooperationReason(String cooperationReason) {
		this.cooperationReason = cooperationReason;
	}

	public ProcBankCreditBlocConfirm getProcBankCreditBlocConfirm() {
        return this.procBankCreditBlocConfirm;
    }

    public void setProcBankCreditBlocConfirm(ProcBankCreditBlocConfirm procBankCreditBlocConfirm) {
        this.procBankCreditBlocConfirm = procBankCreditBlocConfirm;
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

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getCreditLine() {
        return this.creditLine;
    }

    public void setCreditLine(Double creditLine) {
        this.creditLine = creditLine;
    }

    public Double getNotarizeCreditLine() {
        return this.notarizeCreditLine;
    }

    public void setNotarizeCreditLine(Double notarizeCreditLine) {
        this.notarizeCreditLine = notarizeCreditLine;
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

    public Double getLiquCred() {
        return this.liquCred;
    }

    public void setLiquCred(Double liquCred) {
        this.liquCred = liquCred;
    }

    public String getLiquCredRa() {
        return this.liquCredRa;
    }

    public void setLiquCredRa(String liquCredRa) {
        this.liquCredRa = liquCredRa;
    }

    public Double getLiquCredAp() {
        return this.liquCredAp;
    }

    public void setLiquCredAp(Double liquCredAp) {
        this.liquCredAp = liquCredAp;
    }

    public Double getBankAcpt() {
        return this.bankAcpt;
    }

    public void setBankAcpt(Double bankAcpt) {
        this.bankAcpt = bankAcpt;
    }

    public Double getBankAcptGp() {
        return this.bankAcptGp;
    }

    public void setBankAcptGp(Double bankAcptGp) {
        this.bankAcptGp = bankAcptGp;
    }

    public Double getBankAcptFe() {
        return this.bankAcptFe;
    }

    public void setBankAcptFe(Double bankAcptFe) {
        this.bankAcptFe = bankAcptFe;
    }

    public Double getBankAcptEf() {
        return this.bankAcptEf;
    }

    public void setBankAcptEf(Double bankAcptEf) {
        this.bankAcptEf = bankAcptEf;
    }

    public Double getImportCredit() {
        return this.importCredit;
    }

    public void setImportCredit(Double importCredit) {
        this.importCredit = importCredit;
    }

    public Double getImportCreditGp() {
        return this.importCreditGp;
    }

    public void setImportCreditGp(Double importCreditGp) {
        this.importCreditGp = importCreditGp;
    }

    public Double getImportCreditFe() {
        return this.importCreditFe;
    }

    public void setImportCreditFe(Double importCreditFe) {
        this.importCreditFe = importCreditFe;
    }

    public Double getImportFinance() {
        return this.importFinance;
    }

    public void setImportFinance(Double importFinance) {
        this.importFinance = importFinance;
    }

    public String getImportFinanceLink() {
        return this.importFinanceLink;
    }

    public void setImportFinanceLink(String importFinanceLink) {
        this.importFinanceLink = importFinanceLink;
    }

    public Double getImportFinancePonit() {
        return this.importFinancePonit;
    }

    public void setImportFinancePonit(Double importFinancePonit) {
        this.importFinancePonit = importFinancePonit;
    }

    public Double getExportFinance() {
        return this.exportFinance;
    }

    public void setExportFinance(Double exportFinance) {
        this.exportFinance = exportFinance;
    }

    public String getExportFinanceLink() {
        return this.exportFinanceLink;
    }

    public void setExportFinanceLink(String exportFinanceLink) {
        this.exportFinanceLink = exportFinanceLink;
    }

    public Double getExportFinancePonit() {
        return this.exportFinancePonit;
    }

    public void setExportFinancePonit(Double exportFinancePonit) {
        this.exportFinancePonit = exportFinancePonit;
    }

    public Double getDollarFlowFinance() {
        return this.dollarFlowFinance;
    }

    public void setDollarFlowFinance(Double dollarFlowFinance) {
        this.dollarFlowFinance = dollarFlowFinance;
    }

    public String getDollarFlowLink() {
        return this.dollarFlowLink;
    }

    public void setDollarFlowLink(String dollarFlowLink) {
        this.dollarFlowLink = dollarFlowLink;
    }

    public Double getDollarFlowPoint() {
        return this.dollarFlowPoint;
    }

    public void setDollarFlowPoint(Double dollarFlowPoint) {
        this.dollarFlowPoint = dollarFlowPoint;
    }

    public Double getDomesticCred() {
        return this.domesticCred;
    }

    public void setDomesticCred(Double domesticCred) {
        this.domesticCred = domesticCred;
    }

    public Double getDomesticCredGp() {
        return this.domesticCredGp;
    }

    public void setDomesticCredGp(Double domesticCredGp) {
        this.domesticCredGp = domesticCredGp;
    }

    public Double getDomesticCredFe() {
        return this.domesticCredFe;
    }

    public void setDomesticCredFe(Double domesticCredFe) {
        this.domesticCredFe = domesticCredFe;
    }

    public Double getDomesticCredDf() {
        return this.domesticCredDf;
    }

    public void setDomesticCredDf(Double domesticCredDf) {
        this.domesticCredDf = domesticCredDf;
    }

    public Double getBussTicket() {
        return this.bussTicket;
    }

    public void setBussTicket(Double bussTicket) {
        this.bussTicket = bussTicket;
    }

    public Double getBussTicketGp() {
        return this.bussTicketGp;
    }

    public void setBussTicketGp(Double bussTicketGp) {
        this.bussTicketGp = bussTicketGp;
    }

    public Double getBussTicketFe() {
        return this.bussTicketFe;
    }

    public void setBussTicketFe(Double bussTicketFe) {
        this.bussTicketFe = bussTicketFe;
    }

    public Double getBussTicketDc() {
        return this.bussTicketDc;
    }

    public void setBussTicketDc(Double bussTicketDc) {
        this.bussTicketDc = bussTicketDc;
    }
    

    /** 流动资金贷款-利率(点数)*/
    public Double getLiquCredPonit() {
		return liquCredPonit;
	}
    /** 流动资金贷款-利率(点数)*/
	public void setLiquCredPonit(Double liquCredPonit) {
		this.liquCredPonit = liquCredPonit;
	}

    public List<ProcRptBankCreditBlocConfirm> getProcRptBankCreditBlocConfirms() {
        return procRptBankCreditBlocConfirms;
    }

    public void setProcRptBankCreditBlocConfirms(List<ProcRptBankCreditBlocConfirm> procRptBankCreditBlocConfirms) {
        this.procRptBankCreditBlocConfirms = procRptBankCreditBlocConfirms;
    }

    @Transient
    @Override
    public String getDisplayText() {
        // TODO Auto-generated method stub
        return null;
    }

	public Double getForwTradeCr() {
		return forwTradeCr;
	}

	public void setForwTradeCr(Double forwTradeCr) {
		this.forwTradeCr = forwTradeCr;
	}

	public Double getForwTrade() {
		return forwTrade;
	}

	public void setForwTrade(Double forwTrade) {
		this.forwTrade = forwTrade;
	}

	public String getFloatFlag() {
		return floatFlag;
	}

	public void setFloatFlag(String floatFlag) {
		this.floatFlag = floatFlag;
	}

	public Double getFloat1() {
		return float1;
	}

	public void setFloat1(Double float1) {
		this.float1 = float1;
	}

	public Double getFloat2() {
		return float2;
	}

	public void setFloat2(Double float2) {
		this.float2 = float2;
	}

}