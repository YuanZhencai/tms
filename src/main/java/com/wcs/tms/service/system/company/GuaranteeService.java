package com.wcs.tms.service.system.company;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.wcs.base.service.EntityService;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.Guarantee;


/**
 * 
 * <p>Project: tms</p>
 * <p>Description:公司历史担保信息管理Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Stateless
public class GuaranteeService implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	EntityService entityService;
	
	/**
	 * 担保方查担保信息
	 * @param compId
	 * @return
	 */
	public List<Guarantee> findGuaranteesBySecured(Long compId){
		List<Guarantee> guaranteesList = new ArrayList<Guarantee>();
		StringBuilder jpql = new StringBuilder("select g from Guarantee g");
		jpql.append(" join fetch g.insuredCom");
		jpql.append(" join fetch g.securedCom");
		jpql.append(" join fetch g.bank");
		jpql.append(" where g.defunctInd = 'N'");
		jpql.append(" and g.securedCom.id = " + compId);
		guaranteesList = entityService.find(jpql.toString());
		return guaranteesList;
	}
	
	/**
	 * 得到担保信息实体
	 * @param guaId
	 * @return
	 */
	public Guarantee findGuaranteeById(Long guaId){
		List<Guarantee> guaranteesList = new ArrayList<Guarantee>();
		Guarantee g = new Guarantee();
		StringBuilder jpql = new StringBuilder("select g from Guarantee g");
		jpql.append(" join fetch g.insuredCom");
		jpql.append(" join fetch g.securedCom");
		jpql.append(" join fetch g.bank");
		jpql.append(" where g.defunctInd = 'N'");
		jpql.append(" and g.id = " + guaId);
		guaranteesList = entityService.find(jpql.toString());
		if(guaranteesList.size()>0){
			g = guaranteesList.get(0);
		}
		return g;
	}
	
	/**
	 * 保存担保信息
	 * @param gua
	 */
	public void saveOrUpdate(Guarantee gua){
		gua.setSecuredCom(entityService.find(Company.class,gua.getSecuredCom().getId()));
		gua.setInsuredCom(entityService.find(Company.class,gua.getInsuredCom().getId()));
		gua.setBank(entityService.find(Bank.class,gua.getBank().getId()));
		Long id = gua.getId();
    	if(id==null){
    		// 管理员添加担保信息“净资产”默认为0
    		gua.setSecuredAssets(0.0);
    		entityService.create(gua);
    	}else{
    		entityService.update(gua);
    	}
	}
	
	/**
	 * 删除担保信息
	 * @param gua
	 */
	public void delete(Guarantee gua){
		gua.setSecuredCom(entityService.find(Company.class,gua.getSecuredCom().getId()));
		gua.setInsuredCom(entityService.find(Company.class,gua.getInsuredCom().getId()));
		gua.setBank(entityService.find(Bank.class,gua.getBank().getId()));
		gua.setDefunctInd("Y");
    	entityService.update(gua);
    	
	}
}
