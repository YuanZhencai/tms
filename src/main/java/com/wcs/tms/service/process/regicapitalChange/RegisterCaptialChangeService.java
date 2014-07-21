package com.wcs.tms.service.process.regicapitalChange;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcRegiCapital;
import com.wcs.tms.model.ProcRegiCapitalChange;
import com.wcs.tms.model.ProcRegiCapitalChangeShareholder;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.system.company.ShareHolderService;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.ChangeShareholderVo;

import filenet.vw.api.VWWorkObjectNumber;

/** 
* <p>Project: tms</p> 
* <p>Title: RegisterCaptialChangeService.java</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2014 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:yuanzhencai@wcs-global.com">Yuan</a> 
*/
@Stateless
public class RegisterCaptialChangeService implements Serializable {

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
	@Inject
	private ShareHolderService shareHolderService;

	private static final Log log = LogFactory.getLog(RegisterCaptialChangeService.class);

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
	public String vwCreateProcessInstance(String processName, String comment, String memo, List<String> documentList) throws ServiceException {
		String workflowNumber = "";
		// 检查FN是否存在该流程
		if (peManager.checkWorkClassName(processName)) {
			try {
				workflowNumber = peManager.vwCreateInstance(processName, comment);
				HashMap<String, Object> step1para = new HashMap<String, Object>();
				// 得到登录用户
				Usermstr user = loginService.getCurrentUsermstr();
				step1para.put("TMS_Requester_Exer", user.getAdAccount());
				step1para.put("TMS_Requester_Memo", memo);
				peManager.vwLauchStep("TMS_Fac_Fund_Pos", step1para, workflowNumber, documentList);
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
	public void vwReplayRegister(String memo, String workflowNumber, List<String> documentList) throws ServiceException {
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
	public void saveRegisCaptial(ProcRegiCapital procRegiCapital) throws ServiceException {
		try {
			this.entityService.create(procRegiCapital);
			// 生成流程实例编号映射9.10
			processUtilMapService.generateProcessMap(procRegiCapital.getProcInstId(), "BPM_RA_019", procRegiCapital.getCompany());
		} catch (Exception e) {
			log.error("saveRegisCaptial方法 保存注册资本金  出现异常：", e);
			throw new ServiceException(ExceptionMessage.PREGITCAPTIAL_SAVE, e);
		}
	}

	/**
	 * 
	 * <p>
	 * Description:保存注册资本金变更
	 * </p>
	 * 
	 * @param procRegiCapital
	 * @throws ServiceException
	 */
	public void saveRegisCaptialChange(ProcRegiCapitalChange procRegiCapitalChange) throws ServiceException {
		try {
			this.entityService.create(procRegiCapitalChange);
			// 生成流程实例编号映射9.10
			processUtilMapService.generateProcessMap(procRegiCapitalChange.getProcInstId(), "BPM_RA_019", procRegiCapitalChange.getCompany());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(ExceptionMessage.PREGITCAPTIAL_SAVE, e);
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 根据流程实例编号得到注册资本金变成
	 * </p>
	 * 
	 * @param workClassName
	 * @return
	 * @throws ServiceException
	 */
	public ProcRegiCapitalChange findProcRegiCaptialChange(String workClassName) throws ServiceException {
		Validate.notNull(workClassName, "注册资本金对应流程实例编号为空");
		try {
			StringBuilder bulder = new StringBuilder();
			bulder.append("select prc from ProcRegiCapitalChange prc left join fetch prc.procRegiCapitalChangeShareholders").append(
					" where prc.procInstId=?1 and prc.defunctInd = 'N'");
			List<ProcRegiCapitalChange> list = this.entityService.find(bulder.toString(), workClassName);
			if (list.isEmpty()) {
				return null;
			}
			return list.get(0);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
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
	public ProcRegiCapital findProcRegiCaptial(String workClassName) throws ServiceException {
		Validate.notNull(workClassName, "注册资本金对应流程实例编号为空");
		try {
			StringBuilder bulder = new StringBuilder();
			bulder.append("select prc from ProcRegiCapital prc left join fetch prc.company ").append(
					"where prc.procInstId=?1 and prc.defunctInd = 'N'");
			List<ProcRegiCapital> list = this.entityService.find(bulder.toString(), workClassName);
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
	public void vwDisposeTask(String workClassNum, Map<String, Object> paramMap, String memo) throws ServiceException {
		Validate.notNull(workClassNum, "注册资本金变更对应流程实例编号为空");
		try {
			this.peManager.vwDisposeTask(workClassNum, paramMap, memo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
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
		StringBuilder filter = new StringBuilder("1=1 and F_EventType = :EventType");
		Object[] substitutionVars = new Object[2];
		filter.append(" and F_WobNum = :wobNum");
		substitutionVars[0] = ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd);
		substitutionVars[1] = new VWWorkObjectNumber(processId);

		List<String> steps = new ArrayList<String>();
		steps.add("集团项目经理审批");
		// 邮件封装
		mails = mailService.findEmailBySteps(filter.toString(), substitutionVars, steps, MailUtil.MailTypeEnum.GroupPM);
		sendMailService.send(mails);
	}

	public List<ChangeShareholderVo> findChangeShareholderVosBy(Long companyId) {
		StringBuilder sql = new StringBuilder(100);
        sql.append(" select sh");
        sql.append(" from ShareHolder sh left join fetch sh.company ").append(" where sh.company.id=?1 ");
        sql.append(" and sh.defunctInd = 'N' and sh.company.defunctInd = 'N'");
        List<ShareHolder> holders = this.entityService.find(sql.toString(), companyId);
        List<ChangeShareholderVo> holderVos = new ArrayList<ChangeShareholderVo>();
        for (ShareHolder s : holders) {
        	holderVos.add(new ChangeShareholderVo(s));
		}
        return holderVos;
	}
	
	public List<ChangeShareholderVo> findChangeShareholderVosBy(ProcRegiCapitalChange preg) {
		List<ChangeShareholderVo> changeShareholderVos = new ArrayList<ChangeShareholderVo>();

		Company company = preg.getCompany();
		if (company.getId() == null) {
			return changeShareholderVos;
		}
		List<ChangeShareholderVo> holderVos = findChangeShareholderVosBy(company.getId());
		if (preg.getId() == null) {
			return holderVos;
		}
		Map<Long, ChangeShareholderVo> shareHolderVoMap = new HashMap<Long, ChangeShareholderVo>();
		for (ChangeShareholderVo vo : holderVos) {
			shareHolderVoMap.put(vo.getShareholderIdOriginal(), vo);
		}
		List<ProcRegiCapitalChangeShareholder> css = preg.getProcRegiCapitalChangeShareholders();
		for (ProcRegiCapitalChangeShareholder cs : css) {
			if("N".equals(cs.getDefunctInd())) {
				ChangeShareholderVo oldSolderVo = shareHolderVoMap.get(cs.getShareholderIdOriginal());
				if (oldSolderVo == null) {
					oldSolderVo = new ChangeShareholderVo();
					oldSolderVo.setShareholderName(cs.getShareholderName());
					oldSolderVo.getShareHolders().add(oldSolderVo);
				} 
				oldSolderVo.getShareHolders().add(new ChangeShareholderVo(cs));
				changeShareholderVos.add(oldSolderVo);
			}
		}
		return changeShareholderVos;
	}

	public void updateCampanyByChange(ProcRegiCapitalChange preg) {
		String user = loginService.getCurrentUserName();
		Date date = new Date();
		Company company = preg.getCompany();
		company.setInvestTotal(preg.getInvestTotal());
		company.setInvestCurrency(preg.getInvestCurrency());
		company.setIsInvestRegRemaAvai(preg.getIsInvestRegRemaAvai());
		company.setInvestRegRemaFunds(preg.getInvestRegRemaFunds());
		company.setInvestRegRemaFundsCu(preg.getInvestRegRemaFundsCu());
		company.setUpdatedBy(user);
		company.setUpdatedDatetime(date);
		entityService.update(company);
		
		List<ShareHolder> holders = shareHolderService.findShareHolderListByCp(company.getId());
		Map<Long, ShareHolder> shareHolderMap = new HashMap<Long, ShareHolder>();
		for (ShareHolder shareHolder : holders) {
			shareHolderMap.put(shareHolder.getId(), shareHolder);
		}
		
		List<ProcRegiCapitalChangeShareholder> shareholders = preg.getProcRegiCapitalChangeShareholders();
		
		for (ProcRegiCapitalChangeShareholder sh : shareholders) {
			if("N".equals(sh.getDefunctInd())) {
				ShareHolder shareHolder = shareHolderMap.get(sh.getShareholderIdOriginal());
				if(shareHolder == null) {
					shareHolder = new ShareHolder();
					shareHolder.setCompany(company);
					shareHolder.setCreatedBy(user);
					shareHolder.setCreatedDatetime(date);
				}
				shareHolder.setUpdatedBy(user);
				shareHolder.setUpdatedDatetime(date);
				
				shareHolder.setDefunctInd("删除".equals(sh.getStatus()) ? "Y": "N");
				
				shareHolder.setEquityPerc(sh.getEquityPerc());
				shareHolder.setFondsCurrency(sh.getFondsCurrency());
				shareHolder.setFondsInPlace(sh.getFondsInPlace());
				shareHolder.setFondsTotal(sh.getFondsTotal());
				shareHolder.setIsEquityRelated(sh.getIsEquityRelated());
				shareHolder.setRelatedPerc(sh.getRelatedPerc());
				shareHolder.setShareHolderName(sh.getShareholderName());
				
				entityService.update(shareHolder);
			}
		}
		
		preg.setProcessStatus("0");
		entityService.update(preg);
		
	}
	
	public boolean hasCapitalChange(ProcRegiCapitalChange preg) {
		StringBuilder jpql = new StringBuilder();
		jpql.append(" select p from ProcRegiCapitalChange p");
		jpql.append(" where p.defunctInd = 'N'");
		jpql.append(" and p.processStatus = '1'");
		jpql.append(" and p.company.id = ?1");
		List<ProcRegiCapitalChange> prcs = this.entityService.find(jpql.toString(), preg.getCompany().getId());
		return prcs.size() > 0;
	}
}
