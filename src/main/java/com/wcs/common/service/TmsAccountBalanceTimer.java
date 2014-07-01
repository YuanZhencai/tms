package com.wcs.common.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.MathUtil;
import com.wcs.common.filenet.env.SysCfg;
import com.wcs.sys.ejbtimer.annotation.WCSTimerClass;
import com.wcs.sys.ejbtimer.annotation.WCSTimerMethod;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.CompanyAccountBalance;
import com.wcs.tms.service.process.common.TmsAccountBalanceService;


@Stateless
@WCSTimerClass
public class TmsAccountBalanceTimer {
	private Log log = LogFactory.getLog(TmsAccountBalanceTimer.class);
	
	@EJB
	EntityService entityService ;
	@EJB
	TmsAccountBalanceService tmsAccountBalanceService;
	
	/**
	 * 根据TMS系统数据库账户余额表 更新 账户余额表
	 */
	@WCSTimerMethod(subject = "根据TMS余额表定时同步公司账号余额", cronExpression = "0 0/2 * * * ?", desc = "TMS提供余额表sgPayment_QuotaBalance")
	public void updateBalanceByTmsDatabase() {
		try {
			log.info("根据TMS系统数据库账户余额表 更新 账户余额表开始");
			List<Object[]> rows = tmsAccountBalanceService.getRows();
			for(Object[] r:rows){
				this.saveAccountBalanceByRow(r);
			}
			log.info("根据TMS系统数据库账户余额表 更新 账户余额表成功");
		} catch (Exception e) {
			log.error("updateBalanceByTmsDatabase方法 根据TMS系统数据库账户余额表 更新 账户余额表：",e);
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveAccountBalanceByRow(Object[] r) {
		CompanyAccountBalance accountBalance = new CompanyAccountBalance();
		accountBalance.setAccount((String)r[0]);
		accountBalance.setAvailableAmount(Double.parseDouble((String)r[1]));
		accountBalance.setTransDatetime((Date)r[2]);
		accountBalance.setFtpFlag("N");
		entityService.create(accountBalance);
	}

	/**
	 * 根据TMS系统提供xml文件 更新 账户余额表
	 */
	@WCSTimerMethod(subject = "根据上传FTP xml文件定时同步公司账号余额", cronExpression = "0 0/2 * * * ?", desc = "检索ftp上bankbalance目录中的xml文件并且备份到bankbalancebak目录下，每次解析100文件，注意定时器时间设置问题。")
	public void updateBalanceByTmsXmlFile() {
		FTPClient ftpClient = new FTPClient();
			try {
				String ftpServer = SysCfg.getStrConfig("ftp.server");
		        int ftpPort = Integer.valueOf(SysCfg.getStrConfig("ftp.port"));
		        String ftpUserName = SysCfg.getStrConfig("ftp.username");
		        String ftpPassword = SysCfg.getStrConfig("ftp.password");
		        String bankbalancePath = SysCfg.getStrConfig("ftp.TMS.bankbalance.path");
		        String bankbalancebakPath = SysCfg.getStrConfig("ftp.TMS.bankbalancebak.path");
		        log.info("=======ftp server:===========" + ftpServer);
		        log.info("=======ftp ftpPort:===========" + ftpPort);
		        log.info("=======ftp ftpUserName:===========" + ftpUserName);
		        log.info("=======ftp bankbalancePath:===========" + bankbalancePath);
		        log.info("=======ftp bankbalancebakPath:===========" + bankbalancebakPath);
				Date startdate = new Date();
		        ftpClient.connect(ftpServer, ftpPort);
		        log.info("连接耗时:" + (new Date().getTime() - startdate.getTime()));
				ftpClient.login(ftpUserName, ftpPassword);
				log.info("登陆耗时:" + (new Date().getTime() - startdate.getTime()));
				// 返回值
				int reply = ftpClient.getReplyCode();
				if ((!FTPReply.isPositiveCompletion(reply))) {
					log.info("登陆ftp服务器失败,检查用户名或密码是否正确");
					throw new ServiceException(
							"Login ftp server failure. Please check the ftp server["
									+ ftpServer + "] is avalable, And user '"
									+ ftpUserName + "' and the password is right .");
				}
				log.info("开始获取bankbalance目录下的余额xml文件名列表");
				FTPFile[] bankbalanceFiles = ftpClient.listFiles(bankbalancePath);
				log.info("获取bankbalance目录下的余额xml文件名列表成功文件个数为："+bankbalanceFiles.length);
				ftpClient.enterLocalPassiveMode();
				ftpClient.setControlEncoding("UTF-8");
				// 设置文件类型（二进制）
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				// 账号
				List<String> accountNumList = new ArrayList<String>();
				// 账号可用余额
				List<Double> balanceList = new ArrayList<Double>();
				// 统计时间
				List<Date> transTimeList = new ArrayList<Date>();
				for(FTPFile file : bankbalanceFiles){
					// 每次读取100个xml文件
					if(Arrays.asList(bankbalanceFiles).indexOf(file)>100){
						break;
					}
					if(file.isFile()){
						 this.insertDataByXmlFile(file,ftpClient,bankbalancePath,bankbalancebakPath,accountNumList,balanceList,transTimeList);
					}
				}
				// 批量保存数据库
				long start = System.currentTimeMillis();
				for(String account : accountNumList){
					long s = System.currentTimeMillis();
					CompanyAccountBalance accountBalance = new CompanyAccountBalance();
					accountBalance.setFtpFlag("Y");
					accountBalance.setAccount(account);
					accountBalance.setAvailableAmount(balanceList.get(accountNumList.indexOf(account)));
					accountBalance.setTransDatetime(transTimeList.get(accountNumList.indexOf(account)));
					entityService.create(accountBalance);
					long e = System.currentTimeMillis();
					log.info("保存一次数据库耗时："+(e-s));
				}
				long end = System.currentTimeMillis();
				log.info("保存数据库需要时间："+(end - start));
				log.info("根据TMS系统提供xml文件 更新 账户余额表成功！");
			} catch (Exception e) {
				log.error("updateBalanceByTmsXmlFile方法 根据TMS系统提供xml文件 更新 账户余额表", e);
			}finally{
				try {
					ftpClient.logout();
					ftpClient.disconnect();
				} catch (IOException e) {
					log.error("关闭FTP连接发生异常！", e);
				}
			}
		}

	/**
	 * 根据ftp文件读取数据
	 * @param file
	 * @param ftpClient
	 * @param bankbalancePath
	 * @param bankbalancebakPath 
	 * @param transTimeList 
	 * @param balanceList 
	 * @param accountNumList 
	 * @throws IOException 
	 * @throws ParseException 
	 */
	private void insertDataByXmlFile(FTPFile file, FTPClient ftpClient, String bankbalancePath, String bankbalancebakPath, List<String> accountNumList, List<Double> balanceList, List<Date> transTimeList) throws IOException, ParseException {
			log.info("开始解析xml文件，文件名为："+file.getName());
			// 账号
			String accountNum = null;
			// 账号可用余额
			Double balance = null;
			// 统计时间
			Date transTime = null;
			String day = null;
			String sec = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			// 备份xml文件
			String fileName = bankbalancePath + "/" + file.getName();
			String toFileName = bankbalancebakPath + "/" + file.getName();
			ftpClient.rename(fileName, toFileName);
			log.info("fileName:"+fileName + "     toFileName:" + toFileName);
			InputStream is = ftpClient.retrieveFileStream(toFileName);
			if(is != null){
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String line;
				while ((line = reader.readLine()) != null) {
					// 读取账号
					if(line.contains("<acctNo>") && line.contains("</acctNo>")) {
							String[] strArr = line.split("<acctNo>");
							String[] strArr2 = strArr[1].split("</acctNo>");
							accountNum = strArr2[0];
							log.info("账号为： "+accountNum);
					}
					// 读取账号余额
					if(line.contains("<useableBalance>") && line.contains("</useableBalance>")) {
							String[] strArr = line.split("<useableBalance>");
							String[] strArr2 = strArr[1].split("</useableBalance>");
							balance = Double.parseDouble(strArr2[0]);
							log.info("账号余额为： "+balance);
					}
					// 读取统计时间new版本
					if(line.contains("<transDate>") && line.contains("</transDate>")
							&& line.contains("<transTime>") && line.contains("</transTime>")) {
							String[] strArr = line.split("<transDate>");
							String[] strArr2 = strArr[1].split("</transDate>");
							// 年月日
							String d = strArr2[0];
							String[] strArr3 = line.split("<transTime>");
							String[] strArr4 = strArr3[1].split("</transTime>");
							// 时分秒
							String s = strArr4[0];
							transTime = sdf.parse(d+s);
							log.info("统计时间为： "+transTime);
					}
					// 读取统计时间old版本
					if(line.contains("<Date>") && line.contains("</Date>")) {
							String[] strArr = line.split("<Date>");
							String[] strArr2 = strArr[1].split("</Date>");
							// 年月日
							day = strArr2[0];
							log.info("统计时间为： "+transTime);
					}
					if(line.contains("<Time>") && line.contains("</Time>")) {
						String[] strArr = line.split("<Time>");
						String[] strArr2 = strArr[1].split("</Time>");
						// 时分秒
						sec = strArr2[0];
						if(!MathUtil.isEmptyOrNull(day)){
							transTime = sdf.parse(day+sec);
						}
						log.info("统计时间为： "+transTime);
					}
				}
				ftpClient.completePendingCommand();
				reader.close();
				is.close();
			}
			accountNumList.add(accountNum);
			balanceList.add(balance);
			transTimeList.add(transTime);
	}

	
}
