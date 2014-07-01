package com.wcs.tms.service.process.bankcredit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.common.model.P;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.mail.Mail;
import com.wcs.tms.mail.MailService;
import com.wcs.tms.mail.MailUtil;
import com.wcs.tms.mail.SendMailService;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcBankCredit;
import com.wcs.tms.model.ProcRptCredit;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWWorkObject;
import filenet.vw.api.VWWorkObjectNumber;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Stateless
public class BankCreditService implements Serializable {
    @Inject
    private PEManager peManager;
    @Inject
    private EntityService entityService;
    @Inject
    private MailService mailService;
    @Inject
    private LoginService loginService;
    @Inject
    ProcessUtilMapService processUtilMapService;
    @Inject
    SendMailService sendMailService;
    
    private static final Log log = LogFactory.getLog(BankCreditService.class);
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     * <p>Description: 发起流程申请</p>
     * @param processName
     * @param comment
     * @return
     */
    public String vwCreateProcessInstance(String processName, String comment) throws ServiceException {
        String workflowNumber = "";
        // 检查FN是否存在该流程
        if (peManager.checkWorkClassName(processName)) {
            try {
                workflowNumber = peManager.vwCreateInstance(processName, comment);
                peManager.vwLauchStep(workflowNumber, "TMS_Requester", comment);
                return workflowNumber;
            } catch (Exception e) {
            	log.error("vwCreateProcessInstance方法 发起流程申请 出现异常：",e);
                throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
            }
        } else {
            throw new ServiceException(ExceptionMessage.FN_NO_CLASS);
        }
    }

    /**
     * 
     * <p>Description: 添加流程授信其它产品</p>
     * @param proRptCreditList
     * @throws ServiceException
     */
    public void batchAddCreditRpt(List<ProcRptCredit> proRptCreditList, String workClassnumber) throws ServiceException {
        Validate.notNull(workClassnumber, "流程实例编号为空");
        try {
            // 判断是否需要删除 通过比较内存的对象与受管对象大小
            List<ProcRptCredit> dataList = findProcRptCreditList(workClassnumber);
            for (ProcRptCredit rpt : dataList) {
                if (!proRptCreditList.contains(rpt)) {
                    rpt.setDefunctInd("Y");
                    this.entityService.update(rpt);
                }
            }
            if (proRptCreditList.isEmpty()) { return; }
            if (!proRptCreditList.isEmpty()) {
                for (ProcRptCredit rp : proRptCreditList) {
                    rp.setProcInstId(workClassnumber);
                    if (rp.getId() != null) {
                        entityService.update(rp);
                    } else {
                        entityService.create(rp);
                    }
                }
            }
        } catch (Exception e) {
            log.error("batchAddCreditRpt方法 添加流程授信其它产品 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 根据流程实例查询银行授信流程申请明细产品</p>
     * @param processInd
     * @return
     * @throws ServiceException
     */
    public List<ProcRptCredit> findProcRptCreditList(String processInd) throws ServiceException {
        Validate.notNull(processInd, "流程实例编号为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select rpt from ProcRptCredit rpt where rpt.procInstId=?1 and rpt.defunctInd = 'N'");
            return entityService.find(bulder.toString(), processInd);
        } catch (Exception e) {
            log.error("findProcRptCreditList方法 根据流程实例查询银行授信流程申请明细产品 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 根据产品名称查询是否存在同样的产品</p>
     * @param processInd
     * @param rptName
     * @return
     * @throws ServiceException
     */
    public boolean findProcRptCreditByName(String processInd, String rptName) throws ServiceException {
        Validate.notNull(processInd, "流程实例编号为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select rpt from ProcRptCredit rpt where rpt.procInstId=?1 and rpt.defunctInd = 'N' and rpt.cdProName=?2");
            List<ProcRptCredit> list = this.entityService.find(bulder.toString(), processInd, rptName);
            if (list.size() > 1) { return true; }
            return false;
        } catch (Exception e) {
            log.error("findProcRptCreditByName方法 根据产品名称查询是否存在同样的产品 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 根据流程实例编号查询银行授信记录</p>
     * @param processInd
     * @return
     * @throws ServiceException
     */
    public ProcBankCredit findProcBankCd(String processInd) throws ServiceException {
        Validate.notNull(processInd, "流程实例编号为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select pcd from ProcBankCredit pcd left join fetch pcd.company ")
                    .append(" left join fetch pcd.bank ").append("where pcd.procInstId=?1 and pcd.defunctInd = 'N'");
            List<ProcBankCredit> pBankCdList = this.entityService.find(bulder.toString(), processInd);
            if (pBankCdList.isEmpty()) { return null; }
            if (pBankCdList.size() > 1) { throw new ServiceException("该流程实例".concat(processInd).concat("相关银行授信记录存在多条数据")); }
            return pBankCdList.get(0);
        } catch (Exception e) {
            log.error("findProcBankCd方法 根据流程实例编号查询银行授信记录 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 银行授信审批</p>
     * @param workClassNum
     * @param paramMap
     * @param memo
     * @throws ServiceException
     */
    public void vwDisposeTask(String workClassNum, boolean isPass, String memo) throws ServiceException {
        Validate.notNull(workClassNum, "银行授信申请流程对应流程实例编号为空");
        try {
            this.peManager.vwDisposeTask(workClassNum, isPass, memo);
        } catch (Exception e) {
            log.error("vwDisposeTask方法 银行授信审批 出现异常：",e);
            throw new ServiceException(ExceptionMessage.PREGITCAPTIAL_SAVE, e);
        }
    }

    /**
     * 
     * <p>Description: 重新申请</p>
     * @param memo
     * @param workflowNumber
     * @param documentList
     * @throws ServiceException
     */
    public void vwReplayBankRegister(String memo, String workflowNumber) throws ServiceException {
        try {
            peManager.vwSubmitApply(memo, workflowNumber, null);
        } catch (Exception e) {
            log.error("vwReplayBankRegister方法 重新申请 出现异常：",e);
            throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
        }
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
			log.error("getProcessDetail方法 查询流程详细 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return detailVos;
	}

	/**
	 * 知会申请人流程实例编号(add on 2013-1-21 by yanchangjing)
	 * @param procBankCredit 
	 * @param stepName 
	 * @throws Exception 
	 */
	public void mailApplicant(ProcBankCredit procBankCredit, String stepName ) throws Exception {
		List<Mail> mailList = new ArrayList<Mail>();
		// 邮件业务内容
		StringBuilder bussMailBody = new StringBuilder("您申请的“银行授信申请审批流程”,申请单位为:"+procBankCredit.getCompany().getCompanyName()
				+"，“集团资金部门经理审批”已通过,因为您申请的该流程本次担保方式中涉及到担保，担保额度为："+procBankCredit.getGuaranteeGr()+"万元，所以您需要发起一个“内部担保申请流程”" +
						"，请在系统“流程”菜单中选择“发起新流程”进行对应操作，特此告知。");
		// 根据流程实例编号查出流程申请人
		P requester =  this.findRequesterByProcessInstId(procBankCredit.getProcInstId());
					Mail m = new Mail();
					m.setTelno(requester.getCelno());
					m.setEmail(requester.getEmail());
					m.setSubject(mailService.generationTitle(
							MailUtil.MailTypeEnum.Notice, "TMS_BankCreditReg",
							processUtilMapService
									.getTmsIdByFnId(procBankCredit
											.getProcInstId()), stepName,
							loginService.getCurrentUserName(), null));
					m.setBody(mailService.generationContent(
							MailUtil.MailTypeEnum.Notice, "TMS_BankCreditReg",
							processUtilMapService
									.getTmsIdByFnId(procBankCredit
											.getProcInstId()), stepName,
							loginService.getCurrentUserName(), null, null)
							+ bussMailBody.toString());
					mailList.add(m);
		sendMailService.send(mailList);

	}
	
	/**
	 * 根据流程实例编号查出流程申请人
	 * @param procInstId
	 * @return
	 * @throws Exception
	 */
	private P findRequesterByProcessInstId(String procInstId) throws Exception {
		VWWorkObject object = peManager.vwGetTmsWorkObject(procInstId);
		// 申请人用户名
		String requester = object.getOriginator();
        return mailService.findEmailByAccount(requester);
	}

	/**
	 * 得到集团公司sap代码
	 */
	private List<Company> findBlocSapCode() {
		StringBuilder jpql = new StringBuilder(
				"select c from Company c where c.defunctInd = 'N' and c.corporationFlag='Y'");
		return entityService.find(jpql.toString());
	}
	
	
}
