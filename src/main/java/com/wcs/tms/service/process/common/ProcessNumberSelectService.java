package com.wcs.tms.service.process.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.MathUtil;
import com.wcs.tms.model.ProcessMap;
/**
 * 
 * <p>Project: tms</p>
 * <p>Description:账户银行选择service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */

@Stateless
public class ProcessNumberSelectService implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Inject
	EntityService entityService;
	private String[] cashPoolProcessEntities = {"ProcDailyPayLoanTran","ProcPayProject",
			"ProcNoHoldCompLoanTran","ProcElseSpeLoan","ProcInveFinaBail","ProcPurchaseFundProd","ProcPurchaseFundTrade"};
	
	/**
	 * 根据过滤条件查询流程实例编号
	 * @param tmsNumber
	 * @return
	 */
	public List<ProcessMap> filterProcessMap(String tmsNumber) {
		StringBuilder jpql = new StringBuilder("select p from ProcessMap p where p.defunctInd = 'N'") ;
		jpql.append(" and p.pidFn in (?1)");
		if(!MathUtil.isEmptyOrNull(tmsNumber)){
			jpql.append(" and lower(p.pidTms) like '%" + tmsNumber.toLowerCase()+ "%'") ;
		}
		return entityService.find(jpql.toString(), this.findAllCashPoolProcessNums()) ;
	}

	/**
	 * 查询所有现金池流程实例编号
	 * @return
	 */
	public List<String> findAllCashPoolProcessNums() {
		List<String> nums = new ArrayList<String>();
		for(String entity : cashPoolProcessEntities){
			StringBuilder jpql = new StringBuilder("select e.procInstId from "+entity+" e  where e.defunctInd = 'N' ");
			List<String> procNums = entityService.find(jpql.toString());
			nums.addAll(procNums);
		}
		return nums;
	}

	public String[] getCashPoolProcessEntities() {
		return cashPoolProcessEntities;
	}

	public void setCashPoolProcessEntities(String[] cashPoolProcessEntities) {
		this.cashPoolProcessEntities = cashPoolProcessEntities;
	}

	/**
	 * 分页查询 PROCESS_MAP
	 * @param sql
	 * @param first
	 * @param pageSize
	 * @return
	 */
	public List<Object[]> getPagedRowsBySql(String sql, int first,
			int pageSize) {
		Query query = entityService.findNativeQuery(sql);
		query.setFirstResult(first);
		query.setMaxResults(pageSize);
		return query.getResultList();
	}
	
	/**
	 * 将原生sql数据 组装成对象
	 * @param processMapRows
	 * @return
	 */
	public List<ProcessMap> transferDataToProcessMap(List<Object[]> processMapRows) {
		List<ProcessMap> processMaps = new ArrayList<ProcessMap>();
		for(Object[] row : processMapRows){
			ProcessMap processMap = new ProcessMap();
			processMap.setId((Long)row[0]);
			processMap.setPidFn((String)row[1]);
			processMap.setPidTms((String)row[2]);
			processMap.setCompanyId((Long)row[3]);
			processMap.setCompanyName((String)row[4]);
			processMaps.add(processMap);
		}
		return processMaps;
	}
	
}
