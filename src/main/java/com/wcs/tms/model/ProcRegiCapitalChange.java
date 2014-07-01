package com.wcs.tms.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;


/**
 * The persistent class for the PROC_REGI_CAPITAL_CHANGE database table.
 * 
 */
@Entity
@Table(name="PROC_REGI_CAPITAL_CHANGE")
@NamedQuery(name="ProcRegiCapitalChange.findAll", query="SELECT p FROM ProcRegiCapitalChange p")
public class ProcRegiCapitalChange extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/** 保存PE的备注*/
	@Transient
	private String peMemo;
	
	/** 公司*/
	@ManyToOne
	@JoinColumn(name = "COMPANY_ID")
	private Company company;

	@Column(name="CURRENT_NODE")
	private String currentNode;

	@Column(name="INVEST_CURRENCY")
	private String investCurrency;

	@Column(name="INVEST_CURRENCY_ORI")
	private String investCurrencyOri;

	@Column(name="INVEST_REG_REMA_FUNDS")
	private Double investRegRemaFunds;

	@Column(name="INVEST_REG_REMA_FUNDS_CU")
	private String investRegRemaFundsCu;

	@Column(name="INVEST_REG_REMA_FUNDS_CU_ORI")
	private String investRegRemaFundsCuOri;

	@Column(name="INVEST_REG_REMA_FUNDS_ORI")
	private Double investRegRemaFundsOri;

	@Column(name="INVEST_TOTAL")
	private Double investTotal;

	@Column(name="INVEST_TOTAL_ORI")
	private Double investTotalOri;

	@Column(name="IS_INVEST_REG_REMA_AVAI")
	private String isInvestRegRemaAvai;

	@Column(name="IS_INVEST_REG_REMA_AVAI_ORI")
	private String isInvestRegRemaAvaiOri;

	@Column(name="PROC_INST_ID")
	private String procInstId;

	@Column(name="PROCESS_STATUS")
	private String processStatus;

	//bi-directional many-to-one association to ProcRegiCapitalChangeShareholder
	@OneToMany(mappedBy="procRegiCapitalChange", cascade = CascadeType.ALL)
	private List<ProcRegiCapitalChangeShareholder> procRegiCapitalChangeShareholders;

	public ProcRegiCapitalChange() {
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}


	public String getCurrentNode() {
		return this.currentNode;
	}

	public void setCurrentNode(String currentNode) {
		this.currentNode = currentNode;
	}

	public String getInvestCurrency() {
		return this.investCurrency;
	}

	public void setInvestCurrency(String investCurrency) {
		this.investCurrency = investCurrency;
	}

	public String getInvestCurrencyOri() {
		return this.investCurrencyOri;
	}

	public void setInvestCurrencyOri(String investCurrencyOri) {
		this.investCurrencyOri = investCurrencyOri;
	}

	public Double getInvestRegRemaFunds() {
		return this.investRegRemaFunds;
	}

	public void setInvestRegRemaFunds(Double investRegRemaFunds) {
		this.investRegRemaFunds = investRegRemaFunds;
	}

	public String getInvestRegRemaFundsCu() {
		return this.investRegRemaFundsCu;
	}

	public void setInvestRegRemaFundsCu(String investRegRemaFundsCu) {
		this.investRegRemaFundsCu = investRegRemaFundsCu;
	}

	public String getInvestRegRemaFundsCuOri() {
		return this.investRegRemaFundsCuOri;
	}

	public void setInvestRegRemaFundsCuOri(String investRegRemaFundsCuOri) {
		this.investRegRemaFundsCuOri = investRegRemaFundsCuOri;
	}

	public Double getInvestRegRemaFundsOri() {
		return this.investRegRemaFundsOri;
	}

	public void setInvestRegRemaFundsOri(Double investRegRemaFundsOri) {
		this.investRegRemaFundsOri = investRegRemaFundsOri;
	}

	public Double getInvestTotal() {
		return this.investTotal;
	}

	public void setInvestTotal(Double investTotal) {
		this.investTotal = investTotal;
	}

	public Double getInvestTotalOri() {
		return this.investTotalOri;
	}

	public void setInvestTotalOri(Double investTotalOri) {
		this.investTotalOri = investTotalOri;
	}

	public String getIsInvestRegRemaAvai() {
		return this.isInvestRegRemaAvai;
	}

	public void setIsInvestRegRemaAvai(String isInvestRegRemaAvai) {
		this.isInvestRegRemaAvai = isInvestRegRemaAvai;
	}

	public String getIsInvestRegRemaAvaiOri() {
		return this.isInvestRegRemaAvaiOri;
	}

	public void setIsInvestRegRemaAvaiOri(String isInvestRegRemaAvaiOri) {
		this.isInvestRegRemaAvaiOri = isInvestRegRemaAvaiOri;
	}

	public String getProcInstId() {
		return this.procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getProcessStatus() {
		return this.processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public List<ProcRegiCapitalChangeShareholder> getProcRegiCapitalChangeShareholders() {
		return this.procRegiCapitalChangeShareholders;
	}

	public void setProcRegiCapitalChangeShareholders(List<ProcRegiCapitalChangeShareholder> procRegiCapitalChangeShareholders) {
		this.procRegiCapitalChangeShareholders = procRegiCapitalChangeShareholders;
	}

	public ProcRegiCapitalChangeShareholder addProcRegiCapitalChangeShareholder(ProcRegiCapitalChangeShareholder procRegiCapitalChangeShareholder) {
		getProcRegiCapitalChangeShareholders().add(procRegiCapitalChangeShareholder);
		procRegiCapitalChangeShareholder.setProcRegiCapitalChange(this);

		return procRegiCapitalChangeShareholder;
	}

	public ProcRegiCapitalChangeShareholder removeProcRegiCapitalChangeShareholder(ProcRegiCapitalChangeShareholder procRegiCapitalChangeShareholder) {
		getProcRegiCapitalChangeShareholders().remove(procRegiCapitalChangeShareholder);
		procRegiCapitalChangeShareholder.setProcRegiCapitalChange(null);

		return procRegiCapitalChangeShareholder;
	}

	@Override
	@Transient
	public String getDisplayText() {
		return null;
	}

	public String getPeMemo() {
		return peMemo;
	}

	public void setPeMemo(String peMemo) {
		this.peMemo = peMemo;
	}

}