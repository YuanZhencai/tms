package com.wcs.tms.service.report.regicapitalgeneral;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.SortOrder;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.StringUtils;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.view.process.common.entity.ShareHolderVo;

/** 
* <p>Project: tms</p> 
* <p>Title: 注册资本情况一览表Service</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
@Stateless
public class RegicapitalGeneralReService implements Serializable {

	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(RegicapitalGeneralReService.class);
	@PersistenceContext(unitName = "pu")
	private EntityManager em;
	@Inject
	PEManager peManager;
	@Inject
	EntityService entityService;
	@Inject
	ProcessUtilMapService processUtilMapService;

	/**
	 * 懒加载方法
	 * @param first
	 * @param pageSize
	 * @param sortField
	 * @param sortOrder
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> findShareHolderBySqLoad(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> conditionMap) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			String companyId = (String) conditionMap.get("companyId");
			if (!StringUtils.isBlankOrNull(companyId)) {
				StringBuilder jpql = new StringBuilder("select c from Company c where c.defunctInd = 'N' and c.status = 'Y' ");
				jpql.append(" and c.id = " + companyId);
				jpql.append(" order by c.id");
				// 得到查询结果
				Query query = em.createQuery(jpql.toString());
				int count = 1;
				if (first == count && count != 0) {
					query.setFirstResult(first - pageSize);
				} else {
					query.setFirstResult(first);
				}
				query.setMaxResults(pageSize);
				List<Company> companyList = (List<Company>) query.getResultList();
				List<ShareHolderVo> shareHolderVoList = new ArrayList<ShareHolderVo>();
				if (!companyList.isEmpty()) {
					for (Company company : companyList) {
						ShareHolderVo shVo = this.getSHVo(company);
						shareHolderVoList.add(shVo);
					}
					map.put("count", count);
					map.put("list", shareHolderVoList);
				}
			} else {
				List<String> list = (List<String>) conditionMap.get("companyIds");
				int count = (int) list.size();
				String jpql = "select c from Company c where c.defunctInd = 'N' and c.status = 'Y' and c.id in(:companyIds) order by c.id";
				// 得到查询结果
				Query query = em.createQuery(jpql).setParameter("companyIds", list);
				if (first == count && count != 0) {
					query.setFirstResult(first - pageSize);
				} else {
					query.setFirstResult(first);
				}
				query.setMaxResults(pageSize);
				List<Company> companyList = (List<Company>) query.getResultList();
				List<ShareHolderVo> shareHolderVoList = new ArrayList<ShareHolderVo>();
				if (!companyList.isEmpty()) {
					for (Company company : companyList) {
						ShareHolderVo shVo = this.getSHVo(company);
						shareHolderVoList.add(shVo);
					}
					map.put("count", count);
					map.put("list", shareHolderVoList);
				}
			}
		} catch (Exception e1) {
			log.error("findShareHolderBySqLoad方法 错误信息" + e1.getMessage());
		}
		return map;
	}

	/**
	 * 组装VO
	 * @param procRegiCapital
	 * @return
	 */
	public ShareHolderVo getSHVo(Company company) {
		ShareHolderVo shVo = new ShareHolderVo();
		// 公司名称
		shVo.setCompanyName(company.getCompanyName());
		// 公司币别
		shVo.setCompanyCu(company.getInvestCurrency());
		// 公司投资总额
		shVo.setCompanyAmount(company.getInvestTotal());
		// 注册资本（计算所得）
		shVo.setRegiCapi(this.getRegiCapi(company));
		shVo.setShareHolder(this.getShareHolder(company));
		return shVo;
	}

	/**
	 * 获得注册资本
	 * @param procRegiCapital
	 * @return
	 */
	public Double getRegiCapi(Company company) {
		Double amount = 0D;
		List<ShareHolder> sh = new ArrayList<ShareHolder>();
		StringBuilder jpql = new StringBuilder("select sh from ShareHolder sh join fetch sh.company where sh.defunctInd='N' ");
		jpql.append(" and sh.company.id = " + company.getId());
		sh = entityService.find(jpql.toString());
		if (sh.size() != 0) {
			for (ShareHolder s : sh) {
				amount += s.getFondsTotal();
			}
		}
		return amount;
	}

	/**
	 * 根据注册资本金流程中的公司ID获得它的股东
	 * @param procRegiCapital
	 * @return
	 */
	public List<ShareHolder> getShareHolder(Company company) {
		List<ShareHolder> sh = new ArrayList<ShareHolder>();
		StringBuilder jpql = new StringBuilder("select sh from ShareHolder sh join fetch sh.company where sh.defunctInd='N' ");
		jpql.append(" and sh.company.id = " + company.getId());
		sh = entityService.find(jpql.toString());
		return sh;
	}

}
