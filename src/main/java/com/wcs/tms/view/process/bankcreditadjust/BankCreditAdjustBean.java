package com.wcs.tms.view.process.bankcreditadjust;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.view.ViewBaseBean;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.Credit;
import com.wcs.tms.model.CreditO;
import com.wcs.tms.model.CreditP;
import com.wcs.tms.model.CreditR;
import com.wcs.tms.model.ProcBankCreditAdjust;
import com.wcs.tms.model.ProcRptAdjustO;
import com.wcs.tms.model.ProcRptAdjustProv;
import com.wcs.tms.model.ProcRptAdjustProvO;
import com.wcs.tms.service.process.bankcreditadjust.BankCreditAdjustService;
import com.wcs.tms.service.process.bankcreditadjust.entity.Provider;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessUtilService;
import com.wcs.tms.service.process.common.patch.service.PatchMainService;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

/**
 * 
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description:授信调剂Bean
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
public class BankCreditAdjustBean extends ViewBaseBean<ProcBankCreditAdjust> {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory
			.getLog(BankCreditAdjustBean.class);

	@Inject
	EntityService entityService;
	@Inject
	CommonBean dictBean;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	BankCreditAdjustService bankCreditAdjustService;
	@Inject
	BankService bankService;
	@Inject
	LoginService loginService;
	@Inject
	ProcessUtilService processUtilService;// 9.5
	@Inject
	ProcessUtilMapService processUtilMapService;// 9.11
	@Inject
	PatchMainService patchMainService;
	@Inject
	PEManager peManager;

	// 授信调剂流程实体对象
	private ProcBankCreditAdjust procBankCreditAdjust = new ProcBankCreditAdjust();
	// 公司下拉菜单
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
	// 分支银行下拉菜单
	private List<SelectItem> childBankSelect = new ArrayList<SelectItem>();
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

	// 授信提供方pojo
	private Provider provider = new Provider();
	// 提供额度备份
	private Double creditReduceBk = 0d;

	// 授信提供方列表
	private List<Provider> providers = new ArrayList<Provider>();
	// 授信提供方下拉列表
	private List<SelectItem> providerSelect = new ArrayList<SelectItem>();
	// 提供方弹出窗操作
	private String providerDlgOp;

	// 申请方其他产品实体对象
	private ProcRptAdjustO procRptAdjustO = new ProcRptAdjustO();
	// 提供方实体对象
	private ProcRptAdjustProv procRptAdjustProv = new ProcRptAdjustProv();
	// 提供方其他产品实体对象
	private ProcRptAdjustProvO procRptAdjustProvO = new ProcRptAdjustProvO();

	private String menuTwo;// 二级菜单参数

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
	public void initBankCreditAdjust() {
		log.info("BankCreditAdjustBean~~~~~~~~~~~~~~");
		initDict();
		initOp();
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
				menuTwo = JSFUtils.getRequestParam("menu2");
				procInstId = JSFUtils.getParamValue("procInstId");
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

				procInstId = JSFUtils.getParamValue("procInstId");
				stepName = JSFUtils.getParamValue("stepName");
				this.getInputable();
				this.findProcInstance();
				initCompany(true);
				// 非重新申请页面设置“处理意见”默认值
				if (!"申请".equals(stepName)) {
					procBankCreditAdjust.setPeMemo("同意");
				}
			}
		} else {
			if (loginService.isCopUser()) {
				initCompany(true);
			} else {
				initCompany(false);
			}
		}
	}

	/**
	 * 初始化数据字典
	 */
	public void initDict() {
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
		childBankSelect = bankCreditAdjustService
				.getCreditBankSelect(procBankCreditAdjust.getCompany().getId());
		if (childBankSelect.size() != 0) {
			SelectItem si = childBankSelect.get(0);
			Bank b = procBankCreditAdjust.getBank();
			if (b == null || b.getId() == null) {
				procBankCreditAdjust.getBank().setId((Long) si.getValue());
			}
			this.ajaxCredit();
		}
	}

	/**
	 * 公司+银行得到授信额度
	 */
	public void ajaxCredit() {
		Credit c = new Credit();
		if (!"view".equals(JSFUtils.getParamValue("op"))
				&& !"集团资金部门经理确认".equals(stepName)) {

			Bank childBank = entityService.find(Bank.class,
					procBankCreditAdjust.getBank().getId());
			procBankCreditAdjust.setBank(childBank);
			c = bankCreditAdjustService.getCredit(procBankCreditAdjust
					.getCompany().getId(), childBank.getId());
			procBankCreditAdjust.setCreditOri(c.getCreditLine() == null ? 0d
					: c.getCreditLine());
			procBankCreditAdjust.setCreditCu(c.getCreditLineCu());

			Double creditAdd = procBankCreditAdjust.getCreditAdd() == null ? 0d
					: procBankCreditAdjust.getCreditAdd();
			// other credit stuff
			procBankCreditAdjust.setLiquCred((c.getLiquCred() == null ? 0d : c
					.getLiquCred()) + creditAdd);
			procBankCreditAdjust.setBankAcpt((c.getBankAcpt() == null ? 0d : c
					.getBankAcpt()) + creditAdd);
			procBankCreditAdjust
					.setImportCredit((c.getImportCredit() == null ? 0d : c
							.getImportCredit()) + creditAdd);
			procBankCreditAdjust
					.setImportFinance((c.getImportFinance() == null ? 0d : c
							.getImportFinance()) + creditAdd);
			procBankCreditAdjust
					.setOutportFinance((c.getExportFinance() == null ? 0d : c
							.getExportFinance()) + creditAdd);
			procBankCreditAdjust
					.setDollarFlow((c.getDollarFlowFinance() == null ? 0d : c
							.getDollarFlowFinance()) + creditAdd);
			procBankCreditAdjust
					.setDomesticCred((c.getDomesticCred() == null ? 0d : c
							.getDomesticCred()) + creditAdd);
			procBankCreditAdjust.setBussTicket((c.getBussTicket() == null ? 0d
					: c.getBussTicket()) + creditAdd);
			procBankCreditAdjust.setForwTrade((c.getForwTrade() == null ? 0d
					: c.getForwTrade()) + creditAdd);
			List<ProcRptAdjustO> procRptAdjustOs = new ArrayList<ProcRptAdjustO>();
			for (CreditO co : c.getCreditOs()) {
				ProcRptAdjustO pao = new ProcRptAdjustO();
				pao.setOtherName(co.getOtherName());
				pao.setOtherLimit((co.getOtherLimit() == null ? 0d : co
						.getOtherLimit()) + creditAdd);
				pao.setProcBankCreditAdjust(procBankCreditAdjust);
				procRptAdjustOs.add(pao);
			}
			procBankCreditAdjust.setProcRptAdjustOs(procRptAdjustOs);
		}

		this.initProvider(c.getId());
	}

	/**
	 * 初始化授信提供方下拉
	 */
	public void initProvider(Long exceptCid) {
		Bank childBank = procBankCreditAdjust.getBank();
		providerSelect = bankCreditAdjustService.findProviders(childBank,
				exceptCid);
	}

	/**
	 * 工厂流程创建保存
	 */
	public String createProcInstance() {
		Company company = entityService.find(Company.class,
				procBankCreditAdjust.getCompany().getId());
		procBankCreditAdjust.setCompany(company);

		try {
			String procInstId = bankCreditAdjustService
					.createProcInstance(procBankCreditAdjust);
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
	 * 集团流程创建保存
	 */
	public String createProcInstanceCop() {
		ajaxCredit();
		if (loginService.isCopUser()) {
			if (providers == null || providers.size() == 0) {
				MessageUtils.addErrorMessage("msg",
						MessageUtils.getMessage("msg_creditProviderNoNull"));
				return null;
			}
			Double reduceSum = 0d;
			for (Provider p : providers) {
				reduceSum = reduceSum + p.getCreditReduce();
			}
			if (procBankCreditAdjust.getCreditAdd() != null
					&& !reduceSum.equals(procBankCreditAdjust.getCreditAdd())) {
				MessageUtils.addErrorMessage("msg", MessageUtils
						.getMessage("msg_confirmCreditNotEquersApply"));
				return null;
			}
		}

		Company company = entityService.find(Company.class,
				procBankCreditAdjust.getCompany().getId());
		procBankCreditAdjust.setCompany(company);
		try {
			String procInstId = bankCreditAdjustService.createProcInstanceCop(
					procBankCreditAdjust, providers);
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
		procBankCreditAdjust = bankCreditAdjustService
				.findProcInstanceByProcInstId(procInstId);
		providers = procBankCreditAdjust.getProviders();
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
		inputableFields = ProcessXmlUtil.getInputableDatas("BankCreditAdjust",
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
	 * 工厂审批保存
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
				bankCreditAdjustService.doApprove(procBankCreditAdjust, pass,
						stepName);
				MessageUtils.addSuccessMessage(
						"doneMsg",
						stepName
								+ MessageUtils.getMessage("save_success_1",
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
	 * 集团审批保存
	 * 
	 * @return
	 */
	public String doCopApprove() {
		if (!this.approveValidater()) {
			ajaxCredit();
			return null;
		}

		boolean pass = false;
		if (approveStatus != null) {
			if ("Y".equals(approveStatus)) {
				pass = true;
			}
			try {
				bankCreditAdjustService.doCopApprove(procBankCreditAdjust,
						pass, stepName, providers);
				MessageUtils.addSuccessMessage(
						"doneMsg",
						stepName
								+ MessageUtils.getMessage("save_success_1",
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
	 * 集团审批验证
	 * 
	 * @return
	 */
	private boolean approveValidater() {
		if (approveStatus != null && "Y".equals(approveStatus)) {
			if (providers == null || providers.size() == 0) {
				MessageUtils.addErrorMessage("msg",
						MessageUtils.getMessage("msg_creditProviderNoNull"));
				return false;
			}
		}
		if (procBankCreditAdjust.getPeMemo() == null
				|| "".equals(procBankCreditAdjust.getPeMemo())) {
			MessageUtils.addErrorMessage("msg",
					MessageUtils.getMessage("msg_approveRemarkNoNull"));
			return false;
		}

		return true;
	}

	/**
	 * 添加提供方弹出dialog
	 */
	public void clear() {
		provider = new Provider();
		providerDlgOp = MessageUtils.getMessage("btn_add");

		// 重新初始化提供方下拉
		this.ajaxCredit();
		List<SelectItem> removeSis = new ArrayList<SelectItem>();
		for (Provider p : providers) {
			for (SelectItem si : providerSelect) {
				if (p.getProviderId().equals(si.getValue())) {
					removeSis.add(si);
					break;
				}
			}
		}
		providerSelect.removeAll(removeSis);
	}

	/**
	 * 弹出提供方修改dialog<
	 */
	public void toEditProvider() {
		providerDlgOp = MessageUtils.getMessage("btn_recompose");
		creditReduceBk = provider.getCreditReduce();

		// 重新初始化提供方下拉
		this.ajaxCredit();
		// 9.24 add by liushengbin,修改提供方信息，把剩余授信额度带出来
		ajaxProviderBank();
	}

	/**
	 * <p>
	 * Description: 删除提供方
	 * </p>
	 */
	public void toDeleteProvider() {
		if (providers.contains(provider)) {
			providers.remove(provider);
		}

		// 9.26 liushengbin modify
		// 需求变动：被调剂金额可以大于或不等于申请调剂额度金额，如果不等于申请调剂金额，则自动以调剂后的总和为准。
		// 这里及时更新 页面中的 删除的授信额度（更新为调剂的授信的额度）
		Double reduceSum = 0d;
		for (Provider p : providers) {
			reduceSum = reduceSum + p.getCreditReduce();
		}
		procBankCreditAdjust.setCreditAdd(reduceSum);
	}

	/**
	 * 授信提供方得到授信银行
	 */
	public void ajaxProviderBank() {
		Long providerId = provider.getProviderId();
		Credit c = bankCreditAdjustService.findCreditFetch(providerId);
		provider.setProviderName(c.getCompany().getCompanyName());
		provider.setProviderBankName(c.getBank().getBankName());
		provider.setCreditLine(c.getCreditLine());
		// 9.20 add by
		// liushengbin【剩余授信额度】=【该公司当前的授信额度】+【有效期内从其他公司调剂获得的授信额度】-【有效期内调剂给其他公司的授信额度】

		List<CreditR> creditRs = c.getCreditRs();
		List<CreditP> creditPs = c.getCreditPs();
		// 从其他公司调剂获得的授信额度
		Double creditR = 0d;
		// 调剂给其他公司的授信额度
		Double creditP = 0d;
		for (CreditR r : creditRs) {
			creditR = creditR + r.getCreditAdd();
		}
		for (CreditP p : creditPs) {
			creditP = creditP + p.getCreditReduce();
		}
		// credit表数据库实时更新 modified on 2013-6-26
		provider.setCreditLineLeft(c.getCreditLine());

	}

	/**
	 * 保存授信提供方
	 */
	public void saveProvider() {
		boolean check = true;
		if (provider.getProviderId() == null || provider.getProviderId() == 0l) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_creditProviderNoNull"));
			check = false;
		}

		if (provider.getCreditReduce() == null
				|| provider.getCreditReduce() == 0d) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_confirmCreditNoNull"));
			check = false;
		}
		if (!check) {
			return;
		}

		if (providerDlgOp.equals("添加")) {
			if (provider.getCreditReduce() > provider.getCreditLineLeft()) {
				MessageUtils
						.addErrorMessage(
								"errorMsg",
								MessageUtils
										.getMessage("msg_adjustAmountNoBeyandCreditAll")
										+ provider.getCreditLineLeft() + "！");
				return;
			}
			providers.add(provider);
		}
		if (providerDlgOp.equals("修改")) {
			if (provider.getCreditReduce() > provider.getCreditLineLeft()) {
				MessageUtils
						.addErrorMessage(
								"errorMsg",
								MessageUtils
										.getMessage("msg_adjustAmountNoBeyandCreditAll")
										+ provider.getCreditLineLeft() + "！");
				provider.setCreditReduce(creditReduceBk);
				return;
			}
		}
		// 9.20 liushengbin modify
		// 需求变动：被调剂金额可以大于或不等于申请调剂额度金额，如果不等于申请调剂金额，则自动以调剂后的总和为准。
		// 这里及时更新 页面中的 增加的授信额度（更新为调剂的授信的额度）
		Double reduceSum = 0d;
		for (Provider p : providers) {
			reduceSum = reduceSum + p.getCreditReduce();
		}
		procBankCreditAdjust.setCreditAdd(reduceSum);

	}

	/***** 确认页面 *******************************************************/

	/**
	 * 弹出申请方其他产品的确认dialog
	 */
	public void toEditRequestOther() {
		return;
	}

	/**
	 * 申请方其他产品的修改保存
	 */
	public void saveRequestOther() {
		return;
	}

	/**
	 * 弹出提供方的确认diolog
	 */
	public void toConfirmProvider() {
		creditReduceBk = procRptAdjustProv.getCreditReduce();
		return;
	}

	/**
	 * 弹出提供方的确认保存
	 */
	public void confirmProvider() {
		this.checkProviderTotal(procRptAdjustProv);
		return;
	}

	/**
	 * 检查提供方授信额度数据
	 */
	private void checkProviderTotal(ProcRptAdjustProv p) {
		Double creditTotal = p.getCreditTotal();
		if (p.getLiquCred() > creditTotal) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_workingCapitalLoansError"));
		}
		if (p.getBankAcpt() > creditTotal) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_bankAcceptanceError"));
		}
		if (p.getImportCredit() > creditTotal) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_inLetterCreditError"));
		}
		if (p.getImportFinance() > creditTotal) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_importBillAdvanceError"));
		}
		if (p.getOutportFinance() > creditTotal) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_outportBillAdvanceError"));
		}
		if (p.getDollarFlow() > creditTotal) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_dollarFlowError"));
		}
		if (p.getDomesticCred() > creditTotal) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_civilLetterCreditError"));
		}
		if (p.getBussTicket() > creditTotal) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_businessTicketError"));
		}
		if (p.getForwTrade() > creditTotal) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_forwTradeError"));
		}
		for (ProcRptAdjustProvO o : p.getProcRptAdjustProvOs()) {
			if (o.getOtherLimit() > creditTotal) {
				MessageUtils.addErrorMessage("errorMsg",
						MessageUtils.getMessage("msg_otherProductsError"));
			}
		}
	}

	/**
	 * 弹出提供方其他产品的确认dialog
	 */
	public void toEditProviderOther() {
		return;
	}

	/**
	 * 提供方其他产品的修改保存
	 */
	public void saveProviderOther() {
		if (procRptAdjustProv.getCreditReduce() > creditReduceBk
				+ procRptAdjustProv.getCreditTotal()) {
			MessageUtils.addErrorMessage(
					"errorMsg",
					MessageUtils.getMessage("msg_bankCheckAmountNobeyandApply")
							+ creditReduceBk
							+ procRptAdjustProv.getCreditTotal() + "！");
			provider.setCreditReduce(creditReduceBk);
			return;
		}
		return;
	}

	/**
	 * 确认提交
	 */
	public String doConfirm() {
		if (!this.confirmValidater()) {
			return null;
		}

		boolean pass = false;
		if (approveStatus != null) {
			if ("Y".equals(approveStatus)) {
				pass = true;
			}
			try {
				bankCreditAdjustService.doConfirm(procBankCreditAdjust, pass,
						stepName);
				MessageUtils.addSuccessMessage(
						"doneMsg",
						stepName
								+ MessageUtils.getMessage("save_success_1",
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
	 * 集团确认验证
	 * 
	 * @return
	 */
	private boolean confirmValidater() {
		// 检查备注
		if (procBankCreditAdjust.getPeMemo() == null
				|| "".equals(procBankCreditAdjust.getPeMemo())) {
			MessageUtils.addErrorMessage("msg",
					MessageUtils.getMessage("msg_confirmRemarkNoNull"));
			return false;
		}
		Double reduceSum = 0d;
		for (ProcRptAdjustProv p : procBankCreditAdjust.getProcRptAdjustProvs()) {
			reduceSum = reduceSum + p.getCreditReduce();
		}
		// 检查调剂总额
		if (procBankCreditAdjust.getCreditAdd() != null
				&& !reduceSum.equals(procBankCreditAdjust.getCreditAdd())) {
			MessageUtils.addErrorMessage("msg",
					MessageUtils.getMessage("msg_confirmCreditNotEquersApply"));
			return false;
		}
		// 检查授信额度其他数据
		if (!checkRequestTotal(procBankCreditAdjust)) {
			return false;
		}
		return true;
	}

	/**
	 * 检查申请方授信额度数据
	 */
	private boolean checkRequestTotal(ProcBankCreditAdjust p) {
		boolean checkFlag = true;
		Double creditTotal = p.getCreditOri() + p.getCreditAdd();
		if (p.getLiquCred() > creditTotal) {
			// MessageUtils.addErrorMessage("errorMsg",
			// "流动资金贷款 大于 银行核准后总额度，请更正！");
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_workingCapitalLoansError"));
			checkFlag = false;
		}
		if (p.getBankAcpt() > creditTotal) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_bankAcceptanceError"));
			checkFlag = false;
		}
		if (p.getImportCredit() > creditTotal) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_inLetterCreditError"));
			checkFlag = false;
		}
		if (p.getImportFinance() > creditTotal) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_importBillAdvanceError"));
			checkFlag = false;
		}
		if (p.getOutportFinance() > creditTotal) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_outportBillAdvanceError"));
			checkFlag = false;
		}
		if (p.getDollarFlow() > creditTotal) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_dollarFlowError"));
			checkFlag = false;
		}
		if (p.getDomesticCred() > creditTotal) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_civilLetterCreditError"));
			checkFlag = false;
		}
		if (p.getBussTicket() > creditTotal) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_businessTicketError"));
			checkFlag = false;
		}
		if (p.getForwTrade() > creditTotal) {
			MessageUtils.addErrorMessage("errorMsg",
					MessageUtils.getMessage("msg_forwTradeError"));
			checkFlag = false;
		}
		for (ProcRptAdjustO o : p.getProcRptAdjustOs()) {
			if (o.getOtherLimit() > creditTotal) {
				MessageUtils.addErrorMessage("errorMsg",
						MessageUtils.getMessage("msg_otherProductsError"));
				checkFlag = false;
			}
		}
		return checkFlag;
	}

	/*** set@get **********************************************/

	public String getProviderDlgOp() {
		return providerDlgOp;
	}

	public Double getCreditReduceBk() {
		return creditReduceBk;
	}

	public void setCreditReduceBk(Double creditReduceBk) {
		this.creditReduceBk = creditReduceBk;
	}

	public ProcRptAdjustProvO getProcRptAdjustProvO() {
		return procRptAdjustProvO;
	}

	public void setProcRptAdjustProvO(ProcRptAdjustProvO procRptAdjustProvO) {
		this.procRptAdjustProvO = procRptAdjustProvO;
	}

	public ProcRptAdjustProv getProcRptAdjustProv() {
		return procRptAdjustProv;
	}

	public void setProcRptAdjustProv(ProcRptAdjustProv procRptAdjustProv) {
		this.procRptAdjustProv = procRptAdjustProv;
	}

	public ProcRptAdjustO getProcRptAdjustO() {
		return procRptAdjustO;
	}

	public void setProcRptAdjustO(ProcRptAdjustO procRptAdjustO) {
		this.procRptAdjustO = procRptAdjustO;
	}

	public void setProviderDlgOp(String providerDlgOp) {
		this.providerDlgOp = providerDlgOp;
	}

	public List<SelectItem> getProviderSelect() {
		return providerSelect;
	}

	public void setProviderSelect(List<SelectItem> providerSelect) {
		this.providerSelect = providerSelect;
	}

	public List<Provider> getProviders() {
		return providers;
	}

	public void setProviders(List<Provider> providers) {
		this.providers = providers;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public ProcBankCreditAdjust getProcBankCreditAdjust() {
		return procBankCreditAdjust;
	}

	public void setProcBankCreditAdjust(
			ProcBankCreditAdjust procBankCreditAdjust) {
		this.procBankCreditAdjust = procBankCreditAdjust;
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
