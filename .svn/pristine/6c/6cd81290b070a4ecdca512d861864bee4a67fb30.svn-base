package com.wcs.tms.view.process.bankcreditbloc;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
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
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.MathUtil;
import com.wcs.base.view.ViewBaseBean;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.Credit;
import com.wcs.tms.model.ProcBankCreditBloc;
import com.wcs.tms.model.ProcBankCreditBlocCompany;
import com.wcs.tms.model.ProcBankCreditBlocConfirm;
import com.wcs.tms.model.ProcRptBankCreditBloc;
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
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

import filenet.vw.api.VWWorkObjectNumber;

/**
 * 
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description: 银行授信申请审批(集团)
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
public class BankCreditBlocBean extends ViewBaseBean<ProcBankCreditBloc> {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(BankCreditBlocBean.class);

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
	/** 公司下拉选择框 */
	private List<SelectItem> companyNameSelect = new ArrayList<SelectItem>();
	/** 银行总行Id */
	private Long topBankId;
	/** 一级机构银行下拉 */
	private List<SelectItem> topLevelSelect = new ArrayList<SelectItem>();
	/** 支行下拉 */
	private List<SelectItem> banchBankSelect = new ArrayList<SelectItem>();
	/** 资金币种下拉 */
	private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
	/** 利率挂钩下拉 */
	private List<SelectItem> rateHookSelect = new ArrayList<SelectItem>();
	/** 查看成员公司详情 */
	private ProcBankCreditBlocCompany processBlocCompanyView = new ProcBankCreditBlocCompany();
	/** 编辑成员公司明细信息 */
	private ProcBankCreditBlocCompany processBlocCompanyEdit = new ProcBankCreditBlocCompany();
	/** 成員公司列表 */
	private List<ProcBankCreditBlocCompany> blocCompanyList = Lists
			.newArrayList();
	/** 集团成员公司列表 */
	private LazyDataModel<ProcBankCreditBlocCompany> procBlocCompanyLayModel;
	/** 审批步骤名称 */
	private String stepName;
	/** 得到审批时可输入的字段 */
	private List<String> inputableFields = new ArrayList<String>();
	/** 银行授信申请集团其他产品 */
	private List<ProcRptBankCreditBloc> proRptCreditList = Lists.newArrayList();
	/** 银行授信申请集团其他产品分页模型 */
	private LazyDataModel<ProcRptBankCreditBloc> processBlocRptLayModel;
	/** 银行授信集团申请其他产品 */
	private ProcRptBankCreditBloc blocCreditRpt = new ProcRptBankCreditBloc();
	/** 银行授信集团申请其他产品编辑 */
	private ProcRptBankCreditBloc blocCreditRptEdit = new ProcRptBankCreditBloc();
	private ProcRptBankCreditBloc blocCreditRptEditVo = new ProcRptBankCreditBloc();
	private ProcRptBankCreditBloc blocCreditRptCopy = new ProcRptBankCreditBloc();
	/** 记录授信其他产品编辑之前的名称 */
	private String prtBlocCreditNameEdit;

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
	private BankService bacnkSerice;
	@Inject
	private CommonBean dictBean;
	@Inject
	private CompanyTmsService companyTmsService;
	@Inject
	private CreditService creditService;
	@Inject
	private BankCreditBlocService bankCreditBlocService;
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
    @Inject
    PEManager peManager;
	// 是否是补丁表中获取到数据查看，处理2014.4.11~2014.4.28之间发生故障，丢失的流程数据和流程审批日志的查看
	private String isPatch;

	public BankCreditBlocBean() {
		// list已提价流程界面,input已处理流程界面
		setupPage("/faces/process/common/processSubed-list.xhtml",
				"/faces/process/common/processDealed-list.xhtml", null);
	}

	@PostConstruct
	public void init() {
		log.info("init()~~~~~~~~~~~~~~~~~~~~~~");
		registerDate = DateUtil.dateToStrShort(DateUtil.getNowDateShort());
		String audit = JSFUtils.getParamValue("op");
		String workclassNumber = JSFUtils.getParamValue("procInstId");
		processBlocCompanyView.setBank(new Bank());
		processBlocCompanyEdit.setBank(new Bank());
		// 是否是审批和查看
		if (audit != null && workclassNumber != null) {
			isPatch = JSFUtils.getParamValue("isPatch");
			initdata(true);
			String wcnum = String.valueOf(workclassNumber);
			blocCompanyList.addAll(bankCreditBlocService
					.findBlocCompanyByProcId(wcnum));
			procBlocCompanyLayModel = new CustomPageModel<ProcBankCreditBlocCompany>(
					blocCompanyList, false);
			ProcBankCreditBloc blocCredit = bankCreditBlocService
					.findBankBlocRegByProcessId(wcnum);
			setProcInstId(wcnum);
			// 初始化支行
			if (blocCredit != null && blocCredit.getBank() != null) {
				banchBankSelect = creditService.findBranchBankSelect(blocCredit
						.getBank().getId());
			}
			this.setInstance(blocCredit);
			// 审批
			if ("approve".equals(audit)) {
				// add on 2013-5-17
				currentIndex = Integer.parseInt(JSFUtils
						.getParamValue("currentIndex"));
				currentTaskType = (String) JSFUtils
						.getParamValue("currentTaskType");

				stepName = JSFUtils.getParamValue("stepName");
				// 得到可以审批时可以修改的字段
				getInputable(stepName);
			}
			// 查看 （若是查已結束的流程 沒有节点名称）
			if ("view".equals(audit)) {
				String obj = JSFUtils.getParamValue("stepName");
				stepName = obj;
			}
			// 该节点几个字段为可编辑，如果改了之后接下来审批就按改完之后值显示（ modified by yan on 2013-7-5）
			if ("集团资金部审批".equals(stepName) && !"view".equals(audit)) {
				curentYearGuaranteeTotal(blocCompanyList);
			}
			searchProcessDetail();
			this.getInstance().setPeMemo("同意");
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
		if ("3".equals(processBlocCompanyEdit.getExportFinanceLink())) {
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
		if ("3".equals(processBlocCompanyEdit.getImportFinanceLink())) {
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
		if ("3".equals(processBlocCompanyEdit.getDollarFlowLink())) {
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
		if ("1".equals(processBlocCompanyEdit.getLiquCredRa())) {
			float1Disable = true;
			float2Disable = true;
			floatFlagDisable = true;
		} else if ("2".equals(processBlocCompanyEdit.getLiquCredRa())) {
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
		List<Credit> creditList = this.creditService.findlastCredit(companyId,
				topBankId);
		Date date = new Date();
		if (!creditList.isEmpty()) {
			for (Credit c : creditList) {
				if (c.getCreditEnd().before(date)) {
					// 设置上年授信额度
					getInstance().setLastCreditLine(c.getCreditLine());
					break;
				}
			}
		} else {
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
		Double Credit = processBlocCompanyEdit.getCreditLine();
		log.info("本年申请额度:" + Credit);
		try {
			if (processBlocCompanyEdit.getCreditLine() == null
					&& "".equals(processBlocCompanyEdit.getCreditLine()
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
	 * 验证其他产品额度不能大于本年剩余申请额度 huhan add
	 * 
	 * @return true/false
	 */
	private Boolean validationCdProLimit() {
		Boolean notPass = false;
		Double creditLine = processBlocCompanyEdit.getCreditLine(); // 本年申请额度
		Double cdProLimit = this.blocCreditRpt.getCdProLimit(); // 其它授信产品额度
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
		Double Credit = processBlocCompanyEdit.getCreditLine();
		log.info("本年申请额度:" + Credit);
		try {
			if (processBlocCompanyEdit.getCreditLine() == null
					&& "".equals(processBlocCompanyEdit.getCreditLine()
							.toString().trim())) {
				// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			// 请先输入本次申请额度
			MessageUtils.addErrorMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
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
	 * Description: 删除其他产品
	 * </p>
	 */
	public void deleteProcBlocRptCredit() {
		try {
			proRptCreditList.remove(blocCreditRptEdit);
			processBlocRptLayModel = new CustomPageModel<ProcRptBankCreditBloc>(
					proRptCreditList, false);
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("otherPro_delete_success"));
		} catch (Exception e) {
			log.error("deleteProcBlocRptCredit方法 删除其他产品 出现异常：", e);
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 银行授信集团发起新流程
	 * </p>
	 * 
	 * @return
	 */
	public String blocCreditApply() {
		try {
			// 设置银行
			Bank bank = entityService.find(Bank.class, topBankId);
			getInstance().setBank(bank);
			// 设置公司
			Company company = this.entityService.find(Company.class, companyId);
			getInstance().setCompany(company);
			// 流程名字
			String workflowClassName = ProcessXmlUtil.getProcessAttribute("id",
					"BankCreditBloc", "className");
			// 发起新流程得到流程编号
			String workClassNumber = bankCreditBlocService
					.vwCreateProcessInstance(workflowClassName,
							"TMS_Cop_Fund_Dep_1", "集团资金部申请");
			getInstance().setProcInstId(workClassNumber);
			getInstance().setApplyDate(
					DateUtil.strToDate(registerDate, "yyyy-MM-dd").toDate());
			ProcBankCreditBloc creditBloc = entityService.create(getInstance());
			// 生成流程实例编号映射9.11(父流程)
			processUtilMapService.generateProcessMap(workClassNumber,
					"BPM_RA_002", company);
			// 得到所有公司
			List<Company> companyList = bankCreditBlocService.findAllCompany();
			// 发起工厂子流程
			if (!companyList.isEmpty()) {
				for (Company c : companyList) {
					ProcBankCreditBlocCompany bloCompany = new ProcBankCreditBlocCompany();
					bloCompany.setCompany(c);
					// 创建公司申请流程
					String banchworkflowClassName = ProcessXmlUtil
							.getProcessAttribute("id", "BankCreditBlocBanch",
									"className");
					// 发起子流程得到流程编号
					String workClassNumberBanch = bankCreditBlocService
							.vwCreateProcessInstance(banchworkflowClassName,
									"TMS_Cop_Fund_Dep_1", "集团发起");
					bloCompany.setProcInstId(workClassNumberBanch);
					bloCompany.setProcBankCreditBloc(creditBloc);
					// 成员公司币别和集团币别一致
					bloCompany.setCreditLineCu(getInstance().getCreditLineCu());
					entityService.create(bloCompany);
					// 生成流程实例编号映射9.11(子流程)
					processUtilMapService.generateProcessMap(
							workClassNumberBanch, "BPM_RA_002_01", c);
				}
			}
			MessageUtils.addSuccessMessage("doneMsg", MessageUtils.getMessage(
					"process_new_success",
					processUtilMapService.getTmsIdByFnId(workClassNumber)));
			return getListPage();
		} catch (Exception e) {
			log.error("blocCreditApply方法 银行授信集团发起新流程 出现异常：", e);
			MessageUtils.addErrorMessage("msg", "发起流程失败");
			return null;
		}

	}

	/**
	 * 
	 * <p>
	 * Description: 确认成员公司申请
	 * </p>
	 */
	public void confirmRequest() {
		// 成员公司确认授信产品验证
		boolean flag = validateCreditProduct();
		if (flag) {
			return;
		}

		if (stepName != null && stepName.equals("集团资金部确认授信额度")) {
			processBlocCompanyEdit.setStatus("3");
		} else {
			processBlocCompanyEdit.setStatus("2");
		}
		// 更新成员公司下的其他授信产品
		bankCreditBlocCompanyService.batchAddOrUpdateBlocCreditRpt(
				proRptCreditList, processBlocCompanyEdit.getId());
		// 更新成员公司信息
		entityService.update(processBlocCompanyEdit);
		// 集团确认后邮件通知
		StringBuilder bulder = new StringBuilder();
		bulder.append(getInstance().getCompany().getCompanyName()).append("确认")
				.append(processBlocCompanyEdit.getCompany().getCompanyName())
				.append("授信申请，特此告知");

		curentYearGuaranteeTotal(blocCompanyList);
		MessageUtils.addSuccessMessage("bankCreditMsg", MessageUtils
				.getMessage("process_applyConfirm_success",
						processBlocCompanyEdit.getCompany().getCompanyName()));

		bankCreditBlocService.mailNoticeBlocCompany(
				processBlocCompanyEdit.getCreatedBy(), stepName,
				"TMS_BankCreditBloc", processBlocCompanyEdit.getProcInstId(),
				bulder.toString());
	}

	/**
	 * 
	 * <p>
	 * Description:否决成员公司申请(成员公司列表)
	 * </p>
	 */
	public void rejectRequest() {
		processBlocCompanyView.setStatus("5");
		bankCreditBlocService
				.bankCreditBlocRejectRequest(processBlocCompanyView);
		// 集团确认后邮件通知
		StringBuilder bulder = new StringBuilder();
		bulder.append(getInstance().getCompany().getCompanyName()).append("否决")
				.append(processBlocCompanyView.getCompany().getCompanyName())
				.append("授信申请，特此告知");
		// 否决之后重新计算本年申请额度以及担保方式额度
		curentYearGuaranteeTotal(blocCompanyList);
		MessageUtils.addSuccessMessage("bankCreditMsg", MessageUtils
				.getMessage("process_applyReject_success",
						processBlocCompanyView.getCompany().getCompanyName()));

		bankCreditBlocService.mailNoticeBlocCompany(
				processBlocCompanyView.getCreatedBy(), stepName,
				"TMS_BankCreditBloc", processBlocCompanyView.getProcInstId(),
				bulder.toString());
	}

	/**
	 * 
	 * <p>
	 * Description: 否决成员公司
	 * </p>
	 */
	public void rejectCompanyRequest() {
		// 成员公司确认授信产品验证
		boolean flag = validateCreditProduct();
		if (flag) {
			return;
		}
		processBlocCompanyEdit.setStatus("5");
		entityService.update(processBlocCompanyEdit);
		// 集团确认后邮件通知
		StringBuilder bulder = new StringBuilder();
		bulder.append(getInstance().getCompany().getCompanyName()).append("否决")
				.append(processBlocCompanyEdit.getCompany().getCompanyName())
				.append("授信申请，特此告知");
		// 否决之后重新计算本年申请额度以及担保方式额度
		curentYearGuaranteeTotal(blocCompanyList);
		MessageUtils.addSuccessMessage("bankCreditMsg", MessageUtils
				.getMessage("process_applyReject_success",
						processBlocCompanyEdit.getCompany().getCompanyName()));

		bankCreditBlocService.mailNoticeBlocCompany(
				processBlocCompanyEdit.getCreatedBy(), stepName,
				"TMS_BankCreditBloc", processBlocCompanyEdit.getProcInstId(),
				bulder.toString());
	}

	/**
	 * 
	 * <p>
	 * Description: 查看成員公司信息
	 * </p>
	 */
	public void viewCompany() {
		// 设置其他授信产品
		List<ProcRptBankCreditBloc> dataList = bankCreditBlocCompanyService
				.findBlocRptByBlocCom(processBlocCompanyView.getId());
		proRptCreditList.clear();
		proRptCreditList.addAll(dataList);
		processBlocRptLayModel = new CustomPageModel<ProcRptBankCreditBloc>(
				proRptCreditList, false);

	}

	/**
	 * 
	 * <p>
	 * Description: 确认成员公司信息查询其他授信产品
	 * </p>
	 */
	public void confirmCompany() {
		// 设置其他授信产品
		List<ProcRptBankCreditBloc> dataList = bankCreditBlocCompanyService
				.findBlocRptByBlocCom(processBlocCompanyEdit.getId());
		proRptCreditList.clear();
		proRptCreditList.addAll(dataList);
		processBlocRptLayModel = new CustomPageModel<ProcRptBankCreditBloc>(
				proRptCreditList, false);
	}

	/**
	 * 
	 * <p>
	 * Description: 集团确认申请
	 * </p>
	 */
	public String blocConfirmRequest() {
		try {
			// 判断成员公司是否有未确认状态数据
			if (!blocCompanyList.isEmpty()) {
				for (ProcBankCreditBlocCompany company : blocCompanyList) {
					if ("1".equals(company.getStatus())) {
						MessageUtils
								.addErrorMessage(
										"bankCreditMsg",
										MessageUtils
												.getMessage("errMsg_bankCredit_applyer"));
						transportData();
						return null;
					}
				}
			}
			// huhan 8.20 判断成员公司是否全部为"已否决"状态数据
			if (!blocCompanyList.isEmpty()) {
				boolean reject = true;
				for (ProcBankCreditBlocCompany company : blocCompanyList) {
					if (!"5".equals(company.getStatus())) {
						reject = false;
					}
				}
				if (reject) {
					MessageUtils.addErrorMessage("bankCreditMsg", MessageUtils
							.getMessage("errMsg_bankCredit_applyer1"));
					transportData();
					return null;
				}
			} else {
				MessageUtils.addErrorMessage("bankCreditMsg",
						MessageUtils.getMessage("errMsg_bankCredit_applyer2"));
				transportData();
				return null;
			}

			String confirm = ProcessXmlUtil.findStepProperty("id",
					"BankCreditBloc", stepName, "confirm");
			// 生成确认单数据
			if (confirm != null && "true".equals(confirm)) {
				boolean blocflag = bankCreditBlocCompanyService
						.isExistBlocConfirmByblocId(getInstance().getId());
				if (!blocflag) {
					// 集团资金部门经理审批 产生集团确认单
					ProcBankCreditBlocConfirm blocConfirm = new ProcBankCreditBlocConfirm();
					blocConfirm.setProcBankCreditBloc(getInstance());
					blocConfirm.setBank(getInstance().getBank());
					blocConfirm.setCompany(getInstance().getCompany());
					blocConfirm.setCreditLimit(getInstance().getCreditLine());
					// 产生公司确认单数据
					ProcBankCreditBlocConfirm blocConfirmEntity = entityService
							.create(blocConfirm);
					bankCreditBlocCompanyService.batchCreateBlocCompanyConfirm(
							blocCompanyList, blocConfirmEntity, getInstance()
									.getProcInstId());
				}
			}
			entityService.update(getInstance());
			// 更新成员公司信息
			bankCreditBlocCompanyService
					.batchUpdateBlocCompany(blocCompanyList);
			// 确认申请
			bankCreditBlocCompanyService.vwDisposeTask(getInstance()
					.getProcInstId(), true, stepName + "确认。"
					+ getInstance().getPeMemo());
			MessageUtils.addSuccessMessage("doneMsg", MessageUtils.getMessage(
					"process_bankApprove_success_1", stepName,
					processUtilMapService.getTmsIdByFnId(getInstance()
							.getProcInstId())));
		} catch (Exception e) {
			log.error("blocConfirmRequest方法 集团确认申请 出现异常：", e);
		}
		return processUtilService.getNextPage(getInputPage(), doNext,
				currentIndex, currentTaskType);
	}

	/**
	 * 
	 * <p>
	 * Description: 集团资金部经理审批时否决
	 * </p>
	 * 
	 * @return
	 */
	public String blocConfirmReject() {
		boolean flag = validateCredit();
		if (flag) {
			transportData();
			// 设置其他授信产品
			return null;
		}
		// huhan add on 8.10
		if (getInstance().getPeMemo() == null
				|| "".equals(getInstance().getPeMemo())) {
			MessageUtils.addErrorMessage("bankCreditMsg",
					MessageUtils.getMessage("msg_input_approveRemark"));
			return null;
		}

		bankCreditBlocCompanyService.vwDisposeTask(getInstance()
				.getProcInstId(), false, stepName + "否决。"
				+ getInstance().getPeMemo());
		MessageUtils.addSuccessMessage("doneMsg", MessageUtils.getMessage(
				"process_bankApprove_success_2", stepName,
				processUtilMapService.getTmsIdByFnId(getInstance()
						.getProcInstId())));
		return processUtilService.getNextPage(getInputPage(), doNext,
				currentIndex, currentTaskType);
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
	 * 
	 * <p>
	 * Description:将用户输入的数据放入Request对象
	 * </p>
	 */
	private void transportData() {
		JSFUtils.getRequest().setAttribute("validateVo", getInstance());
		JSFUtils.getRequest().setAttribute("stepName", stepName);
		JSFUtils.getRequest().setAttribute("blocCompanyList", blocCompanyList);

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
		if (obj != null) {
			this.setInstance((ProcBankCreditBloc) obj);
		}
		// 保留成員公司信息
		if (blocCompanyListReg != null) {
			blocCompanyList.clear();
			List<ProcBankCreditBlocCompany> blocList = (List<ProcBankCreditBlocCompany>) blocCompanyListReg;
			blocCompanyList.addAll(blocList);
			curentYearGuaranteeTotal(blocCompanyList);
			procBlocCompanyLayModel = new CustomPageModel<ProcBankCreditBlocCompany>(
					blocCompanyList, false);
		}
		// 初始化需要编辑的字段
		if (stepNameReg != null) {
			stepName = stepNameReg.toString();
			getInputable(stepNameReg.toString());
		}
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
	 * 初始化下拉
	 */
	private void initdata(boolean isAll) {
		if (isAll) {
			companyNameSelect = companyTmsService.getAllCompanySelect();
		} else {
			companyNameSelect = companyTmsService.getBlocCompanySelect();
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
	 * Description: 验证本年授信
	 * </p>
	 * 
	 * @return
	 */
	private boolean validateBlocCompanyCredit() {
		Double Cd = processBlocCompanyEdit.getGuaranteeCd();
		Double Gr = processBlocCompanyEdit.getGuaranteeGr();
		Double Mg = processBlocCompanyEdit.getGuaranteeMg();
		Double Qm = processBlocCompanyEdit.getGuaranteeQm();
		Double Ot = processBlocCompanyEdit.getGuaranteeOt();
		Double Credit = processBlocCompanyEdit.getCreditLine();
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
		Double Cd = processBlocCompanyEdit.getGuaranteeCd();
		Double Gr = processBlocCompanyEdit.getGuaranteeGr();
		Double Mg = processBlocCompanyEdit.getGuaranteeMg();
		Double Qm = processBlocCompanyEdit.getGuaranteeQm();
		Double Ot = processBlocCompanyEdit.getGuaranteeOt();
		Double Credit = processBlocCompanyEdit.getCreditLine();
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
	 * Description: 成员公司授信产品验证
	 * </p>
	 * 
	 * @return
	 */
	private boolean validateCreditProduct() {
		Double liquCred = processBlocCompanyEdit.getLiquCred();
		Double bankAcp = processBlocCompanyEdit.getBankAcpt();
		Double importCredit = processBlocCompanyEdit.getImportCredit();
		Double importFinance = processBlocCompanyEdit.getImportFinance();
		Double exportFinance = processBlocCompanyEdit.getExportFinance();
		Double dollarFlowFinance = processBlocCompanyEdit
				.getDollarFlowFinance();
		Double domesticCred = processBlocCompanyEdit.getDomesticCred();
		Double bussTicket = processBlocCompanyEdit.getBussTicket();
		Double forwTrade = processBlocCompanyEdit.getForwTrade();
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
						|| null == processBlocCompanyEdit.getLiquCredAp() || null == processBlocCompanyEdit
						.getLiquCredRa())) {
			if (!"1".equals(processBlocCompanyEdit.getLiquCredRa())) {
				MessageUtils.addErrorMessage("bankCreditMsgConfirm",
						MessageUtils.getMessage("lbl_liquidityPayment")
								+ MessageUtils.getMessage("errMsg_notAll"));
				return true;
			}
		}
		// 银行承兑汇票
		if ((!MathUtil.isZeroOrNull(bankAcp))
				&& (MathUtil.isZeroOrNull(bankAcp)
						|| null == processBlocCompanyEdit.getBankAcptEf()
						|| null == processBlocCompanyEdit.getBankAcptFe() || null == processBlocCompanyEdit
						.getBankAcptGp())) {
			MessageUtils.addErrorMessage(
					"bankCreditMsgConfirm",
					MessageUtils.getMessage("lbl_bankAcpt")
							+ MessageUtils.getMessage("errMsg_notAll"));
			return true;
		}
		// 进口信用
		if ((!MathUtil.isZeroOrNull(importCredit))
				&& (null == processBlocCompanyEdit.getImportCreditFe()
						|| null == processBlocCompanyEdit.getImportCreditGp() || MathUtil
							.isZeroOrNull(importCredit))) {
			MessageUtils.addErrorMessage(
					"bankCreditMsgConfirm",
					MessageUtils.getMessage("lbl_importCredit")
							+ MessageUtils.getMessage("errMsg_notAll"));
			return true;
		}
		// 进口押汇
		if ((!MathUtil.isZeroOrNull(importFinance))
				&& (MathUtil.isZeroOrNull(importFinance)
						|| null == processBlocCompanyEdit
								.getImportFinanceLink() || null == processBlocCompanyEdit
						.getImportFinancePonit())) {
			if (!"3".equals(processBlocCompanyEdit.getImportFinanceLink())) {
				MessageUtils.addErrorMessage("bankCreditMsgConfirm",
						MessageUtils.getMessage("lbl_importFinance")
								+ MessageUtils.getMessage("errMsg_notAll"));
				return true;
			}
		}
		// 出口押汇
		if ((!MathUtil.isZeroOrNull(exportFinance))
				&& (MathUtil.isZeroOrNull(exportFinance)
						|| null == processBlocCompanyEdit
								.getExportFinanceLink() || null == processBlocCompanyEdit
						.getExportFinancePonit())) {
			if (!"3".equals(processBlocCompanyEdit.getExportFinanceLink())) {
				MessageUtils.addErrorMessage("bankCreditMsgConfirm",
						MessageUtils.getMessage("lbl_exportFinance")
								+ MessageUtils.getMessage("errMsg_notAll"));
				return true;
			}
		}
		// 美元流代
		if ((!MathUtil.isZeroOrNull(dollarFlowFinance))
				&& (MathUtil.isZeroOrNull(dollarFlowFinance)
						|| null == processBlocCompanyEdit.getDollarFlowLink() || null == processBlocCompanyEdit
						.getDollarFlowPoint())) {
			// modified by yanchangjing on 2012-9-25
			if (!"3".equals(processBlocCompanyEdit.getDollarFlowLink())) {
				MessageUtils.addErrorMessage("bankCreditMsgConfirm",
						MessageUtils.getMessage("lbl_dollarFlow")
								+ MessageUtils.getMessage("errMsg_notAll"));
				return true;
			}
		}
		// 国内信用证
		if ((!MathUtil.isZeroOrNull(domesticCred))
				&& (MathUtil.isZeroOrNull(domesticCred)
						|| null == processBlocCompanyEdit.getDomesticCredDf()
						|| null == processBlocCompanyEdit.getDomesticCredFe() || null == processBlocCompanyEdit
						.getDomesticCredGp())) {
			MessageUtils.addErrorMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("lbl_nationCredLetter")
							+ MessageUtils.getMessage("errMsg_notAll"));
			return true;
		}
		// 海外代付
		if ((!MathUtil.isZeroOrNull(bussTicket))
				&& (MathUtil.isZeroOrNull(bussTicket)
						|| null == processBlocCompanyEdit.getBussTicketDc()
						|| null == processBlocCompanyEdit.getBussTicketFe() || null == processBlocCompanyEdit
						.getBussTicketGp())) {
			MessageUtils.addErrorMessage(
					"bankCreditMsgConfirm",
					MessageUtils.getMessage("lbl_bussTicket")
							+ MessageUtils.getMessage("errMsg_notAll"));
			return true;
		}
		if ((!MathUtil.isZeroOrNull(forwTrade))
				&& (MathUtil.isZeroOrNull(forwTrade) || null == processBlocCompanyEdit
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
		Double Credit = processBlocCompanyEdit.getCreditLine();
		log.info(Credit);
		if (Credit == null
				&& "".equals(processBlocCompanyEdit.getCreditLine().toString()
						.trim())) {
			MessageUtils.addErrorMessage("bankCreditMsgConfirm",
					MessageUtils.getMessage("errMsg_addApplyLimitFirst"));
			return;
		}
	}

	// 流动资金贷款额度的验证
	public void ifLiquAmountCred(Double am) {
		// 本年申请额度
		Double Credit = processBlocCompanyEdit.getCreditLine();
		log.info("本年申请额度:" + Credit);
		try {
			if (processBlocCompanyEdit.getCreditLine() == null
					&& "".equals(processBlocCompanyEdit.getCreditLine()
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
		Double Credit = processBlocCompanyEdit.getCreditLine();
		log.info("本年申请额度:" + Credit);
		try {
			if (processBlocCompanyEdit.getCreditLine() == null
					&& "".equals(processBlocCompanyEdit.getCreditLine()
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
		Double Credit = processBlocCompanyEdit.getCreditLine();
		try {
			if (processBlocCompanyEdit.getCreditLine() == null
					&& "".equals(processBlocCompanyEdit.getCreditLine()
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
		Double Credit = processBlocCompanyEdit.getCreditLine();
		try {
			if (processBlocCompanyEdit.getCreditLine() == null
					&& "".equals(processBlocCompanyEdit.getCreditLine()
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
		Double Credit = processBlocCompanyEdit.getCreditLine();
		try {
			if (processBlocCompanyEdit.getCreditLine() == null
					&& "".equals(processBlocCompanyEdit.getCreditLine()
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
		Double Credit = processBlocCompanyEdit.getCreditLine();
		try {
			if (processBlocCompanyEdit.getCreditLine() == null
					&& "".equals(processBlocCompanyEdit.getCreditLine()
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
		Double Credit = processBlocCompanyEdit.getCreditLine();
		try {
			if (processBlocCompanyEdit.getCreditLine() == null
					&& "".equals(processBlocCompanyEdit.getCreditLine()
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
		Double Credit = processBlocCompanyEdit.getCreditLine();
		try {
			if (processBlocCompanyEdit.getCreditLine() == null
					&& "".equals(processBlocCompanyEdit.getCreditLine()
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
	 * 
	 * <p>
	 * Description: 统计本年所有成员公司担保信息
	 * </p>
	 * 
	 * @param companyList
	 */
	private void curentYearGuaranteeTotal(
			List<ProcBankCreditBlocCompany> companyList) {
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
		if (companyList.isEmpty()) {
			return;
		}
		for (ProcBankCreditBlocCompany blocCompany : companyList) {
			// 已否决的待确认的成员公司不参与统计
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
			if (blocCompany.getCreditLine() != null) {
				creditLine += blocCompany.getCreditLine();
			}
		}
		getInstance().setGuaranteeOt(guaranteeOt);
		getInstance().setGuaranteeCd(guaranteeCd);
		getInstance().setGuaranteeGr(guaranteeGr);
		getInstance().setGuaranteeMg(guaranteeMg);
		getInstance().setGuaranteeQm(guaranteeQm);
		getInstance().setCreditLine(creditLine);
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
		inputableFields = ProcessXmlUtil.getInputableDatas("BankCreditBloc",
				stepName);
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
	 * 当担保方式为一种时，其值根据批复额度自动增减
	 */
	public void ajaxAssureWay() {
		// 信用
		if (!MathUtil.isZeroOrNull(processBlocCompanyEdit.getGuaranteeCd())
				&& processBlocCompanyEdit.getGuaranteeCd() > 0
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeMg())
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeGr())
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeQm())
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeOt())) {
			processBlocCompanyEdit.setGuaranteeCd(processBlocCompanyEdit
					.getNotarizeCreditLine());
		}
		// 抵押
		if (!MathUtil.isZeroOrNull(processBlocCompanyEdit.getGuaranteeMg())
				&& processBlocCompanyEdit.getGuaranteeMg() > 0
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeCd())
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeGr())
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeQm())
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeOt())) {
			processBlocCompanyEdit.setGuaranteeMg(processBlocCompanyEdit
					.getNotarizeCreditLine());
		}
		// 担保
		if (!MathUtil.isZeroOrNull(processBlocCompanyEdit.getGuaranteeGr())
				&& processBlocCompanyEdit.getGuaranteeGr() > 0
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeMg())
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeCd())
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeQm())
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeOt())) {
			processBlocCompanyEdit.setGuaranteeGr(processBlocCompanyEdit
					.getNotarizeCreditLine());
		}
		// 质押
		if (!MathUtil.isZeroOrNull(processBlocCompanyEdit.getGuaranteeQm())
				&& processBlocCompanyEdit.getGuaranteeQm() > 0
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeMg())
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeGr())
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeCd())
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeOt())) {
			processBlocCompanyEdit.setGuaranteeQm(processBlocCompanyEdit
					.getNotarizeCreditLine());
		}
		// 其他
		if (!MathUtil.isZeroOrNull(processBlocCompanyEdit.getGuaranteeOt())
				&& processBlocCompanyEdit.getGuaranteeOt() > 0
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeMg())
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeGr())
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeQm())
				&& MathUtil.isZeroOrNull(processBlocCompanyEdit
						.getGuaranteeCd())) {
			processBlocCompanyEdit.setGuaranteeOt(processBlocCompanyEdit
					.getNotarizeCreditLine());
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

	// ****************setter、getter方法**************************
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

	public List<SelectItem> getCurrencySelect() {
		return currencySelect;
	}

	public void setCurrencySelect(List<SelectItem> currencySelect) {
		this.currencySelect = currencySelect;
	}

	public LazyDataModel<ProcBankCreditBlocCompany> getProcBlocCompanyLayModel() {
		return procBlocCompanyLayModel;
	}

	public void setProcBlocCompanyLayModel(
			LazyDataModel<ProcBankCreditBlocCompany> procBlocCompanyLayModel) {
		this.procBlocCompanyLayModel = procBlocCompanyLayModel;
	}

	public ProcBankCreditBlocCompany getProcessBlocCompanyView() {
		return processBlocCompanyView;
	}

	public void setProcessBlocCompanyView(
			ProcBankCreditBlocCompany processBlocCompanyView) {
		this.processBlocCompanyView = processBlocCompanyView;
	}

	public List<SelectItem> getRateHookSelect() {
		return rateHookSelect;
	}

	public void setRateHookSelect(List<SelectItem> rateHookSelect) {
		this.rateHookSelect = rateHookSelect;
	}

	public List<SelectItem> getBanchBankSelect() {
		return banchBankSelect;
	}

	public void setBanchBankSelect(List<SelectItem> banchBankSelect) {
		this.banchBankSelect = banchBankSelect;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
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

	public ProcBankCreditBlocCompany getProcessBlocCompanyEdit() {
		return processBlocCompanyEdit;
	}

	public void setProcessBlocCompanyEdit(
			ProcBankCreditBlocCompany processBlocCompanyEdit) {
		this.processBlocCompanyEdit = processBlocCompanyEdit;
	}

	public ProcRptBankCreditBloc getBlocCreditRpt() {
		return blocCreditRpt;
	}

	public void setBlocCreditRpt(ProcRptBankCreditBloc blocCreditRpt) {
		this.blocCreditRpt = blocCreditRpt;
	}

	public ProcRptBankCreditBloc getBlocCreditRptEdit() {
		return blocCreditRptEdit;
	}

	public void setBlocCreditRptEdit(ProcRptBankCreditBloc blocCreditRptEdit) {
		this.blocCreditRptEdit = blocCreditRptEdit;
	}

	public ProcRptBankCreditBloc getBlocCreditRptEditVo() {
		return blocCreditRptEditVo;
	}

	public void setBlocCreditRptEditVo(ProcRptBankCreditBloc blocCreditRptEditVo) {
		this.blocCreditRptEditVo = blocCreditRptEditVo;
	}

	public ProcRptBankCreditBloc getBlocCreditRptCopy() {
		return blocCreditRptCopy;
	}

	public void setBlocCreditRptCopy(ProcRptBankCreditBloc blocCreditRptCopy) {
		this.blocCreditRptCopy = blocCreditRptCopy;
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
