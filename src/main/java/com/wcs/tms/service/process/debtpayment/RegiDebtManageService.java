package com.wcs.tms.service.process.debtpayment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.DebtContract;
import com.wcs.tms.model.DebtQuota;
import com.wcs.tms.model.ProcDebtBorrow;
import com.wcs.tms.model.ProcDebtPayment;
import com.wcs.tms.model.ProcRegiCapital;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.entity.RegiCapitalConfirmVo;
import com.wcs.tms.view.process.common.entity.RegiDebtCashConfirmVo;
import com.wcs.tms.view.process.common.entity.RegiDebtConfirmVo;

public class RegiDebtManageService {
	
	private Log log = LogFactory.getLog(RegiDebtManageService.class);
	
	@PersistenceContext(unitName = "pu")
	private EntityManager em;
	@Inject
	EntityService entityService;
	@Inject
	ProcessUtilMapService processUtilMapService;
	
	  
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
	
	public void confirmRegiCapital(RegiCapitalConfirmVo confirmVo) {
		ProcRegiCapital regiCapital = confirmVo.getRegiCapital();
		
		ShareHolder shareHolder = entityService.find(ShareHolder.class, regiCapital.getPayer());
		// 已到账金额
		Double realReceivedFunds = confirmVo.getAlreadyAccount() + (shareHolder.getRealReceivedFunds() == null ? 0d : shareHolder.getRealReceivedFunds());
		shareHolder.setRealReceivedFunds(realReceivedFunds);
		entityService.update(shareHolder);
		
	}
	
	public void confirmRegiDebt(RegiDebtConfirmVo confirmVo) {
		ProcDebtBorrow debtBorrow = confirmVo.getDebtBorrow();
		DebtContract debtContract = entityService.find(DebtContract.class, debtBorrow.getDebtContractId());
		debtContract.setIsConfirmed("Y");
		// 实际金额
		debtContract.setRealFunds(confirmVo.getContractAccount());
		// 实际金额币别
		debtContract.setRealFundsCu(confirmVo.getCurrency());
		// 借款开始时间
		debtContract.setApprovalStartDate(confirmVo.getBorrowStartDate());
		// 借款结束时间
		debtContract.setApprovalEndDate(confirmVo.getBorrowEndDate());
		// 借款利率
		debtContract.setRealFundsRate(confirmVo.getInterestRate());
		// 合同编号
		debtContract.setDebtContractNo(confirmVo.getContractNo());
		// 登记人员
		debtContract.setContractRegistedBy(confirmVo.getRegistrant());
		// 登记时间
		debtContract.setContractRegistedTime(confirmVo.getRegisterDate());
		entityService.update(debtContract);
		

	}
	
	public void confirmRegiDebtCash(RegiDebtCashConfirmVo confirmVo) {
		ProcDebtPayment debtPayment = confirmVo.getDebtPayment();
		// 是否到账
		debtPayment.setIsReceivedFunds("Y");
		// 已到账金额
		debtPayment.setReceivedFunds(confirmVo.getRequestAccount());
		// 已到账金额币别
		debtPayment.setReceivedFundsCu(confirmVo.getCurrency());
		// 到账时间
		debtPayment.setReceivedFundsTime(confirmVo.getInAccountDate());
		// 登记人员
		debtPayment.setRegistedBy(confirmVo.getRegistrant());
		// 登记时间
		debtPayment.setRegistedTime(confirmVo.getRegisterDate());
		
		entityService.update(debtPayment);
		
	}
	
	
}
