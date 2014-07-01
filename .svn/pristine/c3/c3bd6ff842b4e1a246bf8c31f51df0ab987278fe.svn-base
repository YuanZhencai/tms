package com.wcs.tms.view.process.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.wcs.base.service.EntityService;
import com.wcs.base.util.MathUtil;
import com.wcs.tms.model.ProcessMap;
import com.wcs.tms.service.process.common.ProcessNumberSelectService;
import com.wcs.tms.service.report.cashpool.CashPoolReportService;
/**
 * 
 * <p>Project: tms</p>
 * <p>Description:流程实例号多选过滤选择Bean</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yanchangjing@wcs-global.com">Yan Changjing</a>
 */
@ManagedBean
@ViewScoped
public class ProcessNumberSelectBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	ProcessNumberSelectService processNumberSelectService;
	@Inject
	EntityService entityService;
	@Inject
	CashPoolReportService cashPoolReportService;
	
	// 列表数据
//	private List<ProcessMap> dataModel = new ArrayList<ProcessMap>() ;
	private LazyDataModel<ProcessMap> dataModel;
	// 过滤条件
	private String tmsNumber;
	// 当前页选中流程map
	private ProcessMap[] selectedProcessMaps;
	// 每次翻页首先获取之前所选数据
	private List<ProcessMap> selectDataBefore = new ArrayList<ProcessMap>();
	// 所有选中数据
	private List<ProcessMap> selectedAllProcessMaps = new ArrayList<ProcessMap>();
	
	private String method;
	
	@PostConstruct
	public void initProcessNumberSelectBean(){
		filterProcessMap();
	}
	
	/**
	 * 翻页时保存上一页已选择数据
	 */
	public void saveLastPageSelection(){
		try {
			// 翻页前 将之前所选数据 加上本页所选数据(某次翻页可能选择相同数据)
			for(ProcessMap pm : selectedProcessMaps){
				if(!selectDataBefore.contains(pm)){
					selectDataBefore.add(pm);
				}
			}
//			getSelectDataBefore().addAll(Arrays.asList(selectedProcessMaps));
//			JSFUtils.getRequest().setAttribute("selectDataBefore",getSelectDataBefore());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void filterProcessMap(){
		try {
	//		setDataModel(processNumberSelectService.filterProcessMap(tmsNumber));
			dataModel = new LazyDataModel<ProcessMap>() {
				private static final long serialVersionUID = 1L;

				@Override
				public List<ProcessMap> load(int first, int pageSize, String sortField,
						SortOrder sortOrder, Map<String, String> filters) {
					// 查出所有现金池 流程实例编号
					StringBuilder sql = new StringBuilder("select * from PROCESS_MAP p where p.DEFUNCT_IND = 'N'") ;
					sql.append(" and p.PID_FN in (select distinct PROC_INST_ID from VW_CASHPOOL where DEFUNCT_IND = 'N' and TERMINATE_FLAG = 'N')");
					if(!MathUtil.isEmptyOrNull(tmsNumber)){
						sql.append(" and lower(p.PID_TMS) like '%" + tmsNumber.toLowerCase()+ "%'") ;
					}
					// 查出所有满足条件的个数
					StringBuilder sql2 = new StringBuilder("select COUNT(*) from ("+sql.toString()+")");
					Integer rowCount = cashPoolReportService.getRowCountBySql(sql2.toString());
					this.setRowCount(rowCount);
					List<Object[]> processMapRows = processNumberSelectService.getPagedRowsBySql(sql.toString(),first,pageSize);
					List<ProcessMap> processMaps = processNumberSelectService.transferDataToProcessMap(processMapRows);
					return processMaps;
				}

			};
//			List<String> cashPoolProcessNums = processNumberSelectService.findAllCashPoolProcessNums();
//			dataModel = entityService.findModel(jpql.toString(), cashPoolProcessNums);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void clear(){
		tmsNumber = "";
		selectedAllProcessMaps.clear();
		selectDataBefore.clear();
		filterProcessMap();
	}
	
	/**
	 * 执行上级申请页面方法
	 */
	public void beforeClose(){
		// 关闭控件之前已选所有数据
		selectedAllProcessMaps.addAll(selectDataBefore);
		for(ProcessMap pm : selectedProcessMaps){
			if(!selectDataBefore.contains(pm)){
				selectedAllProcessMaps.add(pm);
			}
		}
//		selectedAllProcessMaps.addAll(Arrays.asList(selectedProcessMaps));
		Application application = FacesContext.getCurrentInstance().getApplication() ;
		ExpressionFactory expressionFactory = application.getExpressionFactory() ;
		ELContext elContext = FacesContext.getCurrentInstance().getELContext() ;
		MethodExpression me = expressionFactory.createMethodExpression(elContext, "#{"+getMethod()+"}", Void.class, new Class[]{}) ;
		try{
			me.invoke(elContext, null) ;
		}catch(Exception e){
			e.printStackTrace() ;
		}
	}

	public String getTmsNumber() {
		return tmsNumber;
	}

	public void setTmsNumber(String tmsNumber) {
		this.tmsNumber = tmsNumber;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public ProcessMap[] getSelectedProcessMaps() {
		return selectedProcessMaps;
	}

	public void setSelectedProcessMaps(ProcessMap[] selectedProcessMaps) {
		this.selectedProcessMaps = selectedProcessMaps;
	}

	public LazyDataModel<ProcessMap> getDataModel() {
		return dataModel;
	}

	public void setDataModel(LazyDataModel<ProcessMap> dataModel) {
		this.dataModel = dataModel;
	}

	public List<ProcessMap> getSelectedAllProcessMaps() {
		return selectedAllProcessMaps;
	}

	public void setSelectedAllProcessMaps(List<ProcessMap> selectedAllProcessMaps) {
		this.selectedAllProcessMaps = selectedAllProcessMaps;
	}

	public List<ProcessMap> getSelectDataBefore() {
		return selectDataBefore;
	}

	public void setSelectDataBefore(List<ProcessMap> selectDataBefore) {
		this.selectDataBefore = selectDataBefore;
	}
	
}
