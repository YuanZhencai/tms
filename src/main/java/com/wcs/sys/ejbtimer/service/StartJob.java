package com.wcs.sys.ejbtimer.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wcs.sys.ejbtimer.model.SysJobInfo;

/**
 * <p>Project: wcsoa</p>
 * <p>Description: </p>
 * <p>Copyright © 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
@Startup
@Singleton
public class StartJob {
    
    private static final Logger logger = LoggerFactory.getLogger(StartJob.class);
    @EJB
    SysEjbTimerService sysEjbTimerService;
    
    @PersistenceContext(unitName = "pu")
    EntityManager em;

    /** 
     * 应用启动后，自动启动所有定时任务 
     */
    @PostConstruct
    public void startAllJobs() {
        
        logger.debug("start =======startAllJobs()===============");
        try {
            // 先取消所有已经安排的定时任务，然后根据配置表重新安排任务
            sysEjbTimerService.cancelAllTimer();

            String queryAllJobSql = "SELECT jobInfo FROM SysJobInfo jobInfo  where jobInfo.isEnabled=:isEnabled";
            Query query = em.createQuery(queryAllJobSql, SysJobInfo.class);
            query.setParameter("isEnabled", true);

            List<SysJobInfo> allJobList = query.getResultList();
            for (SysJobInfo jobModel : allJobList) {
                sysEjbTimerService.createTimer(jobModel);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        
        logger.debug("end =======startAllJobs()===============");
    }
}


