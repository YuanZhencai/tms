package com.wcs.tms.view.process.common.entity;

import java.io.Serializable;
import java.util.Date;

import com.wcs.common.controller.helper.IdModel;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.DebtContract;
import com.wcs.tms.model.ProcDebtBorrow;
import com.wcs.tms.model.ShareHolder;

public class DebtRequestVo extends IdModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	private ProcDebtBorrow debtBorrow;
	private DebtContract contract;
	

	private Company company;

	/*** 资金提供股东 *******/
	private ShareHolder shareHolder;

	private String processNo;
	
	private String procInstId;

	private String companyName;

	private String companyEn;

	private Double investBalance;

	private String investBalanceCu;

	private Double bebtInPlace;

	private String bebtInPlaceCu;

	private Double shBorrow;

	private String shBorrowCu;

	private Double shBorrowLi;

	private Double shBorrowRa;

	private Double fornBebt;

	private String fornBebtCu;

	private Double fornBebtLi;

	private Double fornBebtRa;

	/****** 可用外债额度 改为 可用投注差 ***************/
	private Double availbBebt;

	private String availbBebtCu;

	private String thisShBorrowSe;

	private Double thisShBorrow;

	private String thisShBorrowCu;

	private Double thisShBorrowLi;

	/****** 本次股东借款起始年月 ***************/
	private Date thisShBorrowLis;

	/****** 本次股东借款结束年月 ***************/
	private Date thisShBorrowLie;

	private String thisShBorrowRa;

	/** 外债提供方 (去除掉) start ****/
	private Double bebtProvider;

	private String bebtProviderCu;

	private Double bebtProviderLi;

	private Double bebtProviderRa;
	/** 外债提供方 (去除掉) end ****/

	private String use;

	private String afceFlag = "N";

	private Double afceSign;

	private String afceSignCu;

	/** AFCE折算汇率 ****/
	private Double afceExcRate;

	/** AFCE签批情况(折算值) ****/
	private Double afceSignExc;

	/** AFCE已付款金额 ****/
	private Double afcePaid;

	/** AFCE已付款金额币别 ****/
	private String afcePaidCu;

	private Double corpAudit;

	private String corpAuditCu;

	private Double corpAuditLi;

	/****** 集团项目审批期限起始年月 ***************/
	private Date corpAuditLis;

	/****** 集团项目审批期限结束年月 ***************/
	private Date corpAuditLie;

	private String corpAuditRa;

	/** 备注(小股东是否按比例提供资金) ****/
	private String Memo;

	/** 还款方式及来源 ****/
	private String payBackMemo;

	/** 资金提供方类型 ****/
	private String providerType;

	/** 资金提供公司KEY ****/
	private String providerKey;

	/** 折算汇率 ****/
	private Double exchangeRate;

	/** 保存PE的备注 */
	private String peMemo;
	// Constructors

	/** 贷款时间 */
	private Date borrowDate;

	/** 还款时间 */
	private Date payBackDate;

	/** 资金提供方名称 ****/
	private String providerName;

	/** 2014-07-04 add by liushengbin 新加字段 **/

	/*** 外债期限类型 **/
	private String bebtDeadlineType;
	/*** 申请类型 **/
	private String applyType;
	/*** 展期原外债合同主数据ID 关联 ‘外债合同_主数据(DEBT_CONTRACT)’表 **/
	private Long debtContractId;
	/** 当前节点 **/
	private String currentNode;
	/** 流程状态 **/
	private String processStatus;
	
	/** 外债合同_主数据DEBT_CONTRACT**/
    // 外债合同 = 合同编号+出资方+开始-结束日期+利率
    private String debtContract; 
    private String oldDebtContract; 
    // 外债合同金额
    private String debtContractFunds;
    // 未请款金额
    private String noAppliedFunds;
    // 请款金额
    private String applyFunds;
    // 到账金额
    private String receivedFunds;
    
    // 申请金额
    private String requestMoney;
    
    // 币别
    private String currency;

	public DebtRequestVo() {
	}

	public DebtRequestVo(ProcDebtBorrow debtBorrow) {
		this.debtBorrow = debtBorrow;
		
		this.company = debtBorrow.getCompany();

		/*** 资金提供股东 *******/
		this.shareHolder = debtBorrow.getShareHolder();

		this.procInstId = debtBorrow.getProcInstId();

		this.companyName = debtBorrow.getCompanyName();

		this.companyEn = debtBorrow.getCompanyEn();

		this.investBalance = debtBorrow.getInvestBalance();

		this.investBalanceCu = debtBorrow.getInvestBalanceCu();

		this.bebtInPlace = debtBorrow.getBebtInPlace();

		this.bebtInPlaceCu = debtBorrow.getBebtInPlaceCu();

		this.shBorrow = debtBorrow.getShBorrow();

		this.shBorrowCu = debtBorrow.getShBorrowCu();

		this.shBorrowLi = debtBorrow.getShBorrowLi();

		this.shBorrowRa = debtBorrow.getShBorrowRa();

		this.fornBebt = debtBorrow.getFornBebt();

		this.fornBebtCu = debtBorrow.getFornBebtCu();

		this.fornBebtLi = debtBorrow.getFornBebtLi();

		this.fornBebtRa = debtBorrow.getFornBebtRa();

		/****** 可用外债额度 改为 可用投注差 ***************/
		this.availbBebt = debtBorrow.getAvailbBebt();

		this.availbBebtCu = debtBorrow.getAvailbBebtCu();

		this.thisShBorrowSe = debtBorrow.getThisShBorrowCu();

		this.thisShBorrow = debtBorrow.getThisShBorrow();

		this.thisShBorrowCu = debtBorrow.getThisShBorrowCu();

		this.thisShBorrowLi = debtBorrow.getThisShBorrowLi();

		/****** 本次股东借款起始年月 ***************/
		this.thisShBorrowLis = debtBorrow.getThisShBorrowLis();

		/****** 本次股东借款结束年月 ***************/
		this.thisShBorrowLie = debtBorrow.getThisShBorrowLie();

		this.thisShBorrowRa = debtBorrow.getThisShBorrowRa();

		/** 外债提供方 (去除掉) start ****/
		this.bebtProvider = debtBorrow.getBebtProvider();

		this.bebtProviderCu = debtBorrow.getBebtProviderCu();

		this.bebtProviderLi = debtBorrow.getBebtProviderLi();

		this.bebtProviderRa = debtBorrow.getBebtProviderRa();
		/** 外债提供方 (去除掉) end ****/

		this.use = debtBorrow.getUse();

		this.afceFlag = debtBorrow.getAfceFlag();

		this.afceSign = debtBorrow.getAfceSign();

		this.afceSignCu = debtBorrow.getAfceSignCu();

		/** AFCE折算汇率 ****/
		this.afceExcRate = debtBorrow.getAfceExcRate();

		/** AFCE签批情况(折算值) ****/
		this.afceSignExc = debtBorrow.getAfceSignExc();

		/** AFCE已付款金额 ****/
		this.afcePaid = debtBorrow.getAfcePaid();

		/** AFCE已付款金额币别 ****/
		this.afcePaidCu = debtBorrow.getAfcePaidCu();

		this.corpAudit = debtBorrow.getCorpAudit();

		this.corpAuditCu = debtBorrow.getCorpAuditCu();

		this.corpAuditLi = debtBorrow.getCorpAuditLi();

		/****** 集团项目审批期限起始年月 ***************/
		this.corpAuditLis = debtBorrow.getCorpAuditLis();

		/****** 集团项目审批期限结束年月 ***************/
		this.corpAuditLie = debtBorrow.getCorpAuditLie();

		this.corpAuditRa = debtBorrow.getCorpAuditRa();

		/** 备注(小股东是否按比例提供资金) ****/
		this.Memo = debtBorrow.getMemo();

		/** 还款方式及来源 ****/
		this.payBackMemo = debtBorrow.getPayBackMemo();

		/** 资金提供方类型 ****/
		this.providerType = debtBorrow.getProviderType();

		/** 资金提供公司KEY ****/
		this.providerKey = debtBorrow.getProviderKey();

		/** 折算汇率 ****/
		this.exchangeRate = debtBorrow.getExchangeRate();

		/** 保存PE的备注 */
		this.peMemo = debtBorrow.getPeMemo();
		// Constructors

		/** 贷款时间 */
		this.borrowDate = debtBorrow.getBorrowDate();

		/** 还款时间 */
		this.payBackDate = debtBorrow.getPayBackDate();

		/** 资金提供方名称 ****/
		this.providerName = debtBorrow.getProviderName();

		/** 2014-07-04 add by liushengbin 新加字段 **/

		/*** 外债期限类型 **/
		this.bebtDeadlineType = debtBorrow.getBebtDeadlineType();
		/*** 申请类型 **/
		this.applyType = debtBorrow.getApplyType();
		/*** 展期原外债合同主数据ID 关联 ‘外债合同_主数据(DEBT_CONTRACT)’表 **/
		this.debtContractId = debtBorrow.getDebtContractId();
		/** 当前节点 **/
		this.currentNode = debtBorrow.getCurrentNode();
		/** 流程状态 **/
		this.processStatus = debtBorrow.getProcessStatus();
	}

	public ProcDebtBorrow getDebtBorrow() {
		return debtBorrow;
	}

	public void setDebtBorrow(ProcDebtBorrow debtBorrow) {
		this.debtBorrow = debtBorrow;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public ShareHolder getShareHolder() {
		return shareHolder;
	}

	public void setShareHolder(ShareHolder shareHolder) {
		this.shareHolder = shareHolder;
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyEn() {
		return companyEn;
	}

	public void setCompanyEn(String companyEn) {
		this.companyEn = companyEn;
	}

	public Double getInvestBalance() {
		return investBalance;
	}

	public void setInvestBalance(Double investBalance) {
		this.investBalance = investBalance;
	}

	public String getInvestBalanceCu() {
		return investBalanceCu;
	}

	public void setInvestBalanceCu(String investBalanceCu) {
		this.investBalanceCu = investBalanceCu;
	}

	public Double getBebtInPlace() {
		return bebtInPlace;
	}

	public void setBebtInPlace(Double bebtInPlace) {
		this.bebtInPlace = bebtInPlace;
	}

	public String getBebtInPlaceCu() {
		return bebtInPlaceCu;
	}

	public void setBebtInPlaceCu(String bebtInPlaceCu) {
		this.bebtInPlaceCu = bebtInPlaceCu;
	}

	public Double getShBorrow() {
		return shBorrow;
	}

	public void setShBorrow(Double shBorrow) {
		this.shBorrow = shBorrow;
	}

	public String getShBorrowCu() {
		return shBorrowCu;
	}

	public void setShBorrowCu(String shBorrowCu) {
		this.shBorrowCu = shBorrowCu;
	}

	public Double getShBorrowLi() {
		return shBorrowLi;
	}

	public void setShBorrowLi(Double shBorrowLi) {
		this.shBorrowLi = shBorrowLi;
	}

	public Double getShBorrowRa() {
		return shBorrowRa;
	}

	public void setShBorrowRa(Double shBorrowRa) {
		this.shBorrowRa = shBorrowRa;
	}

	public Double getFornBebt() {
		return fornBebt;
	}

	public void setFornBebt(Double fornBebt) {
		this.fornBebt = fornBebt;
	}

	public String getFornBebtCu() {
		return fornBebtCu;
	}

	public void setFornBebtCu(String fornBebtCu) {
		this.fornBebtCu = fornBebtCu;
	}

	public Double getFornBebtLi() {
		return fornBebtLi;
	}

	public void setFornBebtLi(Double fornBebtLi) {
		this.fornBebtLi = fornBebtLi;
	}

	public Double getFornBebtRa() {
		return fornBebtRa;
	}

	public void setFornBebtRa(Double fornBebtRa) {
		this.fornBebtRa = fornBebtRa;
	}

	public Double getAvailbBebt() {
		return availbBebt;
	}

	public void setAvailbBebt(Double availbBebt) {
		this.availbBebt = availbBebt;
	}

	public String getAvailbBebtCu() {
		return availbBebtCu;
	}

	public void setAvailbBebtCu(String availbBebtCu) {
		this.availbBebtCu = availbBebtCu;
	}

	public String getThisShBorrowSe() {
		return thisShBorrowSe;
	}

	public void setThisShBorrowSe(String thisShBorrowSe) {
		this.thisShBorrowSe = thisShBorrowSe;
	}

	public Double getThisShBorrow() {
		return thisShBorrow;
	}

	public void setThisShBorrow(Double thisShBorrow) {
		this.thisShBorrow = thisShBorrow;
	}

	public String getThisShBorrowCu() {
		return thisShBorrowCu;
	}

	public void setThisShBorrowCu(String thisShBorrowCu) {
		this.thisShBorrowCu = thisShBorrowCu;
	}

	public Double getThisShBorrowLi() {
		return thisShBorrowLi;
	}

	public void setThisShBorrowLi(Double thisShBorrowLi) {
		this.thisShBorrowLi = thisShBorrowLi;
	}

	public Date getThisShBorrowLis() {
		return thisShBorrowLis;
	}

	public void setThisShBorrowLis(Date thisShBorrowLis) {
		this.thisShBorrowLis = thisShBorrowLis;
	}

	public Date getThisShBorrowLie() {
		return thisShBorrowLie;
	}

	public void setThisShBorrowLie(Date thisShBorrowLie) {
		this.thisShBorrowLie = thisShBorrowLie;
	}

	public String getThisShBorrowRa() {
		return thisShBorrowRa;
	}

	public void setThisShBorrowRa(String thisShBorrowRa) {
		this.thisShBorrowRa = thisShBorrowRa;
	}

	public Double getBebtProvider() {
		return bebtProvider;
	}

	public void setBebtProvider(Double bebtProvider) {
		this.bebtProvider = bebtProvider;
	}

	public String getBebtProviderCu() {
		return bebtProviderCu;
	}

	public void setBebtProviderCu(String bebtProviderCu) {
		this.bebtProviderCu = bebtProviderCu;
	}

	public Double getBebtProviderLi() {
		return bebtProviderLi;
	}

	public void setBebtProviderLi(Double bebtProviderLi) {
		this.bebtProviderLi = bebtProviderLi;
	}

	public Double getBebtProviderRa() {
		return bebtProviderRa;
	}

	public void setBebtProviderRa(Double bebtProviderRa) {
		this.bebtProviderRa = bebtProviderRa;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public String getAfceFlag() {
		return afceFlag;
	}

	public void setAfceFlag(String afceFlag) {
		this.afceFlag = afceFlag;
	}

	public Double getAfceSign() {
		return afceSign;
	}

	public void setAfceSign(Double afceSign) {
		this.afceSign = afceSign;
	}

	public String getAfceSignCu() {
		return afceSignCu;
	}

	public void setAfceSignCu(String afceSignCu) {
		this.afceSignCu = afceSignCu;
	}

	public Double getAfceExcRate() {
		return afceExcRate;
	}

	public void setAfceExcRate(Double afceExcRate) {
		this.afceExcRate = afceExcRate;
	}

	public Double getAfceSignExc() {
		return afceSignExc;
	}

	public void setAfceSignExc(Double afceSignExc) {
		this.afceSignExc = afceSignExc;
	}

	public Double getAfcePaid() {
		return afcePaid;
	}

	public void setAfcePaid(Double afcePaid) {
		this.afcePaid = afcePaid;
	}

	public String getAfcePaidCu() {
		return afcePaidCu;
	}

	public void setAfcePaidCu(String afcePaidCu) {
		this.afcePaidCu = afcePaidCu;
	}

	public Double getCorpAudit() {
		return corpAudit;
	}

	public void setCorpAudit(Double corpAudit) {
		this.corpAudit = corpAudit;
	}

	public String getCorpAuditCu() {
		return corpAuditCu;
	}

	public void setCorpAuditCu(String corpAuditCu) {
		this.corpAuditCu = corpAuditCu;
	}

	public Double getCorpAuditLi() {
		return corpAuditLi;
	}

	public void setCorpAuditLi(Double corpAuditLi) {
		this.corpAuditLi = corpAuditLi;
	}

	public Date getCorpAuditLis() {
		return corpAuditLis;
	}

	public void setCorpAuditLis(Date corpAuditLis) {
		this.corpAuditLis = corpAuditLis;
	}

	public Date getCorpAuditLie() {
		return corpAuditLie;
	}

	public void setCorpAuditLie(Date corpAuditLie) {
		this.corpAuditLie = corpAuditLie;
	}

	public String getCorpAuditRa() {
		return corpAuditRa;
	}

	public void setCorpAuditRa(String corpAuditRa) {
		this.corpAuditRa = corpAuditRa;
	}

	public String getMemo() {
		return Memo;
	}

	public void setMemo(String memo) {
		Memo = memo;
	}

	public String getPayBackMemo() {
		return payBackMemo;
	}

	public void setPayBackMemo(String payBackMemo) {
		this.payBackMemo = payBackMemo;
	}

	public String getProviderType() {
		return providerType;
	}

	public void setProviderType(String providerType) {
		this.providerType = providerType;
	}

	public String getProviderKey() {
		return providerKey;
	}

	public void setProviderKey(String providerKey) {
		this.providerKey = providerKey;
	}

	public Double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(Double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getPeMemo() {
		return peMemo;
	}

	public void setPeMemo(String peMemo) {
		this.peMemo = peMemo;
	}

	public Date getBorrowDate() {
		return borrowDate;
	}

	public void setBorrowDate(Date borrowDate) {
		this.borrowDate = borrowDate;
	}

	public Date getPayBackDate() {
		return payBackDate;
	}

	public void setPayBackDate(Date payBackDate) {
		this.payBackDate = payBackDate;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getBebtDeadlineType() {
		return bebtDeadlineType;
	}

	public void setBebtDeadlineType(String bebtDeadlineType) {
		this.bebtDeadlineType = bebtDeadlineType;
	}

	public String getApplyType() {
		return applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	public Long getDebtContractId() {
		return debtContractId;
	}

	public void setDebtContractId(Long debtContractId) {
		this.debtContractId = debtContractId;
	}

	public String getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(String currentNode) {
		this.currentNode = currentNode;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public String getProcessNo() {
		return processNo;
	}

	public void setProcessNo(String processNo) {
		this.processNo = processNo;
	}

	public String getDebtContract() {
		return debtContract;
	}

	public void setDebtContract(String debtContract) {
		this.debtContract = debtContract;
	}

	public String getDebtContractFunds() {
		return debtContractFunds;
	}

	public void setDebtContractFunds(String debtContractFunds) {
		this.debtContractFunds = debtContractFunds;
	}

	public String getNoAppliedFunds() {
		return noAppliedFunds;
	}

	public void setNoAppliedFunds(String noAppliedFunds) {
		this.noAppliedFunds = noAppliedFunds;
	}

	public String getApplyFunds() {
		return applyFunds;
	}

	public void setApplyFunds(String applyFunds) {
		this.applyFunds = applyFunds;
	}

	public String getReceivedFunds() {
		return receivedFunds;
	}

	public void setReceivedFunds(String receivedFunds) {
		this.receivedFunds = receivedFunds;
	}

	public String getRequestMoney() {
		return requestMoney;
	}

	public void setRequestMoney(String requestMoney) {
		this.requestMoney = requestMoney;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getOldDebtContract() {
		return oldDebtContract;
	}

	public void setOldDebtContract(String oldDebtContract) {
		this.oldDebtContract = oldDebtContract;
	}

	public DebtContract getContract() {
		return contract;
	}

	public void setContract(DebtContract contract) {
		this.contract = contract;
	}

}
