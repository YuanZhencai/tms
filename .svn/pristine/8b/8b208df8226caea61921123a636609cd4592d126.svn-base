package com.wcs.tms.view.process.purchaseFundProd;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;

import com.google.common.collect.Lists;
import com.wcs.tms.util.CustomPageModel;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.FileUpload;
import com.wcs.tms.view.process.common.entity.PaymentVo;
import com.wcs.tms.view.process.common.entity.ProcPurchaseFundVo;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;
import com.wcs.tms.view.process.common.entity.ProcessInstanceVo;
import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.MathUtil;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.controller.vo.DictVO;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.common.service.DictService;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.CompanyAccount;
import com.wcs.tms.model.ProcPurchaseFundProd;
import com.wcs.tms.model.ProcPurchaseFundProdDetail;
import com.wcs.tms.model.PurchaseFund;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessUtilService;
import com.wcs.tms.service.process.common.patch.service.PatchMainService;
import com.wcs.tms.service.process.purchaseFundProd.PurchaseFundProdService;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.service.system.company.CompanyAccountServer;
import com.wcs.tms.service.system.company.CompanyTmsService;

/**
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Title: 采购资金（生产）借款/转款流程Bean
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
public class PurchaseFundProdBean extends FileUpload<ProcPurchaseFundProd> {

	private static final long serialVersionUID = 1L;

	private final Log log = LogFactory.getLog(PurchaseFundProdBean.class);

	@Inject
	EntityService entityService;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	CommonBean dictBean;
	@Inject
	BankService bankService;
	@Inject
	LoginService loginService;
	@Inject
	PurchaseFundProdService purchaseFundProdService;
	@Inject
	CompanyAccountServer companyAccountServer;
	// 9.4
	@Inject
	ProcessUtilService processUtilService;
	// 9.11
	@Inject
	ProcessUtilMapService processUtilMapService;
	@Inject
	DictService dictService;

	@Inject
	PatchMainService patchMainService;
	@Inject
    PEManager peManager;
	/** 公司实体 */
	private Company company = new Company();
	/** 采购资金（生产）实体 */
	private ProcPurchaseFundProd procPurchaseFundProd = new ProcPurchaseFundProd();
	/** 银行帐号实体 */
	private CompanyAccount companyAccount = new CompanyAccount();
	/** 采购资金——主数据表 */
	private PurchaseFund purchaseFund = new PurchaseFund();
	/** 付款明细 */
	private ProcPurchaseFundProdDetail procPurchaseFundProdDetail = new ProcPurchaseFundProdDetail();
	/** 申请公司名称下拉选择框 */
	private List<SelectItem> companyNameSelect = new ArrayList<SelectItem>();
	/** 开户银行下拉 */
	private List<SelectItem> depositBankSelect = new ArrayList<SelectItem>();
	/** 银行帐号下拉 */
	private List<SelectItem> bankAccountSelect = new ArrayList<SelectItem>();
	/** 流程详细vo列表 */
	private List<ProcessDetailVo> detailVos = new ArrayList<ProcessDetailVo>();
	/** 付款详细vo列表 */
	private List<PaymentVo> payDetailVos = new ArrayList<PaymentVo>();
	/** 付款审批时品种信息所在VO */
	private List<ProcPurchaseFundVo> procPurchaseFundVos = new ArrayList<ProcPurchaseFundVo>();
	/** 用于编辑删除 */
	private ProcPurchaseFundVo ppfVoEdit = new ProcPurchaseFundVo();
	private ProcPurchaseFundVo ppfVoDelete = new ProcPurchaseFundVo();
	/** 编辑VO信息前 */
	private ProcPurchaseFundVo ppfVoCopyEdit = new ProcPurchaseFundVo();
	/** 得到可输入的审批字段 */
	private List<String> inputableFields = new ArrayList<String>();
	/** 品种下拉 */
	private List<SelectItem> varietySelect = new ArrayList<SelectItem>();
	/** 用于添加 */
	private PurchaseFund pfFund = new PurchaseFund();
	/** 用于编辑删除 */
	private PurchaseFund pfFundEdit = new PurchaseFund();
	private PurchaseFund pfFundEditVo = new PurchaseFund();
	/** 编辑前 */
	private PurchaseFund pfFundCopyEdit = new PurchaseFund();

	/** 采购资金——品种 */
	private List<PurchaseFund> purchaseFundList = Lists.newArrayList();
	/** 采购资金——品种分页模型 */
	private LazyDataModel<PurchaseFund> purchaseFundModel;

	/** 查看时品种信息所在VO */
	private List<ProcPurchaseFundVo> procPurchaseFundViewList = new ArrayList<ProcPurchaseFundVo>();
	/** 品种类型下拉 */
	private List<SelectItem> varietyTypeSelect = new ArrayList<SelectItem>();

	/** 记录品种编辑之前的名称 */
	private String varietyNameEdit;

	// 金额（大写）
	private String upAmount;
	// 流程步骤名称
	private String stepName;
	// 二级菜单参数
	private String menuTwo;
	// 流程实例ID
	private String procInstId;
	// 收款人户名
	private String receiverAccount;
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
	// 本次支付的金额
	private Double thisPay;
	// 剩余需要支付的金额（大写）
	private String upRemainPay;
	// 本次支付的金额（大写）
	private String upThisPay;
	// 若品种列表有品种信息，将申请公司设为不可修改
	private String ifVarietyNull;
	// 品种类型Key值
	private String varietyType;

	// 是否是补丁表中获取到数据查看，处理2014.4.11~2014.4.28之间发生故障，丢失的流程数据和流程审批日志的查看
	private String isPatch;

	/**
	 * list已提交流程界面,input已处理流程界面
	 */
	public PurchaseFundProdBean() {
		setupPage("/faces/process/common/processSubed-list.xhtml",
				"/faces/process/common/processDealed-list.xhtml", null);
	}

	@PostConstruct
	public void init() {
		procPurchaseFundProd.setCreatedDatetime(new Date());
		initDict();
		initOp();
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
				initVarietySelect2();
				log.info("流程表中获得页面品种的详细信息集合");
				procPurchaseFundViewList = purchaseFundProdService
						.getVarietyModelView(procInstId, procPurchaseFundProd,
								purchaseFund);
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
				log.info("op:" + op);
				log.info("stepName:" + stepName);
				this.getInputable();
				this.findProcInstance();
				// 防止重新申请时，公司选择为全部公司
				if ("申请".equals(stepName)) {
					initdata(false);
				} else {
					initdata(true);
				}
				initVarietySelect2();
				this.setIfVarietyNull("Y");
				log.info("审批时获得页面品种的VO展示信息！");
				procPurchaseFundVos = purchaseFundProdService.getVarietyVos(
						procInstId, procPurchaseFundProd);
				lowToUp();
				this.updateVoAmount(procPurchaseFundVos);
				log.info("流程表中获得页面品种的集合");
				purchaseFundList = purchaseFundProdService.getVarietyModel(
						procInstId, procPurchaseFundProd, purchaseFund);
				purchaseFundModel = new CustomPageModel<PurchaseFund>(
						purchaseFundList, false);
				// 默认处理意见为同意
				if (!"申请".equals(stepName)) {
					procPurchaseFundProd.setPeMemo("同意");
				}
			}
		} else {
			initdata(false);
		}
	}

	/**
	 * 初始化农产品品种字典表
	 */
	public void initDict() {
		setVarietyTypeSelect(dictBean
				.getDictByCode(DictConsts.TMS_FARM_PRODUCE_VARIETY_TYPE));
	}

	/**
	 * 申请时初始化品种下拉
	 */
	public void initVarietySelect() {
		varietySelect = dictBean.getDictByCode(getVarietyType());
	}

	/**
	 * 查看或审批时初始化品种下拉
	 */
	public void initVarietySelect2() {
		// 根据cat和key模糊查询出DictVO数据
		List<DictVO> dictVOs = dictService.searchData(
				DictConsts.TMS_FARM_PRODUCE_VARIETY_TYPE,
				procPurchaseFundProd.getVarietyOne(), "", "", "");
		// 确定用户所选品种类型cat
		setVarietyType(dictVOs.get(0).getCodeCat());
		// 初始化品种下拉
		varietySelect = dictBean.getDictByCode(getVarietyType());
	}

	/**
	 * 
	 */
	public String getVarietyName(String keyValue) {
		List<DictVO> dictVOs = dictService.searchData(
				DictConsts.TMS_FARM_PRODUCE_VARIETY_TYPE, keyValue, "", "", "");
		// 确定用户所选品种类型cat
		return dictVOs.get(0).getCodeVal();
	}

	/**
	 * 查询流程实例
	 */
	public void findProcInstance() {
		procPurchaseFundProd = purchaseFundProdService
				.findProcInstanceByProcInstId(procInstId);
		// 查询流程附件
		// 查询流程详细步骤
		searchProcessDetail();
		serchPayDetail();
		ajaxCompany();
		// 获得品种列表的集合
		getVarietyList(procPurchaseFundProd.getProcInstId());
	}

	public void getVarietyList(String workObjNumId) {
		if (workObjNumId == null || "".equals(workObjNumId)) {
			MessageUtils.addErrorMessage("msg", "工作流程实例编号为空");
		}
	}

	/**
	 * 清空添加品种Dialog的缓存
	 */
	public void toNull() {
		log.info("清空缓存！");
		pfFund = new PurchaseFund();
		pfFund.setVarietyRelated("N");
		setVarietyType("");
		pfFund.setVariety(null);
		pfFund.setVarietyRemain(null);
		pfFund.setVarietyAmount(null);
		pfFund.setVarietyNum(null);
		pfFund.setVarietyRemark(null);
		this.initDict();
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
		payDetailVos = purchaseFundProdService.getPayDetail(procInstId);
	}

	/**
	 * 得到可输入的审批字段
	 */
	private void getInputable() {
		inputableFields = ProcessXmlUtil.getInputableDatas("PurchaseFundProd",
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
	 * 初始化业务公司下拉
	 * 
	 * @param all
	 */
	private void initdata(Boolean all) {
		if (all) {
			companyNameSelect = companyTmsService.getAllCompanySelect();
		} else {
			companyNameSelect = companyTmsService.getCompanySelect();
		}
	}

	/**
	 * 申请公司得到与之相关信息
	 */
	public void ajaxCompany() {
		this.gotReceiverName();
		this.getComanyBank();
	}

	/**
	 * <p>
	 * Description: 得到收款人户名
	 * </p>
	 * 
	 * @return
	 */
	public void gotReceiverName() {
		Company com = this.entityService.find(Company.class,
				procPurchaseFundProd.getCompany().getId());
		procPurchaseFundProd.setReceiverName(com.getCompanyName());
	}

	/**
	 * 根据公司得到对应的银行及帐号
	 */
	public void getComanyBank() {
		// 根据公司名称获得与之相关的银行
		depositBankSelect = purchaseFundProdService
				.getCompanyBankSelect(procPurchaseFundProd.getCompany().getId());
		this.getCompanyBankAccount();
	}

	/**
	 * 得到公司对应的帐号
	 */
	public void getCompanyBankAccount() {
		Company com = this.entityService.find(Company.class,
				procPurchaseFundProd.getCompany().getId());
		String accountDesc = procPurchaseFundProd.getAccountDesc();
		bankAccountSelect = purchaseFundProdService.getCompanyAccountSelect(
				com.getId(), accountDesc);
	}

	/**
	 * 添加时：将申请数量换成符合条件的格式
	 */
	public void lowToUpNumAdd() {
		String parten = "#.00";
		DecimalFormat decimal = new DecimalFormat(parten);
		String str = decimal.format(pfFund.getVarietyNum());
		Double amount = Double.parseDouble(str);
		pfFund.setVarietyNum(amount);
	}

	/**
	 * 添加时：将申请金额换成符合条件的格式
	 */
	public void lowToUpAmountAdd() {
		pfFund.setVarietyAmount(MathUtil.cashpoolRound(pfFund
				.getVarietyAmount()));
	}

	/**
	 * 编辑时：将申请数量换成符合条件的格式
	 */
	public void lowToUpNumEdit() {
		String parten = "#.00";
		DecimalFormat decimal = new DecimalFormat(parten);
		String str = decimal.format(pfFundEdit.getVarietyNum());
		Double amount = Double.parseDouble(str);
		pfFundEdit.setVarietyNum(amount);
	}

	/**
	 * 编辑时：将申请金额换成符合条件的格式
	 */
	public void lowToUpAmountEdit() {
		String parten = "#.00";
		DecimalFormat decimal = new DecimalFormat(parten);
		String str = decimal.format(pfFundEdit.getVarietyAmount());
		Double amount = Double.parseDouble(str);
		pfFundEdit.setVarietyAmount(amount);
	}

	/**
	 * 将"金额"阿拉伯数字转换成大写
	 */
	public void lowToUp() {
		// 金额的大小写转换
		setUpAmount(com.wcs.tms.util.MoneyFormatUtil
				.format((procPurchaseFundProd.getAmount() * 10000)));
	}

	/**
	 * <p>
	 * Description: 添加品种
	 * </p>
	 */
	public void addVariety() {
		log.info("执行添加品种的方法！！");
		if (purchaseFundList.size() > 0) {
			for (PurchaseFund pf : purchaseFundList) {
				if (pf.getVariety().equals(pfFund.getVariety())
						&& pf.getVarietyRelated().equals(
								pfFund.getVarietyRelated())) {
					MessageUtils.addErrorMessage("msg", "已存在一条相同品种信息");
					return;
				}
			}
		}
		// 原来系统最多只能添加三种产品 现改为最多添加十种产品
		if (purchaseFundList.size() > 9) {
			MessageUtils.addErrorMessage("msg", "最多只能添加十种产品");
			return;
		} else if (ifThisApplyGreatRemain()) {
			MessageUtils.addErrorMessage("varietyMsg", "本次申请数量大于剩余头寸数量");
			return;
		} else {
			// 若品种列表中有品种信息，则申请公司disable=true
			this.setIfVarietyNull("Y");
			purchaseFundList.add(pfFund);
			purchaseFundModel = new CustomPageModel<PurchaseFund>(
					purchaseFundList, false);
			// 根据品种的金额更新流程的金额
			this.updateProcessAmount(purchaseFundList);
			lowToUp();
			this.setPfFund(new PurchaseFund());
			MessageUtils.addSuccessMessage("msg", "添加品种成功");
		}
	}

	/**
	 * 根据品种的金额更新流程的金额
	 * 
	 * @param purchaseFundList
	 */
	public void updateProcessAmount(List<PurchaseFund> purchaseFundList) {
		log.info("根据品种的金额更新流程的金额");
		Double amount = 0D;
		for (PurchaseFund pf : purchaseFundList) {
			amount += pf.getVarietyAmount();
		}
		procPurchaseFundProd.setAmount(amount);
		log.info("amount:" + amount + "金额（小写）:"
				+ procPurchaseFundProd.getAmount());
	}

	/**
	 * 添加品种时：判断本次申请数量是否大于剩余头寸数量(可能两次调用)
	 */
	public Boolean ifThisApplyGreatRemain() {
		log.info("申请数量：" + pfFund.getVarietyNum());
		log.info("剩余头寸数量：" + pfFund.getVarietyRemain());
		if (!(pfFund.getVarietyRemain() != null)) {
			return true;
		}
		if (pfFund.getVarietyNum() > pfFund.getVarietyRemain()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 编辑品种时：判断本次申请数量是否大于剩余头寸数量
	 */
	public Boolean ifThisApplyGreatRemainTwo() {
		log.info("申请数量：" + pfFundEdit.getVarietyNum());
		log.info("剩余头寸数量：" + pfFundEdit.getVarietyRemain());
		if (pfFundEdit.getVarietyNum() > pfFundEdit.getVarietyRemain()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * 编辑品种
	 */
	public void editVariety() {
		try {
			if (purchaseFundList.size() > 1) {
				if (pfFundEditVo.getVariety().equals(pfFundEdit.getVariety())
						&& pfFundEditVo.getVarietyRelated().equals(
								pfFundEdit.getVarietyRelated())) {
					log.info("品种及是关联方式都未修改！");
					for (PurchaseFund pf : purchaseFundList) {
						if (pf.getVariety().equals(pfFundEdit.getVariety())
								&& pf.getVarietyRelated().equals(
										pfFundEdit.getVarietyRelated())) {
							// 验证申请数量与剩余数量的关系
							if (ifThisApplyGreatRemainTwo()) {
								MessageUtils.addErrorMessage("varietyMsg",
										"本次申请数量大于剩余头寸数量");
								return;
							}
							PropertyUtils.copyProperties(pf, pfFundEdit);
						}
					}
				} else {
					List<PurchaseFund> pfListEdit = new ArrayList<PurchaseFund>();
					pfListEdit.addAll(purchaseFundList);
					boolean test = false;
					for (PurchaseFund pf1 : pfListEdit) {

						if (pf1.getVariety().equals(pfFundEdit.getVariety())
								&& pf1.getVarietyRelated().equals(
										pfFundEdit.getVarietyRelated())) {
							MessageUtils.addErrorMessage("msg", "已存在一条相同品种信息");
							pfListEdit.add(pfFundEditVo);
							return;
						} else {
							// 验证申请数量与剩余数量的关系
							if (ifThisApplyGreatRemainTwo()) {
								MessageUtils.addErrorMessage("varietyMsg",
										"本次申请数量大于剩余头寸数量");
								return;
							}
							test = true;
						}
					}
					if (test) {
						PropertyUtils.copyProperties(pfFundEditVo, pfFundEdit);
					}
				}
			} else {
				// 验证申请数量与剩余数量的关系
				if (ifThisApplyGreatRemainTwo()) {
					MessageUtils
							.addErrorMessage("varietyMsg", "本次申请数量大于剩余头寸数量");
					return;
				}
				PropertyUtils.copyProperties(purchaseFundList.get(0),
						pfFundEdit);
			}
		} catch (IllegalAccessException e) {
			log.error("editVariety方法 错误信息：" + e.getMessage());
		} catch (InvocationTargetException e) {
			log.error("editVariety方法 错误信息：" + e.getMessage());
		} catch (NoSuchMethodException e) {
			log.error("editVariety方法 错误信息：" + e.getMessage());
		}
		log.info("purchaseFundList!!!!" + purchaseFundList.size());
		this.updateProcessAmount(purchaseFundList);
		lowToUp();
		purchaseFundModel = new CustomPageModel<PurchaseFund>(purchaseFundList,
				true);
		MessageUtils.addSuccessMessage("msg", "修改品种成功");
	}

	/**
	 * 删除品种
	 */
	public void deleteVariety() {
		log.info("执行删除品种方法");
		try {
			for (PurchaseFund pf : purchaseFundList) {
				log.info("品种ID:" + pf.getVariety());
			}
			log.info("pfFundEdit:" + pfFundEdit.getVariety());
			purchaseFundList.remove(pfFundEdit);
			if (purchaseFundList.size() == 0) {
				// 如果品种列表为空，释放选择申请公司功能
				this.setIfVarietyNull("N");
			}
			this.updateProcessAmount(purchaseFundList);
			lowToUp();
			purchaseFundModel = new CustomPageModel<PurchaseFund>(
					purchaseFundList, true);
			MessageUtils.addSuccessMessage("msg", "删除品种成功");
		} catch (Exception e) {
			log.error("deleteVariety方法 错误信息：" + e.getMessage());
		}
	}

	/**
	 * 编辑之前
	 */
	public void beforeEdit() {
		String varietyType = purchaseFundProdService.getCodeKey(pfFundEdit
				.getVariety());
		setVarietyType(varietyType);
		initVarietySelect();
		varietyNameEdit = pfFundEdit.getVariety();
		try {
			// 将编辑前的对象属性值赋值出来,因为编辑时将对象赋值到后台，界面修改到编辑用的同一对象，后台验证失败了却更改了值
			PropertyUtils.copyProperties(pfFundCopyEdit, pfFundEdit);
			pfFundEditVo = pfFundEdit;
			pfFundEdit = new PurchaseFund();
			PropertyUtils.copyProperties(pfFundEdit, pfFundCopyEdit);
		} catch (IllegalAccessException e) {
			log.error("beforeEdit方法 错误信息：" + e.getMessage());
		} catch (InvocationTargetException e) {
			log.error("beforeEdit方法 错误信息：" + e.getMessage());
		} catch (NoSuchMethodException e) {
			log.error("beforeEdit方法 错误信息：" + e.getMessage());
		}
	}

	/**
	 * 添加时获得对应品种的剩余头寸数
	 */
	public void getVarietyRemain() {
		log.info("执行获得对应品种的剩余头寸数");
		if (procPurchaseFundProd.getCompany().getId() == null) {
			MessageUtils.addErrorMessage("msg", "请选择申请公司名称");
			return;
		}
		Double remain = purchaseFundProdService.getVarietyRemain(
				procPurchaseFundProd, pfFund);
		log.info("对应品种的剩余头寸数:" + remain);
		pfFund.setVarietyRemain(remain);
	}

	/**
	 * 编辑时获得对应品种的剩余头寸数
	 */
	public void getVarietyRemainEdit() {
		log.info("执行获得对应品种的剩余头寸数");
		if (procPurchaseFundProd.getCompany().getId() == null) {
			MessageUtils.addErrorMessage("msg", "请选择申请公司名称");
			return;
		}
		Double remain = purchaseFundProdService.getVarietyRemain(
				procPurchaseFundProd, pfFundEdit);
		log.info("对应品种的剩余头寸数:" + remain);
		pfFundEdit.setVarietyRemain(remain);
	}

	/**
	 * 如果List当中有两条相同的品种信息
	 * 
	 * @param purchaseFundList
	 * @return
	 */
	public boolean judgeSize(List<PurchaseFund> purchaseFundList) {
		Map<String, Double> map = new HashMap<String, Double>();
		Double amount = 0.0;
		for (PurchaseFund pf : purchaseFundList) {
			if (map.get(pf.getVariety()) == null) {
				map.put(pf.getVariety(), pf.getVarietyNum());
			} else {
				if (map.get(pf.getVariety()) > 0.0) {
					map.put(pf.getVariety(),
							(map.get(pf.getVariety()) + pf.getVarietyNum()));
					amount = pf.getVarietyRemain();
					if (map.get(pf.getVariety()) > amount) {
						return true;
					}
				} else {
					map.put(pf.getVariety(), pf.getVarietyNum());
				}
			}
		}
		return false;
	}

	/**
	 * 采购资金（生产）借款/转款流程创建保存
	 */
	public String applyMargin() {
		// 设置公司、银行
		Company company = this.entityService.find(Company.class,
				procPurchaseFundProd.getCompany().getId());
		procPurchaseFundProd.setCompany(company);
		log.info("采购资金（生产）借款/转款流程创建保存");
		try {
			if (purchaseFundList == null || purchaseFundList.size() == 0) {
				MessageUtils.addErrorMessage("msg", "请添加至少一种品种");
				return null;
			}
			// 若出现一种品种有两条数据（关联方和非关联方）则用这两条数据的申请数量之和和剩余头寸判断大小
			if (purchaseFundList.size() > 1 && judgeSize(purchaseFundList)) {
				MessageUtils.addErrorMessage("msg", "品种申请数量大于剩余头寸数量");
				return null;
			}
			if (!purchaseFundList.isEmpty()) {
				purchaseFundProdService.batchAddCreditRpt(purchaseFundList,
						procPurchaseFundProd);
				// 剩余需要支付金额 默认为 申请金额（add on 2013-7-19 by yan）
				procPurchaseFundProd.setLastPaiedAmount(procPurchaseFundProd
						.getAmount());
				String procInstId = purchaseFundProdService
						.createProcInstance(procPurchaseFundProd);
				purchaseFundProdService.saveProdProduct(procPurchaseFundProd,
						purchaseFundList);
				MessageUtils.addSuccessMessage("doneMsg", MessageUtils
						.getMessage("process_new_success",
								processUtilMapService
										.getTmsIdByFnId(procInstId)));
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
		return "/faces/process/common/processSubed-list.xhtml";
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
				if (purchaseFundList == null || purchaseFundList.size() == 0) {
					MessageUtils.addErrorMessage("msg", "请添加至少一种品种");
					return null;
				}
				procPurchaseFundProd.setLastPaiedAmount(procPurchaseFundProd
						.getAmount());
				purchaseFundProdService.doApprove(purchaseFundList,
						procPurchaseFundProd, pass, stepName);
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
				getCurrentIndex(), getCurrentTaskType());
	}

	/**
	 * 编辑品种信息VO
	 */
	public void editVarietyVo() {
		log.info("执行编辑品种信息VO方法");
		if (ifThisPayMore()) {
			MessageUtils.addErrorMessage("editVoMsg", "本次支付金额大于剩余需要支付的金额");
			return;
		}
		if (ifThisPayNumMore()) {
			MessageUtils.addErrorMessage("editVoMsg", "本次审批数量大于剩余需要审批的数量");
			return;
		}
		try {
			for (ProcPurchaseFundVo pfVo : procPurchaseFundVos) {
				if (pfVo.getVarietyId().equals(ppfVoEdit.getVarietyId())
						&& pfVo.getVarietyRelated().equals(
								ppfVoEdit.getVarietyRelated())) {
					PropertyUtils.copyProperties(pfVo, ppfVoEdit);
				}
			}
		} catch (IllegalAccessException e) {
			log.error("editVarietyVo方法 错误信息：" + e.getMessage());
		} catch (InvocationTargetException e) {
			log.error("editVarietyVo方法 错误信息：" + e.getMessage());
		} catch (NoSuchMethodException e) {
			log.error("editVarietyVo方法 错误信息：" + e.getMessage());
		}
		this.updateVoAmount(procPurchaseFundVos);
		MessageUtils.addSuccessMessage("msg", "修改品种审批信息成功");
	}

	/**
	 * 编辑品种信息VO时：判断本次审批金额是否大于剩余需要审批的金额
	 */
	public Boolean ifThisPayMore() {
		log.info("本次审批金额：" + ppfVoEdit.getThisPurchaseAmountPay());
		log.info("剩余需要审批的金额：" + ppfVoEdit.getLessPurchaseAmountPay());
		if (ppfVoEdit.getThisPurchaseAmountPay() > ppfVoEdit
				.getLessPurchaseAmountPay()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 编辑品种信息VO时：判断本次审批数量是否大于剩余需要审批的数量
	 */
	public Boolean ifThisPayNumMore() {
		log.info("本次审批数量：" + ppfVoEdit.getThisPurchaseNumPay());
		log.info("剩余需要审批的数量：" + ppfVoEdit.getNeedPurchaseNum());
		if (ppfVoEdit.getThisPurchaseNumPay() > ppfVoEdit.getNeedPurchaseNum()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 编辑品种信息VO时：将本次审批数量换成符合条件的格式
	 */
	public void lowToUpNumEditVo() {
		String parten = "#.00";
		DecimalFormat decimal = new DecimalFormat(parten);
		String str = decimal.format(ppfVoEdit.getThisPurchaseNumPay());
		Double amount = Double.parseDouble(str);
		ppfVoEdit.setThisPurchaseNumPay(amount);
	}

	/**
	 * 编辑品种信息VO时：将本次审批金额换成符合条件的格式
	 */
	public void lowToUpAmountEditVo() {
		ppfVoEdit.setThisPurchaseAmountPay(MathUtil.cashpoolRound(ppfVoEdit
				.getThisPurchaseAmountPay()));
	}

	/**
	 * 删除品种信息VO信息
	 */
	public void deleteVarietyVo() {
		try {
			if (procPurchaseFundVos.size() > 1) {
				if ((ppfVoEdit.getVarietyId()).equals(procPurchaseFundProd
						.getVarietyOne())) {
					procPurchaseFundProd.setVarietyOneDefunct("Y");
				}
				if ((ppfVoEdit.getVarietyId()).equals(procPurchaseFundProd
						.getVarietyTwo())) {
					procPurchaseFundProd.setVarietyTwoDefunct("Y");
				}
				if ((ppfVoEdit.getVarietyId()).equals(procPurchaseFundProd
						.getVarietyThr())) {
					procPurchaseFundProd.setVarietyThrDefunct("Y");
				}
				purchaseFundProdService.doDelete(procPurchaseFundProd);
				// 删除品种
				purchaseFundProdService.doDeleteProduct(ppfVoEdit);

				procPurchaseFundVos.remove(ppfVoEdit);
				this.updateVoAmount(procPurchaseFundVos);
				MessageUtils.addSuccessMessage("msg", "删除品种信息成功");
			} else {
				MessageUtils.addErrorMessage("msg", "请至少保留一种品种");
				return;
			}
		} catch (Exception e) {
			log.error("deleteVarietyVo方法 错误信息：" + e.getMessage());
		}
	}

	/**
	 * 根据品种的金额更新本次支付的金额及剩余需要支付的金额
	 * 
	 * @param procPurchaseFundVos
	 */
	public void updateVoAmount(List<ProcPurchaseFundVo> procPurchaseFundVos) {
		log.info("根据品种的金额更新本次支付的金额");
		log.info("品种VO中剩余的品种数：" + procPurchaseFundVos.size());
		Double thisPay = 0D;
		Double needPay = 0D;
		for (ProcPurchaseFundVo pf : procPurchaseFundVos) {
			log.info("本次审批金额（单个）：" + pf.getThisPurchaseAmountPay());
			log.info("剩余需要支付的金额（单个）：" + pf.getLessPurchaseAmountPay());
			thisPay += pf.getThisPurchaseAmountPay();
			needPay += pf.getLessPurchaseAmountPay();
		}
		// 设置小写value
		this.setThisPay(thisPay);
		this.setRemainPay(needPay);
		// 转化成大写
		this.lowToUp2(needPay);
		this.lowToUp3(thisPay);
		log.info("剩余需要支付的金额（小写）：" + this.getRemainPay());
		log.info("本次支付金额（小写）:" + this.getThisPay());
	}

	/**
	 * 将剩余需要支付金额转换成大写
	 * 
	 * @param needPay
	 */
	public void lowToUp2(Double needPay) {
		setUpRemainPay(com.wcs.tms.util.MoneyFormatUtil
				.format(((needPay) * 10000)));
	}

	/**
	 * 将本次支付金额转换成大写
	 * 
	 * @param ThisPay
	 */
	public void lowToUp3(Double ThisPay) {
		setUpThisPay(com.wcs.tms.util.MoneyFormatUtil.format(ThisPay * 10000));
	}

	/**
	 * 编辑品种信息VO之前
	 */
	public void beforeEditVo() {
		String varietyType = purchaseFundProdService.getCodeKey(ppfVoEdit
				.getVarietyId());
		setVarietyType(varietyType);
		initVarietySelect();
		varietyNameEdit = ppfVoEdit.getVarietyId();
		try {
			log.info("编辑品种信息VO之前");
			// 将编辑前的对象属性值赋值出来,因为编辑时将对象赋值到后台，界面修改到编辑用的同一对象，后台验证失败了却更改了值
			PropertyUtils.copyProperties(ppfVoCopyEdit, ppfVoEdit);
			ppfVoDelete = ppfVoEdit;
			ppfVoEdit = new ProcPurchaseFundVo();
			PropertyUtils.copyProperties(ppfVoEdit, ppfVoCopyEdit);
		} catch (IllegalAccessException e) {
			log.error("beforeEditVo方法 错误信息：" + e.getMessage());
		} catch (InvocationTargetException e) {
			log.error("beforeEditVo方法 错误信息：" + e.getMessage());
		} catch (NoSuchMethodException e) {
			log.error("beforeEditVo方法 错误信息：" + e.getMessage());
		}
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
			// 两表之间设置关联(流程与付款详细)
			procPurchaseFundProdDetail
					.setProcPurchaseFundProd(procPurchaseFundProd);
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
					Double sum = MathUtil.roundHalfUp(remainPay - thisPay, 4);
					procPurchaseFundProd.setLastPaiedAmount(sum);
				} else {
					// 停止付款标识
					conf = false;
					// 更新剩余需要支付金额（add on 2013-7-15 by yan ）
					procPurchaseFundProd.setLastPaiedAmount(0.0);
				}
				purchaseFundProdService.doConfirm(procPurchaseFundVos,
						procPurchaseFundProd, procPurchaseFundProdDetail, conf,
						payWay, stepName, purchaseFund);
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
				getCurrentIndex(), getCurrentTaskType());
	}

	// *********************************setter、getter方法**************************************
	public EntityService getEntityService() {
		return entityService;
	}

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	public CompanyTmsService getCompanyTmsService() {
		return companyTmsService;
	}

	public void setCompanyTmsService(CompanyTmsService companyTmsService) {
		this.companyTmsService = companyTmsService;
	}

	public CommonBean getDictBean() {
		return dictBean;
	}

	public void setDictBean(CommonBean dictBean) {
		this.dictBean = dictBean;
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

	public PurchaseFundProdService getPurchaseFundProdService() {
		return purchaseFundProdService;
	}

	public void setPurchaseFundProdService(
			PurchaseFundProdService purchaseFundProdService) {
		this.purchaseFundProdService = purchaseFundProdService;
	}

	public CompanyAccountServer getCompanyAccountServer() {
		return companyAccountServer;
	}

	public void setCompanyAccountServer(
			CompanyAccountServer companyAccountServer) {
		this.companyAccountServer = companyAccountServer;
	}

	public ProcessUtilService getProcessUtilService() {
		return processUtilService;
	}

	public void setProcessUtilService(ProcessUtilService processUtilService) {
		this.processUtilService = processUtilService;
	}

	public ProcessUtilMapService getProcessUtilMapService() {
		return processUtilMapService;
	}

	public void setProcessUtilMapService(
			ProcessUtilMapService processUtilMapService) {
		this.processUtilMapService = processUtilMapService;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public ProcPurchaseFundProd getProcPurchaseFundProd() {
		return procPurchaseFundProd;
	}

	public void setProcPurchaseFundProd(
			ProcPurchaseFundProd procPurchaseFundProd) {
		this.procPurchaseFundProd = procPurchaseFundProd;
	}

	public CompanyAccount getCompanyAccount() {
		return companyAccount;
	}

	public void setCompanyAccount(CompanyAccount companyAccount) {
		this.companyAccount = companyAccount;
	}

	public PurchaseFund getPurchaseFund() {
		return purchaseFund;
	}

	public void setPurchaseFund(PurchaseFund purchaseFund) {
		this.purchaseFund = purchaseFund;
	}

	public ProcPurchaseFundProdDetail getProcPurchaseFundProdDetail() {
		return procPurchaseFundProdDetail;
	}

	public void setProcPurchaseFundProdDetail(
			ProcPurchaseFundProdDetail procPurchaseFundProdDetail) {
		this.procPurchaseFundProdDetail = procPurchaseFundProdDetail;
	}

	public List<SelectItem> getCompanyNameSelect() {
		return companyNameSelect;
	}

	public void setCompanyNameSelect(List<SelectItem> companyNameSelect) {
		this.companyNameSelect = companyNameSelect;
	}

	public List<SelectItem> getDepositBankSelect() {
		return depositBankSelect;
	}

	public void setDepositBankSelect(List<SelectItem> depositBankSelect) {
		this.depositBankSelect = depositBankSelect;
	}

	public List<SelectItem> getBankAccountSelect() {
		return bankAccountSelect;
	}

	public void setBankAccountSelect(List<SelectItem> bankAccountSelect) {
		this.bankAccountSelect = bankAccountSelect;
	}

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

	public List<ProcPurchaseFundVo> getProcPurchaseFundVos() {
		return procPurchaseFundVos;
	}

	public void setProcPurchaseFundVos(
			List<ProcPurchaseFundVo> procPurchaseFundVos) {
		this.procPurchaseFundVos = procPurchaseFundVos;
	}

	public ProcPurchaseFundVo getPpfVoEdit() {
		return ppfVoEdit;
	}

	public void setPpfVoEdit(ProcPurchaseFundVo ppfVoEdit) {
		this.ppfVoEdit = ppfVoEdit;
	}

	public ProcPurchaseFundVo getPpfVoDelete() {
		return ppfVoDelete;
	}

	public void setPpfVoDelete(ProcPurchaseFundVo ppfVoDelete) {
		this.ppfVoDelete = ppfVoDelete;
	}

	public ProcPurchaseFundVo getPpfVoCopyEdit() {
		return ppfVoCopyEdit;
	}

	public void setPpfVoCopyEdit(ProcPurchaseFundVo ppfVoCopyEdit) {
		this.ppfVoCopyEdit = ppfVoCopyEdit;
	}

	public List<String> getInputableFields() {
		return inputableFields;
	}

	public void setInputableFields(List<String> inputableFields) {
		this.inputableFields = inputableFields;
	}

	public List<SelectItem> getVarietySelect() {
		return varietySelect;
	}

	public void setVarietySelect(List<SelectItem> varietySelect) {
		this.varietySelect = varietySelect;
	}

	public PurchaseFund getPfFund() {
		return pfFund;
	}

	public void setPfFund(PurchaseFund pfFund) {
		this.pfFund = pfFund;
	}

	public PurchaseFund getPfFundEdit() {
		return pfFundEdit;
	}

	public void setPfFundEdit(PurchaseFund pfFundEdit) {
		this.pfFundEdit = pfFundEdit;
	}

	public PurchaseFund getPfFundEditVo() {
		return pfFundEditVo;
	}

	public void setPfFundEditVo(PurchaseFund pfFundEditVo) {
		this.pfFundEditVo = pfFundEditVo;
	}

	public PurchaseFund getPfFundCopyEdit() {
		return pfFundCopyEdit;
	}

	public void setPfFundCopyEdit(PurchaseFund pfFundCopyEdit) {
		this.pfFundCopyEdit = pfFundCopyEdit;
	}

	public List<PurchaseFund> getPurchaseFundList() {
		return purchaseFundList;
	}

	public void setPurchaseFundList(List<PurchaseFund> purchaseFundList) {
		this.purchaseFundList = purchaseFundList;
	}

	public LazyDataModel<PurchaseFund> getPurchaseFundModel() {
		return purchaseFundModel;
	}

	public void setPurchaseFundModel(
			LazyDataModel<PurchaseFund> purchaseFundModel) {
		this.purchaseFundModel = purchaseFundModel;
	}

	public List<ProcPurchaseFundVo> getProcPurchaseFundViewList() {
		return procPurchaseFundViewList;
	}

	public void setProcPurchaseFundViewList(
			List<ProcPurchaseFundVo> procPurchaseFundViewList) {
		this.procPurchaseFundViewList = procPurchaseFundViewList;
	}

	public String getVarietyNameEdit() {
		return varietyNameEdit;
	}

	public void setVarietyNameEdit(String varietyNameEdit) {
		this.varietyNameEdit = varietyNameEdit;
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

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getReceiverAccount() {
		return receiverAccount;
	}

	public void setReceiverAccount(String receiverAccount) {
		this.receiverAccount = receiverAccount;
	}

	public String getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}

	public String getConfirmStatus() {
		return confirmStatus;
	}

	public void setConfirmStatus(String confirmStatus) {
		this.confirmStatus = confirmStatus;
	}

	public Boolean getDoNext() {
		return doNext;
	}

	public void setDoNext(Boolean doNext) {
		this.doNext = doNext;
	}

	public ProcessInstanceVo getNextProInstance() {
		return nextProInstance;
	}

	public void setNextProInstance(ProcessInstanceVo nextProInstance) {
		this.nextProInstance = nextProInstance;
	}

	public Double getRemainPay() {
		return remainPay;
	}

	public void setRemainPay(Double remainPay) {
		this.remainPay = remainPay;
	}

	public Double getThisPay() {
		return thisPay;
	}

	public void setThisPay(Double thisPay) {
		this.thisPay = thisPay;
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

	public String getIfVarietyNull() {
		return ifVarietyNull;
	}

	public void setIfVarietyNull(String ifVarietyNull) {
		this.ifVarietyNull = ifVarietyNull;
	}

	public List<SelectItem> getVarietyTypeSelect() {
		return varietyTypeSelect;
	}

	public void setVarietyTypeSelect(List<SelectItem> varietyTypeSelect) {
		this.varietyTypeSelect = varietyTypeSelect;
	}

	public String getVarietyType() {
		return varietyType;
	}

	public void setVarietyType(String varietyType) {
		this.varietyType = varietyType;
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