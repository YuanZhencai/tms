package com.wcs.tms.view.process.regicapitalCharge;

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

import com.wcs.base.service.EntityService;
import com.wcs.base.util.JSFUtils;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcDebtPayment;
import com.wcs.tms.model.ProcRegiCapitalChange;
import com.wcs.tms.model.ProcRegiCapitalChangeShareholder;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessUtilService;
import com.wcs.tms.service.process.regicapitalCharge.RegicapitalChargeService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.service.system.company.ShareHolderService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.FileUpload;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;
@ManagedBean
@ViewScoped
public class RegicapitalChargeBean extends FileUpload<ProcDebtPayment>{
	
	private static final long serialVersionUID = 713786652202859694L;
	
	@Inject
	CommonBean dictBean;
 
	@Inject
	CompanyTmsService companyTmsService;
 
	@Inject
	ShareHolderService shareHolderService;
	
	@Inject
	RegicapitalChargeService regicapitalChargeService;
	
	@Inject
    EntityService entityService;
	
	@Inject
    ProcessUtilService processUtilService;
	
	//注册资本金变更Obj
	private ProcRegiCapitalChange procRegiCapitalChange = new ProcRegiCapitalChange();
	//公司下拉菜单
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
	//资金币种下拉
	private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
	//股东列表
	public List<ShareHolder> shareHolderList = new ArrayList<ShareHolder>();
	//变更流程股东列表
	public List<ProcRegiCapitalChangeShareholder> prccShareHolderList = new ArrayList<ProcRegiCapitalChangeShareholder>();
	//股东对象
	private ShareHolder shareHolder = new ShareHolder();
	//变更流程股东对象
	private ProcRegiCapitalChangeShareholder procRegiCapitalChangeShareholder = new ProcRegiCapitalChangeShareholder();
	// 当前所处理任务在任务列表中的位置
    private Integer currentIndex;
    // 当前处理任务类型（已接受和待接收）
    private String currentTaskType;
    //是否处理下一个任务
    private boolean doNext = true; 
    //流程实例ID
    private String procInstId ;
    //流程步骤名称
    private String stepName;
    //临时股东Id
    private String tempShareholderId = "";
    //是否删除
    private String isDel = "";
    //是否恢复
    private String isCal = "";
    //流程详细vo列表
    private List<ProcessDetailVo> detailVos = new ArrayList<ProcessDetailVo>();
	
	public ShareHolder getShareHolder() {
		return shareHolder;
	}

	public void setShareHolder(ShareHolder shareHolder) {
		this.shareHolder = shareHolder;
	}
	//
	private static final Log log = LogFactory.getLog(RegicapitalChargeBean.class);

	@Inject
	ProcessUtilMapService processUtilMapService;//9.10    

	public List<ShareHolder> getShareHolderList() {
		return shareHolderList;
	}

	public void setShareHolderList(List<ShareHolder> shareHolderList) {
		this.shareHolderList = shareHolderList;
	}

	public ProcessUtilMapService getProcessUtilMapService() {
		return processUtilMapService;
	}

	public void setProcessUtilMapService(ProcessUtilMapService processUtilMapService) {
		this.processUtilMapService = processUtilMapService;
	}

	public ShareHolderService getShareHolderService() {
		return shareHolderService;
	}

	public void setShareHolderService(ShareHolderService shareHolderService) {
		this.shareHolderService = shareHolderService;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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

	public List<SelectItem> getCurrencySelect() {
		return currencySelect;
	}

	public void setCurrencySelect(List<SelectItem> currencySelect) {
		this.currencySelect = currencySelect;
	}

	public List<SelectItem> getCompanySelect() {
		return companySelect;
	}

	public void setCompanySelect(List<SelectItem> companySelect) {
		this.companySelect = companySelect;
	}
	
	public ProcRegiCapitalChange getProcRegiCapitalChange() {
		return procRegiCapitalChange;
	}

	public void setProcRegiCapitalChange(ProcRegiCapitalChange procRegiCapitalChange) {
		this.procRegiCapitalChange = procRegiCapitalChange;
	}

	public static Log getLog() {
		return log;
	}
	
    public List<ProcRegiCapitalChangeShareholder> getPrccShareHolderList() {
		return prccShareHolderList;
	}

	public void setPrccShareHolderList(
			List<ProcRegiCapitalChangeShareholder> prccShareHolderList) {
		this.prccShareHolderList = prccShareHolderList;
	}

	public ProcRegiCapitalChangeShareholder getProcRegiCapitalChangeShareholder() {
		return procRegiCapitalChangeShareholder;
	}

	public void setProcRegiCapitalChangeShareholder(
			ProcRegiCapitalChangeShareholder procRegiCapitalChangeShareholder) {
		this.procRegiCapitalChangeShareholder = procRegiCapitalChangeShareholder;
	}

	public List<ProcessDetailVo> getDetailVos() {
		return detailVos;
	}

	public void setDetailVos(List<ProcessDetailVo> detailVos) {
		this.detailVos = detailVos;
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

	public boolean isDoNext() {
		return doNext;
	}

	public void setDoNext(boolean doNext) {
		this.doNext = doNext;
	}

	@PostConstruct
    public void initRegicapitalCharge(){
        initDict();
        initOp();
    }
    /**
     * 初始化数据字典
     */
    public void initDict(){
        currencySelect = dictBean.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
    }
    /**
     * 初始化公司下拉
     */
    public void initCompany(boolean all){
    	if(all){
    		companySelect = companyTmsService.getAllCompanySelect();
    	}else{
    		companySelect = companyTmsService.getCompanySelect();
    	}
    }
    /**
     * 初始化操作类型
     */
    public void initOp(){
    	String op = (String)JSFUtils.getRequest().getAttribute("op");
    	super.setWorkClassName("TMS_ProcRegICapitalChange");
    	if(StringUtils.isEmpty(op)){
            op = JSFUtils.getRequestParam("op");
        }
    	if(op!=null && !"".equals(op)){
    		//查看表单详细
    		if("view".equals(op)){
    			procInstId = (String)JSFUtils.getRequest().getAttribute("procInstId");
    			if(StringUtils.isEmpty(procInstId)){
                    procInstId = JSFUtils.getRequestParam("procInstId");
                } 
    			//
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
    			if(!"工厂资金人员申请".equals(stepName)){
    				procRegiCapitalChange.setPeMemo("同意");
    			}
    		}
    	}else{
    		procRegiCapitalChange.setCreatedDatetime(new Date());
    		initCompany(false);
    	}
    }
    /**
     * 选择公司时联动
     */
	public void changeComp(){
		//带很多信息出来
		Company com = this.entityService.find(Company.class, procRegiCapitalChange.getCompany().getId());
		procRegiCapitalChange.setCompany(com);  
		//
		Long companyId = procRegiCapitalChange.getCompany().getId();
		shareHolderList = shareHolderService.findShareHolderListByCp(companyId);
		for(int i=0; i<shareHolderList.size(); i++){
			ProcRegiCapitalChangeShareholder prccs = new ProcRegiCapitalChangeShareholder();
			prccs.setShareholderIdOriginal(shareHolderList.get(i).getId());
			prccs.setShareholderNameOriginal(shareHolderList.get(i).getShareHolderName());
			prccs.setFondsTotalOriginal(shareHolderList.get(i).getFondsTotal());
			prccs.setFondsCurrencyOriginal(shareHolderList.get(i).getFondsCurrency());
			prccs.setEquityPercOriginal(shareHolderList.get(i).getEquityPerc());
			prccs.setFondsInPlaceOriginal(shareHolderList.get(i).getFondsInPlace());
			prccs.setIsEquityRelatedOriginal(shareHolderList.get(i).getIsEquityRelated());
			prccs.setRelatedPercOriginal(shareHolderList.get(i).getRelatedPerc());
			prccShareHolderList.add(prccs);
		}
	}
	/**
	 * 添加股东
	 */
	public void shareholderSubmit(){
		List<ProcRegiCapitalChangeShareholder> tempShareHolderList = new ArrayList<ProcRegiCapitalChangeShareholder>();
		for(int i=0; i<prccShareHolderList.size(); i++){
			ProcRegiCapitalChangeShareholder prccs = prccShareHolderList.get(i);
			String shareholderId = "";
//			if(null != prccs.getShareholderIdOriginal()){
//				shareholderId = prccs.getShareholderIdOriginal().toString();	
//			}
			System.out.println("shareholderId" + shareholderId);
			System.out.println("tempShareholderId" + tempShareholderId);
			System.out.println("isDel" + isDel);
			System.out.println("isCal" + isCal);
			System.out.println(!"".equals(tempShareholderId));
			System.out.println(null != tempShareholderId);
			System.out.println(tempShareholderId.equals(shareholderId));
			System.out.println("".equals(isDel));
			System.out.println(!"".equals(isCal));
			if(!"".equals(tempShareholderId)
					&&null != tempShareholderId
					&&tempShareholderId.equals(shareholderId)
					&&"".equals(isDel)
					&&"".equals(isCal)){
				//编辑
				System.out.println("shareholderId=======bianji");
				prccs.setShareholderIdOriginal(new Long(tempShareholderId));
				prccs.setShareholderName(procRegiCapitalChangeShareholder.getShareholderName());
				prccs.setFondsTotal(procRegiCapitalChangeShareholder.getFondsTotal());
				prccs.setFondsCurrency(procRegiCapitalChangeShareholder.getFondsCurrency());
				prccs.setEquityPerc(procRegiCapitalChangeShareholder.getEquityPerc());
				prccs.setFondsInPlaceOriginal(procRegiCapitalChangeShareholder.getFondsInPlace());
				prccs.setIsEquityRelated(procRegiCapitalChangeShareholder.getIsEquityRelated());
				prccs.setRelatedPerc(procRegiCapitalChangeShareholder.getRelatedPerc());
				prccs.setStatus("更新");
			}else if(!"".equals(tempShareholderId)
					&&null != tempShareholderId
					&&tempShareholderId.equals(shareholderId)
					&&!"".equals(isDel)
					&&"".equals(isCal)){
				//删除
				System.out.println("shareholderId=======shanchu");
				prccs.setDefunctInd("Y");
				prccs.setStatus("删除");
			}else if(!"".equals(tempShareholderId)
					&&null != tempShareholderId
					&&tempShareholderId.equals(shareholderId)
					&&"".equals(isDel)
					&&!"".equals(isCal)){
				//撤销
				System.out.println("shareholderId=======chexiao");
				prccs.setDefunctInd("N");
				prccs.setStatus("更新");
			}
//			String sOName = prccs.getShareholderNameOriginal();
//    		String sName = prccs.getShareholderName();
//			String defunctInd = prccs.getDefunctInd();
//			if("Y".equals(defunctInd)){
//				prccs.setStatus("删除");
//    		}else{
//    			if("".equals(sOName)||null == sOName){
//    				prccs.setStatus("新增");
//        		}else if((!"".equals(sOName)
//        				&&null != sOName)
//        				&&(!"".equals(sName)
//        				&&null != sName)){
//        			prccs.setStatus("更新");
//        		}	
//    		}
			tempShareHolderList.add(prccs);
		}
		if(("".equals(tempShareholderId) || null == tempShareholderId)
				&&"".equals(isDel)
				&&"".equals(isCal)){
			//添加
			procRegiCapitalChangeShareholder.setStatus("新增");
			tempShareHolderList.add(procRegiCapitalChangeShareholder);
			System.out.println("shareholderId=======tianjia"+tempShareHolderList.size());
		}
		prccShareHolderList = tempShareHolderList;
	}
	/**
	 * 添加股东
	 */
	public void abcde(){
		System.out.println("prccShareHolderList.size()"+prccShareHolderList.size());
		List<ProcRegiCapitalChangeShareholder> tempShareHolderList = new ArrayList<ProcRegiCapitalChangeShareholder>();
		for(int i=0; i<prccShareHolderList.size(); i++){
			tempShareHolderList.add(prccShareHolderList.get(i));
		}
		tempShareHolderList.add(procRegiCapitalChangeShareholder);
		prccShareHolderList = tempShareHolderList;
		System.out.println("prccShareHolderList.size()-------------------"+prccShareHolderList.size());
	}
	/**
	 * 编辑股东
	 */
	public void shareholderAdd(){
		tempShareholderId = "";
		isDel = "";
		isCal = "";
		this.procRegiCapitalChangeShareholder = new ProcRegiCapitalChangeShareholder();
	}
	/**
	 * 编辑股东
	 */
	public void shareholderEdit(Long shareholderId){
		tempShareholderId = shareholderId.toString();
		isDel = "";
		isCal = "";
		for(int i=0; i<prccShareHolderList.size(); i++){
			ProcRegiCapitalChangeShareholder prccs = prccShareHolderList.get(i);
//			System.out.println("------99999------"+prccs.getShareholderIdOriginal().toString());
//			System.out.println("------99999tempShareholderId------"+tempShareholderId);
//			if(null != prccs.getShareholderIdOriginal()){
//				if(tempShareholderId.equals(prccs.getShareholderIdOriginal().toString())){
//					this.procRegiCapitalChangeShareholder.setShareholderName(prccs.getShareholderNameOriginal());
//					this.procRegiCapitalChangeShareholder.setFondsTotal(prccs.getFondsTotalOriginal());
//					this.procRegiCapitalChangeShareholder.setFondsCurrency(prccs.getFondsCurrencyOriginal());
////					prccs.setShareholderNameOriginal(shareHolderList.get(i).getShareHolderName());
////					prccs.setFondsTotalOriginal(shareHolderList.get(i).getFondsTotal());
////					prccs.setFondsCurrencyOriginal(shareHolderList.get(i).getFondsCurrency());
//					this.procRegiCapitalChangeShareholder.setEquityPerc(prccs.getEquityPercOriginal());
//					this.procRegiCapitalChangeShareholder.setFondsInPlace(prccs.getFondsInPlaceOriginal());
//					this.procRegiCapitalChangeShareholder.setIsEquityRelated(prccs.getIsEquityRelatedOriginal());
//					this.procRegiCapitalChangeShareholder.setRelatedPerc(prccs.getRelatedPercOriginal());
////					prccs.setEquityPercOriginal(shareHolderList.get(i).getEquityPerc());
////					prccs.setFondsInPlaceOriginal(shareHolderList.get(i).getFondsInPlace());
////					prccs.setIsEquityRelatedOriginal(shareHolderList.get(i).getIsEquityRelated());
////					prccs.setRelatedPercOriginal(shareHolderList.get(i).getRelatedPerc());
//				}
//			}
		}
	}
	/**
	 * 删除股东
	 */
	public void shareholderDel(Long shareholderId){
		tempShareholderId = shareholderId.toString();
		isDel = "Y";
		isCal = "";
		System.out.println("---------shanchu--------------");
	}
	/**
	 * 恢复股东
	 */
	public void shareholderCal(Long shareholderId){
		tempShareholderId = shareholderId.toString();
		isCal = "Y";
		isDel = "";
		System.out.println("---------huifu--------------");
	}
	 /**
     * 流程创建保存
     */
	public String createProcInstance(){
    	try {
    		// 保存子表信息
    		
    		//原投资总额
    		procRegiCapitalChange.setInvestTotalOri(procRegiCapitalChange.getCompany().getInvestTotal());
    		//原投资总额币种
    		procRegiCapitalChange.setInvestCurrencyOri(procRegiCapitalChange.getCompany().getInvestCurrency());
    		//投注差可用
    		procRegiCapitalChange.setIsInvestRegRemaAvaiOri(procRegiCapitalChange.getCompany().getIsInvestRegRemaAvai());
    		//投注差可用钱
    		procRegiCapitalChange.setInvestRegRemaFundsOri(procRegiCapitalChange.getCompany().getInvestRegRemaFunds());
    		//投注差可用币种
    		procRegiCapitalChange.setInvestRegRemaFundsCuOri(procRegiCapitalChange.getCompany().getInvestRegRemaFundsCu());
    		//
    		String procInstId = regicapitalChargeService.createProcInstance(procRegiCapitalChange);
    		System.out.println("listlist-----------------------"+prccShareHolderList.size());
    		
    		for(int i=0; i<prccShareHolderList.size(); i++){
    			ProcRegiCapitalChangeShareholder prccss = prccShareHolderList.get(i);
//    			prccss.setRegiCapitalChangeId(procRegiCapitalChange.getId());
    			entityService.create(prccss);
    		}
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
	/**
     * 查询流程实例
     */
    public void findProcInstance(){
    	//对象
    	this.procRegiCapitalChange = this.regicapitalChargeService.findProcInstanceByProcInstId(procInstId);
		//查询流程详细
    	searchProcessDetail();
    	//查询流程附件
    	displayDetailAttach(procRegiCapitalChange.getProcInstId());
    	//子表明晰
    	this.prccShareHolderList = this.regicapitalChargeService.findProcInstanceListByProcInstId(this.procRegiCapitalChange.getId());
    	List<ProcRegiCapitalChangeShareholder> tempList = new ArrayList<ProcRegiCapitalChangeShareholder>();
    	for(int i=0; i<prccShareHolderList.size(); i++){
    		ProcRegiCapitalChangeShareholder tempPrccs = prccShareHolderList.get(i);
    		String sOName = tempPrccs.getShareholderNameOriginal();
    		String sName = tempPrccs.getShareholderName();
    		String defunctInd = tempPrccs.getDefunctInd();
    		if("Y".equals(defunctInd)){
    			tempPrccs.setStatus("删除");
    		}else{
    			if("".equals(sOName)||null == sOName){
        			tempPrccs.setStatus("新增");
        		}else if((!"".equals(sOName)
        				&&null != sOName)
        				&&(!"".equals(sName)
        				&&null != sName)){
        			tempPrccs.setStatus("更新");
        		}	
    		}
    	}
    }
    /**
     * 查询流程详细步骤
     */
    private void searchProcessDetail(){
    	detailVos = this.regicapitalChargeService.getProcessDetail(procInstId);
    }
    //审核
    public String doApprove(String flag){
        try {
        	this.procInstId=this.procRegiCapitalChange.getProcInstId();
            String msgStr="";
            Boolean flagBool=false;
            if (flag.equals("Y")) {
            	flagBool=true;
            } 
            MessageUtils.addSuccessMessage("doneMsg", stepName+MessageUtils.getMessage("process_save_success", processUtilMapService.getTmsIdByFnId(procInstId)));
            this.regicapitalChargeService.doApprove(this.procRegiCapitalChange,flagBool,stepName);
            this.saveProcessFile(procInstId);
            //9.3处理下一个任务
            return processUtilService.getNextPage(getInputPage(), doNext, getCurrentIndex(),getCurrentTaskType());
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