/**
 * DictCat.java
 * Created: 2012-2-16 下午01:52:32
 */
package com.wcs.common.consts;

/**
 * <p>
 * Project: btcbase
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright (c) 2012 Wilmar Consultancy Services
 * </p>
 * <p>
 * All Rights Reserved.
 * </p>
 * 
 * @author <a href="mailto:chengchao@wcs-global.com">ChengChao</a>
 */
public interface DictConsts {
	public final String ZH_CN = "zh_CN";
	// 左边是java类，右边是数据库值
	// 资金币种
	public final String TMS_TAX_PROJECT_CURRENCY_TYPE = "TMS.TAX.PROJECT.CURRENCY.TYPE";
	// 人民币
	public final String TMS_TAX_PROJECT_CURRENCY_TYPE_1 = "TMS.TAX.PROJECT.CURRENCY.TYPE.1";
	// 美元
	public final String TMS_TAX_PROJECT_CURRENCY_TYPE_2 = "TMS.TAX.PROJECT.CURRENCY.TYPE.2";
	// 期限单位
	public final String TMS_LIMIT_DATE_UNIT_TYPE = "TMS.LIMIT.DATE.UNIT.TYPE";
	// 理财产品类型
	public final String TMS_INVE_PRODUCT_TYPE = "TMS.INVE.PRODUCT.TYPE";
	// 利率挂钩
	public final String TMS_RATE_HOOK_TYPE = "TMS.RATE.HOOK.TYPE";
	// 资金提供方公司
	public final String TMS_FUND_PROVIDER_COM_NAME = "TMS.FUND.PROVIDER.COM.NAME";
	// 农产品品种类型
	public final String TMS_FARM_PRODUCE_VARIETY_TYPE = "TMS.FARM.PRODUCE.VARIETY.TYPE";
	// 采购资金类型
	public final String TMS_PURCHASE_FUND_TYPE = "TMS.PURCHASE.FUND.TYPE";
	// P生产
	public final String TMS_PURCHASE_FUND_TYPE_P = "TMS.PURCHASE.FUND.TYPE.P";
	// T贸易
	public final String TMS_PURCHASE_FUND_TYPE_T = "TMS.PURCHASE.FUND.TYPE.T";
	/**
	 * <p>
	 * 是否为借款
	 * </p>
	 * L：借款</br> T：转款</br> A：借款+转款
	 */
	public final String TMS_LOAN_IDEN_TYPE = "TMS.LOAN.IDEN.TYPE";
	/**
	 * <p>
	 * 支付方式
	 * </p>
	 * S：SUNGRAD支付</br> O：网银支付
	 */
	public final String TMS_PAY_WAY_TYPE = "TMS.PAY.WAY.TYPE";
	// 人民币流贷下拉
	public final String TMS_RATEHOOK_RMB_FLOW_TYPE = "TMS.RATE.HOOK.RMB.FLOW.TYPE";
	//
	public final String TMS_RATEHOOK_IMPORT_FINANCE_TYPE = "TMS.RATEHOOK.IMPORT.FINANCE.TYPE";

	// 账户性质
	public final String TMS_ACCOUNT_NATURE_TYPE = "TMS.ACCOUNT.NATURE.TYPE";
	// 一般用户
	public final String TMS_ACCOUNT_NATURE_TYPE_N = "TMS.ACCOUNT.NATURE.TYPE.N";
	// 虚拟用户
	public final String TMS_ACCOUNT_NATURE_TYPE_V = "TMS.ACCOUNT.NATURE.TYPE.V";

	// 账户银行:农业银行,ABCX;交通银行,BCOM;民生银行,CMBC
	public final String BPM_ACCOUNT_BANK_CODE = "BPM.ACCOUNT.BANK.CODE";

	// 表单类型
	public final String TMS_FORM_TYPE = "TMS.FORM.TYPE";
	// 投资理财
	public final String TMS_FORM_TYPE_I = "TMS.FORM.TYPE.I";
	// 融资保证金
	public final String TMS_FORM_TYPE_F = "TMS.FORM.TYPE.F";
	// 还贷
	public final String TMS_FORM_TYPE_R = "TMS.FORM.TYPE.R";

	// 表单类型(支付工程款流程)
	public final String TMS_FORM2_TYPE2 = "TMS.FORM2.TYPE2";
	// 付工程款
	public final String TMS_FORM2_TYPE2_E = "TMS.FORM2.TYPE2.E";
	// 支付股利
	public final String TMS_FORM2_TYPE2_D = "TMS.FORM2.TYPE2.D";
	// 归还股东借款
	public final String TMS_FORM2_TYPE2_A = "TMS.FORM2.TYPE2.A";

	// 城市
	public final String TMS_CITY_NAME = "TMS.CITY.NAME";
	// 上海
	public final String TMS_CITY_NAME_S = "TMS.CITY.NAME.S";
	// 北京
	public final String TMS_CITY_NAME_B = "TMS.CITY.NAME.B";

	// 银行类型
	public final String TMS_BANK_TYPE = "TMS.BANK.TYPE";
	// 账户银行
	public final String TMS_BANK_TYPE_A = "TMS.BANK.TYPE.A";
	// 交易对手
	public final String TMS_BANK_TYPE_C = "TMS.BANK.TYPE.C";
	// 账户银行和交易对手
	public final String TMS_BANK_TYPE_AC = "TMS.BANK.TYPE.AC";
	
	// 同户名划转-下拨银行
	public final String TMS_TRANSFER_ALLOCATED_BANK_NAME = "TMS.TRANSFER.ALLOCATED.BANK.NAME";
	// 工行
	public final String TMS_TRANSFER_ALLOCATED_BANK_NAME_1 = "TMS.TRANSFER.ALLOCATED.BANK.NAME.1";
	// 中行
	public final String TMS_TRANSFER_ALLOCATED_BANK_NAME_2 = "TMS.TRANSFER.ALLOCATED.BANK.NAME.2";
	// 农行
	public final String TMS_TRANSFER_ALLOCATED_BANK_NAME_3 = "TMS.TRANSFER.ALLOCATED.BANK.NAME.3";
	// 招商
	public final String TMS_TRANSFER_ALLOCATED_BANK_NAME_4 = "TMS.TRANSFER.ALLOCATED.BANK.NAME.4";
	// 交行
	public final String TMS_TRANSFER_ALLOCATED_BANK_NAME_5 = "TMS.TRANSFER.ALLOCATED.BANK.NAME.5";

}
