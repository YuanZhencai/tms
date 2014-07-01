package com.wcs.tms.service.system.sysprocess;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.common.model.Companymstr;
import com.wcs.common.model.O;
import com.wcs.common.model.S;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.ProcessAuth;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Stateless
public class ProcessAuthService implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Inject
    private EntityService entityService;
    
    private Log log = LogFactory.getLog(ProcessAuthService.class);
    /**
     * 
     * <p>Description:得到用户所在的公司 </p>
     * @param userId
     * @return
     * @throws Exception
     */
    public List<Companymstr> findCompanyByUser(Long userId) throws Exception {
        Validate.notNull(userId, "用户Id为空");
        try {
            String sql = "select uc.companymstr from Usercompany uc  where uc.usermstr.id=?1 and uc.defunctInd = 'N'";
            return this.entityService.find(sql, userId);
        } catch (Exception e) {
            log.error("findCompanyByUser方法 错误信息：" + e.getMessage());
            throw new ServiceException("findCompanyByUser方法 错误信息：" + e.getMessage());
        }
    }

    /**
     * 
     * <p>Description: 查询所有的公司信息</p>
     * @return
     * @throws Exception
     */
    public List<O> findCompanyList() throws Exception {
        try {
            String sql = "select o from O o where o.defunctInd = 'N'";
            return this.entityService.find(sql);
        } catch (Exception e) {
            log.error("findCompanyList方法 错误信息：" + e.getMessage());
            throw new ServiceException("findCompanyList方法 错误信息：" + e.getMessage());
        }
    }

    /**
     * 
     * <p>Description: 得到公司的下级单位</p>
     * @param oId
     * @return
     * @throws Exception
     */
    public List<O> findChildCompany(Long oId) throws Exception {
        try {
            String sql = "select o from O o where o.parent=?1";
            return this.entityService.find(sql, oId);
        } catch (Exception e) {
            log.error("findChildCompany方法 错误信息：" + e.getMessage());
            throw new ServiceException("findChildCompany方法 错误信息：" + e.getMessage());
        }
    }

    /**
     * 
     * <p>Description: 根据公司得到岗位</p>
     * @param compnyId
     * @return
     * @throws Exception
     */
    public List<S> findPositionByCompny(String compnyId) throws Exception {
        Validate.notNull(compnyId, "公司Id为空");
        try {
            String sql = "select s  from S s where s.oid=?1 and s.defunctInd = 'N'";
            return this.entityService.find(sql, compnyId);
        } catch (Exception e) {
            log.error("findPositionByCompny方法 错误信息：" + e.getMessage());
            throw new ServiceException("findPositionByCompny方法 错误信息：" + e.getMessage());
        }
    }

    /**
     * 
     * <p>Description: 查询具体流程所分配的权限</p>
     * @param processDefinId
     * @return
     * @throws Exception
     */
    public List<ProcessAuth> findProcessAuthListByPId(Long processDefinId) throws Exception {
        Validate.notNull(processDefinId, "系统流程Id为空");
        try {
            String sql = "select pa from ProcessAuth pa  left outer join fetch pa.s left outer join fetch pa.o  where pa.processDefine.id=?1 and pa.defunctInd = 'N'";
            return this.entityService.find(sql, processDefinId);
        } catch (Exception e) {
            log.error("findProcessAuthListByPId方法 错误信息：" + e.getMessage());
            throw new ServiceException("findProcessAuthListByPId方法 错误信息：" + e.getMessage());
        }
    }

    /**
     * 
     * <p>Description: 删除具体流程的权限</p>
     * @param processDefinId
     * @throws Exception
     */
    public void deleteProcessAuthByPId(Long processDefinId) throws Exception {
        Validate.notNull(processDefinId, "系统流程Id为空");
        try {
            String sql = "delete from ProcessAuth p where p.processDefine.id=?1";
            this.entityService.batchExecute(sql, processDefinId);
        } catch (Exception e) {
            log.error("deleteProcessAuthByPId方法 错误信息：" + e.getMessage());
            throw new ServiceException("deleteProcessAuthByPId方法 错误信息：" + e.getMessage());
        }
    }

   

    /**
     * 
     * <p>Description: 批量保存流程分配权限</p>
     * @param processList
     * @throws Exception
     */
    public void batchSaveProcessAuth(List<ProcessAuth> processList) throws Exception {
        if (processList.isEmpty()) { 
            return; 
        }
        try {
            for (ProcessAuth p : processList) {
                this.entityService.create(p);
            }
        } catch (Exception e) {
            log.error("batchSaveProcessAuth方法 错误信息：" + e.getMessage());
            throw new ServiceException("batchSaveProcessAuth方法 错误信息：" + e.getMessage());
        }
    }
}
