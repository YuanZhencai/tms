package com.wcs.tms.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 外债请款流程状态定义
 * */
public class ProcDebtPaymentStatus {
	public static Map<String,String> map;
	public static String endStatus="6";//流程结束节点
	static{
			map=new HashMap<String,String>();
			map.put("工厂资金岗位人员申请", "1");
			map.put("工厂财务经理审批", "2");
			map.put("工厂总经理审批", "3");
			map.put("集团项目经理审批", "4");
			map.put("财务总监审批", "5");
			map.put("集团项目经理知会新加坡", "6");
	}
	
	/**流程节点状态描述转CODE*/
	public static  String conStatus(String stepName){
		if(map!=null){
			String v=map.get(stepName);
			if(v!=null)return v.toString();
			else return "1";
		}else return "1";
	}
	
	public static void main(String args[]){
		System.out.println(ProcDebtPaymentStatus.conStatus("集团项目经理审批"));
	}
}
