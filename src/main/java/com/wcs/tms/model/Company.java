package com.wcs.tms.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;
import com.wcs.base.util.StringUtils;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:公司实体</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Entity
@Table(name = "COMPANY")
public class Company extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "COMPANY_NAME", length = 255)
    private String companyName;

    @Column(name = "COMPANY_EN", length = 255)
    private String companyEn;

    @Column(name = "SAP_CODE", length = 100)
    private String sapCode;

    @Column(name = "INVEST_TOTAL", precision = 12, scale = 4)
    private Double investTotal;

    @Column(name = "INVEST_CURRENCY", length = 101)
    private String investCurrency;

    @Column(name = "CORPORATION_FLAG", length = 1)
    private String corporationFlag;

    @Column(name = "STATUS", length = 1)
    private String status;

    
    @Column(name = "ADDR_CN", length = 255)
    private String addrCn;
    
    @Column(name = "ADDR_EN", length = 255)
    private String addrEn;
    
    @Column(name = "USED_INVEST_REG_REMA") 
    private Double usedInvestRegRema;
    
    @Column(name = "IS_INVEST_REG_REMA_AVAI") 
    private String isInvestRegRemaAvai;
    
    @Column(name = "INVEST_REG_REMA_FUNDS", precision = 12, scale = 4) 
    private Double investRegRemaFunds;
    
    @Column(name = "INVEST_REG_REMA_FUNDS_CU") 
    private String investRegRemaFundsCu;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    private List<ShareHolder> shareHolders = new ArrayList<ShareHolder>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    private List<ProcRegiCapital> procRegiCapitals = new ArrayList<ProcRegiCapital>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    private List<ProcDebtBorrow> procDebtBorrows = new ArrayList<ProcDebtBorrow>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    private List<Credit> credits = new ArrayList<Credit>(0);
    @OneToMany(fetch = FetchType.LAZY, mappedBy="insuredCom")
	private List<Guarantee> insuredComs = new ArrayList<Guarantee>(0);
    @OneToMany(fetch = FetchType.LAZY, mappedBy="securedCom")
	private List<Guarantee> securedComs = new ArrayList<Guarantee>(0);
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    private List<ProcBankCredit> procBankCredits = new ArrayList<ProcBankCredit>(0);
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    private List<ProcBankCreditConfirm> procBankCreditConfirms = new ArrayList<ProcBankCreditConfirm>(0);
   

    // 是否为集团公司boolean标示
    @Transient
    private boolean corporationFlagEx = false;

    // 公司注册资本
    @Transient
    private Double fondsSum;

    // 已到位注册资本
    @Transient
    private Double fondsInPlaceSum;

    //可用投注差
    @Transient
    private Double canUseInvestRegRema;
    
    
    public Company() {
    }

    public Double getCanUseInvestRegRema() {
		return canUseInvestRegRema;
	}

	public void setCanUseInvestRegRema(Double canUseInvestRegRema) {
		this.canUseInvestRegRema = canUseInvestRegRema;
	}

	public Double getUsedInvestRegRema() {
		return usedInvestRegRema;
	}

	public void setUsedInvestRegRema(Double usedInvestRegRema) {
		this.usedInvestRegRema = usedInvestRegRema;
	}

	public String getIsInvestRegRemaAvai() {
		return StringUtils.isBlankOrNull(isInvestRegRemaAvai) ? "1":isInvestRegRemaAvai;
	}

	public void setIsInvestRegRemaAvai(String isInvestRegRemaAvai) {
		this.isInvestRegRemaAvai = isInvestRegRemaAvai;
	}

	public Double getInvestRegRemaFunds() {
		return investRegRemaFunds;
	}

	public void setInvestRegRemaFunds(Double investRegRemaFunds) {
		this.investRegRemaFunds = investRegRemaFunds;
	}

	public String getInvestRegRemaFundsCu() {
		return investRegRemaFundsCu;
	}

	public void setInvestRegRemaFundsCu(String investRegRemaFundsCu) {
		this.investRegRemaFundsCu = investRegRemaFundsCu;
	}

	public String getAddrCn() {
		return addrCn;
	}

	public void setAddrCn(String addrCn) {
		this.addrCn = addrCn;
	}

	public String getAddrEn() {
		return addrEn;
	}

	public void setAddrEn(String addrEn) {
		this.addrEn = addrEn;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Double getFondsSum() {
        return fondsSum;
    }

    public void setFondsSum(Double fondsSum) {
        this.fondsSum = fondsSum;
    }

    public Double getFondsInPlaceSum() {
        return fondsInPlaceSum;
    }

    public void setFondsInPlaceSum(Double fondsInPlaceSum) {
        this.fondsInPlaceSum = fondsInPlaceSum;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyEn() {
        return companyEn;
    }

    public void setCompanyEn(String companyEn) {
        this.companyEn = companyEn;
    }

    public String getSapCode() {
        return sapCode;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public Double getInvestTotal() {
        return investTotal;
    }

    public void setInvestTotal(Double investTotal) {
        this.investTotal = investTotal;
    }

    public String getInvestCurrency() {
        return investCurrency;
    }

    public void setInvestCurrency(String investCurrency) {
        this.investCurrency = investCurrency;
    }

    public String getCorporationFlag() {
        return corporationFlag;
    }

    public void setCorporationFlag(String corporationFlag) {
        this.corporationFlag = corporationFlag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ShareHolder> getShareHolders() {
        return shareHolders;
    }

    public void setShareHolders(List<ShareHolder> shareHolders) {
        this.shareHolders = shareHolders;
    }

    public boolean isCorporationFlagEx() {
        return corporationFlagEx;
    }

    public void setCorporationFlagEx(boolean corporationFlagEx) {
        this.corporationFlagEx = corporationFlagEx;
    }

    /**
     * @return the procRegiCapitals
     */
    public List<ProcRegiCapital> getProcRegiCapitals() {
        return procRegiCapitals;
    }

    /**
     * @param procRegiCapitals the procRegiCapitals to set
     */
    public void setProcRegiCapitals(List<ProcRegiCapital> procRegiCapitals) {
        this.procRegiCapitals = procRegiCapitals;
    }

    /**
     * @return the procDebtBorrows
     */
    public List<ProcDebtBorrow> getProcDebtBorrows() {
        return procDebtBorrows;
    }

    /**
     * @param procDebtBorrows the procDebtBorrows to set
     */
    public void setProcDebtBorrows(List<ProcDebtBorrow> procDebtBorrows) {
        this.procDebtBorrows = procDebtBorrows;
    }

    public List<Credit> getCredits() {
        return credits;
    }

    public void setCredits(List<Credit> credits) {
        this.credits = credits;
    }
    
    public List<Guarantee> getInsuredComs() {
		return insuredComs;
	}

	public void setInsuredComs(List<Guarantee> insuredComs) {
		this.insuredComs = insuredComs;
	}

	public List<Guarantee> getSecuredComs() {
		return securedComs;
	}

	public void setSecuredComs(List<Guarantee> securedComs) {
		this.securedComs = securedComs;
	}

	public List<ProcBankCredit> getProcBankCredits() {
        return procBankCredits;
    }

    public void setProcBankCredits(List<ProcBankCredit> procBankCredits) {
        this.procBankCredits = procBankCredits;
    }

    public List<ProcBankCreditConfirm> getProcBankCreditConfirms() {
        return procBankCreditConfirms;
    }

    public void setProcBankCreditConfirms(List<ProcBankCreditConfirm> procBankCreditConfirms) {
        this.procBankCreditConfirms = procBankCreditConfirms;
    }

   

    @Override
    @Transient
    public String getDisplayText() {
        return null;
    }
}
