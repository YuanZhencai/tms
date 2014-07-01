package com.wcs.tms.mail;

import java.io.Serializable;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:邮件POJO </p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
public class Mail implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /** 系统名称*/
    private String sys = "tmsbpm";
    /** 系统密钥Key*/
    private String key = "ASDZXC123";
    /** 默认为1为发送邮件*/
    private String type = "1";
    /** 邮件地址*/
    private String email;
    /** 电话号码*/
    private String telno;
    /** 员工工号*/
    private String pernr = "";
    /** 邮件主题*/
    private String subject;
    /** 具体邮件内容*/
    private String body;
    /** 附件*/
    private String aux = "";
    
    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelno() {
        return telno;
    }

    public void setTelno(String telno) {
        this.telno = telno;
    }

    public String getPernr() {
        return pernr;
    }

    public void setPernr(String pernr) {
        this.pernr = pernr;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAux() {
        return aux;
    }

    public void setAux(String aux) {
        this.aux = aux;
    }

}
