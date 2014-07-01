package com.wcs.tms.service.system.interfaces;

import java.util.List;

/**
 * 公司的外債资本金主数据服务接口
 * @author liushengbin
 *
 */
public interface IDebtCapitalService {

	/**
	 * 根据公司ID,获取公司的外債资本金主数据
	 * @param companyId
	 * @return
	 */
	public DebtCapitalInfo getCompanyInfoById(Long companyId);
	
	/**
	 * 根据股东ID,获取股东的详细信息
	 * @param companyId
	 * @return
	 */
	public ShareHolderInfo getShareHolderInfoById(Long shareHolderId);
	
	/**
	 * 根据公司ID,获取公司下的所有股东的详细信息
	 * @param companyId
	 * @return
	 */
	public List<ShareHolderInfo> getShareHolderListByCompanyId(Long companyId);
}
