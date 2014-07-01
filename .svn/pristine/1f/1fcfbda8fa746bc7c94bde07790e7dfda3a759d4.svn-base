package com.wcs.tms.view.process.common.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.wcs.common.controller.helper.IdModel;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.Bank;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:现金池查询清单Vo</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
public class CashPoolVo extends IdModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// 流程实例编号
	private String procInstId;
	// 填制时间
	private Date applyDate;
	// 申请付款时间
	private Date appPaymentDate;
	// 收款公司
	private Company receiveCompany;
	// 流程名称
	private String processName;
	// 收款银行
	private Bank payeeBank;
	// 申请金额
	private Double fundsTotal;
	//支付方式
	private List<String> payWayList;
	// 拆分付款次数
	private List<Integer> paymentNumberList;
	// 实际付款金额
	private List<Double> realPaymentAmountList;
	// 剩余需支付金额
	private Double lastPaymentAmount;
	//借款/转款
//	private String loanCondition;
	//付款日期
//	private Date paymentDate;
	//用途
//	private String stirFoundsUseDesc;
	//流程状态
//	private String processStatus;
	
	//TMS付款状态
	private List<String> tmsStatusList;
	//TMS付款描述
	private List<String> payDetailList;
	//TMS付款日期
	private List<Date> payDateList;
	//TMS支付参考号
	private List<String> tmsRefNumberList;
	
	private String payWay;
	private Integer paymentNumber;
	private Double realPaymentAmount;
	private String tmsStatus;
	private String payDetail;
	private Date payDate;
	private String tmsRefNumber;

	
	public CashPoolVo(){
		payeeBank = new Bank();
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


//	public String getUnitName() {
//		return unitName;
//	}
//
//	public void setUnitName(String unitName) {
//		this.unitName = unitName;
//	}


	public Double getFundsTotal() {
		return fundsTotal;
	}

	public void setFundsTotal(Double fundsTotal) {
		this.fundsTotal = fundsTotal;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Date getAppPaymentDate() {
		return appPaymentDate;
	}

	public void setAppPaymentDate(Date appPaymentDate) {
		this.appPaymentDate = appPaymentDate;
	}

//	public String getPayeeBankName() {
//		return payeeBankName;
//	}
//
//	public void setPayeeBankName(String payeeBankName) {
//		this.payeeBankName = payeeBankName;
//	}

	public Double getLastPaymentAmount() {
		return lastPaymentAmount;
	}

	public void setLastPaymentAmount(Double lastPaymentAmount) {
		this.lastPaymentAmount = lastPaymentAmount;
	}

	public Company getReceiveCompany() {
		return receiveCompany;
	}

	public void setReceiveCompany(Company receiveCompany) {
		this.receiveCompany = receiveCompany;
	}

	public Bank getPayeeBank() {
		return payeeBank;
	}

	public void setPayeeBank(Bank payeeBank) {
		this.payeeBank = payeeBank;
	}

	public List<String> getPayWayList() {
		return payWayList;
	}

	public void setPayWayList(List<String> payWayList) {
		this.payWayList = payWayList;
	}

	public List<Integer> getPaymentNumberList() {
		return paymentNumberList;
	}

	public void setPaymentNumberList(List<Integer> paymentNumberList) {
		this.paymentNumberList = paymentNumberList;
	}

	public List<Double> getRealPaymentAmountList() {
		return realPaymentAmountList;
	}

	public void setRealPaymentAmountList(List<Double> realPaymentAmountList) {
		this.realPaymentAmountList = realPaymentAmountList;
	}

	public List<String> getTmsStatusList() {
		return tmsStatusList;
	}

	public void setTmsStatusList(List<String> tmsStatusList) {
		this.tmsStatusList = tmsStatusList;
	}

	public List<String> getPayDetailList() {
		return payDetailList;
	}

	public void setPayDetailList(List<String> payDetailList) {
		this.payDetailList = payDetailList;
	}

	public List<Date> getPayDateList() {
		return payDateList;
	}

	public void setPayDateList(List<Date> payDateList) {
		this.payDateList = payDateList;
	}

	public List<String> getTmsRefNumberList() {
		return tmsRefNumberList;
	}

	public void setTmsRefNumberList(List<String> tmsRefNumberList) {
		this.tmsRefNumberList = tmsRefNumberList;
	}

	public String getPayWay() {
		return payWay;
	}

	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}

	public Integer getPaymentNumber() {
		return paymentNumber;
	}

	public void setPaymentNumber(Integer paymentNumber) {
		this.paymentNumber = paymentNumber;
	}

	public Double getRealPaymentAmount() {
		return realPaymentAmount;
	}

	public void setRealPaymentAmount(Double realPaymentAmount) {
		this.realPaymentAmount = realPaymentAmount;
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
	
}
