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
 * <p>Project: tms</p>
 * <p>Description: 银行授信申请审批流程确认实体</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Entity
@Table(name = "PROC_BANK_CREDIT_CONFIRM")
public class ProcBankCreditConfirm extends BaseEntity {

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
	/** 授信额度*/
	@Column(name = "CREDIT_LIMIT", precision = 12, scale = 4)
	private Double creditLimit;
	/** 授信币别*/
	@Column(name = "CREDIT_LINE_CU", length = 101)
	private String creditLineCu;
	/** 授信开始年月*/
	@Temporal(TemporalType.DATE)
	@Column(name = "CREDIT_START")
	private Date creditStart;
	/** 授信截至年月*/
	@Temporal(TemporalType.DATE)
	@Column(name = "CREDIT_END")
	private Date creditEnd;
	/** 授信担保额度-信用*/
	@Column(name = "GUARANTEE_CD", precision = 12, scale = 4)
	private Double guaranteeCd;
	/** 授信担保额度-抵押*/
	@Column(name = "GUARANTEE_MG", precision = 12, scale = 4)
	private Double guaranteeMg;
	/** 授信担保额度-质押*/
	@Column(name = "GUARANTEE_QM", precision = 12, scale = 4)
	private Double guaranteeQm;
	/** 授信担保额度-担保*/
	@Column(name = "GUARANTEE_GR", precision = 12, scale = 4)
	private Double guaranteeGr;
	/** 授信担保额度-其他*/
	@Column(name = "GUARANTEE_OT", precision = 12, scale = 4)
	private Double guaranteeOt;
	/** 流动资金贷款-额度*/
	@Column(name = "LIQU_CRED", precision = 12, scale = 4)
	private Double liquCred;
	/** 流动资金贷款-利率(挂钩)*/
	@Column(name = "LIQU_CRED_RA", length = 101)
	private String liquCredRa;

	/**上浮/下浮*/
	@Column(name = "FLOAT_FLAG", length = 50)
	private String floatFlag;
	@Column(name = "FLOAT1", precision = 12, scale = 4, columnDefinition = "default null")
	private Double float1;
	@Column(name = "FLOAT2", precision = 12, scale = 4, columnDefinition = "default null")
	private Double float2;

	/** 流动资金贷款-利率(点数)*/
	@Column(name = "LIQU_CRED_PONIT", precision = 12, scale = 4, columnDefinition = "default null")
	private Double liquCredPonit;

	/** 流动资金贷款-融资安排费比例*/
	@Column(name = "LIQU_CRED_AP", precision = 12, scale = 4)
	private Double liquCredAp;
	/** 银行承兑汇票-额度*/
	@Column(name = "BANK_ACPT", precision = 12, scale = 4)
	private Double bankAcpt;
	/** 银行承兑汇票-保证金比例*/
	@Column(name = "BANK_ACPT_GP", precision = 12, scale = 4)
	private Double bankAcptGp;
	/** 银行承兑汇票-手续费*/
	@Column(name = "BANK_ACPT_FE", precision = 12, scale = 4)
	private Double bankAcptFe;
	/** 银行承兑汇票-敞口费*/
	@Column(name = "BANK_ACPT_EF", precision = 12, scale = 4)
	private Double bankAcptEf;
	/** 进口信用证-额度*/
	@Column(name = "IMPORT_CREDIT", precision = 12, scale = 4)
	private Double importCredit;
	/** 进口信用证-保证金比例*/
	@Column(name = "IMPORT_CREDIT_GP", precision = 12, scale = 4)
	private Double importCreditGp;
	/** 进口信用证-手续费/承兑费*/
	@Column(name = "IMPORT_CREDIT_FE", precision = 12, scale = 4)
	private Double importCreditFe;
	/** 进口押汇-额度*/
	@Column(name = "IMPORT_FINANCE", precision = 12, scale = 4)
	private Double importFinance;
	/** 进口押汇-利率(挂钩)*/
	@Column(name = "IMPORT_FINANCE_LINK", length = 101)
	private String importFinanceLink;
	/** 进口押汇-利率(点数)*/
	@Column(name = "IMPORT_FINANCE_PONIT", precision = 12, scale = 4)
	private Double importFinancePonit;
	/** 出口押汇-额度*/
	@Column(name = "EXPORT_FINANCE", precision = 12, scale = 4)
	private Double exportFinance;
	/** 出口押汇-利率(挂钩)*/
	@Column(name = "EXPORT_FINANCE_LINK", length = 101)
	private String exportFinanceLink;
	/** 出口押汇-利率(点数)*/
	@Column(name = "EXPORT_FINANCE_PONIT", precision = 12, scale = 4)
	private Double exportFinancePonit;
	/** 美元流代-额度*/
	@Column(name = "DOLLAR_FLOW_FINANCE", precision = 12, scale = 4)
	private Double dollarFlowFinance;
	/** 美元流代-利率(挂钩)*/
	@Column(name = "DOLLAR_FLOW_LINK", length = 101)
	private String dollarFlowLink;
	/** 美元流代-利率(点数)*/
	@Column(name = "DOLLAR_FLOW_POINT", precision = 12, scale = 4)
	private Double dollarFlowPoint;
	/** 国内信用证-额度*/
	@Column(name = "DOMESTIC_CRED", precision = 12, scale = 4)
	private Double domesticCred;
	/** 国内信用证-保证金比例*/
	@Column(name = "DOMESTIC_CRED_GP", precision = 12, scale = 4)
	private Double domesticCredGp;
	/** 国内信用证-手续费*/
	@Column(name = "DOMESTIC_CRED_FE", precision = 12, scale = 4)
	private Double domesticCredFe;
	/** 国内信用证-议付费*/
	@Column(name = "DOMESTIC_CRED_DF", precision = 12, scale = 4)
	private Double domesticCredDf;
	/** 商票保贴-额度*/
	@Column(name = "BUSS_TICKET", precision = 12, scale = 4)
	private Double bussTicket;
	/** 商票保贴-保证金比例*/
	@Column(name = "BUSS_TICKET_GP", precision = 12, scale = 4)
	private Double bussTicketGp;
	/** 商票保贴-手续费*/
	@Column(name = "BUSS_TICKET_FE", precision = 12, scale = 4)
	private Double bussTicketFe;
	/** 商票保贴-贴现费*/
	@Column(name = "BUSS_TICKET_DC", precision = 12, scale = 4)
	private Double bussTicketDc;
	/** 远期交易-额度*/
	@Column(name = "FORW_TRADE", precision = 12, scale = 4, columnDefinition = "default null")
	private Double forwTrade;
	/** 远期交易-费率*/
	@Column(name = "FORW_TRADE_CR", precision = 12, scale = 4, columnDefinition = "default null")
	private Double forwTradeCr;
	/** creditLineCode*/
	//--12-12-12新增需求
	@Column(name = "CREDIT_LINE_CODE", length = 101)
	private String creditLineCode;

	// Constructors

	/** default constructor */
	public ProcBankCreditConfirm() {
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Bank getBank() {
		return bank;
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

	public Double getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(Double creditLimit) {
		this.creditLimit = creditLimit;
	}

	public String getCreditLineCu() {
		return creditLineCu;
	}

	public void setCreditLineCu(String creditLineCu) {
		this.creditLineCu = creditLineCu;
	}

	public Date getCreditStart() {
		return creditStart;
	}

	public void setCreditStart(Date creditStart) {
		this.creditStart = creditStart;
	}

	public Date getCreditEnd() {
		return creditEnd;
	}

	public void setCreditEnd(Date creditEnd) {
		this.creditEnd = creditEnd;
	}

	public Double getGuaranteeCd() {
		return guaranteeCd;
	}

	public void setGuaranteeCd(Double guaranteeCd) {
		this.guaranteeCd = guaranteeCd;
	}

	public Double getGuaranteeMg() {
		return guaranteeMg;
	}

	public void setGuaranteeMg(Double guaranteeMg) {
		this.guaranteeMg = guaranteeMg;
	}

	public Double getGuaranteeQm() {
		return guaranteeQm;
	}

	public void setGuaranteeQm(Double guaranteeQm) {
		this.guaranteeQm = guaranteeQm;
	}

	public Double getGuaranteeGr() {
		return guaranteeGr;
	}

	public void setGuaranteeGr(Double guaranteeGr) {
		this.guaranteeGr = guaranteeGr;
	}

	public Double getGuaranteeOt() {
		return guaranteeOt;
	}

	public void setGuaranteeOt(Double guaranteeOt) {
		this.guaranteeOt = guaranteeOt;
	}

	public Double getLiquCred() {
		return liquCred;
	}

	public void setLiquCred(Double liquCred) {
		this.liquCred = liquCred;
	}

	public String getLiquCredRa() {
		return liquCredRa;
	}

	public void setLiquCredRa(String liquCredRa) {
		this.liquCredRa = liquCredRa;
	}

	public Double getLiquCredAp() {
		return liquCredAp;
	}

	public void setLiquCredAp(Double liquCredAp) {
		this.liquCredAp = liquCredAp;
	}

	public Double getBankAcpt() {
		return bankAcpt;
	}

	public void setBankAcpt(Double bankAcpt) {
		this.bankAcpt = bankAcpt;
	}

	public Double getBankAcptGp() {
		return bankAcptGp;
	}

	public void setBankAcptGp(Double bankAcptGp) {
		this.bankAcptGp = bankAcptGp;
	}

	public Double getBankAcptFe() {
		return bankAcptFe;
	}

	public void setBankAcptFe(Double bankAcptFe) {
		this.bankAcptFe = bankAcptFe;
	}

	public Double getBankAcptEf() {
		return bankAcptEf;
	}

	public void setBankAcptEf(Double bankAcptEf) {
		this.bankAcptEf = bankAcptEf;
	}

	public Double getImportCredit() {
		return importCredit;
	}

	public void setImportCredit(Double importCredit) {
		this.importCredit = importCredit;
	}

	public Double getImportCreditGp() {
		return importCreditGp;
	}

	public void setImportCreditGp(Double importCreditGp) {
		this.importCreditGp = importCreditGp;
	}

	public Double getImportCreditFe() {
		return importCreditFe;
	}

	public void setImportCreditFe(Double importCreditFe) {
		this.importCreditFe = importCreditFe;
	}

	public Double getImportFinance() {
		return importFinance;
	}

	public void setImportFinance(Double importFinance) {
		this.importFinance = importFinance;
	}

	public String getImportFinanceLink() {
		return importFinanceLink;
	}

	public void setImportFinanceLink(String importFinanceLink) {
		this.importFinanceLink = importFinanceLink;
	}

	public Double getImportFinancePonit() {
		return importFinancePonit;
	}

	public void setImportFinancePonit(Double importFinancePonit) {
		this.importFinancePonit = importFinancePonit;
	}

	public Double getExportFinance() {
		return exportFinance;
	}

	public void setExportFinance(Double exportFinance) {
		this.exportFinance = exportFinance;
	}

	public String getExportFinanceLink() {
		return exportFinanceLink;
	}

	public void setExportFinanceLink(String exportFinanceLink) {
		this.exportFinanceLink = exportFinanceLink;
	}

	public Double getExportFinancePonit() {
		return exportFinancePonit;
	}

	public void setExportFinancePonit(Double exportFinancePonit) {
		this.exportFinancePonit = exportFinancePonit;
	}

	public Double getDollarFlowFinance() {
		return dollarFlowFinance;
	}

	public void setDollarFlowFinance(Double dollarFlowFinance) {
		this.dollarFlowFinance = dollarFlowFinance;
	}

	public String getDollarFlowLink() {
		return dollarFlowLink;
	}

	public void setDollarFlowLink(String dollarFlowLink) {
		this.dollarFlowLink = dollarFlowLink;
	}

	public Double getDollarFlowPoint() {
		return dollarFlowPoint;
	}

	public void setDollarFlowPoint(Double dollarFlowPoint) {
		this.dollarFlowPoint = dollarFlowPoint;
	}

	public Double getDomesticCred() {
		return domesticCred;
	}

	public void setDomesticCred(Double domesticCred) {
		this.domesticCred = domesticCred;
	}

	public Double getDomesticCredGp() {
		return domesticCredGp;
	}

	public void setDomesticCredGp(Double domesticCredGp) {
		this.domesticCredGp = domesticCredGp;
	}

	public Double getDomesticCredFe() {
		return domesticCredFe;
	}

	public void setDomesticCredFe(Double domesticCredFe) {
		this.domesticCredFe = domesticCredFe;
	}

	public Double getDomesticCredDf() {
		return domesticCredDf;
	}

	public void setDomesticCredDf(Double domesticCredDf) {
		this.domesticCredDf = domesticCredDf;
	}

	public Double getBussTicket() {
		return bussTicket;
	}

	public void setBussTicket(Double bussTicket) {
		this.bussTicket = bussTicket;
	}

	public Double getBussTicketGp() {
		return bussTicketGp;
	}

	public void setBussTicketGp(Double bussTicketGp) {
		this.bussTicketGp = bussTicketGp;
	}

	public Double getBussTicketFe() {
		return bussTicketFe;
	}

	public void setBussTicketFe(Double bussTicketFe) {
		this.bussTicketFe = bussTicketFe;
	}

	public Double getBussTicketDc() {
		return bussTicketDc;
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

	@Transient
	@Override
	public String getDisplayText() {
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

	public String getCreditLineCode() {
		return creditLineCode;
	}

	public void setCreditLineCode(String creditLineCode) {
		this.creditLineCode = creditLineCode;
	}

}