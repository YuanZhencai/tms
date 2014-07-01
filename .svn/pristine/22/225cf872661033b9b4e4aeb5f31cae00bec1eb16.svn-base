package com.wcs.common.service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import com.wcs.common.controller.vo.TaxAuthorityVO;
import com.wcs.common.model.Taxauthority;

@Stateless
public class TaxAuthorityService implements Serializable {

	private static final long serialVersionUID = -7707410191691768066L;
	@PersistenceContext(unitName = "pu")
	private EntityManager em;

	public List<TaxAuthorityVO> queryDataPlan(String taxName,
			String taxAddress, String taxState) {

		StringBuilder strSql = new StringBuilder(
				"select t from Taxauthority t where 1=1 ");
		/**
		 * sonar测试 taxName != null && taxName != ""
		 * 改成 StringUtils.isNotBlank(taxName)
		 */
		if (StringUtils.isNotBlank(taxName)) {
			strSql.append(" AND t.name LIKE '%" + taxName + "%' ");
		}
		/**
		 * sonar测试 taxAddress != null && taxAddress != ""
		 * 改成 StringUtils.isNotBlank(taxAddress)
		 */
		if (StringUtils.isNotBlank(taxAddress)) {
			strSql.append(" AND t.address LIKE '%" + taxAddress + "%' ");
		}
		/**
		 * sonar测试 taxState != null && taxState != ""
		 * 改成 StringUtils.isNotBlank(taxState)
		 */
		if (StringUtils.isNotBlank(taxState)) {
			strSql.append(" AND t.defunctInd = '" + taxState + "'");
		}

		Query query = em.createQuery(strSql.toString());
		List<Taxauthority> result = query.getResultList();
		List<TaxAuthorityVO> list = new ArrayList<TaxAuthorityVO>();
		for (Taxauthority t : result) {
			list.add(new TaxAuthorityVO(t.getId(), t.getName(), t.getAddress(),
					t.getZipcode(), t.getTelphone(), t.getDefunctInd(), t
							.getLeaderName(), t.getLeaderPosition(), t
							.getLeaderTelphone(), t.getContacterName(), t
							.getContacterPosition(), t.getContacterTelphone()));
		}
		return list;
	}

	// 添加方法
	public void addTaxManage(String addName, String addAddress,
			String addZipCode, String addTelephone, String addLeaderName,
			String addLeaderPosition, String addLeaderPhone,
			String addContacterName, String addContacterPosition,
			String addContacterPhone, String addState, String createUser) {

		Taxauthority taxauthority = new Taxauthority();
		taxauthority.setName(addName);
		taxauthority.setAddress(addAddress);
		taxauthority.setZipcode(addZipCode);
		taxauthority.setTelphone(addTelephone);
		taxauthority.setDefunctInd(addState);
		taxauthority.setLeaderName(addLeaderName);
		taxauthority.setLeaderPosition(addLeaderPosition);
		taxauthority.setLeaderTelphone(addLeaderPhone);
		taxauthority.setContacterName(addContacterName);
		taxauthority.setContacterPosition(addContacterPosition);
		taxauthority.setContacterTelphone(addContacterPhone);
		taxauthority.setCreatedBy(createUser);
		taxauthority.setCreatedDatetime(new Date());
		// 下面两个字段在有些表中是不能为空的,所以为了统一,第一次添加,统一添加创建信息的用户名和当前时间.
		taxauthority.setUpdatedBy(createUser);
		taxauthority.setUpdatedDatetime(new Date());
		this.em.persist(taxauthority);

	}

	// 更新方法
	public void modifyData(TaxAuthorityVO selectData, String updateUser) {
		StringBuilder strSql = new StringBuilder("UPDATE Taxauthority t ");
		strSql.append("SET t.name= '" + selectData.getTaxName() + "',");
		strSql.append("t.address= '" + selectData.getTaxAddress() + "',");
		strSql.append("t.zipcode= '" + selectData.getTaxZipCode() + "',");
		strSql.append("t.telphone= '" + selectData.getTaxPhone() + "',");
		strSql.append("t.leaderName= '" + selectData.getLeaderName() + "',");
		strSql.append("t.leaderPosition= '" + selectData.getLeaderPosition()
				+ "',");
		strSql.append("t.leaderTelphone= '" + selectData.getLeaderPhone()
				+ "',");
		strSql.append("t.contacterName= '" + selectData.getContacterName()
				+ "',");
		strSql.append("t.contacterPosition= '"
				+ selectData.getContacterPosition() + "',");
		strSql.append("t.contacterTelphone= '" + selectData.getContacterPhone()
				+ "',");
		strSql.append("t.defunctInd= '" + selectData.getTaxState() + "',");
		strSql.append("t.updatedBy='" + updateUser + "',");
		strSql.append("t.updatedDatetime= '"
				+ new SimpleDateFormat("yyyy-MM-dd HH:ss:mm")
						.format(new Date()) + "'");
		strSql.append(" where t.id=" + Long.valueOf(selectData.getId()) + "");
		this.em.createQuery(strSql.toString()).executeUpdate();

	}
}
