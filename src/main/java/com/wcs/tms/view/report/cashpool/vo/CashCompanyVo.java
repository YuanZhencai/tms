/** * CashCompanyVo.java 
* Created on 2013-12-23 下午5:07:55 
*/

package com.wcs.tms.view.report.cashpool.vo;

public class CashCompanyVo extends CashPoolStrId {
	private String cpName;

	public String getCpName() {
		return cpName;
	}

	public void setCpName(String cpName) {
		this.cpName = cpName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CashCompanyVo) {
			CashCompanyVo vo = (CashCompanyVo) obj;
			if (vo.getId() != null && vo.getId().equals(this.getId())) {
				return true;
			} else {
				return false;
			}
		} else {
			return super.equals(obj);
		}
	}

}
