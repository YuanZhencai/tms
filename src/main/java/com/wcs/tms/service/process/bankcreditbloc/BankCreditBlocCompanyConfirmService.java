package com.wcs.tms.service.process.bankcreditbloc;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.ProcBankCreditBlocCompanyConfirm;
import com.wcs.tms.model.ProcBankCreditBlocConfirm;
import com.wcs.tms.model.ProcRptBankCreditBlocConfirm;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 确认单Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Stateless
public class BankCreditBlocCompanyConfirmService implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Inject
    private EntityService entityService;
    
    private static final Log log = LogFactory.getLog(BankCreditBlocCompanyConfirmService.class);

    /**
     * 
     * <p>Description: 查询成员公司确认单其他授信产品名称是否重复</p>
     * @param proBlocId
     * @param rptName
     * @return
     * @throws ServiceException
     */
    public boolean findProcBlocRptCreditConfirmByName(Long proBlocId, String rptName) throws ServiceException {
        Validate.notNull(proBlocId, "银行授信成员公司Id为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select rpt from ProcRptBankCreditBlocConfirm rpt left join fetch rpt.procBankCreditBlocCompanyConfirm where rpt.procBankCreditBlocCompanyConfirm.id=?1 and rpt.defunctInd = 'N' and rpt.cdProName=?2");
            List<ProcRptBankCreditBlocConfirm> list = this.entityService.find(bulder.toString(), proBlocId, rptName);
            if (list.size() > 1) { return true; }
            return false;
        } catch (Exception e) {
            log.error("findProcBlocRptCreditConfirmByName方法 查询成员公司确认单其他授信产品名称是否重复 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 得到确认单成员公司的所有其他授信产品</p>
     * @param blocId
     * @return
     * @throws ServiceException
     */
    public List<ProcRptBankCreditBlocConfirm> findBlocRptConfirmByBlocCompany(Long blocId) throws ServiceException {
        Validate.notNull(blocId, "银行授信成员公司确认单Id");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select rpt from ProcRptBankCreditBlocConfirm rpt left join fetch rpt.procBankCreditBlocCompanyConfirm where rpt.procBankCreditBlocCompanyConfirm.id=?1 and rpt.defunctInd = 'N'");
            return this.entityService.find(bulder.toString(), blocId);
        } catch (Exception e) {
            log.error("findBlocRptConfirmByBlocCompany方法 得到确认单成员公司的所有其他授信产品 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 添加或者更新确认单成员公司其他授信产品</p>
     * @param proBlocRptCreditList
     * @param blocId
     * @throws ServiceException
     */
    public void batchAddOrUpdateBlocCreditRpt(List<ProcRptBankCreditBlocConfirm> proBlocRptCreditList, Long blocId)
            throws ServiceException {
        Validate.notNull(blocId, "集团成员公司Id为空");
        try {
            // 判断是否需要删除 通过比较内存的对象与受管对象大小
            List<ProcRptBankCreditBlocConfirm> dataList = findBlocRptByCofirmId(blocId);
            for (ProcRptBankCreditBlocConfirm rpt : dataList) {
                if (!proBlocRptCreditList.contains(rpt)) {
                    rpt.setDefunctInd("Y");
                    this.entityService.update(rpt);
                }
            }
            if (proBlocRptCreditList.isEmpty()) { return; }
            ProcBankCreditBlocCompanyConfirm blocCompany = entityService.find(ProcBankCreditBlocCompanyConfirm.class, blocId);
            if (!proBlocRptCreditList.isEmpty()) {
                for (ProcRptBankCreditBlocConfirm rp : proBlocRptCreditList) {
                    rp.setProcBankCreditBlocCompanyConfirm(blocCompany);
                    if (rp.getId() != null) {
                        entityService.update(rp);
                    } else {
                        entityService.create(rp);
                    }
                }
            }
        } catch (Exception e) {
            log.error("batchAddOrUpdateBlocCreditRpt方法 添加或者更新确认单成员公司其他授信产品 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 得到確認單成员公司的所有其他授信产品</p>
     * @param blocId
     * @return
     * @throws ServiceException
     */
    public List<ProcRptBankCreditBlocConfirm> findBlocRptByCofirmId(Long blocId) throws ServiceException {
        Validate.notNull(blocId, "银行授信確認單成员公司Id");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select rpt from ProcRptBankCreditBlocConfirm rpt left join fetch rpt.procBankCreditBlocCompanyConfirm where rpt.procBankCreditBlocCompanyConfirm.id=?1 and rpt.defunctInd = 'N'");
            return this.entityService.find(bulder.toString(), blocId);
        } catch (Exception e) {
            log.error("findBlocRptByCofirmId方法 得到確認單成员公司的所有其他授信产品 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 根据银行授信集团申请ID得到确认单</p>
     * @param blocId
     * @return
     * @throws ServiceException
     */
    public ProcBankCreditBlocConfirm findBlocConfirmByBlocCreditId(Long blocId) throws ServiceException {
        Validate.notNull(blocId, "银行授信集团申请Id");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append(" select bloc from ProcBankCreditBlocConfirm bloc where bloc.procBankCreditBloc.id =?1 and bloc.defunctInd = 'N'");
            List<ProcBankCreditBlocConfirm> confirmList = this.entityService.find(bulder.toString(), blocId);
            if (confirmList.size() > 1) { throw new ServiceException("银行授信集团申请对应确认单数据多条记录"); }
            return confirmList.get(0);
        } catch (Exception e) {
            log.error("findBlocConfirmByBlocCreditId方法 根据银行授信集团申请ID得到确认单 出现异常：",e);
            throw new ServiceException(e);
        }
    }
    
    
}
