package com.wcs.tms.service.process.common;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.util.ArrayUtils;

import com.google.common.collect.Lists;
import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.MathUtil;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcDebtBorrow;
import com.wcs.tms.model.ProcessMap;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;
import com.wcs.tms.view.process.common.entity.ProcessInstanceVo;

import filenet.vw.api.VWException;
import filenet.vw.api.VWFieldType;
import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWParameter;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueQuery;
import filenet.vw.api.VWStepElement;
import filenet.vw.api.VWWorkObject;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 待接收任务Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Stateless
public class ProcessWaitAcceptService implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Inject
    private PEManager peManager;
    @Inject
    private EntityService entityService;
    @Inject
    private LoginService loginService;
    @Inject
    ProcessUtilMapService processUtilMapService;
    
    private static final Log log = LogFactory.getLog(ProcessWaitAcceptService.class);

    /**
     * 
     * <p>Description: 根据流程定义编号查询流程名称</p>
     * @param processCode 流程实例编号
     * @return
     * @throws Exception
     */
    public String findProceeNameByCode(String processCode) throws ServiceException {
        Validate.notNull(processCode, "TMS流程编号为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select pd.processName from ProcessDefine pd").append(
                    " where pd.processCode=?1 and pd.defunctInd = 'N'");
            List<String> list = this.entityService.find(bulder.toString(), processCode);
            if (list.isEmpty()) { return null; }
            return list.get(0);
        } catch (Exception e) {
//            throw new ServiceException(e);
        	e.printStackTrace();
        }
		return null;
    }

    /**
     * 
     * <p>Description: 查询流程对应的公司</p>
     * @param processInd
     * @param entityClass
     * @return
     * @throws ServiceException
     */
    public String findCompanyByProcee(String processInd, String entityClass) {
        Validate.notNull(processInd, "流程编号为空");
        Validate.notNull(entityClass, "查询实体为空");
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("select p.company.sapCode from ").append(entityClass)
                    .append(" p where p.procInstId=?1 and p.defunctInd = 'N'");
            List<String> comList = entityService.find(builder.toString(), processInd);
            if (comList.size() > 1) { throw new ServiceException("一个流程实例编号只属于一个公司"); }
            if (comList.isEmpty()) { return null; }
            return comList.get(0);
        } catch (Exception e) {
//            throw new ServiceException(e);
        	e.printStackTrace();
        }
		return null;
    }

    /**
     * 
     * <p>Description: 根据MDS SAP查询应用系统的公司</p>
     * @param sqpCode
     * @return
     * @throws ServiceException
     */
    public Company findCompany(String sqpCode) throws ServiceException {
        Validate.notNull(sqpCode, "公司SAP代码为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select c from Company c where c.sapCode=?1 and c.defunctInd = 'N' and c.status='Y'");
            List<Company> cList = entityService.find(bulder.toString(), sqpCode);
            if (cList.isEmpty()) { return null; }
            if (cList.size() > 1) { throw new ServiceException("根据SAP查询存在多个公司"); }
            return cList.get(0);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description:根据流程节点名称和流程编号得到节点URL </p>
     * @param stepName 节点名称
     * @param processNumber 流程编号
     * @return
     * @throws ServiceException
     */
    public String findStepUrl(String stepName, String processNumber) throws ServiceException {
        Validate.notNull(stepName, "流程节点名称为空");
        Validate.notNull(processNumber, "流程编号为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append(" select pn.nodeIdentity from ProcessNode pn")
                    .append(" join pn.processDefine pd  where pn.nodeName=?1")
                    .append(" and pd.processCode=?2 and pd.defunctInd = 'N' and pn.defunctInd = 'N' and pn.status='Y'");
            List<String> list = this.entityService.find(bulder.toString(), stepName, processNumber);
            if (list.isEmpty()) { return null; }
            return list.get(0);
        } catch (Exception e) {
//            throw new ServiceException(e);
        	e.printStackTrace();
        }
		return null;

    }

    /**
     * 
     * <p>Description:查询等待被接收的任务 (判断逻辑：1 首先查询该登录人的公司SAP代码)</p>
     * @param queueName
     * @param queueType
     * @return
     * @throws VWException
     */
    public List<ProcessInstanceVo> vwFindNotReceiveTask(String queueName) throws ServiceException {
        Validate.notNull(queueName, "用户流程队列为空");
        try {
        	long startTime = System.currentTimeMillis();
            List<ProcessInstanceVo> processAccept = Lists.newArrayList();
            // 查询该登录人的公司SAP代码
            List<String> sapList = loginService.findCompanySapCode();
            // if (sapList.isEmpty()) { return processAccept; }
            int queueType = VWQueue.QUERY_NO_OPTIONS;
            VWQueueQuery qQuery = peManager.vwfetchWorkObjByQue(queueName, queueType);
            while (qQuery.hasNext()) {
                // 取出工作对象
                VWWorkObject vobj = (VWWorkObject) qQuery.next();
                // 节点
                VWStepElement step = vobj.fetchStepElement();
                /************************************/
                //产品专项总监特殊处理
                //add on 2012-9-11 by yanchangjing
                if("TMS_Cop_Gv2director".equals(queueName)){
                	VWParameter p = step.getParameter("TMS_PROD_VARITY");
//                	List<String> varities = ProcessXmlUtil.getVarityByUsername("id", "ProdOrTradeCash", loginService.getCurrentUserName());
                	List<String> varities = this.getVarietiesBy("TMS.USER.NAME", loginService.getCurrentUserName());
                	if(varities == null || !varities.contains(p.getValue())){
                		continue;
                	}
                }
               /************************************/ 
                // 得到系统所有流程className
                List<String> allClass = ProcessXmlUtil.getAllProcessAttribut("className");
                // 判断是否为TMS流程
                if (allClass.contains(vobj.getWorkClassName())) {
                    // 参数数组
                    String params[] = step.getParameterNames();
                    // 银行授信调剂流程判断是否是集团提交,True 通过xml读取路径
                    boolean flag = ArrayUtils.contains(params, "TMS_Cop_Submit");
                    String xmlPage = null;
                    if (flag && (Boolean) step.getParameterValue("TMS_Cop_Submit")) {
                        xmlPage = ProcessXmlUtil.findStepProperty("className", vobj.getWorkflowName(), step.getStepName(),
                                "xmlPage");
                    }
                    // 流程在CE的路径
                    String cePath = ProcessXmlUtil.getProcessAttribute("className", vobj.getWorkflowName(), "cePath");
                    // 流程编号
                    String code = ProcessXmlUtil.getProcessAttribute("className", vobj.getWorkflowName(), "code");
                    // 流程实体类
                    String entity = ProcessXmlUtil.getProcessAttribute("className", vobj.getWorkflowName(), "entityClass");
                    // 该节点是否需要判定人(重新申请，申请确认)
                    String createrCheck = ProcessXmlUtil.findStepProperty("className", vobj.getWorkflowName(),
                            step.getStepName(), "createrCheck");
                    // 是否可以跨公司查看
                    // String acrossCompany = ProcessXmlUtil.findStepProperty("className", vobj.getWorkflowName(),
                    // step.getStepName(),
                    // "acrossCompany");
                    // 是否提取操作人通过节点获取
                    String drawOperater = ProcessXmlUtil.findStepProperty("className", vobj.getWorkflowName(),
                            step.getStepName(), "drawOperater");
                    // 得到用户所在公司的SAP代码
                    for (String sapCode : sapList) {
                        // 看该公司是否发存在应用系统当中
                        Company tmsCom = findCompany(sapCode);
                        if (tmsCom == null) {
                            continue;
                        }
                        // 判断该公司是否属于集团公司
                        if ("Y".equals(tmsCom.getCorporationFlag())) {
                            String copStep = ProcessXmlUtil.findStepProperty("className", vobj.getWorkflowName(),
                                    step.getStepName(), "copStep");
                            // 判断该节点是否该集团公司人处理,集团的人可以跨公司处理任务
//                            if ("true".equals(copStep)) {
//                                // 是否需要校验是同一个人
//                                if ("true".equals(createrCheck)) {
//                                    // 当前登录人和流程发起人是否是同一人
//                                    if (loginService.getCurrentUserName().equals(vobj.getOriginator())) {
//                                        addProcessWaitTask(processAccept, vobj, cePath, code, xmlPage);
//                                    }
//                                } else {
//                                    addProcessWaitTask(processAccept, vobj, cePath, code, xmlPage);
//                                }
//                            }
                            //huhan modify 7.24 改为先验证创建人，再验证是否为集团人
                            // 是否需要校验是同一个人
                            if ("true".equals(createrCheck)) {
                            	// 当前登录人和流程发起人是否是同一人
                            	if (loginService.getCurrentUserName().equals(vobj.getOriginator())) {
                            		addProcessWaitTask(processAccept, vobj, cePath, code, xmlPage);
                            	}
                            }else{
                            	// 判断该节点是否该集团公司人处理,集团的人可以跨公司处理任务
                            	if ("true".equals(copStep)) {
                                   addProcessWaitTask(processAccept, vobj, cePath, code, xmlPage);
                                }
                            }
                        } else {
                            // 查询该流程发起的公司SAP代码
                            String sap = findCompanyByProcee(vobj.getWorkflowNumber(), entity);
                            // 判断是否是同一个公司发起的流程
                            if (sapCode.equals(sap)) {
                                // 是否是申请队队的重新申请，申请确认任务
                                if ("true".equals(createrCheck)) {
                                    if (loginService.getCurrentUserName().equals(vobj.getOriginator())) {
                                        addProcessWaitTask(processAccept, vobj, cePath, code, xmlPage);
                                    }
                                } else if (drawOperater != null && "true".equals(drawOperater)) {
                                    String operateUser = isConstansParam(params, step);
                                    if (operateUser != null && !"".equals(operateUser)) {
                                        // 参数提取的操作人和流程发起人是否是同一人
                                        if (operateUser.equals(loginService.getCurrentUserName())) {
                                            addProcessWaitTask(processAccept, vobj, cePath, code, xmlPage);
                                        }
                                    } else {
                                        addProcessWaitTask(processAccept, vobj, cePath, code, xmlPage);
                                    }
                                } else {
                                    addProcessWaitTask(processAccept, vobj, cePath, code, xmlPage);
                                }
                            }
                        }
                    }
                  //add by liushengbin date:20121228 内部担保流程特殊处理：
                    //当前节点在，<担保方财务经理审批><担保方总经理审批>以及确认节点时，让待办能看到
                    if("TMS_GuaranteeReq".equals(vobj.getWorkClassName()) 
                            && ("担保方财务经理审批".equals(vobj.getStepName()) 
                            || "担保方总经理审批".equals(vobj.getStepName())
                            || "担保方财务经理确认审批".equals(vobj.getStepName())
                            || "担保方总经理确认审批".equals(vobj.getStepName())
                              )){
                        log.info("内部担保流程，待办实例ID："+vobj.getWorkflowNumber());
                        addProcessWaitTask(processAccept, vobj, cePath, code, xmlPage);
                    }
                }
                

            }
            

            // 按提交时间排顺序
            processAccept = orderBySubDate(processAccept);
            long endTime = System.currentTimeMillis();
            log.info("根据当前队列查询所有待接收任务耗时："+(endTime - startTime));
            return processAccept;
        } catch (VWException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 是否包含操作人参数</p>
     * @param params
     * @return
     */
    private String isConstansParam(String[] params, VWStepElement step) {
        String operate = null;
        if (params != null && params.length > 0) {
            for (String param : params) {
                if (param.contains("Exr")) {
                    try {
                        operate = step.getParameterValue(param).toString();
                    } catch (VWException e) {
                        log.error("isConstansParam方法 是否包含操作人参数 出现异常：",e);
                    }
                    break;
                }
            }
        }
        return operate;
    }

    /**
     * 流程显示排序(按提交时间排顺序)
     * @param pivos
     * @return
     */
    private List<ProcessInstanceVo> orderBySubDate(List<ProcessInstanceVo> pivos) {
        Collections.sort(pivos, new Comparator<ProcessInstanceVo>() {
            @Override
            public int compare(ProcessInstanceVo o1, ProcessInstanceVo o2) {
                return o1.getSubmitDate().compareTo(o2.getSubmitDate());
            }
        });
        return pivos;
    }

    /**
     * 
     * <p>Description: 组装列表信息</p>
     * @param processAccept
     * @param index
     * @param vobj
     * @param cePath
     * @param code
     * @return
     * @throws VWException
     */
    private void addProcessWaitTask(List<ProcessInstanceVo> processAccept, VWWorkObject vobj, String cePath, String code,
            String xmlPage) throws VWException {
        ProcessInstanceVo pacesspt = new ProcessInstanceVo();
        String proccInd = vobj.getWorkflowNumber();
        pacesspt.setProcInstId(proccInd);
        /***9.10 modify for(申请公司和TMS流程实例编号)**/
		String pidFn = proccInd;
		ProcessMap pm = processUtilMapService.getProcessMapByFnId(pidFn);
		if(pm.getPidTms()==null || "".equals(pm.getPidTms())){
			pacesspt.setPidTms(pidFn);
		}else{
			pacesspt.setPidTms(pm.getPidTms());
		}
		pacesspt.setCompanyName(pm.getCompanyName()==null ? "" : pm.getCompanyName());
		/***9.10 modify end**/
        
        pacesspt.setProcessCode(code);
        pacesspt.setProcessName(findProceeNameByCode(code));
        // 如果是 外债申请和外债展期申请 流程则按以下规则命名：
        // ProcDebtBorrow对象 thisShBorrowSe字段为G或H则命名“外债申请”如果为Z则命名“展期申请”
        if("TMS_BPM_RA_007".equals(code)){
        	pacesspt.setProcessName(this.findDebtBorrowNameByProcInstId(vobj.getWorkflowNumber()));
        }
        pacesspt.setNodeName(vobj.getStepName());
        pacesspt.setProcessVersion(peManager.vwFindProcessVersion(cePath));
        pacesspt.setAcceptDate(vobj.getDateReceived());
        pacesspt.setSubmitDate(vobj.getLaunchDate());
        if (xmlPage != null && !"".equals(xmlPage)) {
            pacesspt.setXmlPath(xmlPage);
        }
        for (ProcessInstanceVo vo : processAccept) {
            if (proccInd.equals(vo.getProcInstId())) { return; }
        }
        processAccept.add(pacesspt);
    }

    /**
     * 根据外债流程流程实例编号 查询其具体流程名称
     * @param workflowNumber
     * @return
     */
    public String findDebtBorrowNameByProcInstId(String workflowNumber) {
    	StringBuilder jpql = new StringBuilder("select db from ProcDebtBorrow db where db.defunctInd = 'N'");
    	jpql.append(" and db.procInstId = '"+workflowNumber+"'");
    	List<ProcDebtBorrow> procDebtBorrows = entityService.find(jpql.toString());
    	if( procDebtBorrows == null || procDebtBorrows.size() == 0 ){
    		return findProceeNameByCode("TMS_BPM_RA_007");
    	}else{
    		// thisShBorrowSe字段为G或H则命名“外债申请”如果为Z则命名“展期申请”
        	if(!MathUtil.isEmptyOrNull(procDebtBorrows.get(0).getThisShBorrowSe())){
        		String procName = findProceeNameByCode("TMS_BPM_RA_007");
        		return "Z".equals(procDebtBorrows.get(0).getThisShBorrowSe()) ? (procName+"（展期申请）") : (procName+"（外债申请）");
        	}else{
        		return findProceeNameByCode("TMS_BPM_RA_007");
        	}
    	}
    	
	}

	/**
     * 
     * <p>Description: 接收但不处理任务</p>
     * @param queueName
     * @param workObjectNum
     * @throws VWException 
     */
    public int vwAcceptTask(String workObjectNum) {
        Validate.notNull(workObjectNum, "工作对象编号能为空");
        VWWorkObject vobj;
        int flag = 0;
        try {
            // vobj = RosterHelper.vwfetchWorkObject(Env.getVWSession(), workObjectNum, 0);
            vobj = peManager.vwGetTmsWorkObject(workObjectNum);
            log.info(vobj.fetchLockedStatus());
            // 得到当前任务节点
            VWStepElement step = vobj.fetchStepElement();
            // 当前登录系统用户
            String userName = this.loginService.getCurrentUserName();
            VWParameter[] pa = step.getParameters(VWFieldType.FIELD_TYPE_STRING, VWStepElement.FIELD_USER_DEFINED);
            // 锁定节点
            step.doLock(false);
            if (pa != null) {
                vwSetOperationParam(step, userName, pa);
            }
            // 保存但是不解锁
            step.doSave(false);
            return flag;
        } catch (Exception e) {
            flag = 1;
            return flag;
        }
    }

    /**
     * 
     * <p>Description: 审批是否通过</p>
     * @param workObjectNum
     * @param isPass
     * @throws VWException
     */
    public void vwDisposeTask(String workObjectNum, boolean isPass, String memo){
        Validate.notNull(workObjectNum, "工作对象编号不能为空");
        try {
            this.peManager.vwDisposeTask(workObjectNum, isPass, memo);
        } catch (VWException e) {
        	log.error("vwDisposeTask方法 审批是否通过 出现异常：",e);
        }
    }

    /**
     * 
     * <p>Description: 根据条件查询不同流程的执行步骤详细记录</p>
     * @param filter 过滤参数条件
     * @param substitutionVars 参数值数组
     * @param userName 用户名
     * @return
     * @throws ServiceException
     */
    public List<ProcessDetailVo> findProcessDetialList(String filter, Object[] substitutionVars) throws ServiceException {
        try {
            List<ProcessDetailVo> processDetailList = Lists.newArrayList();
            // 查询已经被执行流程步骤，通过记录的日志
            List<VWLogElement> les = peManager.vwEventLogWob(filter, substitutionVars);
            // // 日志元素按序列号排序
            // Collections.sort(les, new Comparator() {
            // @Override
            // public int compare(Object o1, Object o2) {
            // int flag = 0;
            // try {
            // VWLogElement le1 = (VWLogElement) o1;
            // VWLogElement le2 = (VWLogElement) o2;
            // flag = Integer.valueOf(le1.getSequenceNumber()).compareTo(Integer.valueOf(le2.getSequenceNumber()));
            // } catch (VWException e) {
            //
            // e.printStackTrace();
            // }
            // return flag;
            // }
            //
            // });
            if (les.isEmpty()) { return processDetailList; }
            long index = 0;
            for (VWLogElement le : les) {
                // 创建流程详细VO对象
                ProcessDetailVo pdetail = new ProcessDetailVo();
                int stopEvnent = ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal);
                if (le.getEventType() == stopEvnent) {
                    pdetail.setProssNodeName("流程终止");
                } else {
                    pdetail.setProssNodeName(le.getStepName());
                }
                pdetail.setOperatorTime((Date) le.getDataField("F_TimeStamp").getValue());
                pdetail.setNodeMemo((String) le.getDataField("F_Comment").getValue());
                // le.getDataField("F_UserId").getValue()
                pdetail.setOperatorsName(le.getUserName());
                pdetail.setId(index++);
                processDetailList.add(pdetail);
            }
            return processDetailList;
        } catch (Exception e) {
            throw new ServiceException(ExceptionMessage.PROCESS_DETAIL, e);
        }

    }

    /**
     * 
     * <p>Description: 设置操作参数</p>
     * @param step
     * @param userName
     * @param pa
     * @throws VWException
     */
    private void vwSetOperationParam(VWStepElement step, Object paramValue, VWParameter[] pa) throws VWException {
        for (VWParameter vwp : pa) {
            // 得到该节点下的锁定人参数
            String paramName = vwp.getName();
            if (paramName.contains("_Exer")) {
                // 并设置锁定人
                step.setParameterValue(paramName, paramValue, true);
            }
            // 是否批准
            if (paramName.contains("_Pass")) {
                // 是否通过
                step.setParameterValue(paramName, paramValue, true);
            }
        }
    }
    
    /**
	 * 查询品种
	 * @param codeCat 
	 * @param userName 用户名
	 * @return
	 */
	public List<String> getVarietiesBy(String codeCat, String userName){
		StringBuilder jpql = new StringBuilder("select d.codeVal from Dict d where d.defunctInd='N'");
		jpql.append(" and d.codeCat='"+codeCat+"'");
		jpql.append(" and d.codeKey='"+userName+"'");
		return entityService.find(jpql.toString());
	}

}
