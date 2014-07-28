package com.wcs.tms.view.process.regicapitalChange;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;

import com.google.common.collect.Maps;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.JSFUtils;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcRegiCapitalChange;
import com.wcs.tms.model.ProcRegiCapitalChangeShareholder;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessUtilService;
import com.wcs.tms.service.process.common.patch.service.PatchMainService;
import com.wcs.tms.service.process.regicapitalChange.RegisterCaptialChangeService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.FileUpload;
import com.wcs.tms.view.process.common.entity.ChangeShareholderVo;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

/** 
* <p>Project: tms</p> 
* <p>Title: RegisterCaptialChangeBean.java</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2014 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:yuanzhencai@wcs-global.com">Yuan</a> 
*/
@ManagedBean
@ViewScoped
public class RegisterCaptialChangeBean extends FileUpload<ProcRegiCapitalChange> {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private static final Log logger = LogFactory.getLog(RegisterCaptialChangeBean.class);

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

	private List<ChangeShareholderVo> changeShareHolders;

	private ChangeShareholderVo shareHolder = new ChangeShareholderVo();
	private ChangeShareholderVo selectedHolder;

	/** 流程详细List数据 */
	private List<ProcessDetailVo> processDetailList = new ArrayList<ProcessDetailVo>();
	/** 资金币种下拉 */
	private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
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

	private String operate;

	private Double holderTotal;
	private Double oldHolderTotal;

	private Boolean isEquityPercChange = false;

	@Inject
	private CompanyTmsService companyTmsService;
	@Inject
	private RegisterCaptialChangeService registerCaptialChangeService;
	@Inject
	private CommonBean dictBean;
	@Inject
	ProcessUtilService processUtilService;// 9.3
	@Inject
	ProcessUtilMapService processUtilMapService;// 9.10
	@Inject
	PatchMainService patchMainService;
	@Inject
	private LoginService loginService;

	@Inject
	PEManager peManager;

	// 是否是补丁表中获取到数据查看，处理2014.4.11~2014.4.28之间发生故障，丢失的流程数据和流程审批日志的查看
	private String isPatch;

	public RegisterCaptialChangeBean() {
		// list已提价流程界面,input已处理流程界面
		this.setupPage("/faces/process/common/processSubed-list.xhtml", "/faces/process/common/processDealed-list.xhtml", null);
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
				currentIndex = Integer.parseInt(JSFUtils.getParamValue("currentIndex"));
				currentTaskType = (String) JSFUtils.getParamValue("currentTaskType");
			}

			title = JSFUtils.getParamValue("stepName");
			getInputable(title);
			displayProcessDetail();
			displayDetailAttach(workObjNum);
			// 查询注册资本金变更对象
			fillData(wcnum);
			initdata(true);
			registerDate = DateUtil.dateToStrShort(DateUtil.dateToDateTime(getInstance().getCreatedDatetime()));
		} else {
			// 注册时清空全局参数
			workObjNum = null;
			title = "工厂资金岗位人员申请";
			getInputable(title);
			registerDate = DateUtil.dateToStrShort(DateUtil.getNowDateShort());
			initdata(false);
			getInstance().setPeMemo("提交。");
			getInstance().setIsInvestRegRemaAvai("0");
		}

	}

	public void findCaptialChangeByCp() {
		Company company = null;
		changeShareHolders = new ArrayList<ChangeShareholderVo>();
		if (companyId != null) {
			company = this.entityService.find(Company.class, companyId);
		} else {
			company = new Company();
		}
		companyNameEn = company.getCompanyEn();
		getInstance().setCompany(company);
		getInstance().setInvestTotal(company.getInvestTotal());
		getInstance().setInvestTotalOri(company.getInvestTotal());
		getInstance().setInvestCurrencyOri(company.getInvestCurrency());
		getInstance().setIsInvestRegRemaAvaiOri(company.getIsInvestRegRemaAvai());
		getInstance().setInvestRegRemaFundsOri(company.getInvestRegRemaFunds());
		getInstance().setInvestRegRemaFundsCuOri(company.getInvestRegRemaFundsCu());
		getInstance().setIsInvestRegRemaAvai(company.getIsInvestRegRemaAvai());
		getInstance().setInvestRegRemaFunds(company.getInvestRegRemaFunds());
		getInstance().setInvestRegRemaFundsCu(company.getInvestRegRemaFundsCu());
		changeShareHolders = registerCaptialChangeService.findChangeShareholderVosBy(getInstance());
		refreshShareHolders();
	}

	/**
	 * 恢复股东
	 */
	public void recoverShareholder() {
		List<ChangeShareholderVo> shareHolders = selectedHolder.getShareHolders();
		ChangeShareholderVo oldShareholderVo = shareHolders.get(0);
		ChangeShareholderVo newShareholderVo = shareHolders.get(1);
		ProcRegiCapitalChangeShareholder sh = newShareholderVo.getChangeShareholder();
		try {
			ConvertUtils.register(new DateConverter(null), java.util.Date.class);
			BeanUtils.copyProperties(newShareholderVo, oldShareholderVo);
			newShareholderVo.setStatus("更新");
			if(sh != null) {
				sh.setDefunctInd("N");
				newShareholderVo.setChangeShareholder(sh);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		isEquityPercChange = true;
		refreshShareHolders();
	}

	/**
	 * 添加股东
	 */
	public void addShareholder() {
		if (companyId == null) {
			MessageUtils.addErrorMessage("msg", "请选择公司");
			return;
		}
		shareHolder = new ChangeShareholderVo();
		selectedHolder = new ChangeShareholderVo();
		handleDialogByWidgetVar("saveHolderDialogVar", "open");
	}

	/**
	 * 创建股东
	 */
	public void createShareholder() {
		if (validateSholder(shareHolder)) {
			if (changeShareHolders == null)
				changeShareHolders = new ArrayList<ChangeShareholderVo>();
			try {
				shareHolder.setStatus(shareHolder.getStatus() == null ? "新增" : shareHolder.getStatus());
				ConvertUtils.register(new DateConverter(null), java.util.Date.class);
				BeanUtils.copyProperties(selectedHolder, shareHolder);
				ChangeShareholderVo oldShareHolder = new ChangeShareholderVo();
				selectedHolder.getShareHolders().add(oldShareHolder);
				selectedHolder.getShareHolders().add(selectedHolder);
				changeShareHolders.add(selectedHolder);
				handleDialogByWidgetVar("saveHolderDialogVar", "close");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			refreshShareHolders();
		}
	}

	/**
	 * 编辑股东
	 */
	public void editShareholder() {
		shareHolder = new ChangeShareholderVo();
		ChangeShareholderVo changeShareholderVo = new ChangeShareholderVo();
		try {
			List<ChangeShareholderVo> shareHolders = selectedHolder.getShareHolders();
			if (shareHolders != null && shareHolders.size() > 1) {
				changeShareholderVo = shareHolders.get(1);
			} else {
				BeanUtils.copyProperties(changeShareholderVo, selectedHolder);
			}
			ConvertUtils.register(new DateConverter(null), java.util.Date.class);
			BeanUtils.copyProperties(shareHolder, changeShareholderVo);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 更新股东
	 */
	public void updateShareholder() {
		if (validateSholder(shareHolder)) {
			try {
				if (!"新增".equals(shareHolder.getStatus())) {
					shareHolder.setStatus("更新");
				}
				ConvertUtils.register(new DateConverter(null), java.util.Date.class);
				ChangeShareholderVo newHolder = new ChangeShareholderVo();
				BeanUtils.copyProperties(newHolder, shareHolder);
				List<ChangeShareholderVo> shareHolders = selectedHolder.getShareHolders();
				if (shareHolders != null && shareHolders.size() > 1) {
					shareHolders.set(1, newHolder);
				} else {
					shareHolders.add(newHolder);
				}
				handleDialogByWidgetVar("saveHolderDialogVar", "close");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			refreshShareHolders();
		}

	}

	/**
	 * 删除股东
	 */
	public void deleteShareholder() {
		List<ChangeShareholderVo> shareHolders = selectedHolder.getShareHolders();
		ChangeShareholderVo vo = shareHolders.get(shareHolders.size() - 1);
		if ("新增".equals(vo.getStatus())) {
			changeShareHolders.remove(selectedHolder);
			isEquityPercChange = true;
			refreshShareHolders();
			ProcRegiCapitalChangeShareholder s = vo.getChangeShareholder();
			if(s != null) {
				s.setDefunctInd("Y");
//				entityService.update(s);
			}
			return;
		}
		ChangeShareholderVo oldHolder = shareHolders.get(0);
		ChangeShareholderVo newHolderVo = new ChangeShareholderVo();
		newHolderVo.getShareHolders().add(newHolderVo);
		newHolderVo.setShareholderIdOriginal(oldHolder.getShareholderIdOriginal());
		newHolderVo.setStatus("删除");
		newHolderVo.setShareholderName(oldHolder.getShareholderName());
		newHolderVo.setIsEquityRelated(oldHolder.getIsEquityRelated());
		if (shareHolders.size() > 1) {
			newHolderVo.setChangeShareholder(vo.getChangeShareholder());
			shareHolders.set(1, newHolderVo);
		} else {
			shareHolders.add(newHolderVo);
		}
		isEquityPercChange = true;
		refreshShareHolders();
	}

	public boolean validateSholder(ChangeShareholderVo s) {
		boolean validate = true;
		String shareholderName = s.getShareholderName();
		if ("add".equals(operate)) {
			if (shareholderName == null || "".equals(shareholderName)) {
				MessageUtils.addErrorMessage("saveHolderMessage", "股东名称：不能为空。");
				validate = false;
			} else {
				for (ChangeShareholderVo sv : changeShareHolders) {
					List<ChangeShareholderVo> shs = sv.getShareHolders();
					ChangeShareholderVo vo = shs.get(shs.size() - 1);
					if (s.getShareholderName().trim().equals(vo.getShareholderName())) {
						MessageUtils.addErrorMessage("saveHolderMessage", "股东名称：不能重复。");
						validate = false;
						break;
					}
				}
			}
		}
		Double fondsTotal = s.getFondsTotal();
		if (fondsTotal == null) {
			MessageUtils.addErrorMessage("saveHolderMessage", "注册资金：不能为空。");
			return false;
		} else if (fondsTotal < 0) {
			MessageUtils.addErrorMessage("saveHolderMessage", "注册资金：必须大于0。");
			validate = false;
		}
		Double fondsInPlace = s.getFondsInPlace();
		if (fondsInPlace == null) {
			MessageUtils.addErrorMessage("saveHolderMessage", "到位资金：不能为空。");
			return false;
		} else if (fondsInPlace < 0) {
			MessageUtils.addErrorMessage("saveHolderMessage", "到位资金：必须大于0。");
			validate = false;
		}
		Double notInflace = fondsTotal - fondsInPlace;
		if (notInflace < 0) {
			MessageUtils.addErrorMessage("saveHolderMessage", "未到位资金：必须大于0。");
			validate = false;
		}
		return validate;
	}

	public boolean validateInstance() {
		boolean validate = true;
		if (companyId == null) {
			MessageUtils.addErrorMessage("msg", "公司名称：不能为空。");
			validate = false;
		}
		if (getInstance().getInvestTotal() == null) {
			MessageUtils.addErrorMessage("msg", "投资总额：不能为空。");
			validate = false;
		}
		if (getInstance().getInvestRegRemaFunds() == null) {
			MessageUtils.addErrorMessage("msg", "投注差可用：不能为空。");
			validate = false;
		} else if (getInstance().getInvestRegRemaFunds() < 0) {
			MessageUtils.addErrorMessage("msg", "投注差可用：必须大于0。");
			validate = false;
		}
		if (validate && registerCaptialChangeService.hasCapitalChange(getInstance())) {
			MessageUtils.addErrorMessage("msg", "该公司的注册资本金变更申请流程正在审批中，请不要重复提交。");
			validate = false;
		}
		return validate;
	}

	/**
	 * 刷新股东列表
	 */
	public void refreshShareHolders() {
		if (isEquityPercChange) {
			try {
				ChangeShareholderVo csv;
				for (ChangeShareholderVo sv : changeShareHolders) {
					List<ChangeShareholderVo> shs = sv.getShareHolders();
					if (shs.size() == 1) {
						csv = new ChangeShareholderVo();
						ConvertUtils.register(new DateConverter(null), java.util.Date.class);
						BeanUtils.copyProperties(csv, shs.get(0));
						csv.setStatus("更新");
						shs.add(csv);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		sumShareHolder();
		for (ChangeShareholderVo csv : changeShareHolders) {
			caculationEquityPerc(csv);
		}
		caculationInvestRegRemaFunds();
	}

	/**
	 * 计算股权比例
	 */
	public void caculationTotal() {
		Double total = sumShareHolder();
		Double fondsTotal = shareHolder.getFondsTotal() == null ? 0d : shareHolder.getFondsTotal();
		if ("edit".equals(operate)) {
			List<ChangeShareholderVo> shs = shareHolder.getShareHolders();
			ChangeShareholderVo vo = shs.get(shs.size() - 1);
			total = total - (vo.getFondsTotal() == null ? 0d : vo.getFondsTotal()) + fondsTotal;
		}
		if ("add".equals(operate)) {
			total = total + fondsTotal;
		}
		Double equityPerc = shareHolder.getEquityPerc();
		shareHolder.setEquityPerc(fondsTotal / total);
		isEquityPercChange = equityPerc != shareHolder.getEquityPerc();
	}

	/**
	 * 计算投注差可用
	 */
	public void caculationInvestRegRemaFunds() {
		if ("1".equals(getInstance().getIsInvestRegRemaAvai())) {
			Double investTotal = getInstance().getInvestTotal() == null ? 0d : getInstance().getInvestTotal();
			getInstance().setInvestRegRemaFunds(investTotal - holderTotal);
		}
	}

	/**
	 * 计算原投资总额、新投资总额
	 * 
	 * @return 新投资总额
	 */
	public Double sumShareHolder() {
		Double sum = 0d;
		Double sumOld = 0d;
		for (ChangeShareholderVo csv : changeShareHolders) {
			List<ChangeShareholderVo> shs = csv.getShareHolders();
			ChangeShareholderVo shareholderVo = shs.get(shs.size() - 1);
			ChangeShareholderVo changeShareholderVo = shs.get(0);
			sumOld = sumOld + (changeShareholderVo.getFondsTotal() == null ? 0d : changeShareholderVo.getFondsTotal());
			if (!"删除".equals(csv.getStatus())) {
				sum = sum + (shareholderVo.getFondsTotal() == null ? 0d : shareholderVo.getFondsTotal());
			}
		}
		holderTotal = sum;
		oldHolderTotal = sumOld;

		return sum;
	}

	/**
	 * 计算股东的原股权比例、新股权比例
	 * 
	 * @param csv
	 */
	public void caculationEquityPerc(ChangeShareholderVo csv) {

		List<ChangeShareholderVo> shareHolders = csv.getShareHolders();

		for (int i = 0; i < shareHolders.size(); i++) {
			ChangeShareholderVo vo = shareHolders.get(i);
			Double fondsTotal = vo.getFondsTotal() == null ? 0d : vo.getFondsTotal();
			if (i == 0) {
				vo.setEquityPerc(oldHolderTotal == 0 ? 0d : fondsTotal / oldHolderTotal);
			} else {
				vo.setEquityPerc(holderTotal == 0 ? 0d : fondsTotal / holderTotal);
			}
		}

	}

	/**
	 * 转换为注册资金变更实体
	 */
	public void convertInstance() {
		ProcRegiCapitalChange change = getInstance();
		String currentUserName = loginService.getCurrentUserName();
		Date createdDatetime = new Date();
		change.setCreatedBy(currentUserName);
		change.setCreatedDatetime(createdDatetime);
		change.setUpdatedBy(currentUserName);
		change.setUpdatedDatetime(createdDatetime);

		List<ProcRegiCapitalChangeShareholder> css = change.getProcRegiCapitalChangeShareholders();
		if (css == null) {
			css = new ArrayList<ProcRegiCapitalChangeShareholder>();
		}
		ProcRegiCapitalChangeShareholder cs = null;
		for (ChangeShareholderVo csv : changeShareHolders) {
			List<ChangeShareholderVo> shareHolders = csv.getShareHolders();
			if (shareHolders != null && shareHolders.size() > 1) {
				ChangeShareholderVo changeShareholderVo = shareHolders.get(1);
				cs = changeShareholderVo.getChangeShareholder();
				if (cs == null) {
					cs = new ProcRegiCapitalChangeShareholder();
					cs.setCreatedBy(currentUserName);
					cs.setCreatedDatetime(createdDatetime);
					css.add(cs);
				}
				cs.setProcRegiCapitalChange(change);
				cs.setEquityPerc(changeShareholderVo.getEquityPerc());
				cs.setEquityPercOriginal(changeShareholderVo.getEquityPercOriginal());
				cs.setFondsCurrency(changeShareholderVo.getFondsCurrency());
				cs.setFondsCurrencyOriginal(changeShareholderVo.getFondsCurrencyOriginal());
				cs.setFondsInPlace(changeShareholderVo.getFondsInPlace());
				cs.setFondsInPlaceOriginal(changeShareholderVo.getFondsInPlaceOriginal());
				cs.setFondsTotal(changeShareholderVo.getFondsTotal());
				cs.setFondsTotalOriginal(changeShareholderVo.getFondsTotalOriginal());
				cs.setIsEquityRelated(changeShareholderVo.getIsEquityRelated());
				cs.setIsEquityRelatedOriginal(changeShareholderVo.getIsEquityRelatedOriginal());
				cs.setRelatedPerc(changeShareholderVo.getRelatedPerc());
				cs.setRelatedPercOriginal(changeShareholderVo.getRelatedPercOriginal());
				cs.setShareholderIdOriginal(changeShareholderVo.getShareholderIdOriginal());
				cs.setShareholderName(changeShareholderVo.getShareholderName());
				cs.setShareholderNameOriginal(changeShareholderVo.getShareholderNameOriginal());
				cs.setStatus(changeShareholderVo.getStatus());

				cs.setUpdatedBy(currentUserName);
				cs.setUpdatedDatetime(createdDatetime);

			}
		}
		change.setProcRegiCapitalChangeShareholders(css);
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
		currencySelect = dictBean.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
	}

	/**
	 * 
	 * <p>
	 * Description: 填充数据
	 * </p>
	 * 
	 * @param workclassNumber
	 */
	public void fillData(String workclassNumber) {
		if (workclassNumber != null) {
			try {
				ProcRegiCapitalChange preg = this.registerCaptialChangeService.findProcRegiCaptialChange(workclassNumber);
				if (preg == null || preg.getCompany() == null) {
					MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("regicapital_noBound"));
					return;
				}
				companyId = preg.getCompany().getId();
				companyNameEn = preg.getCompany().getCompanyEn();
				changeShareHolders = registerCaptialChangeService.findChangeShareholderVosBy(preg);
				refreshShareHolders();
				this.setInstance(preg);
			} catch (Exception e) {
				logger.error("fillData方法 填充数据 出现异常：", e);
			}
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 发起注册资本金变更
	 * </p>
	 * 
	 * @return
	 */
	public String applyRegisterChange() {
		try {
			Company company = this.entityService.find(Company.class, companyId);
			getInstance().setCompany(company);
			if (!validateInstance()) {
				return null;
			}
			String workflowClassName = ProcessXmlUtil.getProcessAttribute("id", "RegiCapitalChange", "className");
			// 设置公司
			String workClassnumber = registerCaptialChangeService.vwCreateProcessInstance(workflowClassName, "注册资本金变更", getInstance().getPeMemo(),
					this.getDocmentIdList());
			getInstance().setProcInstId(workClassnumber);
			getInstance().setCreatedDatetime(DateUtil.strToDate(registerDate, "yyyy-MM-dd").toDate());
			getInstance().setCurrentNode(ProcessXmlUtil.findNextStep(workflowClassName, title));
			getInstance().setProcessStatus("1");
			// 保存流程附件
			this.saveProcessFile(workClassnumber);
			convertInstance();
			registerCaptialChangeService.saveRegisCaptialChange(this.getInstance());
			MessageUtils.addSuccessMessage("doneMsg",
					MessageUtils.getMessage("process_new_success", processUtilMapService.getTmsIdByFnId(workClassnumber)));
			return this.getListPage();
		} catch (ServiceException e) {
			MessageUtils.addErrorMessage("msg", e.getMessage());
			return null;
		} catch (Exception e) {
			if (e instanceof EJBException) {
				try {
					ServiceException se = (ServiceException) ((EJBException) e).getCause();
					MessageUtils.addErrorMessage("msg", se.getMessage());
				} catch (Exception e1) {
					MessageUtils.addErrorMessage("msg", "事务异常");
				}

			}
			if (e instanceof InvocationTargetException) {
				ServiceException se = (ServiceException) ((InvocationTargetException) e).getTargetException().getCause();
				MessageUtils.addErrorMessage("msg", se.getMessage());
			}
			logger.error(e.getMessage(), e);
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
	public String reApplyRegisterChange() {
		try {

			String workClassNum = getInstance().getProcInstId();
			registerCaptialChangeService.vwReplayRegister("重新提交。", workClassNum, this.getDocmentIdList());
			// 保存流程附件
			this.saveProcessFile(workClassNum);
			convertInstance();
			String findNextStep = ProcessXmlUtil.findNextStep(ProcessXmlUtil.getProcessAttribute("id", "RegiCapitalChange", "className"), title);
			getInstance().setCurrentNode(findNextStep);
			getInstance().setUpdatedBy(loginService.getCurrentUserName());
			getInstance().setUpdatedDatetime(new Date());
			entityService.update(this.getInstance());
			MessageUtils.addSuccessMessage("donMsg",
					MessageUtils.getMessage("process_new_success_1", processUtilMapService.getTmsIdByFnId(workClassNum)));
			// 9.3处理下一个任务
			return processUtilService.getNextPage(getInputPage(), doNext, getCurrentIndex(), getCurrentTaskType());
		} catch (ServiceException e) {
			MessageUtils.addErrorMessage("msg", e.getMessage());
			return null;
		} catch (Exception e) {
			if (e instanceof EJBException) {
				try {
					ServiceException se = (ServiceException) ((EJBException) e).getCause();
					MessageUtils.addErrorMessage("msg", se.getMessage());
				} catch (Exception e1) {
					MessageUtils.addErrorMessage("msg", "事务异常");
				}
			}
			if (e instanceof InvocationTargetException) {
				ServiceException se = (ServiceException) ((InvocationTargetException) e).getTargetException().getCause();
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
	public String approveRegisterCaptialChange() {
		try {
			Object obj = JSFUtils.getRequestParam("auit");
			if (getInstance().getProcInstId() == null || "".equals(getInstance().getProcInstId())) {
				MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("process_number_isNull"));
			}
			// 审批通过与否参数
			boolean flag = false;
			if (obj != null && "Y".equals(obj.toString())) {
				flag = true;
			}
			String processFlow = "";
			// 参数Map
			Map<String, Object> paramMap = Maps.newHashMap();
			paramMap.put("_Pass", flag);
			String inputable = ProcessXmlUtil.findStepProperty("id", "RegiCapitalChange", title, "inputable");
			// 审批节点是否需要更新实体
			if (inputable != null && !"".equals(inputable)) {
				convertInstance();
			}
			if (flag) {
				processFlow = ProcessXmlUtil.findStepProperty("id", "RegiCapitalChange", title, "passMemo");
				MessageUtils.addSuccessMessage("doneMsg",
						MessageUtils.getMessage("approve_pass_success", processUtilMapService.getTmsIdByFnId(workObjNum)));
			} else {
				processFlow = ProcessXmlUtil.findStepProperty("id", "RegiCapitalChange", title, "nopassMemo");
				MessageUtils.addSuccessMessage("doneMsg",
						MessageUtils.getMessage("approve_nopass_success", processUtilMapService.getTmsIdByFnId(workObjNum)));
			}
			String findNextStep = ProcessXmlUtil.findNextStep(ProcessXmlUtil.getProcessAttribute("id", "RegiCapitalChange", "className"), title);
			getInstance().setCurrentNode(findNextStep);
			getInstance().setUpdatedBy(loginService.getCurrentUserName());
			getInstance().setUpdatedDatetime(new Date());
			entityService.update(getInstance());

			if ("结束".equals(processFlow)) {
				registerCaptialChangeService.updateCampanyByChange(getInstance());
			}

			registerCaptialChangeService.vwDisposeTask(getInstance().getProcInstId(), paramMap,
					processFlow.concat(getInstance().getPeMemo() == null ? "" : getInstance().getPeMemo()));
			// 9.3处理下一个任务
			return processUtilService.getNextPage(getInputPage(), doNext, getCurrentIndex(), getCurrentTaskType());
		} catch (ServiceException e) {
			MessageUtils.addErrorMessage("msg", e.getMessage());
			return null;
		} catch (Exception e) {
			if (e instanceof EJBException) {
				try {
					ServiceException se = (ServiceException) ((EJBException) e).getCause();
					MessageUtils.addErrorMessage("msg", se.getMessage());
				} catch (Exception e1) {
					MessageUtils.addErrorMessage("msg", "事务异常");
				}
			}
			if (e instanceof InvocationTargetException) {
				ServiceException se = (ServiceException) ((InvocationTargetException) e).getTargetException().getCause();
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
		if ("true".equals(isPatch)) {
			processDetailList = patchMainService.getProcessDetailFor411(workObjNum);
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
	}

	/**
	 * 字段可输入检查
	 * 
	 * @return
	 */
	public boolean checkInputable(String fieldName) {
		if (inputableFields != null && !inputableFields.isEmpty() && inputableFields.contains(fieldName)) {
			return true;
		}
		return false;
	}

	/**
	 * 得到可输入的审批字段
	 */
	private void getInputable(String stepName) {
		inputableFields = ProcessXmlUtil.getInputableDatas("RegiCapitalChange", stepName);
	}

	/**
	 * 关闭、打开对话框
	 * 
	 * @param widgetVar
	 *            对话框
	 * @param option
	 *            关闭、打开
	 */
	public void handleDialogByWidgetVar(String widgetVar, String option) {
		RequestContext context = RequestContext.getCurrentInstance();
		context.addCallbackParam("widgetVar", widgetVar);
		context.addCallbackParam("option", option);
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

	public List<ChangeShareholderVo> getChangeShareHolders() {
		return changeShareHolders;
	}

	public void setChangeShareHolders(List<ChangeShareholderVo> changeShareHolders) {
		this.changeShareHolders = changeShareHolders;
	}

	public ChangeShareholderVo getSelectedHolder() {
		return selectedHolder;
	}

	public void setSelectedHolder(ChangeShareholderVo selectedHolder) {
		this.selectedHolder = selectedHolder;
	}

	public ChangeShareholderVo getShareHolder() {
		return shareHolder;
	}

	public void setShareHolder(ChangeShareholderVo shareHolder) {
		this.shareHolder = shareHolder;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public boolean isViewPage() {
		return "view".equals(JSFUtils.getParamValue("op"));
	}

}
