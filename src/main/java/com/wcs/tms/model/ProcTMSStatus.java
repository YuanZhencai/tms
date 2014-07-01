package com.wcs.tms.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TMS回传表
 * 
 * @author zhuxinyi
 * 
 */
@Entity
@Table(name = "PROC_TMS_STATUS")
public class ProcTMSStatus implements Serializable {

	private static final long serialVersionUID = 3033324859024541080L;
	// 支付编号
	@Id
	@Column(name = "BPM_ID",unique=true, nullable=false)
	private String bpmId;
	// 支付表ID
	@Column(name = "PAY_ID")
	private Long payId;
	// TMS状态(1. 未导入 2. 导入成功 3. 导入失败 4. 支付成功 5. 支付失败)
	@Column(name = "TMS_STATUS")
	private String tmsStatus;
	// 付款描述
	@Column(name = "PAY_DETAIL")
	private String payDetail;
	// 结算时间
	@Column(name = "PAY_DATE")
	private Date payDate;
	// TMS结算参考号
	@Column(name = "TMS_REF_NUMBER")
	private String tmsRefNumber;
	// 申请付款时间
	@Column(name = "APPLICATION_DATE")
	private Date applicationDate;
	// 币种
	@Column(name = "TAX_TYPE")
	private String taxType;
	// TMS支付金额
	@Column(name = "TMS_AMOUNT")
	private Double tmsAmount;

	public String getBpmId() {
		return bpmId;
	}

	public void setBpmId(String bpmId) {
		this.bpmId = bpmId;
	}

	public Long getPayId() {
		return payId;
	}

	public void setPayId(Long payId) {
		this.payId = payId;
	}

	public String getTmsStatus() {
		return tmsStatus;
	}

	public void setTmsStatus(String tmsStatus) {
		this.tmsStatus = tmsStatus;
	}

	public String getPayDetail() {
		return payDetail;
	}

	public void setPayDetail(String payDetail) {
		this.payDetail = payDetail;
	}

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public String getTmsRefNumber() {
		return tmsRefNumber;
	}

	public void setTmsRefNumber(String tmsRefNumber) {
		this.tmsRefNumber = tmsRefNumber;
	}

	public Date getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(Date applicationDate) {
		this.applicationDate = applicationDate;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public Double getTmsAmount() {
		return tmsAmount;
	}

	public void setTmsAmount(Double tmsAmount) {
		this.tmsAmount = tmsAmount;
	}
}
