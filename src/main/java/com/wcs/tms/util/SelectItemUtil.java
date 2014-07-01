package com.wcs.tms.util;

import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SelectItemUtil {

	/**
	 * 将 Dict 实体列表转为一个 List<SelectItem>中的 SelectItem 为JSF 的类，它包含 SelectBox 的
	 * value/caption，供标签 <f:selectItem> 和 <f:selectItems> 使用
	 * 
	 * @param dicts
	 *            Dict 实体列表
	 * @return List<SelectItem>对象
	 */
	public static List<SelectItem> arrayToListSelectItem(List dicts) {
		List<SelectItem> items = Lists.newArrayList();
		if (dicts != null) {
			for (int i = 0; i < dicts.size(); i++) {
				Object[] row = (Object[]) dicts.get(i);
				items.add(new SelectItem(row[0], row[1] + ""));
			}
		}

		return items;
	}

	/**
	 * 将 Dict 实体列表转为一个Map， Map的 key 为 SelectBox 的caption，Map的 value 为 SelectIBox
	 * 的 value，供标签 <f:selectItem> 和 <f:selectItems> 使用
	 * 
	 * @param dicts
	 * @return
	 */
	public static Map<Object, Object> arrayToMap(List dicts) {
		Map<Object, Object> map = Maps.newHashMapWithExpectedSize(3);

		for (int i = 0; i < dicts.size(); i++) {
			Object[] row = (Object[]) dicts.get(i);
			map.put(row[0], row[1]);
		}
		return map;
	}
}
