package com.wcs.tms.service.system.interfaces;

/**
 * 公司的外債资本金主数据信息
 * 
 * @author liushengbin
 * 
 */
public class DebtCapitalInfo {

	/**
	 * 公司ID
	 */
	private Long companyId;
	/**
	 * 公司名称
	 */
	private String companyName;

	/**
	 * 公司英文名
	 */
	private String companyEn;

	/**
	 * 公司SAP CODE
	 */
	private String sapCode;
	/**
	 * 币别
	 */
	private String currency;
	/**
	 * 是否为集团
	 */
	private String corporationFlag;
	/**
	 * 投资总额
	 */
	private Double investTotal;

	/**
	 * 注册资本金（所有股东注册金额之和）
	 */
	private Double fondsTotal;

	/**
	 * 已到位资金（所有股东已到位资金之和）
	 */
	private Double fondsInPlaceTotal;
	/**
	 * 未到位资金（所有股东未到位资金之和）
	 */
	private Double fondsNotInPlaceTotal;

	/**
	 * 投注差（即外债额度） 规则： 1、如果‘投注差是否可用’字段值为true， 投注差 =投资总额-注册资本金
	 * 2、如果‘投注差是否可用’字段值为false，投注差 =指定的投注差
	 */
	private Double investRegRema;
	/**
	 * 已用投注差（即已使用的外债额度，直接读取公司表中字段USED_INVEST_REG_REMA）
	 */
	private Double usedInvestRegRema;
	/**
	 * 可用投注差（投注差-已用的投注差）
	 */
	private Double avaiInvestRegRema;

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
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

	public String getSapCode() {
		return sapCode;
	}

	public void setSapCode(String sapCode) {
		this.sapCode = sapCode;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCorporationFlag() {
		return corporationFlag;
	}

	public void setCorporationFlag(String corporationFlag) {
		this.corporationFlag = corporationFlag;
	}

	public Double getInvestTotal() {
		return investTotal;
	}

	public void setInvestTotal(Double investTotal) {
		this.investTotal = investTotal;
	}

	public Double getFondsTotal() {
		return fondsTotal;
	}

	public void setFondsTotal(Double fondsTotal) {
		this.fondsTotal = fondsTotal;
	}

	public Double getFondsInPlaceTotal() {
		return fondsInPlaceTotal;
	}

	public void setFondsInPlaceTotal(Double fondsInPlaceTotal) {
		this.fondsInPlaceTotal = fondsInPlaceTotal;
	}

	public Double getFondsNotInPlaceTotal() {
		return fondsNotInPlaceTotal;
	}

	public void setFondsNotInPlaceTotal(Double fondsNotInPlaceTotal) {
		this.fondsNotInPlaceTotal = fondsNotInPlaceTotal;
	}

	public Double getInvestRegRema() {
		return investRegRema;
	}

	public void setInvestRegRema(Double investRegRema) {
		this.investRegRema = investRegRema;
	}

	public Double getUsedInvestRegRema() {
		return usedInvestRegRema;
	}

	public void setUsedInvestRegRema(Double usedInvestRegRema) {
		this.usedInvestRegRema = usedInvestRegRema;
	}

	public Double getAvaiInvestRegRema() {
		return avaiInvestRegRema;
	}

	public void setAvaiInvestRegRema(Double avaiInvestRegRema) {
		this.avaiInvestRegRema = avaiInvestRegRema;
	}

}
