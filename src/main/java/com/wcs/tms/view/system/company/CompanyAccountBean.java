package com.wcs.tms.view.system.company;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Lists;
import com.wcs.base.service.EntityService;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.MathUtil;
import com.wcs.base.view.ViewBaseBean;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.CompanyAccount;
import com.wcs.tms.service.process.bankaccount.BankAccountService;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.service.system.company.CompanyAccountServer;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.AccountBankSelectBean;
import com.wcs.tms.view.process.common.entity.AccountBankSelectVo;

/**
 * <p>Project: tms</p>
 * <p>Description:公司银行账户管理Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liaowei@wcs-global.com">廖伟</a>
 */
@ManagedBean
@ViewScoped
public class CompanyAccountBean extends ViewBaseBean<Company> {

	private static final long serialVersionUID = 1L;

	private Log log = LogFactory.getLog(CompanyAccountBean.class);
	
	@Inject
	EntityService entityService;
	@Inject
	CompanyAccountServer companyAccountServer;
	@Inject
	BankService bankService;
	@Inject
	CommonBean dictBean;
	@Inject
	BankAccountService bankAccountService;

	// List
	private List<CompanyAccount> companyAccountList = Lists.newArrayList();
	// 公司账户对象
	private CompanyAccount companyAccount = new CompanyAccount();
	// 公司对象
	private Company company = new Company();
	// 公司Id
	private Long companyId;
	// 公司名称
	private String companyName;
	// 选择银行下拉
	private List<SelectItem> bankSelect = new ArrayList<SelectItem>();
	// 页面显示（新增、编辑）
	private String op = "新增";
	// 账户银行名称
	private String accountBankName;
	@ManagedProperty(value = "#{accountBankSelectBean}")
	private AccountBankSelectBean accountBankSelectBean;

	public CompanyAccountBean() {
		this.setupPage("/faces/system/companyManage/companyAccount.xhtml", null, null);
	}

	/**
	 * 初始化Bean
	 */
	@PostConstruct
	public void initComp() {
		log.info("initCompanyAccountBean~~~~~~~~~~~~~~");
		Object obj = JSFUtils.getRequestParam("compId");
		if (obj != null) {
			companyId = Long.parseLong(obj.toString());
			company = this.entityService.find(Company.class, companyId);
			companyName = company.getCompanyName();
			this.search();
		}
		initDict();

	}

	/**
	 * 设置选中的账户银行code值
	 */
	public void setBankCodeOfProcInst() {
		AccountBankSelectVo vo = getAccountBankSelectBean().getSelectedVO();
		setAccountBankName(vo.getBankName());
		companyAccount.setBank(vo.getBankCode());
	}

	/**
	 * 初始化数据字典
	 */
	public void initDict() {
		log.info("初始化数据字典!!!!!!!!!!!!!!!!!!!!!");
		bankSelect = dictBean.getDictByCode(DictConsts.BPM_ACCOUNT_BANK_CODE);
	}

	/**
	 * 查询公司银行账户列表
	 */
	public void search() {
		log.info("查询公司银行账户~~~");
		companyAccountList = companyAccountServer.findCompanyAccountList(companyId);
	}

	/**
	 * 查找银行名称
	 * @param bankCode
	 * @return
	 */
	public String getBankNameByCode(String bankCode) {
	    return companyAccountServer.getBankNameByCode(bankCode);
	}

	/**
	 * 弹出新增dialog
	 */
	public void clear() {
		log.info("弹出新增dialog");
		op = "新增";
		companyAccount = new CompanyAccount();
		accountBankName = "";
	}

	/**
	 * 弹出编辑dialog
	 */
	public void toEdit() {
		log.info("弹出编辑dialog");
		op = "编辑";
		Long companyAccountId = companyAccount.getId();
		log.info("companyAccountId:" + companyAccountId);
		companyAccount = entityService.find(CompanyAccount.class, companyAccountId);
		setAccountBankName(getBankNameByCode(companyAccount.getBank()));
		log.info("accountBankName:" + accountBankName);
	}

	/**
	 * 添加或修改公司银行账户
	 */
	public void addOrEdit() {
		if (companyAccountServer.checkCounterpartyCode(companyAccount.getId(), companyAccount.getCounterpartyCode())) {
			MessageUtils.addErrorMessage("accountLineIdEditMsg", "帐号标识已存在！");
			return;
		}
		if (MathUtil.isEmptyOrNull(companyAccount.getCounterpartyCode().trim())) {
			MessageUtils.addErrorMessage("accountLineIdEditMsg", "帐号标识不可为空");
			return;
		}
		if (MathUtil.isEmptyOrNull(companyAccount.getAccountDesc().trim())) {
			MessageUtils.addErrorMessage("accountLineIdEditMsg", "银行帐号描述不可为空");
			return;
		}
		if (MathUtil.isEmptyOrNull(companyAccount.getAccount().trim())) {
			MessageUtils.addErrorMessage("accountLineIdEditMsg", "帐号不可为空");
			return;
		}
		try {
			companyAccount.setCounterpartyCode(companyAccount.getCounterpartyCode().trim());
			companyAccount.setAccountDesc(companyAccount.getAccountDesc().trim());
			companyAccount.setAccount(companyAccount.getAccount().trim());
			companyAccount.setBsbCode(companyAccount.getBsbCode().trim());
			companyAccount.setUnionBankNo(companyAccount.getUnionBankNo().trim());
			companyAccount.setCompany(company);
			companyAccountServer.saveOrUpdate(companyAccount);
			MessageUtils.addSuccessMessage("msg", "公司银行账户信息保存成功！");
			log.info("公司银行账户信息保存成功！");
			companyAccount = new CompanyAccount();
		} catch (Exception e) {
			log.info("公司银行账户信息保存失败！");
			MessageUtils.addErrorMessage("msg", "公司银行账户信息保存失败！");
		}
		search();
	}

	/**
	 * <p>Description: 删除公司银行账户</p>
	 */
	public void del() {
		try {
			companyAccount.setDefunctInd("Y");
			this.entityService.update(companyAccount);
			MessageUtils.addSuccessMessage("msg", "公司银行账户信息删除成功！");
		} catch (Exception e) {
			MessageUtils.addErrorMessage("errorMsg", "公司银行账户信息删除失败！");
		}
		search();
	}

	/**
	 * 
	 * <p>Description: 跳转到公司账户维护界面</p>
	 * @return
	 */
	public String goInput() {
		return this.getListPage();
	}

	/******set@get*******************************************************/

	public List<CompanyAccount> getCompanyAccountList() {
		return companyAccountList;
	}

	public void setCompanyAccountList(List<CompanyAccount> companyAccountList) {
		this.companyAccountList = companyAccountList;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public CompanyAccount getCompanyAccount() {
		return companyAccount;
	}

	public void setCompanyAccount(CompanyAccount companyAccount) {
		this.companyAccount = companyAccount;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public List<SelectItem> getBankSelect() {
		return bankSelect;
	}

	public void setBankSelect(List<SelectItem> bankSelect) {
		this.bankSelect = bankSelect;
	}

	public String getAccountBankName() {
		return accountBankName;
	}

	public void setAccountBankName(String accountBankName) {
		this.accountBankName = accountBankName;
	}

	public AccountBankSelectBean getAccountBankSelectBean() {
		return accountBankSelectBean;
	}

	public void setAccountBankSelectBean(AccountBankSelectBean accountBankSelectBean) {
		this.accountBankSelectBean = accountBankSelectBean;
	}

}
