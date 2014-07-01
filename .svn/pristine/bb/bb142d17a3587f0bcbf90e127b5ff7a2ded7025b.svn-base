package com.wcs.common.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wcs.base.service.LoginService;
import com.wcs.base.util.StringUtils;

/**
 * 
 * <p>Project: wcsoa</p>
 * <p>Description: 资源文件通用函数</p>
 * <p>1、faces消息提示通用函数;</p>
 * <p>2、资源文件读取通用函数。</p><br>
 * <p>调用样例：</p>
 *1、获取默认的messages资源文件中的内容.
 *  MessageUtils.getMessage("btn_print");//（采用当前用户的语言）
 *  MessageUtils.getMessage("btn_print","替换｛0｝占位符");// （采用当前用户的语言，并替换第一个占位符）
 *2、获取各个模块中的资源文件中的内容.
 *  MessageUtils.getMessage("sys_osm:osmOrg_orgName");//（采用当前用户的语言,读取com.wcs.sys.osm.messages文件中的osmOrg_orgName的key值）
 *  MessageUtils.getMessage("sys_osm:osmOrg_orgName","替换｛0｝占位符");//（采用当前用户的语言，读取com.wcs.sys.osm.messages文件中的osmOrg_orgName的key值，并替换第一个占位符）<br>
 *3、页面中添加提示信息
 *  MessageUtils.addInfoMessageByKey(CLIENT_ID, "kq:testText");//（采用当前用户的语言）
 *  <p>//指定简体中文，替换{0}{1}两个占位符<p>
 *  MessageUtils.addInfoMessageByKey(CLIENT_ID, "kq:messageTest",Locale.SIMPLIFIED_CHINESE,new Object[]{MessageUtils.getMessage("kq:testText"),"信息级别测试好了"})
 *4、页面中添加错误信息（参数同上）
 *  MessageUtils.addErrorMessageByKey
 *5、页面中添加警告信息（参数同上）
 *  MessageUtils.addWarnMessageByKey
 * <br>
 * <p>Copyright © 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
public class MessageUtils {

	private static final Logger log = LoggerFactory.getLogger(MessageUtils.class);
	// 项目的根包名
	public static final String ROOT_PACKAGE_NAME = "com.wcs.";
	// 资源文件名称
	public static final String MESSAGES_RESOURCE_NAME = "messages";
	// 全局配置文件
	public static String GLOBAL_CONFIG_RESOURCE_NAME = "config/globalconfig";
	// 应用上下文
	public static String CONTEXT_PATH = "";

	private MessageUtils() {
	}

	/**
	 * 添加错误级别的消息
	 * @param clientId
	 * @param summary
	 * @auther:LiWei 2012-11-9 下午1:55:32
	 */
	public static void addErrorMessage(String clientId, String summary) {
		MessageUtils.addMessage(clientId, summary, FacesMessage.SEVERITY_ERROR);
	}

	/**
	 * 添加错误级别的消息
	 * @param clientId 客户端控件id
	 * @param key 国际化资源key 支持如下格式：sys_osm:osmOrg_orgName
	 * @auther:Liushengbin 2012-12-3 下午1:55:32
	 */
	public static void addErrorMessageByKey(String clientId, String key) {
		MessageUtils.addMessage(clientId, getMessage(key), FacesMessage.SEVERITY_ERROR);
	}

	/**
	 * 添加错误级别的消息
	 * @param clientId 客户端控件id
	 * @param key 国际化资源key 支持如下格式：sys_osm:osmOrg_orgName
	 * @param arg 可选，替换｛0｝参数
	 * @auther:Liushengbin 2012-12-3 下午1:55:32
	 */
	public static void addErrorMessageByKey(String clientId, String key, String arg) {
		Locale locale = getCurrentLocale();
		addErrorMessageByKey(clientId, key, locale, new Object[] { arg });
	}

	/**
	 * 添加错误级别的消息
	 * @param clientId 客户端控件id
	 * @param key 国际化资源key 支持如下格式：sys_osm:osmOrg_orgName
	 * @param locale 指定要用的语言
	 * @param args 替换{0}{1}等参数
	 * @auther:Liushengbin 2012-12-3 下午1:55:32
	 */
	public static void addErrorMessageByKey(String clientId, String key, Locale locale, Object[] args) {
		if (locale == null) {
			locale = getCurrentLocale();
		}
		MessageUtils.addMessage(clientId, getMessage(key, locale, args), FacesMessage.SEVERITY_ERROR);
	}

	/**
	 * 添加提示级别的消息
	 * @param clientId
	 * @param summary 
	 * @auther:LiWei 2012-11-9 下午1:55:16
	 */
	public static void addInfoMessage(String clientId, String summary) {
		MessageUtils.addMessage(clientId, summary, FacesMessage.SEVERITY_INFO);
	}

	/**
	 * 添加提示级别的消息
	 * @param clientId 客户端控件id
	 * @param key 国际化资源key 支持如下格式：sys_osm:osmOrg_orgName
	 * @auther:Liushengbin 2012-12-3 下午1:55:32
	 */
	public static void addInfoMessageByKey(String clientId, String key) {
		MessageUtils.addMessage(clientId, getMessage(key), FacesMessage.SEVERITY_INFO);
	}

	/**
	 * 添加提示级别的消息
	 * @param clientId 客户端控件id
	 * @param key 国际化资源key 支持如下格式：sys_osm:osmOrg_orgName
	 * @param arg 可选，替换｛0｝参数
	 * @auther:Liushengbin 2012-12-3 下午1:55:32
	 */
	public static void addInfoMessageByKey(String clientId, String key, String arg) {
		Locale locale = getCurrentLocale();
		addInfoMessageByKey(clientId, key, locale, new Object[] { arg });
	}

	/**
	 * 添加提示级别的消息
	 * @param clientId 客户端控件id
	 * @param key 国际化资源key 支持如下格式：sys_osm:osmOrg_orgName
	 * @param locale 指定要用的语言
	 * @param args 替换{0}{1}等参数
	 * @auther:Liushengbin 2012-12-3 下午1:55:32
	 */
	public static void addInfoMessageByKey(String clientId, String key, Locale locale, Object[] args) {
		if (locale == null) {
			locale = getCurrentLocale();
		}
		MessageUtils.addMessage(clientId, getMessage(key, locale, args), FacesMessage.SEVERITY_INFO);
	}

	/**
	 * 添加警告消息
	 * @param clientId
	 * @param summary
	 * @auther:LiWei 2012-11-9 下午1:47:38
	 */
	public static void addWarnMessage(String clientId, String summary) {
		MessageUtils.addMessage(clientId, summary, FacesMessage.SEVERITY_WARN);
	}

	/**
	 * 添加警告级别的消息
	 * @param clientId 客户端控件id
	 * @param key 国际化资源key 支持如下格式：sys_osm:osmOrg_orgName
	 * @auther:Liushengbin 2012-12-3 下午1:55:32
	 */
	public static void addWarnMessageByKey(String clientId, String key) {
		MessageUtils.addMessage(clientId, getMessage(key), FacesMessage.SEVERITY_WARN);
	}

	/**
	 * 添加警告级别的消息
	 * @param clientId 客户端控件id
	 * @param key 国际化资源key 支持如下格式：sys_osm:osmOrg_orgName
	 * @param arg 可选，替换｛0｝参数
	 * @auther:Liushengbin 2012-12-3 下午1:55:32
	 */
	public static void addWarnMessageByKey(String clientId, String key, String arg) {
		Locale locale = getCurrentLocale();
		addWarnMessageByKey(clientId, key, locale, new Object[] { arg });
	}

	/**
	 * 添加警告级别的消息
	 * @param clientId 客户端控件id
	 * @param key 国际化资源key 支持如下格式：sys_osm:osmOrg_orgName
	 * @param locale 指定要用的语言
	 * @param args 替换{0}{1}等参数
	 * @auther:Liushengbin 2012-12-3 下午1:55:32
	 */
	public static void addWarnMessageByKey(String clientId, String key, Locale locale, Object[] args) {
		if (locale == null) {
			locale = getCurrentLocale();
		}
		MessageUtils.addMessage(clientId, getMessage(key, locale, args), FacesMessage.SEVERITY_WARN);
	}

	/**
	 * 添加自定义级别消息
	 * @param clientId
	 * @param summary
	 * @param servertity
	 * @auther:LiWei 2012-11-9 下午1:47:20
	 */
	private static void addMessage(String clientId, String summary, FacesMessage.Severity servertity) {
		MessageUtils.addMessage(clientId, summary, null, servertity);
	}

	/**
	 * 添加带详细消息的自定义级别消息
	 * @param clientId
	 * @param summary
	 * @param detail 详细消息
	 * @param servertity 验证级别
	 * @auther:LiWei 2012-11-9 下午1:53:21
	 */
	public static void addMessage(String clientId, String summary, String detail, FacesMessage.Severity servertity) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage();
		message.setSeverity(servertity);
		message.setDetail(detail);
		message.setSummary(summary);
		if (servertity == FacesMessage.SEVERITY_ERROR) {
			context.validationFailed();
		}
		context.addMessage(clientId, message);
	}

	/**
	 * <p>Description:获取资源文件中的内容</p>
	 * <p>默认资源文件:src/main/resources/messages</p>
	 * @param key 国际化资源key 支持如下格式：sys_osm:osmOrg_orgName
	 * @return
	 */
	public static String getMessage(String key) {
		Locale locale = getCurrentLocale();
		return getMessage(key, null, locale);
	}

	/**
	 * <p>Description:获取资源文件中的内容</p>
	 * <p>默认资源文件:src/main/resources/messages</p>
	 * @param key 国际化资源key 支持如下格式：sys_osm:osmOrg_orgName
	 * @param arg 替换{0}参数
	 * @return
	 */
	public static String getMessage(String key, String arg) {
		return getMessage(key, new Object[] { arg });
	}

	/**
	 * <p>Description:获取资源文件中的内容</p>
	 * <p>默认资源文件:src/main/resources/messages</p>
	 * @param key 国际化资源key 支持如下格式：sys_osm:osmOrg_orgName
	 * @param values 可替换"{0}{1}{2}"等参数
	 * @return
	 */
	public static String getMessage(String key, Object[] args) {
		Locale locale = getCurrentLocale();
		return getMessage(key, locale, args);
	}

	/**
	 * 获取国际化资源
	 * @param key 国际化资源key
	 * @param bundle 资源文件
	 * @param locale 语言
	 * @param values 可替换"{0}{1}{2}"等参数
	 * @return
	 */
	public static String getMessage(String key, Locale locale, Object[] args) {
		String rtnMessageString = getMessage(key, null, locale);
		for (int i = 0; i < args.length; i++) {
			rtnMessageString = StringUtils.replace(rtnMessageString, "{" + i + "}", String.valueOf(args[i]));
		}
		return rtnMessageString;
	}

	/**
	 * 获取globalconfig.properties的信息,可以替换｛0｝等参数
	 * 
	 * @param key
	 * @param values 可替换"{0}{1}{2}"等参数
	 * @return
	 */
	public static String getGlobalConfigString(String key, Object... values) {
		try {
			ResourceBundle resourceBundle = ResourceBundle.getBundle(GLOBAL_CONFIG_RESOURCE_NAME);
			MessageFormat mf = new MessageFormat(resourceBundle.getString(key));
			return mf.format(values);
		} catch (MissingResourceException e) {
			return "It's not found this key in globalconfig.properties:" + "!" + key + "!";
		}
	}

	/**
	 * 
	 * <p>Description:获取指定bundle和语言环境的资源值 </p>
	 * @param key 支持 sys_osm:osmOrg_orgName
	 * @param bundle
	 * @param locale
	 * @return
	 */
	private static String getMessage(String key, String bundle, Locale locale) {
		if (StringUtils.isBlankOrNull(bundle)) {
			int i = key.indexOf(':');
			if (i > -1) {
				bundle = key.substring(0, i);
				key = key.substring(i + 1);
			}
		}

		if (StringUtils.isBlankOrNull(bundle)) {
			bundle = MESSAGES_RESOURCE_NAME;
		} else {
			bundle = ROOT_PACKAGE_NAME + bundle.replaceAll("_", ".") + "." + MESSAGES_RESOURCE_NAME;
		}
		try {
			ResourceBundle resourceBundle = null;
			if (locale == null) {
				resourceBundle = ResourceBundle.getBundle(bundle);
			} else {
				resourceBundle = ResourceBundle.getBundle(bundle, locale);
			}
			return resourceBundle.getString(key);
		} catch (Exception e) {
			try {
				if (locale == null) {
					return null;
				} else {
					return ResourceBundle.getBundle(bundle).getString(key);
				}
			} catch (Exception e2) {
				return null;
			}
		}
	}

	/**
	 * Converts a locale string to <code>Locale</code> class. Accepts both
	 * '_' and '-' as separators for locale components.
	 *
	 * @param localeString string representation of a locale
	 * @return Locale instance, compatible with the string representation
	 */
	public static Locale toLocale(String localeString) {
		if ((localeString == null) || (localeString.length() == 0)) {
			Locale locale = Locale.getDefault();
			if (log.isWarnEnabled()) {
				log.warn("Locale name in faces-config.xml null or empty, setting locale to default locale : "
						+ locale.toString());
			}
			return locale;
		}

		int separatorCountry = localeString.indexOf('_');
		char separator;
		if (separatorCountry >= 0) {
			separator = '_';
		} else {
			separatorCountry = localeString.indexOf('-');
			separator = '-';
		}

		String language, country, variant;
		if (separatorCountry < 0) {
			language = localeString;
			country = variant = "";
		} else {
			language = localeString.substring(0, separatorCountry);

			int separatorVariant = localeString.indexOf(separator, separatorCountry + 1);
			if (separatorVariant < 0) {
				country = localeString.substring(separatorCountry + 1);
				variant = "";
			} else {
				country = localeString.substring(separatorCountry + 1, separatorVariant);
				variant = localeString.substring(separatorVariant + 1);
			}
		}
		return new Locale(language, country, variant);
	}

	/**
	 * <p>Description: 获得当前登录用户的语言环境，如果没获取到，返回默认的语言</p>
	 * @return 
	 */
	public static Locale getCurrentLocale() {
		Subject currentUser = SecurityUtils.getSubject();
		Object obj = currentUser.getSession().getAttribute(LoginService.SESSION_KEY_LOCALE);
		return obj == null ? Locale.getDefault() : (Locale) obj;
	}

}
