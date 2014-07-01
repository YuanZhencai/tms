package com.wcs.common.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import com.wcs.common.controller.vo.DictVO;
import com.wcs.common.model.Dict;

@Stateless
public class DictService {
	
	@PersistenceContext(unitName = "pu") 
	private EntityManager em;
	
	//查询,模糊条件有"类别","键","值","系统标识","语言"五个.
	public List<DictVO> searchData(String codeCat,String codeKey,String codeVal,String sysInd, String lang){
		StringBuilder strSql=new StringBuilder();
		strSql.append("SELECT d FROM Dict d WHERE d.defunctInd='N' ");
		/**
		 * sonar测试 codeCat!="" && codeCat!= null
		 * 改成 StringUtils.isNotBlank(codeCat)
		 */
		if(StringUtils.isNotBlank(codeCat)){
			strSql.append(" AND upper(d.codeCat) LIKE '%" + codeCat.trim().toUpperCase() + "%' ");
		}
		/**
		 * sonar测试 codeKey!="" && codeKey!= null
		 * 改成 StringUtils.isNotBlank(codeKey)
		 */
		if(StringUtils.isNotBlank(codeKey)){
			strSql.append(" AND upper(d.codeKey) LIKE '%" + codeKey.trim().toUpperCase() + "%' ");
		}
		/**
		 * sonar测试 codeVal!="" && codeVal!= null
		 * 改成 StringUtils.isNotBlank(codeVal)
		 */
		if(StringUtils.isNotBlank(codeVal)){
			strSql.append(" AND upper(d.codeVal) LIKE '%" + codeVal.trim().toUpperCase() + "%' ");
		}
		/**
		 * sonar测试 sysInd!="" && sysInd!= null
		 * 改成 StringUtils.isNotBlank(sysInd)
		 */
		if(StringUtils.isNotBlank(sysInd)){
			strSql.append(" AND d.sysInd LIKE '%" + sysInd + "%' ");
		}
		/**
		 * sonar测试 lang !="" && lang != null
		 * 改成 StringUtils.isNotBlank(lang)
		 */
		if(StringUtils.isNotBlank(lang)){
			strSql.append(" AND upper(d.lang) LIKE '%" + lang.trim().toUpperCase() + "%' ");
		}
		Query query=em.createQuery(strSql.toString());
		@SuppressWarnings("unchecked")
		List<Dict> result=query.getResultList();
		List<DictVO> list=new ArrayList<DictVO>();
		for(Dict d: result){
			list.add(new DictVO(d.getId(), d.getCodeCat(), d.getCodeKey(), d.getCodeVal(), d.getRemarks(), d.getSeqNo(), d.getSysInd(), d.getLang(), d.getDefunctInd()));
		}
		return list;
	}
	
	//添加,顺序为"类别","键","值","顺序号","语言","系统标识","是否生效","备注".共8个参数.
	public void insertData(Dict dict){
		this.em.persist(dict);
	}
	
	//编辑
	public void updateData(DictVO selectData,String updateUser){
		StringBuilder strSql=new StringBuilder("UPDATE Dict d ");
		strSql.append("SET d.codeCat = '"+ selectData.getCodeCat() +"',");
		strSql.append("d.codeKey = '"+ selectData.getCodeKey() +"',");
		strSql.append("d.codeVal = '"+ selectData.getCodeVal() +"',");
		strSql.append("d.seqNo = '"+ selectData.getSeqNo() +"',");
		strSql.append("d.lang = '"+ selectData.getLang() +"',");
		strSql.append("d.sysInd = '"+ selectData.getSysInd() +"',");
		strSql.append("d.defunctInd = '"+ selectData.getDefunctInd() +"',");
		strSql.append("d.remarks = '"+ selectData.getRemarks() +"',");
		strSql.append("d.updatedBy = '"+ updateUser +"',");
		strSql.append("d.updatedDatetime = '"+ new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(new Date()) +"'");
		strSql.append(" WHERE d.id=" + (int)selectData.getId() + " ");
		this.em.createQuery(strSql.toString()).executeUpdate();
	}
	
	public boolean checkData(String codeCat, String codeKey) {
		StringBuilder strSql = new StringBuilder("SELECT t FROM Dict t WHERE t.codeCat = ");
		strSql.append("'").append(codeCat).append("'");
		strSql.append(" AND t.codeKey = ").append("'").append(codeKey).append("'");
		List list = em.createQuery(strSql.toString()).getResultList();
		if(list != null && list.size() > 0) {
			return false;
		}
		return true;
	}
	
	
	
	
}
