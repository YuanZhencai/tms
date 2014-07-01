package com.wcs.tms.service.process.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.common.model.S;
import com.wcs.common.model.Usermstr;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.model.ProcessAuth;

/**
 * <p>Project: tms</p>
 * <p>Description: 发起新流程Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Stateless
public class ProcessNewService implements Serializable{
	
	private static final long serialVersionUID = 1L;

    @Inject
    EntityService entityService;
    @Inject
    LoginService loginService;
    @Inject
    PEManager peManager;
    
    /**
     * 根据登录用户得到授权发起的流程
     * @param user
     * @return
     */
    public List<ProcessAuth> findProcessAuthForLazy (Usermstr user){
    	
    	// 根据用户得到组织、岗位、角色
//    	String positionorgId = "756";
//    	String oid = "1";
//    	Long rolemstrId = 0l;
    	
    	
    	List<ProcessAuth> pas = new ArrayList<ProcessAuth>();
    	//得到岗位列表
    	List<S> ss = loginService.finUserPositon(user.getAdAccount());
    	if(ss.size()==0){
    		return pas;
    	}
    	List<String> sids = new ArrayList<String>();
    	for(S s: ss){
    		sids.add(s.getId());
    	}
    	
    	//检查申请人工作队列
//    	boolean queuePass = false;
//    	List<String> queues = loginService.getQueueByUser();
//    	for(String queue : queues){
//    		if(queue.contains("TMS_Requester")){
//    			queuePass = true;
//    		}
//    	}
//    	if(!queuePass){
//    		return pas;
//    	}
    	
    	StringBuilder jpql = new StringBuilder("select pa from ProcessAuth pa join fetch pa.processDefine where pa.defunctInd = 'N' and pa.processDefine.status = 'Y'");
    	jpql.append(" and pa.s.id in (?1)");
    	pas = entityService.find(jpql.toString(),sids);
    	
    	//剔除重复的流程定义
    	List<ProcessAuth> distinctPas = new ArrayList<ProcessAuth>();
    	for(ProcessAuth pa : pas){
    		boolean has = false;
    		for(ProcessAuth dpa : distinctPas){
    			if(dpa.getProcessDefine().getId().equals(pa.getProcessDefine().getId())){
    				has = true;
    			}
    		}
    		if(!has){
    			distinctPas.add(pa);
    		}
    	}
    	
//    	if(positionorgId!=null && !"".equals(positionorgId)){
//    		jpql.append(" and pa.s.id = '" + positionorgId+"'");
//    	}
//    	if(oid!=null && !"".equals(oid)){
//    		jpql.append(" and pa.o.id = '" + oid+"'");
//    	}
//    	if(rolemstrId!=null && !rolemstrId.equals(0l)){
//    		jpql.append(" and pa.rolemstr.id = " + rolemstrId);
//    	}
    	
    	for(ProcessAuth pa : distinctPas){
    		Integer version = peManager.vwFindProcessVersion(ProcessXmlUtil.getProcessAttribute("code", pa.getProcessDefine().getProcessCode(), "cePath"));
    		pa.getProcessDefine().setProcessVersion(version);
    	}
    	// add on 2013-3-21 by yanchangjing
    	this.orderByProcessDefineId(distinctPas);
    	return distinctPas;
    }

    /**
     * 按照流程定义主键排序
     * @param distinctPas
     */
	private void orderByProcessDefineId(List<ProcessAuth> distinctPas) {
		Collections.sort(distinctPas, new Comparator<ProcessAuth>() {
            @Override
            public int compare(ProcessAuth o1, ProcessAuth o2) {
                return o1.getProcessDefine().getOrderNumber().compareTo(o2.getProcessDefine().getOrderNumber());
            }
        });
	}
}
