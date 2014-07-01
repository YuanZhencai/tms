package com.wcs.tms.conf.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestXml {

	private static Log log = LogFactory.getLog(TestXml.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ProcessParseXmlUtil xmlUtil = new ProcessParseXmlUtil();
		xmlUtil.initXml();
		ProcessXmlUtil.getParasByProcessStep("TMS_DebtBorrow", "工厂财务经理审批");
		log.info(ProcessXmlUtil.findNextStep("TMS_DebtBorrow", "财务总监审批"));
	}

}
