package com.wcs.tms.service.process.inveFinaMargin;

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
import com.wcs.tms.mail.MailService;
import com.wcs.tms.mail.MailUtil;
import com.wcs.tms.mail.SendMailService;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.mail.Mail;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.CompanyAccount;
import com.wcs.tms.model.ProcInveFinaBail;
import com.wcs.tms.model.ProcInveFinaDetail;
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
* <p>Title: 投资融资保证金流程service</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
@Stateless
public class InveFinaMarginService implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(InveFinaMarginService.class);
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
	// 是否需要去除重复的银行
	private Boolean ifa = true;

	/**
	 * 得到与公司有业务关系的银行
	 * @param companyId
	 * @return
	 */
	public List<SelectItem> getCompanyBankSelect(Long companyId) {
		List<SelectItem> bankSelect = new ArrayList<SelectItem>();
		List<CompanyAccount> ca = this.getComanyBank(companyId);
		if (ifa) {
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
				log.info("getCounterpartyCode:" + c.getCounterpartyCode());
				log.info("AccountDesc:" + c.getAccountDesc());
				SelectItem si = new SelectItem(c.getCounterpartyCode(), c.getAccountDesc());
				bankSelect.add(si);
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
	 * @param accountDesc(帐号描述)
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
	 * 得到公司银行里面的帐号
	 * @param counterpartyCode
	 * @return
	 */
	public List<SelectItem> getCompanyAccountSelectNo(String counterpartyCode) {
		List<SelectItem> accountSelect = new ArrayList<SelectItem>();
		log.info("执行获得公司银行帐号方法");
		accountSelect = companyAccountServer.findBankSelect(counterpartyCode);
		log.info("得到公司银行里面的帐号:" + accountSelect.size());
		return accountSelect;
	}

	/**
	 * 保存投融资保证金 
	 * @param procInveFinaBail
	 * @throws ServiceException
	 */
	public void saveInveFinaBail(ProcInveFinaBail procInveFinaBail) throws ServiceException {
		try {
			this.entityService.create(procInveFinaBail);
		} catch (Exception e) {
			log.error("saveInveFinaBail方法 保存投融资保证金出现异常：" , e);
			throw new ServiceException(ExceptionMessage.PREGITCAPTIAL_SAVE, e);
		}
	}

	/**
	 * 流程创建保存
	 * @param procInveFinaBail
	 * @return
	 * @throws ServiceException
	 */
	public String createProcInstance(ProcInveFinaBail procInveFinaBail) throws ServiceException {
		String procInstId = "";
		try {
			// PE流程创建
			// 流程实例ID，并保存
			procInstId = this.vwApplication(procInveFinaBail);
			log.info("PE流程创建成功！！！！");
			procInveFinaBail.setProcInstId(procInstId);
			procInveFinaBail.setCreatedBy(loginService.getCurrentUserName());
			// 保存到数据库中
			entityService.create(procInveFinaBail);
			// 生成流程实例编号映射9.11
			processUtilMapService.generateProcessMap(procInstId, "BPM_RA_014", procInveFinaBail.getCompany());
		} catch (Exception e) {
		    log.error("createProcInstance方法 流程创建保存错误信息：" , e);
			throw new ServiceException(ExceptionMessage.INSTANCE_CREATE, e);
		}
		return procInstId;
	}

	/**
	 * PE流程创建
	 * @param procInveFinaBail
	 * @return
	 * @throws ServiceException
	 */
	public String vwApplication(ProcInveFinaBail procInveFinaBail) throws ServiceException {
		String workClassName = ProcessXmlUtil.getProcessAttribute("id", "InveFinaBail", "className");
		String workflowNumber = "";
		if (peManager.checkWorkClassName(workClassName)) {
			try {
				// 验证流程类名
				workflowNumber = peManager.vwCreateInstance(workClassName, "tms_subject1");
				HashMap<String, Object> step1para = new HashMap<String, Object>();

				peManager.vwLauchStep("TMS_Requester", step1para, workflowNumber);
			} catch (Exception e) {
				log.error("vwApplication方法 PE流程创建错误信息：" , e);
				throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
			}
		} else {
			throw new ServiceException(ExceptionMessage.FN_NO_CLASS);
		}
		return workflowNumber;
	}

	/**
	 * 流程实例Id得到投融资保证金实例
	 * @param procInstId
	 * @return
	 */
	public ProcInveFinaBail findProcInstanceByProcInstId(String procInstId) {
		StringBuilder jpql = new StringBuilder("select iFB from ProcInveFinaBail iFB join fetch iFB.company where iFB.defunctInd = 'N'");
		jpql.append(" and iFB.procInstId='" + procInstId + "'");
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
		    log.error("getProcessDetail方法 查询流程详细错误信息：" , e);
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
			StringBuilder jpql = new StringBuilder("select iFB.id from ProcInveFinaBail iFB join fetch iFB.company where iFB.defunctInd = 'N'");
			jpql.append(" and iFB.procInstId='" + procInstId + "'");
			log.info(jpql);
			log.info("!!!!!!!!!!!!!!!!id为：" + entityService.findUnique(jpql.toString()));
			// 根据明细表中的字段
			StringBuilder jpql2 = new StringBuilder(
					"select iFD from ProcInveFinaDetail iFD left  join fetch iFD.procInveFinaBail where iFD.defunctInd = 'N'");
			jpql2.append(" and iFD.procInveFinaBail.id=" + entityService.findUnique(jpql.toString()) + "");
			log.info(jpql2);
			List<ProcInveFinaDetail> amo = entityService.find(jpql2.toString());
			int i = 1;
			for (ProcInveFinaDetail d : amo) {
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
		    log.error("getPayDetail方法 查询付款过程详细错误信息：" , e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return payDetailVos;
	}

	/**
	 * 审批保存
	 * @param procInveFinaBail
	 * @param pass
	 * @param stepName
	 * @throws ServiceException
	 */
	public void doApprove(ProcInveFinaBail procInveFinaBail, Boolean pass, String stepName) throws ServiceException {
		try {
			// 加入流程备注抬头
			String memoTitle = "";
			if (pass) {
				memoTitle = ProcessXmlUtil.findStepProperty("id", "InveFinaBail", stepName, "passMemo");
			} else {
				memoTitle = ProcessXmlUtil.findStepProperty("id", "InveFinaBail", stepName, "nopassMemo");
			}
			if (memoTitle != null) {
				procInveFinaBail.setPeMemo(memoTitle + procInveFinaBail.getPeMemo());
			}
			// 先执行更新操作
			entityService.update(procInveFinaBail);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("_Pass", pass);
			map.put("_Loan", this.ifLoan(procInveFinaBail));
//			map.put("_Currency", this.ifForeignCu(procInveFinaBail));
			// 如果是转口
			if("Y".equals(procInveFinaBail.getTransit())){
				map.put("UI_Transit", true);
			}else{
				map.put("UI_Transit", false);
			}
			// 表单类型如果是“投资理财”
			if("I".equals(procInveFinaBail.getFormType())){
				map.put("UI_Invest_Finance", true);
			}else{
				map.put("UI_Invest_Finance", false);
			}
			// 表单类型如果是“投资理财”
			if("D".equals(procInveFinaBail.getFormType())){
				map.put("UI_Time_Deposit", true);
			}else{
				map.put("UI_Time_Deposit", false);
			}
			peManager.vwDisposeTask(procInveFinaBail.getProcInstId(), map, procInveFinaBail.getPeMemo());
		} catch (Exception e) {
		    log.error("doApprove方法 审批保存错误信息：" , e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}

	/**
	 * 确认保存(付款或者终止付款)
	 * @param procInveFinaBail
	 * @param procInveFinaDetail
	 * @param conf
	 * @param payWay
	 * @param stepName
	 * @throws ServiceException
	 */
	public void doConfirm(ProcInveFinaBail procInveFinaBail, ProcInveFinaDetail procInveFinaDetail, Boolean conf, Boolean payWay, String stepName)
			throws ServiceException {
		// conf:付款标识符-true:付款，false:终止付款
		try {
			entityService.update(procInveFinaBail);
			// 加入流程备注抬头
			String memoTitle = "";
			if (conf) {
				memoTitle = ProcessXmlUtil.findStepProperty("id", "InveFinaBail", stepName, "passMemo");
			} else {
				memoTitle = ProcessXmlUtil.findStepProperty("id", "InveFinaBail", stepName, "nopassMemo");
			}
			// 邮件知会需要信息（add on 2013-4-10）
			String remark = procInveFinaBail.getPeMemo();
			if (memoTitle != null) {
				procInveFinaBail.setPeMemo(memoTitle + procInveFinaBail.getPeMemo());
			}
			// payWay true:SUNGARD付款 false:网银付款
			if (payWay) {
				procInveFinaDetail.setPayWay("S");
			} else {
				procInveFinaDetail.setPayWay("O");
			}
			if (conf) {
				log.info("执行付款任务1111111111111111");
				// 是否若为true则为足额支付,false为终止付款
				procInveFinaDetail.setPayDatetime(new Date());
				procInveFinaDetail.setCreatedBy(loginService.getCurrentUserName());
				entityService.create(procInveFinaDetail);
				log.info("存入数据库成功！！！！！");
				Boolean ifAll = ifEnoughPay(procInveFinaBail);
				log.info("验证完毕：验证结果是：" + ifAll);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("_Enough", ifAll);
				Boolean isSungard = false;
				if ("S".equals(procInveFinaDetail.getPayWay())) {
					isSungard = true;
					map.put("TMS_Is_Sungard", true);
				} else {
					isSungard = false;
					map.put("TMS_Is_Sungard", false);
				}
				peManager.vwDisposeTask(procInveFinaBail.getProcInstId(), map, procInveFinaBail.getPeMemo());
				//调用生成ftp文件方法 add on 2013-8-1 by yan
				String className = ProcessXmlUtil.getProcessAttribute("id", "InveFinaBail","className");
				String bpmId = ftpUploadService.tmsFtpUploadFile(procInveFinaBail.getProcInstId(),
						className, procInveFinaDetail.getCreatedDatetime(), isSungard);
				if(payWay) {
					ProcTMSStatus tmsStatus = new ProcTMSStatus();
					tmsStatus.setPayId(procInveFinaDetail.getId());
					tmsStatus.setBpmId(bpmId);
					tmsStatus.setTmsStatus(TmsStatusService.STATUS_NOIMPORT);
					//增加TMS回传表
					tmsStatusService.saveTmsStatus(tmsStatus);
				}else {
					// 邮件知会申请人
					log.info("邮件知会申请人");
					mailRequester(procInveFinaBail, procInveFinaDetail, conf, stepName, remark);
				}
			} else {
				log.info("执行终止付款任务222222222222222222");
				conf = true;
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("_Enough", conf);
				map.put("TMS_Is_Sungard", false);
				log.info("stop的值是：" + conf + "执行终止付款");
				peManager.vwDisposeTask(procInveFinaBail.getProcInstId(), map, procInveFinaBail.getPeMemo());
				// 终止付款时同样需要知会申请人
				conf = false;
				mailRequester(procInveFinaBail, procInveFinaDetail, conf, stepName,remark);
			}
		} catch (Exception e) {
		    log.error("doConfirm方法 确认保存(付款或者终止付款)错误信息：" , e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}

	/**
	 * 验证是否足额支付
	 * @param procInveFinaBail
	 * @return
	 */
	public Boolean ifEnoughPay(ProcInveFinaBail procInveFinaBail) {
		Boolean enough = false;
		Double amount = needPay(procInveFinaBail);
		log.info("获得需要支付的总金额：" + amount);
		Double payAmount = payedAccount(procInveFinaBail);
		log.info("获得已经支付的总金额" + payAmount);
		if (amount > payAmount) {
			log.info(enough + "!!!!!!未足额支付!!!!!!");
			return enough;
		}
		return true;
	}

	/**
	 * 获得需要支付的金额
	 * @param procInveFinaBail
	 * @return
	 */
	public Double needPay(ProcInveFinaBail procInveFinaBail) {
		StringBuilder jpql = new StringBuilder("select iFB.amount from ProcInveFinaBail iFB  where iFB.defunctInd = 'N'");
		jpql.append(" and iFB.procInstId='" + procInveFinaBail.getProcInstId() + "'");
		Double amount = entityService.findUnique(jpql.toString());
		log.info("需要支付总金额是：" + amount);
		return amount;
	}

	/**
	 * 获得已支付的总金额
	 * @param procInveFinaBail
	 * @return
	 */
	public Double payedAccount(ProcInveFinaBail procInveFinaBail) {
		Double payAmount = 0D;
		StringBuilder jpql = new StringBuilder(
				"select iFD from ProcInveFinaDetail iFD left  join fetch iFD.procInveFinaBail where iFD.defunctInd = 'N'");
		jpql.append(" and iFD.procInveFinaBail.id=" + procInveFinaBail.getId() + "");
		log.info(jpql);
		List<ProcInveFinaDetail> amo = entityService.find(jpql.toString());
		log.info("amo的队列为：" + amo);
		for (ProcInveFinaDetail d : amo) {
			payAmount += d.getPayFundsTotal();
		}
		log.info("已支付的总金额是：" + payAmount);
		return payAmount;
	}

	/**
	 * 验证是否是借款
	 * @param procInveFinaBail 
	 * @return
	 */
	public Boolean ifLoan(ProcInveFinaBail procInveFinaBail) {
		Boolean isLoan = false;
		String loan = procInveFinaBail.getLoanIden();
		if (loan != null && ("L".equals(loan) || "A".equals(loan))) {
			// 是借款或者借款+转款
			isLoan = true;
			return isLoan;
		} else {
			// 不是借款
			return isLoan;
		}
	}

	/**
	 * 验证是否是外币
	 * @return
	 */
	public Boolean ifForeignCu(ProcInveFinaBail procInveFinaBail) {
		Boolean isForeign = false;
		String foreign = procInveFinaBail.getAmountCu();
		if (foreign != null && "1".equals(foreign)) {
			// 是人民币=1
			return isForeign;
		} else {
			// 是外币
			isForeign = true;
			return isForeign;
		}
	}

	/**
	 * 邮件付款方式选择
	 * @param procInveFinaDetail
	 * @return
	 */
	public String selectPayWay(ProcInveFinaDetail procInveFinaDetail) {
		if ("S".equals(procInveFinaDetail.getPayWay())) {
			return "SUNGARD付款";
		} else {
			return "网银支付";
		}
	}

	/**
	 * 邮件通知申请人
	 * @param procInveFinaBail
	 * @param procInveFinaDetail
	 * @param conf
	 * @param stepName
	 * @param remark 
	 */
	private void mailRequester(ProcInveFinaBail procInveFinaBail, ProcInveFinaDetail procInveFinaDetail, Boolean conf, String stepName, String remark) {
		log.info("邮件知会申请人！！！！！！！！！！！");
		List<Mail> mailList = new ArrayList<Mail>();
		StringBuilder bussMailBody = new StringBuilder("您申请的");
		DecimalFormat df = new DecimalFormat("0.00");
		if (conf && ("集团资金计划员付款".equals(stepName)) && (ifEnoughPay(procInveFinaBail) == false)) {
			// 邮件业务内容
			// 您申请的***流程,编号为[流程实例ID],集团资金计划已经通过***方式下拨***元,请知悉,谢谢!"
			bussMailBody.append("“投资、融资保证金及归还银行贷款借款/转款流程”，编号为：【" + processUtilMapService.getTmsIdByFnId(procInveFinaBail.getProcInstId())
					+ "】，付款金额为：" + df.format(procInveFinaDetail.getPayFundsTotal()) +"万元，各节点已经完成审批，并生成付款指令，资金调度同事即将处理！预计付款日期为：" + 
					DateUtil.dateToStrShort(procInveFinaDetail.getPayDatetime()) + "，付款方式为："
					+ this.selectPayWay(procInveFinaDetail) );
			bussMailBody.append("，审批备注："+remark+"。请知悉，谢谢！");
			log.info(bussMailBody);
		} else if (conf && ("集团资金计划员付款".equals(stepName)) && (ifEnoughPay(procInveFinaBail) == true)) {
			bussMailBody.append("“投资、融资保证金及归还银行贷款借款/转款流程”，编号为：【" + processUtilMapService.getTmsIdByFnId(procInveFinaBail.getProcInstId())
					+ "】，付款金额为：" + df.format(procInveFinaDetail.getPayFundsTotal()) +"万元，各节点已经完成审批，并生成付款指令，资金调度同事即将处理！预计付款日期为：" + 
					DateUtil.dateToStrShort(procInveFinaDetail.getPayDatetime()) + "，付款方式为："
					+ this.selectPayWay(procInveFinaDetail) );
			bussMailBody.append("，审批备注："+remark+"。此流程所有金额均已完成审批！请知悉，谢谢！");
		} else {
			bussMailBody.append("“投资、融资保证金及归还银行贷款借款/转款”流程，编号为：【" + processUtilMapService.getTmsIdByFnId(procInveFinaBail.getProcInstId())
					+ "】已经终止付款！请知悉，谢谢！");
			log.info(bussMailBody);
		}
		StringBuilder jpql = new StringBuilder("select distinct pu.pernr from PU pu where pu.defunctInd='N' ");
		jpql.append(" and pu.id='" + procInveFinaDetail.getProcInveFinaBail().getCreatedBy() + "'");
		String pid = entityService.findUnique(jpql.toString());
		P p = entityService.find(P.class, pid);
		if (p != null) {
			Mail m = new Mail();
			m.setTelno(p.getCelno());
			m.setEmail(p.getEmail());
			m.setSubject(mailService.generationTitle(MailUtil.MailTypeEnum.Notice, "TMS_InveFinaBail",
					processUtilMapService.getTmsIdByFnId(procInveFinaBail.getProcInstId()), stepName, loginService.getCurrentUserName(), null));
			m.setBody(mailService.generationContent(MailUtil.MailTypeEnum.Notice, "TMS_InveFinaBail",
					processUtilMapService.getTmsIdByFnId(procInveFinaBail.getProcInstId()), stepName, loginService.getCurrentUserName(), null, null)
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