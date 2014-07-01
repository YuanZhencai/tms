package com.wcs.tms.service.report.purchaseFund;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.StringUtils;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.vo.DictVO;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.common.service.DictService;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.model.ProcPurchaseFundTrade;
import com.wcs.tms.model.ProcPurchaseFundTradeDetail;
import com.wcs.tms.model.ProdOrTradeCashMain;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.ProcPurchaseFundAmountVo;
import com.wcs.tms.view.process.common.entity.PurchaseFundVo;
import com.wcs.tms.view.process.common.entity.PurchaseFundVoSorter;

import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWWorkObject;

/** 
* <p>Project: tms</p> 
* <p>Title: 采购资金情况表Service</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
@Stateless
public class PurchaseFundReportService implements Serializable {

	private static Log log = LogFactory.getLog(PurchaseFundReportService.class);
	private static final long serialVersionUID = 1L;

	@Inject
	PEManager peManager;
	@Inject
	EntityService entityService;
	@Inject
	ProcessUtilMapService processUtilMapService;
	@Inject
	DictService dictService;

	public List<PurchaseFundVo> getPurchaseFundVos(Map<String, Object> conditionMap) throws Exception {
		// 由于需求暂时不确定，所以目前只能查询采购资金（贸易）流程的
		List<PurchaseFundVo> pfVoList = new ArrayList<PurchaseFundVo>();
		//pfVoList.addAll(this.getPurFundTrade(conditionMap));
		pfVoList.addAll(this.getPurFundTradeVo(conditionMap));
		log.info("pfVoList的个数是：" + pfVoList.size());
		Collections.sort(pfVoList, new PurchaseFundVoSorter());
		return pfVoList;
	}

	/**
	 * 组装采购资金（贸易）流程VO显示数据
	 * @param procPurchaseFundTrade
	 * @return
	 * @throws Exception 
	 */
	public List<PurchaseFundVo> getPurFundTradeVo(Map<String, Object> conditionMap) throws Exception {
		// 获得活动的FN流程实例ID
		List<String> procIds = this.getProcIds(conditionMap);
		// 获得审批结束或者被终止掉的流程实例ID
		List<String> procIdsFilter = this.getDealedProcIds(conditionMap);
		// 品种字典列表
		List<DictVO> dictVOs = dictService.searchData(DictConsts.TMS_FARM_PRODUCE_VARIETY_TYPE, "", "", "", "");
		
		Map<String, ProcPurchaseFundAmountVo> procMap = getProcPurchaseFundAmount(procIds);
		Map<String, ProcPurchaseFundAmountVo> procFilterMap = getProcPurchaseFundAmountFilter(procIdsFilter);
		//最终累计金额、自有资金、集团资金
		Map<String, ProcPurchaseFundAmountVo> finalProcMap = transformMap(procMap, procFilterMap, dictVOs);
		
		List<String> procInstId = new ArrayList<String>();
		procInstId.addAll(procIds);
		procInstId.addAll(procIdsFilter);
		// 组装信息
		return transformPurchaseFundVo(procInstId, finalProcMap);
	}
	
	/**
	 * 查询采购资金（贸易）流程VO数据
	 * @param procInstId
	 * @param finalProcMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PurchaseFundVo> transformPurchaseFundVo(List<String> procInstId, Map<String, ProcPurchaseFundAmountVo> finalProcMap) {
		List<PurchaseFundVo> result = new ArrayList<PurchaseFundVo>();
		if(procInstId != null && procInstId.size() > 0) {
			StringBuilder sql = new StringBuilder("");
			sql.append("SELECT t2.PAYMENT_DATE, t2.PROC_INST_ID , t3.COMPANY_NAME, t1.VARIETY, t1.APP_TYPE,");
			sql.append(" t1.TOTAL_CASH, t1.TOTAL_CASH_AMOUNT, t1.UPDATED_BY, t1.START_DATE,");
			sql.append(" t1.END_DATE, t1.TOTAL_CASH-t.VARIETY_REMAIN,t.VARIETY_REMAIN, t.VARIETY_AMOUNT, t.VARIETY_NUM, t2.LOAN_IDEN");
			sql.append(" FROM PROC_PURCHASE_FUND_TRADE_PRODUCT t");
			sql.append(" LEFT JOIN PROD_OR_TRADE_CASH_MAIN t1 ON t.VARIETY = t1.VARIETY AND t1.APP_TYPE = 'T' AND t1.DEFUNCT_IND = 'N'");
			sql.append(" AND date(t1.START_DATE) <= date(t.CREATED_DATETIME) AND date(t1.END_DATE) >= date(t.CREATED_DATETIME)");
			sql.append(" LEFT JOIN PROC_PURCHASE_FUND_TRADE t2 ON t2.ID = t.PROC_INST_ID LEFT JOIN COMPANY t3 ON t1.COMPANY_ID = t3.ID");
			sql.append(" WHERE t2.PROC_INST_ID in (");
			StringBuilder temp = new StringBuilder("");
			for (String procId : procInstId) {
				temp.append("'").append(procId).append("',");
			}
			sql.append(temp.substring(0, temp.length() - 1)).append(")");
			Query query = entityService.findNativeQuery(sql.toString());
			List<Object[]> res = query.getResultList();
			for (Object[] objects : res) {
				PurchaseFundVo vo = new PurchaseFundVo();
				
				//付款时间
				vo.setPayDate((Date)objects[0]);
				//流程实例编号
				vo.setProcInstId(processUtilMapService.getTmsIdByFnId((String)objects[1]));
				//公司名称
				vo.setCompanyName((String)objects[2]);
				//品种
				String variety = (String)objects[3];
				vo.setVarietyId(variety);
				//贸易类型
				vo.setType((String)objects[4]);
				//审批总头寸(吨)
				vo.setTotalCash(((BigDecimal)objects[5]).doubleValue());
				//总金额(万元)
				vo.setTotalCashAmount(((BigDecimal)objects[6]).doubleValue());
				//审批人
				vo.setUpdatedBy((String)objects[7]);
				//起始日期
				vo.setStartDate((Date)objects[8]);
				//结束日期
				vo.setEndDate((Date)objects[9]);
				//已累计采购数量
				vo.setAllPurchaseNum(((BigDecimal)objects[10]).doubleValue());
				//剩余采购头寸
				vo.setLessPurchaseNum(((BigDecimal)objects[11]).doubleValue());
				//本次采购金额
				vo.setThisPurchaseAmount(((BigDecimal)objects[12]).doubleValue());
				//本次采购数量
				vo.setThisPurchaseNum(((BigDecimal)objects[13]).doubleValue());
				String loanIden = (String)objects[14];
				// 均价（元）
				vo.setAmountAverage(this.format(vo.getTotalCashAmount() * 10000) / vo.getTotalCash());
				ProcPurchaseFundAmountVo amountVo = finalProcMap.get(variety.trim());
				// 已累计采购金额
				vo.setAllPurchaseAmount(amountVo.getAmount());
				//金额
				vo.setLessPurchaseAmount(vo.getTotalCashAmount() - amountVo.getAmount());
				// 采购平均单价（元）
				vo.setPurchaseAverage(this.format((vo.getThisPurchaseAmount() * 10000) / vo.getThisPurchaseNum()));
				if ("T".equals(loanIden)) {
					// 转款时是自有资金
					// 集团提供资金
					vo.setGroPrivader(0.00);
					vo.setOrgOwner(amountVo.getOwnerAmount());
				}else {
					// 借款、借款+转款时是集团提供资金
				    // 非转款
					vo.setGroPrivader(amountVo.getOrgAmount());
					vo.setOrgOwner(0.00);
				}
				result.add(vo);
			}
			
		}
		return result;
	}
	
	/**
	 * 已审批过的流程的累计资金、集团资金、自有资金
	 * @param procIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, ProcPurchaseFundAmountVo> getProcPurchaseFundAmountFilter(List<String> procIdsFilter) {
		Map<String, ProcPurchaseFundAmountVo> map = new HashMap<String, ProcPurchaseFundAmountVo>();
		if(procIdsFilter != null && procIdsFilter.size() > 0) {
			StringBuilder sql = new StringBuilder("");
			sql.append("SELECT a1.VARIETY,");
			sql.append(" value(SUM(case when a2.DEFUNCT_IND = 'N' AND (a2.LOAN_IDEN = 'L' OR  a2.LOAN_IDEN = 'A') then a.VARIETY_AMOUNT else 0 end), 0) AMOUNT,");
			sql.append(" value(SUM(case when a2.DEFUNCT_IND = 'N' AND a2.LOAN_IDEN = 'T' then a.VARIETY_AMOUNT else 0 end), 0) OWNER_AMOUNT,");
			sql.append(" value(SUM(case when a2.DEFUNCT_IND = 'N' AND a2.LOAN_IDEN <> 'T' then a.VARIETY_AMOUNT else 0 end), 0) ORG_AMOUNT");
			sql.append(" FROM  PROC_PURCHASE_FUND_TRADE_PAY_DETAIL a");
			sql.append(" LEFT JOIN PROC_PURCHASE_FUND_TRADE_PRODUCT a1 ON a1.ID = a.PRODUCT_ID");
			sql.append(" LEFT JOIN PROC_PURCHASE_FUND_TRADE a2 ON a1.PROC_INST_ID = a2.ID");
			sql.append(" WHERE a2.PROC_INST_ID in (");
			StringBuilder temp = new StringBuilder("");
			for (String procId : procIdsFilter) {
				temp.append("'").append(procId).append("',");
			}
			sql.append(temp.substring(0, temp.length() - 1)).append(")");
			sql.append(" GROUP BY a1.VARIETY");
			Query query = entityService.findNativeQuery(sql.toString());
			List<Object[]> result = query.getResultList();
			for (Object[] objects : result) {
				ProcPurchaseFundAmountVo vo = new ProcPurchaseFundAmountVo();
				vo.setDictId((String)objects[0]);
				vo.setAmount(((BigDecimal)objects[1]).doubleValue());
				vo.setOwnerAmount(((BigDecimal)objects[2]).doubleValue());
				vo.setOrgAmount(((BigDecimal)objects[3]).doubleValue());
				map.put(vo.getDictId(), vo);
			}
		}
		return map;
	}
	
	
	/**
	 * 正在审批过程中的流程的累计资金、集团资金、自有资金
	 * @param procIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, ProcPurchaseFundAmountVo> getProcPurchaseFundAmount(List<String> procIds) {
		Map<String, ProcPurchaseFundAmountVo> map = new HashMap<String, ProcPurchaseFundAmountVo>();
		if(procIds != null && procIds.size() > 0) {
			StringBuilder sql = new StringBuilder("");
			sql.append("SELECT a.VARIETY ,");
			sql.append(" value(SUM(case when a1.DEFUNCT_IND = 'N' AND (a1.LOAN_IDEN = 'L' OR  a1.LOAN_IDEN = 'A') then a.VARIETY_AMOUNT else 0 end), 0) AMOUNT,");
			sql.append(" value(SUM(case when a1.DEFUNCT_IND = 'N' AND a1.LOAN_IDEN = 'T' then a.VARIETY_AMOUNT else 0 end), 0) OWNER_AMOUNT,");
			sql.append(" value(SUM(case when a1.DEFUNCT_IND = 'N' AND a1.LOAN_IDEN <> 'T' then a.VARIETY_AMOUNT else 0 end), 0) ORG_AMOUNT");
			sql.append(" FROM  PROC_PURCHASE_FUND_TRADE_PRODUCT a");
			sql.append(" LEFT JOIN PROC_PURCHASE_FUND_TRADE a1 ON a.PROC_INST_ID = a1.ID");
			sql.append(" WHERE a1.PROC_INST_ID in (");
			StringBuilder temp = new StringBuilder("");
			for (String procId : procIds) {
				temp.append("'").append(procId).append("',");
			}
			sql.append(temp.substring(0, temp.length() - 1)).append(")");
			sql.append(" GROUP BY a.VARIETY");
			Query query = entityService.findNativeQuery(sql.toString());
			List<Object[]> result = query.getResultList();
			for (Object[] objects : result) {
				ProcPurchaseFundAmountVo vo = new ProcPurchaseFundAmountVo();
				vo.setDictId((String)objects[0]);
				vo.setAmount(((BigDecimal)objects[1]).doubleValue());
				vo.setOwnerAmount(((BigDecimal)objects[2]).doubleValue());
				vo.setOrgAmount(((BigDecimal)objects[3]).doubleValue());
				map.put(vo.getDictId(), vo);
			}
		}
		return map;
	}
	
	/**
	 * 封装累计金额、自有资金、集团资金
	 * @param procMap
	 * @param procFilterMap
	 * @param dictVOs
	 * @return
	 */
	public Map<String, ProcPurchaseFundAmountVo> transformMap(Map<String, ProcPurchaseFundAmountVo> procMap, 
			Map<String, ProcPurchaseFundAmountVo> procFilterMap, List<DictVO> dictVOs) {
		Map<String, ProcPurchaseFundAmountVo> finalProcMap = new HashMap<String, ProcPurchaseFundAmountVo>();
		
		for (DictVO dictVO : dictVOs) {
			ProcPurchaseFundAmountVo vo = new ProcPurchaseFundAmountVo();
			ProcPurchaseFundAmountVo procVo = procMap.get(dictVO.getCodeKey());
			ProcPurchaseFundAmountVo procFilterVo = procFilterMap.get(dictVO.getCodeKey());
			Double amount = 0d;
			Double ownerAmount = 0d;
			Double orgAmount = 0d;
			if(procVo != null) {
				amount += procVo.getAmount();
				ownerAmount += procVo.getOwnerAmount();
				orgAmount += procVo.getOrgAmount();
			}
			if(procFilterVo != null) {
				amount += procFilterVo.getAmount();
				ownerAmount += procFilterVo.getOwnerAmount();
				orgAmount += procFilterVo.getOrgAmount();
			}
			vo.setDictId(dictVO.getCodeKey());
			vo.setAmount(amount);
			vo.setOwnerAmount(ownerAmount);
			vo.setOrgAmount(orgAmount);
System.out.println("dictVO.getCodeKey():"+dictVO.getCodeKey() + "  amount:"+amount+" ownerAmount:"+ownerAmount+" orgAmount:"+orgAmount);
			finalProcMap.put(dictVO.getCodeKey(), vo);
		}
		return finalProcMap;
	}
	
	/**
	 * 将需要计算的内容转换成相应格式
	 * @param am
	 * @return
	 */
	public Double format(Double am) {
		String parten = "#.00";
		DecimalFormat decimal = new DecimalFormat(parten);
		String str = decimal.format(am);
		return Double.parseDouble(str);
	}

	/**
	 * 获得正在审批过程中的流程的流程实例ID（FN服务器上符合条件的流程）
	 * @param procPurchaseFundTrade
	 * @return
	 * @throws Exception
	 */
	public List<String> getProcIds(Map<String, Object> conditionMap) throws Exception {
		// 查询条件组装
		log.info("获得正在审批过程中的流程的流程实例ID（FN服务器上符合条件的流程）");
		Map<String, Object> filterMap = this.getActiveFilterMapTrade(conditionMap);
		String filter = (String) filterMap.get("filter");
		log.info("filter:" + filter);
		Object[] substitutionVars = (Object[]) filterMap.get("substitutionVars");
		List<VWWorkObject> VWObjects = peManager.vwGetTmsWorkObjects(filter, substitutionVars);
		List<String> procIds = new ArrayList<String>();
		for (VWWorkObject o : VWObjects) {
			procIds.add(o.getWorkflowNumber());
		}
		List<String> resultList = new ArrayList<String>();
		StringBuilder jpql = new StringBuilder(
				"select ppFT.procInstId from ProcPurchaseFundTrade ppFT join fetch ppFT.company where ppFT.defunctInd = 'N'");
		jpql.append(" and ppFT.procInstId in (?1)");
		String companyId = (String) conditionMap.get("companyId");
		if (!StringUtils.isBlankOrNull(companyId)) {
			jpql.append(" and ppFT.company.id = " + conditionMap.get("companyId"));
			resultList = entityService.find(jpql.toString(), procIds);
		} else {
			@SuppressWarnings("unchecked")
			List<String> companyIds = (List<String>) conditionMap.get("companyIds");
			if (companyIds != null && !companyIds.isEmpty()) {
				jpql.append(" and ppFT.company.id in (?2)");
				resultList = entityService.find(jpql.toString(), procIds, companyIds);
			}
		}
		return resultList;
	}

	/**
	 * 得到活动的采购资金（贸易）流程的过滤器
	 * @param conditionMap
	 * @return
	 */
	private Map<String, Object> getActiveFilterMapTrade(Map<String, Object> conditionMap) {
		log.info("得到活动的采购资金（贸易）流程的过滤器++++++++++++++");
		Map<String, Object> filterMap = new HashMap<String, Object>();
		StringBuilder filter = new StringBuilder("1=1");
		List<Object> varsList = new ArrayList<Object>();

		// 1.流程类型为采购资金（贸易）流程
		filter.append(" and F_WorkClassId = :workClassId");
		varsList.add(peManager.vwGetClassIdByClassName(ProcessXmlUtil.getProcessAttribute("id", "PurchaseFundTrade", "className")));

		// 2.流程申请日期在日期区间中
		// 开始时间
		if (null != conditionMap.get("startDate")) {
			filter.append(" and F_StartTime >= :startDate");
			varsList.add(conditionMap.get("startDate"));
		}
		// 结束时间
		if (null != conditionMap.get("endDate")) {
			filter.append(" and F_StartTime <= :endDate");
			varsList.add(DateUtil.addOneDay((Date) conditionMap.get("endDate")));
		}

		Object[] substitutionVars = varsList.toArray();
		filterMap.put("filter", filter.toString());
		filterMap.put("substitutionVars", substitutionVars);

		log.info("filter.toString:" + filter.toString());
		log.info("substitutionVars.toString():" + substitutionVars.toString());

		return filterMap;
	}

	/**
	 * 得到结束的采购资金（贸易）流程的流程实例ID（包括正常结束的和手动终止的）
	 * @param procPurchaseFundTrade
	 * @return
	 * @throws Exception
	 */
	public List<String> getDealedProcIds(Map<String, Object> conditionMap) throws Exception {
		// 查询条件组装
		log.info("得到结束的采购资金（贸易）流程的流程实例ID（包括正常结束的和手动终止的）");
		Map<String, Object> filterMap = this.getDealedFilterMapTrade(conditionMap);
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
		StringBuilder jpql = new StringBuilder(
				"select ppFT.procInstId from ProcPurchaseFundTrade ppFT join fetch ppFT.company where ppFT.defunctInd = 'N'");
		jpql.append(" and ppFT.procInstId in (?1)");
		String companyId = (String) conditionMap.get("companyId");
		if (!StringUtils.isBlankOrNull(companyId)) {
			jpql.append(" and ppFT.company.id = " + conditionMap.get("companyId"));
			resultList = entityService.find(jpql.toString(), procIds);
		} else {
			@SuppressWarnings("unchecked")
			List<String> companyIds = (List<String>) conditionMap.get("companyIds");
			if (companyIds != null && !companyIds.isEmpty()) {
				jpql.append(" and ppFT.company.id in (?2)");
				resultList = entityService.find(jpql.toString(), procIds, companyIds);
			}
		}
		return resultList;
	}

	/**
	 * 得到结束的采购资金（贸易）流程的过滤器（包括正常结束的和手动终止的）
	 * @param conditionMap
	 * @return
	 */
	private Map<String, Object> getDealedFilterMapTrade(Map<String, Object> conditionMap) {
		log.info("得到结束的采购资金（贸易）流程的过滤器（包括正常结束的和手动终止的）");
		Map<String, Object> filterMap = new HashMap<String, Object>();
		StringBuilder filter = new StringBuilder("1=1 and (F_EventType = :EventType or F_EventType = :EventTypeT)");
		List<Object> varsList = new ArrayList<Object>();
		varsList.add(ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal));
		varsList.add(ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ForcedToTerminate));

		// 1.流程类型为采购资金（贸易）流程
		filter.append(" and F_WorkClassId = :workClassId");
		varsList.add(peManager.vwGetClassIdByClassName(ProcessXmlUtil.getProcessAttribute("id", "PurchaseFundTrade", "className")));

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

		Object[] substitutionVars = varsList.toArray();
		filterMap.put("filter", filter.toString());
		filterMap.put("substitutionVars", substitutionVars);

		log.info("filter.toString:" + filter.toString());
		log.info("substitutionVars.toString():" + substitutionVars.toString());

		return filterMap;
	}

	/**
	 * 组装采购资金（贸易）流程VO显示数据
	 * @param procPurchaseFundTrade
	 * @return
	 * @throws Exception 
	 */
	public List<PurchaseFundVo> getPurFundTrade(Map<String, Object> conditionMap) throws Exception {
		log.info("组装采购资金（贸易）流程VO显示数据");
		List<PurchaseFundVo> pfVoList = new ArrayList<PurchaseFundVo>();

		// 获得活动的FN流程实例ID
		List<String> procIds = this.getProcIds(conditionMap);
		for (String s : procIds) {
			log.info("活动的FN流程实例ID:" + s);
		}
		StringBuilder jpql0 = new StringBuilder(
				"select ppFT.procInstId from ProcPurchaseFundTrade ppFT join fetch ppFT.company where ppFT.defunctInd = 'N' and ppFT.procInstId in (?1)");
		log.info("jpql0:" + jpql0);
		List<String> ppfTIDIng = entityService.find(jpql0.toString(), procIds);
		log.info("数据库中查询出的活动的FN流程的个数：" + ppfTIDIng.size());
		for (String s : ppfTIDIng) {
			log.info("数据库中查询出的活动的FN流程实例ID:" + s);
		}
		// 获得审批结束或者被终止掉的流程实例ID
		List<String> procIdsFilter = this.getDealedProcIds(conditionMap);
		StringBuilder jpql00 = new StringBuilder(
				"select ppFT.procInstId from ProcPurchaseFundTrade ppFT join fetch ppFT.company where ppFT.defunctInd = 'N' and ppFT.procInstId in (?1)");
		log.info("jpql00:" + jpql00);
		List<String> ppfTIDed = entityService.find(jpql00.toString(), procIdsFilter);
		log.info("数据库中审批结束或者被终止掉的流程的个数：" + ppfTIDed.size());
		for (String p : ppfTIDed) {
			log.info("数据库中审批结束或者被终止掉的流程实例ID：" + p);
		}

		// 组装信息
		log.info("正在审批过程中的流程的信息");
		pfVoList.addAll(this.getPFTrade(ppfTIDIng, ppfTIDed));
		log.info("查看集合的个数：" + pfVoList.size());

		log.info("已审批过的流程的信息");
		pfVoList.addAll(this.getPFTradeFilter(ppfTIDIng, ppfTIDed));
		log.info("查看集合的个数(最终)：" + pfVoList.size());
		return pfVoList;
	}

	/**
	 * 获得已累计采购数量(已经结束的从流程明细中找，未结束的从从流程中找)
	 * @param potcm
	 * @return
	 */
	public Double purchaseNum(ProdOrTradeCashMain potcm, List<String> ppfTIDIng, List<String> ppfTIDed) {
		Double amo = this.purchaseNumDetailIng(potcm, ppfTIDIng);
		Double am = this.purchaseNumDetailDeale(potcm, ppfTIDed);
		Double amount = am + amo;
		log.info("累计采购数量：" + amount);
		return amount;
	}

	/**
	 * 正在进行的流程:已累计采购数量
	 * @param potcm
	 * @param procIds
	 * @return
	 */
	public Double purchaseNumDetailIng(ProdOrTradeCashMain potcm, List<String> ppfTIDIng) {
		log.info("查询正在进行的流程:已累计采购数量");
		Double amount = 0D;
		if (ppfTIDIng.size() != 0) {
			StringBuilder jpql0 = new StringBuilder(
					"select ppFT from ProcPurchaseFundTrade ppFT join fetch ppFT.company where ppFT.defunctInd = 'N' and (ppFT.loanIden = 'L' or ppFT.loanIden = 'A') ");
			jpql0.append(" and (ppFT.varietyOne='" + potcm.getVariety() + "' or ppFT.varietyTwo='" + potcm.getVariety() + "' or ppFT.varietyThr='"
					+ potcm.getVariety() + "')");
			jpql0.append(" and ppFT.procInstId in (?1)");
			log.info("jpql0:" + jpql0);
			List<ProcPurchaseFundTrade> ppfTList = entityService.find(jpql0.toString(), ppfTIDIng);
			for (ProcPurchaseFundTrade ppft : ppfTList) {
				if (ppft.getVarietyOne() != null && ppft.getVarietyOne().equals(potcm.getVariety())) {
					amount += ppft.getVarietyOneNum();
				} else if (ppft.getVarietyTwo() != null && ppft.getVarietyTwo().equals(potcm.getVariety())) {
					amount += ppft.getVarietyTwoNum();
				} else if (ppft.getVarietyThr() != null && ppft.getVarietyThr().equals(potcm.getVariety())) {
					amount += ppft.getVarietyThrNum();
				}
			}
		}
		log.info("正在进行的流程:已累计采购数量：" + amount);
		return amount;
	}

	/**
	 * 已结束的流程:已累计采购数量
	 * @param potcm
	 * @param procIdsFilter
	 * @return
	 */
	public Double purchaseNumDetailDeale(ProdOrTradeCashMain potcm, List<String> ppfTIDed) {
		log.info("查询已结束的流程:已累计采购数量");
		Double amount = 0D;
		if (ppfTIDed.size() != 0) {
			StringBuilder jpql0 = new StringBuilder(
					"select ppFT.id from ProcPurchaseFundTrade ppFT join fetch ppFT.company where ppFT.defunctInd = 'N' and (ppFT.loanIden = 'L' or ppFT.loanIden = 'A') ");
			jpql0.append(" and (ppFT.varietyOne='" + potcm.getVariety() + "' or ppFT.varietyTwo='" + potcm.getVariety() + "' or ppFT.varietyThr='"
					+ potcm.getVariety() + "')");
			jpql0.append(" and ppFT.procInstId in (?1)");
			log.info("jpql0:" + jpql0);
			List<Long> ppfIdTList = entityService.find(jpql0.toString(), ppfTIDed);
			log.info("ppfIdTList.size:" + ppfIdTList.size());
			if (ppfIdTList.size() != 0) {
				StringBuilder jpql1 = new StringBuilder(
						"select ppFTD from ProcPurchaseFundTradeDetail ppFTD join fetch ppFTD.procPurchaseFundTrade where ppFTD.defunctInd = 'N' and ppFTD.procPurchaseFundTrade.id in (?1)");
				log.info("jqpl1:" + jpql1);
				List<ProcPurchaseFundTradeDetail> ppFTDTList = entityService.find(jpql1.toString(), ppfIdTList);
				for (ProcPurchaseFundTradeDetail ppftDetail : ppFTDTList) {
					if (ppftDetail.getVarietyOne() != null && potcm.getVariety().equals(ppftDetail.getVarietyOne())) {
						amount += ppftDetail.getVarietyOneNum();
					} else if (ppftDetail.getVarietyTwo() != null && potcm.getVariety().equals(ppftDetail.getVarietyTwo())) {
						amount += ppftDetail.getVarietyTwoNum();
					} else if (ppftDetail.getVarietyThr() != null && potcm.getVariety().equals(ppftDetail.getVarietyThr())) {
						amount += ppftDetail.getVarietyThrNum();
					}
				}
			}
		}
		log.info("已结束的流程:已累计采购数量：" + amount);
		return amount;
	}

	/**
	 * 获得已累计采购金额
	 * @param potcm
	 * @return
	 */
	public Double purchaseAmount(ProdOrTradeCashMain potcm, List<String> ppfTIDIng, List<String> ppfTIDed) {
		Double amo = this.purchaseAmountDetailIng(potcm, ppfTIDIng);
		Double am = this.purchaseAmountDetailDeale(potcm, ppfTIDed);
		Double amount = am + amo;
		log.info("累计采购金额：" + amount);
		return amount;
	}

	/**
	 * 正在进行的流程:已累计采购金额
	 * @param potcm
	 * @param procIds
	 * @return
	 */
	public Double purchaseAmountDetailIng(ProdOrTradeCashMain potcm, List<String> ppfTIDIng) {
		log.info("查询正在进行的流程:已累计采购金额");
		Double amount = 0D;
		if (ppfTIDIng.size() != 0) {
			StringBuilder jpql0 = new StringBuilder(
					"select ppFT from ProcPurchaseFundTrade ppFT join fetch ppFT.company where ppFT.defunctInd = 'N' and (ppFT.loanIden = 'L' or ppFT.loanIden = 'A') ");
			jpql0.append(" and (ppFT.varietyOne='" + potcm.getVariety() + "' or ppFT.varietyTwo='" + potcm.getVariety() + "' or ppFT.varietyThr='"
					+ potcm.getVariety() + "')");
			jpql0.append(" and ppFT.procInstId in (?1)");
			log.info("jpql0:" + jpql0);
			List<ProcPurchaseFundTrade> ppfTList = entityService.find(jpql0.toString(), ppfTIDIng);
			for (ProcPurchaseFundTrade ppft : ppfTList) {
				if (ppft.getVarietyOne() != null && ppft.getVarietyOne().equals(potcm.getVariety())) {
					amount += ppft.getVarietyOneAmount();
				} else if (ppft.getVarietyTwo() != null && ppft.getVarietyTwo().equals(potcm.getVariety())) {
					amount += ppft.getVarietyTwoAmount();
				} else if (ppft.getVarietyThr() != null && ppft.getVarietyThr().equals(potcm.getVariety())) {
					amount += ppft.getVarietyThrAmount();
				}
			}
		}
		log.info("正在进行的流程:已累计采购金额：" + amount);
		return amount;
	}

	/**
	 * 已结束的流程:已累计采购金额
	 * @param potcm
	 * @param procIdsFilter
	 * @return
	 */
	public Double purchaseAmountDetailDeale(ProdOrTradeCashMain potcm, List<String> ppfTIDed) {
		log.info("查询已结束的流程:已累计采购金额");
		Double amount = 0D;
		if (ppfTIDed.size() != 0) {
			StringBuilder jpql0 = new StringBuilder(
					"select ppFT.id from ProcPurchaseFundTrade ppFT join fetch ppFT.company where ppFT.defunctInd = 'N' and (ppFT.loanIden = 'L' or ppFT.loanIden = 'A') ");
			jpql0.append(" and (ppFT.varietyOne='" + potcm.getVariety() + "' or ppFT.varietyTwo='" + potcm.getVariety() + "' or ppFT.varietyThr='"
					+ potcm.getVariety() + "')");
			jpql0.append(" and ppFT.procInstId in (?1)");
			log.info("jpql0:" + jpql0);
			List<Long> ppfIdTList = entityService.find(jpql0.toString(), ppfTIDed);
			log.info("ppfIdTList.size:" + ppfIdTList.size());
			if (ppfIdTList.size() != 0) {
				StringBuilder jpql1 = new StringBuilder(
						"select ppFTD from ProcPurchaseFundTradeDetail ppFTD join fetch ppFTD.procPurchaseFundTrade where ppFTD.defunctInd = 'N' and ppFTD.procPurchaseFundTrade.id in (?1)");
				log.info("jqpl1:" + jpql1);
				List<ProcPurchaseFundTradeDetail> ppFTDTList = entityService.find(jpql1.toString(), ppfIdTList);
				for (ProcPurchaseFundTradeDetail ppftDetail : ppFTDTList) {
					if (ppftDetail.getVarietyOne() != null && potcm.getVariety().equals(ppftDetail.getVarietyOne())) {
						amount += ppftDetail.getVarietyOneAmount();
					} else if (ppftDetail.getVarietyTwo() != null && potcm.getVariety().equals(ppftDetail.getVarietyTwo())) {
						amount += ppftDetail.getVarietyTwoAmount();
					} else if (ppftDetail.getVarietyThr() != null && potcm.getVariety().equals(ppftDetail.getVarietyThr())) {
						amount += ppftDetail.getVarietyThrAmount();
					}
				}
			}
		}
		log.info("已结束的流程:已累计采购金额：" + amount);
		return amount;
	}

	/**
	 * 获得自有资金/集团提供资金
	 * @param potcm
	 * @return
	 */
	public Double getOorPAmount(String loan, ProdOrTradeCashMain potcm, List<String> ppfTIDIng, List<String> ppfTIDed) {
		Double amo = this.getOrgOwnerTradeIng(loan, ppfTIDIng, potcm);
		Double am = this.getOrgOwnerTradeed(loan, ppfTIDed, potcm);
		Double amount = am + amo;
		log.info("累计采购金额：" + amount);
		return amount;
	}

	/**
	 * 获得自有资金/集团提供资金（正在进行的流程）
	 * @param procPurchaseFundTrade
	 * @param potcm
	 * @return
	 */
	public Double getOrgOwnerTradeIng(String loan, List<String> ppfTIDIng, ProdOrTradeCashMain potcm) {
		Double amount = 0D;
		StringBuilder jpql0 = new StringBuilder("select ppFT from ProcPurchaseFundTrade ppFT join fetch ppFT.company where ppFT.defunctInd = 'N'");
		jpql0.append(" and (ppFT.varietyOne='" + potcm.getVariety() + "' or ppFT.varietyTwo='" + potcm.getVariety() + "' or ppFT.varietyThr='"
				+ potcm.getVariety() + "')");
		if ("T".equals(loan)) {
			jpql0.append(" and ppFT.loanIden='" + loan + "'");
		} else {
			String t = "T";
			jpql0.append(" and ppFT.loanIden <> '" + t + "'");
		}
		jpql0.append(" and ppFT.procInstId in (?1)");
		log.info("jpql0:" + jpql0);
		List<ProcPurchaseFundTrade> ppFTTList = entityService.find(jpql0.toString(), ppfTIDIng);
		log.info("ppFTTList:" + ppFTTList.size());
		for (ProcPurchaseFundTrade ppft : ppFTTList) {
			if (ppft.getVarietyOne() != null && ppft.getVarietyOne().equals(potcm.getVariety())) {
				amount += ppft.getVarietyOneAmount();
			} else if (ppft.getVarietyTwo() != null && ppft.getVarietyTwo().equals(potcm.getVariety())) {
				amount += ppft.getVarietyTwoAmount();
			} else if (ppft.getVarietyThr() != null && ppft.getVarietyThr().equals(potcm.getVariety())) {
				amount += ppft.getVarietyThrAmount();
			}
		}
		return amount;
	}

	/**
	 * 获得自有资金/集团提供资金（审批结束或手动终止的流程）
	 * @param procPurchaseFundTrade
	 * @param potcm
	 * @return
	 */
	public Double getOrgOwnerTradeed(String loan, List<String> ppfTIDed, ProdOrTradeCashMain potcm) {
		Double amount = 0D;
		StringBuilder jpql0 = new StringBuilder("select ppFT.id from ProcPurchaseFundTrade ppFT join fetch ppFT.company where ppFT.defunctInd = 'N'");
		jpql0.append(" and (ppFT.varietyOne='" + potcm.getVariety() + "' or ppFT.varietyTwo='" + potcm.getVariety() + "' or ppFT.varietyThr='"
				+ potcm.getVariety() + "')");
		if ("T".equals(loan)) {
			jpql0.append(" and ppFT.loanIden='" + loan + "'");
		} else {
			String t = "T";
			jpql0.append(" and ppFT.loanIden <>'" + t + "'");
		}
		jpql0.append(" and ppFT.procInstId in (?1)");
		log.info("jpql0:" + jpql0);
		List<Long> ppFTIdList = entityService.find(jpql0.toString(), ppfTIDed);
		log.info("ppFTIdList:" + ppFTIdList.size());
		StringBuilder jpql1 = new StringBuilder(
				"select ppFTD from ProcPurchaseFundTradeDetail ppFTD join fetch ppFTD.procPurchaseFundTrade where ppFTD.defunctInd = 'N' ");
		jpql1.append(" and ppFTD.procPurchaseFundTrade.id in(?1)");
		log.info("jqpl1:" + jpql1);
		List<ProcPurchaseFundTradeDetail> ppFTDList = entityService.find(jpql1.toString(), ppFTIdList);
		log.info("ppFTDList:" + ppFTDList.size());
		for (ProcPurchaseFundTradeDetail ppFTD : ppFTDList) {
			if (ppFTD.getVarietyOne() != null && ppFTD.getVarietyOne().equals(potcm.getVariety())) {
				amount += ppFTD.getVarietyOneAmount();
			} else if (ppFTD.getVarietyTwo() != null && ppFTD.getVarietyTwo().equals(potcm.getVariety())) {
				amount += ppFTD.getVarietyTwoAmount();
			} else if (ppFTD.getVarietyThr() != null && ppFTD.getVarietyThr().equals(potcm.getVariety())) {
				amount += ppFTD.getVarietyThrAmount();
			}
		}
		return amount;
	}

	// 获得已审批过的流程的信息
	public List<PurchaseFundVo> getPFTradeFilter(List<String> ppfTIDIng, List<String> ppfTIDed) {
		List<PurchaseFundVo> pfVoList = new ArrayList<PurchaseFundVo>();
		log.info("已审批过的(贸易)流程的数量：" + ppfTIDed.size());
		if (ppfTIDed.size() != 0) {
			for (String s : ppfTIDed) {
				StringBuilder jpql0 = new StringBuilder(
						"select ppFT from ProcPurchaseFundTrade ppFT join fetch ppFT.company where ppFT.defunctInd = 'N' ");
				jpql0.append(" and ppFT.procInstId='" + s + "'");
				log.info("jpql0:" + jpql0);
				ProcPurchaseFundTrade ppft = entityService.findUnique(jpql0.toString());
				if (ppft.getVarietyOne() != null) {
					// 获得Main中的主数据(公司、品种、类型、期间（锁定申请时用到的那条数据）)
					log.info("第一组品种！！！");
					StringBuilder jpql1 = new StringBuilder(
							"select pfcm from ProdOrTradeCashMain pfcm join fetch pfcm.company where pfcm.defunctInd='N'");
					jpql1.append(" and pfcm.company.id =" + ppft.getCompany().getId());
					// 贸易
					jpql1.append(" and pfcm.appType = 'T'");
					// 品种1
					jpql1.append(" and pfcm.variety = '" + ppft.getVarietyOne() + "'");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					jpql1.append(" and pfcm.startDate <='" + sdf.format(ppft.getCreatedDatetime()) + "'");
					jpql1.append(" and pfcm.endDate >='" + sdf.format(ppft.getCreatedDatetime()) + "'");
					log.info("jpql10:" + jpql1);
					List<ProdOrTradeCashMain> potcmList = entityService.find(jpql1.toString());
					if (potcmList.size() == 0) {
						log.info("采购资金（贸易）流程中品种的信息在生产或贸易总头寸主数据表中已不存在");
					}
					PurchaseFundVo pfVo = this.fillVo(ppft, potcmList.get(0), ppfTIDIng, ppfTIDed);
					// *******************************************
					// 已累计采购数量
					pfVo.setAllPurchaseNum(potcmList.get(0).getTotalCash() - ppft.getVarietyOneRemain());
					// 剩余采购头寸
					pfVo.setLessPurchaseNum(ppft.getVarietyOneRemain());
					// *******************************************
					// 本次采购数量(吨)
					pfVo.setThisPurchaseNum(ppft.getVarietyOneNum());
					// 本次采购金额（万）
					pfVo.setThisPurchaseAmount(ppft.getVarietyOneAmount());
					// 采购平均单价（元）
					pfVo.setPurchaseAverage(this.format((ppft.getVarietyOneAmount() * 10000) / ppft.getVarietyOneNum()));
					pfVoList.add(pfVo);
				}
				if (ppft.getVarietyTwo() != null) {
					// 获得Main中的主数据(公司、品种、类型、期间（锁定申请时用到的那条数据）)
					log.info("第二组品种！！！");
					StringBuilder jpql2 = new StringBuilder(
							"select pfcm from ProdOrTradeCashMain pfcm join fetch pfcm.company where pfcm.defunctInd='N'");
					jpql2.append(" and pfcm.company.id =" + ppft.getCompany().getId());
					// 贸易
					jpql2.append(" and pfcm.appType = 'T'");
					// 品种2
					jpql2.append(" and pfcm.variety = '" + ppft.getVarietyTwo() + "'");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					jpql2.append(" and pfcm.startDate <='" + sdf.format(ppft.getCreatedDatetime()) + "'");
					jpql2.append(" and pfcm.endDate >='" + sdf.format(ppft.getCreatedDatetime()) + "'");
					log.info("jpql11:" + jpql2);
					List<ProdOrTradeCashMain> potcmList = entityService.find(jpql2.toString());
					if (potcmList.size() == 0) {
						log.info("采购资金（贸易）流程中品种的信息在生产或贸易总头寸主数据表中已不存在");
					}
					PurchaseFundVo pfVo = this.fillVo(ppft, potcmList.get(0), ppfTIDIng, ppfTIDed);
					// *******************************************
					// 已累计采购数量
					pfVo.setAllPurchaseNum(potcmList.get(0).getTotalCash() - ppft.getVarietyTwoRemain());
					// 剩余采购头寸
					pfVo.setLessPurchaseNum(ppft.getVarietyTwoRemain());
					// *******************************************
					// 本次采购数量(吨)
					pfVo.setThisPurchaseNum(ppft.getVarietyTwoNum());
					// 本次采购金额（万）
					pfVo.setThisPurchaseAmount(ppft.getVarietyTwoAmount());
					// 采购平均单价（元）
					pfVo.setPurchaseAverage(this.format((ppft.getVarietyTwoAmount() * 10000) / ppft.getVarietyTwoNum()));
					pfVoList.add(pfVo);
				}
				if (ppft.getVarietyThr() != null) {
					// 获得Main中的主数据(公司、品种、类型、期间（锁定申请时用到的那条数据）)
					log.info("第三组品种！！！");
					StringBuilder jpql3 = new StringBuilder(
							"select pfcm from ProdOrTradeCashMain pfcm join fetch pfcm.company where pfcm.defunctInd='N'");
					jpql3.append(" and pfcm.company.id =" + ppft.getCompany().getId());
					// 贸易
					jpql3.append(" and pfcm.appType = 'T'");
					// 品种有三种，需要三次查询
					jpql3.append(" and pfcm.variety = '" + ppft.getVarietyThr() + "'");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					jpql3.append(" and pfcm.startDate <='" + sdf.format(ppft.getCreatedDatetime()) + "'");
					jpql3.append(" and pfcm.endDate >='" + sdf.format(ppft.getCreatedDatetime()) + "'");
					log.info("jpql12:" + jpql3);
					List<ProdOrTradeCashMain> potcmList = entityService.find(jpql3.toString());
					if (potcmList.size() == 0) {
						log.info("采购资金（贸易）流程中品种的信息在生产或贸易总头寸主数据表中已不存在");
					}
					PurchaseFundVo pfVo = this.fillVo(ppft, potcmList.get(0), ppfTIDIng, ppfTIDed);
					// *******************************************
					// 已累计采购数量
					pfVo.setAllPurchaseNum(potcmList.get(0).getTotalCash() - ppft.getVarietyThrRemain());
					// 剩余采购头寸
					pfVo.setLessPurchaseNum(ppft.getVarietyThrRemain());
					// *******************************************
					// 本次采购数量(吨)
					pfVo.setThisPurchaseNum(ppft.getVarietyThrNum());
					// 本次采购金额（万）
					pfVo.setThisPurchaseAmount(ppft.getVarietyThrAmount());
					// 采购平均单价（元）
					pfVo.setPurchaseAverage(this.format((ppft.getVarietyThrAmount() * 10000) / ppft.getVarietyThrNum()));
					pfVoList.add(pfVo);
				}
			}
		}
		return pfVoList;
	}

	/**
	 * 正在审批过程中的流程的处理 
	 * @param procPurchaseFundTrade
	 * @return
	 * @throws Exception
	 */
	public List<PurchaseFundVo> getPFTrade(List<String> ppfTIDIng, List<String> ppfTIDed) throws Exception {
		List<PurchaseFundVo> pfVoList = new ArrayList<PurchaseFundVo>();
		log.info("正在审批过程的(贸易)流程的数量：" + ppfTIDIng.size());
		if (ppfTIDIng.size() != 0) {
			for (String s : ppfTIDIng) {
				StringBuilder jpql0 = new StringBuilder(
						"select ppFT from ProcPurchaseFundTrade ppFT join fetch ppFT.company where ppFT.defunctInd = 'N' ");
				jpql0.append(" and ppFT.procInstId='" + s + "'");
				log.info("jpql0:" + jpql0);
				ProcPurchaseFundTrade ppft = entityService.findUnique(jpql0.toString());
				if (ppft.getVarietyOne() != null) {
					// 获得Main中的主数据(公司、品种、类型、期间（锁定申请时用到的那条数据）)
					log.info("第一组品种！！！");
					StringBuilder jpql1 = new StringBuilder(
							"select pfcm from ProdOrTradeCashMain pfcm join fetch pfcm.company where pfcm.defunctInd='N'");
					jpql1.append(" and pfcm.company.id =" + ppft.getCompany().getId());
					// 贸易
					jpql1.append(" and pfcm.appType = 'T'");
					// 品种1
					jpql1.append(" and pfcm.variety = '" + ppft.getVarietyOne() + "'");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					jpql1.append(" and pfcm.startDate <='" + sdf.format(ppft.getCreatedDatetime()) + "'");
					jpql1.append(" and pfcm.endDate >='" + sdf.format(ppft.getCreatedDatetime()) + "'");
					log.info("jpql10:" + jpql1);
					List<ProdOrTradeCashMain> potcmList = entityService.find(jpql1.toString());
					if (potcmList.size() == 0) {
						log.info("采购资金（贸易）流程中品种的信息在生产或贸易总头寸主数据表中已不存在");
					}
					PurchaseFundVo pfVo = this.fillVo(ppft, potcmList.get(0), ppfTIDIng, ppfTIDed);
					// *******************************************
					// 已累计采购数量
					pfVo.setAllPurchaseNum(potcmList.get(0).getTotalCash() - ppft.getVarietyOneRemain());
					// 剩余采购头寸
					pfVo.setLessPurchaseNum(ppft.getVarietyOneRemain());
					// *******************************************
					// 本次采购数量(吨)
					pfVo.setThisPurchaseNum(ppft.getVarietyOneNum());
					// 本次采购金额（万）
					pfVo.setThisPurchaseAmount(ppft.getVarietyOneAmount());
					// 采购平均单价（元）
					pfVo.setPurchaseAverage(this.format((ppft.getVarietyOneAmount() * 10000) / ppft.getVarietyOneNum()));
					pfVoList.add(pfVo);
				}
				if (ppft.getVarietyTwo() != null) {
					// 获得Main中的主数据(公司、品种、类型、期间（锁定申请时用到的那条数据）)
					log.info("第二组品种！！！");
					StringBuilder jpql2 = new StringBuilder(
							"select pfcm from ProdOrTradeCashMain pfcm join fetch pfcm.company where pfcm.defunctInd='N'");
					jpql2.append(" and pfcm.company.id =" + ppft.getCompany().getId());
					// 贸易
					jpql2.append(" and pfcm.appType = 'T'");
					// 品种2
					jpql2.append(" and pfcm.variety = '" + ppft.getVarietyTwo() + "'");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					jpql2.append(" and pfcm.startDate <='" + sdf.format(ppft.getCreatedDatetime()) + "'");
					jpql2.append(" and pfcm.endDate >='" + sdf.format(ppft.getCreatedDatetime()) + "'");
					log.info("jpql11:" + jpql2);
					List<ProdOrTradeCashMain> potcmList = entityService.find(jpql2.toString());
					if (potcmList.size() == 0) {
						log.info("采购资金（贸易）流程中品种的信息在生产或贸易总头寸主数据表中已不存在");
					}
					PurchaseFundVo pfVo = this.fillVo(ppft, potcmList.get(0), ppfTIDIng, ppfTIDed);
					// *******************************************
					// 已累计采购数量
					pfVo.setAllPurchaseNum(potcmList.get(0).getTotalCash() - ppft.getVarietyTwoRemain());
					// 剩余采购头寸
					pfVo.setLessPurchaseNum(ppft.getVarietyTwoRemain());
					// *******************************************
					// 本次采购数量(吨)
					pfVo.setThisPurchaseNum(ppft.getVarietyTwoNum());
					// 本次采购金额（万）
					pfVo.setThisPurchaseAmount(ppft.getVarietyTwoAmount());
					// 采购平均单价（元）
					pfVo.setPurchaseAverage(this.format((ppft.getVarietyTwoAmount() * 10000) / ppft.getVarietyTwoNum()));
					pfVoList.add(pfVo);
				}
				if (ppft.getVarietyThr() != null) {
					// 获得Main中的主数据(公司、品种、类型、期间（锁定申请时用到的那条数据）)
					log.info("第三组品种！！！");
					StringBuilder jpql3 = new StringBuilder(
							"select pfcm from ProdOrTradeCashMain pfcm join fetch pfcm.company where pfcm.defunctInd='N'");
					jpql3.append(" and pfcm.company.id =" + ppft.getCompany().getId());
					// 贸易
					jpql3.append(" and pfcm.appType = 'T'");
					// 品种有三种，需要三次查询
					jpql3.append(" and pfcm.variety = '" + ppft.getVarietyThr() + "'");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					jpql3.append(" and pfcm.startDate <='" + sdf.format(ppft.getCreatedDatetime()) + "'");
					jpql3.append(" and pfcm.endDate >='" + sdf.format(ppft.getCreatedDatetime()) + "'");
					log.info("jpql12:" + jpql3);
					List<ProdOrTradeCashMain> potcmList = entityService.find(jpql3.toString());
					if (potcmList.size() == 0) {
						log.info("采购资金（贸易）流程中品种的信息在生产或贸易总头寸主数据表中已不存在");
					}
					PurchaseFundVo pfVo = this.fillVo(ppft, potcmList.get(0), ppfTIDIng, ppfTIDed);
					// *******************************************
					// 已累计采购数量
					pfVo.setAllPurchaseNum(potcmList.get(0).getTotalCash() - ppft.getVarietyThrRemain());
					// 剩余采购头寸
					pfVo.setLessPurchaseNum(ppft.getVarietyThrRemain());
					// *******************************************
					// 本次采购数量(吨)
					pfVo.setThisPurchaseNum(ppft.getVarietyThrNum());
					// 本次采购金额（万）
					pfVo.setThisPurchaseAmount(ppft.getVarietyThrAmount());
					// 采购平均单价（元）
					pfVo.setPurchaseAverage(this.format((ppft.getVarietyThrAmount() * 10000) / ppft.getVarietyThrNum()));
					pfVoList.add(pfVo);
				}
			}
		}
		return pfVoList;
	}

	/**
	 * 自动填充总头寸数据
	 * @param procPurchaseFundTrade
	 * @param potcm
	 * @return
	 */
	public PurchaseFundVo fillVo(ProcPurchaseFundTrade procPurchaseFundTrade, ProdOrTradeCashMain potcm, List<String> ppfTIDIng, List<String> ppfTIDed) {
		PurchaseFundVo pfVo = new PurchaseFundVo();
		// 付款时间
		pfVo.setPayDate(procPurchaseFundTrade.getPaymentDate());
		// 流程实例编号
		pfVo.setProcInstId(processUtilMapService.getTmsIdByFnId(procPurchaseFundTrade.getProcInstId()));
		// 公司名称
		pfVo.setCompanyId(potcm.getCompany().getId());
		// 品种
		pfVo.setVarietyId(potcm.getVariety());
		// 类型(贸易时)
		pfVo.setType(procPurchaseFundTrade.getPurchaseType());
		// 审批总头寸（吨）
		pfVo.setTotalCash(potcm.getTotalCash());
		// 总金额（万元）
		pfVo.setTotalCashAmount(potcm.getTotalCashAmount());
		// 均价（元）
		pfVo.setAmountAverage(this.format(potcm.getTotalCashAmount() * 10000) / potcm.getTotalCash());
		// 审批人（根据更新人员获得）
		pfVo.setUpdatedBy(potcm.getUpdatedBy());
		// 起始日期
		pfVo.setStartDate(potcm.getStartDate());
		// 结束日期
		pfVo.setEndDate(potcm.getEndDate());
		// 已累计采购金额
		pfVo.setAllPurchaseAmount(this.purchaseAmount(potcm, ppfTIDIng, ppfTIDed));
		if ("T".equals(procPurchaseFundTrade.getLoanIden())) {
			// 转款时是自有资金
			log.info("转款");
			// 集团提供资金
			pfVo.setGroPrivader(0.00);
			// 转款
			String loan = procPurchaseFundTrade.getLoanIden();
			if (ppfTIDIng.contains(procPurchaseFundTrade.getProcInstId())) {
				log.info("正在进行的流程：获得自有资金");
				// 自有资金
				pfVo.setOrgOwner(this.getOorPAmount(loan, potcm, ppfTIDIng, ppfTIDed));
				log.info("自有资金:" + pfVo.getOrgOwner());
			} else {
				log.info("结束的流程：获得自有资金");
				// 自有资金
				pfVo.setOrgOwner(this.getOorPAmount(loan, potcm, ppfTIDIng, ppfTIDed));
			}
		} else {
			// 借款、借款+转款时是集团提供资金
		    // 非转款
			String loan = procPurchaseFundTrade.getLoanIden();
			if (ppfTIDIng.contains(procPurchaseFundTrade.getProcInstId())) {
				log.info("正在进行的流程：获得集团提供资金");
				// 集团提供资金
				pfVo.setGroPrivader(this.getOorPAmount(loan, potcm, ppfTIDIng, ppfTIDed));
				log.info("集团提供资金:" + pfVo.getGroPrivader());
			} else {
				log.info("结束的流程：获得集团提供资金");
				// 集团提供资金
				pfVo.setGroPrivader(this.getOorPAmount(loan, potcm, ppfTIDIng, ppfTIDed));
				log.info("集团提供资金:" + pfVo.getGroPrivader());
			}
			// 自有资金
			pfVo.setOrgOwner(0.00);
		}
		// 金额
		pfVo.setLessPurchaseAmount(pfVo.getTotalCashAmount() - pfVo.getAllPurchaseAmount());
		return pfVo;
	}
}
