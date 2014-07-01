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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.MathUtil;
import com.wcs.tms.model.Bank;
import com.wcs.tms.service.process.common.AccountBankSelectService;
import com.wcs.tms.service.process.common.BankMultipleSelectService;
import com.wcs.tms.service.system.bank.BankService;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:银行多选条件过滤选择Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
@ManagedBean
@ViewScoped
public class BankMultipleSelectBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(BankMultipleSelectBean.class);
	
	@Inject
	AccountBankSelectService accountBankSelectService;
	@Inject
	EntityService entityService;
	
	@Inject
	BankService bankService;
	
	@Inject
	BankMultipleSelectService bankMultipleSelectService;
	
	//界面展示数据
	private LazyDataModel<Bank> dataModel;

	// 当前页所选数据
	private Bank[] selectedTopBanks ;
	// 每次翻页首先获取之前所选数据
	private List<Bank> selectDataBefore = new ArrayList<Bank>();
	// 所有选中数据
	private List<Bank> selectedAllTopBanks = new ArrayList<Bank>();
	
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
	public void initBankMultipleSelectBean(){
		topBankSelect = bankService.getTopLevelSelect();
		filterTopBank();
	}
	
	/**
	 * 翻页时保存上一页已选择数据
	 */
	public void saveLastPageSelection(){
		try {
			// 翻页前 将之前所选数据 加上本页所选数据(某次翻页可能选择相同数据)
			for(Bank  b : selectedTopBanks){
				if(!selectDataBefore.contains(b)){
					selectDataBefore.add(b);
				}
			}
		} catch (Exception e) {
			log.error("saveLastPageSelection方法 翻页时保存上一页已选择数据 出现异常：",e);
		}
	}
	
	/**
	 * 清除缓存
	 */
	public void clear(){
		selectDataBefore.clear();
		selectedAllTopBanks.clear();
		topBankId = null;
		bankCode = "";
		bsbCode = "";
		unionBankNo = "";
		bankName = "";
		filterTopBank();
	}
	
	/**
	 * 根据条件过滤总行公司
	 */
	public void filterTopBank(){
		try {
			
			StringBuilder jpql = new StringBuilder("select b from Bank b where b.defunctInd = 'N' and b.topLevelFlag = 'Y'") ;
			if(!MathUtil.isEmptyOrNull(bankCode)){
				jpql.append(" and lower(b.bankCode) like '%" + bankCode.toLowerCase()+ "%'") ;
			}
			if(!MathUtil.isEmptyOrNull(bsbCode)){
				jpql.append(" and lower(b.bsbCode) like '%" + bsbCode.toLowerCase()+ "%'") ;
			}
			if(!MathUtil.isEmptyOrNull(unionBankNo)){
				jpql.append(" and lower(b.unionBankNo) like '%" + unionBankNo.toLowerCase()+ "%'") ;
			}
			if(!MathUtil.isEmptyOrNull(bankName)){
				jpql.append(" and lower(b.bankName) like '%" + bankName.toLowerCase()+ "%'") ;
			}
			dataModel = entityService.findModel(jpql.toString());
		} catch (Exception e) {
			log.error("filterTopBank方法 根据条件过滤总行公司出现异常：",e);
		}
		
	}
	
	/**
	 * 执行上级申请页面方法
	 */
	public void beforeClose(){
		// 关闭控件之前已选所有数据
		selectedAllTopBanks.addAll(selectDataBefore);
		// 保存最后一页选择数据
		for(Bank b : selectedTopBanks){
			if(!selectedAllTopBanks.contains(b)){
				selectedAllTopBanks.add(b);
			}
		}
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
	
	public void returnToThis(){
		return ;
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

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Bank[] getSelectedTopBanks() {
		return selectedTopBanks;
	}

	public void setSelectedTopBanks(Bank[] selectedTopBanks) {
		this.selectedTopBanks = selectedTopBanks;
	}

	public LazyDataModel<Bank> getDataModel() {
		return dataModel;
	}

	public void setDataModel(LazyDataModel<Bank> dataModel) {
		this.dataModel = dataModel;
	}

	public List<Bank> getSelectDataBefore() {
		return selectDataBefore;
	}

	public void setSelectDataBefore(List<Bank> selectDataBefore) {
		this.selectDataBefore = selectDataBefore;
	}

	public List<Bank> getSelectedAllTopBanks() {
		return selectedAllTopBanks;
	}

	public void setSelectedAllTopBanks(List<Bank> selectedAllTopBanks) {
		this.selectedAllTopBanks = selectedAllTopBanks;
	}

	
}
