package com.wcs.tms.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;


/**
 * 
 * <p>Project: tms</p>
 * <p>Description:流程定义实体</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Entity
@Table(name="PROCESS_DEFINE"
)
public class ProcessDefine extends BaseEntity{
	
	private static final long serialVersionUID = 1L;

	@Column(name="PROCESS_CODE")
	private String processCode;

	@Column(name="PROCESS_NAME")
	private String processName;
	
	@Column(name="PROCESS_MEMO")
	private String processMemo;
	
	@Column(name="STATUS", length=1)
	private String status;
	
	@Column(name="ORDER_NUMBER")
	private Long orderNumber;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="processDefine")
	private List<ProcessNode> processNodes = new ArrayList<ProcessNode>(0);
	
	@Transient
	private Integer processVersion;

	public ProcessDefine() {
	}
	
	

	public Integer getProcessVersion() {
		return processVersion;
	}



	public void setProcessVersion(Integer processVersion) {
		this.processVersion = processVersion;
	}



	public String getProcessCode() {
		return processCode;
	}

	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getProcessMemo() {
		return processMemo;
	}

	public void setProcessMemo(String processMemo) {
		this.processMemo = processMemo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<ProcessNode> getProcessNodes() {
		return processNodes;
	}

	public void setProcessNodes(List<ProcessNode> processNodes) {
		this.processNodes = processNodes;
	}

	



	@Override
	@Transient
	public String getDisplayText() {
		return null;
	}



	public Long getOrderNumber() {
		return orderNumber;
	}



	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
}
