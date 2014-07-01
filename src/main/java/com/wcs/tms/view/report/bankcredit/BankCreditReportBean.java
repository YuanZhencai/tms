package com.wcs.tms.view.report.bankcredit;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.StringUtils;
import com.wcs.base.view.ViewBaseBean;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.Credit;
import com.wcs.tms.model.ProcessMap;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.report.bankcredit.BankCreditReportService;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.CompanySelectBean;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:授信额度明细表Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@ManagedBean
@ViewScoped
public class BankCreditReportBean extends ViewBaseBean<Credit> {

	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(BankCreditReportBean.class);
	@Inject
	EntityService entityService;
	@Inject
	BankCreditReportService bankCreditReportService;
	@Inject
	LoginService loginService;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	ProcessUtilMapService processUtilMapService;
	@Inject
	BankService bankService;

	// 查询条件
	private Map<String, Object> conditionMap = new HashMap<String, Object>();
	// 授信实体
	private List<Credit> credits = new ArrayList<Credit>();
	// 授信金额之和
	private Double creditSum = 0d;

	// 公司下拉菜单
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
	// 分支银行下拉菜单
	private List<SelectItem> childBankSelect = new ArrayList<SelectItem>();
	@ManagedProperty(value = "#{companySelectBean}")
	private CompanySelectBean companySelectBean;
	private Company company;
	// 判断是否是集团用户
	private Boolean isCopUser = false;
	private Boolean doDisplay = true;

	/**
	 * <p>Description:Bean init </p>
	 */
	@PostConstruct
	public void initBankCreditReport() {
		// 默认查询到期时间为大于大于当天
		getConditionMap().put("creditEndS", new Date());
		if (loginService.isCopUser()) {
			companySelectBean.initCompany();
			setIsCopUser(true);
		} else {
			initCompany(false);
			setIsCopUser(false);
		}
		initBankSelect();
		log.info("initBankCreditReport~~~~~~~~~~~~~~");
		if (credits.size() == 0) {
			searchBankCreditDetail();
		}
	}

	/**
	 * 初始化总行下拉
	 */
	private void initBankSelect() {
		childBankSelect = bankService.getTopLevelSelect();
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
	 * 公司得到已授信分支银行
	 */
	public void ajaxChildBank() {
		if (!isCopUser) {
			String companyId = (String) conditionMap.get("companyId");
			if (StringUtils.isBlankOrNull(companyId)) {
				return;
			}
			// modified by yanchangjing on 2013-2-25
			childBankSelect = bankCreditReportService.getCreditTopBankSelect(Long.valueOf(companyId));
		}else{
			if (StringUtils.isBlankOrNull(company.getCompanyName())) {
				return;
			}
			// modified by yanchangjing on 2013-2-25
			long companyId = companyTmsService.getCompanyName(String.valueOf(company.getCompanyName()));
			childBankSelect = bankCreditReportService.getCreditTopBankSelect(companyId);
		}
	}

	/**
	 * 查询授信明细列表
	 */
	public void searchBankCreditDetail() {
		try {
			if (isCopUser == true) {
				conditionMap = companySelectBean.getFilterMap(company, conditionMap);
			} else {
				List<String> companyIds = new ArrayList<String>();
				for (SelectItem item : companySelect) {
					companyIds.add(String.valueOf(item.getValue()));
				}
				conditionMap.put("companyIds", companyIds);
			}
			creditSum = 0d;
			credits = bankCreditReportService.findBankCreditDetail(conditionMap,doDisplay);
			for (Credit c : credits) {
				// 2012-10-16 add by liushengbin start
				// 把fn的流程实例编号翻译成tms的短编号，性能不是很好,后续应该优化
				String pidFn = c.getProcInstId();
				ProcessMap pm = processUtilMapService.getProcessMapByFnId(pidFn);
				if (pm != null && !StringUtils.isBlankOrNull(pm.getPidTms())) {
					c.setProcInstId(pm.getPidTms());
				}
				// 2012-10-16 add by liushengbin end
				creditSum = creditSum + (c.getCreditLine() == null ? 0d : c.getCreditLine());
			}
		} catch (ServiceException e) {
			MessageUtils.addErrorMessage("doneMsg", e.getMessage());
		} catch (Exception e) {
			if (e instanceof EJBException) {
				ServiceException se = (ServiceException) ((EJBException) e).getCause();
				MessageUtils.addErrorMessage("doneMsg", se.getMessage());
			}
			if (e instanceof InvocationTargetException) {
				ServiceException se = (ServiceException) ((InvocationTargetException) e).getTargetException().getCause();
				MessageUtils.addErrorMessage("doneMsg", se.getMessage());
			}
		}
	}
	
	/**
	 * 获取总行名称
	 * @param child
	 * @return
	 */
	public String getParentBankName(Bank child){
		Bank parent = bankService.getParentBank(child);
		return parent.getBankName();
	}

	/**************************setter、getter方法*************************/
	public Double getCreditSum() {
		return creditSum;
	}

	public void setCreditSum(Double creditSum) {
		this.creditSum = creditSum;
	}

	public Map<String, Object> getConditionMap() {
		return conditionMap;
	}

	public void setConditionMap(Map<String, Object> conditionMap) {
		this.conditionMap = conditionMap;
	}

	public List<Credit> getCredits() {
		return credits;
	}

	public void setCredits(List<Credit> credits) {
		this.credits = credits;
	}

	public List<SelectItem> getCompanySelect() {
		return companySelect;
	}

	public void setCompanySelect(List<SelectItem> companySelect) {
		this.companySelect = companySelect;
	}

	public List<SelectItem> getChildBankSelect() {
		return childBankSelect;
	}

	public void setChildBankSelect(List<SelectItem> childBankSelect) {
		this.childBankSelect = childBankSelect;
	}

	public Boolean getIsCopUser() {
		return isCopUser;
	}

	public void setIsCopUser(Boolean isCopUser) {
		this.isCopUser = isCopUser;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public CompanySelectBean getCompanySelectBean() {
		return companySelectBean;
	}

	public void setCompanySelectBean(CompanySelectBean companySelectBean) {
		this.companySelectBean = companySelectBean;
	}

	public Boolean getDoDisplay() {
		return doDisplay;
	}

	public void setDoDisplay(Boolean doDisplay) {
		this.doDisplay = doDisplay;
	}

}
