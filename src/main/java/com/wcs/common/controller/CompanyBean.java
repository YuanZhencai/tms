package com.wcs.common.controller;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import com.wcs.common.controller.helper.PageModel;
import com.wcs.common.controller.vo.CompanyManagerModel;
import com.wcs.common.model.Companymstr;
import com.wcs.common.model.O;
import com.wcs.common.model.Taxauthority;
import com.wcs.common.service.CompanyService;

@ManagedBean
@SessionScoped
public class CompanyBean {
	@EJB
	private CompanyService companyManagerService;
	private CompanyManagerModel companyManagerModel = new CompanyManagerModel();
	private CompanyManagerModel companyInsertModel = new CompanyManagerModel();
	private CompanyManagerModel companySaveModel = new CompanyManagerModel();
	private boolean flag;

	public CompanyManagerModel getCompanySaveModel() {
		return companySaveModel;
	}

	public void setCompanySaveModel(CompanyManagerModel companySaveModel) {
		this.companySaveModel = companySaveModel;
	}

	private CompanyManagerModel insertSelect;

	public CompanyManagerModel getInsertSelect() {
		return insertSelect;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	private boolean firstBtnFlag;

	public boolean isFirstBtnFlag() {
		return firstBtnFlag;
	}

	public void setFirstBtnFlag(boolean firstBtnFlag) {
		this.firstBtnFlag = firstBtnFlag;
	}

	public void setInsertSelect(CompanyManagerModel insertSelect) {
		this.insertSelect = insertSelect;
	}

	public CompanyManagerModel getCompanyInsertModel() {
		return companyInsertModel;
	}

	public void setCompanyInsertModel(CompanyManagerModel companyInsertModel) {
		this.companyInsertModel = companyInsertModel;
	}

	public CompanyManagerModel getCompanyManagerModel() {
		return companyManagerModel;
	}

	public void setCompanyManagerModel(CompanyManagerModel companyManagerModel) {
		this.companyManagerModel = companyManagerModel;
	}

	private Taxauthority taxauthority = new Taxauthority();

	public Taxauthority getTaxauthority() {
		return taxauthority;
	}

	public void setTaxauthority(Taxauthority taxauthority) {
		this.taxauthority = taxauthority;
	}

	private LazyDataModel<CompanyManagerModel> lazyModel;
	private LazyDataModel<O> insertLazyModel;
	private LazyDataModel<Taxauthority> taxauthorityLazyModel;
	private Taxauthority selectTaxauthority;

	public LazyDataModel<Taxauthority> getTaxauthorityLazyModel() {
		return taxauthorityLazyModel;
	}

	public void setTaxauthorityLazyModel(
			LazyDataModel<Taxauthority> taxauthorityLazyModel) {
		this.taxauthorityLazyModel = taxauthorityLazyModel;
	}

	public Taxauthority getSelectTaxauthority() {
		return selectTaxauthority;
	}

	public void setSelectTaxauthority(Taxauthority selectTaxauthority) {
		this.selectTaxauthority = selectTaxauthority;
	}

	public void searchTaxauthority() {
		List<Taxauthority> bbv = companyManagerService
				.findTaxauthority(taxauthority);
		taxauthorityLazyModel = new PageModel(bbv, false);
	}

	public LazyDataModel<O> getInsertLazyModel() {
		return insertLazyModel;
	}

	public void setInsertLazyModel(LazyDataModel<O> insertLazyModel) {
		this.insertLazyModel = insertLazyModel;
	}

	public void setLazyModel(LazyDataModel<CompanyManagerModel> lazyModel) {
		this.lazyModel = lazyModel;
	}

	public void search() {
		List<CompanyManagerModel> bbv = companyManagerService
				.getCompanyManagerModel(companyManagerModel);
		lazyModel = new PageModel(bbv, false);
	}

	public void searchInsert() {
		List<O> bbv = companyManagerService
				.getInsertCompanyModel(companyInsertModel);
		insertLazyModel = new PageModel(bbv, false);
	}

	private boolean secondBtn;

	public boolean isSecondBtn() {
		return secondBtn;
	}

	public void setSecondBtn(boolean secondBtn) {
		this.secondBtn = secondBtn;
	}

	private Companymstr insertOrUpdateCompany;

	public Companymstr getInsertOrUpdateCompany() {
		return insertOrUpdateCompany;
	}

	public void setInsertOrUpdateCompany(Companymstr insertOrUpdateCompany) {
		this.insertOrUpdateCompany = insertOrUpdateCompany;
	}

	public void setNameAndCode() {
		this.companySaveModel.setStext(this.selecto.getStext());
		this.companySaveModel.setJgCode(this.selecto.getBukrs());
	}

	public LazyDataModel<CompanyManagerModel> getLazyModel() {
		return lazyModel;
	}

	public void resetForm() {
		this.companyManagerModel = new CompanyManagerModel();
	}

	public void resetInsertForm() {
		this.companyInsertModel = new CompanyManagerModel();
	}

	public void resetdataform1() {
		this.taxauthority = new Taxauthority();
	}

	public void findJgName() {
		this.secondBtn = false;
		this.companySaveModel.setJgName(this.selectt.getName());
	}

	private O selecto;
	private Taxauthority selectt;

	public void oRowSelect(SelectEvent event) {
		this.firstBtnFlag = false;
		this.selecto = (O) event.getObject();
	}

	public void tRowSelect(SelectEvent event) {
		this.selectt = ((Taxauthority) event.getObject());
	}

	public void insert() {
		this.flag = false;
		this.companySaveModel = new CompanyManagerModel();
		this.selecto = null;
		this.selectt = null;
		this.insertLazyModel = null;
		this.taxauthorityLazyModel = null;
		this.firstBtnFlag = true;
		this.secondBtn = true;
		companySaveModel.setDefuctInt("Y");
	}

	public void update() {
		this.flag = true;
		this.secondBtn = false;
	}
}
