package com.wcs.tms.service.process.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wcs.tms.model.ProcTMSStatus;

@Stateless
public class TmsImportStatusService implements Serializable{
	private Log log = LogFactory.getLog(TmsImportStatusService.class);
	
	private static final long serialVersionUID = -3537467686019707358L;

	@PersistenceContext(unitName = "ssPu")
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<ProcTMSStatus> getImportStatusList() {
		log.info("getImportStatusList方法开始");
		List<ProcTMSStatus> retList = new ArrayList<ProcTMSStatus>();
		
		try {
			StringBuilder sql = new StringBuilder("SELECT s.ZZLNO, s.DATASOURCE FROM BPMINS_STATUS s where datediff(day,applydatetime,getdate())<14");
			List<Object[]> list = em.createNativeQuery(sql.toString()).getResultList();
			for (Object[] obj : list) {
				ProcTMSStatus status = new ProcTMSStatus();
				status.setBpmId(obj[0].toString());
				Integer state = Integer.parseInt(obj[1].toString());
				
				log.info("ZZLNO:"+obj[0].toString()+"  DATASOURCE:"+obj[1].toString());
				
				int impState = state/10;
				if(impState == 1) {
					status.setTmsStatus(TmsStatusService.STATUS_IMPORT_SUCCESS);
				}else if(impState == 2){
					status.setTmsStatus(TmsStatusService.STATUS_IMPORT_FAILD);
				}else {
					continue;
				}
				retList.add(status);
			}
		} catch (Exception e) {
			log.error("getImportStatusList方法", e);
		}
		log.info("getImportStatusList方法结束");
		return retList;
	}
}
