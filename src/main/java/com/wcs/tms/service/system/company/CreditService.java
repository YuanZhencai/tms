package com.wcs.tms.service.system.company;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;

import com.wcs.base.service.EntityService;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Bank;
import com.wcs.tms.model.Company;
import com.wcs.tms.model.Credit;
import com.wcs.tms.util.SelectItemUtil;

@Stateless
public class CreditService implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Inject
    EntityService entityService;
    @Inject
	CompanyTmsService companyTmsService;

    /**
     * 
     * <p>Description: 得到一级银行下的支行</p>
     * @param topId
     * @return
     * @throws ServiceException
     */
    public List<SelectItem> findBranchBankSelect(Long topId) throws ServiceException {
        Validate.notNull(topId, "总银行Id为空");
        try {
            StringBuilder buff = new StringBuilder();
            buff.append("select b.id,b.bankName from Bank b where b.topBankId=?1 and b.defunctInd = 'N' and b.status='Y' ");
            List list = entityService.find(buff.toString(), topId);
            return SelectItemUtil.arrayToListSelectItem(list);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
    
   

    /**
     * 
     * <p>Description: 查询公司下的授信记录</p>
     * @param companyId
     * @return
     * @throws ServiceException
     */
    public List<Credit> findCreditList(Long companyId) throws ServiceException {
        Validate.notNull(companyId, "公司Id为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select c from Credit c  where c.company.id=?1").append(" and c.defunctInd = 'N'");
            return entityService.find(bulder.toString(), companyId);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 查询公司授信记录并且抓取银行</p>
     * @param companyId
     * @return
     * @throws ServiceException
     */
    public List<Credit> findCreditFetchBank(Long companyId) throws ServiceException {
        Validate.notNull(companyId, "公司Id为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select c from Credit c left join fetch c.bank  where c.company.id=?1").append(
                    " and c.defunctInd = 'N'  ");
            return entityService.find(bulder.toString(), companyId);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
    
    
    /**** HuHan add Start ********************************************************************************/
    /**
     * huhan add
     * <p>Description: 查询公司授信记录并且抓取银行</p>
     * @param companyId
     * @return
     * @throws ServiceException
     */
    public List<Credit> findCreditFetchBank1(Long companyId) throws ServiceException {
        Validate.notNull(companyId, "公司Id为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select c from Credit c left join fetch c.bank  where c.company.id=?1").append(
                    " and c.defunctInd = 'N' and c.status='Y'");
            bulder.append(" order by c.creditEnd desc");
            return entityService.find(bulder.toString(), companyId);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
    /**
     * huhan add
     * <p>Description: 查询公司授信记录并且抓取银行</p>
     * @param companyId
     * @return
     * @throws ServiceException
     */
    public List<Credit> findCreditFetchBank1(String companyName) throws ServiceException {
    	Validate.notNull(companyName, "公司名称为空");
    	try {
    		long companyId = companyTmsService.getCompanyName(companyName);
    		StringBuilder bulder = new StringBuilder();
    		bulder.append("select c from Credit c left join fetch c.bank  where c.company.id=?1").append(
    				" and c.defunctInd = 'N' and c.status='Y'");
    		// add by yan on 2013-7-3 改为有效授信
    		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
    		String now = sdf.format(new Date());
    		bulder.append(" and c.creditStart <= '"+ now +"'");
    		bulder.append(" and c.creditEnd >= '"+ now +"'");
    		bulder.append(" order by c.creditEnd desc");
    		return entityService.find(bulder.toString(), companyId);
    	} catch (Exception e) {
    		throw new ServiceException(e);
    	}
    }
    
    /**
     * huhan add
     * <p>Description: 查询银行授信记录并且抓取公司</p>
     * @param companyId
     * @return
     * @throws ServiceException
     */
    public List<Credit> findCreditFetchComp(Long bankId) throws ServiceException {
        Validate.notNull(bankId, "银行Id为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select c from Credit c join fetch c.company where c.bank.id=?1").append(
                    " and c.defunctInd = 'N' and c.status='Y'");
            bulder.append(" and c.bank.id in (?1)");
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
            String now = sdf.format(new Date());
            bulder.append(" and c.creditStart <= '"+ now +"'");
            bulder.append(" and c.creditEnd >= '"+ now +"'");
            bulder.append(" order by c.creditEnd desc");
            return entityService.find(bulder.toString(), bankId);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
    
    /**
     * huhan add
     * <p>Description: 查询银行授信记录并且抓取公司(多子银行)</p>
     * @param companyId
     * @return
     * @throws ServiceException
     */
    public List<Credit> findCreditFetchCompMut(List<Long> bankIds) throws ServiceException {
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select c from Credit c join fetch c.bank join fetch c.company where c.defunctInd = 'N' and c.status='Y'");
            bulder.append(" and c.bank.id in (?1)");
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
            String now = sdf.format(new Date());
            bulder.append(" and c.creditStart <= '"+ now +"'");
            bulder.append(" and c.creditEnd >= '"+ now +"'");
            bulder.append(" order by c.creditEnd desc");
            
            return entityService.find(bulder.toString(), bankIds);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
    /**
     * 
     * <p>Description: 查询公司+银行有效的授信记录</p>
     * @param companyId
     * @param bankId
     * @return
     * @throws ServiceException
     */
    public List<Credit> findViableCredit(Long companyId, Long bankId) throws ServiceException {
        Validate.notNull(companyId, "公司Id为空");
        Validate.notNull(bankId, "银行Id为空");
        try {
            StringBuilder bulder = new StringBuilder();
            bulder.append("select c from Credit c left outer join fetch c.creditOs where c.bank.id=?1 and c.company.id=?2 and c.defunctInd = 'N' and c.status='Y'");
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
            String now = sdf.format(new Date());
            bulder.append(" and c.creditStart <= '"+ now +"'");
            bulder.append(" and c.creditEnd >= '"+ now +"'");
            bulder.append(" order by c.creditEnd desc");
            return this.entityService.find(bulder.toString(), bankId, companyId);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
    /**** HuHan add End *************************************************************************************/
   
    /**
     * 查找最近更新的一条授信
     * @param registerDate 
     * @param topBankId 
     * @param companyId 
     * @return(modified by yanchangjing on 2012-9-20)
     */
    public List<Credit> findLastCredit(String registerDate, Long topBankId, Long companyId){
    	StringBuilder jpql = new StringBuilder("select c from Credit c join fetch c.bank where c.defunctInd = 'N' and c.status='Y'");
    	jpql.append(" and c.bank.topBankId = "+topBankId);
    	jpql.append(" and c.company.id = "+companyId);
    	jpql.append(" and c.creditStart <= '"+registerDate+"'");
    	jpql.append(" and c.creditEnd >= '"+registerDate+"'");
    	return entityService.find(jpql.toString());
    }
    /**
     * 
     * <p>Description: 查询公司的上一年授信记录</p>
     * @param companyId
     * @param bankId
     * @return
     * @throws ServiceException
     */
    public List<Credit> findlastCredit(Long companyId, Long bankId) throws ServiceException {
        Validate.notNull(companyId, "公司Id为空");
        Validate.notNull(bankId, "银行Id为空");
        try {
            StringBuilder bulder = new StringBuilder();
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
            String now = sdf.format(new Date());
            bulder.append("select c from Credit c where c.bank.id=?1 and c.company.id=?2 and c.defunctInd = 'N'" +
            		" and c.creditEnd < '"+ now +"'" +
            		" order by c.creditEnd desc");
            return this.entityService.find(bulder.toString(), bankId, companyId);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * <p>Description: 保存授信</p>
     * @param companyId
     * @param creditList
     * @throws ServiceException
     */
    public void batchSaveCredit(Long companyId, List<Credit> creditList) throws ServiceException {
        Validate.notNull(companyId, "公司Id为空");
        try {
            // 判断是否需要删除 通过比较内存的对象与受管对象
            List<Credit> dataList = findCreditList(companyId);
            if (!dataList.isEmpty()) {
                for (Credit credit : dataList) {
                    if (!creditList.contains(credit)) {
                        credit.setDefunctInd("Y");
                        credit.setStatus("N");
                        this.entityService.update(credit);
                    }
                }
            }
            // 保存或者更新现有集合
            if (creditList.isEmpty()) { 
                return; 
            }
            for (Credit credit : creditList) {
                if (credit.getId() == null) {
                    Company company = this.entityService.find(Company.class, companyId);
                    credit.setCompany(company);
                    this.entityService.create(credit);
                } else {
                    this.entityService.update(credit);
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }



    /**
     * 查出某公司下已授信总行 add on 2013-2-25
     * @param companyId
     * @return
     */
	public List<Bank> findCreditTopBankSelect(Long companyId) {
		Validate.notNull(companyId, "公司Id为空");
        try {
        	List<Bank> topBanks = new ArrayList<Bank>();
            StringBuilder bulder = new StringBuilder();
            bulder.append("select distinct c.bank.topBankId from Credit c left join fetch c.bank  where c.company.id=?1").append(
                    " and c.defunctInd = 'N' and c.status='Y'");
            bulder.append(" order by c.creditEnd desc");
            List<Long> topBankIds =  entityService.find(bulder.toString(), companyId);
            for(Long id : topBankIds ){
            	Bank bank = entityService.find(Bank.class, id);
            	topBanks.add(bank);
            }
            return topBanks;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
	}

	/**
	 * 根据授信id查询Credit
	 * @param creditId
	 * @return
	 */
	public Credit findUniqueCreditById(Long creditId){
		StringBuilder jpql = new StringBuilder("select c from Credit c join fetch c.bank where c.defunctInd = 'N' and c.status='Y'");
    	jpql.append(" and c.id = "+creditId);
    	return entityService.findUnique(jpql.toString());
	}

}
