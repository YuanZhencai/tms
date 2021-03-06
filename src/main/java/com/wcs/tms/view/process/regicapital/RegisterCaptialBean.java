package com.wcs.tms.view.process.regicapital;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.JSFUtils;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcRegiCapital;
import com.wcs.tms.model.RemittanceLineAccount;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessUtilService;
import com.wcs.tms.service.process.common.ProcessWaitAcceptService;
import com.wcs.tms.service.process.common.patch.service.PatchMainService;
import com.wcs.tms.service.process.regicapital.RegisterCaptialService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.service.system.company.ShareHolderService;
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
 * Description: 注册资本金Bean
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
public class RegisterCaptialBean extends FileUpload<ProcRegiCapital> {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(RegisterCaptialBean.class);

	/*** 选择汇款线路* */
	private List<RemittanceLineAccount> acNoList;
	/** * 被选中的汇款线路* */
	private RemittanceLineAccount selAcNo;
	/*** 新增汇款线路* */
	private RemittanceLineAccount addAcNo = new RemittanceLineAccount();
	/*** 新增资金提供方名称 **/
	private String zjtgfShareHolderName = "";

	/** 填表日期 */
	private String registerDate;
	/** 公司英文名称 */
	private String companyNameEn;
	/** 公司中午名称下拉选择框 */
	private List<SelectItem> companyNameSelect = new ArrayList<SelectItem>();
	/** 选择的公司Id */
	private Long companyId;
	/** 审批页面标题 */
	private String title;
	/** 内嵌股东信息分页模型 */
	private LazyDataModel<ShareHolder> shareHolderModel;
	/** 流程详细List数据 */
	private List<ProcessDetailVo> processDetailList = new ArrayList<ProcessDetailVo>();
	/** 资金币种下拉 */
	private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
	/** 付款方下拉 */
	private List<SelectItem> shareHolderSelect = new ArrayList<SelectItem>();
	/** 用于查看审批详细步骤，附件时用的全局参数 */
	private String workObjNum;
	/** 是否显示详细步骤表格判断 */
	private boolean showDetialStep;
	/** 得到可输入的审批字段 */
	private List<String> inputableFields = new ArrayList<String>();

	// 是否处理下一个任务9.3
	private boolean doNext = true;
	// 当前所处理任务在任务列表中的位置
	private Integer currentIndex;
	// 当前处理任务类型（已接受和待接收）
	private String currentTaskType;

	final Logger logger = LoggerFactory.getLogger(RegisterCaptialBean.class);

	@Inject
	private CompanyTmsService companyTmsService;
	@Inject
	private RegisterCaptialService registerCaptialService;
	@Inject
	private ProcessWaitAcceptService processWaitService;
	@Inject
	private CommonBean dictBean;
	@Inject
	private ShareHolderService shareHolderService;
	@Inject
	ProcessUtilService processUtilService;// 9.3
	@Inject
	ProcessUtilMapService processUtilMapService;// 9.10
	@Inject
	PatchMainService patchMainService;

	@Inject
	PEManager peManager;

	// 是否是补丁表中获取到数据查看，处理2014.4.11~2014.4.28之间发生故障，丢失的流程数据和流程审批日志的查看
	private String isPatch;

	public RegisterCaptialBean() {
		// list已提价流程界面,input已处理流程界面
		this.setupPage("/faces/process/common/processSubed-list.xhtml",
				"/faces/process/common/processDealed-list.xhtml", null);
	}

	@PostConstruct
	public void init() {
		String audit = JSFUtils.getParamValue("op");
		String workclassNumber = JSFUtils.getParamValue("procInstId");
		// 是否是审批和查看
		if (audit != null && workclassNumber != null) {
			String flag = audit;
			String wcnum = workclassNumber;
			this.workObjNum = wcnum;
			isPatch = JSFUtils.getParamValue("isPatch");
			if ("approve".equals(flag)) {
				// add on 2013-5-17
				currentIndex = Integer.parseInt(JSFUtils
						.getParamValue("currentIndex"));
				currentTaskType = (String) JSFUtils
						.getParamValue("currentTaskType");

				title = JSFUtils.getParamValue("stepName");
				getInputable(title);
			}
			displayProcessDetail();
			displayDetailAttach(workObjNum);
			// 查询注册资本金对象
			fillData(wcnum, flag);
			initdata(true);
			registerDate = DateUtil.dateToStrShort(DateUtil
					.dateToDateTime(getInstance().getApplyDate()));
			if (!"申请".equals(title)) {
				getInstance().setPeMemo("同意");
			}
		} else {
			// 注册时清空全局参数
			workObjNum = null;
			title = null;
			registerDate = DateUtil.dateToStrShort(DateUtil.getNowDateShort());
			initdata(false);
		}
	}

	/** *股东变化修改资金提供方* */
	public void ajaxPayer(){
		if (getInstance().getPayer() != null) {

			ShareHolder shareHolder = entityService.find(ShareHolder.class,
					getInstance().getPayer());
			zjtgfShareHolderName = shareHolder.getShareHolderName();
			//流程申请时,保存原股东信息
			getInstance().setShareHolderName(shareHolder.getShareHolderName());
			getInstance().setFondsCurrency(shareHolder.getFondsCurrency());
			getInstance().setFondsTotal(shareHolder.getFondsTotal());
			getInstance().setFondsInPlace(shareHolder.getFondsInPlace());
			getInstance().setEquityPerc(shareHolder.getEquityPerc());
			getInstance().setIsEquityRelated(shareHolder.getIsEquityRelated());
			getInstance().setRelatedPerc(shareHolder.getRelatedPerc());
			
		}
	}

	/** * 选中汇款线路后会写到主页面* */
	public void ajaxSelAc() throws Exception {
		if (selAcNo != null)
			PropertyUtils.copyProperties(getInstance()
					.getRemittanceLineAccount(), this.selAcNo);
	}

	/** 选择汇款线路* */
	public void ajaxSelAcNoList() {
		if (getCompanyId() == null) {
			MessageUtils.addErrorMessage("msg",
					MessageUtils.getMessage("msg_required_companyNameCn"));
		} else {
			this.acNoList = this.registerCaptialService
					.getSelAcNoList(getCompanyId());
		}
	}

	/** 初始化新增汇款 **/
	public void ajaxShowAcWin() {
		addAcNo = new RemittanceLineAccount();
	}

	/**
	 * 新增汇款新路
	 * */
	public void ajaxAddAcNo() {
		if (getCompanyId() == null) {
			MessageUtils.addErrorMessage("msg",
					MessageUtils.getMessage("msg_required_companyNameCn"));
		} else {
			Company company = this.entityService.find(Company.class, companyId);
			addAcNo.setDefunctInd("N");
			addAcNo.setCompany(company);
			addAcNo.setAccountType("2");
			this.registerCaptialService.saveAcNo(addAcNo);
			getInstance().setRemittanceLineAccount(addAcNo);
		}
	}

	/**
	 * 初始化下拉
	 */
	public void initdata(boolean isAll) {
		if (isAll) {
			companyNameSelect = companyTmsService.getAllCompanySelect();
		} else {
			companyNameSelect = companyTmsService.getCompanySelect();
		}
		currencySelect = dictBean
				.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
	}

	/**
	 * 
	 * <p>
	 * Description: 填充数据
	 * </p>
	 * 
	 * @param workclassNumber
	 */
	public void fillData(String workclassNumber, String flag) {
		if (workclassNumber != null) {
			try {
				ProcRegiCapital preg = this.registerCaptialService
						.findProcRegiCaptial(workclassNumber);
				if (preg != null && preg.getCompany() == null) {
					MessageUtils.addErrorMessage("msg",
							MessageUtils.getMessage("regicapital_noBound"));
				}
				companyId = preg.getCompany().getId();
				List<ShareHolder> list = shareHolderService
						.findShareHolderListByCp(companyId);
				if (!list.isEmpty()) {
					shareHolderSelect.clear();
					for (ShareHolder sh : list) {
						shareHolderSelect.add(new SelectItem(sh.getId(), sh
								.getShareHolderName()));
					}
				}
				companyNameEn = preg.getCompany().getCompanyEn();
				shareHolderModel = new CustomPageModel<ShareHolder>(list, false);
				
				
				
				// 设置保证金
				if (preg.getCanUse() != null && preg.getRest() != null) {
					double d = preg.getRest() - preg.getCanUse();
					preg.setInsure(d);
				}
				if ("集团项目经理审批".equals(title)) {
					if (preg.getThisFonds() != null) {
						preg.setActualAudit(preg.getThisFonds());
					}
					if (preg.getThisFondsCu() != null
							&& !"".equals(preg.getThisFondsCu())) {
						preg.setActualAuditCu(preg.getThisFondsCu());
					}
				}
				this.setInstance(preg);
				//设置汇款路线信息
				ajaxPayer();
			} catch (Exception e) {
				log.error("fillData方法 填充数据 出现异常：", e);
			}
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 发起注册资本金申请
	 * </p>
	 * 
	 * @return
	 */
	public String applyRegister() {
		try {
			// 如果是工程类表单做附件检查
			if (getInstance().getApplyType() != null
					&& "Y".equals(getInstance().getApplyType())) {
				List files = this.getProcessFileList();
				if (files == null || files.size() == 0) {
					MessageUtils.addErrorMessage("msg",
							MessageUtils.getMessage("msg_fileUpload_require"));
					return null;
				}
			}
			//本次申请资金金额必须小于等于未到位资金
			Double fondsNotTotal = getInstance().getFondsTotal() - getInstance().getFondsInPlace();
			if(getInstance().getThisFonds() > fondsNotTotal){
				MessageUtils.addErrorMessage("msg",
						"本次申请资金金额必须小于等于未到位资金");
				return null;
			}
			//汇款路线不能为空
			if(getInstance().getRemittanceLineAccount() == null || getInstance().getRemittanceLineAccount().getId() == null){
				MessageUtils.addErrorMessage("msg",
						"汇款路线不能为空");
				return null;
			}

			String workflowClassName = ProcessXmlUtil.getProcessAttribute("id",
					"RegiCapital", "className");
			String workClassnumber = registerCaptialService
					.vwCreateProcessInstance(workflowClassName, "注册资本金",
							getInstance().getPeMemo(), this.getDocmentIdList());
			// 保存流程附件
			this.saveProcessFile(workClassnumber);
			// 设置公司
			Company company = this.entityService.find(Company.class, companyId);
			getInstance().setCompany(company);
			getInstance().setApplyDate(
					DateUtil.strToDate(registerDate, "yyyy-MM-dd").toDate());
			getInstance().setProcInstId(workClassnumber);
			registerCaptialService.saveRegisCaptial(this.getInstance());
			MessageUtils.addSuccessMessage("doneMsg", MessageUtils.getMessage(
					"process_new_success",
					processUtilMapService.getTmsIdByFnId(workClassnumber)));
			return this.getListPage();
		} catch (ServiceException e) {
			MessageUtils.addErrorMessage("msg", e.getMessage());
			return null;
		} catch (Exception e) {
			if (e instanceof EJBException) {
				try {
					ServiceException se = (ServiceException) ((EJBException) e)
							.getCause();
					MessageUtils.addErrorMessage("msg", se.getMessage());
				} catch (Exception e1) {
					MessageUtils.addErrorMessage("msg", "事务异常");
				}

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
	 * Description:重新申请
	 * </p>
	 * 
	 * @return
	 */
	public String reApplyRegister() {
		try {
			// 如果是工程类表单做附件检查
			if (getInstance().getApplyType() != null
					&& "Y".equals(getInstance().getApplyType())) {
				List files = this.getProcessFileList();
				if (files == null || files.size() == 0) {
					MessageUtils.addErrorMessage("msg",
							MessageUtils.getMessage("msg_fileUpload_require"));
					return null;
				}
			}

			String workClassNum = getInstance().getProcInstId();
			registerCaptialService.vwReplayRegister(getInstance().getPeMemo(),
					workClassNum, this.getDocmentIdList());
			// 保存流程附件
			this.saveProcessFile(workClassNum);
			// 设置公司
			Company company = this.entityService.find(Company.class, companyId);
			getInstance().setCompany(company);
			getInstance().setApplyDate(
					DateUtil.strToDate(registerDate, "yyyy-MM-dd").toDate());
			entityService.update(this.getInstance());
			MessageUtils.addSuccessMessage("donMsg", MessageUtils.getMessage(
					"process_new_success_1",
					processUtilMapService.getTmsIdByFnId(workClassNum)));
			// 9.3处理下一个任务
			return processUtilService.getNextPage(getInputPage(), doNext,
					getCurrentIndex(), getCurrentTaskType());
		} catch (ServiceException e) {
			MessageUtils.addErrorMessage("msg", e.getMessage());
			return null;
		} catch (Exception e) {
			if (e instanceof EJBException) {
				try {
					ServiceException se = (ServiceException) ((EJBException) e)
							.getCause();
					MessageUtils.addErrorMessage("msg", se.getMessage());
				} catch (Exception e1) {
					MessageUtils.addErrorMessage("msg", "事务异常");
				}
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
	 * Description: 审批操作，退回操作
	 * </p>
	 * 
	 * @return
	 */
	public String approveRegisterCaptial() {
		try {
			Object obj = JSFUtils.getRequestParam("auit");
			if (getInstance().getProcInstId() == null
					|| "".equals(getInstance().getProcInstId())) {
				MessageUtils.addErrorMessage("msg",
						MessageUtils.getMessage("process_number_isNull"));
			}
			// 审批通过与否参数
			boolean flag = false;
			if (obj != null && "Y".equals(obj.toString())) {
				flag = true;
			}
			String processFlow = "";
			double unregistered = this.shareHolderService
					.calculationUnregistered(companyId);
			// 参数Map
			Map<String, Object> paramMap = Maps.newHashMap();
			paramMap.put("_Pass", flag);
			paramMap.put("_Arrived", unregistered);
			String inputable = ProcessXmlUtil.findStepProperty("id",
					"RegiCapital", title, "inputable");
			// 审批节点是否需要更新实体
			if (inputable != null && !"".equals(inputable)) {
				entityService.update(getInstance());
			}
			if (flag) {
				processFlow = ProcessXmlUtil.findStepProperty("id",
						"RegiCapital", title, "passMemo");
				MessageUtils.addSuccessMessage("doneMsg", MessageUtils
						.getMessage("approve_pass_success",
								processUtilMapService
										.getTmsIdByFnId(workObjNum)));
				//add by liushengbin 20140701 财务总监节点 审批通过后，更新注册资本金的已到位资金
				if("财务总监审批".equals(title)){                	
					registerCaptialService.updateShareHolder(this.getInstance());
                }
				
			} else {
				processFlow = ProcessXmlUtil.findStepProperty("id",
						"RegiCapital", title, "nopassMemo");
				MessageUtils.addSuccessMessage("doneMsg", MessageUtils
						.getMessage("approve_nopass_success",
								processUtilMapService
										.getTmsIdByFnId(workObjNum)));
			}
			registerCaptialService.vwDisposeTask(getInstance().getProcInstId(),
					paramMap, processFlow
							.concat(getInstance().getPeMemo() == null ? ""
									: getInstance().getPeMemo()));
			// 9.3处理下一个任务
			return processUtilService.getNextPage(getInputPage(), doNext,
					getCurrentIndex(), getCurrentTaskType());
		} catch (ServiceException e) {
			MessageUtils.addErrorMessage("msg", e.getMessage());
			return null;
		} catch (Exception e) {
			if (e instanceof EJBException) {
				try {
					ServiceException se = (ServiceException) ((EJBException) e)
							.getCause();
					MessageUtils.addErrorMessage("msg", se.getMessage());
				} catch (Exception e1) {
					MessageUtils.addErrorMessage("msg", "事务异常");
				}
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
	 * Description: 显示注册资本金详细步骤历史
	 * </p>
	 */
	public void displayProcessDetail() {
		// List<Object> filterValue = Lists.newArrayList();
		// Object[] substitutionVars = {};
		// int stepEnventType =
		// ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd);
		// int stopEvnent =
		// ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal);
		// String filter =
		// "(F_EventType = :EventType or F_EventType = :EventType1) ";
		// filterValue.add(stepEnventType);
		// filterValue.add(stopEvnent);
		// if (workObjNum != null) {
		// filter = filter.concat(" and F_WobNum = :wobNum");
		// filterValue.add(new VWWorkObjectNumber(workObjNum));
		// }
		// substitutionVars = filterValue.toArray();
		// processDetailList = processWaitService.findProcessDetialList(filter,
		// substitutionVars);

		if ("true".equals(isPatch)) {
			processDetailList = patchMainService
					.getProcessDetailFor411(workObjNum);
		} else {
			processDetailList = peManager.getProcessDetail(workObjNum);
		}

	}

	/**
	 * 
	 * <p>
	 * Description: 得到公司英文名称
	 * </p>
	 * 
	 * @return
	 */
	public void companyEnName() {
		Company com = this.entityService.find(Company.class, companyId);
		companyNameEn = com.getCompanyEn();
		this.getInstance().setCompany(com);

		// 得到股东信息
		List<ShareHolder> shareHolderList = shareHolderService
				.findShareHolderListByCp(com.getId());
		shareHolderModel = new CustomPageModel<ShareHolder>(shareHolderList,
				false);
		if (!shareHolderList.isEmpty()) {
			shareHolderSelect.clear();
			for (ShareHolder sh : shareHolderList) {
				shareHolderSelect.add(new SelectItem(sh.getId(), sh
						.getShareHolderName()));
			}
		}
		ajaxPayer();
	}

	/**
	 * 
	 * <p>
	 * Description: 资金币别联动其它币别
	 * </p>
	 */
	public void changeCu() {
		String restCu = getInstance().getRestCu();
		// 设置可用资金币别
		getInstance().setCanUseCu(restCu);
		// 保证金币别
		getInstance().setInsureCu(restCu);
	}

	/**
	 * 
	 * <p>
	 * Description:已付款币别联动
	 * </p>
	 */
	public void changePayCu() {
		String signCu = getInstance().getSignConsCu();
		getInstance().setPaidFundsCu(signCu);
	}

	/** 退回是立即执行 所以没有提交表单更新模型值 需要用Ajax更新备注 */
	public void findProcessMemo() {

	}

	/**
	 * 
	 * <p>
	 * Description: 改变公司资金余额计算保证金
	 * </p>
	 */
	public void calculate() {
		if (getInstance().getRest() != null) {
			double canuse = 0.0;
			if (getInstance().getCanUse() != null) {
				canuse = getInstance().getCanUse();
			}
			if (canuse > getInstance().getRest()) {
				MessageUtils
						.addErrorMessage("msg", MessageUtils
								.getMessage("available_greaterthan_remain"));
				getInstance().setCanUse(null);
				getInstance().setInsure(null);
				return;
			}
			Double d = getInstance().getRest() - canuse;
			getInstance().setInsure(d);
		}

	}

	/**
	 * 
	 * <p>
	 * Description: 计算已付款
	 * </p>
	 */
	public void calculatepay() {
		if (getInstance().getSignContract() != null) {
			double pay = 0.0;
			if (getInstance().getPaidFunds() != null) {
				pay = getInstance().getPaidFunds();
			}
			if (pay > getInstance().getSignContract()) {
				MessageUtils.addErrorMessage("msg",
						MessageUtils.getMessage("payed_greaterthan_contract"));
				getInstance().setPaidFunds(null);
				getInstance().setNotPayFounds(null);
				return;
			}
			Double d = getInstance().getSignContract() - pay;
			getInstance().setNotPayFounds(d);
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 计算保证金
	 * </p>
	 */
	public void calculateMarign() {
		if (getInstance().getCanUse() != null
				&& getInstance().getRest() != null) {
			if (getInstance().getCanUse() > getInstance().getRest()) {
				MessageUtils
						.addErrorMessage("msg", MessageUtils
								.getMessage("available_greaterthan_remain"));
				getInstance().setCanUse(null);
				return;
			}
			Double d = getInstance().getRest() - getInstance().getCanUse();
			getInstance().setInsure(d);
		}
	}

	/**
	 * 
	 * <p>
	 * Description:
	 * </p>
	 */
	public void calculateNotPay() {
		if (getInstance().getPaidFunds() != null
				&& getInstance().getSignContract() != null) {
			if (getInstance().getPaidFunds() > getInstance().getSignContract()) {
				MessageUtils.addErrorMessage("msg",
						MessageUtils.getMessage("payed_greaterthan_contract"));
				getInstance().setPaidFunds(null);
				getInstance().setNotPayFounds(null);
				return;
			}
			Double d = getInstance().getSignContract()
					- getInstance().getPaidFunds();
			getInstance().setNotPayFounds(d);
		}
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
		inputableFields = ProcessXmlUtil.getInputableDatas("RegiCapital",
				stepName);
	}

	public List<SelectItem> getCompanyNameSelect() {
		return companyNameSelect;
	}

	public void setCompanyNameSelect(List<SelectItem> companyNameSelect) {
		this.companyNameSelect = companyNameSelect;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}

	public String getCompanyNameEn() {
		return companyNameEn;
	}

	public void setCompanyNameEn(String companyNameEn) {
		this.companyNameEn = companyNameEn;
	}

	public LazyDataModel<ShareHolder> getShareHolderModel() {
		return shareHolderModel;
	}

	public void setShareHolderModel(LazyDataModel<ShareHolder> shareHolderModel) {
		this.shareHolderModel = shareHolderModel;
	}

	public List<ProcessDetailVo> getProcessDetailList() {
		return processDetailList;
	}

	public void setProcessDetailList(List<ProcessDetailVo> processDetailList) {
		this.processDetailList = processDetailList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<SelectItem> getCurrencySelect() {
		return currencySelect;
	}

	public void setCurrencySelect(List<SelectItem> currencySelect) {
		this.currencySelect = currencySelect;
	}

	public String getWorkObjNum() {
		return workObjNum;
	}

	public void setWorkObjNum(String workObjNum) {
		this.workObjNum = workObjNum;
	}

	public boolean isShowDetialStep() {
		return showDetialStep;
	}

	public void setShowDetialStep(boolean showDetialStep) {
		this.showDetialStep = showDetialStep;
	}

	public List<String> getInputableFields() {
		return inputableFields;
	}

	public void setInputableFields(List<String> inputableFields) {
		this.inputableFields = inputableFields;
	}

	public List<SelectItem> getShareHolderSelect() {
		return shareHolderSelect;
	}

	public void setShareHolderSelect(List<SelectItem> shareHolderSelect) {
		this.shareHolderSelect = shareHolderSelect;
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

	public List<RemittanceLineAccount> getAcNoList() {
		return acNoList;
	}

	public void setAcNoList(List<RemittanceLineAccount> acNoList) {
		this.acNoList = acNoList;
	}

	public RemittanceLineAccount getSelAcNo() {
		return selAcNo;
	}

	public void setSelAcNo(RemittanceLineAccount selAcNo) {
		this.selAcNo = selAcNo;
	}

	public RemittanceLineAccount getAddAcNo() {
		return addAcNo;
	}

	public void setAddAcNo(RemittanceLineAccount addAcNo) {
		this.addAcNo = addAcNo;
	}

	public String getZjtgfShareHolderName() {
		return zjtgfShareHolderName;
	}

	public void setZjtgfShareHolderName(String zjtgfShareHolderName) {
		this.zjtgfShareHolderName = zjtgfShareHolderName;
	}
	
	

}
