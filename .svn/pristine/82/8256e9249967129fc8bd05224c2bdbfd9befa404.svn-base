package com.wcs.tms.view.system.sysprocess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Lists;
import com.wcs.base.service.EntityService;
import com.wcs.base.view.ViewBaseBean;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.model.PELog;
import com.wcs.tms.model.ProcessDefine;
import com.wcs.tms.service.system.sysprocess.ProcessDefineService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.util.ProcessDefineUtil;

import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWWorkObject;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:系统流程定义Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@ManagedBean
@ViewScoped
public class ProcessDefineBean extends ViewBaseBean<ProcessDefine>{

	private static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(ProcessDefineBean.class);
	
	@Inject 
	EntityService entityService;
	@Inject
	ProcessDefineService processDefineService;
	@Inject
	PEManager peManager;
	
	//查询条件map
	private Map<String, Object> conditionMap = new HashMap<String, Object>();
	//List
	private List<ProcessDefine> processDefineList = Lists.newArrayList();
	//流程定义对象
	private ProcessDefine processDefine = new ProcessDefine();
	
	//流程编号
	private String processCode;
	
	private String op = "新增";
    
    /**
	 * <p>Description:Bean init </p>
	 */
	@PostConstruct
	public void initProcDefine(){
		log.info("initProcessDefineBean~~~~~~~~~~~~~~");
		if(processDefineList.size() == 0){
		    /* 默认查询所有系统流程定义 */
			searchPd(); 
		}
		try {
//			this.addToPELog();
        	log.info("***************通过日志取生产数据add on 2013-7-29 by yan************");
        	//得到所有活动流程实例
			List<VWWorkObject> VWObjects = peManager.vwGetTmsWorkObjects();
			List<String> workProcIds = new ArrayList<String>();
			HashSet<String> set = new HashSet<String>();
			for(VWWorkObject o : VWObjects){
				if(set.add(o.getWorkflowNumber())){
					workProcIds.add(o.getWorkflowNumber());
				}
			}
			StringBuilder fIdStr = new StringBuilder("(");
			int max = workProcIds.size()-1;
			for(String fId : workProcIds){
				fIdStr.append("'"+fId+"'");
				if( workProcIds.indexOf(fId) != max){
					fIdStr.append(",");
				}
			}
			fIdStr.append(");");
			log.info("********需要取的数据个数"+workProcIds.size()+"***********");
			log.info(fIdStr);
			log.info("**************抓数据完毕***********************");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}
    
    private void addToPELog() throws Exception {
    	String filter = "1=1 and F_TIMESTAMP >= :time1 and F_TIMESTAMP < :time2 and (F_EventType = :eventType1 or F_EventType = :eventType2)";
        List<VWLogElement> les = new ArrayList<VWLogElement>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateStart = sdf.parse("2012-5-1 00:00:00");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateStart);
        // 一个月的log数据
        for(int i = 0;i < 550; i++){
        	Date time1 = calendar.getTime();
        	calendar.add(Calendar.DAY_OF_YEAR, 1);
        	Date time2 = calendar.getTime();
        	System.out.println("时间1："+time1 +"时间2："+time2);
        	Object[] substitutionVars = {time1, time2,ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd),
	                352 };
        	les = peManager.vwEventLogWob(filter, substitutionVars);
        	for (VWLogElement le : les) {
        		PELog peLog = new PELog();
        		peLog.setEventType(new Integer(le.getEventType()).toString());
        		peLog.setStepName(le.getStepName());
        		peLog.setTimeStamp(le.getTimeStamp());
        		peLog.setWobNum(le.getWorkFlowNumber());
        		peLog.setWorkClassName(le.getWorkClassName());
        		entityService.create(peLog);
        	}
        }
	}



	/**
     * 查询流程定义列表
     */
    public void searchPd(){
    	log.info("查询流程定义");
    	processDefineList = processDefineService.findProcessDf(conditionMap);
    	
    }
    
    
    
    
    /**
	 * <p>Description: 弹出新增dialog</p>
	 */
	public void clear(){
		op = "新增";
		processDefine = new ProcessDefine();
		processCode = "";
	}
    
    
	/**
	 * <p>Description: 弹出修改dialog</p>
	 */
	public void toEdit(){
	    op = "修改";
	    Long processDefineId = processDefine.getId();
	    processDefine = entityService.find(ProcessDefine.class, processDefineId);
	    processCode = processDefine.getProcessCode();
	}
	
	/**
	 * 检查银行代码唯一性
	 */
	public boolean checkCode(){
		if(processCode!=null && processCode.equals(processDefine.getProcessCode())){
			return true;
		}
		boolean check = processDefineService.checkCode(processDefine.getProcessCode());
		if(!check){
			MessageUtils.addErrorMessage("errorMsg", "流程编号已存在！");
		}
		return check;
	}
	
	/**
	 * <p>Description: 添加流程定义保存</p>
	 */
	public void savePd(){
		if(!checkCode()){
			return;
		}
		try {
			processDefineService.saveOrUpdate(processDefine);
			MessageUtils.addSuccessMessage("msg", "流程定义保存成功！");
		} catch (Exception e) {
			MessageUtils.addErrorMessage("errorMsg", "流程定义保存失败！");
		}
		searchPd();
	}
	
	
	/**
	 * <p>Description: 流程定义启用/禁用</p>
	 */
	public void disable(){
		String status = processDefine.getStatus();
		if("Y".equals(status)){
		    //禁用
			processDefine.setStatus("N");
		}else{
		    //启用
			processDefine.setStatus("Y");
		}
		try {
			processDefineService.saveOrUpdate(processDefine);
			MessageUtils.addSuccessMessage("msg", "流程定义" + ("Y".equals(status)? "禁用" : "启用") + "成功！");
		} catch (Exception e) {
			MessageUtils.addErrorMessage("errorMsg", "流程定义" + ("Y".equals(status)? "禁用" : "启用") + "失败！");
		}
	}
    
    
    
    
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
    public String getProcessCode() {
		return processCode;
	}

	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

	/**
	 * @return the conditionMap
	 */
	public Map<String, Object> getConditionMap() {
		return conditionMap;
	}

	/**
	 * @param conditionMap the conditionMap to set
	 */
	public void setConditionMap(Map<String, Object> conditionMap) {
		this.conditionMap = conditionMap;
	}

	/**
	 * @return the processDefineList
	 */
	public List<ProcessDefine> getProcessDefineList() {
		return processDefineList;
	}

	/**
	 * @param processDefineList the processDefineList to set
	 */
	public void setProcessDefineList(List<ProcessDefine> processDefineList) {
		this.processDefineList = processDefineList;
	}

	/**
	 * @return the processDefine
	 */
	public ProcessDefine getProcessDefine() {
		return processDefine;
	}

	/**
	 * @param processDefine the processDefine to set
	 */
	public void setProcessDefine(ProcessDefine processDefine) {
		this.processDefine = processDefine;
	}

	/**
	 * @return the op
	 */
	public String getOp() {
		return op;
	}

	/**
	 * @param op the op to set
	 */
	public void setOp(String op) {
		this.op = op;
	}

}
