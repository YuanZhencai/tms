package com.wcs.tms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.wcs.base.model.BaseEntity;
@Entity
@Table(name = "REMITTANCE_LINE_ACCOUNT")
public class RemittanceLineAccount extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5438134219705650547L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;
	
	
	@Column(name = "CORR_BENE_BANK_NAME")
	private String corrBeneBankName; 
	
	@Column(name = "SWIFT_CODE1")
	private String swiftCode1; 
	
	@Column(name = "SWIFT_CODE2")
	private String swiftCode2; 
	
	@Column(name = "AC_NO")
	private String acNo; 
	
	@Column(name = "BENE_BANK_NAME")
	private String beneBankName; 
	
	@Column(name = "ACCOUNT_TYPE")
	private String accountType; 
	
	@Column(name = "DEFUNCT_IND")
	private String defunctInd;
	
	
	public Company getCompany() {
		return company;
	}


	public void setCompany(Company company) {
		this.company = company;
	}


	public String getCorrBeneBankName() {
		return corrBeneBankName;
	}


	public void setCorrBeneBankName(String corrBeneBankName) {
		this.corrBeneBankName = corrBeneBankName;
	}


	public String getSwiftCode1() {
		return swiftCode1;
	}


	public void setSwiftCode1(String swiftCode1) {
		this.swiftCode1 = swiftCode1;
	}


	public String getSwiftCode2() {
		return swiftCode2;
	}


	public void setSwiftCode2(String swiftCode2) {
		this.swiftCode2 = swiftCode2;
	}


	public String getAcNo() {
		return acNo;
	}


	public void setAcNo(String acNo) {
		this.acNo = acNo;
	}


	public String getBeneBankName() {
		return beneBankName;
	}


	public void setBeneBankName(String beneBankName) {
		this.beneBankName = beneBankName;
	}


	public String getAccountType() {
		return accountType;
	}


	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}


	public String getDefunctInd() {
		return defunctInd;
	}


	public void setDefunctInd(String defunctInd) {
		this.defunctInd = defunctInd;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	@Override
	public String getDisplayText() {
		// TODO Auto-generated method stub
		return null;
	}

}
