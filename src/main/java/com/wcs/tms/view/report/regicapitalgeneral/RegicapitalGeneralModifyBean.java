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
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.StringUtils;
import com.wcs.tms.model.Company;
import com.wcs.tms.service.report.regicapitalgeneral.RegicapitalGeneralModifyService;
import com.wcs.tms.service.report.regicapitalgeneral.RegicapitalGeneralReService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.view.process.common.CompanySelectBean;
import com.wcs.tms.view.process.common.entity.RegicapitalMofifyVO;
import com.wcs.tms.view.process.common.entity.ShareHolderVo;

/** 
* <p>Project: tms</p> 
* <p>Title: 注册资本金变更明细Bean</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:guyanyu@wcs-global.com">Gu yanyu</a> 
*/
@ManagedBean
@ViewScoped
public class RegicapitalGeneralModifyBean implements Serializable {
	private static final long serialVersionUID = 1L;
	 
	private Log log = LogFactory.getLog(RegicapitalGeneralModifyBean.class);
	
	@Inject
	EntityService entityService;
	@Inject
	LoginService loginService;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	RegicapitalGeneralModifyService regicapitalGeneralModifyService;
	// 查询条件
	private Map<String, Object> conditionMap = new HashMap<String, Object>();
	// 公司下拉菜单
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
	
	private LazyDataModel<Company> lazyDataModel;
	
	private LazyDataModel<RegicapitalMofifyVO> regicapitalMofifyVOModel;
	
	private List<RegicapitalMofifyVO> regicapitalMofifyVOs = new ArrayList<RegicapitalMofifyVO>();
	
	@ManagedProperty(value = "#{companySelectBean}")
	private CompanySelectBean companySelectBean;
	
	private Company company;
	
	// 判断是否是集团用户
	private Boolean isCopUser = false;

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
		if (regicapitalMofifyVOs.size() == 0) {
			this.searchRegiCaitalModifyDetail();
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
	 * 查询注册资本金变更明细
	 */
	public void searchRegiCaitalModifyDetail() {
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
			regicapitalMofifyVOModel = this.findRegiCaitalMofidyDetail(conditionMap);
		} catch (Exception e) {
			log.error("searchRegiCaitalDetail方法 错误信息：" + e.getMessage());
		}
	}

	/**
	 * 查询报表
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public LazyDataModel<RegicapitalMofifyVO> findRegiCaitalMofidyDetail(final Map<String, Object> conditionMap) {
		LazyDataModel<RegicapitalMofifyVO> lazyDataModel = new LazyDataModel<RegicapitalMofifyVO>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<RegicapitalMofifyVO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
				Map<String, Object> map = new HashMap<String, Object>();
				map = regicapitalGeneralModifyService.findRegiCaitalMofidyDetail(first, pageSize, sortField, sortOrder, conditionMap);
				List<RegicapitalMofifyVO> list = new ArrayList<RegicapitalMofifyVO>();
				if (map.size() != 0) {
					list = (List<RegicapitalMofifyVO>) map.get("list");
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

	public void toViewDetail(String procInstId) {
		procInstId = procInstId == null ? "" : procInstId;
		JSFUtils.getRequest().setAttribute("op", "view");
		JSFUtils.getRequest().setAttribute("menu2", JSFUtils.getParamValue("menu2"));
		JSFUtils.getRequest().setAttribute("procInstId", procInstId);
		JSFUtils.getRequest().setAttribute("stepName", "");
		JSFUtils.getRequest().setAttribute("isPatch", "");
		String viewPage = "/faces/process/foreignDebtRequests/registerCaptialChange-view.xhtml";
		// 设置弹出窗口url的参数
		RequestContext.getCurrentInstance().addCallbackParam("viewPage", StringUtils.safeString(JSFUtils.contextPath() + viewPage));
		RequestContext.getCurrentInstance().addCallbackParam("op", "view");
		RequestContext.getCurrentInstance().addCallbackParam("menu2", StringUtils.safeString(JSFUtils.getParamValue("menu2")));
		RequestContext.getCurrentInstance().addCallbackParam("procInstId", procInstId);
		RequestContext.getCurrentInstance().addCallbackParam("stepName", "");
		RequestContext.getCurrentInstance().addCallbackParam("isPatch", "");
		
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

    /**
     * @return the regicapitalMofifyVOModel
     */
    public LazyDataModel<RegicapitalMofifyVO> getRegicapitalMofifyVOModel() {
        return regicapitalMofifyVOModel;
    }

    /**
     * @param regicapitalMofifyVOModel the regicapitalMofifyVOModel to set
     */
    public void setRegicapitalMofifyVOModel(LazyDataModel<RegicapitalMofifyVO> regicapitalMofifyVOModel) {
        this.regicapitalMofifyVOModel = regicapitalMofifyVOModel;
    }

    /**
     * @return the regicapitalMofifyVOs
     */
    public List<RegicapitalMofifyVO> getRegicapitalMofifyVOs() {
        return regicapitalMofifyVOs;
    }

    /**
     * @param regicapitalMofifyVOs the regicapitalMofifyVOs to set
     */
    public void setRegicapitalMofifyVOs(List<RegicapitalMofifyVO> regicapitalMofifyVOs) {
        this.regicapitalMofifyVOs = regicapitalMofifyVOs;
    }

}