package com.wcs.common.filenet.env;


import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;

import org.apache.log4j.Logger;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;

public class SysCfg {
	private static Logger log = Logger.getLogger(SysCfg.class);
	private static final Configuration config = new Configuration();
	private static Map map = new HashMap();
	private static Connection CE_CONN;
	private static Map<String, ObjectStore> SysOSMap = new HashMap<String, ObjectStore>();
	private String[] configFiles;

	public static String getStrConfig(String configPath) {
		return Configuration.getParameter(configPath);
	}

	public static Connection getFileNetConn() {
		return CE_CONN;
	}

	private static void initOS() {
		String ceAdminUser = SysCfg.getStrConfig("CE.filenetCeAdminUserName");
		String ceAdminPassword = SysCfg.getStrConfig("CE.filenetCeAdminUserPasswd");
		Subject subject = UserContext.createSubject(SysCfg.getFileNetConn(),ceAdminUser, ceAdminPassword, null);
		UserContext uc = UserContext.get();
		uc.pushSubject(subject);
		String os = SysCfg.getStrConfig("CE.ObjectStore");
		SysOSMap.put(os, fechObjectStore(os));
	}

	public static ObjectStore getSysOs(String objStore) {
		return SysOSMap.get(objStore);
	}

	private static ObjectStore fechObjectStore(String objStore) {
		Domain domain = Factory.Domain.fetchInstance(CE_CONN,
				getStrConfig("CE.domain"), null);
		ObjectStore os = Factory.ObjectStore.fetchInstance(domain, objStore,
				null);
		return os;
	}

	public String[] getConfigFiles() {
		return configFiles;
	}

	public void setConfigFiles(String[] configFiles) {
		this.configFiles = configFiles;
	}
}
