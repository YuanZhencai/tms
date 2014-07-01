package com.wcs.tms.service.process.common;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.util.ArrayUtils;

import com.google.common.collect.Lists;
import com.wcs.base.service.LoginService;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.common.model.Usermstr;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.model.ProcessAuth;
import com.wcs.tms.model.ProcessDefine;
import com.wcs.tms.model.ProcessMap;
import com.wcs.tms.view.process.common.entity.ProcessInstanceVo;

import filenet.vw.api.VWException;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueQuery;
import filenet.vw.api.VWStepElement;
import filenet.vw.api.VWWorkObject;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 流程已接收任务Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Stateless
public class ProcessAcceptedService implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(ProcessAcceptedService.class);

    @Inject
    private ProcessNewService processNewService;
    @Inject
    private PEManager peManager;
    @Inject
    private LoginService loginService;
    @Inject
    private ProcessWaitAcceptService processWaitService;
    @Inject
    ProcessUtilMapService processUtilMapService;

    /**
     * 
     * <p>Description:根据用户得到认证的流程定义 </p>
     * @param user
     * @return
     * @throws Exception
     */
    public List<ProcessDefine> findAuthProcessByUser(Usermstr user) {
        Validate.notNull(user, "用户为空");
        List<ProcessDefine> processDefineList = Lists.newArrayList();
        try {
            List<ProcessAuth> processAuthList = processNewService.findProcessAuthForLazy(user);
            if (processAuthList.isEmpty()) { return processDefineList; }
            for (ProcessAuth pa : processAuthList) {
                processDefineList.add(pa.getProcessDefine());
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return processDefineList;
    }

    /**
     * 
     * <p>Description: 查询已被当前用户锁定且被该用户接收任务</p>
     * @param queueName
     * @return
     * @throws VWException
     */
    public List<ProcessInstanceVo> vwfindAcceptTask(String queueName) throws VWException {
        Validate.notNull(queueName, "用户流程队列为空");
        // 已经被锁定
        int queueType = VWQueue.QUERY_READ_LOCKED;
        // 当前队列下被锁定的工作流程实例对象集合
        VWQueueQuery qQuery = peManager.vwfetchWorkObjByQue(queueName, queueType);
        List<ProcessInstanceVo> processAccept = Lists.newArrayList();
        // 当前登录系统用户
        String userName = this.loginService.getCurrentUserName();
        long index = 0L;
        while (qQuery.hasNext()) {
            // 取出工作对象
            VWWorkObject vobj = (VWWorkObject) qQuery.next();
            //得到流程Code
            String processCode = ProcessXmlUtil.getProcessAttribute("className", vobj.getWorkClassName(), "code");
			if(processCode==null || "".equals(processCode)){
				continue;
			}
            // 流程在CE的路径
            String cePath = ProcessXmlUtil.getProcessAttribute("className", vobj.getWorkflowName(), "cePath");
            // 流程编号
            String code = ProcessXmlUtil.getProcessAttribute("className", vobj.getWorkflowName(), "code");
            log.info("流程编号 "+vobj.getWorkflowNumber()+"锁定人"+vobj.getLockedUser());
            // 查看是否是当前用户锁定的任务
            if (userName.equals(vobj.getLockedUser())) {
                ProcessInstanceVo pacesspt = new ProcessInstanceVo();
                pacesspt.setProcInstId(vobj.getWorkflowNumber());
                /***9.10 modify for(申请公司和TMS流程实例编号)**/
        		String pidFn = vobj.getWorkflowNumber();
        		ProcessMap pm = processUtilMapService.getProcessMapByFnId(pidFn);
        		if(pm.getPidTms()==null || "".equals(pm.getPidTms())){
        			pacesspt.setPidTms(pidFn);
				}else{
					pacesspt.setPidTms(pm.getPidTms());
        		}
        		pacesspt.setCompanyName(pm.getCompanyName()==null ? "" : pm.getCompanyName());
        		/***9.10 modify end**/
        		
                pacesspt.setProcessCode(code);
                pacesspt.setProcessName(processWaitService.findProceeNameByCode(code));
                // 如果是 外债申请和外债展期申请 流程则按以下规则命名：
                // ProcDebtBorrow对象 thisShBorrowSe字段为G或H则命名“外债申请”如果为Z则命名“展期申请”
                if("TMS_BPM_RA_007".equals(code)){
                	pacesspt.setProcessName(processWaitService.findDebtBorrowNameByProcInstId(vobj.getWorkflowNumber()));
                }
                pacesspt.setNodeName(vobj.getStepName());
                pacesspt.setId(index++);
                // TODO 应该通过数据库去查流程定义所在CE的流程路径
                pacesspt.setProcessVersion(peManager.vwFindProcessVersion(cePath));
                pacesspt.setAcceptDate(vobj.getDateReceived());
                pacesspt.setSubmitDate(vobj.getLaunchDate());
                
                /******huhan add on 7.24 START* 得到xmlpath*******************/ 
                // 节点
                VWStepElement step = vobj.fetchStepElement();
                // 参数数组
                String params[] = step.getParameterNames();
                // 银行授信调剂流程判断是否是集团提交,True 通过xml读取路径
                boolean flag = ArrayUtils.contains(params, "TMS_Cop_Submit");
                String xmlPage = null;
                if (flag && (Boolean) step.getParameterValue("TMS_Cop_Submit")) {
                    xmlPage = ProcessXmlUtil.findStepProperty("className", vobj.getWorkflowName(), step.getStepName(),
                            "xmlPage");
                    pacesspt.setXmlPath(xmlPage);
                }
                /******huhan add on 7.24 END********************/ 
                
                processAccept.add(pacesspt);
            }
            // 工作对象下的当前节点对象
            // VWStepElement step = vobj.fetchStepElement();
            // // 得到String类型而且是用户自定义的参数
            // VWParameter[] pa = step.getParameters(VWFieldType.FIELD_TYPE_STRING, VWStepElement.FIELD_USER_DEFINED);
            // if (pa != null) {
            // for (VWParameter vwp : pa) {
            // // 得到该节点下的锁定人参数
            // if (vwp.getName().contains("_Exer")) {
            // // 是否就是当前登录系统的用户
            // if (vwp.getValue().equals(userName)) {
            // ProcessInstanceVo pacesspt = new ProcessInstanceVo();
            // pacesspt.setProcInstId(vobj.getWorkflowNumber());
            // pacesspt.setProcessCode(vobj.getWorkflowName());
            // pacesspt.setProcessName(processWaitService.findProceeNameByCode(vobj.getWorkflowName()));
            // pacesspt.setNodeName(vobj.getStepName());
            // pacesspt.setId(index++);
            // // TODO 应该通过数据库去查流程定义所在CE的流程路径
            // pacesspt.setProcessVersion(peManager.vwFindProcessVersion(ProcessDefineUtil.pressCePathMap.get(vobj
            // .getWorkflowName())));
            // pacesspt.setAcceptDate(vobj.getDateReceived());
            // processAccept.add(pacesspt);
            // }
            // }
            // }
            // }
        }
        
        //按提交时间排顺序
        processAccept = orderBySubDate(processAccept);
        
        return processAccept;
    }
    
    /**
	 * 流程显示排序(按提交时间时间排顺序)
	 * @param pivos
	 * @return
	 */
	private List<ProcessInstanceVo> orderBySubDate(List<ProcessInstanceVo> pivos){
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
     * <p>Description: 将任务放回公共收件箱</p>
     * @param workFlowNumber
     * @throws Exception
     */
    public void vwDiscardedTask(String wokrObjectNumber) {
        Validate.notNull(wokrObjectNumber, "工作对象编号能为空");
        // 得到流程实例的工作对象
        try {
            // VWWorkObject vobj = RosterHelper.vwfetchWorkObject(Env.getVWSession(), wokrObjectNumber, 0);
            VWWorkObject vobj = peManager.vwGetTmsWorkObject(wokrObjectNumber);
            // 得到当前任务节点
            VWStepElement step = vobj.fetchStepElement();
            step.doAbort();
            // // 当前登录系统用户
            // String userName = this.loginService.getCurrentUserName();
            // VWParameter[] pa = step.getParameters(VWFieldType.FIELD_TYPE_STRING, VWStepElement.FIELD_USER_DEFINED);
            // if (pa != null) {
            // for (VWParameter vwp : pa) {
            // // 得到该节点下的锁定人参数
            // if (vwp.getName().contains("_Exer")) {
            // if (vwp.getValue().equals(userName)) {
            // // 清空锁定人，并解锁任务放回公共队列
            // step.setParameterValue(vwp.getName(), "", true);
            // step.doSave(true);
            // }
            // }
            // }
            // }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

}
