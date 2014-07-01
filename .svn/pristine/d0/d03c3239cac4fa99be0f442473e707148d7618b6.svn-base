package com.wcs.tms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.wcs.base.model.BaseEntity;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:流程实例编号映射表</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Entity
@Table(name="PROCESS_MAP"
)
public class ProcessMap extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	/**FN流程实例编号 *********/
	@Column(name="PID_FN", length=255)
	private String pidFn;
	
	/**TMS业务流程实例编号 *********/
	@Column(name="PID_TMS", length=255)
	private String pidTms;
	
	/**流程公司ID *********/
	@Column(name="COMPANY_ID")
	private Long companyId;
	
	/**流程公司中文名 *********/
	@Column(name="COMPANY_NAME", length=255)
	private String companyName;
	
	
	
	public String getPidFn() {
		return pidFn;
	}

	public void setPidFn(String pidFn) {
		this.pidFn = pidFn;
	}

	public String getPidTms() {
		return pidTms;
	}

	public void setPidTms(String pidTms) {
		this.pidTms = pidTms;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Override
	public String getDisplayText() {
		// TODO Auto-generated method stub
		return null;
	}
}
