package com.wcs.tms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;

@Entity
@Table(name = "PROC_PURCHASE_FUND_TRADE_PRODUCT")
public class ProcPurchaseFundTradeProduct extends BaseEntity{

	private static final long serialVersionUID = -8162209507351909234L;
	//流程
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROC_INST_ID")
	private ProcPurchaseFundTrade procPurchaseFundTrade;
	/* 品种 */
	@Column(name = "VARIETY", length = 101)
	protected String variety;
	/* 品种1是否为关联方 */
	@Column(name = "VARIETY_RELATED", length = 1)
	private String varietyRelated;
	/* 品种1剩余头寸 */
	@Column(name = "VARIETY_REMAIN", precision = 12, scale = 4)
	private Double varietyRemain;
	/* 品种1申请数量 */
	@Column(name = "VARIETY_NUM", precision = 12, scale = 4)
	private Double varietyNum;
	/* 品种1申请金额 */
	@Column(name = "VARIETY_AMOUNT", precision = 12, scale = 4)
	private Double varietyAmount;
	/* 品种1备注 */
	@Column(name = "VARIETY_REMARK", length = 2000)
	private String varietyRemark;
	/* 品种1状态 */
	@Column(name = "VARIETY_DEFUNCT", length = 1)
	private String varietyDefunct;
	
	public ProcPurchaseFundTrade getProcPurchaseFundTrade() {
		return procPurchaseFundTrade;
	}


	public void setProcPurchaseFundTrade(ProcPurchaseFundTrade procPurchaseFundTrade) {
		this.procPurchaseFundTrade = procPurchaseFundTrade;
	}


	public String getVariety() {
		return variety;
	}


	public void setVariety(String variety) {
		this.variety = variety;
	}


	public String getVarietyRelated() {
		return varietyRelated;
	}


	public void setVarietyRelated(String varietyRelated) {
		this.varietyRelated = varietyRelated;
	}


	public Double getVarietyRemain() {
		return varietyRemain;
	}


	public void setVarietyRemain(Double varietyRemain) {
		this.varietyRemain = varietyRemain;
	}


	public Double getVarietyNum() {
		return varietyNum;
	}


	public void setVarietyNum(Double varietyNum) {
		this.varietyNum = varietyNum;
	}


	public Double getVarietyAmount() {
		return varietyAmount;
	}


	public void setVarietyAmount(Double varietyAmount) {
		this.varietyAmount = varietyAmount;
	}


	public String getVarietyRemark() {
		return varietyRemark;
	}


	public void setVarietyRemark(String varietyRemark) {
		this.varietyRemark = varietyRemark;
	}

	public String getVarietyDefunct() {
		return varietyDefunct;
	}

	public void setVarietyDefunct(String varietyDefunct) {
		this.varietyDefunct = varietyDefunct;
	}
	
	@Override
	@Transient
	public String getDisplayText() {
		return null;
	}


}
