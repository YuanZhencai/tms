package com.wcs.tms.view.process.regicapital;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.tms.mail.Mail;
import com.wcs.tms.mail.SendMailService;

//@WebService
public class UNS {
    @Inject
    private SendMailService sendMailService;
    
    private static final Log log = LogFactory.getLog(UNS.class);

    //@WebMethod
    public String sendMsg(String stepName) {
        Mail mail = new Mail();
        mail.setEmail("chenlong@wcs-global.com");
        mail.setBody("你好！");
        mail.setPernr("");
        mail.setTelno("13880843584");
        mail.setSubject("TMS测试");
        mail.setAux("");
        Mail mail1 = new Mail();
        mail1.setEmail("shenbo@wcs-global.com");
        mail1.setBody("你好！TMS测试");
        mail1.setPernr("");
        mail1.setTelno("13880843584");
        mail1.setSubject("TMS测试");
        mail1.setAux("");
        List<Mail> mailList = new ArrayList<Mail>();
        mailList.add(mail);
        mailList.add(mail1);
        sendMailService.send(mailList);
        String msg = "send msg to " + stepName;
        log.info(msg);
        return msg;
    }
}
