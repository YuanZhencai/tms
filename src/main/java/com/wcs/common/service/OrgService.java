package com.wcs.common.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.wcs.common.model.O;
import com.wcs.common.model.S;

@Stateless
public class OrgService {

	@PersistenceContext(unitName = "pu") 
	private EntityManager em;
	
	/**
	 * 根据父组织ID查询组织机构
	 * @param parentId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<O> getOrgByParent(String parentId) {
		String jpql = "SELECT o FROM O o WHERE o.parent = :parentId AND o.defunctInd = 'N' ORDER BY o.id asc";
		return em.createQuery(jpql).setParameter("parentId", parentId).getResultList();
	}
	
	/**
	 * 根据ID查询组织
	 * @param orgId
	 * @return
	 */
	public O getOrgById(String orgId) {
		String jpql = "SELECT o FROM O o WHERE o.id = :orgId AND o.defunctInd = 'N' ORDER BY o.id asc";
		return (O)em.createQuery(jpql).setParameter("orgId", orgId).getSingleResult();
	}
	
	/**
	 * 根据组织ID查询岗位
	 * @param orgId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<S> getPositionByOrgId(String orgId) {
		String jpql = "SELECT s FROM S s WHERE s.oid = :orgId AND s.defunctInd = 'N' ORDER BY s.id asc";
		return em.createQuery(jpql).setParameter("orgId", orgId).getResultList();
	}
	
	/**
	 * 查询当前O机构最大ID
	 * @param sap
	 * @return
	 */
	public String getMaxOrgId(String sap) {
		String jpql = "SELECT max(o.id) FROM O o where o.id like '" + sap + "%'";;
		return (String)em.createQuery(jpql).getSingleResult();
	}
	
	/**
	 * 查询当前S机构最大ID
	 * @param sap
	 * @return
	 */
	public String getMaxSId(String sap) {
		String jpql = "SELECT max(s.id) FROM S s where s.id like '" + sap + "%'";;
		return (String)em.createQuery(jpql).getSingleResult();
	}
	
	public void saveOrg(O org) {
		em.persist(org);
	}
	
	public void savePosition(S position) {
		em.persist(position);
	}
	
	public void updateOrg(O org) {
		em.merge(org);
	}
	
	public void updatePosition(S position) {
		em.merge(position);
	}
}
