package com.wcs.tms.service.process.common;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.MathUtil;
import com.wcs.tms.model.Bank;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:账户银行选择service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */

@Stateless
public class BankMultipleSelectService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	EntityService entityService;

	
	/**
	 * 根据过滤条件查询所有总行
	 * @param bankCode
	 * @param bsbCode
	 * @param unionBankNo
	 * @param bankName
	 * @return
	 */
	public List<Bank> filterTopBank(String bankCode, String bsbCode,
			String unionBankNo, String bankName) {
		StringBuilder jpql = new StringBuilder("select b from Bank b where b.defunctInd = 'N' and b.topLevelFlag = 'Y'") ;
		if(!MathUtil.isEmptyOrNull(bankCode)){
			jpql.append(" and lower(b.bankCode) like '%" + bankCode.toLowerCase()+ "%'") ;
		}
		if(!MathUtil.isEmptyOrNull(bsbCode)){
			jpql.append(" and lower(b.bsbCode) like '%" + bsbCode.toLowerCase()+ "%'") ;
		}
		if(!MathUtil.isEmptyOrNull(unionBankNo)){
			jpql.append(" and lower(b.unionBankNo) like '%" + unionBankNo.toLowerCase()+ "%'") ;
		}
		if(!MathUtil.isEmptyOrNull(bankName)){
			jpql.append(" and lower(b.bankName) like '%" + bankName.toLowerCase()+ "%'") ;
		}
		return entityService.find(jpql.toString()) ;
	}
	
}
