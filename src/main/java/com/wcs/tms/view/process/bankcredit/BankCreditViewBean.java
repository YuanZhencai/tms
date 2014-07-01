package com.wcs.tms.view.process.bankcredit;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;

import com.google.common.collect.Lists;
import com.wcs.base.util.DateUtil;
import com.wcs.base.view.ViewBaseBean;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.ProcBankCredit;
import com.wcs.tms.model.ProcRptCredit;
import com.wcs.tms.service.process.bankcredit.BankCreditService;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.util.CustomPageModel;
import com.wcs.tms.util.MessageUtils;

@ManagedBean
@ViewScoped
public class BankCreditViewBean extends ViewBaseBean<ProcBankCredit> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(BankCreditViewBean.class);
    
    /** 申请日期*/
    private String registerDate;
    /** 选择的公司Id*/
    private Long companyId;
    /** 资金币种下拉*/
    private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
    /** 公司中午名称下拉选择框*/
    private List<SelectItem> companyNameSelect = new ArrayList<SelectItem>();
    /** 银行总行Id*/
    private Long topBankId;
    /** 一级机构银行下拉*/
    private List<SelectItem> topLevelSelect = new ArrayList<SelectItem>();
    /** 选择支行Id*/
    private Long branchSelectId;
    /** 支行下拉*/
    private List<SelectItem> branchSelect = new ArrayList<SelectItem>();
    private ProcRptCredit rptCredit = new ProcRptCredit();
    /** 银行授信申请其他产品*/
    private List<ProcRptCredit> proRptCreditList = Lists.newArrayList();
    /** 银行授信申请其他产品分页模型*/
    private LazyDataModel<ProcRptCredit> propLayModel;
    
    @Inject
    private BankService bacnkSerice;
    @Inject
    private CommonBean dictBean;
    @Inject
    private CompanyTmsService companyTmsService;
    @Inject
    private BankCreditService bankCreditService;

    public void init() {
        // 是否是审批和查看
        if (getInstance().getProcInstId() != null) {
            String wcnum = getInstance().getProcInstId();
            initdata();
            // 查询其他授信产品,用addAll 查询的结果是只读
            List<ProcRptCredit> list = this.bankCreditService.findProcRptCreditList(wcnum);
            proRptCreditList.addAll(list);
            propLayModel = new CustomPageModel<ProcRptCredit>(proRptCreditList, false);
            fillData(wcnum);
        }
    }

    /**
     * 初始化下拉
     */
    private void initdata() {
        companyNameSelect = companyTmsService.getAllCompanySelect();
        currencySelect = dictBean.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
        topLevelSelect = bacnkSerice.getTopLevelSelect();
    }

    /**
     * 
     * <p>Description: 审批，查看时需要填充的数据</p>
     * @param workclassNumber
     */
    private void fillData(String workclassNumber) {
        if (workclassNumber != null) {
            try {
                ProcBankCredit procBankCredit = bankCreditService.findProcBankCd(workclassNumber);
                setInstance(procBankCredit);
                if (procBankCredit != null && procBankCredit.getCompany() == null) {
                    MessageUtils.addErrorMessage("bankCreditMsg", MessageUtils.getMessage("regicapital_noBound"));
                }
                // 得到公司Id
                companyId = procBankCredit.getCompany().getId();
                registerDate = DateUtil.dateToStrShort(DateUtil.dateToDateTime(procBankCredit.getApplyDate()));
                // 得到分支授信银行
                Bank bank = procBankCredit.getBank();
                if (bank != null) {
                    // 选中一级银行
                    topBankId = bank.getTopBankId();
                    branchSelectId = bank.getId();
                    branchSelect.clear();
                    // 添加分支银行下拉
                    branchSelect.add(new SelectItem(branchSelectId, bank.getBankName()));
                }
            } catch (Exception e) {
                log.error("fillData方法 审批，查看时需要填充的数据 出现异常：",e);
            }
        }
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public List<SelectItem> getCurrencySelect() {
        return currencySelect;
    }

    public void setCurrencySelect(List<SelectItem> currencySelect) {
        this.currencySelect = currencySelect;
    }

    public List<SelectItem> getCompanyNameSelect() {
        return companyNameSelect;
    }

    public void setCompanyNameSelect(List<SelectItem> companyNameSelect) {
        this.companyNameSelect = companyNameSelect;
    }

    public Long getTopBankId() {
        return topBankId;
    }

    public void setTopBankId(Long topBankId) {
        this.topBankId = topBankId;
    }

    public List<SelectItem> getTopLevelSelect() {
        return topLevelSelect;
    }

    public void setTopLevelSelect(List<SelectItem> topLevelSelect) {
        this.topLevelSelect = topLevelSelect;
    }

    public Long getBranchSelectId() {
        return branchSelectId;
    }

    public void setBranchSelectId(Long branchSelectId) {
        this.branchSelectId = branchSelectId;
    }

    public List<SelectItem> getBranchSelect() {
        return branchSelect;
    }

    public void setBranchSelect(List<SelectItem> branchSelect) {
        this.branchSelect = branchSelect;
    }

    public ProcRptCredit getRptCredit() {
        return rptCredit;
    }

    public void setRptCredit(ProcRptCredit rptCredit) {
        this.rptCredit = rptCredit;
    }

    public List<ProcRptCredit> getProRptCreditList() {
        return proRptCreditList;
    }

    public void setProRptCreditList(List<ProcRptCredit> proRptCreditList) {
        this.proRptCreditList = proRptCreditList;
    }

    public LazyDataModel<ProcRptCredit> getPropLayModel() {
        return propLayModel;
    }

    public void setPropLayModel(LazyDataModel<ProcRptCredit> propLayModel) {
        this.propLayModel = propLayModel;
    }

}
