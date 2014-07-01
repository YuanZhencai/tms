package com.wcs.tms.view.process.common;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wcs.tms.view.process.common.entity.QueueInfo;

public class QueueDefine {
    private static final String TMS_Requester = "申请人";
    private static final String TMS_Fac_Finance = "工厂财务经理";
    private static final String TMS_Fac_Finance_Sp2 = "工厂财务经理-授信";
    private static final String TMS_Fac_Manager = "工厂总经理 ";
    private static final String TMS_Fac_Fund_Pos = "工厂资金岗位人员";
    private static final String TMS_Fac_Account = "工厂成本会计";
    private static final String TMS_Com_Trader = "公司贸易内勤";
    private static final String TMS_Com_Trader_M = "公司贸易经理";
    private static final String TMS_Cop_Gv2director = "集团品种转向总监";
    private static final String TMS_Cop_Account = "集团会计部门人员";
    private static final String TMS_Cop_Fund_Dep_1 = "集团资金部门人员-授信组";
    private static final String TMS_Cop_Fund_Dep_2 = "集团资金部门人员-担保组";
    private static final String TMS_Cop_Fund_Dep_3 = "集团资金部门人员-理财组";
    private static final String TMS_Cop_Fund_Dep_4 = "集团资金部门人员-外债申请组";
    private static final String TMS_Cop_Fund_Dep_5 = "集团资金部门人员-银行账户申请组";
    private static final String TMS_Cop_Fund_Dep_M = "集团资金部门经理";
    private static final String TMS_Cop_Fund_Dep_M_Ic = "集团资金部门经理-人民币";
    private static final String TMS_Cop_Fund_Dep_M_Fc = "集团资金部门经理-外币";
    private static final String TMS_Cop_Fund_Dep_M_Sx = "集团资金部门经理-授信";
    private static final String TMS_Cop_CFO2 = "集团资金总监";
    private static final String TMS_Cop_Planner = "集团资金计划员";
    private static final String TMS_Cop_PM = "集团项目经理";
    private static final String TMS_Cop_CFO = "集团财务总监 ";
    private static final String TMS_Singapore = "新加坡领导 ";
    private static final String TMS_Cop_CFOs = "集团资金总监或集团财务总监 ";
    private static final String TMS_Cop_Invest_Finance_M = "集团投资理财经理";
    private static final String TMS_Cop_Transit_Trade_P = "集团转口贸易负责人";
    private static final String TMS_Cop_Ordinary_Trade_P = "集团一般贸易负责人";
    


    static List<QueueInfo> queuelist = Lists.newArrayList();

    /**
     * 
     * <p>Description: 得到队列对象集合</p>
     * @return
     */
    public static List<QueueInfo> findQueueList() {
        if (queuelist.isEmpty()) {
            Map<String, String> queue = queueMap();
            for (Map.Entry<String, String> en : queue.entrySet()) {
                QueueInfo queueInfo = new QueueInfo();
                queueInfo.setQueueName_EN(en.getKey());
                queueInfo.setQueueName_CN(en.getValue());
                queuelist.add(queueInfo);
            }
        }
        return queuelist;
    }

    public static Map<String, String> queueMap() {
        Map<String, String> map = Maps.newLinkedHashMap();
        map.put("TMS_Requester", TMS_Requester);
        map.put("TMS_Fac_Finance", TMS_Fac_Finance);
        map.put("TMS_Fac_Finance_Sp2", TMS_Fac_Finance_Sp2);
        map.put("TMS_Fac_Manager", TMS_Fac_Manager);
        map.put("TMS_Fac_Fund_Pos", TMS_Fac_Fund_Pos);
        map.put("TMS_Fac_Account", TMS_Fac_Account);
        map.put("TMS_Com_Trader", TMS_Com_Trader);
        map.put("TMS_Com_Trader_M", TMS_Com_Trader_M);
        map.put("TMS_Cop_Gv2director", TMS_Cop_Gv2director);
        map.put("TMS_Cop_Account", TMS_Cop_Account);
        map.put("TMS_Cop_Fund_Dep_1", TMS_Cop_Fund_Dep_1);
        map.put("TMS_Cop_Fund_Dep_2", TMS_Cop_Fund_Dep_2);
        map.put("TMS_Cop_Fund_Dep_3", TMS_Cop_Fund_Dep_3);
        map.put("TMS_Cop_Fund_Dep_4", TMS_Cop_Fund_Dep_4);
        map.put("TMS_Cop_Fund_Dep_5", TMS_Cop_Fund_Dep_5);
        map.put("TMS_Cop_Fund_Dep_M", TMS_Cop_Fund_Dep_M);
        map.put("TMS_Cop_Fund_Dep_M_Ic", TMS_Cop_Fund_Dep_M_Ic);
        map.put("TMS_Cop_Fund_Dep_M_Fc", TMS_Cop_Fund_Dep_M_Fc);
        map.put("TMS_Cop_Fund_Dep_M_Sx", TMS_Cop_Fund_Dep_M_Sx);
        map.put("TMS_Cop_Planner", TMS_Cop_Planner);
        map.put("TMS_Cop_PM", TMS_Cop_PM);
        map.put("TMS_Cop_CFO2", TMS_Cop_CFO2);
        map.put("TMS_Cop_CFO", TMS_Cop_CFO);
        map.put("TMS_Cop_CFOs", TMS_Cop_CFOs);
        map.put("TMS_Singapore", TMS_Singapore);
        map.put("TMS_Cop_Invest_Finance_M", TMS_Cop_Invest_Finance_M);
        map.put("TMS_Cop_Transit_Trade_P", TMS_Cop_Transit_Trade_P);
        map.put("TMS_Cop_Ordinary_Trade_P", TMS_Cop_Ordinary_Trade_P);
        return map;
    }

}
