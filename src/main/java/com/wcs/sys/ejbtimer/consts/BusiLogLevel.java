package com.wcs.sys.ejbtimer.consts;
/**
 * <p>Project: wcsoa</p>
 * <p>Description:业务日志级别 </p>
 * <p>Copyright © 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
public enum BusiLogLevel {
    INFO("INFO"),
    WARN("WARN"),
    ERROR("ERROR");
    
    public String value;
    private BusiLogLevel(String value) {
       this.value = value;
    }
}


