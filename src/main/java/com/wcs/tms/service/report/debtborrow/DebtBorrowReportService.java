package com.wcs.tms.service.report.debtborrow;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.wcs.base.service.LoginService;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.StringUtils;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.DebtContract;
import com.wcs.tms.model.ProcDebtBorrow;
import com.wcs.tms.model.ProcDebtPayment;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.DebtBorrowRequestVO;
import com.wcs.tms.view.process.common.entity.DebtRequestVo;

import filenet.vw.api.VWException;
import filenet.vw.api.VWLogElement;

/**
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description:外债申请明细表Service
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Stateless
public class DebtBorrowReportService implements Serializable {

	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(DebtBorrowReportService.class);
	@PersistenceContext(unitName = "pu")
	private EntityManager em;
	@Inject
	EntityService entityService;
	@Inject
	PEManager peManager;
	@Inject
	LoginService loginService;
	@Inject
	CommonBean dictBean;
	@Inject
	ProcessUtilMapService processUtilMapService;

	/**
	 * 查询外债申请明细
	 * 
	 * @param conditionMap
	 * @return
	 * @throws ServiceException
	 */
	public List<ProcDebtBorrow> getProcDebtBorrowList(Map<String, Object> conditionMap) throws ServiceException {
		List<ProcDebtBorrow> dbList = new ArrayList<ProcDebtBorrow>();
		List<VWLogElement> les = new ArrayList<VWLogElement>();
		List<String> procIds = new ArrayList<String>();
		try {
			Map<String, Object> filterMap = this.getDealedFilterMap(conditionMap);
			String filter = (String) filterMap.get("filter");
			Object[] substitutionVars = (Object[]) filterMap.get("substitutionVars");

			les = peManager.vwEventLogWob(filter, substitutionVars);
			for (VWLogElement le : les) {
				procIds.add(le.getWorkObjectNumber());
			}
		} catch (Exception e) {
			log.error("getProcDebtBorrowList方法中前一方法 查询外债申请明细出现异常", e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		try {
			// 查询数据库
			dbList = this.findProcDebtBorrowListByWob(procIds, conditionMap);
			// 计算借款时间和还款时间
			this.procDebtBorrowFilter(dbList, les);
		} catch (Exception e) {
			log.error("getProcDebtBorrowList方法中后一方法 查询外债申请明细出现异常", e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return dbList;
	}

	/**
	 * 得到已完成的外债流程的过滤器
	 * 
	 * @param conditionMap
	 * @return
	 */
	private Map<String, Object> getDealedFilterMap(Map<String, Object> conditionMap) throws ServiceException {
		Map<String, Object> filterMap = new HashMap<String, Object>();
		StringBuilder filter = new StringBuilder("1=1 and F_EventType = :EventType");
		List<Object> varsList = new ArrayList<Object>();
		varsList.add(ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal));
		// 登录人

		// 2.流程类型为外债申请
		filter.append(" and F_WorkClassId = :workClassId");
		varsList.add(peManager.vwGetClassIdByClassName(ProcessXmlUtil.getProcessAttribute("id", "DebtBorrow", "className")));

		// 排除手动终止流程情况
		filter.append(" and F_Originator <> F_UserId");

		Object[] substitutionVars = varsList.toArray();
		filterMap.put("filter", filter.toString());
		filterMap.put("substitutionVars", substitutionVars);
		return filterMap;
	}

	/**
	 * 流程实例ID查询外债实体
	 * 
	 * @param wobNoList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ProcDebtBorrow> findProcDebtBorrowListByWob(List<String> procIds, Map<String, Object> conditionMap) throws ServiceException {
		List<ProcDebtBorrow> resultList = new ArrayList<ProcDebtBorrow>();
		try {
			StringBuilder jpql = new StringBuilder("select db from ProcDebtBorrow db join fetch db.company where db.defunctInd='N' ");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (null != conditionMap.get("creditStartS")) {
				String start = sdf.format(conditionMap.get("startDateS"));
				jpql.append(" and db.corpAuditLis >= '" + start + "' ");
			}
			if (null != conditionMap.get("startDateE")) {
				String start = sdf.format(conditionMap.get("startDateE"));
				jpql.append(" and db.corpAuditLis <= '" + start + "' ");
			}
			if (null != conditionMap.get("endDateS")) {
				String start = sdf.format(conditionMap.get("endDateS"));
				jpql.append(" and db.corpAuditLie >= '" + start + "' ");
			}
			if (null != conditionMap.get("endDateE")) {
				String start = sdf.format(conditionMap.get("endDateE"));
				jpql.append(" and db.corpAuditLie <= '" + start + "' ");
			}
			jpql.append(" and db.procInstId in (?1)");
			String companyId = (String) conditionMap.get("companyId");
			if (!StringUtils.isBlankOrNull(companyId)) {
				jpql.append(" and db.company.id = " + conditionMap.get("companyId"));
				resultList = entityService.find(jpql.toString(), procIds);
			} else {
				List<String> companyIds = (List<String>) conditionMap.get("companyIds");
				if (companyIds != null && !companyIds.isEmpty()) {
					jpql.append(" and db.company.id in (?2)");
					resultList = entityService.find(jpql.toString(), procIds, companyIds);
				}
			}
		} catch (Exception e) {
			log.error("findProcDebtBorrowListByWob方法 流程实例ID查询外债实体出现异常", e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return resultList;
	}

	/**
	 * 计算贷款时间(不知道这个方法有啥作用)
	 * 
	 * @param dbList
	 * @param les
	 * @throws VWException
	 * @throws Exception
	 */
	private void procDebtBorrowFilter(List<ProcDebtBorrow> dbList, List<VWLogElement> les) throws VWException {
		for (ProcDebtBorrow db : dbList) {
			String wobNo = db.getProcInstId();
			for (VWLogElement le : les) {
				if (wobNo.equals(le.getWorkObjectNumber())) {
					Date borrowDate = le.getTimeStamp();
					Integer limit = db.getCorpAuditLi() == null ? 0 : db.getCorpAuditLi().intValue();
					Calendar c = Calendar.getInstance();
					c.setTime(borrowDate);
					c.add(Calendar.MONTH, limit);
					Date payBackDate = c.getTime();
					db.setBorrowDate(borrowDate);
					if (limit != 0) {
						db.setPayBackDate(payBackDate);
					}
					break;
				}
			}
			String providerType = db.getProviderType();
			if (providerType != null) {
				// 股东类型
				if ("G".equals(providerType)) {
					if (db.getShareHolder() != null) {
						db.setProviderName(db.getShareHolder().getShareHolderName());
					}
				} else {
					// 海外外债 和 展期 类型
					db.setProviderName(dictBean.getValueByDictCatAndKey("TMS_FUND_PROVIDER_COM_NAME", db.getProviderKey()));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> findDebtBorrowRequestDetail(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> conditionMap) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Query query = null;
		try {
			StringBuilder jpql = new StringBuilder("select db from ProcDebtBorrow db join fetch db.company where db.defunctInd='N' ");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (null != conditionMap.get("creditStartS")) {
				String start = sdf.format(conditionMap.get("startDateS"));
				jpql.append(" and db.corpAuditLis >= '" + start + "' ");
			}
			if (null != conditionMap.get("startDateE")) {
				String start = sdf.format(conditionMap.get("startDateE"));
				jpql.append(" and db.corpAuditLis <= '" + start + "' ");
			}
			if (null != conditionMap.get("endDateS")) {
				String start = sdf.format(conditionMap.get("endDateS"));
				jpql.append(" and db.corpAuditLie >= '" + start + "' ");
			}
			if (null != conditionMap.get("endDateE")) {
				String start = sdf.format(conditionMap.get("endDateE"));
				jpql.append(" and db.corpAuditLie <= '" + start + "' ");
			}
			// 流程编号
			List<String> procInstIds = new ArrayList<String>();
			if (null != conditionMap.get("processNo") && !"".equals(conditionMap.get("processNo"))) {
				procInstIds = processUtilMapService.getFnIdsByTmsId(conditionMap.get("processNo").toString());
				if (procInstIds != null) {
					jpql.append(" and pdp.procInstId in (:procInstIds)");
				}
			}

			String companyId = (String) conditionMap.get("companyId");
			if (!StringUtils.isBlankOrNull(companyId)) {
				jpql.append(" and db.company.id = " + conditionMap.get("companyId"));
				query = em.createQuery(jpql.toString());
			} else {
				List<String> companyIds = (List<String>) conditionMap.get("companyIds");
				if (companyIds != null && !companyIds.isEmpty()) {
					jpql.append(" and db.company.id in (:companyIds)");
					query = em.createQuery(jpql.toString()).setParameter("companyIds", companyIds);
				}
			}
			if (!procInstIds.isEmpty()) {
				query.setParameter("procInstIds", procInstIds);
			}
			int count = query.getResultList().size();
			query.setFirstResult(first);
			query.setMaxResults(pageSize);
			List<ProcDebtBorrow> procDebtBorrows = (List<ProcDebtBorrow>) query.getResultList();
			List<DebtRequestVo> debtRequestVos = new ArrayList<DebtRequestVo>();
			for (ProcDebtBorrow pdb : procDebtBorrows) {
				DebtRequestVo debtRequestVo = this.getDebtRequestVo(pdb);
				debtRequestVos.add(debtRequestVo);
			}
			map.put("count", count);
			map.put("list", debtRequestVos);

		} catch (Exception e) {
			log.error("findDebtBorrowRequestDetail方法 错误信息" + e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * @param pdb
	 * @return
	 */
	public DebtRequestVo getDebtRequestVo(ProcDebtBorrow pdb) {
		DebtRequestVo debtRequestVo = new DebtRequestVo(pdb);
		
		// 流程编号
		debtRequestVo.setProcessNo(processUtilMapService.getTmsIdByFnId(pdb.getProcInstId()));
		// 申请金额
		debtRequestVo.setRequestMoney(pdb.getCorpAudit()== null ? "/" : pdb.getCorpAudit().toString());
		// 币别
		debtRequestVo.setCurrency(pdb.getCorpAuditCu());
		// 是否到账
		/** 外债合同_主数据DEBT_CONTRACT **/
		// 外债合同 = 合同编号 + 出资方 + 开始-结束日期 + 利率
		DebtContract debtContract = entityService.find(DebtContract.class, pdb.getDebtContractId());
		String debtContractStr = debtContract.getDebtContractNo() + " + " + debtContract.getShareHolder().getShareHolderName() + " + " + DateUtil.convertDateToString(debtContract.getContractStartDate(), "yyyy-MM-dd") + "-" + DateUtil.convertDateToString(debtContract.getContractEndDate(), "yyyy-MM-dd") + " + " + (debtContract.getApprovalRate() == null ? "" : debtContract.getApprovalRate());
		debtRequestVo.setDebtContract(debtContractStr);
		// 外债合同金额
		debtRequestVo.setDebtContractFunds(debtContract.getDebtContractFunds() == null ? "/" : debtContract.getDebtContractFunds().toString());
		// 未请款金额 = 外债合同金额 - 未请款金额
		Double noAppliedFunds = (debtContract.getDebtContractFunds() == null ? 0 : debtContract.getDebtContractFunds()) - (debtContract.getAppliedFunds() == null ? 0 : debtContract.getAppliedFunds());
		debtRequestVo.setNoAppliedFunds(noAppliedFunds == 0 ? "/" : noAppliedFunds.toString());
		// 已请款金额
		debtRequestVo.setApplyFunds(debtContract.getAppliedFunds() == null ? "/" : debtContract.getAppliedFunds().toString());

		return debtRequestVo;
	}
}
