package com.wcs.tms.view.process.inveFinaMargin;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJBException;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.MathUtil;
import com.wcs.base.view.ViewBaseBean;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.CompanyAccount;
import com.wcs.tms.model.ProcInveFinaBail;
import com.wcs.tms.model.ProcInveFinaDetail;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessUtilService;
import com.wcs.tms.service.process.common.patch.service.PatchMainService;
import com.wcs.tms.service.process.inveFinaMargin.InveFinaMarginService;
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
 * Title: 投资、融资保证金及归还银行贷款借款/转款申请Bean
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
 * @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a>
 */
@ViewScoped
@ManagedBean
public class InveFinaMarginBean extends ViewBaseBean<ProcInveFinaBail> {

	private static final long serialVersionUID = 1L;

	private Log log = LogFactory.getLog(InveFinaMarginBean.class);

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
	InveFinaMarginService inveFinaMarginService;
	@Inject
	CompanyAccountServer companyAccountServer;
	// 9.3
	@Inject
	ProcessUtilService processUtilService;
	// 9.11
	@Inject
	ProcessUtilMapService processUtilMapService;
	
	@Inject
    PatchMainService patchMainService;
	@Inject
    PEManager peManager;

	/** 公司实体 */
	private Company company = new Company();
	/** 投资融资保证金实体 */
	private ProcInveFinaBail procInveFinaBail = new ProcInveFinaBail();
	/** 银行帐号实体 */
	private CompanyAccount companyAccount = new CompanyAccount();
	/** 付款明细 */
	private ProcInveFinaDetail procInveFinaDetail = new ProcInveFinaDetail();
	/** 银行下拉 */
	private List<SelectItem> depositBankSelect = new ArrayList<SelectItem>();
	/** 银行帐号下拉 */
	private List<SelectItem> bankAccountSelect = new ArrayList<SelectItem>();
	/** 资金币种下拉 */
	private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
	/** 流程详细vo列表 */
	private List<ProcessDetailVo> detailVos = new ArrayList<ProcessDetailVo>();
	/** 申请公司名称下拉选择框 */
	private List<SelectItem> companyNameSelect = new ArrayList<SelectItem>();
	/** 付款详细vo列表 */
	private List<PaymentVo> payDetailVos = new ArrayList<PaymentVo>();
	/** 得到可输入的审批字段 */
	private List<String> inputableFields = new ArrayList<String>();

	// 融资保证金类型表单新增额外字段
	private String fianceDisplay = "none";
	// 还贷类型表单新增额外字段
	private String repayDisplay = "none";
	// 汇率
	private Double exchangeRate;
	// 金额（大写）
	private String upAmount;
	// 流程实例ID
	private String procInstId;
	// 流程步骤名称
	private String stepName;
	// 二级菜单参数
	private String menuTwo;
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
	// 是否需要去除重复的银行
	private Boolean iftrue = true;
	// 币别字段显示内容
	private String amountCuLabel = "币别";
	// 币别字段必输提示
	private String requiredMessage = "请选择币别";
	// 金额字段显示内容
	private String cuAmountLabel = "金额";
	// 金额字段必输提示
	private String cuAmountReqMess = "请输入金额";
	// 金额字段合规提示
	private String validatorMessage = "金额必须在0~100,000,000之间";
	// 是否是补丁表中获取到数据查看，处理2014.4.11~2014.4.28之间发生故障，丢失的流程数据和流程审批日志的查看
	private String isPatch;

	// list已提交流程界面,input已处理流程界面
	public InveFinaMarginBean() {
		setupPage("/faces/process/common/processSubed-list.xhtml",
				"/faces/process/common/processDealed-list.xhtml", null);
	}

	@PostConstruct
	public void init() {
		log.info("初始化bean");
		procInveFinaBail.setCreatedDatetime(new Date());
		initOp();
		initDict();
		if ("集团资金计划员付款".equals(stepName)) {
			lowToUp2();
		}
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
				menuTwo = JSFUtils.getRequestParam("menu2");
				procInstId = JSFUtils.getParamValue("procInstId");
				isPatch = JSFUtils.getParamValue("isPatch");
				this.findProcInstance();
				initdata(true);
				lowToUp();
				changeAmountCu();
				calculateExchangeRate();
				initFormByTransitAndType();
			}
			// 到审批页面
			if ("approve".equals(op)) {
				// add on 2013-5-17
				currentIndex = Integer.parseInt(JSFUtils
						.getParamValue("currentIndex"));
				currentTaskType = (String) JSFUtils
						.getParamValue("currentTaskType");

				procInstId = (String) JSFUtils.getParamValue("procInstId");
				stepName = (String) JSFUtils.getParamValue("stepName");
				log.info("op:" + op);
				log.info("stepName:" + stepName);
				this.getInputable();
				this.findProcInstance();
				// 防止重新申请时，公司名称下拉为全部公司名称
				if ("申请".equals(stepName)) {
					initdata(false);
				} else {
					initdata(true);
				}
				lowToUp();
				lowToUp2();
				changeAmountCu();
				calculateExchangeRate();
				initFormByTransitAndType();
				// 默认处理意见为同意
				if (!"申请".equals(stepName)) {
					procInveFinaBail.setPeMemo("同意");
				}
			}
		} else {
			initdata(false);
			log.info("initdata!!!!!!!!!!!!!!!!!!!!!");
		}
	}

	/**
	 * 根据表单类型初始化表单
	 */
	public void initFormByTransitAndType() {
		try {
			if (!MathUtil.isEmptyOrNull(procInveFinaBail.getFormType())) {
				// 表单类型为：“融资保证金”
				if ("F".equals(procInveFinaBail.getFormType())) {
					fianceDisplay = "block";
					repayDisplay = "none";
					// 表单类型为：“还贷”
				} else if ("R".equals(procInveFinaBail.getFormType())) {
					fianceDisplay = "none";
					repayDisplay = "block";
					// 其他类型
				} else {
					fianceDisplay = "none";
					repayDisplay = "none";
				}
			}
		} catch (Exception e) {
			log.error("initFormByTransitAndType方法 根据是否转口和表单类型初始化表单", e);
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
	 * 根据表单类型的选择改变币别、金额的字段显示
	 */
	public void changeAmountCu() {
		// 改变币别的字段显示
		if ("I".equals(procInveFinaBail.getFormType())) {
			amountCuLabel = "投资币别";
			requiredMessage = "请选择投资币别";
			cuAmountLabel = "投资金额";
			cuAmountReqMess = "请输入投资金额";
			validatorMessage = "投资金额必须在0~100,000,000之间";
		} else if ("F".equals(procInveFinaBail.getFormType())) {
			amountCuLabel = "融资币别";
			requiredMessage = "请选择融资币别";
			cuAmountLabel = "融资金额";
			cuAmountReqMess = "请输入融资金额";
			validatorMessage = "融资金额必须在0~100,000,000之间";
		} else if ("C".equals(procInveFinaBail.getFormType())) {
			amountCuLabel = "利息及银行费用币别";
			requiredMessage = "请选择利息及银行费用币别";
			cuAmountLabel = "利息及银行费用金额";
			cuAmountReqMess = "请输入利息及银行费用金额";
			validatorMessage = "利息及银行费用金额必须在0~100,000,000之间";
		} else if ("D".equals(procInveFinaBail.getFormType())) {
			amountCuLabel = "时点存款币别";
			requiredMessage = "请选择时点存款币别";
			cuAmountLabel = "时点存款金额";
			cuAmountReqMess = "请输入时点存款金额";
			validatorMessage = "时点存款金额必须在0~100,000,000之间";
		} else {
			amountCuLabel = "还贷币别";
			requiredMessage = "请选择还贷币别";
			cuAmountLabel = "还贷金额";
			cuAmountReqMess = "请输入还贷金额";
			validatorMessage = "还贷金额必须在0~100,000,000之间";
		}
	}

	/**
	 * 查询流程实例
	 */
	public void findProcInstance() {
		procInveFinaBail = inveFinaMarginService
				.findProcInstanceByProcInstId(procInstId);
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
		payDetailVos = inveFinaMarginService.getPayDetail(procInstId);
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
		Company com = this.entityService.find(Company.class, procInveFinaBail
				.getCompany().getId());
		procInveFinaBail.setReceiverName((com.getCompanyName()));
	}

	/**
	 * 根据公司得到对应的银行及帐号
	 */
	public void getComanyBank() {
		// 根据公司ID获得帐号描述
		log.info("获得银行描述");
		depositBankSelect = inveFinaMarginService
				.getCompanyBankSelect(procInveFinaBail.getCompany().getId());
		log.info("获得帐号");
		this.getCompanyBankAccount();
	}

	/**
	 * 得到公司对应的帐号
	 */
	public void getCompanyBankAccount() {
		if (iftrue) {
			log.info("准备获得公司去除重复银行的对应的帐号！！！");
			Company com = this.entityService.find(Company.class,
					procInveFinaBail.getCompany().getId());
			log.info("公司的ID是：" + com.getId());
			String accountDesc = procInveFinaBail.getAccountDesc();
			log.info("accountDesc:" + accountDesc);
			bankAccountSelect = inveFinaMarginService.getCompanyAccountSelect(
					com.getId(), accountDesc);
		} else {
			log.info("准备获得公司未去除重复银行的对应的帐号！！！");
			log.info("companyAccount.getId():"
					+ companyAccount.getCounterpartyCode());
			bankAccountSelect = inveFinaMarginService
					.getCompanyAccountSelectNo(companyAccount
							.getCounterpartyCode());
		}
	}

	/**
	 * 将"金额"阿拉伯数字转换成大写
	 */
	public void lowToUp() {
		log.info("将小写金额转换成大写金额！！！！！！！！！！！！！！！");
		// 金额的大小写转换
		procInveFinaBail.setAmount(MathUtil.cashpoolRound(procInveFinaBail
				.getAmount()));
		setUpAmount(com.wcs.tms.util.MoneyFormatUtil.format(procInveFinaBail
				.getAmount() * 10000));
	}

	/**
	 * 计算汇率
	 */
	public void calculateExchangeRate() {
		try {
			if (!MathUtil.isZeroOrNull(procInveFinaBail.getCuAmount())
					&& !MathUtil.isZeroOrNull(procInveFinaBail.getAmount())) {
				setExchangeRate(procInveFinaBail.getAmount()
						/ procInveFinaBail.getCuAmount());
			} else {
				setExchangeRate(null);
			}
		} catch (Exception e) {
			log.error("calculateExchangeRate方法 计算汇率出现异常", e);
		}
	}

	/**
	 * 将剩余需要支付金额转换成大写
	 */
	public void lowToUp2() {
		log.info("将小写金额转换成大写金额！！！！！！！！！！！！！！！");
		// 剩余支付金额的大小写转换
		// 需要支付的总金额
		Double need = inveFinaMarginService.needPay(procInveFinaBail);
		// 已经支付的总金额
		Double payed = inveFinaMarginService.payedAccount(procInveFinaBail);
		remainPay = need - payed;
		// 将本次支付金额初始化为剩余需要支付的金额
		procInveFinaDetail.setPayFundsTotal(remainPay);
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
			procInveFinaDetail.setPayFundsTotal(MathUtil
					.cashpoolRound(procInveFinaDetail.getPayFundsTotal()));
			setUpThisPay(com.wcs.tms.util.MoneyFormatUtil
					.format((procInveFinaDetail.getPayFundsTotal() * 10000)));
		}
	}

	/**
	 * 将币别金额转换成正确的格式
	 */
	public void lowToUp4() {
		procInveFinaBail.setCuAmount(MathUtil.cashpoolRound(procInveFinaBail
				.getCuAmount()));
	}

	/**
	 * 投融资保证金流程创建保存
	 */
	public String applyMargin() {
		// 设置公司、银行
		Company company = this.entityService.find(Company.class,
				procInveFinaBail.getCompany().getId());
		procInveFinaBail.setCompany(company);
		log.info("投融资保证金流程创建保存");
		try {
			// 剩余需要支付金额 默认为 申请金额（add on 2013-7-19 by yan）
			procInveFinaBail.setLastPaiedAmount(procInveFinaBail.getAmount());
			String procInstId = inveFinaMarginService
					.createProcInstance(procInveFinaBail);
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
		inputableFields = ProcessXmlUtil.getInputableDatas("InveFinaBail",
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
		Boolean pass = false;
		if (approveStatus != null) {
			if ("Y".equals(approveStatus)) {
				pass = true;
			}
			try {
				procInveFinaBail.setLastPaiedAmount(procInveFinaBail
						.getAmount());
				inveFinaMarginService.doApprove(procInveFinaBail, pass,
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
				currentIndex, currentTaskType);
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
			procInveFinaDetail.setProcInveFinaBail(procInveFinaBail);
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
					procInveFinaDetail
							.getProcInveFinaBail()
							.setLastPaiedAmount(
									MathUtil.roundHalfUp(
											remainPay
													- procInveFinaDetail
															.getPayFundsTotal(),
											4));
					entityService.update(procInveFinaDetail
							.getProcInveFinaBail());
				} else {
					// 停止付款标识
					conf = false;
					// 更新剩余需要支付金额（add on 2013-7-15 by yan ）
					procInveFinaDetail.getProcInveFinaBail()
							.setLastPaiedAmount(0.0);
					entityService.update(procInveFinaDetail
							.getProcInveFinaBail());
				}
				// 判断“本次支付的金额”是否为空
				if (conf == true && doNull()) {
					MessageUtils.addErrorMessage("doneMsg", "本次支付的金额不能为空！");
					return "faces/process/inveFinaMargin/inveFinaMargin-confirm.xhtml";
				}
				if (conf == true && duMore()) {
					MessageUtils.addErrorMessage("doneMsg",
							"本次支付的金额大于剩余需要支付的金额！");
					return "faces/process/inveFinaMargin/inveFinaMargin-confirm.xhtml";
				}
				inveFinaMarginService.doConfirm(procInveFinaBail,
						procInveFinaDetail, conf, payWay, stepName);
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
				currentIndex, currentTaskType);
	}

	/**
	 * 判断本次支付的金额是否大于剩余需要支付的金额
	 */
	public Boolean duMore() {
		if (procInveFinaDetail.getPayFundsTotal() > (inveFinaMarginService
				.needPay(procInveFinaBail) - inveFinaMarginService
				.payedAccount(procInveFinaBail))) {
			return true;
		}
		return false;
	}

	/**
	 * 判断输入字段“本次付款金额”是否为空
	 */
	public Boolean doNull() {
		if (procInveFinaDetail.getPayFundsTotal() == null
				|| "".equals(procInveFinaDetail.getPayFundsTotal().toString()
						.trim())) {
			// 是空
			return true;
		}
		return false;
	}

	// **********************************************setter、getter方法**********************************//
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

	public ProcInveFinaBail getProcInveFinaBail() {
		return procInveFinaBail;
	}

	public void setProcInveFinaBail(ProcInveFinaBail procInveFinaBail) {
		this.procInveFinaBail = procInveFinaBail;
	}

	public List<SelectItem> getCompanyNameSelect() {
		return companyNameSelect;
	}

	public void setCompanyNameSelect(List<SelectItem> companyNameSelect) {
		this.companyNameSelect = companyNameSelect;
	}

	public EntityService getEntityService() {
		return entityService;
	}

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	public CommonBean getDictBean() {
		return dictBean;
	}

	public void setDictBean(CommonBean dictBean) {
		this.dictBean = dictBean;
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

	public List<SelectItem> getCurrencySelect() {
		return currencySelect;
	}

	public void setCurrencySelect(List<SelectItem> currencySelect) {
		this.currencySelect = currencySelect;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<SelectItem> getDepositBankSelect() {
		return depositBankSelect;
	}

	public void setDepositBankSelect(List<SelectItem> depositBankSelect) {
		this.depositBankSelect = depositBankSelect;
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
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

	public List<String> getInputableFields() {
		return inputableFields;
	}

	public void setInputableFields(List<String> inputableFields) {
		this.inputableFields = inputableFields;
	}

	public String getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}

	public ProcInveFinaDetail getProcInveFinaDetail() {
		return procInveFinaDetail;
	}

	public void setProcInveFinaDetail(ProcInveFinaDetail procInveFinaDetail) {
		this.procInveFinaDetail = procInveFinaDetail;
	}

	public String getConfirmStatus() {
		return confirmStatus;
	}

	public void setConfirmStatus(String confirmStatus) {
		this.confirmStatus = confirmStatus;
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

	public Double getRemainPay() {
		return remainPay;
	}

	public void seRemainPay(Double remainPay) {
		this.remainPay = remainPay;
	}

	public List<SelectItem> getBankAccountSelect() {
		return bankAccountSelect;
	}

	public void setBankAccountSelect(List<SelectItem> bankAccountSelect) {
		this.bankAccountSelect = bankAccountSelect;
	}

	public CompanyAccount getCompanyAccount() {
		return companyAccount;
	}

	public void setCompanyAccount(CompanyAccount companyAccount) {
		this.companyAccount = companyAccount;
	}

	public Boolean getDoNext() {
		return doNext;
	}

	public void setDoNext(Boolean doNext) {
		this.doNext = doNext;
	}

	public InveFinaMarginService getInveFinaMarginService() {
		return inveFinaMarginService;
	}

	public void setInveFinaMarginService(
			InveFinaMarginService inveFinaMarginService) {
		this.inveFinaMarginService = inveFinaMarginService;
	}

	public CompanyAccountServer getCompanyAccountServer() {
		return companyAccountServer;
	}

	public void setCompanyAccountServer(
			CompanyAccountServer companyAccountServer) {
		this.companyAccountServer = companyAccountServer;
	}

	public ProcessInstanceVo getNextProInstance() {
		return nextProInstance;
	}

	public void setNextProInstance(ProcessInstanceVo nextProInstance) {
		this.nextProInstance = nextProInstance;
	}

	public void setRemainPay(Double remainPay) {
		this.remainPay = remainPay;
	}

	public String getAmountCuLabel() {
		return amountCuLabel;
	}

	public void setAmountCuLabel(String amountCuLabel) {
		this.amountCuLabel = amountCuLabel;
	}

	public String getRequiredMessage() {
		return requiredMessage;
	}

	public void setRequiredMessage(String requiredMessage) {
		this.requiredMessage = requiredMessage;
	}

	public String getCuAmountLabel() {
		return cuAmountLabel;
	}

	public void setCuAmountLabel(String cuAmountLabel) {
		this.cuAmountLabel = cuAmountLabel;
	}

	public String getCuAmountReqMess() {
		return cuAmountReqMess;
	}

	public void setCuAmountReqMess(String cuAmountReqMess) {
		this.cuAmountReqMess = cuAmountReqMess;
	}

	public String getValidatorMessage() {
		return validatorMessage;
	}

	public void setValidatorMessage(String validatorMessage) {
		this.validatorMessage = validatorMessage;
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

	public Double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(Double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getFianceDisplay() {
		return fianceDisplay;
	}

	public void setFianceDisplay(String fianceDisplay) {
		this.fianceDisplay = fianceDisplay;
	}

	public String getRepayDisplay() {
		return repayDisplay;
	}

	public void setRepayDisplay(String repayDisplay) {
		this.repayDisplay = repayDisplay;
	}

	public String getIsPatch() {
		return isPatch;
	}

	public void setIsPatch(String isPatch) {
		this.isPatch = isPatch;
	}

}