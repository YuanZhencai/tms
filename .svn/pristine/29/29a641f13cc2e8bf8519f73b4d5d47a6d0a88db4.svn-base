package com.wcs.tms.service.process.common;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jws.WebParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wcs.base.util.StringUtils;
import com.wcs.base.util.des.FileUtils;
import com.wcs.common.filenet.env.SysCfg;
import com.wcs.common.util.FtpUtil;
import com.wcs.common.webservice.WSService;
import com.wcs.tms.mail.Mail;
import com.wcs.tms.mail.MailService;
import com.wcs.tms.mail.SendMailService;
import com.wcs.tms.service.system.interfaces.PayService;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:Ftp生成txt文件Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
@Stateless
public class FtpUploadService {
	
	private static final Log log = LogFactory.getLog(FtpUploadService.class);
	
	@Inject
    private PayService payService;
	@Inject
    MailService mailService;
    @Inject
    SendMailService sendMailService;
    /**
     * 
     * <p>Description:ftp生成txt文件 </p>
     * @param processId 流程实例ID
     * @param processDefineName 流程定义名称。如：TMS_DailyPayLoanTran
     * @param payDatetime 付款日期
     * @param isSungard 是否为sungard付款
     * @return
     */
	public String tmsFtpUploadFile(String processId,String processDefineName,
			Date payDatetime, Boolean isSungard){
		log.info("payDatetime:"+payDatetime);
        
		long startTime = System.currentTimeMillis();
        if (StringUtils.isBlankOrNull(processId) || StringUtils.isBlankOrNull(processDefineName)) { throw new RuntimeException(
                "调用ftp上传的WebService接口，参数不能为空！"); }
        log.info("--上传文件至ftp-流程实例ID--" + processId);
        log.info("--上传文件至ftp-流程定义名称--" + processDefineName);
        
        String timeOutSec =SysCfg.getStrConfig("ftp.generate.file.timeout");
        log.info("开始生成加密文件，上传至ftp...");      
        log.info("当前线程暂停："+timeOutSec+"秒");
        
       //modify by liushengbin 20130712 防止付款的记录在数据库中还未生成，会导致生成付款记录文件内容有问题，所以把当前线程暂停一会
//        try {
//            Thread.currentThread().sleep(Long.valueOf(timeOutSec)*1000);
//        } catch (NumberFormatException e) {
//            log.error("付款加密指令文件上传，线程暂停时出错：",e);
//        } catch (InterruptedException e) {
//            log.error("付款加密指令文件上传，线程暂停时出错：",e);
//        }
        log.info("=============input param isSungard=============" + isSungard);
        if (!isSungard.booleanValue()) { return "Not Sungard payment,cancel pay!"; }

        String result = "";
        // 获取txt文件的内容,和文件名称
        String[] fileMsg = payService.generateTxtFileProp(processId, processDefineName,payDatetime);
        // 文件名命名规则：BPM+接口编号(01)+SAP公司代码后2位+BPM流程编号
        String fileName = fileMsg[0];
        // 上传至ftp服务器
        String ftpServer = SysCfg.getStrConfig("ftp.server");
        int ftpPort = Integer.valueOf(SysCfg.getStrConfig("ftp.port"));
        String ftpUserName = SysCfg.getStrConfig("ftp.username");
        String ftpPassword = SysCfg.getStrConfig("ftp.password");
        String ftpHomePath = SysCfg.getStrConfig("ftp.home.path");
        log.info("=======ftp server:===========" + ftpServer);
        log.info("=======ftp ftpPort:===========" + ftpPort);
        log.info("=======ftp ftpUserName:===========" + ftpUserName);
        log.info("=======ftp ftpHomePath:===========" + ftpHomePath);

        // modify by liushengbin 新加需求，加密文件写入ftp的同时，写入TMS服务端磁盘
        FileUtils.write2FileByString(SysCfg.getStrConfig("disk.home.path"), fileName, fileMsg[1], "UTF-8");

        FtpUtil ftpUtil = new FtpUtil(ftpServer, ftpPort, ftpUserName, ftpPassword, ftpHomePath);
        boolean isUploadSuccess = ftpUtil.uploadFile(fileMsg[1], fileName);
        //modify by liushengbin 2013-07-15 付款文件发生问题，及时邮件给系统管理员
        if(!isUploadSuccess){
            sendErrorInfo2Admin(processId,processDefineName,"ftpNetError","<br>加密文件名是："+fileName);
        } 
        result = fileMsg[2];
        long endTime = System.currentTimeMillis();

        log.info("=======准备加密文件，以及上传至ftp和本地服务器生成成功！===========耗时："+(endTime-startTime));
        return result;
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
}
