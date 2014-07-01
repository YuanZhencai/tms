package com.wcs.tms.service.process.debtborrow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.MathUtil;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.mail.Mail;
import com.wcs.tms.mail.MailService;
import com.wcs.tms.mail.MailUtil;
import com.wcs.tms.mail.SendMailService;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcDebtBorrow;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWWorkObjectNumber;

/**
 * <p>Project: tms</p>
 * <p>Description: 外债-股东借款申请Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Stateless
public class DebtBorrowService implements Serializable{
	
	private static final long serialVersionUID = 1L;

    @Inject
    EntityService entityService;
    @Inject
    PEManager peManager;
    @Inject
	LoginService loginService;
    @Inject
    MailService mailService;
    @Inject
    SendMailService sendMailService;
    @Inject
    ProcessUtilMapService processUtilMapService;//9.10
    
    private static final Log log = LogFactory.getLog(DebtBorrowService.class);
    
    /**
     * 查询公司以及相关股东信息,并计算外债申请相关数据
     * @param compId
     * @return
     */
    public void procDebtBorrowFilter(ProcDebtBorrow procDebtBorrow){
    	Company company = procDebtBorrow.getCompany();
    	if(company==null || company.getId()==null){
    		procDebtBorrow.setCompany(new Company());
    		MessageUtils.addErrorMessage("doneMsg", "请先确定申请公司！");
    		return;
    	}
    	Long compId = company.getId();
    	
    	StringBuilder jpql = new StringBuilder("select c from Company c left outer join fetch c.shareHolders where c.defunctInd = 'N' and c.status = 'Y'");
    	jpql.append(" and c.id="+compId);
    	company = entityService.findUnique(jpql.toString());
    	if(company==null || null==company.getId()){
    		procDebtBorrow.setCompany(new Company());
    	}else{
    		//得到股东信息
    		List<ShareHolder> shList = company.getShareHolders();
    		List<ShareHolder> newShList = new ArrayList<ShareHolder>();
    		for(ShareHolder sh : shList){
    			if("N".equals(sh.getDefunctInd())){
    				newShList.add(sh);
    			}
    		}
    		company.setShareHolders(newShList);
    		
    		//注册资本
    		Double fondsSum = 0d;
    		//已到位注册资本
    		Double fondsInPlaceSum = 0d;
    		for(ShareHolder sh : shList){
    			if("Y".equals(sh.getDefunctInd())){
    				continue;
    			}
    			fondsSum = fondsSum + (sh.getFondsTotal()==null ? 0d : sh.getFondsTotal());
    			fondsInPlaceSum = fondsInPlaceSum + (sh.getFondsInPlace()==null ? 0d : sh.getFondsInPlace());
    		}
    		company.setFondsSum(fondsSum);
    		company.setFondsInPlaceSum(fondsInPlaceSum);
    		procDebtBorrow.setCompany(company);
    		
    		//公司投注差 = 公司投资总额 - 公司注册资本
    		Double investBalance = 0d;
    		if(company.getInvestTotal()!=null && company.getInvestTotal()-fondsInPlaceSum > 0d){
    			investBalance = company.getInvestTotal() - fondsSum;
    		}
    		procDebtBorrow.setInvestBalance(investBalance);
    		procDebtBorrow.setInvestBalanceCu(company.getInvestCurrency());
    		procDebtBorrow.setAvailbBebtCu(company.getInvestCurrency());
    		//已到位股东借款 币种连带 已使用投注差 和 已到位海外外债
    		procDebtBorrow.setFornBebtCu(procDebtBorrow.getShBorrowCu());
    		procDebtBorrow.setBebtInPlaceCu(procDebtBorrow.getShBorrowCu());
    		
    		//股东借款
    		Double shBorrow = procDebtBorrow.getShBorrow()==null ? 0d : procDebtBorrow.getShBorrow();
    		//海外外债
    		Double fornBebt = procDebtBorrow.getFornBebt()==null ? 0d : procDebtBorrow.getFornBebt();
    		//已使用投注差 = 股东借款 + 海外外债
    		Double bebtInPlace = shBorrow + fornBebt;
    		procDebtBorrow.setBebtInPlace(bebtInPlace);
    		
    		Double exchangeRate = procDebtBorrow.getExchangeRate()==null ? 1d : procDebtBorrow.getExchangeRate();
    		//可用投注差 = 公司投注差 - 已使用投注差 * 折算汇率
    		Double availbBebt = investBalance - (bebtInPlace * exchangeRate);
    		//四舍五入到万
    		availbBebt = MathUtil.roundHalfUp(availbBebt, 0);
    		procDebtBorrow.setAvailbBebt(availbBebt);
    		
    		//afce折算
    		if("Y".equals(procDebtBorrow.getAfceFlag())){
    			Double afceExcRate = procDebtBorrow.getAfceExcRate()==null ? 1d : procDebtBorrow.getAfceExcRate();
    			Double afceSignExc = procDebtBorrow.getAfceSign() * afceExcRate;
    			procDebtBorrow.setAfceSignExc(afceSignExc);
    		}
    		
    		if(availbBebt < 0d ){
    			MessageUtils.addErrorMessage("doneMsg", "可用投注差不可小于0!");
    		}
    		
    	}
    }
    
    
    /**
     * 流程创建保存
     * @return
     */
    public String createProcInstance(ProcDebtBorrow procDebtBorrow) throws ServiceException{
    	//设置公司名字
    	procDebtBorrow.setCompanyName(procDebtBorrow.getCompany().getCompanyName());
    	procDebtBorrow.setCompanyEn(procDebtBorrow.getCompany().getCompanyEn());
    	
    	//设置资金提供信息
    	this.setProviderInfo(procDebtBorrow);
    	
    	String procInstId ="";
    	try {
    		//PE流程创建
    		//流程实例ID，并保存
    		procInstId = this.vwApplication(procDebtBorrow);
    		procDebtBorrow.setProcInstId(procInstId);
    		entityService.create(procDebtBorrow);
    		//生成流程实例编号映射9.6
    		processUtilMapService.generateProcessMap(procInstId, "BPM_RA_007", procDebtBorrow.getCompany());
		} catch (Exception e) {
			log.error("createProcInstance方法 流程创建保存 出现异常：",e);
			throw new ServiceException(ExceptionMessage.INSTANCE_CREATE, e);
		}
    	return procInstId;
    }
    
    /**
     * PE流程创建
     * @param procDebtBorrow
     * @return
     * @throws Exception
     */
	public String vwApplication(ProcDebtBorrow procDebtBorrow) throws ServiceException{
		String workClassName = ProcessXmlUtil.getProcessAttribute("id", "DebtBorrow", "className");
    	String workflowNumber = "";
    	if(peManager.checkWorkClassName(workClassName)){
    		try {
    			//验证流程类名
    			workflowNumber = peManager.vwCreateInstance(workClassName, "tms_subject1");
    			
    			HashMap<String, Object> step1para = new HashMap<String, Object>();
    			peManager.vwLauchStep("TMS_Requester", step1para, workflowNumber);
			} catch (Exception e) {
				log.error("vwApplication方法 PE流程创建 出现异常：",e);
				throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
			}
    	}else{
    		throw new ServiceException(ExceptionMessage.FN_NO_CLASS);
    	}
		
    	return workflowNumber;
    	
    }
	

	/**
	 * 流程实例Id得到外债申请实例
	 * @param procInstId
	 * @return
	 */
	public ProcDebtBorrow findProcInstanceByProcInstId(String procInstId){
		StringBuilder jpql = new StringBuilder("select db from ProcDebtBorrow db join fetch db.company left outer join fetch db.shareHolder where db.defunctInd = 'N'");
		jpql.append(" and db.procInstId='"+procInstId+"'");
		return entityService.findUnique(jpql.toString());
	}
	
	
	/**
	 * 查询流程详细
	 * @param procInstId
	 * @return
	 */
	public List<ProcessDetailVo> getProcessDetail(String procInstId) throws ServiceException{
		List<ProcessDetailVo> detailVos = new ArrayList<ProcessDetailVo>();
		String filter = "1=1 and F_WobNum = :wobNum and (F_EventType = :eventType1 or F_EventType = :eventType2)";
		Object[] substitutionVars = { new VWWorkObjectNumber(procInstId) ,ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd) ,ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal)};
		try {
			List<VWLogElement> les = new ArrayList<VWLogElement>();
			les = peManager.vwEventLogWob(filter, substitutionVars);
			for(VWLogElement le : les){
				ProcessDetailVo detailVo=new ProcessDetailVo();
				if(ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal).equals(le.getEventType())){
					detailVo.setProssNodeName("流程终止");
				}else{
					detailVo.setProssNodeName(le.getStepName());
				}
				detailVo.setOperatorsName(le.getUserName());
				detailVo.setOperatorTime(le.getTimeStamp());
				detailVo.setNodeMemo((String)le.getFieldValue("F_Comment"));
				detailVo.setId(new Long(le.getSequenceNumber()));
				detailVos.add(detailVo);
			}
		} catch (Exception e) {
			log.error("getProcessDetail方法 查询流程详细 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return detailVos;
	}
	
	/**
	 * 审批保存
	 * @param procDebtBorrow
	 * @param pass
	 */
	public void doApprove(ProcDebtBorrow procDebtBorrow , boolean pass ,String stepName) throws ServiceException{
		try {
			//设置资金提供信息
	    	this.setProviderInfo(procDebtBorrow);
			//先执行更新操作
			entityService.update(procDebtBorrow);
		} catch (Exception e) {
			log.error("doApprove方法 审批保存 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		
		
		try {
			//加入流程备注抬头
			String memoTitle = "";
			if(pass){
				memoTitle = ProcessXmlUtil.findStepProperty("id", "DebtBorrow", stepName, "passMemo");

			}else{
				memoTitle = ProcessXmlUtil.findStepProperty("id", "DebtBorrow", stepName, "nopassMemo");
			}
			if(memoTitle!=null){
				procDebtBorrow.setPeMemo(memoTitle + (procDebtBorrow.getPeMemo()==null ? "" : procDebtBorrow.getPeMemo()));
			}
			
			peManager.vwDisposeTask(procDebtBorrow.getProcInstId(), pass, procDebtBorrow.getPeMemo());
			
		} catch (Exception e) {
			log.error("doApprove方法 审批保存 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}
	
	
	/**
     * 给集团项目经理发送邮件
     * @param processId
     */
    private void sendGropPm(String processId) {
    	List<Mail> mails = new ArrayList<Mail>();
        StringBuilder filter = new StringBuilder("1=1 and F_EventType = :EventType");
        Object[] substitutionVars = new Object[2];
        filter.append(" and F_WobNum = :wobNum");
        substitutionVars[0] = ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd);
        substitutionVars[1] = new VWWorkObjectNumber(processId);
        
        List<String> steps = new ArrayList<String>();
        steps.add("集团项目经理审批");
        //邮件封装
        mails = mailService.findEmailBySteps(filter.toString(), substitutionVars, steps ,MailUtil.MailTypeEnum.GroupPM);
        sendMailService.send(mails);
    }
	
	/**
	 * 设置资金提供信息
	 * @param procDebtBorrow
	 */
	private void setProviderInfo(ProcDebtBorrow procDebtBorrow){
		//设置资金提供信息
    	String thisShBorrowSe = procDebtBorrow.getThisShBorrowSe();
    	procDebtBorrow.setProviderType(thisShBorrowSe);
    	if(thisShBorrowSe!=null){
    		//股东类型
    		if("G".equals(thisShBorrowSe)){
    			Long shId = procDebtBorrow.getShareHolder().getId();
    			procDebtBorrow.setShareHolder(entityService.find(ShareHolder.class, shId));
    			procDebtBorrow.setProviderKey(null);
    		}
    		//海外外债 和 展期 类型
    		else{
    			procDebtBorrow.setShareHolder(null);
    		}
    	}
	}
}
