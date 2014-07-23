package com.wcs.tms.service.process.debtborrow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.DateUtil;
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
 * <p>Description: 外债-股东借款申请Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Stateless
public class DebtBorrowService implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@PersistenceContext(unitName = "pu")
	protected EntityManager entityManager;
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
	 * 查询所有外债合同
	 * */
	public List<DebtContract> getDebtContractList(Long comId) {
		StringBuilder jpql = new StringBuilder(
				"select dc from DebtContract dc join fetch dc.company join fetch dc.shareHolder where ");
		jpql.append("dc.defunctInd='N' AND dc.isExpired = '0'");
		jpql.append(" and dc.company.id=" + comId);
		return entityService.find(jpql.toString());
	}
    
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
    		//add by liushengbin 2014-07-08 公司表增加 ‘投注差是否可用’字段
    		//如果字段值为0不可用，公司投注差 = ‘指定的投注差’字段值
    		//如果字段值为1可用，公司投注差 = 公司投资总额 - 公司注册资本
    		Double investBalance = 0d;
    		if("0".equals(company.getIsInvestRegRemaAvai())){
    			investBalance = company.getInvestRegRemaFunds()==null ?0d:company.getInvestRegRemaFunds();
    			procDebtBorrow.setInvestBalance(investBalance);
        		procDebtBorrow.setInvestBalanceCu(company.getInvestRegRemaFundsCu());
    		}else{    		
	    		if(company.getInvestTotal()!=null && company.getInvestTotal()-fondsInPlaceSum > 0d){
	    			investBalance = company.getInvestTotal() - fondsSum;
	    			procDebtBorrow.setInvestBalance(investBalance);
	        		procDebtBorrow.setInvestBalanceCu(company.getInvestCurrency());
	    		}
    		}
    		
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
    		procDebtBorrow.setThisShBorrowSe("H");
    		
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
			//add by liushengbin date:2014-07-15 增加流程走到财务总监节点后，自动更新外债主数据
			if("财务总监审批".equals(stepName) && pass){
				saveDebtContract(procDebtBorrow);
			}
			
			
			peManager.vwDisposeTask(procDebtBorrow.getProcInstId(), pass, procDebtBorrow.getPeMemo());
			
		} catch (Exception e) {
			log.error("doApprove方法 审批保存 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}
	
	//保存外债合同主数据
	private void saveDebtContract(ProcDebtBorrow procDebtBorrow ){
		DebtContract debtContract = new DebtContract();
		debtContract.setCompany(procDebtBorrow.getCompany());
		debtContract.setProcDebtBorrow(procDebtBorrow);
		debtContract.setIsConfirmed("0");
		debtContract.setDebtContractFunds(procDebtBorrow.getThisShBorrow());
		debtContract.setDebtContractFundsCu(procDebtBorrow.getThisShBorrowCu());
		debtContract.setContractRate(Double.valueOf(procDebtBorrow.getThisShBorrowRa()));
		debtContract.setContractStartDate(procDebtBorrow.getThisShBorrowLis());
		debtContract.setContractEndDate(procDebtBorrow.getThisShBorrowLie());
		
		
		debtContract.setApprovalFunds(procDebtBorrow.getCorpAudit());
		debtContract.setApprovalFundsCu(procDebtBorrow.getCorpAuditCu());
		debtContract.setApprovalStartDate(procDebtBorrow.getCorpAuditLis());
		debtContract.setApprovalEndDate(procDebtBorrow.getCorpAuditLie());
		debtContract.setApprovalRate(Double.valueOf(procDebtBorrow.getCorpAuditRa()));
		//如果申请类型（1、首次申请，默认值2、展期申请）是展期，
		//外债期限类型(1、短期 （借款期限一年以下）2、中长期（超过一年的）)是中长期；
		//非展期，集团开始结束日期少于一年，就是短期，大于一年为中长期
		String debtDeadlineType = "1";
		if("2".equals(procDebtBorrow.getApplyType())){
			debtDeadlineType = "2";
			//为展期申请，则更新原有的外债合同主数据，已被展期的标识的字段
			String updateFlagSql = "update DEBT_CONTRACT set IS_BY_EXTEND = '1' where id="+procDebtBorrow.getDebtContractId();
			entityManager.createNativeQuery(updateFlagSql).executeUpdate();
			
		}else{
			Date startDate = procDebtBorrow.getCorpAuditLis();
			Date endDate = procDebtBorrow.getCorpAuditLie();
			long between = endDate.getTime() - startDate.getTime();
			long day = between / (24 * 60 * 60 * 1000);
			debtDeadlineType = day < 365 ?"1":"2";
		}
		debtContract.setDebtDeadlineType(debtDeadlineType);
		debtContract.setShareHolder(procDebtBorrow.getShareHolder());
		debtContract.setAppliedFunds(0d);
		debtContract.setReceivedFunds(0d);
		debtContract.setIsByExtend("2".equals(procDebtBorrow.getApplyType())?"1":"0");
		
		entityService.create(debtContract);
		
		
		
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
