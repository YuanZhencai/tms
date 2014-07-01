package com.wcs.tms.service.process.inveproduct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.Credit;
import com.wcs.tms.model.InveProductDetail;
import com.wcs.tms.model.ProcInveProduct;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.system.company.CreditService;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWWorkObjectNumber;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:投资理财流程Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Stateless
public class InveProductService implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(InveProductService.class);
	@Inject
    EntityService entityService;
    @Inject
    PEManager peManager;
    @Inject
	LoginService loginService;
    @Inject
    CreditService creditService;
    //9.11
    @Inject
    ProcessUtilMapService processUtilMapService;
    
    List<Credit> cs = new ArrayList<Credit>();
    
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
    	cs = creditService.findCreditFetchBank1(companyId);
    	return cs;
    }
    
    /**
     * 得到公司银行授信关系
     * @param childBankId
     * @return
     */
    public Credit getCredit(Long companyId, Long childBankId){
    	cs = this.getCreditBank(companyId);
    	Credit c1 = new Credit();
    	for(Credit c : cs){
    		if(childBankId.equals(c.getBank().getId())){
    			c1 = c;
    		}
    	}
    	return c1;
    }
    
    /**
     * 流程创建保存
     * @return
     */
    public String createProcInstance(ProcInveProduct procInveProduct) throws ServiceException{
    	
    	String procInstId ="";
    	try {
    		//PE流程创建
    		//流程实例ID，并保存
    		procInstId = this.vwApplication(procInveProduct);
    		procInveProduct.setProcInstId(procInstId);
    		entityService.create(procInveProduct);
    		//生成流程实例编号映射9.11
    		processUtilMapService.generateProcessMap(procInstId, "BPM_RA_005", procInveProduct.getCompany());
		} catch (Exception e) {
			log.error("createProcInstance方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.INSTANCE_CREATE, e);
		}
    	return procInstId;
    }
    
    /**
     * PE流程创建
     * @param procDebtBorrow
     * @return
     * @throws Exception
     */
	public String vwApplication(ProcInveProduct procInveProduct) throws ServiceException{
		String workClassName = ProcessXmlUtil.getProcessAttribute("id", "InveProduct", "className");
    	String workflowNumber = "";
    	if(peManager.checkWorkClassName(workClassName)){
    		try {
    			//验证流程类名
    			workflowNumber = peManager.vwCreateInstance(workClassName, "tms_subject1");
    			
    			HashMap<String, Object> step1para = new HashMap<String, Object>();
    			
    			peManager.vwLauchStep("TMS_Cop_Fund_Dep_3", step1para, workflowNumber);
			} catch (Exception e) {
			    log.error("vwApplication方法 错误信息：" + e.getMessage());
				throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
			}
    	}else{
    		throw new ServiceException(ExceptionMessage.FN_NO_CLASS);
    	}
		
    	return workflowNumber;
    }
	
	/**
	 * 流程实例Id得到外债申请实例
	 * @param procInstId
	 * @return
	 */
	public ProcInveProduct findProcInstanceByProcInstId(String procInstId){
		StringBuilder jpql = new StringBuilder("select ip from ProcInveProduct ip join fetch ip.company join fetch ip.bank where ip.defunctInd = 'N'");
		jpql.append(" and ip.procInstId='"+procInstId+"'");
		
		return entityService.findUnique(jpql.toString());
	}
	
	/**
	 * 查询流程详细
	 * @param procInstId
	 * @return
	 */
	public List<ProcessDetailVo> getProcessDetail(String procInstId) throws ServiceException{
		List<ProcessDetailVo> detailVos = new ArrayList<ProcessDetailVo>();
		String filter = "1=1 and F_WobNum = :wobNum and (F_EventType = :eventType1 or F_EventType = :eventType2)";
		Object[] substitutionVars = { new VWWorkObjectNumber(procInstId) , ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.StepEnd) , ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal)};
		try {
			List<VWLogElement> les = new ArrayList<VWLogElement>();
			les = peManager.vwEventLogWob(filter, substitutionVars);
			for(VWLogElement le : les){
				ProcessDetailVo detailVo=new ProcessDetailVo();
				if(ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal).equals(le.getEventType())){
					detailVo.setProssNodeName("流程终止");
				}else{
					detailVo.setProssNodeName(le.getStepName());
				}
				detailVo.setOperatorsName(le.getUserName());
				detailVo.setOperatorTime(le.getTimeStamp());
				detailVo.setNodeMemo((String)le.getFieldValue("F_Comment"));
				detailVo.setId(new Long(le.getSequenceNumber()));
				detailVos.add(detailVo);
			}
		} catch (Exception e) {
		    log.error("getProcessDetail方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return detailVos;
	}
	
	/**
	 * 审批保存
	 * @param procDebtBorrow
	 * @param pass
	 */
	public void doApprove(ProcInveProduct procInveProduct , boolean pass, String stepName) throws ServiceException{
		try {
			//加入流程备注抬头
			String memoTitle = "";
			if(pass){
				memoTitle = ProcessXmlUtil.findStepProperty("id", "InveProduct", stepName, "passMemo");
			}else{
				memoTitle = ProcessXmlUtil.findStepProperty("id", "InveProduct", stepName, "nopassMemo");
			}
			if(memoTitle!=null){
				procInveProduct.setPeMemo(memoTitle + procInveProduct.getPeMemo());
			}
			//先执行更新操作
			entityService.update(procInveProduct);
			peManager.vwDisposeTask(procInveProduct.getProcInstId(), pass, procInveProduct.getPeMemo());
			
			//是否保存确认信息
			if(pass){
				String confirm = ProcessXmlUtil.findStepProperty("id", "InveProduct", stepName, "confirm");
				if(confirm!=null && "true".equals(confirm)){
					saveInveDetail(procInveProduct);
				}
			}
		} catch (Exception e) {
		    log.error("doApprove方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}
	
	/**
	 * 理财产品明细保存
	 * @param procInveProduct
	 */
	private void saveInveDetail(ProcInveProduct proc) throws Exception{
		InveProductDetail detail = new InveProductDetail();
		Company comp = entityService.find(Company.class, proc.getCompany().getId());
		Bank bank = entityService.find(Bank.class, proc.getBank().getId());
		detail.setCompany(comp);
		detail.setBank(bank);
		detail.setProcInstId(proc.getProcInstId());
		detail.setBankLevel(proc.getBankLevel());
		detail.setBankCredit(proc.getBankCredit());
		detail.setBankCreditCu(proc.getBankCreditCu());
		detail.setPresent(proc.getPresent());
		detail.setProductType(proc.getProductType());
		detail.setProductName(proc.getProductName());
		detail.setProductForm(proc.getProductForm());
		detail.setLimit(proc.getLimit());
		detail.setLimitUnit(proc.getLimitUnit());
		detail.setAmount(proc.getAmount());
		detail.setAmountCu(proc.getAmountCu());
		detail.setReason(proc.getReason());
		detail.setRequestDate(proc.getCreatedDatetime());
		detail.setPassDate(new Date());
		
		entityService.create(detail);
	}
}
