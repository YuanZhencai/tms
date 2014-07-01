package com.wcs.tms.model;

import java.sql.Timestamp;
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
 * <p>Project: tms</p>
 * <p>Description: 银行授信申请审批流程实体</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Entity
@Table(name = "PROC_BANK_CREDIT")
public class ProcBankCredit extends BaseEntity {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** 公司信息*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private Company company;
    /** 分支银行*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BANK_ID")
    private Bank bank;
    /** 流程实例ID*/
    @Column(name = "PROC_INST_ID")
    private String procInstId;

    /** 申请日期*/
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "APPLY_DATE")
    private Date applyDate;
    /** 授信币别*/
    @Column(name = "CREDIT_LINE_CU", length = 100)
    private String creditLineCu;
    /** 上年授信额度*/
    @Column(name = "LAST_CREDIT_LINE", precision = 12, scale = 4)
    private Double lastCreditLine;
    /** 上年担保额度-信用*/
    @Column(name = "LAST_GUARANTEE_CD", precision = 12, scale = 4)
    private Double lastGuaranteeCd;
    /** 上年担保额度-抵押*/
    @Column(name = "LAST_GUARANTEE_MG", precision = 12, scale = 4)
    private Double lastGuaranteeMg;
    /** 上年担保额度-质押*/
    @Column(name = "LAST_GUARANTEE_QM", precision = 12, scale = 4)
    private Double lastGuaranteeQm;
    /** 上年担保额度-担保*/
    @Column(name = "LAST_GUARANTEE_GR", precision = 12, scale = 4)
    private Double lastGuaranteeGr;
    /** 上年担保额度-其他*/
    @Column(name = "LAST_GUARANTEE_OT", precision = 12, scale = 4)
    private Double lastGuaranteeOt;
    /** 上年授信累计使用量*/
    @Column(name = "LAST_CREDIT_TOTAL", precision = 12, scale = 4)
    private Double lastCreditTotal;
    /** 本年申请金额*/
    @Column(name = "CREDIT_LINE", precision = 12, scale = 4)
    private Double creditLine;
    /** 审批人确认额度*/
    @Column(name = "NOTARIZE_CREDIT_LINE", precision = 12, scale = 4)
    private Double notarizeCreditLine;
    /** 本年担保额度-信用*/
    @Column(name = "GUARANTEE_CD", precision = 12, scale = 4)
    private Double guaranteeCd;
    /** 本年担保额度-抵押*/
    @Column(name = "GUARANTEE_MG", precision = 12, scale = 4)
    private Double guaranteeMg;
    /** 本年担保额度-抵押*/
    @Column(name = "GUARANTEE_QM", precision = 12, scale = 4)
    private Double guaranteeQm;
    /** 本年担保额度-担保*/
    @Column(name = "GUARANTEE_GR", precision = 12, scale = 4)
    private Double guaranteeGr;
    /** 本年担保额度-其他*/
    @Column(name = "GUARANTEE_OT", precision = 12, scale = 4)
    private Double guaranteeOt;
    /** 人民币流贷-额度*/
    @Column(name = "LIQU_CRED",  precision = 12, scale = 4,  columnDefinition="default null")
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
    
    /** 流动资金贷款-融资安排费比例*/
    @Column(name = "LIQU_CRED_AP", precision = 12, scale = 4, columnDefinition="default null")
    private Double liquCredAp;
    /** 银行承兑汇票-额度*/
    @Column(name = "BANK_ACPT", precision = 12, scale = 4, columnDefinition="default null")
    private Double bankAcpt;
    /** 银行承兑汇票-保证金比例*/
    @Column(name = "BANK_ACPT_GP", precision = 12, scale = 4, columnDefinition="default null")
    private Double bankAcptGp;
    /** 银行承兑汇票-手续费*/
    @Column(name = "BANK_ACPT_FE", precision = 12, scale = 4, columnDefinition="default null")
    private Double bankAcptFe;
    /** 银行承兑汇票-敞口费*/
    @Column(name = "BANK_ACPT_EF", precision = 12, scale = 4, columnDefinition="default null")
    private Double bankAcptEf;
    /** 进口信用证-额度*/
    @Column(name = "IMPORT_CREDIT", precision = 12, scale = 4, columnDefinition="default null")
    private Double importCredit;
    /** 进口信用证-保证金比例*/
    @Column(name = "IMPORT_CREDIT_GP", precision = 12, scale = 4, columnDefinition="default null")
    private Double importCreditGp;
    /** 进口信用证-手续费/承兑费*/
    @Column(name = "IMPORT_CREDIT_FE", precision = 12, scale = 4, columnDefinition="default null")
    private Double importCreditFe;
    /** 进口押汇-额度*/
    @Column(name = "IMPORT_FINANCE", precision = 12, scale = 4, columnDefinition="default null")
    private Double importFinance;
    /** 进口押汇-利率(挂钩)*/
    @Column(name = "IMPORT_FINANCE_LINK", length = 101)
    private String importFinanceLink;
    /** 进口押汇-利率(点数)*/
    @Column(name = "IMPORT_FINANCE_PONIT", precision = 12, scale = 4, columnDefinition="default null")
    private Double importFinancePonit;
    /** 出口押汇-额度*/
    @Column(name = "EXPORT_FINANCE", precision = 12, scale = 4, columnDefinition="default null")
    private Double exportFinance;
    /** 出口押汇-利率(挂钩)*/
    @Column(name = "EXPORT_FINANCE_LINK", length = 101)
    private String exportFinanceLink;
    /** 出口押汇-利率(点数)*/
    @Column(name = "EXPORT_FINANCE_PONIT", precision = 12, scale = 4, columnDefinition="default null")
    private Double exportFinancePonit;
    /** 美元流代-额度*/
    @Column(name = "DOLLAR_FLOW_FINANCE", precision = 12, scale = 4, columnDefinition="default null")
    private Double dollarFlowFinance;
    /** 美元流代-利率(挂钩)*/
    @Column(name = "DOLLAR_FLOW_LINK", length = 101)
    private String dollarFlowLink;
    /** 美元流代-利率(点数)*/
    @Column(name = "DOLLAR_FLOW_POINT", precision = 12, scale = 4, columnDefinition="default null")
    private Double dollarFlowPoint;
    /** 国内信用证-额度*/
    @Column(name = "DOMESTIC_CRED", precision = 12, scale = 4, columnDefinition="default null")
    private Double domesticCred;
    /** 国内信用证-保证金比例*/
    @Column(name = "DOMESTIC_CRED_GP", precision = 12, scale = 4, columnDefinition="default null")
    private Double domesticCredGp;
    /** 国内信用证-手续费*/
    @Column(name = "DOMESTIC_CRED_FE", precision = 12, scale = 4, columnDefinition="default null")
    private Double domesticCredFe;
    /** 国内信用证-议付费*/
    @Column(name = "DOMESTIC_CRED_DF", precision = 12, scale = 4, columnDefinition="default null")
    private Double domesticCredDf;
    /** 商票保贴-额度*/
    @Column(name = "BUSS_TICKET", precision = 12, scale = 4, columnDefinition="default null")
    private Double bussTicket;
    /** 商票保贴-保证金比例*/
    @Column(name = "BUSS_TICKET_GP", precision = 12, scale = 4, columnDefinition="default null")
    private Double bussTicketGp;
    /** 商票保贴-手续费*/
    @Column(name = "BUSS_TICKET_FE", precision = 12, scale = 4, columnDefinition="default null")
    private Double bussTicketFe;
    /** 商票保贴-贴现费*/
    @Column(name = "BUSS_TICKET_DC", precision = 12, scale = 4, columnDefinition="default null")
    private Double bussTicketDc;
    /** 远期交易-额度*/
    @Column(name = "FORW_TRADE", precision = 12, scale = 4, columnDefinition="default null")
    private Double forwTrade;
    /** 远期交易-费率*/
    @Column(name = "FORW_TRADE_CR", precision = 12, scale = 4, columnDefinition="default null")
    private Double forwTradeCr;
    /** 合作理由简述*/
    @Column(name = "COOPERATION_REASON", length = 600)
    private String cooperationReason;

    /** 节点备注*/
    @Transient
    private String peMemo;
    
    // Constructors

    public String getPeMemo() {
		return peMemo;
	}

	public void setPeMemo(String peMemo) {
		this.peMemo = peMemo;
	}

	/** default constructor */
    public ProcBankCredit() {
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
    
    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public void setApplyDate(Timestamp applyDate) {
        this.applyDate = applyDate;
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

    public Double getLastGuaranteeCd() {
        return this.lastGuaranteeCd;
    }

    public void setLastGuaranteeCd(Double lastGuaranteeCd) {
        this.lastGuaranteeCd = lastGuaranteeCd;
    }

    public Double getLastGuaranteeMg() {
        return this.lastGuaranteeMg;
    }

    public void setLastGuaranteeMg(Double lastGuaranteeMg) {
        this.lastGuaranteeMg = lastGuaranteeMg;
    }

    public Double getLastGuaranteeQm() {
        return this.lastGuaranteeQm;
    }

    public void setLastGuaranteeQm(Double lastGuaranteeQm) {
        this.lastGuaranteeQm = lastGuaranteeQm;
    }

    public Double getLastGuaranteeGr() {
        return this.lastGuaranteeGr;
    }

    public void setLastGuaranteeGr(Double lastGuaranteeGr) {
        this.lastGuaranteeGr = lastGuaranteeGr;
    }

    public Double getLastGuaranteeOt() {
        return this.lastGuaranteeOt;
    }

    public void setLastGuaranteeOt(Double lastGuaranteeOt) {
        this.lastGuaranteeOt = lastGuaranteeOt;
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
    /** 流动资金贷款-利率(挂钩)*/
    public String getLiquCredRa() {
        return this.liquCredRa;
    }
    /** 流动资金贷款-利率(挂钩)*/
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

    public String getCooperationReason() {
        return this.cooperationReason;
    }

    public void setCooperationReason(String cooperationReason) {
        this.cooperationReason = cooperationReason;
    }
    
    
    /** 流动资金贷款-利率(点数)*/
    public Double getLiquCredPonit() {
		return liquCredPonit;
	}
    /** 流动资金贷款-利率(点数)*/
	public void setLiquCredPonit(Double liquCredPonit) {
		this.liquCredPonit = liquCredPonit;
	}

	public Double getForwTradeCr() {
		return forwTradeCr;
	}

	public void setForwTradeCr(Double forwTradeCr) {
		this.forwTradeCr = forwTradeCr;
	}
	
	@Transient
	@Override
	public String getDisplayText() {
		// TODO Auto-generated method stub
		return null;
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