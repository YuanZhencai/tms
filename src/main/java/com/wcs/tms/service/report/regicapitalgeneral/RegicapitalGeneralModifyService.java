package com.wcs.tms.service.report.regicapitalgeneral;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.SortOrder;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.DateUtil;
import com.wcs.base.util.StringUtils;
import com.wcs.common.filenet.pe.PEManager;
import com.wcs.common.service.CommonService;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.ProcRegiCapitalChange;
import com.wcs.tms.model.ProcRegiCapitalChangeShareholder;
import com.wcs.tms.model.ShareHolder;
import com.wcs.tms.service.process.common.ProcessUtilMapService;
import com.wcs.tms.view.process.common.entity.RegicapitalMofifyShareholderVO;
import com.wcs.tms.view.process.common.entity.RegicapitalMofifyVO;
import com.wcs.tms.view.process.common.entity.ShareHolderVo;

/** 
* <p>Project: tms</p> 
* <p>Title: 注册资本情况一览表Service</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:guyanyu@wcs-global.com">Gu yanyu</a> 
*/
@Stateless
public class RegicapitalGeneralModifyService implements Serializable {

	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(RegicapitalGeneralModifyService.class);
	@PersistenceContext(unitName = "pu")
	private EntityManager em;
	@Inject
	PEManager peManager;
	@Inject
	EntityService entityService;
	@Inject
	ProcessUtilMapService processUtilMapService;
	@EJB 
    private CommonService commonService;
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> findRegiCaitalMofidyDetail(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> conditionMap) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
		    Query query;
			
			StringBuilder jpql = new StringBuilder("select prcc from ProcRegiCapitalChange prcc join fetch prcc.company join fetch prcc.procRegiCapitalChangeShareholders where prcc.company.defunctInd = 'N' and prcc.company.status = 'Y' ");
			
			//申请日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (null != conditionMap.get("applyDateS")) {
                String applyDateS = sdf.format(conditionMap.get("applyDateS"));
                jpql.append(" and prcc.createdDatetime >= '" + applyDateS + "' ");
            }
            if (null != conditionMap.get("applyDateE")) {
                String applyDateS = sdf.format(conditionMap.get("applyDateE"));
                jpql.append(" and prcc.createdDatetime <= '" + applyDateS + "' ");
            }
            
            //流程编号
            List<String> procInstIds = new ArrayList<String>();
            if (null != conditionMap.get("processNo") && !"".equals(conditionMap.get("processNo"))) {
                procInstIds = processUtilMapService.getFnIdsByTmsId(conditionMap.get("processNo").toString());
                if(procInstIds != null) {
                    jpql.append(" and prcc.procInstId in (:procInstIds)");
                }
            }
            
			//流程状态
            if (null != conditionMap.get("processStatus") && !"".equals(conditionMap.get("processStatus"))) {
                jpql.append(" and prcc.processStatus = '" + conditionMap.get("processStatus") + "' ");
            }
			
			//公司
            String companyId = (String) conditionMap.get("companyId");
			if (!StringUtils.isBlankOrNull(companyId)) { 
			    jpql.append(" and prcc.company.id = " + companyId + " order by prcc.id");
			    query = em.createQuery(jpql.toString());
			}else {
			    List<String> list = (List<String>) conditionMap.get("companyIds");
			    jpql.append(" and prcc.company.id in(:companyIds) order by prcc.id");
                query = em.createQuery(jpql.toString()).setParameter("companyIds", list);
			}
			if(!procInstIds.isEmpty()) {
                query.setParameter("procInstIds", procInstIds);
            } 
			System.out.println("jpql : " + jpql);
			int count = 1;
			query.setFirstResult(first);
			query.setMaxResults(pageSize);
			List<ProcRegiCapitalChange> procRegiCapitalChanges = (List<ProcRegiCapitalChange>) query.getResultList();
			List<RegicapitalMofifyVO> regicapitalMofifyVOs = new ArrayList<RegicapitalMofifyVO>();
			if (!procRegiCapitalChanges.isEmpty()) {
				for (ProcRegiCapitalChange prcc : procRegiCapitalChanges) {
				    RegicapitalMofifyVO rcmVO = this.getRcmVO(prcc);
				    regicapitalMofifyVOs.add(rcmVO);
				}
				map.put("count", count);
				map.put("list", regicapitalMofifyVOs);
			}
		} catch (Exception e) {
			log.error("findShareHolderBySqLoad方法 错误信息" + e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	
	/**
	 * 组装RegicapitalMofifyVO
	 * @param prcc
	 * @return
	 */
	public RegicapitalMofifyVO  getRcmVO(ProcRegiCapitalChange prcc) {
	    RegicapitalMofifyVO rcmVO = new RegicapitalMofifyVO();
	    // 申请日期(创建时间)
	    rcmVO.setApplyDate(DateUtil.convertDateToString(prcc.getCreatedDatetime(), "yyyy-MM-dd"));
	    // 流程编号
	    rcmVO.setProcessNo(processUtilMapService.getTmsIdByFnId(prcc.getProcInstId()));
	    // 公司名称
	    rcmVO.setCompanyName(prcc.getCompany().getCompanyName());
	    // 投资总额
	    rcmVO.setCompanyAmount(prcc.getCompany().getInvestTotal());
	    // 币别
	    rcmVO.setCompanyCu(prcc.getCompany().getInvestCurrency());
	    // 流程状态
	    rcmVO.setProcessStatus(prcc.getProcessStatus());
	    // 股东信息
	    List<RegicapitalMofifyShareholderVO> rcmsVOs = new ArrayList<RegicapitalMofifyShareholderVO>();
	    List<ProcRegiCapitalChangeShareholder> prccss = prcc.getProcRegiCapitalChangeShareholders();
	    for(ProcRegiCapitalChangeShareholder prccs : prccss) {
	        RegicapitalMofifyShareholderVO rcmsVO = new RegicapitalMofifyShareholderVO();
	        rcmsVO.setId(prccs.getId());
	        // 股东名称
	        rcmsVO.setName(prccs.getShareholderName());
	        // 状态
	        String status = "/";
	        if(prccs.getFondsCurrencyOriginal() != null && prccs.getFondsCurrency() != null) {
	            status = "更新";
	        }else if (prccs.getFondsCurrencyOriginal() == null && prccs.getFondsCurrency() != null) {
	            status = "新增";
	        }else if (prccs.getFondsCurrencyOriginal() != null && prccs.getFondsCurrency() == null) {
	            status = "删除";
	        }
	        rcmsVO.setStatus(status);
	        // 币别
	        List<String> currencys = new ArrayList<String>();
	        String currencyOriginal = "";
	        String currency = "";
	        if(prccs.getFondsCurrencyOriginal() != null) {
	            currencyOriginal = getValueByDictCatAndKey("TMS_TAX_PROJECT_CURRENCY_TYPE", prccs.getFondsCurrencyOriginal().toString());
	        }
	        if(prccs.getFondsCurrency() != null) {
	            currency = getValueByDictCatAndKey("TMS_TAX_PROJECT_CURRENCY_TYPE", prccs.getFondsCurrency().toString());
	        }
	        currencys.add(currencyOriginal);
	        currencys.add(currency);
	        rcmsVO.setCurrencys(currencys);
	        // 注册金额
	        List<String> fondsTotals = new ArrayList<String>();
	        fondsTotals.add(prccs.getFondsTotalOriginal() == null ? "/" : prccs.getFondsTotalOriginal().toString());
            fondsTotals.add(prccs.getFondsTotal() == null ? "/" : prccs.getFondsTotal().toString());
            rcmsVO.setFondsTotals(fondsTotals);
	        // 到位金额
	        List<String> fondsInPlaces = new ArrayList<String>();
	        fondsInPlaces.add(prccs.getFondsInPlaceOriginal() == null ? "/" : prccs.getFondsInPlaceOriginal().toString()); 
	        fondsInPlaces.add(prccs.getFondsInPlace() == null ? "/" : prccs.getFondsInPlace().toString());
	        rcmsVO.setFondsInPlaces(fondsInPlaces);
	        // 未到位金额 = 注册金额 - 到位金额
	        List<String> fondsNotInPlaces = new ArrayList<String>();
	        Double fondsNotInPlaceOriginal = (prccs.getFondsTotalOriginal() == null ? 0 : prccs.getFondsTotalOriginal()) - (prccs.getFondsInPlaceOriginal() == null ? 0 : prccs.getFondsInPlaceOriginal());
	        Double fondsNotInPlace = (prccs.getFondsTotal() == null ? 0 : prccs.getFondsTotal()) - (prccs.getFondsInPlace() == null ? 0 : prccs.getFondsInPlace());
	        fondsNotInPlaces.add(fondsNotInPlaceOriginal.toString());
	        fondsNotInPlaces.add(fondsNotInPlace.toString());
	        rcmsVO.setFondsNotInPlaces(fondsNotInPlaces);
	        // 股权比例
	        List<String> equityPercs = new ArrayList<String>();
	        equityPercs.add(prccs.getEquityPercOriginal() == null ? "/" : prccs.getEquityPercOriginal().toString());
	        equityPercs.add(prccs.getEquityPerc() == null ? "/" : prccs.getEquityPerc().toString());
	        rcmsVO.setEquityPercs(equityPercs);
	        // 股权关联
	        List<String> isEquityRelas = new ArrayList<String>();
	        isEquityRelas.add(prccs.getIsEquityRelatedOriginal() == null ? "/" : prccs.getIsEquityRelatedOriginal().toString());
	        isEquityRelas.add(prccs.getIsEquityRelated() == null ? "/" : prccs.getIsEquityRelated().toString());
	        rcmsVO.setIsEquityRelas(isEquityRelas);
	        // 实际股权比例 = 股权比例 * 关联比例
	        List<String> actualEquityPercs = new ArrayList<String>();
	        Double actualEquityPercOriginal = (prccs.getEquityPercOriginal() == null ? 0 : prccs.getEquityPercOriginal()) * (prccs.getRelatedPercOriginal() == null ? 0 : prccs.getRelatedPercOriginal());
	        Double actualEquityPerc = (prccs.getEquityPerc() == null ? 0 : prccs.getEquityPerc()) * (prccs.getRelatedPerc() == null ? 0 : prccs.getRelatedPerc());
	        actualEquityPercs.add(actualEquityPercOriginal.toString());
	        actualEquityPercs.add(actualEquityPerc.toString());
	        rcmsVO.setActualEquityPercs(actualEquityPercs);
	        
	        rcmsVOs.add(rcmsVO);
	    }
	    rcmVO.setShareholders(rcmsVOs);
	    return rcmVO;
	}
	
	public String getValueByDictCatAndKey(String cat,String keyValue) {
        String cat_point_key = cat + "_" + keyValue;
        Locale browserLang = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        // 这里的cat_point_key的值是DictConsts下的常量名称.请注意.而非数据库的值.
        // 下面重新组合参数.
        String cat_point_key_lang = cat_point_key + "_" + browserLang.toString();
        return commonService.getValueByDictCatKey(cat_point_key_lang);
    }
}
