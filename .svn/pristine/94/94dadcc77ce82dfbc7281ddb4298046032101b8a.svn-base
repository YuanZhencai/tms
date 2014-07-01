package com.wcs.common.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.MathUtil;
import com.wcs.common.model.P;
import com.wcs.common.model.PS;
import com.wcs.common.model.S;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:岗位管理service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */

@Stateless
public class StationService {

	@EJB
	EntityService entityService;
	
	/**
	 * 根据组织Id查询所属公司
	 * @param oId
	 * @return
	 */
	public String findParentComNameByOId(String oId) {
		StringBuilder sql = new StringBuilder("select FULL_PATH from VW_ORG where DEFUNCT_IND = 'N' and ORG_ID = '"+oId+"'");
		return (String)entityService.findNativeQuery(sql.toString()).getSingleResult();
	}

	/**
	 * 根据岗位查询已分配人员
	 * @param station
	 * @return
	 */
	public List<P> findDistributedPersons(S station) {
		StringBuilder jpql = new StringBuilder("select p from P p where p.id in(" +
				"select ps.pid from PS ps where ps.defunctInd = 'N' and ps.sid = '"+station.getId()+"')");
		return entityService.find(jpql.toString());
	}

	/**
	 * 根据岗位id和人员id取消该人员分配
	 */
	public void cancelDistributionByPidSid(String pid, String sid) {
		StringBuilder jpql = new StringBuilder("select ps from PS ps where ps.defunctInd = 'N'");
		jpql.append(" and ps.pid = '"+pid+"'");
		jpql.append(" and ps.sid = '"+sid+"'");
		PS ps = entityService.findUnique(jpql.toString());
		ps.setDefunctInd("Y");
		entityService.update(ps);
	}

	public List<PS> findPSByPidAndSid(String pid, String sid) {
		StringBuilder jpql = new StringBuilder("select ps from PS ps where ps.pid = '"+pid+"'");
		jpql.append(" and ps.sid = '"+sid+"'");
		return entityService.find(jpql.toString());
	}

	/**
	 * 查询PS表中id最大值
	 * @return
	 */
	public Integer findMaxPsId() {
		StringBuilder jpql = new StringBuilder("select MAX(CAST(id AS int)) as id from PS");
		return (Integer)entityService.findNativeQuery(jpql.toString()).getSingleResult();
	}

}
