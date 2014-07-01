package com.wcs.tms.view.process.bankcredit;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
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
import com.wcs.base.service.EntityService;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.MathUtil;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.Credit;
import com.wcs.tms.model.ProcBankCredit;
import com.wcs.tms.model.ProcBankCreditConfirm;
import com.wcs.tms.model.ProcRptCredit;
import com.wcs.tms.model.ProcRptCreditConfirm;
import com.wcs.tms.service.process.bankcredit.BankCreditService;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessUtilService;
import com.wcs.tms.service.process.common.ProcessWaitAcceptService;
import com.wcs.tms.service.process.common.patch.service.PatchMainService;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.service.system.company.CreditService;
import com.wcs.tms.util.CustomPageModel;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.FileUpload;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

import filenet.vw.api.VWWorkObjectNumber;

/**
 * 
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description: 银行授信申请Bean
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
public class BankCreditBean extends FileUpload<ProcBankCredit> {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

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
	/** 选择的公司Id */
	private Long companyId;
	/** 资金币种下拉 */
	private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
	/** 利率挂钩下拉 */
	private List<SelectItem> rateHookSelect = new ArrayList<SelectItem>();
	/** 公司中午名称下拉选择框 */
	private List<SelectItem> companyNameSelect = new ArrayList<SelectItem>();
	/** 银行总行Id */
	private Long topBankId;
	/** 一级机构银行下拉 */
	private List<SelectItem> topLevelSelect = new ArrayList<SelectItem>();
	/** 选择支行Id */
	private Long branchSelectId;
	/** 支行下拉 */
	private List<SelectItem> branchSelect = new ArrayList<SelectItem>();
	/** 用于添加 */
	private ProcRptCredit rptCredit = new ProcRptCredit();
	/** 用于编辑删除 */
	private ProcRptCredit rptCreditEdit = new ProcRptCredit();
	private ProcRptCredit rptCreditEditVo = new ProcRptCredit();
	/** */
	private ProcRptCredit rptCreditCopyEdit = new ProcRptCredit();
	/** 银行授信申请其他产品 */
	private List<ProcRptCredit> proRptCreditList = Lists.newArrayList();
	/** 银行授信申请其他产品分页模型 */
	private LazyDataModel<ProcRptCredit> propLayModel;
	/** 用于查看审批详细步骤，附件时用的全局参数 */
	private String workObjNum;
	/** 审批步骤名称 */
	private String stepName;
	/** 是否显示详细步骤表格判断 */
	private boolean showDetialStep;
	/** 流程详细List数据 */
	private List<ProcessDetailVo> processDetailList = new ArrayList<ProcessDetailVo>();
	/** 得到审批时可输入的字段 */
	private List<String> inputableFields = new ArrayList<String>();
	/** 记录授信其他产品编辑之前的名称 */
	private String prtCreditNameEdit;
	// 流程实例ID
	private String procInstId;
	// 是否处理下一个任务9.5
	private boolean doNext = true;
	// 当前所处理任务在任务列表中的位置
	private Integer currentIndex;
	// 当前处理任务类型（已接受和待接收）
	private String currentTaskType;

	// 是否是补丁表中获取到数据查看，处理2014.4.11~2014.4.28之间发生故障，丢失的流程数据和流程审批日志的查看
	private String isPatch;

	private static final Log log = LogFactory.getLog(BankCreditBean.class);

	@Inject
	private BankService bacnkSerice;
	@Inject
	private CommonBean dictBean;
	@Inject
	private CompanyTmsService companyTmsService;
	@Inject
	private CreditService creditService;
	@Inject
	private EntityService entityService;
	@Inject
	private BankCreditService bankCreditService;
	@Inject
	private ProcessWaitAcceptService processWaitService;
	@Inject
	ProcessUtilService processUtilService;// 9.5
	@Inject
	ProcessUtilMapService processUtilMapService;// 9.11
	@Inject
	PatchMainService patchMainService;

	public BankCreditBean() {
		// list已提价流程界面,input已处理流程界面
		setupPage("/faces/process/common/processSubed-list.xhtml",
				"/faces/process/common/processDealed-list.xhtml", null);
	}

	@PostConstruct
	public void init() {
		// 申请时间
		if (getInstance().getApplyDate() == null) {
			registerDate = DateUtil.dateToStrShort(DateUtil.getNowDateShort());
			getInstance().setApplyDate(
					DateUtil.dateTimeToDate(DateUtil
							.strToDateShort(registerDate)));
			// modified by yanchangjing on 2012-9-20
			getInstance().setLiquCred(0.00);
			getInstance().setBankAcpt(0.00);
			getInstance().setImportCredit(0.00);
			getInstance().setImportFinance(0.00);
			getInstance().setExportFinance(0.00);
			getInstance().setDollarFlowFinance(0.00);
			getInstance().setDomesticCred(0.00);
			getInstance().setBussTicket(0.00);
			getInstance().setForwTrade(0.00);
		}
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
			workObjNum = wcnum;
			// 获取银行授信数据
			fillData(workObjNum);
			if ("approve".equals(flag)) {
				// add on 2013-5-17
				currentIndex = Integer.parseInt(JSFUtils
						.getParamValue("currentIndex"));
				currentTaskType = (String) JSFUtils
						.getParamValue("currentTaskType");

				stepName = JSFUtils.getRequest().getAttribute("stepName")
						.toString();
				// 得到可以审批时可以修改的字段
				getInputable(stepName);
				// 授信组审批时需要将审批额度默认设置为申请审批额度
				if (inputableFields != null && !inputableFields.isEmpty()
						&& inputableFields.contains("notarizeCreditLine")
						&& getInstance().getNotarizeCreditLine() == null) {
					getInstance().setNotarizeCreditLine(
							getInstance().getCreditLine());
				}
				// 非重新申请页面设置“处理意见”默认值
				if (!"申请".equals(stepName)) {
					getInstance().setPeMemo("同意");
				}
			}
			// 查询其他授信产品,用addAll 查询的结果是只读
			List<ProcRptCredit> list = this.bankCreditService
					.findProcRptCreditList(wcnum);
			proRptCreditList.addAll(list);
			propLayModel = new CustomPageModel<ProcRptCredit>(proRptCreditList,
					false);

			searchProcessDetail();
			// 初始化下拉
			initdata(true);
		} else {
			initdata(false);
			workObjNum = null;
			stepName = null;
			// 提交申请之后若验证失败为了保留用户输入数据
			keepUserData();
		}
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
	 * 
	 * <p>
	 * Description: 初始化上年授信额度
	 * </p>
	 */
	public void initLastCreditCu() {
		boolean flag = true;
		if (companyId == null) {
			return;
		}
		List<Credit> viableCreditList = creditService.findViableCredit(
				companyId, branchSelectId);
		List<Credit> creditList = creditService.findlastCredit(companyId,
				branchSelectId);
		//
		if (viableCreditList.isEmpty()) {
			// huhan add 8.14
			if (!creditList.isEmpty()) {
				try {
					getInstance().setLastCreditLine(
							creditList.get(0).getCreditLine());
					return;
				} catch (Exception e) {
					getInstance().setLastCreditLine(null);
					return;
				}
			}
			getInstance().setLastCreditLine(null);
			return;
		}
		// 没有上年授信额度
		if (creditList.isEmpty()) {
			getInstance().setLastCreditLine(null);
			return;
		}
		out: for (Credit c : creditList) {
			for (Credit credit : viableCreditList) {
				if (c.getId().equals(credit.getId())) {
					continue;
				} else {
					getInstance().setLastCreditLine(c.getCreditLine());
					flag = false;
					break out;
				}
			}
		}
		if (flag) {
			getInstance().setLastCreditLine(null);
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 公司Id更新
	 * </p>
	 */
	public void updateCompany() {
		// 选择公司以后需要将界面所选择的公司需要将值传入后台，通过Ajax来更新模型值
		// 若已经选择了银行，此时更换公司自动更新上年授信
		if (topBankId != null && branchSelectId != null) {
			initLastCreditCu();
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 添加授信其他产品
	 * </p>
	 */
	public void addCreditRpt() {
		if (branchSelectId == null || this.topBankId == null) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("msg_required_bank"));
			return;
		}
		if (companyId == null) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("msg_select_company"));
			return;
		}
		if (rptCredit.getCdProLimit() == null
				|| "".equals(rptCredit.getCdProName())) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("msg_required_otherProInfo"));
			return;
		}
		boolean flag = false;
		// 查询产品名称是否存在
		if (workObjNum != null && !"".equals(workObjNum)) {
			flag = bankCreditService.findProcRptCreditByName(workObjNum,
					rptCredit.getCdProName());
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
			for (ProcRptCredit pcredit : proRptCreditList) {
				if (pcredit.getProcInstId() != null
						&& pcredit.getProcInstId().equals(workObjNum)) {
					if (rptCredit.getCdProName().equals(pcredit.getCdProName())) {
						rptFlag = true;
						break;
					}
				} else {
					if (rptCredit.getCdProName().equals(pcredit.getCdProName())) {
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
		proRptCreditList.add(rptCredit);
		propLayModel = new CustomPageModel<ProcRptCredit>(proRptCreditList,
				false);
		this.setRptCredit(new ProcRptCredit());
		MessageUtils.addSuccessMessage("bankCreditMsg",
				MessageUtils.getMessage("otherPro_new_success"));
	}

	/**
	 * 
	 * <p>
	 * Description: 删除其他产品
	 * </p>
	 */
	public void deleteProcRptCredit() {
		try {
			proRptCreditList.remove(rptCreditEdit);
			propLayModel = new CustomPageModel<ProcRptCredit>(proRptCreditList,
					true);
			MessageUtils.addSuccessMessage("bankCreditMsg",
					MessageUtils.getMessage("otherPro_delete_success"));
		} catch (Exception e) {
			log.error("deleteProcRptCredit方法 删除其他产品 出现异常：", e);
		}
	}

	/**
	 * 
	 * <p>
	 * Description:编辑之前
	 * </p>
	 */
	public void beforeEdit() {
		prtCreditNameEdit = rptCreditEdit.getCdProName();
		try {
			// 将编辑前的对象属性值赋值出来,因为编辑时将对象赋值到后台，界面修改到编辑用的同一对象，后台验证失败了却更改了值
			PropertyUtils.copyProperties(rptCreditCopyEdit, rptCreditEdit);
			rptCreditEditVo = rptCreditEdit;
			rptCreditEdit = new ProcRptCredit();
			PropertyUtils.copyProperties(rptCreditEdit, rptCreditCopyEdit);
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
		if (workObjNum != null && !"".equals(workObjNum)) {
			flag = bankCreditService.findProcRptCreditByName(workObjNum,
					rptCredit.getCdProName());
		}
		if (flag && prtCreditNameEdit.equals(rptCreditEdit.getCdProName())) {
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
		if (rptCreditEdit.getCdProLimit() != null
				&& rptCreditEdit.getCdProLimit() > this.getInstance()
						.getCreditLine()) {
			MessageUtils.addWarnMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_otherPro_larger"));
		}
		boolean rptFlag = false;
		if (!proRptCreditList.isEmpty()) {
			for (ProcRptCredit pcredit : proRptCreditList) {
				if (pcredit.getProcInstId() != null) {
					if (!prtCreditNameEdit.equals(pcredit.getCdProName())
							&& rptCreditEdit.getCdProName().equals(
									pcredit.getCdProName())
							&& pcredit.getProcInstId().equals(workObjNum)) {
						rptFlag = true;
						break;
					}
				} else {
					if (!prtCreditNameEdit.equals(pcredit.getCdProName())
							&& rptCreditEdit.getCdProName().equals(
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
					MessageUtils.addErrorMessage("editcdProNameMsg",
							MessageUtils.getMessage("errMsg_existOtherPro"));
					try {
						PropertyUtils.copyProperties(rptCreditEdit,
								rptCreditCopyEdit);
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
		if (proRptCreditList.contains(rptCreditEditVo)) {
			proRptCreditList.remove(rptCreditEditVo);
		}
		proRptCreditList.add(rptCreditEdit);
		propLayModel = new CustomPageModel<ProcRptCredit>(proRptCreditList,
				true);
		MessageUtils.addSuccessMessage("bankCreditMsg",
				MessageUtils.getMessage("otherPro_edit_success"));
	}

	/**
	 * 验证其他产品额度不能大于本次剩余申请额度
	 * 
	 * @return true/false
	 */
	private Boolean validationCdProLimit() {
		Boolean notPass = false;
		Double creditLine = getInstance().getCreditLine(); // 本次申请额度
		Double cdProLimit = this.rptCredit.getCdProLimit(); // 其它授信产品额度
		log.info("!!!!" + creditLine + "###" + cdProLimit);
		if (cdProLimit != null && cdProLimit.compareTo(creditLine) > 0) {
			notPass = true;
		}
		log.info("notPass:" + notPass);
		return notPass;
	}

	/**
	 * 
	 * <p>
	 * Description: 发起授信申请
	 * </p>
	 * 
	 * @return
	 */
	public String applyRegister() {
		try {
			// 上年授信额度验证
			boolean lastCreitFlag = validateLastCredit();
			if (lastCreitFlag) {
				transportData();
				return null;
			}
			// 本年授信额度验证
			boolean creditflag = validateCredit();
			if (creditflag) {
				transportData();
				return null;
			}
			// 授信品种产品验证
			boolean productFlag = validateCreditProduct();
			if (productFlag) {
				transportData();
				return null;
			}
			// 设置银行
			Bank bank = entityService.find(Bank.class, branchSelectId);
			getInstance().setBank(bank);
			// 设置公司
			Company company = this.entityService.find(Company.class, companyId);
			getInstance().setCompany(company);
			String workflowClassName = ProcessXmlUtil.getProcessAttribute("id",
					"BankCreditId", "className");
			String workClassnumber = bankCreditService.vwCreateProcessInstance(
					workflowClassName, "");
			// 设置流程实例Id
			getInstance().setProcInstId(workClassnumber);
			this.entityService.create(getInstance());
			// 添加其它产品
			if (!proRptCreditList.isEmpty()) {
				bankCreditService.batchAddCreditRpt(proRptCreditList,
						workClassnumber);
			}
			// 生成流程实例编号映射9.11
			processUtilMapService.generateProcessMap(workClassnumber,
					"BPM_RA_001", company);
			MessageUtils.addSuccessMessage("doneMsg", MessageUtils.getMessage(
					"process_new_success",
					processUtilMapService.getTmsIdByFnId(workClassnumber)));
			return getListPage();
		} catch (ServiceException e) {
			MessageUtils.addErrorMessage("bankCreditMsg", e.getMessage());
			return null;
		} catch (Exception e) {
			if (e instanceof EJBException) {
				ServiceException se = (ServiceException) ((EJBException) e)
						.getCause();
				MessageUtils.addErrorMessage("bankCreditMsg", se.getMessage());
			}
			if (e instanceof InvocationTargetException) {
				ServiceException se = (ServiceException) ((InvocationTargetException) e)
						.getTargetException().getCause();
				MessageUtils.addErrorMessage("bankCreditMsg", se.getMessage());
			}
			log.error("applyRegister方法 发起授信申请 出现异常：", e);
			return null;
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 重新申请
	 * </p>
	 * 
	 * @return
	 */
	public String reApplyRegBankCredit() {
		try {
			boolean lastCreitFlag = validateLastCredit();
			if (lastCreitFlag) {
				transportData();
				JSFUtils.getRequest().setAttribute("stepName", stepName);
				JSFUtils.getRequest().setAttribute("processInd",
						getInstance().getProcInstId());
				return null;
			}
			boolean creditflag = validateCredit();
			if (creditflag) {
				transportData();
				JSFUtils.getRequest().setAttribute("stepName", stepName);
				JSFUtils.getRequest().setAttribute("processInd",
						getInstance().getProcInstId());
				return null;
			}
			boolean productFlag = validateCreditProduct();
			if (productFlag) {
				transportData();
				JSFUtils.getRequest().setAttribute("stepName", stepName);
				JSFUtils.getRequest().setAttribute("processInd",
						getInstance().getProcInstId());
				return null;
			}

			String workClassNum = getInstance().getProcInstId();
			this.companyId = getInstance().getCompany().getId();
			String memo = getInstance().getPeMemo() == null ? ""
					: getInstance().getPeMemo();
			bankCreditService.vwReplayBankRegister(memo, workClassNum);
			entityService.update(getInstance());
			// 添加其它产品
			if (!proRptCreditList.isEmpty()) {
				bankCreditService.batchAddCreditRpt(proRptCreditList,
						workClassNum);
			}
			MessageUtils.addSuccessMessage("doneMsg", MessageUtils.getMessage(
					"process_new_success_1",
					processUtilMapService.getTmsIdByFnId(workClassNum)));
			// 9.5处理下一个任务
			return processUtilService.getNextPage(getInputPage(), doNext,
					currentIndex, currentTaskType);
		} catch (ServiceException e) {
			MessageUtils.addErrorMessage("msg", e.getMessage());
			return null;
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
			return null;
		}

	}

	/**
	 * 
	 * <p>
	 * Description: 银行授信申请审批
	 * </p>
	 * 
	 * @return
	 */
	public String approveBankCredit() {
		try {
			Object obj = JSFUtils.getRequestParam("auit");
			// 是否有字段可以修改 若有进行相关验证
			String inputable = ProcessXmlUtil.findStepProperty("id",
					"BankCreditId", stepName, "inputable");
			if (!"".equals(inputable)) {
				if (stepName != null
						&& ("集团资金部门经理审批".equals(stepName) || "集团资金部门人员-授信组审批"
								.equals(stepName))) {
					// 避免sonar检查
				} else {
					boolean creditflag = validateCredit();
					if (creditflag) {
						transportData();
						JSFUtils.getRequest()
								.setAttribute("stepName", stepName);
						return null;
					}
				}
				boolean productFlag = validateCreditProduct();
				if (productFlag) {
					transportData();
					JSFUtils.getRequest().setAttribute("stepName", stepName);
					return null;
				}
			}
			// huhan modify on 8.23 只有审批通过才生成确认单
			// 审批通过与否参数
			boolean flag = false;
			String processFlow = "";
			if (obj != null) {
				flag = Boolean.valueOf(obj.toString());
				if (flag) {
					// 经理审批时产生确认单数据以便申请人确认的时候维护数据
					if (stepName != null && "集团资金部门经理审批".equals(stepName)) {
						ProcBankCreditConfirm blocConfirm = changeRequestData(getInstance());
						if (blocConfirm.getProcInstId() != null) {
							entityService.create(blocConfirm);
							// 保存其他授信产品到确认单
							for (ProcRptCredit rptCredit : proRptCreditList) {
								ProcRptCreditConfirm confirmRpt = new ProcRptCreditConfirm();
								confirmRpt.setCdProLimit(rptCredit
										.getCdProLimit());
								confirmRpt.setCdProName(rptCredit
										.getCdProName());
								confirmRpt.setProcInstId(blocConfirm
										.getProcInstId());
								entityService.create(confirmRpt);
							}
						}
					}
					processFlow = ProcessXmlUtil.findStepProperty("id",
							"BankCreditId", stepName, "passMemo");
					// add on 2013-1-15 by yanchangjing
					// 如果本年担保方式里面的担保大于0，则在申请人确认前给申请人发送邮件知会其流程实例编号
					if ("集团资金部门经理审批".equals(stepName)
							&& getInstance().getGuaranteeGr() != null
							&& getInstance().getGuaranteeGr() > 0) {
						bankCreditService
								.mailApplicant(getInstance(), stepName);
					}

				} else {
					processFlow = ProcessXmlUtil.findStepProperty("id",
							"BankCreditId", stepName, "nopassMemo");
					getInstance().setNotarizeCreditLine(null);
				}
			}
			// 审批执行
			bankCreditService.vwDisposeTask(getInstance().getProcInstId(),
					flag, processFlow + getInstance().getPeMemo());
			// 更新其他产品
			bankCreditService.batchAddCreditRpt(proRptCreditList, getInstance()
					.getProcInstId());
			// 更新修改后的银行授信数据
			if (inputable != null && !"".equals(inputable)) {
				entityService.update(getInstance());
			}
			if (flag) {
				MessageUtils.addSuccessMessage(
						"doneMsg",
						stepName
								+ MessageUtils.getMessage(
										"process_approve_success",
										processUtilMapService
												.getTmsIdByFnId(getInstance()
														.getProcInstId())));

			} else {
				MessageUtils.addSuccessMessage(
						"doneMsg",
						stepName
								+ MessageUtils.getMessage(
										"process_approve_fail",
										processUtilMapService
												.getTmsIdByFnId(getInstance()
														.getProcInstId())));
			}
			// 9.5处理下一个任务
			return processUtilService.getNextPage(getInputPage(), doNext,
					currentIndex, currentTaskType);
		} catch (Exception e) {
			log.error("approveBankCredit方法 银行授信申请审批 出现异常：", e);
			return null;
		}
	}

	/**
	 * 
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
	 * 查询流程详细步骤
	 */
	private void searchProcessDetail() {
		log.info("查询流程详细步骤");
		log.info("procInstId:" + procInstId);

		if ("true".equals(isPatch)) {
			processDetailList = patchMainService
					.getProcessDetailFor411(procInstId);
		} else {
			processDetailList = peManager.getProcessDetail(procInstId);
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 总行联动支行
	 * </p>
	 */
	public void bankChange() {
		if (null == companyId) {
			MessageUtils.addErrorMessage("bankCreditMsg", "请先选择申请单位");
			return;
		}
		branchSelect = creditService.findBranchBankSelect(topBankId);
		// modified on 2012-9-25
		List<Credit> credits = creditService.findLastCredit(registerDate,
				topBankId, companyId);
		if (credits.size() != 0) {
			branchSelectId = credits.get(0).getBank().getId();
		} else {
			branchSelectId = 0L;
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 本年申請額度賦值，用于其他产品判断额度
	 * </p>
	 */
	public void creditLineChange() {

	}

	/**
	 * 字段可输入检查
	 * 
	 * @return
	 */
	public boolean checkInputable(String fieldName) {
		if (inputableFields != null && !inputableFields.isEmpty()
				&& inputableFields.contains(fieldName)) {
			return true;
		}
		return false;
	}

	/**
	 * 得到可输入的审批字段
	 */
	private void getInputable(String stepName) {
		inputableFields = ProcessXmlUtil.getInputableDatas("BankCreditId",
				stepName);
	}

	/**
	 * 初始化下拉
	 */
	private void initdata(boolean isAll) {
		if (isAll) {
			companyNameSelect = companyTmsService.getAllCompanySelect();
		} else {
			companyNameSelect = companyTmsService.getCompanySelect();
		}
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
	 * Description: 审批，查看时需要填充的数据
	 * </p>
	 * 
	 * @param workclassNumber
	 */
	private void fillData(String workclassNumber) {
		if (workclassNumber != null) {
			try {
				ProcBankCredit procBankCredit = bankCreditService
						.findProcBankCd(workclassNumber);
				if (procBankCredit == null) {
					return;
				}
				setInstance(procBankCredit);
				if (procBankCredit != null
						&& procBankCredit.getCompany() == null) {
					MessageUtils.addErrorMessage("bankCreditMsg",
							MessageUtils.getMessage("regicapital_noBound"));
				}
				// 得到公司Id
				companyId = procBankCredit.getCompany().getId();
				registerDate = DateUtil.dateToStrShort(DateUtil
						.dateToDateTime(procBankCredit.getApplyDate()));
				// 得到分支授信银行
				Bank bank = procBankCredit.getBank();
				if (bank != null) {
					// 选中一级银行
					topBankId = bank.getTopBankId();
					branchSelectId = bank.getId();
					branchSelect.clear();
					// 添加分支银行下拉
					branchSelect.add(new SelectItem(branchSelectId, bank
							.getBankName()));
				}
			} catch (Exception e) {
				log.error("fillData方法 审批，查看时需要填充的数据 出现异常：", e);
			}
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 将申请单数据赋值到确认单
	 * </p>
	 * 
	 * @param blocCredit
	 * @return
	 */
	private ProcBankCreditConfirm changeRequestData(ProcBankCredit blocCredit) {
		ProcBankCreditConfirm blocConfirm = new ProcBankCreditConfirm();
		if (blocCredit != null && blocCredit.getId() != null) {
			blocConfirm.setBank(blocCredit.getBank());
			blocConfirm.setBankAcpt(blocCredit.getBankAcpt());
			blocConfirm.setBankAcptEf(blocCredit.getBankAcptEf());
			blocConfirm.setBankAcptFe(blocCredit.getBankAcptFe());
			blocConfirm.setBankAcptGp(blocCredit.getBankAcptGp());
			blocConfirm.setBussTicket(blocCredit.getBussTicket());
			blocConfirm.setBussTicketDc(blocCredit.getBussTicketDc());
			blocConfirm.setBussTicketFe(blocCredit.getBussTicketFe());
			blocConfirm.setBussTicketGp(blocCredit.getBussTicketGp());
			blocConfirm.setCompany(blocCredit.getCompany());
			// 改为把审批确认额度写入确认单中
			blocConfirm.setCreditLimit(blocCredit.getNotarizeCreditLine());
			blocConfirm.setCreditLineCu(blocCredit.getCreditLineCu());
			blocConfirm.setDollarFlowFinance(blocCredit.getDollarFlowFinance());
			blocConfirm.setDollarFlowLink(blocCredit.getDollarFlowLink());
			blocConfirm.setDollarFlowPoint(blocCredit.getDollarFlowPoint());
			blocConfirm.setDomesticCred(blocCredit.getDomesticCred());
			blocConfirm.setDomesticCredDf(blocCredit.getDomesticCredDf());
			blocConfirm.setDomesticCredFe(blocCredit.getDomesticCredFe());
			blocConfirm.setDomesticCredGp(blocCredit.getDomesticCredGp());
			blocConfirm.setExportFinance(blocCredit.getExportFinance());
			blocConfirm.setExportFinanceLink(blocCredit.getExportFinanceLink());
			blocConfirm.setExportFinancePonit(blocCredit
					.getExportFinancePonit());
			blocConfirm.setGuaranteeCd(blocCredit.getGuaranteeCd());
			blocConfirm.setGuaranteeGr(blocCredit.getGuaranteeGr());
			blocConfirm.setGuaranteeMg(blocCredit.getGuaranteeMg());
			blocConfirm.setGuaranteeOt(blocCredit.getGuaranteeOt());
			blocConfirm.setGuaranteeQm(blocCredit.getGuaranteeQm());
			blocConfirm.setImportCredit(blocCredit.getImportCredit());
			blocConfirm.setImportCreditFe(blocCredit.getImportCreditFe());
			blocConfirm.setImportCreditGp(blocCredit.getImportCreditGp());
			blocConfirm.setImportFinance(blocCredit.getImportFinance());
			blocConfirm.setImportFinanceLink(blocCredit.getImportFinanceLink());
			blocConfirm.setImportFinancePonit(blocCredit
					.getImportFinancePonit());
			blocConfirm.setLiquCred(blocCredit.getLiquCred());
			blocConfirm.setLiquCredAp(blocCredit.getLiquCredAp());
			blocConfirm.setLiquCredPonit(blocCredit.getLiquCredPonit());
			blocConfirm.setLiquCredRa(blocCredit.getLiquCredRa());
			blocConfirm.setProcInstId(blocCredit.getProcInstId());
			// 添加“远期交易额度-费率”字段add by yanchangjing on 2012-9-21
			blocConfirm.setForwTradeCr(blocCredit.getForwTradeCr());
			blocConfirm.setForwTrade(blocCredit.getForwTrade());
			blocConfirm.setFloatFlag(blocCredit.getFloatFlag());
			blocConfirm.setFloat1(blocCredit.getFloat1());
			blocConfirm.setFloat2(blocCredit.getFloat2());
		}
		return blocConfirm;
	}

	/**
	 * 
	 * <p>
	 * Description: 将用户输入的数据放入Request对象
	 * </p>
	 */
	private void transportData() {
		JSFUtils.getRequest().setAttribute("validateVo1", getInstance());
		Bank bank = this.entityService.find(Bank.class, branchSelectId);
		if (bank != null) {
			// 添加分支银行下拉
			JSFUtils.getRequest().setAttribute("branchSelectId",
					new SelectItem(branchSelectId, bank.getBankName()));
		}
		JSFUtils.getRequest().setAttribute("companyId", companyId);
		JSFUtils.getRequest().setAttribute("topBankId", topBankId);
		JSFUtils.getRequest().setAttribute("bankCreditRpt", proRptCreditList);
		JSFUtils.getRequest().setAttribute("bankCreditProcessDetail",
				processDetailList);
	}

	/**
	 * 
	 * <p>
	 * Description: 记录用户输入的数据
	 * </p>
	 */
	private void keepUserData() {
		// 实体类数据
		Object obj = JSFUtils.getRequest().getAttribute("validateVo1");
		// 公司id
		Object companyIdReg = JSFUtils.getRequest().getAttribute("companyId");
		// 一级银行Id
		Object topBankIdReg = JSFUtils.getRequest().getAttribute("topBankId");
		// 分支银行Id
		Object branchSelectReg = JSFUtils.getRequest().getAttribute(
				"branchSelectId");
		// 节点名称
		Object stepNameReg = JSFUtils.getRequest().getAttribute("stepName");
		Object bankRptList = JSFUtils.getRequest()
				.getAttribute("bankCreditRpt");
		Object bankprocessDetailList = JSFUtils.getRequest().getAttribute(
				"bankCreditProcessDetail");
		if (obj != null) {
			setInstance((ProcBankCredit) obj);
		}
		if (companyIdReg != null) {
			this.companyId = (Long) companyIdReg;
		}
		if (topBankIdReg != null) {
			this.topBankId = (Long) topBankIdReg;
		}
		if (branchSelectReg != null) {
			SelectItem item = (SelectItem) branchSelectReg;
			this.branchSelectId = (Long) item.getValue();
			this.branchSelect.add(item);
		}
		// 银行授信其他产品数据传输
		if (bankRptList != null) {
			List<ProcRptCredit> list = (List<ProcRptCredit>) bankRptList;
			proRptCreditList.clear();
			proRptCreditList.addAll(list);
			propLayModel = new CustomPageModel<ProcRptCredit>(proRptCreditList,
					false);

		}
		// 流程详细传输
		if (bankprocessDetailList != null) {
			processDetailList.clear();
			List<ProcessDetailVo> list = (List<ProcessDetailVo>) bankprocessDetailList;
			processDetailList.addAll(list);
		}
		// 初始化需要编辑的字段
		if (stepNameReg != null) {
			stepName = stepNameReg.toString();
			getInputable(stepNameReg.toString());
			workObjNum = getInstance().getProcInstId();
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 验证上年授信
	 * </p>
	 */
	private boolean validateLastCredit() {
		Double lastCd = getInstance().getLastGuaranteeCd();
		Double lastGr = getInstance().getLastGuaranteeGr();
		Double lastMg = getInstance().getLastGuaranteeMg();
		Double lastQm = getInstance().getLastGuaranteeQm();
		Double lastOt = getInstance().getLastGuaranteeOt();
		// 上年授信额度
		Double lastCredit = getInstance().getLastCreditLine();
		Double[] data = new Double[5];
		data[0] = lastCd;
		data[1] = lastGr;
		data[2] = lastMg;
		data[3] = lastQm;
		data[4] = lastOt;
		if (lastCd == null && lastGr == null && lastMg == null
				&& lastQm == null && lastOt == null) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_lastAssureWay_notNull"));
			return true;
		}
		if ((lastCd != null && lastCd > lastCredit)
				|| (lastGr != null && lastGr > lastCredit)
				|| (lastMg != null && lastMg > lastCredit)
				|| (lastQm != null && lastQm > lastCredit)
				|| (lastOt != null && lastOt > lastCredit)) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_lastAssureWay_larger"));
			return true;
		}
		double tatol = 0.0;
		for (int i = 0; i < 5; i++) {
			if (data[i] != null) {
				tatol += data[i];
			}
		}
		if (lastCredit > tatol) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_lastCreditLine_larger"));
			return true;
		}
		return false;
	}

	/**
	 * 
	 * <p>
	 * Description: 验证本年授信--非集团资金部审批
	 * </p>
	 * 
	 * @param isApproval
	 *            是否集团资金部审批
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
	 * Description: 验证本年授信--集团资金部审批
	 * </p>
	 * 
	 * @param isApproval
	 *            是否集团资金部审批
	 * @return
	 */
	private boolean validateCreditGro() {
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
		// 人民币流贷额度
		Double liquCred = getInstance().getLiquCred();
		// 银行汇兑票额度
		Double bankAcp = getInstance().getBankAcpt();
		// 进口信用额度
		Double importCredit = getInstance().getImportCredit();
		// 进口押汇-额度
		Double importFinance = getInstance().getImportFinance();
		// 出口押汇-额度
		Double exportFinance = getInstance().getExportFinance();
		// 美元流代-额度
		Double dollarFlowFinance = getInstance().getDollarFlowFinance();
		// 国内信用证-额度
		Double domesticCred = getInstance().getDomesticCred();
		// 商票保贴-额度
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
		/**********************************************************/
		// 人民币流贷
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
			// modified by yanchangjing on 2012-9-25
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
		// 海外代付
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
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
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
				// TODO Auto-generated method stub
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

	// 海外代付额度的验证
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
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_forwTrade_larger"));
			return;
		}
	}

	// --------------------------------------------------- Get & Set
	// ----------------------------------------------------------//

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

	public List<SelectItem> getCurrencySelect() {
		return currencySelect;
	}

	public void setCurrencySelect(List<SelectItem> currencySelect) {
		this.currencySelect = currencySelect;
	}

	public List<SelectItem> getCompanyNameSelect() {
		return companyNameSelect;
	}

	public void setCompanyNameSelect(List<SelectItem> companyNameSelect) {
		this.companyNameSelect = companyNameSelect;
	}

	public CommonBean getDictBean() {
		return dictBean;
	}

	public void setDictBean(CommonBean dictBean) {
		this.dictBean = dictBean;
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

	public List<SelectItem> getBranchSelect() {
		return branchSelect;
	}

	public void setBranchSelect(List<SelectItem> branchSelect) {
		this.branchSelect = branchSelect;
	}

	public CompanyTmsService getCompanyTmsService() {
		return companyTmsService;
	}

	public void setCompanyTmsService(CompanyTmsService companyTmsService) {
		this.companyTmsService = companyTmsService;
	}

	public Long getBranchSelectId() {
		return branchSelectId;
	}

	public void setBranchSelectId(Long branchSelectId) {
		this.branchSelectId = branchSelectId;
	}

	public List<ProcRptCredit> getProRptCreditList() {
		return proRptCreditList;
	}

	public void setProRptCreditList(List<ProcRptCredit> proRptCreditList) {
		this.proRptCreditList = proRptCreditList;
	}

	public ProcRptCredit getRptCredit() {
		return rptCredit;
	}

	public void setRptCredit(ProcRptCredit rptCredit) {
		this.rptCredit = rptCredit;
	}

	public LazyDataModel<ProcRptCredit> getPropLayModel() {
		return propLayModel;
	}

	public void setPropLayModel(LazyDataModel<ProcRptCredit> propLayModel) {
		this.propLayModel = propLayModel;
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

	public boolean isShowDetialStep() {
		return showDetialStep;
	}

	public void setShowDetialStep(boolean showDetialStep) {
		this.showDetialStep = showDetialStep;
	}

	public List<ProcessDetailVo> getProcessDetailList() {
		return processDetailList;
	}

	public void setProcessDetailList(List<ProcessDetailVo> processDetailList) {
		this.processDetailList = processDetailList;
	}

	public List<String> getInputableFields() {
		return inputableFields;
	}

	public void setInputableFields(List<String> inputableFields) {
		this.inputableFields = inputableFields;
	}

	public ProcRptCredit getRptCreditEdit() {
		return rptCreditEdit;
	}

	public void setRptCreditEdit(ProcRptCredit rptCreditEdit) {
		this.rptCreditEdit = rptCreditEdit;
	}

	public List<SelectItem> getRateHookSelect() {
		return rateHookSelect;
	}

	public void setRateHookSelect(List<SelectItem> rateHookSelect) {
		this.rateHookSelect = rateHookSelect;
	}

	public String getPrtCreditNameEdit() {
		return prtCreditNameEdit;
	}

	public void setPrtCreditNameEdit(String prtCreditNameEdit) {
		this.prtCreditNameEdit = prtCreditNameEdit;
	}

	public ProcRptCredit getRptCreditCopyEdit() {
		return rptCreditCopyEdit;
	}

	public void setRptCreditCopyEdit(ProcRptCredit rptCreditCopyEdit) {
		this.rptCreditCopyEdit = rptCreditCopyEdit;
	}

	public ProcRptCredit getRptCreditEditVo() {
		return rptCreditEditVo;
	}

	public void setRptCreditEditVo(ProcRptCredit rptCreditEditVo) {
		this.rptCreditEditVo = rptCreditEditVo;
	}

	public boolean isDoNext() {
		return doNext;
	}

	public void setDoNext(boolean doNext) {
		this.doNext = doNext;
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
