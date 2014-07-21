/** * RegicapitalMofifyShareholderVO.java 
* Created on 2014-6-26 下午4:51:53 
*/

package com.wcs.tms.view.process.common.entity;

import java.io.Serializable;
import java.util.List;

import com.wcs.common.controller.helper.IdModel;

/** 
 * <p>Project: tms</p> 
 * <p>Title: RegicapitalMofifyShareholderVO.java</p> 
 * <p>Description: </p> 
 * <p>Copyright (c) 2014 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yourname@wcs-global.com">Your Name</a>
 */

public class RegicapitalMofifyShareholderVO extends IdModel implements Serializable {
    
    private static final long serialVersionUID = 1L;
    // 股东名称
    private String name;
    // 状态
    private String status;
    // 币别
    private List<String> currencys;
    // 注册金额
    private List<String> fondsTotals;
    // 到位金额
    private List<String> fondsInPlaces;
    // 未到位金额
    private List<String> fondsNotInPlaces; // 未到位金额 = 注册金额 - 已到位金额
    // 股权比例
    private List<String> equityPercs;
    // 股权关联
    private List<String> isEquityRelas;
    // 实际股权比例
    private List<String> actualEquityPercs; // 实际股权比例 = 股权比例 * 关联比例
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    /**
     * @return the isEquityRelas
     */
    public List<String> getIsEquityRelas() {
        return isEquityRelas;
    }
    /**
     * @param isEquityRelas the isEquityRelas to set
     */
    public void setIsEquityRelas(List<String> isEquityRelas) {
        this.isEquityRelas = isEquityRelas;
    }
    /**
     * @return the actualEquityPercs
     */
    public List<String> getActualEquityPercs() {
        return actualEquityPercs;
    }
    /**
     * @param actualEquityPercs the actualEquityPercs to set
     */
    public void setActualEquityPercs(List<String> actualEquityPercs) {
        this.actualEquityPercs = actualEquityPercs;
    }
    /**
     * @return the currencys
     */
    public List<String> getCurrencys() {
        return currencys;
    }
    /**
     * @param currencys the currencys to set
     */
    public void setCurrencys(List<String> currencys) {
        this.currencys = currencys;
    }
    /**
     * @return the fondsTotals
     */
    public List<String> getFondsTotals() {
        return fondsTotals;
    }
    /**
     * @param fondsTotals the fondsTotals to set
     */
    public void setFondsTotals(List<String> fondsTotals) {
        this.fondsTotals = fondsTotals;
    }
    /**
     * @return the fondsInPlaces
     */
    public List<String> getFondsInPlaces() {
        return fondsInPlaces;
    }
    /**
     * @param fondsInPlaces the fondsInPlaces to set
     */
    public void setFondsInPlaces(List<String> fondsInPlaces) {
        this.fondsInPlaces = fondsInPlaces;
    }
    /**
     * @return the fondsNotInPlaces
     */
    public List<String> getFondsNotInPlaces() {
        return fondsNotInPlaces;
    }
    /**
     * @param fondsNotInPlaces the fondsNotInPlaces to set
     */
    public void setFondsNotInPlaces(List<String> fondsNotInPlaces) {
        this.fondsNotInPlaces = fondsNotInPlaces;
    }
    /**
     * @return the equityPercs
     */
    public List<String> getEquityPercs() {
        return equityPercs;
    }
    /**
     * @param equityPercs the equityPercs to set
     */
    public void setEquityPercs(List<String> equityPercs) {
        this.equityPercs = equityPercs;
    }
    
}
