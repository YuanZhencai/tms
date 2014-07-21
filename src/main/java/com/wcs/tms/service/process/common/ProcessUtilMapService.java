package com.wcs.tms.service.process.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcessMap;
import com.wcs.tms.service.process.bankcredit.BankCreditService;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:TMS流程实例编号"映射"公共类service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Stateless
public class ProcessUtilMapService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    @Inject
    EntityService entityService;
    
    private static final Log log = LogFactory.getLog(BankCreditService.class);
	
	/**
     * 生成流程实例编号映射(9.6)
     * @param procInstId FN流程实例编号
     * @param processCodeHead TMS流程实例编号抬头(带入每个流程的编号去掉头部的"TMS_",比如TMS_BPM_RA_006	注册资本金申请审批流程,此传入参数为"BPM_RA_006")
     * @param company 流程实例对应的公司实体(必须包含id、sap代码、公司中文名等信息)
     * @throws Exception 
     */
    public void generateProcessMap(String procInstId ,String processCodeHead,Company company) throws ServiceException{
    	try {
    		//tmsPid生成
    		StringBuilder tmsPid = new StringBuilder("");
    		//流程code+公司sap生成抬头
    		if(company!=null && null!=company.getSapCode()){
    			//tmsPid.append(processCodeHead);
    			//tmsPid.append("_");
    			//9.14 客户要求，流程号只要sap代码+流水号
    		    //2012.01.07 modify by liushengbin 改成只获取sapcode后两位
    		    String sapCode = company.getSapCode();
    		    if(sapCode != null && sapCode.trim().length() >0){
    		        sapCode = sapCode.substring(sapCode.length() - 2);
    		    }
    			tmsPid.append(sapCode);
    		}else{
    			throw new ServiceException(ExceptionMessage.PROCESS_MAP);
    		}
    		
    		//通过seqence得到流程实例编号计数器
    		Long seqNext = 0l;
    		List<Object> seqResult = entityService.createNativeQuery("SELECT NEXTVAL FOR TMS_PID_COUNT FROM SYSIBM.SYSDUMMY1");
    		if(seqResult!=null && seqResult.size()>0){
    			seqNext = new Long(seqResult.get(0).toString());
    		}
    		
    		//拼接计数器
    		if(!seqNext.equals(0l)){
    			tmsPid.append("_");
    			tmsPid.append(seqNext.toString());
    		}else{
    			throw new ServiceException(ExceptionMessage.PROCESS_MAP);
    		}
    		
    		
    		//流程映射实体
    		ProcessMap pm = new ProcessMap();
    		
    		pm.setPidFn(procInstId);
    		pm.setPidTms(tmsPid.toString());
    		pm.setCompanyId(company.getId());
    		pm.setCompanyName(company.getCompanyName());
    		
    		//保存映射实体
    		entityService.create(pm);
			
		} catch (Exception e) {
			log.error("generateProcessMap方法 生成流程实例编号映射 出现异常：",e);
			throw new ServiceException(ExceptionMessage.PROCESS_MAP, e);
		}
    }
    
    
    /**
     * 得到流程实例编号映射对象
     * @param procInstId
     * @return
     */
    public ProcessMap getProcessMapByFnId(String procInstId){
    	ProcessMap pm = new ProcessMap();
    	String jpql = "select pm from ProcessMap pm where pm.pidFn = '"+ procInstId +"'";
    	List<ProcessMap> pms = entityService.find(jpql);
    	if(pms!=null && pms.size()>0){
    		pm = pms.get(0);
    	}
    	return pm;
    }
    
    /**
     * 得到TMS流程实例编号
     * @param procInstId
     * @return
     */
    public String getTmsIdByFnId(String procInstId){
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
     * 得到Fn流程实例编号
     * @param pidTms
     * @return
     */
    public String getFnIdByTmsId(String pidTms){
    	ProcessMap pm = new ProcessMap();
    	String jpql = "select pm from ProcessMap pm where pm.pidTms = '"+ pidTms +"'";
    	List<ProcessMap> pms = entityService.find(jpql);
    	if(pms!=null && pms.size()>0){
    		pm = pms.get(0);
    	}
    	if(pm.getPidFn()==null || "".equals(pm.getPidFn())){
    		return pidTms;
    	}
    	return pm.getPidFn();
    }
    
    public List<String> getFnIdsByTmsId(String pidTms){
        String jpql = "select pm from ProcessMap pm where pm.pidTms like '%"+ pidTms +"%'";
        List<ProcessMap> pms = entityService.find(jpql);
        List<String> fnIds = new ArrayList<String>();
        if(pms!=null && pms.size()>0){
            for(ProcessMap pm : pms) {
                fnIds.add(pm.getPidFn());
            }
        }
        return fnIds;
    }
    
    /**
     * 根据流程编号获取流程实例id
     * @param tmsId
     * @return
     */
    public String getProcInstIdByTmsId(String tmsId) {
        String jpql = "select pm.id from ProcessMap pm where pm.pidTms = '" + tmsId + "'";
        List<Object> find = entityService.find(jpql);
        if(find.get(0) != null) {
            return find.get(0).toString();
        }else {
            return null;
        }
    }

}
