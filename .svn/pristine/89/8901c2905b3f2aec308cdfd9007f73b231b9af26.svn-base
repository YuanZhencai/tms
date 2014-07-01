package com.wcs.tms.service.report.regicapital;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import com.wcs.base.util.MathUtil;
import com.wcs.base.util.StringUtils;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcRegiCapital;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.RegiCapitalVo;

import filenet.vw.api.VWLogElement;

/** 
* <p>Project: tms</p> 
* <p>Title: 采购资金情况表Service</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
@Stateless
public class RegicapitalReportService implements Serializable {

	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(RegicapitalReportService.class);
	@PersistenceContext(unitName = "pu")
	private EntityManager em;
	@Inject
	PEManager peManager;
	@Inject
	EntityService entityService;
	@Inject
	ProcessUtilMapService processUtilMapService;

	/**
	 * 懒加载方法
	 * @param first
	 * @param pageSize
	 * @param sortField
	 * @param sortOrder
	 * @param conditionMap
	 * @return
	 */
	public HashMap<String, Object> findBookBySqLoad(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> conditionMap) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			List<String> procIds = this.getDealedProcIds(conditionMap);
			log.info("procIds.size():" + procIds.size());
			for (String s : procIds) {
				log.info("sssssssss:" + s);
			}
			int count = (int) procIds.size();
			if (count > 0) {
				String jpql = 
						"select prc from ProcRegiCapital prc join fetch prc.company where prc.defunctInd = 'N' and prc.procInstId in(:procInstId) order by prc.createdDatetime desc";
				// 得到查询结果
				Query query = em.createQuery(jpql).setParameter("procInstId", procIds);
				if (first == count && count != 0) {
					query.setFirstResult(first - pageSize);
				} else {
					query.setFirstResult(first);
				}
				query.setMaxResults(pageSize);
				@SuppressWarnings("unchecked")
				List<ProcRegiCapital> procRegiCapitalList = (List<ProcRegiCapital>) query.getResultList();
				List<RegiCapitalVo> regiCapitalVoList = new ArrayList<RegiCapitalVo>();
				if (!procRegiCapitalList.isEmpty()) {
					for (ProcRegiCapital prc : procRegiCapitalList) {
						RegiCapitalVo rcVo = this.getRCVo(prc);
						regiCapitalVoList.add(rcVo);
					}
					map.put("count", count);
					map.put("list", regiCapitalVoList);
				}
			}
		} catch (Exception e1) {
			log.error("findBookBySqLoad方法 错误信息：" + e1.getMessage());
		}
		return map;
	}

	/**
	 * 得到结束的注册资本金流程实例ID（仅正常结束的,目前也包括手动终止的）
	 * @param conditionMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<String> getDealedProcIds(Map<String, Object> conditionMap) throws Exception {
		// 查询条件组装
		log.info("得到结束的注册资本金流程实例ID（仅正常结束的,目前也包括手动终止的）");
		Map<String, Object> filterMap = this.getDealedFilterMap(conditionMap);
		String filter = (String) filterMap.get("filter");
		Object[] substitutionVars = (Object[]) filterMap.get("substitutionVars");
		List<VWLogElement> VWObjects = peManager.vwEventLogWob(filter, substitutionVars);

		List<String> procIds = new ArrayList<String>();
		for (VWLogElement o : VWObjects) {
			if (procIds.contains(o.getWorkFlowNumber())) {
				procIds.remove(o.getWorkFlowNumber());
			}
			procIds.add(o.getWorkFlowNumber());
		}
		List<String> resultList = new ArrayList<String>();
		StringBuilder jpql = new StringBuilder("select ppFT.procInstId from ProcRegiCapital ppFT join fetch ppFT.company where ppFT.defunctInd = 'N'");
		jpql.append(" and ppFT.procInstId in (?1)");
		String companyId = (String) conditionMap.get("companyId");
		if (!StringUtils.isBlankOrNull(companyId)) {
			jpql.append(" and ppFT.company.id = " + conditionMap.get("companyId"));
			resultList = entityService.find(jpql.toString(), procIds);
		} else {
			List<String> companyIds = (List<String>) conditionMap.get("companyIds");
			if (companyIds != null && !companyIds.isEmpty()) {
				jpql.append(" and ppFT.company.id in (?2)");
				resultList = entityService.find(jpql.toString(), procIds, companyIds);
			}
		}
		return resultList;
	}

	/**
	 * 得到已完成的注册资本金流程的过滤器
	 * @param conditionMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getDealedFilterMap(Map<String, Object> conditionMap) throws Exception {
		Map<String, Object> filterMap = new HashMap<String, Object>();
		StringBuilder filter = new StringBuilder("1=1 and (F_EventType = :EventType or F_EventType = :EventTypeT)");
		List<Object> varsList = new ArrayList<Object>();
		varsList.add(ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal));
		varsList.add(ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ForcedToTerminate));

		// 1.流程类型为注册资本金流程
		filter.append(" and F_WorkClassId = :workClassId");
		varsList.add(peManager.vwGetClassIdByClassName(ProcessXmlUtil.getProcessAttribute("id", "RegiCapital", "className")));

		// 2.流程申请日期在日期区间中
		// 开始时间
		if (null != conditionMap.get("startDate")) {
			filter.append(" and F_TimeStamp >= :startDate");
			varsList.add(conditionMap.get("startDate"));
		}
		// 结束时间
		if (null != conditionMap.get("endDate")) {
			filter.append(" and F_TimeStamp <= :endDate");
			varsList.add(DateUtil.addOneDay((Date) conditionMap.get("endDate")));
		}

		// 排除手动终止流程情况

		Object[] substitutionVars = varsList.toArray();
		filterMap.put("filter", filter.toString());
		filterMap.put("substitutionVars", substitutionVars);

		log.info("filter.toString:" + filter.toString());
		log.info("substitutionVars.toString():" + substitutionVars.toString());
		return filterMap;
	}

	/**
	 * 组装VO
	 * @param procRegiCapital
	 * @return
	 */
	public RegiCapitalVo getRCVo(ProcRegiCapital procRegiCapital) {
		RegiCapitalVo rcVo = new RegiCapitalVo();
		// 申请日期
		rcVo.setApplyDate(procRegiCapital.getApplyDate());
		log.info("申请日期：" + rcVo.getApplyDate());
		// 流程实例编号
		rcVo.setProcInstId(processUtilMapService.getTmsIdByFnId(procRegiCapital.getProcInstId()));
		log.info("流程实例编号:" + rcVo.getProcInstId());
		// 公司ID
		rcVo.setCompanyId(procRegiCapital.getCompany().getId());
		log.info("公司名称：" + rcVo.getCompanyId());
		// 资金提供方
		rcVo.setPayer(procRegiCapital.getPayer());
		log.info("资金提供方：" + rcVo.getPayer());
		// 申请币别
		rcVo.setApplyCu(procRegiCapital.getThisFondsCu());
		log.info("申请币别：" + rcVo.getApplyCu());
		// 申请金额（万）
		rcVo.setApplyAmount(procRegiCapital.getThisFonds());
		log.info("申请金额（万）:" + rcVo.getApplyAmount());
		// 用途
		rcVo.setUseDesc(procRegiCapital.getUse());
		log.info("用途：" + rcVo.getUseDesc());
		// 公司币别
		rcVo.setCompanyCu(this.getCu(procRegiCapital));
		log.info("公司币别：" + rcVo.getCompanyCu());
		// 公司投资总额
		rcVo.setCompanyAmount(this.getAmount(procRegiCapital));
		log.info("公司投资总额：" + rcVo.getCompanyAmount());
		// 注册资本（计算所得）
		rcVo.setRegiCapi(this.getRegiCapi(procRegiCapital));
		log.info("注册资本：" + rcVo.getRegiCapi());
		// 备注
		rcVo.setRemark(procRegiCapital.getRemark());
		log.info("备注（汇款路线及帐号）：" + rcVo.getRemark());
		rcVo.setShareHolder(this.getShareHolder(procRegiCapital));
		return rcVo;
	}

	/**
	 * 获得公司币别
	 * @param procRegiCapital
	 * @return
	 */
	public String getCu(ProcRegiCapital procRegiCapital) {
		StringBuilder jpql = new StringBuilder("select c from Company c where c.defunctInd = 'N' and c.status = 'Y'");
		jpql.append(" and c.id =" + procRegiCapital.getCompany().getId());
		log.info("jpql:" + jpql);
		Company c = entityService.findUnique(jpql.toString());
		String cu = c.getInvestCurrency();
		log.info("币别：" + cu);
		return cu;
	}

	/**
	 * 获得公司金额
	 * @param procRegiCapital
	 * @return
	 */
	public Double getAmount(ProcRegiCapital procRegiCapital) {
		Double amount = 0D;
		StringBuilder jpql = new StringBuilder("select c from Company c where c.defunctInd = 'N' and c.status = 'Y'");
		jpql.append(" and c.id =" + procRegiCapital.getCompany().getId());
		log.info("jpql:" + jpql);
		Company c = entityService.findUnique(jpql.toString());
		amount = c.getInvestTotal();
		log.info("金额：" + amount);
		return amount;
	}

	/**
	 * 获得注册资本
	 * @param procRegiCapital
	 * @return
	 */
	public Double getRegiCapi(ProcRegiCapital procRegiCapital) {
		Double amount = 0D;
		List<ShareHolder> sh = new ArrayList<ShareHolder>();
		StringBuilder jpql = new StringBuilder("select sh from ShareHolder sh join fetch sh.company where sh.defunctInd='N' ");
		jpql.append(" and sh.company.id = " + procRegiCapital.getCompany().getId());
		sh = entityService.find(jpql.toString());
		log.info("公司ID为" + procRegiCapital.getCompany().getId() + "的股东的个数为：" + sh.size());
		if (sh.size() != 0) {
			for (ShareHolder s : sh) {
				amount += MathUtil.roundHalfUp( s.getFondsTotal(), 4);
			}
		}
		return amount;
	}

	/**
	 * 根据注册资本金流程中的公司ID获得它的股东
	 * @param procRegiCapital
	 * @return
	 */
	public List<ShareHolder> getShareHolder(ProcRegiCapital procRegiCapital) {
		List<ShareHolder> sh = new ArrayList<ShareHolder>();
		StringBuilder jpql = new StringBuilder("select sh from ShareHolder sh join fetch sh.company where sh.defunctInd='N' ");
		jpql.append(" and sh.company.id = " + procRegiCapital.getCompany().getId());
		sh = entityService.find(jpql.toString());
		log.info("公司ID为" + procRegiCapital.getCompany().getId() + "的股东的个数为：" + sh.size());
		return sh;
	}

}
