package com.wcs.tms.view.system.sysprocess;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;

import com.wcs.base.util.JSFUtils;
import com.wcs.base.view.ViewBaseBean;
import com.wcs.tms.model.ProcessNode;
import com.wcs.tms.service.system.sysprocess.ProcessNodeService;
import com.wcs.tms.util.CustomPageModel;
import com.wcs.tms.util.MessageUtils;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@ManagedBean
@ViewScoped
public class ProcessNodeBean extends ViewBaseBean<ProcessNode> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** 流程节点模型*/
    private LazyDataModel<ProcessNode> lazyModel;
    /** 流程节点集合*/
    private List<ProcessNode> processNodeList = new ArrayList<ProcessNode>();
    /** 流程定义Id*/
    private Long processDefinId;
    private ProcessNode processNode;
    /** 编辑时对象*/
    private ProcessNode editProcessNode;

    private Log logger = LogFactory.getLog(ProcessNodeBean.class);

    @Inject
    private ProcessNodeService processNodeService;

    public ProcessNodeBean() {
        this.setupPage("/faces/system/systemProcess/processNode.xhtml", "/faces/system/systemProcess/systemProcess-list.xhtml",
                null);
    }

    @PostConstruct
    public void init() {
        Object obj = JSFUtils.getRequestParam("pnodeId");
        if (obj != null) {
            processDefinId = Long.parseLong(obj.toString());
            List<ProcessNode> list = processNodeService.findProcessNodeList(processDefinId);
            if (!list.isEmpty()) {
                processNodeList.clear();
                processNodeList.addAll(list);
                lazyModel = new CustomPageModel<ProcessNode>(processNodeList, true);
            }
        }
    }

    /**
     * 
     * <p>Description: 逻辑添加流程节点表单绑定</p>
     */
    public void addProcessNodeLogic() {
        try {
            if (!processNodeList.isEmpty()) {
                for (ProcessNode node : processNodeList) {
                    if (node.getNodeName() != null && node.getNodeName().equals(getInstance().getNodeName())) {
                        MessageUtils.addErrorMessage("processNodeMsg", "该节点已经绑定了URL");
                        return;
                    }
                }
            }
            getInstance().setStatus("Y");
            processNodeList.add(this.getInstance());
            lazyModel = new CustomPageModel<ProcessNode>(processNodeList, true);
            // 清空添加From信息
            this.setInstance(new ProcessNode());
            MessageUtils.addSuccessMessage("processNodeMsg", "添加流程节点成功");
        } catch (Exception e) {
            MessageUtils.addErrorMessage("processNodeMsg", "添加流程表节点失败");
            this.logger.error("添加流程节点异常", e);
        }

    }

    /**
     *  
     * <p>Description: 物理添加流程节点</p>
     * @return
     */
    public String addProcessNodePhysical() {
        try {
            this.processNodeService.batchSaveProcessNode(processNodeList, processDefinId);
            MessageUtils.addSuccessMessage("msg", "保存流程节点成功");
            return this.getInputPage();
        } catch (Exception e) {
            MessageUtils.addErrorMessage("processNodeMsg", "保存流程节点失败");
            this.logger.error("保存流程节点绑定异常", e);
        }
        return null;
    }

    /**
     * 
     * <p>Description: 逻辑删除流程节点</p>
     */
    public void deleteProcessNode() {
        processNodeList.remove(processNode);
        lazyModel = new CustomPageModel<ProcessNode>(processNodeList, true);
        MessageUtils.addSuccessMessage("processNodeMsg", "删除流程节点成功");
    }

    public void updateProcessNode() {
        lazyModel = new CustomPageModel<ProcessNode>(processNodeList, true);
        MessageUtils.addSuccessMessage("processNodeMsg", "修改授信成功");
    }

    public String goList() {
        return this.getListPage();
    }

    public LazyDataModel<ProcessNode> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<ProcessNode> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public List<ProcessNode> getProcessNodeList() {
        return processNodeList;
    }

    public void setProcessNodeList(List<ProcessNode> processNodeList) {
        this.processNodeList = processNodeList;
    }

    public Long getProcessDefinId() {
        return processDefinId;
    }

    public void setProcessDefinId(Long processDefinId) {
        this.processDefinId = processDefinId;
    }

    public ProcessNode getProcessNode() {
        return processNode;
    }

    public void setProcessNode(ProcessNode processNode) {
        this.processNode = processNode;
    }

    public ProcessNode getEditProcessNode() {
        return editProcessNode;
    }

    public void setEditProcessNode(ProcessNode editProcessNode) {
        this.editProcessNode = editProcessNode;
    }

}
