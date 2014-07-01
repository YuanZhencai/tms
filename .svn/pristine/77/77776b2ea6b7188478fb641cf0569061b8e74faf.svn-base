/**
 * CurrentUserBean.java
 * Created: 2012-1-30 上午11:20:07
 */
package com.wcs.base.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.TreeNode;

import com.wcs.base.service.LoginService;
import com.wcs.common.model.Resourcemstr;
import com.wcs.common.model.Rolemstr;
import com.wcs.common.model.Usermstr;

/**
 * <p>
 * Project: btcbase
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:chengchao@wcs-global.com">ChengChao</a>
 */
@ManagedBean(name = "currentUser")
@SessionScoped
public class CurrentUserBean implements Serializable {

	private static final long serialVersionUID = 9157970199871052634L;

	private Log log = LogFactory.getLog(CurrentUserBean.class);
	
	@EJB
	private LoginService loginService;

	public String getCurrentUserName() {
		return loginService.getCurrentUserName();
	}

	public Usermstr getCurrentUsermstr() {
		return loginService.getCurrentUsermstr();
	}

	public List<Rolemstr> getCurrentRoles() {
		return loginService.getCurrentRoles();
	}

	public List<Resourcemstr> getCurrentResources() {
		return loginService.getCurrentResources();
	}

	public List<TreeNode> getCurrentMenus() {
		return loginService.getCurrentMenus();
	}

	private static final String LOGIN_SUCCESS = "/template/template.xhtml";

	// 当前用户的菜单树（1~3级）
	private List<TreeNode> menuTree;

	@PostConstruct
	void init() {
		try {
			currentUser = this.getCurrentUserName();
			menuTree = getCurrentMenus();
			topNavs = menuTree;
			
			//登录后得到默认菜单
			for(TreeNode tn : topNavs){
				String code = ((Resourcemstr)tn.getData()).getCode();
				if(code!=null && "tms".equals(code)){
					refreshMidNavs(((Resourcemstr)tn.getData()).getId());
				}
			}
		} catch (Exception e) {
			log.error("登录出现异常" , e);
		}
	}

	// 1级
	private List<TreeNode> topNavs;
	//
	private Long selectedTopNavId;
	// 2级
	private List<TreeNode> midNavs;
	
	//用户名
	private String currentUser;
	
	public String refreshMidNavs(Long topNavId) {
		selectedTopNavId = null;
		midNavs = new ArrayList<TreeNode>();
		if (topNavId == null) {
			return LOGIN_SUCCESS;
		}
		if (menuTree == null || menuTree.isEmpty()) {
			return LOGIN_SUCCESS;
		}
		for (TreeNode menu : menuTree) {
			if (topNavId.equals(((Resourcemstr) (menu.getData())).getId())) {
				midNavs = menu.getChildren();
				selectedTopNavId = topNavId;
				for (TreeNode tn : midNavs) {
					List<TreeNode> threeMenu = tn.getChildren();
					for (TreeNode tm : threeMenu) {
						Resourcemstr r = (Resourcemstr) tm.getData();
						if (r.getUri() != null && !"".equals(r.getUri().trim())) {
							return r.getUri();
						}
					}
				}
			}
		}
		return LOGIN_SUCCESS;
	}
	
	
	/**
	 * 账号得到姓名
	 * @param userAccount
	 * @return
	 */
	public String exchange2CN(String userAccount){
		return loginService.getCNNameByAccount(userAccount);
	}
	
	/**
	 * 得到登录账号姓名
	 * @param userAccount
	 * @return
	 */
	public String getCurrentCN(){
		return loginService.getCNNameByAccount(this.getCurrentUserName());
	}

	public List<TreeNode> getMenuTree() {
		return menuTree;
	}

	public void setMenuTree(List<TreeNode> menuTree) {
		this.menuTree = menuTree;
	}

	public List<TreeNode> getTopNavs() {
		return topNavs;
	}

	public void setTopNavs(List<TreeNode> topNavs) {
		this.topNavs = topNavs;
	}

	public Long getSelectedTopNavId() {
		return selectedTopNavId;
	}

	public void setSelectedTopNavId(Long selectedTopNavId) {
		this.selectedTopNavId = selectedTopNavId;
	}

	public List<TreeNode> getMidNavs() {
		//登录用户变化，重新初始化一次菜单
		if(currentUser==null || !currentUser.equals(this.getCurrentUserName())){
			init();
		}
		return midNavs;
	}

	public void setMidNavs(List<TreeNode> midNavs) {
		this.midNavs = midNavs;
	}

	public String getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(String currentUser) {
		this.currentUser = currentUser;
	}
	
}
