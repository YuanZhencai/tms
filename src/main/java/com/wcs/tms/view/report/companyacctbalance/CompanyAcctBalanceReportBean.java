package com.wcs.tms.view.report.companyacctbalance;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

import com.wcs.base.service.LoginService;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.MathUtil;
import com.wcs.tms.model.Company;
import com.wcs.tms.service.report.companyacctbalance.CompanyAcctBalanceReportService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.view.process.common.CompanyMultipleSelectBean;
import com.wcs.tms.view.process.common.entity.CompanyAcctBalanceVo;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:公司账户余额表Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
@ManagedBean
@ViewScoped
public class CompanyAcctBalanceReportBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(CompanyAcctBalanceReportBean.class);
	
	@ManagedProperty(value = "#{companyMultipleSelectBean}")
	private CompanyMultipleSelectBean companyMultipleSelectBean;
	
	@Inject
	LoginService loginService;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	CompanyAcctBalanceReportService companyAcctBalanceReportService;
	
	// 公司账户余额列表数据
	private LazyDataModel<CompanyAcctBalanceVo> companyAcctBalanceVos;
	private List<Company> companies = new ArrayList<Company>();
	// 查询条件
	private Map<String, Object> conditionMap = new HashMap<String, Object>();
	
	@PostConstruct
	public void initCompanyAcctBalanceBean(){
		Calendar c = Calendar.getInstance();
		conditionMap.put("updateByTmsStartDate",DateUtil.adjustYearAndMonthAndDay(c.getTime(), 0, 0, -7));
		conditionMap.put("updateByTmsEndDate",new Date());
		searchCompanyAcctBalance();
	}

	/**
	 * 根据条件查询余额列表数据
	 */
	public void searchCompanyAcctBalance() {
		try {
			companyAcctBalanceVos = new LazyDataModel<CompanyAcctBalanceVo>() {
				private static final long serialVersionUID = 1L;

				@SuppressWarnings("static-access")
				@Override
				public List<CompanyAcctBalanceVo> load(int first, int pageSize,
						String sortField, SortOrder sortOrder, Map<String, String> filters) {
					SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
					StringBuilder sql = new StringBuilder("select c.COMPANY_NAME,b.BANK_NAME,ca.ACCOUNT,cab.AVAILABLE_AMOUNT,cab.UPDATED_DATETIME "
					+" from COMPANY c,COMPANY_ACCOUNT ca,"
					+"(select b1.ACCOUNT,b1.UPDATED_DATETIME,b1.DEFUNCT_IND,b1.AVAILABLE_AMOUNT from COMPANY_ACCOUNT_BALANCE b1,"
					+"(select b3.ACCOUNT acc,b3.TRANS_DATETIME tran,max(b3.id) maxid"
					+" from (select ACCOUNT,max(TRANS_DATETIME) as maxT From COMPANY_ACCOUNT_BALANCE group by ACCOUNT,year(TRANS_DATETIME),month(TRANS_DATETIME),day(TRANS_DATETIME)) b2,COMPANY_ACCOUNT_BALANCE b3"
					+" where b3.ACCOUNT=b2.ACCOUNT and b3.TRANS_DATETIME = b2.maxT"
					+" group by b3.ACCOUNT,b3.TRANS_DATETIME) b4"
					+" where b1.ACCOUNT=b4.acc and b1.TRANS_DATETIME = b4.tran and b1.id=b4.maxid) cab,BANK b "
					+" where c.ID=ca.COMPANY_ID and ca.ACCOUNT=cab.ACCOUNT and ca.BANK = b.BANK_CODE "
					+" and c.DEFUNCT_IND = 'N' and ca.DEFUNCT_IND = 'N' and cab.DEFUNCT_IND = 'N'");
					if(!MathUtil.isEmptyOrNull((String)conditionMap.get("account"))){
						sql.append(" and lower(ca.ACCOUNT) like '%" +(String)conditionMap.get("account")+"%'");
					}
					
					// 同步时间条件判断
					if(null!=conditionMap.get("updateByTmsStartDate")){
						String start = sdf.format(conditionMap.get("updateByTmsStartDate"));
						sql.append(" and cab.UPDATED_DATETIME >= '"+start+"'" );
					}
					if(null!=conditionMap.get("updateByTmsEndDate")){
						Date eDate  = (Date)conditionMap.get("updateByTmsEndDate");
						Calendar eCal =new GregorianCalendar();
						eCal.setTime(eDate);
						eCal.add(eCal.DATE, 1);
						String end = sdf.format(eCal.getTime());
						sql.append(" and cab.UPDATED_DATETIME <= '"+end+"'" );
					}
					if(!MathUtil.isEmptyOrNull((String)conditionMap.get("bankName"))){
						String bankName = (String)conditionMap.get("bankName");
						sql.append(" and b.BANK_NAME like '%"+bankName+"%'" );
					}
					// 公司名称条件判断
					if(companies.size()>0){
						StringBuilder cIdStr = new StringBuilder("(");
						int max = companies.size()-1;
						for(Company c : companies){
							cIdStr.append(c.getId());
							if( companies.indexOf(c) != max){
								cIdStr.append(",");
							}
						}
						cIdStr.append(")");
						sql.append(" and c.ID in "+cIdStr );
					}else{
						// 报表初始化时 根据用户查出业务相关数据
						List<String> companyIds = new ArrayList<String>();
						if(loginService.isCopUser()){
							List<SelectItem> companySelect = companyTmsService.getAllCompanySelect();
							for (SelectItem item : companySelect) {
								companyIds.add(String.valueOf(item.getValue()));
							}
						}else{
							//获取用户相关公司数据 add on 2013-3-11
							List<SelectItem> companySelect = companyTmsService.getCompanySelect();
							for (SelectItem item : companySelect) {
								companyIds.add(String.valueOf(item.getValue()));
							}
						}
						StringBuilder cIdStr = new StringBuilder("(");
						int max = companyIds.size()-1;
						for(String id : companyIds){
							cIdStr.append(id);
							if( companyIds.indexOf(id) != max){
								cIdStr.append(",");
							}
						}
						cIdStr.append(")");
						sql.append(" and c.ID in "+cIdStr );
					}
					StringBuilder sql2 = new StringBuilder("select COUNT(*) from ("+sql.toString()+")");
					Integer rowCount = companyAcctBalanceReportService.getRowCountBySql(sql2.toString());
					this.setRowCount(rowCount);
					List<CompanyAcctBalanceVo> vos = companyAcctBalanceReportService.getBalanceVosBySql(sql.toString(),first,pageSize);
					return vos;
				}
			};
		} catch (Exception e) {
			log.error("方法searchCompanyAcctBalance 根据条件查询余额列表数据 出现异常：",e);
		}
	}
	
	/**
	 * 重置查询条件
	 */
	public void reset(){
		conditionMap=new HashMap<String, Object>();
		companies = new ArrayList<Company>();
	}
	
	public void setValueBySelectedCompanies(){
		getCompanies().clear();
		List<Company> cs = getCompanyMultipleSelectBean().getSelectedAllCompanies();
		for(Company c : cs){
			// add on 2014-1-8 界面展示效果更改
			getCompanies().add(c);
		}
	}

	
	
	public LazyDataModel<CompanyAcctBalanceVo> getCompanyAcctBalanceVos() {
		return companyAcctBalanceVos;
	}

	public void setCompanyAcctBalanceVos(LazyDataModel<CompanyAcctBalanceVo> companyAcctBalanceVos) {
		this.companyAcctBalanceVos = companyAcctBalanceVos;
	}

	public CompanyMultipleSelectBean getCompanyMultipleSelectBean() {
		return companyMultipleSelectBean;
	}

	public void setCompanyMultipleSelectBean(CompanyMultipleSelectBean companyMultipleSelectBean) {
		this.companyMultipleSelectBean = companyMultipleSelectBean;
	}

	public List<Company> getCompanies() {
		return companies;
	}

	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}

	public Map<String, Object> getConditionMap() {
		return conditionMap;
	}

	public void setConditionMap(Map<String, Object> conditionMap) {
		this.conditionMap = conditionMap;
	}

	
	

}
