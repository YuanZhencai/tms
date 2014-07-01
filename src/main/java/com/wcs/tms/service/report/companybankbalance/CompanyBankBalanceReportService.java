package com.wcs.tms.service.report.companybankbalance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import com.wcs.base.service.EntityService;
import com.wcs.tms.view.process.common.entity.CompanyAcctBalanceVo;
import com.wcs.tms.view.process.common.entity.CompanyBankBalanceVo;

@Stateless
public class CompanyBankBalanceReportService implements Serializable{

	private static final long serialVersionUID = 1L;
	@Inject
	EntityService entityService;
	
	
	public List<CompanyBankBalanceVo> getBalanceVosBySql(String sql,
			int first, int pageSize) {
		Query query = entityService.findNativeQuery(sql);
		query.setFirstResult(first);
		query.setMaxResults(pageSize);
		@SuppressWarnings("unchecked")
		List<Object[]> rows = query.getResultList();
		List<CompanyBankBalanceVo> vos = new ArrayList<CompanyBankBalanceVo>();
		for (Object[] obj : rows) {
			CompanyBankBalanceVo vo = new CompanyBankBalanceVo();
			vo.setCompanyName((String)obj[0]);
			vo.setICBCBalance(obj[1].toString());
			vo.setABCBalance(obj[2].toString());
			vo.setCCBBalance(obj[3].toString());
			vo.setBOCBalance(obj[4].toString());
			vo.setBCMBalance(obj[5].toString());
			vo.setAmountSum(obj[6].toString());
			vos.add(vo);
		}
		return vos;
	}

	
}
