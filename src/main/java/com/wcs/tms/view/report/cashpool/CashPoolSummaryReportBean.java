package com.wcs.tms.view.report.cashpool;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;

import com.wcs.tms.service.report.cashpool.CashPoolSummaryReportService;
import com.wcs.tms.view.report.cashpool.util.CashPoolDateUtil;
import com.wcs.tms.view.report.cashpool.util.CashPoolExcelUtil;
import com.wcs.tms.view.report.cashpool.vo.CashCompanyItemVo;
import com.wcs.tms.view.report.cashpool.vo.CashCompanyVo;
import com.wcs.tms.view.report.cashpool.vo.CashPoolColumnVo;
import com.wcs.tms.view.report.cashpool.vo.CashPoolCompanyVo;
import com.wcs.tms.view.report.cashpool.vo.CashPoolItemVo;
import com.wcs.tms.view.report.cashpool.vo.CashPoolPageModelObj;
import com.wcs.tms.view.report.cashpool.vo.CashPoolRcfkVo;
import com.wcs.tms.view.report.cashpool.vo.CashPoolZjmyscVo;

@ManagedBean(name = "cashPoolSummaryReportBean")
@ViewScoped
public class CashPoolSummaryReportBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(getClass());
	private static final String TYPE_KEY_COMPANY = "company";// 公司
	private static final String TYPE_KEY_ITEM = "item";// 品项
	private static final String TYPE_KEY_COMITEM = "comitem";// 公司+品项
	private String statType;// 对比类型
	private String relatedType;// 资金贸易生产的关联类型
	private String tableTitle;
	private Date beginDate;
	private Date endDate;
	private String reportType;
	private LazyDataModel<CashPoolCompanyVo> cashPoolCompanyVo;
	private LazyDataModel<CashPoolItemVo> cashPoolItemVo;
	private LazyDataModel<CashCompanyItemVo> cashPoolCompanyItemVo;
	private LazyDataModel<CashPoolZjmyscVo> cashPoolZjmyVo;
	private LazyDataModel<CashPoolZjmyscVo> cashPoolZjscVo;
	private LazyDataModel<CashPoolZjmyscVo> cashPoolZjallVo;
	private LazyDataModel<CashPoolRcfkVo> cashPoolRcfkVo;
	private List<CashPoolColumnVo> zjmyscColumns;
	private StreamedContent downloadedExcelFile;// 被下载的文件
	@Inject
	private CashPoolSummaryReportService reportServie;
	@ManagedProperty(value = "#{cashPoolReportSelectBean}")
	private CashPoolReportSelectBean selectBean;

	@PostConstruct
	public void initCashPoolSummaryReportBean() {
		String strDate = CashPoolDateUtil.getStrDate(new Date(), "yyyy-MM-dd");
		beginDate = CashPoolDateUtil.getDateStr(strDate, "yyyy-MM-dd");
		endDate = beginDate;
		statType = TYPE_KEY_COMPANY;
		setZjmyscColumns(new ArrayList<CashPoolColumnVo>());
		initTable();
	}

	public void initTable() {
		if (TYPE_KEY_COMPANY.equals(statType)) {
			searchByCompany();
		} else if (TYPE_KEY_ITEM.equals(statType)) {
			searchByItem();
		} else {
			searchByCompanyItem();
		}
		log.debug(statType);
	}

	/**
	 * 查询按公司的对比表数据
	 * @Author: LiWei
	 * @CreatedTime: 2013-12-19 下午3:09:39
	 * @UpdatedBy:
	 * @UpdatedTime:
	 */
	private void searchByCompany() {
		cashPoolCompanyVo = new LazyDataModel<CashPoolCompanyVo>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<CashPoolCompanyVo> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
				appendTitle();
				long startTime = System.currentTimeMillis();
				CashPoolPageModelObj obj = reportServie.loadByCompany(authSelectedCompanyIds(), beginDate, proEndDate(), first, pageSize, filters);
				long endTime = System.currentTimeMillis();
		        log.info("查询数据库===========耗时："+(endTime-startTime));
				List<CashPoolCompanyVo> rtList = null;
				if (obj != null) {
					this.setRowCount(obj.getCount() == null ? 0 : obj.getCount().intValue());
					rtList = obj.getRtList();
				}
				return rtList;
			}
		};
	}

	/**
	 * 查询按品项的对比表数据
	 * @Author: LiWei
	 * @CreatedTime: 2013-12-19 下午3:10:01
	 * @UpdatedBy:
	 * @UpdatedTime:
	 */
	private void searchByItem() {
		cashPoolItemVo = new LazyDataModel<CashPoolItemVo>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<CashPoolItemVo> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
				appendTitle();
				CashPoolPageModelObj obj = reportServie.loadByItem(authCompanyIds(), selectedItemIds(), beginDate, proEndDate(), first, pageSize, filters);
				List<CashPoolItemVo> rtList = null;
				if (obj != null) {
					this.setRowCount(obj.getCount() == null ? 0 : obj.getCount().intValue());
					rtList = obj.getRtList();
				}
				return rtList;
			}
		};
	}

	/**
	 * 查询按公司+品项的对比表数据
	 * @Author: LiWei
	 * @CreatedTime: 2013-12-19 下午3:10:18
	 * @UpdatedBy:
	 * @UpdatedTime:
	 */
	private void searchByCompanyItem() {
		cashPoolCompanyItemVo = new LazyDataModel<CashCompanyItemVo>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<CashCompanyItemVo> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
				appendTitle();
				CashPoolPageModelObj obj = reportServie.loadByCompanyItem(authSelectedCompanyIds(), beginDate, proEndDate(), first, pageSize, filters, reportType);
				List<CashCompanyItemVo> rtList = null;
				if (obj != null) {
					this.setRowCount(obj.getCount() == null ? 0 : obj.getCount().intValue());
					rtList = obj.getRtList();
				}
				return rtList;
			}
		};
	}

	/**
	 * 查询自己资金贸易生产的实际下拨数据的关联和非关联数据
	 * @param related
	 * @Author: LiWei
	 * @CreatedTime: 2013-12-19 下午3:10:39
	 * @UpdatedBy:
	 * @UpdatedTime:
	 */
	public void searchZjmysc(final String related) {
		relatedType = related;
		setZjmyscColumns(reportServie.findAllZjmyscPxs());
		cashPoolZjmyVo = new LazyDataModel<CashPoolZjmyscVo>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<CashPoolZjmyscVo> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
				List<CashPoolZjmyscVo> rtList = null;
				CashPoolPageModelObj obj = reportServie.loadByCompanyZjmyscRelated(authSelectedCompanyIds(), "zjmy", related, beginDate, proEndDate(), first, pageSize, filters, reportType);
				if (obj != null) {
					this.setRowCount(obj.getCount() == null ? 0 : obj.getCount().intValue());
					rtList = obj.getRtList();
				}
				return rtList;
			}

		};
		cashPoolZjscVo = new LazyDataModel<CashPoolZjmyscVo>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<CashPoolZjmyscVo> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
				List<CashPoolZjmyscVo> rtList = null;
				CashPoolPageModelObj obj = reportServie.loadByCompanyZjmyscRelated(authSelectedCompanyIds(), "zjsc", related, beginDate, proEndDate(), first, pageSize, filters, reportType);
				if (obj != null) {
					this.setRowCount(obj.getCount() == null ? 0 : obj.getCount().intValue());
					rtList = obj.getRtList();
				}
				return rtList;
			}

		};
		
		cashPoolZjallVo = new LazyDataModel<CashPoolZjmyscVo>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<CashPoolZjmyscVo> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
				List<CashPoolZjmyscVo> rtList = null;
				CashPoolPageModelObj obj = reportServie.loadByCompanyZjmyscRelated(authSelectedCompanyIds(), null, related, beginDate, proEndDate(), first, pageSize, filters, reportType);
				if (obj != null) {
					this.setRowCount(obj.getCount() == null ? 0 : obj.getCount().intValue());
					rtList = obj.getRtList();
				}
				return rtList;
			}

		};
	}

	/**
	 * 查询日常付款实际下拨的明细
	 * @Author: LiWei
	 * @CreatedTime: 2013-12-19 下午3:11:17
	 * @UpdatedBy:
	 * @UpdatedTime:
	 */
	public void searchRcfk() {
		setCashPoolRcfkVo(new LazyDataModel<CashPoolRcfkVo>() {
			@Override
			public List<CashPoolRcfkVo> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
				List<CashPoolRcfkVo> rtList = null;
				CashPoolPageModelObj obj = reportServie.loadByCompanyRcfkDetail(authSelectedCompanyIds(), beginDate, proEndDate(), first, pageSize, filters, reportType);
				if (obj != null) {
					this.setRowCount(obj.getCount() == null ? 0 : obj.getCount().intValue());
					rtList = obj.getRtList();
				}
				return rtList;
			}
		});
	}

	private void appendTitle() {
		String statTypeVal = "";
		if (TYPE_KEY_COMITEM.equals(statType)) {
			statTypeVal = "公司+品项";
		} else if (TYPE_KEY_ITEM.equals(statType)) {
			statTypeVal = "品项";
		} else {
			statTypeVal = "公司";
		}
		tableTitle = "申请付款日期：" + CashPoolDateUtil.getStrDate(beginDate, "yyyy-MM-dd") + " 至 " + CashPoolDateUtil.getStrDate(endDate, "yyyy-MM-dd") + "  汇总类型：" + statTypeVal;
	}

	public void handleExcelDownload() {
		try {
			downloadedExcelFile = null;
			HSSFWorkbook workbook = new HSSFWorkbook();
			// 资金贸易关联/非关联的title
			List<String> orderKeys = new ArrayList<String>();
			Map<String, String> myscTitles = new HashMap<String, String>();
			orderKeys.add("cpName");
			orderKeys.add("xiaoji");
			myscTitles.put("cpName", "公司名称");
			myscTitles.put("xiaoji", "小计");
			List<CashPoolColumnVo> zjmyscColumns = reportServie.findAllZjmyscPxs();
			for (CashPoolColumnVo col : zjmyscColumns) {
				orderKeys.add(col.getKey());
				myscTitles.put(col.getKey(), col.getColumnName());
			}
			this.addCompanyItemSheet(workbook);
			// 采购资金(贸易)--非关联
			this.addZjmyFglSheet(workbook, orderKeys, myscTitles);
			// 采购资金(生产)--非关联
			this.addZjscFglSheet(workbook, orderKeys, myscTitles);
			// 采购资金(贸易)--关联
			this.addZjmyGlSheet(workbook, orderKeys, myscTitles);
			// 采购资金(生产)--关联
			this.addZjscGlSheet(workbook, orderKeys, myscTitles);
			// 日常付款明细
			this.addCompanyRcfkDetailSheet(workbook);
			InputStream stream = CashPoolExcelUtil.getInputStream(workbook);
			if (stream != null) {
				String beginStrDate = CashPoolDateUtil.getStrDate(beginDate, "yyyMMdd");
				String endStrDate = CashPoolDateUtil.getStrDate(endDate, "yyyMMdd");
				String fileName = CashPoolExcelUtil.getDownloadFileName(beginStrDate + "-" + endStrDate + "现金池对比表");
				downloadedExcelFile = new DefaultStreamedContent(stream, "application/vnd.ms-excel", fileName + ".xls");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 公司+品项
	@SuppressWarnings("unchecked")
	private void addCompanyItemSheet(HSSFWorkbook workbook) throws Exception {
		CashPoolPageModelObj companyItemObj = reportServie.loadByCompanyItem(authSelectedCompanyIds(), beginDate, proEndDate(), -1, 0, null, reportType);
		List<CashCompanyItemVo> companyItems = companyItemObj.getRtList();
		CashPoolExcelUtil.addExcelSheet(workbook, CashCompanyItemVo.class, "公司品项", companyItems);
	}

	// 资金生产非关联
	@SuppressWarnings("unchecked")
	private void addZjscFglSheet(HSSFWorkbook workbook, List<String> orderKeys, Map<String, String> myscTitles) throws Exception {

		List<Map<String, Object>> zjscFglList = new ArrayList<Map<String, Object>>();
		CashPoolPageModelObj zjscFglObj = reportServie.loadByCompanyZjmyscRelated(authSelectedCompanyIds(), "zjsc", "N", beginDate, proEndDate(), -1, 0, null, reportType);
		List<CashPoolZjmyscVo> zjscFgls = zjscFglObj.getRtList();
		for (CashPoolZjmyscVo zjmy : zjscFgls) {
			Map<String, Object> zjscFglDataMap = new HashMap<String, Object>();
			zjscFglDataMap.put("cpName", zjmy.getCpName());
			zjscFglDataMap.put("xiaoji", zjmy.getXiaoji());
			Map<String, Double> datas = zjmy.getDatas();
			Set<Entry<String, Double>> dataSets = datas.entrySet();
			for (Entry<String, Double> entry : dataSets) {
				zjscFglDataMap.put(entry.getKey(), entry.getValue());
			}
			zjscFglList.add(zjscFglDataMap);
		}
		CashPoolExcelUtil.addExcelSheet(workbook, "采购资金生产_非关联", orderKeys, myscTitles, zjscFglList);
	}

	// 资金贸易非关联
	@SuppressWarnings("unchecked")
	private void addZjmyFglSheet(HSSFWorkbook workbook, List<String> orderKeys, Map<String, String> myscTitles) throws Exception {
		List<Map<String, Object>> zjmyFglList = new ArrayList<Map<String, Object>>();
		CashPoolPageModelObj zjmyFglObj = reportServie.loadByCompanyZjmyscRelated(authSelectedCompanyIds(), "zjmy", "N", beginDate, proEndDate(), -1, 0, null, reportType);
		List<CashPoolZjmyscVo> zjmyFgls = zjmyFglObj.getRtList();
		for (CashPoolZjmyscVo zjmy : zjmyFgls) {
			Map<String, Object> zjmyFglDataMap = new HashMap<String, Object>();
			zjmyFglDataMap.put("cpName", zjmy.getCpName());
			zjmyFglDataMap.put("xiaoji", zjmy.getXiaoji());
			Map<String, Double> datas = zjmy.getDatas();
			Set<Entry<String, Double>> dataSets = datas.entrySet();
			for (Entry<String, Double> entry : dataSets) {
				zjmyFglDataMap.put(entry.getKey(), entry.getValue());
			}
			zjmyFglList.add(zjmyFglDataMap);
		}
		CashPoolExcelUtil.addExcelSheet(workbook, "采购资金贸易_非关联", orderKeys, myscTitles, zjmyFglList);
	}

	// 资金生产关联
	@SuppressWarnings("unchecked")
	private void addZjscGlSheet(HSSFWorkbook workbook, List<String> orderKeys, Map<String, String> myscTitles) throws Exception {
		List<Map<String, Object>> zjscGlList = new ArrayList<Map<String, Object>>();
		CashPoolPageModelObj zjscGlObj = reportServie.loadByCompanyZjmyscRelated(authSelectedCompanyIds(), "zjsc", "Y", beginDate, proEndDate(), -1, 0, null, reportType);
		List<CashPoolZjmyscVo> zjscGls = zjscGlObj.getRtList();
		for (CashPoolZjmyscVo zjmy : zjscGls) {
			Map<String, Object> zjscGlDataMap = new HashMap<String, Object>();
			zjscGlDataMap.put("cpName", zjmy.getCpName());
			zjscGlDataMap.put("xiaoji", zjmy.getXiaoji());
			Map<String, Double> datas = zjmy.getDatas();
			Set<Entry<String, Double>> dataSets = datas.entrySet();
			for (Entry<String, Double> entry : dataSets) {
				zjscGlDataMap.put(entry.getKey(), entry.getValue());
			}
			zjscGlList.add(zjscGlDataMap);
		}
		CashPoolExcelUtil.addExcelSheet(workbook, "采购资金生产_关联", orderKeys, myscTitles, zjscGlList);
	}

	// 资金贸易关联
	@SuppressWarnings("unchecked")
	private void addZjmyGlSheet(HSSFWorkbook workbook, List<String> orderKeys, Map<String, String> myscTitles) throws Exception {
		List<Map<String, Object>> zjmyGlList = new ArrayList<Map<String, Object>>();
		CashPoolPageModelObj zjmyGlObj = reportServie.loadByCompanyZjmyscRelated(authSelectedCompanyIds(), "zjmy", "Y", beginDate, proEndDate(), -1, 0, null, reportType);
		List<CashPoolZjmyscVo> zjmyGls = zjmyGlObj.getRtList();
		for (CashPoolZjmyscVo zjmy : zjmyGls) {
			Map<String, Object> zjmyGlDataMap = new HashMap<String, Object>();
			zjmyGlDataMap.put("cpName", zjmy.getCpName());
			zjmyGlDataMap.put("xiaoji", zjmy.getXiaoji());
			Map<String, Double> datas = zjmy.getDatas();
			Set<Entry<String, Double>> dataSets = datas.entrySet();
			for (Entry<String, Double> entry : dataSets) {
				zjmyGlDataMap.put(entry.getKey(), entry.getValue());
			}
			zjmyGlList.add(zjmyGlDataMap);
		}
		CashPoolExcelUtil.addExcelSheet(workbook, "采购资金贸易_关联", orderKeys, myscTitles, zjmyGlList);
	}

	// 日常付款明细
	@SuppressWarnings("unchecked")
	private void addCompanyRcfkDetailSheet(HSSFWorkbook workbook) throws Exception {
		CashPoolPageModelObj rcfkObj = reportServie.loadByCompanyRcfkDetail(authSelectedCompanyIds(), beginDate, proEndDate(), -1, 0, null, reportType);
		List<CashPoolRcfkVo> rcfks = rcfkObj.getRtList();
		CashPoolExcelUtil.addExcelSheet(workbook, CashPoolRcfkVo.class, "日常付款明细", rcfks);
	}

	private Date proEndDate() {
		Date proEndDate = null;
		if (endDate != null) {
			Date nextDate = CashPoolDateUtil.addDays(endDate, 0, 1, 0, 0);
			proEndDate = new Date(nextDate.getTime() - 1);
		}
		return proEndDate;
	}

	/**
	 * 得到用户已选择的而且有权限公司的id
	 * @return
	 */
	private List<String> authSelectedCompanyIds() {
		List<String> selectedCpIds = new ArrayList<String>();
		List<String> selectedCompanyIds = getSelectBean().getSelectedCompanyIds();
		if (selectedCompanyIds != null) {
			selectedCpIds.addAll(selectedCompanyIds);
		}
		return selectedCpIds;
	}

	/**
	 * 获取当前人员所有有权限的公司id
	 * @return
	 */
	private List<String> authCompanyIds() {
		List<String> cpIds = new ArrayList<String>();
		List<CashCompanyVo> authCPs = reportServie.findCompanyByName(null);
		for (CashCompanyVo vo : authCPs) {
			cpIds.add(vo.getId());
		}
		return cpIds;
	}

	/**
	 * 获取当前人员所有有权限的公司id
	 * @return
	 */
	private List<String> selectedItemIds() {
		List<String> itemIds = new ArrayList<String>();
		List<String> selectedItemIds = selectBean.getSelectedItemIds();
		if (selectedItemIds != null) {
			itemIds.addAll(selectedItemIds);
		}
		return itemIds;
	}

	/**
	 * 处理excel格式，设置标题行颜色为绿色
	 * 对Datatable生成excel表格后，文件下载前会执行
	 * @param document
	 */
	public void postProcessXLS(Object document) {
		log.debug("----Start:Excel格式处理----");
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow titleRow = sheet.getRow(0);
		// 设置第一行样式
		HSSFCellStyle titleCellStyle = wb.createCellStyle();
		HSSFPalette palette = wb.getCustomPalette();
		palette.setColorAtIndex((short) 9, (byte) (220), (byte) (240), (byte) (138));
		titleCellStyle.setFillForegroundColor((short) 9);
		titleCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		titleCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		for (int i = 0; i < titleRow.getPhysicalNumberOfCells(); i++) {
			HSSFCell cell = titleRow.getCell(i);
			cell.setCellStyle(titleCellStyle);
		}
		// 设置单元格宽度
		Map<Integer, Integer> columnSizeMap = new HashMap<Integer, Integer>();
		for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			HSSFRow currentRow = sheet.getRow(rowIndex);
			for (int i = 0; i < currentRow.getPhysicalNumberOfCells(); i++) {
				HSSFCell currentCell = currentRow.getCell(i);
				Integer oldLength = columnSizeMap.get(i);
				int length = currentCell.toString().getBytes().length * 256;
				if (oldLength == null || oldLength.intValue() < length) {
					int columnWidth = sheet.getColumnWidth(i);
					if (columnWidth < length) {
						columnSizeMap.put(i, length);
					}
				}
			}
		}
		Set<Entry<Integer, Integer>> entrySet = columnSizeMap.entrySet();
		for (Entry<Integer, Integer> entry : entrySet) {
			if (entry.getKey() != null && entry.getValue() != null) {
				sheet.setColumnWidth(entry.getKey(), entry.getValue());
			}
		}
		log.debug("----End:Excel格式处理----");
	}

	public LazyDataModel<CashPoolCompanyVo> getCashPoolCompanyVo() {
		return cashPoolCompanyVo;
	}

	public void setCashPoolCompanyVo(LazyDataModel<CashPoolCompanyVo> cashPoolCompanyVo) {
		this.cashPoolCompanyVo = cashPoolCompanyVo;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStatType() {
		return statType;
	}

	public void setStatType(String statType) {
		this.statType = statType;
	}

	public String getTableTitle() {
		return tableTitle;
	}

	public void setTableTitle(String tableTitle) {
		this.tableTitle = tableTitle;
	}

	public LazyDataModel<CashPoolItemVo> getCashPoolItemVo() {
		return cashPoolItemVo;
	}

	public void setCashPoolItemVo(LazyDataModel<CashPoolItemVo> cashPoolItemVo) {
		this.cashPoolItemVo = cashPoolItemVo;
	}

	public LazyDataModel<CashCompanyItemVo> getCashPoolCompanyItemVo() {
		return cashPoolCompanyItemVo;
	}

	public void setCashPoolCompanyItemVo(LazyDataModel<CashCompanyItemVo> cashPoolCompanyItemVo) {
		this.cashPoolCompanyItemVo = cashPoolCompanyItemVo;
	}

	public LazyDataModel<CashPoolZjmyscVo> getCashPoolZjmyVo() {
		return cashPoolZjmyVo;
	}

	public void setCashPoolZjmyVo(LazyDataModel<CashPoolZjmyscVo> cashPoolZjmyVo) {
		this.cashPoolZjmyVo = cashPoolZjmyVo;
	}

	public LazyDataModel<CashPoolZjmyscVo> getCashPoolZjscVo() {
		return cashPoolZjscVo;
	}

	public void setCashPoolZjscVo(LazyDataModel<CashPoolZjmyscVo> cashPoolZjscVo) {
		this.cashPoolZjscVo = cashPoolZjscVo;
	}

	public List<CashPoolColumnVo> getZjmyscColumns() {
		return zjmyscColumns;
	}

	public void setZjmyscColumns(List<CashPoolColumnVo> zjmyscColumns) {
		this.zjmyscColumns = zjmyscColumns;
	}

	public String getRelatedType() {
		return relatedType;
	}

	public void setRelatedType(String relatedType) {
		this.relatedType = relatedType;
	}

	public LazyDataModel<CashPoolRcfkVo> getCashPoolRcfkVo() {
		return cashPoolRcfkVo;
	}

	public void setCashPoolRcfkVo(LazyDataModel<CashPoolRcfkVo> cashPoolRcfkVo) {
		this.cashPoolRcfkVo = cashPoolRcfkVo;
	}

	public StreamedContent getDownloadedExcelFile() {
		return downloadedExcelFile;
	}

	public void setDownloadedExcelFile(StreamedContent downloadedExcelFile) {
		this.downloadedExcelFile = downloadedExcelFile;
	}

	public CashPoolReportSelectBean getSelectBean() {
		return selectBean;
	}

	public void setSelectBean(CashPoolReportSelectBean selectBean) {
		this.selectBean = selectBean;
	}

	public LazyDataModel<CashPoolZjmyscVo> getCashPoolZjallVo() {
		return cashPoolZjallVo;
	}

	public void setCashPoolZjallVo(LazyDataModel<CashPoolZjmyscVo> cashPoolZjallVo) {
		this.cashPoolZjallVo = cashPoolZjallVo;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

}
