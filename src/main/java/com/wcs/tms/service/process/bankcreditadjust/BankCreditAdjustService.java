package com.wcs.tms.service.process.bankcreditadjust;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.common.model.P;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.mail.Mail;
import com.wcs.tms.mail.MailService;
import com.wcs.tms.mail.MailUtil;
import com.wcs.tms.mail.SendMailService;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.Credit;
import com.wcs.tms.model.CreditO;
import com.wcs.tms.model.CreditP;
import com.wcs.tms.model.CreditR;
import com.wcs.tms.model.ProcBankCreditAdjust;
import com.wcs.tms.model.ProcRptAdjustO;
import com.wcs.tms.model.ProcRptAdjustProv;
import com.wcs.tms.model.ProcRptAdjustProvO;
import com.wcs.tms.service.process.bankcreditadjust.entity.Provider;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.system.company.CreditService;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

import filenet.vw.api.VWDataField;
import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWWorkObject;
import filenet.vw.api.VWWorkObjectNumber;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:授信调剂Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Stateless
public class BankCreditAdjustService implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Inject
    EntityService entityService;
    @Inject
    PEManager peManager;
    @Inject
	LoginService loginService;
    @Inject
    CreditService creditService;
    @Inject
    MailService mailService;
    @Inject
    SendMailService sendMailService;
    @Inject
    ProcessUtilMapService processUtilMapService;//9.10
    
    List<Credit> cs = new ArrayList<Credit>();
    
    private static final Log log = LogFactory.getLog(BankCreditAdjustService.class);
    
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
    public Credit getCredit(Long companyId,Long childBankId){
    	//查出授信到期日最晚的一条 modified on 2013-6-25 by yan
    	StringBuilder bulder = new StringBuilder("select c.company.id,c.bank.id,MAX(c.creditEnd) from Credit c join fetch c.company join fetch c.bank " +
    			"where c.defunctInd = 'N' and c.status='Y'");
        bulder.append(" and c.company.id="+companyId);
        bulder.append(" and c.bank.id="+childBankId);
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        String now = sdf.format(new Date());
        bulder.append(" and c.creditStart <= '"+ now +"'");
        bulder.append(" and c.creditEnd >= '"+ now +"'");
        bulder.append(" group by c.company.id,c.bank.id");
        List<Object[]> rows = entityService.find(bulder.toString());
        StringBuilder jpql = new StringBuilder("select c from Credit c join fetch c.company join fetch c.bank " +
    			"where c.defunctInd = 'N' and c.status='Y'");
        jpql.append(" and c.company.id="+companyId);
        jpql.append(" and c.bank.id="+childBankId);
        jpql.append(" and c.creditEnd='"+rows.get(0)[2]+"'");
        return (Credit)entityService.find(jpql.toString()).get(0);
    }
    
    /**
     * 根据子银行得到同一一级银行下的授信提供方下拉列表
     * @param childBank
     */
    public List<SelectItem> findProviders(Bank childBank,Long exceptCid){
    	List<SelectItem> providerSelect = new ArrayList<SelectItem>();
    	List<Long> bankIds = new ArrayList<Long>();
    	StringBuilder jpql = new StringBuilder("select b.id from Bank b where b.defunctInd = 'N' and b.status='Y'");
    	jpql.append(" and b.topBankId =" + childBank.getTopBankId());
    	bankIds = entityService.find(jpql.toString());
    	cs = creditService.findCreditFetchCompMut(bankIds);
    	
    	//add on 8.9 得到申请方公司id 和 币种
    	Long reqCompId = 0l;
    	String reqCreditCu = "";
    	for(Credit c : cs){
    		if(exceptCid!=null && exceptCid.equals(c.getId())){
    			reqCompId = c.getCompany().getId();
    			reqCreditCu = c.getCreditLineCu();
    		}
    	}
    	List<Long> providerIds = new ArrayList<Long>();
    	for(Credit c : cs){
    		//申请方授信id 改为 用申请方公司id 除去申请方的授信
    		if(reqCompId != 0l && reqCompId.equals(c.getCompany().getId())){
    			continue;
    		}
    		//和申请方"不同"币种的授信去掉
    		if(!"".equals(reqCreditCu) && !reqCreditCu.equals(c.getCreditLineCu())){
    			continue;
    		}
    		
    		//去掉提供方重复的工厂
    		if(!providerIds.contains(c.getCompany().getId())){
    			SelectItem si = new SelectItem(c.getId(), c.getCompany().getCompanyName());
    			providerSelect.add(si);
    			providerIds.add(c.getCompany().getId());
    		//如果重复 用到期日最晚的 覆盖add on 2013-6-27 by yan
    		//*****************************************************//
    		}else{
    			Integer index = providerIds.indexOf(c.getCompany().getId());
    			SelectItem si = providerSelect.get(index);
    			Credit cin = creditService.findUniqueCreditById((Long)si.getValue());
    			if(c.getCreditEnd().after(cin.getCreditEnd()) ){
    				providerSelect.set(index, new SelectItem(cin.getId(),c.getCompany().getCompanyName()));
    			}
    		}
    		//*****************************************************//
    	}
    	return providerSelect;
    }
    
    /**
     * 得到授信实体抓取银行和公司
     * @param cid
     * @return
     */
    public Credit findCreditFetch(Long cid){
    	Validate.notNull(cid, "授信Id为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select c from Credit c join fetch c.company join fetch c.bank where c.id=?1").append(
                    " and c.defunctInd = 'N' and c.status='Y'");
            return entityService.findUnique(bulder.toString(), cid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
    
    
    /**
     * 工厂流程创建保存
     * @return
     */
    public String createProcInstance(ProcBankCreditAdjust procBankCreditAdjust) throws ServiceException{
    	
    	String procInstId ="";
    	try {
    		//PE流程创建
    		//流程实例ID，并保存
    		procInstId = this.vwApplication(procBankCreditAdjust);
    		procBankCreditAdjust.setProcInstId(procInstId);
    		
    		//修改申请方相关调剂金额
    		procBankCreditAdjust.setLiquCred(procBankCreditAdjust.getLiquCred() + procBankCreditAdjust.getCreditAdd());
    		procBankCreditAdjust.setBankAcpt(procBankCreditAdjust.getBankAcpt() + procBankCreditAdjust.getCreditAdd());
    		procBankCreditAdjust.setImportCredit(procBankCreditAdjust.getImportCredit() + procBankCreditAdjust.getCreditAdd());
    		procBankCreditAdjust.setImportFinance(procBankCreditAdjust.getImportFinance() + procBankCreditAdjust.getCreditAdd());
    		procBankCreditAdjust.setOutportFinance(procBankCreditAdjust.getOutportFinance() + procBankCreditAdjust.getCreditAdd());
    		procBankCreditAdjust.setDollarFlow(procBankCreditAdjust.getDollarFlow() + procBankCreditAdjust.getCreditAdd());
    		procBankCreditAdjust.setDomesticCred(procBankCreditAdjust.getDomesticCred() + procBankCreditAdjust.getCreditAdd());
    		procBankCreditAdjust.setBussTicket(procBankCreditAdjust.getBussTicket() + procBankCreditAdjust.getCreditAdd());
    		procBankCreditAdjust.setForwTrade(procBankCreditAdjust.getForwTrade() + procBankCreditAdjust.getCreditAdd());
    		
    		ProcBankCreditAdjust newPca = entityService.create(procBankCreditAdjust);
    		for(ProcRptAdjustO pao : procBankCreditAdjust.getProcRptAdjustOs()){
    			pao.setOtherLimit(pao.getOtherLimit() + procBankCreditAdjust.getCreditAdd());
    			pao.setProcBankCreditAdjust(newPca);
    			entityService.create(pao);
    		}
    		//生成流程实例编号映射9.11
    		processUtilMapService.generateProcessMap(procInstId, "BPM_RA_003", procBankCreditAdjust.getCompany());
		} catch (Exception e) {
			log.error("createProcInstance方法 工厂流程创建保存 出现异常：",e);
			throw new ServiceException(ExceptionMessage.INSTANCE_CREATE, e);
		}
    	return procInstId;
    }
    
    /**
	 * 集团流程创建保存
	 * @param procBankCreditAdjust
	 * @param pass
	 * @param stepName
	 * @param providers
	 * @throws ServiceException
	 */
	public String createProcInstanceCop(ProcBankCreditAdjust procBankCreditAdjust ,List<Provider> providers) throws ServiceException{
		String procInstId ="";
		try {
			//PE流程创建
    		//流程实例ID，并保存
    		procInstId = this.vwApplication(procBankCreditAdjust);
    		procBankCreditAdjust.setProcInstId(procInstId);
    		
    		//先执行创建申请方操作
    		//修改申请方相关调剂金额
    		procBankCreditAdjust.setLiquCred(procBankCreditAdjust.getLiquCred() /*+ procBankCreditAdjust.getCreditAdd()*/);
    		procBankCreditAdjust.setBankAcpt(procBankCreditAdjust.getBankAcpt() /*+ procBankCreditAdjust.getCreditAdd()*/);
    		procBankCreditAdjust.setImportCredit(procBankCreditAdjust.getImportCredit() /*+ procBankCreditAdjust.getCreditAdd()*/);
    		procBankCreditAdjust.setImportFinance(procBankCreditAdjust.getImportFinance() /*+ procBankCreditAdjust.getCreditAdd()*/);
    		procBankCreditAdjust.setOutportFinance(procBankCreditAdjust.getOutportFinance() /*+ procBankCreditAdjust.getCreditAdd()*/);
    		procBankCreditAdjust.setDollarFlow(procBankCreditAdjust.getDollarFlow() /*+ procBankCreditAdjust.getCreditAdd()*/);
    		procBankCreditAdjust.setDomesticCred(procBankCreditAdjust.getDomesticCred() /*+ procBankCreditAdjust.getCreditAdd()*/);
    		procBankCreditAdjust.setBussTicket(procBankCreditAdjust.getBussTicket() /*+ procBankCreditAdjust.getCreditAdd()*/);
    		procBankCreditAdjust.setForwTrade(procBankCreditAdjust.getForwTrade());
    		
    		ProcBankCreditAdjust newPca = entityService.create(procBankCreditAdjust);
    		for(ProcRptAdjustO pao : procBankCreditAdjust.getProcRptAdjustOs()){
    			pao.setOtherLimit(pao.getOtherLimit() /*+ procBankCreditAdjust.getCreditAdd()*/);
    			pao.setProcBankCreditAdjust(newPca);
    			entityService.create(pao);
    		}
    		//生成流程实例编号映射9.11
    		processUtilMapService.generateProcessMap(procInstId, "BPM_RA_003", procBankCreditAdjust.getCompany());
			//再执行"刷新"提供方
			this.createProvider(procBankCreditAdjust, providers);
			
		} catch (Exception e) {
			log.error("createProcInstanceCop方法 集团流程创建保存 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return procInstId;
	}
    
    /**
     * PE流程创建
     * @param procDebtBorrow
     * @return
     * @throws Exception
     */
	public String vwApplication(ProcBankCreditAdjust procBankCreditAdjust) throws ServiceException{
		String workClassName = ProcessXmlUtil.getProcessAttribute("id", "BankCreditAdjust", "className");
    	String workflowNumber = "";
    	if(peManager.checkWorkClassName(workClassName)){
    		try {
    			//验证流程类名
    			workflowNumber = peManager.vwCreateInstance(workClassName, "银行授信额度调剂");
    			
    			HashMap<String, Object> step1para = new HashMap<String, Object>();
    			//是否为集团人员申请
    			if(loginService.isCopUser()){
    				step1para.put("TMS_Cop_Submit", true);
    			}else{
    				step1para.put("TMS_Cop_Submit", false);
    			}
    			
    			peManager.vwLauchStep("TMS_Requester", step1para, workflowNumber);
			} catch (Exception e) {
				log.error("vwApplication方法 PE流程创建  出现异常：",e);
				throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
			}
    	}else{
    		throw new ServiceException(ExceptionMessage.FN_NO_CLASS);
    	}
		
    	return workflowNumber;
    }
    
    
    
	/**
	 * 流程实例Id得到授信调剂实例
	 * @param procInstId
	 * @return
	 */
	public ProcBankCreditAdjust findProcInstanceByProcInstId(String procInstId){
		ProcBankCreditAdjust procBankCreditAdjust = new ProcBankCreditAdjust();
		StringBuilder jpql = new StringBuilder("select distinct bca from ProcBankCreditAdjust bca join fetch bca.company join fetch bca.bank ");
		jpql.append(" left outer join fetch bca.procRptAdjustOs");
		jpql.append(" left outer join fetch bca.procRptAdjustProvs");
		jpql.append(" left outer join bca.procRptAdjustProvs s");
		jpql.append(" left outer join fetch s.procRptAdjustProvOs");
		jpql.append(" left outer join fetch s.bank");
		jpql.append(" left outer join fetch s.company");
		jpql.append(" where bca.defunctInd = 'N'");
		jpql.append(" and bca.procInstId='"+procInstId+"'");
		
		procBankCreditAdjust = entityService.findUnique(jpql.toString());
		
		List<Provider> providers = new ArrayList<Provider>();
		for(ProcRptAdjustProv bcap : procBankCreditAdjust.getProcRptAdjustProvs()){
			Provider p = new Provider();
			List<Credit> cs = creditService.findViableCredit(bcap.getCompany().getId(), bcap.getBank().getId());
			if(cs.size()>0){
				p.setProviderId(cs.get(0).getId());
			}
			p.setProviderName(bcap.getCompany().getCompanyName());
			p.setProviderBankName(bcap.getBank().getBankName());
			p.setCreditReduce(bcap.getCreditReduce());
			p.setCreditLine(bcap.getCreditTotal() + bcap.getCreditReduce());
			providers.add(p);
		}
		procBankCreditAdjust.setProviders(providers);
		
		return procBankCreditAdjust;
	}
    
    /**
     * 添加备注抬头
     * @param procBankCreditAdjust
     * @param pass
     * @param stepName
     */
    private void addMemoTitle(ProcBankCreditAdjust procBankCreditAdjust , boolean pass, String stepName){
    	//加入流程备注抬头
		String memoTitle = "";
		if(pass){
			memoTitle = ProcessXmlUtil.findStepProperty("id", "BankCreditAdjust", stepName, "passMemo");
		}else{
			memoTitle = ProcessXmlUtil.findStepProperty("id", "BankCreditAdjust", stepName, "nopassMemo");
		}
		if(memoTitle!=null){
			procBankCreditAdjust.setPeMemo(memoTitle + procBankCreditAdjust.getPeMemo());
		}
    }
    
	/**
	 * 工厂财务审批保存
	 * @param procDebtBorrow
	 * @param pass
	 */
	public void doApprove(ProcBankCreditAdjust procBankCreditAdjust , boolean pass, String stepName) throws ServiceException{
		try {
			//添加备注抬头
			addMemoTitle(procBankCreditAdjust, pass, stepName);
			this.updateRequest(procBankCreditAdjust);
			peManager.vwDisposeTask(procBankCreditAdjust.getProcInstId(), pass, procBankCreditAdjust.getPeMemo());
		} catch (Exception e) {
			log.error("doApprove方法 工厂财务审批保存 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}
    
	/**
	 * 集团审批保存
	 * @param procBankCreditAdjust
	 * @param pass
	 * @param stepName
	 * @param providers
	 * @throws ServiceException
	 */
	public void doCopApprove(ProcBankCreditAdjust procBankCreditAdjust , boolean pass, String stepName ,List<Provider> providers) throws ServiceException{
		try {
			//添加备注抬头
			addMemoTitle(procBankCreditAdjust, pass, stepName);
			this.updateRequest(procBankCreditAdjust);
			//再执行"刷新"提供方
			this.createProvider(procBankCreditAdjust, providers);
			
			//检查是否集团提交流程,而且集团资金部经理审批"通过",更新授信主数据
			if(pass){
				VWWorkObject wob = peManager.vwGetTmsWorkObject(procBankCreditAdjust.getProcInstId());
				VWDataField df = wob.getDataField("TMS_Cop_Submit");
				if(df!=null && df.getValue().equals(true) && "集团资金部门经理审批".equals(stepName)){
					this.updateMainCredit(procBankCreditAdjust,providers,stepName);
				}
			}
			
			//保存流程
			peManager.vwDisposeTask(procBankCreditAdjust.getProcInstId(), pass, procBankCreditAdjust.getPeMemo());
		} catch (Exception e) {
			log.error("doCopApprove方法 集团审批保存 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}
	
	/**
	 * 确认保存
	 * @param procBankCreditAdjust
	 * @param pass
	 * @param stepName
	 * @throws ServiceException
	 */
	public void doConfirm(ProcBankCreditAdjust procBankCreditAdjust , boolean pass, String stepName ) throws ServiceException{
		try {
			//添加备注抬头
			addMemoTitle(procBankCreditAdjust, pass, stepName);
			//先执行更新申请方操作
			this.updateRequest(procBankCreditAdjust);
			//再执行更新提供方
			this.updateProvider(procBankCreditAdjust);
			
			//如果集团资金部经理确认"通过",更新授信主数据
			if(pass){
				this.updateMainCredit(procBankCreditAdjust,stepName);
			}
			
			//保存流程
			peManager.vwDisposeTask(procBankCreditAdjust.getProcInstId(), pass, procBankCreditAdjust.getPeMemo());
			
			//给提供方发送邮件
			mailProviders(procBankCreditAdjust, pass, stepName);
			
		} catch (Exception e) {
			log.error("doConfirm方法 确认保存 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}
	
	/**
	 * 更新申请方
	 * @param procBankCreditAdjust
	 */
	public void updateRequest(ProcBankCreditAdjust procBankCreditAdjust){
		List<ProcRptAdjustO> paos = entityService.find("select pao from ProcRptAdjustO pao" +
				" left outer join pao.procBankCreditAdjust pa" +
				" where pao.defunctInd = 'N'" +
				" and pa.id="+procBankCreditAdjust.getId());
		List<ProcRptAdjustO> paosb = procBankCreditAdjust.getProcRptAdjustOs();
		for(ProcRptAdjustO pao : paos){
			entityService.delete(pao);
		}
		procBankCreditAdjust.setProcRptAdjustOs(null);
		procBankCreditAdjust = entityService.update(procBankCreditAdjust);
		for(ProcRptAdjustO pao : paosb){
			ProcRptAdjustO newPao = new ProcRptAdjustO();
			newPao.setId(null);
			newPao.setOtherLimit(pao.getOtherLimit());
			newPao.setOtherName(pao.getOtherName());
			newPao.setProcBankCreditAdjust(procBankCreditAdjust);
			entityService.create(newPao);
		}
	}
    
    /**
     * 审批保存授信额度提供方
     * @param procBankCreditAdjust
     * @param providers
     * @throws Exception
     */
    private void createProvider(ProcBankCreditAdjust procBankCreditAdjust, List<Provider> providers) throws Exception{
    	List<ProcBankCreditAdjust> bcas = entityService.find("select bca from ProcBankCreditAdjust bca" +
    			" left outer join fetch bca.procRptAdjustProvs" +
    			" left outer join bca.procRptAdjustProvs s" +
    			" left outer join fetch s.procRptAdjustProvOs" +
    			" where bca.id=" + procBankCreditAdjust.getId());
    	procBankCreditAdjust = bcas.get(0);
    	List<ProcRptAdjustProv> procRptAdjustProvs = procBankCreditAdjust.getProcRptAdjustProvs();
    	for(ProcRptAdjustProv bcap : procRptAdjustProvs){
    		for(ProcRptAdjustProvO po : bcap.getProcRptAdjustProvOs()){
    			entityService.delete(po);
    		}
    		entityService.delete(bcap);
    	}
    	
    	for(Provider p : providers){
    		Credit c = this.findCreditFetch(p.getProviderId());
    		Bank bank = entityService.find(Bank.class, c.getBank().getId());
    		Company comp = entityService.find(Company.class, c.getCompany().getId());
    		
    		//提供方设值
    		ProcRptAdjustProv bcap = new ProcRptAdjustProv();
    		bcap.setProcBankCreditAdjust(procBankCreditAdjust);
    		bcap.setBank(bank);
    		bcap.setCompany(comp);
    		//提供方减少额度
    		bcap.setCreditReduce(p.getCreditReduce());
    		//核准后的总额度
    		bcap.setCreditTotal(c.getCreditLine()-p.getCreditReduce());
    		
    		//other credit stuff
    		bcap.setLiquCred(this.reduceFilter(c.getLiquCred(),bcap.getCreditTotal()));
    		bcap.setBankAcpt(this.reduceFilter(c.getBankAcpt(),bcap.getCreditTotal()));
    		bcap.setImportCredit(this.reduceFilter(c.getImportCredit(),bcap.getCreditTotal()));
    		bcap.setImportFinance(this.reduceFilter(c.getImportFinance(),bcap.getCreditTotal()));
    		bcap.setOutportFinance(this.reduceFilter(c.getExportFinance(),bcap.getCreditTotal()));
    		bcap.setDollarFlow(this.reduceFilter(c.getDollarFlowFinance(),bcap.getCreditTotal()));
    		bcap.setDomesticCred(this.reduceFilter(c.getDomesticCred(),bcap.getCreditTotal()));
    		bcap.setBussTicket(this.reduceFilter(c.getBussTicket(),bcap.getCreditTotal()));
    		bcap.setForwTrade(this.reduceFilter(c.getForwTrade(),bcap.getCreditTotal()));
    		
    		ProcRptAdjustProv newBcap = entityService.create(bcap);
    		
    		List<ProcRptAdjustProvO> procRptAdjustProvOs = new ArrayList<ProcRptAdjustProvO>();
        	for(CreditO co : c.getCreditOs()){
        		ProcRptAdjustProvO po = new ProcRptAdjustProvO();
        		po.setOtherName(co.getOtherName());
        		po.setOtherLimit(this.reduceFilter(co.getOtherLimit(),bcap.getCreditTotal()));
        		po.setProcRptAdjustProv(newBcap);
        		po = entityService.create(po);
        		procRptAdjustProvOs.add(po);
        	}
    	}
    }
    
    
    /**
     * 确认保存授信额度提供方
     * @param procBankCreditAdjust
     * @throws Exception
     */
    private void updateProvider(ProcBankCreditAdjust procBankCreditAdjust) throws Exception{
    	List<ProcRptAdjustProv> providers = procBankCreditAdjust.getProcRptAdjustProvs();
    	for(ProcRptAdjustProv p : providers){
    		for(ProcRptAdjustProvO po : p.getProcRptAdjustProvOs()){
    			entityService.update(po);
    		}
    		entityService.update(p);
    	}
    }
    
    /**
     * 确认通过后保存"申请方"和"提供方"的 授信主数据
     * @param procBankCreditAdjust
     * @param stepName 
     */
    public void updateMainCredit(ProcBankCreditAdjust procBankCreditAdjust, String stepName) throws Exception{
    		//提供方 授信数据
	    	List<ProcRptAdjustProv> providers = procBankCreditAdjust.getProcRptAdjustProvs();
	    	for(ProcRptAdjustProv p : providers){
	    		List<Credit> cs = creditService.findViableCredit(p.getCompany().getId(), p.getBank().getId());
				if(cs.size()>0){
					Credit c = cs.get(0);
					c.setCreditLine(p.getCreditTotal());
					c.setLiquCred(p.getLiquCred());
					c.setBankAcpt(p.getBankAcpt());
					c.setImportCredit(p.getImportCredit());
					c.setImportFinance(p.getImportFinance());
					c.setExportFinance(p.getOutportFinance());
					c.setDollarFlowFinance(p.getDollarFlow());
					c.setDomesticCred(p.getDomesticCred());
					c.setBussTicket(p.getBussTicket());
					c.setForwTrade(p.getForwTrade());
					for(CreditO co : c.getCreditOs()){
						for(ProcRptAdjustProvO po : p.getProcRptAdjustProvOs()){
							if(co.getOtherName().equals(po.getOtherName())){
								co.setOtherLimit(po.getOtherLimit());
								//更新提供方的其他产品
								entityService.update(co);
							}
						}
					}
					//更新提供方
					entityService.update(c);
				}
	    	}
	    	
	    	//申请方 授信数据
	    	List<Credit> cs = creditService.findViableCredit(procBankCreditAdjust.getCompany().getId(), procBankCreditAdjust.getBank().getId());
	    	if(cs.size()>0){
				Credit c = cs.get(0);
				c.setCreditLine(procBankCreditAdjust.getCreditOri()+procBankCreditAdjust.getCreditAdd());
				c.setLiquCred(procBankCreditAdjust.getLiquCred());
				c.setBankAcpt(procBankCreditAdjust.getBankAcpt());
				c.setImportCredit(procBankCreditAdjust.getImportCredit());
				c.setImportFinance(procBankCreditAdjust.getImportFinance());
				c.setExportFinance(procBankCreditAdjust.getOutportFinance());
				c.setDollarFlowFinance(procBankCreditAdjust.getDollarFlow());
				c.setDomesticCred(procBankCreditAdjust.getDomesticCred());
				c.setBussTicket(procBankCreditAdjust.getBussTicket());
				c.setForwTrade(procBankCreditAdjust.getForwTrade());
				for(CreditO co : c.getCreditOs()){
					for(ProcRptAdjustO po : procBankCreditAdjust.getProcRptAdjustOs()){
						if(co.getOtherName().equals(po.getOtherName())){
							co.setOtherLimit(po.getOtherLimit());
							//更新申请方的其他产品
							entityService.update(co);
						}
					}
				}
				//更新申请方
				entityService.update(c);
			}
    	//9.19 huhan modify 授信调剂主数据保存方式的变动，如果想更新主数据授信Credit表就取消上面的注释
    	//先保存授信申请方
    	CreditR cr = new CreditR();
    	List<Credit> cs1 = creditService.findViableCredit(procBankCreditAdjust.getCompany().getId(), procBankCreditAdjust.getBank().getId());
    	if(cs1.size()>0){
			Credit c = cs1.get(0);
			cr.setCredit(c);
			cr.setUse(procBankCreditAdjust.getUse());
			cr.setCreditAdd(procBankCreditAdjust.getCreditAdd());
			cr.setCreditStart(c.getCreditStart());
			//保存授信调剂明细_主数据(申请方)
			cr = entityService.create(cr);
		}
    	
    	//再保存授信提供方
    	List<ProcRptAdjustProv> providers1 = procBankCreditAdjust.getProcRptAdjustProvs();
    	for(ProcRptAdjustProv p : providers1){
    		CreditP cp = new CreditP();
    		List<Credit> csp = creditService.findViableCredit(p.getCompany().getId(), p.getBank().getId());
			if(csp.size()>0){
				Credit c = csp.get(0);
				cp.setCredit(c);
				if(cr.getId()!=null){
					cp.setCreditR(cr);
				}
				cp.setCreditReduce(p.getCreditReduce());
				cp.setCreditStart(cr.getCreditStart());
				cp.setStatus("Y");
				entityService.create(cp);
			}
    	}
    }
    
    /**
     * 确认通过后保存"申请方"和"提供方"的 授信主数据(集团版)
     * @param procBankCreditAdjust
     */
    public void updateMainCredit(ProcBankCreditAdjust procBankCreditAdjust,List<Provider> ps,String stepName) throws Exception{
	    	//提供方 授信数据
	    	for(Provider p : ps){
	    		List<Credit> cs = entityService.find("select c from Credit c left outer join fetch c.creditOs where c.id=?1 and c.defunctInd = 'N' and c.status='Y'" ,p.getProviderId());
				if(cs.size()>0){
					Credit c = cs.get(0);
					c.setCreditLine(c.getCreditLine()-p.getCreditReduce());
					c.setLiquCred(this.reduceFilter(c.getLiquCred(),c.getCreditLine()));
					c.setBankAcpt(this.reduceFilter(c.getBankAcpt(),c.getCreditLine()));
					c.setImportCredit(this.reduceFilter(c.getImportCredit(),c.getCreditLine()));
					c.setImportFinance(this.reduceFilter(c.getImportFinance(),c.getCreditLine()));
					c.setExportFinance(this.reduceFilter(c.getExportFinance(),c.getCreditLine()));
					c.setDollarFlowFinance(this.reduceFilter(c.getDollarFlowFinance(),c.getCreditLine()));
					c.setDomesticCred(this.reduceFilter(c.getDomesticCred(),c.getCreditLine()));
					c.setBussTicket(this.reduceFilter(c.getBussTicket(),c.getCreditLine()));
					c.setForwTrade(this.reduceFilter(c.getForwTrade(),c.getCreditLine()));
					for(CreditO co : c.getCreditOs()){
						co.setOtherLimit(this.reduceFilter(co.getOtherLimit(),c.getCreditLine()));
						//更新提供方的其他产品
						entityService.update(co);
					}
					//更新提供方
					entityService.update(c);
				}
	    	}
	    	
	    	//申请方 授信数据
	    	List<Credit> cs1 = creditService.findViableCredit(procBankCreditAdjust.getCompany().getId(), procBankCreditAdjust.getBank().getId());
	    	if(cs1.size()>0){
				Credit c = cs1.get(0);
				c.setCreditLine(procBankCreditAdjust.getCreditOri()+procBankCreditAdjust.getCreditAdd());
				c.setLiquCred(procBankCreditAdjust.getLiquCred());
				c.setBankAcpt(procBankCreditAdjust.getBankAcpt());
				c.setImportCredit(procBankCreditAdjust.getImportCredit());
				c.setImportFinance(procBankCreditAdjust.getImportFinance());
				c.setExportFinance(procBankCreditAdjust.getOutportFinance());
				c.setDollarFlowFinance(procBankCreditAdjust.getDollarFlow());
				c.setDomesticCred(procBankCreditAdjust.getDomesticCred());
				c.setBussTicket(procBankCreditAdjust.getBussTicket());
				c.setForwTrade(procBankCreditAdjust.getForwTrade());
				for(CreditO co : c.getCreditOs()){
					for(ProcRptAdjustO po : procBankCreditAdjust.getProcRptAdjustOs()){
						if(co.getOtherName().equals(po.getOtherName())){
							co.setOtherLimit(po.getOtherLimit());
							//更新申请方的其他产品
							entityService.update(co);
						}
					}
				}
				//更新申请方
				entityService.update(c);
			}
    	//9.19 huhan modify 授信调剂主数据保存方式的变动，如果想更新主数据授信Credit表就取消上面的注释
    	//先保存授信申请方
    	CreditR cr = new CreditR();
    	List<Credit> cs = creditService.findViableCredit(procBankCreditAdjust.getCompany().getId(), procBankCreditAdjust.getBank().getId());
    	if(cs.size()>0){
			Credit c = cs.get(0);
			cr.setCredit(c);
			cr.setUse(procBankCreditAdjust.getUse());
			cr.setCreditAdd(procBankCreditAdjust.getCreditAdd());
			cr.setCreditStart(c.getCreditStart());
			//保存授信调剂明细_主数据(申请方)
			cr = entityService.create(cr);
		}
    	
    	//再保存授信提供方
    	for(Provider p : ps){
    		CreditP cp = new CreditP();
    		List<Credit> csp = entityService.find("select c from Credit c left outer join fetch c.creditOs where c.id=?1 and c.defunctInd = 'N' and c.status='Y'" ,p.getProviderId());
			if(csp.size()>0){
				Credit c = csp.get(0);
				cp.setCredit(c);
				if(cr.getId()!=null){
					cp.setCreditR(cr);
				}
				cp.setCreditReduce(p.getCreditReduce());
				cp.setCreditStart(cr.getCreditStart());
				cp.setStatus("Y");
				entityService.create(cp);
			}
    	}
    }
    
    
    /**
     * 提供方调剂算法
     * @param original
     * @param total
     * @return
     */
    private Double reduceFilter(Double original, Double restTotal){
    	if(original==null){
    		original = 0d;
    	}
    	//原量 少于 剩余总量，不变
    	if(original < restTotal){
    		return original;
    	}
    	//原量 大于等于 剩余总量，则用总量
    	else{
    		return restTotal;
    	}
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
				if(le.getEventType() == ProcessDefineUtil.PROCESS_EVENTTYPE_MAP.get(ProcessDefineUtil.EventTypeEnum.ProcessTerminal)){
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
			log.error("getProcessDetail方法 查询流程详细 出现异常：",e);
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return detailVos;
	}
    
	
	/******** 给提供方财务经理发送邮件  ***********************************************/
    private void mailProviders(ProcBankCreditAdjust procBankCreditAdjust , boolean pass, String stepName ){
    	List<Mail> mailList = new ArrayList<Mail>();
    	if(pass && ("集团资金部门经理审批".equals(stepName) || "集团资金部门经理确认".equals(stepName))){
    		//邮件业务内容
    		StringBuilder bussMailBody = new StringBuilder(procBankCreditAdjust.getCompany().getCompanyName());
    		bussMailBody.append("申请授信调剂，授信提供方为：");
    		//得到提供方的sap代码
    		List<String> providerSaps = new ArrayList<String>();
    		List<ProcRptAdjustProv> providers = procBankCreditAdjust.getProcRptAdjustProvs();
    		for(ProcRptAdjustProv p : providers){
    			bussMailBody.append(p.getCompany().getCompanyName()+" ");
    			String sap = p.getCompany().getSapCode();
    			if(sap!=null){
    				providerSaps.add(sap);
    			}
    		}
    		bussMailBody.append("，特此告知。");
    		
    		//所有工厂财务经理
    		List<P> ps = mailService.findUserByQueue("TMS_Fac_Finance");
    		for(P p :ps){
    			String pid = p.getId();
    			if(pid!=null){
    				List<String> saps = loginService.finSapByPid(pid);
    				boolean isProvider = false;
    				//比对人的公司的sap是否在提供方sap中
    				for(String sap : saps){
    					if(providerSaps.contains(sap)){
    						isProvider = true;
    						break;
    					}
    				}
    				//是工厂提供方财务经理,发送邮件
    				if(isProvider){
    					Mail m = new Mail();
    					m.setTelno(p.getCelno());
    					m.setEmail(p.getEmail());
    					m.setSubject(mailService.generationTitle(MailUtil.MailTypeEnum.Notice, "TMS_BankCreditAdjust", processUtilMapService.getTmsIdByFnId(procBankCreditAdjust.getProcInstId()), stepName, loginService.getCurrentUserName(), null));
    					m.setBody(mailService.generationContent(MailUtil.MailTypeEnum.Notice, "TMS_BankCreditAdjust", processUtilMapService.getTmsIdByFnId(procBankCreditAdjust.getProcInstId()), stepName, loginService.getCurrentUserName(), null, null) + bussMailBody.toString());
    					mailList.add(m);
    				}
    			}
    		}
    		
    		sendMailService.send(mailList);
    	}
    	
    }
    
}
