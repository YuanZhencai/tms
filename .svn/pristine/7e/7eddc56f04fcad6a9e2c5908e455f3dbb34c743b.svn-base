package com.wcs.tms.view.system.company;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.StringUtils;
import com.wcs.base.view.ViewBaseBean;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.controller.vo.DictVO;
import com.wcs.common.service.CommonService;
import com.wcs.common.service.DictService;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProdOrTradeCashMain;
import com.wcs.tms.service.system.company.CompanyCashService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.util.MessageUtils;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:公司头寸主数据管理Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */

@ManagedBean
@ViewScoped
public class CompanyCashBean extends ViewBaseBean<Company>{
	private static final long serialVersionUID = 1L;
	
	@Inject
	CommonBean commonBean;
	@Inject
	CompanyCashService companyCashService;
	@Inject
	DictService dictService;
	@Inject 
	CommonService commonService;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	EntityService entityService;
	
	// 公司Id
	private Long companyId;
	
	//头寸主数据列表
	private List<ProdOrTradeCashMain> cashList = new ArrayList<ProdOrTradeCashMain>();
	private List<CashVo> cashVoList = new ArrayList<CashVo>();
	//头寸编辑对象
	private CashVo companyCashVo ;
	//品种类型Key值(查询条件)
	private String varietyType;
	//新增头寸主数据
	private ProdOrTradeCashMain prodOrTradeCashMain = new ProdOrTradeCashMain();
	//品种类型Key值（新增头寸）
	private String varietyTypeAddCash;
	
	//头寸类型下拉
	private List<SelectItem> cashTypeSelect = new ArrayList<SelectItem>();
	//品种下拉(查询条件)
	private List<SelectItem> varietySelect = new ArrayList<SelectItem>();
	// 品种类型下拉
	private List<SelectItem> varietyTypeSelect = new ArrayList<SelectItem>();
	//品种下拉(新增头寸)
	private List<SelectItem> varietySelectAdd = new ArrayList<SelectItem>();
	
	//查询条件
	private String cashType;
	private String variety;
	
	
	public CompanyCashBean() {
		this.setupPage("/faces/system/companyManage/companyCash.xhtml", null, null);
	}
	
	@PostConstruct
	public void initCompanyCashBean(){
		Object obj = JSFUtils.getRequestParam("compId");
		if (obj != null) {
			setCompanyId(Long.parseLong(obj.toString()));
		}
		searchCash();
		initDict();
	}
	
	/**
	 * 申请时初始化品种下拉
	 */
	public void initVarietySelectAddSelect(){
		varietySelectAdd = commonBean.getDictByCode(varietyTypeAddCash);
	}
	
	/**
	 * 根据查询条件查询头寸主数据
	 */
	public void searchCashByCondition(){
		cashVoList = new ArrayList<CashVo>();
		//查询品种类型bug 增加品种类型查询条件
		cashList = companyCashService.findCashByCondition(companyId, cashType, variety, varietyType) ;
		for(ProdOrTradeCashMain cashMain : cashList){
			CashVo vo = new CashVo();
			vo.setCashMain(cashMain);
			vo.setAppType(cashMain.getAppType());
			vo.setVarietyName(this.getVarietyStr(cashMain));
			vo.setTotalCash(cashMain.getTotalCash());
			vo.setTotalCashAmount(cashMain.getTotalCashAmount());
			vo.setStartDate(cashMain.getStartDate());
			vo.setEndDate(cashMain.getEndDate());
			// 设置数据的状态
			boolean flag = isEffective(cashMain);
			vo.setStatus(flag ? "有效" : "无效");
			cashVoList.add(vo);
		}
	}
	
	/**
	 * 保存头寸修改信息
	 */
	public void saveCash(){
		entityService.update(companyCashVo.getCashMain());
		searchCashByCondition();
	}
	
	/**
	 * 列表显示品种处理
	 * @param prodOrTradeCashMain
	 * @return
	 */
	public String getVarietyStr(ProdOrTradeCashMain prodOrTradeCashMain){
	    /**
	     * sonar测试 prodOrTradeCashMain.getVariety()!="" && prodOrTradeCashMain.getVariety()!= null
	     * 改为
	     * prodOrTradeCashMain != null && prodOrTradeCashMain.getVariety()!= null && !"".equals(prodOrTradeCashMain.getVariety()
	     */
		if(prodOrTradeCashMain != null && prodOrTradeCashMain.getVariety()!= null && !"".equals(prodOrTradeCashMain.getVariety())){
    		//根据cat和key模糊查询出DictVO数据
    		List<DictVO> dictVOs = dictService.searchData(DictConsts.TMS_FARM_PRODUCE_VARIETY_TYPE, prodOrTradeCashMain.getVariety(), "", "", "");
    		String cat;
    		if(dictVOs.size() != 0){
				cat = dictVOs.get(0).getCodeCat();
    		}else{
    			cat = null;
    		}
    		//根据cat和key得到界面显示value
    		return getDictValueByKey(cat, prodOrTradeCashMain.getVariety());
    	}else{
    		return "";
    	}
	}
	
	/**
     * <p>Description:根据code_cat,code_key获取code_value </p>
     * @param code_cat
     * @param code_key
     * @return
     */
    private String getDictValueByKey(String code_cat, String code_key) {
        String retVal = code_key;
        if (!StringUtils.isBlankOrNull(code_key)) {
            String dict_key = code_cat + "." + code_key + ".zh_CN";
            retVal = StringUtils.safeString(commonService.getValueByDictCatKey(dict_key.replace(".", "_")));
        }
        return retVal;
    }
	
	/**
	 * 查询当前公司下的所有头寸信息
	 * @return
	 */
	public void searchCash(){
		setCashList(companyCashService.findCashByComId(companyId)) ;
		for(ProdOrTradeCashMain cashMain : cashList){
			CashVo vo = new CashVo();
			vo.setCashMain(cashMain);
			vo.setAppType(cashMain.getAppType());
			vo.setVarietyName(this.getVarietyStr(cashMain));
			vo.setTotalCash(cashMain.getTotalCash());
			vo.setTotalCashAmount(cashMain.getTotalCashAmount());
			vo.setStartDate(cashMain.getStartDate());
			vo.setEndDate(cashMain.getEndDate());
			// 设置数据的状态
			boolean flag = isEffective(cashMain);
			vo.setStatus(flag ? "有效" : "无效");
			cashVoList.add(vo);
		}
	}

	/**
	 * 初始化查询条件下拉
	 */
	private void initDict() {
		cashTypeSelect = commonBean.getDictByCode(DictConsts.TMS_PURCHASE_FUND_TYPE);
		setVarietyTypeSelect(commonBean.getDictByCode(DictConsts.TMS_FARM_PRODUCE_VARIETY_TYPE));
	}
	/**
	 * 初始化品种下拉
	 */
	public void initVarietySelect(){
		varietySelect = commonBean.getDictByCode(getVarietyType());
	}
	
	/**
	 * 根据当前时间判断数据是否有效
	 * @param prodOrTradeCashMain
	 * @return
	 */
	public Boolean isEffective(ProdOrTradeCashMain prodOrTradeCashMain){
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date();
		String nowDate = sdf.format(now);
		String endDate = sdf.format(prodOrTradeCashMain.getEndDate());
		//如果当前时间在数据有效期内则有效
		if((now.before(prodOrTradeCashMain.getEndDate()) || nowDate.equals(endDate))
				&& now.after(prodOrTradeCashMain.getStartDate())){
			return true;
		}else{
			//无效
			return false;
		}
	}
	
	// 新增头寸保存
	public void addCash(){
		//添加当前操作公司信息
		Company company = new Company();
		company.setId(companyId);
		prodOrTradeCashMain.setCompany(company);
		// 验证是否存在期限冲突数据
		Boolean dateConflict = validateDateConflict();
		if(dateConflict){
			MessageUtils.addErrorMessage("addCashMsg", "您填写的日期与历史预算存在冲突");
			return ;
		}
		// 添加到主数据
		entityService.create(prodOrTradeCashMain);
		// 刷新页面列表
		searchCashByCondition();
	}
	
	/**
	 * 清除上次新增缓存
	 */
	public void clearAddHistory(){
		prodOrTradeCashMain = new ProdOrTradeCashMain();
	}
	
	/**
	 * 验证是否存在期限冲突数据
	 * @return
	 */
	private Boolean validateDateConflict() {
		Boolean dateConflict;
		List<ProdOrTradeCashMain> prodOrTradeCashMains = companyCashService.findMainBy(prodOrTradeCashMain);
		if(prodOrTradeCashMains.size() == 0){
			dateConflict = false;
		}else{
			dateConflict = true;
		}
		return dateConflict;
	}
	
	public void del(){
		
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public List<SelectItem> getCashTypeSelect() {
		return cashTypeSelect;
	}

	public void setCashTypeSelect(List<SelectItem> cashTypeSelect) {
		this.cashTypeSelect = cashTypeSelect;
	}

	public List<SelectItem> getVarietySelect() {
		return varietySelect;
	}

	public List<SelectItem> setVarietySelect(List<SelectItem> varietySelect) {
		this.varietySelect = varietySelect;
		return varietySelect;
	}

	public String getCashType() {
		return cashType;
	}

	public void setCashType(String cashType) {
		this.cashType = cashType;
	}

	public String getVariety() {
		return variety;
	}

	public void setVariety(String variety) {
		this.variety = variety;
	}

	public List<ProdOrTradeCashMain> getCashList() {
		return cashList;
	}

	public void setCashList(List<ProdOrTradeCashMain> cashList) {
		this.cashList = cashList;
	}

	public List<SelectItem> getVarietyTypeSelect() {
		return varietyTypeSelect;
	}

	public void setVarietyTypeSelect(List<SelectItem> varietyTypeSelect) {
		this.varietyTypeSelect = varietyTypeSelect;
	}

	public String getVarietyType() {
		return varietyType;
	}

	public void setVarietyType(String varietyType) {
		this.varietyType = varietyType;
	}

	public List<CashVo> getCashVoList() {
		return cashVoList;
	}

	public void setCashVoList(List<CashVo> cashVoList) {
		this.cashVoList = cashVoList;
	}

	public CashVo getCompanyCashVo() {
		return companyCashVo;
	}

	public void setCompanyCashVo(CashVo companyCashVo) {
		this.companyCashVo = companyCashVo;
	}

	public ProdOrTradeCashMain getProdOrTradeCashMain() {
		return prodOrTradeCashMain;
	}

	public void setProdOrTradeCashMain(ProdOrTradeCashMain prodOrTradeCashMain) {
		this.prodOrTradeCashMain = prodOrTradeCashMain;
	}

	public String getVarietyTypeAddCash() {
		return varietyTypeAddCash;
	}

	public void setVarietyTypeAddCash(String varietyTypeAddCash) {
		this.varietyTypeAddCash = varietyTypeAddCash;
	}

	public List<SelectItem> getVarietySelectAdd() {
		return varietySelectAdd;
	}

	public void setVarietySelectAdd(List<SelectItem> varietySelectAdd) {
		this.varietySelectAdd = varietySelectAdd;
	}


}
