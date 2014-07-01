package com.wcs.base.service;

import org.primefaces.model.LazyDataModel;

import com.wcs.base.model.IdEntity;



public abstract class BasePageDataModel<T extends IdEntity> extends LazyDataModel<T> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Object getRowKey(T entity) {
        return entity.getId();
    }

    @Override
    public int getPageSize() {
        return 10;
    }

}
