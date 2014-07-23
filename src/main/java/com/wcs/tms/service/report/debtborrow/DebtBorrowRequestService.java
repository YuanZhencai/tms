package com.wcs.tms.service.report.debtborrow;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.StringUtils;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.model.DebtContract;
import com.wcs.tms.model.ProcDebtPayment;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.view.process.common.entity.DebtBorrowRequestVO;

/** 
* <p>Project: tms</p> 
* <p>Title: 外债请款申请明细Service</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:guyanyu@wcs-global.com">Gu yanu</a> 
*/
@Stateless
public class DebtBorrowRequestService implements Serializable {

	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(DebtBorrowRequestService.class);
	@PersistenceContext(unitName = "pu")
	private EntityManager em;
	@Inject
	PEManager peManager;
	@Inject
	EntityService entityService;
	@Inject
	ProcessUtilMapService processUtilMapService;
	
	/**
	 * @param first
	 * @param pageSize
	 * @param sortField
	 * @param sortOrder
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> findDebtBorrowRequestDetail(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> conditionMap) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
		    Query query;
			
			StringBuilder jpql = new StringBuilder("select pdp from ProcDebtPayment pdp join fetch pdp.company join fetch pdp.debtContract join fetch pdp.debtContract.shareHolder where pdp.company.defunctInd = 'N' and pdp.company.status = 'Y' ");
			
			//申请日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (null != conditionMap.get("applyDateS")) {
                String applyDate = sdf.format(conditionMap.get("applyDateS"));
                jpql.append(" and pdp.createdDatetime >= '" + applyDate + "' ");
            }
            if (null != conditionMap.get("applyDateE")) {
                String applyDate = sdf.format(conditionMap.get("applyDateE"));
                jpql.append(" and pdp.createdDatetime <= '" + applyDate + "' ");
            }
            
            //流程编号
            List<String> procInstIds = new ArrayList<String>();
            if (null != conditionMap.get("processNo") && !"".equals(conditionMap.get("processNo"))) {
                procInstIds = processUtilMapService.getFnIdsByTmsId(conditionMap.get("processNo").toString());
                if(procInstIds != null) {
                    jpql.append(" and pdp.procInstId in (:procInstIds)");
                }
            }
            
			//流程状态
            if (null != conditionMap.get("processStatus") && !"".equals(conditionMap.get("processStatus"))) {
                jpql.append(" and pdp.processStatus = '" + conditionMap.get("processStatus") + "' ");
            }
            
            //外债出资方
            if (null != conditionMap.get("fundsProvider") && !"".equals(conditionMap.get("fundsProvider"))) {
                jpql.append(" and pdp.debtContract.shareHolder.shareHolderName like '%" + conditionMap.get("fundsProvider") + "%' ");
            }
            
            //外债合同编号
            if (null != conditionMap.get("debtContractNO") && !"".equals(conditionMap.get("debtContractNO"))) {
                jpql.append(" and pdp.debtContract.debtContractNo like '%" + conditionMap.get("debtContractNO") + "%' ");
            }
            
			//公司
            String companyId = (String) conditionMap.get("companyId");
			if (!StringUtils.isBlankOrNull(companyId)) { 
			    jpql.append(" and pdp.company.id = " + companyId + " order by pdp.id");
			    query = em.createQuery(jpql.toString());
			}else {
			    List<String> list = (List<String>) conditionMap.get("companyIds");
			    jpql.append(" and pdp.company.id in(:companyIds) order by pdp.id");
                query = em.createQuery(jpql.toString()).setParameter("companyIds", list);
			}
			if(!procInstIds.isEmpty()) {
			    query.setParameter("procInstIds", procInstIds);
			}
			System.out.println("jpql : " + jpql);
			int count = query.getResultList().size();
            query.setFirstResult(first);
            query.setMaxResults(pageSize);
			List<ProcDebtPayment> ProcDebtPayments = (List<ProcDebtPayment>) query.getResultList();
			List<DebtBorrowRequestVO> DebtBorrowRequestVOs = new ArrayList<DebtBorrowRequestVO>();
			if (!ProcDebtPayments.isEmpty()) {
				for (ProcDebtPayment pdp : ProcDebtPayments) {
				    DebtBorrowRequestVO rcVO = this.getDBRVO(pdp);
				    DebtBorrowRequestVOs.add(rcVO);
				}
				map.put("count", count);
				map.put("list", DebtBorrowRequestVOs);
			}
		} catch (Exception e) {
			log.error("findDebtBorrowRequestDetail方法 错误信息" + e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * @param pdp
	 * @return
	 */
	public DebtBorrowRequestVO  getDBRVO(ProcDebtPayment pdp) {
	    DebtBorrowRequestVO dbrVO = new DebtBorrowRequestVO();
	    dbrVO.setDebtPayment(pdp);
	    // 申请日期(创建时间)
        dbrVO.setApplyDate(DateUtil.convertDateToString(pdp.getCreatedDatetime(), "yyyy-MM-dd"));
        // 流程编号
        dbrVO.setProcInstId(pdp.getProcInstId());
        dbrVO.setProcessNo(processUtilMapService.getTmsIdByFnId(pdp.getProcInstId()));
        // 公司名称
        dbrVO.setCompanyName(pdp.getCompany().getCompanyName());
        // 流程状态
        dbrVO.setProcessStatus(pdp.getProcessStatus());
	    // 币别
        dbrVO.setCurrency(pdp.getApplyFundsCu());
	    // 申请金额
        dbrVO.setRequestMoney(pdp.getApplyFunds() == null ? "/" : pdp.getApplyFunds().toString());
	    // 是否到账
        dbrVO.setToTheAccount("已到账");
	    /** 外债合同_主数据DEBT_CONTRACT**/
	    // 外债合同 = 合同编号 + 出资方 + 开始-结束日期 + 利率
        DebtContract debtContract = pdp.getDebtContract();
        String debtContractStr = debtContract.getDebtContractNo() + " + " + debtContract.getShareHolder().getShareHolderName() + " + " + DateUtil.convertDateToString(debtContract.getContractStartDate(), "yyyy-MM-dd") + "-" + DateUtil.convertDateToString(debtContract.getContractEndDate(), "yyyy-MM-dd") + " + " + (debtContract.getApprovalRate() == null ? "" : debtContract.getApprovalRate());
        dbrVO.setDebtContract(debtContractStr);
        // 外债合同金额
        dbrVO.setDebtContractFunds(debtContract.getDebtContractFunds() == null ? "/" : debtContract.getDebtContractFunds().toString());
	    // 未请款金额  = 外债合同金额 - 未请款金额
        Double noAppliedFunds = (debtContract.getDebtContractFunds() == null ? 0 : debtContract.getDebtContractFunds()) - (debtContract.getAppliedFunds() == null ? 0 : debtContract.getAppliedFunds());
        dbrVO.setNoAppliedFunds(noAppliedFunds == 0 ? "/" : noAppliedFunds.toString());
	    // 已请款金额   
        dbrVO.setApplyFunds(debtContract.getAppliedFunds() == null ? "/" : debtContract.getAppliedFunds().toString());
	    // 到账金额
        dbrVO.setToTheAccount(debtContract.getReceivedFunds() == null ? "/" : debtContract.getReceivedFunds().toString());
	    
	    return dbrVO;
	}

}
