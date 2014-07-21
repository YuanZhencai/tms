package com.wcs.tms.service.process.regicapital;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.common.model.Usermstr;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.mail.Mail;
import com.wcs.tms.mail.MailService;
import com.wcs.tms.mail.MailUtil;
import com.wcs.tms.mail.SendMailService;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.ProcRegiCapital;
import com.wcs.tms.model.RemittanceLineAccount;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.util.ProcessDefineUtil;

import filenet.vw.api.VWWorkObjectNumber;

/**
 * 
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description: 注册资本金申请
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Stateless
public class RegisterCaptialService implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	@Inject
	private PEManager peManager;
	@Inject
	private LoginService loginService;
	@Inject
	private EntityService entityService;
	@Inject
	MailService mailService;
	@Inject
	SendMailService sendMailService;
	@Inject
	ProcessUtilMapService processUtilMapService;// 9.10

	private static final Log log = LogFactory
			.getLog(RegisterCaptialService.class);
	
	
	/**
	 * 更新股东注册资本金主数据
	 * @param entityClass
	 * @param procInstId
	 */
	public void updateShareHolder(ProcRegiCapital procRegiCapital) {
		try{
			ShareHolder shareHolder = entityService.find(ShareHolder.class, procRegiCapital.getPayer());
			//更新的已到位资金 = 原有的已到位资金 + 本次请款金额
			Double fondsInPlace =  shareHolder.getFondsInPlace() + procRegiCapital.getThisFonds();			 
			StringBuilder jpql = new StringBuilder("update ShareHolder s as s set s.fondsInPlace="+fondsInPlace+" where s.id ="+procRegiCapital.getPayer());
			entityService.excuteUpdate(jpql);
		}catch(Exception e){
			log.error("updateShareHolder方法 更新股东注册资本金主数据 出现异常：",e);
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 创建流程实例并返回流程实例编号
	 * </p>
	 * 
	 * @param processName
	 *            FN流程名称
	 * @param comment
	 * @return
	 * @throws ServiceException
	 */
	public String vwCreateProcessInstance(String processName, String comment,
			String memo, List<String> documentList) throws ServiceException {
		String workflowNumber = "";
		// 检查FN是否存在该流程
		if (peManager.checkWorkClassName(processName)) {
			try {
				workflowNumber = peManager.vwCreateInstance(processName,
						comment);
				HashMap<String, Object> step1para = new HashMap<String, Object>();
				// 得到登录用户
				Usermstr user = loginService.getCurrentUsermstr();
				step1para.put("TMS_Requester_Exer", user.getAdAccount());
				step1para.put("TMS_Requester_Memo", memo);
				peManager.vwLauchStep("TMS_Requester", step1para,
						workflowNumber, documentList);
				return workflowNumber;
			} catch (Exception e) {
				log.error("vwCreateProcessInstance方法 创建流程实例并返回流程实例编号 出现异常：", e);
				throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
			}
		} else {
			throw new ServiceException(ExceptionMessage.FN_NO_CLASS);
		}

	}

	/**
	 * 
	 * <p>
	 * Description: 重新申请
	 * </p>
	 * 
	 * @param memo
	 * @param workflowNumber
	 * @param documentList
	 * @throws ServiceException
	 */
	public void vwReplayRegister(String memo, String workflowNumber,
			List<String> documentList) throws ServiceException {
		try {
			peManager.vwSubmitApply(memo, workflowNumber, documentList);
		} catch (Exception e) {
			log.error("vwReplayRegister方法 重新申请 出现异常：", e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}

	/**
	 * 
	 * <p>
	 * Description:保存注册资本金
	 * </p>
	 * 
	 * @param procRegiCapital
	 * @throws ServiceException
	 */
	public void saveRegisCaptial(ProcRegiCapital procRegiCapital)
			throws ServiceException {
		try {
			this.entityService.create(procRegiCapital);
			// 生成流程实例编号映射9.10
			processUtilMapService.generateProcessMap(
					procRegiCapital.getProcInstId(), "BPM_RA_006",
					procRegiCapital.getCompany());
		} catch (Exception e) {
			log.error("saveRegisCaptial方法 保存注册资本金  出现异常：", e);
			throw new ServiceException(ExceptionMessage.PREGITCAPTIAL_SAVE, e);
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 根据流程实例编号得到注册资本金
	 * </p>
	 * 
	 * @param workClassName
	 * @return
	 * @throws ServiceException
	 */
	public ProcRegiCapital findProcRegiCaptial(String workClassName)
			throws ServiceException {
		Validate.notNull(workClassName, "注册资本金对应流程实例编号为空");
		try {
			StringBuilder bulder = new StringBuilder();
			bulder.append(
					"select prc from ProcRegiCapital prc left join fetch prc.company left join fetch prc.remittanceLineAccount ")
					.append("where prc.procInstId=?1 and prc.defunctInd = 'N'");
			List<ProcRegiCapital> list = this.entityService.find(
					bulder.toString(), workClassName);
			if (list.isEmpty()) {
				return null;
			}
			return list.get(0);
		} catch (Exception e) {
			log.error("findProcRegiCaptial方法 根据流程实例编号得到注册资本金 出现异常：", e);
			throw new ServiceException(ExceptionMessage.PREGITCAPTIAL_SAVE, e);
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 注册资本金审批
	 * </p>
	 * 
	 * @param workClassNum
	 * @param paramMap
	 */
	public void vwDisposeTask(String workClassNum,
			Map<String, Object> paramMap, String memo) throws ServiceException {
		Validate.notNull(workClassNum, "注册资本金对应流程实例编号为空");
		try {
			this.peManager.vwDisposeTask(workClassNum, paramMap, memo);
		} catch (Exception e) {
			log.error("vwDisposeTask方法 注册资本金审批 出现异常：", e);
			throw new ServiceException(ExceptionMessage.PREGITCAPTIAL_SAVE, e);
		}

	}

	/**
	 * 给集团项目经理发送邮件
	 * 
	 * @param processId
	 */
	public void sendGropPm(String processId) {
		List<Mail> mails = new ArrayList<Mail>();
		StringBuilder filter = new StringBuilder(
				"1=1 and F_EventType = :EventType");
		Object[] substitutionVars = new Object[2];
		filter.append(" and F_WobNum = :wobNum");
		substitutionVars[0] = ProcessDefineUtil.PROCESS_EVENTTYPE_MAP
				.get(ProcessDefineUtil.EventTypeEnum.StepEnd);
		substitutionVars[1] = new VWWorkObjectNumber(processId);

		List<String> steps = new ArrayList<String>();
		steps.add("集团项目经理审批");
		// 邮件封装
		mails = mailService.findEmailBySteps(filter.toString(),
				substitutionVars, steps, MailUtil.MailTypeEnum.GroupPM);
		sendMailService.send(mails);
	}

	/**
	 * 查询所有汇款线路
	 * */
	public List<RemittanceLineAccount> getSelAcNoList(Long comId) {
		StringBuilder jpql = new StringBuilder(
				"select rc from RemittanceLineAccount rc join fetch rc.company where ");
		jpql.append("rc.defunctInd='N' AND ");
		jpql.append("rc.company.id=" + comId);
		jpql.append(" and rc.accountType='2'");
		return entityService.find(jpql.toString());
	}

	/**
	 * 新增汇款线路
	 * */
	public void saveAcNo(RemittanceLineAccount rc) {
		rc.setCreatedBy(this.loginService.getCurrentUserName());
		rc.setUpdatedBy(this.loginService.getCurrentUserName());
		this.entityService.create(rc);
	}

}
