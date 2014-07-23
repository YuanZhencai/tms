package com.wcs.tms.service.process.debtpayment;

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
import com.wcs.tms.model.DebtContract;
import com.wcs.tms.model.ProcDebtBorrow;
import com.wcs.tms.model.ProcDebtPayment;
import com.wcs.tms.model.ProcDebtPaymentStatus;
import com.wcs.tms.model.RemittanceLineAccount;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.util.MessageUtils;
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
public class DebtPaymentService implements Serializable{
	
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
    
    private static final Log log = LogFactory.getLog(DebtPaymentService.class);
    
    
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
    public String createProcInstance(ProcDebtPayment procDebtPayment) throws ServiceException{
    	
    	//设置公司名字
    	procDebtPayment.setCompanyName(procDebtPayment.getCompany().getCompanyName());
    	procDebtPayment.setCompanyEn(procDebtPayment.getCompany().getCompanyEn());
    	
    	
    	String procInstId ="";
    	try {
    		//PE流程创建
    		//流程实例ID，并保存
    		procInstId = this.vwApplication();
    		procDebtPayment.setProcInstId(procInstId);
    		procDebtPayment.setCreatedBy(loginService.getCurrentUserName());
    		procDebtPayment.setProcessStatus("1");//初始化状态为1 申请人完成
    		procDebtPayment.setCurrentNode("工厂资金岗位人员申请");
    		entityService.create(procDebtPayment);
    		//生成流程实例编号映射9.6
    		processUtilMapService.generateProcessMap(procInstId, "TMS_BPM_RA_018", procDebtPayment.getCompany());
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
		String workClassName = ProcessXmlUtil.getProcessAttribute("id", "ProcDebtPayment", "className");
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
	public ProcDebtPayment findProcInstanceByProcInstId(String procInstId){
		StringBuilder jpql = new StringBuilder("select db from ProcDebtPayment db join fetch db.company  "
				+ "left outer join fetch db.remittanceLineAccount "
				+ "left outer join fetch db.debtContract "
				+ "left outer join fetch db.debtContract.shareHolder "
				+ "where db.defunctInd = 'N'");
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
	public void doApprove(ProcDebtPayment procDebtPayment , boolean pass ,String stepName) throws ServiceException{
		try {
			//先执行更新操作
			if(pass){
				procDebtPayment.setProcessStatus(ProcDebtPaymentStatus.conStatus(stepName));
				procDebtPayment.setCurrentNode(stepName);
			}else{
				procDebtPayment.setProcessStatus("1");//退回到申请人
				procDebtPayment.setCurrentNode("工厂资金岗位人员申请");
			}
			entityService.update(procDebtPayment);
			if(stepName.equals("集团项目经理知会新加坡")){
				ProcDebtPayment pb=findProcInstanceByProcInstId(procDebtPayment.getProcInstId());
				DebtContract db=pb.getDebtContract();
				db.setAppliedFunds(db.getAppliedFunds()+pb.getApplyFunds());//更新合同已请款金额
				this.entityService.update(db);
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
				procDebtPayment.setPeMemo(memoTitle + (procDebtPayment.getPeMemo()==null ? "" : procDebtPayment.getPeMemo()));
			}

			peManager.vwDisposeTask(procDebtPayment.getProcInstId(), pass,procDebtPayment.getPeMemo());
		} catch (Exception e) {
			log.error("doApprove方法 审批保存 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}
	
	
	public List<DebtContract> getPaymentDebtContract(Long comId){
		StringBuilder jpql = new StringBuilder("select dc from DebtContract dc left join fetch dc.shareHolder "
				+ "left join fetch dc.procDebtBorrow where ");
		jpql.append("dc.company.id="+comId);
		jpql.append(" and dc.isExpired='0'");
		jpql.append(" and dc.debtContractFunds-dc.appliedFunds>0");
		return entityService.find(jpql.toString()); 
	}
	
	/**
	 * 查询所有汇款线路
	 * */
	public List<RemittanceLineAccount> getSelAcNoList(Long comId){
		StringBuilder jpql=new  StringBuilder("select rc from RemittanceLineAccount rc join fetch rc.company where ");
		jpql.append("rc.defunctInd='N' AND ");
		jpql.append("rc.company.id="+comId);
		return entityService.find(jpql.toString()); 
	}
	
	/**
	 * 新增汇款线路
	 * */
	public void saveAcNo(RemittanceLineAccount rc){
		rc.setCreatedBy(this.loginService.getCurrentUserName());
		rc.setUpdatedBy(this.loginService.getCurrentUserName());
		this.entityService.create(rc);
	}
	
	public void disComp(ProcDebtPayment procDebtPayment){
    	Company company = procDebtPayment.getCompany();
    	if(company==null || company.getId()==null){
    		MessageUtils.addErrorMessage("doneMsg", "请先确定申请公司！");
    		return;
    	}
    	Long compId = company.getId();
    	
    	StringBuilder jpql = new StringBuilder("select c from Company c left outer join fetch c.shareHolders where c.defunctInd = 'N' and c.status = 'Y'");
    	jpql.append(" and c.id="+compId);
    	company = entityService.findUnique(jpql.toString());
    	if(company==null || null==company.getId()){
    		procDebtPayment.setCompany(new Company());
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
    		procDebtPayment.setCompany(company);
    		
    		//公司投注差 = 公司投资总额 - 公司注册资本
    		/*Double investBalance = 0d;
    		if(company.getInvestTotal()!=null && company.getInvestTotal()-fondsInPlaceSum > 0d){
    			investBalance = company.getInvestTotal() - fondsSum;
    		}*/
    		//可用投注差=投资总额 - 已用投注差 
    		company.setCanUseInvestRegRema(company.getInvestTotal()-company.getUsedInvestRegRema());
    	}
    }
}
