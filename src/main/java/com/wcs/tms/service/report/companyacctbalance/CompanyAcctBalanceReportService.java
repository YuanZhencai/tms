package com.wcs.tms.service.report.companyacctbalance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import com.wcs.base.service.EntityService;
import com.wcs.tms.view.process.common.entity.CompanyAcctBalanceVo;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:公司账户余额表Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
@Stateless
public class CompanyAcctBalanceReportService implements Serializable{

	private static final long serialVersionUID = 1L;
	@Inject
	EntityService entityService;

	public Integer getRowCountBySql(String countSql) {
		Query query = entityService.findNativeQuery(countSql);
		return (Integer)query.getSingleResult();
	}

	public List<CompanyAcctBalanceVo> getBalanceVosBySql(String sql,
			int first, int pageSize) {
		Query query = entityService.findNativeQuery(sql);
		query.setFirstResult(first);
		query.setMaxResults(pageSize);
		@SuppressWarnings("unchecked")
		List<Object[]> ret = query.getResultList();
		List<CompanyAcctBalanceVo> vos = new ArrayList<CompanyAcctBalanceVo>();
		for (Object[] obj : ret) {
			CompanyAcctBalanceVo vo = new CompanyAcctBalanceVo();
			vo.setCompanyName((String)obj[0]);
			vo.setBankName((String)obj[1]);
			vo.setAccount((String)obj[2]);
			vo.setAvailableAmount(obj[3].toString());
			vo.setUpdateDate((Date)obj[4]);
			vos.add(vo);
		}
		return vos;
	}

}
