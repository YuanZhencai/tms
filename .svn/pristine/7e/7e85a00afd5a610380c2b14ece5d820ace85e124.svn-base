package com.wcs.tms.service.process.common.patch.pe;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 读取patch配置文件
 * @author liushengbin
 *
 */
public class ConfigUtils {

	// patch配置文件
	public static final String CONFIG_RESOURCE_NAME = "config/patch_fnconfig";
	/**
	 * 获取patch_fnconfig.properties的信息,可以替换｛0｝等参数
	 * 
	 * @param key
	 * @param values 可替换"{0}{1}{2}"等参数
	 * @return
	 */
	public static String getString(String key, Object... values) {
		try {
			ResourceBundle resourceBundle = ResourceBundle.getBundle(CONFIG_RESOURCE_NAME);
			MessageFormat mf = new MessageFormat(resourceBundle.getString(key));
			return mf.format(values);
		} catch (MissingResourceException e) {
			return "It's not found this key in globalconfig.properties:" + "!" + key + "!";
		}
	}
}
