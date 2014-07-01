package com.wcs.base.view;

import com.wcs.base.model.BaseEntity;
import com.wcs.base.util.JSFUtils;

public abstract class ViewBaseBean<T extends BaseEntity> extends BaseBean<T> implements IBaseBean {
    private static final long serialVersionUID = 1L;

    public String save() {
        super.saveEntity();
        return listPage == null ? "/faces/debug/failed.xhtml" : listPage;
    }

    public String delete() {
        super.deleteEntity();
        return listPage == null ? "/faces/debug/failed.xhtml" : listPage;
    }

    public String findUserName() {
        Object obj = JSFUtils.getSession().get("userName");
        return obj != null ? obj.toString() : null;
    }
}
