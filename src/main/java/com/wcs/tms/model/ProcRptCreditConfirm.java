package com.wcs.tms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 流程确认明细_其他授信产品确认单</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Entity
@Table(name = "PROC_RPT_CREDIT_CONFIRM")
public class ProcRptCreditConfirm extends BaseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /** 流程实例ID*/
    @Column(name = "PROC_INST_ID")
    private String procInstId;
    /** 其他授信产品*/
    @Column(name = "CD_PRO_NAME", length = 100)
    private String cdProName;
    /** 其他授信产品额度*/
    @Column(name = "CD_PRO_LIMIT", precision = 12, scale = 4)
    private Double cdProLimit;

    public ProcRptCreditConfirm() {
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
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
        ProcRptCreditConfirm other = (ProcRptCreditConfirm) obj;
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