package com.wcs.tms.view.process.common;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

import com.google.common.collect.Lists;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.StringUtils;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.service.process.common.ProcessWaitAcceptService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.entity.ProcessInstanceVo;

import filenet.vw.api.VWException;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 待接收任务Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@ManagedBean
@ViewScoped
public class ProcessWaitAcceptBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /** 待接收任务分页模型*/
    private List<ProcessInstanceVo> prcessWaitAcceModel = Lists.newArrayList();
    /** 当前待接收任务VO对象*/
    private ProcessInstanceVo curentProInstance;
    /** 当前任务在列表里面的位置 */
    private Integer index;
    /** 用户队列List*/
    private List<String> userQueue;
    private List<ProcessInstanceVo> processVoList = Lists.newArrayList();
    @Inject
    private ProcessWaitAcceptService processWaitService;
    @Inject
    private LoginService loginService;
    
    private static final Log log = LogFactory.getLog(ProcessWaitAcceptBean.class);

    @PostConstruct
    public void init() {
        try {
            // TODO 根据当前用户查询对应的队列集合
            // TODO 需要根据不同人梯不同公司所发起的流程(过滤发起人所属公司和用户公司是否匹配)
        	long startTime = System.currentTimeMillis();
            userQueue = loginService.getQueueByUser();
            long endTime = System.currentTimeMillis();
            log.info("待接收任务初始化查询当前用户拥有队列耗时："+(endTime - startTime));
            log.info("JSF实现代码  ----  "
                    + javax.faces.webapp.FacesServlet.class.getProtectionDomain().getCodeSource().getLocation().toString());
            serach();
        } catch (ServiceException e) {
            log.error("init方法 构造方法 出现异常：",e);
        }
    }

    /**
     * 
     * <p>Description:接收任务但是不处理该任务 </p>
     */
    public void receiveTaskNoOption() {
        Object param = JSFUtils.getRequestParam("workObjNum");
        if (param == null) {
            MessageUtils.addErrorMessage("msg", "工作流程实例编号为空");
            return;
        }
        String workObjNum = param.toString();
        try {
            int flag = processWaitService.vwAcceptTask(workObjNum);
            if (flag == 1) {
                for (ProcessInstanceVo pin : processVoList) {
                    if (pin != null && workObjNum != null && workObjNum.equals(pin.getProcInstId())) {
                        processVoList.remove(pin);
                        break;
                    }
                }
                serach();
                MessageUtils.addWarnMessage("msg", "该任务已被其他人接收");
                return;
            }
            // TODO 根据当前用户查询对应的队列集合
            // TODO 需要根据不同人梯不同公司所发起的流程(过滤发起人所属公司和用户公司是否匹配)
            serach();
            MessageUtils.addSuccessMessage("msg", "接收任务成功");
        } catch (ServiceException e) {
            log.error("receiveTaskNoOption方法 接收任务但是不处理该任务 出现异常：",e);
        } catch (Exception e) {
            if (e instanceof EJBException) {
                try {
                    ServiceException se = (ServiceException) ((EJBException) e).getCause();
                    MessageUtils.addErrorMessage("msg", se.getMessage());
                } catch (Exception e1) {
                    MessageUtils.addErrorMessage("msg", "事务异常");
                }
            }
            if (e instanceof InvocationTargetException) {
                ServiceException se = (ServiceException) ((InvocationTargetException) e).getTargetException().getCause();
                MessageUtils.addErrorMessage("msg", se.getMessage());
            }
        }
    }

    /**
     * 
     * <p>Description:接收任务并跳转到相应的审批界面 </p>
     * @return
     */
    public String approveTaskPage() {
        try {
            String url = null;
            if (curentProInstance.getXmlPath() != null && !"".equals(curentProInstance.getXmlPath())) {
                url = curentProInstance.getXmlPath();
            }else{
            	url = processWaitService.findStepUrl(curentProInstance.getNodeName(), curentProInstance.getProcessCode());	
            }
            if (url == null || "".equals(url)) {
              MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("task_not_bound_form"));
            }
            // 接收任务
            int flag = processWaitService.vwAcceptTask(this.curentProInstance.getProcInstId());
            if (flag == 1) {
                for (ProcessInstanceVo pin : processVoList) {
                    if (pin != null && curentProInstance != null
                            && curentProInstance.getProcInstId().equals(pin.getProcInstId())) {
                        processVoList.remove(pin);
                        break;
                    }
                }
                serach();
                MessageUtils.addWarnMessage("msg", MessageUtils.getMessage("task_accept_by_one_yet"));
                return null;
            }
            JSFUtils.getRequest().setAttribute("procInstId", curentProInstance.getProcInstId());
            JSFUtils.getFlash().put("workObjNum", curentProInstance.getProcInstId());
            JSFUtils.getRequest().setAttribute("op", "approve");
            JSFUtils.getRequest().setAttribute("stepName", curentProInstance.getNodeName());
            //用于处理任务时获取下一任务页面参数 on 2013-5-17
            index = prcessWaitAcceModel.indexOf(curentProInstance);
            JSFUtils.getRequest().setAttribute("currentIndex", index.toString());
            JSFUtils.getRequest().setAttribute("currentTaskType", "waitAccept");
            return url;
        } catch (Exception e) {
            log.error("approveTaskPage方法 接收任务并跳转到相应的审批界面 出现异常：",e);
            MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("task_deal_failed"));
            return null;
        }
    }

    /**
     * 
     * <p>Description:跳转到查看详细 </p>
     * @return
     */
    public void viewDetailPage() {
        String viewUrl = null;
        // 是否查看确认单View
        String confirmView = ProcessXmlUtil.findStepProperty("code", getCurentProInstance().getProcessCode(),
                getCurentProInstance().getNodeName(), "confirmView");
        // 是否有确认单查看URL
        if (confirmView != null && "true".equals(confirmView)) {
            viewUrl = ProcessXmlUtil.getProcessAttribute("code", this.getCurentProInstance().getProcessCode(),
                    "viewConfirmPage");
        } else {
            // 得到详细页面URL
            viewUrl = ProcessXmlUtil.getProcessAttribute("code", this.getCurentProInstance().getProcessCode(), "viewPage");
        }
        System.out.println("[viewUrl]" + viewUrl);
        JSFUtils.getRequest().setAttribute("procInstId", curentProInstance.getProcInstId());
        JSFUtils.getRequest().setAttribute("op", "view");
        JSFUtils.getRequest().setAttribute("stepName", curentProInstance.getNodeName());
        
      //设置弹出窗口url的参数
        RequestContext.getCurrentInstance().addCallbackParam("viewPage",StringUtils.safeString(JSFUtils.contextPath()+viewUrl));
        RequestContext.getCurrentInstance().addCallbackParam("op","view");
        RequestContext.getCurrentInstance().addCallbackParam("menu2",StringUtils.safeString(JSFUtils.getRequestParam("menu2")));
        RequestContext.getCurrentInstance().addCallbackParam("procInstId",StringUtils.safeString(curentProInstance.getProcInstId()));
        RequestContext.getCurrentInstance().addCallbackParam("stepName",StringUtils.safeString(curentProInstance.getNodeName()));  
        
    }

    /**
     * 
     * <p>Description:查询待接收任务 </p>
     * @throws VWException
     */
    private void serach() throws ServiceException {
    	long startTime = System.currentTimeMillis();
        processVoList.clear();
        for (String queueName : userQueue) {
            List<ProcessInstanceVo> list = processWaitService.vwFindNotReceiveTask(queueName);
            if (!list.isEmpty()) {
                processVoList.addAll(list);
            }
        }
        prcessWaitAcceModel = processVoList;
        long endTime = System.currentTimeMillis();
        log.info("待接收任务根据用户拥有队列查询所有待接收任务耗时："+(endTime - startTime));
        
    }

    public List<ProcessInstanceVo> getProcessVoList() {
        return processVoList;
    }

    public void setProcessVoList(List<ProcessInstanceVo> processVoList) {
        this.processVoList = processVoList;
    }

    public List<ProcessInstanceVo> getPrcessWaitAcceModel() {
        return prcessWaitAcceModel;
    }

    public void setPrcessWaitAcceModel(List<ProcessInstanceVo> prcessWaitAcceModel) {
        this.prcessWaitAcceModel = prcessWaitAcceModel;
    }

    public ProcessInstanceVo getCurentProInstance() {
        return curentProInstance;
    }

    public void setCurentProInstance(ProcessInstanceVo curentProInstance) {
        this.curentProInstance = curentProInstance;
    }

    public List<String> getUserQueue() {
        return userQueue;
    }

    public void setUserQueue(List<String> userQueue) {
        this.userQueue = userQueue;
    }

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

}
