package com.wcs.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.wcs.base.model.IdEntity;

@Entity
@Table(name = "COMPANY_ACCOUNT_BALANCE")
public class CompanyAccountBalance  implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACCOUNT_BALANCE_SEQ")
	@SequenceGenerator(name = "ACCOUNT_BALANCE_SEQ", sequenceName = "ACCOUNT_BALANCE_SEQ", allocationSize = 20)
	private Long id;
	
	@Column(name = "ACCOUNT", length = 255)
    private String account;
	
	@Column(name = "AVAILABLE_AMOUNT", precision = 16, scale = 2)
    private Double availableAmount;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TRANS_DATETIME")
	private Date transDatetime;
	
	@Column(name = "FTP_FLAG", length=1)
	private String ftpFlag = "N";
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATETIME")
	private Date createdDatetime;

	@Column(name = "CREATED_BY", length = 50)
    private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_DATETIME")
    private Date updatedDatetime;

	@Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

	@Column(name = "DEFUNCT_IND", length=1)
	private String defunctInd = "N";
	
	public CompanyAccountBalance() {
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Date getTransDatetime() {
		return transDatetime;
	}

	public void setTransDatetime(Date transDatetime) {
		this.transDatetime = transDatetime;
	}

	public Double getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(Double availableAmount) {
		this.availableAmount = availableAmount;
	}

	public Date getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getUpdatedDatetime() {
		return updatedDatetime;
	}

	public void setUpdatedDatetime(Date updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getDefunctInd() {
		return defunctInd;
	}

	public void setDefunctInd(String defunctInd) {
		this.defunctInd = defunctInd;
	}
	
	@PrePersist
    public void initTimeStamps() {
        // we do this for the purpose of the demo, this lets us create our own
        // creation dates. Typically we would just set the createdOn field.
        if (createdDatetime == null) {
            createdDatetime = new Date();
        }
        updatedDatetime = createdDatetime;
        defunctInd = "N";
    }

    @PreUpdate
    public void updateTimeStamp() {
        updatedDatetime = new Date();
    }

	public String getFtpFlag() {
		return ftpFlag;
	}

	public void setFtpFlag(String ftpFlag) {
		this.ftpFlag = ftpFlag;
	}

}
