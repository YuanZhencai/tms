package com.wcs.common.filenet.env;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;

import filenet.vw.api.VWSession;

/**
 * 
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description: 当前用户fn环境信息
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
public class TmsEnv implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private boolean springinit;
	private Connection ceConn;
	private boolean ceinit;
	private VWSession VWSession;
	private boolean peinit;
	private Domain dom;
	private boolean dominit;

	private Map<String, ObjectStore> SysOSMap = new HashMap<String, ObjectStore>();

	void setCeConn(Connection ceconn) {
		if (ceconn != null) {
			ceConn = ceconn;
			ceinit = true;
		} else {
			ceinit = false;
		}
	}

	void setVWSess(VWSession vwSession) {
		if (vwSession != null) {
			VWSession = vwSession;
			peinit = true;
		} else {
			peinit = false;
		}
	}

	public ObjectStore fechObjectStore(String objStore) {
		Object osb = SysOSMap.get(objStore);
		if (osb == null) {
			Domain domain = Factory.Domain.fetchInstance(ceConn,
					SysCfg.getStrConfig("CE.domain"), null);
			ObjectStore os = Factory.ObjectStore.fetchInstance(domain,
					objStore, null);
			SysOSMap.put(objStore, os);
			return os;
		} else {
			return (ObjectStore) osb;
		}
	}

	public Connection getFileNetConn() {
		return ceConn;
	}

	public VWSession getVWSession() {
		return VWSession;
	}

	public Domain getDom() {
		return dom;
	}

	public void setDom(Domain dom) {
		if (dominit) {
			return;
		}
		if (dom != null) {
			this.dom = dom;
			dominit = true;
		} else {
			dominit = false;
		}

	}

	public boolean isSpringinit() {
		return springinit;
	}

	public void setSpringinit(boolean springinit) {
		this.springinit = springinit;
	}

	public boolean isCeinit() {
		return ceinit;
	}

	public void setCeinit(boolean ceinit) {
		this.ceinit = ceinit;
	}

	public boolean isPeinit() {
		return peinit;
	}

	public void setPeinit(boolean peinit) {
		this.peinit = peinit;
	}

	public boolean isDominit() {
		return dominit;
	}

	public void setDominit(boolean dominit) {
		this.dominit = dominit;
	}

	public Connection getCeConn() {
		return ceConn;
	}

}
