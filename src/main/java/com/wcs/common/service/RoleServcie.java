package com.wcs.common.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.common.model.Resourcemstr;
import com.wcs.common.model.Rolemstr;
import com.wcs.common.model.Roleresource;

/**
 * Project: tih
 * Description: Role Service
 * Copyright (c) 2012 Wilmar Consultancy Services
 * All Rights Reserved.
 * @author <a href="mailto:guanluyong@wcs-global.com">Mr.Guan</a>
 */
@Stateless
public class RoleServcie {
	@PersistenceContext(unitName = "pu") 
    private EntityManager em;
    
    private Log log = LogFactory.getLog(RoleServcie.class);
    /**
     * <p>Description: search Roles by q</p>
     * @param q
     * @return
     */
    public List<Rolemstr> search(HashMap<String, String> q) {
        String sql = "SELECT r FROM Rolemstr r WHERE r.name LIKE :name" +
        		" AND r.defunctInd LIKE :del";
        Query query = em.createQuery(sql);
        query.setParameter("name", 
                q.get("name") == null ? "%" : "%" + q.get("name").trim() + "%");
        query.setParameter("del", q.get("del") == null ? "%" : q.get("del"));
        return query.getResultList();
    }
    
    public void insert(Rolemstr r) throws Exception {
        String sql = "SELECT r FROM Rolemstr r WHERE r.code = :code";
        int num = em.createQuery(sql).setParameter("code", r.getCode()).getResultList().size();
        if(num > 0) {
            throw new Exception("角色编码有重复,请重新填写");
        }
        try {
            em.persist(r);
        } catch (Exception e) {
        	log.error("新增角色出现异常", e);
            throw new Exception("请刷新重试。如果还没有解决问题，请联系系统管理员");
        }
    }
    
    public void update(Rolemstr r) throws Exception {
        String sql = "SELECT r FROM Rolemstr r WHERE r.code = :code AND r.id<>:id";
        int num = em.createQuery(sql).setParameter("code", r.getCode()).setParameter("id", r.getId()).getResultList().size();
        if(num > 0) {
            throw new Exception("角色编码有重复,请重新填写");
        }
        try{
            em.merge(r);
        } catch (Exception e) {
        	log.error("更新角色出现异常", e);
            throw new Exception("请刷新重试。如果还没有解决问题，请联系系统管理员");
        }
    }
    
    public List<Resourcemstr> searchResources() {
        String sql = "SELECT r FROM Resourcemstr r WHERE r.defunctInd='N'";
        return em.createQuery(sql).getResultList();
    }
    
    public List<Roleresource> searchOldResources(Rolemstr r) {
        String sql = "SELECT r FROM Roleresource r,Rolemstr m,Resourcemstr c " +
        		"WHERE r.rolemstr=m AND r.resourcemstr=c AND c.defunctInd='N' AND r.defunctInd='N' AND m.id=:id";
        return em.createQuery(sql).setParameter("id", r.getId()).getResultList();
    }
    
    public void dispatchRoleToResources(List<Roleresource> older, List<Roleresource> repeat,
            List<Resourcemstr> newer, Rolemstr role, String user) throws Exception {
        try {
            // change 'Y' to old
            for(Roleresource r : older) {
                log.info("older:" + r.getResourcemstr().getName());
                r.setDefunctInd("Y");
                r.setUpdatedBy(user);
                r.setUpdatedDatetime(new Date());
                em.merge(r);
            }
            
            String sql = "SELECT r FROM Roleresource r,Rolemstr l,Resourcemstr c " +
                    "WHERE r.resourcemstr=c AND r.rolemstr=l AND l.id=:lid AND c.id=:cid";
            
            // repeat.parent 'N'
            for(Roleresource r : repeat) {
                Resourcemstr rp = r.getResourcemstr();
                while(rp.getParentId() != 0) {
                    rp = em.find(Resourcemstr.class, rp.getParentId());
                    Roleresource t = (Roleresource) em.createQuery(sql)
                            .setParameter("lid", role.getId())
                            .setParameter("cid", rp.getId())
                            .getResultList()
                            .get(0);
                    if("Y".equals(t.getDefunctInd())) {
                        t.setDefunctInd("N");
                        em.merge(t);
                    }
                }
            }
            
            // add new
            Query query = em.createQuery(sql).setParameter("lid", role.getId());
            for(Resourcemstr r : newer) {
                query.setParameter("cid", r.getId());
                List<Roleresource> tmp = query.getResultList();
                if(tmp.size() > 0) {
                    Roleresource t = tmp.get(0);
                    t.setDefunctInd("N");
                    t.setUpdatedBy(user);
                    t.setUpdatedDatetime(new Date());
                    em.merge(t);
                } else {
                    Roleresource t = new Roleresource();
                    t.setCreatedBy(user);
                    t.setCreatedDatetime(new Date());
                    t.setUpdatedBy(user);
                    t.setUpdatedDatetime(new Date());
                    t.setDefunctInd("N");
                    t.setResourcemstr(r);
                    t.setRolemstr(role);
                    em.persist(t);
                    
                    // loop parent
                    Resourcemstr rp = r;
                    while(rp.getParentId() != 0) {
                        rp = em.find(Resourcemstr.class, rp.getParentId());
                        @SuppressWarnings("unchecked")
                        List<Roleresource> ps = em.createQuery(sql).setParameter("lid", role.getId())
                                .setParameter("cid", rp.getId())
                                .getResultList();
                        if(ps.size() == 0) {
                            Roleresource p = new Roleresource();
                            p.setCreatedBy(user);
                            p.setCreatedDatetime(new Date());
                            p.setUpdatedBy(user);
                            p.setUpdatedDatetime(new Date());
                            p.setDefunctInd("N");
                            p.setResourcemstr(rp);
                            p.setRolemstr(role);
                            em.persist(p);
                        } else {
                        	/**
                        	 * sonar测试 ps.get(0).getDefunctInd() == "Y"
                        	 * 改成 "Y".equals(ps.get(0).getDefunctInd())
                        	 */
                            if ("Y".equals(ps.get(0).getDefunctInd())) {
                                ps.get(0).setDefunctInd("N");
                                em.merge(ps.get(0));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
