package com.wcs.tms.view.report.purchaseFund;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.vo.DictVO;
import com.wcs.common.service.DictService;
import com.wcs.tms.model.Company;
import com.wcs.tms.service.report.purchaseFund.PurchaseFundReportService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.view.process.common.CompanySelectBean;
import com.wcs.tms.view.process.common.entity.PurchaseFundVo;

/** 
* <p>Project: tms</p> 
* <p>Title: PurchaseFundReportBean.java</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
@ManagedBean
@ViewScoped
public class PurchaseFundReportBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(PurchaseFundReportBean.class);
	
	@Inject
	EntityService entityService;
	@Inject
	LoginService loginService;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	PurchaseFundReportService purchaseFundReportService;
	@Inject
	DictService dictService;
	
	// 查询条件
	private Map<String, Object> conditionMap = new HashMap<String, Object>();
	// 公司下拉菜单
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
	// 流程列表VO
	private List<PurchaseFundVo> purchaseFundVos = new ArrayList<PurchaseFundVo>();
	@ManagedProperty(value = "#{companySelectBean}")
	private CompanySelectBean companySelectBean;
	private Company company;
	// 判断是否是集团用户
	private Boolean isCopUser = false;

	/**
	 * <p>Description:Bean init </p>
	 */
	@PostConstruct
	public void init() {
		if (loginService.isCopUser()) {
			log.info("是集团用户");
			companySelectBean.initCompany();
			setIsCopUser(true);
		} else {
			log.info("不是集团用户");
			initCompany(false);
			setIsCopUser(false);
		}
		if (purchaseFundVos.size() == 0) {
			this.searchPurchaseDetail();
		}
	}

	public void clear() {
		log.info("页面bean清空缓存");
		company = new Company();
		companySelectBean.clear();
	}

	public void reset() {
		log.info("重置");
		conditionMap = new HashMap<String, Object>();
		company = new Company();
	}

	/**
	 * 页面将选择的公司传到公司名称框中
	 */
	public void getSelectedCompany() {
		company = companySelectBean.getSelectedCompany();
		log.info("*****companyName********" + company.getCompanyName());
	}

	/**
	 * 初始化公司下拉
	 */
	public void initCompany(boolean all) {
		if (all) {
			companySelect = companyTmsService.getAllCompanySelect();
		} else {
			companySelect = companyTmsService.getCompanySelect();
		}
	}

	public String getVarietyName(String keyValue) {
		List<DictVO> dictVOs = dictService.searchData(DictConsts.TMS_FARM_PRODUCE_VARIETY_TYPE, keyValue, "", "", "");
		// 确定用户所选品种类型cat
		return dictVOs.get(0).getCodeVal();
	}
	
	/**
	 * 查询采购资金情况表
	 */
	public void searchPurchaseDetail() {
		try {
			if (isCopUser == true) {
				conditionMap = companySelectBean.getFilterMap(company, conditionMap);
			} else {
				List<String> companyIds = new ArrayList<String>();
				for (SelectItem item : companySelect) {
					companyIds.add(String.valueOf(item.getValue()));
				}
				conditionMap.put("companyIds", companyIds);
				log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				for (String s : companyIds) {
					log.info("ssssssssssssssssssss:" + s);
				}
			}
			purchaseFundVos = purchaseFundReportService.getPurchaseFundVos(conditionMap);
		} catch (Exception e) {
			log.error("searchPurchaseDetail方法 错误信息" + e.getMessage());
		}
	}

	/**
	 * 根据公司ID获得公司名称
	 * @param d
	 * @return
	 */
	public String getCompanyName(Long d) {
		StringBuilder jpql = new StringBuilder("select c from Company c where c.defunctInd = 'N' and c.status = 'Y'");
		jpql.append(" and c.id =" + d);
		Company c = entityService.findUnique(jpql.toString());
		return c.getCompanyName();
	}

	/**************************setter、getter方法*************************/
	public EntityService getEntityService() {
		return entityService;
	}

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}

	public CompanyTmsService getCompanyTmsService() {
		return companyTmsService;
	}

	public void setCompanyTmsService(CompanyTmsService companyTmsService) {
		this.companyTmsService = companyTmsService;
	}

	public Map<String, Object> getConditionMap() {
		return conditionMap;
	}

	public void setConditionMap(Map<String, Object> conditionMap) {
		this.conditionMap = conditionMap;
	}

	public List<SelectItem> getCompanySelect() {
		return companySelect;
	}

	public void setCompanySelect(List<SelectItem> companySelect) {
		this.companySelect = companySelect;
	}

	public List<PurchaseFundVo> getPurchaseFundVos() {
		return purchaseFundVos;
	}

	public void setPurchaseFundVos(List<PurchaseFundVo> purchaseFundVos) {
		this.purchaseFundVos = purchaseFundVos;
	}

	public CompanySelectBean getCompanySelectBean() {
		return companySelectBean;
	}

	public void setCompanySelectBean(CompanySelectBean companySelectBean) {
		this.companySelectBean = companySelectBean;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Boolean getIsCopUser() {
		return isCopUser;
	}

	public void setIsCopUser(Boolean isCopUser) {
		this.isCopUser = isCopUser;
	}

}