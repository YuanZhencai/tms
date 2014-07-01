package com.wcs.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;
/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 日常付款借款转款流程明细实体</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
@Entity
@Table(name = "PROC_DAILY_PAY_DETAIL")
public class ProcDailyPayDetail extends BaseEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROC_INST_ID")
	/** 流程实例ID*/
	private ProcDailyPayLoanTran proDailyPayLoanTran;
	/** 支付金额*/
	@Column(name = "PAY_FUNDS_TOTAL", precision = 12, scale = 4)
	private Double payFundsTotal;
	/** 支付方式*/
    @Column(name = "PAY_WAY", length=255)
	private String payWay;
    /** 支付时间*/
    @Temporal( TemporalType.TIMESTAMP)
	@Column(name="PAY_DATETIME")
    private Date payDatetime;
    
    /** 保存PE的备注*/
    @Transient
    private String peMemo;
	
    public ProcDailyPayDetail(){
    	proDailyPayLoanTran = new ProcDailyPayLoanTran();
    }
    
	public ProcDailyPayLoanTran getProDailyPayLoanTran() {
		return proDailyPayLoanTran;
	}

	public void setProDailyPayLoanTran(ProcDailyPayLoanTran proDailyPayLoanTran) {
		this.proDailyPayLoanTran = proDailyPayLoanTran;
	}

	public Double getPayFundsTotal() {
		return payFundsTotal;
	}

	public void setPayFundsTotal(Double payFundsTotal) {
		this.payFundsTotal = payFundsTotal;
	}

	public String getPayWay() {
		return payWay;
	}

	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}

	public Date getPayDatetime() {
		return payDatetime;
	}

	public void setPayDatetime(Date payDatetime) {
		this.payDatetime = payDatetime;
	}

	/**
	 * @return the peMemo
	 */
	public String getPeMemo() {
		return peMemo;
	}

	/**
	 * @param peMemo the peMemo to set
	 */
	public void setPeMemo(String peMemo) {
		this.peMemo = peMemo;
	}

	@Override
	public String getDisplayText() {
		return null;
	}
	
}
