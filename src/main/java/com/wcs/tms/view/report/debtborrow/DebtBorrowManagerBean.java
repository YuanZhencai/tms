package com.wcs.tms.view.report.debtborrow;

import java.util.ArrayList;
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
import com.wcs.tms.model.DebtContract;
import com.wcs.tms.model.ProcDebtBorrow;
import com.wcs.tms.model.ProcessMap;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.debtpayment.RegiDebtManageService;
import com.wcs.tms.service.report.debtborrow.DebtBorrowReportService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.CompanySelectBean;
import com.wcs.tms.view.process.common.FileUpload;
import com.wcs.tms.view.process.common.entity.DebtRequestVo;
import com.wcs.tms.view.process.common.entity.RegiDebtConfirmVo;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:外债申请明细表Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yuanzhencai@wcs-global.com">Yuan</a>
 */
@ManagedBean
@ViewScoped
public class DebtBorrowManagerBean extends FileUpload<ProcDebtBorrow> {

	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(DebtBorrowManagerBean.class);
	@Inject
	EntityService entityService;
	@Inject
	DebtBorrowReportService debtBorrowReportService;
	@Inject
	LoginService loginService;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	ProcessUtilMapService processUtilMapService;
	@Inject
	RegiDebtManageService regiDebtManageService;

	// 查询条件
	private Map<String, Object> conditionMap = new HashMap<String, Object>();
	// 外债列表
	private List<ProcDebtBorrow> procDebtBorrows = new ArrayList<ProcDebtBorrow>();
	private LazyDataModel<DebtRequestVo> debtBorrowRequestVOModel = null;
	// 申请金额之和
	private Double corpAuditSum = 0d;
	// 公司下拉菜单
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
	/** 资金币种下拉 */
	private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
	
	@ManagedProperty(value = "#{companySelectBean}")
	private CompanySelectBean companySelectBean;
	@Inject
	private CommonBean dictBean;
	
	private Company company;
	// 判断是否是集团用户
	private Boolean isCopUser = false;
	// 外债申请确认
	private RegiDebtConfirmVo confirmVo = new RegiDebtConfirmVo();
	private ProcDebtBorrow debtBorrow;

	/**
	 * <p>Description:Bean init </p>
	 */
	@PostConstruct
	public void initDebtBorrowReport() {
		if (loginService.isCopUser()) {
			companySelectBean.initCompany();
			setIsCopUser(true);
		} else {
			initCompany(false);
			setIsCopUser(false);
		}

		if (procDebtBorrows.size() == 0) {
			searchDebtBorrowRequestDetail();
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
	 */
	public void initCompany(boolean all) {
		if (all) {
			companySelect = companyTmsService.getAllCompanySelect();
		} else {
			companySelect = companyTmsService.getCompanySelect();
		}

	}

	/**
	 * 查询已完成的外债申请明细表
	 */
	public void searchDebtBorrow() {
		if (isCopUser == true) {
			conditionMap = companySelectBean.getFilterMap(company, conditionMap);
		} else {
			List<String> companyIds = new ArrayList<String>();
			for (SelectItem item : companySelect) {
				companyIds.add(String.valueOf(item.getValue()));
			}
			conditionMap.put("companyIds", companyIds);
		}
		procDebtBorrows = debtBorrowReportService.getProcDebtBorrowList(conditionMap);
		corpAuditSum = 0d;
		for (ProcDebtBorrow db : procDebtBorrows) {
			// 2012-10-16 add by liushengbin start
			// 把fn的流程实例编号翻译成tms的短编号，性能不是很好,后续应该优化
			String pidFn = db.getProcInstId();
			ProcessMap pm = processUtilMapService.getProcessMapByFnId(pidFn);
			if (pm != null && !StringUtils.isBlankOrNull(pm.getPidTms())) {
				db.setProcInstId(pm.getPidTms());
			}
			// 2012-10-16 add by liushengbin end
			corpAuditSum = corpAuditSum + (db.getCorpAudit() == null ? 0d : db.getCorpAudit());
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
	public LazyDataModel<DebtRequestVo> findDebtBorrowRequest(final Map<String, Object> conditionMap) {
		LazyDataModel<DebtRequestVo> lazyDataModel = new LazyDataModel<DebtRequestVo>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<DebtRequestVo> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
				Map<String, Object> map = new HashMap<String, Object>();
				map = debtBorrowReportService.findDebtBorrowRequestDetail(first, pageSize, sortField, sortOrder, conditionMap);
				List<DebtRequestVo> list = new ArrayList<DebtRequestVo>();
				if (map.size() != 0) {
					list = (List<DebtRequestVo>) map.get("list");
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
		String viewPage = "/faces/process/foreignDebtRequests/debtBorrow-view.xhtml";
		// 设置弹出窗口url的参数
		RequestContext.getCurrentInstance().addCallbackParam("viewPage", StringUtils.safeString(JSFUtils.contextPath() + viewPage));
		RequestContext.getCurrentInstance().addCallbackParam("op", "view");
		RequestContext.getCurrentInstance().addCallbackParam("menu2", StringUtils.safeString(JSFUtils.getParamValue("menu2")));
		RequestContext.getCurrentInstance().addCallbackParam("procInstId", procInstId);
		RequestContext.getCurrentInstance().addCallbackParam("stepName", "");
		RequestContext.getCurrentInstance().addCallbackParam("isPatch", "");
		
	}
	
	public void initConfirmRegiDebt(){
		confirmVo  = new RegiDebtConfirmVo();
		confirmVo.setDebtBorrow(debtBorrow);
		confirmVo.setContractAccount(debtBorrow.getCorpAudit());
		confirmVo.setCurrency(debtBorrow.getCorpAuditCu());
		confirmVo.setInterestRate(debtBorrow.getCorpAuditRa() == null ? 0d : Double.valueOf(debtBorrow.getCorpAuditRa()));
		confirmVo.setBorrowStartDate(debtBorrow.getCorpAuditLis());
		confirmVo.setBorrowStartDate(debtBorrow.getCorpAuditLie());
		if(debtBorrow.getDebtContractId() != null) {
			DebtContract debtContract = entityService.find(DebtContract.class, debtBorrow.getDebtContractId());
			confirmVo.setDebtContract(debtContract);
			confirmVo.setContractNo(debtContract.getDebtContractNo());
		}
		confirmVo.setFiller(loginService.getCurrentUserName());
		confirmVo.setRegistrant(loginService.getCurrentUserName());
		displayDetailAttach(debtBorrow.getProcInstId());
	}
	
	public void confirmRegiDebt(){
		try {
			if(confirmVo.getDebtContract() == null) {
				MessageUtils.addErrorMessage("msg", "数据错误：没有关联合同。");
				return;
			}
			regiDebtManageService.confirmRegiDebt(confirmVo);
			// 保存流程附件
			this.saveProcessFile(confirmVo.getDebtBorrow().getProcInstId());
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("widgetVar", "debtConfirmDialogVar");
			context.addCallbackParam("option", "close");
			MessageUtils.addSuccessMessage("msg", "操作成功。");
		} catch (Exception e) {
			MessageUtils.addErrorMessage("msg", "外债合同信息报备登记失败，请重新操作。");
			log.error(e.getMessage(), e);
		}
	}
	
	/******set@get*******************************************************/
	public Double getCorpAuditSum() {
		return corpAuditSum;
	}

	public void setCorpAuditSum(Double corpAuditSum) {
		this.corpAuditSum = corpAuditSum;
	}

	public List<ProcDebtBorrow> getProcDebtBorrows() {
		return procDebtBorrows;
	}

	public void setProcDebtBorrows(List<ProcDebtBorrow> procDebtBorrows) {
		this.procDebtBorrows = procDebtBorrows;
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

	public CompanySelectBean getCompanySelectBean() {
		return companySelectBean;
	}

	public void setCompanySelectBean(CompanySelectBean companySelectBean) {
		this.companySelectBean = companySelectBean;
	}

	public LazyDataModel<DebtRequestVo> getDebtBorrowRequestVOModel() {
		return debtBorrowRequestVOModel;
	}

	public void setDebtBorrowRequestVOModel(LazyDataModel<DebtRequestVo> debtBorrowRequestVOModel) {
		this.debtBorrowRequestVOModel = debtBorrowRequestVOModel;
	}

	public RegiDebtConfirmVo getConfirmVo() {
		return confirmVo;
	}

	public void setConfirmVo(RegiDebtConfirmVo confirmVo) {
		this.confirmVo = confirmVo;
	}

	public ProcDebtBorrow getDebtBorrow() {
		return debtBorrow;
	}

	public void setDebtBorrow(ProcDebtBorrow debtBorrow) {
		this.debtBorrow = debtBorrow;
	}

	public List<SelectItem> getCurrencySelect() {
		return currencySelect;
	}

	public void setCurrencySelect(List<SelectItem> currencySelect) {
		this.currencySelect = currencySelect;
	}

}
