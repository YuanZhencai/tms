package com.wcs.tms.view.report.companybankbalance;

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
import com.wcs.tms.model.Company;
import com.wcs.tms.service.report.companyacctbalance.CompanyAcctBalanceReportService;
import com.wcs.tms.service.report.companybankbalance.CompanyBankBalanceReportService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.view.process.common.CompanyMultipleSelectBean;
import com.wcs.tms.view.process.common.entity.CompanyBankBalanceVo;
import com.wcs.tms.view.report.companyacctbalance.CompanyAcctBalanceReportBean;
/**
 * 
 * <p>Project: tms</p>
 * <p>Description:公司银行余额表Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
@ManagedBean
@ViewScoped
public class CompanyBankBalanceReportBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(CompanyBankBalanceReportBean.class);
	
	@ManagedProperty(value = "#{companyMultipleSelectBean}")
	private CompanyMultipleSelectBean companyMultipleSelectBean;
	
	@Inject
	LoginService loginService;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	CompanyAcctBalanceReportService companyAcctBalanceReportService;
	@Inject
	CompanyBankBalanceReportService companyBankBalanceReportService;
	// 查询条件
	private Map<String, Object> conditionMap = new HashMap<String, Object>();
	
	private LazyDataModel<CompanyBankBalanceVo> companyBankBalanceVos;
	private List<Company> companies = new ArrayList<Company>();
	@PostConstruct
	public void initCompanyAcctBalanceBean(){
		Calendar c = Calendar.getInstance();
		conditionMap.put("updateByTmsDate",c.getTime());
		searchCompanyBankBalance();
	}
	
	public void searchCompanyBankBalance(){
		try {
			companyBankBalanceVos = new LazyDataModel<CompanyBankBalanceVo>() {
				private static final long serialVersionUID = 1L;

				@SuppressWarnings("static-access")
				@Override
				public List<CompanyBankBalanceVo> load(int first, int pageSize,
						String sortField, SortOrder sortOrder, Map<String, String> filters) {
					StringBuilder sql = new StringBuilder("select t.COMPANY_NAME,"
							+"sum(case  when BAN_ID='2003' or BANK_CODE='ICBC' then t.AVAILABLE_AMOUNT else 0 end) ICBC,"
							+"sum(case  when BAN_ID='2000' or BANK_CODE='ABCX' then t.AVAILABLE_AMOUNT else 0 end) ABCX," 
							+"sum(case  when BAN_ID='2002' or BANK_CODE='CMBC' then t.AVAILABLE_AMOUNT else 0 end) CMBC,"
							+"sum(case  when BAN_ID='2001' or BANK_CODE='BOCX' then t.AVAILABLE_AMOUNT else 0 end) BOCX,"
							+"sum(case  when BAN_ID='2004' or BANK_CODE='BOCC' then t.AVAILABLE_AMOUNT else 0 end) BOCC,"
							+"sum(t.AVAILABLE_AMOUNT) 总计"
							+" from "
							+"(select c.COMPANY_NAME as COMPANY_NAME,b.BAN_ID,b.BANK_CODE,ca.ACCOUNT as ACCOUNT,cab.AVAILABLE_AMOUNT as AVAILABLE_AMOUNT,cab.UPDATED_DATETIME as UPDATED_DATETIME"
							+" from COMPANY c,COMPANY_ACCOUNT ca,"
							+"(select b1.ACCOUNT,b1.UPDATED_DATETIME,b1.DEFUNCT_IND,b1.AVAILABLE_AMOUNT from COMPANY_ACCOUNT_BALANCE b1,"
							+"(select b3.ACCOUNT acc,b3.TRANS_DATETIME tran,max(b3.id) maxid"
							+" from "
							+" (select ACCOUNT,max(TRANS_DATETIME) as maxT From COMPANY_ACCOUNT_BALANCE "
							 );
							// 同步时间条件判断
							SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
							if(null!=conditionMap.get("updateByTmsDate")){
								Date eDate  = (Date)conditionMap.get("updateByTmsDate");
								Calendar eCal =new GregorianCalendar();
								eCal.setTime(eDate);
								eCal.add(eCal.DATE, 1);
								String date = sdf.format(eCal.getTime());
								sql.append(" where UPDATED_DATETIME <= '"+date+"'" );
							}
							sql.append(" group by ACCOUNT) b2,COMPANY_ACCOUNT_BALANCE b3 ");
							sql.append(" where b3.ACCOUNT=b2.ACCOUNT and b3.TRANS_DATETIME = b2.maxT "
							+" group by b3.ACCOUNT,b3.TRANS_DATETIME) b4 "
							+" where b1.ACCOUNT=b4.acc and b1.TRANS_DATETIME = b4.tran and b1.id=b4.maxid) cab,BANK b ");
							sql.append("where c.ID=ca.COMPANY_ID and ca.ACCOUNT=cab.ACCOUNT and ca.BANK = b.BANK_CODE "
							+"and c.DEFUNCT_IND = 'N' and ca.DEFUNCT_IND = 'N' and cab.DEFUNCT_IND = 'N'");
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
							sql.append(") as t group by COMPANY_NAME");
							StringBuilder sql2 = new StringBuilder("select COUNT(*) from ("+sql.toString()+")");
							Integer rowCount = companyAcctBalanceReportService.getRowCountBySql(sql2.toString());
							log.info("searchCompanyBankBalance方法 sql："+sql.toString());
							this.setRowCount(rowCount);
							List<CompanyBankBalanceVo> vos = companyBankBalanceReportService.getBalanceVosBySql(sql.toString(),first,pageSize);
							return vos;
				}
			};
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	/**
	 * 重置查询条件
	 */
	public void reset(){
		setConditionMap(new HashMap<String, Object>());
		companies = new ArrayList<Company>();
	}
	
	public void setValueBySelectedCompanies(){
		getCompanies().clear();
		List<Company> cs = getCompanyMultipleSelectBean().getSelectedAllCompanies();
		for(Company c : cs){
			getCompanies().add(c);
		}
	}
	

	public static Log getLog() {
		return log;
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

	public LazyDataModel<CompanyBankBalanceVo> getCompanyBankBalanceVos() {
		return companyBankBalanceVos;
	}

	public void setCompanyBankBalanceVos(LazyDataModel<CompanyBankBalanceVo> companyBankBalanceVos) {
		this.companyBankBalanceVos = companyBankBalanceVos;
	}

}
