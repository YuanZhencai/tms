package com.wcs.tms.service.system.company;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;

import com.wcs.base.service.EntityService;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ShareHolder;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 股权信息Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Stateless
public class ShareHolderService implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Inject
    private EntityService entityService;

    /**
     * 
     * <p>Description: 根据公司Id查询该公司的所有股东信息</p>
     * @param companyId
     * @return
     * @throws Exception
     */
    public List<ShareHolder> findShareHolderListByCp(Long companyId) throws ServiceException {
        Validate.notNull(companyId, "公司Id不能为空");
        try {
            StringBuilder sql = new StringBuilder(100);
            sql.append("select sh from ShareHolder sh left join fetch  sh.company ").append(" where sh.company.id=?1 ")
                    .append(" and sh.defunctInd = 'N' and sh.company.defunctInd = 'N'");
            return this.entityService.find(sql.toString(), companyId);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 保存或者更新股东信息实体</p>
     * @param shareHeoldList
     * @throws RuntimeException
     */
    public void batchSaveShareHeold(List<ShareHolder> shareHeoldList, Long companyId) throws ServiceException {
        Validate.notNull(companyId, "公司Id不能为空");
        try {
            // 判断是否需要删除 通过比较内存的对象与受管对象大小
            List<ShareHolder> dataList = this.findShareHolderListByCp(companyId);
            for (ShareHolder share : dataList) {
                if (!shareHeoldList.contains(share)) {
                    share.setDefunctInd("Y");
                    this.entityService.update(share);
                }
            }
            if (shareHeoldList.isEmpty()) { 
                return; 
            }
            // 保存或者更新现有集合
            for (ShareHolder share : shareHeoldList) {
                // 通过判断Id是创建还是更新对象
                if (share.getId() == null) {
                    Company company = this.entityService.find(Company.class, companyId);
                    share.setCompany(company);
                    this.entityService.create(share);
                } else {

                    this.entityService.update(share);
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 查看是否已经存在同样的股东名称</p>
     * @param shName
     * @return true 存在,false不存在
     * @throws ServiceException
     */
    public boolean findShareHolByName(String shName, Long companyId) throws ServiceException {
        Validate.notNull(shName, "股东名称不能为空");
        Validate.notNull(companyId, "公司Id不能为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select count(sh.id) from ShareHolder sh where sh.shareHolderName=?1 and sh.company.id=?2 and sh.defunctInd = 'N'");
            Long count = entityService.countHqlResult(bulder.toString(), shName,companyId);
            if (count > 0) { 
                return true; 
            }
            return false;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 计算未注册金额</p>
     * @return
     */
    public Double calculationUnregistered(Long companyId) throws ServiceException {
        Validate.notNull(companyId, "公司Id不能为空");
        try {
            Double unregister = 0.0d;
            StringBuilder bulder = new StringBuilder();
            bulder.append("select sum(sh.fondsTotal),sum(sh.fondsInPlace) from ShareHolder sh where sh.defunctInd = 'N' and sh.company.id=?1");
            List list = this.entityService.find(bulder.toString(),companyId);
            Object[] fonds = (Object[]) list.get(0);
            if (fonds != null) {
                unregister = (Double) fonds[0] - (Double) fonds[1];
            }
            return unregister;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

}
