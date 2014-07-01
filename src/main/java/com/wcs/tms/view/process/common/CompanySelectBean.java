package com.wcs.tms.view.process.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.StringUtils;
import com.wcs.tms.model.Company;
import com.wcs.tms.service.process.common.CompanySelectService;
import com.wcs.tms.service.system.company.CompanyTmsService;

/** 
* <p>Project: tms</p> 
* <p>Title: CompanySelectBean.java</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
@ManagedBean
@ViewScoped
public class CompanySelectBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(CompanySelectBean.class);

	@Inject
	EntityService entityService;
	@Inject
	LoginService loginService;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	CompanySelectService companySelectService;
	// 公司下拉菜单
	private List<Company> companyList = new ArrayList<Company>();
	// 公司下拉菜单
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
	// 公司名称的选择列表
	private LazyDataModel<Company> lazyDataModel;
	// Dialog中的输入框中的内容
	private String companyName;
	private String sapCode;
	private String companyEnName;
	// Dialog中选中的那条数据
	private Company selectedCompany;
	private String method;

	/**
	 * Dialog初始化
	 */
	public void initCompany() {
		log.info("公司选择Dialog初始化");
		companyName = "";
		sapCode = "";
		companyEnName = "";
		selectedCompany = new Company();
		filterCompany();
	}
	
	/**
	 * 主要用于清空缓存
	 */
	public void clear() {
		System.out.print("清空缓存");
		companyName = "";
		sapCode = "";
		companyEnName = "";
		selectedCompany = new Company();
		filterCompany();
	}

	/**
	 * 集团用户才执行此方法
	 */
	public void filterCompany() {
		log.info("是集团用户");
		// 方法一：Service中返回LazyModel
		// lazyDataModel = companySelectService.findAllCompany(companyName);
		// 方法二：Service中返回List
		log.info("查询条件companyName:" + companyName);
		log.info("查询条件sapCode:" + sapCode);
		log.info("查询条件companyEnName:" + companyEnName);
		lazyDataModel = this.findAllCompany(companyName, sapCode, companyEnName);
	}

	public Map<String, Object> getFilterMap(Company company, Map<String, Object> conditionMap) {
		log.info("集团用户查询");
		if (company != null && null != company.getCompanyName()) {
			log.info("company:" + company.getCompanyName());
			long companyId = companyTmsService.getCompanyName(company.getCompanyName());
			conditionMap.put("companyId", String.valueOf(companyId));
			log.info("conditionMap:" + conditionMap.get("companyId"));
		} else {
			companySelect = companyTmsService.getAllCompanySelect();
			List<String> companyIds = new ArrayList<String>();
			for (SelectItem item : companySelect) {
				companyIds.add(String.valueOf(item.getValue()));
			}
			conditionMap.put("companyIds", companyIds);
		}
		return conditionMap;
	}

	/**
	 * 集团用户时，公司列表需要懒加载的特殊处理
	 * @param companyName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public LazyDataModel<Company> findAllCompany(final String companyName, final String sapCode, final String companyEnName) {
		LazyDataModel<Company> lazyDataModel = new LazyDataModel<Company>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<Company> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
				Map<String, Object> map = new HashMap<String, Object>();
				map = companySelectService.findBySqLoad(first, pageSize, sortField, sortOrder, companyName, sapCode, companyEnName);
				List<Company> list = new ArrayList<Company>();
				if (map.size() != 0) {
					list = (List<Company>) map.get("list");
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

	/**
	 * 列表选中行后不同bean中传参
	 */
	public void beforeClose() {
		log.info("***************************************" + method);
		String stringarray[]=method.split(";");
		for(String s :stringarray ){
			if (StringUtils.hasText(s)) {
				Application application = FacesContext.getCurrentInstance().getApplication();
				ExpressionFactory expressionFactory = application.getExpressionFactory();
				ELContext elContext = FacesContext.getCurrentInstance().getELContext();
				MethodExpression me = expressionFactory.createMethodExpression(elContext, "#{" + s + "}", Void.class, new Class[] {});
				try {
					me.invoke(elContext, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
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

	public CompanySelectService getCompanySelectService() {
		return companySelectService;
	}

	public void setCompanySelectService(CompanySelectService companySelectService) {
		this.companySelectService = companySelectService;
	}

	public List<Company> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<Company> companyList) {
		this.companyList = companyList;
	}

	public LazyDataModel<Company> getLazyDataModel() {
		return lazyDataModel;
	}

	public void setLazyDataModel(LazyDataModel<Company> lazyDataModel) {
		this.lazyDataModel = lazyDataModel;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Company getSelectedCompany() {
		return selectedCompany;
	}

	public void setSelectedCompany(Company selectedCompany) {
		this.selectedCompany = selectedCompany;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<SelectItem> getCompanySelect() {
		return companySelect;
	}

	public void setCompanySelect(List<SelectItem> companySelect) {
		this.companySelect = companySelect;
	}

	public String getSapCode() {
		return sapCode;
	}

	public void setSapCode(String sapCode) {
		this.sapCode = sapCode;
	}

	public String getCompanyEnName() {
		return companyEnName;
	}

	public void setCompanyEnName(String companyEnName) {
		this.companyEnName = companyEnName;
	}

}
