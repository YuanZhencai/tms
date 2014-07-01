package com.wcs.tms.view.process.common.entity;

import java.io.Serializable;
import java.util.Date;

import com.wcs.common.controller.helper.IdModel;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 流程详细VO</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
public class ProcessDetailVo extends IdModel implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /** 已操作流程步骤名称*/
    private String prossNodeName;
    /** 该流程步骤执行人*/
    private String operatorsName;
    /** 该流程步骤操作时间*/
    private Date operatorTime;
    /** 该流程步骤操作时备注*/
    private String nodeMemo;

    public String getProssNodeName() {
        return prossNodeName;
    }

    public void setProssNodeName(String prossNodeName) {
        this.prossNodeName = prossNodeName;
    }

    public String getOperatorsName() {
        return operatorsName;
    }

    public void setOperatorsName(String operatorsName) {
        this.operatorsName = operatorsName;
    }

    public Date getOperatorTime() {
        return operatorTime;
    }

    public void setOperatorTime(Date operatorTime) {
        this.operatorTime = operatorTime;
    }

    public String getNodeMemo() {
        return nodeMemo;
    }

    public void setNodeMemo(String nodeMemo) {
        this.nodeMemo = nodeMemo;
    }

}
