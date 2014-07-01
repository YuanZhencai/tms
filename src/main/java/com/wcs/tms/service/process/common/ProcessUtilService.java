package com.wcs.tms.service.process.common;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Lists;
import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.JSFUtils;
import com.wcs.tms.model.ProcessMap;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.entity.ProcessInstanceVo;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:流程处理"下一步"公共类service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Stateless
public class ProcessUtilService implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(ProcessUtilService.class);
    
    @Inject
    LoginService loginService;
    @Inject
    ProcessWaitAcceptService processWaitService;
    @Inject
    ProcessAcceptedService processAcceptService;
    @Inject
    EntityService entityService;
    
    
    
    /**
     * 得到下一个任务的页面(包括接收下一个任务)
     * @param originalPage 原先需要跳转的页面(基本都是已处理任务页面)
     * @param doNext 是否处理下一个任务
     * @return
     */
    public String getNextPage(String originalPage , boolean doNext){
    	try {
    		if(!doNext){
    			return originalPage;
    		}
    		ProcessInstanceVo next = new ProcessInstanceVo();
    		List<ProcessInstanceVo> processVoList = Lists.newArrayList();
    		//用户queue
    		List<String> userQueue = loginService.getQueueByUser();
    		
    		/****第一次循环处理 已接收任务*************/
    		for (String queueName : userQueue) {
    			List<ProcessInstanceVo> adlist = processAcceptService.vwfindAcceptTask(queueName);
    			if(!adlist.isEmpty()){
    				processVoList.addAll(adlist);
    			}
    		}
    		//已接收流程实例对象
    		if(processVoList.size()>0){
    			next = processVoList.get(0);
    			//处理任务
    			String url = this.processingTask(next);
    			//变更跳转页面
    			if (url == null || "".equals(url)) {
    				return originalPage;
    			}else{
    				MessageUtils.addSuccessMessage("msg", MessageUtils.getMessage("task_next_pid_msg", this.getTmsIdByFnId(next.getProcInstId())));
    				return url;
    			}
    		}
    		
    		
    		/****第二次循环处理 待接收任务*************/
    		for (String queueName : userQueue) {
    			List<ProcessInstanceVo> walist = processWaitService.vwFindNotReceiveTask(queueName);
    			if (!walist.isEmpty()) {
    				processVoList.addAll(walist);
    			}
    		}
    		//待接收流程实例对象
    		if(processVoList.size()>0){
    			next = processVoList.get(0);
    			//接收并处理任务
    			String url = this.approveTaskPage(next);
    			//变更跳转页面
    			if (url == null || "".equals(url)) {
    				return originalPage;
    			}else{
    				MessageUtils.addSuccessMessage("msg", MessageUtils.getMessage("task_next_pid_msg", this.getTmsIdByFnId(next.getProcInstId())));
    				return url;
    			}
    		}else{
    			return originalPage;
    		}
    		
		} catch (Exception e) {
			log.error("getNextPage方法 得到下一个任务的页面(包括接收下一个任务) 出现异常：",e);
			return originalPage;
		}
    }
    
    /**
     * 
     * <p>Description: (已接收任务)处理任务</p>
     * @return
     */
    private String processingTask(ProcessInstanceVo next) {
    	try {
    		String url = null;
    		if (next.getXmlPath() != null && !"".equals(next.getXmlPath())) {
    			url = next.getXmlPath();
    		}else{
    			url = processWaitService.findStepUrl(next.getNodeName(), next.getProcessCode());	
    		}
    		if (url == null || "".equals(url)) {
    			//MessageUtils.addErrorMessage("msg", "下一个任务没有绑定表单");
    			MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("task_next_not_bound_form"));
    		}
    		JSFUtils.getRequest().setAttribute("procInstId", next.getProcInstId());
    		JSFUtils.getRequest().setAttribute("op", "approve");
    		JSFUtils.getRequest().setAttribute("stepName", next.getNodeName());
    		
    		return url;
		} catch (Exception e) {
			log.error("processingTask方法 处理任务 出现异常：",e);
            //MessageUtils.addErrorMessage("msg", "下一个任务处理失败，请用待接收/已接收任务重试");
            MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("task_next_deal_failed"));
            return null;
		}
    }
    
    /**
     * 
     * <p>Description: (已接收任务)处理任务</p>
     * @return
     */
    private String processingTask(ProcessInstanceVo next, Integer currentIndex, String currentTaskType) {
    	try {
    		String url = null;
    		if (next.getXmlPath() != null && !"".equals(next.getXmlPath())) {
    			url = next.getXmlPath();
    		}else{
    			url = processWaitService.findStepUrl(next.getNodeName(), next.getProcessCode());	
    		}
    		if (url == null || "".equals(url)) {
    			//MessageUtils.addErrorMessage("msg", "下一个任务没有绑定表单");
    			MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("task_next_not_bound_form"));
    		}
    		JSFUtils.getRequest().setAttribute("procInstId", next.getProcInstId());
    		JSFUtils.getRequest().setAttribute("op", "approve");
    		JSFUtils.getRequest().setAttribute("stepName", next.getNodeName());
    		JSFUtils.getRequest().setAttribute("currentIndex", currentIndex.toString());
    		JSFUtils.getRequest().setAttribute("currentTaskType", currentTaskType);
    		return url;
		} catch (Exception e) {
			log.error("processingTask方法 处理任务 出现异常：",e);
            //MessageUtils.addErrorMessage("msg", "下一个任务处理失败，请用待接收/已接收任务重试");
            MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("task_next_deal_failed"));
            return null;
		}
    }
    
    /**
     * 
     * <p>Description:(待接收任务)接收任务并跳转到相应的审批界面 </p>
     * @return
     */
    private String approveTaskPage(ProcessInstanceVo next) {
        try {
            String url = null;
            if (next.getXmlPath() != null && !"".equals(next.getXmlPath())) {
                url = next.getXmlPath();
            }else{
            	url = processWaitService.findStepUrl(next.getNodeName(), next.getProcessCode());	
            }
            if (url == null || "".equals(url)) {
            //MessageUtils.addErrorMessage("msg", "下一个任务没有绑定表单");
              MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("task_next_not_bound_form"));
            }
            // 接收任务
            int flag = processWaitService.vwAcceptTask(next.getProcInstId());
            if (flag == 1) {
                //MessageUtils.addWarnMessage("msg", "下一个任务已被其他人接收");
                MessageUtils.addWarnMessage("msg", MessageUtils.getMessage("task_next_accept_by_one_yet"));
                return null;
            }
            JSFUtils.getRequest().setAttribute("procInstId", next.getProcInstId());
            JSFUtils.getFlash().put("workObjNum", next.getProcInstId());
            JSFUtils.getRequest().setAttribute("op", "approve");
            JSFUtils.getRequest().setAttribute("stepName", next.getNodeName());
            return url;
        } catch (Exception e) {
            log.error("approveTaskPage方法 接收任务并跳转到相应的审批界面 出现异常：",e);
            //MessageUtils.addErrorMessage("msg", "下一个任务处理失败，请用待接收/已接收任务重试");
            MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("task_next_deal_failed"));
            return null;
        }
    }
    
    /**
     * 
     * <p>Description:(待接收任务)接收任务并跳转到相应的审批界面 </p>
     * @return
     */
    private String approveTaskPage(ProcessInstanceVo next, Integer currentIndex, String currentTaskType) {
        try {
            String url = null;
            if (next.getXmlPath() != null && !"".equals(next.getXmlPath())) {
                url = next.getXmlPath();
            }else{
            	url = processWaitService.findStepUrl(next.getNodeName(), next.getProcessCode());	
            }
            if (url == null || "".equals(url)) {
            //MessageUtils.addErrorMessage("msg", "下一个任务没有绑定表单");
              MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("task_next_not_bound_form"));
            }
            // 接收任务
            int flag = processWaitService.vwAcceptTask(next.getProcInstId());
            if (flag == 1) {
                //MessageUtils.addWarnMessage("msg", "下一个任务已被其他人接收");
                MessageUtils.addWarnMessage("msg", MessageUtils.getMessage("task_next_accept_by_one_yet"));
                return null;
            }
            JSFUtils.getRequest().setAttribute("procInstId", next.getProcInstId());
            JSFUtils.getFlash().put("workObjNum", next.getProcInstId());
            JSFUtils.getRequest().setAttribute("op", "approve");
            JSFUtils.getRequest().setAttribute("stepName", next.getNodeName());
            JSFUtils.getRequest().setAttribute("currentIndex", currentIndex.toString());
    		JSFUtils.getRequest().setAttribute("currentTaskType", currentTaskType);
            return url;
        } catch (Exception e) {
            log.error("approveTaskPage方法 接收任务并跳转到相应的审批界面 出现异常：",e);
            //MessageUtils.addErrorMessage("msg", "下一个任务处理失败，请用待接收/已接收任务重试");
            MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("task_next_deal_failed"));
            return null;
        }
    }
    /**
     * 得到TMS流程实例编号(防止循环注入，把方法拷贝过来)
     * @param procInstId
     * @return
     */
    private String getTmsIdByFnId(String procInstId){
    	ProcessMap pm = new ProcessMap();
    	String jpql = "select pm from ProcessMap pm where pm.pidFn = '"+ procInstId +"'";
    	List<ProcessMap> pms = entityService.find(jpql);
    	if(pms!=null && pms.size()>0){
    		pm = pms.get(0);
    	}
    	if(pm.getPidTms()==null || "".equals(pm.getPidTms())){
    		return procInstId;
    	}
    	return pm.getPidTms();
    }

    /**
     * 获取列表中当前处理任务的下个任务页面 on 2013-5-17
     * @param originalPage
     * @param doNext
     * @param currentIndex
     * @param currentTaskType
     * @return
     */
	public String getNextPage(String originalPage, Boolean doNext, Integer currentIndex, String currentTaskType) {
	    long startTime = System.currentTimeMillis();
	    try{			    
			if(!doNext){
				return originalPage;
			}
			ProcessInstanceVo next = new ProcessInstanceVo();
			List<ProcessInstanceVo> processVoList = Lists.newArrayList();
			//用户queue
			List<String> userQueue = loginService.getQueueByUser();
			//如果当前处理任务是待接收任务类型
			if("waitAccept".equals(currentTaskType)){
				for (String queueName : userQueue) {
	    			List<ProcessInstanceVo> walist = processWaitService.vwFindNotReceiveTask(queueName);
	    			if (!walist.isEmpty()) {
	    				processVoList.addAll(walist);
	    			}
	    		}
				// 如果还有剩余待接收任务则使用当前任务索引参数获取
				if(processVoList.size() > currentIndex){
					
					next = processVoList.get(currentIndex);
					//接收并处理任务
	    			String url = this.approveTaskPage(next,currentIndex,currentTaskType);
	    			//变更跳转页面
	    			if (url == null || "".equals(url)) {
	    				return originalPage;
	    			}else{
	    				MessageUtils.addSuccessMessage("msg", MessageUtils.getMessage("task_next_pid_msg", this.getTmsIdByFnId(next.getProcInstId())));
	    				return url;
	    			}
	    			
	    		// 如果没有剩余待接收任务则处理已接受任务
				}else{
					
					processVoList.clear();
					for (String queueName : userQueue) {
		    			List<ProcessInstanceVo> adlist = processAcceptService.vwfindAcceptTask(queueName);
		    			if(!adlist.isEmpty()){
		    				processVoList.addAll(adlist);
		    			}
		    		}
					//已接收流程实例对象
					if(processVoList.size()>0){
						next = processVoList.get(0);
						//处理任务
						String url = this.processingTask(next, currentIndex, currentTaskType);
						//变更跳转页面
						if (url == null || "".equals(url)) {
							return originalPage;
						}else{
							MessageUtils.addSuccessMessage("msg", MessageUtils.getMessage("task_next_pid_msg", this.getTmsIdByFnId(next.getProcInstId())));
							return url;
						}
					}else{
						return originalPage;
					}
				}
			}else if("acceptType".equals(currentTaskType)){
				
				for (String queueName : userQueue) {
	    			List<ProcessInstanceVo> adlist = processAcceptService.vwfindAcceptTask(queueName);
	    			if(!adlist.isEmpty()){
	    				processVoList.addAll(adlist);
	    			}
	    		}
				if(processVoList.size() > currentIndex){
					next = processVoList.get(currentIndex);
					//处理任务
					String url = this.processingTask(next, currentIndex, currentTaskType);
					//变更跳转页面
					if (url == null || "".equals(url)) {
						return originalPage;
					}else{
						MessageUtils.addSuccessMessage("msg", MessageUtils.getMessage("task_next_pid_msg", this.getTmsIdByFnId(next.getProcInstId())));
						return url;
					}
				}else{
					processVoList.clear();
					for (String queueName : userQueue) {
		    			List<ProcessInstanceVo> walist = processWaitService.vwFindNotReceiveTask(queueName);
		    			if (!walist.isEmpty()) {
		    				processVoList.addAll(walist);
		    			}
		    		}
					if(processVoList.size()>0){
						next = processVoList.get(0);
						//接收并处理任务
		    			String url = this.approveTaskPage(next, currentIndex, currentTaskType);
		    			//变更跳转页面
		    			if (url == null || "".equals(url)) {
		    				return originalPage;
		    			}else{
		    				MessageUtils.addSuccessMessage("msg", MessageUtils.getMessage("task_next_pid_msg", this.getTmsIdByFnId(next.getProcInstId())));
		    				return url;
		    			}
					}else{
						return originalPage;
					}
				}
				
			}else{
				return originalPage;
			}
		} catch (Exception e) {
			log.error("getNextPage方法 获取列表中当前处理任务的下个任务页面 出现异常：",e);
			return originalPage;
		}finally {
		    long endTime = System.currentTimeMillis();
		    log.info("获取列表中当前处理任务的下个任务页面，总耗时："+(endTime-startTime));
		}
	}
	
}
