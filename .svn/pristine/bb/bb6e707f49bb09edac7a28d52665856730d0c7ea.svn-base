package com.wcs.tms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:银行授信集团申请确认其他产授信产品 </p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Entity
@Table(name = "PROC_RPT_BANK_CREDIT_BLOC_CONFIRM")
public class ProcRptBankCreditBlocConfirm extends BaseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // Fields

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BLOC_COMPANY_CONFIRM_ID")
    private ProcBankCreditBlocCompanyConfirm procBankCreditBlocCompanyConfirm;
    @Column(name = "CD_PRO_NAME", length = 100)
    private String cdProName;
    @Column(name = "CD_PRO_LIMIT", precision = 12, scale = 4)
    private Double cdProLimit;

    // Constructors

    /** default constructor */
    public ProcRptBankCreditBlocConfirm() {
    }

    public ProcBankCreditBlocCompanyConfirm getProcBankCreditBlocCompanyConfirm() {
        return this.procBankCreditBlocCompanyConfirm;
    }

    public void setProcBankCreditBlocCompanyConfirm(ProcBankCreditBlocCompanyConfirm procBankCreditBlocCompanyConfirm) {
        this.procBankCreditBlocCompanyConfirm = procBankCreditBlocCompanyConfirm;
    }

    public String getCdProName() {
        return this.cdProName;
    }

    public void setCdProName(String cdProName) {
        this.cdProName = cdProName;
    }

    public Double getCdProLimit() {
        return this.cdProLimit;
    }

    public void setCdProLimit(Double cdProLimit) {
        this.cdProLimit = cdProLimit;
    }

    @Transient
    @Override
    public String getDisplayText() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((cdProName == null) ? 0 : cdProName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
        	return true;
        }
        if (!super.equals(obj)) {
        	return false;
        }
        if (getClass() != obj.getClass()) {
        	return false;
        }
        ProcRptBankCreditBlocConfirm other = (ProcRptBankCreditBlocConfirm) obj;
        if (cdProName == null) {
            if (other.cdProName != null) {
            	return false;
            }
        } else if (!cdProName.equals(other.cdProName)) {
        	return false;
        }
        return true;
    }

}