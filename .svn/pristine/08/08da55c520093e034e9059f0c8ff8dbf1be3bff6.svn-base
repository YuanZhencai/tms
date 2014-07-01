package com.wcs.tms.view.report.regicapitalgeneral;

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
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.tms.model.Company;
import com.wcs.tms.service.report.regicapitalgeneral.RegicapitalGeneralReService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.view.process.common.CompanySelectBean;
import com.wcs.tms.view.process.common.entity.ShareHolderVo;

/** 
* <p>Project: tms</p> 
* <p>Title: 注册资本金报表Bean</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
@ManagedBean
@ViewScoped
public class RegicapitalGeneralReBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(RegicapitalGeneralReBean.class);
	
	@Inject
	EntityService entityService;
	@Inject
	LoginService loginService;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	RegicapitalGeneralReService regicapitalGeneralReService;
	// 查询条件
	private Map<String, Object> conditionMap = new HashMap<String, Object>();
	// 公司下拉菜单
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
	// 公司名称的选择列表
	private LazyDataModel<Company> lazyDataModel;
	private LazyDataModel<ShareHolderVo> shareHolderVoModel;
	// 公司股东信息列表VO
	private List<ShareHolderVo> shareHolderVos = new ArrayList<ShareHolderVo>();
	@ManagedProperty(value = "#{companySelectBean}")
	private CompanySelectBean companySelectBean;
	private Company company;
	// 判断是否是集团用户
	private Boolean isCopUser = false;

	/**
	 * <p>Description:工厂用户要执行下拉功能，集团用户是弹框功能 </p>
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
		if (shareHolderVos.size() == 0) {
			this.searchRegiCaitalDetail();
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
	 * @param all
	 */
	public void initCompany(boolean all) {
		if (all) {
			companySelect = companyTmsService.getAllCompanySelect();
		} else {
			companySelect = companyTmsService.getCompanySelect();
		}
	}

	/**
	 * 查询采购资金情况表
	 */
	public void searchRegiCaitalDetail() {
		try {
			if (isCopUser) {
				conditionMap = companySelectBean.getFilterMap(company, conditionMap);
			} else {
				List<String> companyIds = new ArrayList<String>();
				for (SelectItem item : companySelect) {
					companyIds.add(String.valueOf(item.getValue()));
				}
				conditionMap.put("companyIds", companyIds);
			}
			shareHolderVoModel = this.findAllCompany(conditionMap);
		} catch (Exception e) {
			log.error("searchRegiCaitalDetail方法 错误信息：" + e.getMessage());
		}
	}

	/**
	 * 懒加载处理
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public LazyDataModel<ShareHolderVo> findAllCompany(final Map<String, Object> conditionMap) {
		LazyDataModel<ShareHolderVo> lazyDataModel = new LazyDataModel<ShareHolderVo>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<ShareHolderVo> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
				Map<String, Object> map = new HashMap<String, Object>();
				map = regicapitalGeneralReService.findShareHolderBySqLoad(first, pageSize, sortField, sortOrder, conditionMap);
				List<ShareHolderVo> list = new ArrayList<ShareHolderVo>();
				if (map.size() != 0) {
					list = (List<ShareHolderVo>) map.get("list");
					this.setRowCount((Integer) map.get("count"));
				} else {
					// 防止页面没有数据时报错
					this.setRowCount(0);
				}
				return list;
			}
		};
		return lazyDataModel;
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

	public LazyDataModel<Company> getLazyDataModel() {
		return lazyDataModel;
	}

	public void setLazyDataModel(LazyDataModel<Company> lazyDataModel) {
		this.lazyDataModel = lazyDataModel;
	}

	public LazyDataModel<ShareHolderVo> getShareHolderVoModel() {
		return shareHolderVoModel;
	}

	public void setShareHolderVoModel(LazyDataModel<ShareHolderVo> shareHolderVoModel) {
		this.shareHolderVoModel = shareHolderVoModel;
	}

	public List<ShareHolderVo> getShareHolderVos() {
		return shareHolderVos;
	}

	public void setShareHolderVos(List<ShareHolderVo> shareHolderVos) {
		this.shareHolderVos = shareHolderVos;
	}

}