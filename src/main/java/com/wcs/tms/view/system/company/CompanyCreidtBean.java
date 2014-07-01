package com.wcs.tms.view.system.company;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Months;
import org.primefaces.model.LazyDataModel;

import com.google.common.collect.Lists;
import com.wcs.base.service.EntityService;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.view.ViewBaseBean;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.controller.helper.PageModel;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.Credit;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.service.system.company.CreditService;
import com.wcs.tms.util.MessageUtils;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 公司银行授信管理</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@ManagedBean
@ViewScoped
public class CompanyCreidtBean extends ViewBaseBean<Credit> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /** 公司Id*/
    private Long companyId;
    /** 一级机构银行下拉*/
    private List<SelectItem> topLevelSelect = new ArrayList<SelectItem>();
    /** 支行下拉*/
    private List<SelectItem> branchSelect = new ArrayList<SelectItem>();
    /** 资金币种下拉*/
    private List<SelectItem> currencySelect = new ArrayList<SelectItem>();
    /** 信用信息*/
    private List<CreditVo> creditVoList = Lists.newArrayList();
    private LazyDataModel<CreditVo> creditLazyModel;
    private CreditVo creditVo = new CreditVo();
    private CreditVo editDelCreaditVo = new CreditVo();
    @Inject
    private EntityService entityService;
    @Inject
    private BankService bacnkSerice;
    @Inject
    private CommonBean dictBean;
    @Inject
    private CreditService creditService;
    private Logger log = Logger.getLogger(CompanyCreidtBean.class);
    public CompanyCreidtBean() {
        this.setupPage("/faces/system/companyManage/companyCredit.xhtml", "/faces/system/companyManage/comManage-list.xhtml",
                null);
    }

    @PostConstruct
    public void init() {
        currencySelect = dictBean.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
        topLevelSelect = bacnkSerice.getTopLevelSelect();
        Object obj = JSFUtils.getRequestParam("compId");
        if (obj != null) {
            companyId = Long.parseLong(obj.toString());
            List<Credit> list = creditService.findCreditFetchBank(companyId);
            if (!list.isEmpty()) {
                creditVoList.clear();
                for (Credit c : list) {
                    creditVoList.add(transformInsanceToVo(c));
                    creditLazyModel = new PageModel<CreditVo>(creditVoList, false);
                }
            }
        }
    }

    /**
     * 
     * <p>Description: 总行联动支行</p>
     */
    public void bankChange() {
        branchSelect = creditService.findBranchBankSelect(this.creditVo.getTopBankId());
    }

    /**
     * 
     * <p>Description: 逻辑添加授信</p>
     */
    public void logicAddCreidt() {
        Bank bank = this.entityService.find(Bank.class, creditVo.getBankId());
        Bank topBank = this.entityService.find(Bank.class, creditVo.getTopBankId());
        int flag = validationCredit(creditVo);
        if (flag == 1) {
            return;
        }
        if (creditVo.getLastYearCreidt() != null && creditVo.getLastYearCreidt() > 0.0) {
            if (creditVo.getLastCreditLineCu() == null) {
                MessageUtils.addErrorMessage("msg", "请为上年授信选择币别");
                return;
            }
            // 插入一条已经失效的上一年数据
            CreditVo credit = new CreditVo();
            credit.setBankId(creditVo.getBankId());
            credit.setBankName(bank.getBankName());
            credit.setTopBankId(creditVo.getTopBankId());
            credit.setTopBankName(topBank.getBankName());
            credit.setCreditLine(creditVo.getLastYearCreidt());
            credit.setCreditLineCu(creditVo.getLastCreditLineCu());
            // 起始日期与结束日期相差的时间间隔
            DateTime startTime = DateUtil.dateToDateTime(creditVo.getCreditStart());
            DateTime endTime = DateUtil.dateToDateTime(creditVo.getCreditEnd());
            // 相差月份
            int spacingMonth = Months.monthsBetween(startTime, endTime).getMonths();
            DateTime newStart = startTime.minusMonths(spacingMonth + 1);
            DateTime newEnd = endTime.minusDays(spacingMonth+1);
            credit.setCreditStart(DateUtil.dateTimeToDate(newStart));
            credit.setCreditEnd(DateUtil.dateTimeToDate(newEnd));
            credit.setStatus("N");
            int flag1 = validationCredit(credit);
            if (flag1 == 1) {
                return;
            }
            creditVoList.add(credit);
            creditVo.setLastCreditLineCu(null);
            creditVo.setLastYearCreidt(null);
        }
        creditVo.setBankName(bank.getBankName());
        creditVo.setTopBankName(topBank.getBankName());
        creditVo.setStatus("Y");
        this.creditVoList.add(creditVo);
        creditLazyModel = new PageModel<CreditVo>(creditVoList, false);
        // 情况VO
        creditVo = new CreditVo();
        //add on 2013-12-2 by yan添加操作逻辑添加改为物理添加
        this.addCreidtPhysical();
        MessageUtils.addSuccessMessage("msg", "添加授信成功");
    }

    /**
     * 
     * <p>Description: 添加之前</p>
     */
    public void beffAdd() {
        branchSelect.clear();
        branchSelect.add(new SelectItem(editDelCreaditVo.getBankId(), editDelCreaditVo.getBankName()));
        this.addCreidtPhysical();
    }

    /**
     * 
     * <p>Description: 物理保存授信</p>
     * @return
     */
    public void addCreidtPhysical() {
        try {
            List<Credit> creditList = Lists.newArrayList();
            for (CreditVo creditVo : creditVoList) {
                creditList.add(tansformVoToEntity(creditVo));
            }
            this.creditService.batchSaveCredit(companyId, creditList);
        } catch (ServiceException e) {
            log.error("addCreidtPhysical方法 错误信息：" + e.getMessage());
            MessageUtils.addErrorMessage("msg", "保存授信失败");
        }
    }

    /**
     * 
     * <p>Description: 授信删除</p>
     */
    public void deleteCreditVo() {
        creditVoList.remove(editDelCreaditVo);
        creditLazyModel = new PageModel<CreditVo>(creditVoList, false);
        //add on 2013-12-2 by yan添加操作逻辑添加改为物理添加
        this.addCreidtPhysical();
        MessageUtils.addSuccessMessage("msg", "删除授信成功");
    }

    /**
     * 
     * <p>Description: 授信编辑</p>
     */
    public void editCredit() {
        // 修改的同一个引用对象
        creditLazyModel = new PageModel<CreditVo>(creditVoList, false);
        this.addCreidtPhysical();
        MessageUtils.addSuccessMessage("msg", "修改授信成功");
    }

    /**
     * 
     * <p>Description: 跳转到公司授信维护界面</p>
     * @return
     */
    public String goInput() {
        return this.getListPage();
    }

    private int validationCredit(CreditVo credit) {
        if (!creditVoList.isEmpty()) {
            for (CreditVo c : creditVoList) {
                // 判定是否是同一家总行
                if (c.getTopBankId().equals(credit.getTopBankId())) {
                    // 判断授信时间
                    if (c.getCreditStart().equals(credit.getCreditStart()) || c.getCreditEnd().equals(credit.getCreditStart())
                            || c.getCreditEnd().equals(credit.getCreditEnd())
                            || c.getCreditStart().equals(credit.getCreditEnd())) {
                        MessageUtils.addErrorMessage("msg", "该授信时间范围已经存在同样的总行信息");
                        return 1;
                    }
                    if (DateUtil.dateToDateTime(c.getCreditStart()).get(DateTimeFieldType.year()) == DateUtil.dateToDateTime(
                            credit.getCreditStart()).get(DateTimeFieldType.year())) {
                        if (credit.getCreditStart().after(c.getCreditStart())
                                && credit.getCreditStart().before(c.getCreditEnd())) {
                            MessageUtils.addErrorMessage("msg", "该授信时间范围已经存在同样的总行信息");
                            return 1;
                        }
                        if (credit.getCreditEnd().after(c.getCreditStart()) && credit.getCreditEnd().before(c.getCreditEnd())) {
                            MessageUtils.addErrorMessage("msg", "该授信时间范围已经存在同样的总行信息");
                            return 1;
                        }
                        if (credit.getCreditStart().before(c.getCreditStart())
                                && credit.getCreditEnd().after(c.getCreditStart())) {
                            MessageUtils.addErrorMessage("msg", "该授信时间范围已经存在同样的总行信息");
                            return 1;
                        }
                    } else {
                        if (credit.getCreditStart().after(c.getCreditStart())
                                && credit.getCreditStart().before(c.getCreditEnd())) {
                            MessageUtils.addErrorMessage("msg", "该授信时间范围已经存在同样的总行信息");
                            return 1;
                        }
                        if (credit.getCreditEnd().after(c.getCreditStart())
                                && credit.getCreditEnd().before(c.getCreditEnd())) {
                            MessageUtils.addErrorMessage("msg", "该授信时间范围已经存在同样的总行信息");
                            return 1;
                        }
                       
                    }

                }
            }
        }
        return 0;
    }

    /**
     * 
     * <p>Description: 授信实体转换成VO</p>
     * @param credit
     * @return
     */
    private CreditVo transformInsanceToVo(Credit credit) {
        CreditVo vo = new CreditVo();
        vo.setId(credit.getId());
        vo.setBankId(credit.getBank().getId());
        vo.setComanyId(companyId);
        // top bank
        Bank bank = entityService.find(Bank.class, credit.getBank().getTopBankId());
        vo.setTopBankName(bank.getBankName());
        vo.setTopBankId(bank.getId());
        vo.setBankName(credit.getBank().getBankName());
        vo.setCreditLine(credit.getCreditLine());
        vo.setCreditLineCu(credit.getCreditLineCu());
        vo.setCreditStart(credit.getCreditStart());
        vo.setCreditEnd(credit.getCreditEnd());
        vo.setStatus(credit.getStatus());
        return vo;
    }

    /**
     * 
     * <p>Description: Vo转换成Entity</p>
     * @param creidtVo
     * @return
     */
    private Credit tansformVoToEntity(CreditVo creidtVo) {
    	Credit credit = new Credit();
    	if(creidtVo.getId()!=null){
    		credit = this.entityService.find(Credit.class, creidtVo.getId());
    	}   	
        Bank bank = this.entityService.find(Bank.class, creidtVo.getBankId());
        credit.setBank(bank);
        credit.setCreditLine(creidtVo.getCreditLine());
        credit.setCreditLineCu(creidtVo.getCreditLineCu());
        credit.setCreditStart(creidtVo.getCreditStart());
        credit.setCreditEnd(creidtVo.getCreditEnd());
        credit.setStatus(creidtVo.getStatus());
        return credit;
    }

    public List<SelectItem> getTopLevelSelect() {
        return topLevelSelect;
    }

    public void setTopLevelSelect(List<SelectItem> topLevelSelect) {
        this.topLevelSelect = topLevelSelect;
    }

    public List<SelectItem> getBranchSelect() {
        return branchSelect;
    }

    public void setBranchSelect(List<SelectItem> branchSelect) {
        this.branchSelect = branchSelect;
    }

    public List<SelectItem> getCurrencySelect() {
        return currencySelect;
    }

    public void setCurrencySelect(List<SelectItem> currencySelect) {
        this.currencySelect = currencySelect;
    }

    public List<CreditVo> getCreditVoList() {
        return creditVoList;
    }

    public void setCreditVoList(List<CreditVo> creditVoList) {
        this.creditVoList = creditVoList;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public CreditVo getCreditVo() {
        return creditVo;
    }

    public void setCreditVo(CreditVo creditVo) {
        this.creditVo = creditVo;
    }

    public LazyDataModel<CreditVo> getCreditLazyModel() {
        return creditLazyModel;
    }

    public void setCreditLazyModel(LazyDataModel<CreditVo> creditLazyModel) {
        this.creditLazyModel = creditLazyModel;
    }

    public CreditVo getEditDelCreaditVo() {
        return editDelCreaditVo;
    }

    public void setEditDelCreaditVo(CreditVo editDelCreaditVo) {
        this.editDelCreaditVo = editDelCreaditVo;
    }

}
