package com.wcs.tms.service.report.cashpool;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.MathUtil;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcessDefine;
import com.wcs.tms.service.process.bankaccount.BankAccountService;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.service.system.sysprocess.ProcessDefineService;
import com.wcs.tms.view.process.common.entity.CashPoolVo;
/**
 * 
 * <p>Project: tms</p>
 * <p>Description:现金池查询清单Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
@Stateless
public class CashPoolReportService implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(CashPoolReportService.class);
	@Inject
    PEManager peManager;
	@Inject
	ProcessDefineService processDefineService;
	@Inject
	EntityService entityService;
	@Inject
	ProcessUtilMapService processUtilMapService;
	@Inject
	BankAccountService bankAccountService;
	@Inject
	BankService bankService;
	


	public List<String> transformSelectedInstanceNums(List<String> selectedInstanceNums) {
		List<String> fnIds = new ArrayList<String>();
		for(String num : selectedInstanceNums){
			fnIds.add(processUtilMapService.getFnIdByTmsId(num));
		}
		return fnIds;
	}


	/**
	 * 流程显示排序(按任务处理时间排倒叙)
	 * @param pivos
	 * @return
	 */
	private List<CashPoolVo> orderByStepDate(List<CashPoolVo> cashPoolVos){
		Collections.sort(cashPoolVos, new Comparator<CashPoolVo>() {
			@Override
			public int compare(CashPoolVo o1, CashPoolVo o2) {
				return o2.getApplyDate().compareTo(o1.getApplyDate());
			}
		});
		return cashPoolVos;
	}
	
	/**
	 * 得到现金池流程下拉
	 * @return
	 */
	public List<SelectItem> getCashPoolNameSelect() {
		StringBuilder jpql = new StringBuilder("select pd from ProcessDefine pd where pd.defunctInd = 'N' ");
		jpql.append(" and pd.status = 'Y'");
		List<ProcessDefine> pds = entityService.find(jpql.toString());
		List<SelectItem> selects = new ArrayList<SelectItem>();
		List<String> codeList = new ArrayList<String>();
		codeList.add("TMS_BPM_RA_013");
		codeList.add("TMS_BPM_RA_009");
		codeList.add("TMS_BPM_RA_014");
		codeList.add("TMS_BPM_RA_012");
		codeList.add("TMS_BPM_RA_011");
		for(ProcessDefine pd : pds){
			if(codeList.contains(pd.getProcessCode())){
				SelectItem si = new SelectItem(pd.getId(), pd.getProcessName());
				selects.add(si);
			}
		}
		return selects;
	}

	/**
	 * 查询所有公司
	 * @return
	 */
	public List<Company> findAllCompany() {
		StringBuilder jpql = new StringBuilder("select m from Company m where m.defunctInd = 'N' and m.status = 'Y' ");
		return entityService.find(jpql.toString());
	}
	

	/**
	 * 获取数据库数据总行数
	 * @param countSql
	 * @return
	 */
	public Integer getRowCountBySql(String countSql) {
		Query query = entityService.findNativeQuery(countSql);
		return (Integer)query.getSingleResult();
	}

	/**
	 * 分页查询视图数据
	 * @param sql
	 * @param first
	 * @param pageSize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getPagedRowsBySql(String sql, int first, int pageSize) {
		Query query = entityService.findNativeQuery(sql);
		query.setFirstResult(first);
		query.setMaxResults(pageSize);
		List<Object[]> ret = query.getResultList();
		List<String> pids = new ArrayList<String>();
		for (Object[] obj : ret) {
			pids.add(obj[0].toString());
		}
		return pids;
	}
	
	/**
	 * 查出流程实例个数
	 * @param sql
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getRowsBySql(String sql){
		Query query = entityService.findNativeQuery(sql);
		return query.getResultList();
	}

	
	/**
	 * 根据流程实例编号 查询数据库 组装Vo
	 * @param cVos
	 * @param processIds
	 */
	public void transferVosByProcessIds(List<CashPoolVo> cVos,
			List<String> processIds) {
		try {
		for(String pId : processIds ){
			//根据每个流程实例编号去组装vo
			StringBuilder str = new StringBuilder("select * from VW_CASHPOOL where PROC_INST_ID = '"+pId+"'");
			// 查询一个流程的所有信息
			List<Object[]> rs = this.getRowsBySql(str.toString());
			//组装cashpoolVo
			// 如果视图中一个流程只有一条数据且付款方式为空 说明没有付款记录 (取反 为有付款记录情况)
			if(!(rs.size()==1 && (String)rs.get(0)[8] == null)){
				int index = 1;
				// 同个流程付款金额和
				Double sum = 0.0;
				for(Object[] r : rs){
					CashPoolVo vo = new CashPoolVo();
					// 流程实例编号
					vo.setProcInstId(processUtilMapService.getTmsIdByFnId((String)r[1]));
					// 填制时间
					vo.setApplyDate((Date)r[5]);
					// 申请付款时间
					vo.setAppPaymentDate((Date)r[4]);
					// 收款公司
					vo.setReceiveCompany(entityService.find(Company.class, (Long)r[2]));
					// 流程名称
					vo.setProcessName(processDefineService.getProcDefineById((Long)r[0]).getProcessName());
					// 收款银行
					vo.setPayeeBank(entityService.find(Bank.class, (Long)r[3]));
					// 申请金额
					vo.setFundsTotal(Double.valueOf(r[6].toString()));
					// 剩余需支付金额(如果没付款记录 则为申请金额)
					vo.setLastPaymentAmount(null == r[11] ? Double.valueOf(r[6].toString()):Double.valueOf(r[11].toString()));
					if(! MathUtil.isEmptyOrNull((String)r[8])){
						vo.setPayWay("O".equals((String)r[8]) ? "网银支付" : "SUNGARD支付");
					}
					vo.setPaymentNumber(index++);
					// 注：每次付款 不能为0
					if( null != r[7]){
						vo.setRealPaymentAmount(Double.parseDouble(r[7].toString()));
						if(r[13] != null) {
							String tmsStatus = r[13].toString();
							String tmsState = "";
							if("1".equals(tmsStatus)) {
								tmsState = "TMS支付-未导入";
							}else if("2".equals(tmsStatus)) {
								tmsState = "TMS支付-导入成功";
							}else if("3".equals(tmsStatus)) {
								tmsState = "TMS支付-导入失败";
							}else if("4".equals(tmsStatus)) {
								tmsState = "TMS支付-支付成功";
							}else if("5".equals(tmsStatus)) {
								tmsState = "TMS支付-支付失败";
							}
							vo.setTmsStatus(tmsState);
						}
						if(r[14] != null) {
							vo.setPayDetail(r[14].toString());
						}else {
							vo.setPayDetail("");
						}
						if(r[15] != null) {
							vo.setPayDate((Date)r[15]);
						}else {
							vo.setPayDate(null);
						}
						if(r[16] != null) {
							vo.setTmsRefNumber(r[16].toString());
						}else {
							vo.setTmsRefNumber("");
						}
					}
					sum +=Double.parseDouble(r[7].toString());
					sum = MathUtil.roundHalfUp(sum,4);
					cVos.add(vo);
				}
				// 如果剩余需要支付字段 不等于实际 剩余需要支付金额 就为“终止支付”
				Double last = MathUtil.roundHalfUp(Double.valueOf(rs.get(0)[6].toString())- sum,4);
				Double lastDatabase = MathUtil.roundHalfUp(Double.valueOf(rs.get(0)[11].toString()),4);
				if(rs.get(0)[11] != null && !lastDatabase.equals(last)){
					CashPoolVo vo = new CashPoolVo();
					// 流程实例编号
					vo.setProcInstId(processUtilMapService.getTmsIdByFnId((String)rs.get(0)[1]));
					// 填制时间
					vo.setApplyDate((Date)rs.get(0)[5]);
					// 申请付款时间
					vo.setAppPaymentDate((Date)rs.get(0)[4]);
					// 收款公司
					vo.setReceiveCompany(entityService.find(Company.class, (Long)rs.get(0)[2]));
					// 流程名称
					vo.setProcessName(processDefineService.getProcDefineById((Long)rs.get(0)[0]).getProcessName());
					// 收款银行
					vo.setPayeeBank(entityService.find(Bank.class, (Long)rs.get(0)[3]));
					// 申请金额
					vo.setFundsTotal(Double.valueOf(rs.get(0)[6].toString()));
					// 剩余需支付金额(如果没付款记录 则为申请金额)
					vo.setLastPaymentAmount(null == rs.get(0)[11] ? Double.valueOf(rs.get(0)[6].toString()):Double.valueOf(rs.get(0)[11].toString()));
					vo.setPayWay("终止支付");
					cVos.add(vo);
				}
				// 如果一个流程未结束且没有支付记录则显示“未完成审批”，如果流程终止则显示“终止支付”
				
			}else{
				CashPoolVo vo = new CashPoolVo();
				// 流程实例编号
				vo.setProcInstId(processUtilMapService.getTmsIdByFnId((String)rs.get(0)[1]));
				// 填制时间
				vo.setApplyDate((Date)rs.get(0)[5]);
				// 申请付款时间
				vo.setAppPaymentDate((Date)rs.get(0)[4]);
				// 收款公司
				vo.setReceiveCompany(entityService.find(Company.class, (Long)rs.get(0)[2]));
				// 流程名称
				vo.setProcessName(processDefineService.getProcDefineById((Long)rs.get(0)[0]).getProcessName());
				// 收款银行
				vo.setPayeeBank(entityService.find(Bank.class, (Long)rs.get(0)[3]));
				// 申请金额
				vo.setFundsTotal(Double.valueOf(rs.get(0)[6].toString()));
				// 剩余需支付金额(如果没付款记录 则为申请金额)
				vo.setLastPaymentAmount(null == rs.get(0)[11] ? Double.valueOf(rs.get(0)[6].toString()):Double.valueOf(rs.get(0)[11].toString()));
				// 如果剩余需要支付字段 等于0 剩余需要支付金额 就为“终止支付”
				if(rs.get(0)[11] != null && Double.valueOf(rs.get(0)[11].toString()) == 0){
					vo.setPayWay("终止支付");
				}else{
					vo.setPayWay("未完成审批");
				}
				cVos.add(vo);
			}
			
		}
		} catch (Exception e) {
			log.error("transferVosByProcessIds方法  根据流程实例编号 查询数据库 组装Vo出现异常" , e);
		}
	}

	/**
	 * 查询条件下的 所有流程申请金额之和
	 * @param processIds
	 */
	public Double getAppAmountSumByProcIds(String processIds) {
		// 每个流程申请额度只算一次
		StringBuilder sql = new StringBuilder("select SUM(AMOUNT) from(select distinct PROC_INST_ID,AMOUNT from VW_CASHPOOL where PROC_INST_ID in "+processIds+")");
		Query query = entityService.findNativeQuery(sql.toString());
		if(null != query.getSingleResult()){
			return Double.valueOf(query.getSingleResult().toString());
		}else{
			return 0.0;
		}
	}

	public Double getRealPaymentAmountSumByProcIds(String processIds) {
		StringBuilder sql = new StringBuilder("select sum(PAY_FUNDS_TOTAL) from VW_CASHPOOL where PROC_INST_ID in "+processIds+"");
		Query query = entityService.findNativeQuery(sql.toString());
		if(null != query.getSingleResult()){
			return Double.valueOf(query.getSingleResult().toString());
		}else{
			return 0.0;
		}
	}

	public Double getLastPaymentAmountSumByProcIds(String processIds) {
		// 每个流程剩余需要支付金额只算一次
		StringBuilder sql = new StringBuilder("select sum(LAST_PAIED_AMOUNT) from (select distinct PROC_INST_ID,LAST_PAIED_AMOUNT from VW_CASHPOOL where PROC_INST_ID in "+processIds+")");
		Query query = entityService.findNativeQuery(sql.toString());
		if(null != query.getSingleResult()){
			return Double.valueOf(query.getSingleResult().toString());
		}else{
			return 0.0;
		}
	}
	
}

