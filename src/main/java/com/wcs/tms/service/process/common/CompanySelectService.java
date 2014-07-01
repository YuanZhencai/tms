package com.wcs.tms.service.process.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.MathUtil;
import com.wcs.base.util.StringUtils;
import com.wcs.tms.model.Company;

/** 
* <p>Project: tms</p> 
* <p>Title: CompanySelectService.java</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
public class CompanySelectService implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(unitName = "pu")
	private EntityManager em;
	@Inject
	EntityService entityService;
	@Inject
	LoginService loginService;
	
	private static final Log log = LogFactory.getLog(CompanySelectService.class);

	/**
	 * 集团用户时，公司列表需要懒加载的特殊处理（方法一）
	 * @param companyName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public LazyDataModel<Company> findAllCompany(final String companyName) {
		LazyDataModel<Company> lazyDataModel = new LazyDataModel<Company>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<Company> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
				StringBuilder sb = new StringBuilder("select m from Company m where m.defunctInd = 'N' and m.status = 'Y'");
				if (null != companyName && !"".equals(companyName)) {
					if (StringUtils.hasText(companyName)) {
						sb.append(" and m.companyName like '%" + companyName + "%'");
					}
				}
				String jpql = sb.toString();
				log.info("****************************" + jpql);
				Long count = (Long) em.createQuery(jpql.replaceFirst("m", "COUNT(m)")).getSingleResult();
				log.info("Long_count:" + count);
				System.out.print("first:" + first + ",pageSize:" + pageSize + ",sortField:" + sortField + ",sortOrder:" + sortOrder);
				this.setRowCount((int) count.longValue());
				if (count < pageSize) {
					first = 0;
				}
				return em.createQuery(jpql).setFirstResult(first).setMaxResults(pageSize).getResultList();
			}

			@Override
			public void setRowIndex(int rowIndex) {

				if (rowIndex == -1 || getPageSize() == 0) {
					super.setRowIndex(-1);
				} else{
					super.setRowIndex(rowIndex % getPageSize());
				}
			}
		};
		return lazyDataModel;
	}

	/**
	 * 方法二：为防止数据量过大时在WAS上报错，将LazyModel放在Bean中，Servic中返回List和Count
	 * @param first
	 * @param pageSize
	 * @param sortField
	 * @param sortOrder
	 * @param companyName
	 * @return List和Count
	 */
	public HashMap<String, Object> findBySqLoad(int first, int pageSize, String sortField, SortOrder sortOrder, String companyName, String sapCode,
			String companyEnName) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			int count = (int) findResultCountByObj(companyName, sapCode, companyEnName);
			StringBuilder jpql = new StringBuilder("select m from Company m where m.defunctInd = 'N' and m.status = 'Y' ");
			if (!MathUtil.isEmptyOrNull(sapCode)) {
				jpql.append(" and upper(m.sapCode) like '%" + sapCode.toUpperCase() + "%'");
			}
			if (!MathUtil.isEmptyOrNull(companyName)) {
				jpql.append(" and upper(m.companyName) like '%" + companyName.toUpperCase() + "%'");
			}
			if (!MathUtil.isEmptyOrNull(companyEnName)) {
				jpql.append(" and upper(m.companyEn) like '%" + companyEnName.toUpperCase() + "%'");
			}
			String sb = jpql.toString();
			log.info("****************************" + sb);
			// 得到查询结果
			Query query = em.createQuery(sb);
			// 设置分页参数
			if (first == count && count != 0) {
				query.setFirstResult(first - pageSize);
			} else {
				query.setFirstResult(first);
			}
			query.setMaxResults(pageSize);
			@SuppressWarnings("unchecked")
			List<Company> companyList = (List<Company>) query.getResultList();
			if (!companyList.isEmpty()) {
				map.put("count", count);
				map.put("list", companyList);
			}
		} catch (Exception e1) {
			log.error("findBySqLoad方法 将LazyModel放在Bean中，Servic中返回List和Count 出现异常：",e1);
		}
		return map;
	}

	/**
	 * 获取记录总数
	 * @param companyName
	 * @return
	 */
	public long findResultCountByObj(String companyName, String sapCode, String companyEnName) {
		Long count = 0L;
		try {
			log.info("获取记录总数的公司名称：" + companyName);
			StringBuilder jpql = new StringBuilder("select count(m) from Company m where m.defunctInd = 'N' and m.status = 'Y' ");
			if (!MathUtil.isEmptyOrNull(sapCode)) {
				jpql.append(" and upper(m.sapCode) like '%" + sapCode.toUpperCase() + "%'");
			}
			if (!MathUtil.isEmptyOrNull(companyName)) {
				jpql.append(" and upper(m.companyName) like '%" + companyName.toUpperCase() + "%'");
			}
			if (!MathUtil.isEmptyOrNull(companyEnName)) {
				jpql.append(" and upper(m.companyEn) like '%" + companyEnName.toUpperCase() + "%'");
			}
			String sb = jpql.toString();
			log.info("jpql sb:" + sb);
			count = (Long) em.createQuery(sb).getSingleResult();

		} catch (Exception e) {
			log.error("findResultCountByObj方法 获取记录总数 出现异常：",e);
		}
		log.info("count:" + count);
		return count;
	}
}
