package com.wcs.common.filenet.util;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import sun.misc.BASE64Decoder;

public class ConfigLocationsUtil {
	private static String webContentPath;
	private static String decode1(String s) {
		StringBuffer sf1 = new StringBuffer();
		StringBuffer sf2 = new StringBuffer();
		int n = s.length() / 2;
		for (int i = 0; i < n; i++) {
			sf1.append(s.charAt(2 * i));
			sf2.append(s.charAt(2 * i + 1));
		}
		if (s.length() % 2 == 1) {
			sf1.append(s.charAt(s.length() - 1));
		}
		return sf2.append(sf1).toString();
	}

	private static String decode2(String s) {
		for (int i = 0; i < 5; i++) {
			int len = s.charAt(0);
			len = (len - 35) % 10;
			int index = 0;
			for (int j = 0; j < len; j++) {
				index = index * 90 + ((int) s.charAt(1 + j)) - 35;
			}
			s = s.substring(1 + len);
			index = s.length() - index;
			s = s.substring(index) + s.substring(0, index);
		}
		return s;
	}

	private static String decode(String str) throws Exception {
		BASE64Decoder decoder = new BASE64Decoder();
		str = decode2(str);
		str = decode1(str);
		str = decode2(str);
		str = new String(decoder.decodeBuffer(str));
		str = decode2(str);
		str = decode1(str);
		str = decode2(str);
		str = new String(decoder.decodeBuffer(str));
		str = URLDecoder.decode(str, "UTF-8");
		return str;
	}

	

	public static String getWebContentPath() {
		if (webContentPath == null) {
			URL url = Thread.currentThread().getContextClassLoader()
					.getResource("ApplicationResources.properties");
			webContentPath = url.getPath().substring(0,
					url.getPath().lastIndexOf("/WEB-INF/"));
		}
		return webContentPath;
	}

	public static String getKmssConfigPath() {
		return getWebContentPath() + "/WEB-INF/KmssConfig";
	}

	/**
	 * @param path
	 *            样例：D:\tools\kmssworkplace\kmss_hr\WebContent
	 * @param fileName
	 *            样例：spring.xml
	 * @return String[0...n]="/WEB-INF/KmssConfig/sys/common/spring.xml"
	 */
	public static String[] getConfigLocationArray(String path,
			String cfgFileName) {
		return getConfigLocationArray(path, cfgFileName, "");
	}

	/**
	 * @param path
	 *            样例：D:\tools\kmssworkplace\kmss_hr\WebContent
	 * @param cfgFileName
	 *            样例：spring.xml
	 * @param filePrefix
	 *            样例：file:///
	 * @return 
	 *         String[0...n]=filePrefix+"/WEB-INF/KmssConfig/sys/common/spring.xml"
	 */
	public static String[] getConfigLocationArray(String path,
			String cfgFileName, String filePrefix) {		
	    String curPath = "";
		File dir = new File(path);
		List files = new ArrayList();
		getConfigLocations(dir, curPath, cfgFileName, filePrefix, files);
		return (String[]) files.toArray(new String[0]);
	}

	/**
	 * @param path
	 *            样例：D:\tools\kmssworkplace\kmss_hr\WebContent
	 * @param fileName
	 *            样例：spring.xml
	 * @return "/WEB-INF/KmssConfig/sys/common/spring.xml,/WEB-INF/KmssConfig/sys/notify/spring.xml"
	 */
	public static String getConfigLocations(String path, String cfgFileName) {
		return getConfigLocations(path, cfgFileName, "");
	}

	/**
	 * @param path
	 *            样例：D:\tools\kmssworkplace\kmss_hr\WebContent
	 * @param cfgFileName
	 *            样例：spring.xml
	 * @param filePrefix
	 *            样例：file:///
	 * @return 
	 *         filePrefix+"/WEB-INF/KmssConfig/sys/common/spring.xml,"+filePrefix
	 *         +"/WEB-INF/KmssConfig/sys/notify/spring.xml"
	 */
	public static String getConfigLocations(String path, String cfgFileName,
			String filePrefix) {
		String[] files = getConfigLocationArray(path, cfgFileName, filePrefix);
		if (files.length == 0) {
			return "";
		}
		StringBuffer rtnVal = new StringBuffer();
		for (int i = 0; i < files.length; i++) {
			rtnVal.append(',').append(files[i]);
		}
		return rtnVal.substring(1);
	}

	private static void getConfigLocations(File dir, String curPath,
			String cfgFileName, String filePrefix, List files) {
		String[] fileNames = dir.list();
		if (fileNames == null) {
			return;
		}
		String filePath = dir.getPath();
		outloop: for (int i = 0; i < fileNames.length; i++) {
			if (fileNames[i].startsWith(".")) {
				continue;
			}
			String newPath = curPath + "/" + fileNames[i];
			if (fileNames[i].lastIndexOf(cfgFileName) != -1) {				
				files.add(filePrefix + newPath);
				continue;
			}
			File file = new File(filePath + "/" + fileNames[i]);
			if (file.isDirectory()) {
				getConfigLocations(file, newPath, cfgFileName, filePrefix,
						files);
			}
		}
	}


	public static void main(String[] args) {
		
	}
}
