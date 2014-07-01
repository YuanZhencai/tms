package com.wcs.tms.conf.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Element;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:流程xml公共类</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
public class ProcessXmlUtil {
    public static List<Element> processes = ProcessParseXmlUtil.getProcesses();

    private static Log log = LogFactory.getLog(ProcessXmlUtil.class);
    /**
     * 通过指定的唯一流程属性得到另一指定的属性值
     * @param byAttrName
     * @param byAttrValue
     * @param getAttrName
     * @return
     */
    public static String getProcessAttribute(String byAttrName, String byAttrValue, String getAttrName) {
        String getAttrValue = "";
        for (Element p : processes) {
            if (p.attribute(byAttrName) != null && byAttrValue.equals(p.attribute(byAttrName).getText())) {
                getAttrValue = p.attribute(getAttrName)==null ? "" : p.attribute(getAttrName).getText();
            }
        }
        return getAttrValue;
    }

    /**
     * 得到所有流程的某个属性
     * @param getAttrName
     * @return
     */
    public static List<String> getAllProcessAttribut(String getAttrName) {
        List<String> attris = new ArrayList<String>();
        for (Element p : processes) {
            if (p.attribute(getAttrName) != null) {
                attris.add(p.attribute(getAttrName).getText());
            }
        }
        return attris;
    }

    /**
     * 得到步骤所有的参数
     * @param className
     * @param stepName
     * @return
     */
    public static List<String> getParasByProcessStep(String className, String stepName) {
        List<String> paras = new ArrayList<String>();
        Element process = null;
        for (Element p : processes) {
            if (p.attribute("className") != null && className.equals(p.attribute("className").getText())) {
                process = p;
                break;
            }
        }
        if (process != null) {
            List<Element> steps = process.selectNodes("step");
            Element step = null;
            for (Element s : steps) {
                if (s.attribute("name") != null && stepName.equals(s.attribute("name").getText())) {
                    step = s;
                    break;
                }
            }
            if (step != null) {
                List<Element> parae = step.selectNodes("parameter");
                for (Element p : parae) {
                    paras.add(p.getText());
                    log.info(p.getText());
                }
            }
        }
        return paras;
    }

    /**
     * 
     * <p>Description: 查找当前流程当前节点的下一个节点元素</p>
     * @param className
     * @param stepName
     * @return
     */
    public static String findNextStep(String className, String stepName) {
        int index = 0;
        String step = "";
        Element process = null;
        for (Element p : processes) {
            if (p.attribute("className") != null && className.equals(p.attribute("className").getText())) {
                process = p;
                break;
            }
        }
        if (process != null) {
            List<Element> steps = process.selectNodes("step");
            int size = steps.size();
            for (int i = 0; i < size; i++) {
                Element s = steps.get(i);
                if (s.attribute("name") != null && stepName.equals(s.attribute("name").getText())) {
                    index = i + 1;
                    break;
                }
            }
            log.info(index + "   " + size);
            if (index != 0 && index != size && steps.get(index).attribute("name") != null) {
                step = steps.get(index).attribute("name").getText();
            }
        }
        return step;
    }

    /**
     * 得到可输入的字段列表
     * @param xmlid
     * @param stepName
     * @param dataFieldName
     * @return
     */
    public static List<String> getInputableDatas(String xmlid, String stepName) {
        List<String> dataFields = new ArrayList<String>();
        Element process = null;
        for (Element p : processes) {
            if (p.attribute("id") != null && xmlid.equals(p.attribute("id").getText())) {
                process = p;
                break;
            }
        }
        if (process != null) {
            List<Element> steps = process.selectNodes("step");
            Element step = null;
            for (Element s : steps) {
                if (s.attribute("name") != null && stepName.equals(s.attribute("name").getText())) {
                    step = s;
                    break;
                }
            }
            if (step != null) {
                Attribute a = step.attribute("inputable");
                if (a == null) { 
                	return dataFields; 
                }
                String dataFieldStr = step.attribute("inputable").getText().trim();
                String[] fields = dataFieldStr.split(",");
                for (String f : fields) {
                    dataFields.add(f.trim());
                }
            }
        }
        return dataFields;
    }
    
    /**
     * 得到界面不显示字段列表
     * @param xmlid
     * @param stepName
     * @param dataFieldName
     * @return
     */
    public static List<String> getNotVisiblePropertyDatas(String xmlid, String stepName) {
        List<String> dataFields = new ArrayList<String>();
        Element process = null;
        for (Element p : processes) {
            if (p.attribute("id") != null && xmlid.equals(p.attribute("id").getText())) {
                process = p;
                break;
            }
        }
        if (process != null) {
            List<Element> steps = process.selectNodes("step");
            Element step = null;
            for (Element s : steps) {
                if (s.attribute("name") != null && stepName.equals(s.attribute("name").getText())) {
                    step = s;
                    break;
                }
            }
            if (step != null) {
                Attribute a = step.attribute("notVisibleProperty");
                if (a == null) { 
                	return dataFields; 
                }
                String dataFieldStr = step.attribute("notVisibleProperty").getText().trim();
                String[] fields = dataFieldStr.split(",");
                for (String f : fields) {
                    dataFields.add(f.trim());
                }
            }
        }
        return dataFields;
    }
    
    /**
     * 
     * <p>Description: 得到流程节点元素属性值</p>
     * @param id 流程唯一标示属性
     * @param idValue 唯一标示属性值 
     * @param stepName 节点名称
     * @param stepProperty 节点属性
     * @return
     */
    public static String findStepProperty(String id, String idValue, String stepName, String stepProperty) {
        String dataFieldStr = null;
        Element process = null;
        for (Element p : processes) {
            if (p.attribute(id) != null && idValue.equals(p.attribute(id).getText())) {
                process = p;
                break;
            }
        }
        if (process != null) {
            List<Element> steps = process.selectNodes("step");
            Element step = null;
            for (Element s : steps) {
                if (s.attribute("name") != null && null != stepName && stepName.equals(s.attribute("name").getText())) {
                    step = s;
                    break;
                }
            }
            if (step != null) {
                Attribute a = step.attribute(stepProperty);
                if (a == null) { 
                	return dataFieldStr; 
                }
                dataFieldStr = step.attribute(stepProperty).getText();
            }
        }
        return dataFieldStr;
    }
    
    /**
     * 生产或贸易总头寸申请审批流程特殊处理(获取当前用户所管专项品种)
     * add on 2012-9-11 by yanchangjing
     * @param id 流程唯一标示属性
     * @param idValue 唯一标示属性值 
     * @param currentUserName 当前用户名
     * @return
     */
	public static List<String> getVarityByUsername(String id,
			String idValue, String currentUserName) {
		 Element process = null;
		 List<Element> users = null;
		 List<String> varities = new ArrayList<String>();
	      for (Element p : processes) {
	          if (p.attribute(id) != null && idValue.equals(p.attribute(id).getText())) {
	              process = (Element) p.selectSingleNode("vartymajordomo");
	               break;
	           }
	       }
	      if(process != null){
	    	  users = process.selectNodes("username");
	    	  for(Element u : users){
	    		  if (u.attribute("name") != null && currentUserName.equals(u.attribute("name").getText())) {
		              List<Element> vs = u.selectNodes("varity");
		              for(Element v : vs){
		            	  varities.add(v.getText());
		              }
		           } 
	    		  break;
	    	  }
	      }
	    	  return varities;
	}
	
}
