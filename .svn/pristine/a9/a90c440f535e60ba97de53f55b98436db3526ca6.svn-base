package com.wcs.tms.service.report.inveproduct;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.StringUtils;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.InveProductDetail;
import com.wcs.tms.service.system.company.CompanyTmsService;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:投资理财产品额度明细表Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Stateless
public class InveProductReportService implements Serializable {

	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(InveProductReportService.class);
	@Inject
	EntityService entityService;
	@Inject
	PEManager peManager;
	@Inject
	LoginService loginService;
	@Inject
	CompanyTmsService companyTmsService;

	/**
	 * 得到公司银行select
	 * @param companyId
	 * @return
	 */
	public List<SelectItem> getCreditBankSelect(Long companyId) {
		List<SelectItem> bankSelect = new ArrayList<SelectItem>();
		List<InveProductDetail> cs = findFetchBank(companyId);
		for (InveProductDetail c : cs) {
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
	 * 从投资理财额度确认实体中获得银行
	 * @param companyId
	 * @return
	 * @throws ServiceException
	 */
	public List<InveProductDetail> findFetchBank(Long companyId) throws ServiceException {
		Validate.notNull(companyId, "公司Id为空");
		try {
			StringBuilder bulder = new StringBuilder();
			bulder.append("select ipd from InveProductDetail ipd join fetch ipd.company join fetch ipd.bank where ipd.defunctInd='N' and ipd.company.id = ?1");
			bulder.append(" order by ipd.requestDate desc");
			return entityService.find(bulder.toString(), companyId);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 得到公司银行select
	 * @param companyId
	 * @return
	 */
	public List<SelectItem> getCreditBankSelect(String companyName) {
		List<SelectItem> bankSelect = new ArrayList<SelectItem>();
		List<InveProductDetail> cs = findCreditFetchBank1(companyName);
		for (InveProductDetail c : cs) {
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
	 * 从投资理财额度确认实体中获得银行
	 * @param companyName
	 * @return
	 * @throws ServiceException
	 */
	public List<InveProductDetail> findCreditFetchBank1(String companyName) throws ServiceException {
		Validate.notNull(companyName, "公司名称为空");
		try {
			long companyId = companyTmsService.getCompanyName(companyName);
			StringBuilder bulder = new StringBuilder();
			bulder.append("select ipd from InveProductDetail ipd join fetch ipd.company join fetch ipd.bank where ipd.defunctInd='N' and ipd.company.id = ?1");
			bulder.append(" order by ipd.requestDate desc");
			return entityService.find(bulder.toString(), companyId);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 查询投资理财产品额度明细表
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<InveProductDetail> findInveProductDetail(Map<String, Object> conditionMap) throws ServiceException {
		List<InveProductDetail> ipds = new ArrayList<InveProductDetail>();
		StringBuilder jpql = new StringBuilder(
				"select ipd from InveProductDetail ipd join fetch ipd.company join fetch ipd.bank where ipd.defunctInd='N' ");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (null != conditionMap.get("reqStartDate")) {
			String start = sdf.format(conditionMap.get("reqStartDate"));
			jpql.append(" and ipd.requestDate >= '" + start + "' ");
		}
		if (null != conditionMap.get("reqEndDate")) {
			String end = sdf.format(DateUtil.addOneDay((Date) conditionMap.get("reqEndDate")));
			jpql.append(" and ipd.requestDate <= '" + end + "' ");
		}
		if (null != conditionMap.get("passStartDate")) {
			String start = sdf.format(conditionMap.get("passStartDate"));
			jpql.append(" and ipd.passDate >= '" + start + "' ");
		}
		if (null != conditionMap.get("passEndDate")) {
			String end = sdf.format(DateUtil.addOneDay((Date) conditionMap.get("passEndDate")));
			jpql.append(" and ipd.passDate <= '" + end + "' ");
		}

		if (!StringUtils.isBlankOrNull((String) conditionMap.get("bankId"))) {
			jpql.append(" and ipd.bank.id = " + conditionMap.get("bankId"));
		}

		try {
			String companyId = (String) conditionMap.get("companyId");
			log.info("====companyId====" + companyId);
			if (!StringUtils.isBlankOrNull(companyId)) {
				jpql.append(" and ipd.company.id = " + conditionMap.get("companyId"));
				log.info("====jpql.toString()====" + jpql.toString());
				ipds = entityService.find(jpql.toString());
			} else {
				List<String> companyIds = (List<String>) conditionMap.get("companyIds");
				log.info("====companyIds====" + companyIds);
				if (companyIds != null && !companyIds.isEmpty()) {
					jpql.append(" and ipd.company.id in (?1)");
					log.info("====else jpql.toString()====" + jpql.toString());
					ipds = entityService.find(jpql.toString(), companyIds);
				}
			}

		} catch (Exception e) {
			log.error("findInveProductDetail方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}

		return ipds;
	}
}
