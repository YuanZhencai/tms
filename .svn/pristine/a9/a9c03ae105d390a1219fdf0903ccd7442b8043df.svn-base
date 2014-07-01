package com.wcs.tms.service.process.purchaseFundProd;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.MathUtil;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.common.model.P;
import com.wcs.tms.mail.Mail;
import com.wcs.tms.mail.MailService;
import com.wcs.tms.mail.MailUtil;
import com.wcs.tms.mail.SendMailService;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.message.ExceptionMessage;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.CompanyAccount;
import com.wcs.tms.model.ProcPurchaseFundProd;
import com.wcs.tms.model.ProcPurchaseFundProdDetail;
import com.wcs.tms.model.ProcPurchaseFundProdPay;
import com.wcs.tms.model.ProcPurchaseFundProdPayDetail;
import com.wcs.tms.model.ProcPurchaseFundProdProduct;
import com.wcs.tms.model.ProcPurchaseFundTrade;
import com.wcs.tms.model.ProcPurchaseFundTradePay;
import com.wcs.tms.model.ProcPurchaseFundTradePayDetail;
import com.wcs.tms.model.ProcPurchaseFundTradeProduct;
import com.wcs.tms.model.ProcTMSStatus;
import com.wcs.tms.model.ProdOrTradeCashMain;
import com.wcs.tms.model.PurchaseFund;
import com.wcs.tms.model.PurchaseFundDetail;
import com.wcs.tms.service.process.common.FtpUploadService;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.TmsStatusService;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.service.system.company.CompanyAccountServer;
import com.wcs.tms.util.ProcessDefineUtil;
import com.wcs.tms.view.process.common.entity.PaymentVo;
import com.wcs.tms.view.process.common.entity.ProcPurchaseFundVo;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;

import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWWorkObject;
import filenet.vw.api.VWWorkObjectNumber;

/** 
* <p>Project: tms</p> 
* <p>Title: 采购资金（生产）借款/转款流程service</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
@Stateless
public class PurchaseFundProdService implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(PurchaseFundProdService.class);
	@Inject
	PEManager peManager;
	@Inject
	LoginService loginService;
	@Inject
	EntityService entityService;
	@Inject
	MailService mailService;
	@Inject
	SendMailService sendMailService;
	@Inject
	CompanyAccountServer companyAccountServer;
	@Inject
	ProdOrTradeCashMain prodOrTradeCashMain;
	@Inject
	BankService bankService;
	// 9.11
	@Inject
	ProcessUtilMapService processUtilMapService;
	@Inject
	FtpUploadService ftpUploadService;
	@EJB
	private TmsStatusService tmsStatusService;
	List<CompanyAccount> ca = new ArrayList<CompanyAccount>();

	/**
	 * 得到与公司有业务关系的银行
	 * @param companyId
	 * @return
	 */
	public List<SelectItem> getCompanyBankSelect(Long companyId) {
		List<SelectItem> bankSelect = new ArrayList<SelectItem>();
		List<CompanyAccount> ca = this.getComanyBank(companyId);
		boolean iftrue = true;
		if (iftrue) {
			log.info("去重复银行");
			for (CompanyAccount c : ca) {
				// 去重复银行
				Boolean has = false;
				for (SelectItem si : bankSelect) {
					if (c.getAccountDesc().equals(si.getValue())) {
						has = true;
					}
				}
				if (!has) {
					bankSelect.add(new SelectItem(c.getAccountDesc(), c.getAccountDesc()));
				}
				log.info("cao" + c.getAccountDesc());
			}
		} else {
			log.info("不去重复银行");
			for (CompanyAccount c : ca) {
				// 不去重复银行
				bankSelect.add(new SelectItem(c.getCounterpartyCode(), c.getAccountDesc()));
			}
		}
		log.info("bankSelect.size():" + bankSelect.size());
		return bankSelect;
	}

	/**
	 * 得到公司银行关系列表
	 * @param companyId
	 * @return
	 */
	public List<CompanyAccount> getComanyBank(Long companyId) {
		ca = companyAccountServer.findCompanyAccountList(companyId);
		return ca;
	}

	/**
	 * 得到公司银行里面的帐号
	 * @param companyId
	 * @param accountDesc
	 * @return
	 */
	public List<SelectItem> getCompanyAccountSelect(Long companyId, String accountDesc) {
		List<SelectItem> accountSelect = new ArrayList<SelectItem>();
		accountSelect = companyAccountServer.findBankSelect(companyId, accountDesc);
		return accountSelect;
	}

	public String getCodeKey(String code) {
		StringBuilder jpql = new StringBuilder(
				"SELECT d.codeCat FROM Dict d where d.defunctInd = 'N' and d.codeCat like 'TMS.FARM.PRODUCE.VARIETY.TYPE%'");
		jpql.append(" and d.codeKey = '" + code + "'");
		log.info("jpql:" + jpql);
		return entityService.findUnique(jpql.toString());
	}

	/**
	 * 从流程表单中获得品种
	 * @param procInstId
	 * @param procPurchaseFundProd
	 * @param purchaseFund
	 * @return
	 * @throws ServiceException
	 */
	public List<PurchaseFund> getVarietyModel(String procInstId, ProcPurchaseFundProd procPurchaseFundProd, PurchaseFund purchaseFund)
			throws ServiceException {
		List<PurchaseFund> VariteyList = new ArrayList<PurchaseFund>();
		try {
			StringBuilder jpql = new StringBuilder("select pFt from ProcPurchaseFundProd pFt join fetch pFt.company where pFt.defunctInd = 'N'");
			jpql.append(" and pFt.procInstId='" + procInstId + "'");
			ProcPurchaseFundProd entity = entityService.findUnique(jpql.toString());
			Company company = this.entityService.find(Company.class, procPurchaseFundProd.getCompany().getId());
			purchaseFund.setCompany(company);
			
			StringBuilder jpql2 = new StringBuilder("SELECT p FROM ProcPurchaseFundProdProduct p WHERE p.varietyDefunct = 'N'");
			jpql2.append(" AND p.procPurchaseFundProd.id=" + entity.getId());
			
			List<ProcPurchaseFundProdProduct> products = this.entityService.find(jpql2.toString());
			if(products != null && products.size() > 0) {
				for (ProcPurchaseFundProdProduct procPurchaseFundProdProduct : products) {
					PurchaseFund pf = new PurchaseFund();
					pf.setVariety(procPurchaseFundProdProduct.getVariety());// 品种
					pf.setVarietyRelated(procPurchaseFundProdProduct.getVarietyRelated());// 是否为关联方
					pf.setVarietyRemain(procPurchaseFundProdProduct.getVarietyRemain());// 剩余头寸数量
					pf.setVarietyAmount(procPurchaseFundProdProduct.getVarietyAmount());// 申请金额
					pf.setVarietyNum(procPurchaseFundProdProduct.getVarietyNum());// 申请头寸
					pf.setVarietyRemark(procPurchaseFundProdProduct.getVarietyRemark());// 备注
					VariteyList.add(pf);
				}
			}
			
		} catch (Exception e) {
			log.error("getVarietyModel方法 错误信息：" + e.getMessage());
		}
		return VariteyList;
	}

	/**
	 * 从流程表单中获得品种VO信息
	 * @param procInstId
	 * @param procPurchaseFundProd
	 * @param purchaseFund
	 * @return
	 * @throws ServiceException
	 */
	public List<ProcPurchaseFundVo> getVarietyModelView(String procInstId, ProcPurchaseFundProd procPurchaseFundProd, PurchaseFund purchaseFund)
			throws ServiceException {
		log.info("从流程表单中获得品种VO信息");
		List<ProcPurchaseFundVo> VariteyViewList = new ArrayList<ProcPurchaseFundVo>();
		try {
			StringBuilder jpql = new StringBuilder("select pFt from ProcPurchaseFundProd pFt join fetch pFt.company where pFt.defunctInd = 'N'");
			jpql.append(" and pFt.procInstId='" + procInstId + "'");
			log.info("jpql:" + jpql);
			ProcPurchaseFundProd entity = entityService.findUnique(jpql.toString());

			StringBuilder jpql2 = new StringBuilder(
					"select pFTD from ProcPurchaseFundProdDetail pFTD left  join fetch pFTD.procPurchaseFundProd where pFTD.defunctInd = 'N'");
			jpql2.append(" and pFTD.procPurchaseFundProd.id=" + entity.getId() + "");
			log.info("jpql2:" + jpql2);
			List<ProcPurchaseFundProdDetail> amo = entityService.find(jpql2.toString());
			log.info("amo.size():" + amo.size());
			
			StringBuilder jpql3 = new StringBuilder("SELECT p FROM ProcPurchaseFundProdProduct p WHERE p.varietyDefunct = 'N'");
			jpql3.append(" AND p.procPurchaseFundProd.id=" + entity.getId());
			List<ProcPurchaseFundProdProduct> products = entityService.find(jpql3.toString());
			
			if(products != null && products.size() > 0) {
				for (ProcPurchaseFundProdProduct procPurchaseFundProdProduct : products) {
					ProcPurchaseFundVo pf = new ProcPurchaseFundVo();
					pf.setVarietyId(procPurchaseFundProdProduct.getVariety());// 品种
					pf.setVarietyRelated(procPurchaseFundProdProduct.getVarietyRelated());// 是否为关联方
					pf.setLessPurchaseNum(procPurchaseFundProdProduct.getVarietyRemain());// 剩余头寸数量
					pf.setThisPurchaseNum(procPurchaseFundProdProduct.getVarietyNum());// 本次申请数量（获得）
					pf.setThisPurchaseNumPay(this.getNeedPurchaseNum(procPurchaseFundProdProduct));// 已审批数量
					pf.setThisPurchaseAmount(procPurchaseFundProdProduct.getVarietyAmount());// 本次申请金额（获得）
					pf.setThisPurchaseAmountPay(this.getNeedPurchaseAmount(procPurchaseFundProdProduct));// 本次支付金额（可改）
					pf.setRemark(procPurchaseFundProdProduct.getVarietyRemark());// 品种备注
					pf.setProductId(procPurchaseFundProdProduct.getId());
					VariteyViewList.add(pf);
				}
			}
			
		} catch (Exception e) {
		    log.error("getVarietyModelView方法 错误信息：" + e.getMessage());
		}
		return VariteyViewList;
	}

	/**
	 * 获得剩余头寸数量=ZZZ-111-222
	 * @param procPurchaseFundProd
	 * @param purchaseFund
	 * @return
	 */
	public Double getVarietyRemain(ProcPurchaseFundProd procPurchaseFundProd, PurchaseFund purchaseFund) {
		Double remain = 0.0;
		Double allCash = 0.0;
		try {
			List<ProdOrTradeCashMain> ptc = getRemainAll(procPurchaseFundProd, purchaseFund);
			for (ProdOrTradeCashMain am : ptc) {
				allCash += am.getTotalCash();
			}
			if (allCash > 0.0) {
				remain = allCash - getRemainUsed(ptc.get(0), purchaseFund, procPurchaseFundProd)
						- getActive(ptc.get(0), purchaseFund, procPurchaseFundProd);
			}
		} catch (Exception e) {
		    log.error("getVarietyRemain方法 错误信息：" + e.getMessage());
		}
		log.info("剩余头寸数量:" + remain);
		return remain;
	}

	/**
	 * 获得生产或贸易总头寸主数据表中的总头寸ZZZ
	 * @param procPurchaseFundProd
	 * @param purchaseFund
	 * @return
	 * @throws Exception 
	 */
	public List<ProdOrTradeCashMain> getRemainAll(ProcPurchaseFundProd procPurchaseFundProd, PurchaseFund purchaseFund) throws Exception {
		log.info("获得生产或贸易总头寸主数据表中的总头寸ZZZ");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String now = sdf.format(new Date());
		StringBuilder jpql = new StringBuilder("select p from ProdOrTradeCashMain p join fetch p.company where p.defunctInd = 'N'");
		jpql.append(" and p.company.id=" + procPurchaseFundProd.getCompany().getId());
		jpql.append(" and p.appType='P'");
		jpql.append(" and p.variety='" + purchaseFund.getVariety() + "'");
		jpql.append(" and p.startDate <='" + now + "'");
		jpql.append(" and p.endDate >='" + now + "'");
		log.info("jpql:" + jpql);
		return entityService.find(jpql.toString());
	}

	/**
	 * 获得采购资金（生产或贸易）主数据中已用的头寸111
	 * @param purchaseFund
	 * @param procPurchaseFundProd
	 * @return
	 * @throws Exception
	 */
	public Double getRemainUsed(ProdOrTradeCashMain prodOrTradeCashMain, PurchaseFund purchaseFund, ProcPurchaseFundProd procPurchaseFundProd)
			throws Exception {
		log.info("获得采购资金（生产或贸易）主数据中已用的头寸111");
		Double remain = 0.0;
		log.info("品种ID:" + purchaseFund.getVariety());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<String> procIds = this.getProcIds(procPurchaseFundProd);
		StringBuilder jpql0 = new StringBuilder(
				"select ppFT.id from ProcPurchaseFundProd ppFT join fetch ppFT.company where ppFT.defunctInd = 'N' and ppFT.procInstId not in (?1)");
		jpql0.append(" and ppFT.company.id = " + procPurchaseFundProd.getCompany().getId());
		// 已用头寸要满足是符合头寸有效期范围的
		jpql0.append(" and ppFT.createdDatetime >='" + sdf.format(prodOrTradeCashMain.getStartDate()) + "'");
		jpql0.append(" and ppFT.createdDatetime <='" + sdf.format(DateUtil.adjustYearAndMonthAndDay(prodOrTradeCashMain.getEndDate(), 0, 0, 1)) + "'");
		// *******************************
		log.info("jpql0:" + jpql0);
		List<Long> ppfIdTList = entityService.find(jpql0.toString(), procIds);
		StringBuilder jpql1 = new StringBuilder(
				"select ppFTD from ProcPurchaseFundProdDetail ppFTD join fetch ppFTD.procPurchaseFundProd where ppFTD.defunctInd = 'N' and ppFTD.procPurchaseFundProd.id in (?1)");
		StringBuilder jpdl2 = new StringBuilder("");
		jpdl2.append("select p from ProcPurchaseFundProdPayDetail p join fetch p.procPurchaseFundProdPay join fetch p.procPurchaseFundProdProduct join fetch p.procPurchaseFundProdPay.procPurchaseFundProd ");
		jpdl2.append("where p.defunctInd = 'N' and p.procPurchaseFundProdPay.procPurchaseFundProd.id in (?1) and p.procPurchaseFundProdProduct.variety = '").append(purchaseFund.getVariety()).append("'");
		List<ProcPurchaseFundProdPayDetail> ppFTDTList = entityService.find(jpdl2.toString(), ppfIdTList);
		for (ProcPurchaseFundProdPayDetail ppFTDT : ppFTDTList) {
			remain += ppFTDT.getVarietyNum();
		}
		log.info("获得采购资金（生产或贸易）主数据中已用的头寸111:" + remain);
		return remain;
	}

	/**
	 * 获得正在进行中的流程占用的头寸数量222
	 * @param purchaseFund
	 * @param procPurchaseFundProd
	 * @return
	 * @throws Exception
	 */
	public Double getActive(ProdOrTradeCashMain prodOrTradeCashMain, PurchaseFund purchaseFund, ProcPurchaseFundProd procPurchaseFundProd)
			throws Exception {
		log.info("获得正在进行中的流程占用的头寸数量222");
		// 获得正在审批过程中的流程实例ID集合
		List<String> procIds = this.getProcIds(procPurchaseFundProd);
		Double remain = 0.0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (procIds.size() != 0) {
			List<Long> ppftIdTList = new ArrayList<Long>();
			for (String s : procIds) {
				StringBuilder jpql = new StringBuilder("select pFt from ProcPurchaseFundProd pFt join fetch pFt.company where pFt.defunctInd = 'N'");
				jpql.append(" and pFt.procInstId='" + s + "'");
				jpql.append(" and pFt.company.id = " + procPurchaseFundProd.getCompany().getId());
				// 转款流程是不占用头寸数量的
				jpql.append(" and pFt.createdDatetime >='" + sdf.format(prodOrTradeCashMain.getStartDate()) + "'");
				jpql.append(" and pFt.createdDatetime <='" + sdf.format(DateUtil.adjustYearAndMonthAndDay(prodOrTradeCashMain.getEndDate(), 0, 0, 1))
						+ "'");
				log.info("jpql:" + jpql);
				List<ProcPurchaseFundProd> pft = entityService.find(jpql.toString());
				if (!pft.isEmpty()) {
					ppftIdTList.add(pft.get(0).getId());
				}
			}
			StringBuilder jpdl1 = new StringBuilder("select p from ProcPurchaseFundProdProduct p join fetch p.procPurchaseFundProd where p.procPurchaseFundProd.id in (?1) and p.defunctInd = 'N' ");
			jpdl1.append("and p.variety ='").append(purchaseFund.getVariety()).append("'");
			List<ProcPurchaseFundProdProduct> pft = entityService.find(jpdl1.toString(), ppftIdTList);
			for (ProcPurchaseFundProdProduct pf : pft) {
				log.info("品种:" + purchaseFund.getVariety());
				remain += pf.getVarietyNum();
			}
		}
		log.info("正在进行中的流程占用的头寸数量：" + remain);
		return remain;
	}

	/**
	 * 获得正在审批过程中的流程的流程实例ID
	 * @param procPurchaseFundProd
	 * @return
	 * @throws Exception
	 */
	public List<String> getProcIds(ProcPurchaseFundProd procPurchaseFundProd) throws Exception {
		// 查询条件组装
		Map<String, Object> filterMap = this.getActiveFilterMap();
		String filter = (String) filterMap.get("filter");
		Object[] substitutionVars = (Object[]) filterMap.get("substitutionVars");
		List<VWWorkObject> VWObjects = peManager.vwGetTmsWorkObjects(filter, substitutionVars);

		List<String> procIds = new ArrayList<String>();
		for (VWWorkObject o : VWObjects) {
			// 在重新申请时，排除此流程本身占用的资源
			if (procPurchaseFundProd.getProcInstId() != null) {
				log.info("本流程的ID：" + procPurchaseFundProd.getProcInstId());
				if (!procPurchaseFundProd.getProcInstId().equals(o.getWorkflowNumber())) {
					procIds.add(o.getWorkflowNumber());
				}
			} else {
				procIds.add(o.getWorkflowNumber());
			}
		}
		log.info("活动流程中采购资金（生产）流程的数量：" + procIds.size());
		return procIds;
	}

	/**
	 * 得到活动的采购资金（生产）流程的过滤器
	 * @return
	 */
	private Map<String, Object> getActiveFilterMap() {
		Map<String, Object> filterMap = new HashMap<String, Object>();
		StringBuilder filter = new StringBuilder("1=1");
		List<Object> varsList = new ArrayList<Object>();

		// 1.流程类型为采购资金（生产）流程
		filter.append(" and F_WorkClassId = :workClassId");
		varsList.add(peManager.vwGetClassIdByClassName(ProcessXmlUtil.getProcessAttribute("id", "PurchaseFundProd", "className")));

		Object[] substitutionVars = varsList.toArray();
		filterMap.put("filter", filter.toString());
		filterMap.put("substitutionVars", substitutionVars);

		return filterMap;
	}

	/**
	 * 流程创建保存
	 * @param procPurchaseFundProd
	 * @return
	 * @throws ServiceException
	 */
	public String createProcInstance(ProcPurchaseFundProd procPurchaseFundProd) throws ServiceException {
		String procInstId = "";
		try {
			// PE流程创建
			// 流程实例ID，并保存
			procInstId = this.vwApplication(procPurchaseFundProd);
			procPurchaseFundProd.setProcInstId(procInstId);
			procPurchaseFundProd.setPurchaseType("P");
			procPurchaseFundProd.setCreatedBy(loginService.getCurrentUserName());
			// 保存到数据库中
			entityService.create(procPurchaseFundProd);
			// 生成流程实例编号映射9.11
			processUtilMapService.generateProcessMap(procInstId, "BPM_RA_012", procPurchaseFundProd.getCompany());
		} catch (Exception e) {
			log.error("createProcInstance方法 流程创建保存异常" + e.getMessage());
			throw new ServiceException(ExceptionMessage.INSTANCE_CREATE, e);
		}
		return procInstId;
	}

	/**
	 * PE流程创建
	 * @param procPurchaseFundProd
	 * @return
	 * @throws ServiceException
	 */
	public String vwApplication(ProcPurchaseFundProd procPurchaseFundProd) throws ServiceException {
		String workClassName = ProcessXmlUtil.getProcessAttribute("id", "PurchaseFundProd", "className");
		String workflowNumber = "";
		if (peManager.checkWorkClassName(workClassName)) {
			try {
				// 验证流程类名
				workflowNumber = peManager.vwCreateInstance(workClassName, "采购资金（生产）");
				HashMap<String, Object> step1para = new HashMap<String, Object>();
				peManager.vwLauchStep("TMS_Requester", step1para, workflowNumber);
			} catch (Exception e) {
				log.error("vwApplication方法 PE流程创建出现异常" + e.getMessage());
				throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
			}
		} else {
			throw new ServiceException(ExceptionMessage.FN_NO_CLASS);
		}
		return workflowNumber;
	}

	/**
	 * 添加品种
	 * @param purchaseFundList
	 * @param procPurchaseFundProd
	 * @return
	 * @throws ServiceException
	 */
	public ProcPurchaseFundProd batchAddCreditRpt(List<PurchaseFund> purchaseFundList, ProcPurchaseFundProd procPurchaseFundProd)
			throws ServiceException {
		log.info("service中的添加品种");
		try {
			if (!purchaseFundList.isEmpty()) {
				log.info("执行添加品种方法！！");
				if (purchaseFundList.size() == 1) {
					log.info("品种数量为1");
					PurchaseFund pf0 = purchaseFundList.get(0);
					procPurchaseFundProd.setVarietyOne(pf0.getVariety());
					procPurchaseFundProd.setVarietyOneRelated(pf0.getVarietyRelated());
					procPurchaseFundProd.setVarietyOneRemain(pf0.getVarietyRemain());
					procPurchaseFundProd.setVarietyOneNum(pf0.getVarietyNum());
					procPurchaseFundProd.setVarietyOneAmount(pf0.getVarietyAmount());
					procPurchaseFundProd.setVarietyOneRemark(pf0.getVarietyRemark());
					// 设置品种状态，默认'N'
					procPurchaseFundProd.setVarietyOneDefunct("N");
					// 为重新申请时方便品种操作，故设为空
					procPurchaseFundProd.setVarietyTwo(null);
					procPurchaseFundProd.setVarietyTwoRelated(null);
					procPurchaseFundProd.setVarietyTwoAmount(null);
					procPurchaseFundProd.setVarietyTwoNum(null);
					procPurchaseFundProd.setVarietyTwoRemain(null);
					procPurchaseFundProd.setVarietyTwoRemark(null);
					procPurchaseFundProd.setVarietyTwoDefunct(null);
					procPurchaseFundProd.setVarietyThr(null);
					procPurchaseFundProd.setVarietyThrRelated(null);
					procPurchaseFundProd.setVarietyThrAmount(null);
					procPurchaseFundProd.setVarietyThrNum(null);
					procPurchaseFundProd.setVarietyThrRemain(null);
					procPurchaseFundProd.setVarietyThrRemark(null);
					procPurchaseFundProd.setVarietyThrDefunct(null);
				}
				if (purchaseFundList.size() == 2) {
					log.info("品种数量为2");
					PurchaseFund pf0 = purchaseFundList.get(0);
					PurchaseFund pf1 = purchaseFundList.get(1);
					procPurchaseFundProd.setVarietyOne(pf0.getVariety());
					procPurchaseFundProd.setVarietyOneRelated(pf0.getVarietyRelated());
					procPurchaseFundProd.setVarietyOneAmount(pf0.getVarietyAmount());
					procPurchaseFundProd.setVarietyOneNum(pf0.getVarietyNum());
					procPurchaseFundProd.setVarietyOneRemain(pf0.getVarietyRemain());
					procPurchaseFundProd.setVarietyOneRemark(pf0.getVarietyRemark());
					// 设置品种状态，默认'N'
					procPurchaseFundProd.setVarietyOneDefunct("N");
					procPurchaseFundProd.setVarietyTwo(pf1.getVariety());
					procPurchaseFundProd.setVarietyTwoRelated(pf1.getVarietyRelated());
					procPurchaseFundProd.setVarietyTwoAmount(pf1.getVarietyAmount());
					procPurchaseFundProd.setVarietyTwoNum(pf1.getVarietyNum());
					procPurchaseFundProd.setVarietyTwoRemain(pf1.getVarietyRemain());
					procPurchaseFundProd.setVarietyTwoRemark(pf1.getVarietyRemark());
					// 设置品种状态，默认'N'
					procPurchaseFundProd.setVarietyTwoDefunct("N");
					procPurchaseFundProd.setVarietyThr(null);
					procPurchaseFundProd.setVarietyThrRelated(null);
					procPurchaseFundProd.setVarietyThrAmount(null);
					procPurchaseFundProd.setVarietyThrNum(null);
					procPurchaseFundProd.setVarietyThrRemain(null);
					procPurchaseFundProd.setVarietyThrRemark(null);
					procPurchaseFundProd.setVarietyThrDefunct(null);
				}
				if (purchaseFundList.size() >= 3) {
					log.info("品种数量为3");
					PurchaseFund pf0 = purchaseFundList.get(0);
					PurchaseFund pf1 = purchaseFundList.get(1);
					PurchaseFund pf2 = purchaseFundList.get(2);
					procPurchaseFundProd.setVarietyOne(pf0.getVariety());
					procPurchaseFundProd.setVarietyOneRelated(pf0.getVarietyRelated());
					procPurchaseFundProd.setVarietyOneAmount(pf0.getVarietyAmount());
					procPurchaseFundProd.setVarietyOneNum(pf0.getVarietyNum());
					procPurchaseFundProd.setVarietyOneRemain(pf0.getVarietyRemain());
					procPurchaseFundProd.setVarietyOneRemark(pf0.getVarietyRemark());
					// 设置品种状态，默认'N'
					procPurchaseFundProd.setVarietyOneDefunct("N");
					procPurchaseFundProd.setVarietyTwo(pf1.getVariety());
					procPurchaseFundProd.setVarietyTwoRelated(pf1.getVarietyRelated());
					procPurchaseFundProd.setVarietyTwoAmount(pf1.getVarietyAmount());
					procPurchaseFundProd.setVarietyTwoNum(pf1.getVarietyNum());
					procPurchaseFundProd.setVarietyTwoRemain(pf1.getVarietyRemain());
					procPurchaseFundProd.setVarietyTwoRemark(pf1.getVarietyRemark());
					// 设置品种状态，默认'N'
					procPurchaseFundProd.setVarietyTwoDefunct("N");
					procPurchaseFundProd.setVarietyThr(pf2.getVariety());
					procPurchaseFundProd.setVarietyThrRelated(pf2.getVarietyRelated());
					procPurchaseFundProd.setVarietyThrAmount(pf2.getVarietyAmount());
					procPurchaseFundProd.setVarietyThrNum(pf2.getVarietyNum());
					procPurchaseFundProd.setVarietyThrRemain(pf2.getVarietyRemain());
					procPurchaseFundProd.setVarietyThrRemark(pf2.getVarietyRemark());
					// 设置品种状态，默认'N'
					procPurchaseFundProd.setVarietyThrDefunct("N");
				}
			}
		} catch (Exception e) {
			log.error("batchAddCreditRpt方法 错误信息：" + e.getMessage());
			throw new ServiceException(e);
		}
		return procPurchaseFundProd;
	}

	/**
	 * 流程实例Id得到其他特殊借款实例
	 * @param procInstId
	 * @return
	 */
	public ProcPurchaseFundProd findProcInstanceByProcInstId(String procInstId) {
		StringBuilder jpql = new StringBuilder("select pFt from ProcPurchaseFundProd pFt join fetch pFt.company where pFt.defunctInd = 'N'");
		jpql.append(" and pFt.procInstId='" + procInstId + "'");
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
		    log.error("getProcessDetail方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return detailVos;
	}

	/**
	 * 查询付款过程详细
	 * @param procInstId
	 * @return
	 * @throws ServiceException
	 */
	public List<PaymentVo> getPayDetail(String procInstId) throws ServiceException {
		List<PaymentVo> payDetailVos = new ArrayList<PaymentVo>();
		try {
			// 查询流程实例编号对应的流程ID
			StringBuilder jpql = new StringBuilder("select pFT.id from ProcPurchaseFundProd pFT join fetch pFT.company where pFT.defunctInd = 'N'");
			jpql.append(" and pFT.procInstId='" + procInstId + "'");
			log.info(jpql);
			// 根据明细表中的字段
			StringBuilder jpql2 = new StringBuilder(
					"select pFTD from ProcPurchaseFundProdDetail pFTD left join fetch pFTD.procPurchaseFundProd where pFTD.defunctInd = 'N'");
			jpql2.append(" and pFTD.procPurchaseFundProd.id=" + entityService.findUnique(jpql.toString()) + "");
			log.info(jpql2);
			List<ProcPurchaseFundProdDetail> amo = entityService.find(jpql2.toString());
			int i = 1;
			for (ProcPurchaseFundProdDetail d : amo) {
				PaymentVo payDetailVo = new PaymentVo();
				payDetailVo.setSerialNumber(i);
				payDetailVo.setPayFundsTotal(d.getPayFundsTotal());
				payDetailVo.setPayDatetime(d.getCreatedDatetime());
				payDetailVo.setPayWay(d.getPayWay());
				payDetailVo.setPayer(d.getCreatedBy());
				i++;
				payDetailVos.add(payDetailVo);
			}
		} catch (Exception e) {
		    log.error("getPayDetail方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
		return payDetailVos;
	}

	/**
	 * 审批保存
	 * @param purchaseFundList
	 * @param procPurchaseFundProd
	 * @param pass
	 * @param stepName
	 * @throws ServiceException
	 */
	public void doApprove(List<PurchaseFund> purchaseFundList, ProcPurchaseFundProd procPurchaseFundProd, Boolean pass, String stepName)
			throws ServiceException {
		try {
			// 加入流程备注抬头
			String memoTitle = "";
			if (pass) {
				memoTitle = ProcessXmlUtil.findStepProperty("id", "PurchaseFundProd", stepName, "passMemo");
			} else {
				memoTitle = ProcessXmlUtil.findStepProperty("id", "PurchaseFundProd", stepName, "nopassMemo");
			}
			if (memoTitle != null) {
				procPurchaseFundProd.setPeMemo(memoTitle + procPurchaseFundProd.getPeMemo());
			}
			// 先执行更新操作
			if ("申请".equals(stepName)) {
				this.batchAddCreditRpt(purchaseFundList, procPurchaseFundProd);
				entityService.update(procPurchaseFundProd);
				saveProdProduct(procPurchaseFundProd, purchaseFundList);
			} else {
				entityService.update(procPurchaseFundProd);
			}
			log.info("是否大于1000万:" + procPurchaseFundProd.getAmount());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("UI_More_Ten_Million", procPurchaseFundProd.getAmount());
			map.put("_Pass", pass);
			map.put("_Loan", this.ifLoan(procPurchaseFundProd));
			peManager.vwDisposeTask(procPurchaseFundProd.getProcInstId(), map, procPurchaseFundProd.getPeMemo());
		} catch (Exception e) {
			log.error("doApprove方法 审批保存出现异常" +e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}

	/**
	 * 验证是否是借款
	 * @param procPurchaseFundProd
	 * @return
	 */
	public Boolean ifLoan(ProcPurchaseFundProd procPurchaseFundProd) {
		Boolean isLoan = false;
		String loan = procPurchaseFundProd.getLoanIden();
		if (loan != null && ("L".equals(loan) || "A".equals(loan))) {
			// 是借款或者借款+转款
			isLoan = true;
			return isLoan;
		} else {
			// 不是借款
			return isLoan;
		}
	}

	/**
	 * 审批时获得品种详细信息的VO
	 * @param procInstId
	 * @param procPurchaseFundProd
	 * @return
	 * @throws ServiceException
	 */
	public List<ProcPurchaseFundVo> getVarietyVos(String procInstId, ProcPurchaseFundProd procPurchaseFundProd) throws ServiceException {
		log.info("审批时获得品种详细信息的VO");
		List<ProcPurchaseFundVo> variteyVoList = new ArrayList<ProcPurchaseFundVo>();
		try {
			StringBuilder jpql = new StringBuilder("select pFT from ProcPurchaseFundProd pFT join fetch pFT.company where pFT.defunctInd = 'N'");
			jpql.append(" and pFT.procInstId='" + procInstId + "'");
			log.info("jpql:" + jpql);
			ProcPurchaseFundProd entity = entityService.findUnique(jpql.toString());
			log.info("entity:" + entity);

			StringBuilder jpql2 = new StringBuilder(
					"select pFTD from ProcPurchaseFundProdDetail pFTD left  join fetch pFTD.procPurchaseFundProd where pFTD.defunctInd = 'N'");
			jpql2.append(" and pFTD.procPurchaseFundProd.id=" + entity.getId() + "");
			log.info("jpql2:" + jpql2);
			List<ProcPurchaseFundProdDetail> amo = entityService.find(jpql2.toString());
			log.info("amo.size():" + amo.size());
			
			StringBuilder jpql3 = new StringBuilder("SELECT p FROM ProcPurchaseFundProdProduct p WHERE p.varietyDefunct = 'N'");
			jpql3.append(" AND p.procPurchaseFundProd.id=" + entity.getId());
			List<ProcPurchaseFundProdProduct> products = entityService.find(jpql3.toString());
			
			if(products != null && products.size() > 0) {
				for (ProcPurchaseFundProdProduct procPurchaseFundProdProduct : products) {
					ProcPurchaseFundVo pf = new ProcPurchaseFundVo();
					pf.setVarietyId(procPurchaseFundProdProduct.getVariety());// 品种
					pf.setVarietyRelated(procPurchaseFundProdProduct.getVarietyRelated());// 是否为关联方
					pf.setLessPurchaseNum(procPurchaseFundProdProduct.getVarietyRemain());// 剩余头寸数量
					pf.setThisPurchaseNum(procPurchaseFundProdProduct.getVarietyNum());// 本次申请数量（获得）
					pf.setNeedPurchaseNum(procPurchaseFundProdProduct.getVarietyNum() - this.getNeedPurchaseNum(procPurchaseFundProdProduct));// 剩余需要审批数量（自动计算）
					pf.setThisPurchaseNumPay(procPurchaseFundProdProduct.getVarietyNum() - this.getNeedPurchaseNum(procPurchaseFundProdProduct));// 本次审批数量（可改）
					pf.setThisPurchaseAmount(procPurchaseFundProdProduct.getVarietyAmount());// 本次申请金额（获得）
					pf.setLessPurchaseAmountPay(procPurchaseFundProdProduct.getVarietyAmount() - this.getNeedPurchaseAmount(procPurchaseFundProdProduct));// 剩余需要支付的申请金额（计算）
					pf.setThisPurchaseAmountPay(procPurchaseFundProdProduct.getVarietyAmount() - this.getNeedPurchaseAmount(procPurchaseFundProdProduct));// 本次支付金额（可改）
					pf.setRemark(procPurchaseFundProdProduct.getVarietyRemark());// 品种备注
					pf.setProductId(procPurchaseFundProdProduct.getId());
					variteyVoList.add(pf);
				}
			}
		} catch (Exception e) {
			log.error(" getVarietyVos方法 错误信息：" + e.getMessage());
		}
		log.info("variteyVoList.size():" + variteyVoList.size());
		return variteyVoList;
	}
	
	/**
	 * 获得品种已审批头寸
	 * @param amo
	 * @return
	 */
	public Double getNeedPurchaseNum(ProcPurchaseFundProdProduct product) {
		StringBuilder jpql = new StringBuilder("SELECT p FROM ProcPurchaseFundProdPayDetail p WHERE p.defunctInd = 'N'");
		jpql.append(" AND p.procPurchaseFundProdProduct.id=" + product.getId());
		List<ProcPurchaseFundProdPayDetail> details = entityService.find(jpql.toString());
		Double num = 0.0D;
		if(details != null && details.size() > 0) {
			for (ProcPurchaseFundProdPayDetail procPurchaseFundProdPayDetail : details) {
				if(procPurchaseFundProdPayDetail.getVarietyNum() != null) {
					num += procPurchaseFundProdPayDetail.getVarietyNum();
				}
			}
		}
		log.info("获得品种剩余需要审批头寸:" + num);
		return num;
	}
	
	/**
	 * 获得品种已审批金额
	 * @param amo
	 * @return
	 */
	public Double getNeedPurchaseAmount(ProcPurchaseFundProdProduct product) {
		StringBuilder jpql = new StringBuilder("SELECT p FROM ProcPurchaseFundProdPayDetail p WHERE p.defunctInd = 'N'");
		jpql.append(" AND p.procPurchaseFundProdProduct.id=" + product.getId());
		List<ProcPurchaseFundProdPayDetail> details = entityService.find(jpql.toString());
		Double num = 0.0D;
		if(details != null && details.size() > 0) {
			for (ProcPurchaseFundProdPayDetail procPurchaseFundProdPayDetail : details) {
				if(procPurchaseFundProdPayDetail.getVarietyAmount() != null) {
					num += procPurchaseFundProdPayDetail.getVarietyAmount();
				}
			}
		}
		log.info("获得品种剩余需要审批金额:" + num);
		return num;
	}

	/**
	 * 获得品种1已审批头寸
	 * @param amo
	 * @return
	 */
	public Double getNeedPurchaseNumOne(List<ProcPurchaseFundProdDetail> amo) {
		Double num = 0D;
		for (ProcPurchaseFundProdDetail d : amo) {
			if (d.getVarietyOneNum() != null) {
				log.info("d.getVarietyOneAmount()(单个):" + d.getVarietyOneNum());
				num += d.getVarietyOneNum();
			}
		}
		log.info("获得品种1剩余需要审批头寸:" + num);
		return num;
	}

	/**
	 * 获得品种1已审批金额
	 * @param amo
	 * @return
	 */
	public Double getNeedPurchaseAmountOne(List<ProcPurchaseFundProdDetail> amo) {
		Double num = 0D;
		for (ProcPurchaseFundProdDetail d : amo) {
			if (d.getVarietyOneAmount() != null) {
				log.info("d.getVarietyOneAmount()(单个):" + d.getVarietyOneAmount());
				num += d.getVarietyOneAmount();
			}
		}
		log.info("获得品种1剩余需要审批金额:" + num);
		return num;
	}

	/**
	 * 获得品种2已审批头寸
	 * @param amo
	 * @return
	 */
	public Double getNeedPurchaseNumTwo(List<ProcPurchaseFundProdDetail> amo) {
		Double num = 0D;
		for (ProcPurchaseFundProdDetail d : amo) {
			if (d.getVarietyTwoNum() != null) {
				log.info("d.getVarietyTwoAmount()(单个):" + d.getVarietyTwoNum());
				num += d.getVarietyTwoNum();
			}
		}
		log.info("获得品种2剩余需要审批头寸:" + num);
		return num;
	}

	/**
	 * 获得品种2已审批金额
	 * @param amo
	 * @return
	 */
	public Double getNeedPurchaseAmountTwo(List<ProcPurchaseFundProdDetail> amo) {
		Double num = 0D;
		for (ProcPurchaseFundProdDetail d : amo) {
			if (d.getVarietyTwoAmount() != null) {
				log.info("d.getVarietyTwoAmount()(单个):" + d.getVarietyTwoAmount());
				num += d.getVarietyTwoAmount();
			}
		}
		log.info("获得品种2剩余需要审批金额:" + num);
		return num;
	}

	/**
	 * 获得品种3已审批头寸
	 * @param amo
	 * @return
	 */
	public Double getNeedPurchaseNumThr(List<ProcPurchaseFundProdDetail> amo) {
		Double num = 0D;
		for (ProcPurchaseFundProdDetail d : amo) {
			if (d.getVarietyThrNum() != null) {
				log.info("d.getVarietyThrAmount()(单个):" + d.getVarietyThrNum());
				num += d.getVarietyThrNum();
				log.info("获得品种tttttt1111111111111:" + num.doubleValue());
			}
		}
		log.info("获得品种3剩余需要审批头寸:" + num);
		return num;
	}

	/**
	 * 获得品种3已审批金额
	 * @param amo
	 * @return
	 */
	public Double getNeedPurchaseAmountThr(List<ProcPurchaseFundProdDetail> amo) {
		Double num = 0D;
		for (ProcPurchaseFundProdDetail d : amo) {
			if (d.getVarietyThrAmount() != null) {
				log.info("d.getVarietyThrAmount()(单个):" + d.getVarietyThrAmount());
				num += d.getVarietyThrAmount();
				log.info("获得品种31111111111111:" + num.doubleValue());
			}
		}
		log.info("获得品种2剩余需要审批金额:" + num);
		return num;
	}

	/**
	 * VO中删除品种
	 * @param procPurchaseFundProd
	 */
	public void doDelete(ProcPurchaseFundProd procPurchaseFundProd) {
		log.info("更新数据库品种状态");
		entityService.update(procPurchaseFundProd);
	}

	/**
	 * 确认保存(付款或者终止付款)
	 * @param procPurchaseFundVos
	 * @param procPurchaseFundProd
	 * @param procPurchaseFundProdDetail
	 * @param conf
	 * @param payWay
	 * @param stepName
	 * @param purchaseFund
	 * @throws ServiceException
	 */
	public void doConfirm(List<ProcPurchaseFundVo> procPurchaseFundVos, ProcPurchaseFundProd procPurchaseFundProd,
			ProcPurchaseFundProdDetail procPurchaseFundProdDetail, Boolean conf, Boolean payWay, String stepName, PurchaseFund purchaseFund)
			throws ServiceException {
		// conf:付款标识符-true:付款，false:终止付款
		try {
			// add on 2013-7-22
			entityService.update(procPurchaseFundProd);
			// 加入流程备注抬头
			String memoTitle = "";
			if (conf) {
				memoTitle = ProcessXmlUtil.findStepProperty("id", "PurchaseFundProd", stepName, "passMemo");
			} else {
				memoTitle = ProcessXmlUtil.findStepProperty("id", "PurchaseFundProd", stepName, "nopassMemo");
			}
			String remark = procPurchaseFundProd.getPeMemo();
			if (memoTitle != null) {
				procPurchaseFundProd.setPeMemo(memoTitle + procPurchaseFundProd.getPeMemo());
			}
			// payWay true:SUNGARD付款 false:网银付款
			if (payWay) {
				procPurchaseFundProdDetail.setPayWay("S");
			} else {
				procPurchaseFundProdDetail.setPayWay("O");
			}
			if (conf) {
				log.info("执行付款任务1111111111111111");
				// 是否若为true则为足额支付(足额支付包括头寸和金额两个条件),false为终止付款
				Boolean ifAll = ifEnoughPay(procPurchaseFundVos);
				log.info("验证完毕：验证结果是：" + ifAll);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("_Enough", ifAll);
				Boolean isSungard = false;
				if ("S".equals(procPurchaseFundProdDetail.getPayWay())) {
					isSungard = true;
					map.put("TMS_Is_Sungard", true);
				} else {
					isSungard = false;
					map.put("TMS_Is_Sungard", false);
				}
				log.info("保存流程明细");
				ProcPurchaseFundProdDetail detail = new ProcPurchaseFundProdDetail();
				// 保存流程明细
				detail = this.saveDetail(procPurchaseFundVos, procPurchaseFundProd, procPurchaseFundProdDetail, payWay);
				log.info("保存主数据、支付明细主数据");
				// 保存主数据、支付明细主数据
				this.savePFToMain(procPurchaseFundVos, procPurchaseFundProd, purchaseFund, payWay);
				// 像新表增加支付主数据和支付明细
				ProcPurchaseFundProdPay pay = this.savePayInfo(procPurchaseFundVos, procPurchaseFundProd, payWay);
				peManager.vwDisposeTask(procPurchaseFundProd.getProcInstId(), map, procPurchaseFundProd.getPeMemo());
				//调用生成ftp文件方法 add on 2013-8-1 by yan
				String className = ProcessXmlUtil.getProcessAttribute("id", "PurchaseFundProd","className");
				String bpmId = ftpUploadService.tmsFtpUploadFile(procPurchaseFundProd.getProcInstId(),
						className, procPurchaseFundProdDetail.getCreatedDatetime(), isSungard);
				if(payWay) {
					ProcTMSStatus tmsStatus = new ProcTMSStatus();
					tmsStatus.setPayId(pay.getId());
					tmsStatus.setBpmId(bpmId);
					tmsStatus.setTmsStatus(TmsStatusService.STATUS_NOIMPORT);
					//增加TMS回传表
					tmsStatusService.saveTmsStatus(tmsStatus);
				}else {
					// 邮件知会申请人
					mailRequester(procPurchaseFundVos, procPurchaseFundProd, procPurchaseFundProdDetail, conf, ifAll, stepName,remark);
				}
			} else {
				log.info("执行终止付款任务222222222222222222");
				conf = true;
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("_Enough", conf);
				map.put("TMS_Is_Sungard", false);
				log.info("stop的值是：" + conf);
				peManager.vwDisposeTask(procPurchaseFundProd.getProcInstId(), map, procPurchaseFundProd.getPeMemo());
				// 邮件知会申请人
				conf = false;
				mailRequester(procPurchaseFundVos, procPurchaseFundProd, procPurchaseFundProdDetail, conf, conf, stepName,remark);
			}
		} catch (Exception e) {
		    log.error(" doConfirm方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}

	/**
	 * 保存数据到流程明细
	 * @param procPurchaseFundVos
	 * @param procPurchaseFundProd
	 * @param procPurchaseFundProdDetail
	 * @param payWay
	 * @throws ServiceException
	 */
	public ProcPurchaseFundProdDetail saveDetail(List<ProcPurchaseFundVo> procPurchaseFundVos, ProcPurchaseFundProd procPurchaseFundProd,
			ProcPurchaseFundProdDetail procPurchaseFundProdDetail, Boolean payWay) throws ServiceException {
		log.info("保存数据到流程明细表中");
		try {
			Double amount = 0D;
			for (ProcPurchaseFundVo ppfVo : procPurchaseFundVos) {
				if ((ppfVo.getVarietyId()).equals(procPurchaseFundProd.getVarietyOne()) && "N".equals(procPurchaseFundProd.getVarietyOneDefunct())
						&& (ppfVo.getVarietyRelated()).equals(procPurchaseFundProd.getVarietyOneRelated())) {
					procPurchaseFundProdDetail.setVarietyOne(ppfVo.getVarietyId());
					procPurchaseFundProdDetail.setVarietyOneAmount(ppfVo.getThisPurchaseAmountPay());
					procPurchaseFundProdDetail.setVarietyOneNum(ppfVo.getThisPurchaseNumPay());
					log.info("品种1信息保存到流程明细！");
				}
				if ((ppfVo.getVarietyId()).equals(procPurchaseFundProd.getVarietyTwo()) && "N".equals(procPurchaseFundProd.getVarietyTwoDefunct())
						&& (ppfVo.getVarietyRelated()).equals(procPurchaseFundProd.getVarietyTwoRelated())) {
					procPurchaseFundProdDetail.setVarietyTwo(ppfVo.getVarietyId());
					procPurchaseFundProdDetail.setVarietyTwoAmount(ppfVo.getThisPurchaseAmountPay());
					procPurchaseFundProdDetail.setVarietyTwoNum(ppfVo.getThisPurchaseNumPay());
					log.info("品种2信息保存到流程明细！");
				}
				if ((ppfVo.getVarietyId()).equals(procPurchaseFundProd.getVarietyThr()) && "N".equals(procPurchaseFundProd.getVarietyThrDefunct())
						&& (ppfVo.getVarietyRelated()).equals(procPurchaseFundProd.getVarietyThrRelated())) {
					procPurchaseFundProdDetail.setVarietyThr(ppfVo.getVarietyId());
					procPurchaseFundProdDetail.setVarietyThrAmount(ppfVo.getThisPurchaseAmountPay());
					procPurchaseFundProdDetail.setVarietyThrNum(ppfVo.getThisPurchaseNumPay());
					log.info("品种3信息保存到流程明细！");
				}
				amount = MathUtil.roundHalfUp(amount + ppfVo.getThisPurchaseAmountPay(),4);
				procPurchaseFundProdDetail.setPayFundsTotal(amount);
			}
			// payWay true:SUNGARD付款 false:网银付款
			if (payWay) {
				procPurchaseFundProdDetail.setPayWay("S");
			} else {
				procPurchaseFundProdDetail.setPayWay("O");
			}
			procPurchaseFundProdDetail.setPayDatetime(new Date());
			procPurchaseFundProdDetail.setCreatedBy(loginService.getCurrentUserName());
			return entityService.create(procPurchaseFundProdDetail);
		} catch (Exception e) {
		    log.error(" saveDetail方法 错误信息：" + e.getMessage());
		    return null;
		}
	}

	/**
	 * 保存品种信息到主数据表
	 * @param procPurchaseFundVos
	 * @param procPurchaseFundProd
	 * @param purchaseFund
	 * @param payWay
	 */
	private void savePFToMain(List<ProcPurchaseFundVo> procPurchaseFundVos, ProcPurchaseFundProd procPurchaseFundProd, PurchaseFund purchaseFund,
			Boolean payWay) {
		log.info("保存到主数据");
		try {
			for (ProcPurchaseFundVo pfVo : procPurchaseFundVos) {
				StringBuilder jpql = new StringBuilder("select pF from PurchaseFund pF join fetch pF.company where pF.defunctInd = 'N'");
				jpql.append(" and pF.procInstId='" + procPurchaseFundProd.getProcInstId() + "'");
				jpql.append(" and pF.variety='" + pfVo.getVarietyId() + "'");
				jpql.append(" and pF.varietyRelated='" + pfVo.getVarietyRelated() + "'");
				log.info("jpql:" + jpql);
				List<PurchaseFund> pf = entityService.find(jpql.toString());
				log.info("List<PurchaseFund> pf:" + pf.size());
				// 判断相同流程ID和品种ID是否在主数据表中已经存在数据，若存在，创建其子表，若不存在，创建主数据表，并创建其子表。
				if (pf.size() == 0) {
					if (pfVo.getVarietyId().equals(procPurchaseFundProd.getVarietyOne()) && "N".equals(procPurchaseFundProd.getVarietyOneDefunct())
							&& (pfVo.getVarietyRelated().equals(procPurchaseFundProd.getVarietyOneRelated()))) {
						log.info("获得品种1");
						PurchaseFund pf1 = new PurchaseFund();
						// 获得品种1
						// 流程实例ID
						pf1.setProcInstId(procPurchaseFundProd.getProcInstId());
						// 帐号描述
						pf1.setAccountDesc(procPurchaseFundProd.getAccountDesc());
						// 类型
						pf1.setTape("P");
						// 借款转款标识
						pf1.setLoanIden(procPurchaseFundProd.getLoanIden());
						// 付款日期
						pf1.setPayTime(procPurchaseFundProd.getPaymentDate());
						// 帐号
						pf1.setAccounts(procPurchaseFundProd.getAccountNumber());
						// 收款人姓名
						pf1.setReceiverName(procPurchaseFundProd.getReceiverName());
						// 下拨资金用途描述
						pf1.setFundPurpDesc(procPurchaseFundProd.getUseDesc());
						pf1.setCompany(procPurchaseFundProd.getCompany());
						pf1.setCreatedBy(loginService.getCurrentUserName());
						pf1.setAppTime(procPurchaseFundProd.getCreatedDatetime());
						// 品种
						pf1.setVariety(procPurchaseFundProd.getVarietyOne());
						// 是否为关联方
						pf1.setVarietyRelated(procPurchaseFundProd.getVarietyOneRelated());
						// 申请金额
						pf1.setVarietyAmount(procPurchaseFundProd.getVarietyOneAmount());
						// 剩余头寸数量
						pf1.setVarietyNum(procPurchaseFundProd.getVarietyOneNum());
						// 申请金额
						pf1.setVarietyRemain(procPurchaseFundProd.getVarietyOneRemain());
						// 备注
						pf1.setVarietyRemark(procPurchaseFundProd.getVarietyOneRemark());
						entityService.create(pf1);
						// 创建支付明细主数据表
						String varietyID = procPurchaseFundProd.getVarietyOne();
						log.info("varietyID:" + varietyID);
						this.setPayDetail(procPurchaseFundProd, varietyID, pfVo, payWay);
					} else if (pfVo.getVarietyId().equals(procPurchaseFundProd.getVarietyTwo())
							&& "N".equals(procPurchaseFundProd.getVarietyTwoDefunct())
							&& (pfVo.getVarietyRelated().equals(procPurchaseFundProd.getVarietyTwoRelated()))) {
						PurchaseFund pf2 = new PurchaseFund();
						log.info("获得品种2");
						// 获得品种2
						// 流程实例ID
						pf2.setProcInstId(procPurchaseFundProd.getProcInstId());
						// 帐号描述
						pf2.setAccountDesc(procPurchaseFundProd.getAccountDesc());
						// 类型
						pf2.setTape("P");
						// 借款转款标识
						pf2.setLoanIden(procPurchaseFundProd.getLoanIden());
						// 付款日期
						pf2.setPayTime(procPurchaseFundProd.getPaymentDate());
						// 帐号
						pf2.setAccounts(procPurchaseFundProd.getAccountNumber());
						// 收款人姓名
						pf2.setReceiverName(procPurchaseFundProd.getReceiverName());
						// 下拨资金用途描述
						pf2.setFundPurpDesc(procPurchaseFundProd.getUseDesc());
						pf2.setCompany(procPurchaseFundProd.getCompany());
						pf2.setCreatedBy(loginService.getCurrentUserName());
						pf2.setAppTime(procPurchaseFundProd.getCreatedDatetime());

						pf2.setVariety(procPurchaseFundProd.getVarietyTwo());
						// 是否为关联方
						pf2.setVarietyRelated(procPurchaseFundProd.getVarietyTwoRelated());
						pf2.setVarietyAmount(procPurchaseFundProd.getVarietyTwoAmount());
						pf2.setVarietyNum(procPurchaseFundProd.getVarietyTwoNum());
						pf2.setVarietyRemain(procPurchaseFundProd.getVarietyTwoRemain());
						pf2.setVarietyRemark(procPurchaseFundProd.getVarietyTwoRemark());
						entityService.create(pf2);
						// 创建支付明细主数据表
						String varietyID = procPurchaseFundProd.getVarietyTwo();
						log.info("varietyID:" + varietyID);
						this.setPayDetail(procPurchaseFundProd, varietyID, pfVo, payWay);
					} else if (pfVo.getVarietyId().equals(procPurchaseFundProd.getVarietyThr())
							&& "N".equals(procPurchaseFundProd.getVarietyThrDefunct())
							&& (pfVo.getVarietyRelated().equals(procPurchaseFundProd.getVarietyThrRelated()))) {
						log.info("获得品种3");
						// 获得品种1
						PurchaseFund pf3 = new PurchaseFund();
						// 流程实例ID
						pf3.setProcInstId(procPurchaseFundProd.getProcInstId());
						// 帐号描述
						pf3.setAccountDesc(procPurchaseFundProd.getAccountDesc());
						// 类型
						pf3.setTape("P");
						// 借款转款标识
						pf3.setLoanIden(procPurchaseFundProd.getLoanIden());
						// 付款日期
						pf3.setPayTime(procPurchaseFundProd.getPaymentDate());
						// 帐号
						pf3.setAccounts(procPurchaseFundProd.getAccountNumber());
						// 收款人姓名
						pf3.setReceiverName(procPurchaseFundProd.getReceiverName());
						// 下拨资金用途描述
						pf3.setFundPurpDesc(procPurchaseFundProd.getUseDesc());
						pf3.setCompany(procPurchaseFundProd.getCompany());
						pf3.setCreatedBy(loginService.getCurrentUserName());
						pf3.setAppTime(procPurchaseFundProd.getCreatedDatetime());

						pf3.setVariety(procPurchaseFundProd.getVarietyThr());
						// 是否为关联方
						pf3.setVarietyRelated(procPurchaseFundProd.getVarietyThrRelated());
						pf3.setVarietyAmount(procPurchaseFundProd.getVarietyThrAmount());
						pf3.setVarietyNum(procPurchaseFundProd.getVarietyThrNum());
						pf3.setVarietyRemain(procPurchaseFundProd.getVarietyThrRemain());
						pf3.setVarietyRemark(procPurchaseFundProd.getVarietyThrRemark());
						entityService.create(pf3);
						// 创建支付明细主数据表
						String varietyID = procPurchaseFundProd.getVarietyThr();
						log.info("varietyID:" + varietyID);
						this.setPayDetail(procPurchaseFundProd, varietyID, pfVo, payWay);
					}
				} else {
					log.info("主数据表中已经有相关品种的信息");
					// 创建支付明细主数据表
					this.setPayDetail(procPurchaseFundProd, pfVo.getVarietyId(), pfVo, payWay);
				}
			}
		} catch (Exception e) {
			log.error("savePFToMain方法 错误信息：" + e.getMessage());
			throw new ServiceException(ExceptionMessage.FN_CONNECT, e);
		}
	}
	
	/**
	 * 向新表增加支付主数据和支付明细
	 * @param procPurchaseFundVos
	 * @param procPurchaseFundTrade
	 * @param payWay
	 */
	public ProcPurchaseFundProdPay savePayInfo(List<ProcPurchaseFundVo> procPurchaseFundVos, ProcPurchaseFundProd procPurchaseFundProd,  Boolean payWay) {
		ProcPurchaseFundProdPay pay = new ProcPurchaseFundProdPay();
		//支付方式
		if (payWay) {
			pay.setPayWay("S");
		} else {
			pay.setPayWay("O");
		}
		//支付总金额
		Double amount = 0D;
		for (ProcPurchaseFundVo procPurchaseFundVo : procPurchaseFundVos) {
			amount = MathUtil.roundHalfUp(amount + procPurchaseFundVo.getThisPurchaseAmountPay(),4);
		}
		//总金额
		pay.setPayFundsTotal(amount);
		//创建人
		pay.setCreatedBy(loginService.getCurrentUserName());
		//修改人
		pay.setUpdatedBy(loginService.getCurrentUserName());
		pay.setDefunctInd("N");
		//支付时间
		pay.setPayDatetime(new Date());
		//流程贸易ID
		pay.setProcPurchaseFundProd(procPurchaseFundProd);
		entityService.create(pay);
		//保存支付明细
		for (ProcPurchaseFundVo procPurchaseFundVo : procPurchaseFundVos) {
			if((procPurchaseFundVo.getThisPurchaseAmountPay() != null && procPurchaseFundVo.getThisPurchaseAmountPay() > 0) 
					|| (procPurchaseFundVo.getThisPurchaseNumPay() != null && procPurchaseFundVo.getThisPurchaseNumPay() > 0)) {
				ProcPurchaseFundProdPayDetail detail = new ProcPurchaseFundProdPayDetail();
				ProcPurchaseFundProdProduct product = new ProcPurchaseFundProdProduct();
				product.setId(procPurchaseFundVo.getProductId());
				detail.setProcPurchaseFundProdPay(pay);
				detail.setProcPurchaseFundProdProduct(product);
				detail.setVarietyAmount(procPurchaseFundVo.getThisPurchaseAmountPay());
				detail.setVarietyNum(procPurchaseFundVo.getThisPurchaseNumPay());
				entityService.create(detail);
			}
		}
		
		return pay;
	}

	/**
	 * 向支付明细表中保存数据
	 * @param procPurchaseFundProd
	 * @param varietyId
	 * @param pfVo
	 * @param payWay
	 */
	public void setPayDetail(ProcPurchaseFundProd procPurchaseFundProd, String varietyId, ProcPurchaseFundVo pfVo, Boolean payWay) {
		PurchaseFundDetail purchaseFundDetail = new PurchaseFundDetail();
		StringBuilder jpql = new StringBuilder("select pF from PurchaseFund pF join fetch pF.company where pF.defunctInd = 'N'");
		jpql.append(" and pF.procInstId='" + procPurchaseFundProd.getProcInstId() + "'");
		jpql.append(" and pF.variety='" + varietyId + "'");
		log.info("jpql:" + jpql);
		List<PurchaseFund> pf = entityService.find(jpql.toString());
		// 设置外键关联
		PurchaseFund pu = this.entityService.find(PurchaseFund.class, pf.get(0).getId());
		purchaseFundDetail.setPurchaseFund(pu);
		purchaseFundDetail.setProcInstId(procPurchaseFundProd.getProcInstId());
		// 审批头寸
		purchaseFundDetail.setAppNumPay(pfVo.getThisPurchaseNumPay());
		// 审批金额
		purchaseFundDetail.setAppAmountPay(pfVo.getThisPurchaseAmountPay());
		// payWay true:SUNGARD付款 false:网银付款
		if (payWay) {
			purchaseFundDetail.setPayWay("S");
		} else {
			purchaseFundDetail.setPayWay("O");
		}
		// 支付时间
		purchaseFundDetail.setPayTime(new Date());
		purchaseFundDetail.setCreatedBy(loginService.getCurrentUserName());
		purchaseFundDetail.setCreatedDatetime(new Date());
		entityService.create(purchaseFundDetail);
	}

	/**
	 * 验证是否足额支付
	 * @param procPurchaseFundVos
	 * @return
	 */
	public Boolean ifEnoughPay(List<ProcPurchaseFundVo> procPurchaseFundVos) {
		log.info("验证是否足额支付");
		// 剩余需要支付的金额
		double lessPay = 0D;
		// 本次支付的金额
		double needPay = 0D;
		// 剩余需要支付的数量
		double lessAmount = 0D;
		// 本次审批数量
		double needAmount = 0D;
		for (ProcPurchaseFundVo ppfVo : procPurchaseFundVos) {
			lessPay += ppfVo.getLessPurchaseAmountPay();
			needPay += ppfVo.getThisPurchaseAmountPay();
			lessAmount += ppfVo.getNeedPurchaseNum();
			needAmount += ppfVo.getThisPurchaseNumPay();
		}
		log.info("获得剩余需要支付的总金额：" + lessPay);
		log.info("获得本次支付的总金额:" + needPay);
		log.info("剩余需要支付的数量:" + lessAmount);
		log.info("本次审批数量:" + needAmount);
		if (lessPay == needPay && lessAmount == needAmount) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 邮件付款方式选择
	 * @param procPurchaseFundProdDetail
	 * @return
	 */
	public String selectPayWay(ProcPurchaseFundProdDetail procPurchaseFundProdDetail) {
		if ("S".equals(procPurchaseFundProdDetail.getPayWay())) {
			return "SUNGARD付款";
		} else {
			return "网银支付";
		}
	}

	/**
	 * 邮件通知申请人
	 * @param procPurchaseFundVos
	 * @param procPurchaseFundProd
	 * @param procPurchaseFundProdDetail
	 * @param conf
	 * @param ifAll
	 * @param stepName
	 * @param remark 
	 */
	private void mailRequester(List<ProcPurchaseFundVo> procPurchaseFundVos, ProcPurchaseFundProd procPurchaseFundProd,
			ProcPurchaseFundProdDetail procPurchaseFundProdDetail, Boolean conf, Boolean ifAll, String stepName, String remark) {
	    // 本次支付的金额
	    Double needPay = 0D;
	    // 本次审批数量
		Double needAmount = 0D;
		DecimalFormat df = new DecimalFormat("0.00");
		for (ProcPurchaseFundVo ppfVo : procPurchaseFundVos) {
			needPay = MathUtil.roundHalfUp(needPay + ppfVo.getThisPurchaseAmountPay(),4);
			needAmount = MathUtil.roundHalfUp(needAmount + ppfVo.getThisPurchaseNumPay(),4);
		}
		log.info("邮件知会申请人！！！！！！！！！！！");
		List<Mail> mailList = new ArrayList<Mail>();
		StringBuilder bussMailBody = new StringBuilder("您申请的");
		if (conf && ("集团资金计划员付款".equals(stepName)) && (ifAll == false)) {
			// 邮件业务内容
			// 您申请的***流程,编号为[流程实例ID],集团资金计划已经通过***方式下拨***元,请知悉,谢谢!"
			bussMailBody.append("“采购资金（生产）借款/转款流程”，编号为：【" + processUtilMapService.getTmsIdByFnId(procPurchaseFundProd.getProcInstId())
					+ "】，付款金额为：" + df.format(procPurchaseFundProdDetail.getPayFundsTotal()) +"万元，各节点已经完成审批，并生成付款指令，资金调度同事即将处理！预计付款日期为：" + 
					DateUtil.dateToStrShort(procPurchaseFundProdDetail.getPayDatetime()) + "，付款方式为："
					+ this.selectPayWay(procPurchaseFundProdDetail)+",审批头寸数为：" + df.format(needAmount) + "吨。" );
			bussMailBody.append("审批备注："+remark+"。请知悉，谢谢！");
			log.info(bussMailBody);
		} else if (conf && ("集团资金计划员付款".equals(stepName)) && (ifAll == true)) {
			bussMailBody.append("“采购资金（生产）借款/转款流程”，编号为：【" + processUtilMapService.getTmsIdByFnId(procPurchaseFundProd.getProcInstId())
					+ "】，付款金额为：" + df.format(procPurchaseFundProdDetail.getPayFundsTotal())+"万元，各节点已经完成审批，并生成付款指令，资金调度同事即将处理！预计付款日期为：" + 
					DateUtil.dateToStrShort(procPurchaseFundProdDetail.getPayDatetime()) + "，付款方式为："
					+ this.selectPayWay(procPurchaseFundProdDetail)+",审批头寸数为：" + df.format(needAmount) + "吨。" );
			bussMailBody.append("审批备注："+remark+"。此流程所有金额均已完成审批！请知悉，谢谢！");
		} else {
			bussMailBody.append("“采购资金（生产）借款/转款流程”，编号为：【" + processUtilMapService.getTmsIdByFnId(procPurchaseFundProd.getProcInstId())
					+ "】已经终止付款！请知悉，谢谢！");
			log.info(bussMailBody);
		}
		StringBuilder jpql = new StringBuilder("select distinct pu.pernr from PU pu where pu.defunctInd='N' ");
		jpql.append(" and pu.id='" + procPurchaseFundProdDetail.getProcPurchaseFundProd().getCreatedBy() + "'");
		log.info("jpql语句为：" + jpql);
		String pid = entityService.findUnique(jpql.toString());
		log.info("pid为：" + pid);
		P p = entityService.find(P.class, pid);
		log.info("p:" + p);
		log.info(p.getEmail() + p.getNachn() + p.getName2());
		if (p != null) {
			Mail m = new Mail();
			m.setTelno(p.getCelno());
			m.setEmail(p.getEmail());
			m.setSubject(mailService.generationTitle(MailUtil.MailTypeEnum.Notice, "TMS_PurchaseFundProd",
					processUtilMapService.getTmsIdByFnId(procPurchaseFundProd.getProcInstId()), stepName, loginService.getCurrentUserName(), null));
			m.setBody(mailService.generationContent(MailUtil.MailTypeEnum.Notice, "TMS_PurchaseFundProd",
					processUtilMapService.getTmsIdByFnId(procPurchaseFundProd.getProcInstId()), stepName, loginService.getCurrentUserName(), null,
					null) + bussMailBody.toString());
			mailList.add(m);
		}
		log.info("邮件内容：" + mailList.get(0).getBody());
		log.info("邮件主题：" + mailList.get(0).getSubject());
		log.info("邮箱：" + mailList.get(0).getEmail());
		log.info(mailList);
		log.info("邮件封装前！！！！！！！！！");
		// 邮件封装
		sendMailService.send(mailList);
	}

	/**
	 * 保存资金生产产品
	 * @param procPurchaseFundTrade
	 * @param purchaseFundList
	 */
	public void saveProdProduct(ProcPurchaseFundProd procPurchaseFundProd,
			List<PurchaseFund> purchaseFundList) {
		String jpql = "DELETE FROM ProcPurchaseFundProdProduct p WHERE p.procPurchaseFundProd.id =" + procPurchaseFundProd.getId();
		entityService.batchExecute(jpql);
System.out.println("purchaseFundList size:" + purchaseFundList.size());
		for (PurchaseFund purchaseFund : purchaseFundList) {
			ProcPurchaseFundProdProduct product = new ProcPurchaseFundProdProduct();
			product.setProcPurchaseFundProd(procPurchaseFundProd);
			product.setVariety(purchaseFund.getVariety());
			product.setVarietyAmount(purchaseFund.getVarietyAmount());
			product.setVarietyDefunct("N");
			product.setVarietyNum(purchaseFund.getVarietyNum());
			product.setVarietyRelated(purchaseFund.getVarietyRelated());
			product.setVarietyRemain(purchaseFund.getVarietyRemain());
			product.setVarietyRemark(purchaseFund.getVarietyRemark());
			entityService.create(product);
		}
	}

	/**
	 * 查询品种信息
	 * @param ppfVoEdit
	 * @return
	 */
	public ProcPurchaseFundProdProduct getTradeProduct(ProcPurchaseFundVo ppfVoEdit) {
		StringBuilder jpql = new StringBuilder("select pp from ProcPurchaseFundProdProduct pp where pp.id = ?1");
		return entityService.findUnique(jpql.toString(), ppfVoEdit.getProductId());
	}

	/**
	 * 删除品种信息
	 * @param ppfVoEdit
	 */
	public void doDeleteProduct(ProcPurchaseFundVo ppfVoEdit) {
		ProcPurchaseFundProdProduct product = getTradeProduct(ppfVoEdit);
		product.setVarietyDefunct("Y");
		entityService.update(product);
	}
}