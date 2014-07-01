package com.wcs.base.config.loader;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

import com.wcs.base.util.StringUtils;
import com.wcs.common.filenet.env.Configuration;
import com.wcs.common.util.MessageUtils;

/**
 * <p>Project: wcsoa</p>
 * <p>Description: 应用启动加载类</p>
 * <p>1、初始化全局配置文件</p>
 * <p>Copyright © 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
public class ConfigListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext sc = event.getServletContext();
        //设置应用上下文
        MessageUtils.CONTEXT_PATH = sc.getContextPath();
        sc.log("ConfigListener init!");
        String tmsConfig = sc.getInitParameter("filenet.config");
        sc.log("current config is============================:" + tmsConfig);
        if (!StringUtils.isBlankOrNull(tmsConfig)) {
            // 设置当前环境使用的配置文件
            Configuration.BUNDLE_NAME = tmsConfig;
        }
    }

}
