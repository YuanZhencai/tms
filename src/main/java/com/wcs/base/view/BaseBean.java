package com.wcs.base.view;

import java.io.Serializable;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wcs.base.model.BaseEntity;
import com.wcs.base.service.EntityService;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.Validate;
import com.wcs.base.util.view.ReflectionUtils;

public class BaseBean<T extends BaseEntity> implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Log logger = LogFactory.getLog(getClass());

	protected String inputPage = null;
	protected String listPage = null;
	protected String viewPage = null;

	@Inject
	protected EntityService entityService;
	private Long id;
	// currentEntity
	private T instance;

	/**
	 * 
	 * @param listPage
	 *            如果为null，则不改变LIST页面
	 * @param inputPage
	 *            如果为null，则不改变INPUT页面
	 * @param viewPage
	 *            如果为null，则不改变VIEW页面
	 */
	public void setupPage(String listPage, String inputPage, String viewPage) {
		if (!StringUtils.isEmpty(listPage)) {
			this.listPage = listPage;
		}
		if (!StringUtils.isEmpty(inputPage)) {
			this.inputPage = inputPage;
		}
		if (!StringUtils.isEmpty(viewPage)) {
			this.viewPage = viewPage;
		}
	}

	protected void initInstance() {
		if (instance == null) {
			if (id != null) {
				instance = loadInstance();
			} else {
				instance = createInstance();
			}
		}
	}

	public T loadInstance() {
		return entityService.find(getClassType(), getId());
	}

	public T createInstance() {
		try {
			return getClassType().newInstance();
		} catch (Exception e) {
			logger.error("loadInstance方法异常", e);
		}
		return null;
	}

	/**
	 * 判断实体 instance 的id是否有值
	 * 
	 * @return
	 */
	public boolean idIsEmpty() {
		// 修改人：liaowei
		return getInstance().getId() != null && getInstance().getId() > 0l;
	}

	/**
	 * 判断 instance 是否处于EJB容器托管状态
	 * 
	 * @return
	 */
	public boolean isManaged() {
		return entityService.isManaged(getInstance());
	}

	public void saveEntity() {
		Object obj = JSFUtils.getSession().get("userName");
		String acount = obj != null ? obj.toString() : null;
		if (idIsEmpty()) {
			this.instance.setUpdatedBy(acount);
			entityService.update(getInstance());
		} else {
			if (obj != null) {
				this.instance.setCreatedBy(acount);
			}
			// 修改人：liaowei
			this.getInstance().setId(null);
			entityService.create(getInstance());
			Validate.isTrue(entityService.isManaged(getInstance()));
		}
	}

	public void deleteEntity() {
		T entity = getInstance();
		entity.setDefunctInd("Y");
		entityService.update(entity);
	}

	private Class<T> getClassType() {
		return ReflectionUtils.getSuperClassGenricType(getClass());
	}

	// ----------------------------- set & get
	// --------------------------------//

	public T getInstance() {
		initInstance();
		return instance;
	}

	public void setInstance(T instance) {
		this.instance = instance;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInputPage() {
		return inputPage;
	}

	public void setInputPage(String inputPage) {
		this.inputPage = inputPage;
	}

	public String getListPage() {
		return listPage;
	}

	public void setListPage(String listPage) {
		this.listPage = listPage;
	}

	public String getViewPage() {
		return viewPage;
	}

	public void setViewPage(String viewPage) {
		this.viewPage = viewPage;
	}

}
