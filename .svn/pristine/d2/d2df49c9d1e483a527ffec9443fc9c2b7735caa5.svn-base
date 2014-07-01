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
 * 采购资金（生产）支付详情
 * 
 * @author WCS-RAY
 * 
 */
@Entity
@Table(name = "PROC_PURCHASE_FUND_PROD_PAY_DETAIL")
public class ProcPurchaseFundProdPayDetail extends BaseEntity {

	private static final long serialVersionUID = -3431073403161039720L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAY_ID")
	private ProcPurchaseFundProdPay procPurchaseFundProdPay;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_ID")
	private ProcPurchaseFundProdProduct procPurchaseFundProdProduct;

	/* 品种1审批头寸 */
	@Column(name = "VARIETY_NUM", precision = 12, scale = 4)
	private Double varietyNum;
	/* 品种1支付金额 */
	@Column(name = "VARIETY_AMOUNT", precision = 12, scale = 4)
	private Double varietyAmount;

	public ProcPurchaseFundProdPay getProcPurchaseFundProdPay() {
		return procPurchaseFundProdPay;
	}

	public void setProcPurchaseFundProdPay(
			ProcPurchaseFundProdPay procPurchaseFundProdPay) {
		this.procPurchaseFundProdPay = procPurchaseFundProdPay;
	}

	public ProcPurchaseFundProdProduct getProcPurchaseFundProdProduct() {
		return procPurchaseFundProdProduct;
	}

	public void setProcPurchaseFundProdProduct(
			ProcPurchaseFundProdProduct procPurchaseFundProdProduct) {
		this.procPurchaseFundProdProduct = procPurchaseFundProdProduct;
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

	@Override
	@Transient
	public String getDisplayText() {
		// TODO Auto-generated method stub
		return null;
	}

}
