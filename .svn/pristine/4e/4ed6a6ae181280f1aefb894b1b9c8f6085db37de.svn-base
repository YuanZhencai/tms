package com.wcs.tms.view.report.cashpool.util;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class CashPoolExcelStyle {

	private CashPoolExcelStyle() {

	}

	public static HSSFCellStyle setHeadStyle(HSSFWorkbook workbook, HSSFCellStyle style) {
		HSSFPalette palette = workbook.getCustomPalette();
		palette.setColorAtIndex((short) 9, (byte) (220), (byte) (240), (byte) (138));
		style.setFillForegroundColor((short) 9);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		// 生成字体
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) 10);
		// font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 把字体应用到当前的样样式
		style.setFont(font);
		return style;

	}
}
