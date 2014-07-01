package com.wcs.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.wcs.common.controller.vo.PositionCompanyVO;
import com.wcs.common.model.Companymstr;
import com.wcs.common.model.O;
import com.wcs.common.model.Position;
import com.wcs.common.model.Positionorg;

/**
 * Project: tih
 * Description: position service. do CRUD operation.
 * Copyright (c) 2012 Wilmar Consultancy Services
 * All Rights Reserved.
 * @author <a href="mailto:guanluyong@wcs-global.com">Mr.Guan</a>
 */
@Stateless
public class PositionService {
	@PersistenceContext(unitName = "pu") 
    private EntityManager em;
    
    public List<Position> searchPosition(Map<String, String> m) {
        String sql = null;
        boolean f = false;
        if (m.get("stext") == null || "".equals(m.get("stext"))) {
            sql = "SELECT DISTINCT p FROM Position p WHERE p.defunctInd LIKE :del AND p.name LIKE :name "
                    + "ORDER BY p.id";
        } else {
            f = true;
            sql = "SELECT DISTINCT p FROM Position p, Positionorg po, O o WHERE p=po.position AND po.oid=o.id "
                    + "AND p.defunctInd LIKE :del AND p.name LIKE :name AND o.stext LIKE :stext ORDER BY p.id";
        }
        Query q = em.createQuery(sql);
        q.setParameter("del", (m.get("del") == null || "".equals(m.get("del")) ? "%" : m.get("del")));
        q.setParameter("name", "%" + m.get("name") + "%");
        if (f) {
            f = false;
            q.setParameter("stext", "%" + m.get("stext") + "%");
        }
        return q.getResultList();
    }
    
    public void insertPosition(Position p) {
        em.persist(p);
    }
    
    public void updatePosition(Position p) {
        em.merge(p);
    }
    
    /**
     * find belong company
     */
    public List<PositionCompanyVO> findCompanys(Long id) {
        String sql = 
            "SELECT o,c,po FROM Position p,Positionorg po,O o,Companymstr c " +
        	"WHERE o.defunctInd='N' AND c.defunctInd='N' AND po.defunctInd='N' " +
        	"AND p=po.position AND po.oid=o.id AND po.oid=c.oid " +
        	"AND p.id=:id " +
        	"ORDER BY o.id";
        List<Object[]> list =
            em.createQuery(sql).setParameter("id", id).getResultList();
        List<PositionCompanyVO> rs = new ArrayList<PositionCompanyVO>();
        for(Object[] objs : list) {
            rs.add(new PositionCompanyVO((Companymstr) objs[1], (O) objs[0], (Positionorg) objs[2]));
        }
        return rs;
    }
    
    /**
     * find other company
     */
    public List<PositionCompanyVO> findOtherCompanys(Position p, Map<String, String> m) {
        String sql = 
            "SELECT o,c FROM O o, Companymstr c " +
        	"WHERE o.defunctInd='N' AND c.defunctInd='N' " +
        	"AND o.id=c.oid AND o.stext LIKE :stext " +
        	"AND o.bukrs LIKE :bukrs AND c.address LIKE :address " +
        	"ORDER BY o.id";
        
        Query q = em.createQuery(sql);
        String stext = m.get("stext") == null ? "" : m.get("stext");
        String bukrs = m.get("bukrs") == null ? "" : m.get("bukrs");
        String address = m.get("address") == null ? "" : m.get("address");
        
        q.setParameter("stext", "%" + stext + "%");
        q.setParameter("bukrs", "%" + bukrs + "%");
        q.setParameter("address", "%" + address + "%");
        List<Object[]> list = q.getResultList();
        List<PositionCompanyVO> rs = new ArrayList<PositionCompanyVO>();
        for(Object[] objs : list) {
            rs.add(new PositionCompanyVO((Companymstr) objs[1], (O) objs[0], null));
        }
        return rs;
    }
    
    /**
     * <p>Description: union position and company</p>
     * @param positionCompany
     * @param belongedCompany
     */
    public void addPostionorg(List<Positionorg> newPositionorg, List<PositionCompanyVO> belongedCompany) {
        String sql = "SELECT po FROM Positionorg po,Position p WHERE po.position=p AND po.oid=:oid AND p.id=:pid";
        Query q = em.createQuery(sql);
        for(Positionorg p : newPositionorg) {
            List<Positionorg> list = q.setParameter("oid", p.getOid()).setParameter("pid", p.getPosition().getId()).getResultList();
            if(!list.isEmpty()) {
                p.setId(list.get(0).getId());
                p.setCreatedBy(list.get(0).getCreatedBy());
                p.setCreatedDatetime(list.get(0).getCreatedDatetime());
                em.merge(p);
            } else {
                em.persist(p);
            }
        }

        for(PositionCompanyVO pc : belongedCompany) {
            Positionorg p = em.find(Positionorg.class, pc.getPositionorgId());
            p.setDefunctInd("Y");
            em.merge(p);
        }
    }
    
    /**
     * <p>Description: release resource</p>
     */
    @PreDestroy
    public void destory() {
        em.flush();
        em.close();
    }
}
