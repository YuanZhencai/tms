package com.wcs.tms.service.process.dailypayloantran;

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
import com.wcs.tms.model.InveProductDetail;
import com.wcs.tms.model.ProcDailyPayDetail;
import com.wcs.tms.model.ProcDailyPayLoanTran;
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
 * <p>Description:日常付款借款转款流程Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */

@Stateless
public class DailyPayLoanTranService implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(DailyPayLoanTranService.class);
	
	//申请金额流程走向判断值
	public static final Double JUDGMENTAMOUNT = 1000.0;
	
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
	 * 向流程发起人发送付款邮件
	 * @param procDailyPayDetail
	 * @param fnParamMap
	 * @param stepName
	 * @param remark 用户填写的备注
	 */
    private void mailProviders(ProcDailyPayDetail procDailyPayDetail , Map<String, Object> fnParamMap, String stepName, String remark ){
    	Double lastFunds = initLastFunds(procDailyPayDetail);
    	DecimalFormat df = new DecimalFormat("0.00");
    	List<Mail> mailList = new ArrayList<Mail>();
    		StringBuilder bussMailBody = new StringBuilder("您申请的“日常付款借款转款流程”，编号为:["+processUtilMapService.getTmsIdByFnId(procDailyPayDetail.getProDailyPayLoanTran().getProcInstId())+"],本次付款金额为："+ df.format(procDailyPayDetail.getPayFundsTotal()) +"万元，" +
    				"各节点已经完成审批，并生成付款指令，资金调度同事即将处理！预计付款日期为：" + DateUtil.dateToStrShort(procDailyPayDetail.getPayDatetime())
    				+",付款方式为："+("O".equals(procDailyPayDetail.getPayWay())?"网银付款，":"SUNGARD付款，")+""+(lastFunds == 0.0 ? "审批备注："+remark+"。此流程所有金额均已完成审批！请知悉，谢谢！":"审批备注："+remark+"。请知悉，谢谢！"));
    		StringBuilder puJpql = new StringBuilder();
            puJpql.append("select distinct pu.pernr from PU pu where pu.defunctInd='N' and pu.id=?1");
    		String pId = entityService.findUnique(puJpql.toString(), procDailyPayDetail.getProDailyPayLoanTran().getCreatedBy());
    		P p = entityService.find(P.class, pId);
    			if(p != null){
    				Mail m = new Mail();
    				m.setTelno(p.getCelno());
    				m.setEmail(p.getEmail());
    				m.setSubject(mailService.generationTitle(MailUtil.MailTypeEnum.Notice, "TMS_DailyPayLoanTran", processUtilMapService.getTmsIdByFnId(procDailyPayDetail.getProDailyPayLoanTran().getProcInstId()), stepName, loginService.getCurrentUserName(), null));
    				m.setBody(mailService.generationContent(MailUtil.MailTypeEnum.Notice, "TMS_DailyPayLoanTran", processUtilMapService.getTmsIdByFnId(procDailyPayDetail.getProDailyPayLoanTran().getProcInstId()), stepName, loginService.getCurrentUserName(), null, null) + bussMailBody.toString());
    				mailList.add(m);
    			}
    		sendMailService.send(mailList);
    }
    
    /**
     * 向流程发起人发送停止付款邮件
     * @param procDailyPayDetail
     * @param fnParamMap
     * @param stepName
     */
    private void mailTerminated(ProcDailyPayDetail procDailyPayDetail , Map<String, Object> fnParamMap, String stepName) {
    	List<Mail> mailList = new ArrayList<Mail>();
		StringBuilder bussMailBody = new StringBuilder("您申请的“日常付款借款/转款流程”，编号为【"+processUtilMapService.getTmsIdByFnId(procDailyPayDetail.getProDailyPayLoanTran().getProcInstId())+"】，" +
				"集团资金计划已经停止付款，请知悉，谢谢！");
		StringBuilder puJpql = new StringBuilder();
        puJpql.append("select distinct pu.pernr from PU pu where pu.defunctInd='N' and pu.id=?1");
		String pId = entityService.findUnique(puJpql.toString(), procDailyPayDetail.getProDailyPayLoanTran().getCreatedBy());
		P p = entityService.find(P.class, pId);
			if(p != null){
				Mail m = new Mail();
				m.setTelno(p.getCelno());
				m.setEmail(p.getEmail());
				m.setSubject(mailService.generationTitle(MailUtil.MailTypeEnum.Notice, "TMS_DailyPayLoanTran", processUtilMapService.getTmsIdByFnId(procDailyPayDetail.getProDailyPayLoanTran().getProcInstId()), stepName, loginService.getCurrentUserName(), null));
				m.setBody(mailService.generationContent(MailUtil.MailTypeEnum.Notice, "TMS_DailyPayLoanTran", processUtilMapService.getTmsIdByFnId(procDailyPayDetail.getProDailyPayLoanTran().getProcInstId()), stepName, loginService.getCurrentUserName(), null, null) + bussMailBody.toString());
				mailList.add(m);
			}
		sendMailService.send(mailList);
	}
	
	/**
	 * 审批保存
	 * @param dailyPayLoanTran
	 * @param pass
	 * @param stepName
	 * @throws ServiceException
	 */
	public void doApprove(ProcDailyPayLoanTran dailyPayLoanTran, boolean pass, String stepName) throws ServiceException{
		try {
			//加入流程备注抬头
			String memoTitle = "";
			if(pass){
				memoTitle = ProcessXmlUtil.findStepProperty("id", "DailyPayLoanTran", stepName, "passMemo");
			}else{
				memoTitle = ProcessXmlUtil.findStepProperty("id", "DailyPayLoanTran", stepName, "nopassMemo");
			}
			if(memoTitle!=null){
				dailyPayLoanTran.setPeMemo(memoTitle + dailyPayLoanTran.getPeMemo());
			}
			//先执行更新操作
			entityService.update(dailyPayLoanTran);
			Map<String, Object> fnParamMap = new HashMap<String, Object>();
			fnParamMap.put("_Pass", pass);
			Boolean loanFlag = true;
			if("L".equals(dailyPayLoanTran.getLoanIden())||"A".equals(dailyPayLoanTran.getLoanIden())){
				fnParamMap.put("TMS_AppIsLoan", loanFlag);
			}else{
				loanFlag = false;
				fnParamMap.put("TMS_AppIsLoan", loanFlag);
			}
			Boolean isMore = true;
			if(dailyPayLoanTran.getAmount()>JUDGMENTAMOUNT){
				fnParamMap.put("TMS_Punds_Is_More_10_Million", isMore);
			}else{
				isMore = false;
				fnParamMap.put("TMS_Punds_Is_More_10_Million", isMore);
			}
			peManager.vwDisposeTask(dailyPayLoanTran.getProcInstId(), fnParamMap, dailyPayLoanTran.getPeMemo());
			//是否保存确认信息
			if(pass){
				String confirm = ProcessXmlUtil.findStepProperty("id", "DailyPayLoanTran", stepName, "confirm");
				if(confirm!=null && "true".equals(confirm)){
					saveDailyPayDetail(dailyPayLoanTran);
				}
			}
		} catch (Exception e) {
			log.error("doApprove方法 审批保存 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}
	
	/**
	 * 日常付款明细保存
	 * @param dailyPayLoanTran
	 */
	private void saveDailyPayDetail(ProcDailyPayLoanTran dailyPayLoanTran) throws Exception{
		InveProductDetail detail = new InveProductDetail();
		Company comp = entityService.find(Company.class, dailyPayLoanTran.getCompany().getId());
		detail.setCompany(comp);
		entityService.create(detail);
	}
	
	/**
     * 流程创建保存
     * @return
     */
	public String createProcInstance(ProcDailyPayLoanTran dailyPayLoanTran) throws ServiceException{
		String procInstId ="";
		Company company = entityService.find(Company.class, dailyPayLoanTran.getCompany().getId());
		dailyPayLoanTran.setCompany(company);
    	try {
    		//PE流程创建
    		//流程实例ID，并保存
    		procInstId = this.vwApplication(dailyPayLoanTran);
    		dailyPayLoanTran.setProcInstId(procInstId);
    		dailyPayLoanTran.setCreatedBy(loginService.getCurrentUserName());
    		entityService.create(dailyPayLoanTran);
    		//生成流程实例编号映射9.19
    		processUtilMapService.generateProcessMap(procInstId, "TMS_BPM_RA_013", dailyPayLoanTran.getCompany());
		} catch (Exception e) {
			log.error("createProcInstance方法 流程创建保存 出现异常：",e);
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
	public String vwApplication(ProcDailyPayLoanTran dailyPayLoanTran) throws ServiceException{
		String workClassName = ProcessXmlUtil.getProcessAttribute("id", "DailyPayLoanTran", "className");
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
	 * 根据流程实例ID得到申请实例
	 * @param procInstId
	 * @return
	 */
	public ProcDailyPayLoanTran findProcInstanceByProcInstId(String procInstId) {
		StringBuilder jpql = new StringBuilder("select dplt from ProcDailyPayLoanTran dplt join fetch dplt.company where dplt.defunctInd = 'N'");
		jpql.append(" and dplt.procInstId='"+procInstId+"'");
		return entityService.findUnique(jpql.toString());
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
			log.error("getProcessDetail方法 查询流程详细过程 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return detailVos;
	}
	/**
	 * 不同方式的支付
	 * @param procDailyPayDetail
	 * @param lastFunds
	 * @param stepName
	 * @param isSun 
	 */
	public void doPayment(ProcDailyPayDetail procDailyPayDetail, Double lastFunds, String stepName, Boolean isSungard) {
		try{	
		    long startTime = System.currentTimeMillis();
			//加入流程备注抬头
			String memoTitle = "";
			memoTitle = ProcessXmlUtil.findStepProperty("id", "DailyPayLoanTran", stepName, "passMemo");
			// 邮件知会需要信息（add on 2013-4-10）
			String remark = procDailyPayDetail.getProDailyPayLoanTran().getPeMemo();
			if(memoTitle!=null){
				procDailyPayDetail.getProDailyPayLoanTran().setPeMemo(memoTitle + procDailyPayDetail.getProDailyPayLoanTran().getPeMemo());
			}
			//先执行更新操作
			entityService.create(procDailyPayDetail);
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
			peManager.vwDisposeTask(procDailyPayDetail.getProDailyPayLoanTran().getProcInstId(), fnParamMap, procDailyPayDetail.getProDailyPayLoanTran().getPeMemo());
			
			
			long workFlowEndTime = System.currentTimeMillis();
			log.info("日常付款流程，流程数据处理耗时："+(workFlowEndTime-startTime));
			//调用生成ftp文件方法 add on 2013-7-31 by yan
			String className = ProcessXmlUtil.getProcessAttribute("id", "DailyPayLoanTran","className");
			String bpmId = ftpUploadService.tmsFtpUploadFile(procDailyPayDetail.getProDailyPayLoanTran().getProcInstId(),
					className, procDailyPayDetail.getCreatedDatetime(), isSungard);
			if(isSungard) {
				ProcTMSStatus tmsStatus = new ProcTMSStatus();
				tmsStatus.setPayId(procDailyPayDetail.getId());
				tmsStatus.setBpmId(bpmId);
				tmsStatus.setTmsStatus(TmsStatusService.STATUS_NOIMPORT);
				//增加TMS回传表
				tmsStatusService.saveTmsStatus(tmsStatus);
			}else {
				//根据节点名判断是否发送邮件知会
				mailProviders(procDailyPayDetail, fnParamMap, stepName,remark);
			}
			long endTime = System.currentTimeMillis();
			log.info("流程处理完毕和ftp上传总耗时："+(endTime-startTime));
		} catch (Exception e) {
			log.error("doPayment方法 不同方式的支付 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}
	
	/**
	 * 根据流程实例的ID查询流程明细集合
	 * @param procInstId
	 * @return
	 */
	public List<ProcDailyPayDetail> findDetailByProcInstId(Long procInstId) {
		StringBuilder jpql = new StringBuilder("select d from ProcDailyPayDetail d join fetch d.proDailyPayLoanTran where d.defunctInd = 'N'");
		jpql.append(" and d.proDailyPayLoanTran.id="+procInstId);
		return entityService.find(jpql.toString());
	}
	/**
	 * 计算“剩余需要支付的金额”
	 * @param procDailyPayDetail
	 * @return
	 */
	public Double initLastFunds(ProcDailyPayDetail procDailyPayDetail) {
		Double lastFunds;
        List<ProcDailyPayDetail> details = new ArrayList<ProcDailyPayDetail>();
        details = this.findDetailByProcInstId(procDailyPayDetail.getProDailyPayLoanTran().getId());
        if (details.size() == 0) {
            lastFunds = procDailyPayDetail.getProDailyPayLoanTran().getAmount();
        } else {
            Double paiedSum = 0.0;
            for (ProcDailyPayDetail d : details) {
                paiedSum = paiedSum + d.getPayFundsTotal();
            }
            lastFunds = procDailyPayDetail.getProDailyPayLoanTran().getAmount() - paiedSum;
        }
        return lastFunds;
    }
	
	/**
	 * 停止付款
	 * @param procDailyPayDetail
	 * @param terminated
	 * @param stepName 
	 * @param isSungard 
	 */
	public void terminatePayment(ProcDailyPayDetail procDailyPayDetail,
			Boolean terminated, String stepName, Boolean isSungard) {
		//加入流程备注抬头
		String memoTitle = "停止付款。";
		procDailyPayDetail.getProDailyPayLoanTran().setPeMemo(memoTitle + procDailyPayDetail.getProDailyPayLoanTran().getPeMemo());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("TMS_Proc_Is_Terminated", terminated);
		map.put("TMS_Is_Sungard", isSungard);
		try {
			peManager.vwDisposeTask(procDailyPayDetail.getProDailyPayLoanTran().getProcInstId(), map, procDailyPayDetail.getProDailyPayLoanTran().getPeMemo());
			mailTerminated(procDailyPayDetail, map, stepName);
		} catch (VWException e) {
			log.error("terminatePayment方法 停止付款 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}
	
	/**
	 * 根据账户初始化公司下拉
	 * @param dailyPayLoanTran
	 * @return
	 */
	public List<SelectItem> initCompanySelect(
			ProcDailyPayLoanTran dailyPayLoanTran) {
		List<String> sapList = loginService.findCompanySapCode(dailyPayLoanTran.getCreatedBy());
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




	
	
	
	
}
