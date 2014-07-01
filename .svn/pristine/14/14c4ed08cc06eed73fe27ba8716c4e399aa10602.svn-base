/**
 * DatasourceService.java
 * Created: 2012-01-29 下午6:23:06
 */
package com.wcs.report.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wcs.report.model.Datasourcemstr;

/**
 * <p>Project: oa</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yansong@wcs-global.com">Yan Song</a>
 */
@Stateless
public class DatasourceService implements Serializable{
	private static Logger log = LoggerFactory.getLogger(DatasourceService.class);
//	
	private static final long serialVersionUID = 1L;
	
	@PersistenceContext(unitName = "pu")
    private EntityManager em;
	
    public Datasourcemstr getDatasourceById(long datasourceMstrId) {
    	log.info("---------------------Start: getDatasourceById-----------------");
    	Datasourcemstr datasourcemstr=(Datasourcemstr) em.find(Datasourcemstr.class, datasourceMstrId);
    	log.info("---------------------End: getDatasourceById-----------------");
        return datasourcemstr;
    }
    
    public void creatDatasource(Datasourcemstr datasourcemstr) {
    	log.info("---------------------Start: creatDatasource-----------------");
        em.persist(datasourcemstr);
        log.info("---------------------End: creatDatasource-----------------");
    }
    
    public void deleteDatasourceById(long datasourceMstrId) {
    	log.info("---------------------Start: deleteDatasourceById-----------------");
        em.remove(getDatasourceById(datasourceMstrId));
        log.info("---------------------End: deleteDatasourceById-----------------");
    }
    
    public void updateDatasource(Datasourcemstr datasourcemstr) {
    	log.info("---------------------Start: updateDatasource-----------------");
        em.merge(datasourcemstr);
        log.info("---------------------End: updateDatasource-----------------");
    }
    
    public List<Datasourcemstr> getDatasourceListAll() {
    	log.info("---------------------Start: getDatasourceListAll-----------------");
    	String sql = "select d from Datasourcemstr d";
    	List<Datasourcemstr> result =em.createQuery(sql, Datasourcemstr.class).getResultList();
        log.info("---------------------End: getDatasourceListAll , size " + result.size() + "-----------------");
        return result;

    }
    
    public Datasourcemstr getDatasourceSingle(long datasourcemstrId) {
    	String sql = "select d from Datasourcemstr d where d.datasourcemstrId = :datasourcemstrId";
        return em.createQuery(sql, Datasourcemstr.class).setParameter("datasourcemstrId", datasourcemstrId).getSingleResult();
    }
    
}
