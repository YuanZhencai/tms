package com.wcs.tms.service.process.bankcreditbloc;

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
import com.wcs.tms.model.ProcBankCreditBloc;
import com.wcs.tms.model.ProcBankCreditBlocCompany;
import com.wcs.tms.model.ProcBankCreditBlocCompanyConfirm;
import com.wcs.tms.service.process.common.ProcessUtilMapService;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 银行授信集团申请Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Stateless
public class BankCreditBlocService implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Inject
    private PEManager peManager;
    @Inject
    private EntityService entityService;
    @Inject
    private MailService mailService;
    @Inject
    private SendMailService sendMailService;
    @Inject
    private LoginService loginService;
    @Inject
    ProcessUtilMapService processUtilMapService;//9.10
    
    private static final Log log = LogFactory.getLog(BankCreditBlocService.class);

    /**
     * 
     * <p>Description:发起流程申请 </p>
     * @param processName
     * @param regQueue
     * @param comment
     * @return
     * @throws ServiceException
     */
    public String vwCreateProcessInstance(String processName, String regQueue, String comment) throws ServiceException {
        String workflowNumber = "";
        // 检查FN是否存在该流程
        if (peManager.checkWorkClassName(processName)) {
            try {
                workflowNumber = peManager.vwCreateInstance(processName, comment);
                peManager.vwLauchStep(workflowNumber, regQueue, comment);
                return workflowNumber;
            } catch (Exception e) {
                log.error("vwCreateProcessInstance方法 发起流程申请  出现异常：",e);
                throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
            }
        } else {
            throw new ServiceException(ExceptionMessage.FN_NO_CLASS);
        }
    }

    /**
     * 
     * <p>Description: 根据Id得到银行授信集团申请对象</p>
     * @param blocId
     * @return
     * @throws ServiceException
     */
    public ProcBankCreditBloc findBankBlocRegByPBlocId(Long blocId) throws ServiceException {
        Validate.notNull(blocId, "银行授信集团申请Id为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select pb from ProcBankCreditBloc pb left join fetch pb.company ").append(
                    " left join fetch pb.bank where pb.id=?1   and pb.defunctInd = 'N'");
            List<ProcBankCreditBloc> blocCreditList = this.entityService.find(bulder.toString(), blocId);
            if (blocCreditList.size() > 1) { throw new ServiceException("该流程存在多条授信申请（集团）"); }
            return blocCreditList.get(0);
        } catch (Exception e) {
            log.error("findBankBlocRegByPBlocId方法 根据Id得到银行授信集团申请对象 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description:根据流程实例编号得到银行授信集团申请对象 </p>
     * @param processId
     * @return
     */
    public ProcBankCreditBloc findBankBlocRegByProcessId(String processId) {
        Validate.notNull(processId, "银行授信集团申请流程实例编号为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select pb from ProcBankCreditBloc pb left join fetch pb.company ").append(
                    " left join fetch pb.bank where pb.procInstId=?1   and pb.defunctInd = 'N'");
            List<ProcBankCreditBloc> blocCreditList = this.entityService.find(bulder.toString(), processId);
            if (blocCreditList.size() > 1) { throw new ServiceException("该流程存在多条授信申请（集团）"); }
            return blocCreditList.get(0);
        } catch (Exception e) {
            log.error("findBankBlocRegByProcessId方法 根据流程实例编号得到银行授信集团申请对象 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 得到集团下所有的公司</p>
     * @return
     * @throws ServiceException
     */
    public List<Company> findAllCompany() throws ServiceException {
        try {
            StringBuilder jpql = new StringBuilder(
                    "select c from Company c where c.defunctInd = 'N' and c.status = 'Y' and c.corporationFlag = 'N'");
            return this.entityService.find(jpql.toString());
        } catch (Exception e) {
            log.error("findAllCompany方法 得到集团下所有的公司 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 查詢一個公司是否在一家總行下面的支行是否存在授信記錄</p>
     * @param companyId
     * @param topBankId
     * @return
     * @throws ServiceException
     */
    public boolean isExistCredit(Long companyId, Long topBankId) throws ServiceException {
        Validate.notNull(companyId, "公司Id为空");
        Validate.notNull(topBankId, "銀行Id为空");
        try {
            StringBuilder jpql = new StringBuilder();
            jpql.append("select count(credit.id) from Credit credit where credit.company.id=?1 and ").append(
                    " credit.bank.topBankId=?2");
            List<Long> list = entityService.find(jpql.toString(), companyId, topBankId);
            if (list.get(0) > 0) { return true; }
            return false;
        } catch (Exception e) {
            log.error("isExistCredit方法 查詢一個公司是否在一家總行下面的支行是否存在授信記錄 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 根据银行集团流程实例编号查询已经提交申请的公司</p>
     * @param processInd 集团公司流程实例编号
     * @return
     * @throws ServiceException
     */
    public List<ProcBankCreditBlocCompany> findBlocCompanyByProcId(String processInd) throws ServiceException {
        Validate.notNull(processInd, "银行授信集团流程实例编号为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append(
                    "select blocCompany from ProcBankCreditBlocCompany blocCompany left  join fetch blocCompany.company left join fetch blocCompany.bank ")
                    .append("where blocCompany.status <> '0' and blocCompany.defunctInd = 'N'  ")
                    .append("and blocCompany.status <> '4' ").append("and  blocCompany.procBankCreditBloc.id in(")
                    .append("select bankBloc.id from ProcBankCreditBloc bankBloc where  ")
                    .append("bankBloc.defunctInd = 'N' and bankBloc.procInstId=?1)");
            return entityService.find(bulder.toString(), processInd);
        } catch (Exception e) {
            log.error("findBlocCompanyByProcId方法 根据银行集团流程实例编号查询已经提交申请的公司 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 根据集团流程实例编号查询成员公司确认单</p>
     * @param processInd
     * @return
     * @throws ServiceException
     */
    public List<ProcBankCreditBlocCompanyConfirm> findBlocCompanyConfirmByProcId(String processInd) throws ServiceException {
        Validate.notNull(processInd, "银行授信集团流程实例编号为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append(
                    "select blocCompany from ProcBankCreditBlocCompanyConfirm blocCompany left  join fetch blocCompany.company left join fetch blocCompany.bank ")
                    .append("where blocCompany.status <> '0' and blocCompany.defunctInd = 'N'  ")
                    .append("and blocCompany.status <> '4' ")
                    .append("and  blocCompany.procBankCreditBlocConfirm.procBankCreditBloc.id in(")
                    .append("select bankBloc.id from ProcBankCreditBloc bankBloc where  ")
                    .append("bankBloc.defunctInd = 'N' and bankBloc.procInstId=?1)");
            return entityService.find(bulder.toString(), processInd);
        } catch (Exception e) {
            log.error("findBlocCompanyConfirmByProcId方法 根据集团流程实例编号查询成员公司确认单 出现异常：",e);
            throw new ServiceException(e);
        }
    }
    
    
    /**
     * 
     * <p>Description: 否决成员公司申请(成员公司列表)</p>
     * @param processBlocCompany
     */
    public void bankCreditBlocRejectRequest(ProcBankCreditBlocCompany processBlocCompany) throws ServiceException {
    	try {
    		processBlocCompany = entityService.find(ProcBankCreditBlocCompany.class, processBlocCompany.getId());
	    	processBlocCompany.setStatus("5");
	        entityService.update(processBlocCompany);
	    } catch (Exception e) {
	        log.error("bankCreditBlocRejectRequest方法 否决成员公司申请 出现异常：",e);
	        throw new ServiceException(e);
	    }
    }


    /**
     * 
     * <p>Description: 集团确认（否决）成员公司邮件通知</p>
     * @param adAcount
     * @param stepName
     * @param workClassName
     * @param processId
     * @param noticeMsg
     */
    public void mailNoticeBlocCompany(String adAcount, String stepName, String workClassName, String processId, String noticeMsg)
            throws ServiceException {
        try {
            List<Mail> mailList = new ArrayList<Mail>();
            // 根据账户得到人
            P p = null;
            if (adAcount != null) {
                p = mailService.findEmailByAccount(adAcount);
            }
            if (p == null) { return; }
            Mail m = new Mail();
            m.setTelno(p.getCelno());
            m.setEmail(p.getEmail());
            m.setSubject(mailService.generationTitle(MailUtil.MailTypeEnum.Notice, workClassName, processUtilMapService.getTmsIdByFnId(processId), stepName,
                    loginService.getCurrentUserName(), null));
            m.setBody(mailService.generationContent(MailUtil.MailTypeEnum.Notice, workClassName, processUtilMapService.getTmsIdByFnId(processId), stepName,
                    loginService.getCurrentUserName(), null, null) + noticeMsg);
            mailList.add(m);
            sendMailService.send(mailList);
        } catch (Exception e) {
            log.error("mailNoticeBlocCompany方法 集团确认（否决）成员公司邮件通知 出现异常：",e);
            throw new ServiceException(e);
        }

    }
}
