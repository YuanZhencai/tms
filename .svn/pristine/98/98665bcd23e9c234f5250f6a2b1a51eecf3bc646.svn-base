package com.wcs.tms.service.process.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.StringUtils;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.mail.Mail;
import com.wcs.tms.mail.MailService;
import com.wcs.tms.mail.SendMailService;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.ProcGuarantee;
import com.wcs.tms.model.ProcessDefine;
import com.wcs.tms.model.ProcessMap;
import com.wcs.tms.service.process.common.patch.service.PatchMainService;
import com.wcs.tms.service.system.sysprocess.ProcessDefineService;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.ProcessInstanceVo;

import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWWorkObject;
import filenet.vw.api.VWWorkObjectNumber;


/**
 * <p>Project: tms</p>
 * <p>Description: 已提交流程Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Stateless
public class ProcessSubedService implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Inject
    EntityService entityService;
	@Inject
	LoginService loginService;
	@Inject
    PEManager peManager;
	@Inject
	ProcessDefineService processDefineService;
	@Inject
    MailService mailService;
    @Inject
    SendMailService sendMailService;
    @Inject
    ProcessUtilMapService processUtilMapService;
    @Inject
    PatchMainService patchMainService;
    
    private static final Log log = LogFactory.getLog(ProcessSubedService.class);
	
	
	/**
	 * 按状态查询相关流程实例vo
	 * @param conditionMap
	 * @return
	 */
	public List<ProcessInstanceVo> getProcessInstanceVos(Map<String ,Object> conditionMap) throws ServiceException{
		List<ProcessInstanceVo> pivos = new ArrayList<ProcessInstanceVo>();
		if(conditionMap.get("exeStatus")==null || "1".equals(conditionMap.get("exeStatus"))){
			pivos = this.getSubedProcessInstanceVos(conditionMap);
		}else if("2".equals(conditionMap.get("exeStatus"))){
			pivos = this.getTerminatedProcessInstanceVos(conditionMap);
		}
		//按提交时间排
		pivos = orderBySubDate(pivos);
		return pivos;
	}
	
	/**
	 * 流程显示排序(按提交时间排倒叙)
	 * @param pivos
	 * @return
	 */
	private List<ProcessInstanceVo> orderBySubDate(List<ProcessInstanceVo> pivos){
		Collections.sort(pivos, new Comparator<ProcessInstanceVo>() {
			@Override
			public int compare(ProcessInstanceVo o1, ProcessInstanceVo o2) {
				return o2.getSubmitDate().compareTo(o1.getSubmitDate());
			}
		});
//		Object[] objs = (Object[]) pivos.toArray();
//		for(int i=0 ;i<objs.length; i++){
//			for(int j=i; j<objs.length; j++){
//				Object temp = new Object();
//				//i的时间如果小于j的时间,交换位置
//				if(((ProcessInstanceVo)objs[i]).getSubmitDate().compareTo(((ProcessInstanceVo)objs[j]).getSubmitDate())<0){
//					temp = objs[j];
//					objs[j] = objs[i];
//					objs[i] = temp;
//				}
//			}
//		}
//		for(Object obj : objs){
//			newPivos.add((ProcessInstanceVo)obj);
//		}
		return pivos;
	}
	
	
	/**
	 * 得到执行中流程实例vo
	 * @return
	 * @throws ServiceException
	 */
	public List<ProcessInstanceVo> getSubedProcessInstanceVos(Map<String ,Object> conditionMap) throws ServiceException{
		/***包装VO信息************************/
		List<ProcessInstanceVo> pivos = new ArrayList<ProcessInstanceVo>();
		try {
		    // 查询条件，公司id
            String companyId = (String) conditionMap.get("companyId");
			//查询条件组装
			Map<String,Object> filterMap = this.getIngFilterMap(conditionMap);
			String filter = (String)filterMap.get("filter");
			Object[] substitutionVars = (Object[])filterMap.get("substitutionVars");
			//List<VWWorkObject> wobs = peManager.vwGetTmsWorkObjects();
			List<VWWorkObject> wobs = peManager.vwGetTmsWorkObjects(filter, substitutionVars);
			for(VWWorkObject wob : wobs){
				//得到流程Code
				//String processCode = ProcessDefineUtil.PROCESS_CLASS_CODE_MAP.get(le.getWorkClassName());
				String processCode = ProcessXmlUtil.getProcessAttribute("className", wob.getWorkClassName(), "code");
				if(processCode==null || "".equals(processCode)){
					continue;
				}
				//得到流程定义obj
				ProcessDefine pd = processDefineService.getProcDefineByCode(processCode);
				if(pd==null){
					continue;
				}
				//流程名称检查
				if(this.checkNoFilter(conditionMap ,pd)){
					//打印相关信息
					//peManager.printWob(wob);
					//找出节点执行人(锁定人)
					String nodeExer = wob.getLockedUser()==null ? "" : wob.getLockedUser();
					
					ProcessInstanceVo vo = new ProcessInstanceVo();
					vo.setProcInstId(wob.getWorkflowNumber());
					/***9.10 modify for(申请公司和TMS流程实例编号)**/
					String pidFn = wob.getWorkflowNumber();
					ProcessMap pm = processUtilMapService.getProcessMapByFnId(pidFn);
					//add by liushengbin 2012.10.8 剔除 公司查询条件不匹配的数据
                    String tempCompanyId = String.valueOf(pm.getCompanyId());
                    if (!StringUtils.isBlankOrNull(companyId) && !StringUtils.isBlankOrNull(tempCompanyId) && !companyId.equals(tempCompanyId)) {
                        continue;
                    }
					if(pm.getPidTms()==null || "".equals(pm.getPidTms())){
						vo.setPidTms(pidFn);
					}else{
						vo.setPidTms(pm.getPidTms());
					}
					vo.setCompanyName(pm.getCompanyName()==null ? "" : pm.getCompanyName());
					/***9.10 modify end**/
					
					vo.setProcessCode(processCode);
					vo.setProcessName(pd.getProcessName());
					vo.setProcessVersion(peManager.vwFindProcessVersion(ProcessXmlUtil.getProcessAttribute("className", wob.getWorkflowName(), "cePath")));
					
					vo.setDescrible(pd.getProcessMemo());
					vo.setSubmitDate(wob.getLaunchDate());
					vo.setNodeName(wob.getStepName());
					vo.setNodeExer(nodeExer);
					vo.setTerminalFlag(wob.getStepName()==null ? true :false);
					
					//(银行授信或者集团授信)特殊处理,看step是否允许查看确认详细
					if("TMS_BankCreditBloc".equals(wob.getWorkflowName()) || "TMS_BankCreditReg".equals(wob.getWorkflowName())){
						//得到执行的最后一步
						VWLogElement lastLe = getLastStepLog(wob.getWorkflowNumber());
						if(lastLe!=null){
							String confirmView = ProcessXmlUtil.findStepProperty("className", lastLe.getWorkflowName(), lastLe.getStepName(), "confirmView");
							if(confirmView!=null && !"".equals(confirmView)){
								if("true".equals(confirmView)){
									vo.setUserComfirmView(true);
								}
							}
						}
						//如果没有当前节点，用confirmView判断流程是否结束
						if(wob.getStepName()==null || "".equals(wob.getStepName())){
							vo.setTerminalFlag(vo.getUserComfirmView() ? true : false);
						}
					}
					
					pivos.add(vo);
				}
			}
		} catch (Exception e) {
			log.error("getSubedProcessInstanceVos方法 得到执行中流程实例vo 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return pivos;
	}
	
	/**
	 * 得到执行中流程的过滤器
	 * @param conditionMap
	 * @return
	 */
	private Map<String,Object> getIngFilterMap(Map<String ,Object> conditionMap) throws Exception{
		Map<String,Object> filterMap = new HashMap<String, Object>();
		StringBuilder filter = new StringBuilder("1=1");
		List<Object> varsList = new ArrayList<Object>();
		//登录人
//		//1.创建人检查
		filter.append(" and F_Originator = :originator");
		varsList.add(peManager.vwGetIdByUserName(loginService.getCurrentUserName()));

		//2.流程实例编号检查
		if(conditionMap.get("procInstId")!=null && !"".equals(conditionMap.get("procInstId")) ){
			filter.append(" and F_WobNum = :wobNum");
			//modified on 2012-11-8
            String fnId = processUtilMapService.getFnIdByTmsId((String) conditionMap.get("procInstId"));
			varsList.add(new VWWorkObjectNumber(fnId));
			
		}
		//3.提交时间
		//开始时间
    	if(null!=conditionMap.get("startDate")){

    		filter.append(" and F_StartTime >= :startDate");
    		varsList.add(conditionMap.get("startDate"));
    	}
    	//结束时间
    	if(null!=conditionMap.get("endDate")){
    		filter.append(" and F_StartTime <= :endDate");
    		varsList.add(DateUtil.addOneDay((Date)conditionMap.get("endDate")));
    	}
		
    	Object[] substitutionVars = varsList.toArray();
    	filterMap.put("filter", filter.toString());
    	filterMap.put("substitutionVars", substitutionVars);
    	
		return filterMap;
	}
	
	
	/**
	 * 得到已结束的流程实例vo
	 * @param conditionMap
	 * @return
	 */
	public List<ProcessInstanceVo> getTerminatedProcessInstanceVos(Map<String ,Object> conditionMap) throws ServiceException{
		/***包装VO信息************************/
		List<ProcessInstanceVo> pivos = new ArrayList<ProcessInstanceVo>();
		try {
			//2014-05-16 add by liushengbin 查询4.11~4.28故障期间的已提交数据
		    List<ProcessInstanceVo> pivosFor411List = patchMainService.getTerminatedProcessInstanceVosFor411(conditionMap);
			
		    // 查询条件，公司名称
            String companyId = (String) conditionMap.get("companyId");
			//查询条件组装
			Map<String,Object> filterMap = this.getTerFilterMap(conditionMap);
			String filter = (String)filterMap.get("filter");
			Object[] substitutionVars = (Object[])filterMap.get("substitutionVars");
			List<VWLogElement> les = peManager.vwEventLogWob(filter, substitutionVars);
			for(VWLogElement le : les){
				ProcessInstanceVo vo = new ProcessInstanceVo();
				vo.setProcInstId(le.getWorkFlowNumber());
				
				//modify by liushengbin 2014-05-26去除重复逻辑：
             	//通过PE API查找其他已提交流程的循环中，如果遇到4.11~4.28之间的已提交的流程实例号，就跳过（即continue）;
            	 if(pivosFor411List.contains(vo)){
            		 continue;
            	 }         
				
				//得到流程Code
				//String processCode = ProcessDefineUtil.PROCESS_CLASS_CODE_MAP.get(le.getWorkClassName());
				String processCode = ProcessXmlUtil.getProcessAttribute("className", le.getWorkClassName(), "code");
				if(processCode==null || "".equals(processCode)){
					continue;
				}
				//得到流程定义obj
				ProcessDefine pd = processDefineService.getProcDefineByCode(processCode);
				if(pd==null){
					continue;
				}
				//流程名称检查
				if(this.checkNoFilter(conditionMap ,pd)){
					//打印相关信息
					//peManager.printWob(le);
					//节点执行人
					String nodeExer = "";
					
					
					/***9.10 modify for(申请公司和TMS流程实例编号)**/
					String pidFn = le.getWorkFlowNumber();
					ProcessMap pm = processUtilMapService.getProcessMapByFnId(pidFn);
					//add by liushengbin 2012.10.8 剔除 公司查询条件不匹配的数据
                    String tempCompanyId = String.valueOf(pm.getCompanyId());
                    if (!StringUtils.isBlankOrNull(companyId) && !StringUtils.isBlankOrNull(tempCompanyId) && !companyId.equals(tempCompanyId)) {
                        continue;
                    }
					if(pm.getPidTms()==null || "".equals(pm.getPidTms())){
						vo.setPidTms(pidFn);
					}else{
						vo.setPidTms(pm.getPidTms());
					}
					vo.setCompanyName(pm.getCompanyName()==null ? "" : pm.getCompanyName());
					/***9.10 modify end**/
					
					vo.setProcessCode(processCode);
					vo.setProcessName(pd.getProcessName());
					vo.setProcessVersion(peManager.vwFindProcessVersion(ProcessXmlUtil.getProcessAttribute("className", le.getWorkflowName(), "cePath")));
					vo.setDescrible(pd.getProcessMemo());
					vo.setSubmitDate((Date)le.getDataField("F_StartTime").getValue());
					vo.setProcessEndDate(le.getTimeStamp());
					vo.setNodeName(le.getStepName());
					vo.setNodeExer(nodeExer);
					vo.setTerminalFlag(true);
					
					//集团授信特殊处理,看step是否允许查看确认详细
					if("TMS_BankCreditBloc".equals(le.getWorkflowName())){
						//得到执行的最后一步
						VWLogElement lastLe = getLastStepLog(le.getWorkFlowNumber());
						if(lastLe!=null){
							String confirmView = ProcessXmlUtil.findStepProperty("className", lastLe.getWorkflowName(), lastLe.getStepName(), "confirmView");
							if(confirmView!=null && !"".equals(confirmView)){
								if("true".equals(confirmView)){
									vo.setUserComfirmView(true);
								}
							}
						}
					}					
					pivos.add(vo);
				}				
			}
			//把4.11~4.28之间的处理任务追加到总的结果集中
	        pivos.addAll(pivosFor411List);			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("getTerminatedProcessInstanceVos方法 得到已结束的流程实例vo 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		
		return pivos;
	}
	
	/**
	 * 得到已结束流程的过滤器
	 * @param conditionMap
	 * @return
	 */
	private Map<String,Object> getTerFilterMap(Map<String ,Object> conditionMap) throws Exception{
		Map<String,Object> filterMap = new HashMap<String, Object>();
		//已结束的
		StringBuilder filter = new StringBuilder("1=1 and F_EventType = :EventType");
		List<Object> varsList = new ArrayList<Object>();
		varsList.add(ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal));
		//登录人
		//1.创建人检查
		filter.append(" and F_Originator = :originator");
		varsList.add(peManager.vwGetIdByUserName(loginService.getCurrentUserName()));

		//2.流程实例编号检查
		if(conditionMap.get("procInstId")!=null && !"".equals(conditionMap.get("procInstId")) ){
			filter.append(" and F_WobNum = :wobNum");
			//modified on 2012-11-8
            String fnId = processUtilMapService.getFnIdByTmsId((String) conditionMap.get("procInstId"));
			varsList.add(new VWWorkObjectNumber(fnId));
			
		}
		//3.提交时间
		//开始时间
    	if(null!=conditionMap.get("startDate")){

    		filter.append(" and F_StartTime >= :startDate");
    		varsList.add(conditionMap.get("startDate"));
    	}
    	//结束时间
    	if(null!=conditionMap.get("endDate")){
    		filter.append(" and F_StartTime <= :endDate");
    		varsList.add(DateUtil.addOneDay((Date)conditionMap.get("endDate")));
    	}
		
//    	//4.流程名称检查
//    	if(conditionMap.get("processId")!=null && !"0".equals(conditionMap.get("processId")) ){
//			if(!pd.getId().toString().equals(conditionMap.get("processId"))){
//				chackFlag = false;
//			}
//		}
    	Object[] substitutionVars = varsList.toArray();
    	filterMap.put("filter", filter.toString());
    	filterMap.put("substitutionVars", substitutionVars);
    	
		return filterMap;
	}
	
	
	/**
	 * 检查非过滤器条件
	 * @param conditionMap
	 * @param pd
	 * @return
	 */
	private boolean checkNoFilter(Map<String ,Object> conditionMap,ProcessDefine pd){
		//4.流程名称检查
    	if(conditionMap.get("processId")!=null && !"0".equals(conditionMap.get("processId")) ){
			if(!pd.getId().toString().equals(conditionMap.get("processId"))){
				return false;
			}
		}
		return true;
	}
	
	
	
	
	/**
	 * 得到流程执行的最后一步活动节点
	 * @param workFlowNumber
	 * @return
	 */
	private VWLogElement getLastStepLog (String workFlowNumber) throws Exception{
		StringBuilder filter = new StringBuilder("1=1 and F_EventType = :EventType");
		List<Object> varsList = new ArrayList<Object>();
		varsList.add(ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd));
		
		filter.append(" and F_WobNum = :wobNum");
		varsList.add(new VWWorkObjectNumber(workFlowNumber));
		
		Object[] substitutionVars = varsList.toArray();
		List<VWLogElement> les = peManager.vwEventLogWob(filter.toString(), substitutionVars);
		
		if(les.size()==0){
			return null;
		}else{
			return les.get(les.size()-1);
		}
		
	}
	
	
	
	
	/**
	 * 流程终止
	 * @param processInstanceVo
	 */
	public void doTerminal(ProcessInstanceVo processInstanceVo) throws ServiceException{
		log.info("终止流程"+processInstanceVo.getProcInstId()+"~~~~~~~~~~~~~~~~");
		String workObjNumber = processInstanceVo.getProcInstId();
		try {
			VWWorkObject wob = peManager.vwGetTmsWorkObject(workObjNumber);
			//String queueName = wob.fetchStepElement().getQueueName();
			peManager.vwTerminalWorkObject(wob);
			/********add on 2012-12-19******/
			if("内部担保申请流程".equals(processInstanceVo.getProcessName())){
				//根据配置和参数从数据库查出流程实例数据
				ProcGuarantee guarantee = this.findGuaranteeByProcId(processInstanceVo);
				//数据设置为无效
				guarantee.setDefunctInd("Y");
				//更新数据库数据
				entityService.update(guarantee);
			}
			/******************************/
			/************add on 2013-3-28 by yan******************/
			// 对现金池流程进行特殊操作，更新数据库“是否为发起人终止标识”状态，为了在现金池报表中将手动终止的流程数据过滤掉
			String isCashPoolFlag = ProcessXmlUtil.getProcessAttribute("code", processInstanceVo.getProcessCode(), "isCashPoolProcess");
			if("Y".equals(isCashPoolFlag)){
				String entityClass = ProcessXmlUtil.getProcessAttribute("code", processInstanceVo.getProcessCode(), "entityClass");
				// 更新数据库流程状态(新增字段terminateFlag add on 2013-3-29)
				this.setTerminateFlag(entityClass, processInstanceVo.getProcInstId());
			}
			/*****************************************************/
			//同时发送邮件
			sendAll(workObjNumber);
		} catch (Exception e) {
			log.error("doTerminal方法 流程终止 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}
	
	/**
	 * 更新终止流程数据库状态
	 * @param entityClass
	 * @param procInstId
	 */
	private void setTerminateFlag(String entityClass, String procInstId) {
		try{
			StringBuilder jpql = new StringBuilder("update "+entityClass+" as p set p.terminateFlag = 'Y' where p.procInstId = '"+procInstId+"'");
			entityService.excuteUpdate(jpql);
		}catch(Exception e){
			log.error("setTerminateFlag方法 更新终止流程数据库状态 出现异常：",e);
		}
	}

	/**
	 * 查出内部担保流程数据
	 * @param processInstanceVo
	 * @return
	 */
	private ProcGuarantee findGuaranteeByProcId(ProcessInstanceVo processInstanceVo) {
		StringBuilder jpql = new StringBuilder("select c from ProcGuarantee c where c.defunctInd = 'N' ");
		jpql.append(" and c.procInstId = '"+processInstanceVo.getProcInstId()+"'"); 
		return (ProcGuarantee) entityService.find(jpql.toString()).get(0);
	}

	/**
     * 给所有相关人发送
     * @param processId
     */
    private void sendAll(String processId) {
        List<Mail> mails = new ArrayList<Mail>();
        StringBuilder filter = new StringBuilder("1=1 and (F_EventType = :EventType or F_EventType = :EventTypeL or F_EventType = :EventTypeT)");
        Object[] substitutionVars = new Object[4];
        filter.append(" and F_WobNum = :wobNum");
        substitutionVars[0] = ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd);
        substitutionVars[1] = ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.Lock);
        substitutionVars[2] = ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ForcedToTerminate);
        substitutionVars[3] = new VWWorkObjectNumber(processId);
        mailService.initMailPE();
        mails = mailService.findAllEmailByTerProcess(filter.toString(), substitutionVars);

        sendMailService.send(mails);
    }
	
	
}
