package com.wcs.tms.view.process.common.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.wcs.common.controller.helper.IdModel;
import com.wcs.tms.model.ShareHolder;

/** 
* <p>Project: tms</p> 
* <p>Title: 注册资本金Vo</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
public class RegiCapitalVo extends IdModel implements Serializable {

	private static final long serialVersionUID = 1L;

	// 申请日期
	private Date applyDate;
	// 流程实例Id
	private String procInstId;
	// 公司
	private Long companyId;
	// 付款方(资金提供方)
	private Long payer;
	// 申请币别
	private String applyCu;
	// 申请金额
	private Double applyAmount;
	// 用途
	private String useDesc;
	// 公司投资币别
	private String companyCu;
	// 公司投资金额
	private Double companyAmount;
	// 注册资本（计算所得）
	private Double regiCapi;
	// 备注
	private String remark;
	// 股东信息
	private List<ShareHolder> shareHolder;

	public RegiCapitalVo() {

	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getPayer() {
		return payer;
	}

	public void setPayer(Long payer) {
		this.payer = payer;
	}

	public String getApplyCu() {
		return applyCu;
	}

	public void setApplyCu(String applyCu) {
		this.applyCu = applyCu;
	}

	public Double getApplyAmount() {
		return applyAmount;
	}

	public void setApplyAmount(Double applyAmount) {
		this.applyAmount = applyAmount;
	}

	public String getUseDesc() {
		return useDesc;
	}

	public void setUseDesc(String useDesc) {
		this.useDesc = useDesc;
	}

	public String getCompanyCu() {
		return companyCu;
	}

	public void setCompanyCu(String companyCu) {
		this.companyCu = companyCu;
	}

	public Double getCompanyAmount() {
		return companyAmount;
	}

	public void setCompanyAmount(Double companyAmount) {
		this.companyAmount = companyAmount;
	}

	public Double getRegiCapi() {
		return regiCapi;
	}

	public void setRegiCapi(Double regiCapi) {
		this.regiCapi = regiCapi;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<ShareHolder> getShareHolder() {
		return shareHolder;
	}

	public void setShareHolder(List<ShareHolder> shareHolder) {
		this.shareHolder = shareHolder;
	}

}