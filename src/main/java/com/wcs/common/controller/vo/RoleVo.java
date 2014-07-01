package com.wcs.common.controller.vo;

import java.io.Serializable;

import com.wcs.common.controller.helper.IdModel;
import com.wcs.common.model.Rolemstr;

public class RoleVo extends IdModel implements Serializable {
	private static final long serialVersionUID = -4441499615900424867L;

	private Rolemstr rolemstr;

	public Rolemstr getRolemstr() {
		return rolemstr;
	}

	public void setRolemstr(Rolemstr rolemstr) {
		this.rolemstr = rolemstr;
	}

}
