package com.wcs.tms.service.process.bankcreditadjust.entity;

import javax.faces.model.SelectItem;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:授信额度提供方pojo</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
public class Provider {

    // 提供方id(实为授信Id)
    private Long providerId;
    // 提供方名称(公司Name)
    private String providerName;
    // 提供方授信银行名称
    private String providerBankName;
    // 确认授信调剂金额
    private Double creditReduce;

    // 提供方的授信金额
    private Double creditLine;

    // 9.20 add by liushengbin 提供方的剩余的授信金额
    private Double creditLineLeft;

    // 下拉列表
    private SelectItem providerSelectItem;

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public String getProviderBankName() {
        return providerBankName;
    }

    public void setProviderBankName(String providerBankName) {
        this.providerBankName = providerBankName;
    }

    public Double getCreditReduce() {
        return creditReduce;
    }

    public void setCreditReduce(Double creditReduce) {
        this.creditReduce = creditReduce;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public SelectItem getProviderSelectItem() {
        return providerSelectItem;
    }

    public void setProviderSelectItem(SelectItem providerSelectItem) {
        this.providerSelectItem = providerSelectItem;
    }

    public Double getCreditLine() {
        return creditLine;
    }

    public void setCreditLine(Double creditLine) {
        this.creditLine = creditLine;
    }

    public Double getCreditLineLeft() {
        return creditLineLeft;
    }

    public void setCreditLineLeft(Double creditLineLeft) {
        this.creditLineLeft = creditLineLeft;
    }

}
