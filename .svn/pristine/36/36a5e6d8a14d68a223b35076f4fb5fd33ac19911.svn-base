package com.wcs.tms.view.process.bankcreditbloc;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;

import com.google.common.collect.Lists;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.MathUtil;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.Credit;
import com.wcs.tms.model.CreditO;
import com.wcs.tms.model.ProcBankCreditBloc;
import com.wcs.tms.model.ProcBankCreditBlocCompanyConfirm;
import com.wcs.tms.model.ProcBankCreditBlocConfirm;
import com.wcs.tms.model.ProcRptBankCreditBlocConfirm;
import com.wcs.tms.service.process.bankcreditbloc.BankCreditBlocCompanyConfirmService;
import com.wcs.tms.service.process.bankcreditbloc.BankCreditBlocCompanyService;
import com.wcs.tms.service.process.bankcreditbloc.BankCreditBlocService;
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
 * Description: 申请人确认
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
public class BankCreidtBlocCompanyConfirmBean extends
		FileUpload<ProcBankCreditBlocCompanyConfirm> {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory
			.getLog(BankCreidtBlocCompanyConfirmBean.class);

	@Inject
	private BankService bacnkSerice;
	@Inject
	private CommonBean dictBean;
	@Inject
	private CompanyTmsService companyTmsService;
	@Inject
	private BankCreditBlocService bankCreditBlocService;
	@Inject
	private CreditService creditService;
	@Inject
	private BankCreditBlocCompanyConfirmService bankCreditCompanyConfirmService;
	@Inject
	private BankCreditBlocCompanyService bankCreditBlocCompanyService;
	@Inject
	private ProcessWaitAcceptService processWaitService;
	@Inject
	ProcessUtilService processUtilService;// 9.5
	@Inject
	ProcessUtilMapService processUtilMapService;// 9.11
    @Inject
    PatchMainService patchMainService; 

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
	/** 选择的公司Id */
	private Long companyId;
	/** 公司下拉选择框 */
	private List<SelectItem> companyNameSelect = new ArrayList<SelectItem>();
	/** 银行总行Id */
	private Long topBankId;
	/** 审批步骤名称 */
	private String stepName;
	/** 一级机构银行下拉 */
	private List<SelectItem> topLevelSelect = new ArrayList<SelectItem>();
	/** 支行下拉 */
	private List<SelectItem> banchBankSelect = new ArrayList<SelectItem>();
	/** 资金币种下拉 */
	private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
	/** 利率挂钩下拉 */
	private List<SelectItem> rateHookSelect = new ArrayList<SelectItem>();
	/** 银行授信集团申请单 */
	private ProcBankCreditBloc bankCreditBlocView = new ProcBankCreditBloc();
	/** 银行授信集团申请确认单 */
	private ProcBankCreditBlocConfirm blocConpanyConfirm = new ProcBankCreditBlocConfirm();
	/** 成員公司列表 */
	private List<ProcBankCreditBlocCompanyConfirm> blocCompanyConfirmList = Lists
			.newArrayList();
	/** 集团成员公司列表 */
	private LazyDataModel<ProcBankCreditBlocCompanyConfirm> procBlocCompanyComfirmLayModel;
	/** 集团成员公司查看 */
	private ProcBankCreditBlocCompanyConfirm blocCompanyConfirmView = new ProcBankCreditBlocCompanyConfirm();
	/** 集团成员公司编辑 */
	private ProcBankCreditBlocCompanyConfirm blocCompanyConfirmEdit = new ProcBankCreditBlocCompanyConfirm();
	/** 集团其他产品添加 */
	private ProcRptBankCreditBlocConfirm rptBlocConfirm = new ProcRptBankCreditBlocConfirm();
	/** 银行授信申请集团其他产品 */
	private List<ProcRptBankCreditBlocConfirm> proRptConfirmCreditList = Lists
			.newArrayList();
	/** 银行授信申请集团其他产品分页模型 */
	private LazyDataModel<ProcRptBankCreditBlocConfirm> processBlocRptConfirmLayModel;
	/** 银行授信集团申请其他产品编辑 */
	private ProcRptBankCreditBlocConfirm blocCreditRptConfirmEdit = new ProcRptBankCreditBlocConfirm();
	private ProcRptBankCreditBlocConfirm blocCreditRptConfirmEditVo = new ProcRptBankCreditBlocConfirm();
	private ProcRptBankCreditBlocConfirm blocCreditRptConfirmCopy = new ProcRptBankCreditBlocConfirm();
	/** 记录授信其他产品编辑之前的名称 */
	private String prtBlocCreditNameEdit;
	/** 得到审批时可输入的字段 */
	private List<String> inputableFields = new ArrayList<String>();

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
	// 12.14字段长度验证
	private String testLength;
	// 是否验证credit line code字段
	private boolean ifSure = true;
	// 是否是运维人员
	private boolean ifYunwei = false;
	// 最后两人才可查看的字段
	private boolean ifRequest = false;
	// 是否可以上传附件
	private boolean ifAccessory = false;
	// 是否是补丁表中获取到数据查看，处理2014.4.11~2014.4.28之间发生故障，丢失的流程数据和流程审批日志的查看
	private String isPatch;

	public BankCreidtBlocCompanyConfirmBean() {
		// list已提价流程界面,input已处理流程界面
		setupPage("/faces/process/common/processSubed-list.xhtml",
				"/faces/process/common/processDealed-list.xhtml", null);
	}

	@PostConstruct
	public void init() {
		log.info("初始化");
		Object audit = JSFUtils.getParamValue("op");
		Object workclassNumber = JSFUtils.getParamValue("procInstId");
		blocCompanyConfirmView.setBank(new Bank());
		blocCompanyConfirmEdit.setBank(new Bank());
		log.info("audit:" + audit);
		log.info("workclassNumber:" + workclassNumber);
		isPatch = JSFUtils.getParamValue("isPatch");
		// 是否是审批和查看
		if (audit != null && workclassNumber != null) {
			initdata(true);
			String wcnum = String.valueOf(workclassNumber.toString());
			log.info("wcnum:" + wcnum);
			setProcInstId(wcnum);
			blocCompanyConfirmList.addAll(bankCreditBlocService
					.findBlocCompanyConfirmByProcId(wcnum));
			procBlocCompanyComfirmLayModel = new CustomPageModel<ProcBankCreditBlocCompanyConfirm>(
					blocCompanyConfirmList, false);
			ProcBankCreditBloc blocCredit = bankCreditBlocService
					.findBankBlocRegByProcessId(wcnum);
			// 初始化支行
			if (blocCredit != null && blocCredit.getBank() != null) {
				banchBankSelect = creditService.findBranchBankSelect(blocCredit
						.getBank().getId());
			}
			// 审批
			if ("approve".equals(audit)) {
				// add on 2013-5-17
				currentIndex = Integer.parseInt(JSFUtils
						.getParamValue("currentIndex"));
				currentTaskType = (String) JSFUtils
						.getParamValue("currentTaskType");

				stepName = JSFUtils.getRequest().getAttribute("stepName")
						.toString();
				// 得到可以审批时可以修改的字段
				getInputable(stepName);
			} else {
				stepName = (String) JSFUtils.getParamValue("stepName");
			}
			log.info("stepName:" + stepName);
			// 是否可以上传附件
			String accessoryView = ProcessXmlUtil.findStepProperty("id",
					"BankCreditBloc", stepName, "accessoryView");
			if (accessoryView != null && "true".equals(accessoryView)) {
				setIfAccessory(true);
			}
			bankCreditBlocView = blocCredit;
			blocConpanyConfirm = bankCreditCompanyConfirmService
					.findBlocConfirmByBlocCreditId(blocCredit.getId());
			// 该节点几个字段为可编辑，如果改了之后接下来审批就按改完之后值显示（ modified by yan on 2013-7-5）
			if ("集团资金部审批".equals(stepName)) {
				curentYearGuaranteeTotal(blocCompanyConfirmList,
						bankCreditBlocView);
			}
			// 查询流程附件
			displayDetailAttach(wcnum);
			searchProcessDetail();
			blocConpanyConfirm.setPeMemo("同意");
			if ("WCS-TMS运维人员维护".equals((String) JSFUtils
					.getParamValue("stepName"))) {
				setIfYunwei(true);
			}
			if ("风险管理组管理人员确认".equals((String) JSFUtils
					.getParamValue("stepName"))) {
				setIfRequest(true);
			} else if ("WCS-TMS运维人员维护".equals((String) JSFUtils
					.getParamValue("stepName"))) {
				setIfRequest(true);
			}
		} else {
			initdata(false);
			keepUserData();
		}
	}

	/**
	 * “出口押汇”可编辑控制
	 */
	public void exportFinanceLinkChange() {
		// 3、再议
		if ("3".equals(blocCompanyConfirmEdit.getExportFinanceLink())) {
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
		if ("3".equals(blocCompanyConfirmEdit.getImportFinanceLink())) {
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
		if ("3".equals(blocCompanyConfirmEdit.getDollarFlowLink())) {
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
		if ("1".equals(blocCompanyConfirmEdit.getLiquCredRa())) {
			float1Disable = true;
			float2Disable = true;
			floatFlagDisable = true;
		} else if ("2".equals(blocCompanyConfirmEdit.getLiquCredRa())) {
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
	 * Description: 显示成员公司信息
	 * </p>
	 */
	public void viewCompanyCofnirm() {
		// 设置其他授信产品
		List<ProcRptBankCreditBlocConfirm> dataList = bankCreditCompanyConfirmService
				.findBlocRptConfirmByBlocCompany(blocCompanyConfirmView.getId());
		proRptConfirmCreditList.clear();
		proRptConfirmCreditList.addAll(dataList);
		processBlocRptConfirmLayModel = new CustomPageModel<ProcRptBankCreditBlocConfirm>(
				proRptConfirmCreditList, false);
	}

	/**
	 * 
	 * <p>
	 * Description: 确认成员公司信息查询其他授信产品
	 * </p>
	 */
	public void confirmCompany() {
		// 设置其他授信产品
		List<ProcRptBankCreditBlocConfirm> dataList = bankCreditCompanyConfirmService
				.findBlocRptConfirmByBlocCompany(blocCompanyConfirmEdit.getId());
		proRptConfirmCreditList.clear();
		proRptConfirmCreditList.addAll(dataList);
		processBlocRptConfirmLayModel = new CustomPageModel<ProcRptBankCreditBlocConfirm>(
				proRptConfirmCreditList, false);
	}

	/**
	 * 
	 * <p>
	 * Description: 添加银行授信集团成员公司申请其他产品
	 * </p>
	 */
	public void addBlocCreditRpt() {
		// 查询产品名称是否存在
		boolean flag = bankCreditCompanyConfirmService
				.findProcBlocRptCreditConfirmByName(
						blocCompanyConfirmEdit.getId(),
						rptBlocConfirm.getCdProName());
		// 验证本年申请额度是否为空
		Double Credit = blocCompanyConfirmEdit.getCreditLine();
		log.info("本年申请额度:" + Credit);
		try {
			if (blocCompanyConfirmEdit.getCreditLine() == null
					&& "".equals(blocCompanyConfirmEdit.getCreditLine()
							.toString().trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		// 提示用户其他授信产品额度大于本年申请额度
		Boolean cdProlimitIsPass = validationCdProLimit();
		if (cdProlimitIsPass) {
			MessageUtils.addWarnMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_otherPro_larger"));
		}
		boolean rptFlag = false;
		if (!proRptConfirmCreditList.isEmpty()) {
			for (ProcRptBankCreditBlocConfirm pcredit : proRptConfirmCreditList) {
				if (pcredit.getId() != null
						&& pcredit.getId().equals(getInstance().getId())) {
					if (rptBlocConfirm.getCdProName().equals(
							pcredit.getCdProName())) {
						rptFlag = true;
						break;
					}
				} else {
					if (rptBlocConfirm.getCdProName().equals(
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
		proRptConfirmCreditList.add(rptBlocConfirm);
		processBlocRptConfirmLayModel = new CustomPageModel<ProcRptBankCreditBlocConfirm>(
				proRptConfirmCreditList, false);
		setRptBlocConfirm(new ProcRptBankCreditBlocConfirm());
		MessageUtils.addSuccessMessage("bankCreditMsg",
				MessageUtils.getMessage("otherPro_new_success"));
	}

	/**
	 * 验证其他产品额度不能大于本年剩余申请额度 huhan add
	 * 
	 * @return true/false
	 */
	private Boolean validationCdProLimit() {
		Boolean notPass = false;
		Double creditLine = blocCompanyConfirmEdit.getNotarizeCreditLine(); // 审批通过的授信额度
		Double cdProLimit = rptBlocConfirm.getCdProLimit(); // 其它授信产品额度
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
			prtBlocCreditNameEdit = blocCreditRptConfirmEdit.getCdProName();
			// 将编辑前的对象属性值赋值出来,因为编辑时将对象赋值到后台，界面修改到编辑用的同一对象，后台验证失败了却更改了值
			PropertyUtils.copyProperties(blocCreditRptConfirmCopy,
					blocCreditRptConfirmEdit);
			blocCreditRptConfirmEditVo = blocCreditRptConfirmEdit;
			blocCreditRptConfirmEdit = new ProcRptBankCreditBlocConfirm();
			PropertyUtils.copyProperties(blocCreditRptConfirmEdit,
					blocCreditRptConfirmCopy);
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
		flag = bankCreditCompanyConfirmService
				.findProcBlocRptCreditConfirmByName(
						blocCompanyConfirmEdit.getId(),
						blocCreditRptConfirmEdit.getCdProName());
		if (flag
				&& prtBlocCreditNameEdit.equals(blocCreditRptConfirmEdit
						.getCdProName())) {
			flag = false;
		}
		// 验证本年申请额度是否为空
		Double Credit = blocCompanyConfirmEdit.getCreditLine();
		log.info("本年申请额度:" + Credit);
		try {
			if (blocCompanyConfirmEdit.getCreditLine() == null
					&& "".equals(blocCompanyConfirmEdit.getCreditLine()
							.toString().trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		// 提示用户其他授信产品额度大于本年申请额度
		if (blocCreditRptConfirmEdit.getCdProLimit() != null
				&& blocCreditRptConfirmEdit.getCdProLimit() > this
						.getInstance().getCreditLine()) {
			MessageUtils.addWarnMessage("bankCreditMsg",
					MessageUtils.getMessage("errMsg_otherPro_larger"));
		}
		boolean rptFlag = false;
		if (!proRptConfirmCreditList.isEmpty()) {
			for (ProcRptBankCreditBlocConfirm pcredit : proRptConfirmCreditList) {
				if (pcredit.getProcBankCreditBlocCompanyConfirm() != null) {
					if (!prtBlocCreditNameEdit.equals(pcredit.getCdProName())
							&& blocCreditRptConfirmEdit.getCdProName().equals(
									pcredit.getCdProName())
							&& pcredit
									.getProcBankCreditBlocCompanyConfirm()
									.getId()
									.equals(blocCreditRptConfirmEdit
											.getProcBankCreditBlocCompanyConfirm()
											.getId())) {
						rptFlag = true;
						break;
					}
				} else {
					if (!prtBlocCreditNameEdit.equals(pcredit.getCdProName())
							&& blocCreditRptConfirmEdit.getCdProName().equals(
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
						PropertyUtils.copyProperties(blocCreditRptConfirmEdit,
								blocCreditRptConfirmCopy);
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
		if (proRptConfirmCreditList.contains(blocCreditRptConfirmEditVo)) {
			proRptConfirmCreditList.remove(blocCreditRptConfirmEditVo);
		}
		proRptConfirmCreditList.add(blocCreditRptConfirmEdit);
		processBlocRptConfirmLayModel = new CustomPageModel<ProcRptBankCreditBlocConfirm>(
				proRptConfirmCreditList, false);
		MessageUtils.addSuccessMessage("bankCreditMsg",
				MessageUtils.getMessage("otherPro_edit_success"));
	}

	/**
	 * 
	 * <p>
	 * Description: 删除其他产品
	 * </p>
	 */
	public void deleteProConfirmRpt() {
		proRptConfirmCreditList.remove(blocCreditRptConfirmEdit);
		processBlocRptConfirmLayModel = new CustomPageModel<ProcRptBankCreditBlocConfirm>(
				proRptConfirmCreditList, false);
		MessageUtils.addSuccessMessage("bankCreditMsg",
				MessageUtils.getMessage("otherPro_delete_success"));
	}

	/**
	 * 
	 * <p>
	 * Description: 集团资金部确认授信额度维护确认单数据
	 * </p>
	 */
	public void confirmQuest() {
		blocCompanyConfirmEdit.setStatus("3");
		// 更新确认单数据
		entityService.update(blocCompanyConfirmEdit);
		// 更新确认单下的其他授信产品
		bankCreditCompanyConfirmService.batchAddOrUpdateBlocCreditRpt(
				proRptConfirmCreditList, blocCompanyConfirmEdit.getId());
		MessageUtils.addSuccessMessage("bankCreditMsg",
				blocCompanyConfirmEdit.getCompany().getCompanyName()
						+ MessageUtils.getMessage("msg_approve_success"));
	}

	/**
	 * 
	 * <p>
	 * Description: 否决
	 * </p>
	 */
	public void confirmVeto() {
		blocCompanyConfirmEdit.setStatus("5");
		// 更新确认单数据
		entityService.update(blocCompanyConfirmEdit);
		// 更新确认单下的其他授信产品
		bankCreditCompanyConfirmService.batchAddOrUpdateBlocCreditRpt(
				proRptConfirmCreditList, blocCompanyConfirmEdit.getId());
		// huhan add on 8.20
		curentYearGuaranteeTotal(blocCompanyConfirmList, bankCreditBlocView);
		MessageUtils.addSuccessMessage("bankCreditMsg", MessageUtils
				.getMessage("process_applyReject_success",
						blocCompanyConfirmEdit.getCompany().getCompanyName()));
	}

	/**
	 * 
	 * <p>
	 * Description: 申请人确认审批
	 * </p>
	 */
	public String requestDispack() {
		// 判断成员公司是否有未确认状态数据
		if (!blocCompanyConfirmList.isEmpty()) {
			for (ProcBankCreditBlocCompanyConfirm company : blocCompanyConfirmList) {
				if ("2".equals(company.getStatus())) {
					MessageUtils.addErrorMessage("bankCreditMsg", MessageUtils
							.getMessage("errMsg_bankCredit_applyer"));
					transportData();
					return null;
				}
			}
		}
		// huhan add on 8.10
		boolean check = true;
		if (checkInputable("creditStart")
				&& blocConpanyConfirm.getCreditStart() == null) {
			MessageUtils.addErrorMessage(
					"msg",
					MessageUtils.getMessage("txt_pleaseSelect")
							+ MessageUtils.getMessage("lbl_startDate"));
			check = false;
		}
		if (checkInputable("creditEnd")
				&& blocConpanyConfirm.getCreditEnd() == null) {
			MessageUtils.addErrorMessage(
					"msg",
					MessageUtils.getMessage("txt_pleaseSelect")
							+ MessageUtils.getMessage("lbl_endDate"));
			check = false;
		}
		if (!check) {
			return null;
		}
		String upload = ProcessXmlUtil.findStepProperty("id", "BankCreditBloc",
				stepName, "upload");
		if (upload != null && "true".equals(upload)) {
			// 保存流程附件
			this.saveProcessFile(procInstId);
			log.info("保存流程附件！！！");
		}
		// 是否需要向主数据公司授信写入数据
		String confirm = ProcessXmlUtil.findStepProperty("id",
				"BankCreditBloc", stepName, "confirm");
		entityService.update(blocConpanyConfirm);
		// 由于上个审批节点 几个字段为可编辑字段 所以需要更新界面显示数据 add by yan on 2013-07-08
		entityService.update(bankCreditBlocView);
		// 集团资金部审批确认后向主数据公司授信插入数据
		if (confirm != null && "true".equals(confirm)) {

			// 产生公司授信主数据
			for (ProcBankCreditBlocCompanyConfirm creditBlocConfirm : blocCompanyConfirmList) {
				if (!"3".equals(creditBlocConfirm.getStatus())) {
					continue;
				}
				Credit credit = exchangeData(creditBlocConfirm,
						blocConpanyConfirm, bankCreditBlocView);
				Credit presCredit = entityService.create(credit);
				// 产生主数据公司其他授信产品
				List<ProcRptBankCreditBlocConfirm> dataList = bankCreditCompanyConfirmService
						.findBlocRptConfirmByBlocCompany(creditBlocConfirm
								.getId());
				for (ProcRptBankCreditBlocConfirm rpt : dataList) {
					CreditO creditO = new CreditO();
					creditO.setCredit(presCredit);
					creditO.setDefunctInd("N");
					creditO.setOtherName(rpt.getCdProName());
					creditO.setOtherLimit(rpt.getCdProLimit());
					entityService.create(creditO);
				}
			}
		}
		// 确认申请
		bankCreditBlocCompanyService.vwDisposeTask(
				bankCreditBlocView.getProcInstId(), true, stepName + "通过。"
						+ blocConpanyConfirm.getPeMemo());
		MessageUtils.addSuccessMessage(
				"doneMsg",
				stepName
						+ MessageUtils.getMessage(
								"process_bankApprove_success",
								processUtilMapService
										.getTmsIdByFnId(bankCreditBlocView
												.getProcInstId())));
		return processUtilService.getNextPage(getInputPage(), doNext,
				getCurrentIndex(), getCurrentTaskType());
	}

	/**
	 * 
	 * <p>
	 * Description: 状态码转换
	 * </p>
	 * 
	 * @param str
	 * @return
	 */
	public String statusChinse(String str) {
		String mean = "";
		if (str != null && "1".equals(str)) {
			mean = "未确认";
		}
		if (str != null && "2".equals(str)) {
			mean = "待确认";
		}
		if (str != null && "3".equals(str)) {
			mean = "已确认";
		}
		if (str != null && "5".equals(str)) {
			mean = "已否决";
		}
		return mean;
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
	 * 
	 * <p>
	 * Description:将用户输入的数据放入Request对象
	 * </p>
	 */
	private void transportData() {
		JSFUtils.getRequest().setAttribute("validateVo", bankCreditBlocView);
		JSFUtils.getRequest().setAttribute("bolcConfirm", blocConpanyConfirm);
		JSFUtils.getRequest().setAttribute("banchBankSelect", banchBankSelect);
		JSFUtils.getRequest().setAttribute("stepName", stepName);
		JSFUtils.getRequest().setAttribute("blocCompanyList",
				blocCompanyConfirmList);

	}

	/**
	 * 
	 * <p>
	 * Description: 记录用户输入的数据
	 * </p>
	 */
	private void keepUserData() {
		// 实体类数据
		Object obj = JSFUtils.getRequest().getAttribute("validateVo");
		// 节点名称
		Object stepNameReg = JSFUtils.getRequest().getAttribute("stepName");
		Object blocCompanyListReg = JSFUtils.getRequest().getAttribute(
				"blocCompanyList");
		Object bolcConfirmReg = JSFUtils.getRequest().getAttribute(
				"bolcConfirm");
		Object banchBankSelectReg = JSFUtils.getRequest().getAttribute(
				"banchBankSelect");
		// 申请单
		if (obj != null) {
			bankCreditBlocView = (ProcBankCreditBloc) obj;
		}
		// 确认单
		if (bolcConfirmReg != null) {
			blocConpanyConfirm = (ProcBankCreditBlocConfirm) bolcConfirmReg;
		}
		// 保留成員公司信息
		if (blocCompanyListReg != null) {
			blocCompanyConfirmList.clear();
			List<ProcBankCreditBlocCompanyConfirm> blocList = (List<ProcBankCreditBlocCompanyConfirm>) blocCompanyListReg;
			blocCompanyConfirmList.addAll(blocList);
			procBlocCompanyComfirmLayModel = new CustomPageModel<ProcBankCreditBlocCompanyConfirm>(
					blocCompanyConfirmList, false);
		}
		// 初始化需要编辑的字段
		if (stepNameReg != null) {
			stepName = stepNameReg.toString();
			getInputable(stepName);
		}
		// 支行下拉
		if (banchBankSelectReg != null) {
			banchBankSelect = (List<SelectItem>) banchBankSelectReg;
		}
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
	private Credit exchangeData(ProcBankCreditBlocCompanyConfirm bankConfirm,
			ProcBankCreditBlocConfirm procBankCreditCofnirm,
			ProcBankCreditBloc bankCreditBloc) {
		Credit credit = new Credit();
		credit.setBank(bankConfirm.getBank());
		credit.setCompany(bankConfirm.getCompany());
		credit.setCreditStart(procBankCreditCofnirm.getCreditStart());
		credit.setCreditEnd(procBankCreditCofnirm.getCreditEnd());
		credit.setProcInstId(bankConfirm.getProcInstId());
		credit.setCreditLine(bankConfirm.getNotarizeCreditLine());
		credit.setCreditLineCu(bankCreditBloc.getCreditLineCu());

		credit.setGuaranteeCd(bankConfirm.getGuaranteeCd());
		credit.setGuaranteeGr(bankConfirm.getGuaranteeGr());
		credit.setGuaranteeMg(bankConfirm.getGuaranteeMg());
		credit.setGuaranteeOt(bankConfirm.getGuaranteeOt());
		credit.setGuaranteeQm(bankConfirm.getGuaranteeQm());

		credit.setReason(bankConfirm.getCooperationReason());

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

		credit.setDollarFlowFinance(bankConfirm.getDollarFlowFinance());
		credit.setDollarFlowLink(bankConfirm.getDollarFlowLink());
		credit.setDollarFlowPoint(bankConfirm.getDollarFlowPoint());

		credit.setDomesticCred(bankConfirm.getDomesticCred());
		credit.setDomesticCredDf(bankConfirm.getDomesticCredDf());
		credit.setDomesticCredFe(bankConfirm.getDomesticCredFe());
		credit.setDomesticCredGp(bankConfirm.getDomesticCredGp());

		credit.setBussTicket(bankConfirm.getBussTicket());
		credit.setBussTicketDc(bankConfirm.getBussTicketDc());
		credit.setBussTicketFe(bankConfirm.getBussTicketFe());
		credit.setBussTicketGp(bankConfirm.getBussTicketGp());

		credit.setForwTrade(bankConfirm.getForwTrade());
		credit.setForwTradeCr(bankConfirm.getForwTradeCr());
		credit.setStatus("Y");
		return credit;
	}

	/**
	 * 
	 * <p>
	 * Description: 统计本年所有成员公司担保信息
	 * </p>
	 * 
	 * @param companyList
	 */
	private void curentYearGuaranteeTotal(
			List<ProcBankCreditBlocCompanyConfirm> companyConfirmList,
			ProcBankCreditBloc bankCreditBlocView) {
		// 信用
		Double guaranteeCd = 0.0;
		// 抵押
		Double guaranteeMg = 0.0;
		// 担保
		Double guaranteeGr = 0.0;
		// 质押
		Double guaranteeQm = 0.0;
		// 其他
		Double guaranteeOt = 0.0;
		// 本年申请额度(申请阶段计算申请，审批确认后计算审批确认额度)
		Double creditLine = 0.0;
		for (ProcBankCreditBlocCompanyConfirm blocCompany : companyConfirmList) {
			// 已否决的成员公司不参与统计
			if ("5".equals(blocCompany.getStatus())) {
				continue;
			}
			if (blocCompany.getGuaranteeCd() != null) {
				guaranteeCd += blocCompany.getGuaranteeCd();
			}
			if (blocCompany.getGuaranteeMg() != null) {
				guaranteeMg += blocCompany.getGuaranteeMg();
			}
			if (blocCompany.getGuaranteeGr() != null) {
				guaranteeGr += blocCompany.getGuaranteeGr();
			}
			if (blocCompany.getGuaranteeQm() != null) {
				guaranteeQm += blocCompany.getGuaranteeQm();
			}
			if (blocCompany.getGuaranteeOt() != null) {
				guaranteeOt += blocCompany.getGuaranteeOt();
			}
			if (blocCompany.getNotarizeCreditLine() != null) {
				creditLine += blocCompany.getNotarizeCreditLine();
			}
		}
		// huhan modify on 8.20 确认步骤全部显示统计值
		bankCreditBlocView.setGuaranteeOt(guaranteeOt);
		bankCreditBlocView.setGuaranteeCd(guaranteeCd);
		bankCreditBlocView.setGuaranteeGr(guaranteeGr);
		bankCreditBlocView.setGuaranteeMg(guaranteeMg);
		bankCreditBlocView.setGuaranteeQm(guaranteeQm);
		bankCreditBlocView.setCreditLine(creditLine);
	}

	/**
	 * 
	 * <p>
	 * Description: 验证本年授信
	 * </p>
	 * 
	 * @return
	 */
	private boolean validateBlocCompanyCredit() {
		Double Cd = blocCompanyConfirmEdit.getGuaranteeCd();
		Double Gr = blocCompanyConfirmEdit.getGuaranteeGr();
		Double Mg = blocCompanyConfirmEdit.getGuaranteeMg();
		Double Qm = blocCompanyConfirmEdit.getGuaranteeQm();
		Double Ot = blocCompanyConfirmEdit.getGuaranteeOt();
		Double Credit = blocCompanyConfirmEdit.getCreditLine();
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
	 * Description: 验证审批人确认额度与本年担保方式
	 * </p>
	 * 
	 * @return
	 */
	private boolean validateNotarizeCreditLine() {
		Double Cd = blocCompanyConfirmEdit.getGuaranteeCd();
		Double Gr = blocCompanyConfirmEdit.getGuaranteeGr();
		Double Mg = blocCompanyConfirmEdit.getGuaranteeMg();
		Double Qm = blocCompanyConfirmEdit.getGuaranteeQm();
		Double Ot = blocCompanyConfirmEdit.getGuaranteeOt();
		Double Credit = blocCompanyConfirmEdit.getNotarizeCreditLine();
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
					MessageUtils.getMessage("errMsg_assureWay_bigger"));
			return true;
		}
		double tatol = 0.0;
		for (int i = 0; i < 5; i++) {
			if (data[i] != null) {
				tatol += data[i];
			}
		}
		if (Credit > tatol) {
			MessageUtils
					.addErrorMessage("bankCreditMsg", MessageUtils
							.getMessage("errMsg_notarizeCreditLine_larger"));
			return true;
		}
		return false;
	}

	/**
	 * 
	 * <p>
	 * Description: 成员公司授信产品验证
	 * </p>
	 * 
	 * @return
	 */
	private boolean validateCreditProduct() {
		Double liquCred = blocCompanyConfirmEdit.getLiquCred();
		Double bankAcp = blocCompanyConfirmEdit.getBankAcpt();
		Double importCredit = blocCompanyConfirmEdit.getImportCredit();
		Double importFinance = blocCompanyConfirmEdit.getImportFinance();
		Double exportFinance = blocCompanyConfirmEdit.getExportFinance();
		Double dollarFlowFinance = blocCompanyConfirmEdit
				.getDollarFlowFinance();
		Double domesticCred = blocCompanyConfirmEdit.getDomesticCred();
		Double bussTicket = blocCompanyConfirmEdit.getBussTicket();
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
		// 人民币流贷
		if ((!MathUtil.isZeroOrNull(liquCred))
				&& (MathUtil.isZeroOrNull(liquCred)
						|| null == blocCompanyConfirmEdit.getLiquCredAp() || null == blocCompanyConfirmEdit
						.getLiquCredRa())) {
			if (!"1".equals(blocCompanyConfirmEdit.getLiquCredRa())) {
				MessageUtils.addErrorMessage("bankCreditMsgConfirm",
						MessageUtils.getMessage("lbl_liquidityPayment")
								+ MessageUtils.getMessage("errMsg_notAll"));
				return true;
			}

		}
		// 银行承兑汇票
		if ((!MathUtil.isZeroOrNull(bankAcp))
				&& (MathUtil.isZeroOrNull(bankAcp)
						|| null == blocCompanyConfirmEdit.getBankAcptEf()
						|| null == blocCompanyConfirmEdit.getBankAcptFe() || null == blocCompanyConfirmEdit
						.getBankAcptGp())) {
			MessageUtils.addErrorMessage(
					"bankCreditMsgConfirm",
					MessageUtils.getMessage("lbl_bankAcpt")
							+ MessageUtils.getMessage("errMsg_notAll"));
			return true;
		}
		// 进口信用
		if ((!MathUtil.isZeroOrNull(importCredit))
				&& (null == blocCompanyConfirmEdit.getImportCreditFe()
						|| null == blocCompanyConfirmEdit.getImportCreditGp() || MathUtil
							.isZeroOrNull(importCredit))) {
			MessageUtils.addErrorMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_importCred_notAll"));
			return true;
		}
		// 进口押汇
		if ((!MathUtil.isZeroOrNull(importFinance))
				&& (MathUtil.isZeroOrNull(importFinance)
						|| null == blocCompanyConfirmEdit
								.getImportFinanceLink() || null == blocCompanyConfirmEdit
						.getImportFinancePonit())) {
			if (!"3".equals(blocCompanyConfirmEdit.getImportFinanceLink())) {
				MessageUtils.addErrorMessage("bankCreditMsgConfirm",
						MessageUtils.getMessage("lbl_importFinance")
								+ MessageUtils.getMessage("errMsg_notAll"));
				return true;
			}
		}
		// 出口押汇
		if ((!MathUtil.isZeroOrNull(exportFinance))
				&& (MathUtil.isZeroOrNull(exportFinance)
						|| null == blocCompanyConfirmEdit
								.getExportFinanceLink() || null == blocCompanyConfirmEdit
						.getExportFinancePonit())) {
			if (!"3".equals(blocCompanyConfirmEdit.getExportFinanceLink())) {
				MessageUtils.addErrorMessage("bankCreditMsgConfirm",
						MessageUtils.getMessage("lbl_exportFinance")
								+ MessageUtils.getMessage("errMsg_notAll"));
				return true;
			}
		}
		// 美元流代
		if ((!MathUtil.isZeroOrNull(dollarFlowFinance))
				&& (MathUtil.isZeroOrNull(dollarFlowFinance)
						|| null == blocCompanyConfirmEdit.getDollarFlowLink() || null == blocCompanyConfirmEdit
						.getDollarFlowPoint())) {
			if (!"3".equals(blocCompanyConfirmEdit.getDollarFlowLink())) {
				MessageUtils.addErrorMessage("bankCreditMsgConfirm",
						MessageUtils.getMessage("lbl_dollarFlow")
								+ MessageUtils.getMessage("errMsg_notAll"));
				return true;
			}
		}
		// 国内信用证
		if ((!MathUtil.isZeroOrNull(domesticCred))
				&& (MathUtil.isZeroOrNull(domesticCred)
						|| null == blocCompanyConfirmEdit.getDomesticCredDf()
						|| null == blocCompanyConfirmEdit.getDomesticCredFe() || null == blocCompanyConfirmEdit
						.getDomesticCredGp())) {
			MessageUtils.addErrorMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("lbl_nationCredLetter")
							+ MessageUtils.getMessage("errMsg_notAll"));
			return true;
		}
		// 商票保贴
		if ((!MathUtil.isZeroOrNull(bussTicket))
				&& (MathUtil.isZeroOrNull(bussTicket)
						|| null == blocCompanyConfirmEdit.getBussTicketDc()
						|| null == blocCompanyConfirmEdit.getBussTicketFe() || null == blocCompanyConfirmEdit
						.getBussTicketGp())) {
			MessageUtils.addErrorMessage(
					"bankCreditMsg",
					MessageUtils.getMessage("lbl_bussTicket")
							+ MessageUtils.getMessage("errMsg_notAll"));
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

	// *******************页面一系列的验证***************************
	// 本次申请额度不可为空的验证
	public void ifCreditLineCurrent() {
		log.info("111111111111");
		Double Credit = blocCompanyConfirmEdit.getCreditLine();
		log.info(Credit);
		if (Credit == null
				&& "".equals(blocCompanyConfirmEdit.getCreditLine().toString()
						.trim())) {
			MessageUtils.addErrorMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
	}

	// 流动资金贷款额度的验证
	public void ifLiquAmountCred(Double am) {
		// 本年申请额度
		Double Credit = blocCompanyConfirmEdit.getCreditLine();
		log.info("本年申请额度:" + Credit);
		try {
			if (blocCompanyConfirmEdit.getCreditLine() == null
					&& "".equals(blocCompanyConfirmEdit.getCreditLine()
							.toString().trim())) {
				log.info("222222222222222");
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		log.info("流动资金贷款额度:" + am);
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_liquidityPayment_larger"));
			return;
		}
	}

	// 银行承兑汇票额度的验证
	public void ifBankAcpt(Double am) {
		// 本年申请额度
		Double Credit = blocCompanyConfirmEdit.getCreditLine();
		log.info("本年申请额度:" + Credit);
		try {
			if (blocCompanyConfirmEdit.getCreditLine() == null
					&& "".equals(blocCompanyConfirmEdit.getCreditLine()
							.toString().trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_bankAcpt_larger"));
			return;
		}
	}

	// 进口信用证额度的验证
	public void ifImportCredit(Double am) {
		// 本年申请额度
		Double Credit = blocCompanyConfirmEdit.getCreditLine();
		try {
			if (blocCompanyConfirmEdit.getCreditLine() == null
					&& "".equals(blocCompanyConfirmEdit.getCreditLine()
							.toString().trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_importCred_larger"));
			return;
		}
	}

	// 进口押汇额度的验证
	public void ifImportFinance(Double am) {
		// 本年申请额度
		Double Credit = blocCompanyConfirmEdit.getCreditLine();
		try {
			if (blocCompanyConfirmEdit.getCreditLine() == null
					&& "".equals(blocCompanyConfirmEdit.getCreditLine()
							.toString().trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_importFinance_larger"));
			return;
		}
	}

	// 出口押汇额度的验证
	public void ifExportFinance(Double am) {
		// 本年申请额度
		Double Credit = blocCompanyConfirmEdit.getCreditLine();
		try {
			if (blocCompanyConfirmEdit.getCreditLine() == null
					&& "".equals(blocCompanyConfirmEdit.getCreditLine()
							.toString().trim())) {
				log.info("222222222222222");
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_exportFinance_larger"));
			return;
		}
	}

	// 美元流袋额度的验证
	public void ifDollarFlowFinance(Double am) {
		// 本年申请额度
		Double Credit = blocCompanyConfirmEdit.getCreditLine();
		try {
			if (blocCompanyConfirmEdit.getCreditLine() == null
					&& "".equals(blocCompanyConfirmEdit.getCreditLine()
							.toString().trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_dollarFlow_larger"));
			return;
		}
	}

	// 国内信用证额度的验证
	public void ifomesticCred(Double am) {
		// 本年申请额度
		Double Credit = blocCompanyConfirmEdit.getCreditLine();
		try {
			if (blocCompanyConfirmEdit.getCreditLine() == null
					&& "".equals(blocCompanyConfirmEdit.getCreditLine()
							.toString().trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_nationCredLetter_larger"));
			return;
		}
	}

	// 海外代付额度的验证
	public void ifBussTicket(Double am) {
		// 本年申请额度
		Double Credit = blocCompanyConfirmEdit.getCreditLine();
		try {
			if (blocCompanyConfirmEdit.getCreditLine() == null
					&& "".equals(blocCompanyConfirmEdit.getCreditLine()
							.toString().trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			MessageUtils.addErrorMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
		if (!MathUtil.isZeroOrNull(am) && am > Credit) {
			MessageUtils.addWarnMessage("bankCreditMsgConfirm",
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
	 * 得到可输入的审批字段
	 */
	private void getInputable(String stepName) {
		inputableFields = ProcessXmlUtil.getInputableDatas("BankCreditBloc",
				stepName);
	}

	/**
	 * 显示流程步骤详细（不显示“流程终止”）
	 */
	public void displayProcessDetial() {
		Object workclassNumber = JSFUtils.getParamValue("procInstId");
		String workObjNum = String.valueOf(workclassNumber.toString());
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
	 * 显示流程步骤详细
	 */
	private void searchProcessDetail() {
		log.info("查询流程详细步骤");
		log.info("procInstId:" + getProcInstId());
		
		if("true".equals(isPatch)){
			processDetailList = patchMainService.getProcessDetailFor411(procInstId);
    	}else{
    		processDetailList = peManager.getProcessDetail(procInstId);
    	}    
	}

	/**
	 * 风险管理和运维维护确认
	 */
	public String requestMaintain() {
		log.info("风险管理和运维维护确认");
		// 判断成员公司是否有未确认状态数据
		if (!blocCompanyConfirmList.isEmpty()) {
			for (ProcBankCreditBlocCompanyConfirm company : blocCompanyConfirmList) {
				if ("2".equals(company.getStatus())) {
					MessageUtils.addErrorMessage("bankCreditMsg", MessageUtils
							.getMessage("errMsg_bankCredit_applyer"));
					transportData();
					return null;
				}
			}
		}
		String upload = ProcessXmlUtil.findStepProperty("id", "BankCreditBloc",
				stepName, "upload");
		if (upload != null && "true".equals(upload)) {
			// 保存流程附件
			this.saveProcessFile(procInstId);
			log.info("保存流程附件！！！");
		}
		if (stepName != null) {
			entityService.update(blocConpanyConfirm);
		}
		// 加入流程备注抬头
		String memoTitle = "";
		memoTitle = ProcessXmlUtil.findStepProperty("id", "BankCreditBloc",
				stepName, "passMemo");
		log.info("memoTitle" + memoTitle);
		// 确认申请
		bankCreditBlocCompanyService.vwDisposeTask(
				bankCreditBlocView.getProcInstId(), true, memoTitle);
		MessageUtils.addSuccessMessage(
				"doneMsg",
				stepName
						+ MessageUtils.getMessage(
								"process_bankApprove_success",
								processUtilMapService
										.getTmsIdByFnId(bankCreditBlocView
												.getProcInstId())));
		return processUtilService.getNextPage(getInputPage(), doNext,
				getCurrentIndex(), getCurrentTaskType());
	}

	/**
	 * 验证Credit Line Code的长度
	 * 
	 * @return testLength
	 */

	public String lengthTest() {
		String msgs = "";
		String code = blocConpanyConfirm.getCreditLineCode();
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

	// **********************setter\getter方法*************************
	public ProcBankCreditBloc getBankCreditBlocView() {
		return bankCreditBlocView;
	}

	public void setBankCreditBlocView(ProcBankCreditBloc bankCreditBlocView) {
		this.bankCreditBlocView = bankCreditBlocView;
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

	public List<SelectItem> getBanchBankSelect() {
		return banchBankSelect;
	}

	public void setBanchBankSelect(List<SelectItem> banchBankSelect) {
		this.banchBankSelect = banchBankSelect;
	}

	public List<SelectItem> getCurrencySelect() {
		return currencySelect;
	}

	public void setCurrencySelect(List<SelectItem> currencySelect) {
		this.currencySelect = currencySelect;
	}

	public List<SelectItem> getRateHookSelect() {
		return rateHookSelect;
	}

	public void setRateHookSelect(List<SelectItem> rateHookSelect) {
		this.rateHookSelect = rateHookSelect;
	}

	public List<ProcBankCreditBlocCompanyConfirm> getBlocCompanyConfirmList() {
		return blocCompanyConfirmList;
	}

	public void setBlocCompanyConfirmList(
			List<ProcBankCreditBlocCompanyConfirm> blocCompanyConfirmList) {
		this.blocCompanyConfirmList = blocCompanyConfirmList;
	}

	public LazyDataModel<ProcBankCreditBlocCompanyConfirm> getProcBlocCompanyComfirmLayModel() {
		return procBlocCompanyComfirmLayModel;
	}

	public void setProcBlocCompanyComfirmLayModel(
			LazyDataModel<ProcBankCreditBlocCompanyConfirm> procBlocCompanyComfirmLayModel) {
		this.procBlocCompanyComfirmLayModel = procBlocCompanyComfirmLayModel;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public ProcBankCreditBlocCompanyConfirm getBlocCompanyConfirmView() {
		return blocCompanyConfirmView;
	}

	public void setBlocCompanyConfirmView(
			ProcBankCreditBlocCompanyConfirm blocCompanyConfirmView) {
		this.blocCompanyConfirmView = blocCompanyConfirmView;
	}

	public List<ProcRptBankCreditBlocConfirm> getProRptConfirmCreditList() {
		return proRptConfirmCreditList;
	}

	public void setProRptConfirmCreditList(
			List<ProcRptBankCreditBlocConfirm> proRptConfirmCreditList) {
		this.proRptConfirmCreditList = proRptConfirmCreditList;
	}

	public LazyDataModel<ProcRptBankCreditBlocConfirm> getProcessBlocRptConfirmLayModel() {
		return processBlocRptConfirmLayModel;
	}

	public void setProcessBlocRptConfirmLayModel(
			LazyDataModel<ProcRptBankCreditBlocConfirm> processBlocRptConfirmLayModel) {
		this.processBlocRptConfirmLayModel = processBlocRptConfirmLayModel;
	}

	public ProcBankCreditBlocCompanyConfirm getBlocCompanyConfirmEdit() {
		return blocCompanyConfirmEdit;
	}

	public void setBlocCompanyConfirmEdit(
			ProcBankCreditBlocCompanyConfirm blocCompanyConfirmEdit) {
		this.blocCompanyConfirmEdit = blocCompanyConfirmEdit;
	}

	public ProcRptBankCreditBlocConfirm getRptBlocConfirm() {
		return rptBlocConfirm;
	}

	public void setRptBlocConfirm(ProcRptBankCreditBlocConfirm rptBlocConfirm) {
		this.rptBlocConfirm = rptBlocConfirm;
	}

	public ProcRptBankCreditBlocConfirm getBlocCreditRptConfirmEdit() {
		return blocCreditRptConfirmEdit;
	}

	public void setBlocCreditRptConfirmEdit(
			ProcRptBankCreditBlocConfirm blocCreditRptConfirmEdit) {
		this.blocCreditRptConfirmEdit = blocCreditRptConfirmEdit;
	}

	public ProcRptBankCreditBlocConfirm getBlocCreditRptConfirmEditVo() {
		return blocCreditRptConfirmEditVo;
	}

	public void setBlocCreditRptConfirmEditVo(
			ProcRptBankCreditBlocConfirm blocCreditRptConfirmEditVo) {
		this.blocCreditRptConfirmEditVo = blocCreditRptConfirmEditVo;
	}

	public ProcRptBankCreditBlocConfirm getBlocCreditRptConfirmCopy() {
		return blocCreditRptConfirmCopy;
	}

	public void setBlocCreditRptConfirmCopy(
			ProcRptBankCreditBlocConfirm blocCreditRptConfirmCopy) {
		this.blocCreditRptConfirmCopy = blocCreditRptConfirmCopy;
	}

	public String getPrtBlocCreditNameEdit() {
		return prtBlocCreditNameEdit;
	}

	public void setPrtBlocCreditNameEdit(String prtBlocCreditNameEdit) {
		this.prtBlocCreditNameEdit = prtBlocCreditNameEdit;
	}

	public ProcBankCreditBlocConfirm getBlocConpanyConfirm() {
		return blocConpanyConfirm;
	}

	public void setBlocConpanyConfirm(
			ProcBankCreditBlocConfirm blocConpanyConfirm) {
		this.blocConpanyConfirm = blocConpanyConfirm;
	}

	public List<String> getInputableFields() {
		return inputableFields;
	}

	public void setInputableFields(List<String> inputableFields) {
		this.inputableFields = inputableFields;
	}

	public List<ProcessDetailVo> getProcessDetailList() {
		return processDetailList;
	}

	public void setProcessDetailList(List<ProcessDetailVo> processDetailList) {
		this.processDetailList = processDetailList;
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

	public boolean isIfAccessory() {
		return ifAccessory;
	}

	public void setIfAccessory(boolean ifAccessory) {
		this.ifAccessory = ifAccessory;
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
