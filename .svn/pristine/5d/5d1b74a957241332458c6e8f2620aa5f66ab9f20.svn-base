package com.wcs.tms.service.system.company;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.CompanyAccount;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:公司银行账户</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liaowei@wcs-global.com">廖伟</a>
 */
@Stateless
public class CompanyAccountServer implements Serializable {

	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(CompanyAccountServer.class);
	@Inject
	EntityService entityService;
	@Inject
	LoginService loginService;

	/**
	 * 公司银行账户保存或修改
	 * @param companyAccount 公司银行账户 
	 */
	public void saveOrUpdate(CompanyAccount companyAccount) {
		Long id = companyAccount.getId();
		if (id == null) {
			companyAccount.setCreatedBy(loginService.getCurrentUserName());
			this.entityService.create(companyAccount);
		} else {
			companyAccount.setUpdatedBy(loginService.getCurrentUserName());
			this.entityService.update(companyAccount);
		}
	}

	/**
	 * 获取公司的银行账户信息
	 * @param companyId 公司Id
	 * @return List<CompanyAccount>
	 */
	public List<CompanyAccount> findCompanyAccountList(Long companyId) {
		StringBuilder jpql = new StringBuilder("select ca from CompanyAccount ca join fetch ca.company where ca.defunctInd = 'N' ");
		jpql.append(" and ca.company.id=" + companyId);
		return this.entityService.find(jpql.toString());
	}

	/**
	 * 根据公司ID和银行帐号描述，获得银行帐号
	 * @param companyId
	 * @param accountDesc
	 * @return
	 */
	public List<SelectItem> findBankSelect(Long companyId, String accountDesc) {
		log.info("根据公司ID和银行帐号描述，获得银行帐号");
		log.info("公司ID:" + companyId);
		log.info("帐号描述" + accountDesc);
		List<SelectItem> items = new ArrayList<SelectItem>();
		StringBuilder jpql = new StringBuilder("select ca from CompanyAccount ca join fetch ca.company where ca.defunctInd = 'N' ");
		// 公司
		jpql.append(" and ca.company.id=" + companyId);
		// 银行帐号描述
		jpql.append(" and ca.accountDesc='" + accountDesc + "'");
		// 排序
		jpql.append(" order by ca.createdDatetime");
		log.info("jpql:" + jpql);
		List<CompanyAccount> list = this.entityService.find(jpql.toString());
		for (CompanyAccount ca : list) {
			items.add(new SelectItem(ca.getAccount(), ca.getAccount()));
		}
		return items;
	}

	/**
	 * 根据帐号ID，获得银行帐号
	 * @param accountId
	 * @return
	 */
	public List<SelectItem> findBankSelect(String counterpartyCode) {
		log.info("根据公司ID和银行帐号描述，获得银行帐号");
		log.info("帐号标识:" + counterpartyCode);
		List<SelectItem> items = new ArrayList<SelectItem>();
		StringBuilder jpql = new StringBuilder("select ca from CompanyAccount ca join fetch ca.company where ca.defunctInd = 'N' ");
		// 帐号
		jpql.append(" and ca.counterpartyCode=‘" + counterpartyCode + "'");
		log.info("jpql:" + jpql);
		List<CompanyAccount> list = this.entityService.find(jpql.toString());
		for (CompanyAccount ca : list) {
			items.add(new SelectItem(ca.getAccount(), ca.getAccount()));
		}
		return items;
	}

	/**
	 * 检查公司银行账户的唯一性
	 * @param ca 公司银行账户对象
	 * @return
	 */
	public Boolean checkAccount(Long caId, String account) {
		log.info("检查公司银行账户的唯一性");
		StringBuilder jpql = new StringBuilder();
		jpql.append("select ca from CompanyAccount ca where ca.defunctInd = 'N'");
		jpql.append(" and ca.account = '" + account + "'");
		if (null != caId && caId != 0) {
			jpql.append(" and ca.id <> " + caId);
		}
		log.info("jpql:" + jpql);
		List<CompanyAccount> list = entityService.find(jpql.toString());
		log.info("集合个数：" + list.size());
		if (CollectionUtils.isEmpty(list)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 检查公司银行账户标识的唯一性
	 * @param caId
	 * @param counterpartyCode
	 * @return
	 */
	public Boolean checkCounterpartyCode(Long caId, String counterpartyCode) {
		log.info("检查公司银行账户标识的唯一性");
		StringBuilder jpql = new StringBuilder();
		jpql.append("select ca from CompanyAccount ca where ca.defunctInd = 'N'");
		jpql.append(" and ca.counterpartyCode = '" + counterpartyCode + "'");
		if (null != caId && caId != 0) {
			jpql.append(" and ca.id <> " + caId);
		}
		log.info("jpql:" + jpql);
		List<CompanyAccount> list = new ArrayList<CompanyAccount>();
		list = entityService.find(jpql.toString());
		log.info("集合个数：" + list.size());
		if (CollectionUtils.isEmpty(list)) {
			log.info("false");
			return false;
		} else {
			log.info("true");
			return true;
		}
	}

	/**
	 * 查询银行名称
	 * @param bankCode
	 * @return
	 */
	public String getBankNameByCode(String bankCode) {
		StringBuilder jpql = new StringBuilder("select b from Bank b where b.defunctInd = 'N'");
    	//启用
    	jpql.append(" and b.status = 'Y'");
    	jpql.append(" and b.bankCode = '"+bankCode+"'");
    	List<Bank> banks = entityService.find(jpql.toString());
    	if(banks.size() == 0 ){
    		return "";
    	}else{
    		return banks.get(0).getBankName();
    	}
	}

}
