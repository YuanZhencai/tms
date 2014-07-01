package com.wcs.sys.ejbtimer.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the SYS_JOB_LOG database table.
 * 
 */
@Entity
@Table(name = "SYS_JOB_LOG")
public class SysJobLog extends com.wcs.base.model.IdEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /*
     * 任务标题
     */
    @Column(name = "JOB_SUBJECT")
    private String jobSubject;

    /*
     * 任务执行详细日志
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "LOG_DETAIL")
    private String logDetail;

    /*
     * 任务执行开始时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_TIME")
    private Date startTime;

    /*
     * 任务执行结束时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_TIME")
    private Date endTime;

    /*
     * 任务是否执行成功
     */
    @Column(name = "IS_SUCCESS")
    private Boolean isSuccess;
    /*
     * 任务ID
     */
    @ManyToOne
    @JoinColumn(name = "JOB_ID")
    private SysJobInfo sysJobInfo;

    /*
     * 业务日志列表
     */
    // bi-directional many-to-one association to SysBusinessLog
    @OneToMany(mappedBy = "sysJobLog", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    private List<SysBusinessLog> sysBusinessLogs;

    public SysJobLog() {
    }

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public SysJobInfo getSysJobInfo() {
        return sysJobInfo;
    }

    public void setSysJobInfo(SysJobInfo sysJobInfo) {
        this.sysJobInfo = sysJobInfo;
    }

    public String getJobSubject() {
        return this.jobSubject;
    }

    public void setJobSubject(String jobSubject) {
        this.jobSubject = jobSubject;
    }

    public String getLogDetail() {
        return this.logDetail;
    }

    public void setLogDetail(String logDetail) {
        this.logDetail = logDetail;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<SysBusinessLog> getSysBusinessLogs() {
        return this.sysBusinessLogs;
    }

    public void setSysBusinessLogs(List<SysBusinessLog> sysBusinessLogs) {
        this.sysBusinessLogs = sysBusinessLogs;
    }

}