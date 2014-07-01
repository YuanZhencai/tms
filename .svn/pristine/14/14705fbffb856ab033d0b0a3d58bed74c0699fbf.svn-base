package com.wcs.tms.service.process.prodortradecash;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.StringUtils;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.controller.vo.DictVO;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.common.model.P;
import com.wcs.common.service.CommonService;
import com.wcs.common.service.DictService;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.mail.Mail;
import com.wcs.tms.mail.MailService;
import com.wcs.tms.mail.MailUtil;
import com.wcs.tms.mail.SendMailService;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcProdOrTradeCash;
import com.wcs.tms.model.ProdOrTradeCashMain;
import com.wcs.tms.model.PurchaseFundDetail;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWWorkObjectNumber;
/**
 * 
 * <p>Project: tms</p>
 * <p>Description:生产或贸易总头寸申请流程Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */

@Stateless
public class ProdOrTradeCashService implements Serializable{

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(ProdOrTradeCashService.class);
	@Inject 
	SendMailService sendMailService;
	
	@Inject 
	ProcessUtilMapService processUtilMapService;
	
	@Inject
	MailService mailService;
	
	@Inject 
	EntityService entityService;
	
	@Inject 
	LoginService loginService;
	
	@Inject 
	PEManager peManager;
	
	@Inject
	CommonBean dictBean;
	
	@Inject
	DictService dictService;
	
	@Inject 
	CommonService commonService;
	
	/**
     * 流程创建保存
     * @return
     */
	public String createProcInstance(ProcProdOrTradeCash procProdOrTradeCash) throws ServiceException{
		
			String procInstId ="";
			Company company = entityService.find(Company.class, procProdOrTradeCash.getCompany().getId());
			procProdOrTradeCash.setCompany(company);
	    	try {
	    		//PE流程创建
	    		//流程实例ID，并保存
	    		procInstId = this.vwApplication(procProdOrTradeCash);
	            procProdOrTradeCash.setProcInstId(procInstId);
	    		procProdOrTradeCash.setCreatedBy(loginService.getCurrentUserName());
	    		entityService.create(procProdOrTradeCash);
	    		//生成流程实例编号映射9.19
	    		processUtilMapService.generateProcessMap(procInstId, "TMS_BPM_RA_010", procProdOrTradeCash.getCompany());
			} catch (Exception e) {
				log.error("createProcInstance方法 错误信息：" + e.getMessage());
				throw new ServiceException(ExceptionMessage.INSTANCE_CREATE, e);
			}
	    	return procInstId;
	}
	
	/**
	 * PE流程创建
	 * @param procProdOrTradeCash
	 * @return
	 * @throws ServiceException
	 */
	public String vwApplication(ProcProdOrTradeCash procProdOrTradeCash) throws ServiceException{
		String workClassName = ProcessXmlUtil.getProcessAttribute("id", "ProdOrTradeCash", "className");
    	String workflowNumber = "";
    	if(peManager.checkWorkClassName(workClassName)){
    		try {
    			//验证流程类名
    			workflowNumber = peManager.vwCreateInstance(workClassName, "tms_subject1");
    			
    			HashMap<String, Object> step1para = new HashMap<String, Object>();
    			step1para.put("TMS_PROD_VARITY", procProdOrTradeCash.getVariety());
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
	 * 根据流程实例ID得到申请实例
	 * @param procInstId
	 * @return
	 */
	public ProcProdOrTradeCash findProcInstanceByProcInstId(String procInstId) {
		StringBuilder jpql = new StringBuilder("select p from ProcProdOrTradeCash p join fetch p.company where p.defunctInd = 'N'");
		jpql.append(" and p.procInstId='"+procInstId+"'");
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
		    log.error("getProcessDetail方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return detailVos;
	}

	/**
	 * 审批保存
	 * @param procProdOrTradeCash
	 * @param pass
	 * @param stepName
	 * @param addCash 
	 * @throws ServiceException
	 */
	public void doApprove(ProcProdOrTradeCash procProdOrTradeCash, boolean pass, String stepName, Boolean addCash) throws ServiceException{
		try {
			//加入流程备注抬头
			String memoTitle = "";
			if(pass){
				memoTitle = ProcessXmlUtil.findStepProperty("id", "ProdOrTradeCash", stepName, "passMemo");
			}else{
				memoTitle = ProcessXmlUtil.findStepProperty("id", "ProdOrTradeCash", stepName, "nopassMemo");
			}
			if(memoTitle!=null){
				procProdOrTradeCash.setPeMemo(memoTitle + procProdOrTradeCash.getPeMemo());
			}
			//更新流程数据表
			procProdOrTradeCash.setUpdatedBy(loginService.getCurrentUserName());
			entityService.update(procProdOrTradeCash);
			//操作主数据表
			if( "集团专项总监审批".equals(stepName) && true == pass){
				List<ProdOrTradeCashMain> mains = findMainByTimeLimit(procProdOrTradeCash);
				//录入主数据表
				ProdOrTradeCashMain main = new ProdOrTradeCashMain();
				main.setCompany(procProdOrTradeCash.getCompany());
				main.setAppType(procProdOrTradeCash.getAppType());
				main.setVariety(procProdOrTradeCash.getVariety());
				if(addCash){
					List<ProdOrTradeCashMain> prodOrTradeCashMains = this.prodOrTradeCashExists(procProdOrTradeCash);
					main.setTotalCash(procProdOrTradeCash.getThisCash()+prodOrTradeCashMains.get(0).getTotalCash());
				}else{
					main.setTotalCash(procProdOrTradeCash.getThisCash());
				}
				if(addCash){
					List<ProdOrTradeCashMain> prodOrTradeCashMains = this.prodOrTradeCashExists(procProdOrTradeCash);
					main.setTotalCashAmount(procProdOrTradeCash.getThisCashAmount()+prodOrTradeCashMains.get(0).getTotalCashAmount());
				}else{
					main.setTotalCashAmount(procProdOrTradeCash.getThisCashAmount());
				}
				main.setStartDate(procProdOrTradeCash.getStartDate());
				main.setEndDate(procProdOrTradeCash.getEndDate());
				main.setSmallVariety(procProdOrTradeCash.getSmallVariety());
				main.setCreatedBy(procProdOrTradeCash.getCreatedBy());
				main.setUpdatedBy(procProdOrTradeCash.getUpdatedBy());
				//判断数据库操作
				if(mains.size() != 0){
					main.setId(mains.get(0).getId());
					entityService.update(main);
				}else{
					entityService.create(main);
				}
			}
			//判断发送邮件
			mailRelated(procProdOrTradeCash, pass, stepName);
			peManager.vwDisposeTask(procProdOrTradeCash.getProcInstId(), pass, procProdOrTradeCash.getPeMemo());
		} catch (Exception e) {
		    log.error("doApprove方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}
	
	/**
	 * 给相关人员发送邮件
	 * @param procProdOrTradeCash
	 * @param pass
	 * @param stepName
	 */
	private void mailRelated(ProcProdOrTradeCash procProdOrTradeCash , boolean pass, String stepName ){
    	List<Mail> mailList = new ArrayList<Mail>();
    	if(pass && ("集团专项总监审批".equals(stepName))){
    		//邮件业务内容
    		StringBuilder bussMailBody = new StringBuilder("以下为此次申请相关信息，申请方："+procProdOrTradeCash.getCompany().getCompanyName()+"；申请类型："+
    		("T".equals(procProdOrTradeCash.getAppType()) ? "贸易":"生产") + "；申请日期："+ DateUtil.dateToStrShort(procProdOrTradeCash.getCreatedDatetime())
    		+ "；申请品种："+this.getVarietyStr(procProdOrTradeCash.getVariety())
    		+ "；上次申请剩余头寸:" +procProdOrTradeCash.getLastCash()+"吨；本次申请头寸:"+procProdOrTradeCash.getThisCash() 
    		+"吨；上次申请剩余头寸金额:" +procProdOrTradeCash.getLastCashAmount()+"万；本次申请头寸金额:" +procProdOrTradeCash.getThisCashAmount()
    		+"万；申请期限:" +DateUtil.dateToStrShort(procProdOrTradeCash.getStartDate())+" 到 "+DateUtil.dateToStrShort(procProdOrTradeCash.getEndDate())+
    		"；小品种："+procProdOrTradeCash.getSmallVariety());
    		
    		bussMailBody.append("    特此告知，谢谢！");
    		//得到集团公司sap代码
    		List<Company> companies = findBlocSapCode();
    		List<String> blocSapCodes = new ArrayList<String>();
    		for(Company c : companies){
    			blocSapCodes.add(c.getSapCode());
    		}
    		//得到当前流程所属公司SAPCode
    		String comSapCode = procProdOrTradeCash.getCompany().getSapCode();
    		
    		List<P> ps = new ArrayList<P>();
    		//集团资金经理
    		ps.addAll(mailService.findUserByQueue("TMS_Cop_Fund_Dep_M"));
    		//集团资金计划员
    		ps.addAll(mailService.findUserByQueue("TMS_Cop_Planner"));
    		//工厂财务经理
    		ps.addAll(mailService.findUserByQueue("TMS_Fac_Finance"));
    		//工厂资金岗位人员
    		ps.addAll(mailService.findUserByQueue("TMS_Fac_Fund_Pos"));
    		for(P p :ps){
	    			String pid = p.getId();
	    			if(pid!=null){
	    				List<String> saps = loginService.finSapByPid(pid);
	    				boolean isProvider = false;
	    				//比对人的公司的sap是否包含流程所属公司sap,集团的人除外
	    				if(blocSapCodes.size() != 0){
	    					for(String blocSapCode : blocSapCodes){
	    						if(saps.contains(blocSapCode) || saps.contains(comSapCode)){
	    							isProvider = true;
	    							break;
	    						}
	    					}
	    				}else{
	    					if(saps.contains(comSapCode)){
    							isProvider = true;
    						}
	    				}
	    				//是工厂提供方财务经理,发送邮件
	    				if(isProvider){
	    					Mail m = new Mail();
	    					m.setTelno(p.getCelno());
	    					m.setEmail(p.getEmail());
	    					m.setSubject(mailService.generationTitle(MailUtil.MailTypeEnum.Notice, "TMS_ProdOrTradeCash", processUtilMapService.getTmsIdByFnId(procProdOrTradeCash.getProcInstId()), stepName, loginService.getCurrentUserName(), null));
	    					m.setBody(mailService.generationContent(MailUtil.MailTypeEnum.Notice, "TMS_ProdOrTradeCash", processUtilMapService.getTmsIdByFnId(procProdOrTradeCash.getProcInstId()), stepName, loginService.getCurrentUserName(), null, null) + bussMailBody.toString());
	    					mailList.add(m);
	    				}
	    			}
    			}
    		}
    		sendMailService.send(mailList);
    	}
	
	/**
	 * 列表显示品种处理
	 * @param prodOrTradeCashMain
	 * @return
	 */
	public String getVarietyStr(String variety){
	    /**
	     * sonar测试 variety!="" && variety!= null 改为 variety!= null && !"".equals(variety)
	     */
		if(variety!= null && !"".equals(variety)){
    		//根据cat和key模糊查询出DictVO数据
    		List<DictVO> dictVOs = dictService.searchData(DictConsts.TMS_FARM_PRODUCE_VARIETY_TYPE, variety, "", "", "");
    		String cat = dictVOs.get(0).getCodeCat();
    		//根据cat和key得到界面显示value
    		return getDictValueByKey(cat, variety);
    	}else{
    		return "";
    	}
	}
	
	/**
     * <p>Description:根据code_cat,code_key获取code_value </p>
     * @param code_cat
     * @param code_key
     * @return
     */
    private String getDictValueByKey(String code_cat, String code_key) {
        String retVal = code_key;
        if (!StringUtils.isBlankOrNull(code_key)) {
            String dict_key = code_cat + "." + code_key + ".zh_CN";
            retVal = StringUtils.safeString(commonService.getValueByDictCatKey(dict_key.replace(".", "_")));
        }
        return retVal;
    }
	
	/**
	 * 得到集团公司sap代码
	 */
	private List<Company> findBlocSapCode() {
		StringBuilder jpql = new StringBuilder("select c from Company c where c.defunctInd = 'N' and c.corporationFlag='Y'");
		return entityService.find(jpql.toString());
	}

	/**
	 * 查看主数据表该预算是否存在历史申请
	 * @param procProdOrTradeCash
	 * @return 
	 */
	private List<ProdOrTradeCashMain> findMainByTimeLimit(ProcProdOrTradeCash procProdOrTradeCash) {
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		String start = sdf.format(procProdOrTradeCash.getStartDate());
		String end = sdf.format(procProdOrTradeCash.getEndDate());
		StringBuilder jpql = new StringBuilder("select p from ProdOrTradeCashMain p join fetch p.company where p.defunctInd = 'N'");
		jpql.append(" and p.company.id="+procProdOrTradeCash.getCompany().getId());
		jpql.append(" and p.appType='"+procProdOrTradeCash.getAppType()+"'");
		jpql.append(" and p.variety='"+procProdOrTradeCash.getVariety()+"'");
		jpql.append(" and p.startDate ='"+start+"'");
		jpql.append(" and p.endDate ='"+end+"'");
		return entityService.find(jpql.toString());
		
	}

	/**
	 * 增加头寸数量时判断主数据表是否存在当前时间记录
	 * @param procProdOrTradeCash
	 * @return
	 */
	public List<ProdOrTradeCashMain > findProdOrTradeCashByNow(
			ProcProdOrTradeCash procProdOrTradeCash) {
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		String now = sdf.format(procProdOrTradeCash.getUpdatedDatetime());
		StringBuilder jpql = new StringBuilder("select p from ProdOrTradeCashMain p join fetch p.company where p.defunctInd = 'N'");
		jpql.append(" and p.company.id="+procProdOrTradeCash.getCompany().getId());
		jpql.append(" and p.appType='"+procProdOrTradeCash.getAppType()+"'");
		jpql.append(" and p.variety='"+procProdOrTradeCash.getVariety()+"'");
		jpql.append(" and p.startDate <='"+now+"'");
		jpql.append(" and p.endDate >='"+now+"'");
		return entityService.find(jpql.toString());
	}
	
	/**
	 * 查看主数据表中是否存在期限冲突数据
	 * @param procProdOrTradeCash
	 * @return
	 */
	public List<ProdOrTradeCashMain> findMainBy(
			ProcProdOrTradeCash procProdOrTradeCash) {
		
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		String start = sdf.format(procProdOrTradeCash.getStartDate());
		String end = sdf.format(procProdOrTradeCash.getEndDate());
		StringBuilder jpql = new StringBuilder("select p from ProdOrTradeCashMain p join fetch p.company where p.defunctInd = 'N'");
		jpql.append(" and p.company.id="+procProdOrTradeCash.getCompany().getId());
		jpql.append(" and p.appType='"+procProdOrTradeCash.getAppType()+"'");
		jpql.append(" and p.variety='"+procProdOrTradeCash.getVariety()+"'");
		jpql.append(" and p.startDate <='"+start+"'");
		jpql.append(" and p.endDate >='"+start+"'");
		List<ProdOrTradeCashMain> sMains = entityService.find(jpql.toString());
		jpql = new StringBuilder(" select p from ProdOrTradeCashMain p join fetch p.company where p.defunctInd = 'N'");
		jpql.append(" and p.company.id="+procProdOrTradeCash.getCompany().getId());
		jpql.append(" and p.appType='"+procProdOrTradeCash.getAppType()+"'");
		jpql.append(" and p.variety='"+procProdOrTradeCash.getVariety()+"'");
		jpql.append(" and p.startDate <='"+end+"'");
		jpql.append(" and p.endDate >='"+end+"'");
		List<ProdOrTradeCashMain> eMains = entityService.find(jpql.toString());
		List<ProdOrTradeCashMain> mains = new ArrayList<ProdOrTradeCashMain>();
		mains.addAll(sMains);
		mains.addAll(eMains);
		return mains;
	}
	
	/**
	 * 判断当前审批数据是否存在于主数据表中
	 * @param procProdOrTradeCash
	 * @return
	 */
	public List<ProdOrTradeCashMain> prodOrTradeCashExists(
			ProcProdOrTradeCash procProdOrTradeCash) {
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		String start = sdf.format(procProdOrTradeCash.getStartDate());
		String end = sdf.format(procProdOrTradeCash.getEndDate());
		StringBuilder jpql = new StringBuilder("select p from ProdOrTradeCashMain p join fetch p.company where p.defunctInd = 'N'");
		jpql.append(" and p.company.id="+procProdOrTradeCash.getCompany().getId());
		jpql.append(" and p.appType='"+procProdOrTradeCash.getAppType()+"'");
		jpql.append(" and p.variety='"+procProdOrTradeCash.getVariety()+"'");
		jpql.append(" and p.startDate ='"+start+"'");
		jpql.append(" and p.endDate ='"+end+"'");
		return entityService.find(jpql.toString());
	}
	
	/**
	 * 查询主数据历史数据
	 * @param procProdOrTradeCash
	 * @return
	 */
	public List<ProdOrTradeCashMain> getLastProdOrCashMainsBy(
			ProcProdOrTradeCash procProdOrTradeCash) {
		StringBuilder jpql = new StringBuilder("select p from ProdOrTradeCashMain p join fetch p.company where p.defunctInd = 'N'");
		jpql.append(" and p.company.id="+procProdOrTradeCash.getCompany().getId());
		jpql.append(" and p.appType='"+procProdOrTradeCash.getAppType()+"'");
		jpql.append(" and p.variety='"+procProdOrTradeCash.getVariety()+"'");
		return entityService.find(jpql.toString());
	}

	/**
	 * 查询满足品种主数据条件的支付明细
	 * @param prodOrTradeCashMain
	 * @return
	 */
	public List<PurchaseFundDetail> getPurchaseFundDetails(
			ProdOrTradeCashMain prodOrTradeCashMain) {
		
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		String start = sdf.format(prodOrTradeCashMain.getStartDate());
		String end = sdf.format(prodOrTradeCashMain.getEndDate());
		
		StringBuilder jpql = new StringBuilder("select p from PurchaseFundDetail p join fetch p.purchaseFund where p.defunctInd = 'N'");
		jpql.append(" and p.purchaseFund.company.id="+prodOrTradeCashMain.getCompany().getId());
		jpql.append(" and p.purchaseFund.tape='"+prodOrTradeCashMain.getAppType()+"'");
		jpql.append(" and p.purchaseFund.variety='"+prodOrTradeCashMain.getVariety()+"'");
		jpql.append(" and p.purchaseFund.appTime >='"+start+"'");
		jpql.append(" and p.purchaseFund.appTime <='"+end+"'");
		
		return entityService.find(jpql.toString());
	}

	public List<ProdOrTradeCashMain> getLastProdOrCashMainsBy(Long comId,
			String appType, String varity) {
		StringBuilder jpql = new StringBuilder("select p from ProdOrTradeCashMain p join fetch p.company where p.defunctInd = 'N'");
		jpql.append(" and p.company.id="+comId);
		jpql.append(" and p.appType='"+appType+"'");
		jpql.append(" and p.variety='"+varity+"'");
		return entityService.find(jpql.toString());
	}
	

	

}
