/** * SysLogBean.java 
* Created on 2013-8-15 下午2:46:50 
*/

package com.wcs.sys.ejbtimer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.wcs.sys.ejbtimer.model.SysBusinessLog;
import com.wcs.sys.ejbtimer.service.SysJobLogService;
import com.wcs.sys.ejbtimer.vo.SysJobLogVo;

/** 
 * <p>Project: wcsoa</p> 
 * <p>Title: SysLogBean.java</p> 
 * <p>Description: </p> 
 * <p>Copyright (c) 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:lujiawei@wcs-global.com">Lu jiawei</a>
 */
@ManagedBean(name="sysJobLogBean")
@ViewScoped
public class SysJobLogBean {
	private LazyDataModel<SysJobLogVo> sysJobLogVos;
	private SysJobLogVo selectSysJobLogVo;
	private LazyDataModel<SysBusinessLog> sysBusinessLogs;
	private Map<String,Object> queryMap;
	@EJB
	private SysJobLogService sysJobLogService;
	
	
	public SysJobLogBean(){
		
	}
	
	/**
	 * 初始化方法 
	 * @author:LuJiaWei 2013-8-15 下午2:54:35
	 */
	@PostConstruct
	public void init(){
		queryMap=new HashMap<String, Object>();
		this.searchSysLog();
	}
	
	/**
	 * 查询内务日志
	 * @author:LuJiaWei 2013-8-15 下午2:59:07
	 */
	private void searchSysLog(){
		if(sysJobLogVos==null){
			sysJobLogVos = new LazyDataModel<SysJobLogVo>() {
				@Override
				public List<SysJobLogVo> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
					Map<String, Object> findSysLog = sysJobLogService.findSysLog(first,pageSize,sortField,sortOrder,queryMap);
					this.setRowCount((Integer) findSysLog.get("size"));
					List<SysJobLogVo> logs= (List<SysJobLogVo>) findSysLog.get("result");
					return logs;
				}
				
			};
		}
	}
	
	/**
	 * 查询业务日志
	 * @author:LuJiaWei 2013-8-15 下午3:02:25
	 */
	public void searchBussessLog(){
		if(sysBusinessLogs==null){
			sysBusinessLogs=new LazyDataModel<SysBusinessLog>() {
				@Override
				public List<SysBusinessLog> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
					filters.put("id", selectSysJobLogVo.getSysJobLog().getId().toString());
					Map<String, Object> findSysBusinessLog = sysJobLogService.findSysBusinessLog(first,pageSize,sortField,sortOrder,filters);
					this.setRowCount((Integer) findSysBusinessLog.get("size"));
					List<SysBusinessLog> logs= (List<SysBusinessLog>) findSysBusinessLog.get("result");
					if (this.getRowCount() < first) {
						setFirstPage(":showDataTableForm:detailDataTableId", 0,false);
					}
					return logs;
				}				
			};
		}
	}
	
	/**
	 * 设置table属性
	 * @param id
	 * @param setFirst
	 * @author:LuJiaWei 2013-8-15 下午3:02:52
	 * @param clear 
	 */
	public void setFirstPage(String id, int setFirst, boolean clear) {
		UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
		UIComponent findComponent = viewRoot.findComponent(id);
		if (findComponent != null && findComponent instanceof DataTable) {
			DataTable dataTable = (DataTable) findComponent;
			dataTable.setFirst(setFirst);
			if(clear){
				dataTable.setFilters(new HashMap<String, String>());
			}
		}
	}
	
	/**
	 * 查询重置方法
	 * @author:LuJiaWei 2013-8-15 下午3:04:00
	 */
	public void reset(){
		queryMap.clear();
	}
	
	/**
	 * 查找任务日志详情
	 * @author:LuJiaWei 2013-8-15 下午6:08:32
	 */
	public void findLogDetail(){
		if(selectSysJobLogVo!=null){
			sysJobLogService.finLogDetail(selectSysJobLogVo);
		}
	}
	
	public void findBlog(){
		if(selectSysJobLogVo!=null){
			this.searchBussessLog();
			this.setFirstPage(":showDataTableForm:detailDataTableId", 0,true);
		}
	}

	public LazyDataModel<SysJobLogVo> getSysJobLogVos() {
		return sysJobLogVos;
	}

	public void setSysJobLogVos(LazyDataModel<SysJobLogVo> sysJobLogVos) {
		this.sysJobLogVos = sysJobLogVos;
	}

	public SysJobLogVo getSelectSysJobLogVo() {
		return selectSysJobLogVo;
	}

	public void setSelectSysJobLogVo(SysJobLogVo selectSysJobLogVo) {
		this.selectSysJobLogVo = selectSysJobLogVo;
	}

	public LazyDataModel<SysBusinessLog> getSysBusinessLogs() {
		return sysBusinessLogs;
	}

	public void setSysBusinessLogs(LazyDataModel<SysBusinessLog> sysBusinessLogs) {
		this.sysBusinessLogs = sysBusinessLogs;
	}

	public Map<String, Object> getQueryMap() {
		return queryMap;
	}

	public void setQueryMap(Map<String, Object> queryMap) {
		this.queryMap = queryMap;
	}	
}
