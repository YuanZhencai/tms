package com.wcs.tms.view.report.cashpool.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.wcs.base.util.JSFUtils;
import com.wcs.tms.view.report.cashpool.annotation.CashPoolExcelTitleAnnotation;
import com.wcs.tms.view.report.cashpool.vo.CashCompanyItemVo;

public class CashPoolExcelUtil {
	private CashPoolExcelUtil() {

	}

	public static void main(String[] args) throws Exception {
		CashCompanyItemVo vo = new CashCompanyItemVo();
		vo.setCpName("aaa");
		vo.setTzlc(new Double(1200.0D));
		List<CashCompanyItemVo> vos = new ArrayList<CashCompanyItemVo>();
		vos.add(vo);
		HSSFWorkbook workbook = new HSSFWorkbook();
		addExcelSheet(workbook, CashCompanyItemVo.class, "sheet1", vos);

		List<String> orderKeys = new ArrayList<String>();
		orderKeys.add("0");
		orderKeys.add("1");
		Map<String, String> titleMap = new HashMap<String, String>();
		titleMap.put("0", "T1");
		titleMap.put("1", "T2");
		Map<String, Object> valMap = new HashMap<String, Object>();
		valMap.put("0", "V1");
		valMap.put("1", "V2");
		List<Map<String, Object>> valMapList = new ArrayList<Map<String, Object>>();
		valMapList.add(valMap);
		System.out.println(valMap);
		addExcelSheet(workbook, "sheet2", orderKeys, titleMap, valMapList);

		FileOutputStream fos = new FileOutputStream("d:\\aa.xls");
		workbook.write(fos);
		fos.flush();
		fos.close();
	}

	/**
	 * 获取Workbook的输入流
	 */
	public static InputStream getInputStream(HSSFWorkbook workbook) throws IOException {
		if (workbook == null) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);
		out.close();
		byte[] data = out.toByteArray();
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		return in;
	}

	/**
	 * 添加一个excel的页签
	 */
	public static void addExcelSheet(HSSFWorkbook workbook, Class<?> clazz, String sheetTitle, List<?> dataList) throws Exception {
		List<String> titleNames = getTitleNames(clazz);
		Map<String, Object> valueObjMap = new TreeMap<String, Object>();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(sheetTitle);
		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth(20);
		// 生成一个样式
		HSSFCellStyle style = workbook.createCellStyle();
		// 设置标题样式
		style = CashPoolExcelStyle.setHeadStyle(workbook, style);
		// 产生表格标题行
		HSSFRow row = sheet.createRow(0);
		for (int j = 0; j < titleNames.size(); j++) {
			HSSFCell cell = row.createCell(j);
			cell.setCellStyle(style);
			HSSFRichTextString text = new HSSFRichTextString(titleNames.get(j));
			cell.setCellValue(text);
		}
		for (int i = 0; i < dataList.size(); i++) {
			HSSFRow dataRow = sheet.createRow(i + 1);
			valueObjMap = getValueByTitleNames(clazz, dataList.get(i), titleNames);
			for (int k = 0; k < titleNames.size(); k++) {
				HSSFCell cell = dataRow.createCell(k);
				setCellValue(cell, valueObjMap.get(titleNames.get(k)));
			}
		}
	}

	/**
	 * 添加一个excel的页签
	 */
	public static void addExcelSheet(HSSFWorkbook workbook, String sheetTitle, List<String> orderKeys, Map<String, String> titleMap, List<Map<String, Object>> valMapList) throws Exception {
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(sheetTitle);
		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth(20);
		// 生成一个样式
		HSSFCellStyle style = workbook.createCellStyle();
		// 设置标题样式
		style = CashPoolExcelStyle.setHeadStyle(workbook, style);
		// 产生表格标题行
		HSSFRow row = sheet.createRow(0);
		for (int j = 0; j < orderKeys.size(); j++) {
			HSSFCell cell = row.createCell(j);
			cell.setCellStyle(style);
			HSSFRichTextString text = new HSSFRichTextString(titleMap.get(orderKeys.get(j)));
			cell.setCellValue(text);
		}
		for (int i = 0; i < valMapList.size(); i++) {
			Map<String, Object> valMap = valMapList.get(i);
			HSSFRow dataRow = sheet.createRow(i + 1);
			for (int k = 0; k < orderKeys.size(); k++) {
				HSSFCell cell = dataRow.createCell(k);
				setCellValue(cell, valMap.get(orderKeys.get(k)));
			}
		}
	}

	/**
	 * 根据注解的对应excel的列title得到字段值
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map<String, Object> getValueByTitleNames(Class clazz, Object dataObj, List<String> titleNames) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		Map<String, Object> map = new TreeMap<String, Object>();
		for (String titleName : titleNames) {
			Class tClazz = clazz;
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				CashPoolExcelTitleAnnotation exAnnotation = field.getAnnotation(CashPoolExcelTitleAnnotation.class);
				if (exAnnotation != null && exAnnotation.titleName().equals(titleName)) {
					Method getMethod = tClazz.getMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1),
							new Class[] {});
					Object valueObj = getMethod.invoke(dataObj, new Object[] {});
					map.put(titleName, valueObj);
					break;
				}
			}

		}
		return map;
	}

	/**
	 * 通过注解形式解析时，获取所有的title
	 */
	@SuppressWarnings("rawtypes")
	private static List<String> getTitleNames(Class clazz) {
		Map<Integer, String> indexValMap = new HashMap<Integer, String>();
		int maxIndex = 0;
		List<String> titleNames = new ArrayList<String>();
		Class tClazz = clazz;
		Field[] tFields = tClazz.getDeclaredFields();
		for (Field f : tFields) {
			CashPoolExcelTitleAnnotation exAnnotation = f.getAnnotation(CashPoolExcelTitleAnnotation.class);
			if (exAnnotation != null) {
				Integer index = Integer.valueOf(exAnnotation.columnNO());
				indexValMap.put(index, exAnnotation.titleName());
				maxIndex = index.intValue() > maxIndex ? index.intValue() : maxIndex;
			}
		}
		for (int i = 0; i <= maxIndex; i++) {
			if (indexValMap.get(i) != null) {
				titleNames.add(indexValMap.get(i));
			} else {
				titleNames.add("");
			}
		}
		return titleNames;
	}

	/**
	 * 设置cell数据
	 */
	private static void setCellValue(HSSFCell cell, Object val) {
		if (val instanceof Double || val instanceof Float || val instanceof Integer) {
			Double dbVal = Double.valueOf(val.toString());
			cell.setCellValue(dbVal.doubleValue());
		} else if (val instanceof Date) {
			Date dtVal = (Date) val;
			cell.setCellValue(dtVal);
		} else {
			if (val != null) {
				cell.setCellValue(val.toString());
			}
		}
	}

	public static String getDownloadFileName(String fileName) {
		String codeName = "";
		if (fileName == null) {
			return "";
		}
		try {
			codeName = fileName;
			HttpServletRequest request = JSFUtils.getRequest();
			if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
				codeName = URLEncoder.encode(codeName, "UTF-8");
				// IE浏览器
			} else {
				codeName = new String(codeName.getBytes("UTF-8"), "ISO8859-1");
			}
		} catch (Exception e) {
			codeName = "";
		}
		return codeName;
	}
}
