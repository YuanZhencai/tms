package com.wcs.tms.view.process.common;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import com.wcs.base.util.JSFUtils;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Company;
import com.wcs.tms.service.process.common.ProcessSubedService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.service.system.sysprocess.ProcessDefineService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.entity.ProcessInstanceVo;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 已提交流程Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@ManagedBean
@ViewScoped
public class ProcessSubedBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(ProcessSubedBean.class);

	@Inject
	EntityService entityService;
	@Inject
	ProcessSubedService processSubedService;
	@Inject
	LoginService loginService;
	@Inject
	ProcessDefineService processDefineService;
	@Inject
	CompanyTmsService companyTmsService;

	// 已提交流程列表VO
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
	public void initSubed() {
		log.info("initProcessSubedBean~~~~~~~~~~~~~~");
		initProcess();
		if (loginService.isCopUser()) {
			companySelectBean.initCompany();
			setIsCopUser(true);
		} else {
			initCompany(false);
			setIsCopUser(false);
		}
		if (processInstanceVos.size() == 0) {
			searchSubedProcessInstance(); /* 默认查询所有已提交的流程 */
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
	 * 初始化流程名称下拉菜单
	 */
	public void initProcess() {
		processNameSelect = processDefineService.getProcessNameSelect();
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
	 * 查询已提交流程
	 */
	public void searchSubedProcessInstance() {
		try {
			if (isCopUser == true) {
				conditionMap = companySelectBean.getFilterMap(company, conditionMap);
			} else {
				List<String> companyIds = new ArrayList<String>();
				for (SelectItem item : companySelect) {
					companyIds.add(String.valueOf(item.getValue()));
				}
				conditionMap.put("companyIds", companyIds);
				log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				for (String s : companyIds) {
					log.info("ssssssssssssssssssss:" + s);
				}
			}
			processInstanceVos = processSubedService.getProcessInstanceVos(conditionMap);
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
	 * 终止流程实例
	 */
	public void doTerminal() {
		try {
			processSubedService.doTerminal(processInstanceVo);
			MessageUtils.addSuccessMessage("doneMsg", MessageUtils.getMessage("txt_process_end_success", processInstanceVo.getPidTms()));
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
		// 再查询一次
		searchSubedProcessInstance();
	}

	/**
	 * 到流程实例详细页面
	 * @return
	 */
	public void toView() {
		JSFUtils.getRequest().setAttribute("op", "view");
		JSFUtils.getRequest().setAttribute("menu2", JSFUtils.getRequestParam("menu2"));
		JSFUtils.getRequest().setAttribute("procInstId", processInstanceVo.getProcInstId());
		JSFUtils.getRequest().setAttribute("stepName", processInstanceVo.getNodeName());
		JSFUtils.getRequest().setAttribute("isPatch", processInstanceVo.getIsPatch());
		String viewPage = getViewPage(processInstanceVo);
		// 设置弹出窗口url的参数
		RequestContext.getCurrentInstance().addCallbackParam("viewPage", JSFUtils.contextPath() + viewPage);
		RequestContext.getCurrentInstance().addCallbackParam("op", "view");
		RequestContext.getCurrentInstance().addCallbackParam("menu2", JSFUtils.getRequestParam("menu2"));
		RequestContext.getCurrentInstance().addCallbackParam("procInstId",
				processInstanceVo.getProcInstId() == null ? "" : processInstanceVo.getProcInstId());
		RequestContext.getCurrentInstance().addCallbackParam("stepName",
				processInstanceVo.getNodeName() == null ? "" : processInstanceVo.getNodeName());
		RequestContext.getCurrentInstance().addCallbackParam("isPatch",processInstanceVo.getIsPatch());
	}

	public String getParameter(String key) {
		return JSFUtils.getRequestParam(key);
	}

	/**
	 * 得到流程详细页面
	 * @param processDefineCode
	 * @return
	 */
	private String getViewPage(ProcessInstanceVo processInstanceVo) {
		String processPage = "";
		// modify on 7.24(因为特殊处理集团授信流程改成下面方式)
		// 是否查看确认单View
		String confirmView = ProcessXmlUtil.findStepProperty("code", processInstanceVo.getProcessCode(), processInstanceVo.getNodeName(),
				"confirmView");
		// 是否有确认单查看URL
		if (confirmView != null && "true".equals(confirmView)) {
			processPage = ProcessXmlUtil.getProcessAttribute("code", processInstanceVo.getProcessCode(), "viewConfirmPage");
		} else {
			if ((null == processInstanceVo.getNodeName() || "".equals(processInstanceVo.getNodeName())) && processInstanceVo.getUserComfirmView()) {
				processPage = ProcessXmlUtil.getProcessAttribute("code", processInstanceVo.getProcessCode(), "viewConfirmPage");
			}
			// 如果没有确认详细页面就得到普通详细页面URL
			if (processPage == null || "".equals(processPage)) {
				processPage = ProcessXmlUtil.getProcessAttribute("code", processInstanceVo.getProcessCode(), "viewPage");
			}

		}
		return processPage;
	}

	/***********************************set@get**************************/
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
