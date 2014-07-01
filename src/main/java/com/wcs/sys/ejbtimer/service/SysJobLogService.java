package com.wcs.sys.ejbtimer.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wcs.base.util.StringUtils;
import com.wcs.sys.ejbtimer.model.SysBusinessLog;
import com.wcs.sys.ejbtimer.model.SysJobLog;
import com.wcs.sys.ejbtimer.vo.SysJobLogVo;

/**
 * <p>Project: wcsoa</p>
 * <p>Description: </p>
 * <p>Copyright © 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
@Stateless
public class SysJobLogService {

	private static final Logger logger = LoggerFactory.getLogger(SysJobLogService.class);

	@PersistenceContext(unitName = "pu")
	EntityManager em;

	/**
	 * 
	 * <p>Description: 分页查询定时任务日志</p>
	 * @param first
	 * @param pageSize
	 * @param sortField
	 * @param sortOrder
	 * @param filters
	 * @return
	 */
	public Map findAll(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
		Map map = new HashMap();

		StringBuilder strCountSql = new StringBuilder("SELECT count(s.id) FROM SysJobLog s WHERE 1=1 ");
		StringBuilder strSql = new StringBuilder("SELECT s FROM SysJobLog s join fetch s.sysJobInfo WHERE 1=1 ");

		StringBuilder whereSql = new StringBuilder();
		if (filters.get("jobSubject") != null) {
			whereSql.append(" AND s.jobSubject LIKE '%" + filters.get("jobSubject") + "%' ");
		}
		if (filters.get("isSuccess") != null) {
			whereSql.append(" AND s.isSuccess =" + filters.get("isSuccess"));
		}

		strCountSql.append(whereSql);
		strSql.append(whereSql);

		logger.debug("query sql:" + strSql.toString());

		// 得到总记录数
		Query countQuery = em.createQuery(strCountSql.toString());
		Object countObj = countQuery.getSingleResult();

		// 得到查询结果
		Query query = em.createQuery(strSql.toString());
		query.setFirstResult(first);
		query.setMaxResults(pageSize);
		List resultList = query.getResultList();
		map.put("list", resultList);
		map.put("count", ((Long) countObj).intValue());
		return map;
	}

	/**
	 * 查询任务日志信息
	 * @param first
	 * @param pageSize
	 * @param sortField
	 * @param sortOrder
	 * @param queryMap
	 * @author:LuJiaWei 2013-8-15 下午3:18:21
	 */
	public Map<String, Object> findSysLog(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> queryMap) {
		Map<String, Object> result = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("select jobLog,info from SysJobLog  jobLog  left join jobLog.sysJobInfo info ").append("where jobLog.id is not null ");
		if (queryMap.size() > 0) {
			if (queryMap.get("name") != null && !StringUtils.isBlankOrNull(queryMap.get("name").toString())) {
				sql.append("and info.jobSubject like :jobSubject ");
				result.put("jobSubject", "%" + queryMap.get("name").toString().trim() + "%");
			}

			if (queryMap.get("isSuccess") != null && !StringUtils.isBlankOrNull(queryMap.get("isSuccess").toString())) {
				String str = queryMap.get("isSuccess").toString();
				if ("Y".equals(str)) {
					sql.append("and jobLog.isSuccess = true ");
				} else if ("N".equals(str)) {
					sql.append("and jobLog.isSuccess = false ");
				}
			}
			
			if(queryMap.get("start") instanceof Date){
				sql.append("and jobLog.startTime >= :start ");
				result.put("start",queryMap.get("start"));
			}
			
			if(queryMap.get("end") instanceof Date){
				Calendar instance = Calendar.getInstance();
				instance.setTime((Date) queryMap.get("end"));
				instance.add(Calendar.DATE, 1);
				sql.append("and jobLog.startTime < :end ");
				result.put("end",instance.getTime());
			}
		}

		Query countQuery = em.createQuery(sql.toString().replaceFirst("jobLog,info", " count(jobLog.id) "));
		if (sortField != null) {
           if(SortOrder.ASCENDING.equals(sortOrder)){
        	   sql.append("order  by ").append(sortField);
           }else{
        	   sql.append("order  by ").append(sortField).append(" desc ");
           }
		} else {
			   sql.append(" order by jobLog.id desc ");
		}
		Query createQuery = em.createQuery(sql.toString());
		Set<Entry<String, Object>> entrySet = result.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			countQuery.setParameter(entry.getKey(), entry.getValue());
			createQuery.setParameter(entry.getKey(), entry.getValue());
		}
		int size = Integer.valueOf(countQuery.getSingleResult().toString());
		List<SysJobLogVo> sysJobLogVos = new ArrayList<SysJobLogVo>();
		if (size > 0) {
			List<Object[]> resultList = createQuery.setFirstResult(first).setMaxResults(pageSize).getResultList();
			for (Object[] objects : resultList) {
				SysJobLogVo sysJobLogVo = new SysJobLogVo(objects[0], objects[1]);
				sysJobLogVos.add(sysJobLogVo);
			}
		}
		result.clear();
		result.put("size", size);
		result.put("result", sysJobLogVos);
		return result;
	}

	/**
	 * 查看任务详细日志
	 * @param selectSysJobLogVo
	 * @author:LuJiaWei 2013-8-15 下午5:35:17
	 */
	public void finLogDetail(SysJobLogVo selectSysJobLogVo) {
		if (selectSysJobLogVo != null) {
			SysJobLog find = em.find(SysJobLog.class, selectSysJobLogVo.getSysJobLog().getId());
			if (find != null){
				selectSysJobLogVo.setDetail(find.getLogDetail());
			}
		}
	}

	/**
	 * 查找业务日志
	 * @param first
	 * @param pageSize
	 * @param sortField
	 * @param sortOrder
	 * @param filters
	 * @author:LuJiaWei 2013-8-15 下午6:13:42
	 */
	public Map<String, Object> findSysBusinessLog(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
		StringBuffer sql = new StringBuffer();
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		sql.append("select bg.ID,bg.BUSI_LOG_DESC,bg.BUSI_LOG_INFO,bg.BUSI_LOG_LEVEL,bg.CREATED_DATETIME from SYS_BUSINESS_LOG bg ");
		int i = 0;
		if (filters.size() > 0) {
			if (!StringUtils.isBlankOrNull(filters.get("id"))) {
				sql.append("where bg.LOG_ID = " + filters.get("id"));
			}

			if (!StringUtils.isBlankOrNull(filters.get("BUSI_LOG_DESC"))) {
				i++;
				sql.append(" and bg.BUSI_LOG_DESC like ?" + i);
				hashMap.put(String.valueOf(i), "%" + filters.get("BUSI_LOG_DESC") + "%");
			}

			if (!StringUtils.isBlankOrNull(filters.get("BUSI_LOG_INFO"))) {
				i++;
				sql.append(" and bg.BUSI_LOG_INFO like ?" + i);
				hashMap.put(String.valueOf(i), "%" + filters.get("BUSI_LOG_INFO") + "%");
			}

			if (!StringUtils.isBlankOrNull(filters.get("BUSI_LOG_LEVEL"))) {
				i++;
				sql.append(" and bg.BUSI_LOG_LEVEL like ?" + i);
				hashMap.put(String.valueOf(i), "%" + filters.get("BUSI_LOG_LEVEL") + "%");
			}

			if (!StringUtils.isBlankOrNull(filters.get("CREATED_DATETIME"))) {
				i++;
				sql.append(" and bg.CREATED_DATETIME like ?" + i);
				hashMap.put(String.valueOf(i), "%" + filters.get("CREATED_DATETIME") + "%");
			}
		}
		Query countQuery = em.createNativeQuery(sql.toString().replaceFirst(
				"bg.ID,bg.BUSI_LOG_DESC,bg.BUSI_LOG_INFO,bg.BUSI_LOG_LEVEL,bg.CREATED_DATETIME", "count(bg.ID)"));
		Query createNativeQuery = em.createNativeQuery(sql.toString(), SysBusinessLog.class);
		Set<Entry<String, Object>> entrySet = hashMap.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			countQuery.setParameter(Integer.valueOf(entry.getKey()), entry.getValue());
			createNativeQuery.setParameter(Integer.valueOf(entry.getKey()), entry.getValue());
		}
		List<SysBusinessLog> sysBusinessLogs = new ArrayList<SysBusinessLog>();
		int size = Integer.valueOf(countQuery.getSingleResult().toString());
		if (size > 0) {
			if (size >= first) {
				sysBusinessLogs = createNativeQuery.setFirstResult(first).setMaxResults(pageSize).getResultList();
			} else {
				sysBusinessLogs = createNativeQuery.setFirstResult(0).setMaxResults(pageSize).getResultList();
			}
		}
		hashMap.clear();
		hashMap.put("size", size);
		hashMap.put("result", sysBusinessLogs);
		return hashMap;
	}

}
