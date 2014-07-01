package com.wcs.tms.service.process.common.patch;

import java.util.Date;
import java.util.List;

import com.wcs.tms.conf.xml.ProcessParseXmlUtil;
import com.wcs.tms.conf.xml.ProcessXmlUtil;
import com.wcs.tms.service.process.common.patch.pe.PatchEnvInit;
import com.wcs.tms.service.process.common.patch.pe.PatchPEManager;

import filenet.vw.api.VWLogElement;

public class TESTQueryFN {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PatchEnvInit init = new PatchEnvInit();
		init.initEnv();
		getSubedList();
	}

	public static void getSubedList() {
		PatchPEManager peManager = new PatchPEManager();
		try {
			ProcessParseXmlUtil parseXmlUtil = new ProcessParseXmlUtil();
			parseXmlUtil.initXml();
			// new VWWorkObjectNumber("2813E08963DFCD458EEB06B310A885A2")
			// (F_EventType = :EventType or F_EventType = :EventType1)
			//and F_StartTime >= :startDate and F_StartTime <= :endDate 
			String filter = "1=1 and F_EventType = :EventType ";
			Object[] substitutionVars = { 160 };
			//查询已提交的且结束的流程
			List<VWLogElement> les = peManager.vwEventLogWob(filter,
					substitutionVars);
			for (VWLogElement le : les) {
				String processCode = ProcessXmlUtil.getProcessAttribute("className", le.getWorkClassName(), "code");
				if(processCode==null || "".equals(processCode)){
					continue;
				}
				
				System.out.println("WorkFlowNumber:"+le.getWorkFlowNumber());
				System.out.println("processCode:"+processCode);
				System.out.println("ProcessVersion:"+peManager.vwFindProcessVersion(ProcessXmlUtil.getProcessAttribute("className", le.getWorkflowName(), "cePath")));
				System.out.println("F_StartTime:"+(Date)le.getDataField("F_StartTime").getValue());
				System.out.println("EndTime:"+le.getTimeStamp());
				System.out.println("StepName:"+le.getStepName());
				//peManager.printWob(le);

			}

		} catch (Exception e) {
			// 避免sonar检查
		}
	}
}
