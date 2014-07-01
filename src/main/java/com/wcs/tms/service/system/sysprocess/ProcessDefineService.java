package com.wcs.tms.service.system.sysprocess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import com.wcs.base.service.EntityService;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.ProcessDefine;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:流程定义Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Stateless
public class ProcessDefineService implements Serializable{
	
	private static final long serialVersionUID = 1L;

    @Inject
    EntityService entityService;
    
    
    
    public List<ProcessDefine> findProcessDf(Map<String ,Object> conditionMap){
    	StringBuilder jpql = new StringBuilder("select pd from ProcessDefine pd where pd.defunctInd = 'N' ");
    	if(null!=conditionMap.get("processCode")){
    		String tmp = conditionMap.get("processCode").toString().toLowerCase();
    		jpql.append(" and lower(pd.processCode) like '%"+tmp+"%'");
    	}
    	if(null!=conditionMap.get("processName")){
    		String tmp = conditionMap.get("processName").toString().toLowerCase();
    		jpql.append(" and lower(pd.processName) like '%"+tmp+"%'");
    	}
    	if(null!=conditionMap.get("processMemo") && !"".equals(conditionMap.get("processMemo"))){
    		String tmp = conditionMap.get("processMemo").toString().toLowerCase();
    		jpql.append(" and lower(pd.processMemo) like '%"+tmp+"%'");
    	}
    	return entityService.find(jpql.toString());
    }
    
    /**
     * 检查流程编号唯一性
     * @param code
     * @return
     */
    public boolean checkCode(String code){
    	List<Bank> bs = new ArrayList<Bank>();
    	StringBuilder jpql = new StringBuilder("select pd from ProcessDefine pd where pd.defunctInd = 'N' ");
    	jpql.append(" and pd.processCode = '"+code+"'");
    	bs = entityService.find(jpql.toString());
    	if(bs.size()>0){
    		return false;
    	}else{
    		return true;
    	}
    }
    
    public void saveOrUpdate(ProcessDefine pd){
    	Long id = pd.getId();
    	if(id==null){
    		pd.setStatus("Y");
    		entityService.create(pd);
    	}else{
    		entityService.update(pd);
    	}
    }
    
    
    /**
	 * Code得到流程定义对象
	 * @param workClassName
	 * @return
	 */
	public ProcessDefine getProcDefineByCode(String processCode){
		StringBuilder jpql = new StringBuilder("select pd from ProcessDefine pd where pd.defunctInd = 'N' ");
		jpql.append(" and pd.processCode = '" + processCode + "'");
		List<ProcessDefine> pds = entityService.find(jpql.toString());
		if(pds.size()>0){
			return pds.get(0);
		}else{
			return null;
		}
	}
	public ProcessDefine getProcDefineById(Long processId){
		StringBuilder jpql = new StringBuilder("select pd from ProcessDefine pd where pd.defunctInd = 'N' ");
		jpql.append(" and pd.id = " + processId);
		List<ProcessDefine> pds = entityService.find(jpql.toString());
		if(pds.size()>0){
			return pds.get(0);
		}else{
			return null;
		}
	}
    
    /**
     * 初始化流程名称下拉菜单
     * @return
     */
    public List<SelectItem> getProcessNameSelect(){
    	StringBuilder jpql = new StringBuilder("select pd from ProcessDefine pd where pd.defunctInd = 'N' ");
    	jpql.append(" and pd.status = 'Y'");
    	List<ProcessDefine> pds = entityService.find(jpql.toString());
    	List<SelectItem> selects = new ArrayList<SelectItem>();
    	for(ProcessDefine pd : pds){
    		SelectItem si = new SelectItem(pd.getId(), pd.getProcessName());
    		selects.add(si);
    	}
    	return selects;
    }
}
