package com.wcs.tms.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.wcs.base.model.BaseEntity;
import com.wcs.common.model.O;
import com.wcs.common.model.Rolemstr;
import com.wcs.common.model.S;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:流程授权实体</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Entity
@Table(name="PROCESS_AUTH"
)
public class ProcessAuth extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESS_ID")
    private ProcessDefine processDefine;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SID")
    private S s;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID")
    private Rolemstr rolemstr;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OID")
    private O o;
	
	
	/**
	 * @return the processDefine
	 */
	public ProcessDefine getProcessDefine() {
		return processDefine;
	}


	/**
	 * @param processDefine the processDefine to set
	 */
	public void setProcessDefine(ProcessDefine processDefine) {
		this.processDefine = processDefine;
	}

	
	public S getS() {
		return s;
	}

	public void setS(S s) {
		this.s = s;
	}


	/**
	 * @return the rolemstr
	 */
	public Rolemstr getRolemstr() {
		return rolemstr;
	}


	/**
	 * @param rolemstr the rolemstr to set
	 */
	public void setRolemstr(Rolemstr rolemstr) {
		this.rolemstr = rolemstr;
	}


	/**
	 * @return the o
	 */
	public O getO() {
		return o;
	}


	/**
	 * @param o the o to set
	 */
	public void setO(O o) {
		this.o = o;
	}


	@Override
	@Transient
	public String getDisplayText() {
		return null;
	}
}
