package com.wcs.tms.view.process.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.MathUtil;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcessMap;
import com.wcs.tms.service.process.common.CompanyMultipleSelectService;
import com.wcs.tms.service.process.common.CompanySelectService;
import com.wcs.tms.service.system.company.CompanyTmsService;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:公司多选条件过滤选择Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
@ManagedBean
@ViewScoped
public class CompanyMultipleSelectBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Inject
	EntityService entityService;
	@Inject
	LoginService loginService;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	CompanySelectService companySelectService;
	@Inject
	CompanyMultipleSelectService companyMultipleSelectService;
	
	// 公司列表
//	private List<Company> dataModel;
	private LazyDataModel<Company> dataModel;
	// 过滤条件
	private String companyName;
	private String sapCode;
	private String companyEnName;
	// 当前页选中公司数据
	private Company[] selectedCompanies;
	// 每次翻页首先获取之前所选数据
	private List<Company> selectDataBefore = new ArrayList<Company>();
	// 所有选中数据
	private List<Company> selectedAllCompanies = new ArrayList<Company>();
	
	private String method;

	@PostConstruct
	public void initCompanyMultipleSelectBean(){
		filterCompany();
	}
	
	/**
	 * 翻页时保存上一页已选择数据
	 */
	public void saveLastPageSelection(){
		try {
			// 翻页前 将之前所选数据 加上本页所选数据(某次翻页可能选择相同数据)
			for(Company c : selectedCompanies){
				if(!selectDataBefore.contains(c)){
					selectDataBefore.add(c);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 主要用于清空缓存
	 */
	public void clear() {
		selectDataBefore.clear();
		selectedAllCompanies.clear();
		companyName = "";
		sapCode = "";
		companyEnName = "";
//		selectedCompany = new Company();
		filterCompany();
	}
	
	/**
	 * 列表重置
	 */
	public void reset(){
		setSelectedCompanies(new Company[20]);
		companyName = "";
		sapCode = "";
		companyEnName = "";
		filterCompany();
	}
	/**
	 * 集团用户才执行此方法
	 */
	public void filterCompany() {
		try {
			
	//		setDataModel(companyMultipleSelectService.filterCompanyByCurrentUser(companyName, sapCode, companyEnName));
			List<String> saps = loginService.findCompanySapCode();
			StringBuilder jpql = new StringBuilder("select c from Company c where c.defunctInd = 'N' and c.status = 'Y'");
			if(!MathUtil.isEmptyOrNull(companyName)){
				jpql.append(" and lower(c.companyName) like '%"+companyName.toLowerCase()+"%'");
			}
			if(!MathUtil.isEmptyOrNull(sapCode)){
				jpql.append(" and lower(c.sapCode) like '%"+sapCode.toLowerCase()+"%'");
			}
			if(!MathUtil.isEmptyOrNull(companyEnName)){
				jpql.append(" and lower(c.companyEnName) like '%"+companyEnName.toLowerCase()+"%'");
			}
			if(loginService.isCopUser()){
				dataModel = entityService.findModel(jpql.toString());
			}else {
				// 如果不是集团的人则只查出所在公司
				if (saps != null && saps.size() != 0 ) {
					jpql.append(" and c.sapCode in (?1)");
					dataModel = entityService.findModel(jpql.toString(),saps);
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	 * 执行上级申请页面方法
	 */
	public void beforeClose(){
		// 关闭控件之前已选所有数据
		selectedAllCompanies.addAll(selectDataBefore);
		for(Company c : selectedCompanies){
			if(!selectedAllCompanies.contains(c)){
				selectedAllCompanies.add(c);
			}
		}
		Application application = FacesContext.getCurrentInstance().getApplication() ;
		ExpressionFactory expressionFactory = application.getExpressionFactory() ;
		ELContext elContext = FacesContext.getCurrentInstance().getELContext() ;
		MethodExpression me = expressionFactory.createMethodExpression(elContext, "#{"+method+"}", Void.class, new Class[]{}) ;
		try{
			me.invoke(elContext, null) ;
		}catch(Exception e){
			e.printStackTrace() ;
		}
	}

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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
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

	public Company[] getSelectedCompanies() {
		return selectedCompanies;
	}

	public void setSelectedCompanies(Company[] selectedCompanies) {
		this.selectedCompanies = selectedCompanies;
	}

	public LazyDataModel<Company> getDataModel() {
		return dataModel;
	}

	public void setDataModel(LazyDataModel<Company> dataModel) {
		this.dataModel = dataModel;
	}

	public List<Company> getSelectDataBefore() {
		return selectDataBefore;
	}

	public void setSelectDataBefore(List<Company> selectDataBefore) {
		this.selectDataBefore = selectDataBefore;
	}

	public List<Company> getSelectedAllCompanies() {
		return selectedAllCompanies;
	}

	public void setSelectedAllCompanies(List<Company> selectedAllCompanies) {
		this.selectedAllCompanies = selectedAllCompanies;
	}

	
}
