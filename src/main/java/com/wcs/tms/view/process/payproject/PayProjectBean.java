package com.wcs.tms.view.process.payproject;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.tms.view.process.common.FileUpload;
import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.MathUtil;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.CompanyAccount;
import com.wcs.tms.model.ProcPayProject;
import com.wcs.tms.model.ProcPayProjectDetails;
import com.wcs.tms.model.ProcessFile;
import com.wcs.tms.service.process.common.FileUploadService;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessUtilService;
import com.wcs.tms.service.process.common.patch.service.PatchMainService;
import com.wcs.tms.service.process.payproject.PayProjectService;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.service.system.company.CompanyAccountServer;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.entity.PaymentVo;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;
import com.wcs.tms.view.process.common.entity.ProcessInstanceVo;

/**
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Title: 付工程款股利及归还股东借款流程Bean
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:liaowei@wcs-global.com">廖伟</a>
 */
@ViewScoped
@ManagedBean
public class PayProjectBean extends FileUpload<ProcPayProject> {

	private static final long serialVersionUID = 1L;

	private Log log = LogFactory.getLog(PayProjectBean.class);

	@Inject
	EntityService entityService;
	@Inject
	CommonBean dictBean;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	BankService bankService;
	@Inject
	LoginService loginService;
	@Inject
	PayProjectService payProjectService;
	@Inject
	PatchMainService patchMainService;
	@Inject
    PEManager peManager;
	@Inject
	CompanyAccountServer companyAccountServer;
	// 9.4
	@Inject
	ProcessUtilService processUtilService;
	// 9.11
	@Inject
	ProcessUtilMapService processUtilMapService;
	@Inject
	private FileUploadService fileUploadService;

	/** 公司实体 */
	private Company company = new Company();
	/** 支付工程款实体 */
	private ProcPayProject procPayProject = new ProcPayProject();
	/** 银行帐号实体 */
	private CompanyAccount companyAccount = new CompanyAccount();
	/** 付款明细 */
	private ProcPayProjectDetails procPayProjectDetails = new ProcPayProjectDetails();
	/** 申请公司名称下拉选择框 */
	private List<SelectItem> companyNameSelect = new ArrayList<SelectItem>();
	/** 开户银行下拉 */
	private List<SelectItem> depositBankSelect = new ArrayList<SelectItem>();
	/** 银行帐号下拉 */
	private List<SelectItem> bankAccountSelect = new ArrayList<SelectItem>();
	/** 流程详细vo列表 */
	private List<ProcessDetailVo> detailVos = new ArrayList<ProcessDetailVo>();
	/** 付款详细vo列表 */
	private List<PaymentVo> payDetailVos = new ArrayList<PaymentVo>();
	/** 得到可输入的审批字段 */
	private List<String> inputableFields = new ArrayList<String>();
	/** 资金币种下拉 */
	private List<SelectItem> currencySelect = new ArrayList<SelectItem>();

	// 金额（大写）
	private String upAmount;
	// 流程步骤名称
	private String stepName;
	// 二级菜单参数
	private String menuTwo;
	// 流程实例ID
	private String procInstId;
	// 收款人户名
	private String receiverAccount;
	// 审批状态(通过与否)
	private String approveStatus;
	// 确认状态（付款方式选择）
	private String confirmStatus;
	// 是否继续处理下个任务
	private Boolean doNext = true;
	// 当前所处理任务在任务列表中的位置
	private Integer currentIndex;
	// 当前处理任务类型（已接受和待接收）
	private String currentTaskType;
	// 下一个需要处理的任务
	private ProcessInstanceVo nextProInstance;
	// 剩余需要支付的金额
	private Double remainPay;
	// 剩余需要支付的金额（大写）
	private String upRemainPay;
	// 本次支付的金额（大写）
	private String upThisPay;
	// 是否存在附件
	private boolean existAttach = true;
	// 是否需要展示还款的详细信息
	private boolean ifRepay = false;
	// 是否是补丁表中获取到数据查看，处理2014.4.11~2014.4.28之间发生故障，丢失的流程数据和流程审批日志的查看
	private String isPatch;

	/**
	 * list已提交流程界面,input已处理流程界面
	 */
	public PayProjectBean() {
		setupPage("/faces/process/common/processSubed-list.xhtml",
				"/faces/process/common/processDealed-list.xhtml", null);
	}

	@PostConstruct
	public void init() {
		procPayProject.setCreatedDatetime(new Date());
		initOp();
		initDict();
	}

	/**
	 * 初始化操作类型
	 */
	public void initOp() {
		String op = JSFUtils.getParamValue("op");
		log.info("op的值是：" + op);
		if (op != null && !"".equals(op)) {
			// 查看表单详细
			if ("view".equals(op)) {
				isPatch = JSFUtils.getParamValue("isPatch");
				menuTwo = JSFUtils.getRequestParam("menu2");
				procInstId = JSFUtils.getParamValue("procInstId");
				this.findProcInstance();
				initdata(true);
				lowToUp();
				changeView();
			}
			// 到审批页面
			if ("approve".equals(op)) {
				// add on 2013-5-17
				currentIndex = Integer.parseInt(JSFUtils
						.getParamValue("currentIndex"));
				currentTaskType = (String) JSFUtils
						.getParamValue("currentTaskType");

				procInstId = JSFUtils.getParamValue("procInstId");
				stepName = JSFUtils.getParamValue("stepName");
				log.info("op:" + op);
				log.info("stepName:" + stepName);
				this.getInputable();
				this.findProcInstance();
				// 重新申请时防止出现全部公司
				if ("申请".equals(stepName)) {
					initdata(false);
				} else {
					initdata(true);
				}
				lowToUp();
				lowToUp2();
				changeView();
				// 默认处理意见为同意
				if (!"申请".equals(stepName)) {
					procPayProject.setPeMemo("同意");
				}
			}
		} else {
			initdata(false);
			log.info("initdata!!!!!!!!!!!!!!!!!!!!!");
		}
	}

	/**
	 * 初始化数据字典
	 */
	public void initDict() {
		log.info("初始化数据字典!!!!!!!!!!!!!!!!!!!!!");
		currencySelect = dictBean
				.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
	}

	/**
	 * 初始化业务公司下拉
	 * 
	 * @param all
	 */
	private void initdata(Boolean all) {
		log.info("初始化业务公司下拉");
		if (all) {
			companyNameSelect = companyTmsService.getAllCompanySelect();
		} else {
			companyNameSelect = companyTmsService.getCompanySelect();
		}
	}

	/**
	 * 根据表单类型的选择是否显示还款明细
	 */
	public void changeView() {
		// 改变币别的字段显示
		if ("A".equals(procPayProject.getFormType())) {
			setIfRepay(true);
		} else {
			setIfRepay(false);
		}
		log.info("procPayProject:" + procPayProject.getFormType());
		log.info("procPayProject:" + ifRepay);
	}

	/**
	 * 查询流程实例
	 */
	public void findProcInstance() {
		procPayProject = payProjectService
				.findProcInstanceByProcInstId(procInstId);
		// 查询流程附件
		displayDetailAttach(procPayProject.getProcInstId());
		// 若无附件，则不显示附件列表
		if (fileUploadService.findProceeFileList(procInstId).size() == 0) {
			setExistAttach(false);
		}
		// 查询流程详细步骤
		searchProcessDetail();
		serchPayDetail();
		ajaxCompany();
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
	 * 查询付款过程详细
	 */
	private void serchPayDetail() {
		payDetailVos = payProjectService.getPayDetail(procInstId);
	}

	/**
	 * 申请公司得到与之相关信息
	 */
	public void ajaxCompany() {
		this.gotReceiverName();
		log.info("准备获得收款人相关信息！！！！！");
		this.getComanyBank();
		log.info("获得收款人信息成功！！！！");
	}

	/**
	 * <p>
	 * Description: 得到收款人户名
	 * </p>
	 * 
	 * @return
	 */
	public void gotReceiverName() {
		Company com = this.entityService.find(Company.class, procPayProject
				.getCompany().getId());
		procPayProject.setReceiverName(com.getCompanyName());
	}

	/**
	 * 根据公司得到对应的银行及帐号
	 */
	public void getComanyBank() {
		// 根据公司名称获得与之相关的银行
		depositBankSelect = payProjectService
				.getCompanyBankSelect(procPayProject.getCompany().getId());
		log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + depositBankSelect);
		this.getCompanyBankAccount();
	}

	/**
	 * 得到公司对应的帐号
	 */
	public void getCompanyBankAccount() {
		log.info("准备获得公司对应的帐号！！！");
		Company com = this.entityService.find(Company.class, procPayProject
				.getCompany().getId());
		log.info("公司的ID是：" + com.getId());
		String accountDesc = procPayProject.getAccountDesc();
		log.info("accountDesc:" + accountDesc);
		bankAccountSelect = payProjectService.getCompanyAccountSelect(
				com.getId(), accountDesc);
	}

	/**
	 * 将"金额"阿拉伯数字转换成大写
	 */
	public void lowToUp() {
		log.info("将小写金额转换成大写金额！！！！！！！！！！！！！！！");
		// 金额的大小写转换
		procPayProject.setAmount(MathUtil.cashpoolRound(procPayProject
				.getAmount()));
		setUpAmount(com.wcs.tms.util.MoneyFormatUtil.format((procPayProject
				.getAmount() * 10000)));
	}

	/**
	 * 将剩余需要支付金额转换成大写
	 */
	public void lowToUp2() {
		log.info("将小写金额转换成大写金额！！！！！！！！！！！！！！！");
		// 剩余支付金额的大小写转换
		// 需要支付的总金额
		Double need = payProjectService.needPay(procPayProject);
		// 已经支付的总金额
		Double payed = payProjectService.payedAccount(procPayProject);
		remainPay = need - payed;
		// 将本次支付金额初始化为剩余需要支付的金额
		procPayProjectDetails.setPayFundsTotal(remainPay);
		setUpRemainPay(com.wcs.tms.util.MoneyFormatUtil
				.format(((remainPay) * 10000)));
		lowToUp3();
	}

	/**
	 * 将本次支付金额转换成大写
	 */
	public void lowToUp3() {
		// 本次支付金额的大小写转换
		if (!doNull()) {
			// 不为空，才执行大小写转换
			procPayProjectDetails.setPayFundsTotal(MathUtil
					.cashpoolRound(procPayProjectDetails.getPayFundsTotal()));
			setUpThisPay(com.wcs.tms.util.MoneyFormatUtil
					.format(procPayProjectDetails.getPayFundsTotal() * 10000));
		}
	}

	/**
	 * 将还款金额转换成正确的格式
	 */
	public void lowToUp4() {
		String parten = "#.00";
		DecimalFormat decimal = new DecimalFormat(parten);
		String str = decimal.format(procPayProject.getRepayAmount());
		Double amount = Double.parseDouble(str);
		procPayProject.setRepayAmount(amount);
	}

	/**
	 * 判断输入字段“本次付款金额”是否为空
	 */
	public Boolean doNull() {
		if (procPayProjectDetails.getPayFundsTotal() == null
				|| "".equals(procPayProjectDetails.getPayFundsTotal()
						.toString().trim())) {
			// 是空
			return true;
		}
		return false;
	}

	/**
	 * 付工程款股利及归还股东借款流程创建保存
	 * 
	 * @return
	 */
	public String applyMargin() {
		// 设置公司、银行
		Company company = this.entityService.find(Company.class, procPayProject
				.getCompany().getId());
		procPayProject.setCompany(company);
		log.info("付工程款股利及归还股东借款流程创建保存");
		// 若还款明细有内容，但表单类型不选择还款，去除还款明细里的内容
		if ("申请".equals(stepName)
				&& (!"A".equals(procPayProject.getFormType()))) {
			log.info("设空");
			procPayProject.setAmountCu(null);
			procPayProject.setExpireTime(null);
			procPayProject.setRepayAmount(null);
		}
		try {
			// 剩余需要支付金额 默认为 申请金额（add on 2013-7-19 by yan）
			procPayProject.setLastPaiedAmount(procPayProject.getAmount());
			String procInstId = payProjectService
					.createProcInstance(procPayProject);
			// 流程附件不为必填
			List<ProcessFile> files = this.getProcessFileList();
			/**
			 * sonar测试 files.size() > 0 || files != null 改为 files != null &&
			 * files.size() > 0
			 */
			if (files != null && files.size() > 0) {
				// 保存流程附件
				log.info("保存流程附件");
				this.saveProcessFile(procInstId);
			}
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
	 * 得到可输入的审批字段
	 */
	private void getInputable() {
		inputableFields = ProcessXmlUtil.getInputableDatas("PayProject",
				stepName);
	}

	/**
	 * 字段可输入检查
	 * 
	 * @param fieldName
	 * @return
	 */
	public Boolean checkInputable(String fieldName) {
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
		if (approveStatus != null) {
			try {
				// 流程附件为选填（主要为了重新申请时）
				List<ProcessFile> files = this.getProcessFileList();
				if (files.size() > 0 || files == null) {
					String upload = ProcessXmlUtil.findStepProperty("id",
							"PayProject", stepName, "upload");
					if (upload != null && "true".equals(upload)) {
						// 保存流程附件
						this.saveProcessFile(procInstId);
						log.info("保存流程附件！！！");
					}
				}
				// 若还款明细有内容，但表单类型不选择还款，去除还款明细里的内容
				if (!"A".equals(procPayProject.getFormType())) {
					procPayProject.setAmountCu(null);
					procPayProject.setExpireTime(null);
					procPayProject.setRepayAmount(null);
				}
				log.info("准备执行service方法！！！");
				procPayProject.setLastPaiedAmount(procPayProject.getAmount());
				payProjectService.doApprove(procPayProject, approveStatus,
						stepName);
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
		// 9.4处理下一个任务
		return processUtilService.getNextPage(
				"/faces/process/common/processDealed-list.xhtml", doNext,
				getCurrentIndex(), getCurrentTaskType());
	}

	/**
	 * 确认付款/终止付款
	 * 
	 * @return
	 */
	public String doConfirm() {
		Boolean conf = false;
		Boolean payWay = false;
		log.info("doNext" + doNext);
		try {
			// 两表之间设置关联
			procPayProjectDetails.setProcPayProject(procPayProject);
			if (confirmStatus != null) {
				// "S"为SUNGRAD付款，“O”为网银付款，“P”为停止付款
				if ("S".equals(confirmStatus) || "O".equals(confirmStatus)) {
					// 付款标识
					conf = true;
					if ("S".equals(confirmStatus)) {
						// SUNGARD
						payWay = true;
					} else {
						// 网银
						payWay = false;
					}
					// 更新剩余需要支付金额（add on 2013-7-15 by yan ）
					procPayProjectDetails
							.getProcPayProject()
							.setLastPaiedAmount(
									MathUtil.roundHalfUp(
											remainPay
													- procPayProjectDetails
															.getPayFundsTotal(),
											4));
					entityService.update(procPayProjectDetails
							.getProcPayProject());
				} else {
					// 停止付款标识
					conf = false;
					// 更新剩余需要支付金额（add on 2013-7-15 by yan ）
					procPayProjectDetails.getProcPayProject()
							.setLastPaiedAmount(0.0);
					entityService.update(procPayProjectDetails
							.getProcPayProject());
				}
				// 判断“本次付款的金额”是否为空
				if (conf == true && doNull()) {
					MessageUtils.addErrorMessage("doneMsg", "本次支付的金额不可为空！");
					return "faces/process/payProject/payProject-pay.xhtml";
				}
				if (conf == true && duMore()) {
					MessageUtils.addErrorMessage("doneMsg",
							"本次支付的金额大于剩余需要支付的金额！");
					return "faces/process/payProject/payProject-pay.xhtml";
				}
				payProjectService.doConfirm(procPayProject,
						procPayProjectDetails, conf, payWay, stepName);
				if (!doNext) {
					if (conf) {
						if ("S".equals(confirmStatus)) {
							MessageUtils.addSuccessMessage(
									"doneMsg",
									MessageUtils
											.getMessage("sungard_payment_suc")
											+ "流程实例编号["
											+ processUtilMapService
													.getTmsIdByFnId(procInstId)
											+ "]");
						} else {
							MessageUtils.addSuccessMessage(
									"doneMsg",
									MessageUtils
											.getMessage("online_payment_suc")
											+ "流程实例编号["
											+ processUtilMapService
													.getTmsIdByFnId(procInstId)
											+ "]");
						}
					} else {
						MessageUtils.addSuccessMessage(
								"doneMsg",
								"终止付款成功！流程实例编号["
										+ processUtilMapService
												.getTmsIdByFnId(procInstId)
										+ "]");
					}
				}
			}
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
		// 9.4处理下一个任务
		return processUtilService.getNextPage(
				"/faces/process/common/processDealed-list.xhtml", doNext,
				getCurrentIndex(), getCurrentTaskType());
	}

	/**
	 * 判断本次支付的金额是否大于剩余需要支付的金额
	 */
	public Boolean duMore() {
		if (procPayProjectDetails.getPayFundsTotal() > (payProjectService
				.needPay(procPayProject) - payProjectService
				.payedAccount(procPayProject))) {
			return true;
		}
		return false;
	}

	// ************************************setter、getter方法***************************************

	public EntityService getEntityService() {
		return entityService;
	}

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	public CompanyTmsService getCompanyTmsService() {
		return companyTmsService;
	}

	public void setCompanyTmsService(CompanyTmsService companyTmsService) {
		this.companyTmsService = companyTmsService;
	}

	public BankService getBankService() {
		return bankService;
	}

	public void setBankService(BankService bankService) {
		this.bankService = bankService;
	}

	public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}

	public PayProjectService getPayProjectService() {
		return payProjectService;
	}

	public void setPayProjectService(PayProjectService payProjectService) {
		this.payProjectService = payProjectService;
	}

	public CompanyAccountServer getCompanyAccountServer() {
		return companyAccountServer;
	}

	public void setCompanyAccountServer(
			CompanyAccountServer companyAccountServer) {
		this.companyAccountServer = companyAccountServer;
	}

	public ProcessUtilService getProcessUtilService() {
		return processUtilService;
	}

	public void setProcessUtilService(ProcessUtilService processUtilService) {
		this.processUtilService = processUtilService;
	}

	public ProcessUtilMapService getProcessUtilMapService() {
		return processUtilMapService;
	}

	public void setProcessUtilMapService(
			ProcessUtilMapService processUtilMapService) {
		this.processUtilMapService = processUtilMapService;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public ProcPayProject getProcPayProject() {
		return procPayProject;
	}

	public void setProcPayProject(ProcPayProject procPayProject) {
		this.procPayProject = procPayProject;
	}

	public CompanyAccount getCompanyAccount() {
		return companyAccount;
	}

	public void setCompanyAccount(CompanyAccount companyAccount) {
		this.companyAccount = companyAccount;
	}

	public ProcPayProjectDetails getProcPayProjectDetails() {
		return procPayProjectDetails;
	}

	public void setProcPayProjectDetails(
			ProcPayProjectDetails procPayProjectDetails) {
		this.procPayProjectDetails = procPayProjectDetails;
	}

	public List<SelectItem> getCompanyNameSelect() {
		return companyNameSelect;
	}

	public void setCompanyNameSelect(List<SelectItem> companyNameSelect) {
		this.companyNameSelect = companyNameSelect;
	}

	public List<SelectItem> getDepositBankSelect() {
		return depositBankSelect;
	}

	public void setDepositBankSelect(List<SelectItem> depositBankSelect) {
		this.depositBankSelect = depositBankSelect;
	}

	public List<SelectItem> getBankAccountSelect() {
		return bankAccountSelect;
	}

	public void setBankAccountSelect(List<SelectItem> bankAccountSelect) {
		this.bankAccountSelect = bankAccountSelect;
	}

	public List<ProcessDetailVo> getDetailVos() {
		return detailVos;
	}

	public void setDetailVos(List<ProcessDetailVo> detailVos) {
		this.detailVos = detailVos;
	}

	public List<PaymentVo> getPayDetailVos() {
		return payDetailVos;
	}

	public void setPayDetailVos(List<PaymentVo> payDetailVos) {
		this.payDetailVos = payDetailVos;
	}

	public List<String> getInputableFields() {
		return inputableFields;
	}

	public void setInputableFields(List<String> inputableFields) {
		this.inputableFields = inputableFields;
	}

	public String getUpAmount() {
		return upAmount;
	}

	public void setUpAmount(String upAmount) {
		this.upAmount = upAmount;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public String getMenuTwo() {
		return menuTwo;
	}

	public void setMenuTwo(String menuTwo) {
		this.menuTwo = menuTwo;
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getReceiverAccount() {
		return receiverAccount;
	}

	public void setReceiverAccount(String receiverAccount) {
		this.receiverAccount = receiverAccount;
	}

	public String getConfirmStatus() {
		return confirmStatus;
	}

	public void setConfirmStatus(String confirmStatus) {
		this.confirmStatus = confirmStatus;
	}

	public Boolean getDoNext() {
		return doNext;
	}

	public void setDoNext(Boolean doNext) {
		this.doNext = doNext;
	}

	public ProcessInstanceVo getNextProInstance() {
		return nextProInstance;
	}

	public void setNextProInstance(ProcessInstanceVo nextProInstance) {
		this.nextProInstance = nextProInstance;
	}

	public Double getRemainPay() {
		return remainPay;
	}

	public void setRemainPay(Double remainPay) {
		this.remainPay = remainPay;
	}

	public String getUpRemainPay() {
		return upRemainPay;
	}

	public void setUpRemainPay(String upRemainPay) {
		this.upRemainPay = upRemainPay;
	}

	public String getUpThisPay() {
		return upThisPay;
	}

	public void setUpThisPay(String upThisPay) {
		this.upThisPay = upThisPay;
	}

	public String getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}

	public boolean isExistAttach() {
		return existAttach;
	}

	public void setExistAttach(boolean existAttach) {
		this.existAttach = existAttach;
	}

	public List<SelectItem> getCurrencySelect() {
		return currencySelect;
	}

	public void setCurrencySelect(List<SelectItem> currencySelect) {
		this.currencySelect = currencySelect;
	}

	public boolean isIfRepay() {
		return ifRepay;
	}

	public void setIfRepay(boolean ifRepay) {
		this.ifRepay = ifRepay;
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