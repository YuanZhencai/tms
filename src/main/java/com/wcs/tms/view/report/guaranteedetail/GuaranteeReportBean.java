package com.wcs.tms.view.report.guaranteedetail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.StringUtils;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.service.CommonService;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.Guarantee;
import com.wcs.tms.model.ProcGuarantee;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.report.guaranteedetail.GuaranteeReportService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.view.process.common.CompanySelectBean;
import com.wcs.tms.view.process.common.entity.GuaranteeReportVo;
import com.wcs.tms.view.process.common.entity.GuaranteeVo;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:集团内部担保明细Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */

@ManagedBean
@ViewScoped
public class GuaranteeReportBean implements Serializable{

	private static final long serialVersionUID = 1L;
	@Inject
	GuaranteeReportService guaranteeReportService;
	@Inject
	CompanyTmsService companyTmsService;
	@Inject
	ProcessUtilMapService processUtilMapService;
	@Inject
	CommonService commonService;
	@Inject
	LoginService loginService;
	
	//查询条件
	private Map<String, Object> conditionMap = new HashMap<String, Object>();
	//担保方vos
	private List<GuaranteeVo> guaranteeVos = new ArrayList<GuaranteeVo>();
	//人民币合计(源币种金额)
	private Double securedAssetsTotal;
	//人民币合计(担保总额)
	private Double guarAmountTotal;
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
	private List<SelectItem> companyRelatedSelect = new ArrayList<SelectItem>();
	//当前用户是否为集团人
	private Boolean isCopUser ;
	@ManagedProperty(value = "#{companySelectBean}")
	private CompanySelectBean companySelectBean;
	private Company company;
	
	@PostConstruct
	public void initGuaranteeReportBean(){
		initCompany();
		searchGuaranteeDetails();
	}
	
	/**
	 * 根据条件查询明细
	 */
	public void searchGuaranteeDetails(){
		// 报表初始化时 根据用户查出业务相关数据
		List<String> companyIds = new ArrayList<String>();
		if(isCopUser){
			List<SelectItem> companySelect = companyTmsService.getAllCompanySelect();
			for (SelectItem item : companySelect) {
				companyIds.add(String.valueOf(item.getValue()));
			}
		}else{
			//获取用户相关公司数据 add on 2013-3-11
			List<SelectItem> companySelect = companyTmsService.getCompanySelect();
			for (SelectItem item : companySelect) {
				companyIds.add(String.valueOf(item.getValue()));
			}
		}
		conditionMap.put("companyIds", companyIds);
		
		guaranteeVos.clear();
		//根据担保公司查询条件筛选出担保主数据公司即担保状态（区分担保和预担保两种态）
		List<Company> companies = guaranteeReportService.findAllDoneSecuredCompany(conditionMap);
		//流程数据公司（预担保）
		List<Company> procCompanies = guaranteeReportService.findAllPreSecuredCompany(conditionMap);
		HashMap<Company, GuaranteeVo> map = this.makeGuarVosByDoneSecuredCom(companies);
		this.makeGuarVosByDoingSecuredCom(procCompanies, map);
		caculateSecuredAssetsTotal();
		caculateGuarAmountTotal();
	}
	
	/**
	 * 根据担保流程公司写出担保方Vo
	 * @param procCompanies
	 * @param map
	 */
	private void makeGuarVosByDoingSecuredCom(List<Company> procCompanies,
			HashMap<Company, GuaranteeVo> map) {
		for(Company c : procCompanies){
			List<ProcGuarantee> procGuarantees = guaranteeReportService.findProcGuaranteeDetailsBy(c, conditionMap);
			List<GuaranteeReportVo> guaranteeReportVos = this.transferProcGuaranteeToVo(procGuarantees);
			
			if(0 != procGuarantees.size()){
				
				GuaranteeVo vo;
				// 如果预担保数据中的担保公司在担保数据中已存在
				if(map.containsKey(c)){
					vo = map.get(c);
					
					List<GuaranteeReportVo> reportVos = vo.getGuaranteeReportVos();
					// 将预担保与已担保数据合并（注：页面以担保公司分类小计）
					reportVos.addAll(guaranteeReportVos);
					vo.setGuaranteeReportVos(reportVos);
					
					vo.setGuarAmountSubtotal(this.getGuarAmountSubtotalBy(vo.getGuaranteeReportVos()));
					vo.setSecuredAssetsSubtotal(this.getSecuredAssetsSubtotalBy(vo.getGuaranteeReportVos()));
					map.put(c, vo);
				}else{
					// 如果不存在则直接添加vo数据map
					vo = new GuaranteeVo();
					vo.setGuaranteeReportVos(guaranteeReportVos);
					vo.setSecuredCompany(c);
					vo.setGuarAmountSubtotal(this.getGuarAmountSubtotalBy(vo.getGuaranteeReportVos()));
					vo.setSecuredAssetsSubtotal(this.getSecuredAssetsSubtotalBy(vo.getGuaranteeReportVos()));
					map.put(c, vo);
				}
			}
		}
		// 给页面显示数据赋值
		Iterator<Entry<Company, GuaranteeVo>> iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<Company, GuaranteeVo> entry = iter.next();
			guaranteeVos.add(entry.getValue());
		}
		
	}

	/**
	 * 根据担保明细公司写出担保方Vo
	 * @param companies
	 * @return
	 */
	private HashMap<Company, GuaranteeVo> makeGuarVosByDoneSecuredCom(
			List<Company> companies) {
		HashMap<Company, GuaranteeVo> map = new HashMap<Company, GuaranteeVo>();
		for(Company c : companies){
			// 按担保公司分类去查出满足条件的数据（方便计算小计信息）
			List<Guarantee> guarantees = guaranteeReportService.findGuaranteeDetailsBy(c, conditionMap);
			List<GuaranteeReportVo> guaranteeReportVos = this.transferGuaranteeToVo(guarantees);
			
			if(0 != guarantees.size()){
				GuaranteeVo vo = new GuaranteeVo();
				vo.setSecuredCompany(c);
				vo.setGuaranteeReportVos(guaranteeReportVos);
				vo.setGuarAmountSubtotal(this.getGuarAmountSubtotalBy(vo.getGuaranteeReportVos()));
				vo.setSecuredAssetsSubtotal(this.getSecuredAssetsSubtotalBy(vo.getGuaranteeReportVos()));
				map.put(c, vo);
			}
		}
		return map;
	}

	/**
	 * 将流程数据写到Vo中
	 * @param procGuarantees
	 * @return
	 */
	private List<GuaranteeReportVo> transferProcGuaranteeToVo(
			List<ProcGuarantee> procGuarantees) {
		List<GuaranteeReportVo> guaranteeReportVos = new ArrayList<GuaranteeReportVo>();
		for(ProcGuarantee g : procGuarantees){
			GuaranteeReportVo reportVo = new GuaranteeReportVo();
			reportVo.setCreatedDatetime(g.getCreatedDatetime());
			reportVo.setProcInstId(processUtilMapService.getTmsIdByFnId(g.getProcInstId()));
			reportVo.setSecuredCom(g.getSecuredCom());
			reportVo.setBank(g.getBank());
			reportVo.setUseDesc(g.getUseDesc());
			reportVo.setInsuredCom(g.getCompany());
			//根据币种key动态加载其value
			reportVo.setGuarAmountCu(this.getDictValueByKey(
					DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE,g.getGuarAmountCu()));
			reportVo.setSecuredAssets(Math.round(g.getSecuredAssets()*1000)/1000.0);
			reportVo.setGuarAmount(Math.round(g.getGuarAmount()*1000)/1000.0);
			//预担保“N”
			reportVo.setSecuredIdent("N");
			reportVo.setGuarStart(g.getGuarStart());
			reportVo.setGuarEnd(g.getGuarEnd());
			guaranteeReportVos.add(reportVo);
		}
		return guaranteeReportVos;
	}

	/**
	 * 将明细数据写到Vo中
	 * @param guarantees
	 * @return
	 */
	private List<GuaranteeReportVo> transferGuaranteeToVo(
			List<Guarantee> guarantees) {
		List<GuaranteeReportVo> guaranteeReportVos = new ArrayList<GuaranteeReportVo>();
		for(Guarantee g : guarantees){
			GuaranteeReportVo reportVo = new GuaranteeReportVo();
			reportVo.setCreatedDatetime(g.getCreatedDatetime());
			reportVo.setProcInstId(processUtilMapService.getTmsIdByFnId(g.getProcInstId()));
			reportVo.setSecuredCom(g.getSecuredCom());
			reportVo.setBank(g.getBank());
			reportVo.setUseDesc(g.getUseDesc());
			reportVo.setInsuredCom(g.getInsuredCom());
			reportVo.setGuarAmountCu(this.getDictValueByKey(
					DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE,g.getGuarAmountCu()));
			reportVo.setSecuredAssets(Math.round(g.getSecuredAssets()*1000)/1000.0);
			reportVo.setGuarAmount(Math.round(g.getGuarAmount()*1000)/1000.0);
			//担保“Y”
			reportVo.setSecuredIdent("Y");
			reportVo.setGuarStart(g.getGuarStart());
			reportVo.setGuarEnd(g.getGuarEnd());
			guaranteeReportVos.add(reportVo);
		}
		return guaranteeReportVos;
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
	 * 计算人民币合计(担保总额)
	 */
	private void caculateGuarAmountTotal() {
		guarAmountTotal = 0.0;
		for(GuaranteeVo vo : guaranteeVos){
			guarAmountTotal = guarAmountTotal + vo.getGuarAmountSubtotal();
		}
	}
	
	/**
	 * 清缓存
	 */
	public void clear(){
		setCompany(new Company());
		getCompanySelectBean().clear();
	}
	
	/**
	 * 控件之间传参
	 */
	public void getSelectedCompany(){
		setCompany(getCompanySelectBean().getSelectedCompany());
		// 查询条件赋值
		conditionMap.put("insuredCompanyId", Long.toString(getCompany().getId()));
	}

	/**
	 * 计算人民币合计(源币种金额)
	 */
	private void caculateSecuredAssetsTotal() {
		securedAssetsTotal = 0.0;
		for(GuaranteeVo vo : guaranteeVos){
			securedAssetsTotal = securedAssetsTotal + vo.getSecuredAssetsSubtotal();
		}
	}

	/**
	 * 担保总额小计
	 * @param reportVos
	 * @return
	 */
	private Double getGuarAmountSubtotalBy(List<GuaranteeReportVo> reportVos) {
		Double subtotal = 0.0;
		for(GuaranteeReportVo g : reportVos){
			if(g.getGuarAmount() == null){
				continue;
			}
			subtotal = subtotal + g.getGuarAmount();
		}
		return subtotal;
	}

	/**
	 * 源币种金额小计
	 * @param guarantees
	 * @return
	 */
	private Double getSecuredAssetsSubtotalBy(List<GuaranteeReportVo> reportVos) {
		Double subtotal = 0.0;
		for(GuaranteeReportVo g : reportVos){
			if(g.getSecuredAssets() == null ){
				continue ;
			}
			subtotal = subtotal + g.getSecuredAssets();
		}
		return subtotal;
	}

	/**
     * 初始化公司下拉
     */
    public void initCompany() {
    	setCompanySelect(companyTmsService.getAllCompanySelect());
    	if(loginService.isCopUser()){
    		isCopUser = true;
    		getCompanySelectBean().initCompany();
    	}else{
    		isCopUser = false;
    		setCompanyRelatedSelect(companyTmsService.getCompanySelect());
    	}
    }

	public List<GuaranteeVo> getGuaranteeVos() {
		return guaranteeVos;
	}

	public void setGuaranteeVos(List<GuaranteeVo> guaranteeVos) {
		this.guaranteeVos = guaranteeVos;
	}

	public Double getSecuredAssetsTotal() {
		return securedAssetsTotal;
	}

	public void setSecuredAssetsTotal(Double securedAssetsTotal) {
		this.securedAssetsTotal = securedAssetsTotal;
	}

	public Double getGuarAmountTotal() {
		return guarAmountTotal;
	}

	public void setGuarAmountTotal(Double guarAmountTotal) {
		this.guarAmountTotal = guarAmountTotal;
	}


	public Map<String, Object> getConditionMap() {
		return conditionMap;
	}


	public void setConditionMap(Map<String, Object> conditionMap) {
		this.conditionMap = conditionMap;
	}


	public List<SelectItem> getCompanySelect() {
		return companySelect;
	}


	public void setCompanySelect(List<SelectItem> companySelect) {
		this.companySelect = companySelect;
	}

	public List<SelectItem> getCompanyRelatedSelect() {
		return companyRelatedSelect;
	}

	public void setCompanyRelatedSelect(List<SelectItem> companyRelatedSelect) {
		this.companyRelatedSelect = companyRelatedSelect;
	}

	public Boolean getIsCopUser() {
		return isCopUser;
	}

	public void setIsCopUser(Boolean isCopUser) {
		this.isCopUser = isCopUser;
	}

	public CompanySelectBean getCompanySelectBean() {
		return companySelectBean;
	}

	public void setCompanySelectBean(CompanySelectBean companySelectBean) {
		this.companySelectBean = companySelectBean;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	
}
