package com.wcs.tms.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.wcs.base.model.IdEntity;
import com.wcs.common.controller.helper.LazySorter;

public class CustomPageModel<T extends IdEntity> extends LazyDataModel<T> {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private List<T> list;
	// 是否排序
	private boolean sort;

	public CustomPageModel(List<T> list, boolean sort) {
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
