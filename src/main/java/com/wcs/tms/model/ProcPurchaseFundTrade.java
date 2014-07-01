package com.wcs.tms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/** 
* <p>Project: tms</p> 
* <p>Title: 流程_采购资金(贸易)Model</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
@Entity
@Table(name = "PROC_PURCHASE_FUND_TRADE")
public class ProcPurchaseFundTrade extends BasePayEntity {

	private static final long serialVersionUID = 1L;

	/* 流程实例ID */
	@Column(name = "PROC_INST_ID", length = 255)
	private String procInstId;
	/* 收款人户名 */
	@Column(name = "RECEIVER_NAME", length = 255)
	private String receiverName;
	/* 采购资金类型 */
	@Column(name = "PURCHASE_TYPE", length = 1)
	private String purchaseType;
	/* 品种1是否为关联方 */
	@Column(name = "VARIETY_ONE_RELATED", length = 1)
	private String varietyOneRelated;
	/* 品种1剩余头寸 */
	@Column(name = "VARIETY_ONE_REMAIN", precision = 12, scale = 4)
	private Double varietyOneRemain;
	/* 品种1申请数量 */
	@Column(name = "VARIETY_ONE_NUM", precision = 12, scale = 4)
	private Double varietyOneNum;
	/* 品种1申请金额 */
	@Column(name = "VARIETY_ONE_AMOUNT", precision = 12, scale = 4)
	private Double varietyOneAmount;
	/* 品种1备注 */
	@Column(name = "VARIETY_ONE_REMARK", length = 2000)
	private String varietyOneRemark;
	/* 品种1状态 */
	@Column(name = "VARIETY_ONE_DEFUNCT", length = 1)
	private String varietyOneDefunct;
	/* 品种2是否为关联方 */
	@Column(name = "VARIETY_TWO_RELATED", length = 1)
	private String varietyTwoRelated;
	/* 品种2剩余头寸 */
	@Column(name = "VARIETY_TWO_REMAIN", precision = 12, scale = 4)
	private Double varietyTwoRemain;
	/* 品种2申请数量 */
	@Column(name = "VARIETY_TWO_NUM", precision = 12, scale = 4)
	private Double varietyTwoNum;
	/* 品种2申请金额 */
	@Column(name = "VARIETY_TWO_AMOUNT", precision = 12, scale = 4)
	private Double varietyTwoAmount;
	/* 品种2备注 */
	@Column(name = "VARIETY_TWO_REMARK", length = 2000)
	private String varietyTwoRemark;
	/* 品种2状态 */
	@Column(name = "VARIETY_TWO_DEFUNCT", length = 1)
	private String varietyTwoDefunct;
	/* 品种3是否为关联方 */
	@Column(name = "VARIETY_THR_RELATED", length = 1)
	private String varietyThrRelated;
	/* 品种3剩余头寸 */
	@Column(name = "VARIETY_THR_REMAIN", precision = 12, scale = 4)
	private Double varietyThrRemain;
	/* 品种3申请数量 */
	@Column(name = "VARIETY_THR_NUM", precision = 12, scale = 4)
	private Double varietyThrNum;
	/* 品种3申请金额 */
	@Column(name = "VARIETY_THR_AMOUNT", precision = 12, scale = 4)
	private Double varietyThrAmount;
	/* 品种3备注 */
	@Column(name = "VARIETY_THR_REMARK", length = 2000)
	private String varietyThrRemark;
	/* 品种3状态 */
	@Column(name = "VARIETY_THR_DEFUNCT", length = 1)
	private String varietyThrDefunct;
	/* 流程状态 */
	@Column(name = "STATUS", length = 1)
	private String status;

	/** 保存PE的备注*/
	@Transient
	private String peMemo;

	public ProcPurchaseFundTrade() {
		company = new Company();
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public Double getVarietyOneRemain() {
		return varietyOneRemain;
	}

	public void setVarietyOneRemain(Double varietyOneRemain) {
		this.varietyOneRemain = varietyOneRemain;
	}

	public Double getVarietyOneNum() {
		return varietyOneNum;
	}

	public void setVarietyOneNum(Double varietyOneNum) {
		this.varietyOneNum = varietyOneNum;
	}

	public Double getVarietyOneAmount() {
		return varietyOneAmount;
	}

	public void setVarietyOneAmount(Double varietyOneAmount) {
		this.varietyOneAmount = varietyOneAmount;
	}

	public String getVarietyOneRemark() {
		return varietyOneRemark;
	}

	public void setVarietyOneRemark(String varietyOneRemark) {
		this.varietyOneRemark = varietyOneRemark;
	}

	public Double getVarietyTwoRemain() {
		return varietyTwoRemain;
	}

	public void setVarietyTwoRemain(Double varietyTwoRemain) {
		this.varietyTwoRemain = varietyTwoRemain;
	}

	public Double getVarietyTwoNum() {
		return varietyTwoNum;
	}

	public void setVarietyTwoNum(Double varietyTwoNum) {
		this.varietyTwoNum = varietyTwoNum;
	}

	public Double getVarietyTwoAmount() {
		return varietyTwoAmount;
	}

	public void setVarietyTwoAmount(Double varietyTwoAmount) {
		this.varietyTwoAmount = varietyTwoAmount;
	}

	public String getVarietyTwoRemark() {
		return varietyTwoRemark;
	}

	public void setVarietyTwoRemark(String varietyTwoRemark) {
		this.varietyTwoRemark = varietyTwoRemark;
	}

	public Double getVarietyThrRemain() {
		return varietyThrRemain;
	}

	public void setVarietyThrRemain(Double varietyThrRemain) {
		this.varietyThrRemain = varietyThrRemain;
	}

	public Double getVarietyThrNum() {
		return varietyThrNum;
	}

	public void setVarietyThrNum(Double varietyThrNum) {
		this.varietyThrNum = varietyThrNum;
	}

	public Double getVarietyThrAmount() {
		return varietyThrAmount;
	}

	public void setVarietyThrAmount(Double varietyThrAmount) {
		this.varietyThrAmount = varietyThrAmount;
	}

	public String getVarietyThrRemark() {
		return varietyThrRemark;
	}

	public void setVarietyThrRemark(String varietyThrRemark) {
		this.varietyThrRemark = varietyThrRemark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPeMemo() {
		return peMemo;
	}

	public void setPeMemo(String peMemo) {
		this.peMemo = peMemo;
	}

	public String getVarietyOneDefunct() {
		return varietyOneDefunct;
	}

	public void setVarietyOneDefunct(String varietyOneDefunct) {
		this.varietyOneDefunct = varietyOneDefunct;
	}

	public String getVarietyTwoDefunct() {
		return varietyTwoDefunct;
	}

	public void setVarietyTwoDefunct(String varietyTwoDefunct) {
		this.varietyTwoDefunct = varietyTwoDefunct;
	}

	public String getVarietyThrDefunct() {
		return varietyThrDefunct;
	}

	public void setVarietyThrDefunct(String varietyThrDefunct) {
		this.varietyThrDefunct = varietyThrDefunct;
	}

	public String getPurchaseType() {
		return purchaseType;
	}

	public void setPurchaseType(String purchaseType) {
		this.purchaseType = purchaseType;
	}

	@Override
	public String getDisplayText() {
		return null;
	}

	public String getVarietyOneRelated() {
		return varietyOneRelated;
	}

	public void setVarietyOneRelated(String varietyOneRelated) {
		this.varietyOneRelated = varietyOneRelated;
	}

	public String getVarietyTwoRelated() {
		return varietyTwoRelated;
	}

	public void setVarietyTwoRelated(String varietyTwoRelated) {
		this.varietyTwoRelated = varietyTwoRelated;
	}

	public String getVarietyThrRelated() {
		return varietyThrRelated;
	}

	public void setVarietyThrRelated(String varietyThrRelated) {
		this.varietyThrRelated = varietyThrRelated;
	}

}