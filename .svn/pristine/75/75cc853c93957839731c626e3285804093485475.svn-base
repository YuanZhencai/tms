package com.wcs.tms.view.system.company;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.view.ViewBaseBean;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.Guarantee;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.service.system.company.GuaranteeService;
import com.wcs.tms.util.MessageUtils;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:公司历史担保信息管理Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@ManagedBean
@ViewScoped
public class GuaranteeBean extends ViewBaseBean<Guarantee>{

	private static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(GuaranteeBean.class);
	
	@Inject 
	EntityService entityService;
	@Inject
	CommonBean dictBean;
	@Inject
	GuaranteeService guaranteeService;
	@Inject
    CompanyTmsService companyTmsService;
	@Inject
	BankService bankService;
	
	
	//担保方ID
	private Long compId;
	//担保方公司
	private Company securedCom = new Company();
	//担保列表
	private List<Guarantee> guaranteesList = new ArrayList<Guarantee>();
	//担保信息对象
	private Guarantee guarantee = new Guarantee();
	/** 资金币种下拉*/
    private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
    /** 公司下拉*/
    private List<SelectItem> companySelect = new ArrayList<SelectItem>();
    /** 总行下拉*/
    private List<SelectItem> topBankSelect = new ArrayList<SelectItem>();
    /** 总行Id*/
    private Long topBankId;
    /** 分行下拉*/
    private List<SelectItem> childBankSelect = new ArrayList<SelectItem>();
    
    /**
	 * <p>Description:Bean init </p>
	 */
	@PostConstruct
	public void initGuar(){
		log.info("initGuaranteeBean~~~~~~~~~~~~~~");
		//得到担保方ID
		String compIdStr = JSFUtils.getRequestParam("compId");
		if(compIdStr!=null && !"".equals(compIdStr)){
			compId = Long.parseLong(compIdStr);
		}
		//初始化数据
		initPage();
		//查询担保信息
		if(guaranteesList.size() == 0){
		    /* 查询公司为担保方的担保信息 */
			searchGuar(); 
		}
		
	}
	
	/**
	 * 初始化页面数据
	 */
	public void initPage(){
		initDict();
		initCompany(true);
		initBank();
	}
	
	/**
	 * 字典初始化
	 */
	public void initDict(){
	    currencySelect = dictBean.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
	}
	
	/**
     * 初始化受保方公司下拉
     */
    public void initCompany(boolean all){
    	if(all){
    		companySelect = companyTmsService.getAllCompanySelect();
    	}else{
    		companySelect = companyTmsService.getCompanySelect();
    	}
    	
    	//剔除担保方公司
    	SelectItem except= new SelectItem();
    	for(SelectItem si : companySelect){
    		Long cid = (Long)si.getValue();
    		if(cid.equals(compId)){
    			except = si;
    		}
    	}
    	if(companySelect.contains(except)){
    		companySelect.remove(except);
    	}
    }
    
    /**
     * 跳转到公司头寸信息页面
     */
    public String goSearchCash(){
    	return "/faces/system/companyManage/companyCash.xhtml";
    }
    
    /**
     * 初始化银行
     */
    public void initBank(){
    	topBankSelect = bankService.getTopLevelSelect();
    	if(topBankId!=null && !topBankId.equals(0l)){
    		childBankSelect = bankService.getChildBankSelect(topBankId);
    	}else{
    		if(topBankSelect.size()>0){
    			childBankSelect = bankService.getChildBankSelect((Long)topBankSelect.get(0).getValue()); 
    		}
    	}
    }
	
	/**
	 * 查询担保方下的担保信息
	 * @return
	 */
	public String searchGuar(){
		securedCom = entityService.find(Company.class, compId);
		guaranteesList = guaranteeService.findGuaranteesBySecured(compId) ;
		return "/faces/system/companyManage/companyGuarantee.xhtml";
	}
	
	/**
	 * 弹出添加输入框
	 */
	public void clear(){
		guarantee = new Guarantee();
		guarantee.setSecuredCom(securedCom);
	}
	
	/**
	 * 弹出修改输入框
	 */
	public void toEdit(){
		Long guaranteeId = guarantee.getId();
		guarantee = guaranteeService.findGuaranteeById(guaranteeId);
		topBankId = guarantee.getBank().getTopBankId();
	}

	/**
	 * 保存历史担保信息
	 */
	public void saveGua(){
		try {
			guaranteeService.saveOrUpdate(guarantee);
			MessageUtils.addSuccessMessage("msg", "历史担保信息保存成功！");
			guarantee = new Guarantee();
		} catch (Exception e) {
			log.error("saveGua方法 保存历史担保信息出现异常：",e);
			MessageUtils.addErrorMessage("errorMsg", "历史担保信息保存失败！");
		}
		searchGuar();
	}
	
	/**
	 * 删除历史担保信息
	 */
	public void deleteGua(){
		try {
			guaranteeService.delete(guarantee);
			MessageUtils.addSuccessMessage("msg", "历史担保信息删除成功！");
			guarantee = new Guarantee();
		} catch (Exception e) {
			MessageUtils.addErrorMessage("errorMsg", "历史担保信息删除失败！");
		}
		searchGuar();
	}
	
	/***** set@get *****************************************/
	public Long getCompId() {
		return compId;
	}

	public void setCompId(Long compId) {
		this.compId = compId;
	}

	public Company getSecuredCom() {
		return securedCom;
	}

	public void setSecuredCom(Company securedCom) {
		this.securedCom = securedCom;
	}

	public List<Guarantee> getGuaranteesList() {
		return guaranteesList;
	}

	public void setGuaranteesList(List<Guarantee> guaranteesList) {
		this.guaranteesList = guaranteesList;
	}

	public Guarantee getGuarantee() {
		return guarantee;
	}

	public void setGuarantee(Guarantee guarantee) {
		this.guarantee = guarantee;
	}

	public List<SelectItem> getCurrencySelect() {
		return currencySelect;
	}

	public void setCurrencySelect(List<SelectItem> currencySelect) {
		this.currencySelect = currencySelect;
	}

	public List<SelectItem> getCompanySelect() {
		return companySelect;
	}

	public void setCompanySelect(List<SelectItem> companySelect) {
		this.companySelect = companySelect;
	}

	public List<SelectItem> getTopBankSelect() {
		return topBankSelect;
	}

	public void setTopBankSelect(List<SelectItem> topBankSelect) {
		this.topBankSelect = topBankSelect;
	}

	public List<SelectItem> getChildBankSelect() {
		return childBankSelect;
	}

	public void setChildBankSelect(List<SelectItem> childBankSelect) {
		this.childBankSelect = childBankSelect;
	}

	public Long getTopBankId() {
		return topBankId;
	}

	public void setTopBankId(Long topBankId) {
		this.topBankId = topBankId;
	}

	
	
	
}
