package com.wcs.tms.mail;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Lists;
import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.common.filenet.env.SysCfg;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.common.model.P;
import com.wcs.common.model.Rolemstr;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.mail.MailUtil.MailTypeEnum;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.ProcTMSStatus;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessWaitAcceptService;
import com.wcs.tms.service.process.guarantee.InnerGuaranteeService;

import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWWorkObject;

@Stateless
public class MailService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Inject
	private PEManager peManager;
	@Inject
	private EntityService entityService;
	@Inject
	private ProcessWaitAcceptService processWaitService;
	@Inject
	private LoginService loginService;
	// 9.10
	@Inject
	private ProcessUtilMapService processUtilMapService;
	@Inject
	private InnerGuaranteeService innerGuaranteeService;

	private Log log = LogFactory.getLog(MailService.class);
	// TODO 编写WebService客户端

	public void initMailPE() {
		peManager.noSessionInitPE();
	}

	/**
	 * <p>Description: 根据日志查询组装邮件对象</p>
	 * @param filter
	 * @param substitutionVars
	 * @return
	 * @throws ServiceException
	 */
	public List<Mail> findAllEmailByProcess(String filter, Object[] substitutionVars) throws ServiceException {
		try {
			List<Mail> mailList = Lists.newArrayList();
			// 查询已经被执行流程步骤，通过记录的日志
			List<VWLogElement> les = peManager.vwEventLogWob(filter, substitutionVars);
			// 执行过的最后一步
			VWLogElement lastLe = null;
			if (les.isEmpty()) {
				return mailList;
			} else {
				lastLe = les.get(les.size() - 1);
			}
			List<String> distinctUsers = new ArrayList<String>();
			for (VWLogElement le : les) {
				// 剔除重复的用户
				String user = le.getUserName();
				if (distinctUsers.contains(user)) {
					continue;
				} else {
					distinctUsers.add(user);
				}

				Mail mail = new Mail();
				// 查询该账户是否在主数据存在记录
				P p = findEmailByAccount(le.getUserName());
				if (p != null) {
					mail.setTelno(p.getCelno());
					mail.setEmail(p.getEmail());
					mail.setSubject(generationTitle(MailUtil.MailTypeEnum.All, le.getWorkClassName(),
							processUtilMapService.getTmsIdByFnId(le.getWorkFlowNumber()), lastLe.getStepName(), lastLe.getUserName(), null));
					mail.setBody(generationContent(MailUtil.MailTypeEnum.All, le.getWorkClassName(),
							processUtilMapService.getTmsIdByFnId(le.getWorkFlowNumber()), lastLe.getStepName(), lastLe.getUserName(), null,
							(String) lastLe.getFieldValue("F_Comment")));
					mailList.add(mail);
				}
			}
			return mailList;
		} catch (Exception e) {
			throw new ServiceException(ExceptionMessage.PROCESS_DETAIL, e);
		}
	}

	/**
	 * 根据日志查询组装邮件对象(得到已处理过流程的人员，在集团授信和银行授信处用)
	 * @param filter
	 * @param substitutionVars
	 * @return
	 * @throws ServiceException
	 */
	public List<Mail> findAllHandledByProcess(String filter, Object[] substitutionVars) throws ServiceException {
		try {
			List<Mail> mailList = Lists.newArrayList();
			// 查询已经被执行流程步骤，通过记录的日志
			List<VWLogElement> les = peManager.vwEventLogWob(filter, substitutionVars);
			// 执行过的最后一步
			VWLogElement lastLe = null;
			if (les.isEmpty()) {
				return mailList;
			} else {
				lastLe = les.get(les.size() - 1);
			}
			List<String> distinctUsers = new ArrayList<String>();
			for (VWLogElement le : les) {
				// 剔除重复的用户
				String user = le.getUserName();
				if (distinctUsers.contains(user)) {
					continue;
				} else {
					distinctUsers.add(user);
				}

				Mail mail = new Mail();
				// 查询该账户是否在主数据存在记录
				P p = findEmailByAccount(le.getUserName());
				if (p != null) {
					mail.setTelno(p.getCelno());
					mail.setEmail(p.getEmail());
					mail.setSubject(generationTitle(MailUtil.MailTypeEnum.Handled, le.getWorkClassName(),
							processUtilMapService.getTmsIdByFnId(le.getWorkFlowNumber()), lastLe.getStepName(), lastLe.getUserName(), null));
					mail.setBody(generationContent(MailUtil.MailTypeEnum.Handled, le.getWorkClassName(),
							processUtilMapService.getTmsIdByFnId(le.getWorkFlowNumber()), lastLe.getStepName(), lastLe.getUserName(), null,
							(String) lastLe.getFieldValue("F_Comment")));
					mailList.add(mail);
				}
			}
			return mailList;
		} catch (Exception e) {
			throw new ServiceException(ExceptionMessage.PROCESS_DETAIL, e);
		}
	}

	/**
	 * 申请人确认时，给已处理过的人员发送（内部担保，申请人确认后终止）
	 * @param filter
	 * @param substitutionVars
	 * @return
	 * @throws ServiceException
	 */
	public List<Mail> findAssureByProcess(String filter, Object[] substitutionVars) throws ServiceException {
		try {
			List<Mail> mailList = Lists.newArrayList();
			// 查询已经被执行流程步骤，通过记录的日志
			List<VWLogElement> les = peManager.vwEventLogWob(filter, substitutionVars);
			// 执行过的最后一步
			VWLogElement lastLe = null;
			if (les.isEmpty()) {
				return mailList;
			} else {
				lastLe = les.get(les.size() - 1);
			}
			List<String> distinctUsers = new ArrayList<String>();
			for (VWLogElement le : les) {
				// 剔除重复的用户
				String user = le.getUserName();
				if (distinctUsers.contains(user)) {
					continue;
				} else {
					distinctUsers.add(user);
				}

				Mail mail = new Mail();
				// 查询该账户是否在主数据存在记录
				P p = findEmailByAccount(le.getUserName());
				if (p != null) {
					mail.setTelno(p.getCelno());
					mail.setEmail(p.getEmail());
					mail.setSubject(generationTitle(MailUtil.MailTypeEnum.Assure, le.getWorkClassName(),
							processUtilMapService.getTmsIdByFnId(le.getWorkFlowNumber()), lastLe.getStepName(), lastLe.getUserName(), null));
					mail.setBody(generationContent(MailUtil.MailTypeEnum.Assure, le.getWorkClassName(),
							processUtilMapService.getTmsIdByFnId(le.getWorkFlowNumber()), lastLe.getStepName(), lastLe.getUserName(), null,
							(String) lastLe.getFieldValue("F_Comment")));
					mailList.add(mail);
				}
			}
			return mailList;
		} catch (Exception e) {
			throw new ServiceException(ExceptionMessage.PROCESS_DETAIL, e);
		}
	}

	/**
	 * 内部担保，申请人确认后需重新审批
	 * @param processInd
	 * @param filter
	 * @param substitutionVars
	 * @return
	 * @throws ServiceException
	 */
	public List<Mail> findVouchforByProcess(String processInd, String filter, Object[] substitutionVars) throws ServiceException {
		Validate.notNull(processInd, "流程实例编号为空");
		try {
			List<Mail> mailList = Lists.newArrayList();
			VWWorkObject object = peManager.vwGetTmsWorkObject(processInd);
			List<VWLogElement> les = peManager.vwEventLogWob(filter, substitutionVars);
			// 执行过的最后一步
			VWLogElement lastLe = null;
			if (les.isEmpty()) {
				return mailList;
			} else {
				lastLe = les.get(les.size() - 1);
			}
			// 操作最后一步
			// 得到下一步节点
			String nextStep = object.getStepName();
			// 流程实体类
			String entity = ProcessXmlUtil.getProcessAttribute("className", lastLe.getWorkflowName(), "entityClass");
			// 该节点是否需要判定人(重新申请，申请确认)
			String createrCheck = ProcessXmlUtil.findStepProperty("className", lastLe.getWorkflowName(), nextStep, "createrCheck");
			String copStep = ProcessXmlUtil.findStepProperty("className", lastLe.getWorkflowName(), nextStep, "copStep");
			// 发指定人邮件
			if (createrCheck != null && "true".equals(createrCheck)) {
				// 获取流程发起人
				String account = object.getOriginator();
				P p = this.findEmailByAccount(account);
				Mail mail = new Mail();
				if (p != null) {
					mail.setTelno(p.getCelno());
					mail.setEmail(p.getEmail());
					mail.setSubject(generationTitle(MailUtil.MailTypeEnum.Vouchfor, object.getWorkflowName(),
							processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), object.getStepName()));
					mail.setBody(generationContent(MailUtil.MailTypeEnum.Vouchfor, object.getWorkflowName(),
							processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), object.getStepName(),
							(String) lastLe.getFieldValue("F_Comment")));
				}
				mailList.add(mail);
				return mailList;
			}
			// 得到下步的队列
			String queueName = ProcessXmlUtil.findStepProperty("className", lastLe.getWorkflowName(), nextStep, "queueName");
			if (queueName != null && !"".equals(queueName)) {
				log.info("为静态队列！！！！！！！！！！！！！");
				// 得到下部的人
				List<P> userList = findUserByQueue(queueName);
				// 是否是集团,因为集团公司只有一个就不用判断集团公司到底是哪个
				if (copStep != null && !"".equals(copStep) && "true".equals(copStep)) {
					// 得到用户
					for (P u : userList) {
						Mail mail = new Mail();
						mail.setTelno(u.getCelno());
						mail.setEmail(u.getEmail());
						mail.setSubject(generationTitle(MailUtil.MailTypeEnum.Vouchfor, object.getWorkflowName(),
								processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), nextStep));
						mail.setBody(generationContent(MailUtil.MailTypeEnum.Vouchfor, object.getWorkflowName(),
								processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), nextStep,
								(String) lastLe.getFieldValue("F_Comment")));
						mailList.add(mail);
					}
					return mailList;
				}
				// 不是集团步骤，判断公司SAP
				// 查询该流程发起的公司SAP代码
				String sap = processWaitService.findCompanyByProcee(processInd, entity);
				for (P u : userList) {
					String adCount = loginService.getAdCountByPid(u.getId());
					if (adCount == null) {
						continue;
					}
					List<String> sapList = loginService.findCompanySapCode(adCount);
					if (!sapList.isEmpty() && sapList.contains(sap)) {
						Mail mail = new Mail();
						mail.setTelno(u.getCelno());
						mail.setEmail(u.getEmail());
						mail.setSubject(generationTitle(MailUtil.MailTypeEnum.Vouchfor, object.getWorkflowName(),
								processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), nextStep));
						mail.setBody(generationContent(MailUtil.MailTypeEnum.Vouchfor, object.getWorkflowName(),
								processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), nextStep,
								(String) lastLe.getFieldValue("F_Comment")));
						mailList.add(mail);
					}
				}
			} else {
				log.info("为动态队列！！！！！！！！！！！！！！！");
				// 内部担保流程，有的步骤为动态参与组，没有队列名，需特殊处理
				if (nextStep.indexOf("受保方") != -1) {
					// 受保方总经理审批
					// 设置工作流组TMS_Insured_Top_Manager，受保方总经理
					List<String> pernrList = loginService.getPernrByComIdOrPosName(innerGuaranteeService.findProcInstanceByProcInstId(processInd)
							.getCompany().getId(), "总经理");
					List<P> persons = loginService.getPByPUPernr(pernrList);
					for (P p : persons) {
						Mail mail = new Mail();
						mail.setTelno(p.getCelno());
						mail.setEmail(p.getEmail());
						mail.setSubject(generationTitle(MailUtil.MailTypeEnum.Vouchfor, object.getWorkflowName(),
								processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), nextStep));
						mail.setBody(generationContent(MailUtil.MailTypeEnum.Vouchfor, object.getWorkflowName(),
								processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), nextStep,
								(String) lastLe.getFieldValue("F_Comment")));
						mailList.add(mail);
					}

				} else if (nextStep.indexOf("担保方") != -1) {
					String name = "财务经理";
					if (nextStep.indexOf("总经理") != -1) {
						name = "总经理";
					}
					// 下一步节点为担保方（总经理审批）
					List<String> pernrList = loginService.getPernrByComIdOrPosName(innerGuaranteeService.findProcInstanceByProcInstId(processInd)
							.getSecuredCom().getId(), name);
					List<P> persons = loginService.getPByPUPernr(pernrList);
					for (P p : persons) {
						Mail mail = new Mail();
						mail.setTelno(p.getCelno());
						mail.setEmail(p.getEmail());
						mail.setSubject(generationTitle(MailUtil.MailTypeEnum.Vouchfor, object.getWorkflowName(),
								processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), nextStep));
						mail.setBody(generationContent(MailUtil.MailTypeEnum.Vouchfor, object.getWorkflowName(),
								processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), nextStep,
								(String) lastLe.getFieldValue("F_Comment")));
						mailList.add(mail);
					}
				}
			}
			return mailList;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * <p>Description: 根据日志查询组装邮件对象(终止流程)</p>
	 * @param filter
	 * @param substitutionVars
	 * @return
	 * @throws ServiceException
	 */
	public List<Mail> findAllEmailByTerProcess(String filter, Object[] substitutionVars) throws ServiceException {
		try {
			List<Mail> mailList = Lists.newArrayList();
			// 查询已经被执行流程步骤，通过记录的日志
			List<VWLogElement> les = peManager.vwEventLogWob(filter, substitutionVars);
			// 执行过的最后一步
			VWLogElement lastLe = null;
			if (les.isEmpty()) {
				return mailList;
			} else {
				lastLe = les.get(les.size() - 1);
			}
			List<String> distinctUsers = new ArrayList<String>();
			for (VWLogElement le : les) {
				// 剔除重复的用户
				String user = le.getUserName();
				if (distinctUsers.contains(user)) {
					continue;
				} else {
					distinctUsers.add(user);
				}

				Mail mail = new Mail();
				// 查询该账户是否在主数据存在记录
				P p = findEmailByAccount(le.getUserName());
				if (p != null) {
					mail.setTelno(p.getCelno());
					mail.setEmail(p.getEmail());
					mail.setSubject(generationTitle(MailUtil.MailTypeEnum.Terminal, le.getWorkClassName(),
							processUtilMapService.getTmsIdByFnId(le.getWorkFlowNumber()), lastLe.getStepName(), lastLe.getUserName(), null));
					mail.setBody(generationContent(MailUtil.MailTypeEnum.Terminal, le.getWorkClassName(),
							processUtilMapService.getTmsIdByFnId(le.getWorkFlowNumber()), lastLe.getStepName(), lastLe.getUserName(), null, null));
					mailList.add(mail);
				}
			}
			return mailList;
		} catch (Exception e) {
			throw new ServiceException(ExceptionMessage.PROCESS_DETAIL, e);
		}
	}

	/**
	 * <p>Description: 根据需要发送的步骤组装邮件对象</p>
	 * @param filter
	 * @param substitutionVars
	 * @param steps
	 * @param mailtype
	 * @return
	 * @throws ServiceException
	 */
	public List<Mail> findEmailBySteps(String filter, Object[] substitutionVars, List<String> steps, MailTypeEnum mailtype) throws ServiceException {
		try {
			List<Mail> mailList = Lists.newArrayList();
			// 查询已经被执行流程步骤，通过记录的日志
			List<VWLogElement> les = peManager.vwEventLogWob(filter, substitutionVars);
			// 执行过的最后一步
			VWLogElement lastLe = null;
			if (les.isEmpty()) {
				return mailList;
			} else {
				lastLe = les.get(les.size() - 1);
			}
			List<String> distinctUsers = new ArrayList<String>();
			for (VWLogElement le : les) {
				// 剔除重复的用户
				String user = le.getUserName();
				if (distinctUsers.contains(user)) {
					continue;
				} else {
					distinctUsers.add(user);
				}

				// 是否为需发送邮件的步骤
				if (!steps.contains(le.getStepName())) {
					continue;
				}

				Mail mail = new Mail();
				// 查询该账户是否在主数据存在记录
				P p = findEmailByAccount(le.getUserName());
				if (p != null) {
					mail.setTelno(p.getCelno());
					mail.setEmail(p.getEmail());
					mail.setSubject(generationTitle(mailtype, le.getWorkClassName(), processUtilMapService.getTmsIdByFnId(le.getWorkFlowNumber()),
							lastLe.getStepName(), lastLe.getUserName(), null));
					mail.setBody(generationContent(mailtype, le.getWorkClassName(), processUtilMapService.getTmsIdByFnId(le.getWorkFlowNumber()),
							lastLe.getStepName(), lastLe.getUserName(), null, (String) lastLe.getFieldValue("F_Comment")));
					mailList.add(mail);
				}
			}
			return mailList;
		} catch (Exception e) {
			throw new ServiceException(ExceptionMessage.PROCESS_DETAIL, e);
		}
	}

	/**
	 * 得到邮件主题
	 * @param mailTypeEnum
	 * @param workClassName
	 * @param processId
	 * @param doneStepName
	 * @param newStepName
	 * @return
	 */
	public String generationTitle(MailTypeEnum mailTypeEnum, String workClassName, String processId, String doneStepName, String doneUserName,
			String newStepName) {
		String proceeName = ProcessXmlUtil.getProcessAttribute("className", workClassName, "name");
		String subjectModel = MailUtil.MAIL_SUBJECT_MAP.get(mailTypeEnum);
		subjectModel = subjectModel.contains("pName") ? subjectModel.replace("pName", proceeName) : subjectModel;
		subjectModel = subjectModel.contains("pId") ? subjectModel.replace("pId", processId) : subjectModel;
		return subjectModel;
	}

	/**
	 * 得到邮件内容
	 * @param mailTypeEnum
	 * @param workClassName
	 * @param processId
	 * @param doneStepName
	 * @param newStepName
	 * @return
	 */
	public String generationContent(MailTypeEnum mailTypeEnum, String workClassName, String processId, String doneStepName, String doneUserName,
			String newStepName, String doneStepComment) {
		String proceeName = ProcessXmlUtil.getProcessAttribute("className", workClassName, "name");
		String contentModel = MailUtil.MAIL_CONTENT_MAP.get(mailTypeEnum);
		contentModel = contentModel.contains("pName") ? contentModel.replace("pName", proceeName) : contentModel;
		contentModel = contentModel.contains("pId") ? contentModel.replace("pId", processId) : contentModel;
		contentModel = contentModel.contains("pDone") ? contentModel.replace("pDone", doneStepName) : contentModel;
		contentModel = contentModel.contains("pUser") ? contentModel.replace("pUser", doneUserName) : contentModel;
		contentModel = contentModel.contains("pNext") ? contentModel.replaceAll("pNext", newStepName) : contentModel;
		contentModel = contentModel.contains("pComment") ? contentModel.replaceAll("pComment", doneStepComment) : contentModel;
		return contentModel;
	}

	/**
	 * <p>Description: 申请人发邮件</p>
	 * @param processInd
	 * @return
	 * @throws ServiceException
	 */
	public Mail findRequestMail(String processInd, String filter, Object[] substitutionVars) throws ServiceException {
		Validate.notNull(processInd, "流程实例编号不能能为空");
		try {
			Mail mail = new Mail();
			VWWorkObject object = peManager.vwGetTmsWorkObject(processInd);

			List<VWLogElement> les = peManager.vwEventLogWob(filter, substitutionVars);
			// 执行过的最后一步
			VWLogElement lastLe = null;
			if (les.isEmpty()) {
				return mail;
			} else {
				lastLe = les.get(les.size() - 1);
			}

			String requester = object.getOriginator();
			P p = this.findEmailByAccount(requester);
			if (p != null) {
				mail.setTelno(p.getCelno());
				mail.setEmail(p.getEmail());
				mail.setSubject(generationTitle(MailUtil.MailTypeEnum.Request, object.getWorkflowName(),
						processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), object.getStepName()));
				mail.setBody(generationContent(MailUtil.MailTypeEnum.Request, object.getWorkflowName(),
						processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), object.getStepName(),
						(String) lastLe.getFieldValue("F_Comment")));
			}

			return mail;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * <p>Description: 根据账户查询邮件地址
	 * @param account
	 * @return
	 * @throws ServiceException
	 */
	public P findEmailByAccount(String account) throws ServiceException {
		Validate.notNull(account, "用户账号不能能为空");
		try {
			StringBuilder bulder = new StringBuilder();
			bulder.append(" select p from P p where p.id in(select pu.pernr from PU pu where pu.id=?1) and p.defunctInd = 'N'");
			List<P> emailList = entityService.find(bulder.toString(), account);
			if (emailList.size() > 1) {
				throw new ServiceException("该账户不唯一存在多条记录");
			}
			return emailList.get(0);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * <p>Description: 下一步发邮件</p>
	 * @param processInd
	 * @param filter
	 * @param substitutionVars
	 * @return
	 * @throws ServiceException
	 */
	public List<Mail> findWaitPerMai(String processInd, String filter, Object[] substitutionVars) throws ServiceException {
		Validate.notNull(processInd, "流程实例编号为空");
		try {
			List<Mail> mailList = Lists.newArrayList();
			VWWorkObject object = peManager.vwGetTmsWorkObject(processInd);
			List<VWLogElement> les = peManager.vwEventLogWob(filter, substitutionVars);
			// 执行过的最后一步
			VWLogElement lastLe = null;
			if (les.isEmpty()) {
				return mailList;
			} else {
				lastLe = les.get(les.size() - 1);
			}
			// 操作最后一步
			String nextStep = object.getStepName();
			// 流程实体类
			String entity = ProcessXmlUtil.getProcessAttribute("className", lastLe.getWorkflowName(), "entityClass");
			// 该节点是否需要判定人(重新申请，申请确认)
			String createrCheck = ProcessXmlUtil.findStepProperty("className", lastLe.getWorkflowName(), nextStep, "createrCheck");
			String copStep = ProcessXmlUtil.findStepProperty("className", lastLe.getWorkflowName(), nextStep, "copStep");
			// 发指定人邮件
			if (createrCheck != null && "true".equals(createrCheck)) {
				// 获取流程发起人
				String account = object.getOriginator();
				P p = this.findEmailByAccount(account);
				Mail mail = new Mail();
				if (p != null) {
					mail.setTelno(p.getCelno());
					mail.setEmail(p.getEmail());
					mail.setSubject(generationTitle(MailUtil.MailTypeEnum.Next, object.getWorkflowName(),
							processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), object.getStepName()));
					mail.setBody(generationContent(MailUtil.MailTypeEnum.Next, object.getWorkflowName(),
							processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), object.getStepName(),
							(String) lastLe.getFieldValue("F_Comment")));
				}
				mailList.add(mail);
				return mailList;
			}
			// 得到下步的队列
			String queueName = ProcessXmlUtil.findStepProperty("className", lastLe.getWorkflowName(), nextStep, "queueName");
			log.info("得到下步的队列:" + queueName);
			// 内部担保流程，有的步骤为动态参与组，没有队列名，需特殊处理
			if (queueName != null && !"".equals(queueName)) {
				log.info("为静态队列！！！！！！！！！！！！！！！！！！！");
				// 得到下部的人
				List<P> userList = findUserByQueue(queueName);
				// 是否是集团,因为集团公司只有一个就不用判断集团公司到底是哪个
				if (copStep != null && !"".equals(copStep) && "true".equals(copStep)) {
					// 得到用户
					for (P u : userList) {
						Mail mail = new Mail();
						mail.setTelno(u.getCelno());
						mail.setEmail(u.getEmail());
						mail.setSubject(generationTitle(MailUtil.MailTypeEnum.Next, object.getWorkflowName(),
								processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), nextStep));
						mail.setBody(generationContent(MailUtil.MailTypeEnum.Next, object.getWorkflowName(),
								processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), nextStep,
								(String) lastLe.getFieldValue("F_Comment")));
						mailList.add(mail);
					}
					return mailList;
				}
				// 不是集团步骤，判断公司SAP
				// 查询该流程发起的公司SAP代码
				String sap = processWaitService.findCompanyByProcee(processInd, entity);
				log.info("查询该流程发起的公司SAP代码:" + sap);
				for (P u : userList) {
					String adCount = loginService.getAdCountByPid(u.getId());
					if (adCount == null) {
						continue;
					}
					List<String> sapList = loginService.findCompanySapCode(adCount);
					if (!sapList.isEmpty() && sapList.contains(sap)) {
						Mail mail = new Mail();
						mail.setTelno(u.getCelno());
						mail.setEmail(u.getEmail());
						mail.setSubject(generationTitle(MailUtil.MailTypeEnum.Next, object.getWorkflowName(),
								processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), nextStep));
						mail.setBody(generationContent(MailUtil.MailTypeEnum.Next, object.getWorkflowName(),
								processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), nextStep,
								(String) lastLe.getFieldValue("F_Comment")));
						mailList.add(mail);
					}
				}
			} else {
				// 内部担保流程，有的步骤为动态参与组，没有队列名，需特殊处理
				log.info("为动态队列！！！！！！！！！！！");
				if (nextStep.indexOf("受保方") != -1) {
					// 受保方总经理审批
					// 设置工作流组TMS_Insured_Top_Manager，受保方总经理
					List<String> pernrList = loginService.getPernrByComIdOrPosName(innerGuaranteeService.findProcInstanceByProcInstId(processInd)
							.getCompany().getId(), "总经理");
					List<P> persons = loginService.getPByPUPernr(pernrList);
					for (P p : persons) {
						Mail mail = new Mail();
						mail.setTelno(p.getCelno());
						mail.setEmail(p.getEmail());
						mail.setSubject(generationTitle(MailUtil.MailTypeEnum.Next, object.getWorkflowName(),
								processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), nextStep));
						mail.setBody(generationContent(MailUtil.MailTypeEnum.Next, object.getWorkflowName(),
								processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), nextStep,
								(String) lastLe.getFieldValue("F_Comment")));
						mailList.add(mail);
					}

				} else if (nextStep.indexOf("担保方") != -1) {
					String name = "财务经理";
					if (nextStep.indexOf("总经理") != -1) {
						name = "总经理";
					}
					// 下一步节点为担保方（总经理审批）
					List<String> pernrList = loginService.getPernrByComIdOrPosName(innerGuaranteeService.findProcInstanceByProcInstId(processInd)
							.getSecuredCom().getId(), name);
					List<P> persons = loginService.getPByPUPernr(pernrList);
					for (P p : persons) {
						Mail mail = new Mail();
						// mail.setPernr(u.getBukrs());
						mail.setTelno(p.getCelno());
						mail.setEmail(p.getEmail());
						mail.setSubject(generationTitle(MailUtil.MailTypeEnum.Next, object.getWorkflowName(),
								processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), nextStep));
						mail.setBody(generationContent(MailUtil.MailTypeEnum.Next, object.getWorkflowName(),
								processUtilMapService.getTmsIdByFnId(processInd), lastLe.getStepName(), lastLe.getUserName(), nextStep,
								(String) lastLe.getFieldValue("F_Comment")));
						mailList.add(mail);
					}
				}
			}
			return mailList;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * <p>Description: 根据队列得到拥有该队列的所有用户</p>
	 * @param queueName
	 * @return
	 * @throws ServiceException
	 */
	public List<P> findUserByQueue(String queueName) throws ServiceException {
		try {
			List<Long> roleIdList = findRoleByQueue(queueName);
			// 根据角色得到用户
			StringBuilder bulder = new StringBuilder();
			bulder.append("select distinct ur.usermstr.adAccount from Userrole ur where ur.rolemstr.id in(?1) and ur.defunctInd = 'N'");
			List<String> userAdList = entityService.find(bulder.toString(), roleIdList);
			// 根据用户得到P的Id
			StringBuilder puJpql = new StringBuilder();
			puJpql.append("select distinct pu.pernr from PU pu where pu.defunctInd='N' and pu.id in(?1)");
			List<String> pId = entityService.find(puJpql.toString(), userAdList);
			// 根据P的Id得到P对象
			StringBuilder pJpql = new StringBuilder();
			pJpql.append("select distinct p from P p where p.id in(?1)");
			return this.entityService.find(pJpql.toString(), pId);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * <p>Description: 根据队列名称查询拥有该队列的角色</p>
	 * @param queueName
	 * @return
	 * @throws ServiceException
	 */
	private List<Long> findRoleByQueue(String queueName) throws ServiceException {
		try {
			List<Long> newRoleList = Lists.newArrayList();
			StringBuilder bulder = new StringBuilder();
			bulder.append("select r from Rolemstr r where r.defunctInd = 'N'");
			List<Rolemstr> roleList = this.entityService.find(bulder.toString());
			out: for (Rolemstr r : roleList) {
				// 得到角色的所有队列
				if (r.getQueueName() != null && !"".equals(r.getQueueName())) {
					// 将角色的队列筛分成数组
					String[] queueArray = r.getQueueName().split(",");
					if (queueArray != null) {
						// 判断该角色的队列数组是否包含该队列
						for (String queue : queueArray) {
							if (queueName.equals(queue)) {
								newRoleList.add(r.getId());
								continue out;
							}
						}
					}
				}
			}
			return newRoleList;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	
	/**
	 * 
	 * <p>Description: 获取付款接口文件生成时，发送给管理员的邮件信息</p>
	 * @param processId
	 * @param processDefineName
	 * @param errorType
	 * @param extraInfo 额外的信息
	 * @return
	 */
	public List<Mail> getErrorInfo2AdminEmail(String processId,String processDefineName,String errorType,String extraInfo) {
        List<Mail> mails = new ArrayList<Mail>();
        StringBuffer subject = new StringBuffer();
        StringBuffer body = new StringBuffer();
        //ftp连接不上
        if("ftpNetError".equals(errorType)){
            subject.append("TMS付款接口文件生成异常-[").append(processDefineName).append("]-[").append(processId).append("]");
            body.append("流程：[").append(processDefineName).append("]-[").append(processId).append("],");
            body.append("付款接口文件未成功上传到ftp服务器，请尽快人工处理,可能原因是连接不到ftp服务器。").append(extraInfo);
        }
        //加密文件中，支付金额丢失
        if("losePayAmount".equals(errorType)){
            subject.append("TMS付款接口文件生成异常-[").append(processDefineName).append("]-[").append(processId).append("]");
            body.append("流程：[").append(processDefineName).append("]-[").append(processId).append("],");
            body.append("付款接口文件中，缺少付款金额和付款日期两个字段的数据，请人工处理。").append(extraInfo);
        }
        
        String[] mailsTo = SysCfg.getStrConfig("ftp.error.notify.mails").split(";");
        
        for(int i = 0; i< mailsTo.length;i++){
            Mail mail = new Mail();
            mail.setTelno("");
            mail.setEmail(mailsTo[i]);
            mail.setSubject(subject.toString());
            mail.setBody(body.toString());
            mails.add(mail);
        }

        return mails;
    }
	
	public List<Mail> getCashPoolPayEmail(ProcTMSStatus tmsStatus) {
		String sql = "SELECT ID, PROC_NAME, PROC_INST_ID, CREATED_BY, COMPANY_NAME, AMOUNT, NACHN, EMAIL, TELNO FROM VW_CASHPOOL_PAY_EMAIL WHERE ID = " + tmsStatus.getPayId();
		
		List<Object[]> list = entityService.getEm().createNativeQuery(sql).getResultList();
		
		List<Mail> retList = new ArrayList<Mail>();
		
		boolean flag = true;
		if("5".equals(tmsStatus.getTmsStatus())) {
			flag = false;
		}
		
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DecimalFormat df = new DecimalFormat("0.00");
		
		for (Object[] obj : list) {
			String procName = obj[1].toString();
			String procInstId = obj[2].toString();
			String companyName = obj[4].toString();
			Double amount = Double.parseDouble(obj[5].toString()); 
			String userName = obj[6].toString();
			String email = obj[7].toString();
			String telno = obj[8].toString();
			
			String processId = processUtilMapService.getTmsIdByFnId(procInstId);
			String detail = "";
			if(flag) {
				detail = "TMS付款成功";
			}else {
				detail = "TMS付款失败";
			}
			
			Mail m = new Mail();
			m.setTelno(telno);
			m.setEmail(email);
			
			String subjectModel = MailUtil.MAIL_SUBJECT_MAP.get(MailUtil.MailTypeEnum.payNotice);
			subjectModel = subjectModel.contains("pName") ? subjectModel.replace("pName", procName) : subjectModel;
			subjectModel = subjectModel.contains("pId") ? subjectModel.replace("pId", processId) : subjectModel;
			subjectModel = subjectModel.contains("pDetail") ? subjectModel.replace("pDetail", detail) : subjectModel;
			
			StringBuilder bodyModel = new StringBuilder("<strong>流程发起人:</strong> ").append(userName).append("<br>");
			bodyModel.append("<strong>公司名称：</strong> ").append(companyName).append("<br>");
			bodyModel.append("<strong>应付金额：</strong> ").append(df.format(amount*10000)).append("元<br>");
			bodyModel.append("<strong>实付金额：</strong> ").append(df.format(tmsStatus.getTmsAmount())).append("元<br>");
			if(flag) {
				bodyModel.append("<font color='green'>");
			}else {
				bodyModel.append("<font color='red'>");
			}
			bodyModel.append("<strong>付款状态：</strong>").append(detail).append("</font><br>");
			bodyModel.append("<strong>付款描述：</strong> ").append(tmsStatus.getPayDetail()).append("<br>");
			bodyModel.append("<strong>实际付款日期：</strong> ").append(sdf.format(tmsStatus.getPayDate())).append("<br>");
			bodyModel.append("<strong>本次付款方式：</strong> ").append("SUNGARD付款").append("<br>");
			bodyModel.append("<strong>流程名称：</strong> ").append(procName).append("<br>");
			bodyModel.append("<strong>流程实例编号：</strong> ").append(processId).append("<br>");
			bodyModel.append("<strong>TMS参考号：</strong> ").append(tmsStatus.getTmsRefNumber()).append("<br>");
			bodyModel.append("<strong>系统网址：</strong> ").append("http://tmsbpm.wilmar.cn/tms").append("<br>");
			
			m.setSubject(subjectModel);
			m.setBody(bodyModel.toString());
			
			retList.add(m);
		}
		
		return retList;
	}
}
