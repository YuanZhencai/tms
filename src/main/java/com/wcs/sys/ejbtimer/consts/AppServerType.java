package com.wcs.sys.ejbtimer.consts;
/**
 * <p>Project: wcsoa</p>
 * <p>Description: 应用服务器常量类</p>
 * <p>Copyright © 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
public enum AppServerType {

    WAS("WAS"),
    GLASSFISH("GlassFish"),
    TOMCAT("Tomcat"),
    JBOSS("Jboss");
    
    public String value;
    private AppServerType(String value) {
       this.value = value;
    }
}


