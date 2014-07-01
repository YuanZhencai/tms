package com.wcs.tms.service.system.company;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.MathUtil;
import com.wcs.common.model.Dict;
import com.wcs.common.service.CommonService;
import com.wcs.tms.model.ProdOrTradeCashMain;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:公司头寸主数据管理Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
public class CompanyCashService implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(CompanyCashService.class);
	
	@Inject
	private EntityService entityService;
	
	@Inject 
    private CommonService commonService;
	
	/**
	 * 查询公司头寸主数据
	 * @param compId
	 * @return
	 */
	public List<ProdOrTradeCashMain> findCashByComId(Long compId) {
		StringBuilder jpql = new StringBuilder("select p from ProdOrTradeCashMain p join fetch p.company where p.defunctInd = 'N'");
		jpql.append(" and p.company.id="+compId);
		jpql.append("order by p.endDate desc");
		return entityService.find(jpql.toString());
	}

	/**
	 * 根据查询条件查询当前公司头寸信息
	 * @param companyId
	 * @param cashType
	 * @param variety
	 * @return
	 */
	public List<ProdOrTradeCashMain> findCashByCondition(Long companyId, String cashType,
			String variety, String varietyType) {
		
		StringBuilder jpql = new StringBuilder("select p from ProdOrTradeCashMain p join fetch p.company where p.defunctInd = 'N'");
		jpql.append(" and p.company.id="+companyId);
		if( !MathUtil.isEmptyOrNull(cashType)){
			jpql.append(" and p.appType='"+cashType+"'");
		}
		if( !MathUtil.isEmptyOrNull(variety)){
			jpql.append(" and p.variety='"+variety+"'");
		}
		//查询品种类型bug 增加品种类型查询条件
		if(StringUtils.isNotBlank(varietyType)) {
		    List<Dict> dicts = commonService.getDictByCat(varietyType,"zh_CN");
		    StringBuilder varityTypeJphl = new StringBuilder();
		    for (Dict dict : dicts) {
		        varityTypeJphl.append(" '").append(dict.getCodeKey()).append("' ,");
            }
		    if(varityTypeJphl.length() > 0) {
		        String varityTypeQuery = varityTypeJphl.substring(0, varityTypeJphl.length() - 1);
		        jpql.append(" and p.variety  in (").append(varityTypeQuery).append(")");
		    }
		}
		jpql.append("order by p.endDate desc");
		log.info(jpql.toString());
		return entityService.find(jpql.toString());
	}
	
	/**
	 * 查看主数据表中是否存在期限冲突数据
	 * @param prodOrTradeCashMain
	 * @return
	 */
	public List<ProdOrTradeCashMain> findMainBy(
			ProdOrTradeCashMain prodOrTradeCashMain) {
		
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		String start = sdf.format(prodOrTradeCashMain.getStartDate());
		String end = sdf.format(prodOrTradeCashMain.getEndDate());
		StringBuilder jpql = new StringBuilder("select p from ProdOrTradeCashMain p join fetch p.company where p.defunctInd = 'N'");
		jpql.append(" and p.company.id="+prodOrTradeCashMain.getCompany().getId());
		jpql.append(" and p.appType='"+prodOrTradeCashMain.getAppType()+"'");
		jpql.append(" and p.variety='"+prodOrTradeCashMain.getVariety()+"'");
		jpql.append(" and p.startDate <='"+start+"'");
		jpql.append(" and p.endDate >='"+start+"'");
		List<ProdOrTradeCashMain> sMains = entityService.find(jpql.toString());
		jpql = new StringBuilder(" select p from ProdOrTradeCashMain p join fetch p.company where p.defunctInd = 'N'");
		jpql.append(" and p.company.id="+prodOrTradeCashMain.getCompany().getId());
		jpql.append(" and p.appType='"+prodOrTradeCashMain.getAppType()+"'");
		jpql.append(" and p.variety='"+prodOrTradeCashMain.getVariety()+"'");
		jpql.append(" and p.startDate <='"+end+"'");
		jpql.append(" and p.endDate >='"+end+"'");
		List<ProdOrTradeCashMain> eMains = entityService.find(jpql.toString());
		List<ProdOrTradeCashMain> mains = new ArrayList<ProdOrTradeCashMain>();
		mains.addAll(sMains);
		mains.addAll(eMains);
		return mains;
	}
}
