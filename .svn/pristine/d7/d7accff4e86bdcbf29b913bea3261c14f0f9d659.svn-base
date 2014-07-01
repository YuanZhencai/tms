package com.wcs.tms.view.process.noholdcomploantran;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
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

import com.google.common.collect.Lists;
import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.MathUtil;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.CompanyAccount;
import com.wcs.tms.model.ProcNoHoldCompLoanTran;
import com.wcs.tms.model.ProcNoHoldCompLoanTranDetail;
import com.wcs.tms.service.process.common.ProcessAcceptedService;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessUtilService;
import com.wcs.tms.service.process.common.ProcessWaitAcceptService;
import com.wcs.tms.service.process.common.patch.service.PatchMainService;
import com.wcs.tms.service.process.dailypayloantran.DailyPayLoanTranService;
import com.wcs.tms.service.process.noholdcomploantran.NoHoldCompLoanTranService;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.service.system.company.CompanyAccountServer;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.util.MoneyFormatUtil;
import com.wcs.tms.view.process.common.FileUpload;
import com.wcs.tms.view.process.common.entity.PaymentVo;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;
import com.wcs.tms.view.process.common.entity.ProcessInstanceVo;

import filenet.vw.api.VWException;

/**
 * 
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description:非控股公司借款转款流程Bean
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
public class NoHoldCompLoanTranBean extends FileUpload<ProcNoHoldCompLoanTran> {

	private static final long serialVersionUID = 1L;
	
	private final Log log = LogFactory.getLog(NoHoldCompLoanTranBean.class);
	
	@Inject
    ProcessUtilMapService processUtilMapService;
	
	@Inject
	ProcessUtilService processUtilService;
	
	@Inject 
	DailyPayLoanTranService dailyPayLoanTranService;
	
	@Inject
	CompanyAccountServer companyAccountServer;

	@Inject
	LoginService loginService;

	@Inject
	ProcessWaitAcceptService processWaitAcceptService;

	@Inject
	ProcessAcceptedService processAcceptedService;

	@Inject
	EntityService entityService;

	@Inject
	CompanyTmsService companyTmsService;

	@Inject
	BankService bankService;

	@Inject
	NoHoldCompLoanTranService noHoldCompLoanTranService;
	
	@Inject
    PatchMainService patchMainService;
	@Inject
    PEManager peManager;
	
	//是否为Sungard支付
    private Boolean isSungard = false;
	// 本次支付金额大写
	private String payFundsTotalCapital;
	// 剩余需要支付金额大写
	private String lastFundsCapital;
	// 下一个需要处理的任务
	private ProcessInstanceVo nextProInstance;
	// 剩余未付金额
	private Double lastFunds;
	// 是否继续处理下个任务
	private Boolean doNext = true;
	// 当前所处理任务在任务列表中的位置
    private Integer currentIndex;
    // 当前处理任务类型（已接受和待接收）
    private String currentTaskType;
	// 申请日期
	private String applyDate;
	// 开户行ID
	private String openBankId;
	// 金额汉字大写
	private String fundsTotalCh;
	// 流程实体数据
	private ProcNoHoldCompLoanTran procNoHoldCompLoanTran = new ProcNoHoldCompLoanTran();
	// 流程明细
	 private ProcNoHoldCompLoanTranDetail procNoHoldCompLoanTranDetail = new ProcNoHoldCompLoanTranDetail();
	// 支付列表数据
	private List<PaymentVo> paymentVos = new ArrayList<PaymentVo>();
	// 公司下拉菜单
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
	// 开户行下拉菜单
	private List<SelectItem> bankSelect = new ArrayList<SelectItem>();
	// 账号下拉菜单
	private List<SelectItem> accountSelect = new ArrayList<SelectItem>();

	// 用户队列List
	private List<String> userQueue;
	private List<ProcessInstanceVo> procWaitAcceptVoList = Lists.newArrayList();
	private List<ProcessInstanceVo> procAcceptedVoList = Lists.newArrayList();
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
	//是否是补丁表中获取到数据查看，处理2014.4.11~2014.4.28之间发生故障，丢失的流程数据和流程审批日志的查看
    private String isPatch;

	
	
	/**
     * <p>Description:Bean init </p>
     */
    @PostConstruct
    public void initNoHoldCompLoanTranBean(){
    	log.info("NoHoldCompLoanTranBean.initNoHoldCompLoanTranBean()----");
    	userQueue = getLoginService().getQueueByUser();
    	initOp();
    	// 申请时间
    	setApplyDate(DateUtil.dateToStrShort(
    			DateUtil.dateToDateTime(
    					(procNoHoldCompLoanTran.getCreatedDatetime()))));
    	if("集团资金计划员付款".equals(stepName)){
    		initLastFunds();
    		setLastFundsCapital(MoneyFormatUtil.format(10000*lastFunds));
    	}
    }
	
	
    
    /**
     * 计算出剩余金额并将本次支付金额和剩余需要支付金额转化为大写
     */
    public void formatPayAndLastFunds(){
    	setLastFundsValue();
    	setLastFundsCapital(MoneyFormatUtil.format(10000*lastFunds));
    	setPayFundsTotalCapital(MoneyFormatUtil.format(10000*procNoHoldCompLoanTranDetail.getPayFundsTotal()));
    }
    
    /**
     * 根据“本次支付金额”设置剩余金额
     */
    public void setLastFundsValue(){
    	initLastFunds();
    	lastFunds = lastFunds - procNoHoldCompLoanTranDetail.getPayFundsTotal(); 
    }
    
    /**
     * 初始化“剩余需要支付的金额”
     */
    public void initLastFunds(){
    	List<ProcNoHoldCompLoanTranDetail> details = new ArrayList<ProcNoHoldCompLoanTranDetail>();
    	details = noHoldCompLoanTranService.findDetailByProcInstId(procNoHoldCompLoanTran.getId());
    	if(details.size() == 0){
    		lastFunds = procNoHoldCompLoanTran.getAmount();
    	}else{
	    	Double paiedSum = 0.0;
	    	for(ProcNoHoldCompLoanTranDetail d : details){
	    		paiedSum = paiedSum + d.getPayFundsTotal();
	    	}
	    	lastFunds = procNoHoldCompLoanTran.getAmount() - paiedSum;
    	}
    	procNoHoldCompLoanTranDetail.setPayFundsTotal(lastFunds);
    	paymentFundsChange();
    }
    
    /**
     * 不同方式的付款
     */
    public String doPayment(){
    	try{
    		if(procNoHoldCompLoanTranDetail.getPayFundsTotal() > lastFunds){
    			MessageUtils.addErrorMessage("msg", "不好意思，您支付的金额已超出剩余需要支付金额！");
    			return null;
        	}
    		if(procNoHoldCompLoanTranDetail.getPayFundsTotal() == 0){
    			MessageUtils.addErrorMessage("msg", "不好意思，您支付的金额不能为0！");
    			return null;
    		}
    		procNoHoldCompLoanTranDetail.setPayDatetime( new Date());
    		procNoHoldCompLoanTranDetail.setProcNoHoldCompLoanTran(procNoHoldCompLoanTran);
    		procNoHoldCompLoanTranDetail.setCreatedBy(loginService.getCurrentUserName());
    		// 更新剩余需要支付金额（add on 2013-7-15 by yan ）
			procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().setLastPaiedAmount(MathUtil.roundHalfUp(lastFunds - procNoHoldCompLoanTranDetail.getPayFundsTotal(),4));
			entityService.update(procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran());
    		//SUNGARD支付
	    	if("S".equals(procNoHoldCompLoanTranDetail.getPayWay())){
	    		isSungard = true;
	    			String upload = ProcessXmlUtil.findStepProperty("id", "NoHoldCompLoanTran", stepName, "upload");
	    			if(upload!=null && "true".equals(upload)){
	    				// 保存流程附件
	    	            this.saveProcessFile(procInstId);
	    			}
	    			//调用Sungard接口处理流程
	    			lastFunds = lastFunds - procNoHoldCompLoanTranDetail.getPayFundsTotal(); 
	    			noHoldCompLoanTranService.doPayment(procNoHoldCompLoanTranDetail, lastFunds, stepName,isSungard);
	    			MessageUtils.addSuccessMessage("doneMsg", MessageUtils.getMessage("sungard_payment_suc")+"流程实例编号["+ processUtilMapService.getTmsIdByFnId(procNoHoldCompLoanTran.getProcInstId())+"]");
	    			return processUtilService.getNextPage("/faces/process/common/processDealed-list.xhtml", doNext, currentIndex,currentTaskType);
	    	}
	    	//网银支付
	    	if("O".equals(procNoHoldCompLoanTranDetail.getPayWay())){
	    		isSungard = false;
	    			String upload = ProcessXmlUtil.findStepProperty("id", "NoHoldCompLoanTran", stepName, "upload");
	    			if(upload!=null && "true".equals(upload)){
	    				// 保存流程附件
	    	            this.saveProcessFile(procInstId);
	    			}
	    			lastFunds = lastFunds - procNoHoldCompLoanTranDetail.getPayFundsTotal(); 
	    			noHoldCompLoanTranService.doPayment(procNoHoldCompLoanTranDetail, lastFunds, stepName,isSungard);
	    			MessageUtils.addSuccessMessage("doneMsg", MessageUtils.getMessage("online_payment_suc")+"流程实例编号["+ processUtilMapService.getTmsIdByFnId(procNoHoldCompLoanTran.getProcInstId())+"]");
	    			return processUtilService.getNextPage("/faces/process/common/processDealed-list.xhtml", doNext, currentIndex,currentTaskType);
	    	}
    	} catch (ServiceException e) {
			MessageUtils.addErrorMessage("doneMsg", e.getMessage());
		} catch (Exception e) {
			if(e instanceof EJBException ){
				ServiceException se =(ServiceException)((EJBException)e).getCause();
				MessageUtils.addErrorMessage("doneMsg", se.getMessage());
			}
			if(e instanceof InvocationTargetException){
				ServiceException se =(ServiceException)((InvocationTargetException)e).getTargetException().getCause();
				MessageUtils.addErrorMessage("doneMsg", se.getMessage());
			}
		}
    	return "/faces/process/common/processDealed-list.xhtml";
    }
    
    /**
     * 查询待接收任务
     */
    public void searchProcWaitAccept() {
    	procWaitAcceptVoList.clear();
    	//根据用户队列List查询当前用户所有待接收处理任务
        for (String queueName : userQueue) {
            List<ProcessInstanceVo> list = processWaitAcceptService.vwFindNotReceiveTask(queueName);
            
            if (!list.isEmpty()) {
            	procWaitAcceptVoList.addAll(list);
            }
        }
            
	}
    
    /**
     * 查找已接受任务
     */
    public void searchProcAccepted(){
    	 //查找已接受任务
    	procAcceptedVoList.clear();
		try {
			for (String queueName : userQueue) {
				List<ProcessInstanceVo> list = processAcceptedService.vwfindAcceptTask(queueName);
				if (!list.isEmpty()) {
					procAcceptedVoList.addAll(list);
				}
			}
			} catch (VWException e) {
				log.error("searchProcAccepted方法 错误信息：" + e.getMessage());
			}
    }
    
    /**
     * 查找下一个待接收任务（接收和处理下一任务）
     * @return
     */
    public String searchNextTaskWait(){
    	String url = null;
        if (nextProInstance.getXmlPath() != null && !"".equals(nextProInstance.getXmlPath())) {
            url = nextProInstance.getXmlPath();
        }else{
        	url = processWaitAcceptService.findStepUrl(nextProInstance.getNodeName(), nextProInstance.getProcessCode());	
        }
        if (url == null || "".equals(url)) {
            MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("task_not_bound_form"));
        }
        // 接收任务
        int flag = processWaitAcceptService.vwAcceptTask(this.nextProInstance.getProcInstId());
        if (flag == 1) {
            for (ProcessInstanceVo pin : procWaitAcceptVoList) {
                if (pin != null && nextProInstance != null
                        && nextProInstance.getProcInstId().equals(pin.getProcInstId())) {
                	procWaitAcceptVoList.remove(pin);
                    break;
                }
            }
            searchProcWaitAccept();
            MessageUtils.addWarnMessage("msg", MessageUtils.getMessage("task_accept_by_one_yet"));
            return null;
        }
        JSFUtils.getRequest().setAttribute("procInstId", nextProInstance.getProcInstId());
        JSFUtils.getFlash().put("workObjNum", nextProInstance.getProcInstId());
        JSFUtils.getRequest().setAttribute("op", "approve");
        JSFUtils.getRequest().setAttribute("stepName", nextProInstance.getNodeName());
        return url;
    }
    
    /**
     * 查找下一个已接收任务
     * @return
     */
     
    public String searchNextTaskAccepted(){
    	String url = null;
        if (nextProInstance.getXmlPath() != null && !"".equals(nextProInstance.getXmlPath())) {
            url = nextProInstance.getXmlPath();
        }else{
        	url = processWaitAcceptService.findStepUrl(nextProInstance.getNodeName(), nextProInstance.getProcessCode());	
        }
        if (url == null || "".equals(url)) {
            MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("task_not_bound_form"));
        }
        JSFUtils.getRequest().setAttribute("procInstId", nextProInstance.getProcInstId());
        JSFUtils.getFlash().put("workObjNum", nextProInstance.getProcInstId());
        JSFUtils.getRequest().setAttribute("op", "approve");
        JSFUtils.getRequest().setAttribute("stepName", nextProInstance.getNodeName());
        return url;
    }
    
    /**
     * 终止当前付款
     * @return
     */
    public String terminatePayment(){
    	String url = "/faces/process/common/processDealed-list.xhtml" ;
    	isSungard = false;
    	try{
    		procNoHoldCompLoanTranDetail.setPayDatetime( DateUtil.getNowDateShort().toDate());
    		procNoHoldCompLoanTranDetail.setProcNoHoldCompLoanTran(procNoHoldCompLoanTran);
    		procNoHoldCompLoanTranDetail.setCreatedBy(loginService.getCurrentUserName());
    		Boolean terminated = true;
    			String upload = ProcessXmlUtil.findStepProperty("id", "DailyPayLoanTran", stepName, "upload");
    			if(upload!=null && "true".equals(upload)){
    				// 保存流程附件
    	            this.saveProcessFile(procInstId);
    			}
    			// 更新剩余需要支付金额（注： 终止支付则为0 add on 2013-7-15 by yan ）
    			procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().setLastPaiedAmount(0.0);
    			entityService.update(procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran());
    			
    			noHoldCompLoanTranService.terminatePayment(procNoHoldCompLoanTranDetail, terminated, stepName, isSungard);
    			MessageUtils.addSuccessMessage("doneMsg", MessageUtils.getMessage("终止付款成功！")+"流程实例编号["+ processUtilMapService.getTmsIdByFnId(procNoHoldCompLoanTran.getProcInstId())+"]");
    			return processUtilService.getNextPage("/faces/process/common/processDealed-list.xhtml", doNext, currentIndex,currentTaskType);
    	} catch (ServiceException e) {
			MessageUtils.addErrorMessage("doneMsg", e.getMessage());
			return url;
		} catch (Exception e) {
			if(e instanceof EJBException ){
				ServiceException se =(ServiceException)((EJBException)e).getCause();
				MessageUtils.addErrorMessage("doneMsg", se.getMessage());
			}
			if(e instanceof InvocationTargetException){
				ServiceException se =(ServiceException)((InvocationTargetException)e).getTargetException().getCause();
				MessageUtils.addErrorMessage("doneMsg", se.getMessage());
			}
			return url;
		}
    }
    
    /**
     * 审批保存
     * @return
     */
    public String doApprove(){
    	//验证是否已上传附件
    	boolean pass = false;
    	if(approveStatus!=null){
    		if("Y".equals(approveStatus)){
    			pass = true;
    		}
    		try {
    			procNoHoldCompLoanTran.setLastPaiedAmount(procNoHoldCompLoanTran.getAmount());
    			noHoldCompLoanTranService.doApprove(procNoHoldCompLoanTran , pass , stepName);
    			MessageUtils.addSuccessMessage("doneMsg",stepName + MessageUtils.getMessage("process_step_save_success", 
                		processUtilMapService.getTmsIdByFnId(procInstId)));
    		} catch (ServiceException e) {
    			MessageUtils.addErrorMessage("doneMsg", e.getMessage());
    		} catch (Exception e) {
    			if(e instanceof EJBException ){
    				ServiceException se =(ServiceException)((EJBException)e).getCause();
    				MessageUtils.addErrorMessage("doneMsg", se.getMessage());
    			}
    			if(e instanceof InvocationTargetException){
    				ServiceException se =(ServiceException)((InvocationTargetException)e).getTargetException().getCause();
    				MessageUtils.addErrorMessage("doneMsg", se.getMessage());
    			}
    		}
    	}
    	return processUtilService.getNextPage("/faces/process/common/processDealed-list.xhtml", doNext, currentIndex,currentTaskType);
    }
    
    /**
     * 初始化操作类型
     */
    public void initOp(){
    	String op = (String)JSFUtils.getRequest().getAttribute("op");
    	if(StringUtils.isEmpty(op)){
            op = JSFUtils.getRequestParam("op");
        }
    	if(op!=null && !"".equals(op)){
    		//查看表单详细
    		if("view".equals(op)){
    			isPatch = JSFUtils.getParamValue("isPatch"); 
    			menuTwo = JSFUtils.getRequestParam("menu2");
    			if(StringUtils.isEmpty(menuTwo)){
                    menuTwo = JSFUtils.getRequestParam("menu2");
                }
    			procInstId = (String)JSFUtils.getRequest().getAttribute("procInstId");
    			if(StringUtils.isEmpty(procInstId)){
                    procInstId = JSFUtils.getRequestParam("procInstId");
                }
    			this.findProcInstance();
    			formatFundsTotal();
    			initCompany(true);
    			initBank();
    			bankChange();
    		}
    		//到审批页面
    		if("approve".equals(op)){
    			// add on 2013-5-17
                currentIndex =Integer.parseInt(JSFUtils.getParamValue("currentIndex"));
                currentTaskType = (String)JSFUtils.getParamValue("currentTaskType");
                
    			procInstId = (String)JSFUtils.getRequest().getAttribute("procInstId");
    			stepName = (String)JSFUtils.getRequest().getAttribute("stepName");
    			this.findProcInstance();
    			formatFundsTotal();
    			initCompany(true);
    			initBank();
    			bankChange();
    			//非重新申请页面设置“处理意见”默认值
                if(!"申请".equals(stepName)){
                	procNoHoldCompLoanTran.setPeMemo("同意");
                }
    		}
    	}else{
    		initCompany(false);
    		procNoHoldCompLoanTran.setUpdatedDatetime(DateUtil.getNowDate().toDate());
    	}
    }
    
    /**
     * 计算出剩余金额并将本次支付金额和剩余需要支付金额转化为大写
     */
    public void paymentFundsChange(){
    	procNoHoldCompLoanTranDetail.setPayFundsTotal(MathUtil.cashpoolRound(procNoHoldCompLoanTranDetail.getPayFundsTotal()));
    	setPayFundsTotalCapital(MoneyFormatUtil.format(10000*procNoHoldCompLoanTranDetail.getPayFundsTotal()));
    }
    
    
    /**
     * 查询流程实例
     */
    public void findProcInstance(){
    	procNoHoldCompLoanTran = noHoldCompLoanTranService.findProcInstanceByProcInstId(procInstId);
    	//查询流程附件
    	displayDetailAttach(procNoHoldCompLoanTran.getProcInstId());
    	searchProcessDetail();
    	searchPaymentVos();
    }
    
    /**
     * 查询当前流程实例的支付过程
     */
	private void searchPaymentVos() {
		List<ProcNoHoldCompLoanTranDetail> details = new ArrayList<ProcNoHoldCompLoanTranDetail>();
    	details = noHoldCompLoanTranService.findDetailByProcInstId(procNoHoldCompLoanTran.getId());
    	int i = 1;
    	for(ProcNoHoldCompLoanTranDetail d : details){
    		PaymentVo pv = new PaymentVo();
    		pv.setSerialNumber(i++);
    		pv.setPayDatetime(d.getCreatedDatetime());
    		pv.setPayWay(d.getPayWay());
    		pv.setPayFundsTotal(d.getPayFundsTotal());
    		pv.setPayer(loginService.getCNNameByAccount(d.getCreatedBy()));
    		paymentVos.add(pv);
    	}
	}
    
    /**
     * 查询流程详细步骤
     */
    
    private void searchProcessDetail(){
    	
    	if("true".equals(isPatch)){
    		detailVos = patchMainService.getProcessDetailFor411(procInstId);
    	}else{
    		detailVos = peManager.getProcessDetail(procInstId);
    	}
    }
    
    /**
     * 初始化公司下拉
     */
    public void initCompany(boolean created){
    	if(created){
    		companySelect =noHoldCompLoanTranService.initCompanySelect(procNoHoldCompLoanTran);
    	}else{
    		companySelect = companyTmsService.getCompanySelect();
    	}
    }
    
    /**
     * 创建保存流程
     */
    public String createProcInstance(){
    	//验证是否已上传附件
    	Company company = entityService.find(Company.class, procNoHoldCompLoanTran.getCompany().getId());
    	procNoHoldCompLoanTran.setCompany(company);
    	try {
    		// 剩余需要支付金额 默认为 申请金额（add on 2013-7-19 by yan）
    		procNoHoldCompLoanTran.setLastPaiedAmount(procNoHoldCompLoanTran.getAmount());
    		String procInstId = noHoldCompLoanTranService.createProcInstance(procNoHoldCompLoanTran);
    		// 保存流程附件
            MessageUtils.addSuccessMessage("doneMsg", MessageUtils.getMessage("process_new_success", processUtilMapService.getTmsIdByFnId(procInstId)));
    	} catch (ServiceException e) {
			MessageUtils.addErrorMessage("msg", e.getMessage());
		} catch (Exception e) {
			if(e instanceof EJBException ){
				ServiceException se =(ServiceException)((EJBException)e).getCause();
				MessageUtils.addErrorMessage("msg", se.getMessage());
			}
			if(e instanceof InvocationTargetException){
				ServiceException se =(ServiceException)((InvocationTargetException)e).getTargetException().getCause();
				MessageUtils.addErrorMessage("msg", se.getMessage());
			}
		}
    	return "/faces/process/common/processSubed-list.xhtml";
    }
    
    /**
     * 将金额小写转换为汉字大写
     */
    public void formatFundsTotal(){
    	this.cashpoolAmountProcess();
    	fundsTotalCh = MoneyFormatUtil.format(10000*procNoHoldCompLoanTran.getAmount());
    }
    
    /**
     * 对前台传过来的金额值业务相关处理
     */
    private void cashpoolAmountProcess() {
    	procNoHoldCompLoanTran.setAmount(MathUtil.cashpoolRound(procNoHoldCompLoanTran.getAmount()));
	}



	/**
     * 开户行选择完执行（初始化账号下拉）
     */
    public void bankChange(){
    	List<SelectItem> items = companyAccountServer.findBankSelect(procNoHoldCompLoanTran.getCompany().getId(),
    			procNoHoldCompLoanTran.getAccountDesc());
        setAccountSelect(items);
    }
    
	/**
     * 公司选择后执行的操作
     */
    public void companyChange(){
    	setPayeeName();
    	initBank();
    }
    
    /**
     * 根据“申请公司名”设置“收款人户名”
     */
    public void setPayeeName(){
    	Company company = entityService.find(Company.class, this.procNoHoldCompLoanTran.getCompany().getId());
    	this.procNoHoldCompLoanTran.setCompany(company);
    	this.procNoHoldCompLoanTran.setPayeeName(company.getCompanyName());
    }
    
    /**
     * 初始化银行下拉
     */
    private void initBank() {
    	bankSelect.clear();
    	List<CompanyAccount> companyAccounts = companyAccountServer.findCompanyAccountList(procNoHoldCompLoanTran.getCompany().getId());
        for(CompanyAccount ca : companyAccounts){
        	bankSelect.add(new SelectItem(ca.getAccountDesc(), ca.getAccountDesc()));
        }
	}
    
	
	
	public CompanyAccountServer getCompanyAccountServer() {
		return companyAccountServer;
	}

	public void setCompanyAccountServer(
			CompanyAccountServer companyAccountServer) {
		this.companyAccountServer = companyAccountServer;
	}

	public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}

	public ProcessWaitAcceptService getProcessWaitAcceptService() {
		return processWaitAcceptService;
	}

	public void setProcessWaitAcceptService(
			ProcessWaitAcceptService processWaitAcceptService) {
		this.processWaitAcceptService = processWaitAcceptService;
	}

	public ProcessAcceptedService getProcessAcceptedService() {
		return processAcceptedService;
	}

	public void setProcessAcceptedService(
			ProcessAcceptedService processAcceptedService) {
		this.processAcceptedService = processAcceptedService;
	}

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

	public BankService getBankService() {
		return bankService;
	}

	public void setBankService(BankService bankService) {
		this.bankService = bankService;
	}

	public NoHoldCompLoanTranService getNoHoldCompLoanTranService() {
		return noHoldCompLoanTranService;
	}

	public void setNoHoldCompLoanTranService(
			NoHoldCompLoanTranService noHoldCompLoanTranService) {
		this.noHoldCompLoanTranService = noHoldCompLoanTranService;
	}

	public String getPayFundsTotalCapital() {
		return payFundsTotalCapital;
	}

	public void setPayFundsTotalCapital(String payFundsTotalCapital) {
		this.payFundsTotalCapital = payFundsTotalCapital;
	}

	public String getLastFundsCapital() {
		return lastFundsCapital;
	}

	public void setLastFundsCapital(String lastFundsCapital) {
		this.lastFundsCapital = lastFundsCapital;
	}

	public ProcessInstanceVo getNextProInstance() {
		return nextProInstance;
	}

	public void setNextProInstance(ProcessInstanceVo nextProInstance) {
		this.nextProInstance = nextProInstance;
	}

	public Double getLastFunds() {
		return lastFunds;
	}

	public void setLastFunds(Double lastFunds) {
		this.lastFunds = lastFunds;
	}

	public Boolean getDoNext() {
		return doNext;
	}

	public void setDoNext(Boolean doNext) {
		this.doNext = doNext;
	}

	public String getOpenBankId() {
		return openBankId;
	}

	public void setOpenBankId(String openBankId) {
		this.openBankId = openBankId;
	}

	public String getFundsTotalCh() {
		return fundsTotalCh;
	}

	public void setFundsTotalCh(String fundsTotalCh) {
		this.fundsTotalCh = fundsTotalCh;
	}

	public ProcNoHoldCompLoanTran getProcNoHoldCompLoanTran() {
		return procNoHoldCompLoanTran;
	}

	public void setProcNoHoldCompLoanTran(
			ProcNoHoldCompLoanTran procNoHoldCompLoanTran) {
		this.procNoHoldCompLoanTran = procNoHoldCompLoanTran;
	}

	public List<PaymentVo> getPaymentVos() {
		return paymentVos;
	}

	public void setPaymentVos(List<PaymentVo> paymentVos) {
		this.paymentVos = paymentVos;
	}

	public List<SelectItem> getCompanySelect() {
		return companySelect;
	}

	public void setCompanySelect(List<SelectItem> companySelect) {
		this.companySelect = companySelect;
	}

	public List<SelectItem> getBankSelect() {
		return bankSelect;
	}

	public void setBankSelect(List<SelectItem> bankSelect) {
		this.bankSelect = bankSelect;
	}

	public List<SelectItem> getAccountSelect() {
		return accountSelect;
	}

	public void setAccountSelect(List<SelectItem> accountSelect) {
		this.accountSelect = accountSelect;
	}

	public List<String> getUserQueue() {
		return userQueue;
	}

	public void setUserQueue(List<String> userQueue) {
		this.userQueue = userQueue;
	}

	public List<ProcessInstanceVo> getProcWaitAcceptVoList() {
		return procWaitAcceptVoList;
	}

	public void setProcWaitAcceptVoList(
			List<ProcessInstanceVo> procWaitAcceptVoList) {
		this.procWaitAcceptVoList = procWaitAcceptVoList;
	}

	public List<ProcessInstanceVo> getProcAcceptedVoList() {
		return procAcceptedVoList;
	}

	public void setProcAcceptedVoList(List<ProcessInstanceVo> procAcceptedVoList) {
		this.procAcceptedVoList = procAcceptedVoList;
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


	public ProcNoHoldCompLoanTranDetail getProcNoHoldCompLoanTranDetail() {
		return procNoHoldCompLoanTranDetail;
	}


	public void setProcNoHoldCompLoanTranDetail(
			ProcNoHoldCompLoanTranDetail procNoHoldCompLoanTranDetail) {
		this.procNoHoldCompLoanTranDetail = procNoHoldCompLoanTranDetail;
	}



	public String getApplyDate() {
		return applyDate;
	}



	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}



	public Boolean getIsSungard() {
		return isSungard;
	}



	public void setIsSungard(Boolean isSungard) {
		this.isSungard = isSungard;
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
