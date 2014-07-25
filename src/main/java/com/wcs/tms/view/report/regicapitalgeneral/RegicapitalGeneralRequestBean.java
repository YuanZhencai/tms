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
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcRegiCapital;
import com.wcs.tms.service.process.debtpayment.RegiDebtManageService;
import com.wcs.tms.service.report.regicapitalgeneral.RegicapitalGeneralRequestService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.CompanySelectBean;
import com.wcs.tms.view.process.common.entity.RegiCapitalConfirmVo;
import com.wcs.tms.view.process.common.entity.RegicapitalRequestVO;

/** 
* <p>Project: tms</p> 
* <p>Title: 注册资本金申请Bean</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:guyanyu@wcs-global.com">Gu yanyu</a> 
*/
@ManagedBean
@ViewScoped
public class RegicapitalGeneralRequestBean implements Serializable {
	private static final long serialVersionUID = 1L;
	 
	private Log log = LogFactory.getLog(RegicapitalGeneralRequestBean.class);
	
	@Inject
	EntityService entityService;
	@Inject
	LoginService loginService;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	RegicapitalGeneralRequestService regicapitalGeneralRequestService;
	@Inject
	RegiDebtManageService regiDebtManageService;
	// 查询条件
	private Map<String, Object> conditionMap = new HashMap<String, Object>();
	// 公司下拉菜单
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
	// 公司名称的选择列表
	private LazyDataModel<Company> lazyDataModel;
	private LazyDataModel<RegicapitalRequestVO> regicapitalRequestVOModel;
	// 公司股东信息列表VO
	private List<RegicapitalRequestVO> regicapitalRequestVOs = new ArrayList<RegicapitalRequestVO>();
	@ManagedProperty(value = "#{companySelectBean}")
	private CompanySelectBean companySelectBean;
	@Inject
	private CommonBean dictBean;
	
	private Company company;
	// 判断是否是集团用户
	private Boolean isCopUser = false;
	
	//注册资本金确认
	private RegiCapitalConfirmVo confirmVo = new RegiCapitalConfirmVo();

	private ProcRegiCapital regiCapital;
	
	/** 资金币种下拉 */
	private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
	

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
		if (regicapitalRequestVOs.size() == 0) {
			this.searchRegiCaitalRequestDetail();
		}
		currencySelect = dictBean.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
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
	 * 查询注册资本金申请明细
	 */
	public void searchRegiCaitalRequestDetail() {
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
			regicapitalRequestVOModel = this.findRegicapitalMofify(conditionMap);
		} catch (Exception e) {
			log.error("searchRegiCaitalDetail方法 错误信息：" + e.getMessage());
		}
	}

	/**
	 * 查询
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public LazyDataModel<RegicapitalRequestVO> findRegicapitalMofify(final Map<String, Object> conditionMap) {
		LazyDataModel<RegicapitalRequestVO> lazyDataModel = new LazyDataModel<RegicapitalRequestVO>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<RegicapitalRequestVO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
				Map<String, Object> map = new HashMap<String, Object>();
				map = regicapitalGeneralRequestService.findRegicapitalRequestDetail(first, pageSize, sortField, sortOrder, conditionMap);
				List<RegicapitalRequestVO> list = new ArrayList<RegicapitalRequestVO>();
				if (map.size() != 0) {
					list = (List<RegicapitalRequestVO>) map.get("list");
					this.setRowCount((Integer) map.get("count"));
				} else {
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
		String viewPage = "/faces/process/foreignDebtRequests/registerCaptial-view.xhtml";
		// 设置弹出窗口url的参数
		RequestContext.getCurrentInstance().addCallbackParam("viewPage", StringUtils.safeString(JSFUtils.contextPath() + viewPage));
		RequestContext.getCurrentInstance().addCallbackParam("op", "view");
		RequestContext.getCurrentInstance().addCallbackParam("menu2", StringUtils.safeString(JSFUtils.getParamValue("menu2")));
		RequestContext.getCurrentInstance().addCallbackParam("procInstId", procInstId);
		RequestContext.getCurrentInstance().addCallbackParam("stepName", "");
		RequestContext.getCurrentInstance().addCallbackParam("isPatch", "");
		
	}
	
	public void initConfirmRegiCapital(){
		confirmVo = new RegiCapitalConfirmVo();
		// ....
		confirmVo.setRegiCapital(regiCapital);
		confirmVo.setRegistrant(loginService.getCurrentUserName());
	}
	
	public void confirmRegiCapital(){
		try {
			regiDebtManageService.confirmRegiCapital(confirmVo);
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("widgetVar", "regiCapitalConfirmDialogVar");
			context.addCallbackParam("option", "close");
			MessageUtils.addSuccessMessage("msg", "操作成功。");
		} catch (Exception e) {
			MessageUtils.addErrorMessage("msg", "注册资本金到账确认失败，请重新操作。");
		}
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
     * @return the regicapitalRequestVOModel
     */
    public LazyDataModel<RegicapitalRequestVO> getRegicapitalRequestVOModel() {
        return regicapitalRequestVOModel;
    }

    /**
     * @param regicapitalRequestVOModel the regicapitalRequestVOModel to set
     */
    public void setRegicapitalRequestVOModel(LazyDataModel<RegicapitalRequestVO> regicapitalRequestVOModel) {
        this.regicapitalRequestVOModel = regicapitalRequestVOModel;
    }

    /**
     * @return the regicapitalRequestVOs
     */
    public List<RegicapitalRequestVO> getRegicapitalRequestVOs() {
        return regicapitalRequestVOs;
    }

    /**
     * @param regicapitalRequestVOs the regicapitalRequestVOs to set
     */
    public void setRegicapitalRequestVOs(List<RegicapitalRequestVO> regicapitalRequestVOs) {
        this.regicapitalRequestVOs = regicapitalRequestVOs;
    }

	public RegiCapitalConfirmVo getConfirmVo() {
		return confirmVo;
	}

	public void setConfirmVo(RegiCapitalConfirmVo confirmVo) {
		this.confirmVo = confirmVo;
	}

	public ProcRegiCapital getRegiCapital() {
		return regiCapital;
	}

	public void setRegiCapital(ProcRegiCapital regiCapital) {
		this.regiCapital = regiCapital;
	}

	public List<SelectItem> getCurrencySelect() {
		return currencySelect;
	}

	public void setCurrencySelect(List<SelectItem> currencySelect) {
		this.currencySelect = currencySelect;
	}
	
}