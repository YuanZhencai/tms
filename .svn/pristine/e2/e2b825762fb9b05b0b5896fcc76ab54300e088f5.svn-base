package com.wcs.tms.service.system.interfaces;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.MathUtil;
import com.wcs.base.util.StringUtils;
import com.wcs.base.util.des.DESEncrypt;
import com.wcs.common.consts.DictConsts;
import com.wcs.common.controller.vo.DictVO;
import com.wcs.common.service.CommonService;
import com.wcs.common.service.DictService;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.mail.Mail;
import com.wcs.tms.mail.MailService;
import com.wcs.tms.mail.SendMailService;
import com.wcs.tms.model.CompanyAccount;
import com.wcs.tms.service.process.common.ProcessUtilMapService;

/**
 * <p>Project: tms</p>
 * <p>Description:付款接口Service </p>
 * <p>Copyright © 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
public class PayService {

    private static Log log = LogFactory.getLog(PayService.class);
    @EJB
    private EntityService entityService;
    
    @PersistenceContext(unitName = "pu") 
    public EntityManager em;
    @EJB
    CommonService commonService;

    @Inject
    ProcessUtilMapService processUtilMapService;

    @Inject
    MailService mailService;
    @Inject
    SendMailService sendMailService;
    @Inject
    DictService dictService;
    // tab分隔符
    private static final String TAB_SPLIT = "\t";
    // 未加密的文件后缀
    private static final String NOT_ENCRYPTED_FILE_EXT = ".txt";
    // 加密的文件名后缀
    private static final String ENCRYPTED_FILE_EXT = ".src";
    // 下拨资金用途描述之间的分隔符
    // 付款明细表实体类和主表关联属性之间的分隔符，在ProcessConfig.xml文件中配置
    private static final String PAY_DETAIL_SPLIT = "#";

    /**
     * 
     * <p>Description:生成付款接口的Txt文件内容和文件名 </p>
     * @param processInstId 流程实例ID
     * @param processDefineName  流程定义名称
     * @param payDatetime 付款日期
     * @return new String[]{文件名，文件内容字符串}
     */
    public String[] generateTxtFileProp(String processInstId, String processDefineName,Date payDatetime) {
        StringBuffer fileName = new StringBuffer();
        StringBuffer contentStr = new StringBuffer();

        String encryptStr = "";
        String bpmId = "";
        try {
            String entityClass = ProcessXmlUtil.getProcessAttribute("className", processDefineName, "entityClass");
            String processCode = ProcessXmlUtil.getProcessAttribute("className", processDefineName, "code");
            String payDetail = ProcessXmlUtil.getProcessAttribute("className", processDefineName, "payDetail");
            String payDetailEntity = payDetail.split(PAY_DETAIL_SPLIT)[0];
            String payDetailProperty = payDetail.split(PAY_DETAIL_SPLIT)[1];

            log.info("entityClass:::" + entityClass);

            // 查询返回字段依次为：
            // sapcode公司代码,申请日期，账号，借款类型，表单类型，币别，品种1，品种2，品种3，用途描述,申请支付日期
            StringBuffer jpql = new StringBuffer("SELECT t.company.sapCode,t.createdDatetime,");
            jpql.append("t.accountNumber,  ");
            jpql.append("t.loanIden,t.formType,t.amountCu,t.varietyOne,t.varietyTwo,t.varietyThr,");
            jpql.append("t.useDesc,t.paymentDate ");
            jpql.append(" FROM ").append(entityClass).append(" t ");
            jpql.append(" JOIN FETCH t.company WHERE  t.procInstId=?1");
            List resultList = entityService.find(jpql.toString(), processInstId);

            log.info("=====根据流程实例查询业务表记录条：====" + resultList);
            if (resultList != null && !resultList.isEmpty()) {
                Object[] object = (Object[]) resultList.get(0);
                String sapCode = StringUtils.safeString((String) object[0]);
                Date createdDatetime = (Date) object[1];
                String applyDate = DateUtil.dateToDateTime(createdDatetime).toString("yyyyMMdd");
                String accountNumber = StringUtils.safeString((String) object[2]);
                String loanIden = StringUtils.safeString((String) object[3]);
                // 借款类型,从业务字典表中翻译成描述
                loanIden = getDictValueByKey(DictConsts.TMS_LOAN_IDEN_TYPE, loanIden);

                String formType = StringUtils.safeString((String) object[4]);
                formType = getDictValueByKey(DictConsts.TMS_FORM_TYPE, formType);

                String amountCu = StringUtils.safeString((String) object[5]);// 币别
                // 币别,从业务字典表中翻译成描述
                amountCu = getDictValueByKey(DictConsts.TMS_TAX_PROJECT_CURRENCY_TYPE, amountCu);

                String varietyOne = StringUtils.safeString((String) object[6]);
                // modified on 2013-1-7
                varietyOne = getDictValueByKey(getTypeCatByDictKey(varietyOne), varietyOne);

                String varietyTwo = StringUtils.safeString((String) object[7]);
                varietyTwo = getDictValueByKey(getTypeCatByDictKey(varietyTwo), varietyTwo);

                String varietyThr = StringUtils.safeString((String) object[8]);
                varietyThr = getDictValueByKey(getTypeCatByDictKey(varietyThr), varietyThr);

                String useDesc = StringUtils.safeString((String) object[9]);
                // 申请支付日期
                Date prePayDate = (Date) object[10];

                // 从付款明细表中查询 当前付款金额和付款日期
                // 一个流程实例中可能有多笔付款明细，按支付时间倒序，取第一条就是当前付款金额
                Double amount = 0d;
                String paymentDate = "";

                StringBuffer amoutJpql = new StringBuffer("select c.payFundsTotal,c.createdDatetime from ");
                amoutJpql.append(payDetailEntity).append(" c");
                amoutJpql.append(" where c.").append(payDetailProperty).append(".id in(");
                amoutJpql.append(" select d.id from ").append(entityClass).append(" d where d.procInstId=:procInstId ");
                amoutJpql.append(" ) order by c.createdDatetime desc ");

                log.info("amoutJpql:" + amoutJpql);
                
                Query query = em.createQuery(amoutJpql.toString());
                query.setParameter("procInstId", processInstId);
                
                List<Object[]> amountList = query.getResultList();
                
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String createdDatetimeFmt = simpleDateFormat.format(payDatetime);
                
                for(Object[] row : amountList){
                	Date paymentDate1 = (Date) row[1];
                	String paymentDate1Fmt = simpleDateFormat.format(paymentDate1);
                	if(paymentDate1Fmt.equals(createdDatetimeFmt)){
                		amount = (Double) row[0];
                		// 20130313新需求，if申请支付日期 >= 实际付款日期，传递申请支付日期，否则就用实际付款日期
                      if (prePayDate.after(paymentDate1)) {
                          paymentDate1 = prePayDate;
                      }
                      paymentDate = DateUtil.dateToDateTime(paymentDate1).toString("yyyyMMdd");
                      break;
                	}
                }
                
                // 20130313新需求，if申请支付日期 >= 实际付款日期，传递申请支付日期，否则就用实际付款日期

                // 从公司账户主数据表 查询开户行的BSBCode
                String bsbCode = "";
                StringBuffer jpql1 = new StringBuffer("select c from CompanyAccount c where");
                jpql1.append(" c.company.sapCode =?1 and c.account=?2");
                List<CompanyAccount> comanyAccountList = entityService.find(jpql1.toString(), sapCode, accountNumber);
                for (CompanyAccount companyAccount : comanyAccountList) {
                    bsbCode = companyAccount.getBsbCode();
                }

                log.info("=====sapCode====" + sapCode);
                log.info("=====applyDate====" + applyDate);
                log.info("=====paymentDate====" + paymentDate);
                log.info("=====accountNumber====" + accountNumber);
                log.info("=====amount====" + amount);
                log.info("=====loanIden====" + loanIden);
                log.info("=====formType====" + formType);
                log.info("=====amountCu====" + amountCu);
                log.info("=====varietyOne====" + varietyOne);
                log.info("=====varietyTwo====" + varietyTwo);
                log.info("=====varietyThr====" + varietyThr);
                log.info("=====useDesc====" + useDesc);

                // 通过seqence得到文件名编号计数器
                Long seqNext = 0l;
                List<Object> seqResult = entityService
                        .createNativeQuery("SELECT NEXTVAL FOR TMS_PAY_COUNT FROM SYSIBM.SYSDUMMY1");
                if (seqResult != null && seqResult.size() > 0) {
                    seqNext = new Long(seqResult.get(0).toString());
                }

                // 通过processCode得到流程名称
                String processCNName = "";
                List<String> processDefineList = entityService.find(
                        "SELECT p.processName FROM ProcessDefine p WHERE p.processCode = ?1", processCode);
                processCNName = processDefineList != null ? processDefineList.get(0) : "";

                // modify by liushegnbin 20130408，根据客户需求，sapCode不做截取,所以注释掉
                // 获取sapcode后两位

                // 资金用户描述字符串，格式：[流程名称];[借款类型];[品种1];[品种2];[品种3];[表单类型];[币别];[描述]
                StringBuffer descStr = new StringBuffer("");

                // 描述字符按需求，固定长度
                processCNName = formatBlankText(processCNName, 20);
                loanIden = formatBlankText(loanIden, 5);
                formType = formatBlankText(formType, 10);
                varietyOne = formatBlankText(varietyOne, 10);
                varietyTwo = formatBlankText(varietyTwo, 10);
                varietyThr = formatBlankText(varietyThr, 10);
                amountCu = formatBlankText(amountCu, 4);
                useDesc = formatBlankText(useDesc, 30);

                // modify by liushengbin 20130227 根据新需求，去掉;分隔符
                descStr.append(processCNName).append(loanIden);
                descStr.append(varietyOne).append(varietyTwo);
                descStr.append(varietyThr).append(formType);
                descStr.append(amountCu).append(useDesc);

                // 拼装txt文件内容 格式：流水号编号+公司+申请日期+付款日期+开户行+账号+金额+资金用户描述
                // 流水号编号= 接口数据来源（BPM） + 接口编号（目前都是01） + sapcode + 流程编号+流程序列号

                // 根据FN流程实例id，获取流程实例业务编号
                String processBusiCode = processUtilMapService.getTmsIdByFnId(processInstId);
                // modify by liushegnbin 20130408，根据客户需求变更，去掉公司代码，流程实例编号和子流程编号中间加个下划线
                // 新需求内容：【BPM + 01 + 公司代码 （去掉） + 流程实例编号（11位，永远都是11位） + 下划线 + 子流程编号（四位）】
                bpmId = "BPM01" + processBusiCode + "_" + seqNext;
                contentStr.append("BPM").append("01")
                        .append(processBusiCode).append("_").append(seqNext).append(TAB_SPLIT);
                contentStr.append(sapCode).append(TAB_SPLIT);
                contentStr.append(applyDate).append(TAB_SPLIT);
                contentStr.append(paymentDate).append(TAB_SPLIT);
                contentStr.append(bsbCode).append(TAB_SPLIT);
                contentStr.append(accountNumber).append(TAB_SPLIT);
                // modify by liushengbin 20130111金额单位是W，改成传递给接口数据中，多乘以10000
                contentStr.append(MathUtil.roundHalfUp(amount * 10000,2)).append(TAB_SPLIT);
                contentStr.append(descStr.toString());

                // 拼装文件名
                // 文件名命名规则：BPM+接口编号(01)+SAP公司代码后2位+BPM流程编号+序列号

                // modify by liushegnbin 20130408，根据客户需求变更，去掉公司代码，流程实例编号和子流程编号中间加个下划线
                // 新需求内容：文件名规则：【BPM + 01 + 公司代码 （去掉） + 流程实例编号（11位，永远都是11位） + 下划线 + 子流程编号（四位）】
                fileName.append("BPM").append("01");
                // .append(sapCode);
                fileName.append(processUtilMapService.getTmsIdByFnId(processInstId)).append("_").append(seqNext);
                fileName.append(NOT_ENCRYPTED_FILE_EXT);
                fileName.append(ENCRYPTED_FILE_EXT);

                // 2013-07-16 如果付款文件中，缺少付款金额和付款日期，发送邮件通知给运维人员
                if (MathUtil.isEmptyOrNull(paymentDate)) {
                    log.info("生成付款文件，有问题：缺少付款金额和付款日期!");
                    List<Mail> mails = mailService.getErrorInfo2AdminEmail(processInstId, processDefineName, "losePayAmount",
                            "<br>加密文件名是：" + fileName);
                    sendMailService.send(mails);
                }
            }
            // txt文件内容加密
            DESEncrypt desEncrypt;

            desEncrypt = new DESEncrypt();
            encryptStr = desEncrypt.encrypt(contentStr.toString());
        } catch (Exception e) {
            log.error("准备付款指令加密内容时出错：", e);
        }
        return new String[] { fileName.toString(), encryptStr , bpmId};
    }

    /**
     * 根据农产品品种确定其类型cat
     * @return
     */
    public String getTypeCatByDictKey(String codeKey) {
        /**
         * sonar测试 codeKey != "" && codeKey != null 改为 codeKey != null && !"".equals(codeKey)
         */
        if (codeKey != null && !"".equals(codeKey)) {
            // 根据cat和key模糊查询出DictVO数据
            List<DictVO> dictVOs = dictService.searchData(DictConsts.TMS_FARM_PRODUCE_VARIETY_TYPE, codeKey, "", "", "");
            String cat = dictVOs.get(0).getCodeCat();
            // 确定品种类型cat
            return cat;
        } else {
            return "";
        }
    }

    /**
     * 
     * <p>Description:根据code_cat,code_key获取code_value </p>
     * @param code_cat
     * @param code_key
     * @return
     */
    private String getDictValueByKey(String code_cat, String code_key) {
        String retVal = code_key;
        if (!StringUtils.isBlankOrNull(code_key)) {
            String dict_key = code_cat + "." + code_key + ".zh_CN";
            retVal = StringUtils.safeString(commonService.getValueByDictCatKey(dict_key.replace(".", "_")));
        }
        return retVal;
    }

    /**
     * 
     * <p>Description:把字符串估计为特定的长度,不足补空格，多余的截取掉 </p>
     * @param str
     * @param length
     * @return
     */
    private String addBlankAdjuct(String str, int length) {
        StringBuilder buf = new StringBuilder(StringUtils.safeString(str));
        buf.setLength(length);
        return buf.toString();
    }

    /**
     * 
     * <p>Description:把字符串估计为特定的长度,不足补空格，多余的截取掉 </p>
     * @param str
     * @param length
     * @return 格式化后的字符串
     */
    public String formatBlankText(String targetStr, int limitLength) {
        targetStr = StringUtils.safeString(targetStr);
        int curLength = targetStr.length();
        if (targetStr != null && curLength > limitLength) { 
            targetStr = subStringByte(targetStr, limitLength);
        }
        String newString = "";
        int cutLength = limitLength - targetStr.length();
        for (int i = 0; i < cutLength; i++) {
            newString += " ";
        }
        return targetStr + newString;
    }

    public String subStringByte(String targetStr, int limitLength) {
        while (targetStr.length() > limitLength) {
            targetStr = targetStr.substring(0, targetStr.length() - 1);
        }
        return targetStr;
    }

}
