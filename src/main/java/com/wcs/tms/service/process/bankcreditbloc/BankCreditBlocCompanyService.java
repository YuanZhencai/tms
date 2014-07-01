package com.wcs.tms.service.process.bankcreditbloc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.ProcBankCreditBlocCompany;
import com.wcs.tms.model.ProcBankCreditBlocCompanyConfirm;
import com.wcs.tms.model.ProcBankCreditBlocConfirm;
import com.wcs.tms.model.ProcRptBankCreditBloc;
import com.wcs.tms.model.ProcRptBankCreditBlocConfirm;
import com.wcs.tms.model.ProcRptCredit;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWWorkObject;
import filenet.vw.api.VWWorkObjectNumber;

@Stateless
public class BankCreditBlocCompanyService implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Inject
    private PEManager peManager;
    @Inject
    private EntityService entityService;
    
    private static final Log log = LogFactory.getLog(BankCreditBlocCompanyService.class);

    /**
     * 
     * <p>Description: 根据银行Id得到银行</p>
     * @param bankId
     * @return
     * @throws ServiceException
     */
    public Bank findBankById(Long bankId) throws ServiceException {
        Validate.notNull(bankId, "银行Id为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select bank from Bank bank where bank.id=?1 and bank.defunctInd = 'N'");
            List<Bank> bankList = this.entityService.find(bulder.toString(), bankId);
            if (bankList.isEmpty()) { return null; }
            if (bankList.size() > 1) { throw new ServiceException("该银行存在多条记录"); }
            return bankList.get(0);
        } catch (Exception e) {
            log.error("findBankById方法 根据银行Id得到银行 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 添加或者更新成员公司其他授信产品</p>
     * @param proBlocRptCreditList
     * @param blocId
     * @throws ServiceException
     */
    public void batchAddOrUpdateBlocCreditRpt(List<ProcRptBankCreditBloc> proBlocRptCreditList, Long blocId)
            throws ServiceException {
        Validate.notNull(blocId, "集团成员公司Id为空");
        try {
            // 判断是否需要删除 通过比较内存的对象与受管对象大小
            List<ProcRptBankCreditBloc> dataList = findBlocRptByBlocCom(blocId);
            for (ProcRptBankCreditBloc rpt : dataList) {
                if (!proBlocRptCreditList.contains(rpt)) {
                    rpt.setDefunctInd("Y");
                    this.entityService.update(rpt);
                }
            }
            if (proBlocRptCreditList.isEmpty()) { return; }
            ProcBankCreditBlocCompany blocCompany = entityService.find(ProcBankCreditBlocCompany.class, blocId);
            if (!proBlocRptCreditList.isEmpty()) {
                for (ProcRptBankCreditBloc rp : proBlocRptCreditList) {
                    rp.setProcBankCreditBlocCompany(blocCompany);
                    if (rp.getId() != null) {
                        entityService.update(rp);
                    } else {
                        entityService.create(rp);
                    }
                }
            }
        } catch (Exception e) {
            log.error("batchAddOrUpdateBlocCreditRpt方法 添加或者更新成员公司其他授信产品 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 得到成员公司的所有其他授信产品</p>
     * @param blocId
     * @return
     * @throws ServiceException
     */
    public List<ProcRptBankCreditBloc> findBlocRptByBlocCom(Long blocId) throws ServiceException {
        Validate.notNull(blocId, "银行授信成员公司Id");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select rpt from ProcRptBankCreditBloc rpt left join fetch rpt.procBankCreditBlocCompany where rpt.procBankCreditBlocCompany.id=?1 and rpt.defunctInd = 'N'");
            return this.entityService.find(bulder.toString(), blocId);
        } catch (Exception e) {
            log.error("findBlocRptByBlocCom方法 得到成员公司的所有其他授信产品 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 根据流程实例编号查询成员公司信息</p>
     * @param processInd
     * @return
     * @throws ServiceException
     */
    public ProcBankCreditBlocCompany findBlocCompanyByProceIn(String processInd) throws ServiceException {
        Validate.notNull(processInd, "成员公司流程实例编号为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append(" select pbloc from  ProcBankCreditBlocCompany pbloc left join fetch pbloc.company left join fetch pbloc.bank left join fetch  pbloc.procBankCreditBloc where pbloc.procInstId=?1 and pbloc.defunctInd = 'N'");
            List<ProcBankCreditBlocCompany> blocCompanyList = this.entityService.find(bulder.toString(), processInd);
            if (blocCompanyList.isEmpty()) { return null; }
            if (blocCompanyList.size() > 1) { throw new ServiceException("该流程存在多条成员公司授信申请记录"); }
            return blocCompanyList.get(0);
        } catch (Exception e) {
            log.error("findBlocCompanyByProceIn方法 根据流程实例编号查询成员公司信息  出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 根据成员公司ID得到成员公司</p>
     * @param blocCompanyId
     * @return
     */
    public ProcBankCreditBlocCompany findBlocCompanyById(Long blocCompanyId) throws ServiceException {
        Validate.notNull(blocCompanyId, "成员公司Id为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append(" select pbloc from  ProcBankCreditBlocCompany pbloc left join fetch pbloc.bank left join fetch pbloc.company where pbloc.id=?1 and pbloc.defunctInd = 'N'");
            List<ProcBankCreditBlocCompany> blocCompanyList = this.entityService.find(bulder.toString(), blocCompanyId);
            if (blocCompanyList.isEmpty()) { return null; }
            if (blocCompanyList.size() > 1) { throw new ServiceException("该流程存在多条成员公司授信申请记录"); }
            return blocCompanyList.get(0);
        } catch (Exception e) {
            log.error("findBlocCompanyById方法 根据成员公司ID得到成员公司 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 成员公司提交申请并设置操作人</p>
     * @param workClassNum
     * @param paramMap
     * @param memo
     * @throws ServiceException
     */
    public void vwBlocCompanyDisposeTask(String workClassNum, Map<String, Object> paramMap, String memo)
            throws ServiceException {
        Validate.notNull(workClassNum, "银行授信集团工厂提交申请流程实例编号为空");
        try {
            peManager.vwDisposeTask(workClassNum, paramMap, memo);
        } catch (Exception e) {
            log.error("vwBlocCompanyDisposeTask方法 成员公司提交申请并设置操作人 出现异常：",e);
            throw new ServiceException(ExceptionMessage.PREGITCAPTIAL_SAVE, e);
        }
    }

    /**
     * 
     * <p>Description: 查询成员公司授信的其他产品是否重复</p>
     * @param proBlocId
     * @param rptName
     * @return
     * @throws ServiceException
     */
    public boolean findProcBlocRptCreditByName(Long proBlocId, String rptName) throws ServiceException {
        Validate.notNull(proBlocId, "银行授信成员公司Id为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select rpt from ProcRptBankCreditBloc rpt left join fetch rpt.procBankCreditBlocCompany where rpt.procBankCreditBlocCompany.id=?1 and rpt.defunctInd = 'N' and rpt.cdProName=?2");
            List<ProcRptCredit> list = this.entityService.find(bulder.toString(), proBlocId, rptName);
            if (list.size() > 1) { return true; }
            return false;
        } catch (Exception e) {
            log.error("findProcBlocRptCreditByName方法 查询成员公司授信的其他产品是否重复 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 放弃申请</p>
     * @param workObjNumber
     * @throws ServiceException
     */
    public void doTerminal(String workObjNumber) throws ServiceException {
        Validate.notNull(workObjNumber, "流程实例编号为空");
        try {
            VWWorkObject wob = peManager.vwGetTmsWorkObject(workObjNumber);
            peManager.vwTerminalWorkObject(wob);
        } catch (Exception e) {
            log.error("doTerminal方法 放弃申请 出现异常：",e);
            throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
        }
    }

    /**
     * 
     * <p>Description: 成员公司审批</p>
     * @param workClassNum
     * @param isPass
     * @param memo
     * @throws ServiceException
     */
    public void vwDisposeTask(String workClassNum, boolean isPass, String memo) throws ServiceException {
        Validate.notNull(workClassNum, "银行授信申请流程对应流程实例编号为空");
        try {
            this.peManager.vwDisposeTask(workClassNum, isPass, memo);
        } catch (Exception e) {
            log.error("vwDisposeTask方法 成员公司审批 出现异常：",e);
            throw new ServiceException(ExceptionMessage.PREGITCAPTIAL_SAVE, e);
        }
    }

    /**
     * 
     * <p>Description: 批量更新成员公司</p>
     * @param blocCompanyList
     */
    public void batchUpdateBlocCompany(List<ProcBankCreditBlocCompany> blocCompanyList) {
        if (blocCompanyList != null && blocCompanyList.isEmpty()) { return; }
        for (ProcBankCreditBlocCompany blocCompany : blocCompanyList) {
            if (blocCompany.getId() != null) {
                entityService.update(blocCompany);
            }
        }
    }

    /**
     * 
     * <p>Description:银行授信集团申请是否已经产生过确认单</p>
     * @param blocId
     * @return
     * @throws ServiceException
     */
    public boolean isExistBlocConfirmByblocId(Long blocId) throws ServiceException {
        Validate.notNull(blocId, "银行授信集团申请Id");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append(" select count(bloc) from ProcBankCreditBlocConfirm bloc where bloc.procBankCreditBloc.id =?1 and bloc.defunctInd = 'N'");
            List<Long> list = this.entityService.find(bulder.toString(), blocId);
            Long number = list.get(0);
            return number >= 1 ? true : false;
        } catch (Exception e) {
            log.error("isExistBlocConfirmByblocId方法 银行授信集团申请是否已经产生过确认单 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 产生成员公司确认单</p>
     * @param blocCompanyList
     * @param blocConfirm
     * @throws ServiceException
     */
    public void batchCreateBlocCompanyConfirm(List<ProcBankCreditBlocCompany> blocCompanyList,
            ProcBankCreditBlocConfirm blocConfirm,String procInstId) throws ServiceException {
        Validate.notNull(blocConfirm, "集团确认单Id为空");
        try {
            if (blocCompanyList.isEmpty()) { return; }
            for (ProcBankCreditBlocCompany company : blocCompanyList) {
                ProcBankCreditBlocCompanyConfirm companyConfirm = new ProcBankCreditBlocCompanyConfirm();
                companyConfirm.setBank(company.getBank());
                
                companyConfirm.setProcInstId(procInstId);//设置集团银行授信流程编号
                
                companyConfirm.setBankAcpt(company.getBankAcpt());
                companyConfirm.setBankAcptEf(company.getBankAcptEf());
                companyConfirm.setBankAcptFe(company.getBankAcptFe());
                companyConfirm.setBankAcptGp(company.getBankAcptGp());
                companyConfirm.setBussTicket(company.getBussTicket());
                companyConfirm.setBussTicketDc(company.getBussTicketDc());
                companyConfirm.setBussTicketFe(company.getBussTicketFe());
                companyConfirm.setBussTicketGp(company.getBussTicketGp());
                // add on 2013-7-1 by yan
                companyConfirm.setForwTrade(company.getForwTrade());
                companyConfirm.setForwTradeCr(company.getForwTradeCr());
                companyConfirm.setCompany(company.getCompany());
                companyConfirm.setCreditLine(company.getCreditLine());
                companyConfirm.setDefunctInd("N");
                companyConfirm.setDollarFlowFinance(company.getDollarFlowFinance());
                companyConfirm.setDollarFlowLink(company.getDollarFlowLink());
                companyConfirm.setDollarFlowPoint(company.getDollarFlowPoint());
                companyConfirm.setDomesticCred(company.getDomesticCred());
                companyConfirm.setDomesticCredDf(company.getDomesticCredDf());
                companyConfirm.setDomesticCredFe(company.getDomesticCredFe());
                companyConfirm.setDomesticCredGp(company.getDomesticCredGp());
                companyConfirm.setExportFinance(company.getExportFinance());
                companyConfirm.setExportFinanceLink(company.getExportFinanceLink());
                companyConfirm.setExportFinancePonit(company.getExportFinancePonit());
                companyConfirm.setGuaranteeCd(company.getGuaranteeCd());
                companyConfirm.setGuaranteeGr(company.getGuaranteeGr());
                companyConfirm.setGuaranteeMg(company.getGuaranteeMg());
                companyConfirm.setGuaranteeOt(company.getGuaranteeOt());
                companyConfirm.setGuaranteeQm(company.getGuaranteeQm());
                companyConfirm.setImportCredit(company.getImportCredit());
                companyConfirm.setImportCreditFe(company.getImportCreditFe());
                companyConfirm.setImportCreditGp(company.getImportCreditGp());
                companyConfirm.setImportFinance(company.getImportFinance());
                companyConfirm.setImportFinanceLink(company.getImportFinanceLink());
                companyConfirm.setImportFinancePonit(company.getImportFinancePonit());
                companyConfirm.setLiquCred(company.getLiquCred());
                companyConfirm.setLiquCredAp(company.getLiquCredAp());
                companyConfirm.setLiquCredPonit(company.getLiquCredPonit());
                companyConfirm.setLiquCredRa(company.getLiquCredRa());
                companyConfirm.setNotarizeCreditLine(company.getNotarizeCreditLine());
                companyConfirm.setProcBankCreditBlocConfirm(blocConfirm);
                companyConfirm.setStatus(company.getStatus());
                //huhan add on 8.10 合作理由简述
                companyConfirm.setCooperationReason(company.getCooperationReason());
                
                ProcBankCreditBlocCompanyConfirm newCompanyConfirm = entityService.create(companyConfirm);
                List<ProcRptBankCreditBloc> rptList = findBlocRptByBlocCom(company.getId());
                for (ProcRptBankCreditBloc rptCreditBloc : rptList) {
                    ProcRptBankCreditBlocConfirm blocConfirmRpt = new ProcRptBankCreditBlocConfirm();
                    blocConfirmRpt.setCdProLimit(rptCreditBloc.getCdProLimit());
                    blocConfirmRpt.setCdProName(rptCreditBloc.getCdProName());
                    blocConfirmRpt.setProcBankCreditBlocCompanyConfirm(newCompanyConfirm);
                    entityService.create(blocConfirmRpt);
                }
            }
        } catch (Exception e) {
            log.error("batchCreateBlocCompanyConfirm方法 产生成员公司确认单 出现异常：",e);
            throw new ServiceException(e);
        }
    }

    /**
   	 * 查询流程详细
   	 * @param procInstId
   	 * @return
   	 */
   	public List<ProcessDetailVo> getProcessDetail(String procInstId) throws ServiceException {
   		List<ProcessDetailVo> detailVos = new ArrayList<ProcessDetailVo>();
   		String filter = "1=1 and F_WobNum = :wobNum and (F_EventType = :eventType1 or F_EventType = :eventType2)";
   		Object[] substitutionVars = { new VWWorkObjectNumber(procInstId),
   				ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd),
   				ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal) };
   		try {
   			List<VWLogElement> les = new ArrayList<VWLogElement>();
   			les = peManager.vwEventLogWob(filter, substitutionVars);
   			for (VWLogElement le : les) {
   				ProcessDetailVo detailVo = new ProcessDetailVo();
   				if (ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal).equals(le.getEventType())) {
   					detailVo.setProssNodeName("流程终止");
   				} else {
   					detailVo.setProssNodeName(le.getStepName());
   				}
   				detailVo.setOperatorsName(le.getUserName());
   				detailVo.setOperatorTime(le.getTimeStamp());
   				detailVo.setNodeMemo((String) le.getFieldValue("F_Comment"));
   				detailVo.setId(new Long(le.getSequenceNumber()));
   				detailVos.add(detailVo);
   			}
   		} catch (Exception e) {
   			log.error("getProcessDetail方法 查询流程详细 出现异常：",e);
   			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
   		}
   		return detailVos;
   	}
}
