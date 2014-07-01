package com.wcs.sys.ejbtimer.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the SYS_BUSINESS_LOG database table.
 * 
 */
@Entity
@Table(name = "SYS_BUSINESS_LOG")
public class SysBusinessLog extends com.wcs.base.model.IdEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /*
     * 业务日志信息
     */
    @Column(name = "BUSI_LOG_INFO")
    private String busiLogInfo;

    /*
     * 业务日志描述
     */
    @Column(name = "BUSI_LOG_DESC")
    private String busiLogDesc;

    /*
     * 业务日志级别
     */
    @Column(name = "BUSI_LOG_LEVEL")
    private String busiLogLevel;

    /*
     * 业务日志创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATETIME")
    private Date createdDatetime;

    /*
     * 任务日志ID
     */
    // bi-directional many-to-one association to SysJobLog
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LOG_ID")
    private SysJobLog sysJobLog;

    public SysBusinessLog() {
    }

    public String getBusiLogDesc() {
        return this.busiLogDesc;
    }

    public void setBusiLogDesc(String busiLogDesc) {
        this.busiLogDesc = busiLogDesc;
    }

    public String getBusiLogInfo() {
        return this.busiLogInfo;
    }

    public void setBusiLogInfo(String busiLogInfo) {
        this.busiLogInfo = busiLogInfo;
    }

    public String getBusiLogLevel() {
        return this.busiLogLevel;
    }

    public void setBusiLogLevel(String busiLogLevel) {
        this.busiLogLevel = busiLogLevel;
    }

    public Date getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public SysJobLog getSysJobLog() {
        return this.sysJobLog;
    }

    public void setSysJobLog(SysJobLog sysJobLog) {
        this.sysJobLog = sysJobLog;
    }

}