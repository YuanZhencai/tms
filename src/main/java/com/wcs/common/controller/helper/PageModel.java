package com.wcs.common.controller.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * 
 * 延迟加载类(包括分页、排序等) 调用：lazyModel = new PageModel(List集合, true); //true表示排序
 * false表示不排序 注意要排序时 Model类属性必须是public的 而不是private的
 * 
 * */
public class PageModel<T extends IdModel> extends LazyDataModel<T> {

	private List<T> list;
	// 是否排序
	private boolean sort;

	public PageModel(List<T> list, boolean sort) {
		this.list = list;
		this.sort = sort;
	}

	public List<T> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, String> filters) {
		// sort
		if (sort) {
			if (sortField != null) {
				Collections.sort((List) list, new LazySorter(sortField,
						sortOrder));
			}
		}

		int size = list.size();
		setRowCount(size);

		if (size > pageSize) {
			try {
				return new ArrayList<T>(list.subList(first, first + pageSize));
			} catch (IndexOutOfBoundsException e) {
				return new ArrayList<T>(list.subList(first, first
						+ (size % pageSize)));
			}
		}
		return list;
	}

	public T getRowData(String rowKey) {
		for (T t : list) {
			if (t.getId().toString().equals(rowKey)) {
				return t;
			}
		}
		return null;
	}

	public Object getRowKey(T t) {
		return t.getId();
	}
}
