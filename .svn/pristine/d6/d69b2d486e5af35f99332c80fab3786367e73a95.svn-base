package com.wcs.sys.ejbtimer.util;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wcs.base.util.StringUtils;
import com.wcs.common.util.MessageUtils;
import com.wcs.sys.ejbtimer.consts.AppServerType;
import com.wcs.sys.ejbtimer.service.SysJobService;

/**
 * <p>Project: wcsoa</p>
 * <p>Description: JNDI 工具类，这里都是指获取同一个JVM中的jndi </p>
 * <br>
 * <p>
 * 例如：
 *   app-name为wcsoa,module-name为wcsoa
 *   EJB类名为：com.wcs.sys.quartz.service.SysQuartzJobService
 *   jndi则是：java:global/wcsoa/wcsoa/SysQuartzJobService!com.wcs.sys.quartz.service.SysQuartzJobService
 *  </p>
 * <p>Copyright © 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
public class JNDIUtil {

	private static final Logger logger = LoggerFactory.getLogger(JNDIUtil.class);
	// EJB
	// JNDI命名规则：java:global[/<app-name>]/<module-name>/<bean-name>[!<fully-qualified-interface-name>]

	public static final String JNDI_PREFIX = "java:global";

	public static final String SYS_APP_NAME = MessageUtils.CONTEXT_PATH;
	// glassfish下无MODULE,所有先暂时赋值为空的字符串
	public static final String SYS_MODULE_NAME = "";

	public static String SYS_APP_SERVER = AppServerType.WAS.value;

	public static final String JNDI_SPLIT = "/";

	public static final String PROVIDER_URL = "java.naming.provider.url";

	public static final String URL_PKG_PREFIXES = "java.naming.factory.url.pkgs";

	// 静态块中，检测当前应用所在应用服务器，目前只考虑WAS和Glassfish
	// 检测方式：默认按WAS中的JNDI方式去获取ejb对象，不出错，即是WAS环境，否则是Glassfish
	
	private JNDIUtil(){
		
	}
	static {
		try {
			Context jndiContext = new InitialContext();
			jndiContext.lookup("ejblocal:" + SysJobService.class.getName());
			SYS_APP_SERVER = AppServerType.WAS.value;
		} catch (Exception e) {
			SYS_APP_SERVER = AppServerType.GLASSFISH.value;
		}

		logger.info("current SYS_APP_SERVER is:=========================================" + SYS_APP_SERVER);
	}

	/**
	 * 
	 * <p>Description: 根据ejb的类名，返回jndi字符串</p>
	 * @param ejbClassName ejb的类名
	 * @return jndi字符串
	 */
	public static String findJNDI(String ejbClassName) {
		if (!StringUtils.hasLength(ejbClassName)) {
			return "";
		}

		// 没有配置，默认认为是GlassFish应用服务器的jndi规则
		if (!StringUtils.hasLength(SYS_APP_SERVER)) {
			SYS_APP_SERVER = AppServerType.GLASSFISH.value;
		}

		StringBuffer jndi = new StringBuffer();

		// WAS应用服务器，JNDI拼装
		// 示例：ejblocal:com.wcs.sys.quartz.service.TestService
		if (AppServerType.WAS.value.equalsIgnoreCase(SYS_APP_SERVER)) {
			jndi.append("ejblocal:");
			// 拼装全类名
			jndi.append(ejbClassName);
		}
		// 其他应用服务器，jndi拼装
		// 命名规则：java:global[/<app-name>]/<module-name>/<bean-name>[!<fully-qualified-interface-name>]
		// 示例：java:global/wcsoa/wcsoa/SysQuartzJobService!com.wcs.sys.quartz.service.SysQuartzJobService
		else {
			jndi.append(JNDI_PREFIX);
			if (StringUtils.hasLength(SYS_APP_NAME)) {
				jndi.append(SYS_APP_NAME).append(JNDI_SPLIT);
			}
			if (StringUtils.hasLength(SYS_MODULE_NAME)) {
				jndi.append(SYS_MODULE_NAME).append(JNDI_SPLIT);
			}
			if (StringUtils.hasLength(ejbClassName)) {
				String shortClassName = getShortClassName(ejbClassName);
				jndi.append(shortClassName);
				// 拼装全类名
				jndi.append("!").append(ejbClassName);
			}
		}
		logger.debug("ejb jndi is:" + jndi.toString());
		return jndi.toString();
	}

	/** 
	 * 创建jndi的上下文信息 
	 * @param map  
	 * @return 
	 * @throws NamingException 
	 */
	public static Context getEnvContext(Map map) throws Exception {
		Hashtable<String, String> params = new Hashtable<String, String>(3);
		String initialContextFactory = (String) map.get(InitialContext.INITIAL_CONTEXT_FACTORY);
		if (initialContextFactory != null) {
			params.put(InitialContext.INITIAL_CONTEXT_FACTORY, initialContextFactory);
		}
		String providerUrl = (String) map.get(InitialContext.PROVIDER_URL);
		if (providerUrl != null) {
			params.put(InitialContext.PROVIDER_URL, providerUrl);
		}
		String urlpkgprefixes = (String) map.get(URL_PKG_PREFIXES);
		if (urlpkgprefixes != null) {
			params.put(URL_PKG_PREFIXES, urlpkgprefixes);
		}
		return params.size() != 0 ? new InitialContext(params) : new InitialContext();
	}

	/**
	 * 
	 * <p>Description:获取不包含包名的简单类名 </p>
	 * <p>如全类名为：com.wcs.common.service.CommonService，简单类名为：CommonService</p>
	 * @param fullyClassName
	 * @return 
	 */
	public static String getShortClassName(String fullyClassName) {
		String shortClassName = fullyClassName;
		if (fullyClassName.indexOf('.') != -1) {
			shortClassName = fullyClassName.substring(fullyClassName.lastIndexOf('.') + 1);
		}
		return shortClassName;
	}

	public static void main(String[] args) {
		logger.info(findJNDI("com.wcs.sys.quartz.service.TestService"));

	}
}
