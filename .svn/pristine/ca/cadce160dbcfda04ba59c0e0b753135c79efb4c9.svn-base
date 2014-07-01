package com.wcs.tms.view.system.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;

import com.google.common.collect.Lists;
import com.wcs.base.service.EntityService;
import com.wcs.base.view.ViewBaseBean;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.tms.model.Company;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.util.MessageUtils;


/**
 * <p>Project: tms</p>
 * <p>Description: 公司管理Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@ManagedBean
@ViewScoped
public class CompanyTmsBean extends ViewBaseBean<Company>{

	private static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(CompanyTmsBean.class);
	
	@Inject 
	EntityService entityService;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	CommonBean dictBean;
	
	//查询条件map
	private Map<String, Object> conditionMap = new HashMap<String, Object>();
	//lazymodel
	private LazyDataModel<Company> companyLazyModel;
	//List
	private List<Company> companyList = Lists.newArrayList();
	//公司对象
	private Company company = new Company();
	//sap代码
	private String sapCode;

	private String op = "新增";

	/** 资金币种下拉*/
    private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
    

    /**
	 * <p>Description:Bean init </p>
	 */
	@PostConstruct
	public void initComp(){
		log.info("initCompanyBean~~~~~~~~~~~~~~");
		initDict();
		searchComp(); 
	}

	
	public void initDict(){
	    currencySelect = dictBean.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
	}
	
	
	/**
	 * <p>Description: 查询公司列表</p>
	 */
	public void searchComp(){
		try {
			log.info("查询公司~~~");
			companyLazyModel = companyTmsService.findCompanyLazyDataModle(conditionMap);
		} catch (Exception e) {
			log.error("searchComp方法 查询公司列表", e);
		}
	}
	
	
	/**
	 * <p>Description: 弹出新增dialog</p>
	 */
	public void clear(){
		op = "新增";
		sapCode = null;
		company = new Company();
		sapCode = "";
	}
	
	/**
	 * <p>Description: 弹出修改dialog</p>
	 */
	public void toEdit(){
	    op = "修改";
	    Long companyId = company.getId();
	    company = entityService.find(Company.class, companyId);
	    company.setCorporationFlagEx("Y".equals(company.getCorporationFlag()) ? true : false);
	    sapCode = company.getSapCode();
	}
	
	/**
	 * 检查公司sap代码唯一性
	 * @return
	 */
	public boolean checkSap(){
		if(sapCode!=null && sapCode.equals(company.getSapCode())){
			return true;
		}
		boolean check = companyTmsService.checkSap(company.getSapCode());
		if(!check){
			MessageUtils.addErrorMessage("errorMsg1", "SAP代码已存在！");
		}
		return check;
	}
	
	/**
	 * <p>Description: 公司保存</p>
	 */
	public void saveCom(){
		if(!checkSap()){
			return;
		}
		company.setCorporationFlag(company.isCorporationFlagEx() ? "Y" : "N");
		try {
			companyTmsService.saveOrUpdate(company);
			MessageUtils.addSuccessMessage("msg", "公司信息保存成功！");
			company = new Company();
		} catch (Exception e) {
			MessageUtils.addErrorMessage("errorMsg", "公司信息保存失败！");
			log.error("saveCom方法 公司保存异常：", e);
		}
		searchComp();
	}
	
	
	/**
	 * <p>Description: 公司启用/禁用</p>
	 */
	public void disable(){
		String status = company.getStatus();
		if("Y".equals(status)){
		    //禁用
			company.setStatus("N");
		}else{
		    //启用
			company.setStatus("Y");
		}
		try {
			companyTmsService.saveOrUpdate(company);
			MessageUtils.addSuccessMessage("msg", "公司信息" + ("Y".equals(status) ? "禁用" : "启用") + "成功！");
		} catch (Exception e) {
			MessageUtils.addErrorMessage("msg", "公司信息" + ("Y".equals(status) ? "禁用" : "启用") + "失败！");
		}
	}
	
	
	/******set@get*******************************************************/
    public String getOp() {
        return op;
    }
    public String getSapCode() {
		return sapCode;
	}

	public void setSapCode(String sapCode) {
		this.sapCode = sapCode;
	}

	public void setOp(String op) {
        this.op = op;
    }
	public LazyDataModel<Company> getCompanyLazyModel() {
		return companyLazyModel;
	}
	public void setCompanyLazyModel(LazyDataModel<Company> companyLazyModel) {
		this.companyLazyModel = companyLazyModel;
	}
	public Map<String, Object> getConditionMap() {
		return conditionMap;
	}
	public void setConditionMap(Map<String, Object> conditionMap) {
		this.conditionMap = conditionMap;
	}
	public List<Company> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<Company> companyList) {
		this.companyList = companyList;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

    public List<SelectItem> getCurrencySelect() {
        return currencySelect;
    }
    public void setCurrencySelect(List<SelectItem> currencySelect) {
        this.currencySelect = currencySelect;
    }
}
