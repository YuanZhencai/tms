package com.wcs.tms.conf.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:流程xml读取初始化</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Startup
@Singleton
public class ProcessParseXmlUtil {
    private static XMLConfiguration config;

    private static final String CONFIG_FILE_PATH = "filenetenv/ProcessConfig.xml";

    private static Document document;

    private static List<Element> processes;
    
    private static Log log = LogFactory.getLog(ProcessParseXmlUtil.class);

    @PostConstruct
    public void initXml() {
        initParseXml();
    }

    /**
     * 流程xml读取初始化
     */
	private static void initParseXml() {
        try {
            config = new XMLConfiguration(CONFIG_FILE_PATH);
            File f = config.getFile();
            SAXReader saxReader = new SAXReader();
            Document d = saxReader.read(new InputStreamReader(new FileInputStream(f.getAbsolutePath()), "utf-8"));
            document = DocumentHelper.parseText(d.asXML());
            processes = ProcessParseXmlUtil.document.selectNodes("FileNet/PE-Process/process");
            log.info("流程配置文件" + CONFIG_FILE_PATH + "读取成功！");
        } catch (UnsupportedEncodingException e) {
            log.error("initParseXml方法 流程xml读取初始化发生异常" , e);
        } catch (FileNotFoundException e) {
        	log.error("initParseXml方法 流程xml读取初始化发生异常" , e);
        } catch (DocumentException e) {
        	log.error("initParseXml方法 流程xml读取初始化发生异常" , e);
        } catch (ConfigurationException e) {
        	log.error("initParseXml方法 流程xml读取初始化发生异常" , e);
        }
    }

    public static List<Element> getProcesses() {
        return processes;
    }

}
