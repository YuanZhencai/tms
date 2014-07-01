package com.wcs.tms.view.process.common.entity;

import java.util.Comparator;

/** 
* <p>Project: tms</p> 
* <p>Title: 根据流程的付款时间排序</p> 
* <p>Description: </p> 
* <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
* <p>All Rights Reserved.</p>
* @author <a href="mailto:wangyang6@wcs-global.com">Wang Yang</a> 
*/
public class PurchaseFundVoSorter implements Comparator<PurchaseFundVo> {

	@Override
	public int compare(PurchaseFundVo Vo1, PurchaseFundVo Vo2) {
		if (Vo1 != null && Vo2 != null) {
			if (Vo1.getPayDate().getTime() > Vo2.getPayDate().getTime()) {
				return -1;
			} else if (Vo1.getPayDate().getTime() < Vo2.getPayDate().getTime()) {
				return 1;
			} else {
				int i = Vo1.getProcInstId().compareTo(Vo2.getProcInstId());
				if (i > 0) {
					return -1;
				} else if (i < 0) {
					return 1;
				}
				return 0;
			}
		}
		return 0;
	}
}
