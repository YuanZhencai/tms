package com.wcs.tms.view.process.debtpayment;

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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Maps;
import com.wcs.base.util.JSFUtils;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.DebtContract;
import com.wcs.tms.model.ProcDebtBorrow;
import com.wcs.tms.model.ProcDebtPayment;
import com.wcs.tms.model.RemittanceLineAccount;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessUtilService;
import com.wcs.tms.service.process.debtpayment.DebtPaymentService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.FileUpload;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;
import com.wcs.tms.view.process.debtborrow.DebtBorrowBean;
@ManagedBean
@ViewScoped
public class DebtPaymentBean extends FileUpload<ProcDebtPayment>{
	private static final long serialVersionUID = 713786652202859694L;
	 @Inject
	 CommonBean dictBean;
	 @Inject
	 CompanyTmsService companyTmsService;
	 @Inject
	 DebtPaymentService debtPaymentService;
	 //外债请款Obj
    private ProcDebtPayment procDebtPayment = new ProcDebtPayment();
    /** 资金币种下拉*/
    private List<SelectItem> currencySelect = new ArrayList<SelectItem>(); 
    /** 资金提供方公司下拉*/
    private List<SelectItem> dirComSelect = new ArrayList<SelectItem>();
    //公司下拉菜单
    private List<SelectItem> companySelect = new ArrayList<SelectItem>();
    @Inject
    ProcessUtilMapService processUtilMapService;//9.10
    @Inject
    ProcessUtilService processUtilService;//9.3
    /*** 选择汇款线路* */
    private List<RemittanceLineAccount> debtAcNoList;
    /** * 被选中的汇款线路* */
    private RemittanceLineAccount selAcNo;
    /*** 新增汇款线路* */
    private RemittanceLineAccount addAcNo=new RemittanceLineAccount();
    /*** 外债合同* */
    private List<DebtContract> debtContractList;
	private String menuTwo;//二级菜单参数
	/*** 主页面外债合同* */
	private DebtContract mainContract=new DebtContract();
	/*** 选中的外债合同* */
	private DebtContract selContract=new DebtContract();
	// 当前所处理任务在任务列表中的位置
    private Integer currentIndex;
    // 当前处理任务类型（已接受和待接收）
    private String currentTaskType;
    //流程实例ID
    private String procInstId ;
    //流程步骤名称
    private String stepName;
    //得到可输入的审批字段
    private List<String> inputableFields = new ArrayList<String>();
    //流程详细vo列表
    private List<ProcessDetailVo> detailVos = new ArrayList<ProcessDetailVo>();
	private boolean doNext = true; //是否处理下一个任务
    private String title;	/** 审批页面标题*/
    private String workObjNum; /** 用于查看审批详细步骤，附件时用的全局参数*/

	public boolean isDoNext() {
		return doNext;
	}

	public ProcessUtilService getProcessUtilService() {
		return processUtilService;
	}

	public void setProcessUtilService(ProcessUtilService processUtilService) {
		this.processUtilService = processUtilService;
	}

	public String getWorkObjNum() {
		return workObjNum;
	}

	public void setWorkObjNum(String workObjNum) {
		this.workObjNum = workObjNum;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDoNext(boolean doNext) {
		this.doNext = doNext;
	}

	public List<ProcessDetailVo> getDetailVos() {
		return detailVos;
	}

	public void setDetailVos(List<ProcessDetailVo> detailVos) {
		this.detailVos = detailVos;
	}

	public List<String> getInputableFields() {
		return inputableFields;
	}

	public void setInputableFields(List<String> inputableFields) {
		this.inputableFields = inputableFields;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
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

	public DebtContract getSelContract() {
		return selContract;
	}

	public void setSelContract(DebtContract selContract) {
		this.selContract = selContract;
	}

	public ProcessUtilMapService getProcessUtilMapService() {
		return processUtilMapService;
	}

	public void setProcessUtilMapService(ProcessUtilMapService processUtilMapService) {
		this.processUtilMapService = processUtilMapService;
	}

	public DebtContract getMainContract() {
		return mainContract;
	}

	public void setMainContract(DebtContract mainContract) {
		this.mainContract = mainContract;
	}

	public RemittanceLineAccount getAddAcNo() {
		return addAcNo;
	}

	public void setAddAcNo(RemittanceLineAccount addAcNo) {
		this.addAcNo = addAcNo;
	}

	public RemittanceLineAccount getSelAcNo() {
		return selAcNo;
	}

	public void setSelAcNo(RemittanceLineAccount selAcNo) {
		this.selAcNo = selAcNo;
	}

	public DebtPaymentService getDebtPaymentService() {
		return debtPaymentService;
	}

	public void setDebtPaymentService(DebtPaymentService debtPaymentService) {
		this.debtPaymentService = debtPaymentService;
	}

	public List<RemittanceLineAccount> getDebtAcNoList() {
		return debtAcNoList;
	}

	public void setDebtAcNoList(List<RemittanceLineAccount> debtAcNoList) {
		this.debtAcNoList = debtAcNoList;
	}

	public List<DebtContract> getDebtContractList() {
		return debtContractList;
	}

	public void setDebtContractList(List<DebtContract> debtContractList) {
		this.debtContractList = debtContractList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static Log getLog() {
		return log;
	}

	private static final Log log = LogFactory.getLog(DebtPaymentBean.class);
    @PostConstruct
    public void initDebtPayment(){
        log.info("init DebtPaymentBean~~~~~~~~~~~~~~");
        initDict();
        initOp();
    }
	
    /**
     * 初始化数据字典
     */
    public void initDict(){
        currencySelect = dictBean.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
        dirComSelect = dictBean.getDictByCode(DictConsts.TMS_FUND_PROVIDER_COM_NAME);
    }
    
    /**
     * 初始化操作类型
     */
    public void initOp(){
    	String op =  JSFUtils.getParamValue("op");
    	super.setWorkClassName("TMS_ProcDebtPayment");
    	if(op!=null && !"".equals(op)){
    		//查看表单详细
    		if("view".equals(op)){
    			procInstId = (String)JSFUtils.getRequest().getAttribute("procInstId");
    			if(StringUtils.isEmpty(procInstId)){
                    procInstId = JSFUtils.getRequestParam("procInstId");
                } 
    			this.findProcInstance();
    			initCompany(true);
    		}
    		//到审批页面
    		if("approve".equals(op)){
                currentIndex =Integer.parseInt(JSFUtils.getParamValue("currentIndex"));
                currentTaskType = (String)JSFUtils.getParamValue("currentTaskType");
    			procInstId = (String)JSFUtils.getRequest().getAttribute("procInstId");
    			stepName = (String)JSFUtils.getRequest().getAttribute("stepName");
    			this.findProcInstance();
    			initCompany(true);
    			makeMainContract();
    			 if(!"工厂资金岗位人员申请".equals(stepName)){
                 	procDebtPayment.setPeMemo("同意");
                 }
    		}
    	}else{
    		procDebtPayment.setCreatedDatetime(new Date());
    		initCompany(false);
    		this.procDebtPayment.setIsHasDebtContract("Y");//初始化默认已有外债合同
    		this.procDebtPayment.setAfceFlag("N");//初始化默认为非AFCE
    		this.procDebtPayment.setCreatedBy(this.debtPaymentService.getLoginService().getCurrentUserName());
    		this.procDebtPayment.setCreatedBy(super.findUserName());
    	}
    } 
	
    public void makeMainContract(){
	    	this.mainContract=this.procDebtPayment.getDebtContract();
    }
    /**
     * 查询外资合同
     * */
    public void ajaxDebtContract(){
    	Long comId=this.procDebtPayment.getCompany().getId();
    	if(comId==null){
    		 MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("msg_required_companyNameCn"));
    		 return;
    	}
    	this.debtContractList=this.debtPaymentService.getPaymentDebtContract(comId);
    }
    /*** 初始化公司下拉*/
    public void initCompany(boolean all){
    	if(all){
    		companySelect = companyTmsService.getAllCompanySelect();
    	}else{
    		companySelect = companyTmsService.getCompanySelect();
    	}
    }

    
    public void ajaxAfce(){
    	procDebtPayment.setAfceSign(null);
    	procDebtPayment.setAfcePaid(null);
    	procDebtPayment.setAfceExcRate(null);
    	procDebtPayment.setAfceSignExc(null);
    }
    
    public void changeComp(){
    	Company com = this.entityService.find(Company.class, procDebtPayment.getCompany().getId());
    	procDebtPayment.setCompany(com);  
    	procDebtPayment.setDebtRequester(com.getCompanyEn());
    	procDebtPayment.setDebtRequesterAddr(com.getAddrEn());
    }
    
    /** * 选中汇款线路后会写到主页面* */
    public void ajaxSelAc()throws Exception{
    	if(selAcNo!=null)
    	PropertyUtils.copyProperties( procDebtPayment.getRemittanceLineAccount(),this.selAcNo);
    }
    /** 选择汇款线路* */
    public void ajaxSelAcNoList(){
    	if(procDebtPayment.getCompany().getId()==null){
    		MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("msg_required_companyNameCn"));
    	}else{
    		this.debtAcNoList=this.debtPaymentService.getSelAcNoList(this.procDebtPayment.getCompany().getId());
    	}
    }
    /**选中外债合同*/
    public void ajaxSelContract()throws Exception{
    	DebtContract dc=this.procDebtPayment.getDebtContract();
    	PropertyUtils.copyProperties(this.mainContract,this.selContract);
    	dc.setId(this.selContract.getId());
    	this.procDebtPayment.setApplyFundsCu(selContract.getDebtContractFundsCu());
    	this.procDebtPayment.setAfceSign(null);
    	this.procDebtPayment.setDebtProvider(selContract.getShareHolder().getShareHolderName()); 
    }
    /**初始化新增汇款**/
    public void ajaxShowAcWin(){
    	addAcNo=new RemittanceLineAccount();
    }
    /**
     * 新增汇款新路
     * */
    public void ajaxAddAcNo(){
    	if(procDebtPayment.getCompany().getId()==null){
    		MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("msg_required_companyNameCn"));
    	}else{
    		addAcNo.setDefunctInd("N");
    		addAcNo.setCompany(this.procDebtPayment.getCompany());
    		this.debtPaymentService.saveAcNo(addAcNo);
    		this.procDebtPayment.setRemittanceLineAccount(addAcNo);
    	}
    }
    public void ajaxDisCapital(){
    	this.debtPaymentService.disComp(procDebtPayment);
    }
    /**校验可请款金额*/
    public void ajaxCalcQkje(){
    	try{
    		if(this.procDebtPayment.getApplyFunds()!=null&&this.procDebtPayment.getApplyFunds()>this.mainContract.getUnAppliedFunds()){
    		  MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("errMsg_qkje_dywqkje"));
    		  this.procDebtPayment.setApplyFunds(this.mainContract.getUnAppliedFunds());
    		}
    	}catch(Exception e){
    		this.procDebtPayment.setApplyFunds(this.mainContract.getUnAppliedFunds());
    	}
    }
    /**AFCE签批情况(折算值)：*/
    public void ajaxAfceCalc(Double r,Double s){
    	if(r!=null&&s!=null){
    		Double d=r*s;
    		this.procDebtPayment.setAfceSignExc(d);
    	}
    }
    /**
     * 流程创建保存
     */
    public String createProcInstance(){

    	try {
    		String procInstId = debtPaymentService.createProcInstance(procDebtPayment);
    		// 保存流程附件
            this.saveProcessFile(procInstId);
            MessageUtils.addSuccessMessage("doneMsg", MessageUtils.getMessage("process_new_success", processUtilMapService.getTmsIdByFnId(procInstId)));
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
    	return "/faces/process/common/processSubed-list.xhtml";
    }
    
	public CommonBean getDictBean() {
		return dictBean;
	}
 
	public void setDictBean(CommonBean dictBean) {
		this.dictBean = dictBean;
	}
 
	public CompanyTmsService getCompanyTmsService() {
		return companyTmsService;
	}

	public void setCompanyTmsService(CompanyTmsService companyTmsService) {
		this.companyTmsService = companyTmsService;
	}

	public ProcDebtPayment getProcDebtPayment() {
		return procDebtPayment;
	}

	public void setProcDebtPayment(ProcDebtPayment procDebtPayment) {
		this.procDebtPayment = procDebtPayment;
	}

	public List<SelectItem> getCurrencySelect() {
		return currencySelect;
	}

	public void setCurrencySelect(List<SelectItem> currencySelect) {
		this.currencySelect = currencySelect;
	}

	public List<SelectItem> getDirComSelect() {
		return dirComSelect;
	}

	public void setDirComSelect(List<SelectItem> dirComSelect) {
		this.dirComSelect = dirComSelect;
	}

	public List<SelectItem> getCompanySelect() {
		return companySelect;
	}

	public void setCompanySelect(List<SelectItem> companySelect) {
		this.companySelect = companySelect;
	}

	public String getMenuTwo() {
		return menuTwo;
	}

	public void setMenuTwo(String menuTwo) {
		this.menuTwo = menuTwo;
	}
	
    
    /**
     * 查询流程实例
     */
    public void findProcInstance(){
    	this.procDebtPayment = this.debtPaymentService.findProcInstanceByProcInstId(procInstId);
		//查询流程详细
    	searchProcessDetail();
    	//查询流程附件
    	displayDetailAttach(procDebtPayment.getProcInstId());
    }
    /**
     * 查询流程详细步骤
     */
    private void searchProcessDetail(){
    	detailVos = this.debtPaymentService.getProcessDetail(procInstId);
    }
    
    //审核
    public String doApprove(String flag){
        try {
        	this.procInstId=this.procDebtPayment.getProcInstId();
            String msgStr="";
            Boolean flagBool=false;
            if (flag.equals("Y")) {
            	flagBool=true;
            } 
            MessageUtils.addSuccessMessage("doneMsg", stepName+MessageUtils.getMessage("process_save_success", processUtilMapService.getTmsIdByFnId(procInstId)));
            this.debtPaymentService.doApprove(this.procDebtPayment,flagBool,stepName);
            this.saveProcessFile(procInstId);
            //9.3处理下一个任务
            return processUtilService.getNextPage("/faces/process/common/processDealed-list.xhtml", doNext, getCurrentIndex(),getCurrentTaskType());
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
}
