package com.wcs.tms.view.report.cashpool.vo;

import com.wcs.tms.view.report.cashpool.annotation.CashPoolExcelTitleAnnotation;

public class CashPoolRcfkVo {
	private Long cpId;
	@CashPoolExcelTitleAnnotation(columnNO = "0", titleName = "公司名称")
	private String cpName;
	@CashPoolExcelTitleAnnotation(columnNO = "1", titleName = "总计")
	private Double total;
	@CashPoolExcelTitleAnnotation(columnNO = "2", titleName = "税款")
	private Double tax;
	@CashPoolExcelTitleAnnotation(columnNO = "3", titleName = "辅料/包材/备件")
	private Double packMater;

	private Double sperpart;

	private Double coal;
	@CashPoolExcelTitleAnnotation(columnNO = "4", titleName = "水/电/汽/煤")
	private Double steam;
	@CashPoolExcelTitleAnnotation(columnNO = "5", titleName = "工资/社保/花红")
	private Double salary;
	@CashPoolExcelTitleAnnotation(columnNO = "6", titleName = "运费/港杂费/加工费")
	private Double freight;
	@CashPoolExcelTitleAnnotation(columnNO = "7", titleName = "其他")
	private Double elseProj;
	@CashPoolExcelTitleAnnotation(columnNO = "8", titleName = "小计")
	private Double xiaoji2;
	@CashPoolExcelTitleAnnotation(columnNO = "9", titleName = "营销公司专用")
	private Double marketDedicted;
	@CashPoolExcelTitleAnnotation(columnNO = "10", titleName = "现金池划转")
	private Double transfer;

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

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	public Double getPackMater() {
		return packMater;
	}

	public void setPackMater(Double packMater) {
		this.packMater = packMater;
	}

	public Double getSperpart() {
		return sperpart;
	}

	public void setSperpart(Double sperpart) {
		this.sperpart = sperpart;
	}

	public Double getCoal() {
		return coal;
	}

	public void setCoal(Double coal) {
		this.coal = coal;
	}

	public Double getSteam() {
		return steam;
	}

	public void setSteam(Double steam) {
		this.steam = steam;
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}

	public Double getElseProj() {
		return elseProj;
	}

	public void setElseProj(Double elseProj) {
		this.elseProj = elseProj;
	}

	public Double getFreight() {
		return freight;
	}

	public void setFreight(Double freight) {
		this.freight = freight;
	}

	public Double getXiaoji2() {
		return xiaoji2;
	}

	public void setXiaoji2(Double xiaoji2) {
		this.xiaoji2 = xiaoji2;
	}

	public Double getMarketDedicted() {
		return marketDedicted;
	}

	public void setMarketDedicted(Double marketDedicted) {
		this.marketDedicted = marketDedicted;
	}

	public Double getTransfer() {
		return transfer;
	}

	public void setTransfer(Double transfer) {
		this.transfer = transfer;
	}


}
