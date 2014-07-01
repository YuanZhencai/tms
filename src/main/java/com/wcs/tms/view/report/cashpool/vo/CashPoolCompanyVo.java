package com.wcs.tms.view.report.cashpool.vo;

import java.math.BigDecimal;

public class CashPoolCompanyVo {
	private Long cpId;
	private String cpName;
	private Double sumAmount;
	private Double sumSjxb;
	private Double sumAvAmount;
	private Double sumSjzf;

	public Long getCpId() {
		return cpId;
	}

	public void setCpId(Long cpId) {
		this.cpId = cpId;
	}

	public String getCpName() {
		return cpName;
	}

	public void setCpName(String cpName) {
		this.cpName = cpName;
	}

	public Double getSumAmount() {
		return sumAmount;
	}

	public void setSumAmount(Double sumAmount) {
		this.sumAmount = sumAmount;
	}

	public Double getSumSjxb() {
		return sumSjxb;
	}

	public void setSumSjxb(Double sumSjxb) {
		this.sumSjxb = sumSjxb;
	}

	public Double getSumAvAmount() {
		return sumAvAmount;
	}

	public void setSumAvAmount(Double sumAvAmount) {
		this.sumAvAmount = sumAvAmount;
	}

	public Double getSumSjzf() {
		if (sumSjxb != null && sumAvAmount != null) {
			BigDecimal bd1 = new BigDecimal(Double.toString(sumSjxb));
			BigDecimal bd2 = new BigDecimal(Double.toString(sumAvAmount/10000));
			sumSjzf = bd1.subtract(bd2).doubleValue();
		}

		return sumSjzf;
	}

	public void setSumSjzf(Double sumSjzf) {
		this.sumSjzf = sumSjzf;
	}

}
