package com.wcs.tms.view.system.sysprocess;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.google.common.collect.Lists;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.view.ViewBaseBean;
import com.wcs.common.model.O;
import com.wcs.common.model.S;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.ProcessAuth;
import com.wcs.tms.model.ProcessDefine;
import com.wcs.tms.service.system.sysprocess.ProcessAuthService;
import com.wcs.tms.util.MessageUtils;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 流程权限授权</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@ManagedBean
@ViewScoped
public class ProcessAuthBean extends ViewBaseBean<ProcessAuth> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /** 公司组织架构树*/
    private TreeNode root;
    private TreeNode[] selectedNodes;
    /** 所有节点集合*/
    private List<TreeNode> totalNodes = new ArrayList<TreeNode>();
    /** 系统流程ID*/
    private Long processDefinId;

    @Inject
    private ProcessAuthService processAuthService;
    private Logger logger = Logger.getLogger(ProcessAuthBean.class);

    public ProcessAuthBean() {
        this.setupPage("/faces/system/systemProcess/processAuth.xhtml", "/faces/system/systemProcess/systemProcess-list.xhtml",
                null);
    }

    @PostConstruct
    public void initTreeNode() {
        // 组织 岗位树初始化
        root = new DefaultTreeNode("Root", null);
        totalNodes.clear();
        // 通过Request取得流程Id 并赋值到当前Bean
        Object obj = JSFUtils.getRequestParam("pdefinId");
        if (obj != null) {
            processDefinId = Long.parseLong(obj.toString());
        }
        try {
            // 得到所有公司机构
            List<O> companyList = processAuthService.findCompanyList();
            for (O company : companyList) {
                if (company.getParent() == null || "".equals(company.getParent())) {
                    TreeNode tn = new DefaultTreeNode(company, root);
                    totalNodes.add(tn);
                    // 组织下是否有岗位
                    List<S> postionList = this.processAuthService.findPositionByCompny(company.getId());
                    if (!postionList.isEmpty()) {
                        addCompanyPostion(postionList, tn);
                    }
                    // 添加下级机构
                    this.findSonCompany(companyList, tn, company);
                }
            }
            // 得到该流程分配的权限
            List<ProcessAuth> procesAuthList = this.processAuthService.findProcessAuthListByPId(processDefinId);
            if (procesAuthList.isEmpty()) { 
                return; 
            }
            selectProcessAuthTree(procesAuthList);
        } catch (Exception e) {
            this.logger.error("初始化组织架构树", e);
        }
    }

    /**
     * 
     * <p>Description: 保存流程权限分配</p>
     * @return
     */
    public String saveProcessAuth() {
        try {
            // 删除当前系统流程之前分配的权限
            this.processAuthService.deleteProcessAuthByPId(processDefinId);
            // 解析选择的组织架构树节点
            List<ProcessAuth> processList = this.combinationProAuth();
            // 保存流程权限分配记录
            this.processAuthService.batchSaveProcessAuth(processList);
            return this.getInputPage();
        } catch (Exception e) {
            this.logger.error("保存流程权限分配异常", e);
            MessageUtils.addErrorMessage("msgId", "保存流程权限分配失败");
            return null;
        }

    }

    /**
     * 
     * <p>Description: 跳转到流程授权界面</p>
     * @return
     */
    public String goProcessAuthList() {
        return this.getListPage();
    }

    /**
     * 
     * <p>Description: 递归公司，添加公司下属机构</p>
     * @param companyList
     * @param tn
     * @param company
     * @throws Exception 
     */
    private void findSonCompany(List<O> companyList, TreeNode tn, O company) throws Exception {
        for (O cm : companyList) {
            if(cm.getParent() != null && !"".equals(cm.getParent())){
                if (cm.getParent().equals(company.getId()) ) {
                    TreeNode n = new DefaultTreeNode(cm, tn);
                    totalNodes.add(n);
                    // 组织岗位
                    // 组织下岗位List
                    List<S> postionList = this.processAuthService.findPositionByCompny(cm.getId());
                    if (!postionList.isEmpty()) {
                        addCompanyPostion(postionList, n);
                    }
                    findSonCompany(companyList, n, cm);
                }
            }
        }
    }

    /**
     * 
     * <p>Description: 为组织添加岗位</p>
     * @param postionList
     * @param tn
     */
    private void addCompanyPostion(List<S> postionList, TreeNode tn) {
        for (S p : postionList) {
            TreeNode n = new DefaultTreeNode(p, tn);
            totalNodes.add(n);
        }
    }

    /**
     * 
     * <p>Description: 若有流程权限树分配节点则选中</p>
     * @throws Exception
     */
    private void selectProcessAuthTree(List<ProcessAuth> procesAuthList) throws Exception {
        try {
            for (ProcessAuth pa : procesAuthList) {
                String authId = null;
                if (pa.getO() != null) {
                    authId = pa.getO().getId();
                }
                if (pa.getS() != null) {
                    authId = pa.getS().getId();
                }
                // 若分配的流程权限在流程权限树当中则选中该节点
                for (TreeNode node : totalNodes) {
                    String nodeId = findNodeId(node);
                    if (authId.equals(nodeId)) {
                        node.setSelected(true);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            logger.error("流程权限树分配节点发生异常", e);
        }
    }

    /**
     * 
     * <p>Description: 得到节点Id</p>
     * @param node
     * @return
     * @throws Exception
     */
    private String findNodeId(TreeNode node) throws Exception {
        String id = null;
        if (node.getData() instanceof O) {
            O org = (O) node.getData();
            id = org.getId();
        }
        if (node.getData() instanceof S) {
            S s = (S) node.getData();
            id = s.getId();
        }
        return id;
    }

    /**
     * 
     * <p>Description: 通过权限树节点组合流程权限对象</p>
     * @return
     * @throws Exception
     */
    private List<ProcessAuth> combinationProAuth() throws Exception {
        // 选择的节点数组大小
        try {
            int size = this.getSelectedNodes().length;
            List<ProcessAuth> processAuthList = Lists.newArrayList();
            if (size != 0) {
                ProcessDefine processDefine = this.entityService.find(ProcessDefine.class, processDefinId);
                for (TreeNode t : selectedNodes) {
                    ProcessAuth pauth = new ProcessAuth();
                    // 是否是组织节点
                    if (t.getData() instanceof O) {
                        O org = (O) t.getData();
                        pauth.setO(org);
                    }
                    // 是否是岗位节点
                    if (t.getData() instanceof S) {
                        // 得到当前岗位节点所属的组织
                        S s = (S) t.getData();
                        O o = this.entityService.find(O.class, s.getOid());
                        pauth.setO(o);
                        pauth.setS(s);
                    }
                    // 设置系统流程
                    pauth.setProcessDefine(processDefine);
                    processAuthList.add(pauth);
                }
            }
            return processAuthList;
        } catch (Exception e) {
            logger.error("权限树节点组合流程权限对象发生异常", e);
            throw new ServiceException(e);
        }

    }

    // ---- --//
    public TreeNode[] getSelectedNodes() {
        return selectedNodes;
    }

    public void setSelectedNodes(TreeNode[] selectedNodes) {
        this.selectedNodes = selectedNodes;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public List<TreeNode> getTotalNodes() {
        return totalNodes;
    }

    public void setTotalNodes(List<TreeNode> totalNodes) {
        this.totalNodes = totalNodes;
    }

    public Long getProcessDefinId() {
        return processDefinId;
    }

    public void setProcessDefinId(Long processDefinId) {
        this.processDefinId = processDefinId;
    }

}
