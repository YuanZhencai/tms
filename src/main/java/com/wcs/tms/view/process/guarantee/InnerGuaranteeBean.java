package com.wcs.tms.view.process.guarantee;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.service.LoginService;
import com.wcs.base.util.ArrayUtil;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.MathUtil;
import com.wcs.base.util.StringUtils;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.CommonBean;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.Credit;
import com.wcs.tms.model.ProcGuarantee;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.service.process.common.ProcessUtilService;
import com.wcs.tms.service.process.common.patch.service.PatchMainService;
import com.wcs.tms.service.process.guarantee.InnerGuaranteeService;
import com.wcs.tms.service.system.bank.BankService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.service.system.company.CreditService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.FileUpload;
import com.wcs.tms.view.process.common.entity.ProcessDetailVo;
import com.wcs.tms.view.process.guarantee.vo.GuaranteeVO;

/**
 * <p>Project: tms</p>
 * <p>Description: 内部担保流程ManagedBean</p>
 * <p>Copyright © 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
@ManagedBean
@ViewScoped
public class InnerGuaranteeBean extends FileUpload<ProcGuarantee> {

    private static final long serialVersionUID = 8132156562201274352L;
    @Inject
    EntityService entityService;
    @Inject
    InnerGuaranteeService innerGuaranteeService;
    @Inject
    ProcessUtilService processUtilService;
    @Inject
    ProcessUtilMapService processUtilMapService;
    @Inject
    CompanyTmsService companyTmsService;
    @Inject
    LoginService loginService;
    @Inject
    CommonBean dictBean;
    @Inject
    private BankService bankSerice;
    @Inject
    PatchMainService patchMainService;
	@Inject
    PEManager peManager;
    @Inject
    private CreditService creditService;
    private static Log log = LogFactory.getLog(InnerGuaranteeBean.class);
    // 流程实体数据
    private ProcGuarantee procGuarantee = new ProcGuarantee();
    // 担保方,受保方信息VO
    private GuaranteeVO guarVO = new GuaranteeVO();
    //申请方公司ID
    private Long companyId ;
    // 申请公司下拉菜单
    private List<SelectItem> companySelect = new ArrayList<SelectItem>();
    // 担保方公司下拉菜单
    private List<SelectItem> guarCompanySelect = new ArrayList<SelectItem>();
    // 银行总行Id
    private Long topBankId;
    // 一级机构银行下拉
    private List<SelectItem> topLevelSelect = new ArrayList<SelectItem>();
    // 支行下拉
    private List<SelectItem> branchSelect = new ArrayList<SelectItem>();
    // 选择支行Id
    private Long branchSelectId;
    // 受益人(分支银行下拉菜单)
    private List<SelectItem> childBankSelect = new ArrayList<SelectItem>();
    // 资金币种下拉
    private List<SelectItem> currencySelect = new ArrayList<SelectItem>();

    /** 申请日期*/
    private String registerDate;
    // 二级菜单参数
    private String menuTwo;

    // 流程实例ID
    private String procInstId;

    // 流程步骤名称
    private String stepName;
    
    // 流程当前处理人（add by yanchangjing on 2013-1-11）
    private String thisStepExer;

    // 流程详细vo列表
    private List<ProcessDetailVo> detailVos = new ArrayList<ProcessDetailVo>();

    // 得到可输入的审批字段
    private List<String> inputableFields = new ArrayList<String>();
    // 得到可见块的审批字段
    private List<String> visibilityBlock = new ArrayList<String>();

    // 审批状态(通过与否)
    private String approveStatus;
    // 是否处理下一个任务
    private boolean doNext = true;
    // 当前所处理任务在任务列表中的位置
    private Integer currentIndex;
    // 当前处理任务类型（已接受和待接收）
    private String currentTaskType;
  //是否是补丁表中获取到数据查看，处理2014.4.11~2014.4.28之间发生故障，丢失的流程数据和流程审批日志的查看
    private String isPatch;
    

    /**
     * <p>Description:Bean init </p>
     */
    @PostConstruct
    public void init() {
        initDict();
        initOp();
        topLevelSelect = bankSerice.getTopLevelSelect();
        registerDate = DateUtil.dateToStrShort(DateUtil.getNowDateShort());
    }

    /**
     * 初始化操作类型
     */
    public void initOp() {
        String op = JSFUtils.getParamValue("op");
        if (op != null && !"".equals(op)) {
            procInstId = JSFUtils.getParamValue("procInstId");
            thisStepExer = JSFUtils.getParamValue("thisStepExer");
            // 查询 审批，查看时需要填充的数据
            fillData(procInstId);
            // 查看表单详细
            if ("view".equals(op)) {
                stepName = JSFUtils.getParamValue("stepName");
                menuTwo = JSFUtils.getParamValue("menu2");
                isPatch = JSFUtils.getParamValue("isPatch");
                this.findProcInstance();
                initCompany(true);
                initViewVisibilityBlock();
            }
            // 到审批页面
            if ("approve".equals(op)) {
            	// add on 2013-5-17
                currentIndex =Integer.parseInt(JSFUtils.getParamValue("currentIndex"));
                currentTaskType = (String)JSFUtils.getParamValue("currentTaskType");
                
                stepName = JSFUtils.getParamValue("stepName");
                this.getInputable();
                this.findProcInstance();
                initCompany(true);
                //非重新申请页面设置“处理意见”默认值
                if(!"申请".equals(stepName)){
                	procGuarantee.setPeMemo("同意");
                }	
                initApproveVisibilityBlock();
            }
            initGuarInfo();
            initGuarCompany();
        } else {
            if (loginService.isCopUser()) {
                initCompany(true);
            } else {
                initCompany(false);
            }
        }
    }

    /**
     * 
     * <p>Description:集团资金部审批 节点后，加载担保方和受保方信息 </p>
     */
    public void initGuarInfo() {
    	// modified on 2013-1-14 by yanchangjing
    	// add on 2013-1-14 by yanchangjing 集团资金部门审批时受保信息初始化
    	if("集团资金部门审批".equals(stepName)){
    	 // 当前流程实例
        	String processInstId = procGuarantee.getProcInstId(); 
        	/*
             * 受保方信息设置
             */
            // 有效期内已接受担保的金额
            Double validReceivedGuarAmount = innerGuaranteeService.getGuarAmountByCompanyId(processInstId, companyId, false);
            // 接受预担保总额度（经过【集团财务总监审批】并同意的担保）
            Double preReceivedGuarAmount = innerGuaranteeService.getPreGuarAmountByCompanyId(processInstId, companyId, "3", false);
            // 有效期内担保给其他公司的额度
            Double validOuterProviderGuarAmount = innerGuaranteeService.getGuarAmountByCompanyId(processInstId,companyId, true);
            // 预担保给其他公司的额度（经过【集团财务总监审批】并同意的担保）
            Double preOuterProviderGuarAmount = innerGuaranteeService.getPreGuarAmountByCompanyId(processInstId, companyId, "3",
                    true);
            // 审批中总额度（集团资金部审批完到集团财务总监审批完之间）
            Double outerReviewingGuarAmount = innerGuaranteeService
                    .getPreGuarAmountByCompanyId(processInstId, companyId, "2", false);
            // 已接受担保总额度(已接受担保总额度+预担保总额度)
            guarVO.setReceivedGuarAmount(validReceivedGuarAmount + preReceivedGuarAmount);
            // 已向外担保总额度
            guarVO.setOuterProviderGuarAmount(validOuterProviderGuarAmount + preOuterProviderGuarAmount);
            // 审批中总额度（集团资金部审批完到集团财务总监审批完之间）
            guarVO.setOuterReviewingGuarAmount(outerReviewingGuarAmount);
        }
        // 当前流程节点，需要显示担保方信息的时候，发出ajax请求
        if (checkVisibleBlock("danbaoInfoDiv")) {
            ajaxDisplay();
        }

    }

    /**
     * 初始化公司下拉
     */
    public void initCompany(boolean all) {
        if (all) {
            companySelect = companyTmsService.getAllCompanySelect();
        } else {
            companySelect = companyTmsService.getCompanySelect();
        }
    }

    /**
     * 初始化担保方公司下拉(所有公司去掉申请公司)
     */
    public void initGuarCompany() {
        if (procGuarantee == null || procGuarantee.getCompany() == null) { 
            return; 
        }

        List<SelectItem> allCompany = companyTmsService.getAllCompanySelect();
        for (SelectItem item : allCompany) {
            if (companyId.equals(item.getValue())) {
                continue;
            }
            guarCompanySelect.add(item);
        }

    }

    /**
     * 初始化数据字典
     */
    public void initDict() {
        currencySelect = dictBean.getDictByCode(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE);
    }

    /**
     * 
     * <p>Description: 申请公司id更新</p>
     */
    public void updateCompany() {

        // 选择公司以后需要将界面所选择的公司需要将值传入后台，通过Ajax来更新模型值
        log.info(procGuarantee.getCompany().getId());
    }

    /**
     * 
     * <p>Description: ajax请求,动态加载担保方和受保方信息：
     *  1、担保方公司id更新值后；
     *  2、净资产值更新后；
     *  3、本次担保额度更新后；
     *  然后更新相关控件值
     * </p>
     */
    public void ajaxDisplay() {
        // 担保方公司
        Long securedComId = procGuarantee.getSecuredCom().getId();        
        // 当前流程实例
        String processInstId = procGuarantee.getProcInstId(); 

        if (null == companyId || 0 == companyId) {
            MessageUtils.addErrorMessage("msg", "请先选择申请公司！");
            return;
        }
        /**
         * sonar测试  securedComId == companyId 改为 securedComId.equals(companyId)
         */
        if (null == securedComId || 0 == securedComId.longValue() || securedComId.equals(companyId)) {
            MessageUtils.addErrorMessage("msg", "请先选择担保公司！");
            return;
        }
        //如果申请公司和担保方公司相等，说明担保方公司数据错误的，不计算担保方的信息
        /**
         * sonar测试 companyId != securedComId改为!companyId.equals(securedComId)
         */
        if (!companyId.equals(securedComId)) {
            // 担保方净资产
            Double securedAssets = procGuarantee.getSecuredAssets() == null ? 0d : procGuarantee.getSecuredAssets(); 
            /*
             * 担保方信息设置
             */
            // 有效期内已担保的金额（（有效期内的经过审批的担保））
            Double validGuarAmount = innerGuaranteeService.getGuarAmountByCompanyId(processInstId, securedComId, true);
            // 预担保总额度（经过【集团财务总监审批】并同意的担保）
            Double preGuarAmount = innerGuaranteeService.getPreGuarAmountByCompanyId(processInstId, securedComId, "3", true);
            // 审批中总额度（集团资金部审批完到集团财务总监审批完之间）
            Double reviewingGuarAmount = innerGuaranteeService.getPreGuarAmountByCompanyId(processInstId, securedComId, "2",
                    true);
            // 已担保总额度
            Double alreadyTotalGuarAmount = validGuarAmount + preGuarAmount;

            // 剩余可用担保额度(可担保总额度-已担保总额度-预担保总额度-审批中担保总额度（集团资金部审批完到集团财务总监审批完之间的担保）
            Double remainGuarAmount = securedAssets * 1.50 - alreadyTotalGuarAmount - reviewingGuarAmount;

            guarVO.setTotalGuarAmount(securedAssets * 1.50);
            // 已担保总额度
            guarVO.setAlreadyTotalGuarAmount(alreadyTotalGuarAmount); 
            // 剩余可用担保额度
            guarVO.setRemainGuarAmount(remainGuarAmount); 
            // 审批中的额度
            guarVO.setReviewingGuarAmount(reviewingGuarAmount);
        }
        /*
         * 受保方信息设置
         */
        // 有效期内已接受担保的金额
        Double validReceivedGuarAmount = innerGuaranteeService.getGuarAmountByCompanyId(processInstId, companyId, false);
        // 接受预担保总额度（经过【集团财务总监审批】并同意的担保）
        Double preReceivedGuarAmount = innerGuaranteeService.getPreGuarAmountByCompanyId(processInstId, companyId, "3", false);
        // 有效期内担保给其他公司的额度
        Double validOuterProviderGuarAmount = innerGuaranteeService.getGuarAmountByCompanyId(processInstId,companyId, true);
        // 预担保给其他公司的额度（经过【集团财务总监审批】并同意的担保）
        Double preOuterProviderGuarAmount = innerGuaranteeService.getPreGuarAmountByCompanyId(processInstId, companyId, "3",
                true);
        // 审批中总额度（集团资金部审批完到集团财务总监审批完之间）
        Double outerReviewingGuarAmount = innerGuaranteeService
                .getPreGuarAmountByCompanyId(processInstId, companyId, "2", false);
        // 已接受当前担保公司的额度 (modified on 2013-1-10 已担保+预担保)
        Double alreadyReceCurCompanyAmount = innerGuaranteeService.getAmountByInsuredComAndCompanyId(processInstId, companyId, securedComId)
        									+ innerGuaranteeService.getPreAmountByInsuredComAndCompanyId(processInstId, companyId, securedComId, "3");
        // 已接受担保总额度(已接受担保总额度+预担保总额度)
        guarVO.setReceivedGuarAmount(validReceivedGuarAmount + preReceivedGuarAmount);
        // 已向外担保总额度
        guarVO.setOuterProviderGuarAmount(validOuterProviderGuarAmount + preOuterProviderGuarAmount);
        // 审批中总额度（集团资金部审批完到集团财务总监审批完之间）
        guarVO.setOuterReviewingGuarAmount(outerReviewingGuarAmount);
        // 已接受当前担保公司的额度
        guarVO.setAlreadyReceCurCompanyAmount(alreadyReceCurCompanyAmount);

    }

    /**
     * 
     * <p>Description: 总行联动支行</p>
     */
    public void bankChange() {
        if (null == procGuarantee.getCompany().getId()) {
            MessageUtils.addErrorMessage("msg", "请先选择申请公司");
            return;
        }
        branchSelect = creditService.findBranchBankSelect(topBankId);
        List<Credit> credits = creditService.findLastCredit(registerDate, topBankId, procGuarantee.getCompany().getId());
        if (credits.size() != 0) {
            branchSelectId = credits.get(0).getBank().getId();
        } else {
            branchSelectId = 0L;
        }
    }

    /**
     * 得到可输入的审批字段
     */
    private void getInputable() {
        inputableFields = ProcessXmlUtil.getInputableDatas("GuaranteeReq", stepName);
    }

    /**
     * 得到可输入的审批字段
     */
    private void initApproveVisibilityBlock() {
        if (StringUtils.isBlankOrNull(stepName)) {
            return;
        }
        String visibilityStr = StringUtils.safeString(ProcessXmlUtil.findStepProperty("id", "GuaranteeReq", stepName,
                "visibility"));
        List<String> blockFileds = ArrayUtil.convertArrayToList(visibilityStr.split(","));
        this.setVisibilityBlock(blockFileds);
    }
    
    /**
     * 设置查看页面的可见模块
     */
    private void initViewVisibilityBlock(){
    	// 如果流程走到这两节点，查看页面只显示申请信息
    	if("1".equals(procGuarantee.getStatus())){
    		List<String> blockFileds = new ArrayList<String>();
    		this.setVisibilityBlock(blockFileds);
    		return ;
    	}
    	// 不是审批中状态或者预担保申请人确认前任何用户都会看到担保方和受保方信息
    	if("2".equals(procGuarantee.getStatus())){
    		List<String> blockFileds = new ArrayList<String>();
    		blockFileds.add("danbaoInfoDiv");
    		blockFileds.add("tabView");
    		this.setVisibilityBlock(blockFileds);
    		return ;
    	}
    	if( "3".equals(procGuarantee.getStatus()) && ( null == procGuarantee.getRealGuarAmount() || procGuarantee.getRealGuarAmount() == 0 )
    			){
    		List<String> blockFileds = new ArrayList<String>();
    		blockFileds.add("danbaoInfoDiv");
    		blockFileds.add("tabView");
    		this.setVisibilityBlock(blockFileds);
    		return ;
    	}
    	// 申请人确认填写完确认数据后所有节点都可查看 确认数据
    	if("3".equals(procGuarantee.getStatus()) 
    			&& ( null != procGuarantee.getRealGuarAmount() && procGuarantee.getRealGuarAmount() != 0 )
    			|| "4".equals(procGuarantee.getStatus())){
    		List<String> blockFileds = new ArrayList<String>();
    		blockFileds.add("danbaoInfoDiv");
    		blockFileds.add("tabView");
    		blockFileds.add("confimInfoDiv");
    		this.setVisibilityBlock(blockFileds);
    		return ;
    	}
    	
    }

    /**
     * 可见的字段块检查
     */
    public boolean checkVisibleBlock(String blockName) {
        List<String> blockFileds = getVisibilityBlock();
        // modified on 2013-1-10 如果流程结束则返回true即查看可见
        if (StringUtils.isBlankOrNull(stepName)){
        	return true;
        }
        if (blockFileds != null && blockFileds.contains(blockName)) { 
        	return true; 
        }
        return false;
    }

    /**
     * 字段可输入检查
     * @return
     */
    public boolean checkInputable(String fieldName) {
        if (inputableFields != null && inputableFields.contains(fieldName)) { 
            return true; 
        }
        return false;
    }

    /**
     * 查询流程实例
     */
    public void findProcInstance() {
        procGuarantee = innerGuaranteeService.findProcInstanceByProcInstId(procInstId);
        companyId = procGuarantee.getCompany().getId();
        
        // 查询流程详细
        if("true".equals(isPatch)){
    		detailVos = patchMainService.getProcessDetailFor411(procInstId);
    	}else{
    		detailVos = peManager.getProcessDetail(procInstId);
    	}
        // 查询流程附件
        displayDetailAttach(procInstId);
    }

    /**
     * 
     * <p>Description: 审批，查看时需要填充的数据</p>
     * @param processInstId
     */
    private void fillData(String processInstId) {
        if (processInstId != null) {
            try {
                ProcGuarantee procGuarantee1 = innerGuaranteeService.findProcInstanceByProcInstId(processInstId);
                if (procGuarantee == null) { 
                    return; 
                }
                procGuarantee = procGuarantee1;
                if (procGuarantee != null && procGuarantee.getCompany() == null) {
                    MessageUtils.addErrorMessage("bankCreditMsg", MessageUtils.getMessage("regicapital_noBound"));
                }

                registerDate = DateUtil.dateToStrShort(DateUtil.dateToDateTime(procGuarantee.getCreatedDatetime()));
                // 得到分支授信银行
                Bank bank = procGuarantee.getBank();
                if (bank != null) {
                    // 选中一级银行
                    topBankId = bank.getTopBankId();
                    branchSelectId = bank.getId();
                    branchSelect.clear();
                    // 添加分支银行下拉
                    branchSelect.add(new SelectItem(branchSelectId, bank.getBankName()));
                }
            } catch (Exception e) {
                log.error("fillData方法  错误信息：" + e.getMessage());
            }
        }
    }

    /**
     * 
     * <p>Description:提交内部担保申请单，启动流程实例 </p>
     * @return
     */
    public String createAndStartProc() {
        // 设置申请公司
        Company company1 = entityService.find(Company.class, procGuarantee.getCompany().getId());
        procGuarantee.setCompany(company1);
        // 设置担保公司,由于担保方公司设置了外键，不能插入为空的值，先用申请方公司代替插入
        Company company2 = entityService.find(Company.class, procGuarantee.getCompany().getId());
        procGuarantee.setSecuredCom(company2);
        // 设置申请人和修改人
        if (StringUtils.isBlankOrNull(procGuarantee.getCreatedBy())) {
            procGuarantee.setCreatedBy(loginService.getCurrentUserName());
            procGuarantee.setUpdatedBy(loginService.getCurrentUserName());
        }

        // 设置银行
        Bank bank = entityService.find(Bank.class, branchSelectId);
        procGuarantee.setBank(bank);
        // 设置流程状态
        procGuarantee.setStatus("1");

        try {
            String procInstId = innerGuaranteeService.createProcInstance(procGuarantee);
            // 保存流程附件
            this.saveProcessFile(procInstId);
            MessageUtils.addSuccessMessage("msg", "发起内部担保申请新流程成功！流程实例编号[" + processUtilMapService.getTmsIdByFnId(procInstId) + "]");
        } catch (ServiceException e) {
            MessageUtils.addErrorMessage("msg", e.getMessage());
        } catch (Exception e) {
            if (e instanceof EJBException) {
                ServiceException se = (ServiceException) ((EJBException) e).getCause();
                MessageUtils.addErrorMessage("msg", se.getMessage());
            }
            if (e instanceof InvocationTargetException) {
                ServiceException se = (ServiceException) ((InvocationTargetException) e).getTargetException().getCause();
                MessageUtils.addErrorMessage("msg", se.getMessage());
            }
        }
        return "/faces/process/common/processSubed-list.xhtml";
    }

    /**
     * 审批保存
     * @return
     */
    public String doApprove() {
        boolean pass = false;
        if (approveStatus != null) {
            if ("Y".equals(approveStatus)) {
            	// 本次实际担保不能大于可担保金额 modified on 2013-1-15
            	if(stepName.endsWith("集团资金部门审批")){
            		//验证整个页面必填字段
            		Boolean succeed = this.validateWholePage();
            		if(!succeed){
            			return null;
            		}
            	}
                pass = true;
            }
            try {
                // 设置流程状态
                String passOrNoPassValueProperty = pass ? "passValue" : "nopassValue";
                procGuarantee.setStatus(ProcessXmlUtil.findStepProperty("id", "GuaranteeReq", stepName,
                        passOrNoPassValueProperty));
                //modified on 2013-1-8 如果确认金额小于等于申请金额流程结束否则未结束
                if("申请人确认".equals(stepName) && procGuarantee.getRealGuarAmount() <= procGuarantee.getApplyGuarAmount()){
                	procGuarantee.setStatus("4");
                }
                // 设置担保公司
                // 2013-5-28
                if(true == pass){
                	Company company2 = entityService.find(Company.class, procGuarantee.getSecuredCom().getId());
                	procGuarantee.setSecuredCom(company2);
                }

                innerGuaranteeService.doApprove(procGuarantee, pass, stepName);
                // 保存流程附件
                this.saveProcessFile(procInstId);
                MessageUtils.addSuccessMessage(
                        "doneMsg",
                        stepName
                                + MessageUtils.getMessage("process_save_success",
                                        processUtilMapService.getTmsIdByFnId(procInstId)));
            } catch (ServiceException e) {
                MessageUtils.addErrorMessage("doneMsg", e.getMessage());
            } catch (Exception e) {
                if (e instanceof EJBException) {
                    ServiceException se = (ServiceException) ((EJBException) e).getCause();
                    MessageUtils.addErrorMessage("doneMsg", se.getMessage());
                }
                if (e instanceof InvocationTargetException) {
                    ServiceException se = (ServiceException) ((InvocationTargetException) e).getTargetException().getCause();
                    MessageUtils.addErrorMessage("doneMsg", se.getMessage());
                }
                log.error(" doApprove方法 错误信息：" + e.getMessage());
            }
        }

        return processUtilService.getNextPage("/faces/process/common/processDealed-list.xhtml", doNext, getCurrentIndex(),getCurrentTaskType());
    }

    /**
     * 集团资金部门审批时页面验证
     * @return
     */
    private Boolean validateWholePage() {
    	Boolean succeed = true;
		
		if(null == procGuarantee.getSecuredCom().getId() || 0 == procGuarantee.getSecuredCom().getId()){
			MessageUtils.addErrorMessage("doneMsg", "请选择担保方");
			succeed = false;
		}
		if(null == procGuarantee.getSecuredAssets() || 0 == procGuarantee.getSecuredAssets()){
			MessageUtils.addErrorMessage("doneMsg", "请填写净资产");
			succeed = false;
		}
		if(null == procGuarantee.getGuarAmount() || 0 == procGuarantee.getGuarAmount()){
			MessageUtils.addErrorMessage("doneMsg", "请填写本次担保额度");
			succeed = false;
		}
		if(null == procGuarantee.getAllTotalGuarAmount() || 0 == procGuarantee.getAllTotalGuarAmount()){
			MessageUtils.addErrorMessage("doneMsg", "请填写累计担保额度");
			succeed = false;
		}
		if(MathUtil.isEmptyOrNull(procGuarantee.getIsNewGuarantee())){
			MessageUtils.addErrorMessage("doneMsg", "请选择是否为新增担保");
			succeed = false;
		}
		return succeed;
	}

	public EntityService getEntityService() {
        return entityService;
    }

    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    public InnerGuaranteeService getInnerGuaranteeService() {
        return innerGuaranteeService;
    }

    public void setInnerGuaranteeService(InnerGuaranteeService innerGuaranteeService) {
        this.innerGuaranteeService = innerGuaranteeService;
    }

    public ProcessUtilService getProcessUtilService() {
        return processUtilService;
    }

    public void setProcessUtilService(ProcessUtilService processUtilService) {
        this.processUtilService = processUtilService;
    }

    public ProcGuarantee getProcGuarantee() {
        return procGuarantee;
    }

    public void setProcGuarantee(ProcGuarantee procGuarantee) {
        this.procGuarantee = procGuarantee;
    }

    public String getMenuTwo() {
        return menuTwo;
    }

    public void setMenuTwo(String menuTwo) {
        this.menuTwo = menuTwo;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public List<ProcessDetailVo> getDetailVos() {
        return detailVos;
    }

    public void setDetailVos(List<ProcessDetailVo> detailVos) {
        this.detailVos = detailVos;
    }

    public List<String> getInputableFields() {
        return inputableFields;
    }

    public void setInputableFields(List<String> inputableFields) {
        this.inputableFields = inputableFields;
    }

    public List<String> getVisibilityBlock() {
        return visibilityBlock;
    }

    public void setVisibilityBlock(List<String> visibilityBlock) {
        this.visibilityBlock = visibilityBlock;
    }

    public String getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(String approveStatus) {
        this.approveStatus = approveStatus;
    }

    public boolean isDoNext() {
        return doNext;
    }

    public void setDoNext(boolean doNext) {
        this.doNext = doNext;
    }

    public List<SelectItem> getCompanySelect() {
        return companySelect;
    }

    public void setCompanySelect(List<SelectItem> companySelect) {
        this.companySelect = companySelect;
    }

    public List<SelectItem> getCurrencySelect() {
        return currencySelect;
    }

    public void setCurrencySelect(List<SelectItem> currencySelect) {
        this.currencySelect = currencySelect;
    }

    public Long getTopBankId() {
        return topBankId;
    }

    public void setTopBankId(Long topBankId) {
        this.topBankId = topBankId;
    }

    public List<SelectItem> getTopLevelSelect() {
        return topLevelSelect;
    }

    public void setTopLevelSelect(List<SelectItem> topLevelSelect) {
        this.topLevelSelect = topLevelSelect;
    }

    public List<SelectItem> getBranchSelect() {
        return branchSelect;
    }

    public void setBranchSelect(List<SelectItem> branchSelect) {
        this.branchSelect = branchSelect;
    }

    public List<SelectItem> getChildBankSelect() {
        return childBankSelect;
    }

    public void setChildBankSelect(List<SelectItem> childBankSelect) {
        this.childBankSelect = childBankSelect;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public Long getBranchSelectId() {
        return branchSelectId;
    }

    public void setBranchSelectId(Long branchSelectId) {
        this.branchSelectId = branchSelectId;
    }

    public GuaranteeVO getGuarVO() {
        return guarVO;
    }

    public void setGuarVO(GuaranteeVO guarVO) {
        this.guarVO = guarVO;
    }

    public List<SelectItem> getGuarCompanySelect() {
        return guarCompanySelect;
    }

    public void setGuarCompanySelect(List<SelectItem> guarCompanySelect) {
        this.guarCompanySelect = guarCompanySelect;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

	public String getThisStepExer() {
		return thisStepExer;
	}

	public void setThisStepExer(String thisStepExer) {
		this.thisStepExer = thisStepExer;
	}

	public Integer getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(Integer currentIndex) {
		this.currentIndex = currentIndex;
	}

	public String getCurrentTaskType() {
		return currentTaskType;
	}

	public void setCurrentTaskType(String currentTaskType) {
		this.currentTaskType = currentTaskType;
	}

	public String getIsPatch() {
		return isPatch;
	}

	public void setIsPatch(String isPatch) {
		this.isPatch = isPatch;
	}    

}
