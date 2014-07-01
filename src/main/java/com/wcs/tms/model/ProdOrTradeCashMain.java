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
 * Description: 生产或贸易总头寸主数据实体
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
@Table(name = "PROD_OR_TRADE_CASH_MAIN")
public class ProdOrTradeCashMain extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/** 公司信息 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;
	/** 类型 */
	@Column(name = "APP_TYPE", length = 50)
	private String appType;
	/** 品种 */
	@Column(name = "VARIETY", length = 255)
	private String variety;
	/** 头寸数量 */
	@Column(name = "TOTAL_CASH", precision = 12, scale = 4)
	private Double totalCash;
	/** 头寸金额 */
	@Column(name = "TOTAL_CASH_AMOUNT", precision = 12, scale = 4)
	private Double totalCashAmount;
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

	public ProdOrTradeCashMain() {
		this.company = new Company();
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
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

	public Double getTotalCash() {
		return totalCash;
	}

	public void setTotalCash(Double totalCash) {
		this.totalCash = totalCash;
	}

	public Double getTotalCashAmount() {
		return totalCashAmount;
	}

	public void setTotalCashAmount(Double totalCashAmount) {
		this.totalCashAmount = totalCashAmount;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getSmallVariety() {
		return smallVariety;
	}

	public void setSmallVariety(String smallVariety) {
		this.smallVariety = smallVariety;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	
	@Transient
	@Override
	public String getDisplayText() {
		return null;
	}

}
