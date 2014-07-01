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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.MathUtil;
import com.wcs.common.controller.vo.PoVo;
import com.wcs.common.controller.vo.UsermstrVo;
import com.wcs.common.model.P;
import com.wcs.common.model.PS;
import com.wcs.common.model.PU;
import com.wcs.common.model.S;
import com.wcs.common.service.PoSearchService;
import com.wcs.common.service.StationService;
import com.wcs.common.util.MessageUtils;

/**
 * 
 * <p>
 * Project: tms
 * </p>
 * <p>
 * Description:岗位管理Bean
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */

@ManagedBean
@ViewScoped
public class StationBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(StationBean.class);

	@EJB
	EntityService entityService;
	@EJB
	StationService stationService;
	@EJB
	PoSearchService poSearchService;

	// 岗位列表查询条件
	private Map<String, String> conditionMap = new HashMap<String, String>();
	// 人员列表查询条件
	private Map<String, Object> personSearchConditionMap = new HashMap<String, Object>();
	// 岗位列表数据
	private LazyDataModel<S> stationLazyModel;
	// 人员列表数据
	private LazyDataModel<PoVo> personDataModel;
	// 列表中当前操作对象
	private S station;
	// 已分配人员列表中当前操作对象
	private P person;
	// 已分配人员列表
	private List<P> personList = new ArrayList<P>();
	// 选中人员
	private PoVo selectedPerson;

	@PostConstruct
	public void init() {
		this.searchByCondition();
	}

	/**
	 * 根据条件查询岗位数据
	 * 
	 * @param conditionMap
	 */
	public void searchByCondition() {
		try {
			stationLazyModel = new LazyDataModel<S>() {
				private static final long serialVersionUID = 1L;

				@Override
				public List<S> load(int first, int pageSize, String sortField,
						SortOrder sortOrder, Map<String, String> filters) {
					List<S> sList = new ArrayList<S>();
					String stationName = conditionMap.get("stationName");
					String defunctFlag = conditionMap.get("defunctFlag");
					String parentComName = conditionMap.get("parentComName");
					StringBuilder sql = new StringBuilder(
							"select * from S where 1=1");
					if (!MathUtil.isEmptyOrNull(stationName)) {
						sql.append(" and lower(STEXT) like '%"
								+ stationName.toLowerCase() + "%'");
					}
					if (!MathUtil.isEmptyOrNull(defunctFlag)) {
						sql.append(" and DEFUNCT_IND = '" + defunctFlag + "'");
					}
					if (!MathUtil.isEmptyOrNull(parentComName)) {
						sql.append(" and OID in( select ORG_ID from VW_ORG where lower(FULL_PATH) like '%"
								+ parentComName.toLowerCase() + "%')");
					}
					StringBuilder countSql = new StringBuilder(
							"select COUNT(*) from (" + sql.toString() + ")");
					Integer count = poSearchService.getRowCountBySql(countSql
							.toString());
					this.setRowCount(count);
					List<Object[]> rs = poSearchService.getPagedRowsBySql(
							sql.toString(), first, pageSize);
					for (Object[] r : rs) {
						S s = new S();
						s.setId((String) r[0]);
						s.setOid((String) r[1]);
						s.setStext((String) r[2]);
						s.setDefunctInd((String) r[6]);
						sList.add(s);
					}
					return sList;
				}
			};

		} catch (Exception e) {
			log.error("searchByCondition方法 根据条件查询岗位数据", e);
		}
	}

	/**
	 * 查询条件重置
	 */
	public void reset() {
		conditionMap = new HashMap<String, String>();
	}

	/**
	 * 模糊查询人员列表
	 */
	public void filterPerson() {
		try {
			personDataModel = new LazyDataModel<PoVo>() {

				private static final long serialVersionUID = -8568009349743548184L;

				@Override
				public List<PoVo> load(int first, int pageSize,
						String sortField, SortOrder sortOrder,
						Map<String, String> filters) {
					List<PoVo> usermstrList = new ArrayList<PoVo>();

					String id = (String) personSearchConditionMap.get("id");
					String name = (String) personSearchConditionMap.get("name");
					String userName = (String) personSearchConditionMap
							.get("userName");
					StringBuilder sql = new StringBuilder(
							"select p.id,p.nachn,u.ad_Account from P p,Usermstr u where p.defunct_Ind = 'N' and p.id=u.pernr ");
					if (!MathUtil.isEmptyOrNull(id)) {
						sql.append(" and lower(p.id) like '%"
								+ id.toLowerCase() + "%'");
					}
					if (!MathUtil.isEmptyOrNull(name)) {
						sql.append(" and lower(p.nachn) like '%"
								+ name.toLowerCase() + "%'");
					}
					if (!MathUtil.isEmptyOrNull(userName)) {
						sql.append(" and lower(u.ad_Account) like '%"
								+ userName.toLowerCase() + "%'");
					}

					StringBuilder countSql = new StringBuilder(
							"select COUNT(*) from (" + sql.toString() + ")");
					Integer count = poSearchService.getRowCountBySql(countSql
							.toString());
					this.setRowCount(count);
					List<Object[]> rs = poSearchService.getPagedRowsBySql(
							sql.toString(), first, pageSize);
					for (Object[] r : rs) {
						PoVo poVo = new PoVo();
						poVo.setpId((String) r[0]);
						poVo.setpName((String) r[1]);
						poVo.setUserName((String) r[2]);
						usermstrList.add(poVo);
					}
					return usermstrList;
				}

			};

		} catch (Exception e) {
			log.error("filterPerson方法 模糊查询人员列表", e);
		}
	}

	/**
	 * 确认分配选中人员
	 */
	public void saveDistribution() {
		try {
			if (selectedPerson != null) {
				List<PS> psList = stationService.findPSByPidAndSid(
						selectedPerson.getpId(), station.getId());
				if (psList.size() > 0) {
					psList.get(0).setDefunctInd("N");
					entityService.update(psList.get(0));
				} else {
					Integer maxId = stationService.findMaxPsId();
					PS ps = new PS();
					ps.setId(Integer.toString(maxId + 1));
					ps.setDefunctInd("N");
					ps.setPid(selectedPerson.getpId());
					ps.setSid(station.getId());
					entityService.create(ps);
				}
			}
			MessageUtils.addInfoMessage("msg", "分配人员成功！");
		} catch (Exception e) {
			log.error("saveDistribution方法 确认分配选中人员", e);
			MessageUtils.addErrorMessage("msg", "分配人员，保存失败！");
		}
	}

	/**
	 * 人员查询条件清空
	 */
	public void clear() {
		personSearchConditionMap.clear();
		selectedPerson = null;
	}

	/**
	 * 取消当前人员分配
	 */
	public void cancelDistribution() {
		try {
			stationService.cancelDistributionByPidSid(person.getId(),
					station.getId());
			this.findDistributedPersons();
			MessageUtils.addInfoMessage("msg", "岗位取消分配人员成功！");
		} catch (Exception e) {
			log.error("cancelDistribution方法 取消当前人员分配", e);
			MessageUtils.addErrorMessage("msg", "岗位取消分配人员失败！");
		}
	}

	/**
	 * 根据当前岗位查询人员列表
	 */
	public void findDistributedPersons() {
		try {
			personList = stationService.findDistributedPersons(station);
		} catch (Exception e) {
			log.error("findDistributedPersons方法 根据当前岗位查询人员列表：", e);
		}
	}

	/**
	 * 根据组织Id查询所属公司
	 * 
	 * @param oId
	 * @return
	 */
	public String findParentComNameByOId(String oId) {
		try {
			return stationService.findParentComNameByOId(oId);
		} catch (Exception e) {
			log.error("findParentComNameByOId 方法 根据组织Id查询所属公司：", e);
			return null;
		}
	}

	public Map<String, String> getConditionMap() {
		return conditionMap;
	}

	public void setConditionMap(Map<String, String> conditionMap) {
		this.conditionMap = conditionMap;
	}

	public LazyDataModel<S> getStationLazyModel() {
		return stationLazyModel;
	}

	public void setStationLazyModel(LazyDataModel<S> stationLazyModel) {
		this.stationLazyModel = stationLazyModel;
	}

	public S getStation() {
		return station;
	}

	public void setStation(S station) {
		this.station = station;
	}

	public List<P> getPersonList() {
		return personList;
	}

	public void setPersonList(List<P> personList) {
		this.personList = personList;
	}

	public P getPerson() {
		return person;
	}

	public void setPerson(P person) {
		this.person = person;
	}

	public LazyDataModel<PoVo> getPersonDataModel() {
		return personDataModel;
	}

	public void setPersonDataModel(LazyDataModel<PoVo> personDataModel) {
		this.personDataModel = personDataModel;
	}

	public PoVo getSelectedPerson() {
		return selectedPerson;
	}

	public void setSelectedPerson(PoVo selectedPerson) {
		this.selectedPerson = selectedPerson;
	}

	public Map<String, Object> getPersonSearchConditionMap() {
		return personSearchConditionMap;
	}

	public void setPersonSearchConditionMap(
			Map<String, Object> personSearchConditionMap) {
		this.personSearchConditionMap = personSearchConditionMap;
	}

}
