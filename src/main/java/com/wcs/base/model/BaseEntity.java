package com.wcs.base.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


/**
 * 基本的抽象实体父类。
 * 它实现了公共的主键 Id 、创建时间和更新时间。并自动生成创建时间和更新时间。
 * 同时，它还实现了基于主键 Id 的 hashcode 和 equals 方法，可以用来比较两个对象。
 * <p/>
 * 该类实现了一个抽象的方法 getDisplayText() ， 该方法提供了一个描述该类的入口，
 * 用户可以根据自己的要求实现。
 *
 * @author Chris Guan
 * 
 */
@MappedSuperclass
public abstract class BaseEntity extends IdEntity {

	private static final long serialVersionUID = 1L;

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

    public Date getCreatedDatetime() {
        return createdDatetime;
    }

    
    public String getCreatedBy() {
        return createdBy;
    }

    
    public Date getUpdatedDatetime() {
        return updatedDatetime;
    }

    
    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getDefunctInd() {
		return defunctInd;
	}


	public void setDefunctInd(String defunctInd) {
		this.defunctInd = defunctInd;
	}


	public void setUpdatedDatetime(Date updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Transient
    public abstract String getDisplayText();

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

}
