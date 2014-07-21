package com.wcs.tms.view.process.debtborrow;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.wcs.tms.model.Company;
import com.wcs.tms.model.DebtContract;
import com.wcs.tms.model.ProcDebtBorrow;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessUtilService;
import com.wcs.tms.service.process.common.patch.service.PatchMainService;
import com.wcs.tms.service.process.debtborrow.DebtBorrowService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.service.system.company.ShareHolderService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.FileUpload;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

/**
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description: 外债-股东借款申请Bean
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
public class DebtBorrowBean extends FileUpload<ProcDebtBorrow> {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(DebtBorrowBean.class);

	@Inject
	EntityService entityService;
	@Inject
	DebtBorrowService debtBorrowService;
	@Inject
	PatchMainService patchMainService;
	@Inject
	CommonBean dictBean;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	private ShareHolderService shareHolderService;
	@Inject
	ProcessUtilService processUtilService;// 9.3
	@Inject
	ProcessUtilMapService processUtilMapService;// 9.10
	@Inject
	PEManager peManager;



	// 外债-股东借款申请Obj
	private ProcDebtBorrow procDebtBorrow = new ProcDebtBorrow();

	// 公司下拉菜单
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();

	/** 资金币种下拉 */
	private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
	/** 公司股东下拉 */
	private List<SelectItem> shareHolderSelect = new ArrayList<SelectItem>();
	/** 资金提供方公司下拉 */
	private List<SelectItem> dirComSelect = new ArrayList<SelectItem>();

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

	private String menuTwo;// 二级菜单参数

	// 是否处理下一个任务9.3
	private boolean doNext = true;

	// 当前所处理任务在任务列表中的位置
	private Integer currentIndex;
	// 当前处理任务类型（已接受和待接收）
	private String currentTaskType;
	// 表单申请类型
	private String typeDesc;
	// 是否是补丁表中获取到数据查看，处理2014.4.11~2014.4.28之间发生故障，丢失的流程数据和流程审批日志的查看
	private String isPatch;
	
	/*** 选择外债合同* */
	private List<DebtContract> debtContractList;
	/** * 被选中的外债合同* */
	private DebtContract selDebtContract;

	/**
	 * <p>
	 * Description:Bean init
	 * </p>
	 */
	@PostConstruct
	public void initDebtBorrow() {
		log.info("DebtBorrowBean~~~~~~~~~~~~~~");
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
				// 集团审批相关信息，设置默认值
				if (checkInputable("corpAudit")
						&& (procDebtBorrow.getCorpAudit() == null || procDebtBorrow
								.getCorpAudit().equals(0d))) {
					procDebtBorrow.setCorpAudit(procDebtBorrow
							.getThisShBorrow());
				}
				if (checkInputable("corpAuditLi")
						&& procDebtBorrow.getCorpAuditLis() == null) {
					procDebtBorrow.setCorpAuditLis(procDebtBorrow
							.getThisShBorrowLis());
				}
				if (checkInputable("corpAuditLi")
						&& procDebtBorrow.getCorpAuditLie() == null) {
					procDebtBorrow.setCorpAuditLie(procDebtBorrow
							.getThisShBorrowLie());
				}
				if (checkInputable("corpAuditRa")
						&& (procDebtBorrow.getCorpAuditRa() == null || procDebtBorrow
								.getCorpAuditRa().equals(0d))) {
					procDebtBorrow.setCorpAuditRa(procDebtBorrow
							.getThisShBorrowRa());
				}
				if (checkInputable("corpAuditCu")
						&& (procDebtBorrow.getCorpAuditCu() == null || ""
								.equals(procDebtBorrow.getCorpAuditCu()))) {
					procDebtBorrow.setCorpAuditCu(procDebtBorrow
							.getThisShBorrowCu());
				}
				// 非重新申请页面设置“处理意见”默认值
				if (!"申请".equals(stepName)) {
					procDebtBorrow.setPeMemo("同意");
				}
				typeDesc = "Z".equals(procDebtBorrow.getThisShBorrowSe()) ? "展期申请"
						: "外债申请";
			}
		} else {
			procDebtBorrow.setCreatedDatetime(new Date());
			procDebtBorrow.setExchangeRate(1d);
			procDebtBorrow.setAfceExcRate(1d);
			initCompany(false);
		}
	}

	/**
	 * 初始化数据字典
	 */
	public void initDict() {
		currencySelect = dictBean
				.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
		dirComSelect = dictBean
				.getDictByCode(DictConsts.TMS_FUND_PROVIDER_COM_NAME);
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
	 * ajax更新相关信息
	 */
	public void ajaxDebt() {
		procDebtBorrowFilter();
	}

	/**
	 * 借款期限起始时间 提醒
	 */
	public boolean checkLimitDate(String idName, boolean required,
			boolean showWarn) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		String nowStr = sdf.format(new Date());
		if (idName.equals("thisShBorrowLis")) {
			Date limitStart = procDebtBorrow.getThisShBorrowLis();
			if (required && limitStart == null) {
				MessageUtils.addErrorMessage("msg",
						MessageUtils.getMessage("msg_required_thisShBorrowLis")
								+ "!");
				return false;
			}
			if (limitStart != null) {
				String limitStr = sdf.format(limitStart);
				if (limitStr.compareTo(nowStr) < 0 && showWarn) {
					MessageUtils.addWarnMessage("msg",
							MessageUtils.getMessage("errMsg_thisShBorrowLis")
									+ "!");
					return true;
				}
			}
		}
		if (idName.equals("corpAuditLis")) {
			Date limitStart = procDebtBorrow.getCorpAuditLis();
			if (required && limitStart == null) {
				MessageUtils.addErrorMessage("msg",
						MessageUtils.getMessage("msg_required_thisShBorrowLis")
								+ "!");
				return false;
			}
			if (limitStart != null) {
				String limitStr = sdf.format(limitStart);
				if (limitStr.compareTo(nowStr) < 0 && showWarn) {
					MessageUtils.addWarnMessage("msg",
							MessageUtils.getMessage("errMsg_thisShBorrowLis")
									+ "!");
					return true;
				}
			}
		}
		return true;
	}
	
	/** 选择外债合同* */
	public void ajaxSelDebtPaymentList() {
		if (procDebtBorrow.getCompany() == null ||  procDebtBorrow.getCompany().getId() == null ) {
			MessageUtils.addErrorMessage("msg",
					MessageUtils.getMessage("msg_required_companyNameCn"));
		} else {
			this.debtContractList = this.debtBorrowService.getDebtContractList(procDebtBorrow.getCompany().getId());
		}
	}
	

	/** * 选中外债合同后会写到主页面* */
	public void ajaxSelDebtPayment() throws Exception {
		if (selDebtContract != null){
			procDebtBorrow.setThisShBorrow(selDebtContract.getAppliedFunds());
			procDebtBorrow.setThisShBorrowCu(selDebtContract.getAppliedFundsCu());			
			procDebtBorrow.setThisShBorrowLis(selDebtContract.getContractStartDate());
			procDebtBorrow.setThisShBorrowLie(selDebtContract.getContractEndDate());
			
			procDebtBorrow.setThisShBorrowRa(selDebtContract.getContractRate().toString());
			procDebtBorrow.setDebtContractId(selDebtContract.getId());
		}
	}
	

	/** * 修改申请类型* */
	public void ajaxSelApplyType() throws Exception {
		if (procDebtBorrow.getApplyType() != null){
			if("1".equals(procDebtBorrow.getApplyType())){
				procDebtBorrow.setThisShBorrow(0d);
				procDebtBorrow.setThisShBorrowCu("");			
				procDebtBorrow.setThisShBorrowLis(null);
				procDebtBorrow.setThisShBorrowLie(null);
				procDebtBorrow.setDebtContractId(null);
				procDebtBorrow.setThisShBorrowRa("");
				if(selDebtContract != null){
					selDebtContract.setContractStartDate(null);
					selDebtContract.setContractEndDate(null);
					selDebtContract.setDebtContractNo("");
				}
			
			}
		}
	}
	
	
	

	/**
	 * 计算实体数据
	 */
	private void procDebtBorrowFilter() {
		// 公司投注差 = 公司投资总额 - 公司注册资本
		debtBorrowService.procDebtBorrowFilter(procDebtBorrow);
		// 得到股东下拉
		Company company = procDebtBorrow.getCompany();
		
		if (company != null && company.getId() != null) {
			//如果公司			
			List<ShareHolder> shs = procDebtBorrow.getCompany()
					.getShareHolders();
			for (ShareHolder sh : shs) {
				shareHolderSelect.add(new SelectItem(sh.getId(), sh
						.getShareHolderName()));
			}
		}
	}

	public void ajaxAfce() {
		procDebtBorrow.setAfceSign(null);
		procDebtBorrow.setAfcePaid(null);
		procDebtBorrow.setAfceExcRate(1d);
		procDebtBorrow.setAfceSignExc(null);
		procDebtBorrow.setAfceSignExc(null);
	}

	/**
	 * 流程创建保存
	 */
	public String createProcInstance() {
		if (procDebtBorrow.getAvailbBebt() < 0) {
			MessageUtils.addErrorMessage("msg",
					MessageUtils.getMessage("errMsg_availbBebt"));
			return null;
		}
		//申请金额必须小于可用投注差
		if(procDebtBorrow.getThisShBorrow() > procDebtBorrow.getAvailbBebt()){
			MessageUtils.addErrorMessage("msg",
					MessageUtils.getMessage("申请金额必须小于可用投注差！"));
			return null;
		}
		
		if (!this.checkLimitDate("thisShBorrowLis", true, false)) {
			return null;
		}
		// 附件检查
		List files = this.getProcessFileList();
		if (files == null || files.size() == 0) {
			MessageUtils.addErrorMessage("msg",
					MessageUtils.getMessage("msg_fileUpload_require"));
			return null;
		}

		Company company = entityService.find(Company.class, procDebtBorrow
				.getCompany().getId());
		procDebtBorrow.setCompany(company);

		try {
			String procInstId = debtBorrowService
					.createProcInstance(procDebtBorrow);
			// 保存流程附件
			this.saveProcessFile(procInstId);
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
		procDebtBorrow = debtBorrowService
				.findProcInstanceByProcInstId(procInstId);
		if (procDebtBorrow.getShareHolder() == null) {
			procDebtBorrow.setShareHolder(new ShareHolder());
		}
		// 查询流程详细
		searchProcessDetail();
		// 查询流程附件
		displayDetailAttach(procDebtBorrow.getProcInstId());
		procDebtBorrowFilter();
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
		inputableFields = ProcessXmlUtil.getInputableDatas("DebtBorrow",
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
		if (!this.checkLimitDate("corpAuditLis",
				this.checkInputable("corpAuditLi") ? true : false, false)) {
			return null;
		}

		// 附件检查
		List files = this.getProcessFileList();
		if (files == null || files.size() == 0) {
			MessageUtils.addErrorMessage("msg",
					MessageUtils.getMessage("msg_fileUpload_require"));
			return null;
		}

		boolean pass = false;
		if (approveStatus != null) {
			if ("Y".equals(approveStatus)) {
				pass = true;
			}
			try {
				debtBorrowService.doApprove(procDebtBorrow, pass, stepName);
				// 保存流程附件
				this.saveProcessFile(procInstId);
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
		return processUtilService.getNextPage(
				"/faces/process/common/processDealed-list.xhtml", doNext,
				currentIndex, currentTaskType);
	}

	public List<SelectItem> getShareHolderSelect() {
		return shareHolderSelect;
	}

	public void setShareHolderSelect(List<SelectItem> shareHolderSelect) {
		this.shareHolderSelect = shareHolderSelect;
	}

	public List<SelectItem> getDirComSelect() {
		return dirComSelect;
	}

	public void setDirComSelect(List<SelectItem> dirComSelect) {
		this.dirComSelect = dirComSelect;
	}

	public List<String> getInputableFields() {
		return inputableFields;
	}

	public void setInputableFields(List<String> inputableFields) {
		this.inputableFields = inputableFields;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public String getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}

	public ProcDebtBorrow getProcDebtBorrow() {
		return procDebtBorrow;
	}

	public void setProcDebtBorrow(ProcDebtBorrow procDebtBorrow) {
		this.procDebtBorrow = procDebtBorrow;
	}

	public List<SelectItem> getCurrencySelect() {
		return currencySelect;
	}

	public void setCurrencySelect(List<SelectItem> currencySelect) {
		this.currencySelect = currencySelect;
	}

	public List<SelectItem> getCompanySelect() {
		return companySelect;
	}

	public void setCompanySelect(List<SelectItem> companySelect) {
		this.companySelect = companySelect;
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getMenuTwo() {
		return menuTwo;
	}

	public void setMenuTwo(String menuTwo) {
		this.menuTwo = menuTwo;
	}

	public List<ProcessDetailVo> getDetailVos() {
		return detailVos;
	}

	public void setDetailVos(List<ProcessDetailVo> detailVos) {
		this.detailVos = detailVos;
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

	public String getTypeDesc() {
		return typeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}

	public String getIsPatch() {
		return isPatch;
	}

	public void setIsPatch(String isPatch) {
		this.isPatch = isPatch;
	}

	public List<DebtContract> getDebtContractList() {
		return debtContractList;
	}

	public void setDebtContractList(List<DebtContract> debtContractList) {
		this.debtContractList = debtContractList;
	}

	public DebtContract getSelDebtContract() {
		return selDebtContract;
	}

	public void setSelDebtContract(DebtContract selDebtContract) {
		this.selDebtContract = selDebtContract;
	}

	

}
