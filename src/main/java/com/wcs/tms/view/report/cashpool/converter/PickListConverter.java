package com.wcs.tms.view.report.cashpool.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

import com.wcs.tms.view.report.cashpool.vo.CashPoolStrId;

@FacesConverter(value = "pickListConverter")
public class PickListConverter<T extends CashPoolStrId> implements Converter {
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent uIComponent, String stringValue) {
		Object tValue = null;
		if (uIComponent instanceof PickList) {
			String personId = null;
			Object dualList = ((PickList) uIComponent).getValue();
			DualListModel<T> dualListModel = (DualListModel<T>) dualList;
			// 移动的值在source中
			for (T obj : dualListModel.getSource()) {
				personId = obj.getId();
				if (stringValue.equals(personId)) {
					tValue = obj;
					break;
				}
			}
			if (tValue == null) {
				// 移动的值在target中
				for (T obj : dualListModel.getTarget()) {
					personId = obj.getId();
					if (stringValue.equals(personId)) {
						tValue = obj;
						break;
					}
				}
			}
		}
		return tValue;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent uIComponent, Object objectValue) {
		String personStrId = null;
		T tObj = (T) objectValue;
		personStrId = tObj.getId().toString();
		return personStrId;
	}

}
