package com.wcs.tms.view.report.debtborrow;

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
import com.wcs.tms.service.report.debtborrow.DebtBorrowRequestService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.view.process.common.CompanySelectBean;
import com.wcs.tms.view.process.common.entity.DebtBorrowRequestVO;

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
public class DebtBorrowRequestBean implements Serializable {
	private static final long serialVersionUID = 1L;
	 
	private Log log = LogFactory.getLog(DebtBorrowRequestBean.class);
	
	@Inject
	EntityService entityService;
	@Inject
	LoginService loginService;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	DebtBorrowRequestService debtBorrowRequestService;
	// 查询条件
	private Map<String, Object> conditionMap = new HashMap<String, Object>();
	// 公司下拉菜单
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
	private LazyDataModel<DebtBorrowRequestVO> debtBorrowRequestVOModel;
	// 公司股东信息列表VO
	private List<DebtBorrowRequestVO> debtBorrowRequestVOs = new ArrayList<DebtBorrowRequestVO>();
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
		if (debtBorrowRequestVOs.size() == 0) {
			this.searchDebtBorrowRequestDetail();
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
	 * 查询注册资本金申请明细
	 */
	public void searchDebtBorrowRequestDetail() {
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
			debtBorrowRequestVOModel = this.findDebtBorrowRequest(conditionMap);
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
	public LazyDataModel<DebtBorrowRequestVO> findDebtBorrowRequest(final Map<String, Object> conditionMap) {
		LazyDataModel<DebtBorrowRequestVO> lazyDataModel = new LazyDataModel<DebtBorrowRequestVO>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<DebtBorrowRequestVO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
				Map<String, Object> map = new HashMap<String, Object>();
				map = debtBorrowRequestService.findDebtBorrowRequestDetail(first, pageSize, sortField, sortOrder, conditionMap);
				List<DebtBorrowRequestVO> list = new ArrayList<DebtBorrowRequestVO>();
				if (map.size() != 0) {
					list = (List<DebtBorrowRequestVO>) map.get("list");
					this.setRowCount((Integer) map.get("count"));
				} else {
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

    /**
     * @return the debtBorrowRequestVOModel
     */
    public LazyDataModel<DebtBorrowRequestVO> getDebtBorrowRequestVOModel() {
        return debtBorrowRequestVOModel;
    }

    /**
     * @param debtBorrowRequestVOModel the debtBorrowRequestVOModel to set
     */
    public void setDebtBorrowRequestVOModel(LazyDataModel<DebtBorrowRequestVO> debtBorrowRequestVOModel) {
        this.debtBorrowRequestVOModel = debtBorrowRequestVOModel;
    }

    /**
     * @return the DebtBorrowRequestVOs
     */
    public List<DebtBorrowRequestVO> getDebtBorrowRequestVOs() {
        return debtBorrowRequestVOs;
    }

    /**
     * @param DebtBorrowRequestVOs the DebtBorrowRequestVOs to set
     */
    public void setDebtBorrowRequestVOs(List<DebtBorrowRequestVO> DebtBorrowRequestVOs) {
        this.debtBorrowRequestVOs = DebtBorrowRequestVOs;
    }
	
}