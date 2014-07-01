package com.wcs.tms.service.system.company;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.common.model.O;
import com.wcs.common.model.S;
import com.wcs.tms.model.Company;

/**
 * <p>Project: tms</p>
 * <p>Description: 公司管理Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Stateless
public class CompanyTmsService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(CompanyTmsService.class);
	
	@Inject
	EntityService entityService;
	@Inject
	LoginService loginService;

	public List<Company> findCompanyForLazy(Map<String, Object> conditionMap) {
		StringBuilder jpql = new StringBuilder("select c from Company c where c.defunctInd = 'N' ");
		if (null != conditionMap.get("companyName") && !"".equals(conditionMap.get("companyName"))) {
			String lowCompanyName = conditionMap.get("companyName").toString().toLowerCase();
			jpql.append(" and lower(c.companyName) like '%" + lowCompanyName + "%'");
		}
		if (null != conditionMap.get("companyEn") && !"".equals(conditionMap.get("companyEn"))) {
			String lowCompanyEn = conditionMap.get("companyEn").toString().toLowerCase();
			jpql.append(" and lower(c.companyEn) like '%" + lowCompanyEn + "%'");
		}
		if (null != conditionMap.get("sapCode") && !"".equals(conditionMap.get("sapCode"))) {
			String lowSapCode = conditionMap.get("sapCode").toString().toLowerCase();
			jpql.append(" and lower(c.sapCode) like '%" + lowSapCode + "%'");
		}
		if (null != conditionMap.get("corporationFlag") && !"all".equals(conditionMap.get("corporationFlag"))) {
			jpql.append(" and c.corporationFlag = '" + conditionMap.get("corporationFlag") + "'");
		}
		return entityService.find(jpql.toString());
	}
	
	public LazyDataModel<Company> findCompanyLazyDataModle(Map<String, Object> conditionMap){
		StringBuilder jpql = new StringBuilder("select c from Company c where c.defunctInd = 'N' ");
		if (null != conditionMap.get("companyName") && !"".equals(conditionMap.get("companyName"))) {
			String lowCompanyName = conditionMap.get("companyName").toString().toLowerCase();
			jpql.append(" and lower(c.companyName) like '%" + lowCompanyName + "%'");
		}
		if (null != conditionMap.get("companyEn") && !"".equals(conditionMap.get("companyEn"))) {
			String lowCompanyEn = conditionMap.get("companyEn").toString().toLowerCase();
			jpql.append(" and lower(c.companyEn) like '%" + lowCompanyEn + "%'");
		}
		if (null != conditionMap.get("sapCode") && !"".equals(conditionMap.get("sapCode"))) {
			String lowSapCode = conditionMap.get("sapCode").toString().toLowerCase();
			jpql.append(" and lower(c.sapCode) like '%" + lowSapCode + "%'");
		}
		if (null != conditionMap.get("corporationFlag") && !"all".equals(conditionMap.get("corporationFlag"))) {
			jpql.append(" and c.corporationFlag = '" + conditionMap.get("corporationFlag") + "'");
		}
		if (null != conditionMap.get("state") && !"all".equals(conditionMap.get("state"))) {
			jpql.append(" and c.status = '" + conditionMap.get("state") + "'");
		}
		return entityService.findLazyModel(jpql.toString());
	}

	/**
	 * 检查sap代码唯一性
	 * @param sap
	 * @return
	 */
	public boolean checkSap(String sap) {
		List<Company> cs = new ArrayList<Company>();
		StringBuilder jpql = new StringBuilder("select c from Company c where c.defunctInd = 'N'");
		jpql.append(" and c.sapCode = '" + sap + "'");
		cs = entityService.find(jpql.toString());
		if (cs.size() > 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 公司保存
	 * @param company
	 */
	public void saveOrUpdate(Company company) {
		Long id = company.getId();
		if (id == null) {
			company.setStatus("Y");
			entityService.create(company);
			this.createOByCompany(company);
		} else {
			entityService.update(company);
		}
	}

	/**
	 * 根据公司信息添加默认组织
	 * 默认添加规则：        SAP##OSAP010001    	公司名称
					SAP##OSAP011001    SAP-财务部
					SAP##OSAP011002    SAP-总经办
					SAP##OSAP011011    SAP-资金组(父级是 财务部)
	 * @param company qas和生产默认parent:20000001
	 */
	private void createOByCompany(Company company) {
		String sap = company.getSapCode();
		String compCode = sap + "##" + "O" + sap +"010001";
		String financeCode = sap + "##" + "O" + sap +"011001";
		String generalCode = sap + "##" + "O" + sap +"011002";
		String fundCode = sap + "##" + "O" + sap +"011011";
		List<String> codeList = new ArrayList<String>();
		codeList.add(financeCode);
		codeList.add(generalCode);
		List<String> nameList = new ArrayList<String>();
		nameList.add(sap + "-财务部");
		nameList.add(sap + "-总经办");
		// 公司记录
		O o = new O();
		o.setId(compCode);
		o.setBukrs(sap);
		o.setStext(company.getCompanyName());
		o.setParent("20000001");
		o.setDefunctInd("N");
		entityService.create(o);
		// 财务部和总经办记录
		for(String code : codeList){
			O o1 = new O();
			o1.setId(code);
			o1.setBukrs(sap);
			o1.setStext(nameList.get(codeList.indexOf(code)));
			o1.setParent(compCode);
			o1.setDefunctInd("N");
			entityService.create(o1);
		}
		// 资金组记录
		O o2 = new O();
		o2.setId(fundCode);
		o2.setBukrs(sap);
		o2.setStext(sap + "-资金组");
		o2.setParent(financeCode);
		o2.setDefunctInd("N");
		entityService.create(o2);
		this.createSByOCode(sap,financeCode,generalCode,fundCode);
	}

	private void createSByOCode(String sap, String financeCode,
			String generalCode, String fundCode) {
		// 总经办默认岗位添加
		S s1 = new S();
		s1.setId(sap + "##S" + sap +"0002");
		s1.setStext(sap+"-总经理");
		s1.setDefunctInd("N");
		s1.setOid(generalCode);
		entityService.create(s1);
		// 财务部
		S s2 = new S();
		s2.setId(sap + "##S" + sap +"0003");
		s2.setStext(sap+"-财务经理");
		s2.setDefunctInd("N");
		s2.setOid(financeCode);
		entityService.create(s2);
		// 资金组
		List<String> sNames = new ArrayList<String>();
		sNames.add("-资金人员");
		sNames.add("-工厂资金人员-申请人");
		sNames.add("-资金计划员");
		sNames.add("-开销户-申请人");
		List<String> nums = new ArrayList<String>();
		nums.add("0004");
		nums.add("0005");
		nums.add("0006");
		nums.add("0008");
		for(String sName : sNames){
			S s3 = new S();
			s3.setId(sap + "##S" + sap +nums.get(sNames.indexOf(sName)));
			s3.setStext(sap+sName);
			s3.setDefunctInd("N");
			s3.setOid(fundCode);
			entityService.create(s3);
		}
	}

	/**
	 * 初始化业务相关公司下拉菜单
	 * @return
	 */
	public List<SelectItem> getCompanySelect() {
		List<Company> companys = new ArrayList<Company>();
		List<String> saps = loginService.findCompanySapCode();
		StringBuilder jpql = new StringBuilder("select c from Company c where c.defunctInd = 'N' and c.status = 'Y'");
		if (saps != null && saps.size() != 0) {
			jpql.append(" and c.sapCode in (?1)");
			companys = entityService.find(jpql.toString(), saps);
		} else {
			companys = entityService.find(jpql.toString());
		}
		List<SelectItem> selects = new ArrayList<SelectItem>();
		for (Company c : companys) {
			SelectItem si = new SelectItem(c.getId(), c.getCompanyName());
			selects.add(si);
		}
		return selects;
	}

	/**
	 * 初始化业务相关集团公司下拉菜单
	 * @return
	 */
	public List<SelectItem> getBlocCompanySelect() {
		List<Company> companys = new ArrayList<Company>();
		List<String> saps = loginService.findCompanySapCode();
		StringBuilder jpql = new StringBuilder("select c from Company c where c.defunctInd = 'N' and c.status = 'Y' and c.corporationFlag = 'Y'");
		if (saps != null && saps.size() != 0) {
			jpql.append(" and c.sapCode in (?1)");
			companys = entityService.find(jpql.toString(), saps);
		} else {
			companys = entityService.find(jpql.toString());
		}
		List<SelectItem> selects = new ArrayList<SelectItem>();
		for (Company c : companys) {
			SelectItem si = new SelectItem(c.getId(), c.getCompanyName());
			selects.add(si);
		}
		return selects;
	}

	/**
	 * 初始化所有公司下拉菜单
	 * @return
	 */
	public List<SelectItem> getAllCompanySelect() {
		List<Company> companys = new ArrayList<Company>();
		StringBuilder jpql = new StringBuilder("select c from Company c where c.defunctInd = 'N' and c.status = 'Y'");
		companys = entityService.find(jpql.toString());
		List<SelectItem> selects = new ArrayList<SelectItem>();
		for (Company c : companys) {
			SelectItem si = new SelectItem(c.getId(), c.getCompanyName());
			selects.add(si);
		}
		return selects;
	}

	/**
	 * 根据公司准确的名称获得其ID
	 * @param company
	 * @return
	 */
	public Long getCompanyName(String company) {
		Long cs;
		StringBuilder jpql = new StringBuilder("select c.id from Company c where c.defunctInd = 'N'");
		jpql.append(" and c.companyName = '" + company + "'");
		log.info("jpql:" + jpql);
		cs = entityService.findUnique(jpql.toString());
		log.info("cs:" + cs);
		return cs;
	}
	
	/**
	 * 根据sap代码查询company
	 * @param sapCode
	 * @return
	 */
	public Company getCompanyBySapcode(String sapCode) {
		StringBuilder jpql = new StringBuilder("select c from Company c where c.defunctInd = 'N'");
		jpql.append(" and c.sapCode = '" + sapCode + "'");
		return entityService.findUnique(jpql.toString());
	}
}
