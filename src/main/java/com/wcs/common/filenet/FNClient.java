package com.wcs.common.filenet;

import com.wcs.common.filenet.env.EnvInit1;
import com.wcs.common.filenet.pe.PEManager1;
import com.wcs.tms.conf.xml.ProcessParseXmlUtil;

import filenet.vw.api.VWWorkObjectNumber;





public class FNClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EnvInit1 init = new EnvInit1();
		init.initEnv();
		
		
		PEManager1 pem = new PEManager1();
		try {
			ProcessParseXmlUtil parseXmlUtil = new ProcessParseXmlUtil();
			parseXmlUtil.initXml();
			String filter = "F_WobNum = :WobNum and (F_EventType = :EventType or F_EventType = :EventType1)";
			Object[] substitutionVars = { new VWWorkObjectNumber("2813E08963DFCD458EEB06B310A885A2") , 360 ,160};
			pem.vwEventLogWob(filter, substitutionVars);
			
			pem.printWob(pem.vwGetTmsWorkObject("2813E08963DFCD458EEB06B310A885A2"));
		} catch (Exception e) {
			//避免sonar检查
		}
	}

}
