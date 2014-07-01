package com.wcs.tms.service.process.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import com.wcs.base.service.EntityService;
import com.wcs.base.util.MathUtil;
import com.wcs.tms.model.Bank;
import com.wcs.tms.view.process.common.entity.AccountBankSelectVo;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:账户银行选择service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
@Stateless
public class AccountBankSelectService implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Inject
	EntityService entityService;
	
	
	/**
	 * 根据条件过滤账户公司
	 * @param topBankId
	 * @param bankCode
	 * @param bsbCode
	 * @param unionBankNo
	 * @param bankName
	 * @return
	 */
//	public LazyDataModel<AccountBankSelectVo> filterAccountBank(final Long topBankId,
//			final String bankCode, final String bsbCode, final String unionBankNo, final String bankName) {
//		LazyDataModel<AccountBankSelectVo> lazyDataModel = new LazyDataModel<AccountBankSelectVo>() {
//			
//			@Override
//			public List<AccountBankSelectVo> load(int first, int pageSize,
//					String sortField, SortOrder sortOrder, Map<String, String> filters) {
//				pageSize = 10;
//				StringBuilder jpql = new StringBuilder("select b from Bank b where b.defunctInd = 'N' and b.accountBankFlag = 'Y'") ;
//				if(null != topBankId && 0 != topBankId){
//					jpql.append(" and b.topBankId =" +topBankId ) ;
//				}
//				if(!MathUtil.isEmptyOrNull(bankCode)){
//					jpql.append(" and b.bankCode like '%" + bankCode+ "%'") ;
//				}
//				if(!MathUtil.isEmptyOrNull(bsbCode)){
//					jpql.append(" and b.bsbCode like '%" + bsbCode+ "%'") ;
//				}
//				if(!MathUtil.isEmptyOrNull(unionBankNo)){
//					jpql.append(" and b.unionBankNo like '%" + unionBankNo+ "%'") ;
//				}
//				if(!MathUtil.isEmptyOrNull(bankName)){
//					jpql.append(" and b.bankName like '%" + bankName+ "%'") ;
//				}
//				
//				List<Bank> banks = entityService.find(jpql.toString()); 
//				this.setRowCount(banks.size());
//				if(banks.size() < pageSize){
//					first =  0 ;
//				}
//				List<AccountBankSelectVo> vos = setToAccountBankVos(banks);
//				return vos ;
//			}
//
//		};
//			
//		return lazyDataModel ;
//	}

	public  List<AccountBankSelectVo> filterAccountBank(Long topBankId,
			String bankCode, String bsbCode, String unionBankNo, String bankName) {
		
		StringBuilder jpql = new StringBuilder("select b from Bank b where b.defunctInd = 'N' and b.accountBankFlag like 'A%'") ;
				if(null != topBankId && 0 != topBankId){
					jpql.append(" and b.topBankId =" +topBankId ) ;
				}
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
				
				List<Bank> banks = entityService.find(jpql.toString()); 
				return setToAccountBankVos(banks) ;

	}
	
	/**
	 * 设置账户银行Vo
	 * @param banks
	 * @return
	 */
	protected List<AccountBankSelectVo> setToAccountBankVos(List<Bank> banks) {
		
		List<AccountBankSelectVo> vos = new ArrayList<AccountBankSelectVo>();
		Long index = 0L;
		for(Bank b : banks){
			AccountBankSelectVo vo = new AccountBankSelectVo();
			//如果总行则其总行为本身
			if(null == b.getTopBankId() || 0 == b.getTopBankId()){
				vo.setTopBank(b);	
			}else{
				Bank topBank = entityService.find(Bank.class, b.getTopBankId());
				vo.setTopBank(topBank);
			}
			vo.setId(index++);
			vo.setBankCode(b.getBankCode());
			vo.setBsbCode(b.getBsbCode());
			vo.setUnionBankNo(b.getUnionBankNo());
			vo.setBankName(b.getBankName());
			vos.add(vo);
		}
		
		return vos;
	}
	
	
}
