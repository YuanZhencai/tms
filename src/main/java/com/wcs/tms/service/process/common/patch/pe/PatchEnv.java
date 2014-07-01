package com.wcs.tms.service.process.common.patch.pe;

import java.util.HashMap;
import java.util.Map;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;

import filenet.vw.api.VWSession;

public class PatchEnv {
	private static boolean springinit;
	private static Connection ceConn;
	private static boolean ceinit;
	private static VWSession VWSession;
	private static boolean peinit;
	private static Domain dom;
	private static boolean dominit;

	private static Map<String, ObjectStore> SysOSMap = new HashMap<String, ObjectStore>();

	static void setCeConn(Connection ceconn) {
		if (ceconn != null) {
			ceConn = ceconn;
			ceinit = true;
		} else {
			ceinit = false;
		}
	}

	static void setVWSess(VWSession vwSession) {

		if (vwSession != null) {
			VWSession = vwSession;
			peinit = true;
		} else {
			peinit = false;
		}
	}

	public static ObjectStore fechObjectStore(String objStore) {
		Object osb = SysOSMap.get(objStore);
		if (osb == null) {
			Domain domain = Factory.Domain.fetchInstance(ceConn,
					ConfigUtils.getString("CE.domain"), null);
			ObjectStore os = Factory.ObjectStore.fetchInstance(domain,
					objStore, null);
			SysOSMap.put(objStore, os);
			return os;
		} else {
			return (ObjectStore) osb;
		}
	}

	public static Connection getFileNetConn() {
		return ceConn;
	}

	public static VWSession getVWSession() {
		return VWSession;
	}

	public static Domain getDom() {
		return dom;
	}

	public static void setDom(Domain dom) {
		if (dominit) {
			return;
		}
		if (dom != null) {
			PatchEnv.dom = dom;
			dominit = true;
		} else {
			dominit = false;
		}

	}

}
