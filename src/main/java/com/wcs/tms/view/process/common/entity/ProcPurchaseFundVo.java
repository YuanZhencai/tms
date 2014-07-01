package com.wcs.tms.view.process.common.entity;

import java.io.Serializable;

import com.wcs.common.controller.helper.IdModel;

/** 
* <p>Project: tms</p> 
* <p>Title: 付款审批时品种信息Vo</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
public class ProcPurchaseFundVo extends IdModel implements Serializable {

	private static final long serialVersionUID = 1L;

	// 品种
	private String varietyId;
	// 剩余头寸数量（获得）
	private Double lessPurchaseNum;
	// 本次申请数量（获得）
	private Double thisPurchaseNum;
	// 剩余需要审批数量（自动计算）
	private Double needPurchaseNum;
	// 本次审批数量（可改）（查看时做已审批数量使用）
	private Double thisPurchaseNumPay;
	// 本次申请金额（获得）
	private Double thisPurchaseAmount;
	// 剩余需要支付的申请金额（计算）
	private Double lessPurchaseAmountPay;
	// 本次支付金额（可改）（查看时做已支付金额使用）
	private Double thisPurchaseAmountPay;
	// 是否为关联方
	private String varietyRelated;
	// 品种备注
	private String remark;
	
	private Long productId;

	public String getVarietyId() {
		return varietyId;
	}

	public void setVarietyId(String varietyId) {
		this.varietyId = varietyId;
	}

	public Double getLessPurchaseNum() {
		return lessPurchaseNum;
	}

	public void setLessPurchaseNum(Double lessPurchaseNum) {
		this.lessPurchaseNum = lessPurchaseNum;
	}

	public Double getThisPurchaseNum() {
		return thisPurchaseNum;
	}

	public void setThisPurchaseNum(Double thisPurchaseNum) {
		this.thisPurchaseNum = thisPurchaseNum;
	}

	public Double getNeedPurchaseNum() {
		return needPurchaseNum;
	}

	public void setNeedPurchaseNum(Double needPurchaseNum) {
		this.needPurchaseNum = needPurchaseNum;
	}

	public Double getThisPurchaseNumPay() {
		return thisPurchaseNumPay;
	}

	public void setThisPurchaseNumPay(Double thisPurchaseNumPay) {
		this.thisPurchaseNumPay = thisPurchaseNumPay;
	}

	public Double getThisPurchaseAmount() {
		return thisPurchaseAmount;
	}

	public void setThisPurchaseAmount(Double thisPurchaseAmount) {
		this.thisPurchaseAmount = thisPurchaseAmount;
	}

	public Double getLessPurchaseAmountPay() {
		return lessPurchaseAmountPay;
	}

	public void setLessPurchaseAmountPay(Double lessPurchaseAmountPay) {
		this.lessPurchaseAmountPay = lessPurchaseAmountPay;
	}

	public Double getThisPurchaseAmountPay() {
		return thisPurchaseAmountPay;
	}

	public void setThisPurchaseAmountPay(Double thisPurchaseAmountPay) {
		this.thisPurchaseAmountPay = thisPurchaseAmountPay;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public ProcPurchaseFundVo() {

	}

	public String getVarietyRelated() {
		return varietyRelated;
	}

	public void setVarietyRelated(String varietyRelated) {
		this.varietyRelated = varietyRelated;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}
}