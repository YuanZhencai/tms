package com.wcs.tms.service.process.debtpayment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;

import com.wcs.base.service.EntityService;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.DebtQuota;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.util.MessageUtils;

public class RegiDebtManageService {
	@Inject
	EntityService entityService;
	  
	public EntityService getEntityService() {
		return entityService;
	}

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	/**公司外债及注册资本金一览：
	 * */
	public void debt2RegiCaptial(Company company){
    	if(company==null || company.getId()==null){
    		MessageUtils.addErrorMessage("doneMsg", "请先确定申请公司！");
    		return;
    	}
    	Long compId = company.getId();
    	
    	StringBuilder jpql = new StringBuilder("select c from Company c left outer join fetch c.shareHolders where c.defunctInd = 'N' and c.status = 'Y'");
    	jpql.append(" and c.id="+compId);
    	Company company1 = entityService.findUnique(jpql.toString());
    	try{
    		PropertyUtils.copyProperties(company, company1);
    	}catch(Exception e){
    	}
    		//得到股东信息
    		List<ShareHolder> shList = company.getShareHolders();
    		List<ShareHolder> newShList = new ArrayList<ShareHolder>();
    		for(ShareHolder sh : shList){
    			if("N".equals(sh.getDefunctInd())){
    				newShList.add(sh);
    			}
    		}
    		company.setShareHolders(newShList);
    		
    		//注册资本
    		Double fondsSum = 0d;
    		//已到位注册资本
    		Double fondsInPlaceSum = 0d;
    		for(ShareHolder sh : shList){
    			if("Y".equals(sh.getDefunctInd())){
    				continue;
    			}
    			fondsSum = fondsSum + (sh.getFondsTotal()==null ? 0d : sh.getFondsTotal());
    			fondsInPlaceSum = fondsInPlaceSum + (sh.getFondsInPlace()==null ? 0d : sh.getFondsInPlace());
    		}
    		company.setFondsSum(fondsSum);
    		company.setFondsInPlaceSum(fondsInPlaceSum);
    		//可用投注差=投资总额 - 已用投注差 
    		Double usdInvest=company.getUsedInvestRegRema()==null?0:company.getUsedInvestRegRema();
    		company.setCanUseInvestRegRema(company.getInvestTotal()-usdInvest);
    	}
	
	//公司外债额度详情
	public DebtQuota mkDebtQuota(Long comId){
		DebtQuota debtQuota=new DebtQuota();
		//短期外债：期限类型为短期 且 是否已被展期=否
		StringBuilder jpql = new StringBuilder("select sum(c.debtContractFunds) from DebtContract c  where c.defunctInd = 'N' and c.debtDeadlineType = '1' ");
		jpql.append("and c.isByExtend='0' and c.company.id="+comId);
		Object o=entityService.findUnique(jpql.toString());
		debtQuota.setShortDebt((Double)o);
		
		//中长期外债：期限类型为中长期 或 （期限类型为短期 且 是否展期=是)
		StringBuilder s2=new StringBuilder("select sum(c.debtContractFunds) from DebtContract c  where c.defunctInd = 'N' and "
				+ "(c.debtDeadlineType = '2' or (c.debtDeadlineType = '1' and c.isExtend='1')) ");
		s2.append("and c.company.id="+comId);
		Object o2=entityService.findUnique(s2.toString());
		debtQuota.setMidLongDebt((Double)o2);
		
		//展期外债：是否展期=是
		StringBuilder s3=new StringBuilder("select sum(c.debtContractFunds) from DebtContract c  where c.defunctInd = 'N' and "
				+ "c.isExtend='1' ");
		s3.append("and c.company.id="+comId);
		Object o3=entityService.findUnique(s3.toString());
		debtQuota.setExtensionDebt((Double)o3);
		
		//到期中长期：期限类型为中长期 且 是否到期=到期
		StringBuilder s4=new StringBuilder("select sum(c.debtContractFunds) from DebtContract c  where c.defunctInd = 'N' and "
				+ "c.debtDeadlineType='2' and c.isExpired='1' ");
		s4.append("and c.company.id="+comId);
		Object o4=entityService.findUnique(s4.toString());
		debtQuota.setTermMidLongDebt((Double)o4);
		
		//到期中长期：期限类型为中长期 且 是否到期=到期
		StringBuilder s5=new StringBuilder("select sum(c.appliedFunds),sum(c.debtContractFunds-c.appliedFunds) from DebtContract c  where c.defunctInd = 'N' ");
		s5.append("and c.company.id="+comId);
		Object[] o5=entityService.findUnique(s5.toString());
		debtQuota.setPaymentAmount((Double)o5[0]);
		debtQuota.setUnAccountAmount((Double)o5[1]);
		return debtQuota;
	}
}
