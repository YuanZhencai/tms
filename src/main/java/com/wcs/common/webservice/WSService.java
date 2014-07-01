package com.wcs.common.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wcs.tms.mail.Mail;
import com.wcs.tms.mail.MailService;
import com.wcs.tms.mail.SendMailService;
import com.wcs.tms.service.system.interfaces.PayService;
import com.wcs.tms.util.ProcessDefineUtil;

import filenet.vw.api.VWWorkObjectNumber;

@WebService
public class WSService {

    private static Logger log = LoggerFactory.getLogger(WSService.class);
    @Inject
    private MailService mailService;
    @Inject
    private SendMailService sendMailService;
    @Inject
    private PayService payService;

    /**
     * FileNet的WebService邮件发送指令接口
     * @param processId
     * @param noticeType
     * @return
     */
    @WebMethod(operationName = "tmsMailPerson")
    public String tmsMailPerson(@WebParam(name = "processId2") String processId,
            @WebParam(name = "noticeType") String noticeType) {
        String result = "";

        log.info("--模板邮件-流程实例ID--" + processId);
        log.info("--模板邮件-类型--" + noticeType);

        if (noticeType != null && !"".equals(noticeType)) {
            // 发送申请人
            if ("1".equals(noticeType)) {
                this.sendRequester(processId);

                // 发送下一个待接收人
            } else if ("2".equals(noticeType)) {
                this.sendNext(processId);

                // 发送所有相关人
            } else if ("3".equals(noticeType)) {
                this.sendAll(processId);

                // 发送给之前已处理人员（只适用集团授信、银行授信申请人确认）
            } else if ("4".equals(noticeType)) {
                this.sendHandled(processId);
            }

            // 申请人确认时，给已处理过的人员发送（内部担保，申请人确认后终止）
            else if ("5".equals(noticeType)) {
                this.sendAssure(processId);
            }

            // 内部担保，申请人确认后需重新审批
            else if ("6".equals(noticeType)) {
                this.sendVouchfor(processId);
            }
        }

        return result;
    }

    /**
     * 
     * <p>Description:调用FileNet的WebService生成txt文件 </p>
     * @param processId 流程实例ID
     * @param processDefineName 流程定义名称。如：TMS_DailyPayLoanTran
     * @param isSungard 是否为sungard付款
     * @return
     */
    @WebMethod(operationName = "tmsFtpUploadNew")
    public String tmsFtpUploadNew(@WebParam(name = "processId") String processId,
            @WebParam(name = "processDefineName") String processDefineName,@WebParam(name = "isSungard") Boolean isSungard) {
        return null;
    }
    
    /**
     * 
     * <p>Description:给系统管理员(运维人员等)发送错误信息提醒手工处理 </p>
     * @param processId
     * @param processDefineName
     * @param errorType
     */
    public void sendErrorInfo2Admin(String processId,String processDefineName,String errorType,String extraInfo) {
        List<Mail> mails = mailService.getErrorInfo2AdminEmail(processId, processDefineName, errorType,extraInfo);
        sendMailService.send(mails);
    }

    /**
     * 给申请人发送
     * @param processId
     */
    public void sendRequester(String processId) {
        List<Mail> mails = new ArrayList<Mail>();

        StringBuilder filter = new StringBuilder("1=1 and F_EventType = :EventType");
        Object[] substitutionVars = new Object[2];
        filter.append(" and F_WobNum = :wobNum");
        substitutionVars[0] = ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd);
        substitutionVars[1] = new VWWorkObjectNumber(processId);
        mailService.initMailPE();
        Mail mail = mailService.findRequestMail(processId, filter.toString(), substitutionVars);
        mails.add(mail);

        sendMailService.send(mails);
    }

    /**
     * 给下一个待接收人发送
     * @param processId
     */
    public void sendNext(String processId) {
        List<Mail> mails = new ArrayList<Mail>();
        StringBuilder filter = new StringBuilder("1=1 and F_EventType = :EventType");
        Object[] substitutionVars = new Object[2];
        filter.append(" and F_WobNum = :wobNum");
        substitutionVars[0] = ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd);
        substitutionVars[1] = new VWWorkObjectNumber(processId);
        mailService.initMailPE();
        mails = mailService.findWaitPerMai(processId, filter.toString(), substitutionVars);

        sendMailService.send(mails);
    }

    /**
     * 给所有相关人发送
     * @param processId
     */
    public void sendAll(String processId) {
        List<Mail> mails = new ArrayList<Mail>();
        StringBuilder filter = new StringBuilder("1=1 and F_EventType = :EventType");
        Object[] substitutionVars = new Object[2];
        filter.append(" and F_WobNum = :wobNum");
        substitutionVars[0] = ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd);
        substitutionVars[1] = new VWWorkObjectNumber(processId);
        mailService.initMailPE();
        mails = mailService.findAllEmailByProcess(filter.toString(), substitutionVars);

        sendMailService.send(mails);
    }

    /**
     * 申请人确认时，给已处理过的人员发送（集团授信、银行授信）
     * @param processId
     */
    public void sendHandled(String processId) {
        List<Mail> mails = new ArrayList<Mail>();
        StringBuilder filter = new StringBuilder("1=1 and F_EventType = :EventType");
        Object[] substitutionVars = new Object[2];
        filter.append(" and F_WobNum = :wobNum");
        substitutionVars[0] = ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd);
        substitutionVars[1] = new VWWorkObjectNumber(processId);
        mailService.initMailPE();
        mails = mailService.findAllHandledByProcess(filter.toString(), substitutionVars);
        sendMailService.send(mails);
    }

    /**
     * 申请人确认时，给已处理过的人员发送（内部担保，申请人确认后终止）
     * @param processId
     */
    public void sendAssure(String processId) {
        List<Mail> mails = new ArrayList<Mail>();
        StringBuilder filter = new StringBuilder("1=1 and F_EventType = :EventType");
        Object[] substitutionVars = new Object[2];
        filter.append(" and F_WobNum = :wobNum");
        substitutionVars[0] = ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd);
        substitutionVars[1] = new VWWorkObjectNumber(processId);
        mailService.initMailPE();
        mails = mailService.findAssureByProcess(filter.toString(), substitutionVars);
        sendMailService.send(mails);
    }

    /**
     * 内部担保，申请人确认后需重新审批
     * @param processId
     */
    public void sendVouchfor(String processId) {
        List<Mail> mails = new ArrayList<Mail>();
        StringBuilder filter = new StringBuilder("1=1 and F_EventType = :EventType");
        Object[] substitutionVars = new Object[2];
        filter.append(" and F_WobNum = :wobNum");
        substitutionVars[0] = ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd);
        substitutionVars[1] = new VWWorkObjectNumber(processId);
        mailService.initMailPE();
        mails = mailService.findVouchforByProcess(processId, filter.toString(), substitutionVars);
        sendMailService.send(mails);
    }
}
