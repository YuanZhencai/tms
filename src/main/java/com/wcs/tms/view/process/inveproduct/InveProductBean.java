package com.wcs.tms.view.process.inveproduct;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.JSFUtils;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.Credit;
import com.wcs.tms.model.ProcInveProduct;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessUtilService;
import com.wcs.tms.service.process.common.patch.service.PatchMainService;
import com.wcs.tms.service.process.inveproduct.InveProductService;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.FileUpload;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

/**
 * 
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description:投资理财流程Bean
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@ManagedBean
@ViewScoped
public class InveProductBean extends FileUpload<ProcInveProduct> {

	private static final long serialVersionUID = 1L;

	private Log log = LogFactory.getLog(InveProductBean.class);

	@Inject
	EntityService entityService;
	@Inject
	CommonBean dictBean;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	InveProductService inveProductService;
	@Inject
	BankService bankService;
	// 9.5
	@Inject
	ProcessUtilService processUtilService;
	// 9.11
	@Inject
	ProcessUtilMapService processUtilMapService;
	@Inject
	PatchMainService patchMainService;
	@Inject
    PEManager peManager;

	// 投资理财流程Obj
	private ProcInveProduct procInveProduct = new ProcInveProduct();
	// 一级银行对象
	private Bank parentBank = new Bank();

	// 公司下拉菜单
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
	// 分支银行下拉菜单
	private List<SelectItem> childBankSelect = new ArrayList<SelectItem>();
	/** 理财产品类型下拉 */
	private List<SelectItem> prodTypeSelect = new ArrayList<SelectItem>();
	/** 期限单位下拉 */
	private List<SelectItem> limitUnitSelect = new ArrayList<SelectItem>();
	/** 资金币种下拉 */
	private List<SelectItem> currencySelect = new ArrayList<SelectItem>();

	// 流程实例ID
	private String procInstId;

	// 流程步骤名称
	private String stepName;

	// 流程详细vo列表
	private List<ProcessDetailVo> detailVos = new ArrayList<ProcessDetailVo>();

	// 审批状态(通过与否)
	private String approveStatus;

	// 得到可输入的审批字段
	private List<String> inputableFields = new ArrayList<String>();
	// 二级菜单参数
	private String menuTwo;

	// 是否处理下一个任务9.5
	private boolean doNext = true;
	// 当前所处理任务在任务列表中的位置
	private Integer currentIndex;
	// 当前处理任务类型（已接受和待接收）
	private String currentTaskType;
	// 是否是补丁表中获取到数据查看，处理2014.4.11~2014.4.28之间发生故障，丢失的流程数据和流程审批日志的查看
	private String isPatch;

	/**
	 * <p>
	 * Description:Bean init
	 * </p>
	 */
	@PostConstruct
	public void initInveProduct() {
		log.info("InveProductBean~~~~~~~~~~~~~~");
		initDict();
		initOp();
	}

	/**
	 * 初始化操作类型
	 */
	public void initOp() {
		String op = (String) JSFUtils.getRequest().getAttribute("op");
		if (StringUtils.isEmpty(op)) {
			op = JSFUtils.getRequestParam("op");
		}
		if (op != null && !"".equals(op)) {
			// 查看表单详细
			if ("view".equals(op)) {
				isPatch = JSFUtils.getParamValue("isPatch");
				menuTwo = JSFUtils.getRequestParam("menu2");
				if (StringUtils.isEmpty(menuTwo)) {
					menuTwo = JSFUtils.getRequestParam("menu2");
				}
				procInstId = (String) JSFUtils.getRequest().getAttribute(
						"procInstId");
				if (StringUtils.isEmpty(procInstId)) {
					procInstId = JSFUtils.getRequestParam("procInstId");
				}
				this.findProcInstance();
				initCompany(true);
			}
			// 到审批页面
			if ("approve".equals(op)) {
				// add on 2013-5-17
				currentIndex = Integer.parseInt(JSFUtils
						.getParamValue("currentIndex"));
				currentTaskType = (String) JSFUtils
						.getParamValue("currentTaskType");

				procInstId = (String) JSFUtils.getRequest().getAttribute(
						"procInstId");
				stepName = (String) JSFUtils.getRequest().getAttribute(
						"stepName");
				this.getInputable();
				this.findProcInstance();
				initCompany(true);
				if (!"申请".equals(stepName)) {
					procInveProduct.setPeMemo("同意");
				}
			}
		} else {
			initCompany(true);
		}
	}

	/**
	 * 初始化数据字典
	 */
	public void initDict() {
		prodTypeSelect = dictBean
				.getDictByCode(DictConsts.TMS_INVE_PRODUCT_TYPE);
		limitUnitSelect = dictBean
				.getDictByCode(DictConsts.TMS_LIMIT_DATE_UNIT_TYPE);
		currencySelect = dictBean
				.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
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
		childBankSelect = inveProductService
				.getCreditBankSelect(procInveProduct.getCompany().getId());
		if (childBankSelect.size() != 0) {
			SelectItem si = childBankSelect.get(0);
			Bank b = procInveProduct.getBank();
			if (b == null || b.getId() == null) {
				procInveProduct.getBank().setId((Long) si.getValue());
			}
			this.ajaxParentBank();
		}
	}

	/**
	 * 分支银行得到一级银行,并级联分支银行英文和授信
	 */
	public void ajaxParentBank() {
		Bank childBank = entityService.find(Bank.class, procInveProduct
				.getBank().getId());
		procInveProduct.setBank(childBank);
		parentBank = bankService.getParentBank(childBank);
		Credit c = inveProductService.getCredit(procInveProduct.getCompany()
				.getId(), childBank.getId());
		procInveProduct.setBankCredit(c.getCreditLine());
		procInveProduct.setBankCreditCu(c.getCreditLineCu());
	}

	/**
	 * 流程创建保存
	 */
	public String createProcInstance() {
		Company company = entityService.find(Company.class, procInveProduct
				.getCompany().getId());
		procInveProduct.setCompany(company);

		try {
			String procInstId = inveProductService
					.createProcInstance(procInveProduct);
			// 保存流程附件
			this.saveProcessFile(procInstId);
			// modified by yanchangjing on 2012-8-1
			MessageUtils.addSuccessMessage("doneMsg", MessageUtils.getMessage(
					"process_new_success",
					processUtilMapService.getTmsIdByFnId(procInstId)));
		} catch (ServiceException e) {
			MessageUtils.addErrorMessage("doneMsg", e.getMessage());
		} catch (Exception e) {
			if (e instanceof EJBException) {
				ServiceException se = (ServiceException) ((EJBException) e)
						.getCause();
				MessageUtils.addErrorMessage("doneMsg", se.getMessage());
			}
			if (e instanceof InvocationTargetException) {
				ServiceException se = (ServiceException) ((InvocationTargetException) e)
						.getTargetException().getCause();
				MessageUtils.addErrorMessage("doneMsg", se.getMessage());
			}
		}
		return "/faces/process/common/processSubed-list.xhtml";
	}

	/**
	 * 查询流程实例
	 */
	public void findProcInstance() {
		procInveProduct = inveProductService
				.findProcInstanceByProcInstId(procInstId);
		// 查询流程附件
		displayDetailAttach(procInveProduct.getProcInstId());
		searchProcessDetail();
		ajaxChildBank();
	}

	/**
	 * 查询流程详细步骤
	 */
	private void searchProcessDetail() {
		if ("true".equals(isPatch)) {
			detailVos = patchMainService.getProcessDetailFor411(procInstId);
		} else {
			detailVos = peManager.getProcessDetail(procInstId);
		}
	}

	/**
	 * 得到可输入的审批字段
	 */
	private void getInputable() {
		inputableFields = ProcessXmlUtil.getInputableDatas("InveProduct",
				stepName);
	}

	/**
	 * 字段可输入检查
	 * 
	 * @return
	 */
	public boolean checkInputable(String fieldName) {
		if (inputableFields != null && inputableFields.contains(fieldName)) {
			return true;
		}
		return false;
	}

	/**
	 * 审批保存
	 * 
	 * @return
	 */
	public String doApprove() {
		boolean pass = false;
		if (approveStatus != null) {
			if ("Y".equals(approveStatus)) {
				pass = true;
			}
			try {
				String upload = ProcessXmlUtil.findStepProperty("id",
						"InveProduct", stepName, "upload");
				if (upload != null && "true".equals(upload)) {
					// 保存流程附件
					this.saveProcessFile(procInstId);
				}
				inveProductService.doApprove(procInveProduct, pass, stepName);
				// modified by yanchangjing on 2012-8-1
				MessageUtils.addSuccessMessage(
						"doneMsg",
						stepName
								+ MessageUtils.getMessage(
										"process_save_success",
										processUtilMapService
												.getTmsIdByFnId(procInstId)));
			} catch (ServiceException e) {
				MessageUtils.addErrorMessage("doneMsg", e.getMessage());
			} catch (Exception e) {
				if (e instanceof EJBException) {
					ServiceException se = (ServiceException) ((EJBException) e)
							.getCause();
					MessageUtils.addErrorMessage("doneMsg", se.getMessage());
				}
				if (e instanceof InvocationTargetException) {
					ServiceException se = (ServiceException) ((InvocationTargetException) e)
							.getTargetException().getCause();
					MessageUtils.addErrorMessage("doneMsg", se.getMessage());
				}
			}
		}
		// 9.5处理下一个任务
		return processUtilService.getNextPage(
				"/faces/process/common/processDealed-list.xhtml", doNext,
				getCurrentIndex(), getCurrentTaskType());
	}

	public Bank getParentBank() {
		return parentBank;
	}

	public void setParentBank(Bank parentBank) {
		this.parentBank = parentBank;
	}

	public List<SelectItem> getChildBankSelect() {
		return childBankSelect;
	}

	public void setChildBankSelect(List<SelectItem> childBankSelect) {
		this.childBankSelect = childBankSelect;
	}

	public List<SelectItem> getCompanySelect() {
		return companySelect;
	}

	public void setCompanySelect(List<SelectItem> companySelect) {
		this.companySelect = companySelect;
	}

	public ProcInveProduct getProcInveProduct() {
		return procInveProduct;
	}

	public void setProcInveProduct(ProcInveProduct procInveProduct) {
		this.procInveProduct = procInveProduct;
	}

	public List<SelectItem> getProdTypeSelect() {
		return prodTypeSelect;
	}

	public void setProdTypeSelect(List<SelectItem> prodTypeSelect) {
		this.prodTypeSelect = prodTypeSelect;
	}

	public List<SelectItem> getLimitUnitSelect() {
		return limitUnitSelect;
	}

	public void setLimitUnitSelect(List<SelectItem> limitUnitSelect) {
		this.limitUnitSelect = limitUnitSelect;
	}

	public List<SelectItem> getCurrencySelect() {
		return currencySelect;
	}

	public void setCurrencySelect(List<SelectItem> currencySelect) {
		this.currencySelect = currencySelect;
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public List<ProcessDetailVo> getDetailVos() {
		return detailVos;
	}

	public void setDetailVos(List<ProcessDetailVo> detailVos) {
		this.detailVos = detailVos;
	}

	public String getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}

	public List<String> getInputableFields() {
		return inputableFields;
	}

	public void setInputableFields(List<String> inputableFields) {
		this.inputableFields = inputableFields;
	}

	public String getMenuTwo() {
		return menuTwo;
	}

	public void setMenuTwo(String menuTwo) {
		this.menuTwo = menuTwo;
	}

	public boolean isDoNext() {
		return doNext;
	}

	public void setDoNext(boolean doNext) {
		this.doNext = doNext;
	}

	public Integer getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(Integer currentIndex) {
		this.currentIndex = currentIndex;
	}

	public String getCurrentTaskType() {
		return currentTaskType;
	}

	public void setCurrentTaskType(String currentTaskType) {
		this.currentTaskType = currentTaskType;
	}

	public String getIsPatch() {
		return isPatch;
	}

	public void setIsPatch(String isPatch) {
		this.isPatch = isPatch;
	}

}
