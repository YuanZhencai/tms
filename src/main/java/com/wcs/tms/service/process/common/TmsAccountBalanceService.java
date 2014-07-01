package com.wcs.tms.service.process.common;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TmsAccountBalanceService implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(unitName = "ssPu")
	EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<Object[]> getRows() {
		StringBuilder sql = new StringBuilder("select sg1.AcctNo,sg1.AvailableAmount,sg1.UpdateDate from sgPayment_QuotaBalance sg1 " +
				"where exists (select * from sgPayment_QuotaBalance sg2 " +
				"inner join(select sg.AcctNo as acNo,max(UpdateDate) as sgUpdateDate from sgPayment_QuotaBalance sg " +
				"where CycleType='1' group by sg.AcctNo) sg3 " +
				"on sg2.AcctNo=sg3.acNo and sg2.UpdateDate=sg3.sgUpdateDate and sg2.CycleType='1' " +
				"where sg1.AcctNo = sg3.acNo and sg1.UpdateDate= sg3.sgUpdateDate and sg1.CycleType='1')");
		return entityManager.createNativeQuery(sql.toString()).getResultList();
	}

}
