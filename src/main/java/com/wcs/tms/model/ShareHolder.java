package com.wcs.tms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;

/**
 * 
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description: 股权信息实体类
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Entity
@Table(name = "SHAREHOLDER")
public class ShareHolder extends BaseEntity {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	/** 公司信息 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;
	/** 序号 */
	@Column(name = "INDEX", precision = 5, scale = 0)
	private Long index;
	/** 已到位资金 */
	@Column(name = "FONDS_IN_PLACE", precision = 12, scale = 4)
	private Double fondsInPlace;
	/** 股东名称 */
	@Column(name = "SHAREHOLDER_NAME", length = 255)
	private String shareHolderName;
	/** 资金类别 */
	@Column(name = "FONDS_CURRENCY", length = 101)
	private String fondsCurrency;
	/** 股权比例 */
	@Column(name = "EQUITY_PERC", precision = 12, scale = 4)
	private Double equityPerc;
	/** 注册资本金额 */
	@Column(name = "FONDS_TOTAL", precision = 12, scale = 4)
	private Double fondsTotal;
	@Transient
	private Double fondsNotTotal;
	/** 是否股权关联 */
	@Column(name = "IS_EQUITY_RELATED", length = 1)
	private String isEquityRelated;
	/** 关联比例 */
	@Column(name = "RELATED_PERC", precision = 12, scale = 4)
	private Double relatedPerc;

	/** 已到账金额 */
	@Column(name = "REAL_RECEIVED_FUNDS", precision = 12, scale = 4)
	private Double realReceivedFunds;

	public String getIsEquityRelated() {
		return isEquityRelated;
	}

	public void setIsEquityRelated(String isEquityRelated) {
		this.isEquityRelated = isEquityRelated;
	}

	public Double getRelatedPerc() {
		return relatedPerc;
	}

	public void setRelatedPerc(Double relatedPerc) {
		this.relatedPerc = relatedPerc;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public ShareHolder() {
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

	public Double getFondsInPlace() {
		return fondsInPlace;
	}

	public void setFondsInPlace(Double fondsInPlace) {
		this.fondsInPlace = fondsInPlace;
	}

	public String getShareHolderName() {
		return shareHolderName;
	}

	public void setShareHolderName(String shareHolderName) {
		this.shareHolderName = shareHolderName;
	}

	public String getFondsCurrency() {
		return fondsCurrency;
	}

	public void setFondsCurrency(String fondsCurrency) {
		this.fondsCurrency = fondsCurrency;
	}

	public Double getEquityPerc() {
		return equityPerc;
	}

	public void setEquityPerc(Double equityPerc) {
		this.equityPerc = equityPerc;
	}

	public Double getFondsTotal() {
		return fondsTotal;
	}

	public void setFondsTotal(Double fondsTotal) {
		this.fondsTotal = fondsTotal;
	}

	public Double getRealReceivedFunds() {
		return realReceivedFunds;
	}

	public void setRealReceivedFunds(Double realReceivedFunds) {
		this.realReceivedFunds = realReceivedFunds;
	}

	@Override
	@Transient
	public String getDisplayText() {

		return null;
	}

	public Double getFondsNotTotal() {
		return fondsNotTotal;
	}

	public void setFondsNotTotal(Double fondsNotTotal) {
		this.fondsNotTotal = fondsNotTotal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((shareHolderName == null) ? 0 : shareHolderName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ShareHolder other = (ShareHolder) obj;
		if (shareHolderName == null) {
			if (other.shareHolderName != null) {
				return false;
			}
		} else if (!shareHolderName.equals(other.shareHolderName)) {
			return false;
		}
		return true;
	}

}
