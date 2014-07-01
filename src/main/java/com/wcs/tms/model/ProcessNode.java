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
 * <p>Description:流程节点/表单实体</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Entity
@Table(name = "PROCESS_NODE")
public class ProcessNode extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 节点名称*/
    @Column(name = "NODE_NAME" ,length=255)
    private String nodeName;

    /** 表单标示URL*/
    @Column(name = "NODE_IDENTITY" ,length=255)
    private String nodeIdentity;

    /** 说明*/
    @Column(name = "MEMO" ,length=255)
    private String memo;
    
    /** 是否有效*/
    @Column(name = "STATUS", length=1)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESS_DEFINE_ID")
    private ProcessDefine processDefine;

    public ProcessNode() {
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeIdentity() {
        return nodeIdentity;
    }

    public void setNodeIdentity(String nodeIdentity) {
        this.nodeIdentity = nodeIdentity;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ProcessDefine getProcessDefine() {
        return processDefine;
    }

    public void setProcessDefine(ProcessDefine processDefine) {
        this.processDefine = processDefine;
    }

    @Override
    @Transient
    public String getDisplayText() {
        return null;
    }
}
