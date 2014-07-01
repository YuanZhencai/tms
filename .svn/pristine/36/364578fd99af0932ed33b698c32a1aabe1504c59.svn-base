package com.wcs.tms.service.process.payproject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.DateUtil;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.common.model.P;
import com.wcs.tms.mail.Mail;
import com.wcs.tms.mail.MailService;
import com.wcs.tms.mail.MailUtil;
import com.wcs.tms.mail.SendMailService;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.CompanyAccount;
import com.wcs.tms.model.ProcPayProject;
import com.wcs.tms.model.ProcPayProjectDetails;
import com.wcs.tms.model.ProcTMSStatus;
import com.wcs.tms.service.process.common.FtpUploadService;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.TmsStatusService;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.service.system.company.CompanyAccountServer;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.PaymentVo;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWWorkObjectNumber;

/** 
* <p>Project: tms</p> 
* <p>Title: 付工程款股利及归还股东借款流程service</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:liaowei@wcs-global.com">廖伟</a> 
*/
@Stateless
public class PayProjectService implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(PayProjectService.class);
	@Inject
	PEManager peManager;
	@Inject
	LoginService loginService;
	@Inject
	EntityService entityService;
	@Inject
	MailService mailService;
	@Inject
	SendMailService sendMailService;
	@Inject
	CompanyAccountServer companyAccountServer;
	@Inject
	BankService bankService;
	// 9.11
	@Inject
	ProcessUtilMapService processUtilMapService;
	@Inject
	FtpUploadService ftpUploadService;
	@EJB
	private TmsStatusService tmsStatusService;
	List<CompanyAccount> ca = new ArrayList<CompanyAccount>();

	/**
	 * 得到与公司有业务关系的银行
	 * @param companyId
	 * @return
	 */
	public List<SelectItem> getCompanyBankSelect(Long companyId) {
		List<SelectItem> bankSelect = new ArrayList<SelectItem>();
		List<CompanyAccount> ca = this.getComanyBank(companyId);
		boolean iftrue = true;
		if (iftrue) {
			log.info("去重复银行");
			for (CompanyAccount c : ca) {
				// 去重复银行
				Boolean has = false;
				for (SelectItem si : bankSelect) {
					if (c.getAccountDesc().equals(si.getValue())) {
						has = true;
					}
				}
				if (!has) {
					bankSelect.add(new SelectItem(c.getAccountDesc(), c.getAccountDesc()));
				}
				log.info("cao" + c.getAccountDesc());
			}
		} else {
			log.info("不去重复银行");
			for (CompanyAccount c : ca) {
				// 不去重复银行
				bankSelect.add(new SelectItem(c.getCounterpartyCode(), c.getAccountDesc()));
				log.info("AccountDesc:" + c.getAccountDesc());
				log.info("CounterpartyCode:" + c.getCounterpartyCode());
			}
		}
		log.info("bankSelect.size():" + bankSelect.size());
		return bankSelect;
	}

	/**
	 * 得到公司银行关系列表
	 * @param companyId
	 * @return
	 */
	public List<CompanyAccount> getComanyBank(Long companyId) {
		ca = companyAccountServer.findCompanyAccountList(companyId);
		return ca;
	}

	/**
	 * 得到公司银行里面的帐号
	 * @param companyId
	 * @param accountDesc
	 * @return
	 */
	public List<SelectItem> getCompanyAccountSelect(Long companyId, String accountDesc) {
		List<SelectItem> accountSelect = new ArrayList<SelectItem>();
		log.info("执行获得公司银行帐号方法");
		accountSelect = companyAccountServer.findBankSelect(companyId, accountDesc);
		log.info("得到公司银行里面的帐号" + accountSelect);
		return accountSelect;
	}

	/**
	 * 流程创建保存
	 * @param procPayProject
	 * @return
	 * @throws ServiceException
	 */
	public String createProcInstance(ProcPayProject procPayProject) throws ServiceException {

		String procInstId = "";
		try {
			// PE流程创建
			// 流程实例ID，并保存
			procInstId = this.vwApplication(procPayProject);
			procPayProject.setProcInstId(procInstId);
			log.info("流程实例ID为：" + procPayProject.getProcInstId());
			procPayProject.setCreatedBy(loginService.getCurrentUserName());
			log.info("创建者是：" + procPayProject.getCreatedBy());
			// 保存到数据库中
			log.info("保存到数据库中！！");
			entityService.create(procPayProject);
			// 生成流程实例编号映射9.11
			processUtilMapService.generateProcessMap(procInstId, "BPM_RA_009", procPayProject.getCompany());
		} catch (Exception e) {
			log.error("createProcInstance方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.INSTANCE_CREATE, e);
		}
		return procInstId;
	}

	/**
	 * PE流程创建
	 * @param procPayProject
	 * @return
	 * @throws ServiceException
	 */
	public String vwApplication(ProcPayProject procPayProject) throws ServiceException {
		String workClassName = ProcessXmlUtil.getProcessAttribute("id", "PayProject", "className");
		String workflowNumber = "";
		if (peManager.checkWorkClassName(workClassName)) {
			try {
				// 验证流程类名
				workflowNumber = peManager.vwCreateInstance(workClassName, "tms_subject1");
				HashMap<String, Object> step1para = new HashMap<String, Object>();
				peManager.vwLauchStep("TMS_Requester", step1para, workflowNumber);
			} catch (Exception e) {
			    log.error("vwApplication方法 错误信息：" + e.getMessage());
				throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
			}
		} else {
			throw new ServiceException(ExceptionMessage.FN_NO_CLASS);
		}
		return workflowNumber;
	}

	/**
	 * 流程实例Id得到其他特殊借款实例
	 * @param procInstId
	 * @return
	 */
	public ProcPayProject findProcInstanceByProcInstId(String procInstId) {
		StringBuilder jpql = new StringBuilder("select es from ProcPayProject es join fetch es.company where es.defunctInd = 'N'");
		jpql.append(" and es.procInstId='" + procInstId + "'");
		return entityService.findUnique(jpql.toString());
	}

	/**
	 * 查询流程详细
	 * @param procInstId
	 * @return
	 */
	public List<ProcessDetailVo> getProcessDetail(String procInstId) throws ServiceException {
		List<ProcessDetailVo> detailVos = new ArrayList<ProcessDetailVo>();
		String filter = "1=1 and F_WobNum = :wobNum and (F_EventType = :eventType1 or F_EventType = :eventType2)";
		Object[] substitutionVars = { new VWWorkObjectNumber(procInstId),
				ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd),
				ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal) };
		try {
			List<VWLogElement> les = new ArrayList<VWLogElement>();
			les = peManager.vwEventLogWob(filter, substitutionVars);
			for (VWLogElement le : les) {
				ProcessDetailVo detailVo = new ProcessDetailVo();
				if (ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal).equals(le.getEventType())) {
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
		    log.error("getProcessDetail方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return detailVos;
	}

	/**
	 * 查询付款过程详细
	 * @param procInstId
	 */
	public List<PaymentVo> getPayDetail(String procInstId) throws ServiceException {
		List<PaymentVo> payDetailVos = new ArrayList<PaymentVo>();
		try {
			// 查询流程实例编号对应的流程ID
			StringBuilder jpql = new StringBuilder("select es.id from ProcPayProject es join fetch es.company where es.defunctInd = 'N'");
			jpql.append(" and es.procInstId='" + procInstId + "'");
			log.info(jpql);
			log.info("!!!!!!!!!!!!!!!!id为：" + entityService.findUnique(jpql.toString()));
			// 根据明细表中的字段
			StringBuilder jpql2 = new StringBuilder(
					"select esd from ProcPayProjectDetails esd left  join fetch esd.procPayProject where esd.defunctInd = 'N'");
			jpql2.append(" and esd.procPayProject.id=" + entityService.findUnique(jpql.toString()) + "");
			log.info(jpql2);
			List<ProcPayProjectDetails> amo = entityService.find(jpql2.toString());
			int i = 1;
			for (ProcPayProjectDetails d : amo) {
				PaymentVo payDetailVo = new PaymentVo();
				payDetailVo.setSerialNumber(i);
				payDetailVo.setPayDatetime(d.getCreatedDatetime());
				payDetailVo.setPayWay(d.getPayWay());
				payDetailVo.setPayFundsTotal(d.getPayFundsTotal());
				payDetailVo.setPayer(d.getCreatedBy());
				i++;
				payDetailVos.add(payDetailVo);
			}
		} catch (Exception e) {
		    log.error("getPayDetail方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return payDetailVos;
	}

	/**
	 * 审批保存
	 * @param procPayProject
	 * @param approveStatus
	 * @param stepName
	 * @throws ServiceException
	 */
	public void doApprove(ProcPayProject procPayProject, String approveStatus, String stepName) throws ServiceException {
		try {
			// 获得当前流程节点
			String queueName = ProcessXmlUtil.findStepProperty("id", "PayProject", stepName, "queueName");
			// 加入流程备注抬头
			Map<String, Object> map = new HashMap<String, Object>();
			String memoTitle = "";
			if ("Y".equals(approveStatus)) {
				memoTitle = ProcessXmlUtil.findStepProperty("id", "PayProject", stepName, "passMemo");
				map.put(queueName + "_Pass", true);
			} else {
				memoTitle = ProcessXmlUtil.findStepProperty("id", "PayProject", stepName, "nopassMemo");
				map.put(queueName + "_Pass", false);
			}
			// 是否为转款
			if (!"T".equals(procPayProject.getLoanIden())) {
				map.put(queueName + "_Loan", true);
			} else {
				map.put(queueName + "_Loan", false);
			}
			if (memoTitle != null) {
				procPayProject.setPeMemo(memoTitle + procPayProject.getPeMemo());
				log.info("备注抬头：" + memoTitle + procPayProject.getPeMemo());
			}
			// 先执行更新操作
			log.info("执行跟新前！！！！");
			entityService.update(procPayProject);
			log.info("FN流程运行");
			peManager.vwDisposeTask(procPayProject.getProcInstId(), map, procPayProject.getPeMemo());
		} catch (Exception e) {
		    log.error("doApprove方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}

	/**
	 * 确认保存(付款或者终止付款)
	 * @param procPayProject
	 * @param procPayProjectDetails
	 * @param conf
	 * @param payWay
	 * @param stepName
	 * @throws ServiceException
	 */
	public void doConfirm(ProcPayProject procPayProject, ProcPayProjectDetails procPayProjectDetails, Boolean conf, Boolean payWay, String stepName)
			throws ServiceException {
		// conf:付款标识符-true:付款，false:终止付款
		try {
			// 加入流程备注抬头
			String memoTitle = "";
			if (conf) {
				memoTitle = ProcessXmlUtil.findStepProperty("id", "PayProject", stepName, "passMemo");
			} else {
				memoTitle = ProcessXmlUtil.findStepProperty("id", "PayProject", stepName, "nopassMemo");
			}
			// 邮件知会需要信息（add on 2013-4-10）
			String remark = procPayProject.getPeMemo();
			if (memoTitle != null) {
				procPayProject.setPeMemo(memoTitle + procPayProject.getPeMemo());
			}
			// payWay true:SUNGARD付款 false:网银付款
			if (payWay) {
				procPayProjectDetails.setPayWay("S");
			} else {
				procPayProjectDetails.setPayWay("O");
			}
			if (conf) {
				log.info("执行付款任务1111111111111111");
				// 是否若为true则为足额支付,false为终止付款
				procPayProjectDetails.setPayDatetime(new Date());
				procPayProjectDetails.setCreatedBy(loginService.getCurrentUserName());
				entityService.create(procPayProjectDetails);
				log.info("存入数据库成功！！！！！");
				Boolean ifAll = ifEnoughPay(procPayProject);
				log.info("验证完毕：验证结果是：" + ifAll);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("_Enough", ifAll);
				Boolean isSungard = false; 
				if ("S".equals(procPayProjectDetails.getPayWay())) {
					isSungard = true;
					map.put("TMS_Is_Sungard", true);
				} else {
					isSungard = false;
					map.put("TMS_Is_Sungard", false);
				}
				peManager.vwDisposeTask(procPayProject.getProcInstId(), map, procPayProject.getPeMemo());
				//调用生成ftp文件方法 add on 2013-8-1 by yan
				String className = ProcessXmlUtil.getProcessAttribute("id", "PayProject","className");
				String bpmId = ftpUploadService.tmsFtpUploadFile(procPayProject.getProcInstId(),
						className, procPayProjectDetails.getCreatedDatetime(), isSungard);
				if(payWay) {
					ProcTMSStatus tmsStatus = new ProcTMSStatus();
					tmsStatus.setPayId(procPayProjectDetails.getId());
					tmsStatus.setBpmId(bpmId);
					tmsStatus.setTmsStatus(TmsStatusService.STATUS_NOIMPORT);
					//增加TMS回传表
					tmsStatusService.saveTmsStatus(tmsStatus);
				}else {
					// 邮件知会申请人
					mailRequester(procPayProject, procPayProjectDetails, conf, stepName,remark);
				}
			} else {
				log.info("执行终止付款任务222222222222222222");
				conf = true;
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("_Enough", conf);
				map.put("TMS_Is_Sungard", false);
				log.info("stop的值是：" + conf);
				peManager.vwDisposeTask(procPayProject.getProcInstId(), map, procPayProject.getPeMemo());
				// 邮件知会申请人
				conf = false;
				mailRequester(procPayProject, procPayProjectDetails, conf, stepName,remark);
			}
		} catch (Exception e) {
		    log.error("doConfirm方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}

	/**
	 * 验证是否足额支付
	 * @param procPayProject
	 * @return
	 */
	public Boolean ifEnoughPay(ProcPayProject procPayProject) {
		Boolean enough = false;
		Double amount = needPay(procPayProject);
		log.info("获得需要支付的总金额：" + amount);
		Double payAmount = payedAccount(procPayProject);
		log.info("获得已经支付的总金额" + payAmount);
		if (amount > payAmount) {
			log.info(enough + "!!!!!!未足额支付!!!!!!");
			return enough;
		}
		return true;
	}

	/**
	 * 获得需要支付的金额
	 * @param procPayProject
	 * @return
	 */
	public Double needPay(ProcPayProject procPayProject) {
		StringBuilder jpql = new StringBuilder("select es.amount from ProcPayProject es  where es.defunctInd = 'N'");
		jpql.append(" and es.procInstId='" + procPayProject.getProcInstId() + "'");
		Double amount = entityService.findUnique(jpql.toString());
		log.info("需要支付总金额是：" + amount);
		return amount;
	}

	/**
	 * 获得已支付的总金额
	 * @param procPayProject
	 * @return
	 */
	public Double payedAccount(ProcPayProject procPayProject) {
		Double payAmount = 0D;
		StringBuilder jpql = new StringBuilder(
				"select esd from ProcPayProjectDetails esd left  join fetch esd.procPayProject where esd.defunctInd = 'N'");
		jpql.append(" and esd.procPayProject.id=" + procPayProject.getId() + "");
		log.info("jpql:" + jpql);
		List<ProcPayProjectDetails> amo = entityService.find(jpql.toString());
		log.info("amo的队列为：" + amo);
		if (!amo.isEmpty()) {
			for (ProcPayProjectDetails d : amo) {
				payAmount += d.getPayFundsTotal();
			}
		}
		log.info("已支付的总金额是：" + payAmount);
		return payAmount;
	}

	/**
	 * 邮件付款方式选择
	 * @param procPayProjectDetails
	 * @return
	 */
	public String selectPayWay(ProcPayProjectDetails procPayProjectDetails) {
		if ("S".equals(procPayProjectDetails.getPayWay())) {
			return "SUNGARD付款";
		} else {
			return "网银支付";
		}
	}

	/**
	 * 邮件通知申请人
	 * @param procPayProject
	 * @param procPayProjectDetails
	 * @param conf
	 * @param stepName
	 * @param remark 
	 */
	private void mailRequester(ProcPayProject procPayProject, ProcPayProjectDetails procPayProjectDetails, Boolean conf, String stepName, String remark) {
		log.info("邮件知会申请人！！！！！！！！！！！");
		List<Mail> mailList = new ArrayList<Mail>();
		StringBuilder bussMailBody = new StringBuilder("您申请的");
		DecimalFormat df = new DecimalFormat("0.00");
		if (conf && ("集团资金计划员付款".equals(stepName)) && (ifEnoughPay(procPayProject) == false)) {
			// 邮件业务内容
			// 您申请的***流程,编号为[流程实例ID],集团资金计划已经通过***方式下拨***元,请知悉,谢谢!"
			bussMailBody.append("“付工程款股利及归还股东借款流程”，编号为：【" + processUtilMapService.getTmsIdByFnId(procPayProject.getProcInstId())
					+ "】，付款金额为：" + df.format(procPayProjectDetails.getPayFundsTotal()) +"万元，各节点已经完成审批，并生成付款指令，资金调度同事即将处理！预计付款日期为：" + 
					DateUtil.dateToStrShort(procPayProjectDetails.getPayDatetime()) + "，付款方式为："
					+ this.selectPayWay(procPayProjectDetails) );
			bussMailBody.append("，审批备注："+remark+"。请知悉，谢谢！");
			log.info(bussMailBody);
		} else if (conf && ("集团资金计划员付款".equals(stepName)) && (ifEnoughPay(procPayProject) == true)) {
			bussMailBody.append("“付工程款股利及归还股东借款流程”，编号为：【" + processUtilMapService.getTmsIdByFnId(procPayProject.getProcInstId())
					+ "】，付款金额为：" + df.format(procPayProjectDetails.getPayFundsTotal()) +"万元，各节点已经完成审批，并生成付款指令，资金调度同事即将处理！预计付款日期为：" + 
					DateUtil.dateToStrShort(procPayProjectDetails.getPayDatetime()) + "，付款方式为："
					+ this.selectPayWay(procPayProjectDetails) );
			bussMailBody.append("，审批备注："+remark+"。此流程所有金额均已完成审批！请知悉，谢谢！");
		} else {
			bussMailBody.append("“付工程款股利及归还股东借款流程”，编号为：【" + processUtilMapService.getTmsIdByFnId(procPayProject.getProcInstId()) + "】已经终止付款！请知悉，谢谢！");
			log.info(bussMailBody);
		}
		StringBuilder jpql = new StringBuilder("select distinct pu.pernr from PU pu where pu.defunctInd='N' ");
		jpql.append(" and pu.id='" + procPayProjectDetails.getProcPayProject().getCreatedBy() + "'");
		log.info("jpql语句为：" + jpql);
		String pid = entityService.findUnique(jpql.toString());
		log.info("pid为：" + pid);
		P p = entityService.find(P.class, pid);
		log.info("p:" + p);
		log.info(p.getEmail() + p.getNachn() + p.getName2());
		if (p != null) {
			Mail m = new Mail();
			m.setTelno(p.getCelno());
			m.setEmail(p.getEmail());
			m.setSubject(mailService.generationTitle(MailUtil.MailTypeEnum.Notice, "TMS_PayProject",
					processUtilMapService.getTmsIdByFnId(procPayProject.getProcInstId()), stepName, loginService.getCurrentUserName(), null));
			m.setBody(mailService.generationContent(MailUtil.MailTypeEnum.Notice, "TMS_PayProject",
					processUtilMapService.getTmsIdByFnId(procPayProject.getProcInstId()), stepName, loginService.getCurrentUserName(), null, null)
					+ bussMailBody.toString());
			mailList.add(m);
		}
		log.info("邮件内容：" + mailList.get(0).getBody());
		log.info("邮件主题：" + mailList.get(0).getSubject());
		log.info("邮箱：" + mailList.get(0).getEmail());
		log.info(mailList);
		log.info("邮件封装前！！！！！！！！！");
		// 邮件封装
		sendMailService.send(mailList);
	}
}