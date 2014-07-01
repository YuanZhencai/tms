package com.wcs.tms.view.process.common.entity;

import java.io.Serializable;
import java.util.Date;

import com.wcs.common.controller.helper.IdModel;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.Company;
/**
 * 
 * <p>Project: tms</p>
 * <p>Description:集团内部担保明细Vo</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
public class GuaranteeReportVo extends IdModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	//填报日期
	private Date createdDatetime;
	//流程实例编号
	private String procInstId;
	//担保方
	private Company securedCom; 
	//银行
	private Bank bank;
	//担保用途
	private String useDesc;
	//受保方
	private Company insuredCom;
	//币种
	private String guarAmountCu;
	//源币种金额
	private Double securedAssets;
	//担保总额
	private Double guarAmount;
	//生效日期
	private Date guarStart;
	//终止日期
	private Date guarEnd;
	//担保预担保标识
	private String securedIdent;
	
	public GuaranteeReportVo() {
		insuredCom = new Company();
        securedCom = new Company();
        bank = new Bank();
	}
	
	public Date getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public Company getSecuredCom() {
		return securedCom;
	}

	public void setSecuredCom(Company securedCom) {
		this.securedCom = securedCom;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public String getUseDesc() {
		return useDesc;
	}

	public void setUseDesc(String useDesc) {
		this.useDesc = useDesc;
	}

	public Company getInsuredCom() {
		return insuredCom;
	}

	public void setInsuredCom(Company insuredCom) {
		this.insuredCom = insuredCom;
	}

	public String getGuarAmountCu() {
		return guarAmountCu;
	}

	public void setGuarAmountCu(String guarAmountCu) {
		this.guarAmountCu = guarAmountCu;
	}

	public Double getSecuredAssets() {
		return securedAssets;
	}

	public void setSecuredAssets(Double securedAssets) {
		this.securedAssets = securedAssets;
	}

	public Double getGuarAmount() {
		return guarAmount;
	}

	public void setGuarAmount(Double guarAmount) {
		this.guarAmount = guarAmount;
	}

	public Date getGuarStart() {
		return guarStart;
	}

	public void setGuarStart(Date guarStart) {
		this.guarStart = guarStart;
	}

	public Date getGuarEnd() {
		return guarEnd;
	}

	public void setGuarEnd(Date guarEnd) {
		this.guarEnd = guarEnd;
	}

	public String getSecuredIdent() {
		return securedIdent;
	}

	public void setSecuredIdent(String securedIdent) {
		this.securedIdent = securedIdent;
	}

	

}
