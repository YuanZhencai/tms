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

public class RegicapitalMofifyVO extends IdModel implements Serializable {
    
    private static final long serialVersionUID = 1L;
    // 申请日期
    private String applyDate;
    // 流程编号
    private String processNo;
    // 公司名称
    private String companyName;
    // 币别
    private String companyCu;
    // 投资金额
    private Double companyAmount;
    // 流程状态
    private String processStatus;
    
    private List<RegicapitalMofifyShareholderVO> shareholders;
    
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
     * @return the companyCu
     */
    public String getCompanyCu() {
        return companyCu;
    }
    /**
     * @param companyCu the companyCu to set
     */
    public void setCompanyCu(String companyCu) {
        this.companyCu = companyCu;
    }
    /**
     * @return the companyAmount
     */
    public Double getCompanyAmount() {
        return companyAmount;
    }
    /**
     * @param companyAmount the companyAmount to set
     */
    public void setCompanyAmount(Double companyAmount) {
        this.companyAmount = companyAmount;
    }
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
     * @return the shareholders
     */
    public List<RegicapitalMofifyShareholderVO> getShareholders() {
        return shareholders;
    }
    /**
     * @param shareholders the shareholders to set
     */
    public void setShareholders(List<RegicapitalMofifyShareholderVO> shareholders) {
        this.shareholders = shareholders;
    }
    
}
