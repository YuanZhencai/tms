package com.wcs.tms.service.process.debtpayment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.SortOrder;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.StringUtils;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.DebtQuota;
import com.wcs.tms.model.ProcRegiCapital;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.report.regicapitalgeneral.RegicapitalGeneralRequestService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.entity.RegicapitalRequestVO;

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
	
	/**
	 * 外债申请
	 * @param filter
	 * @return
	 */
	public HashMap<String, Object> findDebtRequests(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filter) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
		    Query query;
			
			StringBuilder jpql = new StringBuilder("select pdb from ProcDebtBorrow pdb join fetch pdb.company join fetch pdb.shareHolder where pdb.company.defunctInd = 'N' and pdb.company.status = 'Y' and pdb.shareHolder.defunctInd = 'N' ");
			
			//申请日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (null != filter.get("applyDateS")) {
                String applyDate = sdf.format(filter.get("applyDateS"));
                jpql.append(" and pdb.createdDatetime >= '" + applyDate + "' ");
            }
            if (null != filter.get("applyDateE")) {
                String applyDate = sdf.format(filter.get("applyDateE"));
                jpql.append(" and pdb.createdDatetime <= '" + applyDate + "' ");
            }
            
            //流程编号
            List<String> procInstIds = new ArrayList<String>();
            if (null != filter.get("processNo") && !"".equals(filter.get("processNo"))) {
                procInstIds = processUtilMapService.getFnIdsByTmsId(filter.get("processNo").toString());
                if(procInstIds != null) {
                    jpql.append(" and pdb.procInstId in (:procInstIds)");
                }
            }
            
			//流程状态
            if (null != filter.get("processStatus") && !"".equals(filter.get("processStatus"))) {
                jpql.append(" and pdb.processStatus = '" + filter.get("processStatus") + "' ");
            }
            
            //股东名称
            if (null != filter.get("shareHolderName") && !"".equals(filter.get("shareHolderName"))) {
                jpql.append(" and pdb.shareHolderName = '" + filter.get("shareHolderName") + "' ");
            }
			
			//公司
            String companyId = (String) filter.get("companyId");
			if (!StringUtils.isBlankOrNull(companyId)) { 
			    jpql.append(" and pdb.company.id = " + companyId + " order by pdb.id");
			    query = em.createQuery(jpql.toString());
			}else {
			    List<String> list = (List<String>) filter.get("companyIds");
			    jpql.append(" and pdb.company.id in(:companyIds) order by pdb.id");
                query = em.createQuery(jpql.toString()).setParameter("companyIds", list);
			}
			if(!procInstIds.isEmpty()) {
                query.setParameter("procInstIds", procInstIds);
            }
			System.out.println("jpql : " + jpql);
			/*int count = 1;
			if (first == count && count != 0) {
				query.setFirstResult(first - pageSize);
			} else {
				query.setFirstResult(first);
			}
			query.setMaxResults(pageSize);*/
			int count = query.getResultList().size();
            query.setFirstResult(first);
            query.setMaxResults(pageSize);
			List<ProcRegiCapital> procRegiCapitals = (List<ProcRegiCapital>) query.getResultList();
			List<RegicapitalRequestVO> regicapitalRequestVOs = new ArrayList<RegicapitalRequestVO>();
			if (!procRegiCapitals.isEmpty()) {
				for (ProcRegiCapital pdb : procRegiCapitals) {
//				    RegicapitalRequestVO rcVO = this.getRRVO(pdb);
//				    regicapitalRequestVOs.add(rcVO);
				}
				map.put("count", count);
				map.put("list", regicapitalRequestVOs);
			}
		} catch (Exception e) {
			log.error("findShareHolderBySqLoad方法 错误信息" + e.getMessage());
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 外债请款
	 * @param filter
	 * @return
	 */
	public HashMap<String, Object> findDebtRequestCashs(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filter) {
		return null;
	}
	
}
