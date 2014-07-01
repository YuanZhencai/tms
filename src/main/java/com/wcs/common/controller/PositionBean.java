package com.wcs.common.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import com.wcs.base.service.LoginService;
import com.wcs.common.controller.helper.PageModel;
import com.wcs.common.controller.vo.PositionCompanyVO;
import com.wcs.common.model.Position;
import com.wcs.common.model.Positionorg;
import com.wcs.common.service.PositionService;

/**
 * Project: btcbase Description: position backing bean. Copyright (c) 2012 Wilmar
 * Consultancy Services All Rights Reserved.
 * 
 * @author <a href="mailto:guanluyong@wcs-global.com">Mr.Guan</a>
 */
@ManagedBean
@ViewScoped
public class PositionBean implements Serializable {

	@EJB
	private LoginService loginService;

	// 当前操作的 position
	private Position position;
	// position option service
	// @Transient
	@EJB
	private PositionService positionService;
	// query condition
	private Map<String, String> query = new HashMap<String, String>(4);
	// position query list
	private LazyDataModel<Position> positionLazyModel;

	// belong's company list
	private LazyDataModel<PositionCompanyVO> companyLazyModel;
	// position company VO
	private PositionCompanyVO[] positionCompany;
	// current position company
	private List<PositionCompanyVO> belongedCompany;
	// 缓存list
	private List<PositionCompanyVO> cacheBelongedCompany = new ArrayList<PositionCompanyVO>();

	// not belong this position's company query Map
	private Map<String, String> companyQuery = new HashMap<String, String>(4);
	// other company list
	private LazyDataModel<PositionCompanyVO> notBelongedCompany;
	// not belong this position's company VO(selected)
	private PositionCompanyVO[] notPositionCompany;

	// constructor
	public PositionBean() {
	}

	public void reset() {
		query.clear();
	}

	/**
	 * < p>
	 * Description: query positions
	 * </p>
	 */
	public void search() {
		positionLazyModel = new PageModel(
				positionService.searchPosition(query), false);
	}

	/**
	 * <p>
	 * Description: add an new position
	 * </p>
	 */
	public void addPosition() {
		position.setId(null);
		// set login
		position.setCreatedBy(loginService.getCurrentUserName()); 
		// name
		position.setCreatedDatetime(new Date());
		position.setUpdatedBy(loginService.getCurrentUserName());
		position.setUpdatedDatetime(new Date());
		positionService.insertPosition(position);
		search();
		RequestContext.getCurrentInstance().addCallbackParam("issucc", "yes");
	}

	/**
	 * <p>
	 * Description: valid Position info is right
	 * </p>
	 */
	public void validPosition(FacesContext context, UIComponent component,
			java.lang.Object value) throws ValidatorException {
		if ("newName".equals(component.getId())
				|| "updateName".equals(component.getId())) {
			if (value == null || "".equals(value.toString().trim())) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "岗位名称：", "不允许为空或空格"));
			} else if (value.toString().length() > 20) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "岗位名称：", "长度不能超过20个字符"));
			}
		} else if ("newDesc".equals(component.getId())
				|| "updateDesc".equals(component.getId())) {
			if (value != null && value.toString().length() > 200) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "岗位描述：", "长度不能超过200个字符"));
			}
		} else if ("newDefunctInd".equals(component.getId())
				|| "updateDefunctInd".equals(component.getId())) {
			if (value == null || "".equals(value.toString())) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "是否有效：", "为必选项"));
			}
		} else {
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "未知错误：", "未找到的组件验证"));
		}
	}

	/**
	 * <p>
	 * Description: modify an exists position
	 * </p>
	 */
	public void updatePosition() {
		position.setUpdatedBy(loginService.getCurrentUserName());
		position.setUpdatedDatetime(new Date());
		positionService.updatePosition(position);
		search();
		RequestContext.getCurrentInstance().addCallbackParam("issucc", "yes");
	}

	/**
	 * <p>
	 * Description: selected
	 * </p>
	 */
	public void onSelectedPosition(SelectEvent event) {
		this.position = (Position) event.getObject();
	}

	/**
	 * <p>
	 * Description: null implements
	 * </p>
	 */
	public void editBelong() {

	}

	/**
	 * <p>
	 * Description: find current position's company which belong this
	 * </p>
	 */
	public void searchBelongCompany() {
		belongedCompany = positionService.findCompanys(position.getId());
		for (PositionCompanyVO p : belongedCompany) {
			cacheBelongedCompany.add(p);
		}

		if (!belongedCompany.isEmpty()) {
			positionCompany = new PositionCompanyVO[belongedCompany.size()];
			int i = 0;
			for (PositionCompanyVO pc : belongedCompany) {
				positionCompany[i++] = pc;
			}
		}
		companyLazyModel = new PageModel(belongedCompany, false);
	}

	/**
	 * <p>
	 * Description: find other not belong this position's company
	 * </p>
	 */
	public void searchOtherCompany() {
		notBelongedCompany = new PageModel(positionService.findOtherCompanys(
				position, companyQuery), false);
	}

	/**
	 * <p>
	 * Description: merge other company
	 * </p>
	 */
	public void mergeVO() {
		if (notPositionCompany == null || notPositionCompany.length == 0) {
			return;
		}
		// add
		if (positionCompany == null) {
			positionCompany = new PositionCompanyVO[0];
		}

		// re count notPositionCompany's length
		List<PositionCompanyVO> tempNot = new ArrayList<PositionCompanyVO>();
		for (PositionCompanyVO pc : notPositionCompany) {
			boolean f = false;
			for (PositionCompanyVO p : belongedCompany) {
				if (p.getOid() == pc.getOid()) {
					f = true;
					break;
				}
			}
			if (!f) {
				belongedCompany.add(pc);
				tempNot.add(pc);
			}
		}
		if (tempNot.isEmpty()) {
			return;
		}
		int i = positionCompany.length;
		positionCompany = Arrays.copyOf(positionCompany, i + tempNot.size());

		for (PositionCompanyVO pc : tempNot) {
			positionCompany[i++] = pc;
		}
		companyLazyModel = new PageModel(belongedCompany, false);
	}

	/**
	 * <p>
	 * Description: Before add position and org , clear lazy model
	 * </p>
	 */
	public void beforeAddPositionorg() {
		companyQuery.clear();
		notBelongedCompany = null;
		notPositionCompany = null;
	}

	/**
	 * <p>
	 * Description: add position company relations
	 * </p>
	 */
	public void addPositionorg() {
		List<Positionorg> newPositionorg = new ArrayList<Positionorg>();
		Positionorg porg = null;
		for (PositionCompanyVO pc : positionCompany) {
			if (pc.getPositionorgId() == null) {
				porg = new Positionorg();
				porg.setOid(pc.getOid());
				porg.setPosition(position);
				// login.user
				porg.setCreatedBy(loginService.getCurrentUserName()); 
				porg.setCreatedDatetime(new Date());
				porg.setDefunctInd("N");
				porg.setUpdatedBy(loginService.getCurrentUserName());
				porg.setUpdatedDatetime(new Date());
				newPositionorg.add(porg);
			}
			for (PositionCompanyVO p : belongedCompany) {
				if (p.getOid() == pc.getOid()) {
					belongedCompany.remove(p);
					break;
				}
			}
		}

		for (PositionCompanyVO p : belongedCompany) {
			boolean f = false;
			for (PositionCompanyVO pc : cacheBelongedCompany) {
				if (p.getOid() == pc.getOid()) {
					f = true;
					break;
				}
			}
			if (!f) {
				belongedCompany.remove(p);
			}
		}
		positionService.addPostionorg(newPositionorg, belongedCompany);
	}

	/**
	 * <p>
	 * Description: update remote data
	 * </p>
	 */
	public void synchData() {

	}

	// Getter & Setter
	public Map<String, String> getQuery() {
		return query;
	}

	public void setQuery(Map<String, String> query) {
		this.query = query;
	}

	public LazyDataModel<Position> getPositionLazyModel() {
		return positionLazyModel;
	}

	public void setPositionLazyModel(LazyDataModel<Position> positionLazyModel) {
		this.positionLazyModel = positionLazyModel;
	}

	public Position getPosition() {
		if (position == null) {
			position = new Position();
			position.setDefunctInd("N");
		}
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public LazyDataModel<PositionCompanyVO> getCompanyLazyModel() {
		return companyLazyModel;
	}

	public void setCompanyLazyModel(
			LazyDataModel<PositionCompanyVO> companyLazyModel) {
		this.companyLazyModel = companyLazyModel;
	}

	public PositionCompanyVO[] getPositionCompany() {
		return positionCompany;
	}

	public void setPositionCompany(PositionCompanyVO[] positionCompany) {
		this.positionCompany = positionCompany;
	}

	public List<PositionCompanyVO> getBelongedCompany() {
		return belongedCompany;
	}

	public void setBelongedCompany(List<PositionCompanyVO> belongedCompany) {
		this.belongedCompany = belongedCompany;
	}

	public Map<String, String> getCompanyQuery() {
		return companyQuery;
	}

	public void setCompanyQuery(Map<String, String> companyQuery) {
		this.companyQuery = companyQuery;
	}

	public LazyDataModel<PositionCompanyVO> getNotBelongedCompany() {
		return notBelongedCompany;
	}

	public void setNotBelongedCompany(
			LazyDataModel<PositionCompanyVO> notBelongedCompany) {
		this.notBelongedCompany = notBelongedCompany;
	}

	public PositionCompanyVO[] getNotPositionCompany() {
		return notPositionCompany;
	}

	public void setNotPositionCompany(PositionCompanyVO[] notPositionCompany) {
		this.notPositionCompany = notPositionCompany;
	}

	public List<PositionCompanyVO> getCacheBelongedCompany() {
		return cacheBelongedCompany;
	}

	public void setCacheBelongedCompany(
			List<PositionCompanyVO> cacheBelongedCompany) {
		this.cacheBelongedCompany = cacheBelongedCompany;
	}

}
