package com.wcs.common.controller.vo;

import java.io.Serializable;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:人员岗位机构查询列表Vo</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */

public class PoVo implements Serializable{
	private static final long serialVersionUID = 2784105434022127910L;
	
	private String pId ;
	
	private String pName;
	
	private String userName;
	
	private String stationName;
	
	private String oPath;
	
	private String pDefunctInd;

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getoPath() {
		return oPath;
	}

	public void setoPath(String oPath) {
		this.oPath = oPath;
	}

	public String getpDefunctInd() {
		return pDefunctInd;
	}

	public void setpDefunctInd(String pDefunctInd) {
		this.pDefunctInd = pDefunctInd;
	}
	
	
}
