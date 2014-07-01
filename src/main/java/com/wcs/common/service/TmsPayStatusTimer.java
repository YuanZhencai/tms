package com.wcs.common.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.util.des.DESEncrypt;
import com.wcs.common.filenet.env.SysCfg;
import com.wcs.common.util.FtpUtil;
import com.wcs.sys.ejbtimer.annotation.WCSTimerClass;
import com.wcs.sys.ejbtimer.annotation.WCSTimerMethod;
import com.wcs.sys.ejbtimer.consts.BusiLogLevel;
import com.wcs.sys.ejbtimer.interfaces.EJBTimerLogContext;
import com.wcs.tms.mail.Mail;
import com.wcs.tms.mail.MailService;
import com.wcs.tms.mail.SendMailService;
import com.wcs.tms.model.ProcTMSStatus;
import com.wcs.tms.service.process.common.TmsImportStatusService;
import com.wcs.tms.service.process.common.TmsStatusService;

@Stateless
@WCSTimerClass
public class TmsPayStatusTimer {

	private Log log = LogFactory.getLog(TmsPayStatusTimer.class);

	// tab分隔符
	private static final String TAB_SPLIT = "\t";
	// tab换行符
	private static final String LINE_SPLIT = "\n";
	// 定时任务，每次读取ftp文件个数
	private static final int MAX_FILE_COUNT = 100;

	@EJB
	private TmsStatusService tmsStatusService;
	@EJB
	private TmsImportStatusService tmsImportStatusService;
	@EJB
	private MailService mailService;
	@EJB
	private SendMailService sendMailService;

	@WCSTimerMethod(subject = "定时更新TMS支付状态", cronExpression = "0 0/2 * * * ?", desc = "读取FTP文件夹内加密文件更新支付状态")
	public void updateTmsPayStatus(EJBTimerLogContext logContext)
			throws Exception {
		// txt文件内容加密
		DESEncrypt desEncrypt = new DESEncrypt();
		// 上传至ftp服务器
		String ftpServer = SysCfg.getStrConfig("ftp.server");
		int ftpPort = Integer.valueOf(SysCfg.getStrConfig("ftp.port"));
		String ftpUserName = SysCfg.getStrConfig("ftp.username");
		String ftpPassword = SysCfg.getStrConfig("ftp.password");
		String ftpHomePath = SysCfg.getStrConfig("ftp.home.path");
		String tmsInPath = SysCfg.getStrConfig("ftp.TMS.in.path");
		String tmsBakPath = SysCfg.getStrConfig("ftp.TMS.out.path");
		log.info("=======ftp server:===========" + ftpServer);
		log.info("=======ftp ftpPort:===========" + ftpPort);
		log.info("=======ftp ftpUserName:===========" + ftpUserName);
		log.info("=======ftp ftpHomePath:===========" + ftpHomePath);
		FtpUtil ftpUtil = new FtpUtil(ftpServer, ftpPort, ftpUserName,
				ftpPassword, ftpHomePath);
		ftpUtil.setFtpTMSINPath(tmsInPath);
		ftpUtil.setFtpTMSBakPath(tmsBakPath);

		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss",
				Locale.ENGLISH);

		// 获取tmsInPath 下的文件和内容,限定一次获取MAX_FILE_COUNT个文件
		Map<String, String> filesMap = ftpUtil.downloadFile(MAX_FILE_COUNT);
		for (Map.Entry<String, String> entry : filesMap.entrySet()) {
			String fileName = entry.getKey();
			String fileContent = entry.getValue();
			try {
				log.info("file:" + fileName);
				String decryptStr = desEncrypt.decrypt(fileContent);
				String[] lins = decryptStr.split(LINE_SPLIT);
				for (int i = 0; i < lins.length; i++) {
					String[] str = lins[i].split(TAB_SPLIT);
					String sysId = str[0];
					String bpmId = str[1];
					String taxType = str[2];
					String tmsAmount = str[3];
					String status = str[4];
					String payDetail = str[5];
					String tmsRefNumber = str[6];
					String payDateStr = str[7];
					String applicationDateStr = str[8];
					Date payDate = sdf.parse(payDateStr);
					Date applicationDate = df.parse(applicationDateStr);

					log.info("sysId:" + sysId + " bpmId:" + bpmId + " taxType:"
							+ taxType + " tmsAmount:" + tmsAmount);
					log.info("status:" + status + " payDetail:" + payDetail
							+ " tmsRefNumber:" + tmsRefNumber + " payDateStr:"
							+ payDateStr);
					log.info("applicationDateStr:" + applicationDateStr
							+ " payDate:" + payDate + " applicationDate:"
							+ applicationDate);

					String bpmno = "BPM" + bpmId.trim();

					if (equalDate(applicationDate)) {

						ProcTMSStatus tmsStatus = tmsStatusService
								.getTmsStatusByBpmId(bpmno);
						log.info("tmsStatus:" + tmsStatus);
						if (tmsStatus != null) {
							//ProcTMSStatus表中TMS_STATUS :TMS状态(1. 未导入 2. 导入成功 3. 导入失败 4. 支付成功 5. 支付失败)
							boolean flag = false;
							tmsStatus.setTmsRefNumber(tmsRefNumber);
							tmsStatus.setPayDetail(payDetail);
							String state = "4";
							//TMS文件中的status：3 支付成功
							if ("3".equals(status)) {
								state = "4";
							} else {
								state = "5";
							}
							if (state.equals(tmsStatus.getTmsStatus())) {
								flag = false;
							} else {
								flag = true;
							}
							tmsStatus.setTmsStatus(state);
							tmsStatus.setPayDate(sdf.parse(payDateStr));
							tmsStatus.setApplicationDate(df
									.parse(applicationDateStr));
							tmsStatus.setTaxType(taxType);
							tmsStatus.setTmsAmount(Double
									.parseDouble(tmsAmount));
							tmsStatusService.updateTmsStatus(tmsStatus);

							logContext.logBusiInfo(BusiLogLevel.INFO,
									"定时任务支付状态更新成功", bpmno + "数据更新成功");

							if (flag) {
								try {
									List<Mail> mailList = mailService
											.getCashPoolPayEmail(tmsStatus);
									sendMailService.send(mailList);
								} catch (Exception ex) {
									ex.printStackTrace();
									logContext.logBusiInfo(BusiLogLevel.ERROR,
											bpmno+"支付状态通知邮件发送失败", "");
								}
							}
						} else {
							logContext.logBusiInfo(BusiLogLevel.ERROR,
									"定时任务支付状态更新失败", bpmno + "数据更新失败，找不到该数据");
						}
					} else {
						logContext.logBusiInfo(BusiLogLevel.INFO,bpmno + "申请日期超出时间，导入支付状态失败!","");
					}
				}

				// 成功更新支付状态后，把tmsInPath下文件，迁移到tmsBakPath的路径下
				String fromfileName = tmsInPath + "/" + fileName;
				String toFileName = tmsBakPath + "/" + fileName;
				log.info("fromFileName:" + fromfileName + "     toFileName:"
						+ toFileName);
				if(ftpUtil.cutFile(fromfileName, toFileName)){
					logContext.logBusiInfo(BusiLogLevel.INFO, "支付状态文件备份成功",
							fromfileName+"成功备份至"+toFileName);
				}else{
					logContext.logBusiInfo(BusiLogLevel.ERROR, "支付状态文件备份失败",
							"");
				}
			} catch (Exception e) {
				log.error("定时任务支付状态更新失败!", e);
				logContext.logBusiInfo(BusiLogLevel.ERROR, "定时任务支付状态更新失败",
						"格式解析错误");
			}

		}
	}

	@WCSTimerMethod(subject = "定时更新TMS导入状态", cronExpression = "0 0/2 * * * ?", desc = "读取TMS数据库BPMINS_STATUS表更新导入状态")
	public void updateTmsImportStatus(EJBTimerLogContext logContext)
			throws Exception {
		log.info("定时器同步TMS导入状态数据方法开始");
		try {

			List<ProcTMSStatus> stateList = tmsImportStatusService
					.getImportStatusList();

			tmsStatusService.updateImportStatus(stateList);

		} catch (Exception e) {
			log.error("updateTmsImportStatus方法 定时更新TMS导入状态", e);
		}
		log.info("定时器同步TMS导入状态数据方法结束");

	}

	private static boolean equalDate(Date date) {
		if (date == null) {
			return false;
		}
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, -14);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		if (date.getTime() > c.getTimeInMillis()) {
			return true;
		}
		return false;
	}
}
