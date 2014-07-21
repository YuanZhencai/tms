package com.wcs.tms.view.process.debtpayment;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.DebtQuota;
import com.wcs.tms.model.RemittanceLineAccount;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.service.process.debtpayment.DebtPaymentService;
import com.wcs.tms.service.process.debtpayment.RegiDebtManageService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.util.MessageUtils;

@ManagedBean
@ViewScoped
public class RegiDebtManageBean {
	 private static final Log log = LogFactory.getLog(RegiDebtManageBean.class);
	 @Inject
	 CompanyTmsService companyTmsService;
	 @Inject
	 CommonBean dictBean;
	 @Inject
	 RegiDebtManageService regiDebtManageService;
	 @Inject
	 DebtPaymentService debtPaymentService;
	 
	private Company company=new Company();
	   //公司下拉菜单
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
    /** 资金币种下拉*/
    private List<SelectItem> currencySelect = new ArrayList<SelectItem>(); 
    /*** 汇款线路* */
    private List<RemittanceLineAccount> debtAcNoList;
    /**股东*/
    private List<ShareHolder> shareHolderList;
    
    /**公司外债额度详情*/
    private DebtQuota debtQuota;
    
	public DebtQuota getDebtQuota() {
		return debtQuota;
	}

	public void setDebtQuota(DebtQuota debtQuota) {
		this.debtQuota = debtQuota;
	}

	public List<ShareHolder> getShareHolderList() {
		return shareHolderList;
	}

	public void setShareHolderList(List<ShareHolder> shareHolderList) {
		this.shareHolderList = shareHolderList;
	}

	public List<RemittanceLineAccount> getDebtAcNoList() {
		return debtAcNoList;
	}

	public void setDebtAcNoList(List<RemittanceLineAccount> debtAcNoList) {
		this.debtAcNoList = debtAcNoList;
	}

	public RegiDebtManageService getRegiDebtManageService() {
		return regiDebtManageService;
	}

	public void setRegiDebtManageService(RegiDebtManageService regiDebtManageService) {
		this.regiDebtManageService = regiDebtManageService;
	}

	public DebtPaymentService getDebtPaymentService() {
		return debtPaymentService;
	}

	public void setDebtPaymentService(DebtPaymentService debtPaymentService) {
		this.debtPaymentService = debtPaymentService;
	}

	public CommonBean getDictBean() {
		return dictBean;
	}

	public void setDictBean(CommonBean dictBean) {
		this.dictBean = dictBean;
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

	public static Log getLog() {
		return log;
	}

	public CompanyTmsService getCompanyTmsService() {
		return companyTmsService;
	}

	public void setCompanyTmsService(CompanyTmsService companyTmsService) {
		this.companyTmsService = companyTmsService;
	}

    public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@PostConstruct
    public void initRegiDebtManage(){
        log.info("init initRegiDebtManage~~~~~~~~~~~~~~");
        initDict();
        initCompany(true);
        
    }
    /**
     * 初始化数据字典
     */
    public void initDict(){
        currencySelect = dictBean.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
    }
    /*** 初始化公司下拉*/
    public void initCompany(boolean all){
    	if(all){
    		companySelect = companyTmsService.getAllCompanySelect();
    	}else{
    		companySelect = companyTmsService.getCompanySelect();
    	}
    }
	public void changeComp(){
		if(company==null||company.getId()==null){
			MessageUtils.addErrorMessage("msg", MessageUtils.getMessage("msg_required_companyNameCn"));
   		 	return;
		}
		regiDebtManageService.debt2RegiCaptial(company);
		this.debtAcNoList=this.debtPaymentService.getSelAcNoList(company.getId());
		this.debtQuota=this.regiDebtManageService.mkDebtQuota(company.getId());
	} 
}
