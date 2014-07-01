package com.wcs.tms.view.process.common;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Lists;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.JSFUtils;
import com.wcs.tms.service.process.common.ProcessAcceptedService;
import com.wcs.tms.service.process.common.ProcessWaitAcceptService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.entity.ProcessInstanceVo;

import filenet.vw.api.VWException;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 已接收任务</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@ManagedBean
@ViewScoped
public class ProcessAcceptedBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(ProcessAcceptedBean.class);

    /** 已接收任务模型*/
    private List<ProcessInstanceVo> processAcceptModel = Lists.newArrayList();
    /** 已接收任务List*/
    private List<ProcessInstanceVo> acceptPressList = Lists.newArrayList();
    /** 已接收任务VO*/
    private ProcessInstanceVo processVo;
    /** 用户队列List*/
    private List<String> userQueue;
    /** 返回任务传递过来的工作流编号*/
    private String workProcessNumber;
    /** 当前任务在列表里面的位置 */
    private Integer index;
    @Inject
    private ProcessAcceptedService processAcceptService;
    @Inject
    private ProcessWaitAcceptService processWaitService;
    @Inject
    private LoginService loginService;

    @PostConstruct
    public void init() {

        try {

            // TODO 根据当前用户查询对应的队列集合
            // TODO Inbox 用户队列目前没有查询
            userQueue = loginService.getQueueByUser();
            for (String queueName : userQueue) {
                List list = processAcceptService.vwfindAcceptTask(queueName); 
                acceptPressList.addAll(list);
            }
            processAcceptModel = acceptPressList;
        } catch (VWException e) {
            log.error("init方法 构造方法 出现异常：",e);
        }
    }

    /**
     * 
     * <p>Description:释放当前用户的任务 </p>
     */
    public void releaseTask() {
        try {
            if (this.workProcessNumber == null) {
            	MessageUtils.addErrorMessage("", MessageUtils.getMessage("txt_processNumberNull"));
                return;
            }
            processAcceptService.vwDiscardedTask(workProcessNumber);
            // TODO 根据当前用户查询对应的队列集合
            // TODO Inbox 用户队列目前没有查询
            acceptPressList.clear();
            userQueue = loginService.getQueueByUser();
            for (String queueName : userQueue) {
                List list = processAcceptService.vwfindAcceptTask(queueName);
                acceptPressList.addAll(list);
            }
            processAcceptModel = acceptPressList;
            MessageUtils.addSuccessMessage("msg", MessageUtils.getMessage("txt_returnTaskSuccess"));
        } catch (Exception e) {
            log.error("releaseTask方法 释放当前用户的任务 出现异常：",e);
        }
    }

    /**
     * 
     * <p>Description: 处理任务</p>
     * @return
     */
    public String processingTask() {
        String url = null;
        if (processVo.getXmlPath() != null && !"".equals(processVo.getXmlPath())) {
            url = processVo.getXmlPath();
        }else{
        	url = processWaitService.findStepUrl(processVo.getNodeName(), processVo.getProcessCode());	
        }
        if (url == null || "".equals(url)) {
            MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("txt_taskNoBindingForm"));
        }
        JSFUtils.getRequest().setAttribute("procInstId", processVo.getProcInstId());
        JSFUtils.getRequest().setAttribute("op", "approve");
        JSFUtils.getRequest().setAttribute("stepName", processVo.getNodeName());
        //用于处理任务时获取下一任务页面参数 on 2013-5-17
        setIndex(processAcceptModel.indexOf(processVo));
        JSFUtils.getRequest().setAttribute("currentIndex", getIndex().toString());
        JSFUtils.getRequest().setAttribute("currentTaskType", "acceptType");
        return url;
    }

    public List<ProcessInstanceVo> getProcessAcceptModel() {
        return processAcceptModel;
    }

    public void setProcessAcceptModel(List<ProcessInstanceVo> processAcceptModel) {
        this.processAcceptModel = processAcceptModel;
    }

    public List<ProcessInstanceVo> getAcceptPressList() {
        return acceptPressList;
    }

    public void setAcceptPressList(List<ProcessInstanceVo> acceptPressList) {
        this.acceptPressList = acceptPressList;
    }

    public ProcessInstanceVo getProcessVo() {
        return processVo;
    }

    public void setProcessVo(ProcessInstanceVo processVo) {
        this.processVo = processVo;
    }

    public List<String> getUserQueue() {
        return userQueue;
    }

    public void setUserQueue(List<String> userQueue) {
        this.userQueue = userQueue;
    }

    public String getWorkProcessNumber() {
        return workProcessNumber;
    }

    public void setWorkProcessNumber(String workProcessNumber) {
        this.workProcessNumber = workProcessNumber;
    }

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

}
