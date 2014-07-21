package com.wcs.tms.service.report.regicapitalgeneral;

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
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcRegiCapital;
import com.wcs.tms.model.ProcRegiCapitalChange;
import com.wcs.tms.model.ProcRegiCapitalChangeShareholder;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.view.process.common.entity.RegicapitalMofifyShareholderVO;
import com.wcs.tms.view.process.common.entity.RegicapitalMofifyVO;
import com.wcs.tms.view.process.common.entity.RegicapitalRequestVO;
import com.wcs.tms.view.process.common.entity.ShareHolderVo;

/** 
* <p>Project: tms</p> 
* <p>Title: 注册资本情况一览表Service</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:guyanyu@wcs-global.com">Gu yanu</a> 
*/
@Stateless
public class RegicapitalGeneralRequestService implements Serializable {

	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(RegicapitalGeneralRequestService.class);
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
	public HashMap<String, Object> findRegicapitalRequestDetail(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> conditionMap) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
		    Query query;
			
			StringBuilder jpql = new StringBuilder("select prc from ProcRegiCapital prc join fetch prc.company where prc.company.defunctInd = 'N' and prc.company.status = 'Y' ");
			
			//申请日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (null != conditionMap.get("applyDateS")) {
                String applyDate = sdf.format(conditionMap.get("applyDateS"));
                jpql.append(" and prc.createdDatetime >= '" + applyDate + "' ");
            }
            if (null != conditionMap.get("applyDateE")) {
                String applyDate = sdf.format(conditionMap.get("applyDateE"));
                jpql.append(" and prc.createdDatetime <= '" + applyDate + "' ");
            }
            
            //流程编号
            List<String> procInstIds = new ArrayList<String>();
            if (null != conditionMap.get("processNo") && !"".equals(conditionMap.get("processNo"))) {
                procInstIds = processUtilMapService.getFnIdsByTmsId(conditionMap.get("processNo").toString());
                if(procInstIds != null) {
                    jpql.append(" and prc.procInstId in (:procInstIds)");
                }
            }
            
			//流程状态
            if (null != conditionMap.get("processStatus") && !"".equals(conditionMap.get("processStatus"))) {
                jpql.append(" and prc.processStatus = '" + conditionMap.get("processStatus") + "' ");
            }
            
            //股东名称
            if (null != conditionMap.get("shareHolderName") && !"".equals(conditionMap.get("shareHolderName"))) {
                jpql.append(" and prc.shareHolderName = '" + conditionMap.get("shareHolderName") + "' ");
            }
			
			//公司
            String companyId = (String) conditionMap.get("companyId");
			if (!StringUtils.isBlankOrNull(companyId)) { 
			    jpql.append(" and prc.company.id = " + companyId + " order by prc.id");
			    query = em.createQuery(jpql.toString());
			}else {
			    List<String> list = (List<String>) conditionMap.get("companyIds");
			    jpql.append(" and prc.company.id in(:companyIds) order by prc.id");
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
				for (ProcRegiCapital prc : procRegiCapitals) {
				    RegicapitalRequestVO rcVO = this.getRRVO(prc);
				    regicapitalRequestVOs.add(rcVO);
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

	public RegicapitalRequestVO  getRRVO(ProcRegiCapital prc) {
	    RegicapitalRequestVO rrVO = new RegicapitalRequestVO();
	    // 申请日期(创建时间)
	    rrVO.setApplyDate(DateUtil.convertDateToString(prc.getCreatedDatetime(), "yyyy-MM-dd"));
	    // 流程编号
	    rrVO.setProcessNo(processUtilMapService.getTmsIdByFnId(prc.getProcInstId()));
	    // 公司名称
	    rrVO.setCompanyName(prc.getCompany().getCompanyName());
	    // 流程状态
	    rrVO.setProcessStatus(prc.getProcessStatus());
	    // 股东名称
	    rrVO.setShareHolderName(prc.getShareHolderName());
	    // 币别
	    rrVO.setCurrency(prc.getFondsCurrency());
	    // 注册金额
	    rrVO.setFondsTotal(prc.getFondsTotal() == null ? "/" : prc.getFondsTotal().toString());
	    // 到位金额
	    rrVO.setFondsInPlace(prc.getFondsInPlace() == null ? "/" : prc.getFondsInPlace().toString());
	    // 未到位金额
	    Double fondsNotInPlace = (prc.getFondsTotal() == null ? 0 : prc.getFondsTotal()) - (prc.getFondsInPlace() == null ? 0 : prc.getFondsInPlace());
	    rrVO.setFondsNotInPlace(fondsNotInPlace == null ? "/" : fondsNotInPlace.toString());
	    // 股权比例
	    rrVO.setEquityPerc(prc.getEquityPerc() == null ? "/" : prc.getEquityPerc().toString());
	    // 申请金额
	    rrVO.setRequestMoney(prc.getThisFonds() == null ? "/" : prc.getThisFonds().toString());
	    // 申请币别
	    rrVO.setRequestCurrency(prc.getThisFondsCu());
	    
	    return rrVO;
	}

}
