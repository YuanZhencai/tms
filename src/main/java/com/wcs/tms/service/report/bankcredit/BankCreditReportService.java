package com.wcs.tms.service.report.bankcredit;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.StringUtils;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.Credit;
import com.wcs.tms.model.CreditP;
import com.wcs.tms.model.CreditR;
import com.wcs.tms.service.system.company.CreditService;

@Stateless
public class BankCreditReportService implements Serializable {

	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(BankCreditReportService.class);
	@Inject
	EntityService entityService;
	@Inject
	PEManager peManager;
	@Inject
	LoginService loginService;
	@Inject
	CreditService creditService;

	/**
	 * 得到公司已授信银行select
	 * @param companyId
	 * @return
	 */
	public List<SelectItem> getCreditBankSelect(Long companyId) {
		List<SelectItem> bankSelect = new ArrayList<SelectItem>();
		List<Credit> cs = creditService.findCreditFetchBank1(companyId);
		log.info("cs.size:" + cs.size());
		for (Credit c : cs) {
			// 去重复银行
			boolean has = false;
			for (SelectItem si : bankSelect) {
				if (c.getBank().getId().equals(si.getValue())) {
					has = true;
				}
			}
			if (!has) {
				bankSelect.add(new SelectItem(c.getBank().getId(), c.getBank().getBankName()));
			}
		}
		return bankSelect;
	}
	
	/**
	 * 得到某公司已授信总行下拉 add on 2013-2-25
	 * @param companyId
	 * @return
	 */
	public List<SelectItem> getCreditTopBankSelect(Long companyId) {
		List<SelectItem> bankSelect = new ArrayList<SelectItem>();
		List<Bank> bs = creditService.findCreditTopBankSelect(companyId);
		for(Bank b : bs){
			bankSelect.add(new SelectItem(b.getId(), b.getBankName()));
		}
		return bankSelect;
	}
	
	/**
	 * 得到公司已授信银行select
	 * @param companyId
	 * @return
	 */
	public List<SelectItem> getCreditBankSelect(String companyName) {
		List<SelectItem> bankSelect = new ArrayList<SelectItem>();
		List<Credit> cs = creditService.findCreditFetchBank1(companyName);
		for (Credit c : cs) {
			// 去重复银行
			boolean has = false;
			for (SelectItem si : bankSelect) {
				if (c.getBank().getId().equals(si.getValue())) {
					has = true;
				}
			}
			if (!has) {
				bankSelect.add(new SelectItem(c.getBank().getId(), c.getBank().getBankName()));
			}
		}
		return bankSelect;
	}

	/**
	 * 按过滤条件查询数据库
	 * @param conditionMap
	 * @param doDisplay 是否选择“显示最新数据”
	 * @return
	 * @throws ServiceException
	 */
	public List<Credit> findBankCreditDetail(Map<String, Object> conditionMap, Boolean doDisplay) throws ServiceException {
		List<Credit> cs = new ArrayList<Credit>();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// 授信主数据Credit表在每次授信调剂后做 了相应更新，所以这里不需要再去根据调剂主数据去计算显示在界面上
			if(doDisplay){
				// 查询条件：相同公司相同银行在条件塞选区间内取到期日最晚一条数据
				StringBuilder jpql2 =new StringBuilder("select c.company.id,c.bank.id,MAX(c.creditEnd) from Credit c join fetch c.company join fetch c.bank where c.defunctInd='N' and c.creditLine <> 0") ;
				if (null != conditionMap.get("creditStartS")) {
					String start = sdf.format(conditionMap.get("creditStartS"));
					jpql2.append(" and c.creditStart >= '" + start + "' ");
				}
				if (null != conditionMap.get("creditStartE")) {
					String end = sdf.format(DateUtil.addOneDay((Date) conditionMap.get("creditStartE")));
					jpql2.append(" and c.creditStart < '" + end + "' ");
				}
				if (null != conditionMap.get("creditEndS")) {
					String start = sdf.format(conditionMap.get("creditEndS"));
					jpql2.append(" and c.creditEnd >= '" + start + "' ");
				}
				if (null != conditionMap.get("creditEndE")) {
					String end = sdf.format(DateUtil.addOneDay((Date) conditionMap.get("creditEndE")));
					jpql2.append(" and c.creditEnd < '" + end + "' ");
				}
				
				if (!StringUtils.isBlankOrNull((String) conditionMap.get("bankId"))) {
					// modfied by yanchangjing on 2013-2-25
					jpql2.append(" and c.bank.topBankId = " + conditionMap.get("bankId"));
				}

				String companyId = (String) conditionMap.get("companyId");
				log.info("====companyId====" + companyId);
				
				// 分组查询，查出row对象
				List<Object[]> rows = new ArrayList<Object[]>();
				if (!StringUtils.isBlankOrNull(companyId)) {
					jpql2.append(" and c.company.id = " + conditionMap.get("companyId"));
					jpql2.append(" GROUP BY c.company.id,c.bank.id  ");
					rows = entityService.find(jpql2.toString());
					cs = this.getCreditsByRows(rows);
				} else {
					@SuppressWarnings("unchecked")
					List<String> companyIds = (List<String>) conditionMap.get("companyIds");
					log.info("====companyIds====" + companyIds);
					if (companyIds != null && !companyIds.isEmpty()) {
						jpql2.append(" and c.company.id in (?1)");
						jpql2.append(" GROUP BY c.company.id,c.bank.id ");
						rows = entityService.find(jpql2.toString(), companyIds);
						cs = this.getCreditsByRows(rows);
					}
				}
		//如果不选择“显示最新数据”
		}else{
			StringBuilder jpql = new StringBuilder("select c from Credit c join fetch c.company join fetch c.bank where c.defunctInd='N' and c.creditLine <> 0 ");
			if (null != conditionMap.get("creditStartS")) {
				String start = sdf.format(conditionMap.get("creditStartS"));
				jpql.append(" and c.creditStart >= '" + start + "' ");
			}
			if (null != conditionMap.get("creditStartE")) {
				String end = sdf.format(DateUtil.addOneDay((Date) conditionMap.get("creditStartE")));
				jpql.append(" and c.creditStart < '" + end + "' ");
			}
			if (null != conditionMap.get("creditEndS")) {
				String start = sdf.format(conditionMap.get("creditEndS"));
				jpql.append(" and c.creditEnd >= '" + start + "' ");
			}
			if (null != conditionMap.get("creditEndE")) {
				String end = sdf.format(DateUtil.addOneDay((Date) conditionMap.get("creditEndE")));
				jpql.append(" and c.creditEnd < '" + end + "' ");
			}

			if (!StringUtils.isBlankOrNull((String) conditionMap.get("bankId"))) {
				// modfied by yanchangjing on 2013-2-25
				jpql.append(" and c.bank.topBankId = " + conditionMap.get("bankId"));
			}

				String companyId = (String) conditionMap.get("companyId");
				log.info("====companyId====" + companyId);

				if (!StringUtils.isBlankOrNull(companyId)) {
					jpql.append(" and c.company.id = " + conditionMap.get("companyId"));
					cs = entityService.find(jpql.toString());
				} else {
					@SuppressWarnings("unchecked")
					List<String> companyIds = (List<String>) conditionMap.get("companyIds");
					log.info("====companyIds====" + companyIds);
					if (companyIds != null && !companyIds.isEmpty()) {
						jpql.append(" and c.company.id in (?1)");
						cs = entityService.find(jpql.toString(), companyIds);
					}
				}
			}
		} catch (Exception e) {
			log.error("findBankCreditDetail方法 按过滤条件查询数据库出现异常",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
			
		return cs;

	}

	/**
	 * 不选“显示最新数据”条件查询数据库
	 */
	private void selectAllDataByCondition(Map<String, Object> conditionMap,
			SimpleDateFormat sdf, List<Credit> cs) {
		StringBuilder jpql = new StringBuilder("select c from Credit c join fetch c.company join fetch c.bank where c.defunctInd='N' and c.creditLine <> 0 ");
		if (null != conditionMap.get("creditStartS")) {
			String start = sdf.format(conditionMap.get("creditStartS"));
			jpql.append(" and c.creditStart >= '" + start + "' ");
		}
		if (null != conditionMap.get("creditStartE")) {
			String end = sdf.format(DateUtil.addOneDay((Date) conditionMap.get("creditStartE")));
			jpql.append(" and c.creditStart < '" + end + "' ");
		}
		if (null != conditionMap.get("creditEndS")) {
			String start = sdf.format(conditionMap.get("creditEndS"));
			jpql.append(" and c.creditEnd >= '" + start + "' ");
		}
		if (null != conditionMap.get("creditEndE")) {
			String end = sdf.format(DateUtil.addOneDay((Date) conditionMap.get("creditEndE")));
			jpql.append(" and c.creditEnd < '" + end + "' ");
		}

		if (!StringUtils.isBlankOrNull((String) conditionMap.get("bankId"))) {
			// modfied by yanchangjing on 2013-2-25
			jpql.append(" and c.bank.topBankId = " + conditionMap.get("bankId"));
		}

			String companyId = (String) conditionMap.get("companyId");
			log.info("====companyId====" + companyId);

			if (!StringUtils.isBlankOrNull(companyId)) {
				jpql.append(" and c.company.id = " + conditionMap.get("companyId"));
				cs = entityService.find(jpql.toString());
			} else {
				@SuppressWarnings("unchecked")
				List<String> companyIds = (List<String>) conditionMap.get("companyIds");
				log.info("====companyIds====" + companyIds);
				if (companyIds != null && !companyIds.isEmpty()) {
					jpql.append(" and c.company.id in (?1)");
					cs = entityService.find(jpql.toString(), companyIds);
				}
			}
	}

	/**
	 * 选择“显示最新数据”过滤条件的数据库查询
	 * @param conditionMap
	 * @param sdf
	 * @param cs
	 */
	private void selectLastDataByCondition(Map<String, Object> conditionMap, SimpleDateFormat sdf, List<Credit> cs) {
		// 查询条件：相同公司相同银行在条件塞选区间内取到期日最晚一条数据
		StringBuilder jpql2 =new StringBuilder("select c.company.id,c.bank.id,MAX(c.creditEnd) from Credit c join fetch c.company join fetch c.bank where c.defunctInd='N' and c.creditLine <> 0 ") ;
		if (null != conditionMap.get("creditStartS")) {
			String start = sdf.format(conditionMap.get("creditStartS"));
			jpql2.append(" and c.creditStart >= '" + start + "' ");
		}
		if (null != conditionMap.get("creditStartE")) {
			String end = sdf.format(DateUtil.addOneDay((Date) conditionMap.get("creditStartE")));
			jpql2.append(" and c.creditStart < '" + end + "' ");
		}
		if (null != conditionMap.get("creditEndS")) {
			String start = sdf.format(conditionMap.get("creditEndS"));
			jpql2.append(" and c.creditEnd >= '" + start + "' ");
		}
		if (null != conditionMap.get("creditEndE")) {
			String end = sdf.format(DateUtil.addOneDay((Date) conditionMap.get("creditEndE")));
			jpql2.append(" and c.creditEnd < '" + end + "' ");
		}
		
		if (!StringUtils.isBlankOrNull((String) conditionMap.get("bankId"))) {
			// modfied by yanchangjing on 2013-2-25
			jpql2.append(" and c.bank.topBankId = " + conditionMap.get("bankId"));
		}

		String companyId = (String) conditionMap.get("companyId");
		log.info("====companyId====" + companyId);
		
		// 分组查询，查出row对象
		List<Object[]> rows = new ArrayList<Object[]>();
		if (!StringUtils.isBlankOrNull(companyId)) {
			jpql2.append(" and c.company.id = " + conditionMap.get("companyId"));
			jpql2.append(" GROUP BY c.company.id,c.bank.id  ");
			rows = entityService.find(jpql2.toString());
			cs = this.getCreditsByRows(rows);
		} else {
			@SuppressWarnings("unchecked")
			List<String> companyIds = (List<String>) conditionMap.get("companyIds");
			log.info("====companyIds====" + companyIds);
			if (companyIds != null && !companyIds.isEmpty()) {
				jpql2.append(" and c.company.id in (?1)");
				jpql2.append(" GROUP BY c.company.id,c.bank.id ");
				rows = entityService.find(jpql2.toString(), companyIds);
				cs = this.getCreditsByRows(rows);
			}
		}
	}

	/**
	 * 处理授信额度字段显示值
	 * @param cs
	 */
	private void processCreditsByCrAndCp(List<Credit> cs) {
		// 列表显示授信额度 = 银行授信额度 + 对应授信调剂相加减 add on 2013-6-17
		// ******************************************************
		for(Credit c : cs){
			//报表“授信额度”显示值
			Double sum = c.getCreditLine();
			//查询每条授信对应调剂
			//申请
			List<CreditR> creditRs = this.findCreditRByCreditId(c);
			for(CreditR cr : creditRs){
				sum = sum + cr.getCreditAdd();
			}
			//提供
			List<CreditP> creditPs = this.findCreditPByCreditId(c);
			for(CreditP cp : creditPs){
				sum = sum - cp.getCreditReduce();
			}
			//界面显示值设置
		}
		// ******************************************************
	}

	/**
	 * 根据分组查出 组数据 查出Credit对象集合
	 * @param rows 三列： 第一列=公司id,第二列=银行id,第三列=同组到期日期最晚时间
	 * @return
	 */
	private List<Credit> getCreditsByRows(List<Object[]> rows) {
		List<Credit> cs = new ArrayList<Credit>();
		for(Object[] row :rows){
			StringBuilder jpql = new StringBuilder("select c from Credit c join fetch c.company join fetch c.bank where c.defunctInd='N'");
			jpql.append(" and c.company.id = "+ row[0]);
			jpql.append(" and c.bank.id = "+ row[1]);
			jpql.append(" and c.creditEnd = '"+ row[2]+"'");
			List<Credit> credits = entityService.find(jpql.toString());
			cs.addAll(credits);
		}
		return cs;
	}

	/**
	 * 根据授信id查询所有授信调剂提供信息
	 * @param c
	 * @return
	 */
	private List<CreditP> findCreditPByCreditId(Credit c) {
		StringBuilder jpql = new StringBuilder("select cp from CreditP cp where cp.defunctInd = 'N'");
		jpql.append(" and cp.credit.id = "+c.getId());
		return entityService.find(jpql.toString());
	}

	/**
	 * 根据授信id查询所有授信调剂申请信息
	 * @param c
	 * @return
	 */
	private List<CreditR> findCreditRByCreditId(Credit c) {
		StringBuilder jpql = new StringBuilder("select cr from CreditR cr where cr.defunctInd = 'N'");
		jpql.append(" and cr.credit.id = "+c.getId());
		return entityService.find(jpql.toString());
	}

	/**
	* 初始化所有公司下拉菜单
	* @return
	*/
	public List<Company> getAllCompanySelect() {
		List<Company> companys = new ArrayList<Company>();
		StringBuilder jpql = new StringBuilder("select c from Company c where c.defunctInd = 'N' and c.status = 'Y'");
		companys = entityService.find(jpql.toString());

		return companys;
	}

}
