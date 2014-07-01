package com.wcs.tms.service.system.bank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.service.EntityService;
import com.wcs.tms.model.Bank;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:银行管理Service</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Stateless
public class BankService implements Serializable{

	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(BankService.class);
    @Inject
    EntityService entityService;
    
    
    /**
     * 得到一级银行机构下拉菜单
     * @return
     */
    public List<SelectItem> getTopLevelSelect(){
    	List<SelectItem> topLevelSelect = new ArrayList<SelectItem>();
    	
    	StringBuilder jpql = new StringBuilder("select b from Bank b where b.defunctInd = 'N'");
    	//启用
    	jpql.append(" and b.status = 'Y'");
    	//一级机构
    	jpql.append(" and b.topLevelFlag = 'Y'");
    	List<Bank> banks = entityService.find(jpql.toString());
    	
    	for(Bank b : banks){
    		topLevelSelect.add(new SelectItem(b.getId(),b.getBankName()));
    	}
    	return topLevelSelect;
    }
    
    /**
     * 一级银行得到分支银行下拉
     * @param topId
     * @return
     */
    public List<SelectItem> getChildBankSelect(Long topId){
    	List<SelectItem> childSelect = new ArrayList<SelectItem>();
    	StringBuilder jpql = new StringBuilder("select b from Bank b where b.defunctInd = 'N' and b.status = 'Y'");
    	jpql.append(" and b.topBankId = "+topId);
    	List<Bank> childBanks = entityService.find(jpql.toString());
    	for(Bank b : childBanks){
    		childSelect.add(new SelectItem(b.getId(),b.getBankName()));
    	}
    	return childSelect;
    }
    
    
    /**
     * 银行列表查询
     * @param conditionMap
     * @return
     */
    public List<Bank> findBankForLazy(Map<String, Object> conditionMap){
    	StringBuilder jpql = new StringBuilder("select b from Bank b where b.defunctInd = 'N' ");
    	if(null!=conditionMap.get("bankCode") && !"".equals(conditionMap.get("bankCode"))){
    		String temp = conditionMap.get("bankCode").toString().toLowerCase();
    		jpql.append(" and lower(b.bankCode) like '%"+temp+"%'");
    	}
    	if(null!=conditionMap.get("bankName") && !"".equals(conditionMap.get("bankName"))){
    		String temp = conditionMap.get("bankName").toString().toLowerCase();
    		jpql.append(" and lower(b.bankName) like '%"+temp+"%'");
    	}
    	if(null!=conditionMap.get("bankEn") && !"".equals(conditionMap.get("bankEn"))){
    		String temp = conditionMap.get("bankEn").toString().toLowerCase();
    		jpql.append(" and lower(b.bankEn) like '%"+temp+"%'");
    	}
    	if(null!=conditionMap.get("bsbCode") && !"".equals(conditionMap.get("bsbCode"))){
    		String temp = conditionMap.get("bsbCode").toString().toLowerCase();
    		jpql.append(" and lower(b.bsbCode) like '%"+temp+"%'");
    	}
    	if(null!=conditionMap.get("topBankId") && !"0".equals(conditionMap.get("topBankId"))){
    		jpql.append(" and (b.topBankId ="+ conditionMap.get("topBankId"));
    		jpql.append(" or b.id ="+ conditionMap.get("topBankId")+")");
    	}
    	
    	jpql.append(" order by b.bankCode,b.createdDatetime DESC");
    	List<Bank> banks = entityService.find(jpql.toString());
    	
    	//银行排序
    	banks = this.orderBanks(banks);
    	return banks;
    }
    
    /**
     * 银行排序
     * @param banks
     * @return
     */
    private List<Bank> orderBanks(List<Bank> banks){
    	List<Bank> banksNew = new ArrayList<Bank>();
    	banksNew.addAll(banks);
    	
    	Collections.sort(banksNew, new Comparator<Bank>() {
			@Override
			public int compare(Bank o1, Bank o2) {
				Long o1pid = o1.getTopBankId()==null ? o1.getId() : o1.getTopBankId(); 
				Long o2pid = o2.getTopBankId()==null ? o2.getId() : o2.getTopBankId(); 
				
				if(o1.getId().equals(3613l) || o2.getId().equals(3613l)){
					log.info("coming~~");
				}
				
				if(o1pid > o2pid){
					return -1;
				}else if(o1pid.equals(o2pid)){
					if("Y".equals(o1.getTopLevelFlag())){
						return -1;
					}
					if("Y".equals(o2.getTopLevelFlag())){
						return 1;
					}
					return 0;
				}else if(o1pid < o2pid){
					return 1;
				}
				return 0;
			}
		});
    	return banksNew;
    }
    
    /**
     * 检查银行代码唯一性
     * @param code
     * @return
     */
    public boolean checkCode(String code){
    	List<Bank> bs = new ArrayList<Bank>();
    	StringBuilder jpql = new StringBuilder("select b from Bank b where b.defunctInd = 'N'");
    	jpql.append(" and b.bankCode = '"+code+"'");
    	bs = entityService.find(jpql.toString());
    	if(bs.size()>0){
    		return false;
    	}else{
    		return true;
    	}
    }
    
    /**
     * 银行信息保存
     * @param bank
     */
    public void saveOrUpdate(Bank bank){
    	Long id = bank.getId();
    	String topFlag = bank.getTopLevelFlag();
    	if(topFlag!=null && "Y".equals(topFlag)){
    		bank.setTopBankId(null);
    	}
    	if(id==null){
    		bank.setStatus("Y");
    		entityService.create(bank);
    	}else{
    		entityService.update(bank);
    	}
    }
    
    /**
     * 分支银行得到一级银行
     * @param bankId
     * @return
     */
    public Bank getParentBank(Bank child){
    	Bank bank = new Bank();
    	StringBuilder jpql = new StringBuilder("select b from Bank b where b.defunctInd = 'N' and b.status = 'Y'");
    	jpql.append(" and b.id = "+child.getTopBankId());
    	bank = entityService.findUnique(jpql.toString());
    	return bank;
    }
}
