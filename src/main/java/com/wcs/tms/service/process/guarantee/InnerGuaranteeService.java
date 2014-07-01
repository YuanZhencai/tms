package com.wcs.tms.service.process.guarantee;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.DateUtil;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.Credit;
import com.wcs.tms.model.Guarantee;
import com.wcs.tms.model.ProcGuarantee;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.system.company.CreditService;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWWorkObjectNumber;

/**
 * <p>Project: tms</p>
 * <p>Description: 内部担保申请Service</p>
 * <p>Copyright © 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
@Stateless
public class InnerGuaranteeService {

    private static Log log = LogFactory.getLog(InnerGuaranteeService.class);
    @Inject
    PEManager peManager;
    @Inject
    EntityService entityService;
    @Inject
    LoginService loginService;
    @Inject
    ProcessUtilMapService processUtilMapService;
    @Inject
    CreditService creditService;

    /**
     * 流程创建保存
     * @return
     */
    public String createProcInstance(ProcGuarantee procGuarantee) throws ServiceException {
        String procInstId = "";
        try {
            // PE流程创建
            // 流程实例ID，并保存
            procInstId = this.vwApplication(procGuarantee);
            procGuarantee.setProcInstId(procInstId);
            entityService.create(procGuarantee);

            // 生成流程实例编号映射
            processUtilMapService.generateProcessMap(procInstId, "BPM_RA_004", procGuarantee.getCompany());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(ExceptionMessage.INSTANCE_CREATE, e);
        }
        return procInstId;
    }

    /**
     * 流程实例Id获取流程实体
     * @param procInstId
     * @return
     */
    public ProcGuarantee findProcInstanceByProcInstId(String procInstId) {
        StringBuilder jpql = new StringBuilder(
                "select db from ProcGuarantee db join fetch db.company join fetch db.securedCom left join fetch db.bank where ");
        jpql.append(" db.procInstId='" + procInstId + "'");
        return entityService.findUnique(jpql.toString());
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
                if (ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal).equals(
                        le.getEventType())) {
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
            e.printStackTrace();
            throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
        }
        return detailVos;
    }

    /**
     * PE流程创建
     * @param dailyPayLoanTran
     * @return
     * @throws ServiceException
     */
    public String vwApplication(ProcGuarantee procGuarantee) throws ServiceException {
        String workClassName = ProcessXmlUtil.getProcessAttribute("id", "GuaranteeReq", "className"); 
        String workflowNumber = "";
        if (peManager.checkWorkClassName(workClassName)) {
            try {
                // 验证流程类名
                workflowNumber = peManager.vwCreateInstance(workClassName, "tms_subject1");

                // 设置流程参数
                HashMap<String, Object> step1para = new HashMap<String, Object>();
                //设置工作流组TMS_Insured_Top_Manager，受保方总经理               
                List<String> userList = loginService.getAccountByComIdOrPosName(procGuarantee.getCompany().getId(), "总经理");
                
                String[] topManagerArr = (String[])userList.toArray(new String[userList.size()]);
                //参与者没找到，默认设置为fnadmin用户，否则，只设置其中一个
                String[] paticpant = {""};
                if(topManagerArr.length == 0){
                    log.info("根据申请公司，没有找到受保方总经理！");
                }else{
                    paticpant[0] = topManagerArr[0];
                }
                
                for(String user :topManagerArr){
                    log.info("TMS_Insured_Top_Manager:---user:"+user);
                }
                
                step1para.put("TMS_Insured_Top_Manager", paticpant);
                peManager.vwLauchStep("TMS_Requester", step1para, workflowNumber);
            } catch (Exception e) {
                log.error("vwApplication方法 PE流程创建出现异常:" , e);
                throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
            }
        } else {
            throw new ServiceException(ExceptionMessage.FN_NO_CLASS);
        }

        return workflowNumber;
    }

    /**
     * 审批保存
     * @param dailyPayLoanTran
     * @param pass
     * @param stepName
     * @throws ServiceException
     */
    public void doApprove(ProcGuarantee procGuarantee, boolean pass, String stepName) throws ServiceException {
        try {
            // 加入流程备注抬头
            String memoTitle = "";
            if (pass) {
                memoTitle = ProcessXmlUtil.findStepProperty("id", "GuaranteeReq", stepName, "passMemo");
            } else {
                memoTitle = ProcessXmlUtil.findStepProperty("id", "GuaranteeReq", stepName, "nopassMemo");
            }
            if (memoTitle != null) {
                procGuarantee.setPeMemo(memoTitle + procGuarantee.getPeMemo());
            }
            // 先执行更新操作
            entityService.update(procGuarantee);
            Map<String, Object> fnParamMap = new HashMap<String, Object>();
            //当受保方和担保方公司，不是同一个时，说明担保方发送了变更，然后重新更新后续节点担保方的相关人员
            // 2013-5-28
            if("集团资金部门审批".equals(stepName) && true == pass){
                //设置工作流组：
                //1、TMS_Secured_Fin_Manager，担保方财务经理    
                //2、TMS_Secured_Top_Manager，担保方总经理
                List<String> finManagerList = loginService.getAccountByComIdOrPosName(procGuarantee.getSecuredCom().getId(), "财务经理");
                List<String> topManagerList = loginService.getAccountByComIdOrPosName(procGuarantee.getSecuredCom().getId(), "总经理");
                
                String[] finManagerArr = (String[])finManagerList.toArray(new String[finManagerList.size()]);
                String[] topManagerArr = (String[])topManagerList.toArray(new String[topManagerList.size()]);
                
                String[] finManager = new String[]{""};
                String[] topManager = new String[]{""};
                for(String user :finManagerArr){
                    log.info("TMS_Secured_Fin_Manager:---user:"+user);
                }
                for(String user :topManagerArr){
                    log.info("TMS_Secured_Top_Manager:---user:"+user);
                }
                if(finManagerArr.length == 0){
                    log.info("根据担保公司，没有找到担保方财务经理！");
                }else{
                    finManager[0] = finManagerArr[0];
                }
                if(topManagerArr.length == 0){
                    log.info("根据担保公司，没有找到担保方总经理！");
                }else{
                    topManager[0] = topManagerArr[0];
                }               
                
                fnParamMap.put("TMS_Secured_Fin_Manager", finManager);
                fnParamMap.put("TMS_Secured_Top_Manager", topManager);
            }
            
            if("申请人确认".equals(stepName)){
                boolean isConfirmAmount_GreaterThan_ReqAmount = procGuarantee.getRealGuarAmount() > procGuarantee.getApplyGuarAmount()?true:false;
                log.info("确认金额大于申请金额："+isConfirmAmount_GreaterThan_ReqAmount);
                fnParamMap.put("TMS_ConfirmAmount_GreaterThan_ReqAmount", isConfirmAmount_GreaterThan_ReqAmount);
            }     
            
            fnParamMap.put("_Pass", pass);            
            //处理流程任务            
            peManager.vwDisposeTask(procGuarantee.getProcInstId(), fnParamMap, procGuarantee.getPeMemo());
            //处理流程结束事件
            handleProcessEndEvent(procGuarantee,pass,stepName);

        } catch (Exception e) {
            log.error("doApprove方法 审批保存 出现异常" , e);
            throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
        }
    }
    
    /**
     * 
     * <p>Description: 处理流程结束事件，保存担保数据至主数据表</p>
     * <p>担保流程结束，两中可能：</p>
     * <p>1、【申请人确认】节点通过后，且确认金额小于申请金额；</p>
     * <p>2、【担保方总经理确认审批】节点，通过后；</p>
     */
    public void handleProcessEndEvent(ProcGuarantee procGuarantee, boolean pass, String stepName){
        if("申请人确认".equals(stepName)){            
            boolean isConfirmAmount_GreaterThan_ReqAmount = procGuarantee.getRealGuarAmount() > procGuarantee.getApplyGuarAmount()?true:false;
            if(!isConfirmAmount_GreaterThan_ReqAmount){
                saveGuarantee(procGuarantee);
            }
        }
        
        if("担保方总经理确认审批".equals(stepName) && pass){
            saveGuarantee(procGuarantee);
        }         
    }
    
    /**
     * 
     * <p>Description:把担保流程实例数据，写入主数据表 </p>
     * @param procGuarantee
     */
    public void saveGuarantee(ProcGuarantee procGuarantee){
        if(procGuarantee == null) {
            return;
        }
        Guarantee guarantee =  new Guarantee();
        guarantee.setBank(procGuarantee.getBank());
        guarantee.setInsuredCom(procGuarantee.getCompany());
        guarantee.setSecuredCom(procGuarantee.getSecuredCom());
        guarantee.setProcInstId(procGuarantee.getProcInstId());        
        guarantee.setUseDesc(procGuarantee.getUseDesc());
        guarantee.setSecuredAssets(procGuarantee.getSecuredAssets());
        //流程实例中实际担保金额字段值，写入主数据中担保金额
        guarantee.setGuarAmount(procGuarantee.getRealGuarAmount());
        guarantee.setGuarAmountCu(procGuarantee.getGuarAmountCu());
        guarantee.setGuarStart(procGuarantee.getGuarStart());
        guarantee.setGuarEnd(procGuarantee.getGuarEnd());
        
        guarantee.setCreatedBy(procGuarantee.getCreatedBy());
        guarantee.setCreatedDatetime(new Date());
        guarantee.setUpdatedBy(procGuarantee.getCreatedBy());
        guarantee.setUpdatedDatetime(new Date());        
        
        entityService.create(guarantee);
    }
    
    /**
     * 得到公司已授信银行select
     * @param companyId
     * @return
     */
    public List<SelectItem> getCreditBankSelect(Long companyId){
        List<SelectItem> bankSelect = new ArrayList<SelectItem>();
        List<Credit> cs = this.getCreditBank(companyId);
        for(Credit c : cs){
            //去重复银行
            boolean has = false;
            for(SelectItem si : bankSelect){
                if(c.getBank().getId().equals(si.getValue())){
                    has = true;
                }
            }
            if(!has){
                bankSelect.add(new SelectItem(c.getBank().getId(), c.getBank().getBankName()));
            }
        }
        return bankSelect;
    }
    
    /**
     * 得到公司授信关系列表
     * @param companyId
     * @return
     */
    public List<Credit> getCreditBank(Long companyId){
        return creditService.findCreditFetchBank1(companyId);
    }
    
    /**
     * <p>Description: 查询担保方(或受保方)公司有效期内经过审批的担保金额</p>
     * @param processInstId 
     * @param companyId
     * @param isGuar true 查询担保方公司的已担保的金额，false 查询受保方公司的已受保金额
     * @return 担保金额
     * @throws ServiceException
     */
    public Double getGuarAmountByCompanyId(String processInstId, Long companyId,boolean isGuar) throws ServiceException {
        Validate.notNull(companyId, "公司Id为空");
        Double retAmount = 0d;
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select c from Guarantee c where c.defunctInd = 'N'");            
            if(isGuar){
                bulder.append(" and c.securedCom.id=?1 ");
            }else{
                bulder.append(" and c.insuredCom.id=?1 ");
            }
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
            String now = sdf.format(new Date());
            bulder.append(" and c.guarStart <= '"+ now +"'");
            // add by yanchangjing on 2013-1-11
            bulder.append(" and c.procInstId <> ?2");
            //modify by liushengbin 2012-11-28 根据需求，修改担保有效期延期一年,即把当前年份减去一年来查询 担保表中的结束日期
            bulder.append(" and c.guarEnd >= '"+ DateUtil.dateToStrShort(DateUtil.adjustYearAndMonthAndDay(new Date(), -1, 0, 0)) +"'");
            bulder.append(" order by c.guarEnd desc");
            List<Guarantee> list = entityService.find(bulder.toString(), companyId, processInstId);
            for(Guarantee guar :list){
                retAmount = retAmount + guar.getGuarAmount();
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return retAmount;
    }
    
    /**
     * <p>Description: 查询担保方(或受保方)公司预担保的金额</p>
     * <p>即：（经过【集团财务总监审批】并同意的担保额度）</p>
     * <p></p>  
     * @param processInstId 当前流程实例 （计算预担保，不把当前流程实例计算在内）
     * @param companyId    担保方或受保方公司id
     * @param status 状态: 1 -- 审批中 , 2 -- 集团资金部审批完到集团财务总监审批完之间, 3 -- 经过【集团财务总监审批】并同意的担保 4 -- 审批结束;
     * @param isGuar true 查询担保方公司的预担保的金额，false 查询受保方公司的预受保金额
     * @return 预担保金额
     * @throws ServiceException
     */
    public Double getPreGuarAmountByCompanyId(String processInstId,Long companyId,String status,boolean isGuar) throws ServiceException {
        Validate.notNull(processInstId, "流程实例Id为空");
        Validate.notNull(companyId, "公司Id为空");
        Validate.notNull(status, "状态为空");
        Double retAmount = 0d;
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select c from ProcGuarantee c where ");
            if(isGuar){
                bulder.append(" c.securedCom.id=?1 ");
            }else{
                bulder.append(" c.company.id=?1 ");
            }            
            bulder.append(" and c.defunctInd = 'N' and c.status = ?2");
            bulder.append(" and c.procInstId <> ?3"); 
            List<ProcGuarantee> list = entityService.find(bulder.toString(), companyId,status,processInstId);
            for(ProcGuarantee guar :list){
                retAmount = retAmount + guar.getGuarAmount();
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return retAmount;
    }
    
    /**
     * <p>Description: 查询受保方在某担保公司有效期内经过审批的担保金额</p>
     * @param processInstId 
     * @param companyId 受保方
     * @param securedComId 担保方
     * @return 担保金额
     * @throws ServiceException
     */
    public Double getAmountByInsuredComAndCompanyId(String processInstId, Long companyId,Long securedComId) throws ServiceException {
        Validate.notNull(companyId, "公司Id为空");
        Double retAmount = 0d;
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select c from Guarantee c where c.defunctInd = 'N'");
            bulder.append(" and c.insuredCom.id=?1 ");
            bulder.append(" and c.securedCom.id=?2 ");
            // add by yanchchangjing on 2013-1-11
            bulder.append(" and c.procInstId <> ?3"); 
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
            String now = sdf.format(new Date());
            bulder.append(" and c.guarStart <= '"+ now +"'");
            bulder.append(" and c.guarEnd >= '"+ DateUtil.dateToStrShort(DateUtil.adjustYearAndMonthAndDay(new Date(), -1, 0, 0)) +"'");
            bulder.append(" order by c.guarEnd desc");
            List<Guarantee> list = entityService.find(bulder.toString(), companyId, securedComId, processInstId);
            for(Guarantee guar :list){
                retAmount = retAmount + guar.getGuarAmount();
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return retAmount;
    }

    /**
     * 查询受保方在某担保公司有效期内预担保金额
     * @param processInstId
     * @param companyId
     * @param securedComId
     * @param status
     * @return
     */
	public Double getPreAmountByInsuredComAndCompanyId(String processInstId,
			Long companyId, Long securedComId, String status) {
		Double preAmount = 0d;
		try {
			StringBuilder bulder = new StringBuilder();
	        bulder.append("select c from ProcGuarantee c where ");
	        bulder.append("c.securedCom.id=?1 ");
	        bulder.append(" and c.company.id=?2 ");
	        bulder.append(" and c.defunctInd = 'N' and c.status = ?3");
	        bulder.append(" and c.procInstId <> ?4");
	        List<ProcGuarantee> list = entityService.find(bulder.toString(), securedComId, companyId,status,processInstId);
	        for(ProcGuarantee guar :list){
	        	preAmount = preAmount + guar.getGuarAmount();
	        }
		}catch (Exception e) {
            throw new ServiceException(e);
        }
        return preAmount;
	}
}
