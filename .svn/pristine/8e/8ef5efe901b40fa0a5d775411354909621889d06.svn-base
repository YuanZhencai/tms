package com.wcs.tms.view.report.cashpool.vo;

public class CashItemVo extends CashPoolStrId {
	private String itemType;
	private String itemName;

	public CashItemVo(String id, String type, String cpName) {
		setId(id);
		this.itemType = type;
		this.itemName = cpName;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CashItemVo) {
			CashItemVo vo = (CashItemVo) obj;
			if (vo.getId() != null && vo.getId().equals(this.getId())) {
				return true;
			} else {
				return false;
			}
		} else {
			return super.equals(obj);
		}
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

}
