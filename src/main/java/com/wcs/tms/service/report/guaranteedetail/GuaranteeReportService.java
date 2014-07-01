package com.wcs.tms.service.report.guaranteedetail;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;


import com.wcs.base.service.EntityService;
import com.wcs.base.util.MathUtil;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.Guarantee;
import com.wcs.tms.model.ProcGuarantee;
/**
 * 
 * <p>Project: tms</p>
 * <p>Description:集团内部担保明细Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
@Stateless
public class GuaranteeReportService implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@EJB
	EntityService entityService;
	
	public GuaranteeReportService(){
		
	}
	
	/**
	 * 查询所有担保方(明细表)
	 * @param conditionMap 
	 * @return
	 */
	public List<Company> findAllDoneSecuredCompany(Map<String, Object> conditionMap) {
		String securedCompanyId = (String) conditionMap.get("securedCompanyId");
		StringBuilder jpql = new StringBuilder("select distinct g.securedCom from Guarantee g join fetch g.insuredCom " +
				"join fetch g.securedCom join fetch g.bank where g.defunctInd = 'N'");
		if(!MathUtil.isEmptyOrNull(securedCompanyId)){
			jpql.append(" and g.securedCom.id="+securedCompanyId);
		}
		return entityService.find(jpql.toString());
	}

	/**
	 * 根据担保方及条件查询其所有明细
	 * @param securedCom 担保方
	 * @param conditionMap 
	 * @return
	 */
	public List<Guarantee> findGuaranteeDetailsBy(Company securedCom, Map<String, Object> conditionMap) {
		
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String insuredCompanyId = (String) conditionMap.get("insuredCompanyId");
		
		StringBuilder jpql = new StringBuilder("select g from Guarantee g join fetch g.insuredCom " +
				"join fetch g.securedCom join fetch g.bank where g.defunctInd = 'N'");
		
		jpql.append(" and g.securedCom.id="+securedCom.getId());
		if(!MathUtil.isEmptyOrNull(insuredCompanyId)){
			jpql.append(" and g.insuredCom.id="+insuredCompanyId);
		// modified on 2013-3-11
		}else{
			List<String> companyIds = (List<String>) conditionMap.get("companyIds");
			if (companyIds != null && !companyIds.isEmpty()) {
				jpql.append(" and g.insuredCom.id in (:a)");
				map.put("a", companyIds);
			}
		}
		if(null != conditionMap.get("writeStartDate")){
			String writeStartDate = sdf.format(conditionMap.get("writeStartDate"));
			jpql.append(" and g.createdDatetime>='"+writeStartDate+"'");
		}
		if(null != conditionMap.get("writeEndDate")){
			Date date = (Date)conditionMap.get("writeEndDate");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			String writeEndDate = sdf.format(calendar.getTime());
			jpql.append(" and g.createdDatetime<='"+writeEndDate+"'");
		}
		if(null != conditionMap.get("effectStartDate")){
			String effectStartDate = sdf.format(conditionMap.get("effectStartDate"));
			jpql.append(" and g.guarStart>='"+effectStartDate+"'");
		}
		if(null != conditionMap.get("effectEndDate")){
			String effectEndDate = sdf.format(conditionMap.get("effectEndDate"));
			jpql.append(" and g.guarStart<='"+effectEndDate+"'");
		}
		if(null != conditionMap.get("terminateStartDate")){
			String terminateStartDate = sdf.format(conditionMap.get("terminateStartDate"));
			jpql.append(" and g.guarEnd>='"+terminateStartDate+"'");
		}
		if(null != conditionMap.get("terminateEndDate")){
			String terminateEndDate = sdf.format(conditionMap.get("terminateEndDate"));
			jpql.append(" and g.guarEnd<='"+terminateEndDate+"'");
		}
		
	
		Query query = entityService.createQuery(jpql.toString(), map);
		return query.getResultList();
	}
	
	/**
	 * 查询所有担保方(流程表)
	 * @param conditionMap 
	 * @return
	 */
	public List<Company> findAllDoingSecuredCompany(
			Map<String, Object> conditionMap) {
		String securedCompanyId = (String) conditionMap.get("securedCompanyId");
		//流程表
		StringBuilder jpql = new StringBuilder("select distinct p.securedCom from ProcGuarantee p join fetch p.company " +
				"join fetch p.securedCom join fetch p.bank where p.defunctInd = 'N'");
		if(!MathUtil.isEmptyOrNull(securedCompanyId)){
			jpql.append(" and p.securedCom.id="+securedCompanyId);
		}
		return entityService.find(jpql.toString());
	}
	
	/**
	 * 根据担保方及条件查询其所有流程数据
	 * @param securedCom 担保方
	 * @param conditionMap 
	 * @return
	 */
	public List<ProcGuarantee> findProcGuaranteeDetailsBy(Company securedCom,
			Map<String, Object> conditionMap) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String insuredCompanyId = (String) conditionMap.get("insuredCompanyId");
		// p.status = '3'为预担保
		StringBuilder jpql = new StringBuilder("select p from ProcGuarantee p join fetch p.company " +
				"join fetch p.securedCom join fetch p.bank where p.defunctInd = 'N' and p.status = '3'");
		
		jpql.append(" and p.securedCom.id="+securedCom.getId());
		if(!MathUtil.isEmptyOrNull(insuredCompanyId)){
			jpql.append(" and p.company.id="+insuredCompanyId);
		}else{
			List<String> companyIds = (List<String>) conditionMap.get("companyIds");
			if (companyIds != null && !companyIds.isEmpty()) {
				jpql.append(" and p.company.id in (:a)");
				map.put("a", companyIds);
			}
		}
		if(null != conditionMap.get("writeStartDate")){
			String writeStartDate = sdf.format(conditionMap.get("writeStartDate"));
			jpql.append(" and p.createdDatetime>='"+writeStartDate+"'");
		}
		if(null != conditionMap.get("writeEndDate")){
			Date date = (Date)conditionMap.get("writeEndDate");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			String writeEndDate = sdf.format(calendar.getTime());
			jpql.append(" and p.createdDatetime<='"+writeEndDate+"'");
		}
		if(null != conditionMap.get("effectStartDate")){
			String effectStartDate = sdf.format(conditionMap.get("effectStartDate"));
			jpql.append(" and p.guarStart>='"+effectStartDate+"'");
		}
		if(null != conditionMap.get("effectEndDate")){
			String effectEndDate = sdf.format(conditionMap.get("effectEndDate"));
			jpql.append(" and p.guarStart<='"+effectEndDate+"'");
		}
		if(null != conditionMap.get("terminateStartDate")){
			String terminateStartDate = sdf.format(conditionMap.get("terminateStartDate"));
			jpql.append(" and p.guarEnd>='"+terminateStartDate+"'");
		}
		if(null != conditionMap.get("terminateEndDate")){
			String terminateEndDate = sdf.format(conditionMap.get("terminateEndDate"));
			jpql.append(" and p.guarEnd<='"+terminateEndDate+"'");
		}
		
		
		Query query = entityService.createQuery(jpql.toString(), map);
		return query.getResultList();
	}

	/**
	 * 查询预担保的所有担保公司
	 * @param conditionMap
	 * @return
	 */
	public List<Company> findAllPreSecuredCompany(Map<String, Object> conditionMap) {
		String securedCompanyId = (String) conditionMap.get("securedCompanyId");
        StringBuilder bulder = new StringBuilder();
        bulder.append("select distinct p.securedCom from ProcGuarantee p " +
            		"join fetch p.company join fetch p.securedCom join fetch p.bank " +
            		"where p.defunctInd = 'N' and p.status = '3'");
        if(!MathUtil.isEmptyOrNull(securedCompanyId)){
           bulder.append(" and p.securedCom.id="+securedCompanyId);
        }
		return entityService.find(bulder.toString());
	}
	
}
