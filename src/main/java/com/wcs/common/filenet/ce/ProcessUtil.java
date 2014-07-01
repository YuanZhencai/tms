package com.wcs.common.filenet.ce;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;
import com.wcs.base.util.DateUtil;
import com.wcs.common.filenet.env.Env;
import com.wcs.common.filenet.env.EnvInit;
import com.wcs.common.filenet.env.SysCfg;
import com.wcs.common.filenet.pe.RosterHelper;
import com.wcs.common.filenet.pe.SessionHelper;
import com.wcs.common.filenet.pe.WorkflowHelper;
import com.wcs.common.filenet.pe.queue.QueueHelper;

import filenet.vw.api.VWAttachment;
import filenet.vw.api.VWDataField;
import filenet.vw.api.VWException;
import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWFieldType;
import filenet.vw.api.VWLog;
import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWLogQuery;
import filenet.vw.api.VWLoggingOptionType;
import filenet.vw.api.VWMapDefinition;
import filenet.vw.api.VWParameter;
import filenet.vw.api.VWProcess;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueQuery;
import filenet.vw.api.VWRoster;
import filenet.vw.api.VWRosterElement;
import filenet.vw.api.VWRosterQuery;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWStepElement;
import filenet.vw.api.VWStepHistory;
import filenet.vw.api.VWStepProcessorInfo;
import filenet.vw.api.VWWorkObject;
import filenet.vw.api.VWWorkObjectNumber;
import filenet.vw.api.VWWorkflowDefinition;
import filenet.vw.api.VWWorkflowHistory;

public class ProcessUtil {
    private String WorkflowNumber = "";
    private VWSession vs = null;
    private Connection ceConn = null;

    private Log logger = LogFactory.getLog(ProcessUtil.class);
    
    public ProcessUtil() {
        EnvInit env = new EnvInit();
        env.setJVMParameters();
        ceConn = initCE();
        vs = initPE();
    }

    private Connection initCE() {
        String ceUrl = SysCfg.getStrConfig("CE.CEURL");
        Connection ceConn1 = Factory.Connection.getConnection(ceUrl);
        String ceAdminUser = SysCfg.getStrConfig("CE.filenetCeAdminUserName");
        String ceAdminPassword = SysCfg.getStrConfig("CE.filenetCeAdminUserPasswd");
        logger.info(ceAdminUser);
        logger.info(ceAdminPassword);
        Subject subject = UserContext.createSubject(ceConn1, ceAdminUser, ceAdminPassword, "FileNetP8");
        UserContext uc = UserContext.get();
        uc.pushSubject(subject);
        return ceConn1;
    }

    private VWSession initPE() {
        String peAdminUser = SysCfg.getStrConfig("PE.FilenetPeAdminUserName");
        String peAdminPassword = SysCfg.getStrConfig("PE.FilenetPeAdminUserPasswd");
        return SessionHelper.logon(peAdminUser, peAdminPassword);
    }

    /**
     * 
     * <p>Description: 创建工作流程实例</p>
     * @param workName
     * @param vs
     * @return
     * @throws VWException
     */
    public VWStepElement createWorkFlow(String workName) {
        try {
            VWStepElement launchStep = WorkflowHelper.createWorkflow(workName, vs);
            launchStep.doDispatch();
            WorkflowNumber = launchStep.getWorkflowNumber();
            logger.info(WorkflowNumber);
            logger.info(launchStep.getWorkObjectNumber());
            return launchStep;
        } catch (VWException e) {
            logger.error("createWorkFlow方法 创建工作流程实例出现异常", e);
        }
        return null;
    }

    /**
     * 
     * <p>Description: 员工提交报销申请</p>
     * @param queueName
     * @param workNum
     */
    public void employeeSubmit(String queueName, String workNum, String param, boolean paramValue, String comment) {
        try {
            VWStepElement step = QueueHelper.getStepElementByWorkObjectNumber(vs.getQueue(queueName), new VWWorkObjectNumber(
                    workNum), VWQueue.QUERY_NO_OPTIONS);
            step.setComment("员工报销申请测试");

            logger.info("节点元素名称---" + step.getStepName());
            logger.info("WorkWorkflowNumber---" + step.getWorkflowNumber());
            // 字段类型 ，用户自定义字段
            VWParameter[] ps = step.getParameters(VWFieldType.ALL_FIELD_TYPES, VWStepElement.FIELD_USER_DEFINED);
            logger.info("参数 -------");
            QueueHelper.lockStepElement(vs, step);
            step.setComment(comment);
            step.setParameterValue(param, paramValue, true);
            for (VWParameter vp : ps) {
                if (vp.getName().contains("_Exer")) {
                    String name = vp.getName();
                    step.setParameterValue(name, "shenbo", true);
                }
            }
            step.doDispatch();
        } catch (VWException e) {
            logger.error("employeeSubmit方法 员工提交报销申请出现异常", e);
        }
    }

    public void vwStepExcution(String workNum, String param, boolean paramValue, int flag, String commonet) {
        VWWorkObject vobj;
        try {

            vobj = RosterHelper.vwfetchWorkObject(Env.getVWSession(), workNum, 0);
            // 得到当前任务节点
            VWStepElement step = vobj.fetchStepElement();
            step.doLock(true);
            step.setComment(commonet);
            if (flag == 1) {
                step.setParameterValue(param, paramValue, true);
            }
            step.doDispatch();
        } catch (VWException e) {
           logger.error("vwStepExcution方法 执行任务节点出现异常", e);
        }

    }
    
    public void vwStepExcution(String workNum,String commonet){
        try {
            VWWorkObject vobj = RosterHelper.vwfetchWorkObject(vs, workNum, 0);
            VWStepElement step = vobj.fetchStepElement();
            step.doLock(true);
            step.setComment(commonet);
            step.doDispatch();
        } catch (VWException e) {
        	logger.error("vwStepExcution方法 执行任务节点出现异常", e);
        }
    }

    public ObjectStore getOS() {
        String ceUsername = SysCfg.getStrConfig("CE.filenetCeAdminUserName");
        String password = SysCfg.getStrConfig("CE.filenetCeAdminUserPasswd");
        String ceUrl = SysCfg.getStrConfig("CE.CEURL").replace("\\", "");
        String objectStore = SysCfg.getStrConfig("CE.ObjectStore");
        CEConnection ce = new CEConnection();
        ce.establishConnection(ceUsername, password, "FileNetP8", ceUrl);
        return ce.fetchOS(objectStore);
    }

    public void findWorkClassNames() {
        try {
            String[] workClassNames = vs.fetchWorkClassNames(true);
            for (String className : workClassNames) {
                logger.info(className + ",");
            }
        } catch (VWException e) {
            logger.error("findWorkClassNames方法 查找所有工作流程名称出现异常", e);
        }
    }

    public void findVwStepList(String queueName, String filter, Object[] substitutionVars) {
        try {
            VWQueue queue = vs.getQueue(queueName);
            queue.setBufferSize(1);
            VWQueueQuery qQuery = queue.createQuery(null, null, null, VWQueue.QUERY_NO_OPTIONS, filter, substitutionVars,
                    VWFetchType.FETCH_TYPE_STEP_ELEMENT);
            while (qQuery.hasNext()) {
                VWStepElement st = (VWStepElement) qQuery.next();
                logger.info(st.getStepName());
            }
        } catch (VWException e) {
        	logger.error("findVwStepList方法 查找所有队列内的工作项出现异常", e);
        }
    }

    public void findVwStepList(String queueName) {
        try {
            VWQueue queue = vs.getQueue(queueName);
            int queryFlag = VWQueue.QUERY_READ_LOCKED;
            int queryType = VWFetchType.FETCH_TYPE_STEP_ELEMENT;
            VWQueueQuery qQuery = queue.createQuery(null, null, null, queryFlag, null, null, queryType);
            while (qQuery.hasNext()) {
                VWStepElement st = (VWStepElement) qQuery.next();
                logger.info("我的手件箱的节点步奏元素----" + st.getStepName() + " | --");
            }
        } catch (VWException e) {
        	logger.error("findVwStepList方法出现异常", e);
        }
    }

    /**
     * 
     * <p>Description: </p>
     */
    public void finvwRoster() {
        try {
            // 得到默认的花名册
            VWRoster vwRoster = vs.getRoster("DefaultRoster");
            vwRoster.setBufferSize(25);
            VWRosterQuery rosterQuery = vwRoster.createQuery(null, null, null, 0, null, null,
                    VWFetchType.FETCH_TYPE_ROSTER_ELEMENT);
            while (rosterQuery.hasNext()) {
                VWRosterElement rosterElement = (VWRosterElement) rosterQuery.next();
                logger.info("花名册-----" + rosterElement.getStepName());
            }
            if (rosterQuery.hasNext()) {
                VWRosterElement rosterElement = (VWRosterElement) rosterQuery.next();
                VWWorkObject vwworkObject = rosterElement.fetchWorkObject(false, false);
                logger.info(vwworkObject.getWorkflowName());
            }

        } catch (VWException e) {
        	logger.error("finvwRoster方法 得到默认的花名册出现异常", e);
        }
    }

    public void findQueueNameList() {
        String[] queueName;
        try {
            queueName = vs.fetchQueueNames(VWSession.QUEUE_PROCESS);
            if (queueName.length > 0) {
                for (String str : queueName) {
                    logger.info("queueName -- " + str + ",");
                }
            }

        } catch (VWException e) {
        	logger.error("findQueueNameList方法 得到所有队列名称出现异常", e);
        }

    }

    public void findProcessStepInfoList() {
        VWStepProcessorInfo[] processInfo;
        try {
            processInfo = vs.fetchStepProcessors(VWStepProcessorInfo.PROCESSOR_STEP);
            if (processInfo.length > 0) {
                for (VWStepProcessorInfo info : processInfo) {
                    logger.info("流程节点Id --- " + info.getId());
                }
            }
        } catch (VWException e) {
        	logger.error("findProcessStepInfoList方法出现异常", e);
        }

    }

    public VWWorkObject findWorkObject(String flowNumber) {
        String filter = "F_WobNum = :WobNum";
        Object[] substitutionVars = { new VWWorkObjectNumber(flowNumber) };
        VWRoster vwRoster;
        try {
            vwRoster = vs.getRoster("DefaultRoster");
            VWRosterQuery rQuery = vwRoster.createQuery(null, null, null, VWRoster.QUERY_NO_OPTIONS, filter, substitutionVars,
                    VWFetchType.FETCH_TYPE_WORKOBJECT);
            if (rQuery.hasNext()) {
                return (VWWorkObject) rQuery.next();
            }
        } catch (VWException e) {
        	logger.error("findWorkObject方法出现异常", e);
        }

        return null;
    }

    public void testRetrieveWorkflowInstance(String flowNumber) throws VWException {
        String filter = "F_WobNum = :WobNum";
        Object[] substitutionVars = { new VWWorkObjectNumber(flowNumber) };
        VWRoster vwRoster = vs.getRoster("DefaultRoster");
        VWRosterQuery rQuery = vwRoster.createQuery(null, null, null, VWRoster.QUERY_NO_OPTIONS, filter, substitutionVars,
                VWFetchType.FETCH_TYPE_WORKOBJECT);
        while (rQuery.hasNext()) {
            VWWorkObject workObject = (VWWorkObject) rQuery.next();
            printFlowInstance(workObject);
        }
    }

    private void printFlowInstance(VWWorkObject flowInstance) throws VWException {
        logger.info("\t*******************");
        logger.info("\tWork Class Name: " + flowInstance.getWorkClassName());
        logger.info("\tWorkflow Number: " + flowInstance.getWorkflowNumber());
        logger.info("\tSubject: " + flowInstance.getSubject());
        logger.info("\tLaunch Date: " + flowInstance.getLaunchDate());
        logger.info("\tAll Data Fields: ");
        VWDataField[] dataFields = flowInstance.getDataFields(VWWorkObject.FIELD_USER_DEFINED,
                VWWorkObject.FIELD_USER_AND_SYSTEM_DEFINED);
        printDataFields(dataFields);
    }

    private void printDataFields(VWDataField[] dataFields) throws VWException {
        for (int i = 0; i < dataFields.length; i++) {
            logger.info(dataFields[i].getName() + "=" + dataFields[i].getValue() + ", ");
        }
        logger.info("\n");
    }

    // ----------------------------新Demo关于TMS实际业务相关-----------------------------------//

    public void findWokInstanceByQueue(String queueName) {
        try {
            VWQueue queue = vs.getQueue(queueName);
            int queryFlag = VWQueue.QUERY_READ_LOCKED;
            int queryType = VWFetchType.FETCH_TYPE_WORKOBJECT;
            VWQueueQuery qQuery = queue.createQuery(null, null, null, queryFlag, null, null, queryType);
            while (qQuery.hasNext()) {
                VWWorkObject vobj = (VWWorkObject) qQuery.next();
                logger.info("节点元素名称   " + vobj.getStepName() + "    工作实例编号  " + vobj.getWorkflowNumber());
                logger.info(vobj.getWorkflowName());
                logger.info(DateUtil.dateToStrShort(DateUtil.dateToDateTime(vobj.getDateReceived())));

                VWStepElement step = vobj.fetchStepElement();
                VWParameter[] pa = step.getParameters(VWFieldType.FIELD_TYPE_STRING, VWStepElement.FIELD_USER_DEFINED);
                step.doLock(true);
                if (pa != null) {
                    for (VWParameter vwp : pa) {
                        if (vwp.getName().contains("_Exer")) {
                            step.setParameterValue(vwp.getName(), "shenbo", true);
                        }
                        step.doSave(false);
                        logger.info("参数名字-----" + vwp.getName() + "-------参数值--" + vwp.getValue());
                    }
                }
            }

        } catch (VWException e) {
            logger.error("findWokInstanceByQueue方法出现异常", e);
        }
    }

    public VWWorkflowHistory vwFindWorkflowHistory(String workObjNum, int queryflag) {
        try {
            // 得到工作对象
            VWWorkObject workObject = RosterHelper.vwfetchWorkObject(vs, workObjNum, queryflag);
            // 得到流程对象
            VWProcess vwProcess = workObject.fetchProcess();
            VWWorkflowDefinition vwdefine = vwProcess.fetchWorkflowDefinition(false);
            VWMapDefinition[] vwdefineMap = vwdefine.getMaps();
            for (VWMapDefinition d : vwdefineMap) {
                logger.info("mapId == " + d.getMapId());
                VWWorkflowHistory vwhistory = vwProcess.fetchFilteredWorkflowHistory(d.getMapId(),
                        VWProcess.FILTER_COMPLETED_STEP_HISTORY);
                while (vwhistory.hasNext()) {
                    VWStepHistory vwstep = vwhistory.next();
                    logger.info("节点名称-----" + vwstep.getStepName());
                }
            }

        } catch (VWException e) {
        	logger.error("vwFindWorkflowHistory方法出现异常", e);
        }
        return null;
    }

    /**
     * 
     * <p>Description: 测试附件</p>
     * @param queueName
     */
    public void testAttachment(String queueName, String workObjNum, String paramName) {
        try {
            VWStepElement step = QueueHelper.getStepElementByWorkObjectNumber(vs.getQueue(queueName), new VWWorkObjectNumber(
                    workObjNum), VWQueue.QUERY_NO_OPTIONS);
            String[] params = step.getParameterNames();
            for (String str : params) {
                logger.info("节点参数名字  " + str + " || ");
            }
            VWAttachment[] vwattach = (VWAttachment[]) step.getParameterValue(paramName);
            logger.info("附件大小--" + vwattach.length);
            for (VWAttachment vw : vwattach) {
                logger.info("附件版本---" + vw.getVersion());
                logger.info("附件ID----" + vw.getId());
            }

        } catch (VWException e) {
        	logger.error("testAttachment方法出现异常", e);
        }
    }

    public ObjectStore getObjectStore(String objStore) {
        Domain domain = Factory.Domain.fetchInstance(ceConn, SysCfg.getStrConfig("CE.domain"), null);
        return Factory.ObjectStore.fetchInstance(domain, objStore, null);
    }

    public void testEndProcess() {
    	
    }

    /**
     * 日志查询Wob
     * @throws Exception
     */
    public List<VWLogElement> vwEventLogWob(String filter, Object[] substitutionVars) throws Exception {
        VWLog log = vs.fetchEventLog("DefaultEventLog");
        List<VWLogElement> les = new ArrayList<VWLogElement>();
        VWLogQuery lq = log.startQuery(null, null, null, VWLog.QUERY_NO_OPTIONS, filter, substitutionVars);
        // 得到系统所有流程className
        while (lq.hasNext()) {
            VWLogElement le = lq.next();

            logger.info("" + lq.fetchCount());
            logger.info("wobNum:" + le.getWorkFlowNumber());
            logger.info("stepName:" + le.getStepName());
            logger.info("sequence:" + le.getSequenceNumber());
            logger.info("eventType:" + le.getEventType());
            logger.info("eventTypeName:" + VWLoggingOptionType.getLocalizedString(le.getEventType()));
            logger.info("className:" + le.getWorkClassName());
            logger.info("userName:" + le.getUserName());
            logger.info("Parameters:");
            String[] paras = le.getFieldNames();
            for (String p : paras) {
                logger.info(p + "|");
            }
            logger.info("");
            les.add(le);

        }
        return les;
    }
}
