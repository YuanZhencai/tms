package com.wcs.tms.service.process.elseSpeLoan;

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
import com.wcs.tms.model.ProcElseSpeLoan;
import com.wcs.tms.model.ProcElseSpeLoanDetail;
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
* <p>Title: 其他特殊的借款流程service</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
@Stateless
public class ElseSpeLoanService implements Serializable {

	private static final long serialVersionUID = 1L;

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
	@Inject
	ProcessUtilMapService processUtilMapService;// 9.11
	@Inject
	FtpUploadService ftpUploadService;
	@EJB
	private TmsStatusService tmsStatusService;
	List<CompanyAccount> ca = new ArrayList<CompanyAccount>();
	
	private static final Log log = LogFactory.getLog(ElseSpeLoanService.class);

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
	 * @param procElseSpeLoan
	 * @return
	 * @throws ServiceException
	 */
	public String createProcInstance(ProcElseSpeLoan procElseSpeLoan) throws ServiceException {
		String procInstId = "";
		try {
			// PE流程创建
			// 流程实例ID，并保存
			procInstId = this.vwApplication(procElseSpeLoan);
			procElseSpeLoan.setProcInstId(procInstId);
			log.info("流程实例ID为：" + procElseSpeLoan.getProcInstId());
			procElseSpeLoan.setCreatedBy(loginService.getCurrentUserName());
			log.info("创建者是：" + procElseSpeLoan.getCreatedBy());
			// 保存到数据库中
			log.info("保存到数据库中！！");
			entityService.create(procElseSpeLoan);
			// 生成流程实例编号映射9.11
			processUtilMapService.generateProcessMap(procInstId, "BPM_RA_016", procElseSpeLoan.getCompany());
		} catch (Exception e) {
			log.error("createProcInstance方法 流程创建保存 出现异常：",e);
			throw new ServiceException(ExceptionMessage.INSTANCE_CREATE, e);
		}
		return procInstId;
	}

	/**
	 * PE流程创建
	 * @param procElseSpeLoan
	 * @return
	 * @throws ServiceException
	 */
	public String vwApplication(ProcElseSpeLoan procElseSpeLoan) throws ServiceException {
		String workClassName = ProcessXmlUtil.getProcessAttribute("id", "ElseSpeLoan", "className");
		String workflowNumber = "";
		if (peManager.checkWorkClassName(workClassName)) {
			try {
				// 验证流程类名
				workflowNumber = peManager.vwCreateInstance(workClassName, "tms_subject1");
				HashMap<String, Object> step1para = new HashMap<String, Object>();
				peManager.vwLauchStep("TMS_Requester", step1para, workflowNumber);
			} catch (Exception e) {
				log.error("vwApplication方法 PE流程创建 出现异常：",e);
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
	public ProcElseSpeLoan findProcInstanceByProcInstId(String procInstId) {
		StringBuilder jpql = new StringBuilder("select es from ProcElseSpeLoan es join fetch es.company where es.defunctInd = 'N'");
		jpql.append(" and es.procInstId='" + procInstId + "'");
		return entityService.findUnique(jpql.toString());
	}

	/**
	 * 查询流程详细
	 * @param procInstId
	 * @return
	 * @throws ServiceException
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
			log.error("getProcessDetail方法 查询流程详细 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return detailVos;
	}

	/**
	 * 查询付款过程详细
	 * @param procInstId
	 * @return
	 * @throws ServiceException
	 */
	public List<PaymentVo> getPayDetail(String procInstId) throws ServiceException {
		List<PaymentVo> payDetailVos = new ArrayList<PaymentVo>();
		try {
			// 查询流程实例编号对应的流程ID
			StringBuilder jpql = new StringBuilder("select es.id from ProcElseSpeLoan es join fetch es.company where es.defunctInd = 'N'");
			jpql.append(" and es.procInstId='" + procInstId + "'");
			log.info(jpql);
			log.info("!!!!!!!!!!!!!!!!id为：" + entityService.findUnique(jpql.toString()));
			// 根据明细表中的字段
			StringBuilder jpql2 = new StringBuilder(
					"select esd from ProcElseSpeLoanDetail esd left  join fetch esd.procElseSpeLoan where esd.defunctInd = 'N'");
			jpql2.append(" and esd.procElseSpeLoan.id=" + entityService.findUnique(jpql.toString()) + "");
			log.info(jpql2);
			List<ProcElseSpeLoanDetail> amo = entityService.find(jpql2.toString());
			int i = 1;
			for (ProcElseSpeLoanDetail d : amo) {
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
			log.error("getPayDetail方法 查询付款过程详细 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return payDetailVos;
	}

	/**
	 * 审批保存
	 * @param procElseSpeLoan
	 * @param pass
	 * @param stepName
	 * @throws ServiceException
	 */
	public void doApprove(ProcElseSpeLoan procElseSpeLoan, Boolean pass, String stepName) throws ServiceException {
		try {
			// 加入流程备注抬头
			String memoTitle = "";
			if (pass) {
				memoTitle = ProcessXmlUtil.findStepProperty("id", "ElseSpeLoan", stepName, "passMemo");
			} else {
				memoTitle = ProcessXmlUtil.findStepProperty("id", "ElseSpeLoan", stepName, "nopassMemo");
			}
			if (memoTitle != null) {
				procElseSpeLoan.setPeMemo(memoTitle + procElseSpeLoan.getPeMemo());
			}
			// 先执行更新操作
			log.info("执行跟新前！！！！");
			entityService.update(procElseSpeLoan);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("_Pass", pass);
			peManager.vwDisposeTask(procElseSpeLoan.getProcInstId(), map, procElseSpeLoan.getPeMemo());
		} catch (Exception e) {
			log.error("doApprove方法 审批保存 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}

	/**
	 * 确认保存(付款或者终止付款)
	 * @param procElseSpeLoan
	 * @param procElseSpeLoanDetail
	 * @param conf
	 * @param payWay
	 * @param stepName
	 * @throws ServiceException
	 */
	public void doConfirm(ProcElseSpeLoan procElseSpeLoan, ProcElseSpeLoanDetail procElseSpeLoanDetail, Boolean conf, Boolean payWay, String stepName)
			throws ServiceException {
		// conf:付款标识符-true:付款，false:终止付款
		try {
			// 加入流程备注抬头
			String memoTitle = "";
			if (conf) {
				memoTitle = ProcessXmlUtil.findStepProperty("id", "ElseSpeLoan", stepName, "passMemo");
			} else {
				memoTitle = ProcessXmlUtil.findStepProperty("id", "ElseSpeLoan", stepName, "nopassMemo");
			}
			// 邮件知会需要信息（add on 2013-4-10）
			String remark = procElseSpeLoan.getPeMemo();
			if (memoTitle != null) {
				procElseSpeLoan.setPeMemo(memoTitle + procElseSpeLoan.getPeMemo());
			}
			// payWay true:SUNGARD付款 false:网银付款
			if (payWay) {
				procElseSpeLoanDetail.setPayWay("S");
			} else {
				procElseSpeLoanDetail.setPayWay("O");
			}
			if (conf) {
				log.info("执行付款任务1111111111111111");
				// 是否若为true则为足额支付,false为终止付款
				procElseSpeLoanDetail.setPayDatetime(new Date());
				procElseSpeLoanDetail.setCreatedBy(loginService.getCurrentUserName());
				entityService.create(procElseSpeLoanDetail);
				log.info("存入数据库成功！！！！！");
				Boolean ifAll = ifEnoughPay(procElseSpeLoan);
				log.info("验证完毕：验证结果是：" + ifAll);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("_Enough", ifAll);
				Boolean isSungard = false; 
				if ("S".equals(procElseSpeLoanDetail.getPayWay())) {
					isSungard = true;
					map.put("TMS_Is_Sungard", true);
				} else {
					isSungard = false;
					map.put("TMS_Is_Sungard", false);
				}
				peManager.vwDisposeTask(procElseSpeLoan.getProcInstId(), map, procElseSpeLoan.getPeMemo());
				//调用生成ftp文件方法 add on 2013-8-1 by yan
				String className = ProcessXmlUtil.getProcessAttribute("id", "ElseSpeLoan","className");
				String bpmId = ftpUploadService.tmsFtpUploadFile(procElseSpeLoan.getProcInstId(),
						className, procElseSpeLoanDetail.getCreatedDatetime(), isSungard);
				if(payWay) {
					ProcTMSStatus tmsStatus = new ProcTMSStatus();
					tmsStatus.setPayId(procElseSpeLoanDetail.getId());
					tmsStatus.setBpmId(bpmId);
					tmsStatus.setTmsStatus(TmsStatusService.STATUS_NOIMPORT);
					//增加TMS回传表
					tmsStatusService.saveTmsStatus(tmsStatus);
				}else {
					// 邮件知会申请人
					mailRequester(procElseSpeLoan, procElseSpeLoanDetail, conf, stepName,remark);
				}
			} else {
				log.info("执行终止付款任务222222222222222222");
				conf = true;
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("_Enough", conf);
				map.put("TMS_Is_Sungard", false);
				log.info("stop的值是：" + conf);
				peManager.vwDisposeTask(procElseSpeLoan.getProcInstId(), map, procElseSpeLoan.getPeMemo());
				// 邮件知会申请人
				conf = false;
				mailRequester(procElseSpeLoan, procElseSpeLoanDetail, conf, stepName,remark);
			}
		} catch (Exception e) {
			log.error("doConfirm方法 确认保存 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}

	/**
	 * 验证是否足额支付
	 * @param procElseSpeLoan
	 * @return
	 */
	public Boolean ifEnoughPay(ProcElseSpeLoan procElseSpeLoan) {
		Boolean enough = false;
		Double amount = needPay(procElseSpeLoan);
		log.info("获得需要支付的总金额：" + amount);
		Double payAmount = payedAccount(procElseSpeLoan);
		log.info("获得已经支付的总金额" + payAmount);
		if (amount > payAmount) {
			log.info(enough + "!!!!!!未足额支付!!!!!!");
			return enough;
		}
		return true;
	}

	/**
	 * 获得需要支付的金额
	 * @param procElseSpeLoan
	 * @return
	 */
	public Double needPay(ProcElseSpeLoan procElseSpeLoan) {
		StringBuilder jpql = new StringBuilder("select es.amount from ProcElseSpeLoan es  where es.defunctInd = 'N'");
		jpql.append(" and es.procInstId='" + procElseSpeLoan.getProcInstId() + "'");
		Double amount = entityService.findUnique(jpql.toString());
		log.info("需要支付总金额是：" + amount);
		return amount;
	}

	/**
	 * 获得已支付的总金额
	 * @param procElseSpeLoan
	 * @return
	 */
	public Double payedAccount(ProcElseSpeLoan procElseSpeLoan) {
		Double payAmount = 0D;
		StringBuilder jpql = new StringBuilder(
				"select esd from ProcElseSpeLoanDetail esd left  join fetch esd.procElseSpeLoan where esd.defunctInd = 'N'");
		jpql.append(" and esd.procElseSpeLoan.id=" + procElseSpeLoan.getId() + "");
		log.info(jpql);
		List<ProcElseSpeLoanDetail> amo = entityService.find(jpql.toString());
		log.info("amo的队列为：" + amo);
		for (ProcElseSpeLoanDetail d : amo) {
			payAmount += d.getPayFundsTotal();
		}
		log.info("已支付的总金额是：" + payAmount);
		return payAmount;
	}

	/**
	 * 邮件付款方式选择
	 * @param procElseSpeLoanDetail
	 * @return
	 */
	public String selectPayWay(ProcElseSpeLoanDetail procElseSpeLoanDetail) {
		if ("S".equals(procElseSpeLoanDetail.getPayWay())) {
			return "SUNGARD付款";
		} else {
			return "网银支付";
		}
	}

	/**
	 * 邮件通知申请人
	 * @param procElseSpeLoan
	 * @param procElseSpeLoanDetail
	 * @param conf
	 * @param stepName
	 * @param remark 
	 */
	private void mailRequester(ProcElseSpeLoan procElseSpeLoan, ProcElseSpeLoanDetail procElseSpeLoanDetail, Boolean conf, String stepName, String remark) {
		log.info("邮件知会申请人！！！！！！！！！！！");
		List<Mail> mailList = new ArrayList<Mail>();
		StringBuilder bussMailBody = new StringBuilder("您申请的");
		DecimalFormat df = new DecimalFormat("0.00");
		if (conf && ("集团资金计划员付款".equals(stepName)) && (ifEnoughPay(procElseSpeLoan) == false)) {
			// 邮件业务内容
			// 您申请的***流程,编号为[流程实例ID],集团资金计划已经通过***方式下拨***元,请知悉,谢谢!"
			bussMailBody.append("“其他特殊的借款流程”，编号为：【" + processUtilMapService.getTmsIdByFnId(procElseSpeLoan.getProcInstId())
					+ "】，付款金额为：" + df.format(procElseSpeLoanDetail.getPayFundsTotal()) +"万元，各节点已经完成审批，并生成付款指令，资金调度同事即将处理！预计付款日期为：" + 
					DateUtil.dateToStrShort(procElseSpeLoanDetail.getPayDatetime()) + "，付款方式为："
					+ this.selectPayWay(procElseSpeLoanDetail) );
			bussMailBody.append("，审批备注："+remark+"。请知悉，谢谢！");
			log.info(bussMailBody);
		} else if (conf && ("集团资金计划员付款".equals(stepName)) && (ifEnoughPay(procElseSpeLoan) == true)) {
			bussMailBody.append("“其他特殊的借款流程”，编号为：【" + processUtilMapService.getTmsIdByFnId(procElseSpeLoan.getProcInstId())
					+ "】，付款金额为：" + df.format(procElseSpeLoanDetail.getPayFundsTotal()) +"万元，各节点已经完成审批，并生成付款指令，资金调度同事即将处理！预计付款日期为：" + 
					DateUtil.dateToStrShort(procElseSpeLoanDetail.getPayDatetime()) + "，付款方式为："
					+ this.selectPayWay(procElseSpeLoanDetail) );
			bussMailBody.append("，审批备注："+remark+"。此流程所有金额均已完成审批！请知悉，谢谢！");
		} else {
			bussMailBody.append("“其他特殊的借款流程”，编号为：【" + processUtilMapService.getTmsIdByFnId(procElseSpeLoan.getProcInstId()) + "】已经终止付款！请知悉，谢谢！");
			log.info(bussMailBody);
		}
		StringBuilder jpql = new StringBuilder("select distinct pu.pernr from PU pu where pu.defunctInd='N' ");
		jpql.append(" and pu.id='" + procElseSpeLoanDetail.getProcElseSpeLoan().getCreatedBy() + "'");
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
			m.setSubject(mailService.generationTitle(MailUtil.MailTypeEnum.Notice, "TMS_ElseSpeLoan",
					processUtilMapService.getTmsIdByFnId(procElseSpeLoan.getProcInstId()), stepName, loginService.getCurrentUserName(), null));
			m.setBody(mailService.generationContent(MailUtil.MailTypeEnum.Notice, "TMS_ElseSpeLoan",
					processUtilMapService.getTmsIdByFnId(procElseSpeLoan.getProcInstId()), stepName, loginService.getCurrentUserName(), null, null)
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