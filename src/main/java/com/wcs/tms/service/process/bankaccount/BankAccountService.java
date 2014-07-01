package com.wcs.tms.service.process.bankaccount;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.MathUtil;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.common.model.P;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.mail.Mail;
import com.wcs.tms.mail.MailService;
import com.wcs.tms.mail.MailUtil;
import com.wcs.tms.mail.SendMailService;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.BankAccountMain;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.CompanyAccount;
import com.wcs.tms.model.ProcBankAccount;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.util.SelectItemUtil;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWWorkObjectNumber;

/**
 * 
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description: 银行账户申请审批流程Service
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */

@Stateless
public class BankAccountService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	ProcessUtilMapService processUtilMapService;
	@Inject
	MailService mailService;
	@Inject
	SendMailService sendMailService;
	@Inject
	PEManager peManager;
	@Inject
	EntityService entityService;
	@Inject
	LoginService loginService;
	
	private static final Log log = LogFactory.getLog(BankAccountService.class);
	
	/**
	 * 查找"银行账户标识"
	 * @param companyId
	 * @param bankCode
	 * @return
	 */
	public CompanyAccount findCompanyAccountBy(Long companyId, String bankCode){
		StringBuilder jpql = new StringBuilder("select ca from CompanyAccount ca join fetch ca.company where ca.defunctInd = 'N' ");
		jpql.append(" and ca.company.id=" + companyId);
		jpql.append(" and ca.bank='" + bankCode+"'");
		return (CompanyAccount)entityService.find(jpql.toString()).get(0);
	}

	/**
	 * 根据账户初始化公司下拉
	 * 
	 * @param procBankAccount
	 * @return
	 */
	public List<SelectItem> initCompanySelect(ProcBankAccount procBankAccount) {
		List<String> sapList = loginService.findCompanySapCode(procBankAccount
				.getCreatedBy());
		StringBuilder jpql = new StringBuilder(
				"select c from Company c where c.defunctInd = 'N' and c.status = 'Y'");
		List<Company> companys = new ArrayList<Company>();
		if (sapList != null && sapList.size() != 0) {
			jpql.append(" and c.sapCode in (?1)");
			companys = entityService.find(jpql.toString(), sapList);
		}
		List<SelectItem> selects = new ArrayList<SelectItem>();
		for (Company c : companys) {
			SelectItem si = new SelectItem(c.getId(), c.getCompanyName());
			selects.add(si);
		}
		return selects;
	}

	/**
	 * 流程创建保存
	 * 
	 * @return
	 */
	public String createProcInstance(ProcBankAccount procBankAccount)
			throws ServiceException {
		String procInstId = "";
		try {
			// PE流程创建
			// 流程实例ID，并保存
			procInstId = this.vwApplication(procBankAccount);
			procBankAccount.setProcInstId(procInstId);
			procBankAccount.setCreatedBy(loginService.getCurrentUserName());
			entityService.create(procBankAccount);
			// 生成流程实例编号映射9.19
			processUtilMapService.generateProcessMap(procInstId,
					"TMS_BPM_RA_008", procBankAccount.getCompany());
		} catch (Exception e) {
			log.error("createProcInstance方法 流程创建保存 出现异常：",e);
			throw new ServiceException(ExceptionMessage.INSTANCE_CREATE, e);
		}
		return procInstId;
	}

	/**
	 * PE流程创建
	 * 
	 * @param procBankAccount
	 * @return
	 * @throws ServiceException
	 */
	public String vwApplication(ProcBankAccount procBankAccount)
			throws ServiceException {
		String workClassName = ProcessXmlUtil.getProcessAttribute("id",
				"BankAccount", "className");
		String workflowNumber = "";
		if (peManager.checkWorkClassName(workClassName)) {
			try {
				// 验证流程类名
				workflowNumber = peManager.vwCreateInstance(workClassName,
						"tms_subject1");

				HashMap<String, Object> step1para = new HashMap<String, Object>();
				peManager.vwLauchStep("TMS_Requester", step1para,
						workflowNumber);
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
	 * 得到总行下的支行
	 * 
	 * @param topId
	 * @return
	 * @throws ServiceException
	 */
	public List<SelectItem> findBranchBankSelect(Long topId)
			throws ServiceException {
		Validate.notNull(topId, "总银行Id为空");
		try {
			StringBuilder buff = new StringBuilder();
			buff.append("select b.id,b.bankName from Bank b where b.topBankId=?1 and b.defunctInd = 'N' and b.status='Y' ");
			List list = entityService.find(buff.toString(), topId);
			return SelectItemUtil.arrayToListSelectItem(list);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 查询流程详细过程
	 * 
	 * @param procInstId
	 * @return
	 */
	public List<ProcessDetailVo> getProcessDetail(String procInstId) {
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
			log.error("getProcessDetail方法 查询流程详细过程 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return detailVos;
	}

	/**
	 * 根据流程实例ID得到申请实例
	 * 
	 * @param procInstId
	 * @return
	 */
	public ProcBankAccount findProcAccountBy(String procInstId) {
		StringBuilder jpql = new StringBuilder(
				"select ba from ProcBankAccount ba join fetch ba.company where ba.defunctInd = 'N'");
		jpql.append(" and ba.procInstId='" + procInstId + "'");
		return entityService.findUnique(jpql.toString());
	}

	/**
	 * 审批保存
	 * 
	 * @param procBankAccount
	 * @param pass
	 * @param stepName
	 * @throws ServiceException
	 */
	public void doApprove(ProcBankAccount procBankAccount, boolean pass,
			String stepName) throws ServiceException {
		try {
			// 加入流程备注抬头
			String memoTitle = "";
			if (pass) {
				memoTitle = ProcessXmlUtil.findStepProperty("id",
						"BankAccount", stepName, "passMemo");
			} else {
				memoTitle = ProcessXmlUtil.findStepProperty("id",
						"BankAccount", stepName, "nopassMemo");
			}
			if (memoTitle != null) {
				procBankAccount.setPeMemo(memoTitle
						+ procBankAccount.getPeMemo());
			}
			// 先执行更新操作
			entityService.update(procBankAccount);
			Map<String, Object> fnParamMap = new HashMap<String, Object>();
			fnParamMap.put("_Pass", pass);
			//是否为个人卡
			Boolean personalFlag = true;
			if ("Y".equals(procBankAccount.getAccountType())) {
				fnParamMap.put("TMS_Is_Personal", personalFlag);
			} else {
				personalFlag = false;
				fnParamMap.put("TMS_Is_Personal", personalFlag);
			}
			//开户销户
			Boolean openFlag = true;
			if ("O".equals(procBankAccount.getOperateType())) {
				fnParamMap.put("TMS_Is_Open", openFlag);
			} else {
				openFlag = false;
				fnParamMap.put("TMS_Is_Open", openFlag);
			}
			
			// 开户销户功能更新（modified on 2013-3-26）如果开户在财务总监审批 通过 就保存主数据
			if (pass && "集团财务总监审批".equals(stepName) && "O".equals(procBankAccount.getOperateType()) ) {
				// 银行账户申请成功保存（add on 2013-3-26）
				this.saveBankAccountMain(procBankAccount);
				this.mailRequester(procBankAccount, stepName);
			}else if(pass && "集团资金部门经理-风险经理审批".equals(stepName) && "C".equals(procBankAccount.getOperateType())
					&& "Y".equals(procBankAccount.getAccountType())){
				this.logOffBankAccountMain(procBankAccount);
				this.mailRequester(procBankAccount, stepName);
			}else if(pass && "集团资金部门经理-风险经理审批".equals(stepName) && 
					"O".equals(procBankAccount.getOperateType()) 
					&& "N".equals(procBankAccount.getAccountType())){
				this.saveBankAccountMain(procBankAccount);
				this.mailRequester(procBankAccount, stepName);
			}else if(pass && "集团资金部门经理-风险经理审批".equals(stepName) && 
					"C".equals(procBankAccount.getOperateType()) 
					&& "N".equals(procBankAccount.getAccountType())){
				this.logOffBankAccountMain(procBankAccount);
				this.mailRequester(procBankAccount, stepName);
			}
			peManager.vwDisposeTask(procBankAccount.getProcInstId(),
					fnParamMap, procBankAccount.getPeMemo());
		} catch (Exception e) {
			log.error("doApprove方法 审批保存 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}
	
	/**
	 * 流程结束给申请人发送相关邮件
	 * @param procBankAccount
	 * @param stepName 
	 */
	private void mailRequester(ProcBankAccount procBankAccount, String stepName) {
		List<Mail> mailList = new ArrayList<Mail>();
		StringBuilder bussMailBody = new StringBuilder("您申请的“银行账户申请审批流程”，编号为:["+processUtilMapService.getTmsIdByFnId(procBankAccount.getProcInstId())+"]账户类型为："+
		("Y".equals(procBankAccount.getAccountType()) ? "个人卡" : "非个人卡")+"；操作类型为："
				+("O".equals(procBankAccount.getOperateType()) ? "开户" : "销户")+",已申请成功，"+"请知悉，谢谢！");
		StringBuilder puJpql = new StringBuilder();
        puJpql.append("select distinct pu.pernr from PU pu where pu.defunctInd='N' and pu.id=?1");
		String pId = entityService.findUnique(puJpql.toString(), procBankAccount.getCreatedBy());
		P p = entityService.find(P.class, pId);
			if(p != null){
				Mail m = new Mail();
				m.setTelno(p.getCelno());
				m.setEmail(p.getEmail());
				m.setSubject(mailService.generationTitle(MailUtil.MailTypeEnum.Notice, "TMS_BankAccount", processUtilMapService.getTmsIdByFnId(procBankAccount.getProcInstId()), stepName, loginService.getCurrentUserName(), null));
				m.setBody(mailService.generationContent(MailUtil.MailTypeEnum.Notice, "TMS_BankAccount", processUtilMapService.getTmsIdByFnId(procBankAccount.getProcInstId()), stepName, loginService.getCurrentUserName(), null, null) + bussMailBody.toString());
				mailList.add(m);
			}
		sendMailService.send(mailList);
	}

	/**
	 * 注销数据库操作
	 * @param procBankAccount
	 */
	private void logOffBankAccountMain(ProcBankAccount procBankAccount) {
		// 注销功能 如果“流程实例”没选则用户手动填写相关字段 最终审批通过 新增到主数据表
		if(MathUtil.isEmptyOrNull( procBankAccount.getProcInstIdClosed())){
			log.info("销户新增操作！");
			BankAccountMain bankAccountMain = new BankAccountMain();
			bankAccountMain.setAccountCheckFlag(procBankAccount.getAccountCheckFlag());
			bankAccountMain.setAccountDesc(procBankAccount.getAccountDesc());
			bankAccountMain.setAccountNature(procBankAccount.getAccountNature());
			bankAccountMain.setAccountType(procBankAccount.getAccountType());
			bankAccountMain.setBankAccount(procBankAccount.getBankAccount());
			bankAccountMain.setBankCode(procBankAccount.getBankCode());
			bankAccountMain.setBelongBankName(procBankAccount.getBelongBankName());
			bankAccountMain.setBocOrganNumber(procBankAccount.getBocOrganNumber());
			bankAccountMain.setCardHolderName(procBankAccount.getCardHolderName());
			bankAccountMain.setCardHolderPosition(procBankAccount.getCardHolderPosition());
			bankAccountMain.setCity(procBankAccount.getCity());
			bankAccountMain.setCompany(procBankAccount.getCompany());
			bankAccountMain.setCurrency(procBankAccount.getCurrency());
			bankAccountMain.setOpenAccountReason(procBankAccount.getOpenAccountReason());
			bankAccountMain.setOperateType(procBankAccount.getOperateType());
			bankAccountMain.setOwnedCardNumber(procBankAccount.getOwnedCardNumber());
			bankAccountMain.setSapBankSaveCurrSubjNum(procBankAccount.getSapBankSaveCurrSubjNum());
			bankAccountMain.setSapBusSubj(procBankAccount.getSapBusSubj());
			bankAccountMain.setTmsBankAccountCode(procBankAccount.getTmsBankAccountCode());
			bankAccountMain.setUseDeadline(procBankAccount.getUseDeadline());
			bankAccountMain.setUseDesc(procBankAccount.getUseDesc());
			bankAccountMain.setUseTime(procBankAccount.getUseTime());
			bankAccountMain.setCreatedBy(procBankAccount.getCreatedBy());
			bankAccountMain.setProcInstId(procBankAccount.getProcInstId());
			// 先保存
			entityService.create(bankAccountMain);
			// 再更新数据注销状态
			bankAccountMain.setDefunctInd("Y");
			entityService.update(bankAccountMain);
			log.info("销户新增销户数据成功！");
		}else{
			// 注销功能 如果“流程实例”选择了 最终审批通过 更新相关主数据
			log.info("销户更新操作！");
			BankAccountMain bankAccountMain = findBankAccountMainByProcId(procBankAccount.getProcInstIdClosed());
			log.info("需要注销主数据Id:" + bankAccountMain.getId());
			bankAccountMain.setCardHolderName(procBankAccount.getCardHolderName());
			bankAccountMain.setCardHolderPosition(procBankAccount.getCardHolderPosition());
			bankAccountMain.setUseDesc(procBankAccount.getUseDesc());
			bankAccountMain.setOwnedCardNumber(procBankAccount.getOwnedCardNumber());
			bankAccountMain.setUseDeadline(procBankAccount.getUseDeadline());
			bankAccountMain.setUseTime(procBankAccount.getUseTime());
			bankAccountMain.setCurrency(procBankAccount.getCurrency());
			bankAccountMain.setAccountNature(procBankAccount.getAccountNature());
			bankAccountMain.setBelongBankName(procBankAccount.getBelongBankName());
			bankAccountMain.setDefunctInd("Y");
			entityService.update(bankAccountMain);
			log.info("销户更新销户数据成功！");
		}
	}

	/**
	 * 银行账户主数据保存
	 * @param procBankAccount
	 */
	private void saveBankAccountMain(ProcBankAccount procBankAccount) {
		BankAccountMain bankAccountMain = new BankAccountMain();
		bankAccountMain.setAccountCheckFlag(procBankAccount.getAccountCheckFlag());
		bankAccountMain.setAccountDesc(procBankAccount.getAccountDesc());
		bankAccountMain.setAccountNature(procBankAccount.getAccountNature());
		bankAccountMain.setAccountType(procBankAccount.getAccountType());
		bankAccountMain.setBankAccount(procBankAccount.getBankAccount());
		bankAccountMain.setBankCode(procBankAccount.getBankCode());
		bankAccountMain.setBelongBankName(procBankAccount.getBelongBankName());
		bankAccountMain.setBocOrganNumber(procBankAccount.getBocOrganNumber());
		bankAccountMain.setCardHolderName(procBankAccount.getCardHolderName());
		bankAccountMain.setCardHolderPosition(procBankAccount.getCardHolderPosition());
		bankAccountMain.setCity(procBankAccount.getCity());
		bankAccountMain.setCompany(procBankAccount.getCompany());
		bankAccountMain.setCurrency(procBankAccount.getCurrency());
		bankAccountMain.setOpenAccountReason(procBankAccount.getOpenAccountReason());
		bankAccountMain.setOperateType(procBankAccount.getOperateType());
		bankAccountMain.setOwnedCardNumber(procBankAccount.getOwnedCardNumber());
		bankAccountMain.setSapBankSaveCurrSubjNum(procBankAccount.getSapBankSaveCurrSubjNum());
		bankAccountMain.setSapBusSubj(procBankAccount.getSapBusSubj());
		bankAccountMain.setTmsBankAccountCode(procBankAccount.getTmsBankAccountCode());
		bankAccountMain.setUseDeadline(procBankAccount.getUseDeadline());
		bankAccountMain.setUseDesc(procBankAccount.getUseDesc());
		bankAccountMain.setUseTime(procBankAccount.getUseTime());
		bankAccountMain.setCreatedBy(procBankAccount.getCreatedBy());
		bankAccountMain.setProcInstId(procBankAccount.getProcInstId());
		entityService.create(bankAccountMain);
	}

	/**
	 * 注销数据库公司账号
	 * @param procBankAccount
	 */
	private void logOffCompanyAccount(ProcBankAccount procBankAccount) {
		List<CompanyAccount> cas = findAccountByIdent(procBankAccount);
		if(0 != cas.size()){
			cas.get(0).setDefunctInd("Y");
			entityService.update(cas.get(0));
		}
	}

	/**
	 * 给相关人员发送邮件
	 * 
	 * @param procProdOrTradeCash
	 * @param pass
	 * @param stepName
	 */
	private void mailRelated(ProcBankAccount procBankAccount, boolean pass,
			String stepName) {
		if (pass && ("集团资金经理审批".equals(stepName))
				&& "Y".equals(procBankAccount.getAccountType())) {
			// 个人卡邮件知会
			personalMail(procBankAccount, stepName);
		}
		if (pass && ("集团财务总监审批".equals(stepName))
				&& "N".equals(procBankAccount.getAccountType())) {
			// 非个人卡邮件知会
			notPersonalMail(procBankAccount, stepName);
		}
	}

	/**
	 * 非个人卡邮件知会
	 * 
	 * @param procBankAccount
	 * @param stepName
	 */
	private void notPersonalMail(ProcBankAccount procBankAccount,
			String stepName) {
		List<Mail> mailList = new ArrayList<Mail>();
		// 邮件业务内容
		StringBuilder bussMailBody = new StringBuilder("集团资金经理审批已通过");

		bussMailBody.append("，特此告知。");
		// 得到集团公司sap代码
		List<Company> companies = findBlocSapCode();
		List<String> blocSapCodes = new ArrayList<String>();
		for (Company c : companies) {
			blocSapCodes.add(c.getSapCode());
		}
		// 得到当前流程所属公司SAPCode
		String comSapCode = procBankAccount.getCompany().getSapCode();
		List<P> ps = new ArrayList<P>();
		// 申请人
		ps.addAll(mailService.findUserByQueue("TMS_Requester"));
		// 工厂财务经理
		ps.addAll(mailService.findUserByQueue("TMS_Fac_Finance"));
		// 工厂总经理
		ps.addAll(mailService.findUserByQueue("TMS_Fac_Manager"));
		// 集团会计部门人员
		ps.addAll(mailService.findUserByQueue("TMS_Cop_Account"));
		// 集团资金部门人员5、银行账户申请组
		ps.addAll(mailService.findUserByQueue("TMS_Cop_Fund_Dep_5"));
		// 集团资金部门经理
		ps.addAll(mailService.findUserByQueue("TMS_Cop_Fund_Dep_M_Sx"));
		for (P p : ps) {
			String pid = p.getId();
			if (pid != null) {
				List<String> saps = loginService.finSapByPid(pid);
				boolean isProvider = false;
				// 比对人的公司的sap是否包含流程所属公司sap,集团的人除外
				if (blocSapCodes.size() != 0) {
					for (String blocSapCode : blocSapCodes) {
						if (saps.contains(blocSapCode)
								|| saps.contains(comSapCode)) {
							isProvider = true;
							break;
						}
					}
				} else {
					if (saps.contains(comSapCode)) {
						isProvider = true;
					}
				}
				// 是工厂提供方财务经理,发送邮件
				if (isProvider) {
					Mail m = new Mail();
					m.setTelno(p.getCelno());
					m.setEmail(p.getEmail());
					m.setSubject(mailService.generationTitle(
							MailUtil.MailTypeEnum.Notice, "TMS_BankAccount",
							processUtilMapService
									.getTmsIdByFnId(procBankAccount
											.getProcInstId()), stepName,
							loginService.getCurrentUserName(), null));
					m.setBody(mailService.generationContent(
							MailUtil.MailTypeEnum.Notice, "TMS_BankAccount",
							processUtilMapService
									.getTmsIdByFnId(procBankAccount
											.getProcInstId()), stepName,
							loginService.getCurrentUserName(), null, null)
							+ bussMailBody.toString());
					mailList.add(m);
				}
			}
		}
		sendMailService.send(mailList);
	}

	/**
	 * 个人卡邮件知会
	 * 
	 * @param procBankAccount
	 * @param stepName
	 */
	private void personalMail(ProcBankAccount procBankAccount, String stepName) {
		List<Mail> mailList = new ArrayList<Mail>();
		// 邮件业务内容
		StringBuilder bussMailBody = new StringBuilder("集团资金经理审批已通过");

		bussMailBody.append("，特此告知。");
		// 得到集团公司sap代码
		List<Company> companies = findBlocSapCode();
		List<String> blocSapCodes = new ArrayList<String>();
		for (Company c : companies) {
			blocSapCodes.add(c.getSapCode());
		}
		// 得到当前流程所属公司SAPCode
		String comSapCode = procBankAccount.getCompany().getSapCode();
		List<P> ps = new ArrayList<P>();
		// 申请人
		ps.addAll(mailService.findUserByQueue("TMS_Requester"));
		// 工厂财务经理
		ps.addAll(mailService.findUserByQueue("TMS_Fac_Finance"));
		// 工厂总经理
		ps.addAll(mailService.findUserByQueue("TMS_Fac_Manager"));
		// 集团会计部门人员
		ps.addAll(mailService.findUserByQueue("TMS_Cop_Account"));
		// 集团资金部门人员5、银行账户申请组
		ps.addAll(mailService.findUserByQueue("TMS_Cop_Fund_Dep_5"));
		for (P p : ps) {
			String pid = p.getId();
			if (pid != null) {
				List<String> saps = loginService.finSapByPid(pid);
				boolean isProvider = false;
				// 比对人的公司的sap是否包含流程所属公司sap,集团的人除外
				if (blocSapCodes.size() != 0) {
					for (String blocSapCode : blocSapCodes) {
						if (saps.contains(blocSapCode)
								|| saps.contains(comSapCode)) {
							isProvider = true;
							break;
						}
					}
				} else {
					if (saps.contains(comSapCode)) {
						isProvider = true;
					}
				}
				// 是工厂提供方财务经理,发送邮件
				if (isProvider) {
					Mail m = new Mail();
					m.setTelno(p.getCelno());
					m.setEmail(p.getEmail());
					m.setSubject(mailService.generationTitle(
							MailUtil.MailTypeEnum.Notice, "TMS_BankAccount",
							processUtilMapService
									.getTmsIdByFnId(procBankAccount
											.getProcInstId()), stepName,
							loginService.getCurrentUserName(), null));
					m.setBody(mailService.generationContent(
							MailUtil.MailTypeEnum.Notice, "TMS_BankAccount",
							processUtilMapService
									.getTmsIdByFnId(procBankAccount
											.getProcInstId()), stepName,
							loginService.getCurrentUserName(), null, null)
							+ bussMailBody.toString());
					mailList.add(m);
				}
			}
		}
		sendMailService.send(mailList);

	}

	/**
	 * 得到集团公司sap代码
	 */
	private List<Company> findBlocSapCode() {
		StringBuilder jpql = new StringBuilder(
				"select c from Company c where c.defunctInd = 'N' and c.corporationFlag='Y'");
		return entityService.find(jpql.toString());
	}

	/**
	 * 公司账户主数据保存
	 * 
	 * @param procBankAccount
	 */
	private void saveCompanyAccount(ProcBankAccount procBankAccount) {
		Bank bank = this.findBankBy(procBankAccount.getBankCode());
		CompanyAccount companyAccount = new CompanyAccount();
		companyAccount.setCompany(procBankAccount.getCompany());
		companyAccount.setBank(procBankAccount.getBankCode());
		companyAccount.setAccount(procBankAccount.getBankAccount());
		companyAccount.setBsbCode(bank.getBsbCode());
		companyAccount.setUnionBankNo(bank.getUnionBankNo());
		companyAccount.setCounterpartyCode(procBankAccount.getAccountIdent());
		companyAccount.setAccountDesc(procBankAccount.getAccountDesc());
		entityService.create(companyAccount);
	}

	/**
	 * 查找交易对手
	 * @param bankCode 银行编码
	 * @return
	 */
	public Bank findBankBy(String bankCode) {
		StringBuilder buff = new StringBuilder("select b from Bank b where b.defunctInd = 'N' and b.status='Y' ");
		buff.append(" and b.bankCode='"+bankCode+"'");
		if( 0 == entityService.find(buff.toString()).size()){
			return new Bank();
		}else{
			return (Bank) entityService.find(buff.toString()).get(0);
		}
	}

	/**
	 * 验证“账户标识”是否已存在
	 * @param accountIdent
	 * @return
	 */
	public Boolean findCompanyAccountBy(String accountIdent) {
		StringBuilder jpql = new StringBuilder("select ca from CompanyAccount ca where ca.defunctInd = 'N'");
		jpql.append(" and ca.counterpartyCode='"+accountIdent+"'");
		List<CompanyAccount> cas = entityService.find(jpql.toString());
		if(0 == cas.size()){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * 根据账号标识查询公司账户
	 * @param procBankAccount
	 * @return
	 */
	public List<CompanyAccount> findAccountByIdent(
			ProcBankAccount procBankAccount) {
		StringBuilder jpql = new StringBuilder(
				"select ca from CompanyAccount ca where ca.defunctInd = 'N' ");
		jpql.append(" and ca.counterpartyCode = '"+procBankAccount.getAccountIdent()+"'");
		return entityService.find(jpql.toString());
	}

	/**
	 * 查找选中银行
	 * @param bankCode
	 * @return
	 */
	public Bank findBankByCode(String bankCode) {
		StringBuilder jpql = new StringBuilder("select b from Bank b where b.defunctInd = 'N' ") ;
		jpql.append(" and b.bankCode = '"+bankCode+"'");
		List<Bank> banks = entityService.find(jpql.toString());
		if(0 == banks.size()){
			return new Bank();
		}else{
			return banks.get(0);
		}
	}

	/**
	 * 查询公司账户（根据账户标识）
	 * @param procBankAccount
	 * @return
	 */
	public List<ProcBankAccount> findCompanyAccountByIdent(
			ProcBankAccount procBankAccount) {
		StringBuilder jpql = new StringBuilder("select ca from CompanyAccount ca join fetch ca.company where ca.defunctInd = 'N' ");
		jpql.append(" and ca.counterpartyCode ='" + procBankAccount.getAccountIdent() +"'");
		return this.entityService.find(jpql.toString());
	}

	/**
	 * 根据条件查询银行账户主数据表
	 * @param procBankAccount
	 * @return
	 */
	public List<SelectItem> findBankAccountSelect(ProcBankAccount procBankAccount) {
		StringBuilder jpql = new StringBuilder("select ba from BankAccountMain ba join fetch ba.company where ba.defunctInd = 'N' ");
		jpql.append(" and ba.createdBy ='" + procBankAccount.getCreatedBy() +"'");
		jpql.append(" and ba.company.id =" + procBankAccount.getCompany().getId() );
		jpql.append(" and ba.accountType ='" + procBankAccount.getAccountType()+"'" );
		List<BankAccountMain> bankAccountMains = entityService.find(jpql.toString());
		List<SelectItem> bankAccountSelect = new ArrayList<SelectItem>(); 
		for(BankAccountMain bc : bankAccountMains){
			String processId = bc.getProcInstId();
			String desc ;
			if(MathUtil.isEmptyOrNull(bc.getCardHolderName())){
				desc = processUtilMapService.getTmsIdByFnId(processId) + "-" +bc.getBelongBankName();
			}else{
				desc = processUtilMapService.getTmsIdByFnId(processId) + "-" +bc.getBelongBankName() + 
						"-" + bc.getCardHolderName();
			}
			SelectItem item = new SelectItem(processId , desc);
			bankAccountSelect.add(item);
		}
		return bankAccountSelect;
	}
	
	/**
	 * 根据销户流程实例编号查询账户主数据
	 * @param procInstIdClosed
	 * @return
	 */
	public BankAccountMain findBankAccountMainByProcId(String procInstIdClosed) {
		StringBuilder jpql = new StringBuilder("select ba from BankAccountMain ba join fetch ba.company where ba.defunctInd = 'N' ");
		jpql.append(" and ba.procInstId ='" + procInstIdClosed +"'");
		List<BankAccountMain> mains = entityService.find(jpql.toString());
		if(mains.size() > 0){
			return mains.get(0);
		}else{
			return new BankAccountMain();
		}
	}

}
