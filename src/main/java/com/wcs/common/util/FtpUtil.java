package com.wcs.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wcs.tms.exception.ServiceException;

/**
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description: ftp上传下载处理类
 * </p>
 * <p>
 * Copyright © 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
public class FtpUtil {

	private static Logger log = LoggerFactory.getLogger(FtpUtil.class);

	private String ftpServer = "";

	private String ftpUserName = "";

	private String ftpPassword = "";

	private String ftpHomePath = "";

	private String ftpTMSINPath = "";

	private String ftpTMSBakPath = "";

	private int port = 21;

	public FtpUtil() {

	}

	public FtpUtil(String ftpServer, int port, String ftpUserName,
			String ftpPassword, String ftpHomePath) {
		this.ftpServer = ftpServer;
		this.ftpUserName = ftpUserName;
		this.ftpPassword = ftpPassword;
		this.ftpHomePath = ftpHomePath;
		if (port != 0) {
			this.port = port;
		}

	}

	public String getFtpTMSINPath() {
		return ftpTMSINPath;
	}

	public void setFtpTMSINPath(String ftpTMSINPath) {
		this.ftpTMSINPath = ftpTMSINPath;
	}

	public String getFtpTMSBakPath() {
		return ftpTMSBakPath;
	}

	public void setFtpTMSBakPath(String ftpTMSBakPath) {
		this.ftpTMSBakPath = ftpTMSBakPath;
	}

	/**
	 * 
	 * <p>
	 * Description:上传文件至FTP服务器
	 * </p>
	 * 
	 * @param str
	 *            字符串
	 * @param fileName
	 *            上传到ftp上的文件名
	 */
	public boolean uploadFile(String str, String fileName) {
		boolean isSuccess = true;

		if (str == null || str.trim().length() == 0) {
			return false;
		}
		InputStream in = getStringStream(str);
		FTPClient ftpClient = new FTPClient();
		Date date = new Date();
		Date startdate = new Date();
		log.info("开始时间:" + date.getTime());
		try {
			ftpClient.connect(ftpServer, port);

			log.info("连接耗时:" + (new Date().getTime() - startdate.getTime()));
			ftpClient.login(ftpUserName, ftpPassword);
			log.info("登陆耗时:" + (new Date().getTime() - startdate.getTime()));
			// 返回值
			int reply = ftpClient.getReplyCode();
			log.info("ftp返回值为：" + reply);
			if ((!FTPReply.isPositiveCompletion(reply))) {
				log.info("登陆ftp服务器失败,检查用户名或密码是否正确");
				throw new ServiceException(
						"Login ftp server failure. Please check the ftp server["
								+ ftpServer + "] is avalable, And user '"
								+ ftpUserName + "' and the password is right .");
			}
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.changeWorkingDirectory(ftpHomePath);
			// Use passive mode as default because most of us are
			// behind firewalls these days.
			ftpClient.enterLocalPassiveMode();
			ftpClient.setControlEncoding("UTF-8");
			// 在FTP服务器上创建目录
			log.info("开始在ftp服务器上检索文件夹");
			mkDir(ftpHomePath, ftpClient);
			log.info("在ftp服务器上检索文件夹结束");
			log.info("start put file to ftp server");
			startdate = new Date();
			ftpClient.storeFile(fileName, in);
			log.info("ftp传输耗时:" + (new Date().getTime() - startdate.getTime()));
			startdate = new Date();
			ftpClient.logout();
			log.info("上传文件至ftp服务器成功:" + fileName);
		} catch (Exception e) {
			isSuccess = false;
			log.error("ftp传输出错了:", e);
		} finally {
			IOUtils.closeQuietly(in);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				log.error("关闭FTP连接发生异常！", e);
			}
		}
		return isSuccess;
	}

	/**
	 * 
	 * <p>
	 * Description:在FTP服务器上创建目录
	 * </p>
	 * 
	 * @param basepath
	 * @param ftp
	 * @throws IOException
	 */
	public static void mkDir(String basepath, FTPClient ftp) throws IOException {
		int exBase = ftp.cwd(basepath);
		if (!FTPReply.isPositiveCompletion(exBase)) {
			exBase = ftp.mkd(basepath);
			if (!FTPReply.isPositiveCompletion(exBase)) {
				log.info("不能创建目录：" + basepath);
				throw new ServiceException("不能创建目录：" + basepath);
			}
			exBase = ftp.cwd(basepath);
		}
	}

	/**
	 * 
	 * <p>
	 * Description:按当前系统 年份+月份创建文件夹
	 * </p>
	 * 
	 * @param basepath
	 *            基础目录
	 * @param ftp
	 * @return
	 * @throws IOException
	 */
	public static String mkDirWithDate(String basepath, FTPClient ftp)
			throws IOException {

		int exBase = ftp.cwd(basepath);
		if (!FTPReply.isPositiveCompletion(exBase)) {
			exBase = ftp.mkd(basepath);
			if (!FTPReply.isPositiveCompletion(exBase)) {
				throw new ServiceException("不能创建目录：" + basepath);
			}
			exBase = ftp.cwd(basepath);
		}

		String filePath = "/upload";
		exBase = ftp.cwd("upload");
		if (!FTPReply.isPositiveCompletion(exBase)) {
			exBase = ftp.mkd("upload");
			if (!FTPReply.isPositiveCompletion(exBase)) {
				throw new ServiceException("不能创建目录：" + basepath + filePath);
			}
			exBase = ftp.cwd("upload");
		}
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		String datePath = format.format(date);
		exBase = ftp.cwd(datePath);
		if (!FTPReply.isPositiveCompletion(exBase)) {
			exBase = ftp.mkd(datePath);
			exBase = ftp.cwd(datePath);
			if (!FTPReply.isPositiveCompletion(exBase)) {
				throw new ServiceException("不能创建目录：" + basepath);
			}
		}
		filePath += "/" + datePath;
		return filePath;
	}

	/**
	 * 
	 * <p>
	 * Description:将一个字符串转化为输入流
	 * </p>
	 * 
	 * @param sInputString
	 * @return
	 */
	public static InputStream getStringStream(String sInputString) {
		if (sInputString != null && !sInputString.trim().equals("")) {
			try {
				ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(
						sInputString.getBytes("UTF-8"));
				return tInputStringStream;
			} catch (Exception ex) {
				log.error("将一个字符串转化为输入流异常", ex);
			}
		}
		return null;
	}

	/**
	 * 获取tmsInPath下的文件名和文件内容
	 * 
	 * @return Map key:文件名，value：文件内容
	 */
	public Map<String, String> downloadFile(int maxFileCount) {
		Map<String, String> map = new HashMap<String, String>();
		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient.connect(ftpServer, port);
			ftpClient.login(ftpUserName, ftpPassword);
			// 返回值
			int reply = ftpClient.getReplyCode();
			log.info("ftp返回值为：" + reply);
			if ((!FTPReply.isPositiveCompletion(reply))) {
				log.info("登陆ftp服务器失败,检查用户名或密码是否正确");
				throw new ServiceException(
						"Login ftp server failure. Please check the ftp server["
								+ ftpServer + "] is avalable, And user '"
								+ ftpUserName + "' and the password is right .");
			}
			String tmsInPath = getFtpTMSINPath();
			String tmsBakPath = getFtpTMSBakPath();

			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.changeWorkingDirectory(tmsInPath);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setControlEncoding("UTF-8");

			if (StringUtils.isNotBlank(tmsInPath)
					&& StringUtils.isNotBlank(tmsBakPath)) {

				log.info("开始在ftp服务器上检索文件夹");
				mkDir(tmsInPath, ftpClient);
				log.info("在ftp服务器上检索文件夹结束");

				List<String> names = getFileList(tmsInPath, ftpClient);
				int count = 0;
				for (String name : names) {
					count++;
					if(count > maxFileCount){
						return map;
					}
					String filePath = tmsInPath + "/" + name;
					InputStream in = ftpClient.retrieveFileStream(filePath);
					map.put(name, inputStream2String(in));
					
					if (in != null) {
						in.close();
					}
					ftpClient.completePendingCommand();
				}
				

			} else {
				log.error("文件夹路径没有设置!");
				return map;
			}
		} catch (Exception e) {
			log.error("ftp传输出错了:", e);
		} finally {
			try {
				ftpClient.logout();
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	/**
	 * 剪切文件
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean cutFile(String from, String to) {
		boolean isSuccess = false;

		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient.connect(ftpServer, port);
			ftpClient.login(ftpUserName, ftpPassword);
			// 返回值
			int reply = ftpClient.getReplyCode();
			log.info("ftp返回值为：" + reply);
			if ((!FTPReply.isPositiveCompletion(reply))) {
				log.info("登陆ftp服务器失败,检查用户名或密码是否正确");
				throw new ServiceException(
						"Login ftp server failure. Please check the ftp server["
								+ ftpServer + "] is avalable, And user '"
								+ ftpUserName + "' and the password is right .");
			}
			String tmsInPath = getFtpTMSINPath();
			String tmsBakPath = getFtpTMSBakPath();

			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.changeWorkingDirectory(tmsInPath);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setControlEncoding("UTF-8");

			log.info("fromFileName:" + from + "     toFileName:" + to);
			ftpClient.rename(from, to);

			//ftpClient.completePendingCommand();
			isSuccess = true;
		} catch (Exception e) {
			log.error("ftp文件剪切出错了:", e);
			isSuccess = false;
		} finally {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return isSuccess;
	}

	/**
	 * 
	 * 查看目录是否存在
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public boolean isDirectoryExists(String path, FTPClient ftpClient)
			throws IOException {
		boolean flag = false;
		FTPFile[] ftpFileArr = ftpClient.listFiles(path);
		log.info(path + "文件夹内有" + ftpFileArr.length + "个文件");
		for (FTPFile ftpFile : ftpFileArr) {
			if (ftpFile.isDirectory()
					&& ftpFile.getName().equalsIgnoreCase(path)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * 
	 * 得到某个目录下的文件名列表
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public List<String> getFileList(String path, FTPClient ftpClient)
			throws IOException {
		FTPFile[] ftpFiles = ftpClient.listFiles(path);
		List<String> retList = new ArrayList<String>();
		if (ftpFiles == null || ftpFiles.length == 0) {
			return retList;
		}
		for (FTPFile ftpFile : ftpFiles) {
			if (ftpFile.isFile()) {
				retList.add(ftpFile.getName());
			}
		}
		return retList;
	}

	public static String inputStream2String(InputStream is) throws IOException {
		if (is == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		return baos.toString();
	}

	/**
	 * FTP下载单个文件测试
	 */
	public static void testDownload() {
		FTPClient ftpClient = new FTPClient();
		FileOutputStream fos = null;

		try {
			ftpClient.connect("192.168.14.117");
			ftpClient.login("admin", "123");

			String remoteFileName = "/admin/pic/3.gif";
			fos = new FileOutputStream("c:/down.gif");

			ftpClient.setBufferSize(1024);
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.retrieveFile(remoteFileName, fos);
		} catch (IOException e) {
			log.error("FTP下载单个文件测试异常", e);
			throw new ServiceException("FTP客户端出错！");
		} finally {
			IOUtils.closeQuietly(fos);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				log.error("关闭FTP连接发生异常！", e);
				throw new ServiceException("关闭FTP连接发生异常！");
			}
		}
	}
}
