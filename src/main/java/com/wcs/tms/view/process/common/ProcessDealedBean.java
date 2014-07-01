package com.wcs.tms.view.process.common;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.primefaces.context.RequestContext;

import com.google.common.collect.Lists;
import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.StringUtils;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Company;
import com.wcs.tms.service.process.common.ProcessDealedService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.service.system.sysprocess.ProcessDefineService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.entity.ProcessInstanceVo;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:已处理任务Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@ManagedBean
@ViewScoped
public class ProcessDealedBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	EntityService entityService;
	@Inject
	ProcessDealedService processDealedService;
	@Inject
	LoginService loginService;
	@Inject
	ProcessDefineService processDefineService;
	@Inject
	CompanyTmsService companyTmsService;
	
	private static final Log log = LogFactory.getLog(ProcessDealedBean.class);

	// 已处理任务的流程列表VO
	private List<ProcessInstanceVo> processInstanceVos = Lists.newArrayList();
	// 查询条件
	private Map<String, Object> conditionMap = new HashMap<String, Object>();
	// 流程实例VO对象
	private ProcessInstanceVo processInstanceVo = new ProcessInstanceVo();
	// 流程名称
	private List<SelectItem> processNameSelect = new ArrayList<SelectItem>();
	// 公司下拉菜单
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
	@ManagedProperty(value = "#{companySelectBean}")
	private CompanySelectBean companySelectBean;
	private Company company;
	// 判断是否是集团用户
	private Boolean isCopUser = false;

	/**
	 * <p>Description:Bean init </p>
	 */
	@PostConstruct
	public void initDealed() {
		// add on 2013-8-12 by yan
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		conditionMap.put("startDate", DateUtil.adjustYearAndMonthAndDay(now.getTime(), 0, 0, -3));
//		conditionMap.put("endDate", DateUtil.adjustYearAndMonthAndDay(now, 0, 0, 3));
		log.info("initProcessDealedBean~~~~~~~~~~~~~~");
		initProcess();
		if (loginService.isCopUser()) {
			companySelectBean.initCompany();
			setIsCopUser(true);
		} else {
			initCompany(false);
			setIsCopUser(false);
		}
		if (processInstanceVos.size() == 0) {
			searchDealedProcessInstance(); /* 默认查询所有已提交的流程 */
		}
	}

	public void clear() {
		log.info("页面bean清空缓存");
		company = new Company();
		companySelectBean.clear();
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
	 * 初始化流程名称下拉菜单
	 */
	public void initProcess() {
		processNameSelect = processDefineService.getProcessNameSelect();
	}

	/**
	 * 查询已处理任务的流程
	 */
	public void searchDealedProcessInstance() {
		try {
			long startTime = System.currentTimeMillis();
			if (isCopUser == true) {
				conditionMap = companySelectBean.getFilterMap(company, conditionMap);
			} else {
				List<String> companyIds = new ArrayList<String>();
				for (SelectItem item : companySelect) {
					companyIds.add(String.valueOf(item.getValue()));
				}
				conditionMap.put("companyIds", companyIds);
			}
			long copUserTime = System.currentTimeMillis();
			processInstanceVos = processDealedService.getProcessInstanceVos(conditionMap);
			long endTime = System.currentTimeMillis();
			log.info("|||||||||||||已处理列表处理总共耗时："+(endTime-startTime));
			log.info("|||||||||||||集团处理耗时："+(copUserTime-startTime));
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
	 * 到流程实例详细页面
	 * @return
	 */
	public void toView() {
		JSFUtils.getRequest().setAttribute("op", "view");
		JSFUtils.getRequest().setAttribute("menu2", JSFUtils.getParamValue("menu2"));
		JSFUtils.getRequest().setAttribute("procInstId", processInstanceVo.getProcInstId());
		JSFUtils.getRequest().setAttribute("stepName", processInstanceVo.getNodeName());
		JSFUtils.getRequest().setAttribute("isPatch", processInstanceVo.getIsPatch());
		String viewPage = getViewPage(processInstanceVo);
		// 设置弹出窗口url的参数
		RequestContext.getCurrentInstance().addCallbackParam("viewPage", StringUtils.safeString(JSFUtils.contextPath() + viewPage));
		RequestContext.getCurrentInstance().addCallbackParam("op", "view");
		RequestContext.getCurrentInstance().addCallbackParam("menu2", StringUtils.safeString(JSFUtils.getParamValue("menu2")));
		RequestContext.getCurrentInstance().addCallbackParam("procInstId", StringUtils.safeString(processInstanceVo.getProcInstId()));
		RequestContext.getCurrentInstance().addCallbackParam("stepName", StringUtils.safeString(processInstanceVo.getNodeName()));
		RequestContext.getCurrentInstance().addCallbackParam("isPatch",processInstanceVo.getIsPatch());
		// log.info("viewPage::"+viewPage);
		// return viewPage;
	}

	/**
	 * 得到流程详细页面
	 * @param processDefineCode
	 * @return
	 */
	private String getViewPage(ProcessInstanceVo processInstanceVo) {
		String processPage = "";
		// String xmlPath = processInstanceVo.getXmlPath();
		// processPage = ProcessXmlUtil.getProcessAttribute("code",processInstanceVo.getProcessCode(), "viewPage");
		// modify on 7.24(因为特殊处理集团授信流程改成下面方式)
		// 是否查看确认单View
		String confirmView = ProcessXmlUtil.findStepProperty("code", processInstanceVo.getProcessCode(), processInstanceVo.getNodeName(),
				"confirmView");
		// 是否有确认单查看URL
		if (confirmView != null && "true".equals(confirmView) && processInstanceVo.getUserComfirmView()) {
			processPage = ProcessXmlUtil.getProcessAttribute("code", processInstanceVo.getProcessCode(), "viewConfirmPage");
		}
		// 如果没有确认详细页面就得到普通详细页面URL
		if (processPage == null || "".equals(processPage)) {
			processPage = ProcessXmlUtil.getProcessAttribute("code", processInstanceVo.getProcessCode(), "viewPage");
		}

		// if(xmlPath!=null && !"".equals(xmlPath)){
		// processPage = ProcessXmlUtil.getProcessAttribute("code",processInstanceVo.getProcessCode(), "viewPage1");
		// }
		return processPage;
	}

	/***************************set@get**********************************/
	public List<ProcessInstanceVo> getProcessInstanceVos() {
		return processInstanceVos;
	}

	public void setProcessInstanceVos(List<ProcessInstanceVo> processInstanceVos) {
		this.processInstanceVos = processInstanceVos;
	}

	public Map<String, Object> getConditionMap() {
		return conditionMap;
	}

	public void setConditionMap(Map<String, Object> conditionMap) {
		this.conditionMap = conditionMap;
	}

	public ProcessInstanceVo getProcessInstanceVo() {
		return processInstanceVo;
	}

	public void setProcessInstanceVo(ProcessInstanceVo processInstanceVo) {
		this.processInstanceVo = processInstanceVo;
	}

	public List<SelectItem> getProcessNameSelect() {
		return processNameSelect;
	}

	public void setProcessNameSelect(List<SelectItem> processNameSelect) {
		this.processNameSelect = processNameSelect;
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

}
