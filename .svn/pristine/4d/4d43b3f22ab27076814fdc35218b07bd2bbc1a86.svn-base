package com.wcs.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.MathUtil;
import com.wcs.common.controller.vo.PoVo;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:人员岗位机构查询service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */

@Stateless
public class PoSearchService {
	@EJB
	EntityService entityService;
	
	/**
	 * 获取数据库数据总行数
	 * @param countSql
	 * @return
	 */
	public Integer getRowCountBySql(String countSql) {
		Query query = entityService.findNativeQuery(countSql);
		return (Integer)query.getSingleResult();
	}
	
	/**
	 * 分页查询视图数据
	 * @param sql
	 * @param first
	 * @param pageSize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getPagedRowsBySql(String sql, int first, int pageSize) {
		Query query = entityService.findNativeQuery(sql);
		query.setFirstResult(first);
		query.setMaxResults(pageSize);
		return query.getResultList();
	}
	
	

}
