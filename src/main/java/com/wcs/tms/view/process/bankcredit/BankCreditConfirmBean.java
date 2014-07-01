package com.wcs.tms.view.process.bankcredit;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;

import com.google.common.collect.Lists;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.MathUtil;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.Credit;
import com.wcs.tms.model.CreditO;
import com.wcs.tms.model.ProcBankCredit;
import com.wcs.tms.model.ProcBankCreditConfirm;
import com.wcs.tms.model.ProcRptCredit;
import com.wcs.tms.model.ProcRptCreditConfirm;
import com.wcs.tms.service.process.bankcredit.BankCreditConfirmService;
import com.wcs.tms.service.process.bankcredit.BankCreditService;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessUtilService;
import com.wcs.tms.service.process.common.ProcessWaitAcceptService;
import com.wcs.tms.service.process.common.patch.service.PatchMainService;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.service.system.company.CreditService;
import com.wcs.tms.util.CustomPageModel;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.FileUpload;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

import filenet.vw.api.VWWorkObjectNumber;

/**
 * 
 * 
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description: 银行授信申请审批确认
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
public class BankCreditConfirmBean extends FileUpload<ProcBankCreditConfirm> {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory
			.getLog(BankCreditConfirmBean.class);

	/** 人民币流贷下拉 */
	private List<SelectItem> rmbFlowSelect = new ArrayList<SelectItem>();
	/** 进口押汇下拉 */
	private List<SelectItem> importFinanceLinkSelect = new ArrayList<SelectItem>();
	private Boolean float1Disable = false;
	private Boolean float2Disable = false;
	private Boolean floatFlagDisable = false;
	private Boolean dollarFlowPointDisable = false;
	private Boolean importFinancePonitDisable = false;
	private Boolean exportFinancePonitDisable = false;
	/** 资金币种下拉 */
	private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
	/** 银行总行Id */
	private Long topBankId;
	/** 一级机构银行下拉 */
	private List<SelectItem> topLevelSelect = new ArrayList<SelectItem>();
	/** 利率挂钩下拉 */
	private List<SelectItem> rateHookSelect = new ArrayList<SelectItem>();
	/** 选择支行Id */
	private Long branchSelectId;
	/** 支行下拉 */
	private List<SelectItem> branchSelect = new ArrayList<SelectItem>();
	private List<SelectItem> requestbranchSelect = new ArrayList<SelectItem>();
	/** 银行授信申请确认其他产品实体 */
	private ProcRptCreditConfirm rptCreditConfirm = new ProcRptCreditConfirm();
	/** 授信编辑时 */
	private ProcRptCreditConfirm rptCreditConfirmEdit = new ProcRptCreditConfirm();
	/** 保存编辑前时对象 */
	private ProcRptCreditConfirm rptCreditConfirmCopy = new ProcRptCreditConfirm();
	private ProcRptCreditConfirm rptCreditConfirmVo = new ProcRptCreditConfirm();
	/** 银行授信申请确认其他产品集合 */
	private List<ProcRptCreditConfirm> proRptCreditConfirmList = Lists
			.newArrayList();
	/** 银行授信申请其他产品确认分页模型 */
	private LazyDataModel<ProcRptCreditConfirm> propConfirmLayModel;
	/** 银行授信申请其他产品 */
	private List<ProcRptCredit> proRptCreditList = Lists.newArrayList();
	/** 银行授信申请其他产品分页模型 */
	private LazyDataModel<ProcRptCredit> propLayModel;
	/** 用于查看审批详细步骤，附件时用的全局参数 */
	private String workObjNum;
	/** 审批步骤名称 */
	private String stepName;
	/** 银行授信申请对象 */
	private ProcBankCredit procBankCredit;
	/** 流程详细List数据 */
	private List<ProcessDetailVo> processDetailList = new ArrayList<ProcessDetailVo>();
	/** 银行授信申请信息详细页面 */
	private static final String BANKCREDIT_VIEW_PAGE = "/faces/process/bankCredit/inc-bankCreditView.xhtml";
	// 流程实例ID
	private String procInstId;
	// 是否处理下一个任务9.5
	private boolean doNext = true;
	// 当前所处理任务在任务列表中的位置
	private Integer currentIndex;
	// 当前处理任务类型（已接受和待接收）
	private String currentTaskType;
	// 12.14字段长度验证
	private String testLength;
	// 是否验证credit line code字段
	private boolean ifSure = true;
	// 是否是运维人员
	private boolean ifYunwei = false;
	// 是否是申请人确认
	private boolean ifRequest = false;
	@Inject
	private BankService bacnkSerice;
	@Inject
	private CommonBean dictBean;
	@Inject
	private CreditService creditService;
	@Inject
	private BankCreditService bankCreditService;
	@Inject
	private BankCreditConfirmService bankCreditConfirmService;
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

	public BankCreditConfirmBean() {
		// list已提价流程界面,input已处理流程界面
		setupPage("/faces/process/common/processSubed-list.xhtml",
				"/faces/process/common/processDealed-list.xhtml", null);
	}

	@PostConstruct
	public void init() {
		String audit = (String) JSFUtils.getRequest().getAttribute("op");
		if (StringUtils.isEmpty(audit)) {
			audit = JSFUtils.getRequestParam("op");
		}
		String workclassNumber = (String) JSFUtils.getRequest().getAttribute(
				"procInstId");
		setProcInstId(workclassNumber);
		if (StringUtils.isEmpty(workclassNumber)) {
			workclassNumber = JSFUtils.getRequestParam("procInstId");
			setProcInstId(workclassNumber);
		}
		isPatch = JSFUtils.getParamValue("isPatch");
		// 是否是审批和查看
		if (audit != null && workclassNumber != null) {
			String flag = audit;
			String wcnum = String.valueOf(workclassNumber);
			ProcBankCreditConfirm bankConfirm = this.bankCreditConfirmService
					.findBankCreditConfirm(wcnum);
			// 设置总行
			topBankId = bankConfirm.getBank().getTopBankId();
			branchSelect.add(new SelectItem(bankConfirm.getBank().getId(),
					bankConfirm.getBank().getBankName()));
			// 根据总行设置支行
			bankChange();
			// 设置支行选中
			branchSelectId = bankConfirm.getBank().getId();
			setInstance(bankConfirm);
			// 得到确认单下的其他授信产品
			proRptCreditConfirmList = bankCreditConfirmService
					.findProRptConmfirm(wcnum);
			propConfirmLayModel = new CustomPageModel<ProcRptCreditConfirm>(
					proRptCreditConfirmList, false);
			// 查询数申请信息
			procBankCredit = bankCreditService.findProcBankCd(wcnum);
			requestbranchSelect.add(new SelectItem(procBankCredit.getBank()
					.getId(), procBankCredit.getBank().getBankName()));
			// 银行授信申请的其他产品信息
			proRptCreditList = bankCreditService.findProcRptCreditList(wcnum);
			propLayModel = new CustomPageModel<ProcRptCredit>(proRptCreditList,
					false);
			this.workObjNum = wcnum;
			if ("approve".equals(flag)) {
				// add on 2013-5-17
				currentIndex = Integer.parseInt(JSFUtils
						.getParamValue("currentIndex"));
				currentTaskType = (String) JSFUtils
						.getParamValue("currentTaskType");

				stepName = JSFUtils.getRequest().getAttribute("stepName")
						.toString();
			}
			// 查询流程附件
			displayDetailAttach(workclassNumber);
			searchProcessDetail();
			if ("WCS-TMS运维人员维护".equals((String) JSFUtils
					.getParamValue("stepName"))) {
				setIfYunwei(true);
			}
			if ("申请人确认".equals((String) JSFUtils.getParamValue("stepName"))) {
				setIfRequest(true);
			}
		} else {
			keepUserData();
		}
		initdata();
	}

	/**
	 * “出口押汇”可编辑控制
	 */
	public void exportFinanceLinkChange() {
		// 3、再议
		if ("3".equals(getInstance().getExportFinanceLink())) {
			exportFinancePonitDisable = true;
		} else {
			exportFinancePonitDisable = false;
		}
	}

	/**
	 * “进口押汇”可编辑控制
	 */
	public void importFinanceLinkChange() {
		// 3、再议
		if ("3".equals(getInstance().getImportFinanceLink())) {
			importFinancePonitDisable = true;
		} else {
			importFinancePonitDisable = false;
		}
	}

	/**
	 * “美元流贷”可编辑控制
	 */
	public void dollarFlowLinkChange() {
		// 3、再议 4、固定
		if ("3".equals(getInstance().getDollarFlowLink())) {
			dollarFlowPointDisable = true;
		} else {
			dollarFlowPointDisable = false;
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
	 * 初始化下拉
	 */
	private void initdata() {
		currencySelect = dictBean
				.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
		topLevelSelect = bacnkSerice.getTopLevelSelect();
		rateHookSelect = dictBean.getDictByCode(DictConsts.TMS_RATE_HOOK_TYPE);
		rmbFlowSelect = dictBean
				.getDictByCode(DictConsts.TMS_RATEHOOK_RMB_FLOW_TYPE);
		importFinanceLinkSelect = dictBean
				.getDictByCode(DictConsts.TMS_RATEHOOK_IMPORT_FINANCE_TYPE);
	}

	/**
	 * 
	 * <p>
	 * Description: 总行联动支行
	 * </p>
	 */
	public void bankChange() {
		branchSelect.clear();
		branchSelect = creditService.findBranchBankSelect(topBankId);
	}

	/**
	 * 
	 * <p>
	 * Description: 银行授信申请人确认
	 * </p>
	 * 
	 * @return
	 */
	public String requestConfirmBankCredit() {
		try {
			String workClassNum = getInstance().getProcInstId();
			String upload = ProcessXmlUtil.findStepProperty("id",
					"BankCreditId", stepName, "upload");
			if (upload != null && "true".equals(upload)) {
				// 保存流程附件
				this.saveProcessFile(workClassNum);
				log.info("保存流程附件！！！");
			}
			log.info("stepName:" + stepName);
			if (stepName != null) {
				// 防止用户直接输地址
				entityService.update(getInstance());
			}
			bankCreditConfirmService.vwReplayBankRegister("申请确认", workClassNum);
			if (!proRptCreditConfirmList.isEmpty()) {
				bankCreditConfirmService.batchAddRptConfirm(workClassNum,
						proRptCreditConfirmList);
			}
			// 是否需要向主数据公司授信写入数据
			String confirm = ProcessXmlUtil.findStepProperty("id",
					"BankCreditId", "申请人确认", "confirm");
			if (confirm != null && "true".equals(confirm)) {
				Credit credit = exchangeData(getInstance(), procBankCredit);
				Credit presCredit = entityService.create(credit);
				// 产生主数据公司其他授信产品
				List<ProcRptCreditConfirm> dataList = bankCreditConfirmService
						.findProRptConmfirm(workClassNum);
				for (ProcRptCreditConfirm rpt : dataList) {
					CreditO creditO = new CreditO();
					creditO.setCredit(presCredit);
					creditO.setDefunctInd("N");
					creditO.setOtherName(rpt.getCdProName());
					creditO.setOtherLimit(rpt.getCdProLimit());
					entityService.create(creditO);
				}
			}
			MessageUtils.addSuccessMessage(
					"doneMsg",
					stepName
							+ MessageUtils.getMessage(
									"process_bankApprove_success",
									processUtilMapService
											.getTmsIdByFnId(getInstance()
													.getProcInstId())));
			return processUtilService.getNextPage(getInputPage(), doNext,
					currentIndex, currentTaskType);
		} catch (Exception e) {
			log.error("requestConfirmBankCredit方法 银行授信申请人确认 出现异常：", e);
			MessageUtils.addErrorMessage("msg", "申请人确认失败");
			return null;
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 添加其他授信产品确认明细
	 * </p>
	 */
	public void addBankRptConfirm() {

		if (rptCreditConfirm.getCdProLimit() == null
				|| "".equals(rptCreditConfirm.getCdProName())) {
			MessageUtils.addErrorMessage("msg",
					MessageUtils.getMessage("msg_required_otherProInfo"));
			return;
		}
		if (rptCreditConfirm.getCdProLimit() != null
				&& rptCreditConfirm.getCdProLimit() > getInstance()
						.getCreditLimit()) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_otherPro_larger"));
			return;
		}
		boolean rptFlag = false;
		if (!proRptCreditConfirmList.isEmpty()) {
			for (ProcRptCreditConfirm pcredit : proRptCreditConfirmList) {
				if (pcredit.getProcInstId() != null
						&& pcredit.getProcInstId().equals(workObjNum)) {
					if (rptCreditConfirm.getCdProName().equals(
							pcredit.getCdProName())) {
						rptFlag = true;
						break;
					}
				} else {
					if (rptCreditConfirm.getCdProName().equals(
							pcredit.getCdProName())) {
						rptFlag = true;
						break;
					}
				}
			}
			if (rptFlag) {
				MessageUtils.addErrorMessage("msg",
						MessageUtils.getMessage("errMsg_existOtherPro"));
				return;
			}
		}
		proRptCreditConfirmList.add(rptCreditConfirm);
		propConfirmLayModel = new CustomPageModel<ProcRptCreditConfirm>(
				proRptCreditConfirmList, false);
		setRptCreditConfirm(new ProcRptCreditConfirm());
	}

	/**
	 * 
	 * <p>
	 * Description: 删除其他产品
	 * </p>
	 */
	public void deleteBanRptCofirm() {
		proRptCreditConfirmList.remove(rptCreditConfirmEdit);
		MessageUtils.addSuccessMessage("msg",
				MessageUtils.getMessage("otherPro_delete_success"));
	}

	/**
	 * 
	 * <p>
	 * Description: 编辑之前初始化
	 * </p>
	 */
	public void befforeBanRptConfirm() {
		// 将编辑前的对象属性值赋值出来,因为编辑时将对象赋值到后台，界面修改到编辑用的同一对象，后台验证失败了却更改了值
		try {
			PropertyUtils.copyProperties(rptCreditConfirmCopy,
					rptCreditConfirmEdit);
			rptCreditConfirmVo = rptCreditConfirmEdit;
			rptCreditConfirmEdit = new ProcRptCreditConfirm();
			PropertyUtils.copyProperties(rptCreditConfirmEdit,
					rptCreditConfirmCopy);
		} catch (IllegalAccessException e) {
			log.error("befforeBanRptConfirm方法 编辑之前初始化 出现异常：", e);
		} catch (InvocationTargetException e) {
			log.error("befforeBanRptConfirm方法 编辑之前初始化 出现异常：", e);
		} catch (NoSuchMethodException e) {
			log.error("befforeBanRptConfirm方法 编辑之前初始化 出现异常：", e);
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 编辑其他产品
	 * </p>
	 */
	public void editBanRptConfirm() {
		if (rptCreditConfirmEdit.getCdProLimit() != null
				&& rptCreditConfirmEdit.getCdProLimit() > getInstance()
						.getCreditLimit()) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_otherPro_larger"));
			return;
		}

		boolean rptFlag = false;
		if (!proRptCreditConfirmList.isEmpty()) {
			for (ProcRptCreditConfirm pcredit : proRptCreditConfirmList) {
				if (pcredit.getProcInstId() != null) {
					if (!rptCreditConfirmCopy.getCdProName().equals(
							rptCreditConfirmEdit.getCdProName())
							&& rptCreditConfirmEdit.getCdProName().equals(
									pcredit.getCdProName())
							&& pcredit.getProcInstId().equals(workObjNum)) {
						rptFlag = true;
						break;
					}
				} else {
					if (!rptCreditConfirmCopy.getCdProName().equals(
							rptCreditConfirmEdit.getCdProName())
							&& rptCreditConfirmEdit.getCdProName().equals(
									pcredit.getCdProName())) {
						rptFlag = true;
						break;
					}
				}
			}
			if (rptFlag) {
				MessageUtils.addErrorMessage("msg",
						MessageUtils.getMessage("errMsg_existOtherPro"));
				try {
					PropertyUtils.copyProperties(rptCreditConfirmEdit,
							rptCreditConfirmCopy);
				} catch (IllegalAccessException e) {
					log.error("editBanRptConfirm方法 编辑其他产品 出现异常：", e);
				} catch (InvocationTargetException e) {
					log.error("editBanRptConfirm方法 编辑其他产品 出现异常：", e);
				} catch (NoSuchMethodException e) {
					log.error("editBanRptConfirm方法 编辑其他产品 出现异常：", e);
				}
				return;
			}
		}
		if (proRptCreditConfirmList.contains(rptCreditConfirmVo)) {
			proRptCreditConfirmList.remove(rptCreditConfirmVo);
		}
		proRptCreditConfirmList.add(rptCreditConfirmEdit);
		propConfirmLayModel = new CustomPageModel<ProcRptCreditConfirm>(
				proRptCreditConfirmList, false);
	}

	/**
	 * 
	 * <p>
	 * Description: 查看授信申请详细
	 * </p>
	 * 
	 * @return
	 */
	public String goBankCreditDetail() {
		JSFUtils.getRequest().setAttribute("procInstId",
				getInstance().getProcInstId());
		JSFUtils.getRequest().setAttribute("op", "view");
		return BANKCREDIT_VIEW_PAGE;
	}

	/**
	 * 查询流程详细步骤
	 */
	private void searchProcessDetail() {
		log.info("查询流程详细步骤");
		log.info("procInstId:" + getProcInstId());

		if ("true".equals(isPatch)) {
			processDetailList = patchMainService
					.getProcessDetailFor411(getProcInstId());
		} else {
			processDetailList = peManager.getProcessDetail(getProcInstId());
		}
	}

	/**
	 * <p>
	 * Description: 显示流程步骤详细
	 * </p>
	 */
	public void displayProcessDetial() {
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
	 * 
	 * <p>
	 * Description: 本年申請額度賦值，用于其他产品判断额度
	 * </p>
	 */
	public void creditLimitChange() {

	}

	/**
	 * 
	 * <p>
	 * Description: 公司授信数据
	 * </p>
	 * 
	 * @param credit
	 * @param bankConfirm
	 * @param procBankCredit
	 * @return
	 */
	private Credit exchangeData(ProcBankCreditConfirm bankConfirm,
			ProcBankCredit procBankCredit) {
		Credit credit = new Credit();
		credit.setBank(bankConfirm.getBank());
		credit.setCompany(bankConfirm.getCompany());
		credit.setCreditStart(bankConfirm.getCreditStart());
		credit.setCreditEnd(bankConfirm.getCreditEnd());
		credit.setProcInstId(bankConfirm.getProcInstId());
		credit.setCreditLine(bankConfirm.getCreditLimit());
		credit.setCreditLineCu(bankConfirm.getCreditLineCu());

		credit.setGuaranteeCd(bankConfirm.getGuaranteeCd());
		credit.setGuaranteeGr(bankConfirm.getGuaranteeGr());
		credit.setGuaranteeMg(bankConfirm.getGuaranteeMg());
		credit.setGuaranteeOt(bankConfirm.getGuaranteeOt());
		credit.setGuaranteeQm(bankConfirm.getGuaranteeQm());
		credit.setReason(procBankCredit.getCooperationReason());

		credit.setLiquCred(bankConfirm.getLiquCred());
		credit.setLiquCredAp(bankConfirm.getLiquCredAp());
		credit.setLiquCredPonit(bankConfirm.getLiquCredPonit());
		credit.setLiquCredRa(bankConfirm.getLiquCredRa());

		credit.setBankAcpt(bankConfirm.getBankAcpt());
		credit.setBankAcptEf(bankConfirm.getBankAcptEf());
		credit.setBankAcptFe(bankConfirm.getBankAcptFe());
		credit.setBankAcptGp(bankConfirm.getBankAcptGp());

		credit.setImportCredit(bankConfirm.getImportCredit());
		credit.setImportCreditFe(bankConfirm.getImportCreditFe());
		credit.setImportCreditGp(bankConfirm.getImportCreditGp());
		credit.setImportFinance(bankConfirm.getImportFinance());

		credit.setImportFinanceLink(bankConfirm.getImportFinanceLink());
		credit.setImportFinancePoint(bankConfirm.getImportFinancePonit());

		credit.setExportFinance(bankConfirm.getExportFinance());
		credit.setExportFinanceLink(bankConfirm.getExportFinanceLink());
		credit.setExportFinancePoint(bankConfirm.getExportFinancePonit());

		credit.setDollarFlowLink(bankConfirm.getDollarFlowLink());
		credit.setDollarFlowPoint(bankConfirm.getDollarFlowPoint());
		credit.setDollarFlowFinance(bankConfirm.getDollarFlowFinance());

		credit.setDomesticCred(bankConfirm.getDomesticCred());
		credit.setDomesticCredDf(bankConfirm.getDomesticCredDf());
		credit.setDomesticCredFe(bankConfirm.getDomesticCredFe());
		credit.setDomesticCredGp(bankConfirm.getDomesticCredGp());

		credit.setBussTicket(bankConfirm.getBussTicket());
		credit.setBussTicketDc(bankConfirm.getBussTicketDc());
		credit.setBussTicketFe(bankConfirm.getBussTicketFe());
		credit.setBussTicketGp(bankConfirm.getBussTicketGp());

		// add by yan on 2013-7-1
		credit.setForwTrade(bankConfirm.getForwTrade());
		credit.setForwTradeCr(bankConfirm.getForwTradeCr());
		credit.setStatus("Y");
		return credit;
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
		Double Credit = getInstance().getCreditLimit();
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
		Double bankAcp = getInstance().getBankAcpt();
		Double importCredit = getInstance().getImportCredit();
		Double importFinance = getInstance().getImportFinance();
		Double exportFinance = getInstance().getExportFinance();
		Double dollarFlowFinance = getInstance().getDollarFlowFinance();
		Double domesticCred = getInstance().getDomesticCred();
		Double bussTicket = getInstance().getBussTicket();
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
				&& (MathUtil.isZeroOrNull(liquCred) || null == getInstance()
						.getLiquCredAp())
				|| null == getInstance().getLiquCredRa()) {
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
				&& (null == getInstance().getImportCreditFe())
				|| null == getInstance().getImportCreditGp()
				|| MathUtil.isZeroOrNull(importCredit)) {
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
						MessageUtils.getMessage("errMsg_dollarFlow_notAll"));
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
		// 远期交易
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
	 * 
	 * <p>
	 * Description: 将用户输入的数据放入Request对象
	 * </p>
	 */
	private void transportData() {
		JSFUtils.getRequest().setAttribute("validateVo2", getInstance());
		Bank bank = this.entityService.find(Bank.class, branchSelectId);
		if (bank != null) {
			// 添加分支银行下拉
			JSFUtils.getRequest().setAttribute("branchSelectId",
					new SelectItem(branchSelectId, bank.getBankName()));
		}
		JSFUtils.getRequest().setAttribute("topBankId", topBankId);
		JSFUtils.getRequest().setAttribute("requestCredit", procBankCredit);
		JSFUtils.getRequest().setAttribute("requestCreditBank",
				requestbranchSelect);

	}

	/**
	 * 
	 * <p>
	 * Description: 记录用户输入的数据
	 * </p>
	 */
	private void keepUserData() {
		// 实体类数据
		Object obj = JSFUtils.getRequest().getAttribute("validateVo2");

		// 一级银行Id
		Object topBankIdReg = JSFUtils.getRequest().getAttribute("topBankId");
		// 分支银行Id
		Object branchSelectReg = JSFUtils.getRequest().getAttribute(
				"branchSelectId");
		// 是否审批
		Object isApproveReg = JSFUtils.getRequest().getAttribute("isApprove");
		// 节点名称
		Object stepNameReg = JSFUtils.getRequest().getAttribute("stepName");
		// 申请其他产品
		Object resutRpt = JSFUtils.getRequest().getAttribute("requestRptList");
		// 确认产品
		Object confirmRpt = JSFUtils.getRequest()
				.getAttribute("confirmRptList");
		// 授信申请对象
		Object rCredit = JSFUtils.getRequest().getAttribute("requestCredit");
		// 授信申请分支银行
		Object rCreditBank = JSFUtils.getRequest().getAttribute(
				"requestCreditBank");
		if (obj != null) {
			setInstance((ProcBankCreditConfirm) obj);
		}
		if (topBankIdReg != null) {
			this.topBankId = (Long) topBankIdReg;
		}
		if (branchSelectReg != null) {
			SelectItem item = (SelectItem) branchSelectReg;
			this.branchSelectId = (Long) item.getValue();
			this.branchSelect.add(item);
		}
		if (resutRpt != null) {
			proRptCreditList = (List<ProcRptCredit>) resutRpt;
			propLayModel = new CustomPageModel<ProcRptCredit>(proRptCreditList,
					false);
		}
		if (confirmRpt != null) {
			proRptCreditConfirmList = (List<ProcRptCreditConfirm>) confirmRpt;
			propConfirmLayModel = new CustomPageModel<ProcRptCreditConfirm>(
					proRptCreditConfirmList, false);
		}
		// 授信申请数据
		if (rCredit != null) {
			procBankCredit = (ProcBankCredit) rCredit;
		}
		// 授信申请银行
		if (rCreditBank != null) {
			requestbranchSelect = (List<SelectItem>) rCreditBank;
		}
		// 是否是审批
		if (isApproveReg != null && ((String) isApproveReg).equals("Y")) {
			// 初始化需要编辑的字段
			if (stepNameReg != null) {
				stepName = stepNameReg.toString();
				workObjNum = getInstance().getProcInstId();
				// 验证失败之后需要将流程实例编号传会实体对象
			}
		}
	}

	/**
	 * 风险管理和运维维护
	 */
	public String doMaintain() {
		try {
			String workClassNum = getInstance().getProcInstId();
			log.info("workClassNum" + workClassNum + ",stepName:" + stepName);
			if (stepName != null) {
				// 防止用户直接输地址
				entityService.update(getInstance());
			}
			String upload = ProcessXmlUtil.findStepProperty("id",
					"BankCreditId", stepName, "upload");
			if (upload != null && "true".equals(upload)) {
				// 保存流程附件
				this.saveProcessFile(workClassNum);
				log.info("保存流程附件！！！");
			}
			// 加入流程备注抬头
			String memoTitle = "";
			memoTitle = ProcessXmlUtil.findStepProperty("id", "BankCreditId",
					stepName, "passMemo");
			log.info("memoTitle" + memoTitle);
			bankCreditConfirmService.vwReplayBankRegister(memoTitle,
					workClassNum);
			MessageUtils.addSuccessMessage(
					"doneMsg",
					stepName
							+ MessageUtils.getMessage(
									"process_bankApprove_success",
									processUtilMapService
											.getTmsIdByFnId(getInstance()
													.getProcInstId())));
			return processUtilService.getNextPage(getInputPage(), doNext,
					currentIndex, currentTaskType);
		} catch (Exception e) {
			log.error("doMaintain方法 风险管理和运维维护 出现异常：", e);
			MessageUtils.addErrorMessage("msg", "申请人确认失败");
			return null;
		}
	}

	/**
	 * 验证Credit Line Code的长度
	 * 
	 * @return testLength
	 */

	public String lengthTest() {
		String msgs = "";
		String code = getInstance().getCreditLineCode();
		log.info("code:" + code.length());
		if (code.length() > 30) {
			msgs = "警告！Credit Line Code里面内容大于30个字符，请确认！";
			setIfSure(false);
		} else if ("风险管理组管理人员确认".equals(stepName) && code.length() > 10
				&& code.length() <= 30) {
			msgs = "Credit Line Code里面内容大于10个字符，请确认！";
			setIfSure(true);
			log.info("msgs:" + msgs);
		} else if ("风险管理组管理人员确认".equals(stepName) && code.length() < 10) {
			msgs = "Credit Line Code里面内容小于10个字符，请确认！";
			setIfSure(true);
			log.info("msgs:" + msgs);
		} else {
			msgs = "是否确认通过？";
			setIfSure(true);
			log.info("msgs:" + msgs);
		}
		testLength = msgs;
		return testLength;
	}

	// -----------------------------------------------Get & Set
	// -------------------------------------------------------//
	public List<SelectItem> getCurrencySelect() {
		return currencySelect;
	}

	public void setCurrencySelect(List<SelectItem> currencySelect) {
		this.currencySelect = currencySelect;
	}

	public Long getTopBankId() {
		return topBankId;
	}

	public void setTopBankId(Long topBankId) {
		this.topBankId = topBankId;
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

	public List<ProcRptCreditConfirm> getProRptCreditConfirmList() {
		return proRptCreditConfirmList;
	}

	public void setProRptCreditConfirmList(
			List<ProcRptCreditConfirm> proRptCreditConfirmList) {
		this.proRptCreditConfirmList = proRptCreditConfirmList;
	}

	public String getWorkObjNum() {
		return workObjNum;
	}

	public void setWorkObjNum(String workObjNum) {
		this.workObjNum = workObjNum;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public List<ProcessDetailVo> getProcessDetailList() {
		return processDetailList;
	}

	public void setProcessDetailList(List<ProcessDetailVo> processDetailList) {
		this.processDetailList = processDetailList;
	}

	public BankService getBacnkSerice() {
		return bacnkSerice;
	}

	public void setBacnkSerice(BankService bacnkSerice) {
		this.bacnkSerice = bacnkSerice;
	}

	public CommonBean getDictBean() {
		return dictBean;
	}

	public void setDictBean(CommonBean dictBean) {
		this.dictBean = dictBean;
	}

	public ProcRptCreditConfirm getRptCreditConfirm() {
		return rptCreditConfirm;
	}

	public void setRptCreditConfirm(ProcRptCreditConfirm rptCreditConfirm) {
		this.rptCreditConfirm = rptCreditConfirm;
	}

	public List<SelectItem> getRateHookSelect() {
		return rateHookSelect;
	}

	public void setRateHookSelect(List<SelectItem> rateHookSelect) {
		this.rateHookSelect = rateHookSelect;
	}

	public ProcRptCreditConfirm getRptCreditConfirmEdit() {
		return rptCreditConfirmEdit;
	}

	public void setRptCreditConfirmEdit(
			ProcRptCreditConfirm rptCreditConfirmEdit) {
		this.rptCreditConfirmEdit = rptCreditConfirmEdit;
	}

	public ProcRptCreditConfirm getRptCreditConfirmCopy() {
		return rptCreditConfirmCopy;
	}

	public void setRptCreditConfirmCopy(
			ProcRptCreditConfirm rptCreditConfirmCopy) {
		this.rptCreditConfirmCopy = rptCreditConfirmCopy;
	}

	public ProcRptCreditConfirm getRptCreditConfirmVo() {
		return rptCreditConfirmVo;
	}

	public void setRptCreditConfirmVo(ProcRptCreditConfirm rptCreditConfirmVo) {
		this.rptCreditConfirmVo = rptCreditConfirmVo;
	}

	public LazyDataModel<ProcRptCreditConfirm> getPropConfirmLayModel() {
		return propConfirmLayModel;
	}

	public void setPropConfirmLayModel(
			LazyDataModel<ProcRptCreditConfirm> propConfirmLayModel) {
		this.propConfirmLayModel = propConfirmLayModel;
	}

	public List<ProcRptCredit> getProRptCreditList() {
		return proRptCreditList;
	}

	public void setProRptCreditList(List<ProcRptCredit> proRptCreditList) {
		this.proRptCreditList = proRptCreditList;
	}

	public LazyDataModel<ProcRptCredit> getPropLayModel() {
		return propLayModel;
	}

	public void setPropLayModel(LazyDataModel<ProcRptCredit> propLayModel) {
		this.propLayModel = propLayModel;
	}

	public ProcBankCredit getProcBankCredit() {
		return procBankCredit;
	}

	public void setProcBankCredit(ProcBankCredit procBankCredit) {
		this.procBankCredit = procBankCredit;
	}

	public List<SelectItem> getRequestbranchSelect() {
		return requestbranchSelect;
	}

	public void setRequestbranchSelect(List<SelectItem> requestbranchSelect) {
		this.requestbranchSelect = requestbranchSelect;
	}

	public boolean isDoNext() {
		return doNext;
	}

	public void setDoNext(boolean doNext) {
		this.doNext = doNext;
	}

	public List<SelectItem> getRmbFlowSelect() {
		return rmbFlowSelect;
	}

	public void setRmbFlowSelect(List<SelectItem> rmbFlowSelect) {
		this.rmbFlowSelect = rmbFlowSelect;
	}

	public List<SelectItem> getImportFinanceLinkSelect() {
		return importFinanceLinkSelect;
	}

	public void setImportFinanceLinkSelect(
			List<SelectItem> importFinanceLinkSelect) {
		this.importFinanceLinkSelect = importFinanceLinkSelect;
	}

	public Boolean getFloat1Disable() {
		return float1Disable;
	}

	public void setFloat1Disable(Boolean float1Disable) {
		this.float1Disable = float1Disable;
	}

	public Boolean getFloat2Disable() {
		return float2Disable;
	}

	public void setFloat2Disable(Boolean float2Disable) {
		this.float2Disable = float2Disable;
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

	public String getTestLength() {
		return testLength;
	}

	public void setTestLength(String testLength) {
		this.testLength = testLength;
	}

	public boolean isIfSure() {
		return ifSure;
	}

	public void setIfSure(boolean ifSure) {
		this.ifSure = ifSure;
	}

	public boolean isIfYunwei() {
		return ifYunwei;
	}

	public void setIfYunwei(boolean ifYunwei) {
		this.ifYunwei = ifYunwei;
	}

	public boolean isIfRequest() {
		return ifRequest;
	}

	public void setIfRequest(boolean ifRequest) {
		this.ifRequest = ifRequest;
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
