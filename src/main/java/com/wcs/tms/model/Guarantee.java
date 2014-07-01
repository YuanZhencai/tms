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
 * <p>Description:担保明细</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Entity
@Table(name = "GUARANTEE")
public class Guarantee extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 受保方
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSURED_COM_ID")
    private Company insuredCom;

    /**
     * 担保方
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SECURED_COM_ID")
    private Company securedCom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BENEFI_BANK_ID")
    private Bank bank;

    @Column(name = "PROC_INST_ID", length = 255)
    private String procInstId;

    @Column(name = "GUAR_AMOUNT", precision = 12, scale = 4)
    private Double guarAmount;

    @Column(name = "GUAR_AMOUNT_CU", length = 101)
    private String guarAmountCu;

    @Column(name = "USE_DESC", length = 1000)
    private String useDesc;

    @Column(name = "SECURED_ASSETS", precision = 12, scale = 4)
    private Double securedAssets;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "GUAR_START")
    private Date guarStart;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "GUAR_END")
    private Date guarEnd;

    /**
     * constructor
     */
    public Guarantee() {
        insuredCom = new Company();
        securedCom = new Company();
        bank = new Bank();
    }

    public Company getInsuredCom() {
        return insuredCom;
    }

    public void setInsuredCom(Company insuredCom) {
        this.insuredCom = insuredCom;
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
}
