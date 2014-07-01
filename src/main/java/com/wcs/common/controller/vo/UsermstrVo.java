package com.wcs.common.controller.vo;

import java.io.Serializable;
import java.util.List;

import com.wcs.common.controller.helper.IdModel;
import com.wcs.common.model.O;
import com.wcs.common.model.P;
import com.wcs.common.model.Rolemstr;
import com.wcs.common.model.Usermstr;

public class UsermstrVo extends IdModel implements Serializable {
	private static final long serialVersionUID = 7922530371478759792L;
	private Usermstr usermstr;
	private P p;
	private O o;
	private List<Rolemstr> roles;
	//为了显示用户所拥有的角色名称
	@SuppressWarnings("unused")
	private String roleNames;

	public Usermstr getUsermstr() {
		return usermstr;
	}

	public void setUsermstr(Usermstr usermstr) {
		this.usermstr = usermstr;
	}

	public P getP() {
		return p;
	}

	public void setP(P p) {
		this.p = p;
	}

	public O getO() {
		return o;
	}

	public void setO(O o) {
		this.o = o;
	}

	public List<Rolemstr> getRoles() {
		return roles;
	}

	public void setRoles(List<Rolemstr> roles) {
		this.roles = roles;
	}

	public String getRoleNames() {
		StringBuilder sb = new StringBuilder("");
		String ret = "";
		if(roles != null && roles.size() > 0) {
			for (Rolemstr role : roles) {
				sb.append(role.getName()).append(",");
			}
			ret = sb.substring(0, sb.length() - 1);
		}
		return ret;
	}

	public void setRoleNames(String roleNames) {
		this.roleNames = roleNames;
	}
	
}
