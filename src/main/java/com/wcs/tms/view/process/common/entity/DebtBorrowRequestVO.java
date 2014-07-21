/** * RegicapitalMofifyVO.java 
* Created on 2014-6-26 下午4:37:24 
*/

package com.wcs.tms.view.process.common.entity;

import java.io.Serializable;
import java.util.List;

import com.wcs.common.controller.helper.IdModel;

/** 
 * <p>Project: tms</p> 
 * <p>Title: RegicapitalMofifyVO.java</p> 
 * <p>Description: </p> 
 * <p>Copyright (c) 2014 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:guyanyu@wcs-global.com">Gu yanyu</a>
 */

public class DebtBorrowRequestVO extends IdModel implements Serializable {
    
    private static final long serialVersionUID = 1L;
    // 申请日期
    private String applyDate;
    // 流程编号
    private String processNo;
    // 公司名称
    private String companyName;
    // 流程状态
    private String processStatus;
    // 币别
    private String currency;
    // 申请金额
    private String requestMoney;
    // 是否到账
    private String toTheAccount; 
    
    /** 外债合同_主数据DEBT_CONTRACT**/
    // 外债合同 = 合同编号+出资方+开始-结束日期+利率
    private String debtContract; 
    // 外债合同金额
    private String debtContractFunds;
    // 未请款金额
    private String noAppliedFunds;
    // 请款金额
    private String applyFunds;
    // 到账金额
    private String receivedFunds;
    /**
     * @return the applyDate
     */
    public String getApplyDate() {
        return applyDate;
    }
    /**
     * @param applyDate the applyDate to set
     */
    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }
    /**
     * @return the processNo
     */
    public String getProcessNo() {
        return processNo;
    }
    /**
     * @param processNo the processNo to set
     */
    public void setProcessNo(String processNo) {
        this.processNo = processNo;
    }
    /**
     * @return the companyName
     */
    public String getCompanyName() {
        return companyName;
    }
    /**
     * @param companyName the companyName to set
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    /**
     * @return the processStatus
     */
    public String getProcessStatus() {
        return processStatus;
    }
    /**
     * @param processStatus the processStatus to set
     */
    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }
    /**
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }
    /**
     * @param currency the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    /**
     * @return the requestMoney
     */
    public String getRequestMoney() {
        return requestMoney;
    }
    /**
     * @param requestMoney the requestMoney to set
     */
    public void setRequestMoney(String requestMoney) {
        this.requestMoney = requestMoney;
    }
    /**
     * @return the toTheAccount
     */
    public String getToTheAccount() {
        return toTheAccount;
    }
    /**
     * @param toTheAccount the toTheAccount to set
     */
    public void setToTheAccount(String toTheAccount) {
        this.toTheAccount = toTheAccount;
    }
    /**
     * @return the debtContract
     */
    public String getDebtContract() {
        return debtContract;
    }
    /**
     * @param debtContract the debtContract to set
     */
    public void setDebtContract(String debtContract) {
        this.debtContract = debtContract;
    }
    /**
     * @return the debtContractFunds
     */
    public String getDebtContractFunds() {
        return debtContractFunds;
    }
    /**
     * @param debtContractFunds the debtContractFunds to set
     */
    public void setDebtContractFunds(String debtContractFunds) {
        this.debtContractFunds = debtContractFunds;
    }
    /**
     * @return the noAppliedFunds
     */
    public String getNoAppliedFunds() {
        return noAppliedFunds;
    }
    /**
     * @param noAppliedFunds the noAppliedFunds to set
     */
    public void setNoAppliedFunds(String noAppliedFunds) {
        this.noAppliedFunds = noAppliedFunds;
    }
    /**
     * @return the applyFunds
     */
    public String getApplyFunds() {
        return applyFunds;
    }
    /**
     * @param applyFunds the applyFunds to set
     */
    public void setApplyFunds(String applyFunds) {
        this.applyFunds = applyFunds;
    }
    /**
     * @return the receivedFunds
     */
    public String getReceivedFunds() {
        return receivedFunds;
    }
    /**
     * @param receivedFunds the receivedFunds to set
     */
    public void setReceivedFunds(String receivedFunds) {
        this.receivedFunds = receivedFunds;
    }

    
}
