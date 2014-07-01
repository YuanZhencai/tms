package com.wcs.tms.service.process.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;


import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.MathUtil;
import com.wcs.tms.model.Company;
/**
 * 
 * <p>Project: tms</p>
 * <p>Description:账户银行选择service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */

@Stateless
public class CompanyMultipleSelectService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	EntityService entityService;
	@Inject
	LoginService loginService;
	
	/**
	 * 根据判断当前用户是否为集团人来模糊查询公司数据
	 * @param companyName （模糊查询条件）
	 * @param sapCode
	 * @param companyEnName
	 * @return
	 */
	public List<Company> filterCompanyByCurrentUser(String companyName,
			String sapCode, String companyEnName) {
		List<Company> companys = new ArrayList<Company>();
		List<String> saps = loginService.findCompanySapCode();
		StringBuilder jpql = new StringBuilder("select c from Company c where c.defunctInd = 'N' and c.status = 'Y'");
		if(!MathUtil.isEmptyOrNull(companyName)){
			jpql.append(" and lower(c.companyName) like '%"+companyName.toLowerCase()+"%'");
		}
		if(!MathUtil.isEmptyOrNull(sapCode)){
			jpql.append(" and lower(c.sapCode) like '%"+sapCode.toLowerCase()+"%'");
		}
		if(!MathUtil.isEmptyOrNull(companyEnName)){
			jpql.append(" and lower(c.companyEnName) like '%"+companyEnName.toLowerCase()+"%'");
		}
		if(loginService.isCopUser()){
			companys = entityService.find(jpql.toString());
		}else {
			// 如果不是集团的人则只查出所在公司
			if (saps != null && saps.size() != 0 ) {
				jpql.append(" and c.sapCode in (?1)");
				companys = entityService.find(jpql.toString(), saps);
			} 
		}
		return companys;
	}

	
	

}
