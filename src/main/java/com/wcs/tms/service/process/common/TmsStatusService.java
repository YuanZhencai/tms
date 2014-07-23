package com.wcs.tms.service.process.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.tms.model.ProcTMSStatus;
import com.wcs.tms.model.ProcTMSStatusError;

/**
 * TMS回传Service
 * @author zhuxinyi
 *
 */
@Stateless
public class TmsStatusService implements Serializable{

	private static final long serialVersionUID = -4344148108327074139L;
	
	private Log log = LogFactory.getLog(TmsStatusService.class);
	@EJB
    private EntityService entityService;
	
	public static final String STATUS_NOIMPORT = "1";
	public static final String STATUS_IMPORT_SUCCESS = "2";
	public static final String STATUS_IMPORT_FAILD = "3";
	
	/**
	 * 保存TMS回传表
	 * @param tmsStatus
	 */
	public void saveTmsStatus(ProcTMSStatus tmsStatus) {
		try {
			entityService.create(tmsStatus);
		} catch (Exception e) {
			log.error("保存TMS回传表出错！", e);
		}
	}
	
	/**
	 * 保存TMS回传异常表
	 * @param tmsStatus
	 */
	public void saveTmsStatusError(ProcTMSStatusError tmsStatus) {
		try {
			entityService.create(tmsStatus);
		} catch (Exception e) {
			log.error("保存TMS回传异常表出错！", e);
		}
	}
	
	/**
	 * 根据BPMID查询TMS回传记录
	 * @param bpmId
	 * @return
	 */
	public ProcTMSStatus getTmsStatusByBpmId (String bpmId) {
		log.info("bpmId:"+ bpmId);
		String hql = "SELECT o FROM ProcTMSStatus o WHERE o.bpmId = ?1";
		List<ProcTMSStatus> list = entityService.find(hql, bpmId);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 更新TMS回传记录
	 * @param tmsStatus
	 */
	public void updateTmsStatus(ProcTMSStatus tmsStatus) {
		entityService.update(tmsStatus);
	}

	/**
	 * 更新导入状态
	 * @param stateList
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateImportStatus(List<ProcTMSStatus> stateList) {
		for (ProcTMSStatus procTMSStatus : stateList) {
			String sql = "UPDATE PROC_TMS_STATUS s set s.TMS_STATUS = '"+procTMSStatus.getTmsStatus()+"' WHERE s.BPM_ID='"+procTMSStatus.getBpmId()+"' AND int(s.TMS_STATUS) < 4";
			entityService.getEm().createNativeQuery(sql).executeUpdate();
		}
	}
	
}
