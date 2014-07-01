package com.wcs.tms.view.process.common.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.wcs.common.controller.helper.IdModel;
import com.wcs.tms.model.Company;
/**
 * 
 * <p>Project: tms</p>
 * <p>Description:集团内部担保明细表担保方Vo</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
public class GuaranteeVo extends IdModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//担保方
	private Company securedCompany;
//	//担保明细
//	private List<Guarantee> guarantees = new ArrayList<Guarantee>();
//	//担保流程
//	private List<ProcGuarantee> procGuarantees = new ArrayList<ProcGuarantee>();
	//担保数据vo
	private List<GuaranteeReportVo> guaranteeReportVos = new ArrayList<GuaranteeReportVo>();
	//源币种金额小计
	private Double securedAssetsSubtotal;
	//担保总额小计
	private Double guarAmountSubtotal;
	
	public GuaranteeVo(){
		securedCompany = new Company();
	}

	public Company getSecuredCompany() {
		return securedCompany;
	}

	public void setSecuredCompany(Company securedCompany) {
		this.securedCompany = securedCompany;
	}

	public Double getSecuredAssetsSubtotal() {
		return securedAssetsSubtotal;
	}

	public void setSecuredAssetsSubtotal(Double securedAssetsSubtotal) {
		this.securedAssetsSubtotal = securedAssetsSubtotal;
	}

	public Double getGuarAmountSubtotal() {
		return guarAmountSubtotal;
	}

	public void setGuarAmountSubtotal(Double guarAmountSubtotal) {
		this.guarAmountSubtotal = guarAmountSubtotal;
	}

	public List<GuaranteeReportVo> getGuaranteeReportVos() {
		return guaranteeReportVos;
	}

	public void setGuaranteeReportVos(List<GuaranteeReportVo> guaranteeReportVos) {
		this.guaranteeReportVos = guaranteeReportVos;
	}
	
}
