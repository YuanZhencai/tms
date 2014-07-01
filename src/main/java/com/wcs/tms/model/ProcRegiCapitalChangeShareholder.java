package com.wcs.tms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;


/**
 * The persistent class for the PROC_REGI_CAPITAL_CHANGE_SHAREHOLDER database table.
 * 
 */
@Entity
@Table(name="PROC_REGI_CAPITAL_CHANGE_SHAREHOLDER")
@NamedQuery(name="ProcRegiCapitalChangeShareholder.findAll", query="SELECT p FROM ProcRegiCapitalChangeShareholder p")
public class ProcRegiCapitalChangeShareholder extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@Column(name="EQUITY_PERC")
	private Double equityPerc;

	@Column(name="EQUITY_PERC_ORIGINAL")
	private Double equityPercOriginal;

	@Column(name="FONDS_CURRENCY")
	private String fondsCurrency;

	@Column(name="FONDS_CURRENCY_ORIGINAL")
	private String fondsCurrencyOriginal;

	@Column(name="FONDS_IN_PLACE")
	private Double fondsInPlace;

	@Column(name="FONDS_IN_PLACE_ORIGINAL")
	private Double fondsInPlaceOriginal;

	@Column(name="FONDS_TOTAL")
	private Double fondsTotal;

	@Column(name="FONDS_TOTAL_ORIGINAL")
	private Double fondsTotalOriginal;

	@Column(name="IS_EQUITY_RELATED")
	private String isEquityRelated;

	@Column(name="IS_EQUITY_RELATED_ORIGINAL")
	private String isEquityRelatedOriginal;

	@Column(name="RELATED_PERC")
	private Double relatedPerc;

	@Column(name="RELATED_PERC_ORIGINAL")
	private Double relatedPercOriginal;

	@Column(name="SHAREHOLDER_ID_ORIGINAL")
	private Long shareholderIdOriginal;

	@Column(name="SHAREHOLDER_NAME")
	private String shareholderName;

	@Column(name="SHAREHOLDER_NAME_ORIGINAL")
	private String shareholderNameOriginal;

	private String status;

	//bi-directional many-to-one association to ProcRegiCapitalChange
	@ManyToOne
	@JoinColumn(name="REGI_CAPITAL_CHANGE_ID")
	private ProcRegiCapitalChange procRegiCapitalChange;

	public ProcRegiCapitalChangeShareholder() {
	}

	public Double getEquityPerc() {
		return this.equityPerc;
	}

	public void setEquityPerc(Double equityPerc) {
		this.equityPerc = equityPerc;
	}

	public Double getEquityPercOriginal() {
		return this.equityPercOriginal;
	}

	public void setEquityPercOriginal(Double equityPercOriginal) {
		this.equityPercOriginal = equityPercOriginal;
	}

	public String getFondsCurrency() {
		return this.fondsCurrency;
	}

	public void setFondsCurrency(String fondsCurrency) {
		this.fondsCurrency = fondsCurrency;
	}

	public String getFondsCurrencyOriginal() {
		return this.fondsCurrencyOriginal;
	}

	public void setFondsCurrencyOriginal(String fondsCurrencyOriginal) {
		this.fondsCurrencyOriginal = fondsCurrencyOriginal;
	}

	public Double getFondsInPlace() {
		return this.fondsInPlace;
	}

	public void setFondsInPlace(Double fondsInPlace) {
		this.fondsInPlace = fondsInPlace;
	}

	public Double getFondsInPlaceOriginal() {
		return this.fondsInPlaceOriginal;
	}

	public void setFondsInPlaceOriginal(Double fondsInPlaceOriginal) {
		this.fondsInPlaceOriginal = fondsInPlaceOriginal;
	}

	public Double getFondsTotal() {
		return this.fondsTotal;
	}

	public void setFondsTotal(Double fondsTotal) {
		this.fondsTotal = fondsTotal;
	}

	public Double getFondsTotalOriginal() {
		return this.fondsTotalOriginal;
	}

	public void setFondsTotalOriginal(Double fondsTotalOriginal) {
		this.fondsTotalOriginal = fondsTotalOriginal;
	}

	public String getIsEquityRelated() {
		return this.isEquityRelated;
	}

	public void setIsEquityRelated(String isEquityRelated) {
		this.isEquityRelated = isEquityRelated;
	}

	public String getIsEquityRelatedOriginal() {
		return this.isEquityRelatedOriginal;
	}

	public void setIsEquityRelatedOriginal(String isEquityRelatedOriginal) {
		this.isEquityRelatedOriginal = isEquityRelatedOriginal;
	}

	public Double getRelatedPerc() {
		return this.relatedPerc;
	}

	public void setRelatedPerc(Double relatedPerc) {
		this.relatedPerc = relatedPerc;
	}

	public Double getRelatedPercOriginal() {
		return this.relatedPercOriginal;
	}

	public void setRelatedPercOriginal(Double relatedPercOriginal) {
		this.relatedPercOriginal = relatedPercOriginal;
	}

	public long getShareholderIdOriginal() {
		return this.shareholderIdOriginal;
	}

	public void setShareholderIdOriginal(long shareholderIdOriginal) {
		this.shareholderIdOriginal = shareholderIdOriginal;
	}

	public String getShareholderName() {
		return this.shareholderName;
	}

	public void setShareholderName(String shareholderName) {
		this.shareholderName = shareholderName;
	}

	public String getShareholderNameOriginal() {
		return this.shareholderNameOriginal;
	}

	public void setShareholderNameOriginal(String shareholderNameOriginal) {
		this.shareholderNameOriginal = shareholderNameOriginal;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ProcRegiCapitalChange getProcRegiCapitalChange() {
		return this.procRegiCapitalChange;
	}

	public void setProcRegiCapitalChange(ProcRegiCapitalChange procRegiCapitalChange) {
		this.procRegiCapitalChange = procRegiCapitalChange;
	}
	
	@Override
	@Transient
	public String getDisplayText() {
		return null;
	}
}