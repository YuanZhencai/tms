package com.wcs.common.filenet.ce;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.util.DateUtil;

import filenet.vw.api.VWException;
import filenet.vw.api.VWStepElement;
import filenet.vw.api.VWWorkObjectNumber;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: PE测试类</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
public class PeDemo {

	private PeDemo() {
		
	}
	
	private static Log log = LogFactory.getLog(PeDemo.class);
	
    private static String WorkflowNumber = "";

    public static void main(String args[]) {
        ProcessUtil pt = new ProcessUtil();
        log.info("----------------创建流程实例-----------");
        String filter = "(F_EventType = :EventType or F_EventType = :EventType1) and F_WobNum = :wobNum";
        Object[] substitutionVars = { 360, 160, new VWWorkObjectNumber("D7FC5848F1BAB14E94E80F4F57A400EF") };
        try {
            pt.vwEventLogWob(filter, substitutionVars);
        } catch (Exception e) {
            log.error("PeDemo测试出现异常", e);
        }
        
        Calendar c =  Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, -1);
        log.info(DateUtil.dateToStrLong(c.getTime()));
    }

    public static void hrTest(ProcessUtil pt) {
        VWStepElement employeeStep = pt.createWorkFlow("EmployeeReimbursement");
        String workNum;
        try {
            workNum = employeeStep.getWorkflowNumber();
            // 员工提交报销申请
            pt.employeeSubmit("Employee", workNum, "node1", true, "员工1次");
            // 经理审批
            pt.employeeSubmit("Manager", workNum, "node2", true, "经里");
            // hr 审批 不通过
            pt.employeeSubmit("HR", workNum, "node3", false, "hr");
            // 员工提交报销申请
            pt.employeeSubmit("Employee", workNum, "node1", true, "员工2次");
            // 经理审批
            // hr 审批通过
        } catch (VWException e) {
        	log.error("PeDemo hrTest测试出现异常", e);
        }
    }

    public static void regist(ProcessUtil pt) {
        VWStepElement employeeStep = pt.createWorkFlow("TMS_RegiCapital");
        String workNum;
        try {
            workNum = employeeStep.getWorkflowNumber();

            pt.vwStepExcution(workNum, "node1", true, 0, "1");
        } catch (VWException e) {
        	log.error("PeDemo regist测试出现异常", e);
        }
    }
}
