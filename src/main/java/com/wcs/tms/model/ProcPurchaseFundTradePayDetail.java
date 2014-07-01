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
@Table(name = "PROC_PURCHASE_FUND_TRADE_PAY_DETAIL")
public class ProcPurchaseFundTradePayDetail extends BaseEntity {

	private static final long serialVersionUID = -7946423513296353203L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAY_ID")
	private ProcPurchaseFundTradePay procPurchaseFundTradePay;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_ID")
	private ProcPurchaseFundTradeProduct procPurchaseFundTradeProduct;
	
	/* 品种1审批头寸 */
	@Column(name = "VARIETY_NUM", precision = 12, scale = 4)
	private Double varietyNum;
	/* 品种1支付金额 */
	@Column(name = "VARIETY_AMOUNT", precision = 12, scale = 4)
	private Double varietyAmount;
	
	public ProcPurchaseFundTradePay getProcPurchaseFundTradePay() {
		return procPurchaseFundTradePay;
	}

	public void setProcPurchaseFundTradePay(
			ProcPurchaseFundTradePay procPurchaseFundTradePay) {
		this.procPurchaseFundTradePay = procPurchaseFundTradePay;
	}

	public ProcPurchaseFundTradeProduct getProcPurchaseFundTradeProduct() {
		return procPurchaseFundTradeProduct;
	}

	public void setProcPurchaseFundTradeProduct(
			ProcPurchaseFundTradeProduct procPurchaseFundTradeProduct) {
		this.procPurchaseFundTradeProduct = procPurchaseFundTradeProduct;
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
