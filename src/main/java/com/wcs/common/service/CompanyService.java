package com.wcs.common.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.wcs.common.controller.vo.CompanyManagerModel;
import com.wcs.common.model.Companymstr;
import com.wcs.common.model.O;
import com.wcs.common.model.Taxauthority;
@Stateless
public class CompanyService {
	@PersistenceContext(unitName = "pu")
	private EntityManager em;
	public List<CompanyManagerModel> getCompanyManagerModel(CompanyManagerModel queryCondition) {
		List<CompanyManagerModel> list = new ArrayList<CompanyManagerModel>();
		StringBuilder sb = new StringBuilder();
		sb.append("select o.STEXT,COMPANYMSTR.ADDRESS,COMPANYMSTR.ZIPCODE,COMPANYMSTR.TELPHONE,TAXAUTHORITY.name,COMPANYMSTR.DEFUNCT_IND,COMPANYMSTR.id,o.BUKRS,COMPANYMSTR.type,COMPANYMSTR.desc,o.id as oid from COMPANYMSTR INNER JOIN O on O.id=COMPANYMSTR.oid INNER JOIN TAXAUTHORITY on TAXAUTHORITY.id=COMPANYMSTR.TAXAUTHORITY_ID where 1=1");
		if(queryCondition!=null){
		if (queryCondition.getStext()!=null&&!queryCondition.getStext().equals("")) {
			sb.append(" and o.STEXT like '%")
					.append(queryCondition.getStext()).append("%'");
		}
		if (queryCondition.getAddress()!=null&&!queryCondition.getAddress().equals("")) {
			sb.append(" and COMPANYMSTR.ADDRESS like '%")
					.append(queryCondition.getAddress()).append("%'");
		}
		if(queryCondition.getJgName()!=null&&!queryCondition.getJgName().equals("")){
			sb.append(" and TAXAUTHORITY.name like '%")
			.append(queryCondition.getJgName()).append("%'");
		}
		if (queryCondition.getDefuctInt()!=null&&!queryCondition.getDefuctInt().equals("")) {
			sb.append(" and COMPANYMSTR.DEFUNCT_IND ='").append(
					queryCondition.getDefuctInt()).append("'");
		}
	}
		List li = this.em.createNativeQuery(sb.toString()).getResultList();
		
		for (int i = 0; i < li.size(); i++) {
			Object[] result = (Object[]) li.get(i);
			CompanyManagerModel model = new CompanyManagerModel();
			model.setStext(result[0]==null?"":result[0].toString());
			model.setAddress(result[1]==null?"":result[1].toString());
			model.setZipcode(result[2]==null?"":result[2].toString());
			model.setTelphone(result[3]==null?"":result[3].toString());
			model.setJgName(result[4]==null?"":result[4].toString());
			model.setDefuctInt(result[5]==null?"":result[5].toString());
			model.setId(Long.valueOf(result[6].toString()));
			model.setJgCode(result[7]==null?"":result[7].toString());
			model.setType(result[8]==null?"":result[8].toString());
			model.setDesc(result[9]==null?"":result[9].toString());
			model.setOid(Long.valueOf(result[10].toString()));
			list.add(model);
		}
		return list;
	}
	public List<O> getInsertCompanyModel(CompanyManagerModel queryCondition){
		StringBuilder sb=new StringBuilder();
		sb.append("select o from O o where 1=1 ");
		if(queryCondition.getZipcode()!=null&&!queryCondition.getZipcode().equals("")){
			sb.append(" and o.bukrs like '%").append(queryCondition.getZipcode()).append("%'");
			
		}
		if(queryCondition.getStext()!=null&&!queryCondition.getStext().equals("")){
			sb.append(" and o.stext like '%").append(queryCondition.getStext()).append("%'");
		}
		return this.em.createQuery(sb.toString()).getResultList();
	}
	public List<Taxauthority> findTaxauthority(Taxauthority t){
		String sql="select t.NAME,t.address,t.CONTACTER_TELPHONE,t.id from Taxauthority t";
		if(t.getContacterName()!=null&&!t.getContacterName().equals("")){
			sql+=" where t.NAME like '%"+t.getContacterName()+"%'";
		}
		List<Taxauthority> list=new ArrayList<Taxauthority>();
		List  li =this.em.createNativeQuery(sql).getResultList();
		for(int i=0;i<li.size();i++){
			Object [] o=(Object[]) li.get(i);
			Taxauthority tt=new Taxauthority();
			tt.setName(o[0] ==null ?"":o[0].toString());
			tt.setAddress(o[1]==null?"":o[1].toString());
			tt.setContacterTelphone(o[2]==null?"":o[2].toString());
			tt.setId(o[3]==null?0l:Long.valueOf(o[3].toString()));
			list.add(tt);
		}
		return list;
	}
	public void saveOrUpdate(Companymstr companymstr,boolean flag,Taxauthority t){
		if(flag==false){
		this.em.persist(companymstr);
		}
		else{
		Companymstr cm =(Companymstr)this.em.createQuery("select c from Companymstr c where c.id = "+companymstr.getId()).getSingleResult();
		if(t!=null){
			cm.setTaxauthority(t);
		}
		cm.setAddress(companymstr.getAddress());
		cm.setDefunctInd(companymstr.getDefunctInd());
		cm.setType(companymstr.getType());
		cm.setDesc(companymstr.getDesc());
		cm.setZipcode(companymstr.getZipcode());
		cm.setTelphone(companymstr.getTelphone());
		cm.setUpdatedBy("11");
		cm.setUpdatedDatetime(new Date());
		this.em.merge(cm);
		}
		
	}
	public void updateCompany(){
		
	}


	public static void main(String[] args) {
	}
}

