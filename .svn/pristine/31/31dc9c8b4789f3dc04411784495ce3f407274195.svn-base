package com.wcs.tms.service.process.bankcredit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.ProcBankCreditConfirm;
import com.wcs.tms.model.ProcRptCreditConfirm;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Stateless
public class BankCreditConfirmService implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Inject
    private EntityService entityService;
    @Inject
    private PEManager peManager;
    
    private static final Log log = LogFactory.getLog(BankCreditConfirmService.class);
    /**
     * 
     * <p>Description: 批量保存银行授信其他产品确认明显</p>
     * @param bankConfirmId
     * @param proRptCreditList
     */
    public void batchAddRptConfirm(String processInd, List<ProcRptCreditConfirm> proRptCreditList) throws ServiceException {
        Validate.notNull(processInd, "银行授信确认单Id为空");
        try {
            if (!proRptCreditList.isEmpty()) {
                for (ProcRptCreditConfirm rptConfirm : proRptCreditList) {
                    if (rptConfirm.getId() == null) {
                        rptConfirm.setProcInstId(processInd);
                        entityService.create(rptConfirm);
                    } else {
                        rptConfirm.setProcInstId(processInd);
                        entityService.update(rptConfirm);
                    }
                }
            }
        } catch (Exception e) {
            log.error("createProcInstance方法 流程创建保存 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description:查询确认单其他产品是否存在 </p>
     * @param processInd
     * @param name
     * @return
     */
    public boolean findBankRptConfirm(String processInd, String name) {
        Validate.notNull(processInd, "流程实例编号为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select rpt from ProcRptCreditConfirm rpt where rpt.procInstId=?1 and rpt.defunctInd = 'N' and rpt.cdProName=?2");
            List<ProcRptCreditConfirm> list = this.entityService.find(bulder.toString(), processInd, name);
            if (list.size() > 1) { return true; }
            return false;
        } catch (Exception e) {
            log.error("findBankRptConfirm方法 查询确认单其他产品是否存在 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description:创建授信确认并抓取公司与银行 </p>
     * @param confirm
     * @return
     * @throws ServiceException
     */
    public ProcBankCreditConfirm findBankCreditConfirm(String processId) throws ServiceException {
        try {
            StringBuilder buff = new StringBuilder();
            buff.append("select pc from ProcBankCreditConfirm pc left  join fetch pc.company left  join fetch pc.bank where pc.procInstId=?1 and pc.defunctInd = 'N'");
            List<ProcBankCreditConfirm> confirmList = this.entityService.find(buff.toString(), processId);
            if (confirmList.isEmpty()) { return null; }
            if (confirmList.size() > 1) { throw new ServiceException("已经存在该条记录"); }
            return confirmList.get(0);
        } catch (Exception e) {
            log.error("findBankCreditConfirm方法 创建授信确认并抓取公司与银行 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 根据流程实例Id得到授信确认单下的其他授信产品</p>
     * @param processId
     * @return
     */
    public List<ProcRptCreditConfirm> findProRptConmfirm(String processId) throws ServiceException {
        Validate.notNull(processId, "流程实例编号为空");
        List<ProcRptCreditConfirm> prccList = new ArrayList<ProcRptCreditConfirm>();
        StringBuilder sb = new StringBuilder();
        sb.append("select confirm from ProcRptCreditConfirm confirm where confirm.procInstId=?1 and confirm.defunctInd = 'N'");
        try {
            List<ProcRptCreditConfirm> reList = entityService.find(sb.toString(), processId);
            if (!reList.isEmpty()) {
                for (ProcRptCreditConfirm procRptCreditConfirm : reList) {
                    prccList.add(procRptCreditConfirm);
                }
            }
        } catch (Exception e) {
            log.error("findProRptConmfirm方法 根据流程实例Id得到授信确认单下的其他授信产品 出现异常：",e);
            throw new ServiceException(e);
        }
        
        return prccList;
    }
    
    /**
     * 
     * <p>Description: 确认申请（与流程中的重新申请方法类似）</p>
     * @param memo
     * @param workflowNumber
     * @param documentList
     * @throws ServiceException
     */
    public void vwReplayBankRegister(String memo, String workflowNumber) throws ServiceException {
        try {
        	log.info("memo,workflowNumber:"+memo+","+workflowNumber);
            peManager.vwSubmitApply(memo, workflowNumber, new ArrayList<String>());
            log.info("确认申请结束！");
        } catch (Exception e) {
            log.error("vwReplayBankRegister方法 确认申请 出现异常：",e);
            throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
        }
    }
}
