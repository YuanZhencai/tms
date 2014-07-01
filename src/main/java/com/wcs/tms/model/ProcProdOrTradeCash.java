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
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description: 生产或贸易总头寸申请流程实体
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
@Entity
@Table(name = "PROC_PROD_OR_TRADE_CASH")
public class ProcProdOrTradeCash extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	/** 公司信息 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;
	/** 流程实例ID */
	@Column(name = "PROC_INST_ID", length = 255)
	private String procInstId;
	/** 类型 */
	@Column(name = "APP_TYPE", length = 50)
	private String appType;
	/** 品种 */
	@Column(name = "VARIETY", length = 255)
	private String variety;
	/** 上次申请剩余头寸 */
	@Column(name = "LAST_CASH", precision = 12, scale = 4)
	private Double lastCash;
	/** 本次申请头寸 */
	@Column(name = "THIS_CASH", precision = 12, scale = 4)
	private Double thisCash;
	/** 上次申请剩余头寸金额 */
	@Column(name = "LAST_CASH_AMOUNT", precision = 12, scale = 4)
	private Double lastCashAmount;
	/** 本次申请头寸金额 */
	@Column(name = "THIS_CASH_AMOUNT", precision = 12, scale = 4)
	private Double thisCashAmount;
	/** 起始日期 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	private Date startDate;
	/** 截止日期 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;
	/** 小品种 */
	@Column(name = "SMALL_VARIETY", length = 6000)
	private String smallVariety;

	/** 保存PE的备注 */
	@Transient
	private String peMemo;
	
	public ProcProdOrTradeCash() {
		this.company = new Company();
	}
	
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getVariety() {
		return variety;
	}

	public void setVariety(String variety) {
		this.variety = variety;
	}

	public Double getLastCash() {
		return lastCash;
	}

	public void setLastCash(Double lastCash) {
		this.lastCash = lastCash;
	}

	public Double getThisCash() {
		return thisCash;
	}

	public void setThisCash(Double thisCash) {
		this.thisCash = thisCash;
	}

	public Double getLastCashAmount() {
		return lastCashAmount;
	}

	public void setLastCashAmount(Double lastCashAmount) {
		this.lastCashAmount = lastCashAmount;
	}

	public Double getThisCashAmount() {
		return thisCashAmount;
	}

	public void setThisCashAmount(Double thisCashAmount) {
		this.thisCashAmount = thisCashAmount;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getSmallVariety() {
		return smallVariety;
	}

	public void setSmallVariety(String smallVariety) {
		this.smallVariety = smallVariety;
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
		return null;
	}

}
