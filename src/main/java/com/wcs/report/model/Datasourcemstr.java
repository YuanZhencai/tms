package com.wcs.report.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the DATASOURCEMSTR database table.
 * 
 */
@Entity
@Table(name="DATASOURCEMSTR")
public class Datasourcemstr implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="DATASOURCEMSTR_ID", unique=true, nullable=false)
	private long datasourcemstrId;

	@Column(name="CONNECT_URL", length=2000)
	private String connectUrl;

	@Column(name="CREATED_BY", length=50)
	private String createdBy;

	@Column(name="CREATED_DATETIME")
	private Timestamp createdDatetime;

	@Column(name="DATASOURCE_CODE", length=200)
	private String datasourceCode;

	@Column(name="DATASOURCE_DESC", length=1000)
	private String datasourceDesc;

	@Column(name="DATASOURCE_TYPE", length=8)
	private String datasourceType;

	@Column(name="DEFUNCT_IND", length=1)
	private String defunctInd;

	@Column(name="DRIVER_CLASSNAME", length=200)
	private String driverClassname;

	@Column(name="PASSWORD", length=200)
	private String password;

	@Column(length=2000)
	private String remarks;

	@Column(name="UPDATED_BY", length=50)
	private String updatedBy;

	@Column(name="UPDATED_DATETIME")
	private Timestamp updatedDatetime;

	@Column(length=200)
	private String username;

	//bi-directional many-to-one association to RptReportmstr
	@OneToMany(mappedBy="datasourcemstr")
	private List<RptReportmstr> rptReportmstrs;

    public Datasourcemstr() {
    }

	public long getDatasourcemstrId() {
		return this.datasourcemstrId;
	}

	public void setDatasourcemstrId(long datasourcemstrId) {
		this.datasourcemstrId = datasourcemstrId;
	}

	public String getConnectUrl() {
		return this.connectUrl;
	}

	public void setConnectUrl(String connectUrl) {
		this.connectUrl = connectUrl;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public String getDatasourceCode() {
		return this.datasourceCode;
	}

	public void setDatasourceCode(String datasourceCode) {
		this.datasourceCode = datasourceCode;
	}

	public String getDatasourceDesc() {
		return this.datasourceDesc;
	}

	public void setDatasourceDesc(String datasourceDesc) {
		this.datasourceDesc = datasourceDesc;
	}

	public String getDatasourceType() {
		return this.datasourceType;
	}

	public void setDatasourceType(String datasourceType) {
		this.datasourceType = datasourceType;
	}

	public String getDefunctInd() {
		return this.defunctInd;
	}

	public void setDefunctInd(String defunctInd) {
		this.defunctInd = defunctInd;
	}

	public String getDriverClassname() {
		return this.driverClassname;
	}

	public void setDriverClassname(String driverClassname) {
		this.driverClassname = driverClassname;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Timestamp getUpdatedDatetime() {
		return this.updatedDatetime;
	}

	public void setUpdatedDatetime(Timestamp updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<RptReportmstr> getRptReportmstrs() {
		return this.rptReportmstrs;
	}

	public void setRptReportmstrs(List<RptReportmstr> rptReportmstrs) {
		this.rptReportmstrs = rptReportmstrs;
	}
	
}