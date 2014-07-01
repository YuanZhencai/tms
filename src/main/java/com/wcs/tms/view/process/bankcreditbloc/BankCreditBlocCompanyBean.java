package com.wcs.tms.view.process.bankcreditbloc;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.MathUtil;
import com.wcs.base.util.StringUtils;
import com.wcs.base.view.ViewBaseBean;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.ProcBankCreditBloc;
import com.wcs.tms.model.ProcBankCreditBlocCompany;
import com.wcs.tms.model.ProcRptBankCreditBloc;
import com.wcs.tms.service.process.bankcreditbloc.BankCreditBlocCompanyService;
import com.wcs.tms.service.process.bankcreditbloc.BankCreditBlocService;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessUtilService;
import com.wcs.tms.service.process.common.ProcessWaitAcceptService;
import com.wcs.tms.service.process.common.patch.service.PatchMainService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.service.system.company.CreditService;
import com.wcs.tms.util.CustomPageModel;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

import filenet.vw.api.VWWorkObjectNumber;

/**
 * 
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description: 银行授信申请集团(成员公司)
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@ManagedBean
@ViewScoped
public class BankCreditBlocCompanyBean extends
		ViewBaseBean<ProcBankCreditBlocCompany> {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory
			.getLog(BankCreditBlocCompanyBean.class);

	/** 人民币流贷下拉 */
	private List<SelectItem> rmbFlowSelect = new ArrayList<SelectItem>();
	/** 进口押汇下拉 */
	private List<SelectItem> importFinanceLinkSelect = new ArrayList<SelectItem>();
	/** 页面控件可编辑控制 */
	private Boolean float1Disable = false;
	private Boolean float2Disable = false;
	private Boolean floatFlagDisable = false;
	private Boolean dollarFlowPointDisable = false;
	private Boolean importFinancePonitDisable = false;
	private Boolean exportFinancePonitDisable = false;
	/** 申请日期 */
	private String registerDate;
	/** 集团公司Id */
	private Long companyId;
	/** 公司下拉选择框 */
	private List<SelectItem> companyNameSelect = new ArrayList<SelectItem>();
	/** 成员公司Id */
	private Long memberComanyId;
	/** 成员公司下拉 */
	private List<SelectItem> memberComanySelect = new ArrayList<SelectItem>();
	/** 资金币种下拉 */
	private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
	/** 一级银行 */
	private Long topBankId;
	/** 一级机构银行下拉 */
	private List<SelectItem> topLevelSelect = new ArrayList<SelectItem>();
	/** 选择支行Id */
	private Long branchSelectId;
	/** 支行下拉 */
	private List<SelectItem> branchSelect = new ArrayList<SelectItem>();
	/** 利率挂钩下拉 */
	private List<SelectItem> rateHookSelect = new ArrayList<SelectItem>();
	/** 银行授信集团申请 */
	private ProcBankCreditBloc procBankBlocCredit = new ProcBankCreditBloc();
	/** 银行授信集团申请其他产品 */
	private ProcRptBankCreditBloc blocCreditRpt = new ProcRptBankCreditBloc();
	/** 银行授信集团申请其他产品编辑 */
	private ProcRptBankCreditBloc blocCreditRptEdit = new ProcRptBankCreditBloc();
	private ProcRptBankCreditBloc blocCreditRptEditVo = new ProcRptBankCreditBloc();
	private ProcRptBankCreditBloc blocCreditRptCopy = new ProcRptBankCreditBloc();
	/** 记录授信其他产品编辑之前的名称 */
	private String prtBlocCreditNameEdit;
	/** 审批步骤名称 */
	private String stepName;
	/** 银行授信申请集团其他产品 */
	private List<ProcRptBankCreditBloc> proRptCreditList = Lists.newArrayList();
	/** 银行授信申请集团其他产品分页模型 */
	private LazyDataModel<ProcRptBankCreditBloc> processBlocRptLayModel;

	/** 流程详细List数据 */
	private List<ProcessDetailVo> processDetailList = new ArrayList<ProcessDetailVo>();

	// 是否处理下一个任务9.5
	private boolean doNext = true;
	// 当前所处理任务在任务列表中的位置
	private Integer currentIndex;
	// 当前处理任务类型（已接受和待接收）
	private String currentTaskType;
	// 流程实例ID
	private String procInstId;
	@Inject
	private CompanyTmsService companyTmsService;
	@Inject
	private BankCreditBlocService bankCreditBlocService;
	@Inject
	private BankCreditBlocCompanyService bankCreditBlocCompanyService;
	@Inject
	private CreditService creditService;
	@Inject
	private CommonBean dictBean;
	@Inject
	private LoginService loginService;
	@Inject
	private ProcessWaitAcceptService processWaitService;
	@Inject
	ProcessUtilService processUtilService;// 9.5
	@Inject
	ProcessUtilMapService processUtilMapService;// 9.11
	@Inject
	PatchMainService patchMainService;
	@Inject
	PEManager peManager;
	// 是否是补丁表中获取到数据查看，处理2014.4.11~2014.4.28之间发生故障，丢失的流程数据和流程审批日志的查看
	private String isPatch;

	public BankCreditBlocCompanyBean() {
		// list已提价流程界面,input已处理流程界面
		setupPage("/faces/process/common/processSubed-list.xhtml",
				"/faces/process/common/processDealed-list.xhtml", null);
	}

	@PostConstruct
	public void init() {
		log.info("init~~~~~~~~~~~~~~~~~~~~~");
		registerDate = DateUtil.dateToStrShort(DateUtil.getNowDateShort());
		String audit = JSFUtils.getParamValue("op");
		String workclassNumber = JSFUtils.getParamValue("procInstId");
		isPatch = JSFUtils.getParamValue("isPatch");
		// 是否是审批和查看
		if (audit != null && workclassNumber != null) {
			String wcnum = String.valueOf(workclassNumber);
			setProcInstId(wcnum);
			if ("approve".equals(audit)) {
				// add on 2013-5-17
				currentIndex = Integer.parseInt(JSFUtils
						.getParamValue("currentIndex"));
				currentTaskType = (String) JSFUtils
						.getParamValue("currentTaskType");

				stepName = JSFUtils.getParamValue("stepName");
			}
			ProcBankCreditBlocCompany blocCompany = bankCreditBlocCompanyService
					.findBlocCompanyByProceIn(wcnum);
			if (blocCompany != null) {
				List<ProcRptBankCreditBloc> dataList = bankCreditBlocCompanyService
						.findBlocRptByBlocCom(blocCompany.getId());
				proRptCreditList.clear();
				proRptCreditList.addAll(dataList);
				processBlocRptLayModel = new CustomPageModel<ProcRptBankCreditBloc>(
						proRptCreditList, false);
				setInstance(blocCompany);
			}
			initData(wcnum);
			searchProcessDetail();
			getInstance().setPeMemo("同意");
		} else {
			keepUserData();
		}
		/************************************************/
		// modified by yanchangjing on 2012-9-25
		if (null == getInstance().getLiquCred()) {
			getInstance().setLiquCred(0.00);
		}
		if (null == getInstance().getBankAcpt()) {
			getInstance().setBankAcpt(0.00);
		}
		if (null == getInstance().getImportCredit()) {
			getInstance().setImportCredit(0.00);
		}
		if (null == getInstance().getImportFinance()) {
			getInstance().setImportFinance(0.00);
		}
		if (null == getInstance().getExportFinance()) {
			getInstance().setExportFinance(0.00);
		}
		if (null == getInstance().getDollarFlowFinance()) {
			getInstance().setDollarFlowFinance(0.00);
		}
		if (null == getInstance().getDomesticCred()) {
			getInstance().setDomesticCred(0.00);
		}
		if (null == getInstance().getBussTicket()) {
			getInstance().setBussTicket(0.00);
		}
		if (null == getInstance().getForwTrade()) {
			getInstance().setForwTrade(0.00);
		}
		/************************************************/
	}

	/**
	 * 查看集团审批信息
	 * 
	 * @return
	 */
	public void toViewBlocDetail() {
		String url = "/faces/process/bankCreditBloc/creditBloc-view.xhtml";
		// 设置弹出窗口url的参数
		RequestContext.getCurrentInstance().addCallbackParam("viewPage",
				StringUtils.safeString(JSFUtils.contextPath() + url));
		RequestContext.getCurrentInstance().addCallbackParam("op", "view");
		RequestContext.getCurrentInstance().addCallbackParam("menu2",
				StringUtils.safeString(JSFUtils.getParamValue("menu2")));
		RequestContext.getCurrentInstance().addCallbackParam("procInstId",
				StringUtils.safeString(procBankBlocCredit.getProcInstId()));
	}

	/**
	 * “出口押汇”可编辑控制
	 */
	public void exportFinanceLinkChange() {
		// 3、再议
		if ("3".equals(getInstance().getExportFinanceLink())) {
			setExportFinancePonitDisable(true);
		} else {
			setExportFinancePonitDisable(false);
		}
	}

	/**
	 * “进口押汇”可编辑控制
	 */
	public void importFinanceLinkChange() {
		// 3、再议
		if ("3".equals(getInstance().getImportFinanceLink())) {
			setImportFinancePonitDisable(true);
		} else {
			setImportFinancePonitDisable(false);
		}
	}

	/**
	 * “美元流贷”可编辑控制
	 */
	public void dollarFlowLinkChange() {
		// 3、再议 4、固定
		if ("3".equals(getInstance().getDollarFlowLink())) {
			setDollarFlowPointDisable(true);
		} else {
			setDollarFlowPointDisable(false);
		}
	}

	/**
	 * “人民币流贷”可编辑控制
	 */
	public void liquCredRaChange() {
		// 1、再议 2、固定
		if ("1".equals(getInstance().getLiquCredRa())) {
			float1Disable = true;
			float2Disable = true;
			floatFlagDisable = true;
		} else if ("2".equals(getInstance().getLiquCredRa())) {
			float1Disable = true;
			float2Disable = false;
			floatFlagDisable = false;
		} else {
			float1Disable = false;
			float2Disable = false;
			floatFlagDisable = false;
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 添加银行授信集团成员公司申请其他产品
	 * </p>
	 */
	public void addBlocCreditRpt() {
		boolean flag = false;
		// 查询产品名称是否存在
		if (getInstance().getProcInstId() != null
				&& !"".equals(getInstance().getProcInstId())) {
			flag = bankCreditBlocCompanyService.findProcBlocRptCreditByName(
					getInstance().getId(), blocCreditRpt.getCdProName());
		}
		// 验证本年申请额度是否为空
		Double Credit = getInstance().getCreditLine();
		log.info("本年申请额度:" + Credit);
		try {
			if (getInstance().getCreditLine() == null
					&& "".equals(getInstance().getCreditLine().toString()
							.trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		// 提示用户其他授信产品额度大于本年申请额度
		Boolean cdProlimitIsPass = validationCdProLimit();
		if (cdProlimitIsPass) {
			MessageUtils.addWarnMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_otherPro_larger"));
		}
		boolean rptFlag = false;
		if (!proRptCreditList.isEmpty()) {
			for (ProcRptBankCreditBloc pcredit : proRptCreditList) {
				if (pcredit.getId() != null
						&& pcredit.getId().equals(getInstance().getId())) {
					if (blocCreditRpt.getCdProName().equals(
							pcredit.getCdProName())) {
						rptFlag = true;
						break;
					}
				} else {
					if (blocCreditRpt.getCdProName().equals(
							pcredit.getCdProName())) {
						rptFlag = true;
						break;
					}
				}
			}
			if (false == flag && rptFlag == false) {
				// TODO Auto-generated method stub
			} else {
				if (flag || rptFlag) {
					MessageUtils.addErrorMessage("bankCreditMsg",
							MessageUtils.getMessage("errMsg_existOtherPro"));
					return;
				}
			}
		}
		proRptCreditList.add(blocCreditRpt);
		processBlocRptLayModel = new CustomPageModel<ProcRptBankCreditBloc>(
				proRptCreditList, false);
		setBlocCreditRpt(new ProcRptBankCreditBloc());
		MessageUtils.addSuccessMessage("bankCreditMsg",
				MessageUtils.getMessage("otherPro_new_success"));
	}

	/**
	 * 验证其他产品额度不能大于本次剩余申请额度
	 * 
	 * @return true/false
	 */
	private Boolean validationCdProLimit() {
		Boolean notPass = false;
		Double creditLine = getInstance().getCreditLine(); // 本次申请额度
		Double cdProLimit = this.blocCreditRpt.getCdProLimit(); // 其它授信产品额度
		if (cdProLimit != null && cdProLimit.compareTo(creditLine) > 0) {
			notPass = true;
		}
		return notPass;
	}

	/**
	 * 验证其他产品额度不能大于本次剩余申请额度
	 * 
	 * @return true/false
	 */
	private Boolean validationCdProLimit(Double cdProLimit) {
		Boolean notPass = false;
		Double creditLine = getInstance().getCreditLine(); // 本次申请额度
		// 计算本次剩余申请金额
		for (ProcRptBankCreditBloc pcredit : proRptCreditList) {
			if (!pcredit.equals(blocCreditRptEditVo)) {
				creditLine = creditLine - pcredit.getCdProLimit();
			}
		}
		if (cdProLimit != null && cdProLimit.compareTo(creditLine) > 0) {
			notPass = true;
		}
		return notPass;
	}

	/**
	 * 
	 * <p>
	 * Description:编辑之前
	 * </p>
	 */
	public void beforeEdit() {
		try {
			prtBlocCreditNameEdit = blocCreditRptEdit.getCdProName();
			// 将编辑前的对象属性值赋值出来,因为编辑时将对象赋值到后台，界面修改到编辑用的同一对象，后台验证失败了却更改了值
			PropertyUtils.copyProperties(blocCreditRptCopy, blocCreditRptEdit);
			blocCreditRptEditVo = blocCreditRptEdit;
			blocCreditRptEdit = new ProcRptBankCreditBloc();
			PropertyUtils.copyProperties(blocCreditRptEdit, blocCreditRptCopy);
		} catch (IllegalAccessException e) {
			log.error("beforeEdit方法 编辑之前 出现异常：", e);
		} catch (InvocationTargetException e) {
			log.error("beforeEdit方法 编辑之前 出现异常：", e);
		} catch (NoSuchMethodException e) {
			log.error("beforeEdit方法 编辑之前 出现异常：", e);
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 编辑其他产品
	 * </p>
	 */
	public void editProcRptCredit() {
		boolean flag = false;
		// 查询产品名称是否存在
		if (getInstance().getProcInstId() != null
				&& !"".equals(getInstance().getProcInstId())) {
			flag = bankCreditBlocCompanyService.findProcBlocRptCreditByName(
					getInstance().getId(), blocCreditRptEdit.getCdProName());
		}
		if (flag
				&& prtBlocCreditNameEdit.equals(blocCreditRptEdit
						.getCdProName())) {
			flag = false;
		}
		// 验证本年申请额度是否为空
		Double Credit = getInstance().getCreditLine();
		log.info("本年申请额度:" + Credit);
		try {
			if (getInstance().getCreditLine() == null
					&& "".equals(getInstance().getCreditLine().toString()
							.trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		// 提示用户其他授信产品额度大于本年申请额度
		if (blocCreditRptEdit.getCdProLimit() != null
				&& blocCreditRptEdit.getCdProLimit() > this.getInstance()
						.getCreditLine()) {
			MessageUtils.addWarnMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_otherPro_larger"));
		}
		boolean rptFlag = false;
		if (!proRptCreditList.isEmpty()) {
			for (ProcRptBankCreditBloc pcredit : proRptCreditList) {
				if (pcredit.getProcBankCreditBlocCompany() != null) {
					if (!prtBlocCreditNameEdit.equals(pcredit.getCdProName())
							&& blocCreditRptEdit.getCdProName().equals(
									pcredit.getCdProName())
							&& pcredit
									.getProcBankCreditBlocCompany()
									.getId()
									.equals(blocCreditRptEdit
											.getProcBankCreditBlocCompany()
											.getId())) {
						rptFlag = true;
						break;
					}
				} else {
					if (!prtBlocCreditNameEdit.equals(pcredit.getCdProName())
							&& blocCreditRptEdit.getCdProName().equals(
									pcredit.getCdProName())) {
						rptFlag = true;
						break;
					}
				}
			}
			if (false == flag && rptFlag == false) {
				// TODO Auto-generated method stub
			} else {
				if (flag || rptFlag) {
					MessageUtils.addErrorMessage("bankCreditMsg",
							MessageUtils.getMessage("errMsg_existOtherPro"));
					try {
						PropertyUtils.copyProperties(blocCreditRptEdit,
								blocCreditRptCopy);
					} catch (IllegalAccessException e) {
						log.error("editProcRptCredit方法 编辑其他产品 出现异常：", e);
					} catch (InvocationTargetException e) {
						log.error("editProcRptCredit方法 编辑其他产品 出现异常：", e);
					} catch (NoSuchMethodException e) {
						log.error("editProcRptCredit方法 编辑其他产品 出现异常：", e);
					}
					return;
				}
			}
		}
		if (proRptCreditList.contains(blocCreditRptEditVo)) {
			proRptCreditList.remove(blocCreditRptEditVo);
		}
		proRptCreditList.add(blocCreditRptEdit);
		processBlocRptLayModel = new CustomPageModel<ProcRptBankCreditBloc>(
				proRptCreditList, false);
		MessageUtils.addSuccessMessage("bankCreditMsg",
				MessageUtils.getMessage("otherPro_edit_success"));
	}

	/**
	 * 
	 * <p>
	 * Description: 删除其他授信产品
	 * </p>
	 */
	public void deleteBlocRptCredeit() {
		proRptCreditList.remove(blocCreditRptEdit);
		processBlocRptLayModel = new CustomPageModel<ProcRptBankCreditBloc>(
				proRptCreditList, false);
		MessageUtils.addSuccessMessage("bankCreditMsg",
				MessageUtils.getMessage("otherPro_delete_success"));
	}

	/**
	 * 
	 * <p>
	 * Description: 提交申请
	 * </p>
	 * 
	 * @return
	 */
	public String submitRegister() {
		// huhan add on 8.17
		if (getInstance().getCooperationReason() == null
				|| "".equals(getInstance().getCooperationReason())) {
			// 请输入合作理由简述
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("msg_input_cooperatio"));
			return null;
		}

		// 本年担保验证
		boolean flag = validateCredit();
		if (flag) {
			transportData();
			return null;
		}
		// 授信产品验证
		boolean productFlag = validateCreditProduct();
		if (productFlag) {
			transportData();
			return null;
		}
		String workClassNum = getInstance().getProcInstId();
		// 设置分支银行
		if (branchSelectId != null) {
			Bank bank = bankCreditBlocCompanyService
					.findBankById(branchSelectId);
			getInstance().setBank(bank);
		}
		// 参数Map
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("TMS_Fac_Fund_Pos_Exr", loginService.getCurrentUserName());
		bankCreditBlocCompanyService.vwBlocCompanyDisposeTask(workClassNum,
				paramMap, "工厂资金提交申请");
		// 设置申请提交时间
		getInstance().setCreatedDatetime(new Date());
		// 设置为工厂资金岗位人员提交申请状态
		getInstance().setStatus("4");
		getInstance().setCreatedBy(loginService.getCurrentUserName());
		entityService.update(getInstance());
		// 保存或者更新其他授信产品
		bankCreditBlocCompanyService.batchAddOrUpdateBlocCreditRpt(
				proRptCreditList, getInstance().getId());
		MessageUtils.addSuccessMessage("doneMsg", MessageUtils.getMessage(
				"process_bankApprove_success_1", stepName,
				processUtilMapService.getTmsIdByFnId(getInstance()
						.getProcInstId())));
		return processUtilService.getNextPage(getInputPage(), doNext,
				currentIndex, currentTaskType);
	}

	/**
	 * 
	 * <p>
	 * Description: 工厂财务经理审批
	 * </p>
	 * 
	 * @return
	 */
	public String approveBlocCompany() {
		// 授信产品验证
		boolean productFlag = validateCreditProduct();
		if (productFlag) {
			transportData();
			return null;
		}
		Object obj = JSFUtils.getRequestParam("auit");
		// 审批通过与否参数
		boolean flag = false;
		String memoContent = "";
		if (obj != null) {
			flag = Boolean.valueOf(obj.toString());
			if (flag) {
				memoContent = ProcessXmlUtil.findStepProperty("id",
						"BankCreditBlocBanch", stepName, "passMemo");
				// 设置为未确认状态
				getInstance().setStatus("1");
			} else {
				memoContent = ProcessXmlUtil.findStepProperty("id",
						"BankCreditBlocBanch", stepName, "nopassMemo");
				// 设置为工厂资金岗位人员提交申请状态
				getInstance().setStatus("4");
			}
		}
		// 审批备注，huhan add on 8.2
		String memo = getInstance().getPeMemo() == null ? "" : getInstance()
				.getPeMemo();
		// 审批操作
		bankCreditBlocCompanyService.vwDisposeTask(getInstance()
				.getProcInstId(), flag, memoContent + memo);
		entityService.update(getInstance());
		// 保存或者更新其他授信产品
		bankCreditBlocCompanyService.batchAddOrUpdateBlocCreditRpt(
				proRptCreditList, getInstance().getId());
		if (flag) {
			MessageUtils.addSuccessMessage("doneMsg", MessageUtils.getMessage(
					"process_bankApprove_success_1", stepName,
					processUtilMapService.getTmsIdByFnId(getInstance()
							.getProcInstId())));

		} else {
			MessageUtils.addSuccessMessage(
					"doneMsg",
					stepName
							+ MessageUtils.getMessage(
									"process_bankNotApprove_success",
									processUtilMapService
											.getTmsIdByFnId(getInstance()
													.getProcInstId())));
		}
		return processUtilService.getNextPage(getInputPage(), doNext,
				currentIndex, currentTaskType);
	}

	/**
	 * 
	 * <p>
	 * Description: 放弃提交申请
	 * </p>
	 * 
	 * @return
	 */
	public String surrenderRegister() {
		bankCreditBlocCompanyService.doTerminal(getInstance().getProcInstId());
		MessageUtils.addSuccessMessage(
				"domeMsg",
				getInstance().getCompany().getCompanyName()
						+ MessageUtils.getMessage("process_quitApply",
								processUtilMapService
										.getTmsIdByFnId(getInstance()
												.getProcInstId())));
		return processUtilService.getNextPage(getInputPage(), doNext,
				currentIndex, currentTaskType);
	}

	/**
	 * 
	 * <p>
	 * Description:将用户输入的数据放入Request对象
	 * </p>
	 */
	private void transportData() {
		JSFUtils.getRequest().setAttribute("validateVo", getInstance());
		JSFUtils.getRequest().setAttribute("companyId", companyId);
		JSFUtils.getRequest().setAttribute("topBankId", topBankId);
		JSFUtils.getRequest().setAttribute("branchSelectId", branchSelectId);
		JSFUtils.getRequest()
				.setAttribute("proRptCreditList", proRptCreditList);
		JSFUtils.getRequest().setAttribute("stepName", stepName);
	}

	/**
	 * 
	 * <p>
	 * Description: 保持用户数据
	 * </p>
	 */
	private void keepUserData() {
		// 实体类数据
		Object obj = JSFUtils.getRequest().getAttribute("validateVo");
		// 公司id
		Object companyIdReg = JSFUtils.getRequest().getAttribute("companyId");
		// 一级银行Id
		Object topBankIdReg = JSFUtils.getRequest().getAttribute("topBankId");
		// 分支银行Id
		Object branchSelectReg = JSFUtils.getRequest().getAttribute(
				"branchSelectId");
		// 其他授信产品
		Object proRptCreditListReg = JSFUtils.getRequest().getAttribute(
				"proRptCreditList");
		// 节点名称
		Object stepNameReg = JSFUtils.getRequest().getAttribute("stepName");
		if (stepNameReg != null) {
			stepName = stepNameReg.toString();
		}
		if (obj != null) {
			ProcBankCreditBlocCompany blocComapny = (ProcBankCreditBlocCompany) obj;
			setInstance(blocComapny);
			initData(blocComapny.getProcInstId());
		}
		if (companyIdReg != null) {
			this.companyId = (Long) companyIdReg;
		}
		if (topBankIdReg != null) {
			this.topBankId = (Long) topBankIdReg;
		}
		if (branchSelectReg != null) {
			this.branchSelectId = (Long) branchSelectReg;
		}
		// 保留其他产品
		if (proRptCreditListReg != null) {
			proRptCreditList.clear();
			List<ProcRptBankCreditBloc> oldList = (List<ProcRptBankCreditBloc>) proRptCreditListReg;
			proRptCreditList.addAll(oldList);
			processBlocRptLayModel = new CustomPageModel<ProcRptBankCreditBloc>(
					proRptCreditList, false);
		}

	}

	/**
	 * 
	 * <p>
	 * Description: 初始数据
	 * </p>
	 * 
	 * @param processInd
	 */
	private void initData(String processInd) {
		// 得到银行授信申请集团数据
		if (getInstance().getProcBankCreditBloc() != null
				&& getInstance().getProcBankCreditBloc().getId() != null) {
			procBankBlocCredit = bankCreditBlocService
					.findBankBlocRegByPBlocId(getInstance()
							.getProcBankCreditBloc().getId());
			// 币别下拉
			currencySelect = dictBean
					.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
			// 初始化公司下拉
			companyNameSelect.add(new SelectItem(procBankBlocCredit
					.getCompany().getId(), procBankBlocCredit.getCompany()
					.getCompanyName()));
			// 初始化银行下拉
			topLevelSelect.add(new SelectItem(procBankBlocCredit.getBank()
					.getId(), procBankBlocCredit.getBank().getBankName()));
			// 初始化分支银行下拉
			branchSelect = creditService
					.findBranchBankSelect(procBankBlocCredit.getBank().getId());
			topBankId = procBankBlocCredit.getBank().getId();
			if (getInstance().getBank() != null) {
				branchSelectId = getInstance().getBank().getId();
			}
			rmbFlowSelect = dictBean
					.getDictByCode(DictConsts.TMS_RATEHOOK_RMB_FLOW_TYPE);
			importFinanceLinkSelect = dictBean
					.getDictByCode(DictConsts.TMS_RATEHOOK_IMPORT_FINANCE_TYPE);
		}

		if (loginService.isCopUser()) {
			memberComanyId = getInstance().getCompany().getId();
			memberComanySelect = companyTmsService.getAllCompanySelect();
		} else {
			memberComanyId = getInstance().getCompany().getId();
			memberComanySelect = companyTmsService.getCompanySelect();
		}
		rateHookSelect = dictBean.getDictByCode(DictConsts.TMS_RATE_HOOK_TYPE);
	}

	/**
	 * 
	 * <p>
	 * Description: 验证本年授信
	 * </p>
	 * 
	 * @return
	 */
	private boolean validateCredit() {
		Double Cd = getInstance().getGuaranteeCd();
		Double Gr = getInstance().getGuaranteeGr();
		Double Mg = getInstance().getGuaranteeMg();
		Double Qm = getInstance().getGuaranteeQm();
		Double Ot = getInstance().getGuaranteeOt();
		Double Credit = getInstance().getCreditLine();
		Double[] data = new Double[5];
		data[0] = Cd;
		data[1] = Gr;
		data[2] = Mg;
		data[3] = Qm;
		data[4] = Ot;
		if ((Cd != null && Cd > Credit) || (Gr != null && Gr > Credit)
				|| (Mg != null && Mg > Credit) || (Qm != null && Qm > Credit)
				|| (Ot != null && Ot > Credit)) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_assureWay_larger"));
			return true;
		}
		double tatol = 0.0;
		for (int i = 0; i < 5; i++) {
			if (data[i] != null) {
				tatol += data[i];
			}
		}
		if (Credit > tatol) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_guaranteeCd_larger"));
			return true;
		}
		return false;
	}

	/**
	 * 
	 * <p>
	 * Description: 授信产品验证
	 * </p>
	 * 
	 * @return
	 */
	private boolean validateCreditProduct() {
		Double liquCred = getInstance().getLiquCred();
		log.info("集团流程资金：" + liquCred);
		Double bankAcp = getInstance().getBankAcpt();
		Double importCredit = getInstance().getImportCredit();
		Double importFinance = getInstance().getImportFinance();
		Double exportFinance = getInstance().getExportFinance();
		Double dollarFlowFinance = getInstance().getDollarFlowFinance();
		Double domesticCred = getInstance().getDomesticCred();
		Double bussTicket = getInstance().getBussTicket();
		// 远期交易额度
		Double forwTrade = getInstance().getForwTrade();
		// 每个品种额度必填
		if (null == liquCred || null == bankAcp || null == importCredit
				|| null == importFinance || null == exportFinance
				|| null == dollarFlowFinance || null == domesticCred
				|| null == bussTicket || null == forwTrade) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_product_notNull_all"));
			return true;
		}
		// 流程资金贷款
		if ((!MathUtil.isZeroOrNull(liquCred))
				&& (MathUtil.isZeroOrNull(liquCred)
						|| null == getInstance().getLiquCredAp() || null == getInstance()
						.getLiquCredRa())) {
			if (!"1".equals(getInstance().getLiquCredRa())) {
				MessageUtils.addErrorMessage("bankCreditMsg", MessageUtils
						.getMessage("errMsg_liquidityPayment_notAll"));
				return true;
			}
		}
		// 银行承兑汇票
		if ((!MathUtil.isZeroOrNull(bankAcp))
				&& (MathUtil.isZeroOrNull(bankAcp)
						|| null == getInstance().getBankAcptEf()
						|| null == getInstance().getBankAcptFe() || null == getInstance()
						.getBankAcptGp())) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_bankAcpt_notAll"));
			return true;
		}
		// 进口信用
		if ((!MathUtil.isZeroOrNull(importCredit))
				&& (null == getInstance().getImportCreditFe()
						|| null == getInstance().getImportCreditGp() || MathUtil
							.isZeroOrNull(importCredit))) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_importCred_notAll"));
			return true;
		}
		// 进口押汇
		if ((!MathUtil.isZeroOrNull(importFinance))
				&& (MathUtil.isZeroOrNull(importFinance)
						|| null == getInstance().getImportFinanceLink() || null == getInstance()
						.getImportFinancePonit())) {
			if (!"3".equals(getInstance().getImportFinanceLink())) {
				MessageUtils.addErrorMessage("bankCreditMsg",
						MessageUtils.getMessage("errMsg_importFinance_notAll"));
				return true;
			}
		}
		// 出口押汇
		if ((!MathUtil.isZeroOrNull(exportFinance))
				&& (MathUtil.isZeroOrNull(exportFinance)
						|| null == getInstance().getExportFinanceLink() || null == getInstance()
						.getExportFinancePonit())) {
			if (!"3".equals(getInstance().getExportFinanceLink())) {
				MessageUtils.addErrorMessage("bankCreditMsg",
						MessageUtils.getMessage("errMsg_exportFinance_notAll"));
				return true;
			}
		}
		// 美元流代
		if ((!MathUtil.isZeroOrNull(dollarFlowFinance))
				&& (MathUtil.isZeroOrNull(dollarFlowFinance)
						|| null == getInstance().getDollarFlowLink() || null == getInstance()
						.getDollarFlowPoint())) {
			if (!"3".equals(getInstance().getDollarFlowLink())) {
				MessageUtils.addErrorMessage("bankCreditMsg",
						MessageUtils.getMessage("process_dollarFlow_notAll"));
				return true;
			}
		}
		// 国内信用证
		if ((!MathUtil.isZeroOrNull(domesticCred))
				&& (MathUtil.isZeroOrNull(domesticCred)
						|| null == getInstance().getDomesticCredDf()
						|| null == getInstance().getDomesticCredFe() || null == getInstance()
						.getDomesticCredGp())) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_nationCredLetter_notAll"));

			return true;
		}
		// 商票保贴
		if ((!MathUtil.isZeroOrNull(bussTicket))
				&& (MathUtil.isZeroOrNull(bussTicket)
						|| null == getInstance().getBussTicketDc()
						|| null == getInstance().getBussTicketFe() || null == getInstance()
						.getBussTicketGp())) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_bussTicket_notAll"));
			return true;
		}
		if ((!MathUtil.isZeroOrNull(forwTrade))
				&& (MathUtil.isZeroOrNull(forwTrade) || null == getInstance()
						.getForwTradeCr())) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_forwTrade_notAll"));
			return true;
		}

		return false;
	}

	/**
	 * 显示流程步骤详细（不显示“流程终止”）
	 */
	public void displayProcessDetial() {
		String workclassNumber = JSFUtils.getParamValue("procInstId");
		String workObjNum = String.valueOf(workclassNumber);
		try {
			List<Object> filterValue = Lists.newArrayList();
			Object[] substitutionVars = {};
			int stepEnventType = ProcessDefineUtil.PROCESS_EVENTTYPE_MAP
					.get(ProcessDefineUtil.EventTypeEnum.StepEnd);
			String filter = "F_EventType = :EventType ";
			filterValue.add(stepEnventType);
			if (workObjNum != null) {
				filter = filter.concat(" and F_WobNum = :wobNum");
				filterValue.add(new VWWorkObjectNumber(workObjNum));
			}
			substitutionVars = filterValue.toArray();
			processDetailList = this.processWaitService.findProcessDetialList(
					filter, substitutionVars);
		} catch (Exception e) {
			log.error("displayProcessDetial方法 显示流程步骤详细 出现异常：", e);
		}
	}

	/**
	 * 查询流程详细步骤
	 */
	private void searchProcessDetail() {
		log.info("查询流程详细步骤");
		log.info("procInstId:" + getProcInstId());

		if ("true".equals(isPatch)) {
			processDetailList = patchMainService
					.getProcessDetailFor411(procInstId);
		} else {
			processDetailList = peManager.getProcessDetail(procInstId);
		}
	}

	// *******************页面一系列的验证***************************
	// 本次申请额度不可为空的验证
	public void ifCreditLineCurrent() {
		log.info("111111111111");
		Double Credit = getInstance().getCreditLine();
		log.info(Credit);
		if (Credit == null
				&& "".equals(getInstance().getCreditLine().toString().trim())) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
	}

	// 流动资金贷款额度的验证
	public void ifLiquAmountCred(Double am) {
		// 本年申请额度
		Double Credit = getInstance().getCreditLine();
		log.info("本年申请额度:" + Credit);
		try {
			if (getInstance().getCreditLine() == null
					&& "".equals(getInstance().getCreditLine().toString()
							.trim())) {
				log.info("222222222222222");
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		log.info("流动资金贷款额度:" + am);
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_liquidityPayment_larger"));
			return;
		}
	}

	// 银行承兑汇票额度的验证
	public void ifBankAcpt(Double am) {
		// 本年申请额度
		Double Credit = getInstance().getCreditLine();
		log.info("本年申请额度:" + Credit);
		try {
			if (getInstance().getCreditLine() == null
					&& "".equals(getInstance().getCreditLine().toString()
							.trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_bankAcpt_larger"));
			return;
		}
	}

	// 进口信用证额度的验证
	public void ifImportCredit(Double am) {
		// 本年申请额度
		Double Credit = getInstance().getCreditLine();
		try {
			if (getInstance().getCreditLine() == null
					&& "".equals(getInstance().getCreditLine().toString()
							.trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_importCred_larger"));
			return;
		}
	}

	// 进口押汇额度的验证
	public void ifImportFinance(Double am) {
		// 本年申请额度
		Double Credit = getInstance().getCreditLine();
		try {
			if (getInstance().getCreditLine() == null
					&& "".equals(getInstance().getCreditLine().toString()
							.trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_importFinance_larger"));
			return;
		}
	}

	// 出口押汇额度的验证
	public void ifExportFinance(Double am) {
		// 本年申请额度
		Double Credit = getInstance().getCreditLine();
		try {
			if (getInstance().getCreditLine() == null
					&& "".equals(getInstance().getCreditLine().toString()
							.trim())) {
				log.info("222222222222222");
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_exportFinance_larger"));
			return;
		}
	}

	// 美元流袋额度的验证
	public void ifDollarFlowFinance(Double am) {
		// 本年申请额度
		Double Credit = getInstance().getCreditLine();
		try {
			if (getInstance().getCreditLine() == null
					&& "".equals(getInstance().getCreditLine().toString()
							.trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_dollarFlow_larger"));
			return;
		}
	}

	// 国内信用证额度的验证
	public void ifomesticCred(Double am) {
		// 本年申请额度
		Double Credit = getInstance().getCreditLine();
		try {
			if (getInstance().getCreditLine() == null
					&& "".equals(getInstance().getCreditLine().toString()
							.trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_nationCredLetter_larger"));
			return;
		}
	}

	// 商票保贴额度的验证
	public void ifBussTicket(Double am) {
		// 本年申请额度
		Double Credit = getInstance().getCreditLine();
		try {
			if (getInstance().getCreditLine() == null
					&& "".equals(getInstance().getCreditLine().toString()
							.trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		log.info("流动资金贷款额度:" + am);
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_bussTicket_larger"));
			return;
		}
	}

	// 远期交易额度的验证
	public void ifForwTrade(Double am) {
		// 本年申请额度
		Double Credit = getInstance().getCreditLine();
		try {
			if (getInstance().getCreditLine() == null
					&& "".equals(getInstance().getCreditLine().toString()
							.trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		log.info("流动资金贷款额度:" + am);
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_forwTrade_larger"));
			return;
		}
	}

	// *************************setter、getter方法********************************
	public List<ProcessDetailVo> getProcessDetailList() {
		return processDetailList;
	}

	public void setProcessDetailList(List<ProcessDetailVo> processDetailList) {
		this.processDetailList = processDetailList;
	}

	public String getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public List<SelectItem> getCompanyNameSelect() {
		return companyNameSelect;
	}

	public void setCompanyNameSelect(List<SelectItem> companyNameSelect) {
		this.companyNameSelect = companyNameSelect;
	}

	public Long getMemberComanyId() {
		return memberComanyId;
	}

	public void setMemberComanyId(Long memberComanyId) {
		this.memberComanyId = memberComanyId;
	}

	public List<SelectItem> getMemberComanySelect() {
		return memberComanySelect;
	}

	public void setMemberComanySelect(List<SelectItem> memberComanySelect) {
		this.memberComanySelect = memberComanySelect;
	}

	public List<SelectItem> getCurrencySelect() {
		return currencySelect;
	}

	public void setCurrencySelect(List<SelectItem> currencySelect) {
		this.currencySelect = currencySelect;
	}

	public List<SelectItem> getTopLevelSelect() {
		return topLevelSelect;
	}

	public void setTopLevelSelect(List<SelectItem> topLevelSelect) {
		this.topLevelSelect = topLevelSelect;
	}

	public Long getBranchSelectId() {
		return branchSelectId;
	}

	public void setBranchSelectId(Long branchSelectId) {
		this.branchSelectId = branchSelectId;
	}

	public List<SelectItem> getBranchSelect() {
		return branchSelect;
	}

	public void setBranchSelect(List<SelectItem> branchSelect) {
		this.branchSelect = branchSelect;
	}

	public ProcBankCreditBloc getProcBankBlocCredit() {
		return procBankBlocCredit;
	}

	public void setProcBankBlocCredit(ProcBankCreditBloc procBankBlocCredit) {
		this.procBankBlocCredit = procBankBlocCredit;
	}

	public List<ProcRptBankCreditBloc> getProRptCreditList() {
		return proRptCreditList;
	}

	public void setProRptCreditList(List<ProcRptBankCreditBloc> proRptCreditList) {
		this.proRptCreditList = proRptCreditList;
	}

	public LazyDataModel<ProcRptBankCreditBloc> getProcessBlocRptLayModel() {
		return processBlocRptLayModel;
	}

	public void setProcessBlocRptLayModel(
			LazyDataModel<ProcRptBankCreditBloc> processBlocRptLayModel) {
		this.processBlocRptLayModel = processBlocRptLayModel;
	}

	public ProcRptBankCreditBloc getBlocCreditRpt() {
		return blocCreditRpt;
	}

	public void setBlocCreditRpt(ProcRptBankCreditBloc blocCreditRpt) {
		this.blocCreditRpt = blocCreditRpt;
	}

	public Long getTopBankId() {
		return topBankId;
	}

	public void setTopBankId(Long topBankId) {
		this.topBankId = topBankId;
	}

	public List<SelectItem> getRateHookSelect() {
		return rateHookSelect;
	}

	public void setRateHookSelect(List<SelectItem> rateHookSelect) {
		this.rateHookSelect = rateHookSelect;
	}

	public ProcRptBankCreditBloc getBlocCreditRptEdit() {
		return blocCreditRptEdit;
	}

	public void setBlocCreditRptEdit(ProcRptBankCreditBloc blocCreditRptEdit) {
		this.blocCreditRptEdit = blocCreditRptEdit;
	}

	public ProcRptBankCreditBloc getBlocCreditRptCopy() {
		return blocCreditRptCopy;
	}

	public void setBlocCreditRptCopy(ProcRptBankCreditBloc blocCreditRptCopy) {
		this.blocCreditRptCopy = blocCreditRptCopy;
	}

	public ProcRptBankCreditBloc getBlocCreditRptEditVo() {
		return blocCreditRptEditVo;
	}

	public void setBlocCreditRptEditVo(ProcRptBankCreditBloc blocCreditRptEditVo) {
		this.blocCreditRptEditVo = blocCreditRptEditVo;
	}

	public String getPrtBlocCreditNameEdit() {
		return prtBlocCreditNameEdit;
	}

	public void setPrtBlocCreditNameEdit(String prtBlocCreditNameEdit) {
		this.prtBlocCreditNameEdit = prtBlocCreditNameEdit;
	}

	public boolean isDoNext() {
		return doNext;
	}

	public void setDoNext(boolean doNext) {
		this.doNext = doNext;
	}

	public Boolean getFloat2Disable() {
		return float2Disable;
	}

	public void setFloat2Disable(Boolean float2Disable) {
		this.float2Disable = float2Disable;
	}

	public Boolean getFloat1Disable() {
		return float1Disable;
	}

	public void setFloat1Disable(Boolean float1Disable) {
		this.float1Disable = float1Disable;
	}

	public List<SelectItem> getImportFinanceLinkSelect() {
		return importFinanceLinkSelect;
	}

	public void setImportFinanceLinkSelect(
			List<SelectItem> importFinanceLinkSelect) {
		this.importFinanceLinkSelect = importFinanceLinkSelect;
	}

	public List<SelectItem> getRmbFlowSelect() {
		return rmbFlowSelect;
	}

	public void setRmbFlowSelect(List<SelectItem> rmbFlowSelect) {
		this.rmbFlowSelect = rmbFlowSelect;
	}

	public Boolean getFloatFlagDisable() {
		return floatFlagDisable;
	}

	public void setFloatFlagDisable(Boolean floatFlagDisable) {
		this.floatFlagDisable = floatFlagDisable;
	}

	public Boolean getDollarFlowPointDisable() {
		return dollarFlowPointDisable;
	}

	public void setDollarFlowPointDisable(Boolean dollarFlowPointDisable) {
		this.dollarFlowPointDisable = dollarFlowPointDisable;
	}

	public Boolean getImportFinancePonitDisable() {
		return importFinancePonitDisable;
	}

	public void setImportFinancePonitDisable(Boolean importFinancePonitDisable) {
		this.importFinancePonitDisable = importFinancePonitDisable;
	}

	public Boolean getExportFinancePonitDisable() {
		return exportFinancePonitDisable;
	}

	public void setExportFinancePonitDisable(Boolean exportFinancePonitDisable) {
		this.exportFinancePonitDisable = exportFinancePonitDisable;
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
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
