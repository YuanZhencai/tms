package com.wcs.common.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.wcs.base.util.MathUtil;
import com.wcs.common.controller.vo.PoVo;
import com.wcs.common.service.PoSearchService;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:人员岗位机构查询Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */

@ManagedBean
@ViewScoped
public class PoSearchBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(PoSearchBean.class);
	
	@EJB
	PoSearchService poSearchService;
	
	// 人员岗位机构列表数据
	private LazyDataModel<PoVo> poVosLazyModel ;
	
	// 查询条件
	private Map<String, String> conditionMap = new HashMap<String, String>();

	@PostConstruct
	public void init(){
		this.searchByCondition();
	}
	
	//根据条件查询列表数据
	public void searchByCondition(){
		try {
			poVosLazyModel = new LazyDataModel<PoVo>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public List<PoVo> load(int first, int pageSize, String sortField,
						SortOrder sortOrder, Map<String, String> filters) {
					List<PoVo> poList = new ArrayList<PoVo>();
					try {
					// 查询条件
					String pName = conditionMap.get("pName");
					String userName = conditionMap.get("userName");
					String stationName = conditionMap.get("stationName");
					String oPath = conditionMap.get("oPath");
					StringBuilder sql = new StringBuilder("select pid,pname,puid,sname,v.FULL_PATH from " +
														"(select pid,pname,puid,s.STEXT sname,s.oid oid " +
														"from (select p.id pid,p.NACHN pname,pu.id puid,ps.SID sid " +
														"from P p left join PS ps on p.id=ps.PID left join PU pu on p.id=pu.PERNR where p.DEFUNCT_IND='N' and ps.DEFUNCT_IND='N' and pu.DEFUNCT_IND='N') " +
														"t1 left join S s on s.id=t1.sid) t2 left join VW_ORG v on t2.oid=v.ORG_ID where 1=1");
					if(!MathUtil.isEmptyOrNull(pName)){
						sql.append(" and lower(pname) like '%" + pName + "%'");
					}
					if(!MathUtil.isEmptyOrNull(userName)){
						sql.append(" and lower(puid) like '%" + userName + "%'");
					}
					if(!MathUtil.isEmptyOrNull(stationName)){
						sql.append(" and lower(sname) like '%" + stationName + "%'");
					}
					if(!MathUtil.isEmptyOrNull(oPath)){
						sql.append(" and lower(v.FULL_PATH) like '%" + oPath + "%'");
					}
					StringBuilder countSql = new StringBuilder("select COUNT(*) from ("+sql.toString()+")");
					Integer count = poSearchService.getRowCountBySql(countSql.toString());
					this.setRowCount(count);
					List<Object[]> rs = poSearchService.getPagedRowsBySql(sql.toString(), first, pageSize);
					for(Object[] r : rs ){
						PoVo poVo = new PoVo();
						poVo.setpId((String)r[0]);
						poVo.setpName((String)r[1]);
						poVo.setUserName((String)r[2]);
						poVo.setStationName((String)r[3]);
						poVo.setoPath((String)r[4]);
						poList.add(poVo);
					}
					} catch (Exception e) {
						log.error("searchByCondition方法 根据条件查询列表数据",e);
					}
					return poList;
				}
			};
		} catch (Exception e) {
			log.error("searchByCondition方法 根据条件查询列表数据：",e);
		}
	}
	
	// 重置
	public void reset(){
		conditionMap = new HashMap<String, String>();
	}
	
	public Map<String, String> getConditionMap() {
		return conditionMap;
	}

	public void setConditionMap(Map<String, String> conditionMap) {
		this.conditionMap = conditionMap;
	}

	public LazyDataModel<PoVo> getPoVosLazyModel() {
		return poVosLazyModel;
	}

	public void setPoVosLazyModel(LazyDataModel<PoVo> poVosLazyModel) {
		this.poVosLazyModel = poVosLazyModel;
	}

}
