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

public class RegicapitalRequestVO extends IdModel implements Serializable {
    
    private static final long serialVersionUID = 1L;
    // 申请日期
    private String applyDate;
    // 流程编号
    private String processNo;
    private String procInstId;
    // 公司名称
    private String companyName;
    // 流程状态
    private String processStatus;
    // 股东名称
    private String shareHolderName;
    // 币别
    private String currency;
    // 注册金额
    private String fondsTotal;
    // 到位金额
    private String fondsInPlace;
    // 未到位金额
    private String fondsNotInPlace;
    // 股权比例
    private String equityPerc;
    // 申请金额
    private String requestMoney;
    // 申请币别
    private String requestCurrency;
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
     * @return the shareHolderName
     */
    public String getShareHolderName() {
        return shareHolderName;
    }
    /**
     * @param shareHolderName the shareHolderName to set
     */
    public void setShareHolderName(String shareHolderName) {
        this.shareHolderName = shareHolderName;
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
     * @return the fondsTotal
     */
    public String getFondsTotal() {
        return fondsTotal;
    }
    /**
     * @param fondsTotal the fondsTotal to set
     */
    public void setFondsTotal(String fondsTotal) {
        this.fondsTotal = fondsTotal;
    }
    /**
     * @return the fondsInPlace
     */
    public String getFondsInPlace() {
        return fondsInPlace;
    }
    /**
     * @param fondsInPlace the fondsInPlace to set
     */
    public void setFondsInPlace(String fondsInPlace) {
        this.fondsInPlace = fondsInPlace;
    }
    /**
     * @return the fondsNotInPlace
     */
    public String getFondsNotInPlace() {
        return fondsNotInPlace;
    }
    /**
     * @param fondsNotInPlace the fondsNotInPlace to set
     */
    public void setFondsNotInPlace(String fondsNotInPlace) {
        this.fondsNotInPlace = fondsNotInPlace;
    }
    /**
     * @return the equityPerc
     */
    public String getEquityPerc() {
        return equityPerc;
    }
    /**
     * @param equityPerc the equityPerc to set
     */
    public void setEquityPerc(String equityPerc) {
        this.equityPerc = equityPerc;
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
     * @return the requestCurrency
     */
    public String getRequestCurrency() {
        return requestCurrency;
    }
    /**
     * @param requestCurrency the requestCurrency to set
     */
    public void setRequestCurrency(String requestCurrency) {
        this.requestCurrency = requestCurrency;
    }
	public String getProcInstId() {
		return procInstId;
	}
	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}
    
}
