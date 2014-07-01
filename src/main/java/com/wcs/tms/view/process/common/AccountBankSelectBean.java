package com.wcs.tms.view.process.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import com.wcs.tms.service.process.common.AccountBankSelectService;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.view.process.common.entity.AccountBankSelectVo;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:账户银行选择Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
@ManagedBean
@ViewScoped
public class AccountBankSelectBean implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Inject
	AccountBankSelectService accountBankSelectService;
	
	@Inject
	BankService bankService;
	
	//界面展示数据
//	private LazyDataModel<AccountBankSelectVo> lazyDataModel;
	private List<AccountBankSelectVo> dataModel;

	private AccountBankSelectVo selectedVO = new AccountBankSelectVo();
	
	//总行下拉
	private List<SelectItem> topBankSelect = new ArrayList<SelectItem>();
	
	//选中数据后所要执行的方法
	private String method;
	
	//筛选条件
	private Long topBankId;
	private String bankCode;
	private String bsbCode;
	private String unionBankNo;
	private String bankName;

	@PostConstruct
	public void initAccountBankSelectBean(){
		topBankSelect = bankService.getTopLevelSelect();
		filterAccountBank();
	}
	
	/**
	 * 清除缓存
	 */
	public void clear(){
		topBankId = null;
		bankCode = "";
		bsbCode = "";
		unionBankNo = "";
		bankName = "";
		selectedVO = new AccountBankSelectVo();
		filterAccountBank();
	}
	
	/**
	 * 根据条件过滤账户公司
	 */
	public void filterAccountBank(){
//		lazyDataModel = accountBankSelectService.filterAccountBank(topBankId,bankCode,bsbCode,unionBankNo,bankName);
		dataModel = accountBankSelectService.filterAccountBank(topBankId,bankCode,bsbCode,unionBankNo,bankName);
	}
	
	/**
	 * 执行上级申请页面方法
	 */
	public void beforeClose(){
		Application application = FacesContext.getCurrentInstance().getApplication() ;
		ExpressionFactory expressionFactory = application.getExpressionFactory() ;
		ELContext elContext = FacesContext.getCurrentInstance().getELContext() ;
		MethodExpression me = expressionFactory.createMethodExpression(elContext, "#{"+method+"}", Void.class, new Class[]{}) ;
		try{
			me.invoke(elContext, null) ;
		}catch(Exception e){
			e.printStackTrace() ;
		}
	}
	

	public AccountBankSelectVo getSelectedVO() {
		return selectedVO;
	}

	public void setSelectedVO(AccountBankSelectVo selectedVO) {
		this.selectedVO = selectedVO;
	}

	public Long getTopBankId() {
		return topBankId;
	}

	public void setTopBankId(Long topBankId) {
		this.topBankId = topBankId;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBsbCode() {
		return bsbCode;
	}

	public void setBsbCode(String bsbCode) {
		this.bsbCode = bsbCode;
	}

	public String getUnionBankNo() {
		return unionBankNo;
	}

	public void setUnionBankNo(String unionBankNo) {
		this.unionBankNo = unionBankNo;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public List<SelectItem> getTopBankSelect() {
		return topBankSelect;
	}

	public void setTopBankSelect(List<SelectItem> topBankSelect) {
		this.topBankSelect = topBankSelect;
	}

	public List<AccountBankSelectVo> getDataModel() {
		return dataModel;
	}

	public void setDataModel(List<AccountBankSelectVo> dataModel) {
		this.dataModel = dataModel;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	


}
