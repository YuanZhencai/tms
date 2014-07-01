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
 * The persistent class for the CREDIT database table.
 * 
 */
/**
 * 
 * <p>Project: tms</p>
 * <p>Description:公司银行授信关系实体</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Entity
@Table(name="CREDIT")
public class Credit extends BaseEntity{
	
	private static final long serialVersionUID = 1L;
	/** 公司*/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="COM_ID")
	private Company company;
	
	/** 银行*/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="BAN_ID")
	private Bank bank;
	
	/** 流程实例编号*/
	@Column(name="PROC_INST_ID", length=255)
	private String procInstId;
	
	/** 授信模式*/
	@Column(name="CREDIT_MODE", length=1)
	private String creditMode;
	
	/** 授信额度*/
	@Column(name="CREDIT_LINE", precision=12, scale=4)
	private Double creditLine;
	
	/** 授信额度币别*/
	@Column(name="CREDIT_LINE_CU", length=101)
	private String creditLineCu;
	
	/** 授信起始年月*/
	@Temporal( TemporalType.TIMESTAMP)
	@Column(name="CREDIT_START")
	private Date creditStart;
	
	/** 授信结束年月*/
	@Column(name="CREDIT_END")
	@Temporal(TemporalType.TIMESTAMP)
	private Date creditEnd;
	
	/** 担保额度-信用*/
	@Column(name="GUARANTEE_CD", precision=12, scale=4)
	private Double guaranteeCd;
	
	/** 担保额度-抵押*/
	@Column(name="GUARANTEE_MG", precision=12, scale=4)
	private Double guaranteeMg;
	
	/** 担保额度-质押*/
	@Column(name="GUARANTEE_QM", precision=12, scale=4)
	private Double guaranteeQm;
	
	/** 担保额度-担保*/
	@Column(name="GUARANTEE_GR", precision=12, scale=4)
	private Double guaranteeGr;
	
	/** 担保额度-其他*/
	@Column(name="GUARANTEE_OT", precision=12, scale=4)
	private Double guaranteeOt;
	
	/** 合作理由简述*/
	@Column(length=2000)
	private String reason;
	
	/** 流动资金贷款-额度*/
	@Column(name="LIQU_CRED", precision=12, scale=4)
	private Double liquCred;
	
    
    /** 流动资金贷款-利率(挂钩)*/
    @Column(name = "LIQU_CRED_RA", length = 101)
    private String liquCredRa;
    
    /** 流动资金贷款-利率(点数)*/
    @Column(name = "LIQU_CRED_PONIT", precision = 12, scale = 4, columnDefinition="default null")
    private Double liquCredPonit;
	
	/** 流动资金贷款-融资安排费比例*/
	@Column(name="LIQU_CRED_AP", precision=12, scale=4)
	private Double liquCredAp;
	
	/** 银行承兑汇票-额度*/
	@Column(name="BANK_ACPT", precision=12, scale=4)
	private Double bankAcpt;
	
	/** 银行承兑汇票-保证金比例*/
	@Column(name="BANK_ACPT_GP", precision=12, scale=4)
	private Double bankAcptGp;
	
	/** 银行承兑汇票-手续费*/
	@Column(name="BANK_ACPT_FE", precision=12, scale=4)
	private Double bankAcptFe;
	
	/** 银行承兑汇票-敞口费*/
	@Column(name="BANK_ACPT_EF", precision=12, scale=4)
	private Double bankAcptEf;
	
	/** 进口信用证-额度*/
	@Column(name="IMPORT_CREDIT", precision=12, scale=4)
	private Double importCredit;
	
	/** 进口信用证-保证金比例*/
	@Column(name="IMPORT_CREDIT_GP", precision=12, scale=4)
	private Double importCreditGp;
	
	/** 进口信用证-手续费/承兑费*/
	@Column(name="IMPORT_CREDIT_FE", precision=12, scale=4)
	private Double importCreditFe;
	
	/** 进口押汇-额度*/
	@Column(name="IMPORT_FINANCE", precision=12, scale=4)
	private Double importFinance;
	
	/*****huhan add on 6.12 start********************************************************/
	/** 进口押汇-利率(挂钩)*/
	@Column(name="IMPORT_FINANCE_LINK", length = 101)
	private String importFinanceLink;
	
	/** 进口押汇-利率(点数)*/
	@Column(name="IMPORT_FINANCE_PONIT", precision=12, scale=4)
	private Double importFinancePoint;
	
	/** 出口押汇-额度*/
	@Column(name="EXPORT_FINANCE", precision=12, scale=4)
	private Double exportFinance;
	
	/** 出口押汇-利率(挂钩)*/
	@Column(name="EXPORT_FINANCE_LINK", length = 101)
	private String exportFinanceLink;
	
	/** 出口押汇-利率(点数)*/
	@Column(name="EXPORT_FINANCE_PONIT", precision=12, scale=4)
	private Double exportFinancePoint;
	
	/** 美元流代-额度*/
	@Column(name="DOLLAR_FLOW_FINANCE", precision=12, scale=4)
	private Double dollarFlowFinance;	
	/*****huhan add on 6.12 end********************************************************/
	
	/** 美元流代-利率(挂钩)*/
	@Column(name="DOLLAR_FLOW_LINK", length = 101)
	private String dollarFlowLink;
	
	/** 美元流代-利率(点数)*/
	@Column(name="DOLLAR_FLOW_POINT", precision=12, scale=4)
	private Double dollarFlowPoint;
	
	/** 国内信用证-额度*/
	@Column(name="DOMESTIC_CRED", precision=12, scale=4)
	private Double domesticCred;
	
	/** 国内信用证-保证金比例*/
	@Column(name="DOMESTIC_CRED_GP", precision=12, scale=4)
	private Double domesticCredGp;
	
	/** 国内信用证-手续费*/
	@Column(name="DOMESTIC_CRED_FE", precision=12, scale=4)
	private Double domesticCredFe;
	
	/** 国内信用证-议付费*/
	@Column(name="DOMESTIC_CRED_DF", precision=12, scale=4)
	private Double domesticCredDf;

	/** 商票保贴-额度*/
	@Column(name="BUSS_TICKET", precision=12, scale=4)
	private Double bussTicket;
	
	/** 商票保贴-保证金比例*/
	@Column(name="BUSS_TICKET_GP", precision=12, scale=4)
	private Double bussTicketGp;
	
	/** 商票保贴-手续费*/
	@Column(name="BUSS_TICKET_FE", precision=12, scale=4)
	private Double bussTicketFe;
	
	/** 商票保贴-贴现费*/
	@Column(name="BUSS_TICKET_DC", precision=12, scale=4)
	private Double bussTicketDc;
	
	/** 远期交易-额度 add on 2013-6-28 by yan*/
    @Column(name = "FORW_TRADE", precision = 12, scale = 4)
    private Double forwTrade;
    
    /** 远期交易-费率*/
    @Column(name = "FORW_TRADE_CR", precision = 12, scale = 4)
    private Double forwTradeCr;
	
	/** 已延展标识*/
	@Column(name="EXTENDED_FLAG", length=1)
	private String extendedFlag;
	
	/** 延展截止日期*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EXTENDED_DATE")
	private Date extendedDate;
	
	/** 数据状态*/
	@Column(name="STATUS", length=1)
	private String status;

	/**其他授信品种*/
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "credit")
    private List<CreditO> creditOs = new ArrayList<CreditO>(0);
	
	/**授信调剂明细_主数据(申请方)*/
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "credit")
    private List<CreditR> creditRs = new ArrayList<CreditR>(0);
	
	/**授信调剂提供方明细_主数据(提供方)*/
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "credit")
	private List<CreditP> creditPs = new ArrayList<CreditP>(0);
	
	
    public Credit() {
    }

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
    
	public Double getBankAcpt() {
		return this.bankAcpt;
	}

	public void setBankAcpt(Double bankAcpt) {
		this.bankAcpt = bankAcpt;
	}

	public Double getBankAcptEf() {
		return this.bankAcptEf;
	}

	public void setBankAcptEf(Double bankAcptEf) {
		this.bankAcptEf = bankAcptEf;
	}

	public Double getBankAcptFe() {
		return this.bankAcptFe;
	}

	public void setBankAcptFe(Double bankAcptFe) {
		this.bankAcptFe = bankAcptFe;
	}

	public Double getBankAcptGp() {
		return this.bankAcptGp;
	}

	public void setBankAcptGp(Double bankAcptGp) {
		this.bankAcptGp = bankAcptGp;
	}

	public Double getBussTicket() {
		return this.bussTicket;
	}

	public void setBussTicket(Double bussTicket) {
		this.bussTicket = bussTicket;
	}

	public Double getBussTicketDc() {
		return this.bussTicketDc;
	}

	public void setBussTicketDc(Double bussTicketDc) {
		this.bussTicketDc = bussTicketDc;
	}

	public Double getBussTicketFe() {
		return this.bussTicketFe;
	}

	public void setBussTicketFe(Double bussTicketFe) {
		this.bussTicketFe = bussTicketFe;
	}

	public Double getBussTicketGp() {
		return this.bussTicketGp;
	}

	public void setBussTicketGp(Double bussTicketGp) {
		this.bussTicketGp = bussTicketGp;
	}


	public Double getCreditLine() {
		return this.creditLine;
	}

	public void setCreditLine(Double creditLine) {
		this.creditLine = creditLine;
	}

	public String getCreditLineCu() {
		return this.creditLineCu;
	}

	public void setCreditLineCu(String creditLineCu) {
		this.creditLineCu = creditLineCu;
	}

	public String getCreditMode() {
		return this.creditMode;
	}

	public void setCreditMode(String creditMode) {
		this.creditMode = creditMode;
	}

	public Date getCreditStart() {
		return this.creditStart;
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

	public Double getDomesticCredDf() {
		return this.domesticCredDf;
	}

	public void setDomesticCredDf(Double domesticCredDf) {
		this.domesticCredDf = domesticCredDf;
	}

	public Double getDomesticCredFe() {
		return this.domesticCredFe;
	}

	public void setDomesticCredFe(Double domesticCredFe) {
		this.domesticCredFe = domesticCredFe;
	}

	public Double getDomesticCredGp() {
		return this.domesticCredGp;
	}

	public void setDomesticCredGp(Double domesticCredGp) {
		this.domesticCredGp = domesticCredGp;
	}

	public Date getExtendedDate() {
		return this.extendedDate;
	}

	public void setExtendedDate(Date extendedDate) {
		this.extendedDate = extendedDate;
	}

	public String getExtendedFlag() {
		return this.extendedFlag;
	}

	public void setExtendedFlag(String extendedFlag) {
		this.extendedFlag = extendedFlag;
	}

	public Double getGuaranteeCd() {
		return this.guaranteeCd;
	}

	public void setGuaranteeCd(Double guaranteeCd) {
		this.guaranteeCd = guaranteeCd;
	}

	public Double getGuaranteeGr() {
		return this.guaranteeGr;
	}

	public void setGuaranteeGr(Double guaranteeGr) {
		this.guaranteeGr = guaranteeGr;
	}

	public Double getGuaranteeMg() {
		return this.guaranteeMg;
	}

	public void setGuaranteeMg(Double guaranteeMg) {
		this.guaranteeMg = guaranteeMg;
	}

	public Double getGuaranteeOt() {
		return this.guaranteeOt;
	}

	public void setGuaranteeOt(Double guaranteeOt) {
		this.guaranteeOt = guaranteeOt;
	}

	public Double getGuaranteeQm() {
		return this.guaranteeQm;
	}

	public void setGuaranteeQm(Double guaranteeQm) {
		this.guaranteeQm = guaranteeQm;
	}

	public Double getImportCredit() {
		return this.importCredit;
	}

	public void setImportCredit(Double importCredit) {
		this.importCredit = importCredit;
	}

	public Double getImportCreditFe() {
		return this.importCreditFe;
	}

	public void setImportCreditFe(Double importCreditFe) {
		this.importCreditFe = importCreditFe;
	}

	public Double getImportCreditGp() {
		return this.importCreditGp;
	}

	public void setImportCreditGp(Double importCreditGp) {
		this.importCreditGp = importCreditGp;
	}

	public Double getImportFinance() {
		return this.importFinance;
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

	public Double getImportFinancePoint() {
		return importFinancePoint;
	}

	public void setImportFinancePoint(Double importFinancePoint) {
		this.importFinancePoint = importFinancePoint;
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

	public Double getExportFinancePoint() {
		return exportFinancePoint;
	}

	public void setExportFinancePoint(Double exportFinancePoint) {
		this.exportFinancePoint = exportFinancePoint;
	}

	public Double getDollarFlowFinance() {
		return dollarFlowFinance;
	}

	public void setDollarFlowFinance(Double dollarFlowFinance) {
		this.dollarFlowFinance = dollarFlowFinance;
	}

	public Double getLiquCred() {
		return this.liquCred;
	}

	public void setLiquCred(Double liquCred) {
		this.liquCred = liquCred;
	}

	public Double getLiquCredAp() {
		return this.liquCredAp;
	}

	public void setLiquCredAp(Double liquCredAp) {
		this.liquCredAp = liquCredAp;
	}

	public String getLiquCredRa() {
		return this.liquCredRa;
	}

	public void setLiquCredRa(String liquCredRa) {
		this.liquCredRa = liquCredRa;
	}

	public String getProcInstId() {
		return this.procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Bank getBank() {
		return this.bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public List<CreditO> getCreditOs() {
		return creditOs;
	}
	public void setCreditOs(List<CreditO> creditOs) {
		this.creditOs = creditOs;
	}
	
    public List<CreditR> getCreditRs() {
		return creditRs;
	}

	public void setCreditRs(List<CreditR> creditRs) {
		this.creditRs = creditRs;
	}

	public List<CreditP> getCreditPs() {
		return creditPs;
	}

	public void setCreditPs(List<CreditP> creditPs) {
		this.creditPs = creditPs;
	}

	/** 流动资金贷款-利率(点数)*/
	public Double getLiquCredPonit() {
		return liquCredPonit;
	}
    /** 流动资金贷款-利率(点数)*/
	public void setLiquCredPonit(Double liquCredPonit) {
		this.liquCredPonit = liquCredPonit;
	}

	@Override
	@Transient
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

	public Double getForwTradeCr() {
		return forwTradeCr;
	}

	public void setForwTradeCr(Double forwTradeCr) {
		this.forwTradeCr = forwTradeCr;
	}
	
}