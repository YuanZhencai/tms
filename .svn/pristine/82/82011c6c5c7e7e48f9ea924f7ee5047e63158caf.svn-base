package com.wcs.common.controller;

import java.io.Serializable;
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
import org.primefaces.model.SortOrder;

import com.wcs.base.service.LoginService;
import com.wcs.common.controller.vo.TaxAuthorityVO;
import com.wcs.common.service.TaxAuthorityService;

@ManagedBean
@ViewScoped
public class TaxAuthorityBean implements Serializable {

	
	private static final long serialVersionUID = 5055100907085739747L;

	@EJB 
	private TaxAuthorityService taxAuthorityService;
	@EJB 
	private LoginService loginService;
	
	/**       
	 * 用于页面wepapp/btcbase/tax_authority/下的managedBean. 
	 */
	   
	private String queryName;
	private String queryAddress;
	private String queryState;
	private LazyDataModel<TaxAuthorityVO> lazyModelIndex;

	private String addName;
	private String addAddress;
	private String addZipCode;
	private String addTelephone;
	private String addLeaderName;
	private String addLeaderPosition; 
	private String addLeaderPhone;
	private String addContacterName;
	private String addContacterPosition;
	private String addContacterPhone;
	private String addState="N";
	
	//WebSphere 8 下,在用这个VO的时候,必须new一个示例出来,
	//虽然浪费,但不new会报错,updateDialog中的h:selectOneMenu组件的获取值会出错.
	private TaxAuthorityVO selectData=new TaxAuthorityVO();   
	
	public TaxAuthorityBean() {
		// 构造方法   
	}

	public void reset(){}
	// 查询方法
	public void queryPlan() {
		lazyModelIndex = new LazyDataModel<TaxAuthorityVO>() {
		
			private static final long serialVersionUID = 5472450185636472922L;
			private List<TaxAuthorityVO> bbv = null;

			@Override
			public List<TaxAuthorityVO> load(int first, int pageSize,
					String sortField, SortOrder sortOrder,
					Map<String, String> filters) {
				bbv = taxAuthorityService.queryDataPlan(queryName,
						queryAddress, queryState);
				int size = bbv.size();
				this.setRowCount(size);
				if (size > pageSize) {
					try {
						return bbv.subList(first, first + pageSize);
					} catch (IndexOutOfBoundsException e) {
						return bbv.subList(first, first + (size % pageSize));
					}
				} else {
					return bbv;
				}
			}

			@Override
			public Object getRowKey(TaxAuthorityVO object) {
				return object.getId();
			}

			@Override
			public TaxAuthorityVO getRowData(String rowKey) {
				for (TaxAuthorityVO b : bbv) {
					if (b.getId() == Integer.parseInt(rowKey)) {
						return b;
					}
				}
				return null;
			}
		};
	}

	// 添加方法
	public void addInfo() {
		taxAuthorityService.addTaxManage(addName, addAddress,
				addZipCode, addTelephone, addLeaderName, addLeaderPosition,
				addLeaderPhone, addContacterName, addContacterPosition,
				addContacterPhone, addState,loginService.getCurrentUserName());
		//成功执行方法,传参数给前台JS,点击确认就可以关闭窗口.  
		RequestContext.getCurrentInstance().addCallbackParam("addInfo", "yes");
	}
	
	//update
	public void modifyData(){
		taxAuthorityService.modifyData(this.selectData,loginService.getCurrentUserName());
		RequestContext.getCurrentInstance().addCallbackParam("modifyInfo", "yes");
	}
	
	//机关名称验证    
	public void checkTaxName(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		if ("".equals(value.toString())||(value.toString())==null) {
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, null, "请输入机关名称!"));
		}  else if(value.toString().length()>= 20){
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,null,"机关名称字节长度不能大于20字节."));
		}else{
			//避免sonar检查 
		}
	}
	//地址验证
	public void checkAddress(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		if ("".equals(value.toString())||(value.toString())==null) {
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, null, "请输入机关地址!"));
		}  else if(value.toString().length()>= 100){
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,null,"机关名称字节长度不能大于100字节."));
		}else{
			//避免sonar检查
		}
	}
	//邮编长度验证
	public void checkZipcode(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		if(value.toString().length()>= 10){
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,null,"邮编长度不能大于10字节."));
		}else{
			//避免sonar检查
		}
	}
	//电话号码长度验证
	public void checkPhone(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		if(value.toString().length()>= 20){
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,null,"您输入的号码有误."));
		}else{
			//避免sonar检查
		}
	}
	//其他长度的验证
	public void checkFile(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		if(value.toString().length()>= 20){
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,null,"人名/职位长度不能大与20字节."));
		}else{
			//避免sonar检查
		}
	}
	
	public void ajaxInfo(SelectEvent event){
		FacesMessage msg = new FacesMessage("Tax Selected", ((TaxAuthorityVO) event.getObject()).getTaxName());

        FacesContext.getCurrentInstance().addMessage(null, msg);
        
	}
	
	// getting and setting
	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public String getQueryAddress() {
		return queryAddress;
	}

	public void setQueryAddress(String queryAddress) {
		this.queryAddress = queryAddress;
	}

	public String getQueryState() {
		return queryState;
	}

	public void setQueryState(String queryState) {
		this.queryState = queryState;
	}

	public LazyDataModel<TaxAuthorityVO> getLazyModelIndex() {
		return lazyModelIndex;
	}

	public void setLazyModelIndex(
			LazyDataModel<TaxAuthorityVO> lazyModelIndex) {
		this.lazyModelIndex = lazyModelIndex;
	}

	public String getAddName() {
		return addName;
	}

	public void setAddName(String addName) {
		this.addName = addName;
	}

	public String getAddAddress() {
		return addAddress;
	}

	public void setAddAddress(String addAddress) {
		this.addAddress = addAddress;
	}

	public String getAddZipCode() {
		return addZipCode;
	}

	public void setAddZipCode(String addZipCode) {
		this.addZipCode = addZipCode;
	}

	public String getAddTelephone() {
		return addTelephone;
	}

	public void setAddTelephone(String addTelephone) {
		this.addTelephone = addTelephone;
	}

	public String getAddLeaderName() {
		return addLeaderName;
	}

	public void setAddLeaderName(String addLeaderName) {
		this.addLeaderName = addLeaderName;
	}

	public String getAddLeaderPosition() {
		return addLeaderPosition;
	}

	public void setAddLeaderPosition(String addLeaderPosition) {
		this.addLeaderPosition = addLeaderPosition;
	}

	public String getAddLeaderPhone() {
		return addLeaderPhone;
	}

	public void setAddLeaderPhone(String addLeaderPhone) {
		this.addLeaderPhone = addLeaderPhone;
	}

	public String getAddContacterName() {
		return addContacterName;
	}

	public void setAddContacterName(String addContacterName) {
		this.addContacterName = addContacterName;
	}

	public String getAddContacterPosition() {
		return addContacterPosition;
	}

	public void setAddContacterPosition(String addContacterPosition) {
		this.addContacterPosition = addContacterPosition;
	}

	public String getAddContacterPhone() {
		return addContacterPhone;
	}

	public void setAddContacterPhone(String addContacterPhone) {
		this.addContacterPhone = addContacterPhone;
	}

	public String getAddState() {
		return addState;
	}

	public void setAddState(String addState) {
		this.addState = addState;
	}

	public TaxAuthorityVO getSelectData() {
		return selectData;
	}

	public void setSelectData(TaxAuthorityVO selectData) {
		this.selectData = selectData;
	}

}
