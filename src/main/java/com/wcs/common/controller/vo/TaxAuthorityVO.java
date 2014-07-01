package com.wcs.common.controller.vo;

import java.io.Serializable;

public class TaxAuthorityVO implements Serializable {
	/**
	 * VO
	 */
	private static final long serialVersionUID = 1L;
	// 页面显示要使用到的数据
	private Long id;
	private String taxName;
	private String taxAddress;
	private String taxPhone;
	private String taxState;
	private String taxZipCode;
	private String leaderName;
	private String leaderPhone;
	private String leaderPosition;
	private String contacterName;
	private String contacterPhone;
	private String contacterPosition;

	public TaxAuthorityVO() {
		// 构造方法
	}

	// 主页上的table显示需用的构造方法
	public TaxAuthorityVO(Long id, String taxName, String taxAddress,
			String taxZipCode, String taxPhone, String taxState,
			String leaderName, String leaderPosition, String leaderPhone,
			String contacterName, String contacterPosition,
			String contacterPhone) {
		this.id = id;
		this.taxName = taxName;
		this.taxAddress = taxAddress;
		this.taxPhone = taxPhone;
		this.taxState = taxState;
		this.taxZipCode = taxZipCode;
		this.contacterName = contacterName;
		this.contacterPhone = contacterPhone;
		this.contacterPosition = contacterPosition;
		this.leaderName = leaderName;
		this.leaderPhone = leaderPhone;
		this.leaderPosition = leaderPosition;
	}

	public String getTaxName() {
		return taxName;
	}

	public void setTaxName(String taxName) {
		this.taxName = taxName;
	}

	public String getTaxAddress() {
		return taxAddress;
	}

	public void setTaxAddress(String taxAddress) {
		this.taxAddress = taxAddress;
	}

	public String getTaxPhone() {
		return taxPhone;
	}

	public void setTaxPhone(String taxPhone) {
		this.taxPhone = taxPhone;
	}

	public String getTaxState() {
		return taxState;
	}

	public void setTaxState(String taxState) {
		this.taxState = taxState;
	}

	public String getTaxZipCode() {
		return taxZipCode;
	}

	public void setTaxZipCode(String taxZipCode) {
		this.taxZipCode = taxZipCode;
	}

	public String getLeaderName() {
		return leaderName;
	}

	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}

	public String getLeaderPhone() {
		return leaderPhone;
	}

	public void setLeaderPhone(String leaderPhone) {
		this.leaderPhone = leaderPhone;
	}

	public String getLeaderPosition() {
		return leaderPosition;
	}

	public void setLeaderPosition(String leaderPosition) {
		this.leaderPosition = leaderPosition;
	}

	public String getContacterName() {
		return contacterName;
	}

	public void setContacterName(String contacterName) {
		this.contacterName = contacterName;
	}

	public String getContacterPhone() {
		return contacterPhone;
	}

	public void setContacterPhone(String contacterPhone) {
		this.contacterPhone = contacterPhone;
	}

	public String getContacterPosition() {
		return contacterPosition;
	}

	public void setContacterPosition(String contacterPosition) {
		this.contacterPosition = contacterPosition;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
