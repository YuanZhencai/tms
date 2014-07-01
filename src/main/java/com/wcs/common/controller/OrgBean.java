package com.wcs.common.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.wcs.base.util.MathUtil;
import com.wcs.common.model.O;
import com.wcs.common.model.P;
import com.wcs.common.model.S;
import com.wcs.common.service.OrgService;
import com.wcs.common.service.StationService;
import com.wcs.common.service.UserService;
import com.wcs.tms.util.MessageUtils;

@ManagedBean(name = "orgManagerBean")
@ViewScoped
public class OrgBean implements Serializable {

	private static final long serialVersionUID = -2754227948892536175L;

	private Log log = LogFactory.getLog(OrgBean.class);
	public static final String ROOT_ORG_ID = "20000001";
	public static final String ORG_TYPE_COMPANY = "company";
	public static final String ORG_TYPE_POSITION = "position";
	public static final String ORG_TYPE_ROOT = "root";
	public static final String ORG_TYPE_ROOT_COMPANY = "rootCompany";
	
	@EJB
	private OrgService orgService;
	@EJB
	private UserService userService;
	@EJB
	private StationService stationService;
	private TreeNode root;
	private TreeNode selectedNode;
	private String selectType;
	private O parentOrgNode = new O();
	private O org = new O();
	private S position = new S();
	
	@PostConstruct
	public void init() {
		root = new DefaultTreeNode("ROOT", null);
		selectType = "root";
		initOrgTree();
	}

	/**
	 * 初始化机构树，只初始化2级
	 * 只加载O表的公司和部门
	 */
	public void initOrgTree() {
		root.getChildren().removeAll(root.getChildren());
		TreeNode rootNode = new DefaultTreeNode(ORG_TYPE_ROOT,
				orgService.getOrgById(ROOT_ORG_ID), root);
		List<O> orgList = orgService.getOrgByParent(ROOT_ORG_ID);
		for (O o : orgList) {
			TreeNode node = new DefaultTreeNode(ORG_TYPE_COMPANY, o, rootNode);
			List<O> subOrgList = orgService.getOrgByParent(o.getId());
			if (subOrgList != null && subOrgList.size() > 0) {
				for (O subO : subOrgList) {
					new DefaultTreeNode(ORG_TYPE_COMPANY, subO,
							node);
				}
			}
		}
	}

	/**
	 * 机构树在展开时调用该方法
	 * 若子节点下还有子节点 则不继续查部门
	 * 岗位都查
	 * @param event
	 *            void
	 */
	public void onNodeExpand(NodeExpandEvent event) {
		// 获取子节点
		List<TreeNode> childNodes = event.getTreeNode().getChildren();
		if (childNodes != null) {
			for (TreeNode treeNode : childNodes) {
				if (ORG_TYPE_COMPANY.equals(treeNode.getType())) {
					O parentOrg = (O) treeNode.getData();
					if (treeNode.getChildCount() == 0) {
						List<O> subOrgList = orgService
								.getOrgByParent(parentOrg.getId());
						if (subOrgList != null && subOrgList.size() > 0) {
							for (O subO : subOrgList) {
								new DefaultTreeNode(
										ORG_TYPE_COMPANY, subO, treeNode);
							}
						}
					}
					List<S> subPositionList = orgService
							.getPositionByOrgId(parentOrg.getId());
					if (subPositionList != null && subPositionList.size() > 0) {
						for (S subPosition : subPositionList) {
							new DefaultTreeNode(ORG_TYPE_POSITION,
									subPosition, treeNode);
						}
					}
				}
			}
		}
	}

	/**
	 * 当单击树形时，设置类型
	 * 1. root 根节点 总公司
	 * 2. company 公司或部门
	 * 3. position 岗位
	 * @param event
	 */
	public void onNodeSelect(NodeSelectEvent event) {
		TreeNode selectNode = event.getTreeNode();
		if(ORG_TYPE_COMPANY.equals(selectNode.getType())) {
			O org = (O) selectNode.getData();
			if(ROOT_ORG_ID.equals(org.getId())) {
				selectType = ORG_TYPE_ROOT;
			}else if(ROOT_ORG_ID.equals(org.getParent())){
				//selectType为company
				selectType = ORG_TYPE_ROOT_COMPANY;
			}else {
				selectType = ORG_TYPE_COMPANY;
			}
		}else {
			//selectType为position
			selectType = selectNode.getType();
		}
	}
	
	/**
	 * 前往添加公司或部门弹出框， 初始化参数
	 */
	public void forwordSaveCompany() {
		//设置父部门
		if(ORG_TYPE_COMPANY.equals(selectedNode.getType()) || ORG_TYPE_ROOT_COMPANY.equals(selectType)) {
			parentOrgNode = (O) selectedNode.getData();
		}else {
			MessageUtils.addErrorMessage("msg", "请选择公司!");
		}
		org = new O();
		//若添加部门则SAP代码为父公司SAP代码
		if(ORG_TYPE_COMPANY.equals(selectType) || ORG_TYPE_ROOT_COMPANY.equals(selectType)) {
			org.setBukrs(parentOrgNode.getBukrs());
		}
	}
	
	/**
	 * 保存公司、部门
	 */
	public void saveCompany() {
		try {
			//生成ID
			String bukrs = org.getBukrs();
			String tempSapCode = bukrs + "##O" + bukrs;
			String maxId = orgService.getMaxOrgId(tempSapCode);
			String oid = "";
			if(StringUtils.isNotBlank(maxId)) {
				oid = tempSapCode + MathUtil.idAddOne(maxId.substring(tempSapCode.length()));
			}else {
				oid = tempSapCode + "000001";
			}
			org.setId(oid);
			if(ORG_TYPE_COMPANY.equals(selectType) || ORG_TYPE_ROOT_COMPANY.equals(selectType)) {
				org.setStext(bukrs + "-" + org.getStext());
			}
			org.setParent(parentOrgNode.getId());
			orgService.saveOrg(org);
			new DefaultTreeNode(
					ORG_TYPE_COMPANY, org, selectedNode);
			MessageUtils.addSuccessMessage("msg", "系统添加组织机构成功!");
			RequestContext.getCurrentInstance().addCallbackParam("issucc", "yes");
		} catch (Exception e) {
			MessageUtils.addErrorMessage("msg", "系统添加组织机构失败!");
			log.error("保存部门出错！", e);
		}
		
	}
	
	/**
	 * 前往添加岗位弹出框， 初始化参数
	 */
	public void forwordSavePosition() {
		//设置父部门
		if("company".equals(selectedNode.getType())) {
			parentOrgNode = (O) selectedNode.getData();
		}else {
			MessageUtils.addErrorMessage("msg", "请选择部门!");
		}
		position = new S();
		
	}
	
	
	/**
	 * 保存岗位
	 */
	public void savePosition() {
		try {
			String bukrs = parentOrgNode.getBukrs();
			String tempSapCode = bukrs + "##S" + bukrs;
			String maxId = orgService.getMaxSId(tempSapCode);
			String sid = "";
			if(StringUtils.isNotBlank(maxId)) {
				sid = tempSapCode + MathUtil.idAddOne(maxId.substring(tempSapCode.length()));
			}else {
				sid = tempSapCode + "000001";
			}
			position.setId(sid);
			position.setStext(bukrs + "-" + position.getStext());
			position.setOid(parentOrgNode.getId());
			orgService.savePosition(position);
			new DefaultTreeNode(
					ORG_TYPE_POSITION, position, selectedNode);
			MessageUtils.addSuccessMessage("msg", "系统添加岗位成功!");
			RequestContext.getCurrentInstance().addCallbackParam("issucc", "yes");
		} catch (Exception e) {
			MessageUtils.addErrorMessage("msg", "系统添加岗位失败!");
			log.error("保存岗位出错！", e);
		}
	}
	
	/**
	 * 前往更新公司、部门弹出框， 初始化参数
	 */
	public void forwordUpdateCompany() {
		if(ORG_TYPE_COMPANY.equals(selectedNode.getType())) {
			org = (O) selectedNode.getData();
			parentOrgNode = orgService.getOrgById(org.getParent());
		}else {
			MessageUtils.addErrorMessage("msg", "请选择部门!");
		}
	}
	
	/**
	 * 更新公司、部门
	 */
	public void updateCompany() {
		try {
			orgService.updateOrg(org);
			MessageUtils.addSuccessMessage("msg", "系统更新组织机构成功!");
			RequestContext.getCurrentInstance().addCallbackParam("issucc", "yes");
		} catch (Exception e) {
			MessageUtils.addErrorMessage("msg", "系统更新组织机构失败!");
			log.error("更新公司、部门出错！", e);
		}
		
	}
	
	/**
	 * 前往更新岗位弹出框、初始化参数
	 */
	public void forwordUpdatePosition() {
		if(ORG_TYPE_POSITION.equals(selectedNode.getType())) {
			position = (S) selectedNode.getData();
			parentOrgNode = orgService.getOrgById(position.getOid());
		}else {
			MessageUtils.addErrorMessage("msg", "请选择岗位!");
		}
	}
	
	/**
	 * 更新岗位
	 */
	public void updatePosition() {
		try {
			orgService.updatePosition(position);
			MessageUtils.addSuccessMessage("msg", "系统更新岗位成功!");
			RequestContext.getCurrentInstance().addCallbackParam("issucc", "yes");
		} catch (Exception e) {
			MessageUtils.addErrorMessage("msg", "系统更新岗位失败!");
			log.error("更新岗位出错！", e);
		}
	}
	
	/**
	 * 删除部门
	 */
	public void deleteCompany() {
		O deleteOrg = (O)selectedNode.getData();
		List<O> subOrgList = orgService.getOrgByParent(deleteOrg.getId());
		if(subOrgList != null && subOrgList.size() > 0) {
			MessageUtils.addErrorMessage("msg", "该部门下含有子部门!");
			return;
		}
		List<S> subPositionList = orgService.getPositionByOrgId(deleteOrg.getId());
		if(subPositionList != null && subPositionList.size() > 0) {
			MessageUtils.addErrorMessage("msg", "该部门下含有岗位!");
			return;
		}
		deleteOrg.setDefunctInd("Y");
		orgService.updateOrg(deleteOrg);
		selectedNode.getParent().getChildren().remove(selectedNode);
		MessageUtils.addSuccessMessage("msg", "删除部门成功!");
	}
	
	/**
	 * 删除岗位
	 */
	public void deletePosition() {
		S deletePosition = (S)selectedNode.getData();
		List<P> subPersonList = stationService.findDistributedPersons(deletePosition);
		if(subPersonList != null && subPersonList.size() > 0) {
			MessageUtils.addErrorMessage("msg", "该岗位下含有人员!");
			return;
		}
		deletePosition.setDefunctInd("Y");
		orgService.updatePosition(deletePosition);
		selectedNode.getParent().getChildren().remove(selectedNode);
		MessageUtils.addSuccessMessage("msg", "删除岗位成功!");
	}
	
	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public String getSelectType() {
		return selectType;
	}

	public void setSelectType(String selectType) {
		this.selectType = selectType;
	}

	public O getParentOrgNode() {
		return parentOrgNode;
	}

	public void setParentOrgNode(O parentOrgNode) {
		this.parentOrgNode = parentOrgNode;
	}

	public O getOrg() {
		return org;
	}

	public void setOrg(O org) {
		this.org = org;
	}

	public S getPosition() {
		return position;
	}

	public void setPosition(S position) {
		this.position = position;
	}
}
