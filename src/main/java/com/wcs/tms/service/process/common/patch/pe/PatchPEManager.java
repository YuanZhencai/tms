package com.wcs.tms.service.process.common.patch.pe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Document;
import com.filenet.api.core.ObjectStore;
import com.google.common.collect.Lists;
import com.wcs.common.filenet.ce.CEConnection;
import com.wcs.common.filenet.ce.CEUtil;
import com.wcs.common.filenet.pe.RosterHelper;
import com.wcs.common.filenet.pe.WorkflowHelper;
import com.wcs.common.filenet.pe.queue.QueueHelper;
import com.wcs.tms.conf.xml.ProcessXmlUtil;

import filenet.vw.api.VWAttachment;
import filenet.vw.api.VWAttachmentType;
import filenet.vw.api.VWDataField;
import filenet.vw.api.VWException;
import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWFieldType;
import filenet.vw.api.VWLibraryType;
import filenet.vw.api.VWLog;
import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWLogQuery;
import filenet.vw.api.VWLoggingOptionType;
import filenet.vw.api.VWModeType;
import filenet.vw.api.VWParameter;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueElement;
import filenet.vw.api.VWQueueQuery;
import filenet.vw.api.VWRoster;
import filenet.vw.api.VWServerException;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWStepElement;
import filenet.vw.api.VWWorkObject;
import filenet.vw.api.VWWorkObjectNumber;

public class PatchPEManager {

	private Log logger = LogFactory.getLog(PatchPEManager.class);

	public void initPE() {

	}

	private Map<String, Integer> pressVserionMap = new ConcurrentHashMap<String, Integer>();

	/**
	 * 检查流程名称
	 * 
	 * @param workClassName
	 * @return
	 */
	public boolean checkWorkClassName(String workClassName) {
		String[] workClassNames = this.getWorkClassNames();
		for (String peClass : workClassNames) {
			if (peClass.equals(workClassName)) {
				logger.info("流程类名验证通过!~");
				return true;
			}
		}
		return false;
	}

	/**
	 * 得到流程名称
	 */
	public String[] getWorkClassNames() {
		logger.info("PEManager.getWorkClassNames()~~~~~~~~~~~~~~~~~~~~");
		String[] workClassNames = null;
		VWSession vwSession = PatchEnv.getVWSession();
		try {
			logger.info("登录之后的用户名"
					+ PatchEnv.getVWSession().fetchCurrentUserInfo().getName());
			workClassNames = vwSession.fetchWorkClassNames(true);
		} catch (VWException e) {
			logger.error("getWorkClassNames方法 得到流程名称出现异常", e);
		}

		for (String className : workClassNames) {
			logger.info(className + "|");
		}
		return workClassNames;
	}

	private String workflowNumber1 = "5357407912A4574BAA39A160BD473560";

	/**
	 * 创建流程实例
	 * 
	 * @throws Exception
	 */
	public String vwCreateInstance(String workFlowName, String subjectName)
			throws Exception {
		logger.info("创建流程实例" + subjectName
				+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~`");
		String workflowNumber = "";
		try {
			logger.info("PE登录人:"
					+ PatchEnv.getVWSession().fetchCurrentUserInfo().getName());
			logger.info("PE登录人:"
					+ PatchEnv.getVWSession().fetchCurrentUserInfo().getName());
			VWStepElement vwStepElement = WorkflowHelper.createWorkflow(
					workFlowName, PatchEnv.getVWSession());
			vwStepElement.setComment("申请单1");
			VWParameter[] ps = vwStepElement.getParameters(
					VWFieldType.ALL_FIELD_TYPES,
					VWStepElement.FIELD_USER_AND_SYSTEM_DEFINED);
			logger.info("参数：");
			if (ps != null) {
				for (VWParameter p : ps) {
					logger.info(p.getName() + "|");
				}
			}
			logger.info("");
			vwStepElement.setParameterValue("F_Subject", subjectName, true);
			vwStepElement.doDispatch();
			logger.info("stepName:" + vwStepElement.getStepName());
			logger.info("WorkObjectNumber:"
					+ vwStepElement.getWorkObjectNumber());
			logger.info("WorkflowNumber:" + vwStepElement.getWorkflowNumber());
			workflowNumber = vwStepElement.getWorkflowNumber();
		} catch (VWException e) {
			logger.error("vwCreateInstance方法 创建流程实例出现异常", e);
			throw new Exception(e);
		}
		return workflowNumber;
	}

	/**
	 * 日志查询Wob
	 * 
	 * @throws Exception
	 */
	public List<VWLogElement> vwEventLogWob(String filter,
			Object[] substitutionVars) throws Exception {
		long startTime =System.currentTimeMillis();
		System.out.println("vwEventLogWob startTime:"+startTime);
		VWLog log = PatchEnv.getVWSession().fetchEventLog("DefaultEventLog");
		List<VWLogElement> les = new ArrayList<VWLogElement>();
		VWLogQuery lq = log.startQuery(null, null, null,
				VWLog.QUERY_NO_OPTIONS, filter, substitutionVars);
		// 得到系统所有流程className
		try {
			//logger.info("log数量:" + lq.fetchCount());
		} catch (Exception e) {
			if (e instanceof VWServerException) {
				return les;
			}
		}
		while (lq.hasNext()) {
			VWLogElement le = lq.next();
			// 是否为TMS定义流程
//			logger.info("" + lq.fetchCount());
//			logger.info("wobNum:" + le.getWorkFlowNumber());
//			logger.info("stepName:" + le.getStepName());
//			logger.info("sequence:" + le.getSequenceNumber());
//			logger.info("eventType:" + le.getEventType());
//			logger.info("eventTypeName:"
//					+ VWLoggingOptionType.getLocalizedString(le.getEventType()));
//			logger.info("className:" + le.getWorkClassName());
//			logger.info("TimeStamp:" + le.getTimeStamp());
//			logger.info("commont:" + le.getFieldValue("F_Comment"));
//			logger.info("userName:" + le.getUserName());
//			logger.info("creater:"
//					+ this.vwGetUserNameById((Integer) le
//							.getFieldValue("F_Originator")));
//			logger.info("pass:");
//			for (String p : le.getAuthoredFieldNames()) {
//				logger.info(p + "|");
//			}
//			logger.info("");
//			logger.info("Parameters:");
//			String[] paras = le.getFieldNames();
//			for (String p : paras) {
//				logger.info(p + "|");
//			}
//			logger.info("");
			les.add(le);
		}
		long endTime = java.lang.System.currentTimeMillis();
		String costTime = String.valueOf((endTime
				- startTime + 0.0) / 1000);
		
		System.out.println("vwEventLogWob costTime:"+(costTime) +"秒");
		return les;
	}

	/**
	 * 执行流程步骤
	 * 
	 * @param workQueueName
	 * @param paras
	 * @param workflowNumber
	 * @throws Exception
	 */
	public void vwLauchStep(String workQueueName,
			HashMap<String, Object> paras, String workflowNumber)
			throws Exception {
		List woj = QueueHelper.getWorkElements(PatchEnv.getVWSession(),
				workQueueName, null, VWFetchType.FETCH_TYPE_WORKOBJECT);
		logger.info(workQueueName + "工作实例对象数:" + woj.size());
		try {
			VWStepElement se = QueueHelper.getStepElementByWorkObjectNumber(
					PatchEnv.getVWSession().getQueue(workQueueName),
					new VWWorkObjectNumber(workflowNumber),
					VWQueue.QUERY_NO_OPTIONS);
			logger.info("流程节点" + se.getStepName()
					+ "执行~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~`");
			logger.info("WorkObjectNumber:" + se.getWorkObjectNumber());
			logger.info("WorkflowNumber:" + se.getWorkflowNumber());
			VWParameter[] ps = se.getParameters(VWFieldType.ALL_FIELD_TYPES,
					VWStepElement.FIELD_USER_DEFINED);
			logger.info("参数：");
			if (ps != null) {
				for (VWParameter p : ps) {
					logger.info(p.getName() + " "
							+ VWModeType.getLocalizedString(p.getMode()) + "|");
				}
			}
			logger.info("");
			QueueHelper.lockStepElement(PatchEnv.getVWSession(), se);
			for (Map.Entry<String, Object> en : paras.entrySet()) {
				se.setParameterValue(en.getKey(), en.getValue(), true);
			}
			se.doDispatch();
		} catch (VWException e) {
			logger.error("vwLauchStep方法 执行流程步骤出现异常", e);
			throw new Exception(e);
		}
	}

	/**
	 * 得到TMS进行中的流程实例
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<VWWorkObject> vwGetTmsWorkObjects() throws Exception {
		List<VWWorkObject> allWobs = RosterHelper.getWorkObjects(PatchEnv
				.getVWSession());
		List<VWWorkObject> tmsWobs = new ArrayList<VWWorkObject>();
		// 得到系统所有流程className
		List<String> allClass = ProcessXmlUtil
				.getAllProcessAttribut("className");
		try {
			for (VWWorkObject wob : allWobs) {
				// 是否为TMS定义流程
				if (allClass.contains(wob.getWorkClassName())) {
					tmsWobs.add(wob);
				}
			}
		} catch (VWException e) {
			logger.error("vwGetTmsWorkObjects方法 得到TMS进行中的流程实例出现异常", e);
			throw new Exception(e);
		}
		return tmsWobs;
	}

	/**
	 * 得到TMS进行中的流程实例 (带过滤器)
	 * 
	 * @param filter
	 * @param substitutionVars
	 * @return
	 * @throws Exception
	 */
	public List<VWWorkObject> vwGetTmsWorkObjects(String filter,
			Object[] substitutionVars) throws Exception {
		List<VWWorkObject> allWobs = RosterHelper.getWorkObjects(
				PatchEnv.getVWSession(), filter, substitutionVars);
		List<VWWorkObject> tmsWobs = new ArrayList<VWWorkObject>();
		// 得到系统所有流程className
		List<String> allClass = ProcessXmlUtil
				.getAllProcessAttribut("className");
		try {
			for (VWWorkObject wob : allWobs) {
				// 是否为TMS定义流程
				if (allClass.contains(wob.getWorkClassName())) {
					tmsWobs.add(wob);
				}
			}
		} catch (VWException e) {
			logger.error("vwGetTmsWorkObjects方法 得到TMS进行中的流程实例 (带过滤器)出现异常", e);
			throw new Exception(e);
		}
		return tmsWobs;
	}

	/**
	 * 流程实例id得到流程对象
	 * 
	 * @param workflowNumber
	 * @throws Exception
	 */
	public VWWorkObject vwGetTmsWorkObject(String workflowNumber)
			throws Exception {
		try {
			VWWorkObject wob = RosterHelper.vwfetchWorkObject(
					PatchEnv.getVWSession(), workflowNumber,
					VWRoster.QUERY_NO_OPTIONS);
			logger.info("登录用户--------"
					+ PatchEnv.getVWSession().fetchCurrentUserInfo().getName());
			logger.info("登录之后的用户名"
					+ PatchEnv.getVWSession().fetchCurrentUserInfo().getName());
			return wob;
		} catch (VWException e) {
			logger.error("vwGetTmsWorkObject方法 流程实例id得到流程对象出现异常", e);
			throw new Exception(e);
		}
	}

	/**
	 * 终止流程实例
	 * 
	 * @param wob
	 * @throws Exception
	 */
	public void vwTerminalWorkObject(VWWorkObject wob) throws Exception {
		try {
			wob.doLock(true);
			wob.doTerminate();
		} catch (VWException e) {
			logger.error("vwTerminalWorkObject方法 终止流程实例出现异常", e);
			throw new Exception(e);
		}
	}

	/**
	 * 打印流程实例对象
	 * 
	 * @param wob
	 */
	public void printWob(Object wob) {
		boolean ing = true;
		if (wob instanceof VWWorkObject) {
			ing = true;
		} else {
			// wob instanceof VWLogElement
			ing = false;
		}
		try {
			logger.info("vwWorkObject:");
			logger.info("WobNumber = "
					+ (ing ? ((VWWorkObject) wob).getWorkObjectNumber()
							: ((VWLogElement) wob).getWorkObjectNumber()));
			logger.info("Subject = "
					+ (ing ? ((VWWorkObject) wob).getSubject()
							: ((VWLogElement) wob).getSubject()));
			logger.info("StepName = "
					+ (ing ? ((VWWorkObject) wob).getStepName()
							: ((VWLogElement) wob).getStepName()));
			logger.info("LaunchDate = "
					+ (ing ? ((VWWorkObject) wob).getLaunchDate()
							: ((VWLogElement) wob).getFieldValue("F_StartTime")));
			logger.info("DataFields : ");
			VWDataField[] dfs = ing ? ((VWWorkObject) wob).getDataFields(
					VWFieldType.ALL_FIELD_TYPES,
					VWWorkObject.FIELD_USER_AND_SYSTEM_DEFINED)
					: ((VWLogElement) wob).getDataFields();
			for (VWDataField d : dfs) {
				logger.info(d.getName() + "=" + d.getStringValue());
			}
		} catch (Exception e) {
			logger.error("printWob方法 打印流程实例对象出现异常", e);
		}
	}

	/**
	 * 用户id得到用户名
	 * 
	 * @param userId
	 * @return
	 */
	public String vwGetUserNameById(Integer userId) {
		String userName = "";
		try {
			userName = PatchEnv.getVWSession().convertIdToUserName(userId);
		} catch (Exception e) {
			logger.error("vwGetUserNameById方法 用户id得到用户名出现异常", e);
		}
		return userName;
	}

	/**
	 * 用户用户名得到id
	 * 
	 * @param userId
	 * @return
	 */
	public Integer vwGetIdByUserName(String userName) {
		Integer userId = 0;
		try {
			userId = PatchEnv.getVWSession().convertUserNameToId(userName);
		} catch (Exception e) {
			logger.error("vwGetIdByUserName方法 用户用户名得到id出现异常", e);
		}
		return userId;
	}

	/**
	 * 流程className得到classId
	 * 
	 * @param className
	 * @return
	 */
	public Integer vwGetClassIdByClassName(String className) {
		Integer classId = 0;
		try {
			classId = PatchEnv.getVWSession().convertClassNameToId(className,
					false);
		} catch (Exception e) {
			logger.error("vwGetClassIdByClassName方法 流程className得到classId出现异常",
					e);
		}
		return classId;
	}

	/****************************************************************************/

	public void queryStep() {
		logger.info("PEManager.queryStep()~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~`");
		String workQueueName = "queue1";

		List woj = QueueHelper.getWorkElements(PatchEnv.getVWSession(),
				workQueueName, null, VWFetchType.FETCH_TYPE_WORKOBJECT);
		logger.info("wojs:" + woj.size());
		try {
			VWStepElement se = QueueHelper.getStepElementByWorkObjectNumber(
					PatchEnv.getVWSession().getQueue(workQueueName),
					new VWWorkObjectNumber(workflowNumber1),
					VWQueue.QUERY_NO_OPTIONS);
			logger.info("stepName:" + se.getStepName());
			logger.info("WorkObjectNumber:" + se.getWorkObjectNumber());
			logger.info("WorkflowNumber:" + se.getWorkflowNumber());
		} catch (VWException e) {
			logger.error("queryStep方法出现异常", e);
		}
	}

	public void queueTest() {
		// QueueHelper.getStepElementByWorkObjectNumber(vwQueue, vwWobNum,
		// queryFlags);

		// Fetch a list of queue names for Work queues (QUEUE_PROCESS)
		// and User Queues (QUEUE_USER_CENTRIC)
		int myQueueFlags = VWSession.QUEUE_PROCESS;
		String[] QueueNames = null;
		try {
			QueueNames = PatchEnv.getVWSession().fetchQueueNames(myQueueFlags);
		} catch (VWException e) {
			e.printStackTrace();
		}
		logger.info("QueueNames:");
		for (String queueName : QueueNames) {
			logger.info(queueName + "|");
		}
		logger.info("");

		String[] RosterNames = null;
		try {
			RosterNames = PatchEnv.getVWSession().fetchRosterNames(true);
		} catch (VWException e) {
			logger.error("queueTest方法出现异常", e);
		}
		logger.info("RosterNames:");
		for (String rosterName : RosterNames) {
			logger.info(rosterName + "|");
		}
		logger.info("");

	}

	public void queryWorkObjs() {
		logger.info("PEManager.queryWorkObjs()~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~`");
		Object[] queryMin = new Object[1];
		Object[] queryMax = new Object[1];
		String wobNumber;
		String userName;
		String workFlowName;
		String subject;
		try {
			VWQueue queue = PatchEnv.getVWSession().getQueue("queue1");
			String queryIndex = "F_WobNum";
			String wob = workflowNumber1;
			queryMin[0] = wob;
			queryMax[0] = wob;

			int queryFlag = VWQueue.QUERY_MIN_VALUES_INCLUSIVE
					+ VWQueue.QUERY_MAX_VALUES_INCLUSIVE;
			int queryType = VWFetchType.FETCH_TYPE_QUEUE_ELEMENT;
			VWQueueQuery query = queue.createQuery(queryIndex, queryMin,
					queryMax, queryFlag, null, null, queryType);

			while (query.hasNext()) {
				VWQueueElement queueElement = (VWQueueElement) query.next();
				// extract useful information to display.
				wobNumber = queueElement.getWorkObjectNumber();
				userName = queueElement.getFieldValue("F_BoundUser").toString();
				workFlowName = queueElement.getWorkflowName();
				subject = queueElement.getFieldValue("F_Subject").toString();
				logger.info("Queue Query With queue:");
				logger.info("WOB Number= " + wobNumber);
				logger.info("UserName = " + userName);
				logger.info("WorkFlowName = " + workFlowName);
				logger.info("Subject= " + subject);
			}
		} catch (VWException e) {
			logger.error("queryWorkObjs方法出现异常", e);
		}
	}

	// ----------------------------------------陈龙添加的方法--------------------------------------------------------------//

	/**
	 * 
	 * <p>
	 * Description: 获取流程版本
	 * </p>
	 * 
	 * @param proceePath
	 * @return
	 */
	public Integer vwFindProcessVersion(String proceePath) {
		Integer version = pressVserionMap.get(proceePath);
		if (version == null) {
			Connection conn = PatchEnv.getFileNetConn();
			logger.info(conn.getConnectionType());
			// TODO 解决CE安全上下文
			ObjectStore os = getOS();
			Document doc = CEUtil.fetchDocByPath(os, proceePath);
			if (doc != null) {
				version = doc.get_MajorVersionNumber();
				pressVserionMap.put(proceePath, version);
			}
		}
		return version;
	}

	/**
	 * 
	 * <p>
	 * Description: 得到当前节点备注
	 * </p>
	 * 
	 * @param fieldDataType
	 *            字段数据类型
	 * @param fieldType
	 *            字段类型（自定义还是系统）
	 * @param wokrObjectNumber
	 *            工作对象编号
	 * @return
	 * @throws VWException
	 */
	public String vwFindCurrentStep(String wokrObjectNumber) throws VWException {
		VWWorkObject vobj;
		try {
			vobj = RosterHelper.vwfetchWorkObject(PatchEnv.getVWSession(),
					wokrObjectNumber, 0);
			// 得到当前任务节点
			VWStepElement step = vobj.fetchStepElement();
			return step.getComment();
		} catch (VWException e) {
			logger.error("vwFindCurrentStep方法 得到当前节点备注出现异常", e);
			throw e;
		}

	}

	/**
	 * 
	 * <p>
	 * Description: 审批是否通过
	 * </p>
	 * 
	 * @param workObjectNum
	 * @param isPass
	 * @throws VWException
	 */
	public void vwDisposeTask(String workObjectNum, boolean isPass, String memo)
			throws VWException {
		Validate.notNull(workObjectNum, "工作对象编号能为空");
		VWWorkObject vobj;
		try {
			// 得到工作对象
			vobj = RosterHelper.vwfetchWorkObject(PatchEnv.getVWSession(),
					workObjectNum, 0);
			// 得到当前任务节点
			VWStepElement step = vobj.fetchStepElement();
			VWParameter[] pa = step.getParameters(
					VWFieldType.FIELD_TYPE_BOOLEAN,
					VWStepElement.FIELD_USER_DEFINED);
			// 锁定节点
			step.doLock(true);
			// 设置备注
			step.setComment(memo);
			if (pa != null) {
				for (VWParameter vwp : pa) {
					String paramName = vwp.getName();
					// 是否批准
					if (paramName.contains("_Pass")) {
						// 是否通过
						step.setParameterValue(paramName, isPass, true);
					}
				}
			}
			// 保存并执行到下一步
			step.doDispatch();
		} catch (VWException e) {
			logger.error("vwDisposeTask方法 审批是否通过出现异常", e);
			throw e;
		}
	}

	/**
	 * 
	 * <p>
	 * Description:复杂条件判断审批
	 * </p>
	 * 
	 * @param workObjectNum
	 * @param paramMap
	 * @throws VWException
	 */
	public void vwDisposeTask(String workObjectNum,
			Map<String, Object> paramMap, String memo) throws VWException {
		Validate.notNull(workObjectNum, "工作对象编号能为空");
		VWWorkObject vobj;
		try {
			// 得到工作对象
			vobj = RosterHelper.vwfetchWorkObject(PatchEnv.getVWSession(),
					workObjectNum, 0);
			// 得到当前任务节点
			VWStepElement step = vobj.fetchStepElement();
			VWParameter[] pa = step.getParameters(VWFieldType.ALL_FIELD_TYPES,
					VWStepElement.FIELD_USER_DEFINED);
			// 锁定节点
			step.doLock(true);
			// 设置备注
			step.setComment(memo);
			if (pa != null) {
				for (VWParameter vwp : pa) {
					String paramName = vwp.getName();
					for (Map.Entry<String, Object> en : paramMap.entrySet()) {
						if (paramName.contains(en.getKey())) {
							step.setParameterValue(paramName, en.getValue(),
									true);
						}
					}
				}
			}
			// 保存并执行到下一步
			step.doDispatch();
		} catch (VWException e) {
			logger.error("vwDisposeTask方法 复杂条件判断审批出现异常", e);
			throw e;
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 提交申请方法，并关联相关附件文档
	 * </p>
	 * 
	 * @param workQueueName
	 * @param paras
	 * @param workflowNumber
	 * @param docIdList
	 * @throws Exception
	 */
	public void vwLauchStep(String workQueueName,
			HashMap<String, Object> paras, String workflowNumber,
			List<String> docIdList) throws Exception {
		try {
			VWStepElement se = QueueHelper.getStepElementByWorkObjectNumber(
					PatchEnv.getVWSession().getQueue(workQueueName),
					new VWWorkObjectNumber(workflowNumber),
					VWQueue.QUERY_NO_OPTIONS);
			VWParameter[] ps = se.getParameters(VWFieldType.ALL_FIELD_TYPES,
					VWStepElement.FIELD_USER_DEFINED);
			QueueHelper.lockStepElement(PatchEnv.getVWSession(), se);
			Object comment = paras.get("TMS_Requester_Memo");
			if (comment != null) {
				se.setComment(comment.toString());
			}
			if (ps != null) {
				for (Map.Entry<String, Object> en : paras.entrySet()) {
					for (VWParameter param : ps) {
						if (param.getName().equals(en.getKey())) {
							se.setParameterValue(en.getKey(), en.getValue(),
									true);
						}
						// 设置附件
						if (param.getName().contains("Attach")) {
							if (!docIdList.isEmpty()) {
								VWAttachment[] attach = this.addStepAttachment(
										se, param.getName(), docIdList);
								se.setParameterValue(param.getName(), attach,
										true);
							}
						}
					}
				}
			}
			se.doDispatch();
		} catch (VWException e) {
			logger.error("vwLauchStep方法 提交申请方法，并关联相关附件文档出现异常", e);
			throw new Exception(e);
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 简单发起流程
	 * </p>
	 * 
	 * @param workflowNumber
	 * @param workQueueName
	 * @param comment
	 * @throws Exception
	 */
	public void vwLauchStep(String workflowNumber, String workQueueName,
			String comment) throws Exception {
		try {
			VWStepElement se = QueueHelper.getStepElementByWorkObjectNumber(
					PatchEnv.getVWSession().getQueue(workQueueName),
					new VWWorkObjectNumber(workflowNumber),
					VWQueue.QUERY_NO_OPTIONS);
			QueueHelper.lockStepElement(PatchEnv.getVWSession(), se);
			se.setComment(comment);
			se.doDispatch();
		} catch (VWException e) {
			logger.error("vwLauchStep方法 简单发起流程出现异常", e);
			throw new Exception(e);
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 重新申请方法(通过申请队列查询节点对象时报异常查询不到该步骤)
	 * </p>
	 * 
	 * @param paras
	 * @param workflowNumber
	 * @param docIdList
	 * @throws Exception
	 */
	public void vwSubmitApply(HashMap<String, Object> paras,
			String workflowNumber, List<String> docIdList) throws Exception {
		Validate.notNull(workflowNumber, "应流程实例编号为空");
		try {
			VWWorkObject vwobj = RosterHelper.vwfetchWorkObject(
					PatchEnv.getVWSession(), workflowNumber, 0);
			VWStepElement se = vwobj.fetchStepElement();
			VWParameter[] ps = se.getParameters(VWFieldType.ALL_FIELD_TYPES,
					VWStepElement.FIELD_USER_DEFINED);
			QueueHelper.lockStepElement(PatchEnv.getVWSession(), se);
			Object comment = paras.get("TMS_Requester_Memo");
			if (comment != null) {
				se.setComment(comment.toString());
			}
			if (ps != null) {
				for (Map.Entry<String, Object> en : paras.entrySet()) {
					for (VWParameter param : ps) {
						if (param.getName().equals(en.getKey())) {
							se.setParameterValue(en.getKey(), en.getValue(),
									true);
						}
						// 设置附件
						if (param.getName().contains("Attach")) {
							if (!docIdList.isEmpty()) {
								VWAttachment[] attach = this.addStepAttachment(
										se, param.getName(), docIdList);
								se.setParameterValue(param.getName(), attach,
										true);
							}
						}
					}
				}
			}
			se.doDispatch();
		} catch (VWException e) {
			logger.error("vwSubmitApply方法 重新申请方法(通过申请队列查询节点对象时报异常查询不到该步骤)出现异常",
					e);
			throw new Exception(e);
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 将附件组关联到Step
	 * </p>
	 * 
	 * @param vwStepElement
	 * @param docIdList
	 * @throws VWException
	 */
	public VWAttachment[] addStepAttachment(VWStepElement vwStepElement,
			String paramName, List<String> docIdList) throws VWException {
		try {
			// 获取新的附件组
			VWAttachment[] vwAtts = null;
			// 附件类 获取需求附件
			// 获取对该附件的权限
			int rights = vwStepElement.getParameter(paramName).getMode();
			if (rights == 2 || rights == 3) {
				// 设置附件组
				int size = docIdList.size();
				vwAtts = new VWAttachment[size];
				for (int i = 0; i < size; i++) {
					VWAttachment vwattach = new VWAttachment();
					vwattach.setId(docIdList.get(i));
					vwattach.setLibraryType(VWLibraryType.LIBRARY_TYPE_CONTENT_ENGINE);
					vwattach.setType(VWAttachmentType.ATTACHMENT_TYPE_DOCUMENT);
					vwAtts[i] = vwattach;
				}
			}
			return vwAtts;
		} catch (VWException e) {
			logger.error("addStepAttachment方法 将附件组关联到Step出现异常", e);
			throw e;
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 清楚附件
	 * </p>
	 * 
	 * @param workObjectNum
	 * @throws Exception
	 */
	public void vwDeleteAttach(String workObjectNum) throws Exception {
		Validate.notNull(workObjectNum, "工作对象编号能为空");
		try {
			// 得到工作对象
			VWWorkObject vobj = RosterHelper.vwfetchWorkObject(
					PatchEnv.getVWSession(), workObjectNum, 0);
			// 得到当前任务节点
			VWStepElement step = vobj.fetchStepElement();
			// 得到附件类型参数
			VWParameter[] ps = step.getParameters(
					VWFieldType.FIELD_TYPE_ATTACHMENT,
					VWStepElement.FIELD_USER_DEFINED);
			step.doLock(true);
			if (ps != null) {
				for (VWParameter pm : ps) {
					step.setParameterValue(pm.getName(), null, true);
				}
			}
		} catch (VWException e) {
			logger.error("vwDeleteAttach方法 清楚附件出现异常", e);
			throw new Exception(e);
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 查询当前节点有哪些附件
	 * </p>
	 * 
	 * @param workObjectNum
	 * @return
	 * @throws Exception
	 */
	public List<String> vwFindDocIdByAttach(String workObjectNum)
			throws Exception {
		Validate.notNull(workObjectNum, "工作对象编号能为空");
		VWWorkObject vobj;
		List<String> docList = Lists.newArrayList();
		try {
			// 得到工作对象
			vobj = RosterHelper.vwfetchWorkObject(PatchEnv.getVWSession(),
					workObjectNum, 0);
			// 得到当前任务节点
			VWStepElement step = vobj.fetchStepElement();
			VWAttachment[] vwAttachments = null;
			// 得到附件类型参数
			VWParameter[] ps = step.getParameters(
					VWFieldType.FIELD_TYPE_ATTACHMENT,
					VWStepElement.FIELD_USER_DEFINED);
			if (ps != null) {
				for (VWParameter param : ps) {
					vwAttachments = (VWAttachment[]) step
							.getParameterValue(param.getName());
					if (vwAttachments != null) {
						for (VWAttachment attach : vwAttachments) {
							docList.add(attach.getId());
						}
					}
				}
			}
			return docList;
		} catch (VWException e) {
			logger.error("vwFindDocIdByAttach方法 查询当前节点有哪些附件出现异常", e);
			throw new Exception(e);
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 通过队列查询VWWorkObject
	 * </p>
	 * 
	 * @param session
	 *            PE Session
	 * @param queueName
	 *            队列名称
	 * @param queryType
	 *            队列类型
	 * @return
	 * @throws VWException
	 */
	public VWQueueQuery vwfetchWorkObjByQue(String queueName, int queryType)
			throws VWException {
		VWQueue queue = PatchSessionHelper.getVWQueue(PatchEnv.getVWSession(),
				queueName);
		return vwCreateQueueQuery(null, null, null, queryType, null, null,
				VWFetchType.FETCH_TYPE_WORKOBJECT, queue);
	}

	/**
	 * 
	 * <p>
	 * Description:创建队列查询对象
	 * </p>
	 * 
	 * @param indexName
	 * @param firstValues
	 * @param lastValues
	 * @param queryFlags
	 * @param filter
	 *            过滤器
	 * @param substitutionVars
	 *            过滤值
	 * @param fetchType
	 *            抓取的查询对象类型
	 * @param vwQueue
	 *            队列
	 * @return
	 * @throws VWException
	 */
	public VWQueueQuery vwCreateQueueQuery(String indexName,
			Object[] firstValues, Object[] lastValues, int queryFlags,
			String filter, Object[] substitutionVars, int fetchType,
			VWQueue vwQueue) throws VWException {
		return vwQueue.createQuery(indexName, firstValues, lastValues,
				queryFlags, filter, substitutionVars, fetchType);
	}

	public ObjectStore getOS() {
		String ceUsername = ConfigUtils.getString("CE.filenetCeAdminUserName");
		String password = ConfigUtils.getString("CE.filenetCeAdminUserPasswd");
		String ceUrl = ConfigUtils.getString("CE.CEURL").replace("\\", "");
		String objectStore = ConfigUtils.getString("CE.ObjectStore");
		CEConnection ce = new CEConnection();
		ce.establishConnection(ceUsername, password, "FileNetP8", ceUrl);
		return ce.fetchOS(objectStore);
	}
}
