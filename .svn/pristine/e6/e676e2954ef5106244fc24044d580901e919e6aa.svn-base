package com.wcs.tms.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 日常付款借款转款流程实体</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
@Entity
@Table(name = "PROC_DAILY_PAY_LOAN_TRAN")
public class ProcDailyPayLoanTran extends BasePayEntity{
	
	/**
     * 
     */
	private static final long serialVersionUID = 1L;
    /** 流程实例ID*/
    @Column(name = "PROC_INST_ID", length=255)
    private String procInstId;
    /**收款人户名*/
    @Column(name = "PAYEE_NAME", length=255)
    private String payeeName;
	/* 税款 */
	@Column(name = "TAX", precision = 12, scale = 4)
	protected Double tax;
	/* 辅料包材 */
	@Column(name = "PACKAGING_MATERIALS", precision = 12, scale = 4)
	protected Double packagingMaterials;
	/* 备品备件 */
	@Column(name = "SPARE_PARTS", precision = 12, scale = 4)
	protected Double spareParts;
	/* 煤炭 */
	@Column(name = "COAL", precision = 12, scale = 4)
	protected Double coal;
	/* 水电汽 */
	@Column(name = "STEAM_ELECTRICITY", precision = 12, scale = 4)
	protected Double steamElectricity;
	/* 工资 */
	@Column(name = "SALARY", precision = 12, scale = 4)
	protected Double slary;
	/* 运费 港杂费*/
	@Column(name = "FREIGHT", precision = 12, scale = 4)
	private Double freight;
	/* 同户名划转*/
	@Column(name = "TRANSFER", precision = 12, scale = 4)
	private Double transfer;
	/* 下拨银行*/
	@Column(name = "TRANSFER_ALLOCATED_BANK", length=10)
	private String transferAllocatedBank;
	/* 营销公司专用*/
	@Column(name = "MARKET_DEDICATED", precision = 12, scale = 4)
	private Double marketDedicated;
	/* 其他 */
	@Column(name = "ELSE_PROJ", precision = 12, scale = 4)
	protected Double elseProj;
    /**流程状态*/
    @Column(name = "PROCESS_STATUS", length=1)
    private String processStatus;
    
    /** 保存PE的备注*/
    @Transient
    private String peMemo;
    
    public ProcDailyPayLoanTran(){
    	this.company = new Company();
    }
    
	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getPayeeName() {
		return payeeName;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}
	
	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	public Double getPackagingMaterials() {
		return packagingMaterials;
	}

	public void setPackagingMaterials(Double packagingMaterials) {
		this.packagingMaterials = packagingMaterials;
	}

	public Double getSpareParts() {
		return spareParts;
	}

	public void setSpareParts(Double spareParts) {
		this.spareParts = spareParts;
	}

	public Double getCoal() {
		return coal;
	}

	public void setCoal(Double coal) {
		this.coal = coal;
	}

	public Double getSteamElectricity() {
		return steamElectricity;
	}

	public void setSteamElectricity(Double steamElectricity) {
		this.steamElectricity = steamElectricity;
	}

	public Double getSlary() {
		return slary;
	}

	public void setSlary(Double slary) {
		this.slary = slary;
	}

	public Double getElseProj() {
		return elseProj;
	}

	public void setElseProj(Double elseProj) {
		this.elseProj = elseProj;
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
	
	@Transient
	@Override
	public String getDisplayText() {
		return null;
	}

	public Double getFreight() {
		return freight;
	}

	public void setFreight(Double freight) {
		this.freight = freight;
	}

	public Double getTransfer() {
		return transfer;
	}

	public void setTransfer(Double transfer) {
		this.transfer = transfer;
	}

	public Double getMarketDedicated() {
		return marketDedicated;
	}

	public void setMarketDedicated(Double marketDedicated) {
		this.marketDedicated = marketDedicated;
	}

	public String getTransferAllocatedBank() {
		return transferAllocatedBank;
	}

	public void setTransferAllocatedBank(String transferAllocatedBank) {
		this.transferAllocatedBank = transferAllocatedBank;
	}

}
