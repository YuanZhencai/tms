package com.wcs.base.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.common.filenet.pe.Logger;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: List常用函数</p>
 * <p>Copyright © 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
public class ArrayUtil {
	
	private static Log log = LogFactory.getLog(ArrayUtil.class);
    /**
     * 判断两个列表中的内容是否一样（忽略顺序）
     * 
     * @param list1
     * @param list2
     * @return
     */
    public static boolean isListSame(List list1, List list2) {
        return list1.size() == list2.size() && list1.containsAll(list2);
    }

    /**
     * 判断两个列表中是否有交集
     * 
     * @param list1
     * @param list2
     * @return
     */
    public static boolean isListIntersect(List list1, List list2) {
        for (int i = 0; i < list1.size(); i++) {
            if (list2.contains(list1.get(i))) {
            	return true;
            }
        }
        return false;
    }

    /**
     * 判断两个数组的内容是否一样（忽略顺序）
     * 
     * @param objArr1
     * @param objArr2
     * @return
     */
    public static boolean isArraySame(Object[] objArr1, Object[] objArr2) {
        return isListSame(Arrays.asList(objArr1), Arrays.asList(objArr2));
    }

    /**
     * 判断objArr1是否包含了objArr2
     * 
     * @param objArr1
     * @param objArr2
     * @return
     */
    public static boolean isArrayContains(Object[] objArr1, Object[] objArr2) {
        return Arrays.asList(objArr1).containsAll(Arrays.asList(objArr2));
    }

    /**
     * 判断两个数组中是否有交集
     * 
     * @param objArr1
     * @param objArr2
     * @return
     */
    public static boolean isArrayIntersect(Object[] objArr1, Object[] objArr2) {
        return isListIntersect(Arrays.asList(objArr1), Arrays.asList(objArr2));
    }

    /**
     * 将所有类型的数组转换为String数组
     * 
     * @param objs
     * @return
     */
    public static String[] toStringArray(Object[] objs) {
        if (objs == null) {
        	return new String[] {};
        }
        String[] strs = new String[objs.length];
        for (int i = 0; i < objs.length; i++) {
            strs[i] = String.valueOf(objs[i]);
        }
        return strs;
    }

    /**
     * 从一个bean列表中提取某个属性的值，并用指定的分隔符拼凑成字符串返回
     * 
     * @param objList
     *            bean列表
     * @param properties
     *            需要提取的值，若需要提取多个属性，使用:分隔，如：fdId:fname
     * @param split
     *            分隔符
     * @return 提取结果，如：properties=fdId:fname，则返回值[0]为fdId拼凑的结果，[1]为fname拼凑的结果
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public static String[] joinProperty(List objList, String properties, String split) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        String[] proArr = properties.split(":");
        String[] rtnArr = new String[proArr.length];
        for (int i = 0; i < rtnArr.length; i++) {
            rtnArr[i] = "";
        }
        if (objList == null || objList.isEmpty()) {
        	return rtnArr;
        }
        for (int i = 0; i < objList.size(); i++) {
            Object obj = objList.get(i);
            if (obj == null) {
                continue;
            }
            for (int j = 0; j < rtnArr.length; j++) {
                try {
                    rtnArr[j] += split + PropertyUtils.getProperty(obj, proArr[j]);
                } catch (NestedNullException ex) {
                	log.error("从一个bean列表中提取某个属性的值，并用指定的分隔符拼凑成字符串返回异常", ex);
                }
            }
        }
        int j = split.length();
        for (int i = 0; i < rtnArr.length; i++) {
            rtnArr[i] = rtnArr[i].length() == 0 ? "" : rtnArr[i].substring(j);
        }
        return rtnArr;
    }

    /**
     * 将fromList中的元素添加到toList中，过滤重复值
     * 
     * @param fromList
     * @param toList
     */
    public static void concatTwoList(List fromList, List toList) {
        if (fromList == null || toList == null) {
        	return;
        }
        for (int i = 0; i < fromList.size(); i++) {
            Object obj = fromList.get(i);
            if (!toList.contains(obj)) {
            	toList.add(obj);
            }
        }
    }

    /**
     * 将多个对象添加到一个List中，过滤重复值
     * 
     * @param obj
     * @return
     */
    public static List<Object> concatList(Object... obj) {
        List<Object> list = new ArrayList<Object>();
        for (Object o : obj) {
            if(!list.isEmpty()){
                concatTwoList(toList(o),list);
            }else{
                list.addAll(toList(o)); 
            }            
        }
        return list;

    }
    
    
    
    /**
     * 将一个对象转换为列表
     * 
     * @param o
     * @return
     */
    public static List<Object> toList(Object o) {
        List<Object> rtnList = new ArrayList<Object>();
        if (o != null) {
            if (o instanceof Object[]) {
                rtnList.addAll(Arrays.asList((Object[]) o));
            } else if (o instanceof Collection) {
                rtnList.addAll((Collection<?>) o);
            } else {
                rtnList.add(o);
            }
        }
        return rtnList;
    }

    /**
     * 判断list是否为null或empty
     * 
     * @param list
     * @return
     */
    public static boolean isEmpty(List list) {
        if (list == null) {
        	return true;
        }
        return list.isEmpty();
    }

    /**
     * 数组转为List
     */
    public static List convertArrayToList(Object[] objects) {
        List rtnList = new ArrayList();
        for (int i = 0; i < objects.length; i++) {
            rtnList.add(objects[i]);
        }
        return rtnList;
    }
    
    public static void main(String[] args) {
        List list1 = new ArrayList();
        List list2 =  new ArrayList();
        String aa = "22";
        list1.add("1");
        list1.add("22");
        list2.add("1");
        list2.add("222");
        List allList = concatList(list1,list2,aa);
        for (Iterator iterator = allList.iterator(); iterator.hasNext();) {
            Object object = (Object) iterator.next();
            log.info("object:"+object);
        }
        
        
    }
}
