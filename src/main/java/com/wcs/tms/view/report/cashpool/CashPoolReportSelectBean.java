package com.wcs.tms.view.report.cashpool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.primefaces.model.DualListModel;

import com.wcs.tms.service.report.cashpool.CashPoolSummaryReportService;
import com.wcs.tms.view.report.cashpool.vo.CashCompanyVo;
import com.wcs.tms.view.report.cashpool.vo.CashItemVo;
import com.wcs.tms.view.report.cashpool.vo.CashPoolColumnVo;

@ManagedBean(name = "cashPoolReportSelectBean")
@ViewScoped
public class CashPoolReportSelectBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private DualListModel<CashCompanyVo> companyDualList;// pickList model
	private DualListModel<CashItemVo> itemDualList;// pickList model
	private String cpName;
	private String selectedCpNames;
	private String itemType;
	private String selectedItemName;
	private List<String> selectedCompanyIds;
	private List<String> selectedItemIds;
	@Inject
	private CashPoolSummaryReportService reportService;

	@PostConstruct
	public void initCashPoolReportSelectBean() {
		companyDualList = new DualListModel<CashCompanyVo>(new ArrayList<CashCompanyVo>(), new ArrayList<CashCompanyVo>());
		itemDualList = new DualListModel<CashItemVo>(new ArrayList<CashItemVo>(), new ArrayList<CashItemVo>());
		selectedCpNames = "全部";
		selectedItemName = "全部";
		sureSelectCompany();
		sureSelectItem();
	}

	public void initCompany() {
		List<CashCompanyVo> cpList = reportService.findCompanyByName(cpName);
		List<CashCompanyVo> source = companyDualList.getSource();
		List<CashCompanyVo> target = companyDualList.getTarget();
		source.clear();
		CashCompanyVo allVo = new CashCompanyVo();
		allVo.setId("0");
		allVo.setCpName("全部");
		source.add(allVo);
		source.addAll(cpList);
		source.removeAll(target);
		companyDualList.setSource(source);
	}

	public void sureSelectCompany() {
		boolean selectedAll = false;
		List<CashCompanyVo> target = companyDualList.getTarget();
		if (target.size() == 0) {
			CashCompanyVo allVo = new CashCompanyVo();
			allVo.setId("0");
			allVo.setCpName("全部");
			target.add(allVo);
			selectedCpNames = "全部";
			selectedAll = true;
		}
		selectedCompanyIds = new ArrayList<String>();
		selectedCpNames = "";
		for (CashCompanyVo selectedVo : target) {
			if ("0".equals(selectedVo.getId())) {
				selectedCompanyIds.clear();
				selectedCpNames = "全部";
				selectedAll = true;
				break;
			} else {
				selectedCompanyIds.add(selectedVo.getId());
				selectedCpNames += selectedVo.getCpName() + ";";
			}
		}
		if (selectedAll) {
			List<CashCompanyVo> authCPs = reportService.findCompanyByName(null);
			for (CashCompanyVo vo : authCPs) {
				selectedCompanyIds.add(vo.getId());
			}
		}
	}

	public void sureSelectItem() {
		Set<String> selectedItemIdSet = new HashSet<String>();
		selectedItemName = "";
		List<CashItemVo> target = itemDualList.getTarget();
		if (target.size() == 0) {
			target.add(new CashItemVo("0", "", "全部"));
		}
		List<CashItemVo> items = new ArrayList<CashItemVo>();
		Map<String, Boolean> selectedAll = new HashMap<String, Boolean>();
		for (CashItemVo vo : target) {
			String id = vo.getId();
			if ("0".equals(id)) {
				selectedAll.put("0", true);
				items.addAll(createZjmyscItems());
				items.addAll(createRcfkItems());
				items.addAll(createTzrzItems());
				items.addAll(createGckItems());
				break;
			} else if ("zjmysc_0".equals(id)) {
				selectedAll.put("zjmysc", true);
				selectedItemName += vo.getItemName() + " ; ";
				items.addAll(createZjmyscItems());
			} else if ("rcfk_0".equals(id)) {
				selectedAll.put("rcfk", true);
				selectedItemName += vo.getItemName() + " ; ";
				items.addAll(createRcfkItems());
			} else if ("tzrz_0".equals(id)) {
				selectedAll.put("tzrz", true);
				selectedItemName += vo.getItemName() + " ; ";
				items.addAll(createTzrzItems());
			} else if ("gck_0".equals(id)) {
				selectedAll.put("gck", true);
				selectedItemName += vo.getItemName() + " ; ";
				items.addAll(createGckItems());
			} else {
				items.add(vo);
			}
		}
		// 获取选择的品项的id和显示名称
		for (CashItemVo vo : items) {
			String id = vo.getId();
			String itemType = vo.getItemType();
			String itemName = vo.getItemName();
			String[] spId = id.split("&");
			if (spId.length == 1) {
				selectedItemIdSet.add(id);
			} else if (spId.length == 2) {
				selectedItemIdSet.add(spId[0]);
				selectedItemIdSet.add(spId[1]);
			}
			if (selectedAll.get(itemType) == null || selectedAll.get(vo.getItemType()).booleanValue() == false) {
				selectedItemName += itemName + " ; ";
			}
		}
		if (selectedAll.get("0") != null && selectedAll.get("0").booleanValue() == true) {
			selectedItemName = " 全部 ";
		}
		selectedItemIds = new ArrayList<String>();
		selectedItemIds.addAll(selectedItemIdSet);
	}

	public void initItem() {
		itemType = "";
		handleItems();
	}

	public void handleItems() {
		List<CashItemVo> source = itemDualList.getSource();
		List<CashItemVo> target = itemDualList.getTarget();
		source.clear();
		source.addAll(createItems());
		source.removeAll(target);
	}

	public List<CashItemVo> createItems() {
		List<CashItemVo> items = new ArrayList<CashItemVo>();
		if (getItemType() == null || getItemType().trim().equals("")) {
			items.add(new CashItemVo("0", "", "全部"));
			items.addAll(createZjmyscItems());
			items.addAll(createRcfkItems());
			items.addAll(createTzrzItems());
			items.addAll(createGckItems());
		} else if ("zjmysc".equals(getItemType())) {
			items.add(new CashItemVo("zjmysc_0", "zjmysc", "全部-贸易/生产"));
			items.addAll(createZjmyscItems());
		} else if ("rcfk".equals(getItemType())) {
			items.add(new CashItemVo("rcfk_0", "rcfk", "全部-日常付款"));
			items.addAll(createRcfkItems());
		} else if ("tzrz".equals(getItemType())) {
			items.add(new CashItemVo("tzrz_0", "tzrz", "全部-投融资"));
			items.addAll(createTzrzItems());
		} else if ("gck".equals(getItemType())) {
			items.add(new CashItemVo("gck_0", "gck", "全部-工程款"));
			items.addAll(createGckItems());
		}
		return items;
	}

	private List<CashItemVo> createZjmyscItems() {
		List<CashItemVo> zjmyscItems = new ArrayList<CashItemVo>();
		List<CashPoolColumnVo> zjmyscPxs = reportService.findAllZjmyscPxs();
		for (CashPoolColumnVo vo : zjmyscPxs) {
			zjmyscItems.add(new CashItemVo("zjmy_" + vo.getKey() + "&zjsc_" + vo.getKey(), "zjmysc", vo.getColumnName()));
		}
		return zjmyscItems;
	}

	private List<CashItemVo> createRcfkItems() {
		List<CashItemVo> rcfkItems = new ArrayList<CashItemVo>();
		rcfkItems.add(new CashItemVo("rcfk_TAX", "rcfk", "税款"));
		rcfkItems.add(new CashItemVo("rcfk_PACKAGING_MATERIALS", "rcfk", "辅料包材"));
		rcfkItems.add(new CashItemVo("rcfk_SPARE_PARTS", "rcfk", "备品备件"));
		rcfkItems.add(new CashItemVo("rcfk_COAL", "rcfk", "煤炭"));
		rcfkItems.add(new CashItemVo("rcfk_STEAM_ELECTRICITY", "rcfk", "水电汽"));
		rcfkItems.add(new CashItemVo("rcfk_SALARY", "rcfk", "工资"));
		rcfkItems.add(new CashItemVo("rcfk_FREIGHT", "rcfk", "运费/港杂费"));
		rcfkItems.add(new CashItemVo("rcfk_ELSE_PROJ", "rcfk", "其他"));
		return rcfkItems;
	}

	private List<CashItemVo> createTzrzItems() {
		List<CashItemVo> tzrzItems = new ArrayList<CashItemVo>();
		tzrzItems.add(new CashItemVo("tzrz_I", "tzrz", "投资理财(纯理财)"));
		tzrzItems.add(new CashItemVo("tzrz_F", "tzrz", "融资保证金"));
		tzrzItems.add(new CashItemVo("tzrz_R", "tzrz", "还贷"));
		tzrzItems.add(new CashItemVo("tzrz_C", "tzrz", "利息及银行费用"));
		tzrzItems.add(new CashItemVo("tzrz_D", "tzrz", "时点存款"));
		return tzrzItems;
	}

	private List<CashItemVo> createGckItems() {
		List<CashItemVo> gckItems = new ArrayList<CashItemVo>();
		gckItems.add(new CashItemVo("gck_E", "gck", "付工程款"));
		gckItems.add(new CashItemVo("gck_D", "gck", "支付股利"));
		gckItems.add(new CashItemVo("gck_A", "gck", "归还股东借款"));
		return gckItems;
	}

	public DualListModel<CashCompanyVo> getCompanyDualList() {
		return companyDualList;
	}

	public void setCompanyDualList(DualListModel<CashCompanyVo> companyDualList) {
		this.companyDualList = companyDualList;
	}

	public String getCpName() {
		return cpName;
	}

	public void setCpName(String cpName) {
		this.cpName = cpName;
	}

	public List<String> getSelectedCompanyIds() {
		return selectedCompanyIds;
	}

	public void setSelectedCompanyIds(List<String> selectedCompanyIds) {
		this.selectedCompanyIds = selectedCompanyIds;
	}

	public String getSelectedCpNames() {
		return selectedCpNames;
	}

	public void setSelectedCpNames(String selectedCpNames) {
		this.selectedCpNames = selectedCpNames;
	}

	public DualListModel<CashItemVo> getItemDualList() {
		return itemDualList;
	}

	public void setItemDualList(DualListModel<CashItemVo> itemDualList) {
		this.itemDualList = itemDualList;
	}

	public String getSelectedItemName() {
		return selectedItemName;
	}

	public void setSelectedItemName(String selectedItemName) {
		this.selectedItemName = selectedItemName;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public List<String> getSelectedItemIds() {
		return selectedItemIds;
	}

	public void setSelectedItemIds(List<String> selectedItemIds) {
		this.selectedItemIds = selectedItemIds;
	}

}
