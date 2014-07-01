package com.wcs.tms.service.report.cashpool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.LoginService;
import com.wcs.tms.view.report.cashpool.vo.CashCompanyItemVo;
import com.wcs.tms.view.report.cashpool.vo.CashCompanyVo;
import com.wcs.tms.view.report.cashpool.vo.CashPoolColumnVo;
import com.wcs.tms.view.report.cashpool.vo.CashPoolCompanyVo;
import com.wcs.tms.view.report.cashpool.vo.CashPoolItemVo;
import com.wcs.tms.view.report.cashpool.vo.CashPoolPageModelObj;
import com.wcs.tms.view.report.cashpool.vo.CashPoolRcfkVo;
import com.wcs.tms.view.report.cashpool.vo.CashPoolZjmyscVo;

@Stateless
public class CashPoolSummaryReportService implements Serializable {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(getClass());
	@PersistenceContext(unitName = "pu")
	private EntityManager em;
	@Inject
	private LoginService loginService;

	/**
	 * 获取按公司对比的数据
	 * @param beginDate：开始时间
	 * @param endDate：结束时间
	 * @param first
	 * @param pageSize
	 * @param filters
	 * @return
	 * @Author: LiWei
	 * @CreatedTime: 2013-12-18 下午1:29:08
	 * @UpdatedBy:
	 * @UpdatedTime:
	 */
	public CashPoolPageModelObj loadByCompany(List<String> cpIds, Date beginDate, Date endDate, int first, int pageSize, Map<String, String> filters) {
		int paramIndex = 1;
		Map<Integer, Object> paramVals = new HashMap<Integer, Object>();
		StringBuffer scWhereSql = new StringBuffer("where 1=1 ");
		if (cpIds != null && cpIds.size() > 0) {
			scWhereSql.append("and sc.cpid in (" + this.appendIds(cpIds, false) + ") ");
		} else {
			return null;
		}
		// 拼接时间限制
		StringBuffer subWhereSql = new StringBuffer(" where 1=1 ");
		StringBuffer avSubWhereSql = new StringBuffer(" where 1=1 ");
		if (beginDate != null) {
			subWhereSql.append("and sc.PAYDATE >=?" + paramIndex + " ");
			paramVals.put(paramIndex, beginDate);
			paramIndex++;
			avSubWhereSql.append("and t1.AVTIME >=?" + paramIndex + " ");
			paramVals.put(paramIndex, beginDate);
			paramIndex++;
		}
		if (endDate != null) {
			subWhereSql.append("and sc.PAYDATE <=?" + paramIndex + " ");
			paramVals.put(paramIndex, endDate);
			paramIndex++;
			avSubWhereSql.append("and t1.AVTIME <=?" + paramIndex + " ");
			paramVals.put(paramIndex, endDate);
			paramIndex++;
		}
		// 组合查询结果为临时表with as
		StringBuffer withAsSql = new StringBuffer();

		withAsSql.append(" with ")
				// 得到流程的申请金额和实际支付
				.append(" wa_stat_cpByProc(procType,cpId,cpName,procId,amount,zf,sjxb) as(")
				.append(" select procType,cpId,cpName,procId,amount,sum(zf),sum(sjxb) from vw_stat_company sc ")
				.append(subWhereSql)
				.append(" and sc.terminateFlag='N'")
				.append(" group by procType,cpId,cpName,procId,amount ")
				// 将申请金额和实际下拨按照公司汇总
				.append(" ),wa_stat_cpByCp(cpId,cpName,amount,zf,sjxb)as(  ")
				.append("       select sc.cpid,sc.cpName,sum(sc.amount),sum(zf),sum(sc.sjxb) from wa_stat_cpByProc sc group by sc.cpid,sc.cpName ")
				// 获取可用余额月公司的对照
				.append(" ),wa_stat_cpAvAmount(cpId,avAmount,account) as( ")
				.append("    select t1.cpId,t1.avAmount,t1.account from vw_stat_cpAvAmount t1,(select account,max(avTime) as maxTime from vw_stat_cpAvAmount sca group by account) t2  ")
				.append(avSubWhereSql)
				.append(" and t1.account=t2.account and t1.avTime=t2.maxTime")
				// 将余额和实际下拨及其申请金额按照按照公司汇总
				.append(" ),wa_stat_company(cpId,cpName,sumAmount,sumSjxb,sumAvAmount) as (")
				.append("   select 0 ,'总计',sum(t.amount),sum(t.sjxb),sum(t.AVAMOUNT) from " +
						"(select max(sc.amount) as amount,max(sc.sjxb) as sjxb,sum(sca.AVAMOUNT) as AVAMOUNT from wa_stat_cpByCp sc left join wa_stat_cpAvAmount sca on(sca.CPID=sc.CPID)  ")
				.append(scWhereSql)
				.append("   group by sc.cpId) t union ")
				.append(" select sc.cpid,sc.cpName,max(sc.amount),max(sc.sjxb),sum(sca.AVAMOUNT) from wa_stat_cpByCp sc left join wa_stat_cpAvAmount sca on(sca.CPID=sc.CPID) ")
				.append(scWhereSql)
				.append(" group by sc.cpid,sc.cpName  ")
				.append(" )");
		// 查询
		String selectSql = withAsSql + "select cpId,cpName,sumAmount,sumSjxb,sumAvAmount from wa_stat_company order by cpId ";
		String countSql = withAsSql + "select count(*) from wa_stat_company ";
		log.info("selectSql:" + selectSql);
		log.info("countSql:" + countSql);
		Query query = em.createNativeQuery(selectSql, CashPoolCompanyVo.class);
		Query queryCount = em.createNativeQuery(countSql.toString());
		// 设置参数
		Set<Entry<Integer, Object>> entrySet = paramVals.entrySet();
		for (Entry<Integer, Object> entry : entrySet) {
			query.setParameter(entry.getKey(), entry.getValue());
			queryCount.setParameter(entry.getKey(), entry.getValue());
		}
		query.setFirstResult(first);
		query.setMaxResults(pageSize);
		// 返回结果
		List<CashPoolCompanyVo> rtList = query.getResultList();
		Integer count = (Integer) queryCount.getSingleResult();
		CashPoolPageModelObj obj = new CashPoolPageModelObj();
		obj.setRtList(rtList);
		obj.setCount(count);
		return obj;
	}

	/**
	 * 获取按品项对比的数据
	 * @param beginDate：开始时间
	 * @param endDate：结束时间
	 * @param first
	 * @param pageSize
	 * @param filters
	 * @return
	 * @Author: LiWei
	 * @CreatedTime: 2013-12-18 下午2:32:09
	 * @UpdatedBy:
	 * @UpdatedTime:
	 */
	public CashPoolPageModelObj loadByItem(List<String> cpIds, List<String> itemIds, Date beginDate, Date endDate, int first, int pageSize, Map<String, String> filters) {
		int paramIndex = 1;
		Map<Integer, Object> paramVals = new HashMap<Integer, Object>();
		// 拼接时间限制
		StringBuffer subWhereSql = new StringBuffer(" where 1=1 ");
		if (cpIds != null && cpIds.size() > 0) {
			subWhereSql.append("and sp.cpid in (" + this.appendIds(cpIds, false) + ") ");
		} else {
			return null;
		}
		if (itemIds != null && itemIds.size() > 0) {
			subWhereSql.append("and sp.procType || '_' || sp.pxKey in(" + this.appendIds(itemIds, true) + ") ");
		} else {
			return null;
		}
		if (beginDate != null) {
			subWhereSql.append("and sp.PAYDATE >=?" + paramIndex + " ");
			paramVals.put(paramIndex, beginDate);
			paramIndex++;
		}
		if (endDate != null) {
			subWhereSql.append("and sp.PAYDATE <=?" + paramIndex + " ");
			paramVals.put(paramIndex, endDate);
			paramIndex++;
		}
		// 组合查询结果为临时表with as
		StringBuffer withAsSql = new StringBuffer();
		withAsSql.append(" with wa_stat_pxByAmountId(procType,pxKey,pxName,amountId,amount,sjxb) as( ")
				.append(" select procType,pxKey,pxName,amountId,amount,sum(sjxb) from vw_stat_px sp ")
				.append(subWhereSql)
				.append(" and sp.terminateFlag='N'")
				.append(" group by procType,pxKey,pxName,amountId,amount ")
				.append(" ),wa_stat_px(pxKey,pxName,sumAmount,sumSjxb) as(  ")
				.append(" select '0_','总计', sum(amount),sum(sjxb)  from wa_stat_pxByAmountId  ")
				.append(" union ")
				.append(" select case when (procType='zjmy'or procType='zjsc') then 'zjmysc_' || pxKey  else procType || '_' || pxKey end")
				.append(",pxName,sum(amount),sum(sjxb)  from wa_stat_pxByAmountId group by pxName, ")
				.append(" case when (procType='zjmy'or procType='zjsc') then 'zjmysc_' || pxKey  else procType || '_' || pxKey end ")
				.append(")");
		// 查询
		String selectSql = withAsSql + "select pxKey,pxName,sumAmount,sumSjxb from wa_stat_px order by pxKey";
		String countSql = withAsSql + "select count(*) from wa_stat_px ";
		log.debug("selectSql:" + selectSql);
		log.debug("selectSql:" + countSql);
		Query query = em.createNativeQuery(selectSql, CashPoolItemVo.class);
		Query queryCount = em.createNativeQuery(countSql.toString());
		// 设置参数
		Set<Entry<Integer, Object>> entrySet = paramVals.entrySet();
		for (Entry<Integer, Object> entry : entrySet) {
			query.setParameter(entry.getKey(), entry.getValue());
			queryCount.setParameter(entry.getKey(), entry.getValue());
		}
		query.setFirstResult(first);
		query.setMaxResults(pageSize);
		// 返回结果
		List<CashPoolItemVo> rtList = query.getResultList();
		Integer count = (Integer) queryCount.getSingleResult();
		CashPoolPageModelObj obj = new CashPoolPageModelObj();
		obj.setRtList(rtList);
		obj.setCount(count);
		return obj;
	}

	/**
	 * 公司+品项的实际下拨对比
	 * @param beginDate
	 * @param endDate
	 * @param first
	 * @param pageSize
	 * @param filters
	 * @return
	 * @Author: LiWei
	 * @CreatedTime: 2013-12-19 下午3:05:31
	 * @UpdatedBy:
	 * @UpdatedTime:
	 */
	public CashPoolPageModelObj loadByCompanyItem(List<String> cpIds, Date beginDate, Date endDate, int first, int pageSize, Map<String, String> filters, String reportType) {
		int paramIndex = 1;
		Map<Integer, Object> paramVals = new HashMap<Integer, Object>();

		// 拼接时间限制
		StringBuffer subWhereSql = new StringBuffer(" where 1=1 ");
		if (cpIds != null && cpIds.size() > 0) {
			subWhereSql.append("and scp.companyId in (" + this.appendIds(cpIds, false) + ") ");
		} else {
			return null;
		}
		if (beginDate != null) {
			subWhereSql.append("and scp.PAYDATE >=?" + paramIndex + " ");
			paramVals.put(paramIndex, beginDate);
			paramIndex++;
		}
		if (endDate != null) {
			subWhereSql.append("and scp.PAYDATE <=?" + paramIndex + " ");
			paramVals.put(paramIndex, endDate);
			paramIndex++;
		}
		// 组合查询结果为临时表with as
		StringBuffer withAsSql = new StringBuffer();
		if(StringUtils.isNotBlank(reportType) && "apply".equals(reportType)) {
			withAsSql.append(" with wa_stat_companypx (cpId,cpName,xiaoji,tzlc,lxyh,sdck,rzzk,rzfzk,hdzk,hdfzk,gck,gl,ghgd,zjgl,zjfgl,rcfk) as( ")
			.append(" select 0,'总计', ").append(" sum(tzlc+lxyh+sdck+rzzk+rzfzk+hdzk+hdfzk+gck+gl+ghgd+zjgl+zjfgl+rcfk),")
			.append(" sum(tzlc),sum(lxyh),sum(sdck),sum(rzzk),sum(rzfzk),sum(hdzk),sum(hdfzk),sum(gck),sum(gl),sum(ghgd),sum(zjgl),sum(zjfgl),sum(rcfk) ")
			.append(" from  vw_stat_companypx_sq scp  ")
			.append(subWhereSql)
			.append(" union ")
			.append(" select cp.ID,cp.COMPANY_NAME, ")
			.append(" sum(tzlc+lxyh+sdck+rzzk+rzfzk+hdzk+hdfzk+gck+gl+ghgd+zjgl+zjfgl+rcfk),")
			.append(" sum(tzlc),sum(lxyh),sum(sdck),sum(rzzk),sum(rzfzk),sum(hdzk),sum(hdfzk),sum(gck),sum(gl),sum(ghgd),sum(zjgl),sum(zjfgl),sum(rcfk) ")
			.append(" from COMPANY cp join  vw_stat_companypx_sq scp on(scp.companyId=cp.ID) ")
			.append(subWhereSql)
			.append(" group by cp.ID,cp.COMPANY_NAME ")
			.append(")");
		}else {
			withAsSql.append(" with wa_stat_companypx (cpId,cpName,xiaoji,tzlc,lxyh,sdck,rzzk,rzfzk,hdzk,hdfzk,gck,gl,ghgd,zjgl,zjfgl,rcfk) as( ")
			.append(" select 0,'总计', ").append(" sum(tzlc+lxyh+sdck+rzzk+rzfzk+hdzk+hdfzk+gck+gl+ghgd+zjgl+zjfgl+rcfk),")
			.append(" sum(tzlc),sum(lxyh),sum(sdck),sum(rzzk),sum(rzfzk),sum(hdzk),sum(hdfzk),sum(gck),sum(gl),sum(ghgd),sum(zjgl),sum(zjfgl),sum(rcfk) ")
			.append(" from  vw_stat_companypx scp  ")
			.append(subWhereSql)
			.append(" union ")
			.append(" select cp.ID,cp.COMPANY_NAME, ")
			.append(" sum(tzlc+lxyh+sdck+rzzk+rzfzk+hdzk+hdfzk+gck+gl+ghgd+zjgl+zjfgl+rcfk),")
			.append(" sum(tzlc),sum(lxyh),sum(sdck),sum(rzzk),sum(rzfzk),sum(hdzk),sum(hdfzk),sum(gck),sum(gl),sum(ghgd),sum(zjgl),sum(zjfgl),sum(rcfk) ")
			.append(" from COMPANY cp join  vw_stat_companypx scp on(scp.companyId=cp.ID) ")
			.append(subWhereSql)
			.append(" group by cp.ID,cp.COMPANY_NAME ")
			.append(")");
		}
		
		// 查询
		String selectSql = withAsSql + "select * from wa_stat_companypx order by cpId";
		String countSql = withAsSql + "select count(*) from wa_stat_companypx ";
		log.debug("selectSql:" + selectSql);
		log.debug("selectSql:" + countSql);
		Query query = em.createNativeQuery(selectSql, CashCompanyItemVo.class);
		Query queryCount = em.createNativeQuery(countSql.toString());
		// 设置参数
		Set<Entry<Integer, Object>> entrySet = paramVals.entrySet();
		for (Entry<Integer, Object> entry : entrySet) {
			query.setParameter(entry.getKey(), entry.getValue());
			queryCount.setParameter(entry.getKey(), entry.getValue());
		}
		if (first >= 0) {
			query.setFirstResult(first);
			query.setMaxResults(pageSize);
		}
		// 返回结果
		List<CashCompanyItemVo> rtList = query.getResultList();
		Integer count = (Integer) queryCount.getSingleResult();
		CashPoolPageModelObj obj = new CashPoolPageModelObj();
		obj.setRtList(rtList);
		obj.setCount(count);
		return obj;
	}

	/**
	 * 资金贸易生产的关联和非关联
	 * @param type
	 * @param related
	 * @param beginDate
	 * @param endDate
	 * @param first
	 * @param pageSize
	 * @param filters
	 * @return
	 * @Author: LiWei
	 * @CreatedTime: 2013-12-19 下午3:05:05
	 * @UpdatedBy:
	 * @UpdatedTime:
	 */
	public CashPoolPageModelObj loadByCompanyZjmyscRelated(List<String> cpIds, String type, String related, Date beginDate, Date endDate, int first, int pageSize, Map<String, String> filters, String reportType) {
		int paramIndex = 1;
		Map<Integer, Object> paramVals = new HashMap<Integer, Object>();
		// 拼接时间限制
		StringBuffer subWhereSql = new StringBuffer(" where 1=1 ");
		if (cpIds != null && cpIds.size() > 0) {
			subWhereSql.append("and sz.cpid in (" + this.appendIds(cpIds, false) + ") ");
		} else {
			return null;
		}
		if (beginDate != null) {
			subWhereSql.append("and sz.PAYDATE >=?" + paramIndex + " ");
			paramVals.put(paramIndex, beginDate);
			paramIndex++;
		}
		if (endDate != null) {
			subWhereSql.append("and sz.PAYDATE <=?" + paramIndex + " ");
			paramVals.put(paramIndex, endDate);
			paramIndex++;
		}
		if (related != null) {
			subWhereSql.append("and sz.RELATED =?" + paramIndex + " ");
			paramVals.put(paramIndex, related);
			paramIndex++;
		}
		if (type != null) {
			subWhereSql.append("and sz.TYPE =?" + paramIndex + " ");
			paramVals.put(paramIndex, type);
			paramIndex++;
		}
		// 组合查询结果为临时表with as
		StringBuffer withAsSql = new StringBuffer();
		if(StringUtils.isNotBlank(reportType) && "apply".equals(reportType)) {
			withAsSql.append(" with wa_stat_zjmysc(cpId,cpName,pxKey,sjxb) as ( ")
			.append(" select 0,'总计',sz.pxKey,sum(sz.sjxb) from vw_stat_zjmysc_sq sz  ")
			.append(subWhereSql)
			.append(" group by sz.pxKey ")
			.append(" union ")
			.append(" select sz.cpId,cp.COMPANY_NAME,sz.pxKey,sum(sz.sjxb) from vw_stat_zjmysc_sq sz join COMPANY cp on(cp.ID=sz.cpId) ")
			.append(subWhereSql)
			.append(" group by sz.cpId,cp.COMPANY_NAME,sz.pxKey ")
			.append(" ), wa_stat_zjmyscGroup(cpId,cpName,xiaoji,datasStr) as(")
			.append(" select wa_sz.cpId,wa_sz.cpName,sum(wa_sz.sjxb) as xiaoji,")
			.append("      varchar(replace(replace(replace(xml2clob(xmlagg(xmlelement(NAME a, wa_sz.pxKey|| '=' || wa_sz.sjxb || ';'))),'<A>',''),'</A>',''),'<A/>',''))")
			.append("	  from wa_stat_zjmysc wa_sz  group by wa_sz.cpId,wa_sz.cpName")
			.append(")");
		}else {
			withAsSql.append(" with wa_stat_zjmysc(cpId,cpName,pxKey,sjxb) as ( ")
			.append(" select 0,'总计',sz.pxKey,sum(sz.sjxb) from vw_stat_zjmysc sz  ")
			.append(subWhereSql)
			.append(" group by sz.pxKey ")
			.append(" union ")
			.append(" select sz.cpId,cp.COMPANY_NAME,sz.pxKey,sum(sz.sjxb) from vw_stat_zjmysc sz join COMPANY cp on(cp.ID=sz.cpId) ")
			.append(subWhereSql)
			.append(" group by sz.cpId,cp.COMPANY_NAME,sz.pxKey ")
			.append(" ), wa_stat_zjmyscGroup(cpId,cpName,xiaoji,datasStr) as(")
			.append(" select wa_sz.cpId,wa_sz.cpName,sum(wa_sz.sjxb) as xiaoji,")
			.append("      varchar(replace(replace(replace(xml2clob(xmlagg(xmlelement(NAME a, wa_sz.pxKey|| '=' || wa_sz.sjxb || ';'))),'<A>',''),'</A>',''),'<A/>',''))")
			.append("	  from wa_stat_zjmysc wa_sz  group by wa_sz.cpId,wa_sz.cpName")
			.append(")");
		}
		// 查询
		String selectSql = withAsSql + "select * from wa_stat_zjmyscGroup order by cpId ";
		String countSql = withAsSql + "select count(*) from wa_stat_zjmyscGroup ";
		log.debug("selectSql:" + selectSql);
		log.debug("selectSql:" + countSql);
		Query query = em.createNativeQuery(selectSql, CashPoolZjmyscVo.class);
		Query queryCount = em.createNativeQuery(countSql.toString());
		// 设置参数
		Set<Entry<Integer, Object>> entrySet = paramVals.entrySet();
		for (Entry<Integer, Object> entry : entrySet) {
			query.setParameter(entry.getKey(), entry.getValue());
			queryCount.setParameter(entry.getKey(), entry.getValue());
		}
		if (first >= 0) {
			query.setFirstResult(first);
			query.setMaxResults(pageSize);
		}
		// 返回结果
		List<CashPoolZjmyscVo> rtList = query.getResultList();
		for (CashPoolZjmyscVo zjmyscVo : rtList) {
			String datasStr = zjmyscVo.getDatasStr() == null ? "" : zjmyscVo.getDatasStr();
			Map<String, Double> dataMap = new HashMap<String, Double>();
			String[] cols = datasStr.split(";");
			for (String str : cols) {
				String[] kv = str.split("=");
				if (kv.length == 2) {
					dataMap.put(kv[0], Double.valueOf(kv[1]));
				}
			}
			zjmyscVo.setDatas(dataMap);
		}
		Integer count = (Integer) queryCount.getSingleResult();
		CashPoolPageModelObj obj = new CashPoolPageModelObj();
		obj.setRtList(rtList);
		obj.setCount(count);
		return obj;
	}

	/**
	 * 日常付款明细
	 * @param beginDate
	 * @param endDate
	 * @param first
	 * @param pageSize
	 * @param filters
	 * @return
	 * @Author: LiWei
	 * @CreatedTime: 2013-12-19 下午3:04:53
	 * @UpdatedBy:
	 * @UpdatedTime:
	 */
	public CashPoolPageModelObj loadByCompanyRcfkDetail(List<String> cpIds, Date beginDate, Date endDate, int first, int pageSize, Map<String, String> filters, String reportType) {
		int paramIndex = 1;
		Map<Integer, Object> paramVals = new HashMap<Integer, Object>();
		// 拼接时间限制
		StringBuffer subWhereSql = new StringBuffer(" where 1=1 ");
		if (cpIds != null && cpIds.size() > 0) {
			subWhereSql.append("and sr.cpid in (" + this.appendIds(cpIds, false) + ") ");
		} else {
			return null;
		}
		if (beginDate != null) {
			subWhereSql.append("and sr.PAYDATE >=?" + paramIndex + " ");
			paramVals.put(paramIndex, beginDate);
			paramIndex++;
		}
		if (endDate != null) {
			subWhereSql.append("and sr.PAYDATE <=?" + paramIndex + " ");
			paramVals.put(paramIndex, endDate);
			paramIndex++;
		}
		// 组合查询结果为临时表with as
		StringBuffer withAsSql = new StringBuffer();
		if(StringUtils.isNotBlank(reportType) && "apply".equals(reportType)) {
			withAsSql.append(" with wa_stat_rcfk(cpId,cpName,total,tax,packMater,steam,salary,freight,elseProj,xiaoji2,marketDedicted,transfer) as( ")
			.append(" select 0,'总计',sum(sr.TOTAL),sum(sr.TAX),sum(sr.PACKMATER),sum(sr.STEAM),sum(sr.SALARY),sum(sr.FREIGHT),sum(sr.ELSEPROJ),sum(sr.marketDedicted+sr.transfer),sum(sr.marketDedicted),sum(sr.transfer) ")
			.append(" from vw_stat_rcfk_sq sr ")
			.append(subWhereSql)
			.append(" union ")
			.append(" select sr.CPID,sr.CPNAME,  ")
			.append("        sum(sr.TOTAL),sum(sr.TAX),sum(sr.PACKMATER),sum(sr.STEAM),sum(sr.SALARY),sum(sr.FREIGHT),sum(sr.ELSEPROJ),sum(sr.marketDedicted+sr.transfer),sum(sr.marketDedicted),sum(sr.transfer) ")
			.append(" from vw_stat_rcfk_sq sr ")
			.append(subWhereSql)
			.append(" group by sr.CPID,sr.CPNAME ")
			.append(")");
		}else {
			withAsSql.append(" with wa_stat_rcfk(cpId,cpName,total,tax,packMater,steam,salary,freight,elseProj,xiaoji2,marketDedicted,transfer) as( ")
			.append(" select 0,'总计',sum(sr.TOTAL),sum(sr.TAX),sum(sr.PACKMATER),sum(sr.STEAM),sum(sr.SALARY),sum(sr.FREIGHT),sum(sr.ELSEPROJ),sum(sr.marketDedicted+sr.transfer),sum(sr.marketDedicted),sum(sr.transfer) ")
			.append(" from vw_stat_rcfk sr ")
			.append(subWhereSql)
			.append(" union ")
			.append(" select sr.CPID,sr.CPNAME,  ")
			.append("        sum(sr.TOTAL),sum(sr.TAX),sum(sr.PACKMATER),sum(sr.STEAM),sum(sr.SALARY),sum(sr.FREIGHT),sum(sr.ELSEPROJ),sum(sr.marketDedicted+sr.transfer),sum(sr.marketDedicted),sum(sr.transfer) ")
			.append(" from vw_stat_rcfk sr ")
			.append(subWhereSql)
			.append(" group by sr.CPID,sr.CPNAME ")
			.append(")");
		}
		// 查询
		String selectSql = withAsSql + "select * from wa_stat_rcfk order by cpId";
		String countSql = withAsSql + "select count(*) from wa_stat_rcfk ";
		log.debug("selectSql:" + selectSql);
		log.debug("selectSql:" + countSql);
		Query query = em.createNativeQuery(selectSql, CashPoolRcfkVo.class);
		Query queryCount = em.createNativeQuery(countSql.toString());
		// 设置参数
		Set<Entry<Integer, Object>> entrySet = paramVals.entrySet();
		for (Entry<Integer, Object> entry : entrySet) {
			query.setParameter(entry.getKey(), entry.getValue());
			queryCount.setParameter(entry.getKey(), entry.getValue());
		}
		if (first >= 0) {
			query.setFirstResult(first);
			query.setMaxResults(pageSize);
		}
		// 返回结果
		List<CashPoolRcfkVo> rtList = query.getResultList();
		Integer count = (Integer) queryCount.getSingleResult();
		CashPoolPageModelObj obj = new CashPoolPageModelObj();
		obj.setRtList(rtList);
		obj.setCount(count);
		return obj;
	}

	/**
	 * 获取资金贸易和生产的所有品项
	 * @return
	 * @Author: LiWei
	 * @CreatedTime: 2013-12-19 上午11:09:59
	 * @UpdatedBy:
	 * @UpdatedTime:
	 */
	public List<CashPoolColumnVo> findAllZjmyscPxs() {
		String sql = "select dc.CODE_KEY as key,dc.CODE_VAL as columnName from DICT dc where dc.CODE_CAT='TMS.FARM.PRODUCE.VARIETY.TYPE.FOOD' or dc.CODE_CAT='TMS.FARM.PRODUCE.VARIETY.TYPE.OIL' order by BigInt(dc.CODE_KEY)";
		Query query = em.createNativeQuery(sql, CashPoolColumnVo.class);
		List<CashPoolColumnVo> rtList = query.getResultList();
		return rtList;
	}

	public List<CashCompanyVo> findCompanyByName(String name) {
		if (name == null) {
			name = "";
		}
		// 获取当前人员所在的机构
		String authCpIds = "";
		boolean isLeader = false;
		String currentUserName = loginService.getCurrentUserName();
		String authCpsql = "select cp.ID,cp.CORPORATION_FLAG from PU bpu join PS bps on(bps.PID=bpu.PERNR) join S bs on(bps.SID=bs.ID) join O bo on(bo.ID=bs.OID) join COMPANY cp on(cp.SAP_CODE=bo.BUKRS) " + " where bpu.ID=?1";
		Query authCpQuery = em.createNativeQuery(authCpsql);
		authCpQuery.setParameter(1, currentUserName);
		List<Object[]> authCps = authCpQuery.getResultList();
		// 如果不属于任何机构
		if (authCps.size() == 0) {
			return new ArrayList<CashCompanyVo>();
		}
		for (Object[] authCp : authCps) {
			if (authCp.length == 2) {
				String id = authCp[0].toString();
				String flag = authCp[1].toString();
				if ("Y".equals(flag)) {
					isLeader = true;
					break;
				} else {
					authCpIds += id + ",";
				}
			}
		}
		if (authCpIds.length() > 1) {
			authCpIds = authCpIds.substring(0, authCpIds.length() - 1);
		}
		name = "%" + name + "%";
		String sql = "select '' || cp.ID as id,cp.COMPANY_NAME as cpName from COMPANY cp where cp.COMPANY_NAME like ?1";
		if (!isLeader) {
			sql += " and cp.ID in(" + authCpIds + ")";
		}
		Query query = em.createNativeQuery(sql, CashCompanyVo.class);
		query.setParameter(1, name);
		List<CashCompanyVo> cps = query.getResultList();
		return cps;
	}

	private String appendIds(List<String> ids, boolean isStr) {
		String idStr = "";
		for (String id : ids) {
			if (isStr) {
				idStr += "'" + id + "',";
			} else {
				idStr += id + ",";
			}
		}
		if (idStr.toString().length() > 1) {
			return idStr.substring(0, idStr.length() - 1);
		} else {
			return "";
		}
	}
}
