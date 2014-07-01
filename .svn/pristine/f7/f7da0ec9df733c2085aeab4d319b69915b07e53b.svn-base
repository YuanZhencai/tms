package com.wcs.common.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;

import com.wcs.base.service.LoginService;
import com.wcs.base.util.MathUtil;
import com.wcs.common.controller.helper.PageModel;
import com.wcs.common.controller.vo.RoleVo;
import com.wcs.common.controller.vo.UsermstrFormItemsVo;
import com.wcs.common.controller.vo.UsermstrVo;
import com.wcs.common.model.O;
import com.wcs.common.model.P;
import com.wcs.common.model.PU;
import com.wcs.common.model.Usermstr;
import com.wcs.common.model.Userrole;
import com.wcs.common.service.UserService;
import com.wcs.tms.model.Company;
import com.wcs.tms.service.process.dailypayloantran.DailyPayLoanTranService;
import com.wcs.tms.service.system.company.CompanyTmsService;
import com.wcs.tms.util.MessageUtils;
import com.wcs.tms.view.process.common.CompanySelectBean;

@ManagedBean(name = "userBean")
@ViewScoped
public class UserBean {
	
	
	private static final Log log = LogFactory.getLog(UserBean.class);
	@EJB
	private UserService userService;
	@EJB
	private LoginService loginService;
	@EJB
	private CompanyTmsService companyTmsService;

	private UsermstrFormItemsVo usermstrFormItemsVo = new UsermstrFormItemsVo();
	private Usermstr usermstr = new Usermstr();
	private UsermstrVo selectedUsermstrVo;
	private List<UsermstrVo> usermstrVos;
	private UsermstrVo[] selectedUsermstrVos;
	private LazyDataModel<UsermstrVo> lazyUsermstrVoModel;

	private P p = new P();
	private Map<String, Long> roleVos;
	private List<Long> selectedRoleVos;
	private List<RoleVo> rolemstrList = new ArrayList<RoleVo>();

	private String method;
	private String bukrsName;
	private String tempAdAccount;
	private List<SelectItem> companySelect = new ArrayList<SelectItem>();
	@ManagedProperty(value = "#{companySelectBean}")
	private CompanySelectBean companySelectBean;
	private Company company;
	
	public UserBean() {

	}

	@PostConstruct
	public void init() {
		rolemstrList = this.userService.getAllRoleVo();
		companySelectBean.initCompany();
		search();
		List<O> list = userService.getAllO();
		for (O o : list) {
			SelectItem si = new SelectItem(o.getBukrs(), o.getStext());
			companySelect.add(si);
		}
		
	}

	public void search() {
		usermstrVos = userService.getAllUsermstrVo(usermstrFormItemsVo);
		lazyUsermstrVoModel = new PageModel<UsermstrVo>(usermstrVos, false);
	}

	public void reset() {
		usermstrFormItemsVo = new UsermstrFormItemsVo();
	}
	
	public void updateUser() {
		this.usermstr = selectedUsermstrVo.getUsermstr();
		this.p = selectedUsermstrVo.getP();

		this.bukrsName = selectedUsermstrVo.getO().getStext();
		this.tempAdAccount = usermstr.getAdAccount();
		this.company = companyTmsService.getCompanyBySapcode(selectedUsermstrVo.getO().getBukrs());
	}

	public void valid(FacesContext context, UIComponent component,
			java.lang.Object value) throws ValidatorException {
		if ("adAccount".equals(component.getId())) {
			if (value == null || "".equals(value.toString().trim())) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "用户帐号：", "不允许为空或空格"));
			} else if (value.toString().length() > 50) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "用户帐号：", "长度不能超过50个字符"));
			} else {
				if ("update".equals(this.method)) {
					if (!value.toString().equals(this.tempAdAccount)) {
						int num = this.userService.getUserCount(value
								.toString().trim());
						if (num != 0) {
							throw new ValidatorException(new FacesMessage(
									FacesMessage.SEVERITY_ERROR, "用户帐号：",
									"帐号已经存在，请重新输入"));
						}
					}
				} else if ("insert".equals(this.method)) {
					int num = this.userService.getUserCount(value.toString()
							.trim());
					if (num != 0) {
						throw new ValidatorException(new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "用户帐号：",
								"帐号已经存在，请重新输入"));
					}
				}
			}
		}
		/*
			验证新增时AD帐号是否合法
			1. 是否为空
			2. 是否超过长度
			3. 是否存在重复数据
		*/
		if("adAccount1".equals(component.getId())) {
			if (value == null || "".equals(value.toString().trim())) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "用户帐号：", "不允许为空或空格"));
			} else if (value.toString().length() > 50) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "用户帐号：", "长度不能超过50个字符"));
			} else {
				int num = this.userService.getUserCount(value.toString()
						.trim());
				if (num != 0) {
					throw new ValidatorException(new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "用户帐号：",
							"帐号已经存在，请重新输入"));
				}
			}
		}
		if ("idtentityId".equals(component.getId())) {
			if (value == null || "".equals(value.toString().trim())) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "证件号：", "不允许为空或空格"));
			} else if (value.toString().length() > 50) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "证件号：", "长度不能超过50个字符"));
			}
		}

	}

	public void userRole() {
		roleVos = new HashMap<String, Long>();
		selectedRoleVos = new ArrayList<Long>();

		this.usermstr = selectedUsermstrVo.getUsermstr();
		this.p = selectedUsermstrVo.getP();

		List<RoleVo> rolemstrList = this.userService.getAllRoleVo();
		if (rolemstrList.size() != 0) {
			for (RoleVo rv : rolemstrList) {
				roleVos.put(rv.getRolemstr().getName(), rv.getRolemstr()
						.getId());
			}
		}

		List<Userrole> userroleList = this.usermstr.getUserroles();
		Userrole ur = null;
		if (userroleList != null && userroleList.size() != 0) {
			for (int i = 0; i < userroleList.size(); i++) {
				ur = userroleList.get(i);
				if ("N".equals(ur.getDefunctInd().trim())) {
					selectedRoleVos.add(ur.getRolemstr().getId());
				}
			}
		}

	}
	
	public void saveUserRole() {

		FacesContext context = FacesContext.getCurrentInstance();
		boolean b = this.userService.saveUserRole(selectedRoleVos,
				this.selectedUsermstrVo.getUsermstr(),
				loginService.getCurrentUserName());
		if (b) {
			MessageUtils.addSuccessMessage("msg", "用户角色分配成功");
		} else {
			MessageUtils.addErrorMessage("msg", "用户角色分配失败");
		}
		search();

	}
	
	/**
	 * 打开新增用户弹出框
	 */
	public void forwordSaveUser() {
		this.usermstr = new Usermstr();
		this.p = new P();
		this.company = new Company();
	}
	
	/**
	 * 更新用户信息
	 */
	public void updateUsermstr() {
		try{
			this.usermstr.setUpdatedBy(loginService.getCurrentUserName());
			this.usermstr.setUpdatedDatetime(new Date());
			
			if(StringUtils.isNotBlank(company.getSapCode())) {
				p.setBukrs(company.getSapCode());
			}
			
			userService.updateUser(usermstr);
			userService.updatePerson(p);
			
			search();
			RequestContext.getCurrentInstance().addCallbackParam("issucc", "yes");
			MessageUtils.addSuccessMessage("msg", "更新用户信息成功");
		}catch (Exception e) {
			log.error("updateUsermstr 方法 更新用户信息", e);
            MessageUtils.addErrorMessage("msg", "更新用户信息失败");
		}
	}
	
	/**
	 * 新增用户信息
	 * ID生成规则 SAP代码+#P+SAP代码+000000+序列
	 * 新增Usermaster表记录、新增P表记录以及PU表记录
	 */
	public void saveUsermstr() {
		usermstr.setCreatedBy(loginService.getCurrentUserName());
		usermstr.setCreatedDatetime(new Date());
		usermstr.setUpdatedBy(loginService.getCurrentUserName());
		usermstr.setUpdatedDatetime(new Date());
		
		String bukrs = company.getSapCode();
		if(StringUtils.isNotBlank(bukrs)) {
			String tempSapCode = bukrs + "##P" + bukrs;
			String pid = userService.getPersonMaxID(tempSapCode);
			String pernr = "";
			if(StringUtils.isNotBlank(pid)) {
				pernr = tempSapCode + MathUtil.idAddOne(pid.substring(tempSapCode.length()));
			}else {
				pernr = tempSapCode + "000001";
			}
			usermstr.setPernr(pernr);
			
			userService.saveUser(usermstr);
			
			p.setId(pernr);
			p.setBukrs(bukrs);
			
			userService.savePerson(p);
			
			PU pu = new PU();
			pu.setId(usermstr.getAdAccount());
			pu.setPernr(pernr);
			pu.setDefunctInd("N");
			
			userService.savePersonUser(pu);
			
			search();
			RequestContext.getCurrentInstance().addCallbackParam("issucc", "yes");
			MessageUtils.addSuccessMessage("msg", "新增用户成功");
		}else {
			MessageUtils.addErrorMessage("errorSaveMsg", "请选择公司");
		}
		
	}
	
	
	/**
	 * <p>Description: 用户启用/禁用</p>
	 */
	public void disable(){
		String status = usermstr.getDefunctInd();
		if(StringUtils.isNotBlank(status)){
			if("Y".equals(status)){
			    //禁用
				usermstr.setDefunctInd("N");
			}else{
			    //启用
				usermstr.setDefunctInd("Y");
			}
			try {
				userService.updateUser(usermstr);
				search();
				MessageUtils.addSuccessMessage("msg", ("N".equals(status)? "禁用" : "启用") + "成功");
			} catch (Exception e) {
				MessageUtils.addErrorMessage("msg", ("N".equals(status)? "禁用" : "启用") + "失败");
			}
		}else{
			MessageUtils.addErrorMessage("msg", "保存用户信息失败");
		}
	}
	
	public void clear() {
		company = new Company();
		companySelectBean.clear();
	}
	
	/**
	 * 页面将选择的公司传到公司名称框中
	 */
	public void getSelectedCompany() {
		company = companySelectBean.getSelectedCompany();
	}

	public Map<String, Long> getRoleVos() {
		return roleVos;
	}

	public void setRoleVos(Map<String, Long> roleVos) {
		this.roleVos = roleVos;
	}

	public List<Long> getSelectedRoleVos() {
		return selectedRoleVos;
	}

	public void setSelectedRoleVos(List<Long> selectedRoleVos) {
		this.selectedRoleVos = selectedRoleVos;
	}

	public UsermstrFormItemsVo getUsermstrFormItemsVo() {
		return usermstrFormItemsVo;
	}

	public void setUsermstrFormItemsVo(UsermstrFormItemsVo usermstrFormItemsVo) {
		this.usermstrFormItemsVo = usermstrFormItemsVo;
	}

	public List<UsermstrVo> getUsermstrVos() {
		return usermstrVos;
	}

	public void setUsermstrVos(List<UsermstrVo> usermstrVos) {
		this.usermstrVos = usermstrVos;
	}

	public Usermstr getUsermstr() {
		return usermstr;
	}

	public void setUsermstr(Usermstr usermstr) {
		this.usermstr = usermstr;
	}

	public P getP() {
		return p;
	}

	public void setP(P p) {
		this.p = p;
	}

	public UsermstrVo getSelectedUsermstrVo() {
		return selectedUsermstrVo;
	}

	public void setSelectedUsermstrVo(UsermstrVo selectedUsermstrVo) {
		this.selectedUsermstrVo = selectedUsermstrVo;
	}

	public UsermstrVo[] getSelectedUsermstrVos() {
		return selectedUsermstrVos;
	}

	public void setSelectedUsermstrVos(UsermstrVo[] selectedUsermstrVos) {
		this.selectedUsermstrVos = selectedUsermstrVos;
	}

	public LazyDataModel<UsermstrVo> getLazyUsermstrVoModel() {
		return lazyUsermstrVoModel;
	}

	public void setLazyUsermstrVoModel(
			LazyDataModel<UsermstrVo> lazyUsermstrVoModel) {
		this.lazyUsermstrVoModel = lazyUsermstrVoModel;
	}

	public List<RoleVo> getRolemstrList() {
		return rolemstrList;
	}

	public void setRolemstrList(List<RoleVo> rolemstrList) {
		this.rolemstrList = rolemstrList;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getBukrsName() {
		return bukrsName;
	}

	public void setBukrsName(String bukrsName) {
		this.bukrsName = bukrsName;
	}

	public String getTempAdAccount() {
		return tempAdAccount;
	}

	public void setTempAdAccount(String tempAdAccount) {
		this.tempAdAccount = tempAdAccount;
	}

	public List<SelectItem> getCompanySelect() {
		return companySelect;
	}

	public void setCompanySelect(List<SelectItem> companySelect) {
		this.companySelect = companySelect;
	}

	public CompanySelectBean getCompanySelectBean() {
		return companySelectBean;
	}

	public void setCompanySelectBean(CompanySelectBean companySelectBean) {
		this.companySelectBean = companySelectBean;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
}
