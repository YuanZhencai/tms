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
 * <p>Description:流程_内部担保申请</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">LiuShengbin</a>
 */
@Entity
@Table(name = "PROC_GUARANTEE")
public class ProcGuarantee extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /* 流程实例ID */
    @Column(name = "PROC_INST_ID", length = 255)
    private String procInstId;

    /* 银行授信申请流程实例ID */
    @Column(name = "CRDIT_PROC_INST_ID", length = 255)
    private String crditProcInstId;
    /*
     * 申请方公司
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSURED_COM_ID")
    private Company company;

    /*
     * 担保方公司
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SECURED_COM_ID")
    private Company securedCom;

    /*
     * 受益人支行ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BENEFI_BANK_ID")
    private Bank bank;

    /*
     * 申请担保金额
     */
    @Column(name = "APPLY_GUAR_AMOUNT", precision = 12, scale = 4)
    private Double applyGuarAmount;
    /*
     * 本次担保金额
     */
    @Column(name = "GUAR_AMOUNT", precision = 12, scale = 4)
    private Double guarAmount;
    
    /*
     * 本次担保金额
     */
    @Column(name = "ALL_TOTAL_GUAR_AMOUNT", precision = 12, scale = 4)
    private Double allTotalGuarAmount;
    
    /*
     * 是否为新增担保
     */
    @Column(name = "IS_NEW_GUARANTEE", length = 1)
    private String isNewGuarantee;

    /*
     * 实际担保金额
     */
    @Column(name = "REAL_GUAR_AMOUNT", precision = 12, scale = 4)
    private Double realGuarAmount;

    /*
     * 申请担保币别
     */
    @Column(name = "GUAR_AMOUNT_CU", length = 101)
    private String guarAmountCu;

    /*
     * 担保期限（月份）
     */
    @Column(name = "LIMIT_MONTH", length = 10)
    private String limitMonth;
    
    /*
     * 担保期限（月份）
     */
    @Column(name = "CREDITOR_LIMIT_MONTH", length = 10)
    private String creditorLimitMonth;
    

    /*
     * 用途
     */
    @Column(name = "USE_DESC", length = 1000)
    private String useDesc;

    /*
     * 担保方净资产
     */
    @Column(name = "SECURED_ASSETS", precision = 12, scale = 4)
    private Double securedAssets;

    /*
     * 担保起始日期
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "GUAR_START")
    private Date guarStart;

    /*
     * 担保结束日期
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "GUAR_END")
    private Date guarEnd;

    /*
     * 流程状态 状态: 1 -- 审批中 , 2 -- 集团资金部审批完到集团财务总监审批完之间, 3 -- 经过【集团财务总监审批】并同意的担保 4 -- 审批结束
     */
    @Column(name = "STATUS", length = 1)
    private String status = "1";

    /** 审批备注 */
    @Transient
    private String peMemo;

    /**
     * constructor
     */
    public ProcGuarantee() {
        company = new Company();
        securedCom = new Company();
        bank = new Bank();
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public Double getGuarAmount() {
        return guarAmount;
    }

    public void setGuarAmount(Double guarAmount) {
        this.guarAmount = guarAmount;
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

    public String getCrditProcInstId() {
        return crditProcInstId;
    }

    public void setCrditProcInstId(String crditProcInstId) {
        this.crditProcInstId = crditProcInstId;
    }

    public Double getApplyGuarAmount() {
        return applyGuarAmount;
    }

    public void setApplyGuarAmount(Double applyGuarAmount) {
        this.applyGuarAmount = applyGuarAmount;
    }

    public Double getRealGuarAmount() {
        return realGuarAmount;
    }

    public void setRealGuarAmount(Double realGuarAmount) {
        this.realGuarAmount = realGuarAmount;
    }

    public String getLimitMonth() {
        return limitMonth;
    }

    public void setLimitMonth(String limitMonth) {
        this.limitMonth = limitMonth;
    }

    public String getUseDesc() {
        return useDesc;
    }

    public void setUseDesc(String useDesc) {
        this.useDesc = useDesc;
    }

    @Override
    @Transient
    public String getDisplayText() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPeMemo() {
        return peMemo;
    }

    public void setPeMemo(String peMemo) {
        this.peMemo = peMemo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public String getIsNewGuarantee() {
		return isNewGuarantee;
	}

	public void setIsNewGuarantee(String isNewGuarantee) {
		this.isNewGuarantee = isNewGuarantee;
	}

	public String getCreditorLimitMonth() {
		return creditorLimitMonth;
	}

	public void setCreditorLimitMonth(String creditorLimitMonth) {
		this.creditorLimitMonth = creditorLimitMonth;
	}

	public Double getAllTotalGuarAmount() {
		return allTotalGuarAmount;
	}

	public void setAllTotalGuarAmount(Double allTotalGuarAmount) {
		this.allTotalGuarAmount = allTotalGuarAmount;
	}

}
