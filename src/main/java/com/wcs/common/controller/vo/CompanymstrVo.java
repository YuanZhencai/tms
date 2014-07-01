package com.wcs.common.controller.vo;

import java.io.Serializable;

import com.wcs.common.controller.helper.IdModel;
import com.wcs.common.model.Companymstr;
import com.wcs.common.model.O;
import com.wcs.common.model.Usercompany;

public class CompanymstrVo extends IdModel implements Serializable {

	private static final long serialVersionUID = -6631723518643161095L;

	private Usercompany usercompany;

	private Companymstr companymstr;

	private O o;

	public Usercompany getUsercompany() {
		return usercompany;
	}

	public void setUsercompany(Usercompany usercompany) {
		this.usercompany = usercompany;
	}

	public Companymstr getCompanymstr() {
		return companymstr;
	}

	public void setCompanymstr(Companymstr companymstr) {
		this.companymstr = companymstr;
	}

	public O getO() {
		return o;
	}

	public void setO(O o) {
		this.o = o;
	}

}
