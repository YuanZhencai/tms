package com.wcs.sys.ejbtimer.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * <p>Project: wcsoa</p>
 * <p>Description: </p>
 * <p>Copyright © 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
public class ClasspathUtil {
	private static Logger logger = Logger.getLogger(ClasspathUtil.class);
	
	private ClasspathUtil(){
		
	}
    /** 
     * @param args 
     * @throws URISyntaxException 
     */  
    public static void main(String[] args) throws URISyntaxException {  
  
        URL resource = ClasspathUtil.class.getResource("/");
        URI uri = resource.toURI();
        String rootPath = new File(uri).getPath();
        logger.info("rootPath:"+rootPath);
        // 一般推荐用此方法  
        // 获取当前ClassPath的绝对URI路径  
        
        
        
        logger.info("Thread.currentThread().getContextClassLoader():");  
        logger.info(Thread.currentThread().getContextClassLoader()  
                .getResource(""));  
        
        logger.info(Thread.currentThread().getContextClassLoader().getResource("/"));
  
        logger.info("ClasspathUtil.class.getResource:");  
        // 获取当前类文件的URI目录  
        logger.info(ClasspathUtil.class.getResource(""));  
  
        // 获取当前的ClassPath的绝对URI路径。  
        logger.info(ClasspathUtil.class.getResource("/"));  
  
  
        logger.info("ClasspathUtil.class.getClassLoader().getResource:");  
  
        // 获取当前ClassPath的绝对URI路径  
        logger.info(ClasspathUtil.class.getClassLoader().getResource(""));  
  
  
        // 获取当前ClassPath的绝对URI路径  
        logger.info("ClassLoader.getSystemResource:");  
  
        logger.info(ClassLoader.getSystemResource(""));  
  
        logger.info("System.getProperty:");  
  
        // 对于一般项目，这是项目的根路径。对于JavaEE服务器，这可能是服务器的某个路径。  
        // 这个并没有统一的规范！所以，绝对不要使用“相对于当前用户目录的相对路径”。  
        logger.info(System.getProperty("user.dir"));  
  
    }  
}


