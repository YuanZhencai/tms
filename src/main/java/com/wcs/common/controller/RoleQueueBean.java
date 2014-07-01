package com.wcs.common.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.primefaces.model.DualListModel;

import com.google.common.collect.Lists;
import com.wcs.base.service.EntityService;
import com.wcs.common.model.Rolemstr;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.QueueDefine;
import com.wcs.tms.view.process.common.entity.QueueInfo;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 角色队列绑定</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@ManagedBean
@ViewScoped
public class RoleQueueBean implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /** 所有队列*/
    private List<QueueInfo> source = new ArrayList<QueueInfo>();
    /** 选择的队列*/
    private List<QueueInfo> target = new ArrayList<QueueInfo>();
    /** 队列选择模型*/
    private DualListModel<QueueInfo> queueModel;
    private static final String QUEUE_LIST = "/faces/common/role/rolequeue-list.xhtml";
    @Inject
    private EntityService entityService;
    private Rolemstr role;

    @PostConstruct
    public void init() {
        source.clear();
        source.addAll(QueueDefine.findQueueList());
        if (role != null) {
            target = findSelectQueue(role);
        }
        
        /**** 9.13 huhan modify source去除包含target的部分***/
        List<QueueInfo> removeSource = new ArrayList<QueueInfo>();
        for(QueueInfo t : target){
        	for(QueueInfo s : source){
        		if(t.getQueueName_EN().equals(s.getQueueName_EN())){
        			removeSource.add(s);
        		}
        	}
        }
        source.removeAll(removeSource);
        /**** 9.13 huhan modify source去除包含target的部分***/
        
        queueModel = new DualListModel<QueueInfo>(source, target);
    }

    /**
     * 
     * <p>Description: 添加角色队列</p>
     * @return
     */
    public void addRoleQueue() {
        List<QueueInfo> queueList = queueModel.getTarget();
        Object[] queueObj = queueList.toArray();
        String queueName = findQueueEn(queueObj);
        role.setQueueName(queueName);
        this.entityService.update(role);
        MessageUtils.addSuccessMessage("msg", "角色工作流队列分配成功");
    }

    /**
     * 
     * <p>Description: 跳转工作流队列分配</p>
     * @return
     */
    public String goRoleQueuePage() {
        return QUEUE_LIST;
    }

    /**
     * 
     * <p>Description: 根据角色得到已经选择的工作队列</p>
     * @param role
     * @return
     */
    private List<QueueInfo> findSelectQueue(Rolemstr role) {
        List<QueueInfo> selectTarget = Lists.newArrayList();
        String queueName = role.getQueueName();
        if (queueName != null && !"".equals(queueName)) {
            String[] queueStr = queueName.split(",");
            if (queueStr != null) {
                int size = queueStr.length;
                for (int i = 0; i < size; i++) {
                    QueueInfo queueInfo = new QueueInfo();
                    queueInfo.setQueueName_CN(QueueDefine.queueMap().get(queueStr[i]));
                    queueInfo.setQueueName_EN(queueStr[i]);
                    selectTarget.add(queueInfo);
                }
            }
        }
        return selectTarget;
    }

    /**
     * 
     * <p>Description: 将选择的工作流队列组合成String</p>
     * @param list
     * @return
     */
    private String findQueueEn(Object[] obj) {
        String queueName = "";
        if (obj != null && obj.length != 0) {
            for (Object ob : obj) {
                if ("".equals(queueName)) {
                    queueName = ob.toString();
                } else {
                    queueName = queueName + "," + ob;
                }
            }
        }
        return queueName;
    }

    public List<QueueInfo> getSource() {
        return source;
    }

    public void setSource(List<QueueInfo> source) {
        this.source = source;
    }

    public List<QueueInfo> getTarget() {
        return target;
    }

    public void setTarget(List<QueueInfo> target) {
        this.target = target;
    }

    public DualListModel<QueueInfo> getQueueModel() {
        return queueModel;
    }

    public void setQueueModel(DualListModel<QueueInfo> queueModel) {
        this.queueModel = queueModel;
    }

	public Rolemstr getRole() {
		return role;
	}

	public void setRole(Rolemstr role) {
		this.role = role;
	}

}
