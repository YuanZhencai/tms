package com.wcs.tms.view.system.bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;

import com.google.common.collect.Lists;
import com.wcs.base.service.EntityService;
import com.wcs.base.view.ViewBaseBean;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.service.TmsAccountBalanceTimer;
import com.wcs.tms.model.Bank;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.util.MessageUtils;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:银行管理Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@ManagedBean
@ViewScoped
public class BankBean extends ViewBaseBean<Bank>{

	private static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(BankBean.class);
	
	@Inject 
	EntityService entityService;
	@Inject
	BankService bankService;
	@Inject
	CommonBean dictBean;
	@Inject
	TmsAccountBalanceTimer tmsAccountBalanceTimer ;
	
	
	//查询条件map
	private Map<String, Object> conditionMap = new HashMap<String, Object>();
	//lazymodel
	private LazyDataModel<Bank> bankLazyModel;
	//List
	private List<Bank> bankList = Lists.newArrayList();
	//银行对象
	private Bank bank = new Bank();
	//银行代码
	private String bankCode ;
	
	private String op = "新增";
	//一级机构银行下拉
	private List<SelectItem> topLevelSelect = new ArrayList<SelectItem>();
	//银行类型下拉
	private List<SelectItem> bankTypeSelect = new ArrayList<SelectItem>();
	
	/**
	 * <p>Description:Bean init </p>
	 */
	@PostConstruct
	public void initBank(){
		log.info("initBankBean~~~~~~~~~~~~~~");
		initTop();
		initBankTypeSelect();
		if(bankList.size() == 0){
		    /* 默认查询所有银行 */
			searchBank(); 
		}
	}
	
	/**
	 * 初始化银行银行类型下拉菜单
	 */
	private void initBankTypeSelect() {
		bankTypeSelect = dictBean.getDictByCode(DictConsts.TMS_BANK_TYPE);
	}

	/**
	 * 初始化银行一级机构下拉菜单
	 */
	public void initTop(){
		topLevelSelect = bankService.getTopLevelSelect();
	}
	
	
	/**
	 * 银行列表查询
	 */
	public void searchBank(){
		bankList = bankService.findBankForLazy(conditionMap);
	}
	
	/**
	 * <p>Description: 弹出新增dialog</p>
	 */
	public void clear(){
		op = "新增";
		bank = new Bank();
		bankCode = "";
	}
	
	/**
	 * <p>Description: 弹出修改dialog</p>
	 */
	public void toEdit(){
	    op = "修改";
	    Long bankId = bank.getId();
	    bank = entityService.find(Bank.class, bankId);
	    bankCode = bank.getBankCode();
	}
	
	/**
	 * 检查银行代码唯一性
	 */
	public boolean checkCode(){
		if(bankCode!=null && bankCode.equals(bank.getBankCode())){
			return true;
		}
		boolean check = bankService.checkCode(bank.getBankCode());
		if(!check){
			MessageUtils.addErrorMessage("errorMsg", "银行代码已存在！");
		}
		return check;
	}
	
	/**
	 * <p>Description: 银行保存</p>
	 */
	public void saveBank(){
		if(!checkCode()){
			return;
		}
		String topFlag = bank.getTopLevelFlag();
		if(topFlag!=null && "N".equals(topFlag)){
    		Long topId = bank.getTopBankId();
    		if(topId.equals(bank.getId())){
    			MessageUtils.addErrorMessage("errorMsg", "不可选择自身为一级机构！");
    			return;
    		}
    	}
		try {
			bankService.saveOrUpdate(bank);
			// 如果本身是一级银行则其一级银行为其本身 add on 2013-8-2 by yan 
			if ("Y".equals(topFlag)) {
				bank.setTopBankId(bank.getId());
				entityService.update(bank);
			}
			MessageUtils.addSuccessMessage("msg", "银行信息保存成功！");
			bank = new Bank();
		} catch (Exception e) {
			MessageUtils.addErrorMessage("errorMsg", "银行信息保存失败！");
		}
		initTop();
		searchBank();
	}
	
	/**
	 * <p>Description: 银行启用/禁用</p>
	 */
	public void disable(){
		String status = bank.getStatus();
		if(status!=null){
			if("Y".equals(status)){
			    //禁用
				bank.setStatus("N");
			}else{
			    //启用
				bank.setStatus("Y");
			}
			try {
				bankService.saveOrUpdate(bank);
				MessageUtils.addSuccessMessage("msg", "银行信息" + ("Y".equals(status)? "禁用" : "启用") + "成功！");
			} catch (Exception e) {
				MessageUtils.addErrorMessage("msg", "银行信息" + ("Y".equals(status)? "禁用" : "启用") + "失败！");
			}
		}else{
			MessageUtils.addErrorMessage("errorMsg", "银行信息保存失败！");
		}
	}
	
	
	
	/******set@get*******************************************************/
	
	
	public LazyDataModel<Bank> getBankLazyModel() {
		return bankLazyModel;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public Map<String, Object> getConditionMap() {
		return conditionMap;
	}

	public void setConditionMap(Map<String, Object> conditionMap) {
		this.conditionMap = conditionMap;
	}

	public void setBankLazyModel(LazyDataModel<Bank> bankLazyModel) {
		this.bankLazyModel = bankLazyModel;
	}

	public List<Bank> getBankList() {
		return bankList;
	}

	public void setBankList(List<Bank> bankList) {
		this.bankList = bankList;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public List<SelectItem> getTopLevelSelect() {
		return topLevelSelect;
	}

	public void setTopLevelSelect(List<SelectItem> topLevelSelect) {
		this.topLevelSelect = topLevelSelect;
	}

	public List<SelectItem> getBankTypeSelect() {
		return bankTypeSelect;
	}

	public void setBankTypeSelect(List<SelectItem> bankTypeSelect) {
		this.bankTypeSelect = bankTypeSelect;
	}
	
	
}
