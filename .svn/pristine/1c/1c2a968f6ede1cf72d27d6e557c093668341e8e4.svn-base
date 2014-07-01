package com.wcs.tms.view.report.cashpool;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.wcs.base.util.MathUtil;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcessMap;
import com.wcs.tms.service.process.common.BankMultipleSelectService;
import com.wcs.tms.service.process.common.CompanySelectService;
import com.wcs.tms.service.process.common.ProcessDealedService;
import com.wcs.tms.service.process.common.ProcessNumberSelectService;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.report.cashpool.CashPoolReportService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.service.system.sysprocess.ProcessDefineService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.BankMultipleSelectBean;
import com.wcs.tms.view.process.common.CompanyMultipleSelectBean;
import com.wcs.tms.view.process.common.ProcessNumberSelectBean;
import com.wcs.tms.view.process.common.entity.CashPoolVo;
import com.wcs.tms.model.Bank;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:现金池查询清单Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
@ManagedBean
@ViewScoped
public class CashPoolReportBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(CashPoolReportBean.class);
	@Inject
	ProcessDealedService processDealedService;
	@Inject
	CashPoolReportService cashPoolReportService;
	@Inject
	BankMultipleSelectService bankMultipleSelectService;
	@Inject
	CompanySelectService companySelectService;
	@Inject
	ProcessNumberSelectService processNumberSelectService;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	LoginService loginService;
	@Inject
	EntityService entityService;
	@Inject
	ProcessDefineService processDefineService;
	@Inject
	ProcessUtilMapService processUtilMapService;
	
	@ManagedProperty(value = "#{bankMultipleSelectBean}")
	private BankMultipleSelectBean bankMultipleSelectBean;
	@ManagedProperty(value = "#{companyMultipleSelectBean}")
	private CompanyMultipleSelectBean companyMultipleSelectBean;
	@ManagedProperty(value = "#{processNumberSelectBean}")
	private ProcessNumberSelectBean processNumberSelectBean;
	// 查询条件
	private Map<String, Object> conditionMap = new HashMap<String, Object>();
	// 流程列表VO
	private LazyDataModel<CashPoolVo> cashPoolVos;
	// 流程名称
    private List<SelectItem> processNameSelect = new ArrayList<SelectItem>();
    // 收款公司
    private List<Company> receiveComs = new ArrayList<Company>();
    private List<String> selectedReceiveComIds = new ArrayList<String>();
    // 收款银行
    private List<Bank> receiveBanks = new ArrayList<Bank>();
    private List<Long> selectedReceiveBankIds = new ArrayList<Long>();
    // 实例编号
    private List<String> instanceNums = new ArrayList<String>();
    private List<String> selectedInstanceNums = new ArrayList<String>();
    // 申请金额合计
    private Double appAmountTotal = 0.0;
    // 实际付款金额合计
    private Double realPaymentAmountTotal = 0.0;
    // 剩余需支付金额合计
    private Double lastPaymentAmountTotal = 0.0;
	
	@PostConstruct
	public void initCashPoolReportBean(){
		// 根据所有vo数据，初始化查询条件所需数据（主要是多选框数据）
		initQueryCondition();
		// 初始化查询出所有数据
		searchCashpoolDetail();
	}
	
	/**
	 * 申请金额 如果为0则设置为null
	 */
	public void setAmountNull(){
		try {
			BigDecimal appAmountEnd = (BigDecimal)conditionMap.get("appAmountEnd");
			if(appAmountEnd !=null && 0 == appAmountEnd.compareTo(BigDecimal.ZERO)){
				conditionMap.remove("appAmountEnd");
	//			conditionMap.put("appAmountEnd", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isNum(String str){
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}
	
	/**
	 * 查询条件重置
	 */
	public void reset(){
		conditionMap = new HashMap<String, Object>();
		selectedReceiveComIds = new ArrayList<String>();
		selectedReceiveBankIds = new ArrayList<Long>();
		selectedInstanceNums = new ArrayList<String>();
		receiveComs = new ArrayList<Company>();
		receiveBanks = new ArrayList<Bank>();
		instanceNums = new ArrayList<String>();
	}
	
	
	/**
	 * 查询所有流程
	 */
	public void searchCashpoolDetail(){
		try {
			// 初始化合计信息
			this.setAppAmountTotal(0.0);
			this.setRealPaymentAmountTotal(0.0);
			this.setLastPaymentAmountTotal(0.0);
			// 验证申请金额是否为数字
			String appAmountStart = (String)conditionMap.get("appAmountStart");
			if(MathUtil.isEmptyOrNull(appAmountStart)){
				Double appAmountS = null;
				conditionMap.put("appAmountStart",appAmountS);
			}else if(isNum(appAmountStart)){
				Double appAmountS = Double.parseDouble(appAmountStart);
				conditionMap.put("appAmountStart",appAmountS);
			}else{
				MessageUtils.addErrorMessage("doneMsg", "申请金额上限必须为数字");
				return;
			}
			String appAmountEnd = (String)conditionMap.get("appAmountEnd");
			if(MathUtil.isEmptyOrNull(appAmountEnd)){
				Double appAmountE = null;
				conditionMap.put("appAmountEnd",appAmountE);
			}else if(isNum(appAmountEnd)){
				Double appAmountE = Double.parseDouble(appAmountEnd);
				conditionMap.put("appAmountEnd",appAmountE);
			}else{
				MessageUtils.addErrorMessage("doneMsg", "申请金额下限必须为数字");
				return;
			}
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
			conditionMap.put("companyIds", companyIds);
			
			// 申请金额查询条件填写限制
			if(null != conditionMap.get("appAmountStart") && null != conditionMap.get("appAmountEnd")){
				
				if(Double.parseDouble(conditionMap.get("appAmountStart").toString()) > Double.parseDouble(conditionMap.get("appAmountEnd").toString())){
					MessageUtils.addErrorMessage("doneMsg", "申请金额上限不能大于下限");
					return ;
				}
			}
			// 根据数据库能够筛选的条件先查询出vo数据
			cashPoolVos = new LazyDataModel<CashPoolVo>() {
				final List<String> fnIds = cashPoolReportService.transformSelectedInstanceNums(selectedInstanceNums);
					private static final long serialVersionUID = 1L;

					@Override
					public List<CashPoolVo> load(int first, int pageSize, String sortField,
							SortOrder sortOrder, Map<String, String> filters) {
						List<CashPoolVo> cVos = new ArrayList<CashPoolVo>();
						try {
							
						
							SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat sdfMutile= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							// 按流程实例编号分页查出 视图中流程实例编号
							StringBuilder sql = new StringBuilder("select DISTINCT PROC_INST_ID pid, CREATED_DATETIME from VW_CASHPOOL where DEFUNCT_IND = 'N' and TERMINATE_FLAG='N'");
							//流程名称
							/**
							 * sonar测试 null != conditionMap.get("processId") && "" !=conditionMap.get("processId")
							 * 改为 null != conditionMap.get("processId") && !"".equals(conditionMap.get("processId"))
							 */
							if(null != conditionMap.get("processId") && !"".equals(conditionMap.get("processId"))){
								sql.append(" and PROC_DEFINE_ID = '"+conditionMap.get("processId")+"'");
							}
							//填制时间
							if(null!=conditionMap.get("paymentStartDate")){
								String start = sdf.format(conditionMap.get("paymentStartDate"));
								sql.append(" and CREATED_DATETIME >= '"+start+"'" );
							}
							if(null!=conditionMap.get("paymentEndDate")){
								Date date = (Date)conditionMap.get("paymentEndDate");
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(date);
								calendar.add(Calendar.DAY_OF_MONTH, 1);
								String end = sdf.format(calendar.getTime());
								sql.append(" and CREATED_DATETIME <= '"+end+"'" );
							}
							//提交时间
							if(null!=conditionMap.get("submitStartDate")){
								String start = sdfMutile.format(conditionMap.get("submitStartDate"));
								sql.append(" and CREATED_DATETIME >= TIMESTAMP('"+start+"')" );
							}
							if(null!=conditionMap.get("submitEndDate")){
								Date date = (Date)conditionMap.get("submitEndDate");
								String end = sdfMutile.format(date);
								sql.append(" and CREATED_DATETIME <= TIMESTAMP('"+end+"')" );
							}
							//申请付款时间 
							if(null!=conditionMap.get("appPaymentStartDate")){
								String start = sdf.format(conditionMap.get("appPaymentStartDate"));
								sql.append(" and PAYMENT_DATE >= '"+start+"'" );
							}
							if(null!=conditionMap.get("appPaymentEndDate")){
								String end = sdf.format(conditionMap.get("appPaymentEndDate"));
								sql.append(" and PAYMENT_DATE <= '"+end+"'" );
							}
							//收款公司
							if(selectedReceiveComIds.size() != 0){
								if(!"all".equals(selectedReceiveComIds.get(0))){
									StringBuilder cIdStr = new StringBuilder("(");
									int max = selectedReceiveComIds.size()-1;
									for(String cId : selectedReceiveComIds){
										cIdStr.append(cId);
										if( selectedReceiveComIds.indexOf(cId) != max){
											cIdStr.append(",");
										}
									}
									cIdStr.append(")");
									sql.append(" and COMPANY_ID in "+cIdStr );
								}
							// 查询条件 收款公司 默认当前用户 所属公司
							}else{
								List<String> companyIds = (List<String>) conditionMap.get("companyIds");
								if (companyIds != null && !companyIds.isEmpty()) {
									StringBuilder cIdStr = new StringBuilder("(");
									int max = companyIds.size()-1;
									for(String cId : companyIds){
										cIdStr.append(cId);
										if( companyIds.indexOf(cId) != max){
											cIdStr.append(",");
										}
									}
									cIdStr.append(")");
									sql.append(" and COMPANY_ID in "+cIdStr );
								}
							}
							//流程实例编号 
							if(fnIds.size() != 0){
								if(!"all".equals(fnIds.get(0))){
									// 对选中的 集合数据进行sql处理
									StringBuilder fIdStr = new StringBuilder("(");
									int max = fnIds.size()-1;
									for(String fId : fnIds){
										fIdStr.append("'"+fId+"'");
										if( fnIds.indexOf(fId) != max){
											fIdStr.append(",");
										}
									}
									fIdStr.append(")");
									sql.append(" and PROC_INST_ID in "+fIdStr );
								}
							}
							//收款银行
							if(selectedReceiveBankIds.size() != 0){
								if(!"all".equals(selectedReceiveBankIds.get(0))){
									StringBuilder bIdStr = new StringBuilder("(");
									int max = selectedReceiveBankIds.size()-1;
									for(Long bId : selectedReceiveBankIds){
										bIdStr.append(bId.toString());
										if( selectedReceiveBankIds.indexOf(bId) != max){
											bIdStr.append(",");
										}
									}
									bIdStr.append(")");
									sql.append(" and TOP_BANK_ID in "+bIdStr );
								}
							}
							//申请金额 
							if(null!=conditionMap.get("appAmountStart")){
								sql.append(" and AMOUNT >= "+conditionMap.get("appAmountStart") );
							}
							if(null!=conditionMap.get("appAmountEnd")){
								sql.append(" and AMOUNT <= "+conditionMap.get("appAmountEnd") );
							}
							//剩余需要支付金额 
							if(null!=conditionMap.get("lastAmountIsZero")){
								// 剩余支付金额等于0
								if("Y".equals(conditionMap.get("lastAmountIsZero").toString())){
									sql.append(" and LAST_PAIED_AMOUNT = 0 ");
								// 剩余支付金额大于0
								}else{
									// 未付款的流程 剩余金额为null
									sql.append(" and (LAST_PAIED_AMOUNT <> 0 or LAST_PAIED_AMOUNT is null)");
								}
							}
							sql.append(" ORDER BY CREATED_DATETIME DESC");
							// 查出视图中所有不重复流程实例个数
							StringBuilder sql2 = new StringBuilder("select COUNT(*) from ("+sql.toString()+")");
							Integer rowCount = cashPoolReportService.getRowCountBySql(sql2.toString());
							this.setRowCount(rowCount);
							// 分页查询(查出为流程实例编号一列的表)
							List<String> processIds  = cashPoolReportService.getPagedRowsBySql(sql.toString(),first,pageSize);
							// 将数据库查出数据 赋值给vo对象
							cashPoolReportService.transferVosByProcessIds(cVos,processIds);
							// 算出合计信息
							// 申请金额合计
							if(processIds.size() != 0){
								StringBuilder pIdStr = new StringBuilder("(");
								int max = processIds.size()-1;
								for(String pId : processIds){
									pIdStr.append("'"+pId+"'");
									if( processIds.indexOf(pId) != max){
										pIdStr.append(",");
									}
								}
								pIdStr.append(")");
								Double appAmountSum = cashPoolReportService.getAppAmountSumByProcIds(pIdStr.toString());
								setAppAmountTotal(appAmountSum);
								//实际付款金额
								Double realPaymentAmountSum = cashPoolReportService.getRealPaymentAmountSumByProcIds(pIdStr.toString());
								setRealPaymentAmountTotal(realPaymentAmountSum);
								//剩余需支付金额
								Double lastPaymentAmountSum = cashPoolReportService.getLastPaymentAmountSumByProcIds(pIdStr.toString());
								setLastPaymentAmountTotal(lastPaymentAmountSum);
							}else{
								setAppAmountTotal(0.0);
								setRealPaymentAmountTotal(0.0);
								setLastPaymentAmountTotal(0.0);
							}
							// 合计当前页数据
							conditionMap.remove("appAmountStart");
							conditionMap.remove("appAmountEnd");
						} catch (Exception e) {
							log.error("load匿名内部类中出现异常" , e);
						}
						return cVos;
					}
				};
		} catch (Exception e) {
		    log.error("searchCashpoolDetail方法 查询所有流程出现异常" , e);
		}
	}
	
	/**
	 * 初始化合计信息
	 * @param cVos 
	 */
	private void initAmountTotal(List<CashPoolVo> cVos) {
		try{
			appAmountTotal = 0.0;
			realPaymentAmountTotal = 0.0;
			lastPaymentAmountTotal = 0.0;
			for(CashPoolVo vo : cVos){
				if(vo.getFundsTotal() == null){
					vo.setFundsTotal(0.0);
				}
				if(vo.getLastPaymentAmount() == null){
					vo.setLastPaymentAmount(0.0);
				}
				// 申请金额
				Double total1 = appAmountTotal + vo.getFundsTotal();
				setAppAmountTotal(Math.round(total1*100)/100.0);
				List<Double> realPaymentAmountList = vo.getRealPaymentAmountList();
				// 实际付款金额
				Double total2 = realPaymentAmountTotal;
				// 没有付款记录的 该list为null
				if(realPaymentAmountList != null){
					int max = realPaymentAmountList.size() - 1;
					for(Double realPaymentAmount:realPaymentAmountList){
						// 最后一条为null，为了界面样式而设，不处理
						if(realPaymentAmountList.indexOf(realPaymentAmount) != max){
							total2 += realPaymentAmount;
						}
					}
				}
				setRealPaymentAmountTotal(Math.round(total2*100)/100.0);
				// 剩余需支付金额
				Double total3 = lastPaymentAmountTotal + vo.getLastPaymentAmount();
				setLastPaymentAmountTotal(Math.round(total3*100)/100.0);
		}
		}catch (Exception e) {
			log.error("initAmountTotal方法 初始化合计信息出现异常" , e);
		}
	}



	/**
	 * 初始化查询条件数据
	 */
	public void initQueryCondition(){
		// 默认查出当天申请的流程
		conditionMap.put("appPaymentStartDate",new Date());
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 10);
		c.set(Calendar.MINUTE, 0);
		conditionMap.put("submitEndDate", c.getTime());
		//流程下拉
		processNameSelect = cashPoolReportService.getCashPoolNameSelect();
	}
	


	/**
	 * 将弹出框银行多选控件中选中的值赋给当前控件
	 */
	public void setValueBySelectedBanks(){
		selectedReceiveBankIds.clear();
		receiveBanks.clear();
		List<Bank> banks = bankMultipleSelectBean.getSelectedAllTopBanks();
		for(Bank b : banks){
			// 选中总行id
			selectedReceiveBankIds.add(b.getId());
			receiveBanks.add(b);
		}
	}
	
	/**
	 * 将弹出框公司多选控件中选中的值赋给当前控件
	 */
	public void setValueBySelectedCompanies(){
		selectedReceiveComIds.clear();
		receiveComs.clear();
		List<Company> companies = companyMultipleSelectBean.getSelectedAllCompanies();
		for(Company c : companies){
			selectedReceiveComIds.add(Long.toString(c.getId()));
			// add on 2013-1-31 界面展示效果更改
			receiveComs.add(c);
		}
	}
	
	/**
	 * 将弹出框流程实例多选控件中选中的值赋给当前控件
	 */
	public void setValueBySelectedProcessMaps(){
		selectedInstanceNums.clear();
		instanceNums.clear();
		List<ProcessMap> maps = processNumberSelectBean.getSelectedAllProcessMaps();
		for(ProcessMap map : maps){
			selectedInstanceNums.add(map.getPidTms());
			instanceNums.add(map.getPidTms());
		}
		maps.clear();
	}

	public Map<String, Object> getConditionMap() {
		return conditionMap;
	}

	public void setConditionMap(Map<String, Object> conditionMap) {
		this.conditionMap = conditionMap;
	}

	public List<SelectItem> getProcessNameSelect() {
		return processNameSelect;
	}


	public void setProcessNameSelect(List<SelectItem> processNameSelect) {
		this.processNameSelect = processNameSelect;
	}


	public List<Company> getReceiveComs() {
		return receiveComs;
	}


	public void setReceiveComs(List<Company> receiveComs) {
		this.receiveComs = receiveComs;
	}


	public List<String> getSelectedReceiveComIds() {
		return selectedReceiveComIds;
	}


	public void setSelectedReceiveComIds(List<String> selectedReceiveComIds) {
		this.selectedReceiveComIds = selectedReceiveComIds;
	}


	public List<Bank> getReceiveBanks() {
		return receiveBanks;
	}


	public void setReceiveBanks(List<Bank> receiveBanks) {
		this.receiveBanks = receiveBanks;
	}


	public List<String> getInstanceNums() {
		return instanceNums;
	}


	public void setInstanceNums(List<String> instanceNums) {
		this.instanceNums = instanceNums;
	}


	public List<String> getSelectedInstanceNums() {
		return selectedInstanceNums;
	}


	public void setSelectedInstanceNums(List<String> selectedInstanceNums) {
		this.selectedInstanceNums = selectedInstanceNums;
	}


	public Double getAppAmountTotal() {
		return appAmountTotal;
	}


	public void setAppAmountTotal(Double appAmountTotal) {
		this.appAmountTotal = appAmountTotal;
	}


	public Double getRealPaymentAmountTotal() {
		return realPaymentAmountTotal;
	}


	public void setRealPaymentAmountTotal(Double realPaymentAmountTotal) {
		this.realPaymentAmountTotal = realPaymentAmountTotal;
	}


	public Double getLastPaymentAmountTotal() {
		return lastPaymentAmountTotal;
	}


	public void setLastPaymentAmountTotal(Double lastPaymentAmountTotal) {
		this.lastPaymentAmountTotal = lastPaymentAmountTotal;
	}


	public BankMultipleSelectBean getBankMultipleSelectBean() {
		return bankMultipleSelectBean;
	}


	public void setBankMultipleSelectBean(BankMultipleSelectBean bankMultipleSelectBean) {
		this.bankMultipleSelectBean = bankMultipleSelectBean;
	}


	public CompanyMultipleSelectBean getCompanyMultipleSelectBean() {
		return companyMultipleSelectBean;
	}


	public void setCompanyMultipleSelectBean(CompanyMultipleSelectBean companyMultipleSelectBean) {
		this.companyMultipleSelectBean = companyMultipleSelectBean;
	}


	public ProcessNumberSelectBean getProcessNumberSelectBean() {
		return processNumberSelectBean;
	}


	public void setProcessNumberSelectBean(ProcessNumberSelectBean processNumberSelectBean) {
		this.processNumberSelectBean = processNumberSelectBean;
	}

	public LazyDataModel<CashPoolVo> getCashPoolVos() {
		return cashPoolVos;
	}

	public void setCashPoolVos(LazyDataModel<CashPoolVo> cashPoolVos) {
		this.cashPoolVos = cashPoolVos;
	}

	

	

}
