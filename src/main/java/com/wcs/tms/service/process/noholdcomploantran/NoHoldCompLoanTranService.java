package com.wcs.tms.service.process.noholdcomploantran;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.DateUtil;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.common.model.P;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.mail.Mail;
import com.wcs.tms.mail.MailService;
import com.wcs.tms.mail.MailUtil;
import com.wcs.tms.mail.SendMailService;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcNoHoldCompLoanTran;
import com.wcs.tms.model.ProcNoHoldCompLoanTranDetail;
import com.wcs.tms.model.ProcTMSStatus;
import com.wcs.tms.service.process.common.FtpUploadService;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.TmsStatusService;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

import filenet.vw.api.VWException;
import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWWorkObjectNumber;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:非控股公司借款转款流程Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */

@Stateless
public class NoHoldCompLoanTranService implements Serializable{

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(NoHoldCompLoanTranService.class);
	@Inject 
	ProcessUtilMapService processUtilMapService; 
	@Inject
    MailService mailService;
    @Inject
    SendMailService sendMailService;
	@Inject
    PEManager peManager;
	@Inject
	EntityService entityService;
	@Inject 
	LoginService loginService;
	@Inject
	FtpUploadService ftpUploadService;
	@EJB
	private TmsStatusService tmsStatusService;

	
	/**
	 * 审批保存
	 * @param dailyPayLoanTran
	 * @param pass
	 * @param stepName
	 * @throws ServiceException
	 */
	public void doApprove(ProcNoHoldCompLoanTran procNoHoldCompLoanTran, boolean pass, String stepName) throws ServiceException{
		try {
			//加入流程备注抬头
			String memoTitle = "";
			if(pass){
				memoTitle = ProcessXmlUtil.findStepProperty("id", "NoHoldCompLoanTran", stepName, "passMemo");
			}else{
				memoTitle = ProcessXmlUtil.findStepProperty("id", "NoHoldCompLoanTran", stepName, "nopassMemo");
			}
			if(memoTitle!=null){
				procNoHoldCompLoanTran.setPeMemo(memoTitle + procNoHoldCompLoanTran.getPeMemo());
			}
			//先执行更新操作
			entityService.update(procNoHoldCompLoanTran);
			
			Map<String, Object> fnParamMap = new HashMap<String, Object>();
			fnParamMap.put("_Pass", pass);
			Boolean loanFlag = true;
			if("L".equals(procNoHoldCompLoanTran.getLoanIden())||"A".equals(procNoHoldCompLoanTran.getLoanIden())){
				fnParamMap.put("TMS_AppIsLoan", loanFlag);
			}else{
				loanFlag = false;
				fnParamMap.put("TMS_AppIsLoan", loanFlag);
			}
			peManager.vwDisposeTask(procNoHoldCompLoanTran.getProcInstId(), fnParamMap, procNoHoldCompLoanTran.getPeMemo());
			//是否保存确认信息
		} catch (Exception e) {
			log.error("doApprove方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}
	
	
	/**
	 * 根据流程实例ID得到申请实例
	 * @param procInstId
	 * @return
	 */
	public ProcNoHoldCompLoanTran findProcInstanceByProcInstId(String procInstId) {
		StringBuilder jpql = new StringBuilder("select nh from ProcNoHoldCompLoanTran nh join fetch nh.company where nh.defunctInd = 'N'");
		jpql.append(" and nh.procInstId='"+procInstId+"'");
		return entityService.findUnique(jpql.toString());
	}

	
	/**
     * 流程创建保存
     * @return
     */
	public String createProcInstance(ProcNoHoldCompLoanTran procNoHoldCompLoanTran) throws ServiceException{
		String procInstId ="";
		Company company = entityService.find(Company.class, procNoHoldCompLoanTran.getCompany().getId());
		procNoHoldCompLoanTran.setCompany(company);
    	try {
    		//PE流程创建
    		//流程实例ID，并保存
    		procInstId = this.vwApplication(procNoHoldCompLoanTran);
    		procNoHoldCompLoanTran.setProcInstId(procInstId);
    		procNoHoldCompLoanTran.setCreatedBy(loginService.getCurrentUserName());
    		entityService.create(procNoHoldCompLoanTran);
    		//生成流程实例编号映射9.19
    		processUtilMapService.generateProcessMap(procInstId, "TMS_BPM_RA_015", procNoHoldCompLoanTran.getCompany());
		} catch (Exception e) {
		    log.error("createProcInstance方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.INSTANCE_CREATE, e);
		}
    	return procInstId;
	}
	
	/**
	 * PE流程创建
	 * @param dailyPayLoanTran
	 * @return
	 * @throws ServiceException
	 */
	public String vwApplication(ProcNoHoldCompLoanTran procNoHoldCompLoanTran) throws ServiceException{
		String workClassName = ProcessXmlUtil.getProcessAttribute("id", "NoHoldCompLoanTran", "className");
    	String workflowNumber = "";
    	if(peManager.checkWorkClassName(workClassName)){
    		try {
    			//验证流程类名
    			workflowNumber = peManager.vwCreateInstance(workClassName, "tms_subject1");
    			
    			HashMap<String, Object> step1para = new HashMap<String, Object>();
    			peManager.vwLauchStep("TMS_Requester", step1para, workflowNumber);
			} catch (Exception e) {
			    log.error("vwApplication方法 错误信息：" + e.getMessage());
				throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
			}
    	}else{
    		throw new ServiceException(ExceptionMessage.FN_NO_CLASS);
    	}
		
    	return workflowNumber;
    }

	/**
	 * 根据账户初始化公司下拉
	 * @param dailyPayLoanTran
	 * @return
	 */
	public List<SelectItem> initCompanySelect(
			ProcNoHoldCompLoanTran procNoHoldCompLoanTran) {
		List<String> sapList = loginService.findCompanySapCode(procNoHoldCompLoanTran.getCreatedBy());
		StringBuilder jpql = new StringBuilder("select c from Company c where c.defunctInd = 'N' and c.status = 'Y'");
    	List<Company> companys = new ArrayList<Company>();
		if(sapList!=null && sapList.size()!=0){
    		jpql.append(" and c.sapCode in (?1)");
    		companys = entityService.find(jpql.toString(), sapList);
    	}
    	List<SelectItem> selects = new ArrayList<SelectItem>(); 
    	for(Company c : companys){
    		SelectItem si = new SelectItem(c.getId(), c.getCompanyName());
    		selects.add(si);
    	}
    	return selects;
	}

	/**
	 * 根据流程实例的ID查询流程明细集合
	 * @param procInstId
	 * @return
	 */
	public List<ProcNoHoldCompLoanTranDetail> findDetailByProcInstId(Long procInstId) {
		StringBuilder jpql = new StringBuilder("select d from ProcNoHoldCompLoanTranDetail d join fetch d.procNoHoldCompLoanTran where d.defunctInd = 'N'");
		jpql.append(" and d.procNoHoldCompLoanTran.id="+procInstId);
		return entityService.find(jpql.toString());
	}


	/**
	 * 不同方式的支付
	 * @param procDailyPayDetail
	 * @param lastFunds
	 * @param stepName
	 */
	public void doPayment(ProcNoHoldCompLoanTranDetail procNoHoldCompLoanTranDetail, 
			Double lastFunds, String stepName, Boolean isSungard) {
		try{	
			//加入流程备注抬头
			String memoTitle = "";
			memoTitle = ProcessXmlUtil.findStepProperty("id", "NoHoldCompLoanTran", stepName, "passMemo");
			String remark = procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getPeMemo();
			if(memoTitle!=null){
				procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().setPeMemo(memoTitle + procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getPeMemo());
			}
			//先执行更新操作
			entityService.create(procNoHoldCompLoanTranDetail);
			Map<String, Object> fnParamMap = new HashMap<String, Object>();
			fnParamMap.put("TMS_Proc_Is_Terminated", false);
			fnParamMap.put("TMS_Is_Sungard", isSungard);
			Boolean enoughFlag = true;
			if(lastFunds > 0){
				enoughFlag = false;
				fnParamMap.put("TMS_Funds_Enough", enoughFlag);
			}else{
				fnParamMap.put("TMS_Funds_Enough", enoughFlag);
			}
			peManager.vwDisposeTask(procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getProcInstId(), fnParamMap, procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getPeMemo());
			//调用生成ftp文件方法 add on 2013-8-1 by yan
			String className = ProcessXmlUtil.getProcessAttribute("id", "NoHoldCompLoanTran","className");
			String bpmId = ftpUploadService.tmsFtpUploadFile(procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getProcInstId(),
					className, procNoHoldCompLoanTranDetail.getCreatedDatetime(), isSungard);
			if(isSungard) {
				ProcTMSStatus tmsStatus = new ProcTMSStatus();
				tmsStatus.setPayId(procNoHoldCompLoanTranDetail.getId());
				tmsStatus.setBpmId(bpmId);
				tmsStatus.setTmsStatus(TmsStatusService.STATUS_NOIMPORT);
				//增加TMS回传表
				tmsStatusService.saveTmsStatus(tmsStatus);
			}else {
				//根据节点名判断是否发送邮件知会
				mailProviders(procNoHoldCompLoanTranDetail, fnParamMap, stepName, remark);
				//是否保存确认信息
			}
		} catch (Exception e) {
		    log.error("doPayment方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}

	/**
	 * 停止付款
	 * @param procDailyPayDetail
	 * @param terminated
	 * @param stepName 
	 */
	public void terminatePayment(ProcNoHoldCompLoanTranDetail procNoHoldCompLoanTranDetail,
			Boolean terminated, String stepName, Boolean isSungard) {
		//加入流程备注抬头
		String memoTitle = "停止付款。";
		procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().setPeMemo(memoTitle + procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getPeMemo());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("TMS_Proc_Is_Terminated", terminated);
		map.put("TMS_Is_Sungard", isSungard);
		try {
			peManager.vwDisposeTask(procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getProcInstId(), map, procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getPeMemo());
			mailTerminated(procNoHoldCompLoanTranDetail, map, stepName);
		} catch (VWException e) {
		    log.error("terminatePayment方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}
	
	/**
	 * 向流程发起人发送付款邮件
	 * @param procDailyPayDetail
	 * @param fnParamMap
	 * @param stepName
	 * @param remark 
	 */
    private void mailProviders(ProcNoHoldCompLoanTranDetail procNoHoldCompLoanTranDetail , Map<String, Object> fnParamMap, String stepName, String remark ){
    	Double lastFunds = initLastFunds(procNoHoldCompLoanTranDetail);
    	List<Mail> mailList = new ArrayList<Mail>();
    	DecimalFormat df = new DecimalFormat("0.00");
    	StringBuilder bussMailBody = new StringBuilder("您申请的“非控股公司借款转款流程”，编号为:["+processUtilMapService.getTmsIdByFnId(procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getProcInstId())+"],本次付款金额为："+df.format(procNoHoldCompLoanTranDetail.getPayFundsTotal())+"万元，" +
				"各节点已经完成审批，并生成付款指令，资金调度同事即将处理！预计付款日期为：" + DateUtil.dateToStrShort(procNoHoldCompLoanTranDetail.getPayDatetime())
				+",付款方式为："+("O".equals(procNoHoldCompLoanTranDetail.getPayWay())?"网银付款，":"SUNGARD付款，")+""+(lastFunds == 0.0 ? "审批备注："+remark+"。此流程所有金额均已完成审批！请知悉，谢谢！":"审批备注："+remark+"。请知悉，谢谢！"));
    		StringBuilder puJpql = new StringBuilder();
            puJpql.append("select distinct pu.pernr from PU pu where pu.defunctInd='N' and pu.id=?1");
    		String pId = entityService.findUnique(puJpql.toString(), procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getCreatedBy());
    		P p = entityService.find(P.class, pId);
    			if(p != null){
    				Mail m = new Mail();
    				m.setTelno(p.getCelno());
    				m.setEmail(p.getEmail());
    				m.setSubject(mailService.generationTitle(MailUtil.MailTypeEnum.Notice, "TMS_NoHoldCompLoanTran", processUtilMapService.getTmsIdByFnId(procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getProcInstId()),
    						stepName, loginService.getCurrentUserName(), null));
    				m.setBody(mailService.generationContent(MailUtil.MailTypeEnum.Notice, "TMS_NoHoldCompLoanTran", processUtilMapService.getTmsIdByFnId(procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getProcInstId()),
    						stepName, loginService.getCurrentUserName(), null, null) + bussMailBody.toString());
    				mailList.add(m);
    			}
    		sendMailService.send(mailList);
    }
    
    /**
	 * 计算“剩余需要支付的金额”
	 * @param procNoHoldCompLoanTranDetail
	 * @return
	 */
	public Double initLastFunds(ProcNoHoldCompLoanTranDetail procNoHoldCompLoanTranDetail) {
		Double lastFunds;
        List<ProcNoHoldCompLoanTranDetail> details = new ArrayList<ProcNoHoldCompLoanTranDetail>();
        details = this.findDetailByProcInstId(procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getId());
        if (details.size() == 0) {
            lastFunds = procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getAmount();
        } else {
            Double paiedSum = 0.0;
            for (ProcNoHoldCompLoanTranDetail d : details) {
                paiedSum = paiedSum + d.getPayFundsTotal();
            }
            lastFunds = procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getAmount() - paiedSum;
        }
        return lastFunds;
    }
    
    /**
     * 向流程发起人发送停止付款邮件
     * @param procDailyPayDetail
     * @param fnParamMap
     * @param stepName
     */
    private void mailTerminated(ProcNoHoldCompLoanTranDetail procNoHoldCompLoanTranDetail , Map<String, Object> fnParamMap, String stepName) {
    	List<Mail> mailList = new ArrayList<Mail>();
		StringBuilder bussMailBody = new StringBuilder("您申请的“非控股公司借款/转款流程”，编号为【"+processUtilMapService.getTmsIdByFnId(procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getProcInstId())+"】，" +
				"集团资金计划已经停止付款，请知悉，谢谢！");
		StringBuilder puJpql = new StringBuilder();
        puJpql.append("select distinct pu.pernr from PU pu where pu.defunctInd='N' and pu.id=?1");
		String pId = entityService.findUnique(puJpql.toString(), procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getCreatedBy());
		P p = entityService.find(P.class, pId);
			if(p != null){
				Mail m = new Mail();
				m.setTelno(p.getCelno());
				m.setEmail(p.getEmail());
				m.setSubject(mailService.generationTitle(MailUtil.MailTypeEnum.Notice, "TMS_NoHoldCompLoanTran", processUtilMapService.getTmsIdByFnId(procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getProcInstId()), 
						stepName, loginService.getCurrentUserName(), null));
				m.setBody(mailService.generationContent(MailUtil.MailTypeEnum.Notice, "TMS_NoHoldCompLoanTran", processUtilMapService.getTmsIdByFnId(procNoHoldCompLoanTranDetail.getProcNoHoldCompLoanTran().getProcInstId()),
						stepName, loginService.getCurrentUserName(), null, null) + bussMailBody.toString());
				mailList.add(m);
			}
		sendMailService.send(mailList);
	}
    
	/**
	 * 查询流程详细过程
	 * @param procInstId
	 * @return
	 */
	public List<ProcessDetailVo> getProcessDetail(String procInstId) {
		List<ProcessDetailVo> detailVos = new ArrayList<ProcessDetailVo>();
		String filter = "1=1 and F_WobNum = :wobNum and (F_EventType = :eventType1 or F_EventType = :eventType2)";
		Object[] substitutionVars = { new VWWorkObjectNumber(procInstId) , ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd) , ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal)};
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
		    log.error("getProcessDetail方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return detailVos;
	}
	

}
