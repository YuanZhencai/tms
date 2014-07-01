package com.wcs.tms.service.process.common.patch.service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.StringUtils;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.ProcessDefine;
import com.wcs.tms.model.ProcessMap;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.patch.model.PatchApprovalLog;
import com.wcs.tms.service.process.common.patch.model.PatchSubedProcess;
import com.wcs.tms.service.process.common.patch.pe.PatchPEManager;
import com.wcs.tms.service.system.sysprocess.ProcessDefineService;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;
import com.wcs.tms.view.process.common.entity.ProcessInstanceVo;

import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWWorkObjectNumber;

/**
 * 处理2014.4.11~2014.4.28之间发生故障，丢失的流程数据和流程审批日志
 * 
 * @author liushengbin
 * 
 */
@Stateless
public class PatchMainService implements Serializable {

	private static final Log log = LogFactory.getLog(PatchMainService.class);

	private static final long serialVersionUID = 4705969437831070005L;
	@PersistenceContext(unitName = "pu")
	protected EntityManager entityManager;
	@Inject
	EntityService entityService;
	@Inject
	LoginService loginService;
	@Inject
	PatchPEManager peManager;
	@Inject
	ProcessDefineService processDefineService;
	@Inject
	ProcessUtilMapService processUtilMapService;

	/**
	 * 清理
	 */
	public int cleanTMPData() {
		entityManager.createNativeQuery("delete from PATCH_APPROVAL_LOG")
				.executeUpdate();
		return entityManager.createNativeQuery(
				"delete from PATCH_SUBED_PROCESS").executeUpdate();
	}

	/**
	 * 把2014.4.11~2014.4.28之间丢失的流程数据和流程审批记录，导入到中间表
	 * 
	 * @param vo
	 */
	public void saveData2TMP(ProcessInstanceVo vo) {
		String pidFn = vo.getProcInstId();
		ProcessMap pm = processUtilMapService.getProcessMapByFnId(pidFn);

		PatchSubedProcess tmp = new PatchSubedProcess();

		tmp.setCompanyId(pm.getCompanyId());
		tmp.setCompanyName(pm.getCompanyName() == null ? "" : pm
				.getCompanyName());
		tmp.setFnPid(pidFn);
		tmp.setTmsPid(vo.getPidTms());

		tmp.setProcessCode(vo.getProcessCode());

		// 得到流程定义obj
		ProcessDefine pd = processDefineService.getProcDefineByCode(vo
				.getProcessCode());

		tmp.setProcessDefineId(pd.getId().toString());

		tmp.setProcessName(vo.getProcessName());
		tmp.setProcessVersion(Integer.toString(vo.getProcessVersion()));
		tmp.setDescrible(vo.getDescrible());
		tmp.setNodeName(vo.getNodeName());
		tmp.setNodeExer(vo.getNodeExer());
		tmp.setOriginator(vo.getOriginator());
		tmp.setOriginatorName(loginService.getCNNameByAccount(vo
				.getOriginator()));
		tmp.setSubmitDate(vo.getSubmitDate());
		tmp.setProcessEndDate(vo.getProcessEndDate());
		tmp.setIsTerminalFlag(vo.getTerminalFlag());
		tmp.setIsComfirmView(vo.getUserComfirmView());
		entityService.create(tmp);

		// 保存流程实例对应的审批日志
		List<ProcessDetailVo> approvalLogs = getProcessDetail(pidFn);
		for (ProcessDetailVo detailVo : approvalLogs) {
			PatchApprovalLog approvalLog = new PatchApprovalLog();
			approvalLog.setFnPid(pidFn);
			approvalLog.setTmsPid(vo.getPidTms());
			approvalLog.setProcessCode(vo.getProcessCode());
			approvalLog.setProcessName(vo.getProcessName());
			approvalLog.setNodeName(detailVo.getProssNodeName());
			approvalLog.setNodeExer(detailVo.getOperatorsName());
			approvalLog.setNodeExerName(loginService
					.getCNNameByAccount(detailVo.getOperatorsName()));
			approvalLog.setOpertorTime(detailVo.getOperatorTime());
			approvalLog.setNodeMemo(detailVo.getNodeMemo());
			entityService.create(approvalLog);
		}

	}

	/**
	 * 查询流程详细
	 * 
	 * @param procInstId
	 * @return
	 */
	public List<ProcessDetailVo> getProcessDetail(String procInstId)
			throws ServiceException {
		List<ProcessDetailVo> detailVos = new ArrayList<ProcessDetailVo>();
		String filter = "1=1 and F_WobNum = :wobNum and (F_EventType = :eventType1 or F_EventType = :eventType2)";
		Object[] substitutionVars = {
				new VWWorkObjectNumber(procInstId),
				ProcessDefineUtil.PROCESS_EVENTTYPE_MAP
						.get(ProcessDefineUtil.EventTypeEnum.StepEnd),
				ProcessDefineUtil.PROCESS_EVENTTYPE_MAP
						.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal) };
		try {
			List<VWLogElement> les = new ArrayList<VWLogElement>();
			les = peManager.vwEventLogWob(filter, substitutionVars);
			for (VWLogElement le : les) {
				ProcessDetailVo detailVo = new ProcessDetailVo();
				if (ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(
						ProcessDefineUtil.EventTypeEnum.ProcessTerminal)
						.equals(le.getEventType())) {
					detailVo.setProssNodeName("流程终止");
				} else {
					detailVo.setProssNodeName(le.getStepName());
				}
				detailVo.setOperatorsName(le.getUserName());
				detailVo.setOperatorTime(le.getTimeStamp());
				detailVo.setNodeMemo((String) le.getFieldValue("F_Comment"));
				detailVo.setId(new Long(le.getSequenceNumber()));
				detailVos.add(detailVo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return detailVos;
	}

	/**
	 * 查询流程详细,获取4.11~4.28故障期间的，从PATCH_APPROVAL_LOG表中，查询已提交的流程列表
	 * 
	 * @param procInstId
	 * @return
	 */
	public List<ProcessDetailVo> getProcessDetailFor411(String procInstId)
			throws ServiceException {
		List<ProcessDetailVo> detailVos = new ArrayList<ProcessDetailVo>();
		try {
			// 查询条件组装
			StringBuilder jpql = new StringBuilder(
					"select db from PatchApprovalLog db where 1=1 and ");
			jpql.append(" db.fnPid='" + procInstId + "'");
			jpql.append(" order by db.opertorTime asc");
			List<PatchApprovalLog> patchApprovalLogList = entityService
					.find(jpql.toString());
			for (PatchApprovalLog patchApprovalLog : patchApprovalLogList) {
				ProcessDetailVo detailVo = new ProcessDetailVo();
				detailVo.setProssNodeName(patchApprovalLog.getNodeName());
				detailVo.setOperatorsName(patchApprovalLog.getNodeExer());
				detailVo.setOperatorTime(patchApprovalLog.getOpertorTime());
				detailVo.setNodeMemo(patchApprovalLog.getNodeMemo());
				detailVo.setId(patchApprovalLog.getId());
				detailVos.add(detailVo);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return detailVos;
	}

	/**
	 * 查询已提交的流程列表,获取4.11~4.28故障期间的，从PATCH_SUBED_PROCESS表中
	 * 
	 * @param conditionMap
	 * @return
	 * @throws ServiceException
	 */
	public List<ProcessInstanceVo> getTerminatedProcessInstanceVosFor411(
			Map<String, Object> conditionMap) throws ServiceException {

		/*** 包装VO信息 ************************/
		List<ProcessInstanceVo> pivos = new ArrayList<ProcessInstanceVo>();
		// 不是，查询已结束的，直接pass
		if (!"2".equals(conditionMap.get("exeStatus"))) {
			return pivos;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String companyId = (String) conditionMap.get("companyId");
			String procInstId = (String) conditionMap.get("procInstId");

			String fnId = processUtilMapService.getFnIdByTmsId(procInstId);

			// 查询条件组装
			StringBuilder jpql = new StringBuilder(
					"select db from PatchSubedProcess db where 1=1 and ");
			jpql.append(" db.originator='" + loginService.getCurrentUserName()
					+ "'");
			if (StringUtils.isNotEmpty(procInstId)) {
				jpql.append(" and db.fnPid ='" + fnId + "'");
			}

			if (null != conditionMap.get("startDate")) {
				String startDate = sdf.format(conditionMap.get("startDate"));
				jpql.append(" and db.submitDate >='" + startDate + "'");
			}

			if (null != conditionMap.get("endDate")) {
				Date date = (Date) conditionMap.get("endDate");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				String endDate = sdf.format(calendar.getTime());
				jpql.append(" and db.submitDate <='" + endDate + "'");
			}

			if (StringUtils.isNotEmpty(companyId)) {
				jpql.append(" and db.companyId ='" + companyId + "'");
			}

			if (conditionMap.get("processId") != null
					&& !"0".equals(conditionMap.get("processId"))) {
				jpql.append(" and db.processDefineId ='"
						+ conditionMap.get("processId") + "'");
			}
			List<PatchSubedProcess> patchSubedProcessList = entityService
					.find(jpql.toString());

			for (PatchSubedProcess p : patchSubedProcessList) {
				ProcessInstanceVo vo = new ProcessInstanceVo();
				vo.setIsPatch("true");
				vo.setProcInstId(p.getFnPid());
				vo.setPidTms(p.getTmsPid());
				vo.setCompanyName(p.getCompanyName() == null ? "" : p
						.getCompanyName());
				vo.setProcessCode(p.getProcessCode());
				vo.setProcessName(p.getProcessName());
				vo.setProcessVersion(Integer
						.parseInt(p.getProcessVersion() == null ? "1" : p
								.getProcessVersion()));
				vo.setDescrible(p.getDescrible());
				vo.setSubmitDate((Date) p.getSubmitDate());
				vo.setProcessEndDate(p.getProcessEndDate());
				vo.setNodeName(p.getNodeName());
				vo.setNodeExer(p.getNodeExer());
				vo.setTerminalFlag(p.getIsTerminalFlag());
				vo.setOriginator(p.getOriginatorName());
				vo.setUserComfirmView(p.getIsComfirmView());
				pivos.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("getTerminatedProcessInstanceVos方法 得到已结束的流程实例vo 出现异常：", e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}

		return pivos;
	}

	/**
	 * 查询已处理的任务列表 ,获取4.11~4.28故障期间的，从PATCH_SUBED_PROCESS和PATCH_APPROVAL_LOG表中
	 * 
	 * @param conditionMap
	 * @return
	 * @throws ServiceException
	 */
	public List<ProcessInstanceVo> getDealedProcessInstanceVosFor411(
			Map<String, Object> conditionMap) throws ServiceException {

		/*** 包装VO信息 ************************/
		List<ProcessInstanceVo> pivos = new ArrayList<ProcessInstanceVo>();
		// 不是，查询已结束的，直接pass
		if (!"2".equals(conditionMap.get("exeStatus"))) {
			return pivos;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String companyId = (String) conditionMap.get("companyId");
			String tmsPid = (String) conditionMap.get("procInstId");			

			// 查询条件组装
			StringBuilder jpql = new StringBuilder(
					"select a.tmsPid,a.companyName,a.processName,a.describle,a.processVersion,b.nodeName,");
			jpql.append("b.opertorTime,a.submitDate,a.processEndDate,a.isComfirmView,a.fnPid,a.processCode,a.isTerminalFlag");
			jpql.append(" from PatchSubedProcess a,PatchApprovalLog b where a.fnPid=b.fnPid and ");
			jpql.append(" b.nodeExer='" + loginService.getCurrentUserName()
					+ "'");
			if (StringUtils.isNotEmpty(tmsPid)) {
				jpql.append(" and a.tmsPid ='" + tmsPid + "'");
			}

			if (null != conditionMap.get("startDate")) {
				String startDate = sdf.format(conditionMap.get("startDate"));
				jpql.append(" and b.opertorTime >='" + startDate + "'");
			}

			if (null != conditionMap.get("endDate")) {
				Date date = (Date) conditionMap.get("endDate");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				String endDate = sdf.format(calendar.getTime());
				jpql.append(" and b.opertorTime <='" + endDate + "'");
			}

			if (StringUtils.isNotEmpty(companyId)) {
				jpql.append(" and a.companyId =" + companyId + "");
			}

			if (conditionMap.get("processId") != null
					&& !"0".equals(conditionMap.get("processId"))) {
				jpql.append(" and a.processDefineId ='"
						+ conditionMap.get("processId") + "'");
			}

			System.out.println("jpql=================================================:"+jpql.toString());
			// 任务处理时间在4.11~4.28之间的，已处理任务列表
			List resultList = entityService.find(jpql.toString());
			if (resultList != null && !resultList.isEmpty()) {
				for (int i = 0; i < resultList.size(); i++) {
					Object[] object = (Object[]) resultList.get(i);
					String pidTms = StringUtils.safeString((String) object[0]);
					String companyName = StringUtils
							.safeString((String) object[1]);
					String processName = StringUtils
							.safeString((String) object[2]);
					String describle = StringUtils
							.safeString((String) object[3]);
					String processVersion = StringUtils
							.safeString((String) object[4]);
					String nodeName = StringUtils
							.safeString((String) object[5]);
					Date stepDate = (Date) object[6];
					Date submitDate = (Date) object[7];
					Date processEndDate = (Date) object[8];
					Boolean isComfirmView = (Boolean) object[9];
					String fnPid = StringUtils.safeString((String) object[10]);
					String processCode = StringUtils
							.safeString((String) object[11]);
					Boolean isTerminalFlag = (Boolean) object[12];

					ProcessInstanceVo vo = new ProcessInstanceVo();
					vo.setIsPatch("true");
					vo.setProcInstId(fnPid);
					vo.setPidTms(pidTms);
					vo.setCompanyName(companyName);
					vo.setProcessCode(processCode);
					vo.setProcessName(processName);
					vo.setProcessVersion(Integer
							.parseInt(processVersion == null ? "1"
									: processVersion));
					vo.setDescrible(describle);
					vo.setSubmitDate(submitDate);
					vo.setStepDate(stepDate);
					vo.setProcessEndDate(processEndDate);
					vo.setNodeName(nodeName);
					vo.setTerminalFlag(isTerminalFlag);
					vo.setUserComfirmView(isComfirmView);
					vo.setThisStatus("2");
					pivos.add(vo);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("getDealedProcessInstanceVosFor411方法 得到已处理的任务列表vo 出现异常：",
					e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}

		return pivos;
	}
}
