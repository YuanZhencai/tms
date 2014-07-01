package com.wcs.tms.service.process.regicapitalCharge;

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
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.mail.MailService;
import com.wcs.tms.mail.SendMailService;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.ProcRegiCapitalChange;
import com.wcs.tms.model.ProcRegiCapitalChangeShareholder;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWWorkObjectNumber;

/**
 * <p>Project: tms</p>
 * <p>Description: 外债请款Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 */
@Stateless
public class RegicapitalChargeService implements Serializable{
	
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
    
    private static final Log log = LogFactory.getLog(RegicapitalChargeService.class);
    
    
    public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}

	/**
     * 流程创建保存
     * @return
     */
    public String createProcInstance(ProcRegiCapitalChange procRegiCapitalChange) throws ServiceException{
    	
    	String procInstId ="";
    	try {
    		//PE流程创建
    		//流程实例ID，并保存
    		procInstId = this.vwApplication();
    		procRegiCapitalChange.setProcInstId(procInstId);
    		procRegiCapitalChange.setCreatedBy(loginService.getCurrentUserName());
    		procRegiCapitalChange.setProcessStatus("1");//初始化状态为1 申请人完成
    		procRegiCapitalChange.setCurrentNode("工厂资金岗位人员申请");
    		entityService.create(procRegiCapitalChange);
    		System.out.println("procInstId"+procInstId);
    		System.out.println(procRegiCapitalChange.getId());
    		//生成流程实例编号映射9.6
    		processUtilMapService.generateProcessMap(procInstId, "TMS_BPM_RA_019", procRegiCapitalChange.getCompany());
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
	public String vwApplication() throws ServiceException{
		String workClassName = ProcessXmlUtil.getProcessAttribute("id", "ProcRegiCapitalChange", "className");
    	String workflowNumber = "";
    	if(peManager.checkWorkClassName(workClassName)){
    		try {
    			//验证流程类名
    			workflowNumber = peManager.vwCreateInstance(workClassName, "tms_subject1");
    			
    			HashMap<String, Object> step1para = new HashMap<String, Object>();
    			peManager.vwLauchStep("TMS_Fac_Fund_Pos", step1para, workflowNumber);
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
	public ProcRegiCapitalChange findProcInstanceByProcInstId(String procInstId){
		StringBuilder jpql = new StringBuilder("select db from ProcRegiCapitalChange db  "
				+ "left outer join fetch db.company "
				+ "where db.defunctInd = 'N'");
		jpql.append(" and db.procInstId='" + procInstId + "'");
		return entityService.findUnique(jpql.toString());
	}
	
	/**
	 * 流程实例Id得到外债申请实例
	 * @param procInstId
	 * @return
	 */
	public List<ProcRegiCapitalChangeShareholder> findProcInstanceListByProcInstId(Long regiCapitalChangeId){
		StringBuilder jpql = new StringBuilder("select db from ProcRegiCapitalChangeShareholder db ");
//				+ "left outer join fetch db.remittanceLineAccount "
//				+ "left outer join fetch db.debtContract "
//				+ "left outer join fetch db.debtContract.shareHolder "
			//	+ "left outer join fetch co.fundsProvider ";
//				+ "where db.defunctInd = 'N'");
		jpql.append(" where db.regiCapitalChangeId="+regiCapitalChangeId+"");
		System.out.println(jpql);
		return entityService.find(jpql.toString());
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
	public void doApprove(ProcRegiCapitalChange procRegiCapitalChange , boolean pass ,String stepName) throws ServiceException{
		try {
			//先执行更新操作
			entityService.update(procRegiCapitalChange);
			if(stepName.equals("集团项目经理审批")){
				System.out.println("审批结束,更新公司信息--------------------------------------------------------");
			}
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
				procRegiCapitalChange.setPeMemo(memoTitle + (procRegiCapitalChange.getPeMemo()==null ? "" : procRegiCapitalChange.getPeMemo()));
			}

			peManager.vwDisposeTask(procRegiCapitalChange.getProcInstId(), pass,procRegiCapitalChange.getPeMemo());
		} catch (Exception e) {
			log.error("doApprove方法 审批保存 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}
	
//	
//	public List<DebtContract> getPaymentDebtContract(Long comId){
//		StringBuilder jpql = new StringBuilder("select dc from DebtContract dc left join fetch dc.shareHolder "
//				+ "left join fetch dc.procDebtBorrow where ");
//		jpql.append("dc.company.id="+comId);
//		jpql.append(" and dc.isExpired='N'");
//		jpql.append(" and dc.debtContractFunds-dc.appliedFunds>0");
//		return entityService.find(jpql.toString()); 
//	}
//	
//	/**
//	 * 查询所有汇款线路
//	 * */
//	public List<RemittanceLineAccount> getSelAcNoList(Long comId){
//		StringBuilder jpql=new  StringBuilder("select rc from RemittanceLineAccount rc join fetch rc.company where ");
//		jpql.append("rc.defunctInd='N' AND ");
//		jpql.append("rc.company.id="+comId);
//		return entityService.find(jpql.toString()); 
//	}
//	
//	/**
//	 * 新增汇款线路
//	 * */
//	public void saveAcNo(RemittanceLineAccount rc){
//		rc.setCreatedBy(this.loginService.getCurrentUserName());
//		rc.setUpdatedBy(this.loginService.getCurrentUserName());
//		this.entityService.create(rc);
//	}
}
