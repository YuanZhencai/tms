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
import com.wcs.tms.message.ExceptionMessage;
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
 * 
 * <p>Project: tms</p>
 * <p>Description: 已处理任务Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Stateless
public class ProcessDealedService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    EntityService entityService;
    @Inject
    PEManager peManager;
    @Inject
    ProcessDefineService processDefineService;
    @Inject
    LoginService loginService;
    @Inject
    ProcessUtilMapService processUtilMapService;
    @Inject
    PatchMainService patchMainService;
    
    private static final Log log = LogFactory.getLog(ProcessDealedService.class);

    public List<ProcessInstanceVo> getProcessInstanceVos(Map<String, Object> conditionMap) throws ServiceException {
        /***包装VO信息************************/
        List<ProcessInstanceVo> pivos = new ArrayList<ProcessInstanceVo>();
        //4.11~4.28数据丢失，查询4.11~4.28之间的已处理任务列表，从patch表中查找
        List<ProcessInstanceVo> pivosFor411List = new ArrayList<ProcessInstanceVo>(); 
        try {
        	//modify by liushengbin 2014-05-26 先开始查询4.11~4.28之间的已处理任务列表
        	pivosFor411List  = patchMainService.getDealedProcessInstanceVosFor411(conditionMap);
        	
            // 查询条件，公司名称
            String companyId = (String) conditionMap.get("companyId");            
            // 查询条件组装
            Map<String, Object> filterMap = this.getDealedFilterMap(conditionMap);
            String filter = (String) filterMap.get("filter");
            Object[] substitutionVars = (Object[]) filterMap.get("substitutionVars");
            List<VWLogElement> les = peManager.vwEventLogWob(filter, substitutionVars);

            // 8.27 huhan add 得到活动流程
            Map<String, Object> filterMap1 = this.getActiveWobs(conditionMap);
            String filter1 = (String) filterMap1.get("filter");
            Object[] substitutionVars1 = (Object[]) filterMap1.get("substitutionVars");
            List<VWWorkObject> wobs = peManager.vwGetTmsWorkObjects(filter1, substitutionVars1);

            for (VWLogElement le : les) {
            	 ProcessInstanceVo vo = new ProcessInstanceVo();
            	 vo.setProcInstId(le.getWorkFlowNumber());
            	 
            	//modify by liushengbin 2014-05-26去除重复逻辑：
             	//通过PE API查找其他已处理任务的循环中，如果遇到4.11~4.28之间的已处理任务的流程实例号，就跳过（即continue）;
             	//因为4.11~4.28之间的已处理任务列表中的流程流转，已经是流程实例完整的流转记录
            	 if(pivosFor411List.contains(vo)){
            		 continue;
            	 }           	 
            	 
                // 得到流程Code
                // String processCode = ProcessDefineUtil.PROCESS_CLASS_CODE_MAP.get(le.getWorkClassName());
                String processCode = ProcessXmlUtil.getProcessAttribute("className", le.getWorkClassName(), "code");
                if (processCode == null || "".equals(processCode)) {
                    continue;
                }
                // 得到流程定义obj
                ProcessDefine pd = processDefineService.getProcDefineByCode(processCode);
                if (pd == null) {
                    continue;
                }
                // 费过滤器条件检查
                if (this.checkNoFilter(le, conditionMap, pd)) {
                    // 打印相关信息
                    // peManager.printWob(le);

                   
                   
                    /***9.10 modify for(申请公司和TMS流程实例编号)**/
                    String pidFn = le.getWorkFlowNumber();
                    ProcessMap pm = processUtilMapService.getProcessMapByFnId(pidFn);
                    //add by liushengbin 2012.10.8 剔除 公司查询条件不匹配的数据
                    String tempCompanyId = String.valueOf(pm.getCompanyId());
                    if (!StringUtils.isBlankOrNull(companyId) && !StringUtils.isBlankOrNull(tempCompanyId) && !companyId.equals(tempCompanyId)) {
                        continue;
                    }
                    if (pm.getPidTms() == null || "".equals(pm.getPidTms())) {
                        vo.setPidTms(pidFn);
                    } else {
                        vo.setPidTms(pm.getPidTms());
                    }

                    vo.setCompanyName(pm.getCompanyName() == null ? "" : pm.getCompanyName());
                    /***9.10 modify end**/

                    vo.setProcessCode(processCode);
                    vo.setProcessName(pd.getProcessName());
                    vo.setProcessVersion(peManager.vwFindProcessVersion(ProcessXmlUtil.getProcessAttribute("className",
                            le.getWorkflowName(), "cePath")));
                    vo.setDescrible(pd.getProcessMemo());
                    // vo.setSubmitDate((Date)le.getDataField("F_StartTime").getValue());
                    vo.setStepDate(le.getTimeStamp()); 
               
                    vo.setNodeName((le.getEventType() == ProcessDefineUtil.PROCESS_EVENTTYPE_MAP
                            .get(ProcessDefineUtil.EventTypeEnum.ForcedToTerminate)) ? "终止流程" : le.getStepName());
                    // vo.setNodeExer(nodeExer);

                    // (银行授信或集团授信)特殊处理，看step是否允许查看确认详细
                    String confirmView = ProcessXmlUtil.findStepProperty("className", le.getWorkflowName(), le.getStepName(),
                            "confirmView");
                    if (confirmView != null && !"".equals(confirmView)) {
                        if ("true".equals(confirmView)) {
                            vo.setUserComfirmView(true);
                        }
                    }

                    // 8.27 huhan add 当前流程实例所处任务/当前处理人/流程状态
                    String wobNum = le.getWorkFlowNumber();
                    boolean active = false;
                    for (VWWorkObject wob : wobs) {
                        // 如果是活动流程
                        if (wobNum != null && wobNum.equals(wob.getWorkflowNumber())) {
                            active = true;
                            vo.setThisStepName(wob.getStepName());
                            String nodeExer = wob.getLockedUser();
                            vo.setThisStepExer(nodeExer);
                            vo.setThisStatus("1");
                        }
                    }
                    if (!active) {
                        vo.setThisStatus("2");
                    }
                    if ("1".equals(vo.getThisStatus())) {
                        if (conditionMap.get("exeStatus") == null || "1".equals(conditionMap.get("exeStatus"))) {
                            pivos.add(vo);
                        }
                    } else {
                        if ("2".equals(conditionMap.get("exeStatus"))) {
                            pivos.add(vo);
                        }
                    }

                }

            }
        } catch (Exception e) {
            log.error("getProcessInstanceVos方法 获取已处理任务 出现异常：",e);
            throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
        }
        
        //把4.11~4.28之间的处理任务追加到总的结果集中
        pivos.addAll(pivosFor411List);
        
        // 按任务时间排倒叙
        pivos = orderByStepDate(pivos);

        return pivos;
    }

    /**
     * 流程显示排序(按任务处理时间排倒叙)
     * @param pivos
     * @return
     */
    private List<ProcessInstanceVo> orderByStepDate(List<ProcessInstanceVo> pivos) {
        Collections.sort(pivos, new Comparator<ProcessInstanceVo>() {
            @Override
            public int compare(ProcessInstanceVo o1, ProcessInstanceVo o2) {
                return o2.getStepDate().compareTo(o1.getStepDate());
            }
        });
        return pivos;
    }

    /**
     * 得到已处理任务的过滤器
     * @param conditionMap
     * @return
     */
    private Map<String, Object> getDealedFilterMap(Map<String, Object> conditionMap) throws Exception {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        StringBuilder filter = new StringBuilder("1=1 and (F_EventType = :EventType or F_EventType = :EventTypeT)");
        List<Object> varsList = new ArrayList<Object>();
        varsList.add(ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd));
        varsList.add(ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ForcedToTerminate));
        // 登录人
        // //1.步骤执行人
        filter.append(" and F_UserId = :userId");
        varsList.add(peManager.vwGetIdByUserName(loginService.getCurrentUserName()));

        // 2.流程实例编号
        if (conditionMap.get("procInstId") != null && !"".equals(conditionMap.get("procInstId"))) {
            filter.append(" and F_WobNum = :wobNum");
            //modified on 2012-11-8
            String fnId = processUtilMapService.getFnIdByTmsId((String) conditionMap.get("procInstId"));
            varsList.add(new VWWorkObjectNumber(fnId));

        }
        // 3.处理时间
        // 开始时间
        if (null != conditionMap.get("startDate")) {

            filter.append(" and F_TimeStamp >= :startDate");
            varsList.add(conditionMap.get("startDate"));
        }
        // 结束时间
        if (null != conditionMap.get("endDate")) {
            filter.append(" and F_TimeStamp <= :endDate");
            varsList.add(DateUtil.addOneDay((Date) conditionMap.get("endDate")));
        }

        Object[] substitutionVars = varsList.toArray();
        filterMap.put("filter", filter.toString());
        filterMap.put("substitutionVars", substitutionVars);

        return filterMap;
    }

    /**
     * 得到活动流程的过滤器
     * @param conditionMap
     * @return
     */
    private Map<String, Object> getActiveWobs(Map<String, Object> conditionMap) throws Exception {
        Map<String, Object> filterMap = new HashMap<String, Object>();
        StringBuilder filter = new StringBuilder("1=1");
        List<Object> varsList = new ArrayList<Object>();

        // 2.流程实例编号检查
        if (conditionMap.get("procInstId") != null && !"".equals(conditionMap.get("procInstId"))) {
            filter.append(" and F_WobNum = :wobNum");
          //modified on 2012-11-8
            String fnId = processUtilMapService.getFnIdByTmsId((String) conditionMap.get("procInstId"));
            varsList.add(new VWWorkObjectNumber(fnId));

        }
        // 3.提交时间
        // 开始时间
        if (null != conditionMap.get("startDate")) {

            filter.append(" and F_StartTime >= :startDate");
            varsList.add(conditionMap.get("startDate"));
        }
        // 结束时间
        if (null != conditionMap.get("endDate")) {
            filter.append(" and F_StartTime <= :endDate");
            varsList.add(DateUtil.addOneDay((Date) conditionMap.get("endDate")));
        }

        Object[] substitutionVars = varsList.toArray();
        filterMap.put("filter", filter.toString());
        filterMap.put("substitutionVars", substitutionVars);

        return filterMap;
    }

    /**
     * 检查流程类型
     * @param conditionMap
     * @param pd
     * @return
     */
    private boolean checkNoFilter(VWLogElement le, Map<String, Object> conditionMap, ProcessDefine pd) throws Exception {
        // 2.步骤名称检查
        // if(stepName.equals("申请")){
        // return false;
        // }

        // 4.流程名称检查
        if (conditionMap.get("processId") != null && !"0".equals(conditionMap.get("processId"))) {
            if (!pd.getId().toString().equals(conditionMap.get("processId"))) { return false; }
        }
        return true;
    }

    /**
     * 查询条件检查
     * @param wob
     * @param conditionMap
     * @return
     * @throws Exception
     */
    @Deprecated
    private boolean conditionCheck(VWLogElement le, Map<String, Object> conditionMap, ProcessDefine pd) throws Exception {
        boolean chackFlag = true;
        // 登录人

        // 得到步骤执行人
        String stepExer = le.getUserName() == null ? "" : le.getUserName();
        String stepName = le.getStepName() == null ? "" : le.getStepName();
        // List<String> paras = ProcessXmlUtil.getParasByProcessStep(le.getWorkClassName(), stepName);
        // System.out.println("步骤:"+stepName);
        // for(String p :paras){
        // if(p.contains("_Exer")){
        // System.out.println("字段:"+p);
        // VWDataField df = le.getDataField(p);
        // stepExer = df.getValue().toString();
        // break;
        // }
        // }

        // 1.步骤执行人检查
        if (!stepExer.equals(loginService.getCurrentUserName())) {
            chackFlag = false;
        }

        // 2.步骤名称检查
        if (stepName.equals("申请")) {
            chackFlag = false;
        }

        // 3.流程实例编号检查
        if (conditionMap.get("procInstId") != null && !"".equals(conditionMap.get("procInstId"))) {
            String wfNumber = le.getWorkFlowNumber();
            if (!wfNumber.equals(conditionMap.get("procInstId"))) {
                chackFlag = false;
            }
        }

        // 4.处理时间检查
        Date startDate = le.getTimeStamp();
        // 开始时间
        if (null != conditionMap.get("startDate")) {
            Date conStartDate = (Date) conditionMap.get("startDate");
            // 小于参数日期 就 小于0
            if (startDate.compareTo(conStartDate) < 0) {
                chackFlag = false;
            }
        }
        // 结束时间
        if (null != conditionMap.get("endDate")) {
            Date conEndDate = DateUtil.addOneDay((Date) conditionMap.get("endDate"));
            // 大于参数日期 就 大于0
            if (startDate.compareTo(conEndDate) > 0) {
                chackFlag = false;
            }
        }

        // 5.流程名称检查
        if (conditionMap.get("processId") != null && !"0".equals(conditionMap.get("processId"))) {
            if (!pd.getId().toString().equals(conditionMap.get("processId"))) {
                chackFlag = false;
            }
        }

        return chackFlag;
    }
}
