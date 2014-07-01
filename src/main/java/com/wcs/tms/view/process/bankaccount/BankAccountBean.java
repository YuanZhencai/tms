package com.wcs.tms.view.process.bankaccount;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.LoginService;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.MathUtil;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.BankAccountMain;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcBankAccount;
import com.wcs.tms.service.process.bankaccount.BankAccountService;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessUtilService;
import com.wcs.tms.service.process.common.patch.service.PatchMainService;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.AccountBankSelectBean;
import com.wcs.tms.view.process.common.FileUpload;
import com.wcs.tms.view.process.common.entity.AccountBankSelectVo;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

/**
 * 
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description: 银行账户申请审批流程Bean
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */

@ManagedBean
@ViewScoped
public class BankAccountBean extends FileUpload<ProcBankAccount> {

	private static final long serialVersionUID = 1L;

	@Inject
	LoginService loginService;
	@Inject
	ProcessUtilMapService processUtilMapService;
	@Inject
	BankAccountService bankAccountService;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	CommonBean dictBean;
	@Inject
	BankService bankService;
	@Inject
	ProcessUtilService processUtilService;
	@ManagedProperty(value = "#{accountBankSelectBean}")
	private AccountBankSelectBean accountBankSelectBean;
	@Inject
	PatchMainService patchMainService;

	@Inject
	PEManager peManager;

	private static final Log log = LogFactory.getLog(BankAccountBean.class);

	//
	private String bsbCode;
	//
	private String unionBankNo;
	// 隐藏模块控制
	private String personalDis = "block";
	private String notPersonalDis = "none";
	private String closeAccountDis = "none";
	// 账户池-币种
	private String accountPoolCurr = "";
	// 交易对手编码
	private String bankCode;
	private String applyDate;
	// 账户银行名称
	private String accountBankName;
	// 根据节点判断是否需要编辑
	private Boolean isDisabled = true;
	// 销户时，相关字段可编辑参数设置
	private Boolean cancelDisable = false;

	// 流程数据实例
	private ProcBankAccount procBankAccount = new ProcBankAccount();
	// 公司下拉菜单
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
	// 总行下拉菜单
	private List<SelectItem> bankSelect = new ArrayList<SelectItem>();
	// 资金币种下拉
	private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
	// 账号性质
	private List<SelectItem> accountNatureSelect = new ArrayList<SelectItem>();
	// 城市下拉
	private List<SelectItem> citySelect = new ArrayList<SelectItem>();
	// 销户流程实例下拉
	private List<SelectItem> bankAccountSelect = new ArrayList<SelectItem>();

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
	private List<String> notVisibleProperty = new ArrayList<String>();
	// 是否继续处理下个任务
	private Boolean doNext = true;
	// 当前所处理任务在任务列表中的位置
	private Integer currentIndex;
	// 当前处理任务类型（已接受和待接收）
	private String currentTaskType;
	// 是否是补丁表中获取到数据查看，处理2014.4.11~2014.4.28之间发生故障，丢失的流程数据和流程审批日志的查看
	private String isPatch;

	@PostConstruct
	public void initBankAccountBean() {
		initOp();
		// 申请时间
		setApplyDate(DateUtil.dateToStrShort(DateUtil
				.dateToDateTime((procBankAccount.getCreatedDatetime()))));
	}

	/**
	 * 设置选中的账户银行code值
	 */
	public void setBankCodeOfProcInst() {
		AccountBankSelectVo vo = accountBankSelectBean.getSelectedVO();
		setAccountBankName(vo.getBankName());
		procBankAccount.setBankCode(vo.getBankCode());
		this.bankChange();
	}

	/**
	 * 审批保存
	 * 
	 * @return
	 */
	public String doApprove() {
		// 重新申请需要验证
		Boolean succeed = validateWholePage();
		if (!succeed) {
			return null;
		}
		boolean pass = false;
		if (approveStatus != null) {
			if ("Y".equals(approveStatus)) {
				// 重复申请销户相关提示
				if ("C".equals(procBankAccount.getOperateType())
						&& !MathUtil.isEmptyOrNull(procBankAccount
								.getProcInstIdClosed())) {
					BankAccountMain accountMain = bankAccountService
							.findBankAccountMainByProcId(procBankAccount
									.getProcInstIdClosed());
					if (MathUtil.isEmptyOrNull(accountMain.getProcInstId())) {
						MessageUtils.addErrorMessage("msg", "当前申请数据已销户！");
						return null;
					}
				}
				pass = true;
			}
			try {
				bankAccountService.doApprove(procBankAccount, pass, stepName);
				MessageUtils.addSuccessMessage(
						"doneMsg",
						stepName
								+ MessageUtils.getMessage(
										"process_step_save_success",
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

	/**
	 * 验证确认表单必填字段
	 * 
	 * @return
	 */
	private Boolean validateAffirmForm() {
		Boolean succeed = true;
		// 验证表单类型公共字段
		if (MathUtil.isEmptyOrNull(accountBankName)) {
			MessageUtils.addErrorMessage("msg", "请确认所属银行");
			succeed = false;
		}
		if ("N".equals(procBankAccount.getAccountType())) {
			if (MathUtil.isEmptyOrNull(procBankAccount.getCity())) {
				MessageUtils.addErrorMessage("msg", "请选择城市");
				succeed = false;
			}
			if (MathUtil.isEmptyOrNull(procBankAccount.getCity())) {
				MessageUtils.addErrorMessage("msg", "请选择是否需要对账");
				succeed = false;
			}
		}
		return succeed;
	}

	/**
	 * 查看公司账户是否存在
	 * 
	 * @param procBankAccount
	 * @return
	 */
	private Boolean findCompanyAccountByIdent(ProcBankAccount procBankAccount) {
		Boolean isExisted = false;
		List<ProcBankAccount> procBankAccounts = bankAccountService
				.findCompanyAccountByIdent(procBankAccount);
		if (procBankAccounts.size() == 0) {
			isExisted = false;
		} else {
			isExisted = true;
		}
		return isExisted;
	}

	/**
	 * 账号类型选择后执行
	 */
	public void accountTypeChange() {
		if ("Y".equals(procBankAccount.getAccountType())) {
			personalDis = "block";
			notPersonalDis = "none";
		} else {
			personalDis = "none";
			notPersonalDis = "block";
		}
		// 如果是销户则刷新流程实例下拉数据
		if ("C".equals(procBankAccount.getOperateType())) {
			initBankAccountSelect();
		}
	}

	/**
	 * 得到可输入的审批字段
	 */
	private void getInputable() {
		inputableFields = ProcessXmlUtil.getInputableDatas("BankAccount",
				stepName);
	}

	/**
	 * 得到界面不显示字段
	 */
	private void getNotVisibleProperty() {
		notVisibleProperty = ProcessXmlUtil.getNotVisiblePropertyDatas(
				"BankAccount", stepName);
	}

	/**
	 * 不可显示字段检查
	 * 
	 * @return
	 */
	public boolean checkNotVisibleProperty(String fieldName) {
		if (notVisibleProperty != null
				&& notVisibleProperty.contains(fieldName)) {
			return true;
		}
		return false;
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
	 * 创建保存流程
	 */
	public String createProcInstance() {
		// 验证页面信息有效性
		Boolean succeed = validateWholePage();
		if (!succeed) {
			return null;
		}
		// 生成“账户描述”
		generateAccountDesc();
		Company company = entityService.find(Company.class, procBankAccount
				.getCompany().getId());
		procBankAccount.setCompany(company);
		try {
			String procInstId = bankAccountService
					.createProcInstance(procBankAccount);
			MessageUtils.addSuccessMessage("doneMsg", MessageUtils.getMessage(
					"process_new_success",
					processUtilMapService.getTmsIdByFnId(procInstId)));
		} catch (ServiceException e) {
			MessageUtils.addErrorMessage("msg", e.getMessage());
		} catch (Exception e) {
			if (e instanceof EJBException) {
				ServiceException se = (ServiceException) ((EJBException) e)
						.getCause();
				MessageUtils.addErrorMessage("msg", se.getMessage());
			}
			if (e instanceof InvocationTargetException) {
				ServiceException se = (ServiceException) ((InvocationTargetException) e)
						.getTargetException().getCause();
				MessageUtils.addErrorMessage("msg", se.getMessage());
			}
		}
		return "/faces/process/common/processSubed-list.xhtml";
	}

	/**
	 * 验证页面信息
	 */
	private Boolean validateWholePage() {
		Boolean succeed = true;

		if (MathUtil.isEmptyOrNull(procBankAccount.getAccountType())) {
			MessageUtils.addErrorMessage("msg", "请选择账户类型");
			succeed = false;
		}
		if (MathUtil.isEmptyOrNull(procBankAccount.getOperateType())) {
			MessageUtils.addErrorMessage("msg", "请选择操作类型");
			succeed = false;
		}
		if (null == procBankAccount.getCompany().getId()
				|| 0 == procBankAccount.getCompany().getId()) {
			MessageUtils.addErrorMessage("msg", "请选择申请公司名称");
			succeed = false;
		}
		if (MathUtil.isEmptyOrNull(procBankAccount.getBelongBankName())) {
			MessageUtils.addErrorMessage("msg", "请填写开户银行");
			succeed = false;
		}
		if (MathUtil.isEmptyOrNull(procBankAccount.getOpenAccountReason())) {
			MessageUtils.addErrorMessage("msg", "请填写开户（或销户）原因");
			succeed = false;
		}
		// 个人卡验证
		if (null != procBankAccount.getAccountType()
				&& "Y".equals(procBankAccount.getAccountType())) {
			if (MathUtil.isEmptyOrNull(procBankAccount.getCardHolderName())) {
				MessageUtils.addErrorMessage("msg", "请填写持卡人姓名");
				succeed = false;
			}
			if (MathUtil.isEmptyOrNull(procBankAccount.getCardHolderPosition())) {
				MessageUtils.addErrorMessage("msg", "请填写持卡人职位");
				succeed = false;
			}
			if (MathUtil.isEmptyOrNull(procBankAccount.getUseDesc())) {
				MessageUtils.addErrorMessage("msg", "请选择用途");
				succeed = false;
			}
			if (null == procBankAccount.getOwnedCardNumber()) {
				MessageUtils.addErrorMessage("msg", "请填写已有卡数量");
				succeed = false;
			}
			if (null == procBankAccount.getUseDeadline()) {
				MessageUtils.addErrorMessage("msg", "请填写使用期限");
				succeed = false;
			}
			if (MathUtil.isEmptyOrNull(procBankAccount.getUseTime())) {
				MessageUtils.addErrorMessage("msg", "请选择使用时间");
				succeed = false;
			}

			// 非个人卡验证
		} else if (!MathUtil.isEmptyOrNull(procBankAccount.getAccountType())
				&& "N".equals(procBankAccount.getAccountType())) {
			if (MathUtil.isEmptyOrNull(procBankAccount.getCurrency())) {
				MessageUtils.addErrorMessage("msg", "请填写币别");
				succeed = false;
			}
			if (MathUtil.isEmptyOrNull(procBankAccount.getAccountNature())) {
				MessageUtils.addErrorMessage("msg", "请填写账户性质");
				succeed = false;
			}
		}
		// 销户验证
		if (null != procBankAccount.getOperateType()
				&& "C".equals(procBankAccount.getOperateType())) {
			if (MathUtil.isEmptyOrNull(procBankAccount.getBankAccount())) {
				MessageUtils.addErrorMessage("msg", "请填写账号");
				succeed = false;
			}
		}
		return succeed;
	}

	/**
	 * 初始化操作类型
	 */
	public void initOp() {
		String op = JSFUtils.getParamValue("op");
		if (op != null && !"".equals(op)) {
			// 查看表单详细
			if ("view".equals(op)) {
				isPatch = JSFUtils.getParamValue("isPatch");
				setProcInstId(JSFUtils.getParamValue("procInstId"));
				setStepName(JSFUtils.getParamValue("stepName"));
				this.findProcInstance();
				this.getNotVisibleProperty();
				initData();
			}
			// 到审批页面
			if ("approve".equals(op)) {
				// add on 2013-5-17
				currentIndex = Integer.parseInt(JSFUtils
						.getParamValue("currentIndex"));
				currentTaskType = (String) JSFUtils
						.getParamValue("currentTaskType");

				setProcInstId((String) JSFUtils.getRequest().getAttribute(
						"procInstId"));
				setStepName((String) JSFUtils.getRequest().getAttribute(
						"stepName"));
				this.findProcInstance();
				this.getInputable();
				initData();
				// 只有“集团资金部门”处理才可编辑“SAP银行存款活期科目号”“SAP业务对应科目”“TMS银行账户代码”
				if ("集团资金部门审批".equals(stepName)) {
					isDisabled = false;
				}
				// 非重新申请页面设置“处理意见”默认值
				if (!"申请".equals(stepName)) {
					procBankAccount.setPeMemo("同意");
				}
			}
		} else {
			initCompany(false);
			initBank();
			initAccountNature();
			initCity();
			procBankAccount.setOwnedCardNumber(0);
			procBankAccount.setUpdatedDatetime(DateUtil.getNowDate().toDate());
			procBankAccount.setCreatedBy(loginService.getCurrentUserName());
		}
	}

	/**
	 * 初始化查看审批数据
	 */
	private void initData() {
		initCompany(true);
		initBank();
		initCity();
		bankChange();
		accountTypeChange();
		setBankName();
		operateTypeChange();
	}

	/**
	 * 设置银行显示名称
	 */
	private void setBankName() {
		Bank bank = bankAccountService.findBankByCode(procBankAccount
				.getBankCode());
		setAccountBankName(bank.getBankName());
	}

	/**
	 * 初始化城市下拉
	 */
	private void initCity() {
		citySelect = dictBean.getDictByCode("TMS.CITY.NAME");
	}

	/**
	 * 查询流程实例
	 */
	private void findProcInstance() {
		procBankAccount = bankAccountService.findProcAccountBy(procInstId);

		if ("true".equals(isPatch)) {
			detailVos = patchMainService.getProcessDetailFor411(procInstId);
		} else {
			detailVos = peManager.getProcessDetail(procInstId);
		}

	}

	/**
	 * 初始化账户性质下拉
	 */
	private void initAccountNature() {
		accountNatureSelect = dictBean.getDictByCode("TMS.ACCOUNT.NATURE.TYPE");
	}

	/**
	 * 验证“账户标识”是否已存在
	 */
	public Boolean validateExists() {
		return bankAccountService.findCompanyAccountBy(procBankAccount
				.getAccountIdent());
	}

	/**
	 * 初始化所属公司下拉
	 */
	private void initBank() {
		bankSelect = dictBean.getDictByCode("BPM.ACCOUNT.BANK.CODE");
	}

	/**
	 * 初始化公司下拉
	 */
	public void initCompany(boolean created) {
		if (created) {
			companySelect = bankAccountService
					.initCompanySelect(procBankAccount);
		} else {
			companySelect = companyTmsService.getCompanySelect();
		}
		currencySelect = dictBean
				.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
	}

	/**
	 * 账户性质选择之后执行
	 */
	public void accountNatureChange() {
		if ("V".equals(procBankAccount.getAccountNature())
				&& null != procBankAccount.getBankCode()) {
			Bank bank = bankAccountService.findBankBy(procBankAccount
					.getBankCode());
			bankCode = bank.getCounterpartyCode();
		} else {
			bankCode = "";
		}
		this.generateAccountDesc();
	}

	/**
	 * 生成"账户描述"
	 */
	public void generateAccountDesc() {
		// 个人卡
		if ("Y".equals(procBankAccount.getAccountType())) {
			if (null != procBankAccount.getAccountType()
					&& null != procBankAccount.getAccountIdent()) {
				String desc;
				String bankAcc = procBankAccount.getBankAccount();
				Bank bank = bankAccountService.findBankByCode(procBankAccount
						.getBankCode());
				if (null == bankAcc) {
					desc = "个人卡" + " - "
							+ procBankAccount.getCompany().getSapCode() + " - "
							+ bank.getBankName() + " - 人民币" + " - 一般用户";
				} else {
					desc = "个人卡"
							+ " - "
							+ procBankAccount.getCompany().getSapCode()
							+ " - "
							+ bank.getBankName()
							+ " - 人民币"
							+ " - 一般用户"
							+ " - "
							+ bankAcc.substring(bankAcc.length() - 4,
									bankAcc.length());
				}
				procBankAccount.setAccountDesc(desc);
			}
			// 非个人卡
		} else {
			if (null != procBankAccount.getCompany().getId()
					&& null != procBankAccount.getBankCode()
					&& null != procBankAccount.getCurrency()
					&& null != procBankAccount.getAccountNature()) {
				String desc;
				String bankAcc = procBankAccount.getBankAccount();
				Bank bank = bankAccountService.findBankByCode(procBankAccount
						.getBankCode());
				if (null == bankAcc) {
					desc = procBankAccount.getCompany().getSapCode()
							+ " - "
							+ bank.getBankName()
							+ " - "
							+ dictBean
									.getValueByDictCatKey("TMS_TAX_PROJECT_CURRENCY_TYPE_"
											+ procBankAccount.getCurrency())
							+ " - "
							+ dictBean
									.getValueByDictCatKey("TMS_ACCOUNT_NATURE_TYPE_"
											+ procBankAccount
													.getAccountNature());
				} else {
					desc = procBankAccount.getCompany().getSapCode()
							+ " - "
							+ bank.getBankName()
							+ " - "
							+ dictBean
									.getValueByDictCatKey("TMS_TAX_PROJECT_CURRENCY_TYPE_"
											+ procBankAccount.getCurrency())
							+ " - "
							+ dictBean
									.getValueByDictCatKey("TMS_ACCOUNT_NATURE_TYPE_"
											+ procBankAccount
													.getAccountNature())
							+ " - "
							+ bankAcc.substring(bankAcc.length() - 4,
									bankAcc.length());
				}
				procBankAccount.setAccountDesc(desc);
			}
		}
	}

	/**
	 * 总行选择之后执行
	 */
	public void bankChange() {
		Bank bank = bankAccountService
				.findBankBy(procBankAccount.getBankCode());
		this.setBsbCode(bank.getBsbCode());
		this.setUnionBankNo(bank.getUnionBankNo());
		accountNatureChange();
	}

	/**
	 * 币别选择之后执行
	 */
	public void currencyChange() {
		if (null != procBankAccount.getCompany().getId()
				&& null != procBankAccount.getCurrency()) {
			accountPoolCurr = procBankAccount.getCompany().getSapCode()
					+ "-"
					+ dictBean
							.getValueByDictCatKey("TMS_TAX_PROJECT_CURRENCY_TYPE_"
									+ procBankAccount.getCurrency());
		}
		accountNatureChange();
	}

	/**
	 * 选择销户执行
	 */
	public void operateTypeChange() {
		try {
			if ("O".equals(procBankAccount.getOperateType())) {
				closeAccountDis = "none";
				cancelDisable = false;
				// 销户才初始化下拉
			} else if ("C".equals(procBankAccount.getOperateType())) {
				// 初始化流程实例需要账户类型字段
				if (MathUtil.isEmptyOrNull(procBankAccount.getAccountType())) {
					procBankAccount.setOperateType("");
					MessageUtils.addErrorMessage("msg", "请选择账户类型");
					return;
				}
				closeAccountDis = "block";
				cancelDisable = true;
				if (null != procBankAccount.getCompany().getId()) {
					initBankAccountSelect();
				}
			}
		} catch (Exception e) {
			log.error("operateTypeChange方法 选择销户执行 出现异常：", e);
		}
	}

	/**
	 * 初始化流程实例下拉
	 */
	private void initBankAccountSelect() {
		bankAccountSelect = bankAccountService
				.findBankAccountSelect(procBankAccount);
	}

	/**
	 * 流程实例选中执行
	 */
	public void bankAccountChange() {
		try {

			if (MathUtil.isEmptyOrNull(procBankAccount.getAccountType())) {
				MessageUtils.addErrorMessage("msg", "请选择账户类型");
				return;
			}
			BankAccountMain accountMain;
			if (MathUtil.isEmptyOrNull(procBankAccount.getProcInstIdClosed())) {
				accountMain = new BankAccountMain();
			} else {
				accountMain = bankAccountService
						.findBankAccountMainByProcId(procBankAccount
								.getProcInstIdClosed());
			}
			// 设置自动带出的字段
			if ("Y".equals(procBankAccount.getAccountType())) {
				procBankAccount.setCardHolderName(accountMain
						.getCardHolderName());
				procBankAccount.setCardHolderPosition(accountMain
						.getCardHolderPosition());
				procBankAccount.setUseDesc(accountMain.getUseDesc());
				procBankAccount.setOwnedCardNumber(accountMain
						.getOwnedCardNumber());
				procBankAccount.setUseDeadline(accountMain.getUseDeadline());
				procBankAccount.setUseTime(accountMain.getUseTime());
				procBankAccount.setBelongBankName(accountMain
						.getBelongBankName());
			} else {
				procBankAccount.setCurrency(accountMain.getCurrency());
				procBankAccount
						.setAccountNature(accountMain.getAccountNature());
				procBankAccount.setBelongBankName(accountMain
						.getBelongBankName());
			}
		} catch (Exception e) {
			log.error("bankAccountChange方法 流程实例选中执行 出现异常：", e);
		}
	}

	/**
	 * 公司选择后执行的操作
	 */
	public void companyChange() {
		if (MathUtil.isEmptyOrNull(procBankAccount.getOperateType())) {
			MessageUtils.addErrorMessage("msg", "请选择操作类型");
			return;
		}
		if (null != procBankAccount.getCompany().getId()) {
			Company company = entityService.find(Company.class,
					this.procBankAccount.getCompany().getId());
			this.procBankAccount.setCompany(company);
		}
		if ("O".equals(procBankAccount.getOperateType())) {
			closeAccountDis = "none";
			// 销户才初始化下拉
		} else if ("C".equals(procBankAccount.getOperateType())) {
			// 初始化流程实例需要账户类型字段
			if (MathUtil.isEmptyOrNull(procBankAccount.getAccountType())) {
				procBankAccount.setOperateType("");
				MessageUtils.addErrorMessage("msg", "请选择账户类型");
				return;
			}
			closeAccountDis = "block";
			if (null != procBankAccount.getCompany().getId()) {
				initBankAccountSelect();
			}
		}
	}

	public ProcBankAccount getProcBankAccount() {
		return procBankAccount;
	}

	public void setProcBankAccount(ProcBankAccount procBankAccount) {
		this.procBankAccount = procBankAccount;
	}

	public List<SelectItem> getCompanySelect() {
		return companySelect;
	}

	public void setCompanySelect(List<SelectItem> companySelect) {
		this.companySelect = companySelect;
	}

	public List<SelectItem> getBankSelect() {
		return bankSelect;
	}

	public void setBankSelect(List<SelectItem> bankSelect) {
		this.bankSelect = bankSelect;
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

	public List<SelectItem> getCurrencySelect() {
		return currencySelect;
	}

	public void setCurrencySelect(List<SelectItem> currencySelect) {
		this.currencySelect = currencySelect;
	}

	public String getPersonalDis() {
		return personalDis;
	}

	public void setPersonalDis(String personalDis) {
		this.personalDis = personalDis;
	}

	public String getNotPersonalDis() {
		return notPersonalDis;
	}

	public void setNotPersonalDis(String notPersonalDis) {
		this.notPersonalDis = notPersonalDis;
	}

	public String getAccountPoolCurr() {
		return accountPoolCurr;
	}

	public void setAccountPoolCurr(String accountPoolCurr) {
		this.accountPoolCurr = accountPoolCurr;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public List<SelectItem> getAccountNatureSelect() {
		return accountNatureSelect;
	}

	public void setAccountNatureSelect(List<SelectItem> accountNatureSelect) {
		this.accountNatureSelect = accountNatureSelect;
	}

	public Boolean getDoNext() {
		return doNext;
	}

	public void setDoNext(Boolean doNext) {
		this.doNext = doNext;
	}

	public String getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}

	public String getBsbCode() {
		return bsbCode;
	}

	public void setBsbCode(String bsbCode) {
		this.bsbCode = bsbCode;
	}

	public String getUnionBankNo() {
		return unionBankNo;
	}

	public void setUnionBankNo(String unionBankNo) {
		this.unionBankNo = unionBankNo;
	}

	public List<SelectItem> getCitySelect() {
		return citySelect;
	}

	public void setCitySelect(List<SelectItem> citySelect) {
		this.citySelect = citySelect;
	}

	public AccountBankSelectBean getAccountBankSelectBean() {
		return accountBankSelectBean;
	}

	public void setAccountBankSelectBean(
			AccountBankSelectBean accountBankSelectBean) {
		this.accountBankSelectBean = accountBankSelectBean;
	}

	public String getAccountBankName() {
		return accountBankName;
	}

	public void setAccountBankName(String accountBankName) {
		this.accountBankName = accountBankName;
	}

	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public void setNotVisibleProperty(List<String> notVisibleProperty) {
		this.notVisibleProperty = notVisibleProperty;
	}

	public String getCloseAccountDis() {
		return closeAccountDis;
	}

	public void setCloseAccountDis(String closeAccountDis) {
		this.closeAccountDis = closeAccountDis;
	}

	public List<SelectItem> getBankAccountSelect() {
		return bankAccountSelect;
	}

	public void setBankAccountSelect(List<SelectItem> bankAccountSelect) {
		this.bankAccountSelect = bankAccountSelect;
	}

	public Boolean getCancelDisable() {
		return cancelDisable;
	}

	public void setCancelDisable(Boolean cancelDisable) {
		this.cancelDisable = cancelDisable;
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
