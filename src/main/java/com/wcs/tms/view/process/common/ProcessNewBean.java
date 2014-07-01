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
import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.common.model.Usermstr;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.model.ProcessAuth;
import com.wcs.tms.model.ProcessDefine;
import com.wcs.tms.service.process.common.ProcessNewService;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 发起新流程Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@ManagedBean
@ViewScoped
public class ProcessNewBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(ProcessNewBean.class);
	
	@Inject 
	EntityService entityService;
	@Inject
	ProcessNewService processNewService;
	@Inject
	LoginService loginService;
	
	private List<ProcessAuth> processAuthList = Lists.newArrayList();
	
	private ProcessDefine processDefine = new ProcessDefine();
	
	



	/**
	 * <p>Description:Bean init </p>
	 */
	@PostConstruct
	public void initNew(){
		log.info("initProcessNewBean~~~~~~~~~~~~~~");
		if(processAuthList.size() == 0){
			searchAuth(); /* 默认查询所有授权发起流程 */
		}
		
	}
	
	
	/**
	 * 查询授权列表
	 */
	public void searchAuth(){
		//得到登录用户
		Usermstr user = loginService.getCurrentUsermstr();
		processAuthList = processNewService.findProcessAuthForLazy(user);
	}
	
	
	/**
	 * 到创建新流程页面
	 */
	public String toNew(){
		String processDefineCode = processDefine.getProcessCode();
		//String processPage = ProcessDefineUtil.PROCESS_CODE_PAGE_MAP.get(processDefineCode);
		//流程Code得到流程申请页面
		return getProcessPage(processDefineCode);
	}
	
	/**
	 * 得到流程申请页面
	 * @param processDefineCode
	 * @return
	 */
	private String getProcessPage(String processDefineCode){
		String processPage = "";
		// 银行授信额度调剂申请(特殊处理，判断流程申请人是否为集团人员，跳转不同的申请页面)
		if("TMS_BPM_RA_003".equals(processDefineCode)){
			if(loginService.isCopUser()){
				processPage = ProcessXmlUtil.getProcessAttribute("code", processDefineCode, "appPage1");
				return processPage;
			}
		}
		processPage = ProcessXmlUtil.getProcessAttribute("code", processDefineCode, "appPage");
		return processPage;
	}
	
	/******set@get*******************************************************/
	public List<ProcessAuth> getProcessAuthList() {
		return processAuthList;
	}
	public void setProcessAuthList(List<ProcessAuth> processAuthList) {
		this.processAuthList = processAuthList;
	}
	public ProcessDefine getProcessDefine() {
		return processDefine;
	}
	public void setProcessDefine(ProcessDefine processDefine) {
		this.processDefine = processDefine;
	}

	
}
