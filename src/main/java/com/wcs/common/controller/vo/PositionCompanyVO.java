package com.wcs.common.controller.vo;

import java.io.Serializable;

import com.wcs.common.model.Companymstr;
import com.wcs.common.model.O;
import com.wcs.common.model.Positionorg;

/**
 * Project: tih
 * Description: position company vo.
 * Copyright (c) 2012 Wilmar Consultancy Services
 * All Rights Reserved.
 * @author <a href="mailto:guanluyong@wcs-global.com">Mr.Guan</a>
 */
@SuppressWarnings("serial")
public class PositionCompanyVO extends Companymstr implements Serializable {

    private String bukrs;

    private String stext;
    
    private Long positionorgId;
    
    public PositionCompanyVO() { }
    
    public PositionCompanyVO(Companymstr c, O o, Positionorg po) {
        this.bukrs = o.getBukrs();
        this.stext = o.getStext();
        this.positionorgId = po == null? null : po.getId();
        
        setId(c.getId());
        setAddress(c.getAddress());
        setCreatedBy(c.getCreatedBy());
        setCreatedDatetime(c.getCreatedDatetime());
        setDefunctInd(c.getDefunctInd());
        setDesc(c.getDesc());
        setOid(c.getOid());
        setTaxauthority(c.getTaxauthority());
        setTelphone(c.getTelphone());
        setType(c.getType());
        setUpdatedBy(c.getUpdatedBy());
        setUpdatedDatetime(c.getUpdatedDatetime());
        setUsercompanies(c.getUsercompanies());
        setZipcode(c.getZipcode());
    }

    public String getBukrs() {
        return bukrs;
    }

    public void setBukrs(String bukrs) {
        this.bukrs = bukrs;
    }

    public String getStext() {
        return stext;
    }

    public void setStext(String stext) {
        this.stext = stext;
    }

    public Long getPositionorgId() {
        return positionorgId;
    }

    public void setPositionorgId(Long positionorgId) {
        this.positionorgId = positionorgId;
    }

}
