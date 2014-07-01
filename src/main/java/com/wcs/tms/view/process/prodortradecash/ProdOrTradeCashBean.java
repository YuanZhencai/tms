package com.wcs.tms.view.process.prodortradecash;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.util.DateUtil;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.MathUtil;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.controller.vo.DictVO;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.common.service.CommonService;
import com.wcs.common.service.DictService;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcProdOrTradeCash;
import com.wcs.tms.model.ProdOrTradeCashMain;
import com.wcs.tms.model.PurchaseFundDetail;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessUtilService;
import com.wcs.tms.service.process.common.patch.service.PatchMainService;
import com.wcs.tms.service.process.prodortradecash.ProdOrTradeCashService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.FileUpload;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

/**
 * 
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description:生产或贸易总头寸申请流程Bean
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
public class ProdOrTradeCashBean extends FileUpload<ProcProdOrTradeCash> {

	private static final long serialVersionUID = 1L;

	private Log log = LogFactory.getLog(ProdOrTradeCashBean.class);

	@Inject
	ProcessUtilMapService processUtilMapService;

	@Inject
	ProcessUtilService processUtilService;

	@Inject
	CommonBean commonBean;

	@Inject
	ProdOrTradeCashService prodOrTradeCashService;

	@Inject
	CompanyTmsService companyTmsService;

	@Inject
	CommonService commonService;
	@Inject
	PatchMainService patchMainService;
	@Inject
	PEManager peManager;

	@Inject
	DictService dictService;
	// 是否继续处理下个任务
	private Boolean doNext = true;
	// 当前所处理任务在任务列表中的位置
	private Integer currentIndex;
	// 当前处理任务类型（已接受和待接收）
	private String currentTaskType;
	// 金额
	private Double amount;
	// 均价
	private Double averPrice;
	// 头寸
	private Double cash;
	// 是否增加头寸数量,用以保存数据库操作“头寸”和“金额”字段是做逻辑加计算还是直接新建数据
	private Boolean addCash = false;
	// 申请日期
	private String applyDate;
	// 页面控件控制
	private Boolean conDisable = false;
	// 界面按钮以下可编辑控件控制
	private Boolean addNewCashDisable = true;
	// 日期控件单独控制
	private Boolean dateControlDisable = true;
	// 按钮控制
	private Boolean addNewCashConDisable = false;
	private Boolean addCashConDisable = false;
	// 品种类型Key值
	private String varietyType;

	// 公司下拉菜单
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
	// 申请实例
	private ProcProdOrTradeCash procProdOrTradeCash = new ProcProdOrTradeCash();
	// 品种下拉菜单
	private List<SelectItem> varietySelect = new ArrayList<SelectItem>();
	// 品种类型下拉
	private List<SelectItem> varietyTypeSelect = new ArrayList<SelectItem>();

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
	// 二级菜单参数
	private String menuTwo;

	// 是否是补丁表中获取到数据查看，处理2014.4.11~2014.4.28之间发生故障，丢失的流程数据和流程审批日志的查看
	private String isPatch;

	/**
	 * <p>
	 * Description:Bean init
	 * </p>
	 */
	@PostConstruct
	public void initProdOrTradeCashBean() {
		log.info("ProdOrTradeCashBean.initProdOrTradeCashBean()");
		initOp();
		initDict();
		// 申请时间
		setApplyDate(DateUtil.dateToStrShort(DateUtil
				.dateToDateTime((procProdOrTradeCash.getCreatedDatetime()))));
	}

	/**
	 * 初始化上次申请头寸和金额
	 */
	public void getLastValues() {
		if (MathUtil.isEmptyOrNull(procProdOrTradeCash.getAppType())
				|| MathUtil.isEmptyOrNull(procProdOrTradeCash.getVariety())
				|| null == procProdOrTradeCash.getCompany()
				|| 0 == procProdOrTradeCash.getCompany().getId()) {
			return;
		}
		List<ProdOrTradeCashMain> lastMains = prodOrTradeCashService
				.getLastProdOrCashMainsBy(procProdOrTradeCash);
		if (lastMains.size() != 0) {
			Double lastTotalCash = 0.0;
			Double lastTotalCashAmount = 0.0;
			List<PurchaseFundDetail> details = prodOrTradeCashService
					.getPurchaseFundDetails(lastMains.get(lastMains.size() - 1));
			for (PurchaseFundDetail d : details) {
				lastTotalCash = lastTotalCash + d.getAppNumPay();
				lastTotalCashAmount = lastTotalCashAmount + d.getAppAmountPay();
			}
			procProdOrTradeCash.setLastCash(lastMains.get(lastMains.size() - 1)
					.getTotalCash() - lastTotalCash);
			procProdOrTradeCash.setLastCashAmount(lastMains.get(
					lastMains.size() - 1).getTotalCashAmount()
					- lastTotalCashAmount);
		} else {
			procProdOrTradeCash.setLastCash(0.0);
			procProdOrTradeCash.setLastCashAmount(0.0);
		}
	}

	/**
	 * 判断当前审批数据是否存在于主数据表中
	 */
	private void initAddCash() {
		List<ProdOrTradeCashMain> prodOrTradeCashMains = prodOrTradeCashService
				.prodOrTradeCashExists(procProdOrTradeCash);
		// 当前审批数据如果存在于主数据则说明为补增头寸
		if (prodOrTradeCashMains.size() != 0) {
			// 页面控件控制参数设置
			setConDisable(true);
			setAddCash(true);
			// add on 2013-1-28
			setAddCashConDisable(true);
			setDateControlDisable(true);
			setAddNewCashConDisable(false);
			setAddNewCashDisable(false);
		}
	}

	/**
	 * 获取主数据头寸信息
	 */
	public void getCashInformation() {
		Boolean succeed = validateGetMain();
		if (!succeed) {
			return;
		}
		List<ProdOrTradeCashMain> prodOrTradeCashMains = prodOrTradeCashService
				.findProdOrTradeCashByNow(procProdOrTradeCash);
		if (prodOrTradeCashMains.size() == 0) {
			MessageUtils.addErrorMessage("msg", "当前类型的该品种在现阶段还未申请过预算！");
			return;
		}
		// 页面控件控制参数设置
		setConDisable(true);
		setAddCash(true);
		// add on 2013-1-28
		setAddCashConDisable(true);
		setDateControlDisable(true);
		setAddNewCashConDisable(false);
		setAddNewCashDisable(false);
		procProdOrTradeCash.setStartDate(prodOrTradeCashMains.get(0)
				.getStartDate());
		procProdOrTradeCash
				.setEndDate(prodOrTradeCashMains.get(0).getEndDate());
	}

	/**
	 * 点击“新增头寸”按钮触发
	 */
	public void getDisableInformation() {
		conDisable = false;
		// 非补增头寸标识
		addCash = false;
		// 补增按钮控制
		addCashConDisable = false;
		addNewCashDisable = false;
		addNewCashConDisable = true;
		dateControlDisable = false;
	}

	/**
	 * 验证获取主数据总头寸前置条件
	 * 
	 * @return
	 */
	public Boolean validateGetMain() {
		Boolean succeed = true;
		if (null == procProdOrTradeCash.getCompany().getId()) {
			MessageUtils.addErrorMessage("msg", "请选择申请公司名称");
			succeed = false;
		}
		if (null == procProdOrTradeCash.getAppType()) {
			MessageUtils.addErrorMessage("msg", "请选择类型");
			succeed = false;
		}
		if (null == procProdOrTradeCash.getVariety()) {
			MessageUtils.addErrorMessage("msg", "请选择品种");
			succeed = false;
		}
		return succeed;
	}

	/**
	 * 计算头寸金额与均价
	 */
	public void calculateAmountAndPrice() {
		Double lastCashAmount = procProdOrTradeCash.getLastCashAmount();
		Double thisCashAmount = procProdOrTradeCash.getThisCashAmount();
		if (null == lastCashAmount) {
			MessageUtils.addErrorMessage("msg", "上次申请剩余头寸金额不能为空！");
			return;
		}
		if (null == thisCashAmount || thisCashAmount == 0) {
			MessageUtils.addErrorMessage("msg", "本次申请剩余头寸金额不能为0或空！");
			return;
		}
		this.amount = lastCashAmount + thisCashAmount;
		if (this.cash != null && this.amount != null) {
			this.averPrice = amount / cash;
		}
	}

	public void calculateByLastCashAmount() {
		Double lastCashAmount = procProdOrTradeCash.getLastCashAmount();
		Double thisCashAmount = procProdOrTradeCash.getThisCashAmount();
		if (null == lastCashAmount) {
			MessageUtils.addErrorMessage("msg", "上次申请剩余头寸金额不能为空！");
			return;
		}
		if (null != thisCashAmount) {
			this.amount = lastCashAmount + thisCashAmount;
		}
		if (this.cash != null && this.amount != null) {
			this.averPrice = amount / cash;
		}
	}

	public void calculateByThisCashAmount() {
		Double lastCashAmount = procProdOrTradeCash.getLastCashAmount();
		Double thisCashAmount = procProdOrTradeCash.getThisCashAmount();
		if (null == thisCashAmount || thisCashAmount == 0) {
			MessageUtils.addErrorMessage("msg", "本次申请剩余头寸金额不能为0或空！");
			return;
		}
		if (null != lastCashAmount) {
			this.amount = lastCashAmount + thisCashAmount;
		}
		if (this.cash != null && this.amount != null) {
			this.averPrice = amount / cash;
		}
	}

	/**
	 * 计算头寸
	 */
	public void calculateTotal() {
		Double lastCash = procProdOrTradeCash.getLastCash();
		Double thisCash = procProdOrTradeCash.getThisCash();
		if (null == thisCash || thisCash == 0) {
			MessageUtils.addErrorMessage("msg", "本次申请头寸不能为0或空！");
			return;
		}
		this.cash = lastCash + thisCash;
		if (this.cash != null && this.amount != null) {
			this.averPrice = amount / cash;
		}
	}

	public void calculateByLastCash() {
		Double lastCash = procProdOrTradeCash.getLastCash();
		Double thisCash = procProdOrTradeCash.getThisCash();
		if (null != thisCash) {
			this.cash = lastCash + thisCash;
		}
		if (this.cash != null && this.amount != null) {
			this.averPrice = amount / cash;
		}
	}

	public void calculateByThisCash() {
		Double lastCash = procProdOrTradeCash.getLastCash();
		Double thisCash = procProdOrTradeCash.getThisCash();
		if (null == thisCash || thisCash == 0) {
			MessageUtils.addErrorMessage("msg", "本次申请头寸不能为0或空！");
			return;
		}
		if (null != lastCash) {
			this.cash = lastCash + thisCash;
		}
		if (this.cash != null && this.amount != null) {
			this.averPrice = amount / cash;
		}
	}

	/**
	 * 审批保存
	 * 
	 * @return
	 */
	public String doApprove() {
		boolean pass = false;
		if (approveStatus != null) {

			if ("Y".equals(approveStatus)) {
				// 验证整个页面必填字段
				Boolean succeed = validateWholeFace();
				if (!succeed) {
					return null;
				}
				// add on 2012-11-22
				if (!addCash) {
					// 验证是否存在期限冲突数据
					Boolean dateConflict = validateDateConflict();
					if (dateConflict) {
						MessageUtils.addErrorMessage("msg", "期限与历史预算存在冲突！");
						return null;
					}
				}

				pass = true;
			}

			try {
				Company company = entityService.find(Company.class,
						procProdOrTradeCash.getCompany().getId());
				procProdOrTradeCash.setCompany(company);
				prodOrTradeCashService.doApprove(procProdOrTradeCash, pass,
						stepName, addCash);
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
	 * 初始化品种类型下拉菜单
	 */
	private void initDict() {
		varietyTypeSelect = commonBean
				.getDictByCode(DictConsts.TMS_FARM_PRODUCE_VARIETY_TYPE);
	}

	/**
	 * 申请时初始化品种下拉
	 */
	public void initVarietySelect() {
		varietySelect = commonBean.getDictByCode(varietyType);
	}

	/**
	 * 查看或审批时初始化品种下拉
	 */
	public void initVarietySelect2() {
		// 根据cat和key模糊查询出DictVO数据
		List<DictVO> dictVOs = dictService.searchData(
				DictConsts.TMS_FARM_PRODUCE_VARIETY_TYPE,
				procProdOrTradeCash.getVariety(), "", "", "");
		// 确定用户所选品种类型cat
		varietyType = dictVOs.get(0).getCodeCat();
		// 初始化品种下拉
		varietySelect = commonBean.getDictByCode(varietyType);
	}

	/**
	 * 创建保存流程
	 */
	public String createProcInstance() {
		// 验证整个页面必填字段
		Boolean succeed = validateWholeFace();
		if (!succeed) {
			return null;
		}
		if (!addCash) {
			// 验证是否存在期限冲突数据
			Boolean dateConflict = validateDateConflict();
			if (dateConflict) {
				MessageUtils.addErrorMessage("msg", "您填写的日期与历史预算存在冲突！");
				return null;
			}
		}
		Company company = entityService.find(Company.class, procProdOrTradeCash
				.getCompany().getId());
		procProdOrTradeCash.setCompany(company);

		try {
			String procInstId = prodOrTradeCashService
					.createProcInstance(procProdOrTradeCash);
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
	 * 验证是否存在期限冲突数据
	 * 
	 * @return
	 */
	private Boolean validateDateConflict() {
		Boolean dateConflict;
		List<ProdOrTradeCashMain> prodOrTradeCashMains = prodOrTradeCashService
				.findMainBy(procProdOrTradeCash);
		if (prodOrTradeCashMains.size() == 0) {
			dateConflict = false;
		} else {
			dateConflict = true;
		}
		return dateConflict;
	}

	/**
	 * 验证整个界面必填字段
	 * 
	 * @return
	 */
	private Boolean validateWholeFace() {
		Boolean succeed = true;

		if (null == procProdOrTradeCash.getCompany().getId()
				|| 0 == procProdOrTradeCash.getCompany().getId()) {
			MessageUtils.addErrorMessage("msg", "请选择申请公司名称");
			succeed = false;
		}
		if (MathUtil.isEmptyOrNull(procProdOrTradeCash.getAppType())) {
			MessageUtils.addErrorMessage("msg", "请选择类型");
			succeed = false;
		}
		if (MathUtil.isEmptyOrNull(procProdOrTradeCash.getVariety())) {
			MessageUtils.addErrorMessage("msg", "请选择品种");
			succeed = false;
		}
		if (null == procProdOrTradeCash.getLastCash()) {
			MessageUtils.addErrorMessage("msg", "请填写上次申请剩余头寸");
			succeed = false;
		}
		if (null == procProdOrTradeCash.getThisCash()) {
			MessageUtils.addErrorMessage("msg", "请填写本次申请头寸");
			succeed = false;
		} else if (0 == procProdOrTradeCash.getThisCash()) {
			MessageUtils.addErrorMessage("msg", "本次申请头寸不能为0");
			succeed = false;
		}
		if (null == procProdOrTradeCash.getLastCashAmount()) {
			MessageUtils.addErrorMessage("msg", "请填写上次申请剩余头寸金额");
			succeed = false;
		}
		if (null == procProdOrTradeCash.getThisCashAmount()) {
			MessageUtils.addErrorMessage("msg", "请填写本次申请剩余头寸金额");
			succeed = false;
		} else if (0 == procProdOrTradeCash.getThisCashAmount()) {
			MessageUtils.addErrorMessage("msg", "本次申请剩余金额不能为0");
			succeed = false;
		}
		if (null == procProdOrTradeCash.getStartDate()) {
			MessageUtils.addErrorMessage("msg", "请选择开始时间");
			succeed = false;
		}
		if (null == procProdOrTradeCash.getEndDate()) {
			MessageUtils.addErrorMessage("msg", "请选择结束时间");
			succeed = false;
		}
		return succeed;

	}

	/**
	 * 初始化操作类型
	 */
	public void initOp() {
		String op = (String) JSFUtils.getRequest().getAttribute("op");
		if (StringUtils.isEmpty(op)) {
			op = JSFUtils.getRequestParam("op");
		}
		if (op != null && !"".equals(op)) {
			// 查看表单详细
			if ("view".equals(op)) {
				isPatch = JSFUtils.getParamValue("isPatch");
				menuTwo = JSFUtils.getRequestParam("menu2");
				if (StringUtils.isEmpty(menuTwo)) {
					menuTwo = JSFUtils.getRequestParam("menu2");
				}
				procInstId = (String) JSFUtils.getRequest().getAttribute(
						"procInstId");
				if (StringUtils.isEmpty(procInstId)) {
					procInstId = JSFUtils.getRequestParam("procInstId");
				}
				this.findProcInstance();
				initCompany(true);
				// 品种下拉初始化
				initVarietySelect2();
			}
			// 到审批页面
			if ("approve".equals(op)) {
				// add on 2013-5-17
				currentIndex = Integer.parseInt(JSFUtils
						.getParamValue("currentIndex"));
				currentTaskType = (String) JSFUtils
						.getParamValue("currentTaskType");

				procInstId = (String) JSFUtils.getRequest().getAttribute(
						"procInstId");
				stepName = (String) JSFUtils.getRequest().getAttribute(
						"stepName");
				this.findProcInstance();
				initCompany(true);
				// add on 2012-11-22
				initAddCash();
				// 品种下拉初始化
				initVarietySelect2();
				// 非重新申请页面设置“处理意见”默认值
				if (!"申请".equals(stepName)) {
					procProdOrTradeCash.setPeMemo("同意");
				}
			}
		} else {
			initCompany(false);
			procProdOrTradeCash.setUpdatedDatetime(DateUtil.getNowDate()
					.toDate());
		}
	}

	/**
	 * 查询流程实例
	 */
	private void findProcInstance() {
		procProdOrTradeCash = prodOrTradeCashService
				.findProcInstanceByProcInstId(procInstId);
		searchProcessDetail();
		calculateTotal();
		calculateAmountAndPrice();
	}

	/**
	 * 查询流程详细步骤
	 */
	private void searchProcessDetail() {
		
		if("true".equals(isPatch)){
    		detailVos = patchMainService.getProcessDetailFor411(procInstId);
    	}else{
    		detailVos = peManager.getProcessDetail(procInstId);
    	}    
	}

	/**
	 * 初始化公司下拉
	 */
	public void initCompany(boolean created) {
		if (created) {
			companySelect = companyTmsService.getAllCompanySelect();
		} else {
			companySelect = companyTmsService.getCompanySelect();
		}
	}

	public ProcProdOrTradeCash getProcProdOrTradeCash() {
		return procProdOrTradeCash;
	}

	public void setProcProdOrTradeCash(ProcProdOrTradeCash procProdOrTradeCash) {
		this.procProdOrTradeCash = procProdOrTradeCash;
	}

	public String getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}

	public List<SelectItem> getCompanySelect() {
		return companySelect;
	}

	public void setCompanySelect(List<SelectItem> companySelect) {
		this.companySelect = companySelect;
	}

	public Boolean getAddCash() {
		return addCash;
	}

	public void setAddCash(Boolean addCash) {
		this.addCash = addCash;
	}

	public Double getCash() {
		return cash;
	}

	public void setCash(Double cash) {
		this.cash = cash;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getAverPrice() {
		return averPrice;
	}

	public void setAverPrice(Double averPrice) {
		this.averPrice = averPrice;
	}

	public List<SelectItem> getVarietySelect() {
		return varietySelect;
	}

	public void setVarietySelect(List<SelectItem> varietySelect) {
		this.varietySelect = varietySelect;
	}

	public CommonBean getCommonBean() {
		return commonBean;
	}

	public void setCommonBean(CommonBean commonBean) {
		this.commonBean = commonBean;
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

	public Boolean getDoNext() {
		return doNext;
	}

	public void setDoNext(Boolean doNext) {
		this.doNext = doNext;
	}

	public Boolean getConDisable() {
		return conDisable;
	}

	public void setConDisable(Boolean conDisable) {
		this.conDisable = conDisable;
	}

	public String getVarietyType() {
		return varietyType;
	}

	public void setVarietyType(String varietyType) {
		this.varietyType = varietyType;
	}

	public List<SelectItem> getVarietyTypeSelect() {
		return varietyTypeSelect;
	}

	public void setVarietyTypeSelect(List<SelectItem> varietyTypeSelect) {
		this.varietyTypeSelect = varietyTypeSelect;
	}

	public Boolean getAddNewCashDisable() {
		return addNewCashDisable;
	}

	public void setAddNewCashDisable(Boolean addNewCashDisable) {
		this.addNewCashDisable = addNewCashDisable;
	}

	public Boolean getAddNewCashConDisable() {
		return addNewCashConDisable;
	}

	public void setAddNewCashConDisable(Boolean addNewCashConDisable) {
		this.addNewCashConDisable = addNewCashConDisable;
	}

	public Boolean getAddCashConDisable() {
		return addCashConDisable;
	}

	public void setAddCashConDisable(Boolean addCashConDisable) {
		this.addCashConDisable = addCashConDisable;
	}

	public Boolean getDateControlDisable() {
		return dateControlDisable;
	}

	public void setDateControlDisable(Boolean dateControlDisable) {
		this.dateControlDisable = dateControlDisable;
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
