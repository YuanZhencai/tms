package com.wcs.common.filenet.pe;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.common.filenet.env.Env;
import com.wcs.common.filenet.pe.queue.QueueHelper;

import filenet.vw.api.VWException;
import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWStepElement;

public class PEOperate {

	private Log logger = LogFactory.getLog(PEOperate.class);
	
	public void testLaunchFlow(){
		try {
			VWStepElement vwStepElement = WorkflowHelper.createWorkflow("testWorkFlow", Env.getVWSession());
			vwStepElement.setParameterValue("pppp", "llll", true);
			vwStepElement.doDispatch();
		} catch (VWException e) {
			logger.error("testLaunchFlow方法出现异常", e);
		}
	}
	
	public void testQTask(){
		try {
			logger.info("PEOperate.testQTask()~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~`");
			StringBuffer fsb = new StringBuffer();
			fsb.append("");
			List tempTaskList = QueueHelper.getWorkElements(Env.getVWSession(),"Inbox", fsb.toString(),
							VWFetchType.FETCH_TYPE_STEP_ELEMENT);
			int i = 0;
			Iterator it = tempTaskList.iterator();
			while (it.hasNext()) {
				
				VWStepElement vs = (VWStepElement) it.next();
				String[] pps = vs.getParameterNames();
				if(i==0){
					String title = "";
					for(String p:pps){
						title = title + p +"|";
					}
					logger.info(title);	
				}
				String values = "";
				for(String p:pps){
					Object v = vs.getParameterValue(p);
					values = values + v.toString() + "|";
				}
				logger.info(values);	
				i++;
			}
		} catch (VWException e) {
			logger.error("testQTask方法出现异常", e);
		}
	}
}
