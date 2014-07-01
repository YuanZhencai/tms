package com.wcs.tms.model;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;


/**
 * 
 * <p>Project: tms</p>
 * <p>Description:流程明细_银行授信额度调剂申请其他产品明细 实体</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Entity
@Table(name = "PROC_RPT_ADJUST_O")
public class ProcRptAdjustO extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROC_ADJUST_ID")
    private ProcBankCreditAdjust procBankCreditAdjust;
	
	@Column(name = "OTHER_NAME", length = 100)
    private String otherName;
	
	@Column(name = "OTHER_LIMIT", precision = 12, scale = 4)
    private Double otherLimit;

    // Constructors

    /** default constructor */
    public ProcRptAdjustO() {
    	procBankCreditAdjust = new ProcBankCreditAdjust();
    }


    
    public ProcBankCreditAdjust getProcBankCreditAdjust() {
        return this.procBankCreditAdjust;
    }

    public void setProcBankCreditAdjust(ProcBankCreditAdjust procBankCreditAdjust) {
        this.procBankCreditAdjust = procBankCreditAdjust;
    }

    public String getOtherName() {
        return this.otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public Double getOtherLimit() {
        return this.otherLimit;
    }

    public void setOtherLimit(Double otherLimit) {
        this.otherLimit = otherLimit;
    }

    @Override
	@Transient
	public String getDisplayText() {
		return null;
	}
    

}