package com.wcs.common.controller.vo;

import java.io.Serializable;

public class CompanymstrFormItems implements Serializable {

	private static final long serialVersionUID = 4297070297159051500L;

	private String stext;

	private String bukrs;

	public String getStext() {
		return stext;
	}

	public void setStext(String stext) {
		this.stext = stext;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

}
