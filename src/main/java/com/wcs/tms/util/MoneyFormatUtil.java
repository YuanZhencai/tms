package com.wcs.tms.util;

import java.text.NumberFormat;
import java.util.HashMap;

import com.wcs.tms.exception.ServiceException;

/**
 * 
 * 直接调用format方法即可,可将任何类型的阿拉伯数字显示转换成汉字显示
 * 
 */
public class MoneyFormatUtil {
	public static final String EMPTY = "";
	public static final String ZERO = "零";
	public static final String ONE = "壹";
	public static final String TWO = "贰";
	public static final String THREE = "叁";
	public static final String FOUR = "肆";
	public static final String FIVE = "伍";
	public static final String SIX = "陆";
	public static final String SEVEN = "柒";
	public static final String EIGHT = "捌";
	public static final String NINE = "玖";
	public static final String TEN = "拾";
	public static final String HUNDRED = "佰";
	public static final String THOUSAND = "仟";
	public static final String TEN_THOUSAND = "万";
	public static final String HUNDRED_MILLION = "亿";
	public static final String DOT = "点";
	public static final String YUAN = "元";
	private static HashMap<String, String> chineseNumberMap = new HashMap<String, String>();
	private static HashMap<String, String> chineseMoneyPattern = new HashMap<String, String>();
	private static NumberFormat numberFormat = NumberFormat.getInstance();

	static {
		numberFormat.setMaximumFractionDigits(4);
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setGroupingUsed(false);

		chineseNumberMap.put("0", ZERO);
		chineseNumberMap.put("1", ONE);
		chineseNumberMap.put("2", TWO);
		chineseNumberMap.put("3", THREE);
		chineseNumberMap.put("4", FOUR);
		chineseNumberMap.put("5", FIVE);
		chineseNumberMap.put("6", SIX);
		chineseNumberMap.put("7", SEVEN);
		chineseNumberMap.put("8", EIGHT);
		chineseNumberMap.put("9", NINE);
		chineseNumberMap.put(".", DOT);

		chineseMoneyPattern.put("1", TEN);
		chineseMoneyPattern.put("2", HUNDRED);
		chineseMoneyPattern.put("3", THOUSAND);
		chineseMoneyPattern.put("4", TEN_THOUSAND);
		chineseMoneyPattern.put("5", TEN);
		chineseMoneyPattern.put("6", HUNDRED);
		chineseMoneyPattern.put("7", THOUSAND);
		chineseMoneyPattern.put("8", HUNDRED_MILLION);
	}

	private static String convertToChineseNumber(String moneyStr) {
		// modified on 2012-9-26
		if ("0.00".equals(moneyStr)) {
			return ZERO;
		}
		String result;
		StringBuffer cMoneyStringBuffer = new StringBuffer();
		for (int i = 0; i < moneyStr.length(); i++) {
			cMoneyStringBuffer.append(chineseNumberMap.get(moneyStr.substring(
					i, i + 1)));
		}
		int indexOfDot = cMoneyStringBuffer.indexOf(DOT);
		int moneyPatternCursor = 1;
		for (int i = indexOfDot - 1; i > 0; i--) {
			cMoneyStringBuffer.insert(i,
					chineseMoneyPattern.get(EMPTY + moneyPatternCursor));
			moneyPatternCursor = moneyPatternCursor == 8 ? 1
					: moneyPatternCursor + 1;
		}

		cMoneyStringBuffer.delete(cMoneyStringBuffer.indexOf(DOT),
				cMoneyStringBuffer.length());
		while (cMoneyStringBuffer.indexOf("零拾") != -1) {
			cMoneyStringBuffer.replace(cMoneyStringBuffer.indexOf("零拾"),
					cMoneyStringBuffer.indexOf("零拾") + 2, ZERO);
		}
		while (cMoneyStringBuffer.indexOf("零佰") != -1) {
			cMoneyStringBuffer.replace(cMoneyStringBuffer.indexOf("零佰"),
					cMoneyStringBuffer.indexOf("零佰") + 2, ZERO);
		}
		while (cMoneyStringBuffer.indexOf("零仟") != -1) {
			cMoneyStringBuffer.replace(cMoneyStringBuffer.indexOf("零仟"),
					cMoneyStringBuffer.indexOf("零仟") + 2, ZERO);
		}
		while (cMoneyStringBuffer.indexOf("零万") != -1) {
			cMoneyStringBuffer.replace(cMoneyStringBuffer.indexOf("零万"),
					cMoneyStringBuffer.indexOf("零万") + 2, TEN_THOUSAND);
		}
		while (cMoneyStringBuffer.indexOf("零亿") != -1) {
			cMoneyStringBuffer.replace(cMoneyStringBuffer.indexOf("零亿"),
					cMoneyStringBuffer.indexOf("零亿") + 2, HUNDRED_MILLION);
		}
		while (cMoneyStringBuffer.indexOf("零零") != -1) {
			cMoneyStringBuffer.replace(cMoneyStringBuffer.indexOf("零零"),
					cMoneyStringBuffer.indexOf("零零") + 2, ZERO);
		}
		if (cMoneyStringBuffer.lastIndexOf(ZERO) == cMoneyStringBuffer.length() - 1) {
			cMoneyStringBuffer.delete(cMoneyStringBuffer.length() - 1,
					cMoneyStringBuffer.length());
		}
		if (cMoneyStringBuffer.indexOf("亿万") != -1) {
			cMoneyStringBuffer.replace(cMoneyStringBuffer.indexOf("亿万"),
					cMoneyStringBuffer.indexOf("亿万") + 2, HUNDRED_MILLION);
		}

		result = cMoneyStringBuffer.toString();
		return result;
	}

	private static String addUnitsToChineseMoneyString(String moneyStr) {
		String result;
		StringBuffer cMoneyStringBuffer = new StringBuffer(moneyStr);
		result = cMoneyStringBuffer.toString();
		if ("".equals(result)) {
			return "";
		} else {
			return result + YUAN;
		}
	}

	private static void checkPrecision(String moneyStr) {
		int fractionDigits = moneyStr.length() - moneyStr.indexOf('.') - 1;
		if (fractionDigits > 2) {
			// 精度不能比分低
			throw new ServiceException("金额" + moneyStr + "的小数位多于两位。");
		}
	}

	public static String format(String moneyStr) {
		String result;
		// 转换成汉字小数
		result = convertToChineseNumber(moneyStr);
		result = addUnitsToChineseMoneyString(result);
		return result;
	}

	public static String format(double moneyDouble) {
		return format(numberFormat.format(moneyDouble));
	}

	public static String format(int moneyInt) {
		return format(numberFormat.format(moneyInt));
	}

	public static String format(long moneyLong) {
		return format(numberFormat.format(moneyLong));
	}

	public static String format(Number moneyNum) {
		return format(numberFormat.format(moneyNum));
	}

}
