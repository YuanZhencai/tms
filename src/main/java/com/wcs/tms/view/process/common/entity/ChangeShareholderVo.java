/** * ChangeShareholderVo.java 
 * Created on 2014年6月23日 下午2:39:12 
 */

package com.wcs.tms.view.process.common.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.wcs.common.controller.helper.IdModel;
import com.wcs.tms.model.ProcRegiCapitalChangeShareholder;
import com.wcs.tms.model.ShareHolder;

/** 
* <p>Project: tms</p> 
* <p>Title: ChangeShareholderVo.java</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2014 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:yuanzhencai@wcs-global.com">Yuan</a> 
*/
public class ChangeShareholderVo extends IdModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ProcRegiCapitalChangeShareholder changeShareholder;
	
	private ShareHolder shareHolder;

	private Double equityPerc;

	private Double equityPercOriginal;

	private String fondsCurrency;

	private String fondsCurrencyOriginal;

	private Double fondsInPlace;

	private Double fondsInPlaceOriginal;

	private Double fondsTotal;

	private Double fondsTotalOriginal;

	private String isEquityRelated;

	private String isEquityRelatedOriginal;

	private Double relatedPerc;

	private Double relatedPercOriginal;

	private Long shareholderIdOriginal;

	private String shareholderName;

	private String shareholderNameOriginal;

	private String status;
	
	private Double fondsSum;
	
	
	private List<ChangeShareholderVo> shareHolders = new ArrayList<ChangeShareholderVo>();

	public ChangeShareholderVo() {
	}

	public ChangeShareholderVo(ShareHolder shareHolder, BigDecimal fondsSum) {
		this.fondsSum = fondsSum.doubleValue();
		
		this.shareHolder = shareHolder;
		
		this.equityPerc = shareHolder.getEquityPerc();
		this.equityPercOriginal = shareHolder.getEquityPerc();
		
		this.fondsCurrency = shareHolder.getFondsCurrency();
		this.fondsCurrencyOriginal = shareHolder.getFondsCurrency();
		
		this.fondsInPlace = shareHolder.getFondsInPlace();
		this.fondsInPlaceOriginal = shareHolder.getFondsInPlace();
		
		this.fondsTotal = shareHolder.getFondsTotal();
		this.fondsTotalOriginal = shareHolder.getFondsTotal();
		
		this.isEquityRelated = shareHolder.getIsEquityRelated();
		this.isEquityRelatedOriginal = shareHolder.getIsEquityRelated();
		
		this.relatedPerc = shareHolder.getRelatedPerc();
		this.relatedPercOriginal = shareHolder.getRelatedPerc();
		
		this.shareholderIdOriginal = shareHolder.getId();
		
		this.shareholderName = shareHolder.getShareHolderName();
		this.shareholderNameOriginal = shareHolder.getShareHolderName();
		shareHolders.add(this);
	}
	
	public ChangeShareholderVo(ShareHolder shareHolder) {
		
		this.shareHolder = shareHolder;
		
		this.equityPerc = shareHolder.getEquityPerc();
		this.equityPercOriginal = shareHolder.getEquityPerc();
		
		this.fondsCurrency = shareHolder.getFondsCurrency();
		this.fondsCurrencyOriginal = shareHolder.getFondsCurrency();
		
		this.fondsInPlace = shareHolder.getFondsInPlace();
		this.fondsInPlaceOriginal = shareHolder.getFondsInPlace();
		
		this.fondsTotal = shareHolder.getFondsTotal();
		this.fondsTotalOriginal = shareHolder.getFondsTotal();
		
		this.isEquityRelated = shareHolder.getIsEquityRelated();
		this.isEquityRelatedOriginal = shareHolder.getIsEquityRelated();
		
		this.relatedPerc = shareHolder.getRelatedPerc();
		this.relatedPercOriginal = shareHolder.getRelatedPerc();
		
		this.shareholderIdOriginal = shareHolder.getId();
		
		this.shareholderName = shareHolder.getShareHolderName();
		this.shareholderNameOriginal = shareHolder.getShareHolderName();
		shareHolders.add(this);
	}
	
	public ChangeShareholderVo(ProcRegiCapitalChangeShareholder changeShareholder) {
		
		this.changeShareholder = changeShareholder;

		this.equityPerc = changeShareholder.getEquityPerc();

		this.equityPercOriginal = changeShareholder.getEquityPercOriginal();

		this.fondsCurrency = changeShareholder.getFondsCurrency();

		this.fondsCurrencyOriginal = changeShareholder.getFondsCurrencyOriginal();

		this.fondsInPlace = changeShareholder.getFondsInPlace();

		this.fondsInPlaceOriginal = changeShareholder.getFondsInPlaceOriginal();

		this.fondsTotal = changeShareholder.getFondsTotal();

		this.fondsTotalOriginal = changeShareholder.getFondsTotalOriginal();

		this.isEquityRelated = changeShareholder.getIsEquityRelated();

		this.isEquityRelatedOriginal = changeShareholder.getIsEquityRelatedOriginal();

		this.relatedPerc = changeShareholder.getRelatedPerc();

		this.relatedPercOriginal = changeShareholder.getRelatedPercOriginal();

		this.shareholderIdOriginal = changeShareholder.getShareholderIdOriginal();

		this.shareholderName = changeShareholder.getShareholderName();

		this.shareholderNameOriginal = changeShareholder.getShareholderNameOriginal();

		this.status = changeShareholder.getStatus();
		
		shareHolders.add(this);

	}


	public ProcRegiCapitalChangeShareholder getChangeShareholder() {
		return changeShareholder;
	}

	public void setChangeShareholder(ProcRegiCapitalChangeShareholder changeShareholder) {
		this.changeShareholder = changeShareholder;
	}

	public ShareHolder getShareHolder() {
		return shareHolder;
	}

	public void setShareHolder(ShareHolder shareHolder) {
		this.shareHolder = shareHolder;
	}

	public Double getEquityPerc() {
		return equityPerc;
	}

	public void setEquityPerc(Double equityPerc) {
		this.equityPerc = equityPerc;
	}

	public Double getEquityPercOriginal() {
		return equityPercOriginal;
	}

	public void setEquityPercOriginal(Double equityPercOriginal) {
		this.equityPercOriginal = equityPercOriginal;
	}

	public String getFondsCurrency() {
		return fondsCurrency;
	}

	public void setFondsCurrency(String fondsCurrency) {
		this.fondsCurrency = fondsCurrency;
	}

	public String getFondsCurrencyOriginal() {
		return fondsCurrencyOriginal;
	}

	public void setFondsCurrencyOriginal(String fondsCurrencyOriginal) {
		this.fondsCurrencyOriginal = fondsCurrencyOriginal;
	}

	public Double getFondsInPlace() {
		return fondsInPlace;
	}

	public void setFondsInPlace(Double fondsInPlace) {
		this.fondsInPlace = fondsInPlace;
	}

	public Double getFondsInPlaceOriginal() {
		return fondsInPlaceOriginal;
	}

	public void setFondsInPlaceOriginal(Double fondsInPlaceOriginal) {
		this.fondsInPlaceOriginal = fondsInPlaceOriginal;
	}

	public Double getFondsTotal() {
		return fondsTotal;
	}

	public void setFondsTotal(Double fondsTotal) {
		this.fondsTotal = fondsTotal;
	}

	public Double getFondsTotalOriginal() {
		return fondsTotalOriginal;
	}

	public void setFondsTotalOriginal(Double fondsTotalOriginal) {
		this.fondsTotalOriginal = fondsTotalOriginal;
	}

	public String getIsEquityRelated() {
		return isEquityRelated;
	}

	public void setIsEquityRelated(String isEquityRelated) {
		this.isEquityRelated = isEquityRelated;
	}

	public String getIsEquityRelatedOriginal() {
		return isEquityRelatedOriginal;
	}

	public void setIsEquityRelatedOriginal(String isEquityRelatedOriginal) {
		this.isEquityRelatedOriginal = isEquityRelatedOriginal;
	}

	public Double getRelatedPerc() {
		return relatedPerc;
	}

	public void setRelatedPerc(Double relatedPerc) {
		this.relatedPerc = relatedPerc;
	}

	public Double getRelatedPercOriginal() {
		return relatedPercOriginal;
	}

	public void setRelatedPercOriginal(Double relatedPercOriginal) {
		this.relatedPercOriginal = relatedPercOriginal;
	}

	public Long getShareholderIdOriginal() {
		return shareholderIdOriginal;
	}

	public void setShareholderIdOriginal(Long shareholderIdOriginal) {
		this.shareholderIdOriginal = shareholderIdOriginal;
	}

	public String getShareholderName() {
		return shareholderName;
	}

	public void setShareholderName(String shareholderName) {
		this.shareholderName = shareholderName;
	}

	public String getShareholderNameOriginal() {
		return shareholderNameOriginal;
	}

	public void setShareholderNameOriginal(String shareholderNameOriginal) {
		this.shareholderNameOriginal = shareholderNameOriginal;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<ChangeShareholderVo> getShareHolders() {
		return shareHolders;
	}

	public void setShareHolders(List<ChangeShareholderVo> shareHolders) {
		this.shareHolders = shareHolders;
	}

	public Double getFondsSum() {
		return fondsSum;
	}

	public void setFondsSum(Double fondsSum) {
		this.fondsSum = fondsSum;
	}

}
