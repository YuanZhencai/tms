/**
 * 
 */
package com.wcs.tms.conf;

import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author aguang
 * 
 */
public final class Config {

	private static XMLConfiguration config;

	private static final String CONFIG_FILE_PATH = "configuration.xml";
	
	private static Log log = LogFactory.getLog(Config.class);

	static {
		try {
			config = new XMLConfiguration(CONFIG_FILE_PATH);
		} catch (ConfigurationException e) {
			log.error("Config静态初始化块异常", e);
		}
			
	}

	private Config() {
		// TODO Auto-generated constructor stub
	}

	public static String getString(String key) {
		return config.getString(key, "");
	}

	public static String[] getStringArray(String key) {
		return config.getStringArray(key);
	}

	@SuppressWarnings("unchecked")
	public static List<String> getList(String key) {
		return config.getList(key, null);
	}

	public static Properties getProperties(String key) {
		return config.getProperties(key);
	}

	public static Object getObject(String key) {
		return config.getProperty(key);
	}

	public static double getDouble(String key) {
		return config.getDouble(key, 0);
	}

	public static float getFloat(String key) {
		return config.getFloat(key, 0);
	}

	public static long getLong(String key) {
		return config.getLong(key, 0);
	}

	public static int getInteger(String key) {
		return config.getInt(key, 0);
	}

	public static short getShort(String key) {
		return config.getShort(key, (short) 0);
	}

	public static byte getByte(String key) {
		return config.getByte(key, (byte) 0);
	}

	public static boolean getBoolean(String key) {
		return config.getBoolean(key, false);
	}

	public static void addProperty(String key, Object value) {
		config.addProperty(key, value);
	}

	public static void setProperty(String key, Object value) {
		config.setProperty(key, value);
	}

}
